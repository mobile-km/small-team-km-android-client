package com.teamkn.model.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TeamknDBHelper extends SQLiteOpenHelper {
    private static final String create_table_account_users = "create table " +
            Constants.TABLE_ACCOUNT_USERS + " (" +
            Constants.KEY_ID + " integer primary key autoincrement, " +
            Constants.TABLE_ACCOUNT_USERS__USER_ID + " integer not null, " +
            Constants.TABLE_ACCOUNT_USERS__NAME + " text not null, " +
            Constants.TABLE_ACCOUNT_USERS__AVATAR + " blob, " +
            Constants.TABLE_ACCOUNT_USERS__COOKIES + " text not null, " +
            Constants.TABLE_ACCOUNT_USERS__INFO + " text not null);";

    private static final String create_table_notes = "create table " +
            Constants.TABLE_NOTES + " (" +
            Constants.KEY_ID + " integer primary key, " +
            Constants.TABLE_NOTES__UUID + " text not null, " +
            Constants.TABLE_NOTES__CONTENT + " text, " +
            Constants.TABLE_NOTES__KIND + " text not null, " +
            Constants.TABLE_NOTES__IS_REMOVED + " integer not null DEFAULT  0, " +
            Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT + " integer not null DEFAULT  1, " +
            Constants.TABLE_NOTES__CLIENT_CREATED_TIME + " long not null DEFAULT 0, " +
            Constants.TABLE_NOTES__CLIENT_UPDATED_TIME + " long not null DEFAULT 0, " +
            Constants.TABLE_NOTES__SYNED_SERVER_TIME + " long not null DEFAULT 0);";
    
    private static final String create_table_contacts = "create table " +
            Constants.TABLE_CONTACTS + " (" +
            Constants.KEY_ID + " integer primary key, " +
            Constants.TABLE_CONTACTS__USER_ID + " integer not null, " +
            Constants.TABLE_CONTACTS__CONTACT_USER_ID + " integer not null, " +
            Constants.TABLE_CONTACTS__CONTACT_USER_NAME + " text not null, " +
            Constants.TABLE_CONTACTS__CONTACT_USER_AVATAR + " blob, " +
            Constants.TABLE_CONTACTS__MESSAGE + " text, " +
            Constants.TABLE_CONTACTS__STATUS + " text not null, " +
            Constants.TABLE_CONTACTS__SERVER_CREATED_TIME + " long not null, " +
            Constants.TABLE_CONTACTS__SERVER_UPDATED_TIME + " long not null);";
    
    private static final String create_chats = "create table " +
            Constants.TABLE_CHATS + " (" +
            Constants.KEY_ID + " integer primary key, " +
            Constants.TABLE_CHATS__UUID + " text not null, " +
            Constants.TABLE_CHATS__SERVER_CHAT_ID + " integer, " +
            Constants.TABLE_CHATS__SERVER_CREATED_TIME + " long, " +
            Constants.TABLE_CHATS__SERVER_UPDATED_TIME + " long);";
    
    private static final String create_chat_memberships = "create table " +
            Constants.TABLE_CHAT_MEMBERSHIPS + " (" +
            Constants.KEY_ID + " integer primary key, " +
            Constants.TABLE_CHAT_MEMBERSHIPS__CLIENT_CHAT_ID + " integer not null, " +
            Constants.TABLE_CHAT_MEMBERSHIPS__CLIENT_USER_ID + " integer not null);";
    
    private static final String create_chat_nodes = "create table " +
            Constants.TABLE_CHAT_NODES + " (" +
            Constants.KEY_ID + " integer primary key, " +
            Constants.TABLE_CHAT_NODES__UUID + " text not null, " +
            Constants.TABLE_CHAT_NODES__CLIENT_CHAT_ID + " integer not null, " +
            Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID + " integer, " +
            Constants.TABLE_CHAT_NODES__CONTENT + " text, " +
            Constants.TABLE_CHAT_NODES__KIND + " text not null, " +
            Constants.TABLE_CHAT_NODES__SERVER_CREATED_TIME + " long, " +
            Constants.TABLE_CHAT_NODES__CLIENT_USER_ID + " integer not null);";
    
    private static final String create_users = "create table " +
            Constants.TABLE_USERS + " (" +
            Constants.KEY_ID + " integer primary key, " +
            Constants.TABLE_USERS__USER_ID + " integer not null, " +
            Constants.TABLE_USERS__USER_NAME + " text not null, " +
            Constants.TABLE_USERS__USER_AVATAR + " blob, " + 
            Constants.TABLE_USERS__SERVER_CREATED_TIME + " long, " +
            Constants.TABLE_USERS__SERVER_UPDATED_TIME + " long);";
    
    private static final String create_attitudes = "create table "+
            Constants.TABLE_ATTITUDES + " ( " + 
    		Constants.TABLE_ATTITUDES__CHAT_NODE_ID + " integer not null, " + 
            Constants.TABLE_ATTITUDES__CLIENT_USER_ID + " integer not null, " + 
    		Constants.TABLE_ATTITUDES__KIND + " text not null ,"+
            Constants.TABLE_ATTITUDES_IS_SYNED + "  text);";
    
    private static final String create_data_lists = "create table "+
            Constants.TABLE_DATA_LISTS + " ( " + 
            Constants.KEY_ID + " integer primary key autoincrement, " +
    		Constants.TABLE_DATA_LISTS_USER_ID + " integer , " + 
            Constants.TABLE_DATA_LISTS_TITLE + " text , " + 
    		Constants.TABLE_DATA_LISTS_KIND + " text , "+
    		Constants.TABLE_DATA_LISTS_PUBLIC + " text , "+
    		Constants.TABLE_DATA_LISTS_HAS_COMMITS + " text , "+ ///  has_commits
            Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " integer ,"+
            Constants.TABLE_DATA_LISTS_SERVER_CREATED_TIME + " integer ,"+
            Constants.TABLE_DATA_LISTS_SERVER_UPDATED_TIME + " integer ,"+
            Constants.TABLE_DATA_LISTS_FORKED_FROM_ID + " integer);";
    
    private static final String create_data_items = "create table "+
            Constants.TABLE_DATA_ITEMS + " ( " + 
            Constants.KEY_ID + " integer primary key autoincrement, " +
    		Constants.TABLE_DATA_ITEMS_TITLE + " text , " + 
            Constants.TABLE_DATA_ITEMS_CONTENT + " text , " + 
    		Constants.TABLE_DATA_ITEMS_URL + " text , "+
    		Constants.TABLE_DATA_ITEMS_KIND + " text , "+
    		Constants.TABLE_DATA_ITEMS_DATA_LIST_ID + " integer, "+
            Constants.TABLE_DATA_ITEMS_POSITION + " integer ,"+
    		Constants.TABLE_DATA_ITEMS_SERVER_DATA_ITEM_ID + " integer,"+
    		Constants.TABLE_DATA_ITEMS_SEED + " text);";
    
    private static final String create_watch = "create table "+
            Constants.TABLE_WATCH + " ( " + 
            Constants.KEY_ID + " integer primary key autoincrement, " +
    		Constants.TABLE_WATCH_USER_ID + " integer , " + 
            Constants.TABLE_WATCH_DATA_LIST_ID + " integer );";
    
    private static final String create_data_list_readings = "create table "+
            Constants.TABLE_DATA_LIST_READINGS + " ( " + 
            Constants.KEY_ID + " integer primary key autoincrement, " +
    		Constants.TABLE_DATA_LIST_READINGS_DATA_LIST_ID + " integer , " + 
            Constants.TABLE_DATA_LIST_READINGS_USER_ID + " integer );";
    
    public TeamknDBHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        db.execSQL(create_table_account_users);
        db.execSQL(create_table_notes);
        db.execSQL(create_table_contacts);
        
        db.execSQL(create_chats);
        db.execSQL(create_chat_memberships);
        db.execSQL(create_chat_nodes);
        db.execSQL(create_users);
            
        db.execSQL(create_attitudes);
        
        db.execSQL(create_data_lists);
        db.execSQL(create_data_items);
        db.execSQL(create_watch);
        db.execSQL(create_data_list_readings);
        
        System.out.println("data_lists " +create_data_lists );
//        System.out.println("create_data_items " + create_data_items);
//        System.out.println("watch " + create_watch);
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Constants.TABLE_ACCOUNT_USERS);
        db.execSQL("drop table if exists " + Constants.TABLE_NOTES);
        db.execSQL("drop table if exists " + Constants.TABLE_CONTACTS);
        
        db.execSQL("drop table if exists " + Constants.TABLE_CHATS);
        db.execSQL("drop table if exists " + Constants.TABLE_CHAT_MEMBERSHIPS);
        db.execSQL("drop table if exists " + Constants.TABLE_CHAT_NODES);
        db.execSQL("drop table if exists " + Constants.TABLE_USERS);
        
        db.execSQL("drop table if exists " + Constants.TABLE_ATTITUDES);
        
        db.execSQL("drop table if exists " + Constants.TABLE_DATA_LISTS);
        db.execSQL("drop table if exists " + Constants.TABLE_DATA_ITEMS);
        db.execSQL("drop table if exists " + Constants.TABLE_WATCH);
        db.execSQL("drop table if exists " + Constants.TABLE_DATA_LIST_READINGS);
        
        onCreate(db);
    }
}
