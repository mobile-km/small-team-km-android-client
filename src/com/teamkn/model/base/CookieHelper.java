package com.teamkn.model.base;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CookieHelper {
	
	public static String parse_string(List<Cookie> cookies) {
		try {
			JSONArray json_arr = new JSONArray();
			for (Cookie cookie : cookies) {
				JSONObject json = new JSONObject();
				json.put("name", cookie.getName());
				json.put("value", cookie.getValue());
				json.put("domain", cookie.getDomain());
				json.put("path", cookie.getPath());
				json_arr.put(json);
			}
			return json_arr.toString();
		} catch (JSONException e) {
			Log.e("CookieHelper", "parse_string cookie json 解析出错", e);
			return "";
		}
	}
	
}
