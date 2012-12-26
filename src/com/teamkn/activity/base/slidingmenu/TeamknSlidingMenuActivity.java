package com.teamkn.activity.base.slidingmenu;

import java.util.ArrayList;
import java.util.Map;

import org.apache.lucene.util.SetOnce;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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
						sliding_menu.left_toggle();
					}
				});
		
        return content_view;
	}
	
	public View init_sliding_menu(int content_view_layout_xml, int right_view_layout_xml){
		// 0 给页面加载抽屉菜单公共布局xml
		LayoutInflater inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.tkn_sliding_menu, null));
		
		// 1 获取实际内容模板并初始化组件
		final SlidingMenuView sliding_menu = (SlidingMenuView) findViewById(R.id.content_container);		
		View content_view = inflater.inflate(content_view_layout_xml, null);
		
		ViewGroup right_container = (ViewGroup)findViewById(R.id.right_container);
		View right_view = inflater.inflate(right_view_layout_xml, null);
		right_container.addView(right_view);
		
		View left_container = findViewById(R.id.menu);
		
		sliding_menu.init2(content_view, left_container, right_container);
		
		// 2 获取按钮并注册事件
		content_view.findViewById(R.id.iv_foot_view).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						sliding_menu.left_toggle();
					}
				});
		
		content_view.findViewById(R.id.mi_data_list_add).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						sliding_menu.right_toggle();
					}
				});
		
		return content_view;
	}

	private void load_list(){
		final SlidingMenuView sliding_menu = (SlidingMenuView) findViewById(R.id.content_container);
		
		ListView list_menu_view = (ListView) findViewById(R.id.list_menu_view); // 包含菜单项的列表
		ArrayList<Map<String, Object>> list = ArrayListMenu.getData();
		MenuListAdapter adapter = new MenuListAdapter(this);
		adapter.add_items(list);
		list_menu_view.setAdapter(adapter);
		list_menu_view.setDivider(null);
		
		list_menu_view.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {
				switch (arg2) {
				case 0: //我的首页  follow  的首页
					sliding_menu.wenyi_close(new SlidingMenuView.OnCloseListener() {
						@Override
						public void on_close() {
							Intent follow_intent = new Intent(TeamknSlidingMenuActivity.this,MainActivity.class);
							follow_intent.putExtra("data_list_public", MainActivity.RequestCode.我的首页);
							follow_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
							startActivity(follow_intent);
							overridePendingTransition(R.anim.xxxx_enter, R.anim.xxxx_exit);
						}
					});
					break;
				case 1: // 我的列表
					sliding_menu.wenyi_close(new SlidingMenuView.OnCloseListener() {
						@Override
						public void on_close() {
							Intent my_intent = new Intent(TeamknSlidingMenuActivity.this,MainActivity.class);
							my_intent.putExtra("data_list_public", MainActivity.RequestCode.我的列表);
							my_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
							startActivity(my_intent);
							overridePendingTransition(R.anim.xxxx_enter, R.anim.xxxx_exit);
						}
					});
					break;
				case 2:  // 公共的列表  
					sliding_menu.wenyi_close(new SlidingMenuView.OnCloseListener() {
						@Override
						public void on_close() {
							Intent public_intent = new Intent(TeamknSlidingMenuActivity.this,MainActivity.class);
							public_intent.putExtra("data_list_public", MainActivity.RequestCode.公开的列表);
							public_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
							startActivity(public_intent);
							overridePendingTransition(R.anim.xxxx_enter, R.anim.xxxx_exit);
						}
					});
					break;
				case 3:  // 社交管理
					sliding_menu.wenyi_close(new SlidingMenuView.OnCloseListener() {
						@Override
						public void on_close() {
							open_activity(SocialCircleActivity.class);
							overridePendingTransition(R.anim.xxxx_enter, R.anim.xxxx_exit);
						}
					});
					break;
				case 4:  // 设置选项
					sliding_menu.wenyi_close(new SlidingMenuView.OnCloseListener() {
						@Override
						public void on_close() {
							Intent setting_intent = new Intent(TeamknSlidingMenuActivity.this,TeamknSettingActivity.class);
							startActivity(setting_intent);
							overridePendingTransition(R.anim.xxxx_enter, R.anim.xxxx_exit);
						}
					});
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
