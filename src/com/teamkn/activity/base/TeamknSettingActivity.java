package com.teamkn.activity.base;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

public class TeamknSettingActivity extends TeamknBaseActivity{
	 View setting;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	        setContentView(R.layout.horz_scroll_with_image_menu);
	        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout_loading);
	        
	        LayoutInflater inflater = LayoutInflater.from(this);
	        setting = inflater.inflate(R.layout.setting, null);
	        layout.addView(setting);
	 
	     
	     LinearLayout ll_setting = (LinearLayout)setting.findViewById(R.id.ll_setting);
	     ll_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setImageView();
			}
		});
    }
	private void setImageView() {
		new AlertDialog.Builder(this).setTitle("复选框")
		         .setMultiChoiceItems( new String[] { "原尺寸", "50%","25%" }, null, null)
			     .setNegativeButton("取消", null)
			     .show();
	}
	public void click_go_account_manage_activity(View view){
		 open_activity(AccountManagerActivity.class);
	}
	 public void click_go_about_teamkn_activity(View view){
		 open_activity(AboutActivity.class);
	}
}
