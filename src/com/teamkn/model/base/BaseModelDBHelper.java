package com.teamkn.model.base;

import android.database.sqlite.SQLiteDatabase;

import com.teamkn.application.MindpinApplication;

abstract public class BaseModelDBHelper {
	final private static MindpinDBHelper get_db_helper() {
		return new MindpinDBHelper(MindpinApplication.context,
				Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
	}

	final public static SQLiteDatabase get_write_db() {
		return get_db_helper().getWritableDatabase();
	}

	final public static SQLiteDatabase get_read_db() {
		return get_db_helper().getReadableDatabase();
	}
}
