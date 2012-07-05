package com.teamkn.model.base;

public class Constants {
    public static final String DATABASE_NAME = "teamkn";
    public static final int DATABASE_VERSION = 21;
    public static final String KEY_ID = "_id";

    // 账号 数据表的常量
    public static final String TABLE_ACCOUNT_USERS = "account_users";
    public static final String TABLE_ACCOUNT_USERS__USER_ID = "user_id";
    public static final String TABLE_ACCOUNT_USERS__NAME = "name";
    public static final String TABLE_ACCOUNT_USERS__AVATAR = "avatar";
    public static final String TABLE_ACCOUNT_USERS__COOKIES = "cookies";
    public static final String TABLE_ACCOUNT_USERS__INFO = "info";

    // note 数据表的常量
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_NOTES__UUID = "uuid";
    public static final String TABLE_NOTES__CONTENT = "content";
    public static final String TABLE_NOTES__KIND = "kind";
    public static final String TABLE_NOTES__IS_REMOVED = "is_removed";
    public static final String TABLE_NOTES__IS_CHANGED_BY_CLIENT = "is_changed_by_client";
    public static final String TABLE_NOTES__CLIENT_CREATED_TIME = "client_created_time";
    public static final String TABLE_NOTES__CLIENT_UPDATED_TIME = "client_updated_time";
    public static final String TABLE_NOTES__SYNED_SERVER_TIME   = "syned_server_time"; 
    
    // contact 数据表的常量
    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_CONTACTS__USER_ID = "user_id";
    public static final String TABLE_CONTACTS__CONTACT_USER_ID = "contact_user_id";
    public static final String TABLE_CONTACTS__CONTACT_USER_NAME = "contact_user_name";
    public static final String TABLE_CONTACTS__CONTACT_USER_AVATAR = "contact_user_avatar";
    public static final String TABLE_CONTACTS__MESSAGE = "message";
    public static final String TABLE_CONTACTS__STATUS = "status";
    public static final String TABLE_CONTACTS__SERVER_CREATED_TIME = "server_created_time";
    public static final String TABLE_CONTACTS__SERVER_UPDATED_TIME = "server_updated_time";
}
