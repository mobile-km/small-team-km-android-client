package com.teamkn.model;

import com.teamkn.model.base.BaseModel;
import com.teamkn.model.database.UserDBHelper;

public class ChatNode extends BaseModel {
  final public static ChatNode NIL_CHAT_NODE = new ChatNode();
  public long server_created_time;
  public int server_chat_node_id;
  public int sender_id;
  public User sender;
  public String kind;
  public String content;
  public long chat_id;
  public long id;

  private ChatNode() {
      set_nil();
  }

  public ChatNode(long id, long chat_id, String content, String kind,
      int sender_id, int server_chat_node_id, long server_created_time) {
    
    this.id = id;
    this.chat_id = chat_id;
    this.content = content;
    this.kind = kind;
    this.sender_id = sender_id;
    this.sender = UserDBHelper.find(sender_id);
    this.server_chat_node_id = server_chat_node_id;
    this.server_created_time = server_created_time;
  }
}
