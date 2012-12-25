package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.teamkn.R;
import com.teamkn.base.utils.BaseUtils;

public class SlidingMenuView extends LinearLayout {

	private static final int DEFAULT_DURATION = 250;
	private static final int TOUCH_SLOP = 16;
	
	private ViewGroup center_view;
	private Scroller scroller;
	
	private int screen_width;
	private int screen_height;
	
	private int CLOSE_X = 0;
	private int OPEN_X;
	
	private int pos_x;
	
	private boolean is_open = false;
	private boolean will_shake = false;
	
	private boolean is_dragging = false;
	private float last_drag_x;
	
	public SlidingMenuView(Context context) {
		super(context);
	}

	public SlidingMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void init(View content_view) {
		this.scroller = new Scroller(getContext());
		this.center_view = (ViewGroup) findViewById(R.id.content_container_center);
		
		_compute_screen_size();
		_init_container_size();
		_set_x(this.CLOSE_X);
		
		this.center_view.addView(content_view);
	}
	
	private void _compute_screen_size() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		this.screen_width  = display.getWidth();
		this.screen_height = display.getHeight();
		
		this.CLOSE_X       = this.CLOSE_X - BaseUtils.dp_to_px(10);
		this.OPEN_X        = this.screen_width - BaseUtils.dp_to_px(50) - BaseUtils.dp_to_px(10);
	}
	
	private void _init_container_size() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) center_view.getLayoutParams();
		lp.height = screen_height;
		lp.width  = screen_width;
		center_view.setLayoutParams(lp);
	}
	
	private void _set_x(int px){
		if(px == pos_x) return;
		
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
		lp.setMargins(px, 0, 0, 0);
		setLayoutParams(lp);
		this.pos_x = px;
	}
	
	public void toggle() {
		if (is_open) {
			close(true);
		} else {
			open(true);
		}
	}
	
	public void open(boolean will_shake) {
		_smooth_move_to(OPEN_X);
		this.is_open = true;
		this.will_shake = will_shake;
	}

	public void close(boolean will_shake) {
		_smooth_move_to(CLOSE_X);
		this.is_open = false;
		this.will_shake = will_shake;
	}
	
	private void _smooth_move_to(final int to_left_px){
		int dx = to_left_px - pos_x;
		scroller.startScroll(pos_x, 0, dx, 0, DEFAULT_DURATION);
		_animate();
	}
	
	private void _animate() {
		if (scroller.computeScrollOffset()) {
			Message msg = Message.obtain();
			msg.obj = this;
			handler.sendMessageDelayed(msg, 0);
			return;
		}
		
		if (this.will_shake) {
			_shake_phone(20);
		}
	}
	
	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			SlidingMenuView sm = (SlidingMenuView) msg.obj;
			sm._set_x(sm.scroller.getCurrX());
			sm._animate();
		}
	};
	
	// ------------- touch
		
	public boolean onTouchEvent(MotionEvent ev) {
		float x = ev.getRawX();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int dx = (int) (x - last_drag_x);
			last_drag_x = x;
			int new_pos_x = pos_x + dx;
			if (new_pos_x < CLOSE_X) new_pos_x = CLOSE_X;
			if (new_pos_x > OPEN_X ) new_pos_x = OPEN_X;
			
			_set_x(new_pos_x);
			break;
		case MotionEvent.ACTION_UP:
			_on_touch_up(ev);
		}

		return true;
	};
	
	private void _on_touch_up(MotionEvent evt) {		
		// 触摸的持续时间
		boolean is_short_touch = (evt.getEventTime() - evt.getDownTime()) < 300;		
		
		if (is_open) {
			boolean is_over_boundary = pos_x < screen_width * 2 / 3;
			boolean is_quick_touch = pos_x < OPEN_X && is_short_touch;
			
			if (is_over_boundary || is_quick_touch) {
				close(true);
			} else {
				open(false); // 此时应该不振动
			}
		} else {
			boolean is_over_boundary = pos_x > screen_width / 3;
			boolean is_quick_touch = pos_x > 0 && is_short_touch;
			
			if (is_over_boundary || is_quick_touch) {
				open(true);
			} else {
				close(false); // 此时应该不振动
			}
		}
	}
	
	private void _shake_phone(int millisecond){
		Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		// 0 秒后 震动 指定的 毫秒
		long[] pattern = {0, millisecond};
		// -1 不重复，非 -1 为从 pattern 的指定下标开始重复
		vibrator.vibrate(pattern, -1);
	}
	
	// 从 HorizontalScrollView 的对应方法改造过来的……略有修改
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();

		if ((action == MotionEvent.ACTION_MOVE) && (is_dragging)) {
			return true;
		}

		final float x = ev.getRawX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			last_drag_x = x;
			is_dragging = !scroller.isFinished();
			break;
		case MotionEvent.ACTION_MOVE:
			final int x_diff = (int) Math.abs(x - last_drag_x);
			if (x_diff > TOUCH_SLOP) {
				is_dragging = true;
				last_drag_x = x;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			is_dragging = false;
			break;
		}

		return is_dragging;
	}
	
}
