package com.teamkn.model;

import com.teamkn.model.base.BaseModel;
import com.teamkn.model.database.UserDBHelper;

public class ChatNode extends BaseModel {
  final public static ChatNode NIL_CHAT_NODE = new ChatNode();
  public long server_created_time;
  public int server_chat_node_id;
  public int client_user_id;
  public User sender;
  public String kind;
  public String content;
  public int chat_id;
  public int id;

  private ChatNode() {
      set_nil();
  }

  public ChatNode(int id, int chat_id, String content, String kind,
      int client_user_id, int server_chat_node_id, long server_created_time) {
    
    this.id = id;
    this.chat_id = chat_id;
    this.content = content;
    this.kind = kind;
    this.client_user_id = client_user_id;
    this.sender = UserDBHelper.find(client_user_id);
    this.server_chat_node_id = server_chat_node_id;
    this.server_created_time = server_created_time;
  }
}
