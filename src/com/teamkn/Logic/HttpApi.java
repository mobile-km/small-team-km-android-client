package com.teamkn.Logic;

import com.teamkn.base.http.*;
import com.teamkn.model.Note;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.model.database.NoteDBHelper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpApi {

    public static final String SITE = "http://192.168.1.28:9527";

    // 各种路径常量
    public static final String 用户登录 = "/login";

    public static final String 请求笔记元信息 = "/syn/detail_meta";

    public static final String 同步推送 = "/syn/push";
    
    public static final String 同步接收 = "/syn/pull";

    public static final String 用户查询 = "/users/search";
    
    public static final String 邀请增加为联系人 = "/contacts/invite";
    
    public static final String 接收加为联系人的邀请 = "/contacts/accept_invite";
    
    public static final String 拒绝加为联系人的邀请 = "/contacts/refuse_invite";
    
    public static final String 删除联系人          = "/contacts/remove";
    
    public static final String 刷新联系人状态      = "/contacts/refresh_status";

    // LoginActivity
    // 用户登录请求
    public static boolean user_authenticate(String email, String password) throws Exception {
        return new TeamknPostRequest<Boolean>(
                用户登录,
                new PostParamText("email", email),
                new PostParamText("password", password)
        ) {
            @Override
            public Boolean on_success(String response_text) throws Exception {
                JSONObject json = new JSONObject(response_text);
                AccountManager.login(get_cookies(), json.toString());
                return true;
            }
        }.go();
    }
    
    public static InputStream download_image(String image_url) {
      try {
        HttpGet httpget = new HttpGet(image_url);
        HttpResponse response = TeamknHttpRequest.get_httpclient_instance().execute(httpget);
        int status_code = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK == status_code) {
          return response.getEntity().getContent();
        } else {
          return null;
        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    public static class Syn {
        
        public static NoteMetaMerge detail_meta() throws Exception{
          long updated_time = TeamknPreferences.last_syn_server_meta_updated_time();
          
          return new TeamknGetRequest<NoteMetaMerge>(请求笔记元信息,
              new BasicNameValuePair("last_syn_server_meta_updated_time", updated_time+"")
              ){
            @Override
            public NoteMetaMerge on_success(String response_text) throws Exception {
              JSONObject json = new JSONObject(response_text);
              JSONArray array = json.getJSONArray("notes");
              long last_syn_server_meta_updated_time = json.getLong("last_syn_server_meta_updated_time");
              
              List<NoteMeta> server_note_metas = new ArrayList<NoteMeta>();
              for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String uuid = obj.getString("uuid");
                long server_updated_time = obj.getLong("server_updated_time");
                NoteMeta note_meta = new NoteMeta(uuid, server_updated_time, 0);
                server_note_metas.add(note_meta);
              }
              
              List<Note> client_changed_notes = NoteDBHelper.client_changed_notes();
              NoteMetaMerge merge = new NoteMetaMerge(client_changed_notes, server_note_metas,last_syn_server_meta_updated_time);
              
              return merge;
            }
          }.go();

        }
        
        public static long pull(String uuid) throws Exception{
          
          return new TeamknGetRequest<Long>(同步接收,
              new BasicNameValuePair("uuid", uuid+"")
              ){
            @Override
            public Long on_success(String response_text) throws Exception {
              JSONObject note_json = new JSONObject(response_text);
              String uuid = (String) note_json.get("uuid");
              String content = (String) note_json.get("content");
              String kind = (String) note_json.get("kind");
              Integer is_removed = (Integer) note_json.get("is_removed");
              long updated_at = (Integer) note_json.get("updated_at");
              String attachment_url = (String) note_json.get("attachment_url");
              long current_server_time = note_json.getLong("current_server_time");
              if (kind.equals(NoteDBHelper.Kind.IMAGE)) {
                HttpApi.Syn.pull_image(uuid, attachment_url);
              }
              NoteDBHelper.pull(uuid, content, kind, is_removed, updated_at);
              return current_server_time;
            }
          }.go();
        }

        private static void pull_image(String uuid, String attachment_url) {

            try {
                HttpGet httpget = new HttpGet(attachment_url);
                HttpResponse response = TeamknHttpRequest.get_httpclient_instance().execute(httpget);
                int status_code = response.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == status_code) {
                    InputStream in = response.getEntity().getContent();
                    File file = Note.note_image_file(uuid);
                    FileOutputStream fos = new FileOutputStream(file);
                    IOUtils.copy(in, fos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public static Long push(final Note note) throws Exception {
            File image = Note.note_image_file(note.uuid);

            if (note.kind.equals(NoteDBHelper.Kind.TEXT)) {

                return new TeamknPostRequest<Long>(同步推送,
                        new PostParamText("note[uuid]", note.uuid),
                        new PostParamText("note[content]", note.content),
                        new PostParamText("note[kind]", note.kind),
                        new PostParamText("note[is_removed]", note.is_removed + "")
                ) {
                    @Override
                    public Long on_success(String response_text) throws Exception {
                        long seconds = Long.parseLong(response_text);
                        NoteDBHelper.after_push(note.uuid, seconds);
                        return seconds;
                    }
                }.go();

            } else {

              return new TeamknPostRequest<Long>(同步推送,
                        new PostParamText("note[uuid]", note.uuid),
                        new PostParamText("note[content]", note.content),
                        new PostParamText("note[kind]", note.kind),
                        new PostParamText("note[is_removed]", note.is_removed + ""),
                        new PostParamFile("note[attachment]", image.getPath(), "image/jpeg")
                ) {
                    @Override
                    public Long on_success(String response_text) throws Exception {
                        long seconds = Long.parseLong(response_text);
                        NoteDBHelper.after_push(note.uuid, seconds);
                        return seconds;
                    }
                }.go();
            }
        }

    }
    
    public static class Contact{
      public static List<SearchUser> search(String query) throws Exception{
        return new TeamknGetRequest<List<SearchUser>>(用户查询,
            new BasicNameValuePair("query", query)
            ){
          @Override
          public List<SearchUser> on_success(String response_text) throws Exception {
            List<SearchUser> list = new ArrayList<SearchUser>();
            JSONArray array = new JSONArray(response_text);
            for (int i = 0; i < array.length(); i++) {
              JSONObject obj = (JSONObject)array.get(i);
              int user_id = obj.getInt("user_id");
              String user_name = obj.getString("user_name");
              String user_avatar_url = obj.getString("user_avatar_url");
              String contact_status = obj.getString("contact_status");
              SearchUser user = new SearchUser(user_id, user_name, user_avatar_url, contact_status);
              list.add(user);
            }
            return list;
          }
        }.go();
      }

      public static void invite(int user_id, String message) throws Exception {
        new TeamknPostRequest<Void>(邀请增加为联系人,
            new PostParamText("user_id",user_id+""),
            new PostParamText("message",message)
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                ContactDBHelper.create_or_update_by_contact_json(response_text);
                return null;
              }
        }.go();
      }

      public static void accept_invite(int user_id) throws Exception {
        new TeamknPostRequest<Void>( 接收加为联系人的邀请,
            new PostParamText("user_id",user_id+"")
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                ContactDBHelper.create_or_update_by_contact_json(response_text);
                return null;
              }
        }.go();
      }

      public static void refuse_invite(int user_id) throws Exception {
        final int other_user_id = user_id;
        new TeamknPostRequest<Void>( 拒绝加为联系人的邀请,
            new PostParamText("user_id",user_id+"")
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                int current_user_id = AccountManager.current_user().user_id;
                ContactDBHelper.destroy(current_user_id,other_user_id);
                return null;
              }
        }.go();
      }
      
      public static void remove_contact(int user_id)  throws Exception {
        final int other_user_id = user_id;
        new TeamknDeleteRequest<Void>( 删除联系人,
            new BasicNameValuePair("user_id", user_id+"")
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                int current_user_id = AccountManager.current_user().user_id;
                ContactDBHelper.destroy(current_user_id,other_user_id);
                return null;
              }
        }.go();
      }

      public static void refresh_status() throws Exception{
        long syn_contact_timestamp = TeamknPreferences.syn_contact_timestamp();
        new TeamknGetRequest<Void>(刷新联系人状态,
            new BasicNameValuePair("syn_contact_timestamp", syn_contact_timestamp+"")
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                ContactDBHelper.create_or_update_by_contact_list_json(response_text);
                return null;
              }
        }.go();
        long timestamp = ContactDBHelper.get_newest_server_updated_time(AccountManager.current_user().user_id);
        if(timestamp == 0){
          TeamknPreferences.set_syn_contact_timestamp(1);
        }else{
          TeamknPreferences.set_syn_contact_timestamp(timestamp);
        }
      }
    }

    public static class IntentException extends Exception {
        private static final long serialVersionUID = -4969746083422993611L;
    }
    
    public static class NetworkUnusableException extends Exception{
      private static final long serialVersionUID = 854703815488292561L;
    }
    
    public static class ServerErrorException extends Exception{
      private static final long serialVersionUID = -3689174865045267291L;
    }
}
