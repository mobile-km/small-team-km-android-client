package com.teamkn.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.common.base.Joiner;
import com.teamkn.model.Note;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;
import com.teamkn.service.IndexService;
import com.teamkn.service.IndexService.IndexHandler.action;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteDBHelper extends BaseModelDBHelper {
    public class Kind {
        public static final String TEXT = "TEXT";
        public static final String IMAGE = "IMAGE";
    }

    public static int unsyn_count() {
        SQLiteDatabase db = get_read_db();

        Cursor cursor;
        try {
            cursor = db.query(Constants.TABLE_NOTES,
                    new String[]{Constants.KEY_ID},
                    Constants.TABLE_NOTES__SYNED_SERVER_TIME + " = ? ",
                    new String[]{"0"}, null, null, null);

            return cursor.getCount();
        } catch (Exception e) {
            Log.e("NoteDBHelper", "unsyn_count", e);
            return 0;
        } finally {
            db.close();
        }
    }
    
    public static List<Note> client_changed_notes() throws Exception{
      SQLiteDatabase db = get_read_db();
      List<Note> notes = new ArrayList<Note>();
      Cursor cursor;
      try {
        cursor = db.query(Constants.TABLE_NOTES,
                get_columns(),
                Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT + " = ?", new String[]{"1"},
                null, null,
                Constants.KEY_ID + " DESC");

          while (cursor.moveToNext()) {
              System.out.println("moveToNext");
              Note note = build_note_by_cursor(cursor);
              notes.add(note);
          }

          return notes;
      } catch (Exception e) {
          Log.e("NoteDBHelper", "client_changed_notes", e);
          throw e;
      } finally {
          db.close();
      }

    }

    public static List<Note> all(boolean has_removed) throws Exception {
        SQLiteDatabase db = get_read_db();
        List<Note> notes = new ArrayList<Note>();
        Cursor cursor;
        try {
            if (has_removed) {
                cursor = db.query(Constants.TABLE_NOTES,
                        get_columns(),
                        null, null, null, null,
                        Constants.KEY_ID + " DESC");
            } else {
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

    public static boolean create_text_note(String note_content) {
        String uuid = create_item_by_kind(note_content, Kind.TEXT);

        IndexService.obtain_index_request(find(uuid),
                                          action.ADD)
                    .sendToTarget();

        return uuid != null;
    }

    public static boolean create_image_note(String origin_image_path) {
        String uuid = create_item_by_kind("", Kind.IMAGE);
        File note_image_file = Note.note_image_file(uuid);

        try {
            FileUtils.copyFile(new File(origin_image_path), note_image_file);
            Note.note_thumb_image_file(uuid);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            destroy(uuid);
            return false;
        }
    }
    
    public static void pull(String uuid, String content, String kind, int is_removed, long updated_at){
      Note note = find(uuid);
      if(Note.NIL_NOTE == note){
        create_from_pull(uuid, content, kind, is_removed, updated_at);
      }else{
        update_from_pull(uuid, content, is_removed, updated_at);
      }
    }

    private static void create_from_pull(String uuid, String content, String kind,
                                                        Integer is_removed, long updated_at) {
        // 保存数据库信息
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_NOTES__UUID, uuid);
        values.put(Constants.TABLE_NOTES__CONTENT, content);
        values.put(Constants.TABLE_NOTES__KIND, kind);
        values.put(Constants.TABLE_NOTES__IS_REMOVED, is_removed);
        values.put(Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT,0);
        values.put(Constants.TABLE_NOTES__SYNED_SERVER_TIME,updated_at);
        
        create_item(values);

        try {
            IndexService.obtain_index_request(find(uuid),
                                              action.ADD)
                        .sendToTarget();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean after_push(String uuid, long seconds) {
        ContentValues values = new ContentValues();
        
        values.put(Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT, 0);
        values.put(Constants.TABLE_NOTES__SYNED_SERVER_TIME, seconds);

        return update_columns(uuid, values);
    }

    private static boolean update_from_pull(String uuid, String content, Integer is_removed,
                                           long updated_at) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_NOTES__CONTENT, content);
        values.put(Constants.TABLE_NOTES__IS_REMOVED, is_removed);
        values.put(Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT, 0);
        values.put(Constants.TABLE_NOTES__SYNED_SERVER_TIME, updated_at);
        
        return update_columns(uuid, values);
    }

    public static int total_count() {
        SQLiteDatabase db = get_read_db();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + Constants.TABLE_NOTES,
                null);
        cursor.moveToPosition(0);

        int count = cursor.getInt(0);

        cursor.close();
        db.close();
        return count;
    }

    public static boolean update(String uuid, String note_content) {
        // 保存数据库信息
        long current_seconds = System.currentTimeMillis() / 1000;

        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_NOTES__CONTENT, note_content);
        values.put(Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT,1);
        values.put(Constants.TABLE_NOTES__CLIENT_UPDATED_TIME, current_seconds);


        return update_columns(uuid, values);
    }

    final public static Note find(String uuid) {
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

    final public static List<Note> find(List<String> uuids) {
        SQLiteDatabase db = get_read_db();

        String uuids_list = Joiner.on(",").join(uuids);

        Cursor cursor = db.query(Constants.TABLE_NOTES,
                                 get_columns(),
                                 "uuid IN (" + uuids_list + ")",
                                 null, null, null, null);

        ArrayList<Note> notes = new ArrayList<Note>();

        if (cursor.moveToFirst()) {
            for (long i = 0; i < cursor.getCount(); i++) {
                notes.add(build_note_by_cursor(cursor));
                cursor.moveToNext();
            }

            return notes;
        }

        return notes;
    }

    public static boolean destroy(String uuid) {
        // 保存数据库信息
        long current_seconds = System.currentTimeMillis() / 1000;

        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_NOTES__IS_REMOVED, 1);
        values.put(Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT,1);
        values.put(Constants.TABLE_NOTES__CLIENT_UPDATED_TIME, current_seconds);

        return update_columns(uuid, values);
    }

    private static String create_item_by_kind(String note_content, String kind) {
        long current_seconds = System.currentTimeMillis() / 1000;
        String uuid = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_NOTES__UUID, uuid);
        values.put(Constants.TABLE_NOTES__CONTENT, note_content);
        values.put(Constants.TABLE_NOTES__KIND, kind);
        values.put(Constants.TABLE_NOTES__IS_REMOVED, 0);
        values.put(Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT,1);
        values.put(Constants.TABLE_NOTES__CLIENT_CREATED_TIME,current_seconds);
        values.put(Constants.TABLE_NOTES__CLIENT_UPDATED_TIME,current_seconds);
        values.put(Constants.TABLE_NOTES__SYNED_SERVER_TIME,0);
        
        create_item(values);

        return uuid;
    }

    private static void create_item(ContentValues values) {
        SQLiteDatabase db = get_write_db();

        try {
            db.insert(Constants.TABLE_NOTES, null, values);
        } catch (Exception e) {
            Log.e("NoteDBHelper", "create_item", e);
        } finally {
            db.close();
        }
    }

    private static boolean update_columns(String uuid, ContentValues values) {
        SQLiteDatabase db = get_write_db();

        try {
            // 保存数据库信息
            int row_count = db.update(Constants.TABLE_NOTES,
                    values, Constants.TABLE_NOTES__UUID + " = ? ",
                    new String[]{uuid});

            Note note = find(uuid);

            if (note.is_removed == 1) {
                IndexService.obtain_index_request(note,
                                                  action.DELETE)
                            .sendToTarget();
            } else {
                IndexService.obtain_index_request(note,
                                                  action.UPDATE)
                            .sendToTarget();
            }

            return row_count == 1;
        } catch (Exception e) {
            Log.e("NoteDBHelper", "update", e);
            return false;
        } finally {
            db.close();
        }
    }

    private static String[] get_columns() {
        return new String[]{
                Constants.KEY_ID, Constants.TABLE_NOTES__UUID,
                Constants.TABLE_NOTES__CONTENT,
                Constants.TABLE_NOTES__KIND,
                Constants.TABLE_NOTES__IS_REMOVED,
                Constants.TABLE_NOTES__IS_CHANGED_BY_CLIENT,
                Constants.TABLE_NOTES__CLIENT_CREATED_TIME,
                Constants.TABLE_NOTES__CLIENT_UPDATED_TIME,
                Constants.TABLE_NOTES__SYNED_SERVER_TIME
          };
    }

    private static Note build_note_by_cursor(Cursor cursor) {
        int id = cursor.getInt(0);
        String uuid = cursor.getString(1);
        String content = cursor.getString(2);
        String kind = cursor.getString(3);
        int is_removed = cursor.getInt(4);
        int is_changed_by_client = cursor.getInt(5);
        long client_created_time = cursor.getLong(6);
        long client_updated_time = cursor.getLong(7);
        long syned_server_time = cursor.getLong(8);
        
        return new Note(id, uuid, content, kind, is_removed, is_changed_by_client, client_created_time,
            client_updated_time, syned_server_time);
    }
    
    public static int getCount()  throws Exception{
        SQLiteDatabase db = get_read_db();
        String sql = "select count(*) from '" + Constants.TABLE_NOTES + "'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int length = c.getInt(0);
        c.close();
        db.close();
        return length;
    }
    
    public static List<Note> getAllItems(int firstResult, int maxResult) throws Exception {
        SQLiteDatabase db = get_read_db();
        String sql = "select * from '" + Constants.TABLE_NOTES + "' limit ?,?";
        List<Note> notes = new ArrayList<Note>();
        Cursor cursor;
        
        try {
                cursor = db.rawQuery(sql, new String[] { String.valueOf(firstResult),
                        String.valueOf(maxResult) });
               
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                }
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                	Note note = build_note_by_cursor(cursor);
	                notes.add(note);
                }
                cursor.close();
                db.close();     
                
            return notes;

        } catch (Exception e) {
            Log.e("NoteDBHelper", "all", e);
            throw e;
        } finally {
            db.close();
        }

    }

}
