package com.mindpin.base.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;

import com.mindpin.Logic.HttpApi;

public abstract class MindpinGetRequest<TResult> extends MindpinHttpRequest<TResult> {
	public MindpinGetRequest(final String request_path, final NameValuePair... nv_pairs) {
		String request_url = HttpApi.SITE + request_path + build_params_string(nv_pairs);
		this.http_uri_request = new HttpGet(request_url);
		this.http_uri_request.setHeader("User-Agent", "android");
	}
}
