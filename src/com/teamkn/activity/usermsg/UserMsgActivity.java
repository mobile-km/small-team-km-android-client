package com.teamkn.activity.usermsg;

import java.io.ByteArrayInputStream;
import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.note.EditNoteActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.AccountUser;

public class UserMsgActivity extends TeamknBaseActivity{
	public class RequestCode{
	    public final static int NEW_TEXT = 0;
	    public final static int FROM_ALBUM = 1;
	    public final static int FROM_CAMERA = 2;
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
					intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
				    intent.setType("image/*");
				    startActivityForResult(intent,UserMsgActivity.RequestCode.FROM_ALBUM);
					break;
				case 1:
					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, UserMsgActivity.RequestCode.FROM_CAMERA);
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
			    String image_path = BaseUtils.get_file_path_from_image_uri(data.getData());
			    start_edit_note_activity_by_image_path(image_path);
			    break;
		  case UserMsgActivity.RequestCode.FROM_CAMERA:
			    Uri uri = data.getData();
			    String scheme = uri.getScheme();
			    System.out.println("uri  :  scheme  --  " + uri + " : " + scheme);
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
	    Intent intent = new Intent(UserMsgActivity.this, UserMsgAvatarSetActivity.class);
	    intent.putExtra(UserMsgAvatarSetActivity.RequestCode.IMAGE_PATH, image_path);
	    startActivity(intent);
	}
}
