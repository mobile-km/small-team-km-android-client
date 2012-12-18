package com.teamkn.activity.base.slidingmenu;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
        final MyHorizontalScrollView scroll_view = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
        
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Display display = getWindowManager().getDefaultDisplay();
				int menu_width = display.getWidth();
				
				Boolean is_open = (Boolean) scroll_view.getTag();
				if(null == is_open) is_open = false;
				
				int left = is_open ? menu_width : 0;
				scroll_view.smoothScrollTo(left, 0);
				
				scroll_view.setTag(!is_open);
			}
        	
        });
		
		// 4 初始化菜单视图
        scroll_view.initViews(new TextView(this), content_view, button);
        
        return content_view;
	}
	
}
