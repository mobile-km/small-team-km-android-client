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

import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.RegisterActivity;
import com.teamkn.base.http.PostParamFile;
import com.teamkn.base.http.PostParamText;
import com.teamkn.base.http.TeamknDeleteRequest;
import com.teamkn.base.http.TeamknGetRequest;
import com.teamkn.base.http.TeamknHttpRequest;
import com.teamkn.base.http.TeamknPostRequest;
import com.teamkn.base.http.TeamknPutRequest;
import com.teamkn.model.AccountUser;
import com.teamkn.model.Product;
import com.teamkn.model.User;
import com.teamkn.model.VersionCheck;
import com.teamkn.model.database.UserDBHelper;

public class HttpApi {

//    public static final String SITE = "http://192.168.1.38:9527";
//	public static final String SITE = "http://192.168.1.26:9527";
//	public static final String SITE = "http://teamkn.mindpin.com";
	public static final String SITE = "http://dev.kaid.me";
//	http://dev.kaid.me/api/products/search
	
    // 各种路径常量
	public static final String 版本检查 = "/check_version";
	
	//设置是否显示 指引提示
	public static final String 设置是否显示指引提示 = "/api/account/change_show_tip";
	
    public static final String 用户注册 = "/signup_submit";
    
    public static final String 用户登录 = "/login";
    
//    查看某个指定用户的信息和列表
    public static final String 查看某个指定用户的信息和列表 = "/api/users/";
    
//    用户可以手动FOLLOW其他用户
    public static final String 用户可以手动FOLLOW_OR_UNFOLLOW其他用户 = "/api/users/";
    
//  按照用户名搜索 [编辑]
//GET '/api/users/search'
    public static final String 按照用户名搜索 = "/api/users/search";
//  查看用户的 FOLLOW 用户集合 [编辑]
    public static final String 查看FOLLOW集合 = "/api/users/";
    
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
    public static final String FOLLOW用户的列表汇总     =  "/api/data_lists/follows_list";
    public static final String 其他用户的公开列表集合     =  "/api/users/";
    
    public static final String 搜索公共_data_list   =  "/api/data_lists/search_public_timeline";
    public static final String 搜索个人书签_data_list =  "/api/data_lists/search_mine_watch";
    public static final String 迁出一个_data_list = "/api/data_lists/";
    public static final String 迁出的data_list列表 = "/api/data_lists/forked_list";
    public static final String 被迁出的data_list列表 = "/api/data_lists/be_forked_list";
    
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
    
    //QRCode 
    public static final String 搜索_QRCode = "/api/products/search/";
    
    // LoginActivity
    public static VersionCheck get_version(String now_version) throws Exception{
    	return new TeamknGetRequest<VersionCheck>(
    			版本检查,
    			new BasicNameValuePair("version",now_version)) {
					@Override
					public VersionCheck on_success(String response_text)throws Exception {
						System.out.println("get_version "+response_text);
						JSONObject json = new JSONObject(response_text);
						String status = json.getString("status");
						JSONObject newest_version_change_log = json.getJSONObject("newest_version_change_log");
						String version = newest_version_change_log.getString("version");
						String change_log = newest_version_change_log.getString("change_log");
						
						return new VersionCheck(status, version, change_log);
					}
		}.go();
    }
    // 设置是否显示 指引提示  POST '/account/change_show_tip'
    public static boolean change_show_tip(boolean is_show_tip) throws Exception {
    	String is_show_tip_str = "false" ;
    	if(is_show_tip){
    		is_show_tip_str = "true";
    	}
    	System.out.println("设置是否显示指引提示 " + 设置是否显示指引提示 + " : " + is_show_tip_str);
    	return new TeamknPostRequest<Boolean>(
    			设置是否显示指引提示,
                new PostParamText("is_show_tip", is_show_tip_str)
        ) {
            @Override
            public Boolean on_success(String response_text) throws Exception {
            	System.out.println("change_show_tip response_text " + response_text);
            	JSONObject json = new JSONObject(response_text);
                AccountManager.login(get_cookies(), json.toString());
                return true;
            }
        }.go();
    }
    
    
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
            	System.out.println("json authenticate " + response_text);
                JSONObject json = new JSONObject(response_text);
                AccountManager.login(get_cookies(), json.toString());
                return true;
            }
        }.go();
    }
