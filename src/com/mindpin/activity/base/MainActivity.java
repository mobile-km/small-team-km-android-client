package com.mindpin.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.Logic.AccountManager;
import com.mindpin.Logic.CameraLogic;
import com.mindpin.activity.collection.CollectionListActivity;
import com.mindpin.activity.comment.ReceivedCommentListActivity;
import com.mindpin.activity.contacts.ContactsActivity;
import com.mindpin.activity.contacts.FollowingGridActivity;
import com.mindpin.activity.feed.FeedListActivity;
import com.mindpin.activity.sendfeed.NewFeedActivity;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.cache.image.ImageCache;
import com.mindpin.receiver.BroadcastReceiverConstants;

public class MainActivity extends MindpinBaseActivity {
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

		update_account_info();			
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注册过的receiver，一定要在onDestroy时反注册，否则会有leak异常，导致程序崩溃
		unregisterReceiver(syn_data_broadcast_receiver);
	}
	
	//同步操作
	private void start_syn_data() {
		sendBroadcast(new Intent("com.mindpin.action.start_syn_data"));
	}
	
	// 在界面上刷新头像和用户名
	private void update_account_info(){
		TextView account_name_textview   = (TextView) findViewById(R.id.account_name);
		ImageView account_avatar_imgview = (ImageView)findViewById(R.id.account_avatar);
		
		account_name_textview.setText(current_user().name);
		ImageCache.load_cached_image(current_user().avatar_url, account_avatar_imgview);
	}
	
	//设置 new_feed 按钮点击事件
	public void main_button_new_feed_click(View view){
		open_activity(NewFeedActivity.class);
	}
	
	//设置 camera 按钮点击事件
	public void main_button_camera_click(View view){
		CameraLogic.call_system_camera(this);
	}
	
	//设置 feeds 按钮点击事件
	public void main_button_feeds_click(View view){
		open_activity(FeedListActivity.class);
	}
	
	//设置collections按钮点击事件
	public void main_button_collections_click(View view){
		open_activity(CollectionListActivity.class);
	}
	
	public void main_button_received_comments_click(View view){
		open_activity(ReceivedCommentListActivity.class);
	}
	
	public void main_button_followings_click(View view){
		open_activity(FollowingGridActivity.class);
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
			open_activity(MindpinSettingActivity.class);
			break;
		case R.id.menu_contacts:
			open_activity(ContactsActivity.class);
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
	
	//处理其他activity界面的回调，例如照相
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		
		switch (requestCode) {
		case CameraLogic.REQUEST_CODE_CAPTURE:
			Intent intent = new Intent(getApplicationContext(), NewFeedActivity.class);
			intent.putExtra(CameraLogic.HAS_IMAGE_CAPTURE, true);
			startActivity(intent);
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 同步服务广播接收器
	class SynDataUIBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			int progress = intent.getExtras().getInt("progress");
			switch(progress){
			case -1:
				//出错
				BaseUtils.toast(R.string.app_data_syn_fail);
				data_syn_progress_bar.setProgress(0);
				data_syn_progress_bar.setVisibility(View.GONE);
				break;
			case 0:
				//开始同步
				data_syn_textview.setText(R.string.now_syning);
				data_syn_progress_bar.setProgress(0);
				data_syn_progress_bar.setVisibility(View.VISIBLE);
			case 1:
				// 同步进行中
				int current_progress = data_syn_progress_bar.getProgress();
				if (current_progress < 90) {
					data_syn_progress_bar.setProgress(current_progress + 1);
				}
				break;
			case 100:
				// 同步完毕
				data_syn_textview.setText("同步完毕");
				data_syn_progress_bar.setProgress(100);
				
				AccountManager.touch_last_syn_time();
				update_account_info();
				break;
			case 101:
				// 界面显示同步结束
				long time = AccountManager.last_syn_time();
				String str = BaseUtils.date_string(time);
				data_syn_textview.setText("数据同步于 " + str);
				data_syn_progress_bar.setVisibility(View.GONE);
				break;
			}
		}
	}
	
}
