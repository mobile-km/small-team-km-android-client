package com.mindpin.base.utils;

import java.io.File;

import com.mindpin.model.Feed;

import android.os.Environment;

public class FileDirs {

	// 尝试获得一个文件夹引用，如果文件夹不存在就创建该文件夹
    public static File get_or_create_dir(String path){
    	File dir = new File(
			Environment.getExternalStorageDirectory().getPath() + path
		);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
    }
    
    public static File feed_data_dir(Feed feed){
    	int user_id = feed.creator.user_id;
    	int feed_id = feed.feed_id;
    	return get_or_create_dir("/mindpin/users/"+user_id+"/data"+"/feeds/"+feed_id);
    }
    
    public static File mindpin_downloaded_image_cache_dir(){
    	return get_or_create_dir("/mindpin/cache/downloaded_image/");
    }
    
    public static File mindpin_user_data_dir(int user_id){
    	return get_or_create_dir("/mindpin/users/"+user_id+"/data/");
    }
    
    public static File mindpin_user_cache_dir(int user_id){
    	return get_or_create_dir("/mindpin/users/"+user_id+"/cache/");
    }
    
    public final static File MINDPIN_DIR 	     = get_or_create_dir("/mindpin/");
    public final static File MINDPIN_CAPTURE_DIR = get_or_create_dir("/mindpin/capture/");
}
