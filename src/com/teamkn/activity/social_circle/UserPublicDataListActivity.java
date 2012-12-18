package com.teamkn.activity.social_circle;

import java.io.ByteArrayInputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataListHelper;
import com.teamkn.widget.adapter.UserPublicDataListAdapter;

public class UserPublicDataListActivity extends TeamknBaseActivity{
	public static class RequestCode{
		public final static char FOLLOW = 'a';
	    public final static char UNFOLLOW = 'b';
	    public final static char NOFOLLOW = 'c';
		
		public final static String COLLECTION = "COLLECTION";
		public final static String STEP = "STEP";
		public final static String ALL = "ALL";
		
		public static String data_list_type = ALL;
		
		public final static int SHOW_BACK = 9;
	}
	TextView user_name_tv;
	LinearLayout follow_tv_ll ;
    Button  follow_tv;
	ImageView iv_user_avatar;
    TextView  tv_user_name;
    ListView list_view;
	/*
     * cursor imageview 页卡头标
     * */
	private static ImageView cursor;// 动画图片
	private static int offset = 0;// 动画图片偏移量
	private static int currIndex = 0;// 当前页卡编号
	private static int bmpW;// 动画图片宽度
	
	AccountUser user ;
	public List<DataList> datalists;
	public List<DataList> record_datalists; 
	UserPublicDataListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_public_data_list_list);
		Intent intent = getIntent();
		user = (AccountUser) intent.getSerializableExtra("user");
		InitImageView();
		load_ui();
		load_mimsg();
		load_httpApi(RequestCode.NOFOLLOW,user.user_id);
		
	}
	private void load_ui() {
		user_name_tv = (TextView)findViewById(R.id.user_name_tv);
		follow_tv_ll = (LinearLayout)findViewById(R.id.follow_tv_ll);
		follow_tv = (Button)findViewById(R.id.follow_tv);
	    iv_user_avatar = (ImageView)findViewById(R.id.iv_user_avatar);
		tv_user_name = (TextView)findViewById(R.id.tv_user_name);
		list_view = (ListView)findViewById(R.id.list_view);
	}
	private void load_mimsg(){	
		list_view.setVisibility(View.GONE);
		if(user.avatar != null){
		      Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(user.avatar));
		      Drawable drawable = new BitmapDrawable(bitmap);
		      iv_user_avatar.setBackgroundDrawable(drawable);
	    }else{
	    	  iv_user_avatar.setBackgroundResource(R.drawable.user_default_avatar_normal);
	    }
	    tv_user_name.setText(user.name);
	    
	    if(user.user_id == current_user().user_id){
	    	follow_tv.setVisibility(View.GONE);
	    }
	    if(user.followed){
	    	follow_tv.setText("已Follow");
	    	follow_tv_ll.setBackgroundColor(getResources().getColor(R.color.green));
	    }else{
	    	follow_tv.setText("未Follow");
	    	follow_tv_ll.setBackgroundColor(getResources().getColor(R.color.yellow));
	    }
	    follow_tv.setOnClickListener(new android.view.View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				 if(user.followed){
					 load_httpApi(RequestCode.UNFOLLOW,user.user_id);
			     }else{
			    	 load_httpApi(RequestCode.FOLLOW,user.user_id); 
			     }
			}
		});  
	    user_name_tv.setText(user.name + "的公开列表");
	}
	private void load_httpApi(final char is_load_follow, final int service_user_id){
		if (BaseUtils.is_wifi_active(UserPublicDataListActivity.this)) {
	    	new TeamknAsyncTask<Void, Void, List<DataList>>(UserPublicDataListActivity.this,"内容加载中") {
				@Override
				public List<DataList> do_in_background(Void... params)
						throws Exception {
					
					switch (is_load_follow) {
					case RequestCode.FOLLOW:
						user.setFollowed(true);
						HttpApi.follow_or_unfollow(service_user_id,true);
						break;
					case RequestCode.UNFOLLOW:
						user.setFollowed(false);
						HttpApi.follow_or_unfollow(service_user_id,false);
						break;
					case RequestCode.NOFOLLOW:
						record_datalists = HttpApi.DataList.user_public_data_lists(1,100 , user.user_id);	
						break;
					default:
						break;
					}
					return null;	
				}
				@Override
				public void on_success(List<DataList> datalists) {
					if(is_load_follow == RequestCode.NOFOLLOW){
						load_list();
					}else{
						load_mimsg();
						load_list();
					}
					
				}
			}.execute();
    	}else{
			BaseUtils.toast("无法连接到网络，请检查网络配置");
		}
	}
	private void load_list(){
		// 导航页签引用
		request_pageselected();
		datalists = DataListHelper.by_type(record_datalists, RequestCode.data_list_type);
		list_view.setVisibility(View.VISIBLE);
		adapter = new UserPublicDataListAdapter(this);
		adapter.add_items(datalists);
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				TextView info_tv = (TextView) list_item.findViewById(R.id.info_tv);
				DataList item = (DataList) info_tv.getTag(R.id.tag_note_uuid);
				Intent intent = new Intent(UserPublicDataListActivity.this,DataItemListActivity.class);
				intent.putExtra("data_list",item);
				intent.putExtra("data_list_public", MainActivity.RequestCode.我的列表);
				System.out.println(RequestCode.data_list_type + " mainactivity setonclick  = " +item.toString());
				
				if(item.is_removed.equals("true")){
					showDialog(item,item_id);
				}else{
					startActivityForResult(intent, RequestCode.SHOW_BACK);
				}
			}
		});
	}
	private void showDialog(final DataList dataList,final int id ){
		AlertDialog.Builder builder = new Builder(UserPublicDataListActivity.this);
		builder.setTitle("注意");
		builder.setMessage("此列表已经被原作者删除，是否删除该记录？");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				DataListDBHelper.remove_by_server_id(dataList);
//				dataListAdapter.remove_item(dataList);
//				dataListAdapter.notifyDataSetChanged();
				remove_data_list(dataList,id,true);
			}
		});
		builder.show();
	}
	private void remove_data_list(final DataList dataList,final int data_list_id,final boolean is_delete_watch){
    	if(BaseUtils.is_wifi_active(this)){
    		new TeamknAsyncTask<Void, Void, Boolean>(UserPublicDataListActivity.this,getResources().getString(R.string.now_deleting)) {
				@Override
				public Boolean do_in_background(Void... params)
						throws Exception {
					if(is_delete_watch){
						HttpApi.WatchList.watch(dataList, false);
					}else{
						HttpApi.DataList.remove(dataList);
					}
					return true;
				}
				@Override
				public void on_success(Boolean result) {
					datalists.remove(data_list_id);
					adapter.remove_item(dataList);
					adapter.notifyDataSetChanged();
					BaseUtils.toast("删除成功");
				}
			}.execute();
    	}else{
    		BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
    	}
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// ---------------------------------------------------------------------
	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.line)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}
	public void click_step_button(View view) {
		RequestCode.data_list_type = RequestCode.STEP;
		load_list();
	}

	public void click_collection_button(View view) {
		RequestCode.data_list_type = RequestCode.COLLECTION;
		load_list();
	}

	public void click_all_button(View view) {
		RequestCode.data_list_type = RequestCode.ALL;
		load_list();
	}
	private void request_pageselected(){
		int index = 0;
		if(RequestCode.data_list_type.equals(RequestCode.STEP)){
			index = 2 ;
		}else if(RequestCode.data_list_type.equals(RequestCode.ALL)){
			index = 0 ;
		}else if(RequestCode.data_list_type.equals(RequestCode.COLLECTION)) {
			index = 1 ;
		}
		MyOnPageChangeListener.onPageSelected(index);
	}
	/**
	 * 页卡切换监听
	 */
	static class MyOnPageChangeListener{
		static int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		static int two = one * 2;// 页卡1 -> 页卡3 偏移量
		public static void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			try {
				animation.setFillAfter(true);// True:图片停在动画结束位置
				animation.setDuration(300);
				cursor.startAnimation(animation);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}
}
