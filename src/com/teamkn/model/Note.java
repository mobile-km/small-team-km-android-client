package com.teamkn.model;

import java.io.File;

import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.base.BaseModel;

public class Note extends BaseModel {
  public int id;
  public String uuid;
  public String content;
  public String kind;
  public int is_removed;
  public long created_at;
  public long updated_at;
  
  final public static Note NIL_NOTE = new Note();
  private Note(){
    set_nil();
  }

  public Note(int id, String uuid, String content,String kind, int is_removed,
      long created_at, long updated_at) {
    this.id = id;
    this.uuid = uuid;
    this.content = content;
    this.kind = kind;
    this.is_removed = is_removed;
    this.created_at = created_at;
    this.updated_at = updated_at;
  }
  
  public static File note_image_file(String uuid) {
    File dir = new File(FileDirs.TEAMKN_NOTES_DIR, uuid);
    if(!dir.exists()){
      dir.mkdir();
    }
    return new File(dir,"image");
  }
  
}
