package com.mindpin.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mindpin.base.utils.BaseUtils;
import com.mindpin.model.base.BaseModel;

public class FeedComment extends BaseModel {
	public int comment_id;
	public String content;
	public long created_at;
	
	public ContactUser creator;
	
	public Feed feed;

	// 一般每个model都这样开头
	final public static FeedComment NIL_FEED_COMMENT = new FeedComment();
	private FeedComment(){
		set_nil();
	}
	
	private FeedComment(String json_str) throws Exception {
		JSONObject json = new JSONObject(json_str);
		
		this.comment_id = json.getInt("id");
		this.content    = json.getString("content");
		this.created_at = BaseUtils.parse_iso_time_string_to_long(json.getString("created_at"));
		
		String user_json_str = json.getString("user");
		String feed_json_str = json.getString("feed");
		
		this.creator = ContactUser.build(user_json_str);
		this.feed    = Feed.build(feed_json_str);
	}

	public static FeedComment build(String json_str) throws Exception{
		if(json_str == "null"){
			return FeedComment.NIL_FEED_COMMENT;
		}else{
			return new FeedComment(json_str);
		}
	}
	
	public static List<FeedComment> build_list_by_json(String json_str) throws Exception {
		List<FeedComment> list = new ArrayList<FeedComment>();
		JSONArray array = new JSONArray(json_str);
		
		for (int i = 0; i < array.length(); i++) {
			String feed_comment_json_str = array.getString(i);
			list.add(FeedComment.build(feed_comment_json_str));
		}
		return list;
	}

}
