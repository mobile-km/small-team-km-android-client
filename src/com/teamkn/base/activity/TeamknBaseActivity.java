package com.teamkn.base.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.base.LoginActivity;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.AccountUser;

abstract public class TeamknBaseActivity extends Activity {
	public class RequestCode{
	    public final static int FROM_ALBUM = 1;
	    public final static int FROM_CAMERA = 2;
	    public final static int NEW_TEXT = 3;
	}

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
	
	 public void click_exit_teamkn_activity(){
//			AlertDialog.Builder builder = new AlertDialog.Builder(this); //这里只能用this，不能用appliction_context
			new AlertDialog.Builder(this)
				.setTitle("退出程序")
				.setMessage("是否退出 teamkn？")
				.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							ActivitiesStackSingleton.clear_activities_stack();
							overridePendingTransition(R.anim.zoom_enter,
									R.anim.zoom_exit);
						}
					})
				.setNegativeButton("取消", null)
				.show();
	}

	//处理其他activity界面的回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void click_nil(View view){
		 
	}
}
