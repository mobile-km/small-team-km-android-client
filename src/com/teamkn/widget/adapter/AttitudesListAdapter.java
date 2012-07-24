package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.Attitudes;
import com.teamkn.model.User;
import com.teamkn.model.database.AttitudesDBHelper.Kind;
import com.teamkn.model.database.UserDBHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AttitudesListAdapter extends TeamknBaseAdapter<Attitudes>{
    Context context;
	public AttitudesListAdapter(TeamknBaseActivity activity) {
		super(activity);
		this.context = activity;
	}

	@Override
	public View inflate_view() {
		return inflate(R.layout.list_chat_node_item_item, null);
	}

	@Override
	public BaseViewHolder build_view_holder(
			View view) {
		 ViewHolder view_holder= new ViewHolder();   
		 view_holder.chat_id_user_avatar_iv = (ImageView)  view.findViewById(R.id.imageview_avatar);
		 view_holder.chat_id_user_comment_tv = (TextView)  view.findViewById(R.id.textview_content);  
		return view_holder;
	}

	@Override
	public void fill_with_data(BaseViewHolder holder,Attitudes item, int position) {
		 ViewHolder view_holder = (ViewHolder) holder;
		 int user_id = item.client_user_id;
		 User user = UserDBHelper.find(user_id); 
		 if(user!=null && !user.equals(null)){
			 if(user.user_avatar!=null){
				 Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(user.user_avatar));
				 Drawable drawable = new BitmapDrawable(bitmap);
				 view_holder.chat_id_user_avatar_iv.setBackgroundDrawable(drawable);
			 }else{
				 view_holder.chat_id_user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
			 }
			 
			 if(item.kind.equals(Kind.GASP)){
			    view_holder.chat_id_user_avatar_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_gasp_extrasmall));		
			 }
			 if(item.kind.equals(Kind.HEART)){
				 view_holder.chat_id_user_avatar_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_heart_extrasmall));
			 }
			 if(item.kind.equals(Kind.SAD)){
				 view_holder.chat_id_user_avatar_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_sad_extrasmall));		
			 }
			 if(item.kind.equals(Kind.SMILE)){
				 view_holder.chat_id_user_avatar_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_smile_extrasmall));		
			 }
			 if(item.kind.equals(Kind.WINK)){
				 view_holder.chat_id_user_avatar_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_wink_extrasmall));
			 }	
		 	 
		 }
	}
	private class ViewHolder implements BaseViewHolder {
	    ImageView chat_id_user_avatar_iv;
	    TextView chat_id_user_comment_tv;
	} 
}
