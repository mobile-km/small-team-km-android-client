package com.teamkn.activity.usermsg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.social_circle.UserPublicDataListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.CameraLogic;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.AccountUser;

public class UserMsgActivity extends TeamknBaseActivity{
	Integer service_user_id ; 
	AccountUser user ;
	public class RequestCode{
	    public final static char FOLLOW = 'a';
	    public final static char UNFOLLOW = 'b';
	    public final static char NOFOLLOW = 'c';
	}
    ImageView iv_user_avatar;
    TextView  tv_user_name;
    LinearLayout follow_tv_ll ;
    Button  follow_tv;
    
    Button public_list_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.user_manager);
		setContentView(R.layout.user_msg);
		Intent intent = getIntent();
		service_user_id = intent.getIntExtra("service_user_id", -1);
		load_ui();	
		load_httpApi(RequestCode.NOFOLLOW,service_user_id);
	}
	private void load_httpApi(final char is_load_follow, final int service_user_id){
		new TeamknAsyncTask<Void, Void, AccountUser>(UserMsgActivity.this,"正在加载") {
			@Override
			public AccountUser do_in_background(Void... params) throws Exception {
				if(BaseUtils.is_wifi_active(UserMsgActivity.this)){
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
						user = HttpApi.get_user_msg(service_user_id);
						break;
					default:
						break;
					}
			    }
				return user;
			}
			@Override
			public void on_success(AccountUser user) {
				set_ui();
		    }
       }.execute();
	}
	private void set_ui(){
		// 设置用户头像和名字
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
					 load_httpApi(RequestCode.UNFOLLOW,service_user_id);
			     }else{
			    	 load_httpApi(RequestCode.FOLLOW,service_user_id); 
			     }
			}
		});  
	    public_list_tv.setText(user.name + "的公开列表");
	}
	private void load_ui(){
		iv_user_avatar = (ImageView)findViewById(R.id.iv_user_avatar);
		tv_user_name = (TextView)findViewById(R.id.tv_user_name);
		follow_tv_ll = (LinearLayout)findViewById(R.id.follow_tv_ll);
		follow_tv = (Button)findViewById(R.id.follow_tv);
		public_list_tv = (Button)findViewById(R.id.public_list_tv);
	}
	public void click_user_public_data_list(View view){
		Intent intent = new Intent(UserMsgActivity.this,UserPublicDataListActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
//		open_activity(UserPublicDataL	istActivity.class);
	}
}
