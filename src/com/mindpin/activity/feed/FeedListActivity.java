package com.mindpin.activity.feed;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.activity.MindpinSimpleDataList;
import com.mindpin.model.Feed;
import com.mindpin.widget.adapter.FeedListAdapter;
import com.mindpin.widget.view.HeadBar;

public class FeedListActivity extends MindpinBaseActivity {
	public static final String EXTRA_COLLECTION_ID    = "collection_id";
	public static final String EXTRA_COLLECTION_TITLE = "collection_title";
	
	private int collection_id;
	private String collection_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_list);
		
		this.collection_id    = getIntent().getIntExtra(EXTRA_COLLECTION_ID, -1);
		this.collection_title = getIntent().getStringExtra(EXTRA_COLLECTION_TITLE);
        
		set_title();
		load_data();
	}
	
	private boolean is_home_timeline(){
		return -1 == collection_id;
	}
	
	private void set_title() {
		if(!is_home_timeline()){
			HeadBar headbar = (HeadBar) findViewById(R.id.head_bar);
			headbar.set_title(collection_title);
		}
	}
	
	private void load_data(){
		ListView list_view = (ListView) findViewById(R.id.feed_list);
		FeedListAdapter adapter = new FeedListAdapter(this);
		
		new MindpinSimpleDataList<Feed, FeedListAdapter>(list_view, adapter) {

			@Override
			public List<Feed> load_list_data() throws Exception {
				if(is_home_timeline()){
					return HttpApi.FeedsApi.get_home_timeline();
				}else{
					return HttpApi.CollectionApi.get_collection_feeds(collection_id);
				}
			}

			@Override
			public List<Feed> load_list_more_data() throws Exception {
				int max_id = get_adapter().get_max_id_for_request();

				if(is_home_timeline()){
					return HttpApi.FeedsApi.get_home_timeline(max_id);
				}else{
					return HttpApi.CollectionApi.get_collection_feeds(collection_id, max_id);
				}
			}

			@Override
			public OnItemClickListener list_item_click_listener() {
				return new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						System.out.println(position);
						System.out.println(get_adapter().fetch_item(position));
						int feed_id = get_adapter().fetch_item(position).feed_id;
						Intent intent = new Intent(getApplicationContext(), FeedDetailActivity.class);
						intent.putExtra(FeedDetailActivity.EXTRA_NAME_FEED_ID, feed_id);
						startActivity(intent);
					}
				};
			}
			
		}.load();
	}	
}
