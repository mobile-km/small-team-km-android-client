package com.teamkn.activity.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

public class AboutActivity extends TeamknBaseActivity{

	View base_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		
		
        setContentView(R.layout.horz_scroll_with_image_menu);
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout_loading);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        base_about = inflater.inflate(R.layout.base_about, null);
        layout.addView(base_about);

    }

    public void open_teamkn_website(View view) {
        Uri uri = Uri.parse("http://www.teamkn.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
