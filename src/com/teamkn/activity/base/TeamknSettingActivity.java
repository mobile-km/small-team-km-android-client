package com.teamkn.activity.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.TeamknSlidingMenuActivity;

public class TeamknSettingActivity extends TeamknSlidingMenuActivity {
	LayoutInflater inflater;
    View show_view;  //显示的View
    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;
//	View setting;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	        setContentView(R.layout.horz_scroll_with_image_menu);

	    setContentView(R.layout.horz_scroll_with_image_menu);
		inflater= LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));

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
