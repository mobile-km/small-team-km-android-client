package com.teamkn.Logic;

import com.teamkn.base.http.*;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class HttpApi {

    public static final String SITE = "http://192.168.1.28:9527";

    // 各种路径常量
    public static final String 用户登录 = "/login";

    public static final String 同步握手 = "/syn/handshake";

    public static final String 同步比对 = "/syn/compare";

    public static final String 同步推送 = "/syn/push";

    public static final String 同步下一个 = "/syn/get_next";

    // LoginActivity
    // 用户登录请求
    public static boolean user_authenticate(String email, String password) throws Exception {
        return new TeamknPostRequest<Boolean>(
                用户登录,
                new BasicNameValuePair("email", email),
                new BasicNameValuePair("password", password)
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
        public static class NoteBean {
            public String uuid;
            public String content;
            public String kind;
            public Integer is_removed;
            public long updated_at;
            public String attachment_url;

            private NoteBean() {
            }

            public static NoteBean build(JSONObject note_json) throws Exception {
                String uuid = (String) note_json.get("uuid");
                String content = (String) note_json.get("content");
                String kind = (String) note_json.get("kind");
                Integer is_removed = (Integer) note_json.get("is_removed");
                long updated_at = (Integer) note_json.get("updated_at");
                String attachment_url = (String) note_json.get("attachment_url");
                NoteBean nb = new NoteBean();
                nb.uuid = uuid;
                nb.content = content;
                nb.kind = kind;
                nb.is_removed = is_removed;
                nb.updated_at = updated_at;
                nb.attachment_url = attachment_url;
                return nb;
            }
        }

        public static class Action {
            public static final String DO_PUSH = "do_push";
            public static final String DO_PULL = "do_pull";
            public static final String DO_NOTHING = "do_nothing";
            public static final String SYN_COMPLETED = "syn_completed";
            public static final String SYN_HAS_NEXT = "syn_has_next";
        }

        public static HashMap<String, Object> handshake() throws Exception,ServerErrorException,NetworkUnusableException {
          return new HttpRequestTryThreeProcessor<HashMap<String, Object>>(){
            @Override
            public HashMap<String, Object> callback() throws Exception {
              return new TeamknGetRequest<HashMap<String, Object>>(同步握手) {
                @Override
                public HashMap<String, Object> on_success(String response_text) throws Exception {
                  JSONObject json = new JSONObject(response_text);
                  String uuid = (String) json.get("syn_task_uuid");
                  Integer count = (Integer) json.get("note_count");
                  HashMap<String, Object> map = new HashMap<String, Object>();
                  map.put("syn_task_uuid", uuid);
                  map.put("note_count", count);
                  return map;
                }
              }.go();
            }
          }.execute();
        }

        public static String compare(final String syn_task_uuid, final Note note) throws Exception,NetworkUnusableException,ServerErrorException {
          return new HttpRequestTryThreeProcessor<String>(){
            @Override
            public String callback() throws Exception {
              return new TeamknPostRequest<String>(同步比对,
                new BasicNameValuePair("syn_task_uuid", syn_task_uuid),
                new BasicNameValuePair("note_uuid", note.uuid),
                new BasicNameValuePair("updated_at", note.updated_at + "")
                ) {
                  @Override
                  public String on_success(String response_text) throws Exception {
                    JSONObject json = new JSONObject(response_text);
                    String action = (String) json.get("action");
                    System.out.println("compare");
                    System.out.println(action);
                    if (action.equals(HttpApi.Syn.Action.DO_PUSH)) {
                      HttpApi.Syn.push(note);
                    } else if (action.equals(HttpApi.Syn.Action.DO_PULL)) {
                      JSONObject note_json = (JSONObject) json.get("note");
                      System.out.println(note_json);
                      NoteBean note_bean = HttpApi.Syn.NoteBean.build(note_json);
                      if (note_bean.kind.equals(NoteDBHelper.Kind.IMAGE)) {
                        HttpApi.Syn.pull_image(note_bean.uuid, note_bean.attachment_url);
                      }
                      NoteDBHelper.update_from_pull(note_bean.uuid, note_bean.content, note_bean.is_removed, note_bean.updated_at);
                    }
                    return action;
                  }
              }.go();
            }
          }.execute();
        }

        public static void pull_image(String uuid, String attachment_url) {

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

        public static void push(final Note note) throws Exception {
            File image = Note.note_image_file(note.uuid);

            if (note.kind.equals(NoteDBHelper.Kind.TEXT)) {

                new TeamknPostRequest<String>(同步推送,
                        new PostParmText("note[uuid]", note.uuid),
                        new PostParmText("note[content]", note.content),
                        new PostParmText("note[kind]", note.kind),
                        new PostParmText("note[is_removed]", note.is_removed + "")
                ) {
                    @Override
                    public String on_success(String response_text) throws Exception {
                        long seconds = Long.parseLong(response_text);
                        NoteDBHelper.touch_updated_at(note.uuid, seconds);
                        return null;
                    }
                }.go();

            } else {

                new TeamknPostRequest<String>(同步推送,
                        new PostParmText("note[uuid]", note.uuid),
                        new PostParmText("note[content]", note.content),
                        new PostParmText("note[kind]", note.kind),
                        new PostParmText("note[is_removed]", note.is_removed + ""),
                        new PostParmFile("note[attachment]", image.getPath(), "image/jpeg")
                ) {
                    @Override
                    public String on_success(String response_text) throws Exception {
                        long seconds = Long.parseLong(response_text);
                        NoteDBHelper.touch_updated_at(note.uuid, seconds);
                        return null;
                    }
                }.go();
            }
        }

        public static boolean syn_next(final String syn_task_uuid) throws Exception,NetworkUnusableException,ServerErrorException {
          return new HttpRequestTryThreeProcessor<Boolean>(){
            @Override
            public Boolean callback() throws Exception {
              return new TeamknGetRequest<Boolean>(同步下一个,
                new BasicNameValuePair("syn_taks_uuid", syn_task_uuid)
                ) {
                  @Override
                  public Boolean on_success(String response_text) throws Exception {
                    JSONObject json = new JSONObject(response_text);
                    String action = (String) json.get("action");
                    if (action.equals(HttpApi.Syn.Action.SYN_COMPLETED)) {
                      return false;
                    } else if (action.equals(HttpApi.Syn.Action.SYN_HAS_NEXT)) {
                      JSONObject note_json = (JSONObject) json.get("note");
                      System.out.println(note_json);
                      NoteBean note_bean = HttpApi.Syn.NoteBean.build(note_json);
                      if (note_bean.kind.equals(NoteDBHelper.Kind.IMAGE)) {
                        HttpApi.Syn.pull_image(note_bean.uuid, note_bean.attachment_url);
                      }
                      NoteDBHelper.create_new_item_from_pull_server(note_bean.uuid, note_bean.content, note_bean.kind, note_bean.is_removed, note_bean.updated_at);
                      return true;
                    }
                    throw new Exception();
                  }
                }.go();
              }
          }.execute();
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
