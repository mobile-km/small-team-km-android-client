package com.mindpin.model.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.mindpin.model.Note;
import com.mindpin.model.base.BaseModelDBHelper;
import com.mindpin.model.base.Constants;

public class NoteDBHelper extends BaseModelDBHelper {
  final public static List<Note> all() throws Exception {
    SQLiteDatabase db = get_read_db();
    List<Note> notes = new ArrayList<Note>();
    try {
      Cursor cursor = db.query(Constants.TABLE_NOTES,
          new String[] { Constants.KEY_ID, Constants.TABLE_NOTES__UUID,
              Constants.TABLE_NOTES__CONTENT,
              Constants.TABLE_NOTES__IS_REMOVED,
              Constants.TABLE_NOTES__CREATED_AT,
              Constants.TABLE_NOTES__UPDATED_AT }, null, null, null, null,
          Constants.KEY_ID + " ASC");

      while (cursor.moveToNext()) {
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
    int is_removed = cursor.getInt(3);
    long created_at = cursor.getLong(4);
    long updated_at = cursor.getLong(5);
    return new Note(id, uuid, content, is_removed, created_at,
        updated_at);
  }

  final public static boolean save(final Note note) {
    long current_timemillis = System.currentTimeMillis();
    Log.d("mindpin", note.is_nil() + "");
    if (note.is_nil()){
      return false;
    }
    
    SQLiteDatabase db = get_write_db();

    try {
      // 保存数据库信息
      ContentValues values = new ContentValues();
      values.put(Constants.TABLE_NOTES__UUID, note.uuid);
      values.put(Constants.TABLE_NOTES__CONTENT, note.content);
      values.put(Constants.TABLE_NOTES__IS_REMOVED, 0);
      values.put(Constants.TABLE_NOTES__UPDATED_AT, current_timemillis);
      
      
      Note o_note = find(note.uuid);

      if (o_note.is_nil()) {
        values.put(Constants.TABLE_NOTES__CREATED_AT, current_timemillis);
        db.insert(Constants.TABLE_NOTES, null, values);
      } else {
        db.update(Constants.TABLE_NOTES, values, Constants.TABLE_NOTES__UUID
            + " = " + note.uuid, null);
      }

      return true;
    } catch (Exception e) {
      Log.e("NoteDBHelper", "save", e);
      return false;
    } finally {
      db.close();
    }
  }
  
  
  final public static Note find(String uuid){
    SQLiteDatabase db = get_read_db();
    
    try {
      Cursor cursor = db.query(
        Constants.TABLE_NOTES, new String[] { 
            Constants.KEY_ID, Constants.TABLE_NOTES__UUID,
            Constants.TABLE_NOTES__CONTENT,
            Constants.TABLE_NOTES__IS_REMOVED,
            Constants.TABLE_NOTES__CREATED_AT,
            Constants.TABLE_NOTES__UPDATED_AT }, 
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

}