//    用户可以查看某个指定用户的信息和列表 [编辑]
//    GET '/api/users/:id'
    public static AccountUser get_user_msg(final int  service_user_id) throws Exception{
    	return new TeamknGetRequest<AccountUser>(
    			查看某个指定用户的信息和列表 + service_user_id,
    			new BasicNameValuePair("service_user_id",service_user_id+"")) {
					@Override
					public AccountUser on_success(String response_text)throws Exception {
						System.out.println("get_user_msg " + service_user_id);
						System.out.println("get_user_msg "+response_text);
						JSONObject json = new JSONObject(response_text);
						return new AccountUser(json.toString());
					}
		}.go();
    }
//	    用户可以手动FOLLOW其他用户 [编辑]
//	 POST '/api/users/:id/follow'  unfollow
    public static boolean follow_or_unfollow(final int service_user_id, final boolean is_follow) throws Exception {
    	String follow_or_unfollow = "follow";
    	if(is_follow){
    		follow_or_unfollow = "follow";
    	}else{
    		follow_or_unfollow = "unfollow";
    	}
    	return new TeamknPostRequest<Boolean>(
    			用户可以手动FOLLOW_OR_UNFOLLOW其他用户 + service_user_id + "/" + follow_or_unfollow,
                new PostParamText("service_user_id", service_user_id + ""),
                new PostParamText("is_follow", is_follow + "")
        ) {
            @Override
            public Boolean on_success(String response_text) throws Exception {
               System.out.println("用户可以手动FOLLOW_OR_UNFOLLOW其他用户  " + 用户可以手动FOLLOW_OR_UNFOLLOW其他用户  + " : " + service_user_id + " : " + is_follow);
            	return true;
            }
        }.go();
    }
//            按照用户名搜索 [编辑]
//    GET '/api/users/search'
    public static List<AccountUser> search_user(String query,int page ,int per_page) throws Exception{
    	return new TeamknGetRequest<List<AccountUser>>(
    			按照用户名搜索,
    			new BasicNameValuePair("query",    query),
    			new BasicNameValuePair("page",    page+""),
    			new BasicNameValuePair("per_page",per_page+"")) {
					@Override
					public List<AccountUser> on_success(String response_text)throws Exception {
						List<AccountUser> accountUsers = new ArrayList<AccountUser>();
						System.out.println("search_user "+response_text);
						JSONArray json_arr = new JSONArray(response_text);
						for(int i = 0 ; i < json_arr.length() ; i ++ ){
							JSONObject json = json_arr.getJSONObject(i);
							accountUsers.add(getAccountUser(json));
						}
						return accountUsers;
					}
		}.go();
    }
    
