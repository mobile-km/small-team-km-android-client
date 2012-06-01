package com.teamkn.Logic;

import com.teamkn.R;
import com.teamkn.application.TeamknApplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TeamknPreferences {

	public static int get_photo_quality(){
		SharedPreferences a = PreferenceManager.getDefaultSharedPreferences(TeamknApplication.context);
		String key = TeamknApplication.context.getResources().getString(R.string.upload_photo_quality);
		String size = a.getString(key, "0");
		return Integer.parseInt(size);
	}
}
