package com.teamkn.model.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.Note;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;
import com.teamkn.model.database.NoteDBHelper.Kind;
import com.teamkn.service.IndexService;
import com.teamkn.service.IndexService.IndexHandler.action;

public class ChatNodeDBHelper extends BaseModelDBHelper {
  public class Kind {
    public static final String TEXT = "TEXT";
    public static final String IMAGE = "IMAGE";
  }
  
  public static List<ChatNode> find_list(int client_chat_id){
    List<ChatNode> chat_node_list = new ArrayList<ChatNode>();
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_CHAT_NODES, get_columns(), 
        Constants.TABLE_CHAT_NODES__CLIENT_CHAT_ID + " = ?", 
        new String[]{client_chat_id+""}, 
        null, null, null);
    
    while(cursor.moveToNext()){
      chat_node_list.add(build_by_cursor(cursor));
    }
    
    cursor.close();
    db.close();
    return chat_node_list;
  }
  
  public static ChatNode find(int client_chat_node_id){
    ChatNode chat_node;
    SQLiteDatabase db = get_read_db();

    Cursor cursor = db.query(Constants.TABLE_CHAT_NODES, get_columns(), 
        Constants.KEY_ID + " = ? ", 
        new String[]{client_chat_node_id+""}, 
        null, null, null);
    
    boolean has_value = cursor.moveToFirst();
    if(has_value){
      chat_node = build_by_cursor(cursor);
    }else{
      chat_node = ChatNode.NIL_CHAT_NODE;
    }
    
    db.close();
    return chat_node;
  }
  
  public static boolean is_exists(String uuid){
    SQLiteDatabase db = get_read_db();

    Cursor cursor = db.query(Constants.TABLE_CHAT_NODES, get_columns(), 
        Constants.TABLE_CHAT_NODES__UUID + " = ? ", 
        new String[]{uuid}, 
        null, null, null);
    boolean has_value = cursor.moveToFirst();
    
    cursor.close();
    db.close();
    
    return has_value;
  }
  
  private static ChatNode build_by_cursor(Cursor cursor) {
    int id = cursor.getInt(0);
    String uuid = cursor.getString(1);
    int chat_id = cursor.getInt(2);
    String content = cursor.getString(3);
    String kind = cursor.getString(4);
    int client_user_id = cursor.getInt(5);
    int server_chat_node_id = cursor.getInt(6);
    long server_created_time = cursor.getLong(7);
    
    return new ChatNode(uuid,id,chat_id,content,kind,client_user_id,server_chat_node_id,server_created_time);
  }

  private static String[] get_columns(){
    return new String[]{
        Constants.KEY_ID,
        Constants.TABLE_CHAT_NODES__UUID,
        Constants.TABLE_CHAT_NODES__CLIENT_CHAT_ID,
        Constants.TABLE_CHAT_NODES__CONTENT,
        Constants.TABLE_CHAT_NODES__KIND,
        Constants.TABLE_CHAT_NODES__CLIENT_USER_ID,
        Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID,
        Constants.TABLE_CHAT_NODES__SERVER_CREATED_TIME
    };
  }

  public static ChatNode create(int client_chat_id, String content,
      int current_user_id, String kind) {
    int client_user_id = UserDBHelper.get_client_user_id(current_user_id);
    String uuid = UUID.randomUUID().toString();
    
    SQLiteDatabase db = get_write_db();
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_CHAT_NODES__UUID,uuid);
    values.put(Constants.TABLE_CHAT_NODES__CONTENT, content);
    values.put(Constants.TABLE_CHAT_NODES__KIND,kind);
    values.put(Constants.TABLE_CHAT_NODES__CLIENT_USER_ID,client_user_id);
    values.put(Constants.TABLE_CHAT_NODES__CLIENT_CHAT_ID,client_chat_id);
    long row_id = db.insert(Constants.TABLE_CHAT_NODES, null, values);
    if(row_id == -1){throw new SQLException();};
    db.close();
    return find_by_uuid(uuid);
  }
  //start mi
  public static ChatNode create_image_chat(int client_chat_id, String origin_image_path,
	      int current_user_id,String kind) {
	  ChatNode chatNode = create(client_chat_id,origin_image_path,current_user_id,kind);
      String uuid = chatNode.uuid;
      File chat_image_file = Chat.note_image_file(uuid);
      try {
          FileUtils.copyFile(new File(origin_image_path), chat_image_file);
      } catch (IOException e) {
          e.printStackTrace();
//          destroy(uuid);
      }
	return chatNode;
  }
  
  //end mi
  
  
  
  
  
  public static ChatNode find_by_uuid(String uuid){
    ChatNode chat_node;
    SQLiteDatabase db = get_read_db();

    Cursor cursor = db.query(Constants.TABLE_CHAT_NODES, get_columns(), 
        Constants.TABLE_CHAT_NODES__UUID + " = ? ", 
        new String[]{uuid+""}, 
        null, null, null);
    
    boolean has_value = cursor.moveToFirst();
    if(has_value){
      chat_node = build_by_cursor(cursor);
    }else{
      chat_node = ChatNode.NIL_CHAT_NODE;
    }
    
    db.close();
    return chat_node;
  }
  
  public static void after_server_create(String uuid,
      int server_chat_node_id, long server_created_time) {
    SQLiteDatabase db = get_read_db();
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID,server_chat_node_id);
    values.put(Constants.TABLE_CHAT_NODES__SERVER_CREATED_TIME,server_created_time);
    
    db.update(Constants.TABLE_CHAT_NODES, values,
        Constants.TABLE_CHAT_NODES__UUID + " = ?", new String[]{uuid});
    db.close();
  }

  public static List<ChatNode> find_unsyn_list() {
    List<ChatNode> chat_node_list = new ArrayList<ChatNode>();
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_CHAT_NODES, get_columns(), 
        Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID + " is null", 
        null, null, null, null);
    
    while(cursor.moveToNext()){
      chat_node_list.add(build_by_cursor(cursor));
    }
    
    cursor.close();
    db.close();
    return chat_node_list;
  }
  
  public static void pull_from_server(String uuid, int server_chat_id, int server_chat_node_id,
      int server_user_id, String content, long server_created_time){
    
    if(is_exists(uuid)){return;}
    
    int client_user_id = UserDBHelper.get_client_user_id(server_user_id);
    int client_chat_id = ChatDBHelper.get_client_chat_id(server_chat_id);
    
    SQLiteDatabase db = get_write_db();
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_CHAT_NODES__UUID,uuid);
    values.put(Constants.TABLE_CHAT_NODES__CONTENT, content);
    values.put(Constants.TABLE_CHAT_NODES__KIND,Kind.TEXT);
    values.put(Constants.TABLE_CHAT_NODES__CLIENT_USER_ID,client_user_id);
    values.put(Constants.TABLE_CHAT_NODES__CLIENT_CHAT_ID,client_chat_id);
    values.put(Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID,server_chat_node_id);
    values.put(Constants.TABLE_CHAT_NODES__SERVER_CREATED_TIME,server_created_time);
    long row_id = db.insert(Constants.TABLE_CHAT_NODES, null, values);
    if(row_id == -1){throw new SQLException();};
    db.close();
  }

}
