package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

import com.teamkn.activity.base.MainActivity;
/**
 * A HorizontalScrollView (HSV) implementation that disallows touch events (so
 * no scrolling can be done by the user).
 * 
 * This HSV MUST contain a single ViewGroup as its only child, and this
 * ViewGroup will be used to display the children Views passed in to the
 * initViews() method.
 */
public class MyHorizontalScrollView extends HorizontalScrollView {

	private VelocityTracker mVelocityTracker;
	private float mLastMotionX;
	private float mLastMotionY;

	private static final int SNAP_VELOCITY = 600;
	private static final int SNAP_VELOCITY_Y = 10;

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
		// remove the fading as the HSV looks better without it
		setHorizontalFadingEdgeEnabled(false);
		setVerticalFadingEdgeEnabled(true);
	}

	/**
	 * @param children
	 *            The child Views to add to parent.
	 * @param scrollToViewIdx
	 *            The index of the View to scroll to after initialisation.
	 * @param sizeCallback
	 *            A SizeCallback to interact with the HSV.
	 */
	public void initViews(View[] children, int scrollToViewIdx,
			SizeCallback sizeCallback) {
		// A ViewGroup MUST be the only child of the HSV
		ViewGroup parent = (ViewGroup) getChildAt(0);

		// Add all the children, but add them invisible so that the layouts are
		// calculated, but you can't see the Views
		for (int i = 0; i < children.length; i++) {
			children[i].setVisibility(View.INVISIBLE);
			parent.addView(children[i]);
		}

		// Add a layout listener to this HSV
		// This listener is responsible for arranging the child views.
		OnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(parent,
				children, scrollToViewIdx, sizeCallback);
		getViewTreeObserver().addOnGlobalLayoutListener(listener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// HorzScrollWithImageMenu.scrollView.smoothScrollTo( 320- (int)x, 0);
		// ListenerScroll.listener_click(HorzScrollWithImageMenu.scrollView,
		// HorzScrollWithImageMenu.foot_view);

		if (mVelocityTracker == null) {
			// 用来跟踪触摸速度的类
			// 使用getXVelocity ()、getYVelocity ()函数来获得当前的速度
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			int move_deltaX = (int) (mLastMotionX - x);
			int move_deltaY = (int) (mLastMotionY - y);

			int scrollView_scrollX = MainActivity.scrollView.getScrollX();
			System.out.println(move_deltaX + " : " + move_deltaY);
			if (scrollView_scrollX < 100 && move_deltaX > 50
					&& Math.abs(move_deltaY) < 50) {
			} else if (move_deltaX < -10 && Math.abs(move_deltaY) < 50) {
				// HorzScrollWithImageMenu.scrollView.smoothScrollTo( 320-
				// (int)x, 0);
				ClickListenerForScrolling.set_smooth_scroll_to(MainActivity.scrollView,
						320 - (int) x, 0);
			}
			break;
		// 在触摸抬起时
		case MotionEvent.ACTION_UP:
			int up_deltaX = (int) (mLastMotionX - x);
			int up_deltaY = (int) (mLastMotionY - y);

			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000); // 使用computeCurrentVelocity
															// (int
															// units)函数来计算当前的速度
			int velocityX = (int) velocityTracker.getXVelocity();
			int velocityY = (int) velocityTracker.getYVelocity();

			if (velocityX > SNAP_VELOCITY) {
				ClickListenerForScrolling.listener_click(MainActivity.scrollView,
						MainActivity.foot_view);
				// result = false;
			} else if (velocityX < -SNAP_VELOCITY) {
				ClickListenerForScrolling.listener_click(MainActivity.scrollView,
						MainActivity.foot_view);
				// result = false;
			} else if (Math.abs(up_deltaX) > 50) {
				// HorzScrollWithImageMenu.scrollView.smoothScrollTo(0, 0);
				ClickListenerForScrolling.set_smooth_scroll_to(MainActivity.scrollView, 0,
						0);
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}

		// 对应三种情况 通过控制 onTouch 事件 的返回值 来 划分
		// return ListenerScroll.result;
		// return true;
		return false;

		// 在垂直方向的移动距离在 50 之内 才会有响应
		// if(Math.abs(deltaY) < 50){
		// HorzScrollWithImageMenu.scrollView.smoothScrollTo( 320- (int)x, 0);
		// }
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}

	/**
	 * An OnGlobalLayoutListener impl that passes on the call to onGlobalLayout
	 * to a SizeCallback, before removing all the Views in the HSV and adding
	 * them again with calculated widths and heights.
	 */
	class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {
		ViewGroup parent;
		View[] children;
		int scrollToViewIdx;
		int scrollToViewPos = 0;
		SizeCallback sizeCallback;

		/**
		 * @param parent
		 *            The parent to which the child Views should be added.
		 * @param children
		 *            The child Views to add to parent.
		 * @param scrollToViewIdx
		 *            The index of the View to scroll to after initialisation.
		 * @param sizeCallback
		 *            A SizeCallback to interact with the HSV.
		 */
		public MyOnGlobalLayoutListener(ViewGroup parent, View[] children,
				int scrollToViewIdx, SizeCallback sizeCallback) {
			this.parent = parent;
			this.children = children;
			this.scrollToViewIdx = scrollToViewIdx;
			this.sizeCallback = sizeCallback;
		}

		@Override
		public void onGlobalLayout() {
			// System.out.println("onGlobalLayout");

			final HorizontalScrollView me = MyHorizontalScrollView.this;

			// The listener will remove itself as a layout listener to the HSV
			me.getViewTreeObserver().removeGlobalOnLayoutListener(this);

			// Allow the SizeCallback to 'see' the Views before we remove them
			// and re-add them.
			// This lets the SizeCallback prepare View sizes, ahead of calls to
			// SizeCallback.getViewSize().
			sizeCallback.onGlobalLayout();

			parent.removeViewsInLayout(0, children.length);

			final int w = me.getMeasuredWidth();
			final int h = me.getMeasuredHeight();

			System.out.println("w=" + w + ", h=" + h);

			// Add each view in turn, and apply the width and height returned by
			// the SizeCallback.
			int[] dims = new int[2];
			scrollToViewPos = 0;
			// 放之前首先移除之前所有的
			parent.removeAllViews();

			for (int i = 0; i < children.length; i++) {
				sizeCallback.getViewSize(i, w, h, dims);

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
				}
			});
		}
	}

	/**
	 * Callback interface to interact with the HSV.
	 */
	public interface SizeCallback {
		/**
		 * Used to allow clients to measure Views before re-adding them.
		 */
		public void onGlobalLayout();

		/**
		 * Used by clients to specify the View dimensions.
		 * 
		 * @param idx
		 *            Index of the View.
		 * @param w
		 *            Width of the parent View.
		 * @param h
		 *            Height of the parent View.
		 * @param dims
		 *            dims[0] should be set to View width. dims[1] should be set
		 *            to View height.
		 */
		public void getViewSize(int idx, int w, int h, int[] dims);
	}
}
