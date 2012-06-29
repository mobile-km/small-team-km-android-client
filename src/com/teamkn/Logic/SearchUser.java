package com.teamkn.Logic;

public class SearchUser {
  public class ContactStatus{
    public static final String SELF = "SELF";
    public static final String APPLIED = "APPLIED";
    public static final String INVITED = "INVITED";
    public static final String BE_INVITED = "BE_INVITED";
    public static final String REFUSED = "REFUSED";
    public static final String BE_REFUSED = "BE_REFUSED";
    public static final String REMOVED = "REMOVED";
    public static final String BE_REMOVED = "BE_REMOVED";
    public static final String NOTHING = "NOTHING";
  }
  public int user_id;
  public String user_name;
  public String user_avator_url;
  public String contact_status;

  public SearchUser(int user_id, String user_name, String user_avatar_url,
      String contact_status) {
    this.user_id = user_id;
    this.user_name = user_name;
    this.user_avator_url = user_avatar_url;
    this.contact_status = contact_status;
  }
}
