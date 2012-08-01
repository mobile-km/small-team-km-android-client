package com.teamkn.model;

import com.teamkn.Logic.HttpApi;
import com.teamkn.model.base.BaseModel;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class AccountUser extends BaseModel {
    public String cookies;
    public String info;

    public int user_id;
    public String name;
    public String sign;
    public String avatar_url;
    public byte[] avatar;

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
    }
    
    public AccountUser(String cookies, String info,byte[] avatar) throws JSONException{
      this.cookies = cookies;
      this.info = info;

      JSONObject json = new JSONObject(info);

      this.user_id = json.getInt("id");
      this.name = json.getString("name");
      this.sign = json.getString("sign");
      this.avatar_url = json.getString("avatar_url");
      this.avatar = avatar;
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
    
    
    
}
