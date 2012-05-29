package com.mindpin.activity.comment;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.activity.feed.FeedDetailActivity;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.activity.MindpinSimpleDataList;
import com.mindpin.model.FeedComment;
import com.mindpin.widget.adapter.ReceivedCommentListAdapter;

public class ReceivedCommentListActivity extends MindpinBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.received_comment_list);
		
		load_data();
	}
	
	private void load_data(){
		ListView list_view = (ListView)findViewById(R.id.received_comment_list);
		ReceivedCommentListAdapter adapter = new ReceivedCommentListAdapter(this);
		
		new MindpinSimpleDataList<FeedComment, ReceivedCommentListAdapter>(list_view, adapter) {

			@Override
			public List<FeedComment> load_list_data() throws Exception {
				return HttpApi.received_comments();
			}

			@Override
			public List<FeedComment> load_list_more_data() throws Exception {
				int max_id = get_adapter().get_max_id_for_request();
				return HttpApi.received_comments(max_id);
			}

			@Override
			public OnItemClickListener list_item_click_listener() {
				return new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						show_context_menu_dialog(get_adapter().fetch_item(position));
					}
				};
			}
		}.load();
	}
	
	private void show_context_menu_dialog(final FeedComment feed_comment){
		Builder builder = new AlertDialog.Builder(this);
		final String[] items = new String[] { "回复评论", "转到主题" };
		builder
			.setTitle("评论")
			.setItems(items, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						reply_comment(feed_comment);
						break;
					case 1:
						redirect_to_feed(feed_comment);
						break;
					}
				}
			})
			.show();
	}
	
	private void reply_comment(FeedComment feed_comment) {
		Intent intent = new Intent(getApplicationContext(), SendFeedCommentActivity.class);
		intent.putExtra(SendFeedCommentActivity.EXTRA_NAME_COMMENT_ID,feed_comment.comment_id);
		ReceivedCommentListActivity.this.startActivity(intent);
	}
	
	private void redirect_to_feed(FeedComment feed_comment) {
		Intent intent = new Intent(getApplicationContext(), FeedDetailActivity.class);
		intent.putExtra(FeedDetailActivity.EXTRA_NAME_FEED_ID, feed_comment.feed.feed_id);
		ReceivedCommentListActivity.this.startActivity(intent);
	}
	
}
