package com.teamkn.activity.base;

import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.activity.note.EditNoteActivity;
import com.teamkn.activity.note.NoteListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.service.SynNoteService;
import com.teamkn.service.SynNoteService.SynNoteBinder;

public class MainActivity extends TeamknBaseActivity {
  public class RequestCode{
    public final static int NEW_TEXT = 0;
    public final static int FROM_ALBUM = 1;
    public final static int FROM_CAMERA = 2;
  }
	private TextView data_syn_textview;
	private ProgressBar data_syn_progress_bar;
	private SynNoteBinder syn_note_binder;
	private SynUIBinder syn_ui_binder = new SynUIBinder();
	
	private ServiceConnection conn = new ServiceConnection(){
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      System.out.println("ServiceConnection  ServiceConnection");
      System.out.println("ServiceConnection  ServiceConnection Thread" + Thread.currentThread());
      syn_note_binder = (SynNoteBinder)service;
      syn_note_binder.set_syn_ui_binder(syn_ui_binder);
      syn_note_binder.start();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      // 当 SynNoteService 因异常而断开连接的时候，这个方法才会被调用
      System.out.println("ServiceConnection  onServiceDisconnected");
      syn_note_binder = null;
    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// load view
		setContentView(R.layout.base_main);
		data_syn_textview = (TextView)findViewById(R.id.main_data_syn_text);
		data_syn_progress_bar = (ProgressBar)findViewById(R.id.main_data_syn_progress_bar);
		
		// 注册更新服务
    Intent intent = new Intent(MainActivity.this,SynNoteService.class);
    bindService(intent, conn, Context.BIND_AUTO_CREATE);
    System.out.println("MainActivity onCreate end");
    System.out.println("MainActivity onCreate Thread" + Thread.currentThread());
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
	
	public void click_manual_syn(View view){
	  if(syn_note_binder != null){
	    syn_note_binder.manual_syn();
	  }
	}
	
	public void show_note_list(View view){
	  open_activity(NoteListActivity.class);
	}
	
	@Override
  protected void onDestroy() {
    super.onDestroy();
    // 解除 和 更新笔记服务的绑定
    unbindService(conn);
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
	
	 public class SynUIBinder{
	    public void set_max_num(int max_num){
	      final int num = max_num;
	      System.out.println("set_max_num   " + max_num);
	      data_syn_progress_bar.post(new Runnable() {
          @Override
          public void run() {
            data_syn_progress_bar.setMax(num);
          }
        });
	    }
	    
	    public void set_start_syn(){
	      System.out.println("set_start_syn");
	      data_syn_textview.post(new Runnable() {
          @Override
          public void run() {
            data_syn_textview.setText(R.string.now_syning);
            data_syn_progress_bar.setProgress(0);
            data_syn_progress_bar.setVisibility(View.VISIBLE);
          }
        });
	    }
	    
	    public void set_progress(int progress){
	      final int num = progress;
	      System.out.println("set_progress  " + progress);
	      data_syn_progress_bar.post(new Runnable() {
          @Override
          public void run() {
            data_syn_progress_bar.setProgress(num);
            data_syn_progress_bar.setVisibility(View.VISIBLE);
          }
        });
	    }
	    
	    public void set_syn_success(){
	      System.out.println("syn_success");
	      TeamknPreferences.touch_last_syn_time(true);
	      data_syn_textview.post(new Runnable() {
          @Override
          public void run() {
            String str = TeamknPreferences.last_syn_time();
            data_syn_textview.setText("上次同步成功: " + str);
            data_syn_progress_bar.setVisibility(View.GONE);
          }
        });
	    }
	    
      public void set_syn_fail() {
        System.out.println("syn_fail");
        TeamknPreferences.touch_last_syn_time(false);
        data_syn_textview.post(new Runnable() {
          @Override
          public void run() {
            String str = TeamknPreferences.last_syn_time();
            data_syn_textview.setText("上次同步失败: " + str);
            data_syn_progress_bar.setVisibility(View.GONE);
          }
        });
      }
	  }
}
