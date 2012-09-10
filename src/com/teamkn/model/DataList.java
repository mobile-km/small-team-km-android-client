package com.teamkn.model;
import com.teamkn.model.base.BaseModel;

public class DataList extends BaseModel {
	  public int id = -1;
	  public int user_id;
	  public String title;
	  public String kind;
	  public String public_boolean;
	  public int server_data_list_id=-1;
	  
	  
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getPublic_boolean() {
		return public_boolean;
	}
	public void setPublic_boolean(String public_boolean) {
		this.public_boolean = public_boolean;
	}
	public int getServer_data_list_id() {
		return server_data_list_id;
	}
	public void setServer_data_list_id(int server_data_list_id) {
		this.server_data_list_id = server_data_list_id;
	}
	public static DataList getNIL_DATA_LIST() {
		return NIL_DATA_LIST;
	}
	public static void setNIL_DATA_LIST(DataList nIL_DATA_LIST) {
		NIL_DATA_LIST = nIL_DATA_LIST;
	}
	public static DataList NIL_DATA_LIST = new DataList();
	public DataList() {
		super();
	}
	public DataList(int id, int user_id, String title, String kind,
			String public_boolean, int server_data_list_id) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.title = title;
		this.kind = kind;
		this.public_boolean = public_boolean;
		this.server_data_list_id = server_data_list_id;
	}
	public DataList(int user_id, String title, String kind,
			String public_boolean, int server_data_list_id) {
		super();
		this.user_id = user_id;
		this.title = title;
		this.kind = kind;
		this.public_boolean = public_boolean;
		this.server_data_list_id = server_data_list_id;
	}  
	@Override
	public String toString() {
		return id+" : "+user_id+" : "+title+" : "+kind+" : "+ public_boolean+" : "+server_data_list_id;
	}
}