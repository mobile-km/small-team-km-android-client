package com.mindpin.activity.comment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.activity.MindpinSimpleDataList;
import com.mindpin.model.FeedComment;
import com.mindpin.widget.adapter.FeedCommentListAdapter;

public class FeedCommentListActivity extends MindpinBaseActivity {
	public static final String EXTRA_NAME_FEED_ID = "feed_id";
	
	private int feed_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_comment_list);
		
		feed_id = getIntent().getIntExtra(EXTRA_NAME_FEED_ID, -1);
		
		load_data();
	}
	
	private void load_data(){
		ListView list_view = (ListView) findViewById(R.id.feed_comment_list);
		FeedCommentListAdapter adapter = new FeedCommentListAdapter(this);
		
		new MindpinSimpleDataList<FeedComment, FeedCommentListAdapter>(list_view, adapter) {

			@Override
			public List<FeedComment> load_list_data() throws Exception {
				return HttpApi.get_feed_comments(feed_id);
			}

			@Override
			public List<FeedComment> load_list_more_data() throws Exception {
				return new ArrayList<FeedComment>();
			}

			@Override
			public OnItemClickListener list_item_click_listener() {
				return new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						get_adapter().show_item_dialog(position);
					}
				};
			}
			
		}.load();
	}
}
