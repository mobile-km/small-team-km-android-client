package com.mindpin.activity.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.mindpin.R;
import com.mindpin.base.activity.MindpinBaseActivity;

public class AboutActivity extends MindpinBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_about);
		
		WebView wv = (WebView) findViewById(R.id.webView1);
		wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		String html = "<html><body style='font-size:0;line-height:0;padding:0;margin:0;'><img style='padding:0;margin:0;width:100%;' src='http://mindmap-image-cache.mindpin.com/photos/images/435/w500/c775096f-bb14-4069-badb-3e271904005d.png?1321460428'/></body></html>";
		wv.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
	}
	
	public void open_mindpin_website(View view){
		Uri uri = Uri.parse("http://www.mindpin.com");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
