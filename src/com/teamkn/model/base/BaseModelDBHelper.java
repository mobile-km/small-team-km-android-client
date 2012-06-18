package com.teamkn.model.base;

import android.database.sqlite.SQLiteDatabase;
import com.teamkn.application.TeamknApplication;

abstract public class BaseModelDBHelper {
    final private static TeamknDBHelper get_db_helper() {
        return new TeamknDBHelper(TeamknApplication.context,
                Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    final public static SQLiteDatabase get_write_db() {
        return get_db_helper().getWritableDatabase();
    }

    final public static SQLiteDatabase get_read_db() {
        return get_db_helper().getReadableDatabase();
    }
}
