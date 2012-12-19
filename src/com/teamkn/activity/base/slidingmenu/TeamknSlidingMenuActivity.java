package com.teamkn.activity.base.slidingmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.TeamknSettingActivity;
import com.teamkn.activity.social_circle.SocialCircleActivity;
import com.teamkn.activity.usermsg.UserManagerActivity;
import com.teamkn.base.activity.ArrayListMenu;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.widget.adapter.MenuListAdapter;

public class TeamknSlidingMenuActivity extends TeamknBaseActivity {
	MenuListAdapter adapter = null;
	ArrayList<Map<String, Object>> list;
	Map<String, Object> map = new HashMap<String, Object>();
	
	ListView list_menu_view; // 包含菜单项的列表
	public View init_sliding_menu(int content_view_layout_xml){
		LayoutInflater inflater = LayoutInflater.from(this);
		// 1 获取实际内容模版View
		View content_view = inflater.inflate(content_view_layout_xml, null);
		
		// 2 获取按钮
		View button = content_view.findViewById(R.id.iv_foot_view);
		// 3 注册按钮事件
        MyHorizontalScrollView scroll_view = (MyHorizontalScrollView) findViewById(R.id.my_scroll_view);
        View menu_view = findViewById(R.id.menu);
        button.setOnClickListener(new ClickListenerForScrolling(scroll_view, menu_view));
		
		// 4 初始化菜单视图
//        View transparent = new TextView(this);
        scroll_view.init(content_view );
        
        return content_view;
	}
	private void load_list(){	
		list_menu_view = (ListView)findViewById(R.id.list_menu_view);
		list = ArrayListMenu.getData();
		adapter = new MenuListAdapter(this);
		adapter.add_items(list);
		list_menu_view.setAdapter(adapter);
		list_menu_view.setDivider(null);
		
		list_menu_view.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {
				switch (arg2) {
				case 0: //我的首页  follow  的首页
					Intent follow_intent = new Intent(TeamknSlidingMenuActivity.this,MainActivity.class);
					follow_intent.putExtra("data_list_public", MainActivity.RequestCode.我的首页);
					follow_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
					startActivity(follow_intent);
					break;
				case 1: // 我的列表
					Intent my_intent = new Intent(TeamknSlidingMenuActivity.this,MainActivity.class);
					my_intent.putExtra("data_list_public", MainActivity.RequestCode.我的列表);
					my_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
					startActivity(my_intent);
					break;
				case 2:  // 公共的列表  
					Intent public_intent = new Intent(TeamknSlidingMenuActivity.this,MainActivity.class);
					public_intent.putExtra("data_list_public", MainActivity.RequestCode.公开的列表);
					public_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
					startActivity(public_intent);
					break;
				case 3:  // 社交管理
//					Intent social_intent = new Intent(TeamknSlidingMenuActivity.this,SocialCircleActivity.class);
//					startActivity(social_intent);
					open_activity(SocialCircleActivity.class);
					break;
				case 4:  // 设置选项
//					open_activity(TeamknSettingActivity.class);
					Intent setting_intent = new Intent(TeamknSlidingMenuActivity.this,TeamknSettingActivity.class);
					startActivity(setting_intent);
					break;
				case 5:  // 退出应用
					click_exit_teamkn_activity();
					break;
				default:
					break;
				}
			}
		});
	}
	android.view.View.OnClickListener setUserManagerClick = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(TeamknSlidingMenuActivity.this , UserManagerActivity.class);
			startActivity(intent);
		}
	};
	@Override
	protected void onResume() {
	  load_list();
	  super.onResume();
	}
}
