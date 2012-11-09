package com.teamkn.Logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.RegisterActivity;
import com.teamkn.activity.usermsg.UserMsgAvatarSetActivity;
import com.teamkn.activity.usermsg.UserMsgNameSetActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.http.PostParamFile;
import com.teamkn.base.http.PostParamText;
import com.teamkn.base.http.TeamknDeleteRequest;
import com.teamkn.base.http.TeamknGetRequest;
import com.teamkn.base.http.TeamknHttpRequest;
import com.teamkn.base.http.TeamknPostRequest;
import com.teamkn.base.http.TeamknPutRequest;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.SharedParam;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataListReading;
import com.teamkn.model.Note;
import com.teamkn.model.User;
import com.teamkn.model.Watch;
import com.teamkn.model.database.AttitudesDBHelper;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.model.database.DataListReadingDBHelper;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.model.database.UserDBHelper;
import com.teamkn.model.database.WatchDBHelper;

public class HttpApi {

//    public static final String SITE = "http://192.168.1.38:9527";
//	public static final String SITE = "http://192.168.1.26:9527";
	public static final String SITE = "http://teamkn.mindpin.com";

    // 各种路径常量
    public static final String 用户注册 = "/signup_submit";
    
    public static final String 用户登录 = "/login";
    
    public static final String 设置用户名 = "/api/account/change_name" ; 
    
    public static final String 设置用户头像 = "/api/account/change_avatar" ; 

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
    
    public static final String 创建对话表情反馈     =  "/api/attitudes/push";
    
    public static final String 获取对话表情反馈     =  "/api/attitudes/pull";
    
    //data_lists
    public static final String 获取_data_list     =  "/api/data_lists";
    public static final String 创建_data_list     =  "/api/data_lists";
    public static final String 修改_data_list     =  "/api/data_lists/";
    public static final String 删除_data_list     =  "/api/data_lists/";
    public static final String 搜索个人_data_list     =  "/api/data_lists/search_mine";
    public static final String 分享_data_list     =  "/api/data_lists/";
    public static final String 公共_data_list     =  "/api/data_lists/public_timeline";
    public static final String 搜索公共_data_list   =  "/api/data_lists/search_public_timeline";
    public static final String 搜索个人书签_data_list =  "/api/data_lists/search_mine_watch";
    public static final String 迁出一个_data_list = "/api/data_lists/";
    public static final String 迁出的data_list列表 = "/api/data_lists/forked_list";
    
    // 查看一个 data_list 的被推送的列表，每个列表项包括 推送作者和修改数量GET /api/data_lists/:id/commit_meta_list
    public static final String 查看data_list被推送的列表 = "/api/data_lists/";
    //列表差异处理界面，origin 列表和 forked 列表内容
    public static final String 列表差异处理界面 = "/api/data_lists/";
    //一个 data_list 中，接受某个推送作者的全部修改
    public static final String 接受全部修改 = "/api/data_lists/";
    //一个 data_list 中，拒绝某个推送作者的全部修改
    public static final String 拒绝全部修改 = "/api/data_lists/";
	//	在逐条处理中，获取 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
    public static final String 获取data_list推送内容 = "/api/data_lists/";
	//	在逐条处理中，接受 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
    public static final String 接受data_list推送内容 = "/api/data_lists/";
	//	在逐条处理中，拒绝 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
    public static final String 拒绝data_list推送内容 = "/api/data_lists/";
    
