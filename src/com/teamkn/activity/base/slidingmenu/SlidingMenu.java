package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.teamkn.base.utils.BaseUtils;

public class SlidingMenu {
	
	private static final int DEFAULT_DURATION = 250;
	
	private LinearLayout content_container;
	private Scroller scroller;
	
	private int screen_width;
	private int screen_height;
	
	private int CLOSE_X = 0;
	private int OPEN_X;
	
	private int margin_left;
	
	private boolean is_open = false;
	
	public SlidingMenu(LinearLayout content_container, View content_view) {
		this.content_container = content_container;
		this.scroller = new Scroller(content_container.getContext());
		
		_get_screen_size();
		_init_container_size();
		_set_container_margin_left(0);
		this.content_container.addView(content_view);
	}
	
	private void _get_screen_size(){
		WindowManager wm = (WindowManager) content_container.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		this.screen_width  = display.getWidth();
		this.screen_height = display.getHeight();
		this.OPEN_X        = this.screen_width - BaseUtils.dp_to_px(50);
	}
	
	private void _init_container_size(){
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) content_container.getLayoutParams();
		lp.height = screen_height;
		lp.width  = screen_width;
		content_container.setLayoutParams(lp);
	}
	
	private void _set_container_margin_left(int px){
		if(px == margin_left) return;
		
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) content_container.getLayoutParams();
		lp.setMargins(px, 0, 0, 0);
		content_container.setLayoutParams(lp);
		this.margin_left = px;
	}
	
	public void toggle() {
		if (is_open) {
			close();
		} else {
			open();
		}
	}

	public void open() {
		_smooth_move_to(OPEN_X);
		this.is_open = true;
	}

	public void close() {
		_smooth_move_to(CLOSE_X);
		this.is_open = false;
	}
	
	private void _smooth_move_to(final int to_left_px){
		int dx = to_left_px - margin_left;
		scroller.startScroll(margin_left, 0, dx, 0, DEFAULT_DURATION);
		_animate();
	}
	
	private void _animate(){
		if(scroller.computeScrollOffset()){
			final int new_x = scroller.getCurrX();
			
			Message msg = Message.obtain();
			msg.obj = this;
			
			new Handler(){
				@Override
				public void handleMessage(Message msg) {
					SlidingMenu.this._set_container_margin_left(new_x);
					_animate();
				}
			}.sendMessageDelayed(msg, 0);	
		}
	}
}
