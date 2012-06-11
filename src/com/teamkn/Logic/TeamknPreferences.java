package com.teamkn.Logic;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.teamkn.R;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.utils.BaseUtils;

public class TeamknPreferences {
    public static final SharedPreferences PREFERENCES = PreferenceManager.getDefaultSharedPreferences(TeamknApplication.context);

    public static void put_int(String key_name, int value) {
        Editor pre_edit = PREFERENCES.edit();
        pre_edit.putInt(key_name, value);
        pre_edit.commit();
    }

    public static void put_long(String key_name, long value) {
        Editor pre_edit = PREFERENCES.edit();
        pre_edit.putLong(key_name, value);
        pre_edit.commit();
    }

    public static String get_resource_string(int resource_id) {
        return TeamknApplication.context.getResources().getString(resource_id);
    }

    public static int get_photo_quality() {
        String key = get_resource_string(R.string.preferences_key_upload_photo_quality);
        return PREFERENCES.getInt(key, 0);
    }

    public static void set_current_user_id(int id) {
        String key = get_resource_string(R.string.preferences_key_current_user_id);
        TeamknPreferences.put_int(key, id);
    }

    public static int current_user_id() {
        String key = get_resource_string(R.string.preferences_key_current_user_id);
        return PREFERENCES.getInt(key, 0);
    }

    public static String last_syn_time() {
        String key = get_resource_string(R.string.preferences_key_last_syn_time);
        long time = PREFERENCES.getLong(key, 0);
        if (time == 0) {
            return null;
        } else {
            return BaseUtils.date_string(time);
        }
    }

    public static void touch_last_syn_time() {
        long time = System.currentTimeMillis();
        String key = get_resource_string(R.string.preferences_key_last_syn_time);
        TeamknPreferences.put_long(key, time);
    }
}
