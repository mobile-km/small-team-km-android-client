package com.teamkn.activity.usermsg;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.FileDirs;

public class UserMsgAvatarSetActivity extends TeamknBaseActivity{
	private File image_file = null;
	public static String requestError = null;
	public class RequestCode{
	    public final static String IMAGE_PATH = "image_path";
	}
	private ImageView iv_show_avatar;
	private String image_path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_msg_set_avatar);
		iv_show_avatar = (ImageView)findViewById(R.id.iv_show_avatar);
		
		Intent intent = getIntent();
		image_path = intent.getStringExtra(UserMsgAvatarSetActivity.RequestCode.IMAGE_PATH);
		
		startPhotoZoom(Uri.parse(image_path));
	}
	
	 public void startPhotoZoom(Uri uri) {   
		 System.out.println(uri.getPath() + " :  " + image_path);
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
         startActivityForResult(intent, 1);  
     }  
     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
		case 1:
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

		default:
			break;
		}
    	super.onActivityResult(requestCode, resultCode, data);
    }
     /**  
      * 保存裁剪之后的图片数据  
      * @param picdata  
      */  
     private void setPicToView(Intent picdata) {  
         Bundle extras = picdata.getExtras();  
         System.out.println(extras.get("data"));
         if (extras != null) {  
             Bitmap photo = extras.getParcelable("data");  
             Drawable drawable = new BitmapDrawable(photo);  
             iv_show_avatar.setBackgroundDrawable(drawable); 
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             photo.compress(CompressFormat.PNG, 100,os);
         	 byte[] bytes = os.toByteArray();
             image_file = FileDirs.getFileFromBytes(bytes, "image_file");
         }  
     }  
	
	
	
	
	
	public void click_set_avatar_update(View view){
		new TeamknAsyncTask<Void, Void, Integer>(UserMsgAvatarSetActivity.this,"信息提交") {
			@Override
			public Integer do_in_background(Void... params) throws Exception {
				if(BaseUtils.is_wifi_active(UserMsgAvatarSetActivity.this)){
			    	HttpApi.user_set_avatar(image_file);
			    }
			    return 1;
			}
			@Override
			public void on_success(Integer client_chat_node_id) {
				if(requestError!=null){
					Toast.makeText(UserMsgAvatarSetActivity.this, requestError, Toast.LENGTH_LONG).show();
				}
				open_activity(UserMsgActivity.class);
				finish();
		    }
       }.execute();
	}
}
