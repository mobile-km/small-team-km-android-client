package com.teamkn.model.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.model.Attitudes;
import com.teamkn.model.ChatNode;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class AttitudesDBHelper extends BaseModelDBHelper{
    public class Kind {
	    public static final String GASP = "GASP";
	    public static final String HEART = "HEART";
	    public static final String SAD = "SAD";
	    public static final String SMILE = "SMILE";
	    public static final String WINK = "WINK";
	}
    public static List<Attitudes> find_list(int chat_node_id){
    	List<Attitudes> chat_node_list = new ArrayList<Attitudes>();
        SQLiteDatabase db = get_read_db();
        Cursor cursor = db.query(Constants.TABLE_ATTITUDES, get_columns(), 
            Constants.TABLE_ATTITUDES__CHAT_NODE_ID + " = ?", 
            new String[]{chat_node_id+""}, 
            null, null, null);
        
        while(cursor.moveToNext()){
          chat_node_list.add(build_by_cursor(cursor));
        }
        cursor.close();
        db.close();
        return chat_node_list;
      }
	public static Attitudes create(int chat_node_id, int current_user_id, String kind) {
		int client_user_id = UserDBHelper.get_client_user_id(current_user_id);
//		String uuid = UUID.randomUUID().toString();
		SQLiteDatabase db = get_write_db();
		
		ContentValues values = new ContentValues();
		values.put(Constants.TABLE_ATTITUDES__CHAT_NODE_ID,chat_node_id);
		values.put(Constants.TABLE_ATTITUDES__CLIENT_USER_ID, client_user_id);
		values.put(Constants.TABLE_ATTITUDES__KIND,kind);
		
		long row_id = db.insert(Constants.TABLE_ATTITUDES, null, values);
		if(row_id == -1){throw new SQLException();};
		db.close();
		return find_by_chat_node_id(chat_node_id);
	}
	 public static Attitudes find_by_chat_node_id(int chat_node_id){
		    Attitudes attitudes;
		    SQLiteDatabase db = get_read_db();

		    Cursor cursor = db.query(Constants.TABLE_ATTITUDES, get_columns(), 
		        Constants.TABLE_ATTITUDES__CHAT_NODE_ID + " = ? ", 
		        new String[]{chat_node_id+""}, 
		        null, null, null);
		    
		    boolean has_value = cursor.moveToFirst();
		    if(has_value){
		    	attitudes = build_by_cursor(cursor);
		    }else{
		    	attitudes = Attitudes.ATTITUDES;
		    }
		    db.close();
		    return attitudes;
     }
	 static int  i = 0;
	 private static Attitudes build_by_cursor(Cursor cursor) {
		    int chat_node_id = cursor.getInt(0);
		    int client_user_id = cursor.getInt(1);
		    String kind = cursor.getString(2); 
		    System.out.println("i = " + i + "; chat_node_id = " +chat_node_id + " ; client_user_id = " + client_user_id + " ;  kind = " + kind);
		    i++;
		    return new Attitudes(chat_node_id, client_user_id, kind);
	 }
	 private static String[] get_columns(){
		    return new String[]{
		        Constants.TABLE_ATTITUDES__CHAT_NODE_ID,
		        Constants.TABLE_ATTITUDES__CLIENT_USER_ID,
		        Constants.TABLE_ATTITUDES__KIND
		   };
	 }
	 
	 public static void after_server_create(int chat_node_id,
		      int server_chat_node_id, long server_created_time) {
		    SQLiteDatabase db = get_read_db();
		    
		    ContentValues values = new ContentValues();
		    values.put(Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID,server_chat_node_id);
		    values.put(Constants.TABLE_CHAT_NODES__SERVER_CREATED_TIME,server_created_time);
		    
		    db.update(Constants.TABLE_CHAT_NODES, values,
		        Constants.TABLE_ATTITUDES__CHAT_NODE_ID + " = ?", new String[]{chat_node_id+""});
		    db.close();
   }
	 
	 public static Attitudes find(int chat_node_id){
		    Attitudes attitudes;
		    SQLiteDatabase db = get_read_db();

		    Cursor cursor = db.query(Constants.TABLE_ATTITUDES, get_columns(), 
		        Constants.TABLE_ATTITUDES__CHAT_NODE_ID+ " = ? ", 
		        new String[]{chat_node_id+""}, 
		        null, null, null);
		    
		    boolean has_value = cursor.moveToFirst();
		    if(has_value){
		    	attitudes = build_by_cursor(cursor);
		    }else{
		    	attitudes = Attitudes.ATTITUDES;
		    }  
		    db.close();
		    return attitudes;
	 }
}
