package com.mindpin.activity.sendfeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.mindpin.R;
import com.mindpin.Logic.CameraLogic;
import com.mindpin.Logic.FeedDraftManager;
import com.mindpin.Logic.HttpApi;
import com.mindpin.application.MindpinApplication;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.base.utils.location.LocationManagerProxy;
import com.mindpin.database.FeedDraft;

public class NewFeedActivity extends MindpinBaseActivity  {
	public static final int REQUEST_SHOW_IMAGE_CAPTURE = 1;
	public static final int REQUEST_SHOW_IMAGE_ALBUM = 2;
	protected static final int REQUEST_SELECT_COLLECTIONS = 3;
	protected static final int REQUEST_SELECT_COLLECTIONS_AND_SEND = 4;
	
	public static final int MESSAGE_AUTH_FAIL = 1;
	public static final int MESSAGE_SENDING_FEED = 2;
	public static final int MESSAGE_SEND_FEED_SUCCESS = 3;
	public static final int MESSAGE_SAVE_FEED_DRAFT = 4;
	public static final int MESSAGE_SENDING_FEED_CHANGE_TITLE = 5;
	LinearLayout feed_captures;
	RelativeLayout feed_captures_parent;
	private List<String> capture_paths = new ArrayList<String>();
	
	private EditText feed_title_et;
	private EditText feed_detail_et;
	private String feed_title;
	private String feed_detail;
	private List<Integer> select_collection_ids;
	
	private ImageButton capture_bn;
	private Button send_bn;
	private ImageButton album_bn;
	private Button select_collections_bn;
	private boolean send_tsina = false;
	private int feed_draft_id = 0;
	private ProgressDialog progress_dialog;
	
