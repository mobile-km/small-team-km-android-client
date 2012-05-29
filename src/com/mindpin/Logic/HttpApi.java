package com.mindpin.Logic;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Location;

import com.mindpin.base.http.MindpinDeleteRequest;
import com.mindpin.base.http.MindpinGetRequest;
import com.mindpin.base.http.MindpinHttpRequest;
import com.mindpin.base.http.MindpinPostRequest;
import com.mindpin.base.http.MindpinPutRequest;
import com.mindpin.base.http.ParamFile;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.model.AccountUser;
import com.mindpin.model.ContactUser;
import com.mindpin.model.Feed;
import com.mindpin.model.FeedComment;
import com.mindpin.model.cache.CollectionsCache;
import com.mindpin.model.database.AccountUserDBHelper;
import com.mindpin.model.database.FeedDBHelper;

public class HttpApi {
	
	public static final String SITE = "http://www.mindpin.com";
	
	// 各种路径常量
	public static final String 用户登录				= "/login";
	
	public static final String 同步数据 			    = "/api0/mobile_data_syn";
	public static final String 我的收件箱主题列表 	= "/api0/home_timeline";
	
	public static final String 收集册的主题列表 		= "/api0/collections/feeds";
	public static final String 创建收集册 			= "/api0/collections/create";
	public static final String 删除收集册 			= "/api0/collections/delete";
	public static final String 收集册改名 			= "/api0/collections/rename";
	
	public static final String 主题详情				= "/api0/feeds/show";
	public static final String 创建主题 		= "/api0/feeds/create";
	public static final String 上传主题图片 			= "/api0/feeds/upload_photo";
	
	public static final String 创建主题评论			= "/api0/comments/create";
	public static final String 主题的评论列表		= "/api0/comments/list";
	public static final String 删除主题评论			= "/api0/comments/delete";
	public static final String 回复主题评论			= "/api0/comments/reply";
	public static final String 当前用户收到的评论    = "/api0/comments/received";
	
	public static final String 用户的关注列表		= "/api0/contacts/followings";
	
	// LoginActivity
	// 用户登录请求
	public static boolean user_authenticate(String email, String password) throws Exception {
		return new MindpinPostRequest<Boolean>(
			用户登录, 
			new BasicNameValuePair("email", email),
			new BasicNameValuePair("password", password)
		){
			@Override
			public Boolean on_success(String response_text) throws Exception{
				JSONObject json = new JSONObject(response_text);
				String user_info = ((JSONObject)json.get("user")).toString();
				AccountManager.login(get_cookies(), user_info);
				return true;
			}
		}.go();
	}
	
	// MainActivity
	public static boolean mobile_data_syn() throws Exception {
		return new MindpinGetRequest<Boolean>(
			同步数据
		){
			@Override
			public Boolean on_success(String response_text) throws Exception{
				JSONObject json = new JSONObject(response_text);
				String collections = ((JSONArray)json.get("collections")).toString();
				String user_info = ((JSONObject)json.get("user")).toString();
				
				AccountUserDBHelper.save(new AccountUser(get_cookies(), user_info));
				CollectionsCache.save(collections);
				return true;
			}
		}.go();
	}
	
	
	public static Void send_text_feed(String title, String detail, 
			List<Integer> select_collection_ids, boolean send_tsina, Location current_location) throws Exception{
		
		String select_collection_ids_str = BaseUtils.integer_list_to_string(select_collection_ids);
		return new MindpinPostRequest<Void>(
				创建主题, 
			new BasicNameValuePair("title", title),
			new BasicNameValuePair("detail", detail),
			new BasicNameValuePair("collection_ids", select_collection_ids_str),
			new BasicNameValuePair("send_tsina", send_tsina ? "true":"false"),
			new BasicNameValuePair("location", BaseUtils.location_to_string(current_location))
		){
			@Override
			public Void on_success(String response_text) throws Exception{
				return null;
			}
		}.go();
	}

