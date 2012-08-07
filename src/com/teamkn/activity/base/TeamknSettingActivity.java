package com.teamkn.activity.base;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.HorzScrollWithListMenu;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.base.activity.TeamknBaseActivity;

public class TeamknSettingActivity extends TeamknBaseActivity {
	//menu菜单
		 MyHorizontalScrollView scrollView;
		 View setting;
		 View foot_view;  //底层  图层 隐形部分
		 ImageView iv_foot_view;
		 
		 boolean menuOut = false;
		 Handler handler = new Handler();
		//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         // <<
		 LayoutInflater inflater = LayoutInflater.from(this);
	     setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));
	     
//	     addPreferencesFromResource(R.xml.settings);
	
	     scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
	     foot_view = findViewById(R.id.menu);    
	     RelativeLayout foot_rl_setting = (RelativeLayout)findViewById(R.id.foot_rl_setting);
	     setting = inflater.inflate(R.layout.setting, null);
	     
	     
	     iv_foot_view = (ImageView) setting.findViewById(R.id.iv_foot_view);
	     iv_foot_view.setOnClickListener(new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view));
	     foot_rl_setting.setOnClickListener(new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view));
	     View transparent = new TextView(this);
	     transparent.setBackgroundColor(android.R.color.transparent);
	
	     final View[] children = new View[] { transparent, setting };
	     int scrollToViewIdx = 1;
	     scrollView.initViews(children, scrollToViewIdx, new HorzScrollWithListMenu.SizeCallbackForMenu(iv_foot_view));    
	     //>>   
	     
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
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("--------++++++++++++++++------------- "  + event.getX());
		return super.onTouchEvent(event);
	}
    
}
