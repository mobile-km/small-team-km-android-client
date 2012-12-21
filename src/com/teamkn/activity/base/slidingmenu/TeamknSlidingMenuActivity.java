package com.teamkn.activity.base.slidingmenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

public class TeamknSlidingMenuActivity extends TeamknBaseActivity {
	
	public View init_sliding_menu(int content_view_layout_xml){

		// 1 获取实际内容模板并初始化组件
		LinearLayout content_container = (LinearLayout) findViewById(R.id.content_container);		
		View content_view = LayoutInflater.from(this).inflate(content_view_layout_xml, null);
		final SlidingMenu sliding_menu = new SlidingMenu(content_container, content_view);
				
		// 2 获取按钮并注册事件
		content_view.findViewById(R.id.iv_foot_view).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						sliding_menu.toggle();
					}
				});
        
        return content_view;
	}
}
