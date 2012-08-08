package com.teamkn.model;

import android.graphics.Bitmap;

import com.teamkn.Logic.CompressPhoto;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.base.BaseModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;

public class Note extends BaseModel {
    public int id;
    public String uuid;
    public String content;
    public String kind;
    public int is_removed;
    public int is_changed_by_client;
    public long client_created_time;
    public long client_updated_time;
    public long syned_server_time;

    final public static Note NIL_NOTE = new Note();

    private Note() {
        set_nil();
    }

    public Note(int id, String uuid, String content, String kind,
        int is_removed, int is_changed_by_client, long client_created_time,
        long client_updated_time, long syned_server_time) {
      this.id = id;
      this.uuid = uuid;
      this.content = content;
      this.kind = kind;
      this.is_removed = is_removed;
      this.is_changed_by_client = is_changed_by_client;
      this.client_created_time = client_created_time;
      this.client_updated_time = client_updated_time;
      this.syned_server_time = syned_server_time;
    }

    public static File note_image_file(String uuid) {
        File dir = new File(FileDirs.TEAMKN_NOTES_DIR, uuid);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, "image");
    }
      
    public static File note_thumb_image_file(String uuid) {
      File note_image_file = note_image_file(uuid);
      if(!note_image_file.exists()){ return null; }
      
      String thumb_image_file_path = BaseUtils.file_path_join(FileDirs.TEAMKN_NOTES_DIR.getPath(),uuid,"thumb_image");
      File thumb_image = new File(thumb_image_file_path);
      
      if(!thumb_image.exists()){
        Bitmap bitmap = CompressPhoto.get_thumb_bitmap_form_file(note_image_file.getPath());
        try {
          FileOutputStream out = new FileOutputStream(thumb_image.getPath());
          bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
      
      return thumb_image;
    }

}
