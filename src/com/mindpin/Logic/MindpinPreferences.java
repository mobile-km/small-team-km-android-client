package com.mindpin.Logic;

import com.mindpin.R;
import com.mindpin.application.MindpinApplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MindpinPreferences {

	public static int get_photo_quality(){
		SharedPreferences a = PreferenceManager.getDefaultSharedPreferences(MindpinApplication.context);
		String key = MindpinApplication.context.getResources().getString(R.string.upload_photo_quality);
		String size = a.getString(key, "0");
		return Integer.parseInt(size);
	}
}
