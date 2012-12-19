package com.teamkn.activity.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.activity.base.slidingmenu.TeamknSlidingMenuActivity;

public class TeamknSettingActivity extends TeamknSlidingMenuActivity {
	LayoutInflater inflater;
	public static MyHorizontalScrollView scrollView;
	public static View foot_view;  //底层  图层 隐形部分
    View show_view;  //显示的View
    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;
//	View setting;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	        setContentView(R.layout.horz_scroll_with_image_menu);
//	        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout_loading);
//	        
//	        LayoutInflater inflater = LayoutInflater.from(this);
//	        setting = inflater.inflate(R.layout.setting, null);
//	        layout.addView(setting);

	    setContentView(R.layout.horz_scroll_with_image_menu);
		inflater= LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));

        scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
        foot_view = findViewById(R.id.menu);
        
        setView();
	
	}
    private  void setView(){
		show_view = init_sliding_menu(R.layout.setting);
		
    }
	public void click_go_account_manage_activity(View view){
		 open_activity(AccountManagerActivity.class);
	}
	 public void click_go_about_teamkn_activity(View view){
		 open_activity(AboutActivity.class);
	}
}
