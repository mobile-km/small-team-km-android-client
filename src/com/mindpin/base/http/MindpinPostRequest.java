package com.mindpin.base.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;

import com.mindpin.Logic.HttpApi;

public abstract class MindpinPostRequest<TResult> extends MindpinHttpRequest<TResult> {
	// 一般文本参数的请求
	public MindpinPostRequest(final String request_path, final NameValuePair...nv_pairs) throws UnsupportedEncodingException{
		HttpEntity entity = build_entity(nv_pairs);
		this.http_uri_request = build_http_post(entity, request_path);
	}
	
	// 上传文件之类的请求
	public MindpinPostRequest(final String request_path, final ParamFile...param_files){
		HttpEntity entity = build_entity(param_files);
		this.http_uri_request = build_http_post(entity, request_path);
	}
	
	private HttpEntity build_entity(ParamFile...param_files){
		MultipartEntity entity = new MultipartEntity();
		for(ParamFile param_file : param_files){
			entity.addPart(param_file.param_name, param_file.get_filebody());
		}
		return entity;
	}
	
	private HttpPost build_http_post(HttpEntity entity, String request_path){
		HttpPost http_post = new HttpPost(HttpApi.SITE + request_path);
		http_post.setHeader("User-Agent", "android");
		http_post.setEntity(entity);
		return http_post;
	}
}