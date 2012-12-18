package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {
	
	public MyHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyHorizontalScrollView(Context context) {
		super(context);
		init(context);
	}

	void init(Context context) {
		setHorizontalFadingEdgeEnabled(false);
		setVerticalFadingEdgeEnabled(true);
	}
	public void initViews(View[] children, int scrollToViewIdx, View button_view) {
		ViewGroup parent = (ViewGroup) getChildAt(0);

		for (int i = 0; i < children.length; i++) {
			children[i].setVisibility(View.INVISIBLE);
			parent.addView(children[i]);
		}
		
		OnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(parent,
				children, scrollToViewIdx, button_view);
		getViewTreeObserver().addOnGlobalLayoutListener(listener);
		// 当一个视图树中的一些组件发生滚动时，所要调用的回调函数的接口类
		// 注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数。
	}

	class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {
		ViewGroup parent;
		View[] children;
		int scrollToViewIdx;
		int scrollToViewPos = 0;
		View button_view;

		public MyOnGlobalLayoutListener(ViewGroup parent, View[] children,
				int scrollToViewIdx, View button_view) {
			this.parent = parent;
			this.children = children;
			this.scrollToViewIdx = scrollToViewIdx;
			this.button_view = button_view;
		}

		@Override
		public void onGlobalLayout() {
			final HorizontalScrollView me = MyHorizontalScrollView.this;
			// 移除之前已经注册的全局布局回调函数。
			// 参数
			// victim 将要被移除的回调函数
			me.getViewTreeObserver().removeGlobalOnLayoutListener(this);

			final int w = me.getMeasuredWidth();
			final int h = me.getMeasuredHeight();

			System.out.println("w=" + w + ", h=" + h);
			
			int[] dims = new int[2];
			scrollToViewPos = 0;
			// 放之前首先移除之前所有的
			parent.removeAllViews();

			int btn_width = button_view.getMeasuredWidth();
			for (int i = 0; i < children.length; i++) {	
				dims[0] = w;
				dims[1] = h;
				final int menuIdx = 0;
				if (i == menuIdx) {
					dims[0] = w - btn_width;
				}

				System.out.println("addView w=" + dims[0] + ", h=" + dims[1]);

				children[i].setVisibility(View.VISIBLE);

				parent.addView(children[i], dims[0], dims[1]);
				if (i < scrollToViewIdx) {
					scrollToViewPos += dims[0];
				}
			}

			// For some reason we need to post this action, rather than call
			// immediately.
			// If we try immediately, it will not scroll.
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					me.scrollBy(scrollToViewPos, 0);
					// 在当前视图内容继续偏移(x , y)个单位，显示(可视)区域也跟着偏移(x,y)个单位
				}
			});
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		VelocityTracker mVelocityTracker = null;
		float mLastMotionY = 0;
		float mLastMotionX = 0;
		int SNAP_VELOCITY = 600;
//		if (mVelocityTracker == null) {
//			// 用来跟踪触摸速度的类
//			// 使用getXVelocity ()、getYVelocity ()函数来获得当前的速度
//			mVelocityTracker = VelocityTracker.obtain();
//		}
//		mVelocityTracker.addMovement(event);
//
//		final int action = event.getAction();
//		final float x = event.getX();
//		final float y = event.getY();
//
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			mLastMotionX = x;
//			mLastMotionY = y;
//			break;
//		case MotionEvent.ACTION_MOVE:
//			int move_deltaX = (int) (mLastMotionX - x);
//			int move_deltaY = (int) (mLastMotionY - y);
//
//			int scrollView_scrollX = this.getScrollX(); // 该View水平方向的偏移量（像素）
//
//			System.out.println(move_deltaX + " : " + move_deltaY);
//
//			if (scrollView_scrollX < 100 && move_deltaX > 50
//					&& Math.abs(move_deltaY) < 50) {
//			} else if (move_deltaX < -10 && Math.abs(move_deltaY) < 50) {
//				WindowManager wm = (WindowManager) getContext()
//						.getSystemService(Context.WINDOW_SERVICE);
//				Display display = wm.getDefaultDisplay();
//				int menuWidth = display.getWidth();
//				ClickListenerForScrolling.set_smooth_scroll_to(this, menuWidth
//						- (int) x, 0);
//			}
//			break;
//		// 在触摸抬起时
//		case MotionEvent.ACTION_UP:
//			int up_deltaX = (int) (mLastMotionX - x);
////			int up_deltaY = (int) (mLastMotionY - y);
//
//			final VelocityTracker velocityTracker = mVelocityTracker;
//			velocityTracker.computeCurrentVelocity(1000); // 使用computeCurrentVelocity
//															// (int
//															// units)函数来计算当前的速度
//			int velocityX = (int) velocityTracker.getXVelocity();
////			int velocityY = (int) velocityTracker.getYVelocity();
//
//			if (velocityX > SNAP_VELOCITY) {
//				ClickListenerForScrolling.listener_click();
//			} else if (velocityX < -SNAP_VELOCITY) {
//				ClickListenerForScrolling.listener_click();
//			} else if (Math.abs(up_deltaX) > 50) {
//				ClickListenerForScrolling.set_smooth_scroll_to(this, 0, 0);
//			}
//
//			if (mVelocityTracker != null) {
//				mVelocityTracker.recycle();
//				mVelocityTracker = null;
//			}
//			break;
//		case MotionEvent.ACTION_CANCEL:
//			break;
//		}

		// 对应三种情况 通过控制 onTouch 事件 的返回值 来 划分
		// return ListenerScroll.result;
		// return true;
//		return false;
		super.onTouchEvent(event);
		return false;
		// 在垂直方向的移动距离在 50 之内 才会有响应
		// if(Math.abs(deltaY) < 50){
		// HorzScrollWithImageMenu.scrollView.smoothScrollTo( 320- (int)x, 0);
	}

}
