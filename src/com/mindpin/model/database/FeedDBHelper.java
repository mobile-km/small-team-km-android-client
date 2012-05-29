package com.mindpin.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mindpin.database.Constants;
import com.mindpin.model.Feed;
import com.mindpin.model.base.BaseModelDBHelper;

public class FeedDBHelper extends BaseModelDBHelper {

	private static boolean create(final Feed feed) {
		SQLiteDatabase db = get_write_db();
		
		try{
			
			ContentValues values = new ContentValues();
			values.put(Constants.TABLE_FEEDS__ID, feed.feed_id);
			values.put(Constants.TABLE_FEEDS__JSON, feed.json);
			values.put(Constants.TABLE_FEEDS__UPDATED_AT, feed.updated_at);
			values.put(Constants.TABLE_FEEDS__USER_ID, feed.creator.user_id);
			
			db.insert(Constants.TABLE_FEEDS, null, values);
			
			return true;
			
		} catch (Exception e) {
			Log.e("FeedDBHelper", "create", e);
			return false;
		} finally {
			db.close();
		}
	}
	
	private static boolean update(final Feed feed) {
		SQLiteDatabase db = get_write_db();
		
		try{
			
			ContentValues values = new ContentValues();
			values.put(Constants.TABLE_FEEDS__JSON, feed.json);
			values.put(Constants.TABLE_FEEDS__UPDATED_AT, feed.updated_at);
			db.update(
				Constants.TABLE_FEEDS, 
				values, 
				Constants.TABLE_FEEDS__ID + " = " + feed.feed_id, null
			);
			
			return true;
			
		} catch (Exception e) {
			Log.e("FeedDBHelper", "update", e);
			return false;
		} finally {
			db.close();
		}
	}
	
	// 创建或更新feed，数据库中不存在，或者传入feed较新时，写入数据库
	// 有更新数据库时返回true
	// 无更新数据库时返回false
	// 不代表更新成功或失败
	final public static boolean create_or_update(final Feed feed) {
		Feed old_feed = find(feed.feed_id);
		//Log.d("FeedDBHelper", "create or update");
		
		if (old_feed.is_nil()) {
			Log.d("FeedDBHelper", "create");
			return create(feed);
		} else if (feed.updated_at > old_feed.updated_at) {
			Log.d("FeedDBHelper", "update");
			return update(feed);
		}
		return false;
	}
	
	final private static Feed find(int feed_id) {
		SQLiteDatabase db = get_read_db();
		
		try{
		
			Cursor cursor = db.query(
				Constants.TABLE_FEEDS,
				new String[] { 
					Constants.TABLE_FEEDS__JSON 
				},
				Constants.TABLE_FEEDS__ID + " = " + feed_id, 
				null, null, null, null
			);
			
			boolean has_result = cursor.moveToFirst();
			
			if (has_result) {
				String json = cursor.getString(0);
				return Feed.build(json);
			} else {
				return Feed.NIL_FEED;
			}
			
		} catch (Exception e) {
			Log.e("FeedDBHelper", "find", e);
			return Feed.NIL_FEED;
		} finally {
			db.close();
		}
	}
	
}
