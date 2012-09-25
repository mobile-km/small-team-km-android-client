package com.teamkn.model.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.model.DataItem;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class DataItemDBHelper extends BaseModelDBHelper {
		public class Kind {
	        public static final String TEXT = "TEXT";
	        public static final String IMAGE = "IMAGE";
	        public static final String URL = "URL";
	    }
	  public static void update_by_id(DataItem dataItem){
		    SQLiteDatabase db = get_write_db();  
		    ContentValues values = new ContentValues();
		    values.put(Constants.TABLE_DATA_ITEMS_TITLE,dataItem.title);
		    values.put(Constants.TABLE_DATA_ITEMS_CONTENT,dataItem.content);
		    values.put(Constants.TABLE_DATA_ITEMS_URL,dataItem.url);
		    values.put(Constants.TABLE_DATA_ITEMS_KIND,dataItem.kind);
		    values.put(Constants.TABLE_DATA_ITEMS_DATA_LIST_ID,dataItem.data_list_id);
		    values.put(Constants.TABLE_DATA_ITEMS_POSITION,dataItem.position);
		    values.put(Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID,dataItem.server_data_item_id);
		    
		    if(find(dataItem.id).id <= 0){ 
		    	db.insert(Constants.TABLE_DATA_ITEMS, null, values);
		    	System.out.println("insert= " + dataItem.toString());
		    }else{
		    	db.update(Constants.TABLE_DATA_ITEMS, values, Constants.KEY_ID + " = ? ", new String[]{dataItem.id+""});
		    	System.out.println("update= " + dataItem.toString());
		    }
		    db.close();
	  }
	  
	  public static void pull(DataItem dataItem){
		    SQLiteDatabase db = get_write_db();  
		    ContentValues values = new ContentValues();
		    values.put(Constants.TABLE_DATA_ITEMS_TITLE,dataItem.title);
		    values.put(Constants.TABLE_DATA_ITEMS_CONTENT,dataItem.content);
		    values.put(Constants.TABLE_DATA_ITEMS_URL,dataItem.url);
		    values.put(Constants.TABLE_DATA_ITEMS_KIND,dataItem.kind);
		    values.put(Constants.TABLE_DATA_ITEMS_DATA_LIST_ID,dataItem.data_list_id);
		    values.put(Constants.TABLE_DATA_ITEMS_POSITION,dataItem.position);
		    values.put(Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID,dataItem.server_data_item_id);
		    
		    if(find_by_server_id(dataItem.server_data_item_id).id <= 0){ 
		    	db.insert(Constants.TABLE_DATA_ITEMS, null, values);
		    }else{
		    	db.update(Constants.TABLE_DATA_ITEMS, values, Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID + " = ? ", new String[]{dataItem.server_data_item_id+""});
		    }
		    db.close();
	  }
	  public static List<DataItem> all(int data_list_id) throws Exception {
		  SQLiteDatabase db = get_read_db();
	      List<DataItem> dataItems = new ArrayList<DataItem>();
	      Cursor cursor= db.query(Constants.TABLE_DATA_ITEMS,
	                      get_columns(),
	                      Constants.TABLE_DATA_ITEMS_DATA_LIST_ID + " = ? ", 
	                      new String[]{data_list_id+""}, null, null,
	                      Constants.TABLE_DATA_ITEMS_POSITION + " ASC");//asc或desc（即升级或降序）
	
	      while (cursor.moveToNext()) {
	    	  DataItem datalist = build_by_cursor(cursor);
	    	  dataItems.add(datalist);
	      }
	      cursor.close();
	      db.close();
		return dataItems;
	  }
	  public static DataItem fist_data_item(int data_list_id) throws Exception {
		  SQLiteDatabase db = get_read_db();
	      List<DataItem> dataItems = new ArrayList<DataItem>();
	      Cursor cursor= db.query(Constants.TABLE_DATA_ITEMS,
	                      get_columns(),
	                      Constants.TABLE_DATA_ITEMS_DATA_LIST_ID + " = ? ", 
	                      new String[]{data_list_id+""}, null, null,
	                      Constants.KEY_ID + " DESC");//asc或desc（即升级或降序）
	
	      while (cursor.moveToNext()) {
	    	  DataItem datalist = build_by_cursor(cursor);
	    	  dataItems.add(datalist);
	      }
	      cursor.close();
	      db.close();
		return dataItems.get(0);
	  }
	  public static DataItem find(int id){
		  DataItem dataItem;
	    SQLiteDatabase db = get_read_db();
	    Cursor cursor = db.query(Constants.TABLE_DATA_ITEMS, get_columns(),
	        Constants.KEY_ID + " = ?", 
	        new String[]{id+""},null, null, null);
	
	    boolean has_value = cursor.moveToFirst();
	    if(has_value){
	    	dataItem = build_by_cursor(cursor);
	    }else{
	    	dataItem = DataItem.NIL_DATA_ITEM;
	    }
	    System.out.println("   d  "  +  dataItem.id);
	    cursor.close();
	    db.close();
	    return dataItem;
	  }
	  public static DataItem find(String title,int data_list_id){
		DataItem dataItem;
	    SQLiteDatabase db = get_read_db();
	    Cursor cursor = db.query(Constants.TABLE_DATA_ITEMS, get_columns(),
	        Constants.TABLE_DATA_ITEMS_TITLE + " = ? AND " + Constants.TABLE_DATA_ITEMS_DATA_LIST_ID + " = ? " , 
	        new String[]{title+"",data_list_id+""},null, null, null);
	
	    boolean has_value = cursor.moveToFirst();
	    if(has_value){
	    	dataItem = build_by_cursor(cursor);
	    }else{
	    	dataItem = DataItem.NIL_DATA_ITEM;
	    }
	    System.out.println("   d  "  +  dataItem.id);
	    cursor.close();
	    db.close();
	    return dataItem;
	  }
	  public static DataItem find_url(String url){
			DataItem dataItem;
		    SQLiteDatabase db = get_read_db();
		    Cursor cursor = db.query(Constants.TABLE_DATA_ITEMS, get_columns(),
		        Constants.TABLE_DATA_ITEMS_URL + " = ?", 
		        new String[]{url+""},null, null, null);
		
		    boolean has_value = cursor.moveToFirst();
		    if(has_value){
		    	dataItem = build_by_cursor(cursor);
		    }else{
		    	dataItem = DataItem.NIL_DATA_ITEM;
		    }
		    System.out.println("   d  "  +  dataItem.id);
		    cursor.close();
		    db.close();
		    return dataItem;
	 }
	  
	  public static void delete(int id){
	    SQLiteDatabase db = get_write_db();
	    if(find(id).id>0){
	    	db.delete(Constants.TABLE_DATA_ITEMS, Constants.KEY_ID + " = ?", 
			        new String[]{id+""});
	    }
	    db.close();
	  }
	  public static void delete_by_server_id(int server_id){
	    SQLiteDatabase db = get_read_db();
	    if(find_by_server_id(server_id).id>0){
	    	db.delete(Constants.TABLE_DATA_ITEMS,
	    	        Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID + " = ?", 
	    	        new String[]{server_id+""});
	    }
	    db.close();
	  }
  	  public static void delete_by_id(int id){
		    SQLiteDatabase db = get_write_db();
		    if(find(id).id>0){
		    	db.delete(Constants.TABLE_DATA_ITEMS,
		    			Constants.KEY_ID + " = ?", new String[]{id+""});
		    }
		    db.close();
	  }
	  public static DataItem find_by_server_id(int server_id){
		  DataItem dataItem;
		    SQLiteDatabase db = get_read_db();
		    Cursor cursor = db.query(Constants.TABLE_DATA_ITEMS, get_columns(),
		    		Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID + " = ?", 
		        new String[]{server_id+""},null, null, null);
	
		    boolean has_value = cursor.moveToFirst();
		    if(has_value){
		    	dataItem = build_by_cursor(cursor);
		    }else{
		    	dataItem = DataItem.NIL_DATA_ITEM;
		    }
		    cursor.close();
		    db.close();
		    return dataItem;  
	  }
	  public static boolean is_exists(int server_data_item_id){
	    SQLiteDatabase db = get_read_db();
	    Cursor cursor = db.query(Constants.TABLE_DATA_ITEMS, get_columns(),
	        Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID + " = ?", 
	        new String[]{server_data_item_id+""},null, null, null);
	
	    boolean has_value = cursor.moveToFirst();
	    
	    cursor.close();
	    db.close();
	    return has_value;
	  }

	  private static DataItem build_by_cursor(Cursor cursor) {
	    int id = cursor.getInt(0);
	    String title = cursor.getString(1);
	    String content = cursor.getString(2);
	    String url = cursor.getString(3);
	    String kind = cursor.getString(4);
	    int data_list_id = cursor.getInt(5);
	    int position = cursor.getInt(6);
	    int server_data_item_id = cursor.getInt(7);
	    return new DataItem(id, title, content, url, kind, data_list_id, position,server_data_item_id);
	  }
  
	  private static String[] get_columns() {
	    return new String[]{
	        Constants.KEY_ID,
	        Constants.TABLE_DATA_ITEMS_TITLE,
	        Constants.TABLE_DATA_ITEMS_CONTENT,
	        Constants.TABLE_DATA_ITEMS_URL,
	        Constants.TABLE_DATA_ITEMS_KIND,
	        Constants.TABLE_DATA_ITEMS_DATA_LIST_ID,
	        Constants.TABLE_DATA_ITEMS_POSITION,
	        Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID
	    };
	  }
	public static void update_position(int server_id, int position) {
		SQLiteDatabase db = get_write_db();
		
		DataItem dataItem = find_by_server_id(server_id);
		dataItem.setPosition(position);
		
		ContentValues values = new ContentValues();
	    values.put(Constants.TABLE_DATA_ITEMS_TITLE,dataItem.title);
	    values.put(Constants.TABLE_DATA_ITEMS_CONTENT,dataItem.content);
	    values.put(Constants.TABLE_DATA_ITEMS_URL,dataItem.url);
	    values.put(Constants.TABLE_DATA_ITEMS_KIND,dataItem.kind);
	    values.put(Constants.TABLE_DATA_ITEMS_DATA_LIST_ID,dataItem.data_list_id);
	    values.put(Constants.TABLE_DATA_ITEMS_POSITION,dataItem.position);
	    values.put(Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID,dataItem.server_data_item_id);
	    
	    if(dataItem.id>0){
	    	db.update(Constants.TABLE_DATA_ITEMS, values, Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID + " = ?", new String[]{dataItem.server_data_item_id+""});
	    }
	    db.close();
	}
}
