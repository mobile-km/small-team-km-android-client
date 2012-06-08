package com.teamkn.activity.base;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.note.EditNoteActivity;
import com.teamkn.activity.note.NoteListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.receiver.BroadcastReceiverConstants;
import com.teamkn.receiver.SynDataBroadcastReceiver;

public class MainActivity extends TeamknBaseActivity {
  public class RequestCode{
    public final static int NEW_TEXT = 0;
    public final static int FROM_ALBUM = 1;
    public final static int FROM_CAMERA = 2;
  }
	private TextView data_syn_textview;
	private ProgressBar data_syn_progress_bar;
	final private SynDataUIBroadcastReceiver syn_data_broadcast_receiver = new SynDataUIBroadcastReceiver();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 注册receiver
		registerReceiver(syn_data_broadcast_receiver, new IntentFilter(
				BroadcastReceiverConstants.ACTION_SYN_DATA_UI));
		
		// load view
		setContentView(R.layout.base_main);
		data_syn_textview = (TextView)findViewById(R.id.main_data_syn_text);
		data_syn_progress_bar = (ProgressBar)findViewById(R.id.main_data_syn_progress_bar);
		
		start_syn_data();
	}
	
  //同步操作
	private void start_syn_data() {
		sendBroadcast(new Intent("com.teamkn.action.start_syn_data"));
	}
	
	public void click_new_text(View view){
	  Intent intent = new Intent();
	  intent.setClass(this, EditNoteActivity.class);
	  intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, NoteDBHelper.Kind.TEXT);
	  startActivityForResult(intent,MainActivity.RequestCode.NEW_TEXT);
	}
	
	public void click_from_album(View view){
	  Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
    intent.setType("image/*");
    startActivityForResult(intent,MainActivity.RequestCode.FROM_ALBUM);
	}
	
	public void click_from_camera(View view){
	  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	  startActivityForResult(intent, MainActivity.RequestCode.FROM_CAMERA);
	}
	
	public void show_note_list(View view){
	  open_activity(NoteListActivity.class);
	}
	
	@Override
  protected void onDestroy() {
    super.onDestroy();
    // 注册过的receiver，一定要在onDestroy时反注册，否则会有leak异常，导致程序崩溃
    unregisterReceiver(syn_data_broadcast_receiver);
  }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			open_activity(AboutActivity.class);
			break;
		case R.id.menu_setting:
			open_activity(TeamknSettingActivity.class);
			break;
		case R.id.menu_account_management:
			open_activity(AccountManagerActivity.class);
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //这里只能用this，不能用appliction_context
			
			builder
				.setTitle(R.string.dialog_close_app_title)
				.setMessage(R.string.dialog_close_app_text)
				.setPositiveButton(R.string.dialog_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							MainActivity.this.finish();
						}
					})
				.setNegativeButton(R.string.dialog_cancel, null)
				.show();
			
			return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}
	
	//处理其他activity界面的回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		switch(requestCode){
		  case MainActivity.RequestCode.NEW_TEXT:
		    BaseUtils.toast("创建成功");
		    break;
		  case MainActivity.RequestCode.FROM_ALBUM:
		    String image_path = BaseUtils.get_file_path_from_image_uri(data.getData());
		    
		    start_edit_note_activity_by_image_path(image_path);
		    break;
		  case MainActivity.RequestCode.FROM_CAMERA:
		    Uri uri = data.getData();
		    String scheme = uri.getScheme();
		    String path;
		    if(scheme.equals("content")){
		      path = BaseUtils.get_file_path_from_image_uri(data.getData());
		    }else{
		      path = uri.getPath();
		    }
		    if(new File(path).exists()){
		      System.out.println(path);
		      start_edit_note_activity_by_image_path(path);
		    }
		    break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void start_edit_note_activity_by_image_path(String image_path){
    Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
    intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, NoteDBHelper.Kind.IMAGE);
    intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH, image_path);
    startActivity(intent);
	}
	
	// 同步服务广播接收器
	class SynDataUIBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
		  String type = intent.getExtras().getString("type");
		  if(type.equals(SynDataBroadcastReceiver.Type.SET_MAX)){
		    int max_num = intent.getExtras().getInt(SynDataBroadcastReceiver.Type.SET_MAX);
		    data_syn_progress_bar.setMax(max_num);
		  }else if(type.equals(SynDataBroadcastReceiver.Type.START_SYN)){
		    data_syn_textview.setText(R.string.now_syning);
		    data_syn_progress_bar.setProgress(0);
        data_syn_progress_bar.setVisibility(View.VISIBLE);
		  }else if(type.equals(SynDataBroadcastReceiver.Type.PROGRESS)){
		    int progress = intent.getExtras().getInt(SynDataBroadcastReceiver.Type.PROGRESS);
		    data_syn_progress_bar.setProgress(progress);
        data_syn_progress_bar.setVisibility(View.VISIBLE);
		  }else if(type.equals(SynDataBroadcastReceiver.Type.EXCEPTION)){
        BaseUtils.toast(R.string.app_data_syn_fail);
		  }else if(type.equals(SynDataBroadcastReceiver.Type.SUCCESS)){
		    AccountManager.touch_last_syn_time();
		  }else if(type.equals(SynDataBroadcastReceiver.Type.FINAL)){
        data_syn_textview.setText("同步完毕");
        long time = AccountManager.last_syn_time();
        String str = BaseUtils.date_string(time);
        data_syn_textview.setText("数据同步于 " + str);
        data_syn_progress_bar.setVisibility(View.GONE);
		  }
		}
	}
	
}
