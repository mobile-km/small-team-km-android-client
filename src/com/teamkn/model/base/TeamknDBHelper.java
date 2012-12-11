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
            Constants.TABLE_ACCOUNT_USERS__INFO + " text not null , " + 
            Constants.TABLE_ACCOUNT_USERS__IS_SHOW_TIP + " text default true );";
    
    private static final String create_users = "create table " +
            Constants.TABLE_USERS + " (" +
            Constants.KEY_ID + " integer primary key, " +
            Constants.TABLE_USERS__USER_ID + " integer not null, " +
            Constants.TABLE_USERS__USER_NAME + " text not null, " +
            Constants.TABLE_USERS__USER_AVATAR + " blob, " + 
            Constants.TABLE_USERS__AVATAR_URL + " text, " +
            Constants.TABLE_USERS__SERVER_CREATED_TIME + " long, " +
            Constants.TABLE_USERS__SERVER_UPDATED_TIME + " long);";
 
    public TeamknDBHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {	
        db.execSQL(create_table_account_users);
        db.execSQL(create_users);    
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Constants.TABLE_ACCOUNT_USERS);
        db.execSQL("drop table if exists " + Constants.TABLE_USERS);       
        onCreate(db);
    }
}
