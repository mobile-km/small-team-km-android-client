package com.teamkn.activity.usermsg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;

public class UserMsgAvatarSetActivity extends TeamknBaseActivity{
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

		Bitmap bitmap = BitmapFactory.decodeFile(image_path);
		iv_show_avatar.setImageBitmap(bitmap);	
		
	}
	public void click_set_avatar_update(View view){
		new TeamknAsyncTask<Void, Void, Integer>(UserMsgAvatarSetActivity.this,"信息提交") {
			@Override
			public Integer do_in_background(Void... params) throws Exception {
				if(BaseUtils.is_wifi_active(UserMsgAvatarSetActivity.this)){
			    	HttpApi.user_set_avatar(image_path);
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
