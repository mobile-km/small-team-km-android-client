package com.teamkn.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.teamkn.R;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.cache.image.ImageCache;

public class MindpinImageSwitcher extends ViewAnimator {
	private List<String> image_urls;
	//private List<Double> image_ratios;
	
	private float event_down_x;
	private TextView footer;

	public MindpinImageSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void load_urls(List<String> image_urls,List<Double> image_ratios, TextView footer) {
		this.image_urls = image_urls;
		//this.image_ratios = image_ratios;
		
		this.footer = footer;
		for (int i = 0; i < image_urls.size(); i++) {
			ImageView image_view = new ImageView(getContext());
			addView(image_view, i);
			init_image(image_view, i);
		}
		setDisplayedChild(0);
	}
	
	private void init_image(ImageView image_view, int index){
		if(index % 2 == 0){
			image_view.setBackgroundResource(R.drawable.bg_image_loading);
		}
		
		LayoutParams lp = (LayoutParams)image_view.getLayoutParams();
		lp.width  = BaseUtils.dp_to_px(300);
		lp.height = BaseUtils.dp_to_px(300);
		lp.gravity = Gravity.CENTER;
	}
	
	@Override
	public void showNext() {
		int index = getDisplayedChild();
		setDisplayedChild(index + 1);
	}

	@Override
	public void showPrevious() {
		int index = getDisplayedChild();
		setDisplayedChild(index - 1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		//System.out.println("mindpin image switch touch");
		//System.out.println(action);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			event_down_x = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			float event_up_x = event.getX();
			if (Math.abs(event_down_x - event_up_x) > 50) {
				if (event_down_x > event_up_x) {
					// left
					showNext();
				} else {
					// right
					showPrevious();
				}
			}
			break;
		}

		return super.onTouchEvent(event);
	}
	
	private void remove_bitmap(int index){
		if(is_index_valid(index)){
			ImageView image_view = (ImageView)getChildAt(index);
			image_view.setImageBitmap(null);
		}
	}
	
	@Override
	public void setDisplayedChild(int index) {
		if(is_index_valid(index)){
			int old_index = getDisplayedChild();
			//System.out.println(old_index);
			if(index > old_index){
				setInAnimation(getContext(), R.anim.slide_in_right);
				setOutAnimation(getContext(), R.anim.slide_out_left);
				remove_bitmap(old_index - 1);
			}
			if(index < old_index){
				setInAnimation(getContext(), R.anim.slide_in_left);
				setOutAnimation(getContext(), R.anim.slide_out_right);
				remove_bitmap(old_index + 1);
			}
			
			
			super.setDisplayedChild(index);
			
			
			pre_load_bitmap(index);
			footer.setText("图 "+(index+1)+" / 共 "+image_urls.size());
		}
	}
	
	// 根据传入的下标加载一张图片
	// 如果前面有图片，向前加载一张
	// 如果后面有图片，向后加载一张
	private void pre_load_bitmap(int index) {
		load_bitmap(index);
		load_bitmap(index - 1);
		load_bitmap(index + 1);
	}

	// 根据传入的下标加载一张图片的bitmap
	// 如果传入的下标不合法，则将其忽略
	private void load_bitmap(int index) {
		if (is_index_valid(index)) {
			String image_url = image_urls.get(index);
			ImageView image_view = (ImageView) getChildAt(index);
			ImageCache.load_cached_image(image_url, image_view);
		}
	}

	// 判断一个下标是否合法
	private boolean is_index_valid(int index) {
		if (index < 0) return false;

		int size = image_urls.size();
		if (index >= size) return false;

		return true;
	}

}
