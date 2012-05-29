package com.mindpin.database;

public class Constants {
	public static final String DATABASE_NAME = "mindpin";
	public static final int DATABASE_VERSION = 9;
	public static final String KEY_ID="_id";
	
	// 主题草稿 数据表的常量
	public static final String TABLE_FEED_DRAFTS = "feed_drafts";
	public static final String TABLE_FEED_DRAFTS__TITLE = "title";
	public static final String TABLE_FEED_DRAFTS__CONTENT = "content";
	public static final String TABLE_FEED_DRAFTS__IMAGE_PATHS = "image_paths";
	public static final String TABLE_FEED_DRAFTS__SELECT_COLLECTION_IDS = "select_collection_ids";
	public static final String TABLE_FEED_DRAFTS__SEND_TSINA = "send_tsina";
	public static final String TABLE_FEED_DRAFTS__TIME = "time";
	public static final String TABLE_FEED_DRAFTS__USER_ID = "user_id";
	
	// 账号 数据表的常量
	public static final String TABLE_USERS = "users";
	public static final String TABLE_USERS__USER_ID = "user_id";
	public static final String TABLE_USERS__NAME = "name";
	public static final String TABLE_USERS__COOKIES = "cookies";
	public static final String TABLE_USERS__INFO = "info";
	
	// feed 缓存
	public static final String TABLE_FEEDS = "feeds";
	public static final String TABLE_FEEDS__ID = "feed_id";
	public static final String TABLE_FEEDS__USER_ID = "user_id";
	public static final String TABLE_FEEDS__JSON = "json";
	public static final String TABLE_FEEDS__UPDATED_AT = "updated_at";
}
