package com.mindpin.database;

import java.util.ArrayList;

import com.mindpin.Logic.AccountManager;
import com.mindpin.application.MindpinApplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FeedDraft {
	public int id;
	public String title;
	public String content;
	public String image_paths;
	public String select_collection_ids;
	public boolean send_tsina;
	public long time;
	public int user_id;

	public FeedDraft(int id,String title, String content, String image_paths,
			String select_collection_ids,boolean send_tsina,long time,int user_id) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.image_paths = image_paths;
		this.select_collection_ids = select_collection_ids;
		this.send_tsina = send_tsina;
		this.time = time;
		this.user_id = user_id;
	}
	
	public static int get_count(){
		SQLiteDatabase db = get_read_db();
		Cursor cursor = db.query(Constants.TABLE_FEED_DRAFTS,new String[]{Constants.KEY_ID}, null, null, null, null, null);
		int count = cursor.getCount();
		db.close();
		return count;
	}
	
	public static FeedDraft find(int fid){
		SQLiteDatabase db = get_read_db();
		Cursor cursor = db.query(Constants.TABLE_FEED_DRAFTS,
				new String[]{
								Constants.KEY_ID,
								Constants.TABLE_FEED_DRAFTS__TITLE,
								Constants.TABLE_FEED_DRAFTS__CONTENT,
								Constants.TABLE_FEED_DRAFTS__IMAGE_PATHS,
								Constants.TABLE_FEED_DRAFTS__SELECT_COLLECTION_IDS,
								Constants.TABLE_FEED_DRAFTS__SEND_TSINA,
								Constants.TABLE_FEED_DRAFTS__TIME,
								Constants.TABLE_FEED_DRAFTS__USER_ID
							}, 
							Constants.KEY_ID + " = "+fid, null, null, null,null);
		boolean has = cursor.moveToFirst();
		db.close();
		if(has){
			int id = cursor.getInt(0);
			String title = cursor.getString(1);
			String content = cursor.getString(2);
			String image_paths = cursor.getString(3);
			String select_collection_ids = cursor.getString(4);
			boolean send_tsina = false;
			if(cursor.getInt(5) == 1) send_tsina = true;			
			long time = cursor.getLong(6);
			int user_id = cursor.getInt(7);
			FeedDraft fh = new FeedDraft(id,title, content, image_paths, select_collection_ids,send_tsina,time,user_id);
			return fh;
		}else{
			return null;
		}
	}
	
	public static ArrayList<FeedDraft> get_feed_drafts(){
		SQLiteDatabase db = get_read_db();
		Cursor cursor = db.query(Constants.TABLE_FEED_DRAFTS,
				new String[]{
								Constants.KEY_ID,
								Constants.TABLE_FEED_DRAFTS__TITLE,
								Constants.TABLE_FEED_DRAFTS__CONTENT,
								Constants.TABLE_FEED_DRAFTS__IMAGE_PATHS,
								Constants.TABLE_FEED_DRAFTS__SELECT_COLLECTION_IDS,
								Constants.TABLE_FEED_DRAFTS__SEND_TSINA,
								Constants.TABLE_FEED_DRAFTS__TIME,
								Constants.TABLE_FEED_DRAFTS__USER_ID
							}, 
				null, null, null, null,Constants.KEY_ID+ " asc");
		ArrayList<FeedDraft> fhs = new ArrayList<FeedDraft>();
		while(cursor.moveToNext()){
			int id = cursor.getInt(0);
			String title = cursor.getString(1);
			String content = cursor.getString(2);
			String image_paths = cursor.getString(3);
			String select_collection_ids = cursor.getString(4);
			boolean send_tsina = false;
			if(cursor.getInt(5) == 1) send_tsina = true;	
			long time = cursor.getLong(6);
			int user_id = cursor.getInt(7);
			FeedDraft fh = new FeedDraft(id,title, content, image_paths, select_collection_ids,send_tsina,time,user_id);
			fhs.add(fh);
		}
		db.close();
		return fhs;
	}
	
	private static SQLiteDatabase get_write_db(){
		MindpinDBHelper md = new MindpinDBHelper(MindpinApplication.context, Constants.DATABASE_NAME,
				null, Constants.DATABASE_VERSION);
		return md.getWritableDatabase();
	}
	
	private static SQLiteDatabase get_read_db(){
		MindpinDBHelper md = new MindpinDBHelper(MindpinApplication.context, Constants.DATABASE_NAME,
				null, Constants.DATABASE_VERSION);
		return md.getReadableDatabase();
	}
	
	public static void destroy_all(int user_id){
		SQLiteDatabase db = get_write_db();
		db.execSQL("DELETE FROM "+ Constants.TABLE_FEED_DRAFTS+" WHERE "+Constants.TABLE_FEED_DRAFTS__USER_ID+" = ?",new Object[]{user_id});
		db.close();
	}

	public static void destroy(int feed_id) {
		SQLiteDatabase db = get_write_db();
		db.execSQL("DELETE FROM "+ Constants.TABLE_FEED_DRAFTS +" WHERE "+Constants.KEY_ID+" = ?", new Object[]{feed_id});
		db.close();
	}

	public static void update(int feed_draft_id, String feed_title,
			String feed_content, String images_str,
			String select_collection_ids_str,boolean send_tsina) {
		SQLiteDatabase db = get_write_db();
		
		ContentValues values = new ContentValues();
		values.put(Constants.TABLE_FEED_DRAFTS__TITLE,feed_title);
		values.put(Constants.TABLE_FEED_DRAFTS__CONTENT,feed_content);
		values.put(Constants.TABLE_FEED_DRAFTS__IMAGE_PATHS,images_str);
		values.put(Constants.TABLE_FEED_DRAFTS__SELECT_COLLECTION_IDS,select_collection_ids_str);
		int send_tsina_int = send_tsina?1:0;
		values.put(Constants.TABLE_FEED_DRAFTS__SEND_TSINA,send_tsina_int);
		values.put(Constants.TABLE_FEED_DRAFTS__TIME,System.currentTimeMillis());
		
		db.update(Constants.TABLE_FEED_DRAFTS, values,Constants.KEY_ID + " = "+ feed_draft_id,null);
		db.close();
	}

	public static void insert( String title, String content,
			String images_str, String select_collection_ids_str,boolean send_tsina) {
		SQLiteDatabase db = get_write_db();
		int user_id = AccountManager.current_user().user_id;
		if(user_id == 0){
			return;
		}
		ContentValues values = new ContentValues();
		values.put(Constants.TABLE_FEED_DRAFTS__TITLE,title);
		values.put(Constants.TABLE_FEED_DRAFTS__CONTENT,content);
		values.put(Constants.TABLE_FEED_DRAFTS__IMAGE_PATHS,images_str);
		values.put(Constants.TABLE_FEED_DRAFTS__SELECT_COLLECTION_IDS,select_collection_ids_str);
		int send_tsina_int = send_tsina?1:0;
		values.put(Constants.TABLE_FEED_DRAFTS__SEND_TSINA,send_tsina_int);
		values.put(Constants.TABLE_FEED_DRAFTS__TIME,System.currentTimeMillis());
		values.put(Constants.TABLE_FEED_DRAFTS__USER_ID,user_id);
		db.insert(Constants.TABLE_FEED_DRAFTS,null, values);
		db.close();
	}
}
