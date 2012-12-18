package com.teamkn.activity.base.slidingmenu;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

public class TeamknSlidingMenuActivity extends TeamknBaseActivity {
	
	public View init_sliding_menu(int content_view_layout_xml){
		final MyHorizontalScrollView scroll_view = (MyHorizontalScrollView) findViewById(R.id.my_scroll_view);
				
		// 1 获取实际内容模板并初始化组件
		View content_view = LayoutInflater.from(this).inflate(content_view_layout_xml, null);
        scroll_view.init(content_view);
		
		// 2 获取按钮并注册事件
		content_view.findViewById(R.id.iv_foot_view).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						scroll_view.toggle();
					}
				});
        
        return content_view;
	}
	
}
