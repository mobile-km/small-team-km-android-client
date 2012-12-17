package com.teamkn.base.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.base.AccountManagerActivity;
import com.teamkn.activity.base.LoginActivity;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.TeamknSettingActivity;
import com.teamkn.activity.social_circle.SocialCircleActivity;
import com.teamkn.activity.usermsg.UserManagerActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.utils.CameraLogic;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.AccountUser;
import com.teamkn.widget.adapter.MenuListAdapter;

abstract public class TeamknBaseActivity extends Activity {
	public class RequestCode{
	    public final static int NEW_TEXT = 3;
	    public final static int FROM_ALBUM = 1;
	    public final static int FROM_CAMERA = 2;
	}
    MenuListAdapter adapter = null;
	ArrayList<Map<String, Object>> list;
	Map<String, Object> map = new HashMap<String, Object>();
	
	ListView list_menu_view; // 包含菜单项的列表
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivitiesStackSingleton.tidy_and_push_activity(this);
	}
	
	private boolean just_menu_actvity(){
		boolean is_menu = false;
		System.out.println(" just_menu_activity : " +TeamknApplication.current_show_activity);
		if( TeamknApplication.current_show_activity!=null 
				&& TeamknApplication.current_show_activity
      		  .equals("com.teamkn.activity.base.MainActivity")){
			return true;
        }
		if( TeamknApplication.current_show_activity!=null 
				&& TeamknApplication.current_show_activity
      		  .equals("com.teamkn.activity.social_circle.SocialCircleActivity")){
			return true;
        }
		if( TeamknApplication.current_show_activity!=null 
				&& TeamknApplication.current_show_activity
      		  .equals("com.teamkn.activity.base.TeamknSettingActivity")){
			return true;
        }
		if( TeamknApplication.current_show_activity!=null 
				&& TeamknApplication.current_show_activity
      		  .equals("com.teamkn.activity.usermsg.UserManagerActivity")){
			return true;
        }
//		if( TeamknApplication.current_show_activity!=null 
//				&& TeamknApplication.current_show_activity
//      		  .equals("com.teamkn.activity.base.AccountManagerActivity")){
//			return true;
//        }
		return is_menu;
	}
	
	private void load_list(){		
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
					Intent follow_intent = new Intent(TeamknBaseActivity.this,MainActivity.class);
					follow_intent.putExtra("data_list_public", MainActivity.RequestCode.我的首页);
					follow_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
					startActivity(follow_intent);
					break;
				case 1: // 我的列表
					Intent my_intent = new Intent(TeamknBaseActivity.this,MainActivity.class);
					my_intent.putExtra("data_list_public", MainActivity.RequestCode.我的列表);
					my_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
					startActivity(my_intent);
					break;
				case 2:  // 公共的列表  
					Intent public_intent = new Intent(TeamknBaseActivity.this,MainActivity.class);
					public_intent.putExtra("data_list_public", MainActivity.RequestCode.公开的列表);
					public_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
					startActivity(public_intent);
					break;
				case 3:  // 社交管理
					Intent social_intent = new Intent(TeamknBaseActivity.this,SocialCircleActivity.class);
					startActivity(social_intent);
					break;
				case 4:  // 设置选项
					open_activity(TeamknSettingActivity.class);
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
			Intent intent = new Intent(TeamknBaseActivity.this , UserManagerActivity.class);
			startActivity(intent);
		}
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitiesStackSingleton.remove_activity(this);
	}
	
	@Override
	protected void onResume() {
	  TeamknApplication.current_show_activity = this.getClass().getName();
	  boolean is_menu = just_menu_actvity();
		if(is_menu){
			list_menu_view = (ListView)findViewById(R.id.list_menu_view);
			load_list();
		}
	  super.onResume();
	}
	
	@Override
	protected void onPause() {	
	  TeamknApplication.current_show_activity = null;
	  System.out.println("teamknbaseactivity on pause"  + this.getClass().getName());
//	  this.finish();
	  super.onPause();
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
	    	.setTitle("选择图片")
				.setTitle("退出程序")
				.setMessage("是否退出 teamkn？")
				.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
//							TeamknBaseActivity.this.finish();
							ActivitiesStackSingleton.clear_activities_stack();
							overridePendingTransition(R.anim.zoom_enter,
									R.anim.zoom_exit);
						}
					})
				.setNegativeButton("取消", null)
				.show();
	}
	 
	

	final public void click_go_edit_node_album_camera_activity(View view){
		
		new AlertDialog.Builder(this)
    	.setTitle("选择图片")
    	.setItems(new String[] { "选择本地图片", "拍照" }, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent;
				switch (which) {
				case 0:
					intent = new Intent(Intent.ACTION_PICK, null);  
					intent.setDataAndType(  
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
                            "image/*");  
				    startActivityForResult(intent,TeamknBaseActivity.RequestCode.FROM_ALBUM);
					break;
				case 1:
                    CameraLogic.call_system_camera(TeamknBaseActivity.this,TeamknBaseActivity.RequestCode.FROM_CAMERA);
					break;
				default:
					break;
				}
			}
		})
    	.setNegativeButton("取消", null)
    	.show();
	}
	final public void click_go_edit_node_record_activity(View view){
		open_activity(AccountManagerActivity.class);
	}
	
	
	//处理其他activity界面的回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){			
			return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}
	public void click_nil(View view){
		 
	}
}
