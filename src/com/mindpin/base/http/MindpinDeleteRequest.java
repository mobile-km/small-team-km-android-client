package com.mindpin.base.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;

import com.mindpin.Logic.HttpApi;

public abstract class MindpinDeleteRequest<TResult> extends MindpinHttpRequest<TResult> {
	public MindpinDeleteRequest(final String request_path, final NameValuePair... nv_pairs) {
		String request_url = HttpApi.SITE + request_path + build_params_string(nv_pairs);
		this.http_uri_request = new HttpDelete(request_url);
		this.http_uri_request.setHeader("User-Agent", "android");
	}
}
