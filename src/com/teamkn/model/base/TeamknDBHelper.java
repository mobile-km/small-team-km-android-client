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
            Constants.KEY_ID + " long primary key, " +
            Constants.TABLE_CHATS__SERVER_CHAT_ID + " integer, " +
            Constants.TABLE_CHATS__SERVER_CREATED_TIME + " long, " +
            Constants.TABLE_CHATS__SERVER_UPDATED_TIME + " long);";
    
    private static final String create_chat_memberships = "create table " +
            Constants.TABLE_CHAT_MEMBERSHIPS + " (" +
            Constants.KEY_ID + " long primary key, " +
            Constants.TABLE_CHAT_MEMBERSHIPS__CHAT_ID + " long not null, " +
            Constants.TABLE_CHAT_MEMBERSHIPS__USER_ID + " long not null);";
    
    private static final String create_chat_nodes = "create table " +
            Constants.TABLE_CHAT_NODES + " (" +
            Constants.KEY_ID + " long primary key, " +
            Constants.TABLE_CHAT_NODES__CHAT_ID + " long not null, " +
            Constants.TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID + " integer, " +
            Constants.TABLE_CHAT_NODES__CONTENT + " text, " +
            Constants.TABLE_CHAT_NODES__KIND + " text not null, " +
            Constants.TABLE_CHAT_NODES__SERVER_CREATED_TIME + " long, " +
            Constants.TABLE_CHAT_NODES__SENDER_ID + " integer not null);";
    
    private static final String create_users = "create table " +
            Constants.TABLE_USERS + " (" +
            Constants.KEY_ID + " long primary key, " +
            Constants.TABLE_USERS__USER_ID + " integer not null, " +
            Constants.TABLE_USERS__USER_NAME + " text not null, " +
            Constants.TABLE_USERS__USER_AVATAR + " blob, " + 
            Constants.TABLE_USERS__SERVER_CREATED_TIME + " long, " +
            Constants.TABLE_USERS__SERVER_UPDATED_TIME + " long);";


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
        
        onCreate(db);
    }
}
