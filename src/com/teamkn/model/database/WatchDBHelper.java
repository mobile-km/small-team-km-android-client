package com.teamkn.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.model.Watch;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class WatchDBHelper extends BaseModelDBHelper {
	public static void createOrUpdate(Watch watch){
		SQLiteDatabase db = get_write_db();
		ContentValues values = get_contentvalues(watch);
		if(find(watch).id <=0){
			db.insert(Constants.TABLE_WATCH, null, values);
		}else{
			db.update(Constants.TABLE_WATCH, values, Constants.KEY_ID + " = ? ", new String[]{find(watch).id+""});
		}
		db.close();
	}
	public static void delete(Watch watch){
		SQLiteDatabase db = get_write_db();
		if(find(watch).id > 0){
			db.delete(Constants.TABLE_WATCH, Constants.TABLE_WATCH_DATA_LIST_ID+ " = ? AND " +  Constants.TABLE_WATCH_USER_ID + " = ? " , 
			        new String[]{watch.data_list_id+"",watch.user_id+""});
		}
		db.close();
	}
	public static Watch find(Watch watch){
		Watch find_watch;
	    SQLiteDatabase db = get_read_db();
	    Cursor cursor = db.query(Constants.TABLE_WATCH, get_columns(), 
	        Constants.TABLE_WATCH_DATA_LIST_ID+ " = ? AND " +  Constants.TABLE_WATCH_USER_ID + " = ? " , 
	        new String[]{watch.data_list_id+"",watch.user_id+""}, 
	        null, null, null);
	    
	    
	    boolean has_value = cursor.moveToFirst();
	    if(has_value){
	    	find_watch = build_by_cursor(cursor);
	    }else{
	    	find_watch = Watch.WATCH;
	    }  
	    db.close();
	    return find_watch;
    }  
	private static Watch build_by_cursor(Cursor cursor) {
	    int id = cursor.getInt(0);
	    int data_list_id = cursor.getInt(1);
	    int user_id = cursor.getInt(2); 
	    return new Watch(id,data_list_id,user_id);
    }
	private static ContentValues get_contentvalues(Watch watch){
		ContentValues values = new ContentValues();
		values.put(Constants.TABLE_WATCH_DATA_LIST_ID,watch.data_list_id);
		values.put(Constants.TABLE_WATCH_USER_ID, watch.user_id);
		return values;
	}
	private static String[] get_columns(){
	    return new String[]{
	    	Constants.KEY_ID,
	        Constants.TABLE_WATCH_DATA_LIST_ID,
	        Constants.TABLE_WATCH_USER_ID,
	};

 }
}
