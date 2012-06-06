package com.teamkn.model.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TeamknDBHelper extends SQLiteOpenHelper{
	private static final String create_table_users = "create table " +
			Constants.TABLE_USERS          + "(" + 
			Constants.KEY_ID               + " integer primary key autoincrement, "+
			Constants.TABLE_USERS__USER_ID + " integer not null, "+
			Constants.TABLE_USERS__NAME    + " text not null, "+
			Constants.TABLE_USERS__COOKIES + " text not null, "+
			Constants.TABLE_USERS__INFO    + " text not null);";
	
	private static final String create_table_notes = "create table " +
	    Constants.TABLE_NOTES             + "(" +
	    Constants.KEY_ID                  + " integer primary key, " +
	    Constants.TABLE_NOTES__UUID       + " text not null, " +
	    Constants.TABLE_NOTES__CONTENT    + " text, " +
	    Constants.TABLE_NOTES__KIND       + " text not null, " +
	    Constants.TABLE_NOTES__IS_REMOVED + " integer not null DEFAULT  0, " +
	    Constants.TABLE_NOTES__IS_SYND    + " integer not null DEFAULT  0, " +
	    Constants.TABLE_NOTES__CREATED_AT + " long not null, " +
	    Constants.TABLE_NOTES__UPDATED_AT + " long not null);";
	    
	
	public TeamknDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(create_table_users);
		db.execSQL(create_table_notes);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists "+Constants.TABLE_USERS);
		db.execSQL("drop table if exists "+Constants.TABLE_NOTES);
		onCreate(db);
	}
}
