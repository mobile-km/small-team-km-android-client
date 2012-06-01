package com.teamkn.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.base.LoginActivity;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.AccountUser;

abstract public class TeamknBaseActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivitiesStackSingleton.tidy_and_push_activity(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitiesStackSingleton.remove_activity(this);
	}
	
	
	// 关闭所有activity，并重新打开login
	final public void restart_to_login(){
		ActivitiesStackSingleton.clear_activities_stack();
		open_activity(LoginActivity.class);
	}

	// 绑定在顶栏 go_back 按钮上的事件处理
	final public void go_back(View view) {
		on_go_back();
		this.finish();
	}

	// 打开一个新的activity，此方法用来简化调用
	final public void open_activity(Class<?> cls) {
		startActivity(new Intent(getApplicationContext(), cls));
	}

	final public boolean is_logged_in() {
		return AccountManager.is_logged_in();
	}

	final public AccountUser current_user() {
		return AccountManager.current_user();
	}

	// 钩子，自行重载
	public void on_go_back() {
	};
	
	
	// 尝试从缓存获取一个图片放到指定的view
	final public void load_cached_image(String image_url, ImageView image_view){
		ImageCache.load_cached_image(image_url, image_view);
	}

}