	// 发送主题
	public static Void send_photo_feed(String title, String detail,
			List<Integer> photo_ids, List<Integer> select_collection_ids, boolean send_tsina, Location current_location) throws Exception {
		
		String photo_string = BaseUtils.integer_list_to_string(photo_ids); 
		String select_collection_ids_str = BaseUtils.integer_list_to_string(select_collection_ids);
		
		return new MindpinPostRequest<Void>(
				创建主题, 
			new BasicNameValuePair("title", title),
			new BasicNameValuePair("detail", detail),
			new BasicNameValuePair("photo_ids", photo_string),
			new BasicNameValuePair("collection_ids", select_collection_ids_str),
			new BasicNameValuePair("send_tsina", send_tsina ? "true":"false"),
			new BasicNameValuePair("location", BaseUtils.location_to_string(current_location))
		){
			@Override
			public Void on_success(String response_text) throws Exception{
				return null;
			}
		}.go();
	}
	
	public static Integer upload_photo(String image_path) throws Exception{
		String upload_image_path = CompressPhoto.get_compress_file_path(image_path);
		return new MindpinPostRequest<Integer>(
			上传主题图片,
			new ParamFile("file", upload_image_path, "image/jpeg")
		){

			@Override
			public Integer on_success(String response_text) throws Exception {
				JSONObject json = new JSONObject(response_text);
				return json.getInt("photo_id");
			}
		}.go();
	}

