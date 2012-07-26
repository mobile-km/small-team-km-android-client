package com.teamkn.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedParam {
	public static void saveParam(Context context,int last_syn_attitudes_updated_time){
		SharedPreferences sp = context.getSharedPreferences("last_syn_attitudes_updated_time", Activity.MODE_PRIVATE);
		sp.edit().putInt("last_syn_attitudes_updated_time", last_syn_attitudes_updated_time).commit();

	}
	public static int getParam(Context context){
		
		SharedPreferences sp = context.getSharedPreferences("last_syn_attitudes_updated_time", Activity.MODE_PRIVATE);
		return sp.getInt("last_syn_attitudes_updated_time", 0);
	}
}
