package com.teamkn.model;

import com.teamkn.model.base.BaseModel;

public class Contact extends BaseModel{

  public int id;
  public int user_id;
  public int contact_user_id;
  public String contact_user_name;
  public byte[] contact_user_avatar;
  public String message;
  public String status;
  public long server_created_time;
  public long server_updated_time;
  
  final public static Contact NIL_CONTACT = new Contact();
  private Contact() {
      set_nil();
  }

  public Contact(int id, int user_id, int contact_user_id,
      String contact_user_name, byte[] contact_user_avatar, String message, String status,
      long server_created_time, long server_updated_time) {
    this.id = id;
    this.user_id = user_id;
    this.contact_user_id = contact_user_id;
    this.contact_user_name = contact_user_name;
    this.contact_user_avatar = contact_user_avatar;
    this.message = message;
    this.status = status;
    this.server_created_time = server_created_time;
    this.server_updated_time = server_updated_time;
  }

}
