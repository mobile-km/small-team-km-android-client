package com.teamkn.activity.social_circle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.slidingmenu.TeamknSlidingMenuActivity;
import com.teamkn.activity.usermsg.SearchUserActivity;
import com.teamkn.activity.usermsg.UserManagerActivity;
import com.teamkn.activity.usermsg.UserMsgNameSetActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.CameraLogic;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataList;
import com.teamkn.widget.adapter.UserAdapter;

public class SocialCircleActivity extends TeamknSlidingMenuActivity{
	LayoutInflater inflater;
    View show_view;  //显示的View
    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;
	
//	View view_show;
	public static class RequestCode{
		public static String social_type = RequestCode.MIMSG;
		public final static String MIMSG = "MIMSG";
		public final static String FOLLOW = "FOLLOW";
		public final static String VERMICELLI = "VERMICELLI";
		
	}
	/*
	 * 收集，步骤，所有
	 */
//	Button click_collection_button, click_step_button, click_all_button;
    /*
     * cursor imageview 页卡头标
     * */
	private static ImageView cursor;// 动画图片
	private static int offset = 0;// 动画图片偏移量
	private static int currIndex = 0;// 当前页卡编号
	private static int bmpW;// 动画图片宽度
	
	/*
	 * user_msg 我的信息
	 * */
	 AccountUser user ;
	 ScrollView user_msg;
	 ImageView iv_user_avatar;
	 TextView  tv_user_name;
	 Button public_list_tv;
	/*
	 * FOLLOW  粉丝
	 * */
	public static UserAdapter adapter ;
	List<AccountUser> users;
	ListView list_view;
	
