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
 @Override
	public String toString() {
		return id + " : " + user_id + " : "
				+ server_created_time + " : " + user_name + " : " + user_avatar + " : " + server_updated_time;
	}
public String getUser_name() {
	return user_name;
}

public void setUser_name(String user_name) {
	this.user_name = user_name;
}

public byte[] getUser_avatar() {
	return user_avatar;
}

public void setUser_avatar(byte[] user_avatar) {
	this.user_avatar = user_avatar;
}
  
  
}
