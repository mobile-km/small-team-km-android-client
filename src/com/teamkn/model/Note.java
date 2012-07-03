package com.teamkn.model;

import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.base.BaseModel;

import java.io.File;

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

}
