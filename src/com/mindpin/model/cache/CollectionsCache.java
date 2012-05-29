package com.mindpin.model.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.util.Log;

import com.mindpin.Logic.AccountManager;
import com.mindpin.base.utils.FileDirs;
import com.mindpin.model.AccountUser;
import com.mindpin.model.Collection;

public class CollectionsCache {

	public static void save(String collections) throws IOException {
		AccountUser current_user = AccountManager.current_user();
		if(current_user.is_nil()) return;

		FileUtils.writeStringToFile(get_collections_file(current_user.user_id), collections);
	}
	
	public static void delete(int user_id){
		File file = get_collections_file(user_id);
		file.delete();
	}
	
	public static List<Collection> get_current_user_collection_list(){
		List<Collection> list = new ArrayList<Collection>();
		
		AccountUser current_user = AccountManager.current_user();
		if(current_user.is_nil()) return list;
		
		try {
			String json_str = IOUtils.toString(new FileInputStream(get_collections_file(current_user.user_id)));
			list = Collection.build_list_by_json(json_str);
			return list;
		} catch (Exception e) {
			Log.e("CollectionsCache","get_current_user_collection_list",e);
			return list;
		}
	}
	

	private static File get_collections_file(int user_id){
		return new File(FileDirs.mindpin_user_cache_dir(user_id), "collections.json");
	}

}
