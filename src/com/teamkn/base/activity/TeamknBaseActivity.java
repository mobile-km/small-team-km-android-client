package com.teamkn.base.activity;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.base.AccountManagerActivity;
import com.teamkn.activity.base.LoginActivity;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.TeamknSettingActivity;
import com.teamkn.activity.note.EditNoteActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.CameraLogic;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.AccountUser;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.widget.adapter.MenuListAdapter;

abstract public class TeamknBaseActivity extends Activity {
	public class RequestCode{
	    public final static int NEW_TEXT = 9;
	    public final static int FROM_ALBUM = 1;
	    public final static int FROM_CAMERA = 2;
	}
    MenuListAdapter adapter = null;
	ArrayList<Map<String, Object>> list;
	Map<String, Object> map = new HashMap<String, Object>();
	ListView list_menu_view;
	
	ImageView menu_user_avater_iv;
	TextView  menu_user_name_tv ;
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
			System.out.println("----------------------------------------");
			return true;
        }
		if( TeamknApplication.current_show_activity!=null 
				&& TeamknApplication.current_show_activity
      		  .equals("com.teamkn.activity.base.TeamknSettingActivity")){
			return true;
        }
		if( TeamknApplication.current_show_activity!=null 
				&& TeamknApplication.current_show_activity
      		  .equals("com.teamkn.activity.usermsg.UserMsgActivity")){
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
		menu_user_avater_iv = (ImageView)findViewById(R.id.menu_user_avater_iv);
		menu_user_name_tv = (TextView)findViewById(R.id.menu_user_name_tv);
		// 设置用户头像和名字
		AccountUser user = current_user();
		byte[] avatar = user.avatar;
		String name = current_user().name;
		if (avatar != null) {
			Bitmap bitmap = BitmapFactory
					.decodeStream(new ByteArrayInputStream(avatar));
			Drawable drawable = new BitmapDrawable(bitmap);
			menu_user_avater_iv.setBackgroundDrawable(drawable);
		} else {
			menu_user_avater_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
		}
		menu_user_name_tv.setText(name);
		
		
		list = ArrayListMenu.getData();
		adapter = new MenuListAdapter(this);
		adapter.add_items(list);
		list_menu_view.setAdapter(adapter);
		list_menu_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				switch (arg2) {
				case 0:  // 公共列表
//					open_activity(MainActivity.class);
					Intent public_intent = new Intent(TeamknBaseActivity.this,MainActivity.class);
					public_intent.putExtra("data_list_public", "true");
					public_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
					startActivity(public_intent);
					break;
				case 1: // 我的列表
//					open_activity(MainActivity.class);
					Intent my_intent = new Intent(TeamknBaseActivity.this,MainActivity.class);
					my_intent.putExtra("data_list_public", "false");
					my_intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
					startActivity(my_intent);
					break;
				case 2: // 我的书签
//					open_activity(UserMsgActivity.class);
					break;
				case 3:  // 设置选项
					open_activity(TeamknSettingActivity.class);
					break;
				case 4:  //用户信息
//					open_activity(UserMsgActivity.class);
					break;
				case 5: // 注销用户
					open_activity(AccountManagerActivity.class);
					break;
				case 6: // 退出登录
					click_exit_teamkn_activity();
					break;
				default:
					break;
				}
			}
		});
	}
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
	 
	
	final public void click_go_edit_node_activity(View view){
		Intent intent = new Intent();
		intent.setClass(this, EditNoteActivity.class);
		intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, NoteDBHelper.Kind.TEXT);
		startActivityForResult(intent,TeamknBaseActivity.RequestCode.NEW_TEXT);
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
		switch(requestCode){
		  case TeamknBaseActivity.RequestCode.NEW_TEXT:
		    BaseUtils.toast("创建成功");
		    break;
		  case TeamknBaseActivity.RequestCode.FROM_ALBUM:
		    String image_path = BaseUtils.get_file_path_from_image_uri(data.getData());
		    
		    start_edit_note_activity_by_image_path(image_path);
		    break;
		  case TeamknBaseActivity.RequestCode.FROM_CAMERA:
			  String file_path = CameraLogic.IMAGE_CAPTURE_TEMP_FILE.getAbsolutePath();
	          try {
	             Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), file_path, null, null));
	             start_edit_note_activity_by_image_path(file_path); 
	          } catch (FileNotFoundException e) {
	             e.printStackTrace();
	          }
		    break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void start_edit_note_activity_by_image_path(String image_path){
	    Intent intent = new Intent(TeamknBaseActivity.this, EditNoteActivity.class);
	    intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, NoteDBHelper.Kind.IMAGE);
	    intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH, image_path);
	    startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){			
			return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}
	
}
