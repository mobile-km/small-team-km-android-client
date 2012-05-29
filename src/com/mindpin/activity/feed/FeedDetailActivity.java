package com.mindpin.activity.feed;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.activity.comment.FeedCommentListActivity;
import com.mindpin.activity.comment.SendFeedCommentActivity;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.model.Feed;
import com.mindpin.widget.MindpinImageSwitcher;

public class FeedDetailActivity extends MindpinBaseActivity {
	public static String EXTRA_NAME_FEED_ID = "feed_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_detail);
		
		load_feed_detail();
		bind_send_feed_comment_event();
		bind_feed_comment_list_event();
	}
	
	private void bind_feed_comment_list_event() {
		Button comment_list = (Button)findViewById(R.id.feed_comment_list);
		comment_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),FeedCommentListActivity.class);
				intent.putExtra(FeedCommentListActivity.EXTRA_NAME_FEED_ID,get_feed_id());
				startActivity(intent);
			}
		});
	}

	private void bind_send_feed_comment_event() {
		Button send_feed_comment_bn = (Button)findViewById(R.id.send_feed_comment_bn);
		send_feed_comment_bn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),SendFeedCommentActivity.class);
				intent.putExtra(SendFeedCommentActivity.EXTRA_NAME_FEED_ID,get_feed_id());
				startActivity(intent);
			}
		});
	}
	
	private int get_feed_id(){
		Bundle ex = getIntent().getExtras();
		return ex.getInt(EXTRA_NAME_FEED_ID);
	}

	private void load_feed_detail(){
		int feed_id = get_feed_id();
		
		new MindpinAsyncTask<Integer, Void, Feed>(this, R.string.now_loading) {			
			@Override
			public Feed do_in_background(Integer... params) throws Exception {
				int feed_id = params[0];
				return HttpApi.read_feed(feed_id);
			}

			@Override
			public void on_success(Feed feed) {
				show_feed(feed);
			}
		}.execute(feed_id);
	}
	
	//显示feed详细信息
	private void show_feed(Feed feed) {
		// 用户基本信息
		FeedHelper.set_part_feed_user_info(this, feed);
		
		// 渲染照片
		show_feed_photos(feed);
		
		//填写标题，正文
		FeedHelper.set_title((TextView)findViewById(R.id.feed_title), feed);
		FeedHelper.set_detail((TextView)findViewById(R.id.feed_detail), feed);
	}
	
	private void show_feed_photos(Feed feed) {
		try {
			RelativeLayout photos_layout = (RelativeLayout) findViewById(R.id.feed_detail_photos);
			TextView footer = (TextView) findViewById(R.id.feed_detail_photos_footer);
			
			List<String> photo_urls = feed.photos_middle;
			if (photo_urls.size() > 0) {
				photos_layout.setVisibility(View.VISIBLE);
				
				final MindpinImageSwitcher switcher = (MindpinImageSwitcher) findViewById(R.id.feed_detail_photos_image_switcher);
				switcher.load_urls(photo_urls, feed.photos_ratio, footer);

				// 注册左右手势滑动事件
				ScrollView feed_detail_scroll = (ScrollView) findViewById(R.id.feed_detail_scroll);
				feed_detail_scroll.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switcher.onTouchEvent(event);
						return false;
					}
				});
			}else{
				photos_layout.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtils.toast("图片加载错误");
		}		
	}
	
}
