package com.teamkn.model;

import java.util.List;

import com.teamkn.model.base.BaseModel;

public class Chat extends BaseModel {
  public long id;
  public int server_chat_id;
  public long server_created_time;
  public long server_updated_time;
  public List<Integer> member_ids;
  
  final public static Chat NIL_CHAT = new Chat();

  private Chat() {
      set_nil();
  }

  public Chat(long id, int server_chat_id, long server_created_time,
      long server_updated_time, List<Integer> member_ids) {
    this.id = id;
    this.server_chat_id = server_chat_id;
    this.server_created_time = server_created_time;
    this.server_updated_time = server_updated_time;
    this.member_ids = member_ids;
  }
  
  public boolean is_syned(){
    return this.server_chat_id != 0;
  }
  
}
