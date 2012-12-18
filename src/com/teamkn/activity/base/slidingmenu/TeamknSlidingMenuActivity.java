package com.teamkn.activity.base.slidingmenu;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

public class TeamknSlidingMenuActivity extends TeamknBaseActivity {
	
	public View init_sliding_menu(int content_view_layout_xml){
		LayoutInflater inflater = LayoutInflater.from(this);
		// 1 获取实际内容模版View
		View content_view = inflater.inflate(content_view_layout_xml, null);
		
		// 2 获取按钮
		View button = content_view.findViewById(R.id.iv_foot_view);
		
		// 3 注册按钮事件
        MyHorizontalScrollView scroll_view = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
        View menu_view = findViewById(R.id.menu);
        button.setOnClickListener(new ClickListenerForScrolling(scroll_view, menu_view));
		
		// 4 初始化菜单视图
        View transparent = new TextView(this);
        scroll_view.initViews(new View[] { transparent, content_view }, 1, button);
        
        return content_view;
	}
	
}
