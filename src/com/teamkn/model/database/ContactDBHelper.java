package com.teamkn.model.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.model.Contact;
import com.teamkn.model.Note;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class ContactDBHelper extends BaseModelDBHelper {
  public class Status{
    public static final String INVITED = "INVITED";
    public static final String BE_INVITED = "BE_INVITED";
    public static final String BE_REFUSED = "BE_REFUSED";
    public static final String APPLIED = "APPLIED";
    public static final String BE_REMOVED = "BE_REMOVED";
  }
  
  public static List<Contact> build_all_contacts(int current_user_id){
    List<Contact> be_invite_contacts = ContactDBHelper.be_invite_contacts(current_user_id);
    List<Contact> be_refused_contacts = ContactDBHelper.be_refused_contacts(current_user_id);
    List<Contact> applied_contacts = ContactDBHelper.applied_contacts(current_user_id);
    List<Contact> invite_contacts = ContactDBHelper.invite_contacts(current_user_id);
    List<Contact> be_removed_contacts = ContactDBHelper.be_removed_contacts(current_user_id);
    
    List<Contact> all_contacts = new ArrayList<Contact>();
    all_contacts.addAll(be_invite_contacts);
    all_contacts.addAll(be_refused_contacts);
    all_contacts.addAll(be_removed_contacts);
    all_contacts.addAll(applied_contacts);
    all_contacts.addAll(invite_contacts);
    return all_contacts;
  }
  
  public static long get_newest_server_updated_time(int current_user_id) throws Exception{
    SQLiteDatabase db = get_read_db();
    int newest_server_updated_time = 0;
    try {
      
      Cursor cursor = db.rawQuery("select max(server_updated_time) from " + Constants.TABLE_CONTACTS + " where " + Constants.TABLE_CONTACTS__USER_ID + " = " + current_user_id, null);
      if (cursor.moveToFirst()){
        newest_server_updated_time = cursor.getInt(0);
      }
    } catch (Exception e) {
        Log.e("ContactDBHelper", "find", e);
    } finally {
        db.close();
    }
    return newest_server_updated_time;
  }
  
  public static void create_or_update_by_contact_list_json(String json_string) throws Exception{
    JSONArray array = new JSONArray(json_string);
    for (int i = 0; i < array.length(); i++) {
      JSONObject obj = array.getJSONObject(i);
      create_or_update_by_contact_json(obj);
    }
  }
  
  public static void create_or_update_by_contact_json(String json_string) throws Exception{
    JSONObject obj = new JSONObject(json_string);
    create_or_update_by_contact_json(obj);
  }
  
  private static void create_or_update_by_contact_json(JSONObject json_obj) throws Exception{
    long server_created_time = json_obj.getLong("server_created_time");
    long server_updated_time = json_obj.getLong("server_updated_time");
    String status = json_obj.getString("status");
    String message = json_obj.getString("message");
    JSONObject user_info = json_obj.getJSONObject("contact_user_info");
    int contact_user_id = user_info.getInt("user_id");
    String contact_user_name = user_info.getString("user_name");
    String contact_user_avatar_url = user_info.getString("user_avatar_url");
    int current_user_id = AccountManager.current_user().user_id;
    
    ContactDBHelper.create_or_update(current_user_id,
        contact_user_id,contact_user_name,contact_user_avatar_url,
        status,message,server_created_time,server_updated_time);
  }
  
  private static void create_or_update(int current_user_id, int contact_user_id,
      String contact_user_name, String contact_user_avatar_url, String status, String message, long server_created_time, long server_updated_time) throws Exception {
    Contact contact = find(current_user_id,contact_user_id);
    if(contact.is_nil()){
      System.out.println("create contact");
      create(current_user_id,contact_user_id,contact_user_name,contact_user_avatar_url,status,message,server_created_time,server_updated_time);
    }else if(contact.server_updated_time < server_updated_time){
      
      System.out.println("update contact");
      System.out.println(contact.user_id);
      System.out.println(contact.contact_user_id);
      System.out.println(contact.contact_user_name);
      System.out.println(contact.message);
      System.out.println(contact.status);
      
      update(current_user_id,contact_user_id,contact_user_name,contact_user_avatar_url,status, message,server_created_time,server_updated_time);
    }
  }
  
  public static List<Contact> invite_contacts(int current_user_id){
    return find(current_user_id,Status.INVITED);
  }
  
  public static List<Contact> be_invite_contacts(int current_user_id){
    return find(current_user_id,Status.BE_INVITED);
  }
  
  public static List<Contact> be_refused_contacts(int current_user_id){
    return find(current_user_id,Status.BE_REFUSED);
  }
  
  public static List<Contact> be_removed_contacts(int current_user_id){
    return find(current_user_id,Status.BE_REMOVED);
  }
  
  public static List<Contact> applied_contacts(int current_user_id){
    return find(current_user_id,Status.APPLIED);
  }
  
  public static List<Contact> find(int current_user_id,String status){
    SQLiteDatabase db = get_read_db();
    List<Contact> list = new ArrayList<Contact>();
    
    try {
      
      Cursor cursor = db.query(Constants.TABLE_CONTACTS,
          get_columns(),
          Constants.TABLE_CONTACTS__USER_ID + " = ? AND " +
          Constants.TABLE_CONTACTS__STATUS + "= ?"
          , new String[]{current_user_id+"",status},
          null, null,null);
      
      while(cursor.moveToNext()){
        System.out.println("moveToNext");
        Contact contact = build_contact_by_cursor(cursor);
        list.add(contact);
      }
      
    } catch (Exception e) {
        Log.e("ContactDBHelper", "find", e);
    } finally {
        db.close();
    }
    return list;
  }
  
  public static Contact find(int current_user_id,int contact_user_id){
    SQLiteDatabase db = get_read_db();

    try {
        Cursor cursor = db.query(
                Constants.TABLE_CONTACTS, get_columns(),
                Constants.TABLE_CONTACTS__USER_ID + " = " + current_user_id + " and " + Constants.TABLE_CONTACTS__CONTACT_USER_ID + " = " + contact_user_id, 
                null, null, null, null
        );

        boolean has_result = cursor.moveToFirst();
        if (has_result) {
            return build_contact_by_cursor(cursor);
        } else {
            return Contact.NIL_CONTACT;
        }

    } catch (Exception e) {
        Log.e("ContactDBHelper", "find", e);
        return Contact.NIL_CONTACT;
    } finally {
        db.close();
    }
  }
  
  public static void destroy(int current_user_id, int other_user_id) {
    SQLiteDatabase db = get_write_db();

    try {
        db.delete(Constants.TABLE_CONTACTS,
            Constants.TABLE_CONTACTS__USER_ID + " = " + current_user_id + " and " + Constants.TABLE_CONTACTS__CONTACT_USER_ID + " = " + other_user_id,
            null);
    } catch (Exception e) {
        Log.e("ContactDBHelper", "destroy", e);
    } finally {
        db.close();
    }
  }
  
  private static void update(int current_user_id, int contact_user_id,
      String contact_user_name, String contact_user_avatar_url, String status, String message,
      long server_created_time, long server_updated_time) throws Exception {
    
    ContentValues values = new ContentValues();
    if(contact_user_avatar_url != null && !contact_user_avatar_url.equals("")){
      InputStream is = HttpApi.download_image(contact_user_avatar_url);
      byte[] avatar = IOUtils.toByteArray(is);
      values.put(Constants.TABLE_CONTACTS__CONTACT_USER_AVATAR, avatar);
    }
    
    values.put(Constants.TABLE_CONTACTS__CONTACT_USER_NAME, contact_user_name);
    values.put(Constants.TABLE_CONTACTS__MESSAGE, message);
    values.put(Constants.TABLE_CONTACTS__STATUS, status);
    values.put(Constants.TABLE_CONTACTS__SERVER_CREATED_TIME, server_created_time);
    values.put(Constants.TABLE_CONTACTS__SERVER_UPDATED_TIME, server_updated_time);

    update_columns(current_user_id,contact_user_id, values);
  }
  
  private static void update_columns(int current_user_id,
      int contact_user_id, ContentValues values) {
    
    SQLiteDatabase db = get_write_db();
    
    try {
        // 保存数据库信息
        db.update(Constants.TABLE_CONTACTS, values,
            Constants.TABLE_CONTACTS__USER_ID + " = " + current_user_id + " and " + Constants.TABLE_CONTACTS__CONTACT_USER_ID + " = " + contact_user_id,
                null);
    } catch (Exception e) {
        Log.e("ContactDBHelper", "update_columns", e);
    } finally {
        db.close();
    }
  }

  private static void create(int current_user_id, int contact_user_id,
      String contact_user_name, String contact_user_avatar_url, String status, String message, long created_time, long updated_time) throws Exception{
    
    ContentValues values = new ContentValues();
    if(contact_user_avatar_url != null && !contact_user_avatar_url.equals("")){
      InputStream is = HttpApi.download_image(contact_user_avatar_url);
      byte[] avatar = IOUtils.toByteArray(is);
      values.put(Constants.TABLE_CONTACTS__CONTACT_USER_AVATAR, avatar);
    }
    values.put(Constants.TABLE_CONTACTS__USER_ID, current_user_id);
    values.put(Constants.TABLE_CONTACTS__CONTACT_USER_ID, contact_user_id);
    values.put(Constants.TABLE_CONTACTS__CONTACT_USER_NAME, contact_user_name);
    values.put(Constants.TABLE_CONTACTS__MESSAGE, message);
    values.put(Constants.TABLE_CONTACTS__STATUS, status);
    values.put(Constants.TABLE_CONTACTS__SERVER_CREATED_TIME, created_time);
    values.put(Constants.TABLE_CONTACTS__SERVER_UPDATED_TIME, updated_time);
    create_item(values);
  }
  
  private static void create_item(ContentValues values) {
    SQLiteDatabase db = get_write_db();

    try {
        db.insert(Constants.TABLE_CONTACTS, null, values);
    } catch (Exception e) {
        Log.e("ContactDBHelper", "create_item", e);
    } finally {
        db.close();
    }
  }
  
  private static String[] get_columns() {
    return new String[]{
            Constants.KEY_ID, 
            Constants.TABLE_CONTACTS__USER_ID,
            Constants.TABLE_CONTACTS__CONTACT_USER_ID,
            Constants.TABLE_CONTACTS__CONTACT_USER_NAME,
            Constants.TABLE_CONTACTS__CONTACT_USER_AVATAR,
            Constants.TABLE_CONTACTS__MESSAGE,
            Constants.TABLE_CONTACTS__STATUS,
            Constants.TABLE_CONTACTS__SERVER_CREATED_TIME,
            Constants.TABLE_CONTACTS__SERVER_UPDATED_TIME};
  }
  
  private static Contact build_contact_by_cursor(Cursor cursor){
    int id = cursor.getInt(0);
    int user_id = cursor.getInt(1);
    int contact_user_id = cursor.getInt(2);
    String contact_user_name = cursor.getString(3);
    byte[] contact_user_avatar = cursor.getBlob(4);
    String message = cursor.getString(5);
    String status = cursor.getString(6);
    long server_created_time = cursor.getLong(7);
    long server_updated_time = cursor.getLong(8);
    
    return new Contact(id,user_id,contact_user_id,contact_user_name,contact_user_avatar,message,status,server_created_time,server_updated_time);
  }

}