//    查看用户的 FOLLOW 用户集合 [编辑]
//    GET '/api/users/:id/follows'
    public static List<AccountUser> follows_or_fans(boolean is_follows,final int  service_user_id,int page ,int per_page) throws Exception{
    	String follows_or_fan = "follows" ;
    	if(is_follows){
    		follows_or_fan =  "follows" ;
    	}else{
    		follows_or_fan =  "fans" ;
    	}
    	return new TeamknGetRequest<List<AccountUser>>(
    			查看FOLLOW集合 + service_user_id + "/" + follows_or_fan,
    			new BasicNameValuePair("page",    page+""),
    			new BasicNameValuePair("per_page",per_page+"")) {
					@Override
					public List<AccountUser> on_success(String response_text)throws Exception {
						List<AccountUser> accountUsers = new ArrayList<AccountUser>();
						System.out.println("follows " + service_user_id);
						System.out.println("follows "+response_text);
						JSONArray json_arr = new JSONArray(response_text);
						for(int i = 0 ; i < json_arr.length() ; i ++ ){
							JSONObject json = json_arr.getJSONObject(i);
							accountUsers.add(getAccountUser(json));
						}
						return accountUsers;
					}
		}.go();
    }
    public static AccountUser getAccountUser(JSONObject user_and_followed_json) throws JSONException, IOException{
    	boolean followed = user_and_followed_json.getBoolean("followed");
    	JSONObject user_json = user_and_followed_json.getJSONObject("user");
    	int user_id = user_json.getInt("id");
    	String name = user_json.getString("name");
    	String sign = user_json.getString("sign");
    	String avatar_url = user_json.getString("avatar_url");
    	byte[] avatar = null ;
    	if(avatar_url != null && !avatar_url.equals("")){
            InputStream is = HttpApi.download_image(avatar_url);
            avatar = IOUtils.toByteArray(is);
        }
    	AccountUser user = new AccountUser(user_id, name, sign, avatar_url, avatar, false,followed);
    	return user;
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
                System.out.println("json register user " + response_text);
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
            
//            System.out.println(data_list_server_created_time + " :  " + data_list_server_updated_time);
            
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
		public static List<com.teamkn.model.DataList> pull(String kind,int page , int per_page) throws Exception{  
			return new TeamknGetRequest<List<com.teamkn.model.DataList>>(获取_data_list,
	            new BasicNameValuePair("kind", kind),
	            new BasicNameValuePair("page", page+""),
	            new BasicNameValuePair("per_page",per_page+"")
	            ){
		          @Override
		          public List<com.teamkn.model.DataList> on_success(String response_text) throws Exception {
		        	  List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>();
		        	  JSONArray data_list_array = new JSONArray(response_text);
		        	  System.out.println("pull response_text =  " + response_text);
	                  for (int i = 0; i < data_list_array.length(); i++) {
			                JSONObject json = data_list_array.getJSONObject(i);
			                com.teamkn.model.DataList dataList_server =getDataList(json);	
			                dataLists.add(dataList_server);
			          }  
		              return dataLists;
		          }
		          public List<com.teamkn.model.DataList> on_unprocessable_entity(String responst_text) {
					return null;
			      };
		        }.go();     
        }
    	
    	public static com.teamkn.model.DataList create(final com.teamkn.model.DataList dataList) throws Exception{
           
 		   return new TeamknPostRequest<com.teamkn.model.DataList>( 创建_data_list,
 	            new PostParamText("data_list[title]",dataList.title),
 	            new PostParamText("data_list[kind]",dataList.kind),
 	            new PostParamText("data_list[public]",dataList.public_boolean)
 		   ) {
 	              @Override
 	              public com.teamkn.model.DataList on_success(String response_text) throws Exception {
		                System.out.println("data_list pull response_text " + response_text);
		                JSONObject json = new JSONObject(response_text);
		                com.teamkn.model.DataList dataList_server =getDataList(json);
		                dataList_server.setId(dataList.id);
					    return dataList_server;   	
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
							System.out.println("update server  " + response_text);
							JSONObject json = new JSONObject(response_text);
						    com.teamkn.model.DataList dataList_server =getDataList(json);
						    dataList_server.setId(dataList.id);							    
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
//				              int server_id = data_list_array.getInt(i);
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
//				              int server_id = data_list_array.getInt(i);
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
//				              int server_id = data_list_array.getInt(i);
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
				                dataLists.add(dataList_server);
		                  }
			              return null;
			          }
			        }.go();
			return dataLists;
        }
//    	其他用户的 公开列表 集合 [编辑]
//    	GET '/api/users/:id/public_data_lists'
    	public static List<com.teamkn.model.DataList> user_public_data_lists(int page , int per_page , int user_id) throws Exception{  
      		 System.out.println("user_public_data_lists  " + 其他用户的公开列表集合 + user_id + "/public_data_lists"); 
      		 final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
   	   		 new TeamknGetRequest<Void>(其他用户的公开列表集合 + user_id + "/public_data_lists",
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
   				                dataLists.add(dataList_server);
   		                  }
   			              return null;
   			          }
   			        }.go();
   			return dataLists;
           }
    	
    	
    	
