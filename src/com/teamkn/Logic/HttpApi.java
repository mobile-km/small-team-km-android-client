package com.teamkn.Logic;

import com.teamkn.base.http.*;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Note;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.model.database.UserDBHelper;
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
    
    public static final String 创建对话串          = "/api/chats";
    
    public static final String 创建对话            = "/api/chat_nodes";
    
    public static final String 获取对话串          = "/api/pull_chats";
    
    public static final String 获取对话            = "/api/pull_chat_nodes";

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
    
    public static class Chat{
      public static void create(final int client_chat_id, List<Integer> server_user_id_list) throws Exception{
        String member_ids_str = BaseUtils.integer_list_to_string(server_user_id_list);
        new TeamknPostRequest<Void>( 创建对话串,
            new PostParamText("member_ids",member_ids_str)
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                JSONObject json = new JSONObject(response_text);
                int server_chat_id = json.getInt("server_chat_id");
                long server_created_time = json.getLong("server_created_time");
                long server_updated_time = json.getLong("server_updated_time");
                ChatDBHelper.after_server_create(client_chat_id,server_chat_id,server_created_time,server_updated_time);
                return null;
              }
        }.go();
      }
      
      public static void pull_chats() throws Exception{
        final long last_syn_chat_updated_time = TeamknPreferences.last_syn_chat_updated_time();
        
        new TeamknGetRequest<Void>(获取对话串,
            new BasicNameValuePair("last_syn_chat_updated_time", last_syn_chat_updated_time+"")
            ){
          @Override
          public Void on_success(String response_text) throws Exception {
            long max_last_syn_chat_updated_time = last_syn_chat_updated_time;
            JSONArray json_array = new JSONArray(response_text);
            for (int i = 0; i < json_array.length(); i++) {
              JSONObject obj = json_array.getJSONObject(i);
              
              int server_chat_id = obj.getInt("server_chat_id");
              long server_created_time = obj.getLong("server_created_time");
              long server_updated_time = obj.getLong("server_updated_time");
              ArrayList<Integer> client_user_id_list = new ArrayList<Integer>();
              
              JSONArray members_array = obj.getJSONArray("members");
              for (int j = 0; j < members_array.length(); j++) {
                JSONObject member = members_array.getJSONObject(j);
                
                int user_id = member.getInt("user_id");
                String user_name = member.getString("user_name");
                String user_avatar_url = member.getString("user_avatar_url");
                long user_server_created_time = member.getLong("server_created_time");
                long user_server_updated_time = member.getLong("server_updated_time");
                
                if(!UserDBHelper.is_exists(user_id)){
                  UserDBHelper.create(user_id, user_name, user_avatar_url, user_server_created_time, user_server_updated_time);
                }
                int client_user_id = UserDBHelper.find_client_user_id(user_id);
                client_user_id_list.add(client_user_id);
              }
              ChatDBHelper.pull_from_server(server_chat_id,client_user_id_list,server_created_time,server_updated_time);
              max_last_syn_chat_updated_time = Math.max(max_last_syn_chat_updated_time,server_updated_time);
            }
            TeamknPreferences.set_last_syn_chat_updated_time(max_last_syn_chat_updated_time);
            return null;
          }
        }.go();
      }
    }
    
    public static class ChatNode{

      public static void create(final int client_chat_node_id, int server_chat_id, String content) throws Exception {
        new TeamknPostRequest<Void>(创建对话,
            new PostParamText("chat_id",server_chat_id+""),
            new PostParamText("chat_node[content]",content),
            new PostParamText("chat_node[kind]",ChatNodeDBHelper.Kind.TEXT)
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                JSONObject json = new JSONObject(response_text);
                int server_chat_node_id = json.getInt("server_chat_node_id");
                long server_created_time = json.getLong("server_created_time");
                
                ChatNodeDBHelper.after_server_create(client_chat_node_id,server_chat_node_id,server_created_time);
                return null;
              }
        }.go();        
      }
      
      public static void pull_chat_nodes() throws Exception{
        final long last_syn_chat_node_created_time = TeamknPreferences.last_syn_chat_node_created_time();
        
        new TeamknGetRequest<Void>(获取对话,
            new BasicNameValuePair("last_syn_chat_node_created_time", last_syn_chat_node_created_time+"")
            ){
          @Override
          public Void on_success(String response_text) throws Exception {
            long max_last_syn_chat_node_created_time = last_syn_chat_node_created_time;
            JSONArray chat_node_array = new JSONArray(response_text);
            for (int i = 0; i < chat_node_array.length(); i++) {
              JSONObject chat_node_obj = chat_node_array.getJSONObject(i);
              int server_chat_id = chat_node_obj.getInt("server_chat_id");
              int server_chat_node_id = chat_node_obj.getInt("server_chat_node_id");
              int sender_id = chat_node_obj.getInt("sender_id");
              String content = chat_node_obj.getString("content");
              long server_created_time = chat_node_obj.getLong("server_created_time");
              
              if(!ChatNodeDBHelper.is_exists(server_chat_node_id)){
                int client_user_id = UserDBHelper.find_client_user_id(sender_id);
                int client_chat_id = ChatDBHelper.find_client_chat_id(server_chat_id);
                ChatNodeDBHelper.pull(client_chat_id,server_chat_node_id,client_user_id,content,server_created_time);
              }
              max_last_syn_chat_node_created_time = Math.max(max_last_syn_chat_node_created_time, server_created_time);
            }
            TeamknPreferences.set_last_syn_chat_node_created_time(max_last_syn_chat_node_created_time);
            return null;
          }
        }.go();
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
