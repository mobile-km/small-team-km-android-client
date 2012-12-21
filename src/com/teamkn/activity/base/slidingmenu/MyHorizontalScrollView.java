package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
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
	
	private boolean will_shake = false;
	
	private int screen_width;
	private int screen_height;
	private int transparent_width;
	
	private ViewGroup parent;
	
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
		_add_content_view(content_view);
		
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
	public boolean onTouchEvent(MotionEvent evt) {
		super.onTouchEvent(evt);
		int action = evt.getAction();
		
		switch (action) {
		case MotionEvent.ACTION_UP:
			_on_touch_up(evt);
			break;
		default:
			break;
		}
		
		return false;
	}
	
	private void _on_touch_up(MotionEvent evt) {
		int scroll_x = getScrollX();
		
		// 触摸的持续时间
		long duration = evt.getEventTime() - evt.getDownTime();		
		
		if (is_open) {
			boolean is_over_boundary = scroll_x > transparent_width - screen_width * 2 / 3;
			boolean is_quick_touch = scroll_x > 0 && duration < 300;
			
			if (is_over_boundary || is_quick_touch) {
				close(true);
			} else {
				open(false); // 此时应该不振动
			}
		} else {
			boolean is_over_boundary = scroll_x < transparent_width - screen_width / 3;
			boolean is_quick_touch = scroll_x < transparent_width && duration < 300;
			
			if (is_over_boundary || is_quick_touch) {
				open(true);
			} else {
				close(false); // 此时应该不振动
			}
		}
	}
	
	/**
	 * 关于振动时机的控制：
	 * 情况一：
	 * 		程序启动时，界面初始化导致 scroll_x 变化，此时不应振动
	 *  	will_shake，初始化为 false 来避免一开始的振动
	 * 情况二： 
	 * 		手指在手机边缘滑动，此时不应该振动
	 *  	这种情况下，并没有触发 open() / close() 方法
	 * 情况三： 
	 * 		按键导致的抽屉打开关闭，此时应该振动
	 *  	这种情况会触发 open() / close() 方法。只要传 true 参数，就能保证方法执行后，一定会振动
	 * 情况四： 
	 * 		手指滑动到中间松开，此时是否振动，应该有所判断依据。
	 * 		两次滑动到同一边，应该是不振动的
	 *  	这种情况同样会触发 open() / close() 方法。
	 *  	在 _on_touch_up 函数里，根据实际情况向方法传入 true 或 false 即可。
	 * 情况五：
	 * 		关闭时，手指按下，移出屏幕左边缘
	 *  	打开时，手指按下，移除屏幕右边缘
	 *  	都不应该振动
	 */
	
	@Override
	protected void onScrollChanged(int left, int top, int oldl, int oldt) {
		super.onScrollChanged(left, top, oldl, oldt);
		
		// 打开和关闭时控制手机振动
		// 根据实验，open() 和 close() 的函数调用，因为调用的是 smoothScrollTo 方法
		// 所以一定是 is_open 的值改变在先，而界面完全打开或关闭在后		

		boolean scroll_totally_opened = (left == transparent_width);
		boolean scroll_totally_closed = (left == 0);

		// 当没有滑动到完全打开或完全关闭时，不振动
		if (!scroll_totally_opened && !scroll_totally_closed) return;

		// 如果通过 open()/close() 方法传参声明了不振动，则不振动
		if (!will_shake) return;
		
		// 振动
		_shake_phone(20);
		this.will_shake = false;
	}
	
	private void _shake_phone(int millisecond){
		Vibrator vibrator = (Vibrator) MyHorizontalScrollView.this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
		// 0 秒后 震动 指定的 毫秒
		long[] pattern = {0, millisecond};
		// -1 不重复，非 -1 为从 pattern 的指定下标开始重复
		vibrator.vibrate(pattern, -1);
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
		View transparent = parent.getChildAt(0);
		
		ViewGroup.LayoutParams lp = transparent.getLayoutParams();
		lp.height = screen_height;
		lp.width  = transparent_width;
		
		transparent.setLayoutParams(lp);
	}
	
	private void _add_content_view(View content_view){
		ViewGroup container = (ViewGroup) parent.getChildAt(1);
		container.addView(content_view, screen_width, screen_height);
	}
	
	// 一些操作方法
	// ---------------------
	
	public void toggle() {
		if (is_open) {
			close(true);
		} else {
			open(true);
		}
	}

	public void open(boolean will_shake) {
		smoothScrollTo(0, 0);
		this.is_open = true;
		this.will_shake = will_shake;
	}

	public void close(boolean will_shake) {
		smoothScrollTo(transparent_width, 0);
		this.is_open = false;
		this.will_shake = will_shake;
	}
}
