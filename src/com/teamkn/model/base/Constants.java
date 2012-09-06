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

    // chat 数据表的常量
    public static final String TABLE_CHATS = "chats";
    public static final String TABLE_CHATS__UUID = "uuid";
    public static final String TABLE_CHATS__SERVER_CHAT_ID = "server_chat_id";
    public static final String TABLE_CHATS__SERVER_CREATED_TIME = "server_created_time";
    public static final String TABLE_CHATS__SERVER_UPDATED_TIME = "server_updated_time";
    
    // chat_memberships 数据表的常量
    public static final String TABLE_CHAT_MEMBERSHIPS = "chat_memberships";
    public static final String TABLE_CHAT_MEMBERSHIPS__CLIENT_CHAT_ID = "client_chat_id";
    public static final String TABLE_CHAT_MEMBERSHIPS__CLIENT_USER_ID = "client_user_id";
    
    // chat_nodes 数据表的常量
    public static final String TABLE_CHAT_NODES = "chat_nodes";
    public static final String TABLE_CHAT_NODES__UUID = "uuid";
    public static final String TABLE_CHAT_NODES__CLIENT_CHAT_ID = "client_chat_id";
    public static final String TABLE_CHAT_NODES__SERVER_CHAT_NODE_ID = "server_chat_node_id";
    public static final String TABLE_CHAT_NODES__CONTENT = "content";
    public static final String TABLE_CHAT_NODES__KIND = "kind";
    public static final String TABLE_CHAT_NODES__SERVER_CREATED_TIME = "server_created_time";
    public static final String TABLE_CHAT_NODES__CLIENT_USER_ID = "client_user_id";
    
    // user 数据表的常量
    public static final String TABLE_USERS = "users";
    public static final String TABLE_USERS__USER_ID = "user_id";
    public static final String TABLE_USERS__USER_NAME = "user_name";
    public static final String TABLE_USERS__USER_AVATAR = "user_avatar";
    public static final String TABLE_USERS__SERVER_CREATED_TIME = "server_created_time";
    public static final String TABLE_USERS__SERVER_UPDATED_TIME = "server_updated_time";
    
    // attitudes 数据库表的常量
    public static final String TABLE_ATTITUDES = "attitudes";
    public static final String TABLE_ATTITUDES__CHAT_NODE_ID = "chat_node_id";
    public static final String TABLE_ATTITUDES__CLIENT_USER_ID = "client_user_id";
    public static final String TABLE_ATTITUDES__KIND = "kind";
    public static final String TABLE_ATTITUDES_IS_SYNED = "is_syned";
    
    
    // data_lists
    
    public static final String TABLE_DATA_LISTS = "data_lists";
    public static final String TABLE_DATA_LISTS_USER_ID = "user_id";
    public static final String TABLE_DATA_LISTS_TITLE = "title";
    public static final String TABLE_DATA_LISTS_KIND = "kind";
    public static final String TABLE_DATA_LISTS_PUBLIC = "public";
    public static final String TABLE_DATA_LISTS_SERVER_DATA_LIST_ID = "server_data_list_id";
    
    
    // data_items
    public static final String TABLE_DATA_ITEMS = "data_items";
    public static final String TABLE_DATA_ITEMS_TITLE = "title";
    public static final String TABLE_DATA_ITEMS_CONTENT = "content";
    public static final String TABLE_DATA_ITEMS_URL = "url";
    public static final String TABLE_DATA_ITEMS_KIND = "kind";
    public static final String TABLE_DATA_ITEMS_DATA_LIST_ID = "data_list_id";
    public static final String TABLE_DATA_ITEMS_POSITION = "position";
    
    // watch
    public static final String TABLE_WATCH = "watch";
    public static final String TABLE_WATCH_USER_ID = "user_id";
    public static final String TABLE_WATCH_DATA_LIST_ID = "data_list_id";
}
/*
 *  users
 2  integer :user_id
 3  text :user_name
 4  blob :user_avatar
 5
 6  data_lists
 7  integer :user_id
 8  text    :title
 9  text    :kind  # COLLECTION STEP
10  text    :public  # true false
11  integer :server_data_list_id
12
13  data_items
14  text :title
15  text :content
16  text :url
17  text :kind  # URL TEXT IMAGE
18  integer :data_list_id
19  integer :position
20
21  watch
22  integer :user_id
23  integer :data_list_id
 * */
