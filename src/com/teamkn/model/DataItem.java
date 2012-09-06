package com.teamkn.model;
import com.teamkn.model.base.BaseModel;
public class DataItem extends BaseModel {
	  public int id;	 
	  public String title;
	  public String content;
	  public String url;
	  public String kind;
	  public int data_list_id;
	  public int position;	  
	public DataItem() {
		super();
	}
	public DataItem(int id, String title, String content, String url,
			String kind, int data_list_id, int position) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.url = url;
		this.kind = kind;
		this.data_list_id = data_list_id;
		this.position = position;
	}
}