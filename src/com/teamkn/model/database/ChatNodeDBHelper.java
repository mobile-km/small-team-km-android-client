package com.teamkn.model.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teamkn.model.ChatNode;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class ChatNodeDBHelper extends BaseModelDBHelper {
  public class Kind {
    public static final String TEXT = "TEXT";
    public static final String IMAGE = "IMAGE";
}
  
  public static List<ChatNode> find_list(int client_chat_id){
    List<ChatNode> chat_node_list = new ArrayList<ChatNode>();
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_CHAT_NODES, get_columns(), 
        Constants.TABLE_CHAT_NODES__CHAT_ID + " = ?", 
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
  
  private static ChatNode build_by_cursor(Cursor cursor) {
    int id = cursor.getInt(0);
    int chat_id = cursor.getInt(1);
    String content = cursor.getString(2);
    String kind = cursor.getString(3);
    int sender_id = cursor.getInt(4);
    int server_chat_node_id = cursor.getInt(5);
    long server_created_time = cursor.getLong(6);
    
    return new ChatNode(id,chat_id,content,kind,sender_id,server_chat_node_id,server_created_time);
  }

  private static String[] get_columns(){
    return new String[]{
        Constants.KEY_ID,
        Constants.TABLE_CHAT_NODES__CHAT_ID,
        Constants.TABLE_CHAT_NODES__CONTENT,
        Constants.TABLE_CHAT_NODES__KIND,
        Constants.TABLE_CHAT_NODES__SENDER_ID,
        Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID,
        Constants.TABLE_CHAT_NODES__SERVER_CREATED_TIME
    };
  }

  public static int create(int client_chat_id, String content,
      int current_user_id) {
    SQLiteDatabase db = get_write_db();
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_CHAT_NODES__CONTENT, content);
    values.put(Constants.TABLE_CHAT_NODES__KIND,Kind.TEXT);
    values.put(Constants.TABLE_CHAT_NODES__SENDER_ID,current_user_id);
    values.put(Constants.TABLE_CHAT_NODES__CHAT_ID,client_chat_id);
    long client_chat_node_id = db.insert(Constants.TABLE_CHAT_NODES, null, values);
    if(client_chat_node_id == -1){throw new SQLException();};
    db.close();
    return get_max_id();
  }
  
  public static int get_max_id(){
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.rawQuery("select max(" + Constants.KEY_ID + ") from " + Constants.TABLE_CHAT_NODES + ";", null);
    cursor.moveToFirst();
    int max_id = cursor.getInt(0);
    cursor.close();
    db.close();
    return max_id;
  }

  public static void after_server_create(int client_chat_node_id,
      int server_chat_node_id, long server_created_time) {
    SQLiteDatabase db = get_read_db();
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID,server_chat_node_id);
    values.put(Constants.TABLE_CHAT_NODES__SERVER_CREATED_TIME,server_created_time);
    
    db.update(Constants.TABLE_CHAT_NODES, values,
        Constants.KEY_ID + " = ?", new String[]{client_chat_node_id+""});
    db.close();
  }

}
