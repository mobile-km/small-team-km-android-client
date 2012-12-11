package com.teamkn.model;

import com.teamkn.Logic.HttpApi;
import com.teamkn.model.base.BaseModel;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class AccountUser extends BaseModel implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String cookies;
    public String info;

    public int user_id;
    public String name;
    public String sign;
    public String avatar_url;
    public byte[] avatar;
    
    public boolean is_show_tip = true; //是否显示 引导界面
    
    public boolean followed = false; //非当前登陆用户

    // 用一个特殊的user实例来表示一个空user
    // 用 is_nil() 方法来判断是否空user
    // 不可用 null == user 来判断
    final public static AccountUser NIL_ACCOUNT_USER = new AccountUser();

    private AccountUser() {
        set_nil();
    }

    public AccountUser(String cookies, String info) throws JSONException, IOException {
        this.cookies = cookies;
        this.info = info;
        JSONObject json = new JSONObject(info);
       
        this.user_id = json.getInt("id");
        this.name = json.getString("name");
        this.sign = json.getString("sign");
        this.avatar_url = json.getString("avatar_url");
        if(this.avatar_url != null && !this.avatar_url.equals("")){
          InputStream is = HttpApi.download_image(this.avatar_url);
          byte[] avatar = IOUtils.toByteArray(is);
          this.avatar = avatar;
        }
        this.is_show_tip = json.getBoolean("is_show_tip");
       
    }
    public AccountUser(String info ) throws JSONException, IOException {
        this.info = info;
        JSONObject get_json = new JSONObject(info);
        JSONObject json = get_json.getJSONObject("user");
        this.user_id = json.getInt("id");
        this.name = json.getString("name");
        this.sign = json.getString("sign");
        this.avatar_url = json.getString("avatar_url");
        if(this.avatar_url != null && !this.avatar_url.equals("")){
          InputStream is = HttpApi.download_image(this.avatar_url);
          byte[] avatar = IOUtils.toByteArray(is);
          this.avatar = avatar;
        }
        this.is_show_tip = json.getBoolean("is_show_tip");
        if(!get_json.isNull("followed")){
        	this.followed = get_json.getBoolean("followed");
        }
    }
    
    public AccountUser(String cookies, String info,byte[] avatar,boolean is_show_tip) throws JSONException{
      this.cookies = cookies;
      this.info = info;

      JSONObject json = new JSONObject(info);

      this.user_id = json.getInt("id");
      this.name = json.getString("name");
      this.sign = json.getString("sign");
      this.avatar_url = json.getString("avatar_url");
      this.avatar = avatar;
      this.is_show_tip = is_show_tip;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public boolean getIs_show_tip() {
		return is_show_tip;
	}

	public void setIs_show_tip(boolean is_show_tip) {
		this.is_show_tip = is_show_tip;
	}
    
	
    public boolean isFollowed() {
		return followed;
	}

	public void setFollowed(boolean followed) {
		this.followed = followed;
	}
	
	
	
	public AccountUser(int user_id, String name, String sign,
			String avatar_url, byte[] avatar, boolean is_show_tip,
			boolean followed) {
		super();
		this.user_id = user_id;
		this.name = name;
		this.sign = sign;
		this.avatar_url = avatar_url;
		this.avatar = avatar;
		this.is_show_tip = is_show_tip;
		this.followed = followed;
	}

	@Override
    public String toString() {
    	return (cookies+ " : " +info+
    			" : " +user_id+ " : " +name+ " : " +
    			sign+ " : " +avatar_url+ " : " +avatar+ " : " +is_show_tip);
    }
    
}