//    	用户可以在一个界面查看自己已FOLLOW用户的列表汇总 [编辑]
//    	GET '/api/data_lists/follows'
    	public static List<com.teamkn.model.DataList> follows_list(int per_page , long since_timestamp ) throws Exception{  
      		System.out.println("FOLLOW用户的列表汇总 " + FOLLOW用户的列表汇总 + " :　" + per_page +  "  :  " + since_timestamp); 
    		final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
   	   		 new TeamknGetRequest<Void>(FOLLOW用户的列表汇总,
   		            new BasicNameValuePair("per_page", per_page+"")
//   	   		 ,new BasicNameValuePair("since_timestamp", since_timestamp+""
   		          ){
   			          @Override
   			          public Void on_success(String response_text) throws Exception {
   			        	  System.out.println("FOLLOW用户的列表汇总  response_text  " + response_text);
   			        	  JSONArray data_list_array = new JSONArray(response_text);
   			        	  for (int i = 0; i < data_list_array.length(); i++) {
   				                JSONObject json = data_list_array.getJSONObject(i);
   				                com.teamkn.model.DataList dataList_server =getDataList(json);		
   				                dataLists.add(dataList_server);
   				                System.out.println("dataList_server " + dataList_server.toString());
   		                  }
   			        	  return null;
   			          }
   			        }.go();
   			return dataLists;
           }
    	//迁出一个data_list
    	public static com.teamkn.model.DataList fork(final com.teamkn.model.DataList dataList) throws Exception{
    		System.out.println("fork  " + 迁出一个_data_list + dataList.server_data_list_id + "/fork");
    		return new TeamknPutRequest<com.teamkn.model.DataList>( 迁出一个_data_list + dataList.server_data_list_id + "/fork",
    				new PostParamText("any_params", 1+"")) {
				@Override
				public com.teamkn.model.DataList on_success(String response_text) throws Exception {
					System.out.println("fork = " + response_text);
					JSONObject json = new JSONObject(response_text);
				    com.teamkn.model.DataList dataList_server =getDataList(json);
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
   				                dataLists.add(dataList_server);
   		                  } 
   			        	  return null;
   			          }
   			        }.go();
   			return dataLists;
         }
    	//被Fork的列表
    	public static List<com.teamkn.model.DataList> be_forked_list(int page , int per_page) throws Exception{  
     		 final List<com.teamkn.model.DataList> dataLists = new ArrayList<com.teamkn.model.DataList>(); 
  	   		 new TeamknGetRequest<Void>(被迁出的data_list列表,
  		            new BasicNameValuePair("page", page+""),
  		            new BasicNameValuePair("per_page", per_page+"")
  		            ){
  			          @Override
  			          public Void on_success(String response_text) throws Exception {
  			        	  System.out.println("be_forked_list:"+response_text);
  			        	  JSONArray data_list_array = new JSONArray(response_text);
  			        	  for (int i = 0; i < data_list_array.length(); i++) {
  				                JSONObject json = data_list_array.getJSONObject(i);
  				                com.teamkn.model.DataList dataList_server =getDataList(json);		
  				                dataLists.add(dataList_server);
  		                  } 
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
					return dataList;
				}
			}.go();
    	}
//    	一个 data_list 中，接受某个推送作者的剩下的全部修改 [编辑]
//
//    			PUT /api/data_lists/:id/accept_rest_commits
    	public static com.teamkn.model.DataList accept_rest_commits( int server_data_list_id,int committer_id) throws Exception{
    		return new TeamknPutRequest<com.teamkn.model.DataList>(接受全部修改 + server_data_list_id + "/accept_rest_commits"
    				,new PostParamText("committer_id", committer_id+"")) {
				@Override
				public com.teamkn.model.DataList on_success(String response_text) throws Exception {
					JSONObject jsonObject = new JSONObject(response_text);
					com.teamkn.model.DataList dataList = DataList.getDataList(jsonObject);
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
					return dataList;
				}
			}.go();
    	}
