package com.teamkn.model.base;

public class Constants {
    public static final String DATABASE_NAME = "teamkn";
    public static final int DATABASE_VERSION = 27;
    public static final String KEY_ID = "_id";
    
    // 账号 数据表的常量
    public static final String TABLE_ACCOUNT_USERS = "account_users";
    public static final String TABLE_ACCOUNT_USERS__USER_ID = "user_id";
    public static final String TABLE_ACCOUNT_USERS__NAME = "name";
    public static final String TABLE_ACCOUNT_USERS__AVATAR = "avatar";
    public static final String TABLE_ACCOUNT_USERS__COOKIES = "cookies";
    public static final String TABLE_ACCOUNT_USERS__INFO = "info";
    
    public static final String TABLE_ACCOUNT_USERS__IS_SHOW_TIP = "is_show_tip";

    // user 数据表的常量
    public static final String TABLE_USERS = "users";
    public static final String TABLE_USERS__USER_ID = "user_id";
    public static final String TABLE_USERS__USER_NAME = "user_name";
    public static final String TABLE_USERS__USER_AVATAR = "user_avatar";
    public static final String TABLE_USERS__AVATAR_URL = "avatar_url";
    public static final String TABLE_USERS__SERVER_CREATED_TIME = "server_created_time";
    public static final String TABLE_USERS__SERVER_UPDATED_TIME = "server_updated_time";

}
