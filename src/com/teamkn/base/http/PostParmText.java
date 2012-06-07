package com.teamkn.base.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;

public class PostParmText implements PostParam {
  private String param_name;
  private String value;

  public PostParmText(String param_name, String value) {
    this.param_name = param_name;
    this.value = value;
  }
  
  @Override
  public String get_name(){
    return param_name;
  }
  
  @Override
  public ContentBody get_body() {
    try {
      return new StringBody(value);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }
}
