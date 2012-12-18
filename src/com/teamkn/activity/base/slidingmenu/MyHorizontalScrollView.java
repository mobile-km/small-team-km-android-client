package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import com.teamkn.base.utils.BaseUtils;

public class MyHorizontalScrollView extends HorizontalScrollView {
	
	private boolean is_open = false;
	
	private int screen_width;
	private int screen_height;
	private int transparent_width;
	
	private ViewGroup parent;
	private View transparent;
	
	public MyHorizontalScrollView(Context context) {
		super(context);
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(View content_view) {
		_get_screen_size();
		_set_transparent_size();
		
		parent.addView(content_view, screen_width, screen_height);
		
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						MyHorizontalScrollView.this.scrollBy(transparent_width, 0);
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
	
	private void _get_screen_size(){
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		this.screen_width      = display.getWidth();
		this.screen_height     = display.getHeight();
		this.transparent_width = screen_width - BaseUtils.dp_to_px(50);
	}
	
	private void _set_transparent_size(){
		this.parent = (ViewGroup) getChildAt(0);
		this.transparent = parent.getChildAt(0);
		
		ViewGroup.LayoutParams lp = transparent.getLayoutParams();
		lp.height = screen_height;
		lp.width  = transparent_width;
		
		transparent.setLayoutParams(lp);
	}
	
	public void toggle() {
		if (is_open) {
			close();
		} else {
			open();
		}
	}
	
	public void open(){
		smoothScrollTo(0, 0);
		this.is_open = true;
	}
	
	public void close(){
		smoothScrollTo(transparent_width, 0);
		this.is_open = false;
	}
	
}