	public static InputStream download_image(String image_url) {
		try {
			HttpGet httpget = new HttpGet(image_url);
			httpget.setHeader("User-Agent", "android");
			HttpResponse response = MindpinHttpRequest.get_httpclient_instance().execute(httpget);
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

	public static boolean create_collection(String title) throws Exception {
		return new MindpinPostRequest<Boolean>(
				创建收集册, 
				new BasicNameValuePair("title", title)
				){
			@Override
			public Boolean on_success(String response_text) throws Exception {
				CollectionsCache.save(response_text);
				return true;
			}
		}.go();
	}
	
	public static boolean destroy_collection(Integer collection_id) throws Exception{
		return new MindpinDeleteRequest<Boolean>(
				删除收集册,
				new BasicNameValuePair("collection_id", collection_id.toString())){
			@Override
			public Boolean on_success(String response_text) throws Exception {
				CollectionsCache.save(response_text);
				return true;
			}
			
		}.go();
	}
	
	public static boolean change_collection_name(Integer collection_id, String title) throws UnsupportedEncodingException, Exception{
		return new MindpinPutRequest<Boolean>(
				收集册改名,
				new BasicNameValuePair("title", title),
				new BasicNameValuePair("collection_id", collection_id.toString())){

					@Override
					public Boolean on_success(String response_text)
							throws Exception {
						CollectionsCache.save(response_text);
						return true;
					}
		}.go();
	}
	
	public static Feed read_feed(Integer feed_id) throws Exception {
		return new MindpinGetRequest<Feed>(
				主题详情,
				new BasicNameValuePair("id", feed_id.toString())){

					@Override
					public Feed on_success(String response_text)
							throws Exception {
						Feed feed = Feed.build(response_text);
						FeedDBHelper.create_or_update(feed);
						return feed;
					}
		}.go();
	}
	
	public static boolean add_feed_commment(Integer feed_id, String content) throws UnsupportedEncodingException, Exception{
		return new MindpinPostRequest<Boolean>(
				创建主题评论, 
				new BasicNameValuePair("feed_id", feed_id.toString()),
				new BasicNameValuePair("content", content)
				){
			@Override
			public Boolean on_success(String response_text) throws Exception {
				return true;
			}
		}.go();
	}
	
	public static List<FeedComment> get_feed_comments(Integer feed_id) throws Exception{
		return new MindpinGetRequest<List<FeedComment>>(
				主题的评论列表, 
				new BasicNameValuePair("feed_id", feed_id.toString())
				){
			@Override
			public List<FeedComment> on_success(String response_text) throws Exception {
				return FeedComment.build_list_by_json(response_text);
			}
		}.go();
	}
	
	public static void destroy_feed_commment(Integer comment_id) throws Exception{
		new MindpinDeleteRequest<Void>(
				删除主题评论,
				new BasicNameValuePair("comment_id", comment_id.toString())
				) {
			@Override
			public Void on_success(String response_text) throws Exception {
				return null;
			}
		}.go();
	}
	
	public static Boolean reply_feed_comment(Integer comment_id, String content) throws Exception{
		return new MindpinPostRequest<Boolean>(
				回复主题评论,
				new BasicNameValuePair("comment_id", comment_id.toString()),
				new BasicNameValuePair("content", content)
				) {
			public Boolean on_success(String response_text) throws Exception {
				return true;
			};
		}.go();
	}
	
	public static List<FeedComment> received_comments() throws Exception{
		return received_comments(-1);
	}
	
	public static List<FeedComment> received_comments(Integer max_id) throws Exception{
		BasicNameValuePair param;
		if (max_id > 0) {
			param = new BasicNameValuePair("max_id", max_id.toString());
		} else {
			param = new BasicNameValuePair("max_id", "");
		}
		return new MindpinGetRequest<List<FeedComment>>(
				当前用户收到的评论,
				param
				) {
					@Override
					public List<FeedComment> on_success(
							String response_text) throws Exception {
						return FeedComment.build_list_by_json(response_text);
					}
		}.go();
	}
	
	public static List<ContactUser> get_current_user_followings() throws Exception{
		return get_followings(AccountManager.current_user().user_id);
	}
	
	public static List<ContactUser> get_followings(Integer user_id)
			throws Exception {
		return new MindpinGetRequest<List<ContactUser>>(
			用户的关注列表,
			new BasicNameValuePair("user_id", user_id.toString())
		) {
			@Override
			public List<ContactUser> on_success(String response_text)
					throws Exception {
				return ContactUser.build_list_by_json(response_text);
			}
		}.go();
	}
	
	public static class IntentException extends Exception{
		private static final long serialVersionUID = -4969746083422993611L;
	}

	public static class FeedsApi{
		public static List<Feed> get_home_timeline() throws Exception{
			return request_我的收件箱主题列表();
		}
		public static List<Feed> get_home_timeline(Integer max_id) throws Exception {
			BasicNameValuePair param = new BasicNameValuePair("max_id", max_id.toString());
			return request_我的收件箱主题列表(param);
		}
		private static List<Feed> request_我的收件箱主题列表(BasicNameValuePair... param) throws Exception{
			return new MindpinGetRequest<List<Feed>>(我的收件箱主题列表, param) {

				@Override
				public List<Feed> on_success(String response_text)
						throws Exception {
					List<Feed> feeds = Feed.build_list_by_json(response_text);
					for (Feed feed : feeds) {
						FeedDBHelper.create_or_update(feed);
					}
					return feeds;
				}
			}.go();
		}
	}

	public static class CollectionApi{
		public static List<Feed> get_collection_feeds(int collection_id) throws Exception {
			return request_收集册的主题列表(collection_id, -1);
		}

		public static List<Feed> get_collection_feeds(int collection_id, int max_id) throws Exception {
			return request_收集册的主题列表(collection_id, max_id);
		}
		private static List<Feed> request_收集册的主题列表(Integer collection_id, Integer max_id) throws Exception{			
			return new MindpinGetRequest<List<Feed>>(
					收集册的主题列表, 
					new BasicNameValuePair("collection_id", collection_id.toString()),
					new BasicNameValuePair("max_id", ( max_id > 0 ? max_id.toString() : ""))
			){
				@Override
				public List<Feed> on_success(String response_text) throws Exception {
					List<Feed> list = Feed.build_list_by_json(response_text);
					for (Feed feed : list) {
						FeedDBHelper.create_or_update(feed);
					}
					return list;
				}
			}.go();
		}
	}
}
