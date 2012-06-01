package com.teamkn.model.base;

public class Constants {
	public static final String DATABASE_NAME = "teamkn";
	public static final int DATABASE_VERSION = 12;
	public static final String KEY_ID        ="_id";
	
	// 账号 数据表的常量
	public static final String TABLE_USERS           = "users";
	public static final String TABLE_USERS__USER_ID  = "user_id";
	public static final String TABLE_USERS__NAME     = "name";
	public static final String TABLE_USERS__COOKIES  = "cookies";
	public static final String TABLE_USERS__INFO     = "info";
	
	// note 数据表的常量
	public static final String TABLE_NOTES             = "notes";
	public static final String TABLE_NOTES__UUID       = "uuid";
	public static final String TABLE_NOTES__CONTENT    = "content";
	public static final String TABLE_NOTES__TYPE       = "type";
	public static final String TABLE_NOTES__IS_REMOVED = "is_removed";
	public static final String TABLE_NOTES__CREATED_AT = "created_at";
	public static final String TABLE_NOTES__UPDATED_AT = "updated_at";
	
}
