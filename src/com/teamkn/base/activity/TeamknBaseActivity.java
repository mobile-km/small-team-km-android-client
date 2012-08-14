package com.teamkn.base.activity;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.base.AboutActivity;
import com.teamkn.activity.base.AccountManagerActivity;
import com.teamkn.activity.base.LoginActivity;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.TeamknSettingActivity;
import com.teamkn.activity.chat.ChatListActivity;
import com.teamkn.activity.note.EditNoteActivity;
import com.teamkn.activity.note.SearchActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.CameraLogic;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.AccountUser;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.widget.adapter.NoteListAdapter;

abstract public class TeamknBaseActivity extends Activity {
	public class RequestCode{
	    public final static int NEW_TEXT = 0;
	    public final static int FROM_ALBUM = 1;
	    public final static int FROM_CAMERA = 2;
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
	
	@Override
	protected void onResume() {
	  TeamknApplication.current_show_activity = this.getClass().getName();
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
	
	 public void click_go_note_activity(View view){
		open_activity(MainActivity.class);
	}
	
	 public void click_go_chatlist_activity(View view){
		open_activity(ChatListActivity.class);
	}
	
	 public void click_go_search_node_activity(View view){
		open_activity(SearchActivity.class);
	}
	
	 public void click_go_setting_activity(View view){
		open_activity(TeamknSettingActivity.class);
	}
	 
	 public void click_go_account_manage_activity(View view){
		 open_activity(AccountManagerActivity.class);
	}
	 public void click_go_about_teamkn_activity(View view){
		 open_activity(AboutActivity.class);
	}
	 public void click_exit_teamkn_activity(View view){
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
