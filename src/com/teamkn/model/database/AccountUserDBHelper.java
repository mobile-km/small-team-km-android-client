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
                    Constants.TABLE_USERS,
                    new String[]{
                            Constants.KEY_ID,
                            Constants.TABLE_USERS__COOKIES,
                            Constants.TABLE_USERS__INFO
                    }, null, null, null, null,
                    Constants.KEY_ID + " ASC"
            );

            List<AccountUser> users = new ArrayList<AccountUser>();
            while (cursor.moveToNext()) {
                String cookies = cursor.getString(1);
                String info = cursor.getString(2);
                users.add(new AccountUser(cookies, info));
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
                    Constants.TABLE_USERS,
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
                    "DELETE FROM " + Constants.TABLE_USERS + " WHERE "
                            + Constants.TABLE_USERS__USER_ID + " = ?",
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
            values.put(Constants.TABLE_USERS__USER_ID, account_user.user_id);
            values.put(Constants.TABLE_USERS__NAME, account_user.name);
            values.put(Constants.TABLE_USERS__COOKIES, account_user.cookies);
            values.put(Constants.TABLE_USERS__INFO, account_user.info);

            AccountUser o_user = find(account_user.user_id);

            if (o_user.is_nil()) {
                db.insert(Constants.TABLE_USERS, null, values);
            } else {
                db.update(Constants.TABLE_USERS, values, Constants.TABLE_USERS__USER_ID + " = " + account_user.user_id, null);
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
                    Constants.TABLE_USERS, new String[]{
                    Constants.KEY_ID,
                    Constants.TABLE_USERS__USER_ID,
                    Constants.TABLE_USERS__COOKIES,
                    Constants.TABLE_USERS__INFO
            },
                    Constants.TABLE_USERS__USER_ID + " = " + user_id,
                    null, null, null, null
            );

            boolean has_result = cursor.moveToFirst();

            if (has_result) {
                String cookies = cursor.getString(2);
                String info = cursor.getString(3);
                return new AccountUser(cookies, info);
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

}
