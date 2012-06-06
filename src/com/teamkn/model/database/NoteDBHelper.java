package com.teamkn.model.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.Note;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class NoteDBHelper extends BaseModelDBHelper {
  public class Kind{
    public static final String TEXT = "TEXT";
    public static final String IMAGE = "IMAGE";
  }
  
  
  final public static List<Note> all(boolean has_removed) throws Exception {
    SQLiteDatabase db = get_read_db();
    List<Note> notes = new ArrayList<Note>();
    Cursor cursor;
    try {
      if(has_removed){
        cursor = db.query(Constants.TABLE_NOTES,
            get_columns(), 
            null, null, null, null,
            Constants.KEY_ID + " DESC");
      }else{
        cursor = db.query(Constants.TABLE_NOTES,
            get_columns(), 
            Constants.TABLE_NOTES__IS_REMOVED + " = ? ", new String[]{"0"}, 
            null, null,
            Constants.KEY_ID + " DESC");
      }

      while (cursor.moveToNext()) {
        System.out.println("moveToNext");
        Note note = build_note_by_cursor(cursor);
        notes.add(note);
      }

      return notes;

    } catch (Exception e) {
      Log.e("NoteDBHelper", "all", e);
      throw e;
    } finally {
      db.close();
    }

  }

  private static Note build_note_by_cursor(Cursor cursor) {
    int id = cursor.getInt(0);
    String uuid = cursor.getString(1);
    String content = cursor.getString(2);
    String kind = cursor.getString(3);
    int is_removed = cursor.getInt(4);
    long created_at = cursor.getLong(5);
    long updated_at = cursor.getLong(6);
    return new Note(id, uuid, content,kind, is_removed, created_at,
        updated_at);
  }
  
  final public static boolean create_text_note(String note_content){
    String uuid = create_item(note_content,Kind.TEXT);
    if(uuid != null){
      return true;
    }
    return false;
  }
  
  public static boolean create_image_note(String origin_image_path) {
    String uuid = create_item("",Kind.IMAGE);
    File note_image_file = note_image_file(uuid);
    
    try {
      FileUtils.copyFile(new File(origin_image_path), note_image_file);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      destroy(uuid);
      return false;
    }
  }
  
  public static void create_item(String uuid, String content, String kind,
      Integer is_removed, long updated_at) {
    SQLiteDatabase db = get_write_db();
    
    try {
      // 保存数据库信息
      ContentValues values = new ContentValues();
      values.put(Constants.TABLE_NOTES__UUID, uuid);
      values.put(Constants.TABLE_NOTES__CONTENT, content);
      values.put(Constants.TABLE_NOTES__KIND, kind);
      values.put(Constants.TABLE_NOTES__IS_REMOVED, is_removed);
      values.put(Constants.TABLE_NOTES__UPDATED_AT, updated_at);
      values.put(Constants.TABLE_NOTES__CREATED_AT, updated_at);
      db.insert(Constants.TABLE_NOTES, null, values);
    } catch (Exception e) {
      Log.e("NoteDBHelper", "create_item", e);
    } finally {
      db.close();
    }
  }
  
  final private static String create_item(String note_content,String kind){
    long current_seconds = System.currentTimeMillis()/1000;
    String uuid = UUID.randomUUID().toString();
    create_item(uuid,note_content, kind,0, current_seconds);
    return uuid;
  }
  
  final public static boolean update_time(String uuid, long seconds){
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_NOTES__UPDATED_AT, seconds);
    
    return update_columns(uuid,values);
  }
  
  public static boolean update(String uuid, String content, Integer is_removed,
      long updated_at) {
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_NOTES__CONTENT, content);
    values.put(Constants.TABLE_NOTES__IS_REMOVED, is_removed);
    values.put(Constants.TABLE_NOTES__UPDATED_AT, updated_at);
    
    return update_columns(uuid,values);
  }
  

  final public static boolean update(String uuid,String note_content) {
    // 保存数据库信息
    long current_seconds = System.currentTimeMillis()/1000;
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_NOTES__CONTENT, note_content);
    values.put(Constants.TABLE_NOTES__UPDATED_AT, current_seconds);
      
    return update_columns(uuid,values);
  }
  
  final public static boolean update_columns(String uuid,ContentValues values){
    SQLiteDatabase db = get_write_db();

    try {
      // 保存数据库信息
      int row_count = db.update(Constants.TABLE_NOTES, 
          values, Constants.TABLE_NOTES__UUID + " = ? ", 
          new String[]{uuid});
      
      if(row_count != 1){return false;}
      return true;
    } catch (Exception e) {
      Log.e("NoteDBHelper", "update", e);
      return false;
    } finally {
      db.close();
    }
  }
  
  
  final public static Note find(String uuid){
    SQLiteDatabase db = get_read_db();
    
    try {
      Cursor cursor = db.query(
        Constants.TABLE_NOTES, get_columns(), 
        Constants.TABLE_NOTES__UUID + " = " + "'" + uuid + "'", 
        null, null, null, null
      );
  
      boolean has_result = cursor.moveToFirst();
  
      if (has_result) {
        return build_note_by_cursor(cursor);
      } else {
        return Note.NIL_NOTE;
      }
      
    } catch (Exception e) {
      Log.e("NoteDBHelper", "find", e);
      return Note.NIL_NOTE;
    } finally {
      db.close();
    }
  }

  public static boolean destroy(String uuid) {
    // 保存数据库信息
    long current_seconds = System.currentTimeMillis()/1000;
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_NOTES__IS_REMOVED, 1);
    values.put(Constants.TABLE_NOTES__UPDATED_AT, current_seconds);
      
    return update_columns(uuid,values);
  }
  
  public static String[] get_columns(){
    return new String[] { 
        Constants.KEY_ID, Constants.TABLE_NOTES__UUID,
        Constants.TABLE_NOTES__CONTENT,
        Constants.TABLE_NOTES__KIND,
        Constants.TABLE_NOTES__IS_REMOVED,
        Constants.TABLE_NOTES__CREATED_AT,
        Constants.TABLE_NOTES__UPDATED_AT };
  }
  
  private static File note_dir(String uuid){
    return new File(FileDirs.TEAMKN_NOTES_DIR, uuid);
  }
  
  public static File note_image_file(String uuid) {
    File dir = note_dir(uuid);
    if(!dir.exists()){
      dir.mkdir();
    }
    return new File(dir,"image");
  }

}
