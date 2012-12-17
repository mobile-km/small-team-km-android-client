package com.teamkn.activity.base;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.ClickListenerForScrolling;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.base.activity.TeamknBaseActivity;

public class TeamknSettingActivity extends TeamknBaseActivity{
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
    	show_view = inflater.inflate(R.layout.setting, null);
    	ViewGroup head_view = (ViewGroup) show_view.findViewById(R.id.head);
    	ImageView btnSlide = (ImageView) head_view.findViewById(R.id.iv_foot_view);
        
        btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, foot_view));
     
        View transparent = new TextView(TeamknSettingActivity.this);
        final View[] children = new View[] { transparent, show_view };
        int scrollToViewIdx = 1;

        scrollView.initViews(children, scrollToViewIdx, btnSlide);
        
        LinearLayout ll_setting = (LinearLayout)show_view.findViewById(R.id.ll_setting);
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
