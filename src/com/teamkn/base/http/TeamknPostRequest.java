package com.teamkn.base.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;

import com.teamkn.Logic.HttpApi;

public abstract class TeamknPostRequest<TResult> extends TeamknHttpRequest<TResult> {
	// 一般文本参数的请求
	public TeamknPostRequest(final String request_path, final NameValuePair...nv_pairs) throws UnsupportedEncodingException{
		HttpEntity entity = build_entity(nv_pairs);
		this.http_uri_request = build_http_post(entity, request_path);
	}
	
	// 上传文件之类的请求
	public TeamknPostRequest(final String request_path, final PostParam...param){
		HttpEntity entity = build_entity(param);
		this.http_uri_request = build_http_post(entity, request_path);
	}
	
	private HttpEntity build_entity(PostParam...param){
		MultipartEntity entity = new MultipartEntity();
		for(PostParam param_file : param){
			entity.addPart(param_file.get_name(), param_file.get_body());
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