//    	一个 data_list 中，拒绝某个推送作者的剩下的全部修改 [编辑]
//
//    			PUT /api/data_lists/:id/reject_rest_commits
    	public static com.teamkn.model.DataList reject_rest_commits( int server_data_list_id,int committer_id) throws Exception{
    		return new TeamknPutRequest<com.teamkn.model.DataList>(拒绝全部修改 + server_data_list_id + "/reject_rest_commits"
    				,new PostParamText("committer_id", committer_id+"")) {
				@Override
				public com.teamkn.model.DataList on_success(String response_text) throws Exception {
					JSONObject jsonObject = new JSONObject(response_text);
					com.teamkn.model.DataList dataList = DataList.getDataList(jsonObject);
					return dataList;
				}
			}.go();
    	}
//    	在逐条处理中，获取 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
//    			GET /api/data_lists/:id/next_commits
    	public static Map<Object, Object> next_commits(  final int server_data_list_id,int committer_id) throws Exception{
    		return new TeamknGetRequest<Map<Object, Object>>(获取data_list推送内容 + server_data_list_id + "/next_commit"
    				,new BasicNameValuePair("committer_id", committer_id+"")) {
				@Override
				public Map<Object, Object> on_success(String response_text) throws Exception {
					JSONObject json = new JSONObject(response_text);
					System.out.println("next_commits =  " + response_text);
					return getDataItem_forked(json,server_data_list_id);
				}
			}.go();
    	}
//    	在逐条处理中，接受 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
//    			PUT /api/data_lists/:id/accept_next_commit
    	public static Map<Object, Object> accept_next_commit( final int server_data_list_id,int committer_id) throws Exception{
    		return new TeamknPutRequest<Map<Object, Object>>(接受data_list推送内容 + server_data_list_id + "/accept_next_commit"
    				,new PostParamText("committer_id", committer_id+"")) {
				@Override
				public Map<Object, Object> on_success(String response_text) throws Exception {
					System.out.println("accept_next_commit:"+response_text);
					JSONObject jsonObject = new JSONObject(response_text);
					return getDataItem_forked(jsonObject,server_data_list_id);
				}
			}.go();
    	}
