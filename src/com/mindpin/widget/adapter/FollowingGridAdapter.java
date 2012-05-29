package com.mindpin.widget.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.application.MindpinApplication;
import com.mindpin.cache.image.ImageCache;
import com.mindpin.model.ContactUser;

public class FollowingGridAdapter extends BaseAdapter {
	private List<ContactUser> followings;

	public FollowingGridAdapter(List<ContactUser> followings) {
		this.followings = followings;
	}

	@Override
	public int getCount() {
		return followings.size();
	}

	@Override
	public Object getItem(int position) {
		return followings.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactUser following = followings.get(position);
		convertView = generate_view_holder(convertView);
		
		ViewHolder view_holder = (ViewHolder)convertView.getTag();
		fill_with_following_data(view_holder, following);
		
		return convertView;
	}
	
	private View generate_view_holder(View convertView) {
		if(null == convertView){
			convertView = MindpinApplication.inflate(R.layout.contacts_following_grid_cell, null);
			ViewHolder view_holder = new ViewHolder();
			
			view_holder.user_id_textview      = (TextView) convertView.findViewById(R.id.user_id);
			view_holder.user_name_textview    = (TextView) convertView.findViewById(R.id.user_name);
			view_holder.user_sign_textview    = (TextView) convertView.findViewById(R.id.user_sign);
			view_holder.user_avatar_imageview = (ImageView)convertView.findViewById(R.id.user_avatar);
			view_holder.v2_activate_textview  = (TextView) convertView.findViewById(R.id.v2_activate);
			
			convertView.setTag(view_holder);
		}
		
		return convertView;
	}
	
	
	private void fill_with_following_data(ViewHolder view_holder,
			ContactUser contact_user) {
		view_holder.user_id_textview.setText(contact_user.user_id+"");
		view_holder.user_name_textview.setText(contact_user.name);
		view_holder.user_sign_textview.setText(contact_user.sign);
		
		ImageView image_view = view_holder.user_avatar_imageview;
		image_view.setImageResource(R.drawable.user_default_avatar_normal);
		ImageCache.load_cached_image(contact_user.avatar_url, image_view);
		
		view_holder.v2_activate_textview.setText(contact_user.v2_activate ? "ÒÑ¼¤»î":"Î´¼¤»î");

		
	}



	private final class ViewHolder {
		public TextView user_id_textview;
		public TextView user_name_textview;
		public ImageView user_avatar_imageview;
		public TextView user_sign_textview;
		public TextView v2_activate_textview;
    } 
}
