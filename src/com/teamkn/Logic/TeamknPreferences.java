package com.teamkn.Logic;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.teamkn.R;
import com.teamkn.application.TeamknApplication;

public class TeamknPreferences {
  public static final SharedPreferences PREFERENCES = PreferenceManager.getDefaultSharedPreferences(TeamknApplication.context);
  
  public static void put_int(String key_name,int value){
    Editor pre_edit = PREFERENCES.edit();
    pre_edit.putInt(key_name, value);
    pre_edit.commit();
  }
  
  public static void put_long(String key_name,long value){
    Editor pre_edit = PREFERENCES.edit();
    pre_edit.putLong(key_name, value);
    pre_edit.commit();
  }
  
  public static void put_boolean(String key_name,boolean value){
    Editor pre_edit = PREFERENCES.edit();
    pre_edit.putBoolean(key_name, value);
    pre_edit.commit();
  }
  
  public static void put_string(String key_name, String value) {
    Editor pre_edit = PREFERENCES.edit();
    pre_edit.putString(key_name, value);
    pre_edit.commit();
  }
  
  public static String get_resource_string(int resource_id){
    return TeamknApplication.context.getResources().getString(resource_id);
  }
  
	public static int get_photo_quality(){
	  String key = get_resource_string(R.string.preferences_key_upload_photo_quality);
		return PREFERENCES.getInt(key, 0);
	}
	
	public static void set_current_user_id(int id){
	 String key = get_resource_string(R.string.preferences_key_current_user_id);
	 TeamknPreferences.put_int(key, id);
	}
	
	public static int current_user_id(){
	  String key = get_resource_string(R.string.preferences_key_current_user_id); 
	  return PREFERENCES.getInt(key, 0);
	}
	
  public static long syn_contact_timestamp(){
    String key = get_resource_string(R.string.preferences_key_syn_contact_timestamp);
    return PREFERENCES.getLong(key, 0);
  }

  public static void set_syn_contact_timestamp(long timestamp) {
    String key = get_resource_string(R.string.preferences_key_syn_contact_timestamp);
    TeamknPreferences.put_long(key, timestamp);
  }
  
  public static boolean never_syn(){
    long timestamp = syn_contact_timestamp();
    if(timestamp == 0){
      return true;
    }else{
      return false;
    }
  }
  
  public static long last_syn_server_meta_updated_time(){
    String key = get_resource_string(R.string.preferences_key_last_syn_server_meta_updated_time);
    return PREFERENCES.getLong(key, 0);
  }
  
  public static void set_last_syn_server_meta_updated_time(long time){
    String key = get_resource_string(R.string.preferences_key_last_syn_server_meta_updated_time);
    TeamknPreferences.put_long(key, time);
  }
  
  public static long last_syn_success_server_time(){
    String key = get_resource_string(R.string.preferences_key_last_syn_success_server_time);
    return PREFERENCES.getLong(key, 0);
  }
  
  public static void set_last_syn_success_server_time(long time){
    String key = get_resource_string(R.string.preferences_key_last_syn_success_server_time);
    TeamknPreferences.put_long(key, time);
  }
  
  public static long last_syn_success_client_time(){
    String key = get_resource_string(R.string.preferences_key_last_syn_success_client_time);
    return PREFERENCES.getLong(key, 0);
  }
  
  public static void touch_last_syn_success_client_time(){
    long time = System.currentTimeMillis();
    String key = get_resource_string(R.string.preferences_key_last_syn_success_client_time);
    TeamknPreferences.put_long(key, time);
  }
  
  public static long last_syn_fail_client_time(){
    String key = get_resource_string(R.string.preferences_key_last_syn_fail_client_time);
    return PREFERENCES.getLong(key, 0);
  }
  
  public static void touch_last_syn_fail_client_time(){
    long time = System.currentTimeMillis();
    String key = get_resource_string(R.string.preferences_key_last_syn_fail_client_time);
    TeamknPreferences.put_long(key, time);
  }
  
}