	Uri uri; //头像
    File image_file;
    public static String requestError = null;
    
    
	public void click_set_user_avatar(View view){
    	new AlertDialog.Builder(this)
    	.setTitle("设置头像")
    	.setItems(new String[] { "选择本地图片", "拍照" }, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("设置头像  " + which);
				Intent intent;
				switch (which) {
				case 0:
					intent = new Intent(Intent.ACTION_PICK, null);  
					intent.setDataAndType(  
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
                            "image/*");  
				    startActivityForResult(intent,UserManagerActivity.RequestCode.FROM_ALBUM);
					break;
				case 1:
                    CameraLogic.call_system_camera(SocialCircleActivity.this,UserManagerActivity.RequestCode.FROM_CAMERA);        
                    break;
				default:
					break;
				}
			}
		})
    	.setNegativeButton("取消", null)
    	.show();
    }
    public void click_set_user_name(View view){
    	Intent intent = new Intent(SocialCircleActivity.this,UserMsgNameSetActivity.class);
    	startActivity(intent);
    } 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
        setView(); 		
	}
	private  void setView(){
		show_view = init_sliding_menu(R.layout.base_social_circle);
        
		InitImageView();
		load_ui();
		RequestCode.social_type =  RequestCode.MIMSG;
		load_usermsg_or_list_httpApi(RequestCode.social_type);
		
    }
	private void load_ui() {
		user_msg = (ScrollView)show_view.findViewById(R.id.user_msg);
	    iv_user_avatar = (ImageView)show_view.findViewById(R.id.iv_user_avatar);
		tv_user_name = (TextView)show_view.findViewById(R.id.tv_user_name);
		list_view = (ListView)show_view.findViewById(R.id.list_view);
		public_list_tv = (Button)show_view.findViewById(R.id.public_list_tv);
	}
	private void load_usermsg_or_list_httpApi(final String social_type){
    	if (BaseUtils.is_wifi_active(SocialCircleActivity.this)) {
	    	new TeamknAsyncTask<Void, Void, List<DataList>>(SocialCircleActivity.this,"内容加载中") {
				@Override
				public List<DataList> do_in_background(Void... params)
						throws Exception {
						if(social_type.equals(RequestCode.MIMSG)){
							user = HttpApi.get_user_msg(current_user().user_id);
						}else if(social_type.equals(RequestCode.VERMICELLI)){
							users = HttpApi.follows_or_fans(false,current_user().user_id, 1, 100);
						}else if(social_type.equals(RequestCode.FOLLOW)){
							users = HttpApi.follows_or_fans(true,current_user().user_id, 1, 100);
						}
						return null;
				}
				@Override
				public void on_success(List<DataList> datalists) {
					load_mimsg_or_list();
				}
			}.execute();
    	}else{
			BaseUtils.toast("无法连接到网络，请检查网络配置");
		}
    }
	private void load_mimsg_or_list(){
		// 导航页签引用
		request_pageselected();	
		if(RequestCode.social_type.equals(RequestCode.MIMSG)){
			load_mimsg();
		}else{
			load_list();
		}
	}
	private void load_mimsg(){
		list_view.setVisibility(View.GONE);
		user_msg.setVisibility(View.VISIBLE);
		if(user.avatar != null){
		      Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(user.avatar));
		      Drawable drawable = new BitmapDrawable(bitmap);
		      iv_user_avatar.setBackgroundDrawable(drawable);
	    }else{
	    	  iv_user_avatar.setBackgroundResource(R.drawable.user_default_avatar_normal);
	    }
	    tv_user_name.setText(user.name);
	    public_list_tv.setText(user.name + "的公开列表");
	}
	private void load_list(){
		
		list_view.setVisibility(View.VISIBLE);
		user_msg.setVisibility(View.GONE);
		adapter = new UserAdapter(this);
		adapter.add_items(users);
		list_view.setAdapter(adapter);
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
	public void click_follow_button(View view) {
		RequestCode.social_type = RequestCode.FOLLOW;
		load_usermsg_or_list_httpApi(RequestCode.social_type);
	}

	public void click_vermicelli_button(View view) {
		RequestCode.social_type = RequestCode.VERMICELLI;
		load_usermsg_or_list_httpApi(RequestCode.social_type);
	}

	public void click_mimsg_button(View view) {
		RequestCode.social_type = RequestCode.MIMSG;
		load_usermsg_or_list_httpApi(RequestCode.social_type);
	}
	public void click_user_public_data_list(View view){
		Intent intent = new Intent(SocialCircleActivity.this,UserPublicDataListActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
	}
	public void click_add_follow_user_ib(View view){
		open_activity(SearchUserActivity.class);
	}
	private void request_pageselected(){
		int index = 0;
		if(RequestCode.social_type.equals(RequestCode.VERMICELLI)){
			index = 2 ;
		}else if(RequestCode.social_type.equals(RequestCode.MIMSG)){
			index = 0 ;
		}else if(RequestCode.social_type.equals(RequestCode.FOLLOW)) {
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
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		switch(requestCode){
		  case UserManagerActivity.RequestCode.FROM_ALBUM:
			    System.out.println(" userManagerActivity "+ data.getData().getPath());
			    startPhotoZoom(data.getData());  
			    break;
		  case UserManagerActivity.RequestCode.FROM_CAMERA:
                new TeamknAsyncTask<Void, Void, Void>(SocialCircleActivity.this,"请稍等") {
					@Override
					public Void do_in_background(Void... params)
							throws Exception {
						String file_path = CameraLogic.IMAGE_CAPTURE_TEMP_FILE.getAbsolutePath();
						try {
							uri= Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), file_path, null, null));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						return null;
					}
					@Override
					public void on_success(Void result) {
						startPhotoZoom(uri); 
					}
				}.execute();       
            break;

		  case 9:  
              /**  
               * 非空判断大家一定要验证，如果不验证的话，  
               * 在剪裁之后如果发现不满意，要重新裁剪，丢弃  
               * 当前功能时，会报NullException，小马只  
               * 在这个地方加下，大家可以根据不同情况在合适的  
               * 地方做判断处理类似情况  
               *   
               */ 
              if(data != null){  
                  setPicToView(data);  
              }  
              break; 
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    /**  
     * 裁剪图片方法实现  
     * @param uri  
     */ 
    public void startPhotoZoom( Uri uri) {  

			    System.out.println(uri.getPath());
				Intent intent = new Intent("com.android.camera.action.CROP");  
		        intent.setDataAndType(uri, "image/*");  
		        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪  
		        intent.putExtra("crop", "true");  
		        // aspectX aspectY 是宽高的比例  
		        intent.putExtra("aspectX", 1);  
		        intent.putExtra("aspectY", 1);  
		        // outputX outputY 是裁剪图片宽高  
		        intent.putExtra("outputX", 150);  
		        intent.putExtra("outputY", 150);  
		        intent.putExtra("return-data", true);  
		        startActivityForResult(intent, 9);  
    }   
      
    /**  
     * 保存裁剪之后的图片数据  
     * @param picdata  
     */  
    private void setPicToView(Intent picdata) {  
        Bundle extras = picdata.getExtras();  
        if (extras != null) {  
            Bitmap photo = extras.getParcelable("data");  
//            Drawable drawable = new BitmapDrawable(photo);  
//            iv_user_avatar.setBackgroundDrawable(drawable); 
            
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            photo.compress(CompressFormat.PNG, 100,os);
            byte[] bytes = os.toByteArray();
//            final File image_file = FileDirs.getFileFromBytes(bytes,
//            		Environment .getExternalStorageDirectory()+"/"+current_user().user_id+".jpg" );
            image_file = FileDirs.getFileFromBytes(bytes,FileDirs.TEAMKN_CAPTURE_TEMP_DIR+  "/IMG_TEMP.jpg");
            new TeamknAsyncTask<Void, Void, Integer>(SocialCircleActivity.this,"信息提交") {
    			@Override
    			public Integer do_in_background(Void... params) throws Exception {
    				if(BaseUtils.is_wifi_active(SocialCircleActivity.this)){
    			    	HttpApi.user_set_avatar(image_file);
    			    }
    			    return 1;
    			}
    			@Override
    			public void on_success(Integer client_chat_node_id) {
    				if(requestError!=null){
    					Toast.makeText(SocialCircleActivity.this, requestError, Toast.LENGTH_LONG).show();
    				}
    				open_activity(UserManagerActivity.class);
    				System.out.println("image_file  = " + image_file.getPath());
    				finish();
    		    }
           }.execute();     
        }
	
    }
}
