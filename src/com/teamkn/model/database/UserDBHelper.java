package com.teamkn.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.model.User;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class UserDBHelper extends BaseModelDBHelper {
  public static User find(int user_id){
    User user;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_USERS, get_columns(),
        Constants.TABLE_USERS__USER_ID + " = ?", 
        new String[]{user_id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
      user = build_by_cursor(cursor);
    }else{
      user = User.NIL_USER;
    }
    
    cursor.close();
    db.close();
    return user;
  }

  private static User build_by_cursor(Cursor cursor) {
    int id = cursor.getInt(0);
    int user_id = cursor.getInt(1);
    String user_name = cursor.getString(2);
    byte[] user_avatar = cursor.getBlob(3);
    long server_created_time = cursor.getLong(4);
    long server_updated_time = cursor.getLong(5);
    return new User(id,user_id,user_name,user_avatar,server_created_time,server_updated_time);
  }

  private static String[] get_columns() {
    return new String[]{
        Constants.KEY_ID,
        Constants.TABLE_USERS__USER_ID,
        Constants.TABLE_USERS__USER_NAME,
        Constants.TABLE_USERS__USER_AVATAR,
        Constants.TABLE_USERS__SERVER_CREATED_TIME,
        Constants.TABLE_USERS__SERVER_UPDATED_TIME
    };
  }
}
