package com.teamkn.model.database;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.Logic.HttpApi;
import com.teamkn.model.User;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class UserDBHelper extends BaseModelDBHelper {
  public static void create(int user_id,
      String user_name, byte[] user_avatar, String avatar_url , long server_created_time, long server_updated_time){
    SQLiteDatabase db = get_write_db();
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_USERS__USER_ID,user_id);
    values.put(Constants.TABLE_USERS__USER_NAME,user_name);
    values.put(Constants.TABLE_USERS__USER_AVATAR,user_avatar);
    values.put(Constants.TABLE_USERS__AVATAR_URL,avatar_url);
    values.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,server_created_time);
    values.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,server_updated_time);
    db.insert(Constants.TABLE_USERS, null, values);
    db.close();
  }
  public static void createOrUpdate(User user){
	    SQLiteDatabase db = get_write_db();
	    ContentValues values = new ContentValues();
	    values.put(Constants.TABLE_USERS__USER_ID,user.user_id);
	    values.put(Constants.TABLE_USERS__USER_NAME,user.user_name);
	    values.put(Constants.TABLE_USERS__USER_AVATAR,user.user_avatar);
	    values.put(Constants.TABLE_USERS__AVATAR_URL,user.avatar_url);
	    values.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,user.server_created_time);
	    values.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,user.server_updated_time);
	    if(find_by_server_user_id(user.user_id).user_name==null || find_by_server_user_id(user.user_id).user_name.equals("")){
	    	db.insert(Constants.TABLE_USERS, null, values);
	    }else{
	    	db.update(Constants.TABLE_USERS, values, Constants.TABLE_USERS__USER_ID + " = ? ", new String[]{user.user_id+""});
	    }
	    db.close();
  }
  public static void create(int user_id,
      String user_name, String user_avatar_url, long server_created_time, long server_updated_time){
    byte[] user_avatar = null;
    if(user_avatar_url != null && !user_avatar_url.equals("")){
      InputStream is = HttpApi.download_image(user_avatar_url);
      try {
        user_avatar = IOUtils.toByteArray(is);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    create(user_id,user_name,user_avatar,user_avatar_url,server_created_time,server_updated_time);
  }
  public static void updateAccount(int user_id , String user_name,String user_avatar_url){
	  SQLiteDatabase db = get_write_db();  
	  byte[] user_avatar = null;
	    if(user_avatar_url != null && !user_avatar_url.equals("")){
	      InputStream is = HttpApi.download_image(user_avatar_url);
	      try {
	        user_avatar = IOUtils.toByteArray(is);
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
	    User user = find(get_client_user_id(user_id));
	    user.setUser_avatar(user_avatar);
	    user.setUser_name(user_name);
	    ContentValues values = new ContentValues();
	    values.put(Constants.TABLE_USERS__USER_NAME,user.user_name);
	    values.put(Constants.TABLE_USERS__USER_AVATAR,user.user_avatar);
	    values.put(Constants.TABLE_USERS__AVATAR_URL,user.avatar_url);
	    values.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,user.server_created_time);
	    values.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,user.server_updated_time);
	    
	    db.update(Constants.TABLE_USERS, values, Constants.TABLE_USERS__USER_ID + " = ? ", new String[]{user.user_id+""});
        
	    System.out.println("user_id : name : avatar = " + user_id + " : " + user_name  + " : " + user_avatar_url );
	    
	    db.close();
  }
  public static int get_client_user_id(int server_user_id){
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_USERS, 
        new String[]{Constants.KEY_ID},
        Constants.TABLE_USERS__USER_ID + " = ?", 
        new String[]{server_user_id+""},null, null, null);

    cursor.moveToFirst();
    int id = cursor.getInt(0);
    cursor.close();
    db.close();
    return id;
  }
  
  public static User find(int client_user_id){
    User user;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_USERS, get_columns(),
        Constants.KEY_ID + " = ?", 
        new String[]{client_user_id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
      user = build_by_cursor(cursor);
    }else{
      user = User.NIL_USER;
    }
    
    cursor.close();
    db.close();
    return user;
  }
  
  public static User find_by_server_user_id(int server_user_id){
    User user;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_USERS, get_columns(),
        Constants.TABLE_USERS__USER_ID + " = ?", 
        new String[]{server_user_id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
      user = build_by_cursor(cursor);
    }else{
      user = User.NIL_USER;
    }
    
    cursor.close();
    db.close();
    return user;
  }
  
  public static boolean is_exists(int server_user_id){
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_USERS, get_columns(),
        Constants.TABLE_USERS__USER_ID + " = ?", 
        new String[]{server_user_id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    
    cursor.close();
    db.close();
    return has_value;
  }

  private static User build_by_cursor(Cursor cursor) {
    int id = cursor.getInt(0);
    int user_id = cursor.getInt(1);
    String user_name = cursor.getString(2);
    byte[] user_avatar = cursor.getBlob(3);
    String avatar_url = cursor.getString(4);
    long server_created_time = cursor.getLong(5);
    long server_updated_time = cursor.getLong(6);
    return new User(id,user_id,user_name,user_avatar,avatar_url,server_created_time,server_updated_time);
  }

  private static String[] get_columns() {
    return new String[]{
        Constants.KEY_ID,
        Constants.TABLE_USERS__USER_ID,
        Constants.TABLE_USERS__USER_NAME,
        Constants.TABLE_USERS__USER_AVATAR,
        Constants.TABLE_USERS__AVATAR_URL,
        Constants.TABLE_USERS__SERVER_CREATED_TIME,
        Constants.TABLE_USERS__SERVER_UPDATED_TIME
    };
  }
}
