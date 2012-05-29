package com.mindpin.widget.adapter;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.activity.feed.FeedHelper;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.adapter.MindpinBaseAdapter;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.cache.image.ImageCache;
import com.mindpin.model.Feed;

public class FeedListAdapter extends MindpinBaseAdapter<Feed> {
	public FeedListAdapter(MindpinBaseActivity activity) {
		super(activity);
	}
	
	@Override
	public View inflate_view() {
		return inflate(R.layout.feed_list_item, null);
	}
	
	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
		
		view_holder.title_textview  = (TextView) view.findViewById(R.id.feed_title);
		view_holder.detail_textview = (TextView) view.findViewById(R.id.feed_detail);
		
		view_holder.user_name_textview 	  = (TextView)  view.findViewById(R.id.user_name);
		view_holder.user_avatar_imageview = (ImageView) view.findViewById(R.id.user_avatar);
		view_holder.updated_at_textview   = (TextView)  view.findViewById(R.id.updated_at);
		
		view_holder.feed_photos_1st = (ImageView) view.findViewById(R.id.feed_photos_1st);
		view_holder.feed_photos_2nd = (ImageView) view.findViewById(R.id.feed_photos_2nd);
		view_holder.feed_photos_3rd = (ImageView) view.findViewById(R.id.feed_photos_3rd);
		view_holder.feed_one_photo  = (ImageView) view.findViewById(R.id.feed_one_photo);
		
		return view_holder;
	}

	@Override
	public void fill_with_data(BaseViewHolder holder, Feed feed, int position) {
		ViewHolder view_holder = (ViewHolder) holder;
		
		set_basic_info(view_holder, feed);
		
		switch (feed.photos_middle.size()) {
		case 0:
			clear_photos(view_holder);
			break;
		case 1:
			set_one_photo(view_holder, feed);
			break;
		default:
			set_photos(view_holder, feed);
			break;
		}
		
	}
	
	private void set_basic_info(ViewHolder view_holder, Feed feed){
		FeedHelper.set_title(view_holder.title_textview, feed);
		FeedHelper.set_detail(view_holder.detail_textview, feed);
		
		FeedHelper.set_user_avatar(view_holder.user_avatar_imageview, feed);
		FeedHelper.set_user_name(view_holder.user_name_textview, feed);
		FeedHelper.set_updated_at(view_holder.updated_at_textview, feed);
	}
	
	private void clear_photos(ViewHolder view_holder){
		view_holder.feed_photos_1st.setVisibility(View.GONE);
		view_holder.feed_photos_2nd.setVisibility(View.GONE);
		view_holder.feed_photos_3rd.setVisibility(View.GONE);
		view_holder.feed_one_photo.setVisibility(View.GONE);
	}
	
	private void set_one_photo(ViewHolder view_holder, Feed feed){
		clear_photos(view_holder);
		
		ImageView image_view = view_holder.feed_one_photo;
		image_view.setImageBitmap(null);
		image_view.setVisibility(View.VISIBLE);
		
		double photo_ratio = feed.photos_ratio.get(0);
		LayoutParams lp = image_view.getLayoutParams();
		lp.height = BaseUtils.dp_to_px((int)(260*photo_ratio));
		image_view.setLayoutParams(lp);
		
		String photo_url = feed.photos_middle.get(0);
		ImageCache.load_cached_image(photo_url, image_view);
	}
	
	private void set_photos(ViewHolder view_holder, Feed feed) {
		clear_photos(view_holder);

		int count = feed.photos_thumbnail.size();
		for (int i = 0; i < count; i++) {
			String photo_url = feed.photos_thumbnail.get(i);
			
			if(0 == i) set_thumbnail(photo_url, view_holder.feed_photos_1st);
			if(1 == i) set_thumbnail(photo_url, view_holder.feed_photos_2nd);
			if(2 == i) set_thumbnail(photo_url, view_holder.feed_photos_3rd);
			if(3 <= i) break;
		}
	}
	
	private void set_thumbnail(String photo_url, ImageView image_view){
		image_view.setImageBitmap(null);
		image_view.setVisibility(View.VISIBLE);
		ImageCache.load_cached_image(photo_url, image_view);
	}
	
	public int get_max_id_for_request(){
		return fetch_item(getCount() - 1).feed_id - 1;
	}
	
	private final class ViewHolder implements BaseViewHolder {
		public TextView title_textview;
		public TextView detail_textview;
		
		public TextView user_name_textview;
		public ImageView user_avatar_imageview;
		public TextView updated_at_textview;
		
        public ImageView feed_photos_1st;
        public ImageView feed_photos_2nd;
        public ImageView feed_photos_3rd;
        public ImageView feed_one_photo;
    }

}
