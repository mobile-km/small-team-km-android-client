package com.mindpin.widget.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.adapter.MindpinBaseAdapter;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.cache.image.ImageCache;
import com.mindpin.model.FeedComment;

public class ReceivedCommentListAdapter extends MindpinBaseAdapter<FeedComment> {

	public ReceivedCommentListAdapter(MindpinBaseActivity activity) {
		super(activity);
	}

	@Override
	public View inflate_view() {
		return inflate(R.layout.feed_comment_item, null);
	}
	
	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
		
		view_holder.content_tv    = (TextView) view.findViewById(R.id.feed_comment_content);
		view_holder.user_logo_iv  = (ImageView)view.findViewById(R.id.user_avatar);
		view_holder.user_name_tv  = (TextView) view.findViewById(R.id.user_name);
		view_holder.created_at_tv = (TextView) view.findViewById(R.id.created_at);
		
		return view_holder;
	}
	
	@Override
	public void fill_with_data(
			com.mindpin.base.adapter.MindpinBaseAdapter.BaseViewHolder holder,
			FeedComment comment, int position) {
		ViewHolder view_holder = (ViewHolder) holder;
		
		view_holder.content_tv.setText(comment.content);
		view_holder.created_at_tv.setText(BaseUtils.date_string(comment.created_at));
		
		view_holder.user_name_tv.setText(comment.creator.name);
		ImageCache.load_cached_image(comment.creator.avatar_url, view_holder.user_logo_iv);
		
	}
	
	public int get_max_id_for_request(){
		return fetch_item(getCount() - 1).comment_id - 1;
	}
	
	public class ViewHolder implements BaseViewHolder {
		public TextView content_tv;
		public ImageView user_logo_iv;
		public TextView user_name_tv;
		public TextView created_at_tv;
	}

}
