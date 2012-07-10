package com.teamkn.model.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.Logic.AccountManager;
import com.teamkn.model.AccountUser;
import com.teamkn.model.Chat;
import com.teamkn.model.Contact;
import com.teamkn.model.User;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class ChatDBHelper extends BaseModelDBHelper {
  public static int create(List<Integer> server_user_id_list){
    int new_chat_id = 0;
    copy_user_info_from_contacts_to_users(server_user_id_list);
    
    AccountUser current_user = AccountManager.current_user();
    int current_user_id = current_user.user_id;
    int current_user_client_id = UserDBHelper.find_client_user_id(current_user_id);
    
    ArrayList<Integer> client_user_id_list = new ArrayList<Integer>();
    client_user_id_list.add(current_user_client_id);
    for(int server_user_id : server_user_id_list){
      int client_user_id = UserDBHelper.find_client_user_id(server_user_id);
      client_user_id_list.add(client_user_id);
    }
    
    SQLiteDatabase db = get_write_db();
    db.beginTransaction();
    try {
      //创建 chats
      ContentValues v = new ContentValues();
      v.put(Constants.TABLE_CHATS__SERVER_CREATED_TIME,0);
      long row_id = db.insert(Constants.TABLE_CHATS, null, v);
      if(row_id == -1){throw new SQLException();}
      Cursor cursor = db.rawQuery("select max(" + Constants.KEY_ID +") from " + Constants.TABLE_CHATS + ";",null);
      cursor.moveToFirst();
      new_chat_id = cursor.getInt(0);
      cursor.close();
      
      // 创建 中间表
      for(int client_user_id : client_user_id_list){
        ContentValues membership_values = new ContentValues();
        membership_values.put(Constants.TABLE_CHAT_MEMBERSHIPS__CLIENT_CHAT_ID,new_chat_id);
        membership_values.put(Constants.TABLE_CHAT_MEMBERSHIPS__CLIENT_USER_ID,client_user_id);
        long m_row_id = db.insert(Constants.TABLE_CHAT_MEMBERSHIPS, null, membership_values);
        if(m_row_id == -1){throw new SQLException();}        
      }
      
      db.setTransactionSuccessful();
    }finally {
      db.endTransaction();
      db.close();
    }
    return new_chat_id;
  }
  
  // 现在联系人模块把联系人信息存在了 contacts 表中了
  // 这里把数据复制一份到 users 表中
  // TODO 联系人模块应该重构，把 contacts 表中的数据移到 users 中
  // 在重构前，先这样复制一份
  private static void copy_user_info_from_contacts_to_users(List<Integer> server_user_id_list){
    AccountUser current_user = AccountManager.current_user();
    int current_user_id = current_user.user_id;
    
    if(!UserDBHelper.is_exists(current_user_id)){
      UserDBHelper.create(current_user.user_id, current_user.name, current_user.avatar, 0, 0);
    }
    
    for(int server_user_id : server_user_id_list){
      if(!UserDBHelper.is_exists(server_user_id)){
        Contact contact = ContactDBHelper.find(current_user_id, server_user_id);
        UserDBHelper.create(contact.contact_user_id, contact.contact_user_name, 
            contact.contact_user_avatar, contact.server_created_time, contact.server_updated_time);
      }
    }
    
  }
  
  public static int get_max_id(){
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.rawQuery("select max(" + Constants.KEY_ID + ") from " + Constants.TABLE_CHATS + ";", null);
    cursor.moveToFirst();
    int max_id = cursor.getInt(0);
    cursor.close();
    db.close();
    return max_id;
  }
  
  public static List<Chat> find_unsyn_list(){
    List<Chat> chat_list = new ArrayList<Chat>();
    SQLiteDatabase db = get_read_db();
    
    Cursor cursor = db.query(Constants.TABLE_CHATS, get_columns(), 
        Constants.TABLE_CHATS__SERVER_CHAT_ID + " is null", 
        null,null, null, null);
    while(cursor.moveToNext()){
      chat_list.add(build_by_cursor(cursor));
    }
    cursor.close();
    db.close();
    return chat_list;
  }
  
  public static List<Chat> find_list(){
    List<Chat> chat_list = new ArrayList<Chat>();
    SQLiteDatabase db = get_read_db();
    
    Cursor cursor = db.query(Constants.TABLE_CHATS, get_columns(), null, null, null, null, null);
    while(cursor.moveToNext()){
      chat_list.add(build_by_cursor(cursor));
    }
    cursor.close();
    db.close();
    return chat_list;
  }
  
  public static List<User> get_member_list(int client_chat_id){
    List<User> user_list = new ArrayList<User>();
    List<Integer> user_id_list = new ArrayList<Integer>();
    SQLiteDatabase db = get_read_db();

    Cursor cursor = db.query(Constants.TABLE_CHAT_MEMBERSHIPS,
        new String[]{Constants.TABLE_CHAT_MEMBERSHIPS__CLIENT_USER_ID},
        Constants.TABLE_CHAT_MEMBERSHIPS__CLIENT_CHAT_ID + " = ?",
        new String[]{client_chat_id+""},null,null,null);
    
    while(cursor.moveToNext()){
      user_id_list.add(cursor.getInt(0));
    }
    cursor.close();
    db.close();
    
    for(Integer client_user_id : user_id_list){
      User user = UserDBHelper.find(client_user_id);
      user_list.add(user);
    }
    
    return user_list;
  }
  
  private static Chat build_by_cursor(Cursor cursor) {
    int client_chat_id = cursor.getInt(0);
    int server_chat_id = cursor.getInt(1);
    long server_created_time = cursor.getLong(2);
    long server_updated_time = cursor.getLong(3);
    return new Chat(client_chat_id, server_chat_id, server_created_time,
        server_updated_time);
  }

  private static String[] get_columns() {
    return new String[]{
      Constants.KEY_ID,
      Constants.TABLE_CHATS__SERVER_CHAT_ID,
      Constants.TABLE_CHATS__SERVER_CREATED_TIME,
      Constants.TABLE_CHATS__SERVER_UPDATED_TIME
    };
  }

  public static Chat find(int client_chat_id){
    SQLiteDatabase db = get_read_db();
    Chat chat;
    
    Cursor cursor = db.query(Constants.TABLE_CHATS, get_columns(),
        Constants.KEY_ID + " = ?",
        new String[]{client_chat_id+""}, null, null, null);
    
    boolean has_value = cursor.moveToFirst();
    if(has_value){
      chat = build_by_cursor(cursor);
    }else{
      chat = Chat.NIL_CHAT;
    }
    cursor.close();
    db.close();
    
    return chat;
  }

  public static void after_server_create(int client_chat_id,
      int server_chat_id, long server_created_time, long server_updated_time) {
    SQLiteDatabase db = get_read_db();
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_CHATS__SERVER_CHAT_ID, server_chat_id);
    values.put(Constants.TABLE_CHATS__SERVER_CREATED_TIME, server_created_time);
    values.put(Constants.TABLE_CHATS__SERVER_UPDATED_TIME, server_updated_time);
    
    db.update(Constants.TABLE_CHATS, values,
        Constants.KEY_ID + " = ?", new String[]{client_chat_id+""});
    
    db.close();
  }

  public static boolean is_exists(int server_chat_id) {
    SQLiteDatabase db = get_read_db();
    
    Cursor cursor = db.query(Constants.TABLE_CHATS, get_columns(),
        Constants.TABLE_CHATS__SERVER_CHAT_ID + " = ?",
        new String[]{server_chat_id+""}, null, null, null);
    
    boolean has_value = cursor.moveToFirst();
    cursor.close();
    db.close();
    
    return has_value;
  }

  public static void pull_from_server(int server_chat_id,
      ArrayList<Integer> client_user_id_list, long server_created_time,
      long server_updated_time) {
    
    SQLiteDatabase db = get_write_db();
    db.beginTransaction();
    try {
      //创建 chats
      ContentValues v = new ContentValues();
      v.put(Constants.TABLE_CHATS__SERVER_CHAT_ID,server_chat_id);
      v.put(Constants.TABLE_CHATS__SERVER_CREATED_TIME,server_created_time);
      v.put(Constants.TABLE_CHATS__SERVER_UPDATED_TIME,server_updated_time);
      long row_id = db.insert(Constants.TABLE_CHATS, null, v);
      if(row_id == -1){throw new SQLException();}
      Cursor cursor = db.rawQuery("select max(" + Constants.KEY_ID +") from " + Constants.TABLE_CHATS + ";",null);
      cursor.moveToFirst();
      int new_chat_id = cursor.getInt(0);
      cursor.close();
      
      // 创建 中间表
      for(int client_user_id : client_user_id_list){
        ContentValues membership_values = new ContentValues();
        membership_values.put(Constants.TABLE_CHAT_MEMBERSHIPS__CLIENT_CHAT_ID,new_chat_id);
        membership_values.put(Constants.TABLE_CHAT_MEMBERSHIPS__CLIENT_USER_ID,client_user_id);
        long m_row_id = db.insert(Constants.TABLE_CHAT_MEMBERSHIPS, null, membership_values);
        if(m_row_id == -1){throw new SQLException();}        
      }
      
      db.setTransactionSuccessful();
    }finally {
      db.endTransaction();
      db.close();
    }
  }

  public static int find_client_chat_id(int server_chat_id) {
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_CHATS, 
        new String[]{Constants.KEY_ID},
        Constants.TABLE_CHATS__SERVER_CHAT_ID + " = ?", 
        new String[]{server_chat_id+""},null, null, null);

    cursor.moveToFirst();
    int id = cursor.getInt(0);
    db.close();
    return id;
  }

}
