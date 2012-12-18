package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.teamkn.base.utils.BaseUtils;

public class MyHorizontalScrollView extends HorizontalScrollView {
	
	public MyHorizontalScrollView(Context context) {
		super(context);
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void initViews(View transparent, View content_view, View button_view) {
		ViewGroup parent = (ViewGroup) getChildAt(0);
		
		transparent.setBackgroundColor(0x7f070028);		
		
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		int width  = display.getWidth();
		int height = display.getHeight();
		int btn_width = BaseUtils.dp_to_px(50);
		
		final int width_off = width - btn_width;
		
		parent.addView(transparent, width_off, height);
		parent.addView(content_view, width, height);
		
		
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						MyHorizontalScrollView.this.scrollBy(width_off, 0);
					}
				});
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		return false;
	}
	
}
