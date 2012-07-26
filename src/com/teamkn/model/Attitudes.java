package com.teamkn.model;

import com.teamkn.model.base.BaseModel;

public class Attitudes extends BaseModel{
	public final static Attitudes ATTITUDES = new Attitudes();
//    integer :chat_node_id # 对话节点的 外键关联
//    integer :client_user_id # 创建者
//    string :kind # gasp  heart  sad  smile  wink
	public int chat_node_id;
	public int client_user_id;
	public String kind;
	
	public String is_syned = "false"; 
	
	public Attitudes() {
		set_nil();
	}
	public Attitudes(int chat_node_id, int client_user_id, String kind) {
		super();
		this.chat_node_id = chat_node_id;
		this.client_user_id = client_user_id;
		this.kind = kind;
	}
	public Attitudes(int chat_node_id, int client_user_id, String kind,
			String is_syned) {
		super();
		this.chat_node_id = chat_node_id;
		this.client_user_id = client_user_id;
		this.kind = kind;
		this.is_syned = is_syned;
	}
	
}
