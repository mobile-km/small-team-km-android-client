package com.teamkn.model;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MusicInfo  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String music_title;
	public String album_title;
	public String author_name;
	public String cover_src;
	public String file_url;
	public long server_created_time;
	public long server_updated_time;
	
	
	public static MusicInfo build_by_json(String music_info) {
		Gson gson = new Gson();
		return gson.fromJson(music_info, MusicInfo.class);    
	}
	
	public static ArrayList<MusicInfo> build_list_by_json(String music_json) {
		Gson gson = new Gson();
		
		Type collectionType = new TypeToken<ArrayList<MusicInfo>>(){}.getType();
		ArrayList<MusicInfo> music_list = gson.fromJson(music_json, collectionType);
		
		return music_list;
	}
}