//    	在逐条处理中，拒绝 一个 data_list 中某一个推送作者推送的内容中下一个推送的内容 [编辑]
//    			PUT /api/data_lists/:id/reject_next_commit
    	public static Map<Object, Object> reject_next_commit( final int server_data_list_id,int committer_id) throws Exception{
    		System.out.println("reject_next_commit:"+server_data_list_id+":"+committer_id);
    		return new TeamknPutRequest<Map<Object, Object>>(拒绝data_list推送内容 + server_data_list_id + "/reject_next_commit"
    				,new PostParamText("committer_id", committer_id+"")) {
				@Override
				public Map<Object, Object> on_success(String response_text) throws Exception {
					JSONObject jsonObject = new JSONObject(response_text);
					return getDataItem_forked(jsonObject,server_data_list_id);
				}
			}.go();
    	}
    	public static Map<Object, Object> getDataItem_forked(JSONObject jsonObject,int server_data_list_id) throws JSONException, IOException{
    		
    		int next_commits_count = jsonObject.getInt("next_commits_count");
    		JSONObject json = jsonObject.getJSONObject("next_commit");
    		com.teamkn.model.DataItem dataItem = new com.teamkn.model.DataItem();
    		if(!json.isNull("seed")){
    			String operation = json.getString("operation");
    			String title  = json.getString("title");
                String kind   = json.getString("kind");
                String content  = json.getString("content");
                String url   = json.getString("url");
                String image_url  = json.getString("image_url");
                String seed = json.getString("seed");
                if (kind.equals(com.teamkn.model.DataItem.Kind.IMAGE)) {
                    HttpApi.DataItem.pull_image(server_data_list_id+"", image_url);
                }
    			boolean conflict = json.getBoolean("conflict");
    			String position = json.getString("position");
    			
    			dataItem = new com.teamkn.model.DataItem(-1, title, content, url, kind, server_data_list_id, position, -1,seed);
    			dataItem.setOperation(operation);
    			dataItem.setConflict(conflict);
    		}

    		dataItem.setNext_commits_count(next_commits_count);

			JSONObject origin_json = jsonObject.getJSONObject("origin");
			JSONObject data_list_json = origin_json.getJSONObject("data_list");
			com.teamkn.model.DataList dataList = DataList.getDataList(data_list_json);
			
			JSONArray data_items_json = origin_json.getJSONArray("data_items");
			List<com.teamkn.model.DataItem> dataItems = new ArrayList<com.teamkn.model.DataItem>();
			for(int i = 0 ; i < data_items_json.length() ; i++ ){
				JSONObject data_item_json = data_items_json.getJSONObject(i);
				dataItems.add(DataItem.getDataItem(data_item_json, server_data_list_id));
			}
			
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("dataItem", dataItem);
			map.put("dataList", dataList);
			map.put("dataItems", dataItems);
			return map;
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
             if (kind.equals(com.teamkn.model.DataItem.Kind.IMAGE)) {
                 HttpApi.DataItem.pull_image(server_id+"", image_url);
             }
//             JSONObject json_data_list = json.getJSONObject("data_list");
//             long data_list_server_updated_time = json_data_list.getLong("server_updated_time");
             
            com.teamkn.model.DataItem dataItem = new com.teamkn.model.DataItem(-1, title, content, url, kind, data_list_server_id, position, server_id,seed);
              
    		if(!json.getJSONObject("product").isNull("name")){
    			 JSONObject product_json =  json.getJSONObject("product");
    			 Product product = Product.get_product_by_json(product_json);
    			 dataItem.setProduct(product);
    			 System.out.println("product: " + product.toString());
    		 }
            return dataItem;
    	}
    	public static Map<Object,Object> pull(final com.teamkn.model.DataList dataList) throws Exception{  
   		 	final Map<Object,Object>  map = new HashMap<Object, Object>();
    		return new TeamknGetRequest<Map<Object,Object>>(获取_data_item + dataList.server_data_list_id+ "/data_items"){
		          @Override
		          public Map<Object,Object> on_success(String response_text) throws Exception {
		        	  System.out.println(" data_item pull response_text " + response_text);
		        	  JSONObject data_list_json = new JSONObject(response_text);
		        	  // 获取 是否 读过 已经 收藏
		        	  boolean read = data_list_json.getBoolean("read");
		        	  String has_commits = data_list_json.getString("has_commits");
	        		  boolean watched = data_list_json.getBoolean("watched");
	        		  boolean forked = data_list_json.getBoolean("forked");
	        		  dataList.setHas_commits(has_commits);
	        		  dataList.setWatched(watched);
	        		  dataList.setForked(forked);
	        		  
		        	  //获取 data_items  列表数据
		        	  JSONArray data_list_array = data_list_json.getJSONArray("data_items");
		        	  List<com.teamkn.model.DataItem> dataItems = new ArrayList<com.teamkn.model.DataItem>();
	                  for (int i = 0; i < data_list_array.length(); i++) {
			                JSONObject json = data_list_array.getJSONObject(i);
			                com.teamkn.model.DataItem data_item_server = getDataItem(json,dataList.server_data_list_id);
			                dataItems.add(data_item_server);
	                  }     
	                  //获取 创建者  信息
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
	                	  UserDBHelper.create(user_id, user_name, user_avatar,avatar_url, server_created_time, server_updated_time);
	                	  user = new User(-1, user_id, user_name, user_avatar, avatar_url, server_created_time, server_updated_time);
	                  }
	                  map.put("read", read);
	                  map.put("user", user);
	                  map.put("data_list", dataList);
	                  map.put("dataItems", dataItems);
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
//		        	  boolean read = data_list_json.getBoolean("read");
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
//    							  JSONObject json_result = new JSONObject(response_text);
//    							  String new_position = json_result.getString("new_position");
//    					          JSONObject data_list_time = json_result.getJSONObject("data_list");
//    			            	  long data_list_server_updated_time = data_list_time.getLong("server_updated_time");
    							return null;
    						}
    			}.go();
       	}
   	public static String create(final com.teamkn.model.DataItem dataItem) throws Exception{
   		  String value = null;
   		  if(dataItem.kind.equals(com.teamkn.model.DataItem.Kind.URL)){
   			value = dataItem.url;
   		  }else if(dataItem.kind.equals(com.teamkn.model.DataItem.Kind.PRODUCT)){
   			value = dataItem.product.id+"";
   		  }else{
   			value = dataItem.content;
   		  }
   		  
		   return new TeamknPostRequest<String>( 创建_data_item + dataItem.server_data_list_id+ "/data_items",
	            new PostParamText("title",dataItem.title),
	            new PostParamText("kind",dataItem.kind),
	            new PostParamText("value",value)
		   ) {
	              @Override
	              public String on_success(String response_text) throws Exception {
		                System.out.println("data_list pull response_text " + response_text);
					    return null;   	
	              }
	              public String on_unprocessable_entity(String responst_text) {
					  return responst_text; 
	              };
	              public String on_permission_denied(String responst_text) { 
					return responst_text;
	              };
	     }.go();
    }
   	public static void create_image(final com.teamkn.model.DataItem dataItem,File image) throws Exception{
 		  
		   new TeamknPostRequest<Void>( 创建_data_item + dataItem.server_data_list_id+ "/data_items",
	            new PostParamText("title",dataItem.title),
	            new PostParamText("kind",dataItem.kind),
	            new PostParamFile("value", image.getPath(), "image/jpeg")
		   ) {
	              @Override
	              public Void on_success(String response_text) throws Exception {
		                System.out.println("data_list pull response_text " + response_text);
//		                JSONObject json = new JSONObject(response_text);
//		                com.teamkn.model.DataItem data_item_server = getDataItem(json,dataItem.server_data_list_id);
					    return null;   	
	              }
	     }.go();
    }
   	public static String update(final com.teamkn.model.DataItem dataItem) throws Exception{
   		System.out.println("dataItem " + dataItem.toString());
   		String value = null;
 		 if(dataItem.kind.equals(com.teamkn.model.DataItem.Kind.URL)){
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
							com.teamkn.model.DataItem data_item_server = getDataItem(json,dataItem.server_data_list_id);
							data_item_server.setId(dataItem.id);
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
//            	JSONObject data_list_time = new JSONObject(response_text);
//          	    long data_list_server_updated_time = data_list_time.getLong("data_list_time");
                return null;
              }
        }.go();
    }
   	
    }
    
    public static List<Product> get_qrcode_search(String code) throws Exception{
    	System.out.println("code:" + code);
    	return new TeamknGetRequest<List<Product>>(搜索_QRCode,
    			new BasicNameValuePair("code", code)){
			@Override
			public List<Product> on_success(String response_text) throws Exception {
				System.out.println("------------get_qrcode_search--------------");
				System.out.println("get_qrcode_search :  "+ response_text);
				List<Product> products = new ArrayList<Product>();
				JSONArray json_arr = new JSONArray(response_text);
				for(int i = 0 ;i < json_arr.length() ;i++){
					Product item= Product.get_product_by_json(json_arr.getJSONObject(i));
					System.out.println(item.toString());
					products.add(item);
				}
				return products;
			}
	   }.go();
		
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
