package com.teamkn.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.teamkn.model.AccountUser;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

import java.util.ArrayList;
import java.util.List;

public class AccountUserDBHelper extends BaseModelDBHelper {

    final public static List<AccountUser> all() throws Exception {
        SQLiteDatabase db = get_read_db();

        try {
            Cursor cursor = db.query(
                    Constants.TABLE_ACCOUNT_USERS,
                    new String[]{
                            Constants.KEY_ID,
                            Constants.TABLE_ACCOUNT_USERS__COOKIES,
                            Constants.TABLE_ACCOUNT_USERS__INFO,
                            Constants.TABLE_ACCOUNT_USERS__AVATAR,
                            Constants.TABLE_ACCOUNT_USERS__IS_SHOW_TIP
                    }, null, null, null, null,
                    Constants.KEY_ID + " ASC"
            );

            List<AccountUser> users = new ArrayList<AccountUser>();
            while (cursor.moveToNext()) {
                String cookies = cursor.getString(1);
                String info = cursor.getString(2);
                byte[] avatar = cursor.getBlob(3);
                String is_show_tip_str = cursor.getString(4);
                boolean is_show_tip = Boolean.parseBoolean(is_show_tip_str);
                users.add(new AccountUser(cookies, info, avatar, is_show_tip));
            }

            return users;

        } catch (Exception e) {
            Log.e("AccountUserDBHelper", "all", e);
            throw e;
        } finally {
            db.close();
        }

    }

    final public static int count() {
        SQLiteDatabase db = get_read_db();

        try {
            Cursor cursor = db.query(
                    Constants.TABLE_ACCOUNT_USERS,
                    new String[]{}, null, null, null, null, null
            );

            return cursor.getCount();

        } catch (Exception e) {
            Log.e("AccountUserDBHelper", "count", e);
            return 0;
        } finally {
            db.close();
        }
    }

    // 删除，同时删除了数据库信息，收集册缓存，草稿
    final public static boolean destroy(final AccountUser account_user) {
        if (account_user.is_nil()) return false;
        SQLiteDatabase db = get_write_db();

        try {
            // 删除数据库信息
            db.execSQL(
                    "DELETE FROM " + Constants.TABLE_ACCOUNT_USERS + " WHERE "
                            + Constants.TABLE_ACCOUNT_USERS__USER_ID + " = ?",
                    new Object[]{
                            account_user.user_id
                    }
            );

            return true;
        } catch (Exception e) {
            Log.e("AccountUserDBHelper", "destroy", e);
            return false;
        } finally {
            db.close();
        }
    }

    // 保存
    final public static boolean save(final AccountUser account_user) {

        Log.d("teamkn", account_user.is_nil() + "");

        if (account_user.is_nil()) return false;
        SQLiteDatabase db = get_write_db();

        try {
            // 保存数据库信息
            ContentValues values = new ContentValues();
            values.put(Constants.TABLE_ACCOUNT_USERS__USER_ID, account_user.user_id);
            values.put(Constants.TABLE_ACCOUNT_USERS__NAME, account_user.name);
            values.put(Constants.TABLE_ACCOUNT_USERS__COOKIES, account_user.cookies);
            values.put(Constants.TABLE_ACCOUNT_USERS__INFO, account_user.info);
            values.put(Constants.TABLE_ACCOUNT_USERS__AVATAR,account_user.avatar);
            values.put(Constants.TABLE_ACCOUNT_USERS__IS_SHOW_TIP,account_user.is_show_tip);
           
            AccountUser o_user = find(account_user.user_id);
           
            if (o_user.is_nil()) {
                db.insert(Constants.TABLE_ACCOUNT_USERS, null, values);
            } else {
                db.update(Constants.TABLE_ACCOUNT_USERS, values, Constants.TABLE_ACCOUNT_USERS__USER_ID + " = " + account_user.user_id, null);
            }
            return true;
        } catch (Exception e) {
            Log.e("AccountUserDBHelper", "save", e);
            return false;
        } finally {
            db.close();
        }
    }

    final public static AccountUser find(int user_id) {
        SQLiteDatabase db = get_read_db();
     
        try {
            Cursor cursor = db.query(
                    Constants.TABLE_ACCOUNT_USERS, new String[]{
                    Constants.KEY_ID,
                    Constants.TABLE_ACCOUNT_USERS__USER_ID,
                    Constants.TABLE_ACCOUNT_USERS__COOKIES,
                    Constants.TABLE_ACCOUNT_USERS__INFO,
                    Constants.TABLE_ACCOUNT_USERS__AVATAR,
                    Constants.TABLE_ACCOUNT_USERS__IS_SHOW_TIP
            },
                    Constants.TABLE_ACCOUNT_USERS__USER_ID + " = " + user_id,
                    null, null, null, null
            );

            boolean has_result = cursor.moveToFirst();

            if (has_result) {
                String cookies = cursor.getString(2);
                String info = cursor.getString(3);
                byte[] avatar = cursor.getBlob(4);
                String is_show_tip_str = cursor.getString(5);
                boolean is_show_tip = false;
                if(is_show_tip_str.equals("1")){
                	is_show_tip = true;
                }
                return new AccountUser(cookies, info, avatar,is_show_tip);
            } else {
                return AccountUser.NIL_ACCOUNT_USER;
            }

        } catch (Exception e) {
            Log.e("AccountUserDBHelper", "find", e);
            return AccountUser.NIL_ACCOUNT_USER;
        } finally {
            db.close();
        }
    }
    // 保存
    final public static boolean update_show_help(final AccountUser account_user) {

        Log.d("teamkn", account_user.is_nil() + "");

        if (account_user.is_nil()) return false;
        SQLiteDatabase db = get_write_db();

        try {
            // 保存数据库信息
            ContentValues values = new ContentValues();
            values.put(Constants.TABLE_ACCOUNT_USERS__USER_ID, account_user.user_id);
            values.put(Constants.TABLE_ACCOUNT_USERS__NAME, account_user.name);
            values.put(Constants.TABLE_ACCOUNT_USERS__COOKIES, account_user.cookies);
            values.put(Constants.TABLE_ACCOUNT_USERS__INFO, account_user.info);
            values.put(Constants.TABLE_ACCOUNT_USERS__AVATAR,account_user.avatar);
            values.put(Constants.TABLE_ACCOUNT_USERS__IS_SHOW_TIP,account_user.is_show_tip);
           
            AccountUser o_user = find(account_user.user_id); 
            if (o_user.is_nil()) {
                db.insert(Constants.TABLE_ACCOUNT_USERS, null, values);
            } else {
                db.update(Constants.TABLE_ACCOUNT_USERS, values, Constants.TABLE_ACCOUNT_USERS__USER_ID + " = " + account_user.user_id, null);
            }

            return true;
        } catch (Exception e) {
            Log.e("AccountUserDBHelper", "save", e);
            return false;
        } finally {
            db.close();
        }
    }
    
}
