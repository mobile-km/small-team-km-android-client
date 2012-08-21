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

	View view_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		
		
        setContentView(R.layout.horz_scroll_with_image_menu);
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout_loading);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        view_show = inflater.inflate(R.layout.base_about, null);
        layout.addView(view_show);

    }

    public void open_teamkn_website(View view) {
        Uri uri = Uri.parse("http://www.teamkn.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
