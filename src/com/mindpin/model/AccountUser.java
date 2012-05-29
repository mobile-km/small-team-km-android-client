package com.mindpin.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.mindpin.model.base.BaseModel;

public class AccountUser extends BaseModel {	
	public String cookies;
	public String info;
	
	public int user_id;
	public String name;
	public String sign;
	public boolean v2_activate;
	public String avatar_url;
	
	// 用一个特殊的user实例来表示一个空user
	// 用 is_nil() 方法来判断是否空user
	// 不可用 null == user 来判断
	final public static AccountUser NIL_ACCOUNT_USER = new AccountUser();
	private AccountUser(){
		set_nil();
	}
	
	public AccountUser(String cookies, String info) throws JSONException{
		this.cookies = cookies;
		this.info = info;
		
		JSONObject json = new JSONObject(info);
		
		this.user_id     = json.getInt("id");
		this.name        = json.getString("name");
		this.sign        = json.getString("sign");
		this.v2_activate = json.getBoolean("v2_activate");
		this.avatar_url  = json.getString("avatar_url");
	}
}
