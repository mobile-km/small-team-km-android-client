package com.teamkn.model;

import com.teamkn.model.base.BaseModel;

public class Note extends BaseModel {
  public int id;
  public String uuid;
  public String content;
  public int is_removed;
  public long created_at;
  public long updated_at;
  
  final public static Note NIL_NOTE = new Note();
  private Note(){
    set_nil();
  }

  public Note(int id, String uuid, String content, int is_removed,
      long created_at, long updated_at) {
    this.id = id;
    this.uuid = uuid;
    this.content = content;
    this.is_removed = is_removed;
    this.created_at = created_at;
    this.updated_at = updated_at;
  }
  
}
