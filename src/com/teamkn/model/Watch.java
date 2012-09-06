package com.teamkn.model;
import com.teamkn.model.base.BaseModel;
public class Watch extends BaseModel {
	  public int id;
	  public int user_id;
	  public int data_list_id;
	  
	public Watch() {
		super();
	}

	public Watch(int id, int user_id, int data_list_id) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.data_list_id = data_list_id;
	}
	
}