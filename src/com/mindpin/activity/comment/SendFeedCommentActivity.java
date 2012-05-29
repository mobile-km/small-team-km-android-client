package com.mindpin.activity.comment;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.base.utils.BaseUtils;

public class SendFeedCommentActivity extends MindpinBaseActivity {
	public static final String EXTRA_NAME_FEED_ID = "feed_id";
	public static final String EXTRA_NAME_COMMENT_ID = "comment_id";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_feed_comment);
		
		bind_send_feed_comment_event();
	}
	
	private void bind_send_feed_comment_event() {
		Button send_feed_comment_bn = (Button)findViewById(R.id.send_feed_comment_bn);
		send_feed_comment_bn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				send_feed_comment();
			}
		});
	}

	private void send_feed_comment(){
		EditText feed_comment_et = (EditText)findViewById(R.id.feed_comment_et);
		String comment = feed_comment_et.getText().toString();
		
		if(BaseUtils.is_str_blank(comment)){
			BaseUtils.toast("评论内容不能为空");
			return;
		}
		
		new MindpinAsyncTask<String, Void, Boolean>(this,R.string.now_sending) {
			@Override
			public Boolean do_in_background(String... params) throws Exception {
				Integer feed_id    = getIntent().getIntExtra(EXTRA_NAME_FEED_ID, -1);
				Integer comment_id = getIntent().getIntExtra(EXTRA_NAME_COMMENT_ID, -1);
				
				String content = params[0];
				if(feed_id > 0){
					return HttpApi.add_feed_commment(feed_id, content);
				}else if(comment_id > 0){
					return HttpApi.reply_feed_comment(comment_id, content);
				}
				return true;
			}

			@Override
			public void on_success(Boolean result) {
				if(result){
					BaseUtils.toast("评论发送成功");
					SendFeedCommentActivity.this.finish();
				}else{
					BaseUtils.toast("评论发送失败，请稍后重试");
				}
			}
		}.execute(comment);
	}
}
