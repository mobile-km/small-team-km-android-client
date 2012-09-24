package com.teamkn.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.model.DataListReading;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class DataListReadingDBHelper extends BaseModelDBHelper {
	public static void createOrUpdate(DataListReading reading){
		SQLiteDatabase db = get_write_db();
		ContentValues values = get_contentvalues(reading);
		
		db.close();
	}
	public static DataListReading find(DataListReading reading){
		DataListReading search_reading;
	    SQLiteDatabase db = get_read_db();

	    Cursor cursor = db.query(Constants.TABLE_DATA_LIST_READINGS, get_columns(), 
	        Constants.TABLE_DATA_LIST_READINGS_DATA_LIST_ID+ " = ? AND " +  Constants.TABLE_DATA_LIST_READINGS_USER_ID + " = ? " , 
	        new String[]{reading.data_list_id+"",reading.user_id+""}, 
	        null, null, null);
	    
	    boolean has_value = cursor.moveToFirst();
	    if(has_value){
	    	search_reading = build_by_cursor(cursor);
	    }else{
	    	search_reading = DataListReading.DATALISTREADING;
	    }  
	    db.close();
	    return search_reading;
 }  
	private static DataListReading build_by_cursor(Cursor cursor) {
	    int id = cursor.getInt(0);
	    int data_list_id = cursor.getInt(1);
	    int user_id = cursor.getInt(2); 
	    return new DataListReading(id,data_list_id,user_id);
 }
	private static ContentValues get_contentvalues(DataListReading reading){
		ContentValues values = new ContentValues();
		values.put(Constants.TABLE_DATA_LIST_READINGS_DATA_LIST_ID,reading.data_list_id);
		values.put(Constants.TABLE_DATA_LIST_READINGS_USER_ID, reading.user_id);
		return values;
	}
	private static String[] get_columns(){
	    return new String[]{
	    	Constants.KEY_ID,
	        Constants.TABLE_DATA_LIST_READINGS_DATA_LIST_ID,
	        Constants.TABLE_DATA_LIST_READINGS_USER_ID,
	};

 }
}
