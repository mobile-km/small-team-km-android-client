package com.teamkn.model.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.model.DataList;
import com.teamkn.model.Watch;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class DataListDBHelper extends BaseModelDBHelper {
  public static DataList update(DataList dataList){
	    SQLiteDatabase db = get_write_db();  
	    ContentValues values = get_contentvalues(dataList);
	    if(find(dataList.id).id <=0){ 
	    	db.insert(Constants.TABLE_DATA_LISTS, null, values);
	    	dataList = pull_last_data();
	    	System.out.println("insert dataList= " + dataList.toString());
	    }else{
	    	db.update(Constants.TABLE_DATA_LISTS, values, Constants.KEY_ID + " = ? ", new String[]{dataList.id+""});
	    	System.out.println("update dataList= " + dataList.toString());
	    }
	    db.close();
		return dataList;
  }
  public static void remove_by_server_id(DataList dataList){
	    SQLiteDatabase db = get_write_db();  
	    db.delete(Constants.TABLE_DATA_LISTS,Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " = ? ", new String[]{dataList.server_data_list_id+""});
	    db.close();
  }
  public static DataList update_by_server_id(DataList dataList){
	    SQLiteDatabase db = get_write_db();  
	    ContentValues values = get_contentvalues(dataList);
	    if(find_by_server_data_list_id(dataList.server_data_list_id).id <=0){ 
	    	db.insert(Constants.TABLE_DATA_LISTS, null, values);
	    	dataList = pull_last_data();
	    	System.out.println("insert dataList= " + dataList.toString());
	    }else{
	    	db.update(Constants.TABLE_DATA_LISTS, values, Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " = ? ", new String[]{dataList.server_data_list_id+""});
	    	System.out.println("update dataList= " + dataList.toString());
	    }
	    db.close();
		return dataList;
  }
  
  public static DataList pull_last_data(){
	  DataList datalist;
	  SQLiteDatabase db = get_write_db();
//	  select from users order by uid desc limit 1
	  String sql = "select * from " + Constants.TABLE_DATA_LISTS  + " order by "+ Constants.KEY_ID + " DESC limit 1 " ;
	    Cursor cursor = db.rawQuery(sql,null);
	    boolean has_value = cursor.moveToFirst();
	    if(has_value){
	    	datalist = build_by_cursor(cursor);
	    }else{
	    	datalist = DataList.NIL_DATA_LIST;
	    }
	    cursor.close();
	    db.close();
	    return datalist;
  }
  public static void pull(DataList dataList){
	    SQLiteDatabase db = get_write_db();  
	    ContentValues values = get_contentvalues(dataList);
	    if(find_by_server_data_list_id(dataList.server_data_list_id).id <= 0){ 
	    	db.insert(Constants.TABLE_DATA_LISTS, null, values);
	    	System.out.println("insert " + dataList.toString());
	    }else{
	    	db.update(Constants.TABLE_DATA_LISTS, values, Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " = ? ", new String[]{dataList.server_data_list_id+""});
	        System.out.println("update " + dataList.toString());
	    }
	    db.close();
}
  public static List<DataList> all(String data_list_type,String data_list_public) throws Exception {
	  System.out.println("all type:public = " + data_list_type + " : " + data_list_public);
	  SQLiteDatabase db = get_read_db();
      List<DataList> datalists = new ArrayList<DataList>();
      Cursor cursor;
      if(data_list_type.equals("ALL")){
    	  if(data_list_public.equals("true")){
    		  cursor= db.query(Constants.TABLE_DATA_LISTS,
                      get_columns(),
                      Constants.TABLE_DATA_LISTS_PUBLIC + " = ? AND " + Constants.TABLE_DATA_LISTS_FORKED_FROM_ID + " = ?", 
                      new String[]{data_list_public,-1+""}, null, null,
                      Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME + " DESC");
    	  }else if(data_list_public.equals("fork")){
    		  cursor= db.query(Constants.TABLE_DATA_LISTS,
                      get_columns(),
                      Constants.TABLE_DATA_LISTS_FORKED_FROM_ID + " > 0 AND " + Constants.TABLE_DATA_LISTS_USER_ID + " = ?", 
                      new String[]{UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id+""}, null, null,
                      Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME + " DESC");
    	  }else{
    		  cursor= db.query(Constants.TABLE_DATA_LISTS,
                      get_columns(),
                      Constants.TABLE_DATA_LISTS_USER_ID + " = ? AND " 
                      + Constants.TABLE_DATA_LISTS_FORKED_FROM_ID + " = ?", 
                      new String[]{UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id+"",-1+""}, null, null,
                      Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME + " DESC");
    	  }
    	  
      }else{	
    	  if(data_list_public.equals("true")){
    		  cursor= db.query(Constants.TABLE_DATA_LISTS,
                      get_columns(),
                      Constants.TABLE_DATA_LISTS_KIND + " = ? AND  " + Constants.TABLE_DATA_LISTS_PUBLIC + " = ? AND " + Constants.TABLE_DATA_LISTS_FORKED_FROM_ID + " = ?", 
                      new String[]{data_list_type,data_list_public,-1+""}, null, null,
                      Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME + " DESC");
    	  }else if(data_list_public.equals("fork")){
    		  cursor= db.query(Constants.TABLE_DATA_LISTS,
                      get_columns(),
//                      Constants.TABLE_DATA_LISTS_KIND + " = ? AND  " +
                      Constants.TABLE_DATA_LISTS_FORKED_FROM_ID + " > 0 AND " + Constants.TABLE_DATA_LISTS_USER_ID + " = ?",
                      new String[]{data_list_public,UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id+""}, null, null,
                      Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME + " DESC");
    	  }else{
    		  cursor= db.query(Constants.TABLE_DATA_LISTS,
                      get_columns(),
                      Constants.TABLE_DATA_LISTS_KIND + " = ? AND  " + Constants.TABLE_DATA_LISTS_USER_ID + " = ? AND " + Constants.TABLE_DATA_LISTS_FORKED_FROM_ID + " = ?", 
                      new String[]{data_list_type,UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id+"",-1+""}, null, null,
                      Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME + " DESC");
    	  }  
      }
      while (cursor.moveToNext()) {
    	  DataList datalist = build_by_cursor(cursor);
          datalists.add(datalist);
          System.out.println(datalist.toString());
      }
      cursor.close();
      db.close();
	return datalists;
  }
  public static DataList find(int id){
	DataList datalist;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_DATA_LISTS, get_columns(),
        Constants.KEY_ID + " = ?", 
        new String[]{id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
    	datalist = build_by_cursor(cursor);
    }else{
    	datalist = DataList.NIL_DATA_LIST;
    }
    
    cursor.close();
    db.close();
    return datalist;
  }
  
  public static boolean just_fored(int fored_from_id){
      SQLiteDatabase db = get_read_db();
      Cursor cursor = db.query(Constants.TABLE_DATA_LISTS, get_columns(),
    		  Constants.TABLE_DATA_LISTS_USER_ID + " = ? AND " 
      + Constants.TABLE_DATA_LISTS_FORKED_FROM_ID + " = ?", 
        new String[]{UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id+"",fored_from_id+""},null, null, null);

     boolean has_value = cursor.moveToFirst();
     cursor.close();
     db.close();
     return has_value;
  }
  
  public static List<DataList> all_by_watch_lists(List<Watch> watchs,String data_list_type){
      List<DataList> datalists = new ArrayList<DataList>();
      for(Watch watch : watchs){
    	  DataList dataList = find(watch.data_list_id);
    	  if(dataList.kind.equals(data_list_type)){
    		  datalists.add(dataList);
    	  }else if(data_list_type.equals(MainActivity.RequestCode.ALL)){
    		  datalists.add(dataList);
    	  }
      }
	  return datalists;
  }
  
  public static DataList find_by_title(String title){
	  DataList datalist;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_DATA_LISTS, get_columns(),
        Constants.TABLE_DATA_LISTS_TITLE + " = ?", 
        new String[]{title},null, null, null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
    	datalist = build_by_cursor(cursor);
    }else{
    	datalist = DataList.NIL_DATA_LIST;
    }
    
    cursor.close();
    db.close();
    return datalist;
  }
  public static DataList find_by_server_data_list_id(int server_data_list_id){
	DataList datalist;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_DATA_LISTS, get_columns(),
        Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " = ?", 
        new String[]{server_data_list_id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
    	datalist = build_by_cursor(cursor);
    }else{
    	datalist = DataList.NIL_DATA_LIST;
    }
    
    cursor.close();
    db.close();
    return datalist;
    
  }
  
  public static boolean is_exists(int server_data_list_id){
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_DATA_LISTS, get_columns(),
        Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " = ?", 
        new String[]{server_data_list_id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    
    cursor.close();
    db.close();
    return has_value;
  }

  private static DataList build_by_cursor(Cursor cursor) {
    int id = cursor.getInt(0);
    int user_id = cursor.getInt(1);
    String title = cursor.getString(2);
    String kind = cursor.getString(3);
    String public_boolean = cursor.getString(4);
    String has_commits = cursor.getString(5);
    int server_data_list_id = cursor.getInt(6);
    long server_created_time = cursor.getLong(7);
    long server_updated_time = cursor.getLong(8);
    int forked_from_id = cursor.getInt(9);
    return new DataList(id,user_id,title,kind,public_boolean,has_commits,server_data_list_id,server_created_time,server_updated_time,forked_from_id);
  }

  private static String[] get_columns() {
    return new String[]{
        Constants.KEY_ID,
        Constants.TABLE_DATA_LISTS_USER_ID,
        Constants.TABLE_DATA_LISTS_TITLE,
        Constants.TABLE_DATA_LISTS_KIND,
        Constants.TABLE_DATA_LISTS_PUBLIC,
        Constants.TABLE_DATA_LISTS_HAS_COMMITS,
        Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID,
        Constants.TABLE_DATA_LISTS_SERVER_CREATED_TIME,
        Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME,
        Constants.TABLE_DATA_LISTS_FORKED_FROM_ID
    };
  }
  private static ContentValues get_contentvalues(DataList dataList){
		ContentValues values = new ContentValues();
		values.put(Constants.TABLE_DATA_LISTS_USER_ID,dataList.user_id);
	    values.put(Constants.TABLE_DATA_LISTS_TITLE,dataList.title);
	    values.put(Constants.TABLE_DATA_LISTS_KIND,dataList.kind);
	    values.put(Constants.TABLE_DATA_LISTS_PUBLIC,dataList.public_boolean);
	    values.put(Constants.TABLE_DATA_LISTS_HAS_COMMITS,dataList.has_commits);
	    values.put(Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID,dataList.server_data_list_id);
	    values.put(Constants.TABLE_DATA_LISTS_SERVER_CREATED_TIME,dataList.server_created_time);
	    values.put(Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME,dataList.server_updated_time);
	    values.put(Constants.TABLE_DATA_LISTS_FORKED_FROM_ID,dataList.forked_from_id);
		return values;
  }
  public static void remove_old(List<DataList> requestList,String data_list_type,String data_list_public) throws Exception{
		List<DataList> oldList = all(data_list_type,data_list_public);
		for(DataList oldItem : oldList){
			boolean has = false;
			for(DataList requstItem : requestList){
				if(oldItem.server_data_list_id==requstItem.server_data_list_id){
					has = true;
					break;
				}
			}
			if(!has){
				remove_by_server_id(oldItem);
			}
		}
  }
  public static List<DataList> deleteDataList(List<DataList> requestList,String data_list_type,String data_list_public) throws Exception{
	  List<DataList> oldList = all(data_list_type,data_list_public);
	  List<DataList> deleteList = new ArrayList<DataList>();
	  for(DataList oldItem : oldList){
			boolean has = false;
			for(DataList requstItem : requestList){
				if(oldItem.server_data_list_id==requstItem.server_data_list_id){
					has = true;
					break;
				}
			}
			if(!has){
//				remove_by_server_id(oldItem);
				deleteList.add(oldItem);
			}
		}
		return deleteList; 
  }
  public static boolean is_delete(List<DataList> dataLists , DataList list){
	  for(DataList item : dataLists){
		  if(list.server_data_list_id == item.server_data_list_id){
			  return true;
		  }  
	  }
	return false;  
  }
}