	private LocationManagerProxy location_manager_proxy;
	private CheckBox pulish_location_check_box;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_feed);
		find_views();
		set_listener();
		process_extra();
		process_share();
		process_feed_draft();
		process_location();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		location_manager_proxy.disable_my_location();
	}
	
	private void process_location(){
		location_manager_proxy = new LocationManagerProxy();
		pulish_location_check_box = (CheckBox)findViewById(R.id.pulish_location);
		
		pulish_location_check_box.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(pulish_location_check_box.isChecked()){
					get_location_dialog();
				}else{
					location_manager_proxy.disable_my_location();
				}
			}
		});
		
		Button bn = (Button)findViewById(R.id.my_location);
		bn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				open_activity(MyLocationActivity.class);
			}
		});
	}
	
	private void process_feed_draft() {
		if(FeedDraftManager.has_feed_draft()){
			show_feed_draft_list_dialog();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		switch (requestCode) {
		case CameraLogic.REQUEST_CODE_CAPTURE:
			add_image_capture_to_feed_captures();
			break;
		case REQUEST_SHOW_IMAGE_ALBUM:
			Uri uri = data.getData();
			String path = get_absolute_imagePath(uri);
			add_image_to_feed_captures(path);
			break;
		case REQUEST_SELECT_COLLECTIONS:
			selected_collections(data);
			break;
		case REQUEST_SELECT_COLLECTIONS_AND_SEND:
			selected_collections_and_send(data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(keyCode == KeyEvent.KEYCODE_BACK){
			feed_title = feed_title_et.getText().toString();
			feed_detail = feed_detail_et.getText().toString();
			boolean is_blank = (
					("".equals(feed_title)) &&
					"".equals(feed_detail) &&
					(capture_paths.size() == 0) &&
					(select_collection_ids == null || select_collection_ids.size() == 0)
					);
			if(feed_draft_id!=0){
				boolean has_change = FeedDraftManager.has_change(
						feed_draft_id,feed_title,feed_detail,capture_paths,select_collection_ids,send_tsina);
				if(!is_blank && has_change){
					save_feed_draft_dialog();
					return true;				
				}
			}else{
				if(!is_blank){
					save_feed_draft_dialog();
					return true;				
				}
			}
	    }  
	    return super.onKeyDown(keyCode, event);  
	} 
	
	private void save_feed_draft_dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("主题尚未发送，是否保存？");
		builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(feed_draft_id != 0){
					FeedDraftManager.update_feed_draft(NewFeedActivity.this,feed_draft_id, 
							feed_title,feed_detail, capture_paths, select_collection_ids,send_tsina);
				}else{
					FeedDraftManager.save_feed_draft(NewFeedActivity.this, 
							feed_title,feed_detail, capture_paths, select_collection_ids,send_tsina);
				}
				NewFeedActivity.this.finish();
			}
		});
		builder.setNeutralButton("不保存", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				NewFeedActivity.this.finish();
			}
		});
		
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		builder.show();
		return;
	}

	private void selected_collections_and_send(Intent data) {
		ArrayList<Integer> ids = data.getIntegerArrayListExtra(SelectCollectionListActivity.EXTRA_NAME_SELECT_COLLECTION_IDS);
		send_tsina = data.getExtras().getBoolean(SelectCollectionListActivity.EXTRA_NAME_SEND_TSINA);
		if(ids!=null && ids.size()!=0){
			select_collections_bn.setText("选择了"+ ids.size() +"收集册");
			select_collection_ids = ids;
			send_feed();
		}
	}
	
	private void send_feed(){
		if(BaseUtils.is_str_blank(feed_title) &&
				BaseUtils.is_str_blank(feed_detail) &&
				capture_paths.size() == 0
				){
			BaseUtils.toast(R.string.feed_valid_blank);
			return;
		}
		
		final int max_count = (capture_paths.size()+1)*100;		
		new MindpinAsyncTask<String, String, Void>(){
			@Override
			public void on_start() {
				super.on_start();
				progress_dialog = new ProgressDialog(NewFeedActivity.this);
				progress_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progress_dialog.setMessage(MindpinApplication.now_sending);
				progress_dialog.setMax(max_count);
				progress_dialog.setProgress(1);
				progress_dialog.show();
			}
			
			public void on_progress_update(String... values) {
				String what = values[0];
				if(what == "change_title"){
					String title = values[1];
					progress_dialog.setTitle(title);
				}else if(what == "sending_feed"){
					String count_str = values[1];
					int count = Integer.parseInt(count_str);
					progress_dialog.setProgress(count);
				}else if(what == "send_feed_success"){
					progress_dialog.dismiss();
					Toast.makeText(getApplicationContext(), "发送成功",
							Toast.LENGTH_SHORT).show();
					NewFeedActivity.this.finish();
				}
			};
			
			@Override
			public boolean on_unknown_exception() {
				super.on_unknown_exception();
				if(feed_draft_id != 0){
					FeedDraftManager.update_feed_draft(NewFeedActivity.this,feed_draft_id, 
							feed_title,feed_detail, capture_paths, select_collection_ids,send_tsina);
				}else{
					FeedDraftManager.save_feed_draft(NewFeedActivity.this, 
							feed_title,feed_detail, capture_paths, select_collection_ids,send_tsina);
				}
				Toast.makeText(getApplicationContext(), "发送失败，已保存草稿",
						Toast.LENGTH_SHORT).show();
				NewFeedActivity.this.finish();
				return false;
			}
			
			@Override
			public Void do_in_background(String... params) throws Exception {
				ArrayList<Integer> photo_ids = new ArrayList<Integer>();
				for (int i = 0; i < capture_paths.size(); i++) {
					String capture_path = capture_paths.get(i);
					final int count = 100*(i+1);
					String title = "正在发送第 "+ (i+1) +" 张图片";
					
					publish_progress("change_title",title);
					
					Timer timer = new Timer();
					TimerTask task = new TimerTask(){
						public void run() {
							int current_count = progress_dialog.getProgress();
							if(current_count < count){
								publish_progress("sending_feed",current_count+5+"");
							}
						}
					};
					timer.schedule(task, 1000, 1000);
					photo_ids.add(HttpApi.upload_photo(capture_path));
					timer.cancel();
					publish_progress("sending_feed",count+"");
				}
				publish_progress("change_title","正在发送文字内容");
				Timer timer = new Timer();
				TimerTask task = new TimerTask(){
					public void run() {
						int current_count = progress_dialog.getProgress();
						if(current_count < max_count-1){
							publish_progress("sending_feed",current_count+5+"");
						}
					}
				};
				timer.schedule(task, 1000, 1000);

				Location send_location = null;
				if(pulish_location_check_box.isChecked()){
					send_location = location_manager_proxy.get_my_location();
				}
				if(photo_ids.size() == 0){
					HttpApi.send_text_feed(feed_title, feed_detail,select_collection_ids,send_tsina,send_location);
				}else{
					HttpApi.send_photo_feed(feed_title, feed_detail, photo_ids, select_collection_ids,send_tsina,send_location);
				} 
				timer.cancel();
				if(feed_draft_id!=0){
					FeedDraft.destroy( feed_draft_id);
					feed_draft_id=0;
				}
				publish_progress("send_feed_success");
				
				return null;
			}

			@Override
			public void on_success(Void v) {
				System.out.println("MindpinAsyncTask send_feed success");
			}
		}.execute();
	}

	private void selected_collections(Intent data) {
		ArrayList<Integer> ids = data.getIntegerArrayListExtra(SelectCollectionListActivity.EXTRA_NAME_SELECT_COLLECTION_IDS);
		send_tsina = data.getExtras().getBoolean(SelectCollectionListActivity.EXTRA_NAME_SEND_TSINA);
		if(ids!=null && ids.size()!=0){
			select_collections_bn.setText("选择了"+ ids.size() +"收集册");
			select_collection_ids = ids;
		}else{
			select_collections_bn.setText(R.string.feed_select_collections);
			select_collection_ids = null;
		}
	}

	private void set_listener() {
		capture_bn = (ImageButton)findViewById(R.id.capture_bn);
		capture_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CameraLogic.call_system_camera(NewFeedActivity.this);
			}
		});
		send_bn = (Button) findViewById(R.id.send_bn);
		send_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				feed_title = feed_title_et.getText().toString();
				feed_detail = feed_detail_et.getText().toString();
				
				if(select_collection_ids == null){
					Intent intent = new Intent(NewFeedActivity.this,SelectCollectionListActivity.class);
					intent.putExtra(SelectCollectionListActivity.EXTRA_NAME_KIND, 
							SelectCollectionListActivity.EXTRA_VALUE_SELECT_FOR_SEND);
					intent.putExtra(SelectCollectionListActivity.EXTRA_NAME_SEND_TSINA, send_tsina);
					startActivityForResult(intent,REQUEST_SELECT_COLLECTIONS_AND_SEND);
				}else{
					send_feed();
				}
			}
		});
		
		album_bn = (ImageButton) findViewById(R.id.album_bn);
		album_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Uri uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
				Intent intent = new Intent("android.intent.action.PICK",
						uri);
				startActivityForResult(intent, REQUEST_SHOW_IMAGE_ALBUM);
			}
		});
		
		select_collections_bn = (Button) findViewById(R.id.select_collections_bn);
		select_collections_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(NewFeedActivity.this,SelectCollectionListActivity.class);
				intent.putExtra(SelectCollectionListActivity.EXTRA_NAME_KIND, 
						SelectCollectionListActivity.EXTRA_VALUE_SELECT_FOR_RESULT);
				if(select_collection_ids != null && select_collection_ids.size() != 0){
					intent.putIntegerArrayListExtra(SelectCollectionListActivity.EXTRA_NAME_SELECT_COLLECTION_IDS, (ArrayList<Integer>)select_collection_ids);
				}
				intent.putExtra(SelectCollectionListActivity.EXTRA_NAME_SEND_TSINA, send_tsina);
				startActivityForResult(intent,REQUEST_SELECT_COLLECTIONS);
			}
		});
		
	}
	
	private void find_views() {
		feed_captures = (LinearLayout)findViewById(R.id.feed_captures);
		feed_captures_parent = (RelativeLayout)findViewById(R.id.feed_captures_parent);
		feed_title_et = (EditText) findViewById(R.id.feed_title_et);
		feed_detail_et = (EditText) findViewById(R.id.feed_content_et);
		
		feed_captures_parent.setVisibility(View.GONE);
	}
	
	private void process_extra() {
		Intent it = getIntent();
		boolean has_image_capture = it.
				getBooleanExtra(CameraLogic.HAS_IMAGE_CAPTURE, false);
		if(has_image_capture){
			add_image_capture_to_feed_captures();
		}
	}
	
	private void process_share(){
		boolean has_share = false;
		Intent it = getIntent();
		if (Intent.ACTION_SEND.equals(it.getAction())) {
			Bundle extras = it.getExtras();
			has_share = true;
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				Uri uri = (Uri) extras.get(Intent.EXTRA_STREAM);
				String path = get_absolute_imagePath(uri);
				add_image_to_feed_captures(path);
			}else if(extras.containsKey(Intent.EXTRA_TEXT)){
				String content = (String)extras.get(Intent.EXTRA_TEXT);
				feed_detail_et.setText(content);
			}
		}
		
		if (Intent.ACTION_SEND_MULTIPLE.equals(it.getAction())) {
			has_share = true;
			ArrayList<Parcelable> uris = it.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
			for (Parcelable parcelable : uris) {
				Uri uri = (Uri)parcelable;
				String path = get_absolute_imagePath(uri);
				add_image_to_feed_captures(path);
			}
		}
		
		if(has_share){
			if(!is_logged_in()){
				alert("请先登录");
			}
		}
	}
	
	private void add_image_capture_to_feed_captures(){
		String path = CameraLogic.IMAGE_CAPTURE_TEMP_PATH.getPath();
		add_image_to_feed_captures(path);
	}
	
	private void add_image_to_feed_captures(String file_path){
		feed_captures_parent.setVisibility(View.VISIBLE);
		capture_paths.add(file_path);
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 8;
		Bitmap b = BitmapFactory.decodeFile(file_path, options);
		
		ImageView img = new ImageView(this);
		img.setAdjustViewBounds(true);
		img.setScaleType(ScaleType.CENTER_CROP);
		LayoutParams lp = new LayoutParams(BaseUtils.dp_to_px(48),
				BaseUtils.dp_to_px(48));
		lp.topMargin = BaseUtils.dp_to_px(5);
		lp.leftMargin = BaseUtils.dp_to_px(4);
		lp.bottomMargin = BaseUtils.dp_to_px(4);
		img.setLayoutParams(lp);
		img.setImageBitmap(b);
		img.setClickable(true);
		img.setTag(file_path);
		img.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				show_image_dialog((String)v.getTag());
			}
		});
		feed_captures.addView(img);
	}

	private String get_absolute_imagePath(Uri uri) 
	   {
	       String [] proj={MediaStore.Images.Media.DATA};
	       Cursor cursor = managedQuery( uri,proj,        
	                       null,null,null); 
	       int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	       cursor.moveToFirst();
	       return cursor.getString(column_index);
	   }
	
	private void alert(String content){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(content);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				NewFeedActivity.this.finish();
			}
		});
		builder.show();
	}
	
	//构建打开草稿dialog
	private void show_feed_draft_list_dialog() {
		LayoutInflater factory = LayoutInflater
				.from(this);
		final View view = factory.inflate(R.layout.feed_draft_list_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("打开草稿");
		builder.setView(view);
		builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Integer id = (Integer)view.getTag();
				if(id == null) return;
				open_feed_draft(id);
			}
		});
		builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Integer id = (Integer)view.getTag();
				if(id == null) return;
				
				FeedDraft.destroy(id);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		final AlertDialog dialog = builder.create();
		
		RadioGroup feed_drafts_rg = (RadioGroup)view.findViewById(R.id.feed_drafts_rg);
		ArrayList<FeedDraft> feed_drafts = FeedDraftManager.get_feed_drafts();
		for (FeedDraft feedDraft : feed_drafts) {
			RadioButton rb = (RadioButton)factory.inflate(R.layout.feed_draft_radio_button, null);
			
//			RadioButton rb = new RadioButton(view.getContext());
			String title = feedDraft.title;
			if(title == null || "".equals(title)) title = "无标题";
			String time_str = BaseUtils.date_string(feedDraft.time);
			title = title + "(" + time_str +")";
			rb.setTag(feedDraft.id);
			rb.setText(title);
			feed_drafts_rg.addView(rb);
		}
		feed_drafts_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb = (RadioButton)view.findViewById(checkedId);
				Integer id = (Integer)rb.getTag();
				view.setTag(id);
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
				dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);				
			}
		});
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
	}
	
	private void open_feed_draft(Integer id) {
		feed_draft_id = id;
		FeedDraft fd = FeedDraft.find(id);
		if(fd == null)return;
		
		feed_title_et.setText(fd.title);
		feed_detail_et.setText(fd.content);
		
		List<String> paths = BaseUtils.string_to_string_list(fd.image_paths);
		for (String path : paths) {
			add_image_to_feed_captures(path);
		}
		
		List<Integer> ids = BaseUtils.string_to_integer_list(fd.select_collection_ids);
		if(ids!=null && ids.size()!=0){
			select_collections_bn.setText("选择了"+ ids.size() +"收集册");
			select_collection_ids = ids;
		}
		send_tsina = fd.send_tsina;
	}
	
	private void show_image_dialog(String image_path){
		LayoutInflater factory = LayoutInflater
				.from(this);
		final View view = factory.inflate(R.layout.show_image_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ImageView iv = (ImageView)view.findViewById(R.id.image_dialog_image_iv);
		Bitmap b = BitmapFactory.decodeFile(image_path);
		iv.setImageBitmap(b);
		builder.setTitle("查看图片");
		builder.setView(view);
		final String path = image_path+"";
		builder.setPositiveButton("移除", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int index = capture_paths.indexOf(path);
				ImageView image = (ImageView) feed_captures.getChildAt(index);
				feed_captures.removeView(image);
				capture_paths.remove(path);
				if(capture_paths.size() == 0){
					feed_captures_parent.setVisibility(View.GONE);
				}
			}
		});
		builder.setNeutralButton("查看", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(NewFeedActivity.this,showImageCaptureActivity.class);
				intent.putExtra(showImageCaptureActivity.EXTRA_NAME_IMAGE_CAPTURE_PATH, path);
				startActivityForResult(intent,REQUEST_SHOW_IMAGE_CAPTURE);
			}
		});
		builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}
	

	private void get_location_dialog() {
		new MindpinAsyncTask<Void, Void, Void>(this, "正在获取地理位置"){
			public void on_start() {
				location_manager_proxy = new LocationManagerProxy();
				location_manager_proxy.enable_my_location();
			};
			
			@Override
			public Void do_in_background(Void... params) throws Exception {
				System.out.println("get_location_dialog..background start");
				while(location_manager_proxy.get_my_location() == null){
				}
				location_manager_proxy.disable_my_location();
				System.out.println("get_location_dialog..background end");
				return null;
			}
			
			@Override
			public void on_success(Void v) {
			}
		}.execute();
	}
	
}
