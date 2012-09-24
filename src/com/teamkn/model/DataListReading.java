package com.teamkn.model;

import com.teamkn.model.base.BaseModel;

public class DataListReading extends BaseModel  {
	public int id;
	public int data_list_id;
	public int user_id;
    
	public static DataListReading DATALISTREADING = new DataListReading();
	public DataListReading() {
		 set_nil();
	}
	public DataListReading(int id, int data_list_id, int user_id) {
		super();
		this.id = id;
		this.data_list_id = data_list_id;
		this.user_id = user_id;
	}
}
