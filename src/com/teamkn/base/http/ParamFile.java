package com.teamkn.base.http;

import java.io.File;

import org.apache.http.entity.mime.content.FileBody;

// 用于包装文件以及文件MIME类型的小类
public class ParamFile{
	public String param_name;
	public String file_path;
	public String mime_type;
	
	public ParamFile(String param_name, String file_path, String mime_type){
		this.param_name = param_name;
		this.file_path = file_path;
		this.mime_type = mime_type;
	}
	
	public FileBody get_filebody(){
		File file = new File(file_path);
		return new FileBody(file, mime_type);
	}
}