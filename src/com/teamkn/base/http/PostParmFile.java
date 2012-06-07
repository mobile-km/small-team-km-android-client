package com.teamkn.base.http;

import java.io.File;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;

public class PostParmFile implements PostParam {
  public String param_name;
  public String file_path;
  public String mime_type;

  public PostParmFile(String param_name, String file_path, String mime_type) {
    this.param_name = param_name;
    this.file_path = file_path;
    this.mime_type = mime_type;
  }
  
  @Override
  public ContentBody get_body() {
    File file = new File(file_path);
    return new FileBody(file, mime_type);
  }

  @Override
  public String get_name() {
    return param_name;
  }

}
