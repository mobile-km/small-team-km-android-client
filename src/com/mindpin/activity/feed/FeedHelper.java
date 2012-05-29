package com.mindpin.activity.feed;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.utils.BaseUtils;
import com.mindpin.cache.image.ImageCache;
import com.mindpin.model.Feed;

public class FeedHelper {
	public static void set_user_avatar(ImageView image_view, Feed feed){
		image_view.setImageResource(R.drawable.user_default_avatar_normal);
		image_view.setVisibility(View.VISIBLE);
		ImageCache.load_cached_image(feed.creator.avatar_url, image_view);
	}
	
	public static void set_user_name(TextView text_view, Feed feed){
		text_view.setText(feed.creator.name);
		text_view.setVisibility(View.VISIBLE);
	}
	
	public static void set_updated_at(TextView text_view, Feed feed){
		text_view.setText(BaseUtils.date_string(feed.updated_at));
		text_view.setVisibility(View.VISIBLE);
	}
	
	public static void set_part_feed_user_info(MindpinBaseActivity activity, Feed feed){
		set_user_avatar((ImageView)activity.findViewById(R.id.user_avatar), feed);
		set_user_name((TextView)activity.findViewById(R.id.user_name), feed);
		set_updated_at((TextView)activity.findViewById(R.id.updated_at), feed);
	}
	
	public static void set_title(TextView title_textview, Feed feed){
		if (BaseUtils.is_str_blank(feed.title)) {
			title_textview.setVisibility(View.GONE);
		} else {
			title_textview.setText(feed.title);
			title_textview.setVisibility(View.VISIBLE);
			// title_textview.getPaint().setFakeBoldText(true);
		}
	}
	
	public static void set_detail(TextView detail_textview, Feed feed) {
		if (BaseUtils.is_str_blank(feed.detail)) {
			detail_textview.setVisibility(View.GONE);
		} else {
			detail_textview.setText(Html.fromHtml(feed.detail));
			detail_textview.setVisibility(View.VISIBLE);
		}
	}
}
