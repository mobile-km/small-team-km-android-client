package com.mindpin.widget.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.activity.comment.SendFeedCommentActivity;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.adapter.MindpinBaseAdapter;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.cache.image.ImageCache;
import com.mindpin.model.FeedComment;

public class FeedCommentListAdapter extends MindpinBaseAdapter<FeedComment> {

	public FeedCommentListAdapter(MindpinBaseActivity activity) {
		super(activity);
	}
	
	@Override
	public View inflate_view() {
		return inflate(R.layout.feed_comment_item, null);
	}
	
	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
		
		view_holder.content_tv 	  = (TextView) view.findViewById(R.id.feed_comment_content);
		view_holder.user_logo_iv  = (ImageView)view.findViewById(R.id.user_avatar);
		view_holder.user_name_tv  = (TextView) view.findViewById(R.id.user_name);
		view_holder.created_at_tv = (TextView) view.findViewById(R.id.created_at);
		
		return view_holder;
	}

	@Override
	public void fill_with_data(BaseViewHolder holder, FeedComment comment, int position) {
		ViewHolder view_holder = (ViewHolder) holder;
		
		view_holder.content_tv.setText(comment.content);
		view_holder.created_at_tv.setText(BaseUtils.date_string(comment.created_at));
		
		view_holder.user_name_tv.setText(comment.creator.name);
		ImageCache.load_cached_image(comment.creator.avatar_url, view_holder.user_logo_iv);
	}
	
	public void show_item_dialog(int position){
		final FeedComment feed_comment = fetch_item(position);
		
		new AlertDialog.Builder(activity)
			.setTitle("评论")
			.setItems(get_alert_dialog_items(feed_comment), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						open_reply_activity(feed_comment);
						break;
					case 1:
						show_destroy_comment_dialog(feed_comment);
						break;
					}
				}
			}).show();
	}
	
	private String[] get_alert_dialog_items(FeedComment feed_comment){
		if(can_delete_comment(feed_comment)){
			return new String[]{"回复评论","删除评论"};
		}else{
			return new String[]{"回复评论"};
		}
	}
	
	private boolean can_delete_comment(FeedComment feed_comment){
		int current_user_id = activity.current_user().user_id;
		int creator_id = feed_comment.creator.user_id;
		int feed_creator_id = feed_comment.feed.creator.user_id;
		
		return current_user_id == creator_id || current_user_id == feed_creator_id;
	}
	
	private void open_reply_activity(final FeedComment feed_comment) {
		Intent intent = new Intent(activity.getApplicationContext(),SendFeedCommentActivity.class);
		intent.putExtra(SendFeedCommentActivity.EXTRA_NAME_COMMENT_ID, feed_comment.comment_id);
		activity.startActivity(intent);
	}
	
	private void show_destroy_comment_dialog(final FeedComment feed_comment) {
		new AlertDialog.Builder(activity)
			.setMessage("确认删除这条评论吗？")
			.setPositiveButton(R.string.dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						destroy_comment(feed_comment);
					}
				})
			.setNegativeButton(R.string.dialog_cancel, null)
			.show();
	}
	
	private void destroy_comment(final FeedComment feed_comment) {
		// TODO MindpinAsyncTask 第二个参数没起作用
		new MindpinAsyncTask<String, Void, Void>(
				activity,
				R.string.now_deleting
		) {
			@Override
			public Void do_in_background(String... params)
					throws Exception {
				HttpApi.destroy_feed_commment(feed_comment.comment_id);
				return null;
			}

			@Override
			public void on_success(Void result) {
				remove_item(feed_comment);
			}
		}.execute();
	}
	
	// ----------------------
	
	public class ViewHolder implements BaseViewHolder {
		public TextView content_tv;
		public ImageView user_logo_iv;
		public TextView user_name_tv;
		public TextView created_at_tv;
	}

}
