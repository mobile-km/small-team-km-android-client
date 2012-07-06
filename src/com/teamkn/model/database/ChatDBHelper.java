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
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class ChatDBHelper extends BaseModelDBHelper {
  public static long create(List<Integer> member_ids){
    AccountUser current_user = AccountManager.current_user();
    int current_user_id = current_user.user_id;
    
    // 拼装 参与者的 ContentValues 
    List<ContentValues> member_values_list = new ArrayList<ContentValues>();
    // 增加 自己到对话串
    ContentValues cv = new ContentValues();
    cv.put(Constants.TABLE_USERS__USER_ID,current_user.user_id);
    cv.put(Constants.TABLE_USERS__USER_NAME,current_user.name);
    cv.put(Constants.TABLE_USERS__USER_AVATAR,current_user.avatar);
    cv.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,0);
    cv.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,0);
    member_values_list.add(cv);
    
    for(int i = 0; i< member_ids.size(); i++){
      Integer contact_user_id = member_ids.get(i);
      Contact contact = ContactDBHelper.find(current_user_id, contact_user_id);
      ContentValues values = new ContentValues();
      values.put(Constants.TABLE_USERS__USER_ID,contact.contact_user_id);
      values.put(Constants.TABLE_USERS__USER_NAME,contact.contact_user_name);
      values.put(Constants.TABLE_USERS__USER_AVATAR,contact.contact_user_avatar);
      values.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,contact.server_created_time);
      values.put(Constants.TABLE_USERS__SERVER_CREATED_TIME,contact.server_updated_time);
      member_values_list.add(values);
    }
    
    SQLiteDatabase db = get_write_db();
    db.beginTransaction();
    try {
      //创建 chats
      ContentValues v = new ContentValues();
      v.put(Constants.TABLE_CHATS__SERVER_CREATED_TIME,0);
      long chat_id = db.insert(Constants.TABLE_CHATS, null, v);
      if(chat_id == -1){throw new SQLException();}
      // 创建 users
      for (int i = 0; i < member_values_list.size(); i++) {
        ContentValues values = member_values_list.get(i);
        long user_id = db.insert(Constants.TABLE_USERS, null, values);
        if(chat_id == -1){throw new SQLException();}
        
        // 创建中间表
        ContentValues membership_values = new ContentValues();
        membership_values.put(Constants.TABLE_CHAT_MEMBERSHIPS__CHAT_ID,chat_id);
        membership_values.put(Constants.TABLE_CHAT_MEMBERSHIPS__USER_ID,user_id);
        long m_id = db.insert(Constants.TABLE_CHAT_MEMBERSHIPS, null, membership_values);
        if(m_id == -1){throw new SQLException();}
      }
      db.setTransactionSuccessful();
      return chat_id;
    }finally {
      db.endTransaction();
      db.close();
    }
  }
  
  public static Chat find(long client_chat_id){
    SQLiteDatabase db = get_read_db();
    Chat chat;
    String sql = "select " +
        Constants.TABLE_CHATS + "." + Constants.KEY_ID + "," +
        Constants.TABLE_CHATS + "." + Constants.TABLE_CHATS__SERVER_CHAT_ID + "," +
        Constants.TABLE_CHATS + "." + Constants.TABLE_CHATS__SERVER_CREATED_TIME + "," +
        Constants.TABLE_CHATS + "." + Constants.TABLE_CHATS__SERVER_UPDATED_TIME + "," +
        Constants.TABLE_CHAT_MEMBERSHIPS + "." + Constants.TABLE_CHAT_MEMBERSHIPS__USER_ID +
        " from " + Constants.TABLE_CHATS +
        " inner join " + Constants.TABLE_CHAT_MEMBERSHIPS +
        " on " + Constants.TABLE_CHAT_MEMBERSHIPS + "." + Constants.TABLE_CHAT_MEMBERSHIPS__CHAT_ID + " = " +
        Constants.TABLE_CHATS + "." + Constants.KEY_ID +
        " where " + Constants.TABLE_CHATS + "." + Constants.KEY_ID + " = ?;";
        
    Cursor cursor = db.rawQuery(sql, new String[]{client_chat_id+""});
    List<Integer> member_ids = new ArrayList<Integer>();
    boolean has_value = cursor.moveToFirst();
    if(has_value){
      int server_chat_id = cursor.getInt(1);
      long server_created_time = cursor.getLong(2);
      long server_updated_time = cursor.getLong(3);
      member_ids.add(cursor.getInt(4));
      while(cursor.moveToNext()){
        member_ids.add(cursor.getInt(4));
      }
      chat = new Chat(client_chat_id,server_chat_id,server_created_time,server_updated_time,member_ids);
    }else{
      chat = Chat.NIL_CHAT;
    }
    cursor.close();
    db.close();
    
    return chat;
  }

  public static void after_server_create(long client_chat_id,
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

}
