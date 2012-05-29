package com.mindpin.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mindpin.base.utils.BaseUtils;
import com.mindpin.model.base.BaseModel;

public class Feed extends BaseModel {
	
	public int feed_id;
	public String title;
	public String detail;
	public List<String> photos_middle;
	public List<String> photos_large;
	public List<String> photos_thumbnail;
	public List<Double> photos_ratio;
	public long updated_at;
	public String from;
	
	public ContactUser creator;
	
	public String json;
	
	// 一般每个model都这样开头
	final public static Feed NIL_FEED = new Feed();
	private Feed(){
		set_nil();
	}
	
	private Feed(String json) throws Exception{
		JSONObject json_obj = new JSONObject(json);
		
		this.feed_id 			= (Integer) json_obj.get("id");
		this.title 				= (String) json_obj.get("title");
		this.detail 			= (String) json_obj.get("detail");
		this.photos_middle 		= json_array_to_array_list((JSONArray) json_obj.get("photos_middle"));
		this.photos_large 		= json_array_to_array_list((JSONArray) json_obj.get("photos_large"));
		this.photos_thumbnail 	= json_array_to_array_list((JSONArray) json_obj.get("photos_thumbnail"));
		this.photos_ratio 		= json_array_to_array_list_double((JSONArray) json_obj.get("photos_ratio"));
		this.updated_at 		= BaseUtils.parse_iso_time_string_to_long((String)json_obj.get("updated_at"));
		
		String user_json_str = json_obj.getString("user");
		this.creator = ContactUser.build(user_json_str);
		
		this.json = json;
	}
	
	public static Feed build(String json_str) throws Exception{
		if(json_str == "null"){
			return Feed.NIL_FEED;
		}else{
			return new Feed(json_str);
		}
	}
	
	public static List<Feed> build_list_by_json(String json_str) throws Exception {
		List<Feed> feeds = new ArrayList<Feed>();
		
		JSONArray json_arr = new JSONArray(json_str);
		for (int i = 0; i < json_arr.length(); i++) {
			String feed_json_str = json_arr.getString(i);
			
			feeds.add(Feed.build(feed_json_str));
		}
		return feeds;
	}
	
	private static List<String> json_array_to_array_list(JSONArray json_array) throws JSONException {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < json_array.length(); i++) {
			String url = json_array.getString(i);
			list.add(url);
		}
		return list;
	}

	private static List<Double> json_array_to_array_list_double(JSONArray json_array) throws JSONException {
		List<Double> list = new ArrayList<Double>();
		for (int i = 0; i < json_array.length(); i++) {
			double ratio = json_array.getDouble(i);
			list.add(ratio);
		}
		return list;
	}
}
