package com.teamkn.activity.usermsg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.CameraLogic;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.AccountUser;

public class UserMsgActivity extends TeamknBaseActivity{
	public static String requestError = null;
	public class RequestCode{
	    public final static int NEW_TEXT = 0;
	    public final static int FROM_ALBUM = 3;
	    public final static int FROM_CAMERA = 4;
	}
    ImageView iv_user_avatar;
    TextView  tv_user_name;
    
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
				    startActivityForResult(intent,UserMsgActivity.RequestCode.FROM_ALBUM);
					break;
				case 1:
                    CameraLogic.call_system_camera(UserMsgActivity.this,UserMsgActivity.RequestCode.FROM_CAMERA);        
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
    	Intent intent = new Intent(UserMsgActivity.this,UserMsgNameSetActivity.class);
    	startActivity(intent);
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_msg);
		load_ui();	
		
		// 设置用户头像和名字
	    AccountUser user = current_user();
	    if(user.avatar != null){
	      Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(user.avatar));
	      Drawable drawable = new BitmapDrawable(bitmap);
	      iv_user_avatar.setBackgroundDrawable(drawable);
	    }else{
	      iv_user_avatar.setBackgroundResource(R.drawable.user_default_avatar_normal);
	    }
	    tv_user_name.setText(user.name);		
	}
	private void load_ui(){
		iv_user_avatar = (ImageView)findViewById(R.id.iv_user_avatar);
		tv_user_name = (TextView)findViewById(R.id.tv_user_name);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		switch(requestCode){
		  case UserMsgActivity.RequestCode.FROM_ALBUM:
			    System.out.println(" userMsgActivity "+ data.getData().getPath());
			    startPhotoZoom(data.getData());  
			    break;
		  case UserMsgActivity.RequestCode.FROM_CAMERA:
                new TeamknAsyncTask<Void, Void, Void>(UserMsgActivity.this,"请稍等") {
					@Override
					public Void do_in_background(Void... params)
							throws Exception {
						String file_path = CameraLogic.IMAGE_CAPTURE_TEMP_FILE.getAbsolutePath();
						Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), file_path, null, null));
						startPhotoZoom(uri); 
						return null;
					}

					@Override
					public void on_success(Void result) {}
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
            Drawable drawable = new BitmapDrawable(photo);  
//            iv_user_avatar.setBackgroundDrawable(drawable); 
            
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            photo.compress(CompressFormat.PNG, 100,os);
            byte[] bytes = os.toByteArray();
            final File image_file = FileDirs.getFileFromBytes(bytes,
            		Environment .getExternalStorageDirectory()+"/"+current_user().user_id+".png" );
            System.out.println("sdcard/  " +Environment .getExternalStorageDirectory()+"/"+current_user().user_id+".png");
            new TeamknAsyncTask<Void, Void, Integer>(UserMsgActivity.this,"信息提交") {
    			@Override
    			public Integer do_in_background(Void... params) throws Exception {
    				if(BaseUtils.is_wifi_active(UserMsgActivity.this)){
    			    	HttpApi.user_set_avatar(image_file);
    			    }
    			    return 1;
    			}
    			@Override
    			public void on_success(Integer client_chat_node_id) {
    				if(requestError!=null){
    					Toast.makeText(UserMsgActivity.this, requestError, Toast.LENGTH_LONG).show();
    				}
    				open_activity(UserMsgActivity.class);
    				finish();
    		    }
           }.execute();
            
        }
	
    }
	
}