    // watch_list
    public static final String 查看收藏列表_watch_list    =  "/api/data_lists/watch_list";
    public static final String 收藏_data_list_watch_list    =  "/api/data_lists/";
    // data_item
    public static final String 获取_data_item    =  "/api/data_lists/";
    public static final String 创建_data_item    =  "/api/data_lists/";
    public static final String 修改_data_item    =  "/api/data_items/";
    public static final String 删除_data_item    =  "/api/data_items/";
    public static final String 排序_data_item    =  "/api/data_items/";
    // LoginActivity
    // 用户登录请求
    public static boolean user_authenticate(String email, String password) throws Exception {
    	System.out.println("login : = " + 用户登录 +" : " +email + " : " +  password);
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
    
    public static Boolean user_register(String email,String name, String password,String affirm_password) throws Exception {
    	return new TeamknPostRequest<Boolean>(
        		用户注册,
                new PostParamText("user[email]", email),
                new PostParamText("user[name]", name),
                new PostParamText("user[password]", password),
                new PostParamText("user[password_confirmation]", affirm_password)
        ) {
            @Override
            public Boolean on_success(String response_text) throws Exception {
                JSONObject json = new JSONObject(response_text);
                AccountManager.login(get_cookies(), json.toString());
                
                return true;
            }
            
            @Override
            public Boolean on_unprocessable_entity(String responst_text) {
            	RegisterActivity.questError = responst_text;
				return false;	
            };
        }.go();
    }
    
    public static Boolean user_set_name(String uname) throws Exception {
    	return new TeamknPostRequest<Boolean>(
        		设置用户名,
                new PostParamText("name", uname)
        ) {
            @Override
            public Boolean on_success(String response_text) throws Exception {
            	
                JSONObject json = new JSONObject(response_text);
                AccountManager.login(get_cookies(), json.toString());
//                AccountUser manager = AccountManager.current_user();
//                UserDBHelper.updateAccount(manager.user_id, manager.name, manager.avatar_url);
                return true;
            }
            
            @Override
            public Boolean on_unprocessable_entity(String responst_text) {
            	UserMsgNameSetActivity.requestError = responst_text;
				return false;	
            };
        }.go();
    }
    
    public static Boolean user_set_avatar(File avatar) throws Exception {
    	return new TeamknPostRequest<Boolean>(
        		设置用户头像,
                new PostParamFile("avatar", avatar.getPath(),"image/jpeg")
        ) {
//    		new PostParamFile("chat_node[content]", image.getPath(), "image/jpeg"),
            @Override
            public Boolean on_success(String response_text) throws Exception {
            	
                JSONObject json = new JSONObject(response_text);
                AccountManager.login(get_cookies(), json.toString());
                AccountUser manager = AccountManager.current_user();
                UserDBHelper.updateAccount(manager.user_id, manager.name, manager.avatar_url);   
                return true;
            }
            
            @Override
            public Boolean on_unprocessable_entity(String responst_text) {
            	UserMsgAvatarSetActivity.requestError = responst_text;
				return false;	
            };
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
                    Note.note_thumb_image_file(uuid);
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
      public static void create(final String uuid, List<Integer> server_user_id_list) throws Exception{
        String member_ids_str = BaseUtils.integer_list_to_string(server_user_id_list);
        new TeamknPostRequest<Void>( 创建对话串,
            new PostParamText("member_ids",member_ids_str),
            new PostParamText("uuid",uuid)
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                JSONObject json = new JSONObject(response_text);
                int server_chat_id = json.getInt("server_chat_id");
                long server_created_time = json.getLong("server_created_time");
                long server_updated_time = json.getLong("server_updated_time");
                ChatDBHelper.after_server_create(uuid,server_chat_id,server_created_time,server_updated_time);
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
              
              String uuid = obj.getString("uuid");
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
                int client_user_id = UserDBHelper.get_client_user_id(user_id);
                client_user_id_list.add(client_user_id);
              }
              ChatDBHelper.pull_from_server(uuid,server_chat_id,client_user_id_list,server_created_time,server_updated_time);
              max_last_syn_chat_updated_time = Math.max(max_last_syn_chat_updated_time,server_updated_time);
            }
            TeamknPreferences.set_last_syn_chat_updated_time(max_last_syn_chat_updated_time);
            return null;
          }
        }.go();
      }
    }
    public static class Attitudes{
    	public static void create(final int chat_node_id, final int current_user_id, final String kind,int server_chat_node_id) throws Exception {
            new TeamknPostRequest<Void>(创建对话表情反馈,
                new PostParamText("chat_node_id",server_chat_node_id+""),
                new PostParamText("user_id",current_user_id+""),
                new PostParamText("kind",kind)
                ) {
                  @Override
                  public Void on_success(String response_text) throws Exception {
                      AttitudesDBHelper.create(chat_node_id,current_user_id,kind,"true");
                    return null;
                  }
            }.go();        
        }
    	public static void getcreat(final Context context) throws Exception {
    		int time = SharedParam.getParam(context);
    		try {
				new TeamknGetRequest<Void>(获取对话表情反馈,
				        new BasicNameValuePair("last_syn_attitudes_updated_time", time+"")){
				      @Override
				      public Void on_success(String response_text) throws Exception { 
				    	int maxTime = 0; 
				        JSONArray attitudes_array = new JSONArray(response_text);
				        for (int i = 0; i < attitudes_array.length(); i++) {
				           JSONObject att = attitudes_array.getJSONObject(i);
				           int server_updated_time = att.getInt("server_updated_time");
				           if(maxTime<server_updated_time){
				        	   maxTime = server_updated_time ;
				           } 
				           
				           int server_chat_node_id = att.getInt("server_chat_node_id");
				           String kind = att.getString("kind");
				           User user = null;
			        	   JSONObject user_json = att.getJSONObject("user");
			        	   int user_id_s = user_json.getInt("user_id");
			        	   String user_name = user_json.getString("user_name");
			        	   String user_avatar_url = (user_json.getString("user_avatar_url"));
			        	   byte[] user_avatar = null;
			        	   User user_s = UserDBHelper.find_by_server_user_id(user_id_s);
			               if(user_s.avatar_url.equals(user_avatar_url)){
			            	   user_avatar = user_s.user_avatar;
				           }else{
				        	   InputStream is = HttpApi.download_image(user_avatar_url);
				               user_avatar = IOUtils.toByteArray(is);
				           }
			        	   
			        	   long server_created_time = user_json.getLong("server_created_time");
			        	   long server_updated_time1 = user_json.getLong("server_updated_time");
			        	   user = new User(0, user_id_s, user_name,user_avatar, user_avatar_url, server_created_time, server_updated_time1);
			           
				           
				           int chat_node_id = ChatNodeDBHelper.find_by_server_chat_node_id(server_chat_node_id).id;
				           int user_id = UserDBHelper.find_by_server_user_id(user.user_id).id;
				           com.teamkn.model.Attitudes attitudes= AttitudesDBHelper.find_by_chat_node_id_AND_user_id(chat_node_id, user_id);
				           if(attitudes.kind != kind){
				        	   AttitudesDBHelper.create(chat_node_id, user.user_id, kind, "true");
				           }    
				        }
				        SharedParam.saveParam(context, maxTime);
				        return null;
				        
				      }
				    }.go();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    }
    public static class ChatNode{

      public static void create(final String uuid, int server_chat_id, String content) throws Exception {
        new TeamknPostRequest<Void>(创建对话,
            new PostParamText("chat_id",server_chat_id+""),
            new PostParamText("chat_node[uuid]",uuid),
            new PostParamText("chat_node[content]",content),
            new PostParamText("chat_node[kind]",ChatNodeDBHelper.Kind.TEXT)
            ) {
              @Override
              public Void on_success(String response_text) throws Exception {
                JSONObject json = new JSONObject(response_text);
                int server_chat_node_id = json.getInt("server_chat_node_id");
                long server_created_time = json.getLong("server_created_time");
                
                ChatNodeDBHelper.after_server_create(uuid,server_chat_node_id,server_created_time);
                return null;
              }
        }.go();        
      }
      // 加载提交图片
      public static void create_image(final String uuid, int server_chat_id, String content , String kind) throws Exception {
    	  File image = com.teamkn.model.Chat.note_image_file(uuid);
    	  new TeamknPostRequest<Void>(创建对话,
              new PostParamText("chat_id",server_chat_id+""),
              new PostParamText("chat_node[uuid]",uuid),
              new PostParamFile("chat_node[content]", image.getPath(), "image/jpeg"),
              new PostParamText("chat_node[kind]",kind)
              ) {
                @Override
                public Void on_success(String response_text) throws Exception {
                  JSONObject json = new JSONObject(response_text);
                  int server_chat_node_id = json.getInt("server_chat_node_id");
                  long server_created_time = json.getLong("server_created_time");
                  
                  ChatNodeDBHelper.after_server_create(uuid,server_chat_node_id,server_created_time);
 
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
              String uuid = chat_node_obj.getString("uuid");
              int server_chat_id = chat_node_obj.getInt("server_chat_id");
              int server_chat_node_id = chat_node_obj.getInt("server_chat_node_id");
              int sender_id = chat_node_obj.getInt("sender_id");
              String content = chat_node_obj.getString("content");
              long server_created_time = chat_node_obj.getLong("server_created_time");
            
              ChatNodeDBHelper.pull_from_server(uuid, server_chat_id, server_chat_node_id, sender_id, content, server_created_time);
              
              max_last_syn_chat_node_created_time = Math.max(max_last_syn_chat_node_created_time, server_created_time);
           
              if( TeamknApplication.current_show_activity!=null && TeamknApplication.current_show_activity
            		  .equals("com.teamkn.activity.chat.ChatActivity")){
//            	  com.teamkn.model.ChatNode chat_node = ChatNodeDBHelper.find_by_server_chat_node_id(server_chat_node_id);
//            	  ChatActivity.add_chat_node_item(chat_node);
              }
            }
            TeamknPreferences.set_last_syn_chat_node_created_time(max_last_syn_chat_node_created_time);
            return null;
          }
        }.go();
      }
      
    }
    
    public static class DataList{
    	public static List<com.teamkn.model.DataList> deletForkList = new ArrayList<com.teamkn.model.DataList>();
    	
    	private static com.teamkn.model.DataList getDataList(JSONObject json) throws JSONException, IOException{
    		int server_data_list_id = json.getInt("id");
    		
            String title  = json.getString("title");
            String kind   = json.getString("kind");
            String public_boolean  = json.getString("public");
            String has_commits = "false";
            
            if(json.getString("has_commits")!=null && json.getString("has_commits").equals("true")){
            	has_commits = "true";
            } 
            String forked_from_id_ob = json.getString("forked_from_id");
            int forked_from_id = -1;
//            System.out.println(forked_from_id_ob + " :  " + json.getString("has_commits"));
            if(!forked_from_id_ob.equals(null) && forked_from_id_ob!=null && !forked_from_id_ob.equals("null")){
            	forked_from_id = Integer.parseInt(forked_from_id_ob);
            }
            JSONObject user_json = json.getJSONObject("creator");
            // --------------  
            int server_user_id = user_json.getInt("id");
            String user_name = user_json.getString("name");
            String avatar_url = user_json.getString("avatar_url");
            byte[] user_avatar = null; 
            User user_query = UserDBHelper.find_by_server_user_id(server_user_id);
            if(user_query.avatar_url!=null && !user_json.isNull("avatar_url")  && !avatar_url.equals("")&& user_query.avatar_url.equals(avatar_url)){
            	user_avatar = user_query.user_avatar;
            }else if(!user_json.isNull("avatar_url") && !avatar_url.equals("")){
            	InputStream is = HttpApi.download_image(avatar_url);
            	user_avatar = IOUtils.toByteArray(is);
            } 
            long user_server_created_time  = user_json.getLong("server_created_time");
            long user_server_updated_time  = user_json.getLong("server_updated_time");
            
            long data_list_server_created_time = json.getLong("server_created_time");
            long data_list_server_updated_time = json.getLong("server_updated_time");
            
            String forked_from_is_removed = json.getString("forked_from_is_removed");
            
            String is_removed = json.getString("is_removed");
            
            User user = new User(0, server_user_id, user_name, user_avatar,avatar_url, user_server_created_time, user_server_updated_time);
            UserDBHelper.createOrUpdate(user); 
            
            com.teamkn.model.DataList dataList_server =
            		new com.teamkn.model.DataList
            		( UserDBHelper.find_by_server_user_id(server_user_id).id, 
            				title, kind, public_boolean,has_commits,server_data_list_id,
            				data_list_server_created_time,data_list_server_updated_time,
            				forked_from_id,forked_from_is_removed,is_removed);
            
            return dataList_server;
    	}
		public static void pull(String kind,int page , int per_page) throws Exception{  
			new TeamknGetRequest<Void>(获取_data_list,
	            new BasicNameValuePair("kind", kind),
	            new BasicNameValuePair("page", page+""),
	            new BasicNameValuePair("per_page",per_page+"")
	            ){
		          @Override
		          public Void on_success(String response_text) throws Exception {
		        	  JSONArray data_list_array = new JSONArray(response_text);
		        	  System.out.println("pull response_text =  " + response_text);
	                  for (int i = 0; i < data_list_array.length(); i++) {
			                JSONObject json = data_list_array.getJSONObject(i);
			                com.teamkn.model.DataList dataList_server =getDataList(json);		
			                DataListDBHelper.pull(dataList_server);  
			                System.out.println("pull:dataList - " + dataList_server.toString());
	                  }  
		              return null;
		          }
		          public Void on_unprocessable_entity(String responst_text) {
					return null;
			      };
		        }.go();     
        }
    	
    	public static void create(final com.teamkn.model.DataList dataList) throws Exception{
           
 		   new TeamknPostRequest<Void>( 创建_data_list,
 	            new PostParamText("data_list[title]",dataList.title),
 	            new PostParamText("data_list[kind]",dataList.kind),
 	            new PostParamText("data_list[public]",dataList.public_boolean)
 		   ) {
 	              @Override
 	              public Void on_success(String response_text) throws Exception {
		                System.out.println("data_list pull response_text " + response_text);
		                JSONObject json = new JSONObject(response_text);
		                com.teamkn.model.DataList dataList_server =getDataList(json);
		                dataList_server.setId(dataList.id);
		                System.out.println("create clint datalist :  " + dataList.toString());
		                System.out.println("create server datalist :  " +dataList_server.toString());
		                DataListDBHelper.update(dataList_server);
					    return null;   	
 	              }
 	     }.go();
       }
    	public static void remove(com.teamkn.model.DataList dataList) throws Exception{
    		new TeamknDeleteRequest<Void>(删除_data_list+dataList.server_data_list_id,
    				new BasicNameValuePair("id","id")) {
						@Override
						public Void on_success(String response_text)
								throws Exception {
							return null;
						}
			}.go();
    	}
    	public static com.teamkn.model.DataList update(final com.teamkn.model.DataList dataList) throws Exception{
    		return new TeamknPutRequest<com.teamkn.model.DataList>( 修改_data_list + dataList.server_data_list_id,
    				new PostParamText("title", dataList.title)
    				,new PostParamText("public",dataList.public_boolean)
    		        ) {
						@Override
						public com.teamkn.model.DataList on_success(String response_text)throws Exception {
							    JSONObject json = new JSONObject(response_text);
							    com.teamkn.model.DataList dataList_server =getDataList(json);
							    dataList_server.setId(dataList.id);
							    System.out.println("update server  " + dataList_server.toString());
				                DataListDBHelper.update(dataList_server);
							return dataList_server;
						}
			}.go();
    	}
    	public static List<com.teamkn.model.DataList> search_mine(String search_str) throws Exception{  
    		 final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
	   		 new TeamknGetRequest<Void>(搜索个人_data_list,
		            new BasicNameValuePair("query", search_str)
		            ){
			          @Override
			          public Void on_success(String response_text) throws Exception {
			        	  System.out.println(response_text);
			        	  JSONArray data_list_array = new JSONArray(response_text);
		            	  for (int i = 0; i < data_list_array.length(); i++) {
				              int server_id = data_list_array.getInt(i);
				              com.teamkn.model.DataList dataList = DataListDBHelper.find_by_server_data_list_id(server_id);
				              dataLists.add(dataList);
		                  }  
			              return null;
			          }
			        }.go();
			return dataLists;
       }
    	public static List<com.teamkn.model.DataList> search_public_timeline(String search_str) throws Exception{  
   		 final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
	   		 new TeamknGetRequest<Void>(搜索公共_data_list,
		            new BasicNameValuePair("query", search_str)
		            ){
			          @Override
			          public Void on_success(String response_text) throws Exception {
			        	  System.out.println(response_text);
			        	  JSONArray data_list_array = new JSONArray(response_text);
		            	  for (int i = 0; i < data_list_array.length(); i++) {
				              int server_id = data_list_array.getInt(i);
				              com.teamkn.model.DataList dataList = DataListDBHelper.find_by_server_data_list_id(server_id);
				              dataLists.add(dataList);
		                  }  
			              return null;
			          }
			        }.go();
			return dataLists;
      }
    	public static List<com.teamkn.model.DataList> search_mine_watch(String search_str) throws Exception{  
   		 final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
	   		 new TeamknGetRequest<Void>(搜索个人书签_data_list,
		            new BasicNameValuePair("query", search_str)
		            ){
			          @Override
			          public Void on_success(String response_text) throws Exception {
			        	  System.out.println(response_text);
			        	  JSONArray data_list_array = new JSONArray(response_text);
		            	  for (int i = 0; i < data_list_array.length(); i++) {
				              int server_id = data_list_array.getInt(i);
				              com.teamkn.model.DataList dataList = DataListDBHelper.find_by_server_data_list_id(server_id);
				              dataLists.add(dataList);
				              System.out.println(dataList.toString());
		                  }  
			              return null;
			          }
			        }.go();
			return dataLists;
      }
    	public static void share(final com.teamkn.model.DataList dataList) throws Exception{
    		new TeamknPutRequest<Void>( 分享_data_list + dataList.server_data_list_id + "/share_setting",
    				new PostParamText("share", dataList.public_boolean)) {
						@Override
						public Void on_success(String response_text)
								throws Exception {
							return null;
						}
			}.go();
    	}
    	
    	public static List<com.teamkn.model.DataList> public_timeline(int page , int per_page) throws Exception{  
   		 final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
	   		 new TeamknGetRequest<Void>(公共_data_list,
		            new BasicNameValuePair("page", page+""),
		            new BasicNameValuePair("per_page", per_page+"")
		            ){
			          @Override
			          public Void on_success(String response_text) throws Exception {
			        	  System.out.println(response_text);
			        	  JSONArray data_list_array = new JSONArray(response_text);
			        	  for (int i = 0; i < data_list_array.length(); i++) {
				                JSONObject json = data_list_array.getJSONObject(i);
				                com.teamkn.model.DataList dataList_server =getDataList(json);		
				                DataListDBHelper.pull(dataList_server);
				                dataLists.add(dataList_server);
		                  }
			        	  DataListDBHelper.remove_old(dataLists, MainActivity.RequestCode.data_list_type,  MainActivity.RequestCode.data_list_public);
			              return null;
			          }
			        }.go();
			return dataLists;
        }
    	//迁出一个data_list
    	public static com.teamkn.model.DataList fork(final com.teamkn.model.DataList dataList) throws Exception{
    		return new TeamknPutRequest<com.teamkn.model.DataList>( 迁出一个_data_list + dataList.server_data_list_id + "/fork",
    				new PostParamText("any_params", 1+"")) {
				@Override
				public com.teamkn.model.DataList on_success(String response_text) throws Exception {
					System.out.println("fork = " + response_text);
					JSONObject json = new JSONObject(response_text);
				    com.teamkn.model.DataList dataList_server =getDataList(json);
				    System.out.println("fork datalist " + dataList_server.toString());
				    DataListDBHelper.pull(dataList_server);
				    dataList_server.setId(DataListDBHelper.find_by_server_data_list_id(dataList_server.server_data_list_id).id);
					
				    return dataList_server;
				}
			}.go();
    	}
//    	迁出的data_list列表
    	public static List<com.teamkn.model.DataList> forked_list(int page , int per_page) throws Exception{  
      		 final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
   	   		 new TeamknGetRequest<Void>(迁出的data_list列表,
   		            new BasicNameValuePair("page", page+""),
   		            new BasicNameValuePair("per_page", per_page+"")
   		            ){
   			          @Override
   			          public Void on_success(String response_text) throws Exception {
   			        	  System.out.println(response_text);
   			        	  JSONArray data_list_array = new JSONArray(response_text);
   			        	  for (int i = 0; i < data_list_array.length(); i++) {
   				                JSONObject json = data_list_array.getJSONObject(i);
   				                com.teamkn.model.DataList dataList_server =getDataList(json);		
   				                DataListDBHelper.pull(dataList_server);
   				                dataList_server.setId(DataListDBHelper.find_by_server_data_list_id(dataList_server.server_data_list_id).id);
   				                dataLists.add(dataList_server);
   		                  } 
//   			        	deletForkList =  DataListDBHelper.deleteDataList(dataLists,MainActivity.RequestCode.ALL,  MainActivity.RequestCode.data_list_public);
   			              return null;
   			          }
   			        }.go();
   			return dataLists;
         }
    }
    public static class WatchList{
    	public static List<com.teamkn.model.DataList> deletWatchList = new ArrayList<com.teamkn.model.DataList>();
    	//收藏_data_list_watch_list
    	public static List<com.teamkn.model.DataList> watch_public_timeline(int page , int per_page) throws Exception{  
      		 final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
   	   		 new TeamknGetRequest<Void>(查看收藏列表_watch_list,
   		            new BasicNameValuePair("page", page+""),
   		            new BasicNameValuePair("per_page", per_page+"")
   		            ){
   			          @Override
   			          public Void on_success(String response_text) throws Exception {
   			        	  
   			        	  JSONArray data_list_array = new JSONArray(response_text);
   			        	  System.out.println("watch_public_timeline  =  " + data_list_array.length());
   			        	  System.out.println("watch_public_timeline response_text =  " + response_text);
   			        	  for (int i = 0; i < data_list_array.length(); i++) {
   				                JSONObject json = data_list_array.getJSONObject(i);
   				                com.teamkn.model.DataList dataList_server;
//   				                System.out.println(json.);
   				                if(json.getString("is_removed").equals("true")){
   				                	dataList_server=new com.teamkn.model.DataList();
   				                	int service_id = json.getInt("id");
   				                	String is_removed = json.getString("is_removed");
   				                	dataList_server.setServer_data_list_id(service_id);
   				                	dataList_server.setIs_removed(is_removed);
   				                	dataList_server.setKind(MainActivity.RequestCode.COLLECTION);
   				                }else{
   				                	dataList_server=DataList.getDataList(json);	
   				                	
   				                	System.out.println("pull watch " + dataList_server.toString());
   	   				                DataListDBHelper.pull(dataList_server);
   	   				                
   	   				                Watch watch = new Watch(-1, UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id
   	   				                , DataListDBHelper.find_by_server_data_list_id(dataList_server.server_data_list_id).id);
   	   				                WatchDBHelper.createOrUpdate(watch);
   	   				                
   	   				                dataLists.add(dataList_server);
   				                } 
   				                
   		                  }
//   			        	deletWatchList =  DataListDBHelper.deleteDataList(dataLists, MainActivity.RequestCode.data_list_type,  MainActivity.RequestCode.data_list_public);
   			              return null;
   			          }
   			        }.go();
   			return dataLists;
         }
    	public static void watch(final com.teamkn.model.DataList dataList,boolean watch) throws Exception{
    		System.out.println(watch + "   watch server " + dataList.toString());
    		new TeamknPutRequest<Void>( 收藏_data_list_watch_list + dataList.server_data_list_id + "/watch_setting",
    				new PostParamText("watch", watch+"")) {
						@Override
						public Void on_success(String response_text)
								throws Exception {
							return null;
						}
			}.go();
    	}
    	///api/data_lists/:id/commit_meta_list
    	public static List<User> commit_meta_list(com.teamkn.model.DataList dataList) throws Exception{
    		return new TeamknGetRequest<List<User>>(查看data_list被推送的列表 + dataList.server_data_list_id + "/commit_meta_list"
    				, new BasicNameValuePair("nil", "nil")) {
				@Override
				public List<User> on_success(String response_text)
						throws Exception {
					System.out.println("commit_meta_list = "+ response_text);
					List<User> list = new ArrayList<User>();
					JSONArray commit_meta_list = new JSONArray(response_text);
					for (int i = 0; i < commit_meta_list.length(); i++) {
		                JSONObject json = commit_meta_list.getJSONObject(i); 
		                int count = json.getInt("count");
		                JSONObject origin = json.getJSONObject("committer");
		                int user_id = origin.getInt("id");
		                String user_name = origin.getString("name");
		                String avatar_url = origin.getString("avatar_url");
		                byte[] user_avatar = null;
		                User user_query = UserDBHelper.find_by_server_user_id(user_id);
		                if(user_query.avatar_url!=null && !origin.isNull("avatar_url")  && !avatar_url.equals("")&& user_query.avatar_url.equals(avatar_url)){
		                	user_avatar = user_query.user_avatar;
		                }else if(!origin.isNull("avatar_url") && !avatar_url.equals("")){
		                	InputStream is = HttpApi.download_image(avatar_url);
		                	user_avatar = IOUtils.toByteArray(is);
		                }
		                long server_created_time = origin.getLong("server_created_time");
		                long server_updated_time = origin.getLong("server_updated_time");
		                
		                User user = new User(-1, user_id, user_name, user_avatar,avatar_url, server_created_time, server_updated_time);
		                UserDBHelper.createOrUpdate(user);
		                user.setCount(count);
		                list.add(user);
	                }
					return list;
				}
			}.go();
    	}
    	//列表差异处理界面，origin 列表和 forked 列表内容   列表差异处理界面
    	public static Map<Object, Object> diff(final int server_data_list_id,int committer_id) throws Exception{
    		return new TeamknGetRequest<Map<Object,Object>>(列表差异处理界面 + server_data_list_id + "/diff",
					new BasicNameValuePair("committer_id", committer_id+"")) {
						@Override
						public Map<Object, Object> on_success(String response_text) throws Exception {
							Map<Object, Object> map = new HashMap<Object, Object>();
							System.out.println("diff = "+ response_text);
							JSONObject json = new JSONObject(response_text); 
							
							JSONObject origin = json.getJSONObject("origin");
							JSONObject data_list_json_origin = origin.getJSONObject("data_list");
							com.teamkn.model.DataList dataList_origin = DataList.getDataList(data_list_json_origin);
							
							map.put("dataList_origin", dataList_origin);
							
							JSONArray data_items_origin = origin.getJSONArray("data_items");
							List<com.teamkn.model.DataItem> dataItems_origin = new ArrayList<com.teamkn.model.DataItem>();
							for(int i = 0 ; i<data_items_origin.length();i++){
								JSONObject item_json = data_items_origin.getJSONObject(i);
								com.teamkn.model.DataItem dataItem = DataItem.getDataItem(item_json, server_data_list_id);
								dataItems_origin.add(dataItem);
							}
							map.put("data_items_origin", dataItems_origin);
							
							JSONObject forked  = json.getJSONObject("forked");
							JSONObject data_list_json_forked = forked.getJSONObject("data_list");
							com.teamkn.model.DataList dataList_forked = DataList.getDataList(data_list_json_forked);
							map.put("dataList_forked", dataList_forked);
							JSONArray data_items_forked = forked.getJSONArray("data_items");
							List<com.teamkn.model.DataItem> dataItems_forked = new ArrayList<com.teamkn.model.DataItem>();
							for(int i = 0 ; i<data_items_forked.length();i++){
								JSONObject item_json = data_items_forked.getJSONObject(i);
								com.teamkn.model.DataItem dataItem = DataItem.getDataItem(item_json, server_data_list_id);
								dataItems_forked.add(dataItem);
								System.out.println("dataItem " + dataItem.position);
							}
							map.put("dataItems_forked", dataItems_forked);
							return map;
						}
			}.go();
    	}
//    	一个 data_list 中，接受某个推送作者的全部修改 [编辑]
//    			PUT /api/data_lists/:id/accept_commits
    	public static com.teamkn.model.DataList accept_commits( int server_data_list_id,int committer_id) throws Exception{
    		return new TeamknPutRequest<com.teamkn.model.DataList>(接受全部修改 + server_data_list_id + "/accept_commits"
    				,new PostParamText("committer_id", committer_id+"")) {
				@Override
				public com.teamkn.model.DataList on_success(String response_text) throws Exception {
					JSONObject jsonObject = new JSONObject(response_text);
					com.teamkn.model.DataList dataList = DataList.getDataList(jsonObject);
					DataListDBHelper.update_by_server_id(dataList);
					return dataList;
				}
			}.go();
    	}
//    	一个 data_list 中，拒绝某个推送作者的全部修改 [编辑]
    	public static com.teamkn.model.DataList reject_commits( int server_data_list_id,int committer_id) throws Exception{
    		return new TeamknPutRequest<com.teamkn.model.DataList>(拒绝全部修改 + server_data_list_id + "/reject_commits"
    				,new PostParamText("committer_id", committer_id+"")) {
				@Override
				public com.teamkn.model.DataList on_success(String response_text) throws Exception {
					JSONObject jsonObject = new JSONObject(response_text);
					com.teamkn.model.DataList dataList = DataList.getDataList(jsonObject);
					DataListDBHelper.update_by_server_id(dataList);
					return dataList;
				}
			}.go();
    	}
//    	在逐条处理中，获取 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
//    			GET /api/data_lists/:id/next_commits
    	public static com.teamkn.model.DataItem next_commits(  final int server_data_list_id,int committer_id) throws Exception{
    		return new TeamknGetRequest<com.teamkn.model.DataItem>(获取data_list推送内容 + server_data_list_id + "/next_commit"
    				,new BasicNameValuePair("committer_id", committer_id+"")) {
				@Override
				public com.teamkn.model.DataItem on_success(String response_text) throws Exception {
					JSONObject json = new JSONObject(response_text);
					System.out.println("next_commits =  " + response_text);
					return getDataItem_forked(json,server_data_list_id);
				}
			}.go();
    	}
//    	在逐条处理中，接受 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
//    			PUT /api/data_lists/:id/accept_next_commit
    	public static com.teamkn.model.DataItem accept_next_commit( final int server_data_list_id,int committer_id,final com.teamkn.model.DataItem last_dataItem) throws Exception{
    		return new TeamknPutRequest<com.teamkn.model.DataItem>(接受data_list推送内容 + server_data_list_id + "/accept_next_commit"
    				,new PostParamText("committer_id", committer_id+"")) {
				@Override
				public com.teamkn.model.DataItem on_success(String response_text) throws Exception {
					System.out.println("accept_next_commit:"+response_text);
					JSONObject jsonObject = new JSONObject(response_text);
					com.teamkn.model.DataItem dataItem = getDataItem_forked(jsonObject,server_data_list_id);					
					JSONObject data_item = jsonObject.getJSONObject("data_item");
		    		int server_id = data_item.getInt("server_id");
		    		String position = data_item.getString("position");
		    		last_dataItem.setPosition(position);
					last_dataItem.setServer_data_item_id(server_id);
					if(dataItem.seed!=null && !last_dataItem.getOperation().equals("REMOVE")){
						System.out.println("seed != null =  " + dataItem.toString());
						DataItemDBHelper.update_by_server_id(last_dataItem);
					}else if(last_dataItem.getOperation().equals("REMOVE")){
						DataItemDBHelper.delete_by_seed(last_dataItem.seed);
					}
					return dataItem;
				}
			}.go();
    	}
//    	在逐条处理中，拒绝 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
//    			PUT /api/data_lists/:id/reject_next_commit
    	public static com.teamkn.model.DataItem reject_next_commit( final int server_data_list_id,int committer_id) throws Exception{
    		System.out.println("reject_next_commit:"+server_data_list_id+":"+committer_id);
    		return new TeamknPutRequest<com.teamkn.model.DataItem>(拒绝data_list推送内容 + server_data_list_id + "/reject_next_commit"
    				,new PostParamText("committer_id", committer_id+"")) {
				@Override
				public com.teamkn.model.DataItem on_success(String response_text) throws Exception {
					JSONObject jsonObject = new JSONObject(response_text);
					com.teamkn.model.DataItem dataItem = getDataItem_forked(jsonObject,server_data_list_id);
					return dataItem;
				}
			}.go();
    	}
    	public static com.teamkn.model.DataItem getDataItem_forked(JSONObject jsonObject,int server_data_list_id) throws JSONException{
//    		int server_id = -1;

    		int next_commits_count = jsonObject.getInt("next_commits_count");
    		
    		JSONObject json = jsonObject.getJSONObject("next_commit");
    		
    		com.teamkn.model.DataItem dataItem = new com.teamkn.model.DataItem(); ;
//    		String json_seed = json.getString("seed");
    		if( !json.isNull("seed")){
    			String title  = json.getString("title");
//        		String title = "有时候没有 title";
                String kind   = json.getString("kind");
                String content  = json.getString("content");
                String url   = json.getString("url");
                String image_url  = json.getString("image_url");
                String seed = json.getString("seed");
                if (kind.equals(DataItemDBHelper.Kind.IMAGE)) {
                    HttpApi.DataItem.pull_image(server_data_list_id+"", image_url);
                }
                String operation = json.getString("operation");
    			
    			boolean conflict = json.getBoolean("conflict");
    			
    			String position = json.getString("position");
    			dataItem = new com.teamkn.model.DataItem(-1, title, content, url, kind, DataListDBHelper.find_by_server_data_list_id(server_data_list_id).id, position, -1,seed); 
    			
    			dataItem.setOperation(operation);
    			dataItem.setConflict(conflict);
    		}
    		dataItem.setNext_commits_count(next_commits_count);
			return dataItem;
    	}
    }
    public static class DataItem{
    	private static com.teamkn.model.DataItem getDataItem(JSONObject json , int data_list_server_id) throws JSONException{
    		 int server_id = json.getInt("id");
             String title  = json.getString("title");
             String kind   = json.getString("kind");
             String content  = json.getString("content");
             String url   = json.getString("url");
             String image_url  = json.getString("image_url");
             String seed = json.getString("seed");
             String position = json.getString("position");
             if (kind.equals(DataItemDBHelper.Kind.IMAGE)) {
                 HttpApi.DataItem.pull_image(server_id+"", image_url);
             }
             JSONObject json_data_list = json.getJSONObject("data_list");
             long data_list_server_updated_time = json_data_list.getLong("server_updated_time");
             com.teamkn.model.DataList dataList = DataListDBHelper.find_by_server_data_list_id(data_list_server_id);
             dataList.setServer_updated_time(data_list_server_updated_time);
             DataListDBHelper.update(dataList);
             
            com.teamkn.model.DataItem dataItem = new com.teamkn.model.DataItem(-1, title, content, url, kind, dataList.id, position, server_id,seed);
			return dataItem;
    	}
    	public static Map<Object,Object> pull(final com.teamkn.model.DataList dataList) throws Exception{  
   		 	final Map<Object,Object>  map = new HashMap<Object, Object>();
    		return new TeamknGetRequest<Map<Object,Object>>(获取_data_item + dataList.server_data_list_id+ "/data_items"){
		          @Override
		          public Map<Object,Object> on_success(String response_text) throws Exception {
		        	  System.out.println(" data_item pull response_text " + response_text);
		        	  JSONObject data_list_json = new JSONObject(response_text);
		        	  boolean read = data_list_json.getBoolean("read");
		        	  String has_commits = data_list_json.getString("has_commits");
//	        		  com.teamkn.model.DataList data_list = DataListDBHelper.find_by_server_data_list_id(data_list_server_id);
		        	  com.teamkn.model.DataList data_list = dataList;
		        	  data_list.setHas_commits(has_commits);
	        		  DataListDBHelper.update(data_list);
	        		  
	        		  DataListReading reading = new DataListReading(-1, data_list.id, UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id);
	        		  DataListReadingDBHelper.createOrUpdate(reading);
	        	
		        	  System.out.println(" read  有什么用意  " + read);
		        	  JSONArray data_list_array = data_list_json.getJSONArray("data_items");
		        	  List<com.teamkn.model.DataItem> dataItems = new ArrayList<com.teamkn.model.DataItem>();
	                  for (int i = 0; i < data_list_array.length(); i++) {
			                JSONObject json = data_list_array.getJSONObject(i);
			                com.teamkn.model.DataItem data_item_server = getDataItem(json,dataList.server_data_list_id);
			                DataItemDBHelper.pull(data_item_server); 
			                dataItems.add(data_item_server);
	                  }     
	                  DataItemDBHelper.remove_old(dataItems,dataList);
	                  
	                  JSONObject forked_from = data_list_json.getJSONObject("forked_from");
	                  User user = null;
	                  System.out.println("forked_from="+forked_from);
	                  if( !forked_from.isNull("creator") ){
	                	  JSONObject create = forked_from.getJSONObject("creator");
	                	  int user_id = create.getInt("id");
	                	  String user_name = create.getString("name");
	                	  String avatar_url = create.getString("avatar_url");
	                	  byte[] user_avatar = null;
			              User user_query = UserDBHelper.find_by_server_user_id(user_id);
			              if(user_query.avatar_url!=null && !create.isNull("avatar_url")  && !avatar_url.equals("")&& user_query.avatar_url.equals(avatar_url)){
			                	user_avatar = user_query.user_avatar;
			              }else if(!create.isNull("avatar_url") && !avatar_url.equals("")){
			                	InputStream is = HttpApi.download_image(avatar_url);
			                	user_avatar = IOUtils.toByteArray(is);
			              }
	                	  long server_created_time = create.getLong("server_created_time");
	                	  long server_updated_time = create.getLong("server_updated_time");
//	                	  User user = new User(-1, user_id, user_name, user_avatar, server_created_time, server_updated_time);
	                	  UserDBHelper.create(user_id, user_name, user_avatar,avatar_url, server_created_time, server_updated_time);
	                	  user = new User(-1, user_id, user_name, user_avatar, avatar_url, server_created_time, server_updated_time);
	                	  
	                	  System.out.println("pull create.toString() = " + user_id);
	                  }
	                  map.put("read", read);
	                  map.put("user", user);
		              return map;
		          }
		        }.go();
        }
    	public static List<com.teamkn.model.DataItem> fork_list(final int data_list_server_id) throws Exception{  
   		 	return new TeamknGetRequest<List<com.teamkn.model.DataItem>>(获取_data_item + data_list_server_id+ "/data_items"){
		          @Override
		          public List<com.teamkn.model.DataItem> on_success(String response_text) throws Exception {
		        	  System.out.println(" data_item pull response_text " + response_text);
		        	  JSONObject data_list_json = new JSONObject(response_text);
		        	  boolean read = data_list_json.getBoolean("read");

	        		  com.teamkn.model.DataList data_list = DataListDBHelper.find_by_server_data_list_id(data_list_server_id);
	        		  DataListReading reading = new DataListReading(-1, data_list.id, UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id);
	        		  DataListReadingDBHelper.createOrUpdate(reading);
	        	
		        	  System.out.println(" read  有什么用意  " + read);
		        	  JSONArray data_list_array = data_list_json.getJSONArray("data_items");
		        	  
		        	  List<com.teamkn.model.DataItem> list = new ArrayList<com.teamkn.model.DataItem>();
	                  for (int i = 0; i < data_list_array.length(); i++) {
			                JSONObject json = data_list_array.getJSONObject(i);
			                com.teamkn.model.DataItem data_item_server = getDataItem(json,data_list_server_id);
			                list.add(data_item_server);
	                  }  
		              return list;
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
                    File file = com.teamkn.model.DataItem.data_item_image_file(uuid);
                    FileOutputStream fos = new FileOutputStream(file);
                    IOUtils.copy(in, fos);
                    com.teamkn.model.DataItem.data_item_thumb_image_file(uuid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    	public static void order(final int from,String  left_position,String right_position) throws Exception{
       		System.out.println("dataItem order " + from + " : " + left_position + " : " + right_position);
       		new TeamknPutRequest<Void>( 排序_data_item + from + "/order",
       				new PostParamText("left_position", left_position),
       				new PostParamText("right_position",right_position)) {
    						@Override
    						public Void on_success(String response_text)
    								throws Exception {
    							  JSONObject json_result = new JSONObject(response_text);
    							  String new_position = json_result.getString("new_position");
    			                  System.out.println("data_item order response_text " + response_text);
    					          DataItemDBHelper.update_position(from,new_position);
    			            	  
    					          JSONObject data_list_time = json_result.getJSONObject("data_list");
    			            	  long data_list_server_updated_time = data_list_time.getLong("server_updated_time");
    			            	  com.teamkn.model.DataList dataList = DataListDBHelper.find(DataItemDBHelper.find_by_server_id(from).data_list_id);
    			            	  dataList.setServer_updated_time(data_list_server_updated_time);
    			            	  DataListDBHelper.update(dataList);
    							return null;
    						}
    			}.go();
       	}
   	public static String create(final com.teamkn.model.DataItem dataItem) throws Exception{
   		  String value = null;
   		  if(dataItem.kind.equals(DataItemDBHelper.Kind.URL)){
   			value = dataItem.url;
   		  }else{
   			value = dataItem.content;
   		  }
   		  final com.teamkn.model.DataList dataList = DataListDBHelper.find(dataItem.data_list_id);
		   return new TeamknPostRequest<String>( 创建_data_item + dataList.server_data_list_id+ "/data_items",
	            new PostParamText("title",dataItem.title),
	            new PostParamText("kind",dataItem.kind),
	            new PostParamText("value",value)
		   ) {
	              @Override
	              public String on_success(String response_text) throws Exception {
		                System.out.println("data_list pull response_text " + response_text);
		                JSONObject json = new JSONObject(response_text);
		                com.teamkn.model.DataItem data_item_server = getDataItem(json,dataList.server_data_list_id);
		                data_item_server.setId(dataItem.id);
		                DataItemDBHelper.update_by_id(data_item_server);
					    return null;   	
	              }
	              public String on_unprocessable_entity(String responst_text) {
	            	  if(!BaseUtils.is_str_blank(responst_text)){
	            		  DataItemDBHelper.delete(dataItem.id);
	            		  return responst_text;
	            	  }
	            	  System.out.println(responst_text);
					  return null; 
	              };
	              public String on_permission_denied(String responst_text) {
	            	 if(!BaseUtils.is_str_blank(responst_text)){
	            		  DataItemDBHelper.delete(dataItem.id);
	            		  return responst_text;
	            	}  
					return responst_text;
	              };
	     }.go();
    }
   	public static void create_image(final com.teamkn.model.DataItem dataItem,File image) throws Exception{
 		  
		   new TeamknPostRequest<Void>( 创建_data_item + dataItem.data_list_id+ "/data_items",
	            new PostParamText("title",dataItem.title),
	            new PostParamText("kind",dataItem.kind),
	            new PostParamFile("value", image.getPath(), "image/jpeg")
		   ) {
	              @Override
	              public Void on_success(String response_text) throws Exception {
		                System.out.println("data_list pull response_text " + response_text);
		                JSONObject json = new JSONObject(response_text);
		                com.teamkn.model.DataItem data_item_server = getDataItem(json,DataListDBHelper.find(dataItem.id).server_data_list_id);
		                data_item_server.setId(dataItem.id);
		                DataItemDBHelper.update_by_id(data_item_server);
					    return null;   	
	              }
	     }.go();
    }
   	public static String update(final com.teamkn.model.DataItem dataItem) throws Exception{
   		System.out.println("dataItem " + dataItem.toString());
   		String value = null;
 		 if(dataItem.kind.equals(DataItemDBHelper.Kind.URL)){
 			value = dataItem.url;
 		 }else{
 			value = dataItem.content;
 		 }
   		return new TeamknPutRequest<String>( 修改_data_item + dataItem.server_data_item_id,
   				new PostParamText("title", dataItem.title),
	            new PostParamText("value",value)) {
						@Override
						public String on_success(String response_text)
								throws Exception {
							JSONObject json = new JSONObject(response_text);
							com.teamkn.model.DataItem data_item_server = getDataItem(json,DataListDBHelper.find(dataItem.id).server_data_list_id);
							data_item_server.setId(dataItem.id);
							DataItemDBHelper.update_by_id(data_item_server);
							return null;
						}
						public String on_unprocessable_entity(String responst_text) {
							return responst_text;
					    };
			}.go();
   	}
   	public static void remove_contact(final int server_data_item_id)  throws Exception {
        new TeamknDeleteRequest<Void>( 删除_data_item + server_data_item_id){
              @Override
              public Void on_success(String response_text) throws Exception {
            	DataItemDBHelper.delete_by_server_id(server_data_item_id);
            	JSONObject data_list_time = new JSONObject(response_text);
          	    long data_list_server_updated_time = data_list_time.getLong("data_list_time");
          	    com.teamkn.model.DataList dataList = 
          	    		DataListDBHelper.find(DataItemDBHelper.find_by_server_id(server_data_item_id).data_list_id);
          	    dataList.setServer_updated_time(data_list_server_updated_time);
          	    DataListDBHelper.update(dataList);
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
