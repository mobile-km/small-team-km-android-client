package com.mindpin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MindpinDBHelper extends SQLiteOpenHelper{
	private static final String create_table_feed_drafts = "create table " +
			Constants.TABLE_FEED_DRAFTS+" (" +
			Constants.KEY_ID+" integer primary key autoincrement, "+
			Constants.TABLE_FEED_DRAFTS__TITLE+" text not null, "+
			Constants.TABLE_FEED_DRAFTS__CONTENT+" text not null, "+
			Constants.TABLE_FEED_DRAFTS__IMAGE_PATHS+" text not null, "+
			Constants.TABLE_FEED_DRAFTS__SELECT_COLLECTION_IDS+" text not null, " +
			Constants.TABLE_FEED_DRAFTS__SEND_TSINA+" integer not null, " +
			Constants.TABLE_FEED_DRAFTS__TIME+" long not null, "+
			Constants.TABLE_FEED_DRAFTS__USER_ID+" integer not null);";
	
	private static final String create_table_users = "create table " +
			Constants.TABLE_USERS + "(" + 
			Constants.KEY_ID + " integer primary key autoincrement, "+
			Constants.TABLE_USERS__USER_ID + " integer not null, "+
			Constants.TABLE_USERS__NAME + " text not null, "+
			Constants.TABLE_USERS__COOKIES + " text not null, "+
			Constants.TABLE_USERS__INFO + " text not null);";
	
	private static final String create_table_feeds = "create table " +
			Constants.TABLE_FEEDS + "(" +
			Constants.KEY_ID + " integer primary key autoincrement, "+
			Constants.TABLE_FEEDS__ID + " integer not null, "+
			Constants.TABLE_FEEDS__USER_ID + " integer not null, "+
			Constants.TABLE_FEEDS__JSON + " text not null, "+
			Constants.TABLE_FEEDS__UPDATED_AT+ " long not null);";
	
	
	public MindpinDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(create_table_feed_drafts);
		db.execSQL(create_table_users);
		db.execSQL(create_table_feeds);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists "+Constants.TABLE_FEED_DRAFTS);
		db.execSQL("drop table if exists "+Constants.TABLE_USERS);
		db.execSQL("drop table if exists "+Constants.TABLE_FEEDS);
		onCreate(db);
	}
}
