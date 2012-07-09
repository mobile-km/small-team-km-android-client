package com.teamkn.model;

import com.teamkn.model.base.BaseModel;

public class User extends BaseModel {
  final public static User NIL_USER = new User();
  public int id;
  public int user_id;
  public long server_created_time;
  public String user_name;
  public byte[] user_avatar;
  public long server_updated_time;

  private User() {
      set_nil();
  }

  public User(int id, int user_id, String user_name, byte[] user_avatar,
      long server_created_time, long server_updated_time) {
    this.id = id;
    this.user_id = user_id;
    this.user_name = user_name;
    this.user_avatar = user_avatar;
    this.server_created_time = server_created_time;
    this.server_updated_time = server_updated_time;
  }
}
