package com.teamkn.activity.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

public class AboutActivity extends TeamknBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_about);
	}
	
	public void open_teamkn_website(View view){
		Uri uri = Uri.parse("http://www.teamkn.com");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
