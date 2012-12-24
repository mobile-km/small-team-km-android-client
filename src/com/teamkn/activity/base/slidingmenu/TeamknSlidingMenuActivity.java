package com.teamkn.activity.base.slidingmenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

public class TeamknSlidingMenuActivity extends TeamknBaseActivity {
	
	public View init_sliding_menu(int content_view_layout_xml){
		// 0 给页面加载抽屉菜单公共布局xml
		LayoutInflater inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.tkn_sliding_menu, null));
		
		// 1 获取实际内容模板并初始化组件
		final SlidingMenuView sliding_menu = (SlidingMenuView) findViewById(R.id.content_container);		
		View content_view = inflater.inflate(content_view_layout_xml, null);
		
		sliding_menu.init(content_view);
				
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
