package com.teamkn.base.http;

import android.util.Log;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.teamkn.base.utils.BaseUtils;
import java.util.List;

public class CookieHelper {
  
  public static String parse_cookie_store_to_string(CookieStore cookieStore) {
    List<Cookie> cookies = cookieStore.getCookies();
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

  public static CookieStore parse_string_to_cookie_store(String cookies_string){
    BasicCookieStore cookie_store = new BasicCookieStore();
    try {
      if (!BaseUtils.is_str_blank(cookies_string)) {
        JSONArray json_arr = new JSONArray(cookies_string);
        for (int i = 0; i < json_arr.length(); i++) {
          JSONObject json = (JSONObject) json_arr.get(i);
          String name = (String) json.get("name");
          String value = (String) json.get("value");
          String domain = (String) json.get("domain");
          String path = (String) json.get("path");
          BasicClientCookie cookie = new BasicClientCookie(name, value);
          cookie.setDomain(domain);
          cookie.setPath(path);
          cookie_store.addCookie(cookie);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return cookie_store;
  }

}
