package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.social_circle.SocialCircleActivity;
import com.teamkn.activity.usermsg.UserMsgActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.AccountUser;

public class UserAdapter extends TeamknBaseAdapter<AccountUser>{
	Activity activity;
	public UserAdapter(TeamknBaseActivity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	public View inflate_view() {
		return inflate(R.layout.list_user_item, null);
	}

	@Override
	public com.teamkn.base.adapter.TeamknBaseAdapter.BaseViewHolder build_view_holder(
			View view) {
		ViewHolder view_holder      = new ViewHolder();
        view_holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
        
        view_holder.user_avatar_iv = (ImageView)view.findViewById(R.id.user_avatar_iv);
        view_holder.user_name_tv = (TextView)view.findViewById(R.id.user_name_tv);
        view_holder.user_follow_bt = (Button) view.findViewById(R.id.user_follow_bt);
		return view_holder;
	}

	@Override
	public void fill_with_data(
			com.teamkn.base.adapter.TeamknBaseAdapter.BaseViewHolder holder,
			final AccountUser item, int position) {
		final ViewHolder view_holder = (ViewHolder) holder;
        view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
        
        view_holder.user_name_tv.setText(item.name);
    	if (item.avatar!=null) {
    		Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(item.avatar));
        	Drawable drawable = new BitmapDrawable(bitmap);
    		view_holder.user_avatar_iv.setBackgroundDrawable(drawable);
        } else {
        	view_holder.user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
        }
    	view_holder.user_avatar_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity,UserMsgActivity.class);
				intent.putExtra("service_user_id", item.user_id);
				activity.startActivity(intent);
			}
		});
    	if(item.followed){
    		view_holder.user_follow_bt.setText("取消Follow");
    	}else{
    		view_holder.user_follow_bt.setText("Follow");
    	}
    	view_holder.user_follow_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (BaseUtils.is_wifi_active(activity)) {
					new TeamknAsyncTask<Void, Void, Void>() {
						@Override
						public Void do_in_background(Void... params)
								throws Exception {
							if(item.followed){
								HttpApi.follow_or_unfollow(item.user_id,false);
					    	}else{
					    		HttpApi.follow_or_unfollow(item.user_id,true);
					    	}
							return null;
						}
						@Override
						public void on_success(Void result) {	
							
							if(SocialCircleActivity.RequestCode.social_type .equals(SocialCircleActivity.RequestCode.VERMICELLI)){
								SocialCircleActivity.adapter.notifyDataSetChanged();
								view_holder.user_follow_bt.post(new Runnable() {
									@Override
									public void run() {
										if(!item.followed){
								    		view_holder.user_follow_bt.setText("Follow");
								    	}else{
								    		view_holder.user_follow_bt.setText("取消Follow");
								    	}
									}
								});
								item.setFollowed(!item.followed);
							}else if((SocialCircleActivity.RequestCode.social_type .equals(SocialCircleActivity.RequestCode.FOLLOW))){
								SocialCircleActivity.adapter.remove_item(item);
								SocialCircleActivity.adapter.notifyDataSetChanged();
							}
						}
					}.execute();
		    	}else{
					BaseUtils.toast("无法连接到网络，请检查网络配置");
				}
			}
		});
        
	}
	private class ViewHolder implements BaseViewHolder {
    	TextView info_tv;
    	// 个人列表子项显示
    	ImageView user_avatar_iv;
        TextView user_name_tv;   
        Button user_follow_bt;     
	}
}
