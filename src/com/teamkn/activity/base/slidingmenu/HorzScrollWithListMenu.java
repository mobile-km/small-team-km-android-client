package com.teamkn.activity.base.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;

import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView.SizeCallback;

/**
 * This demo uses a custom HorizontalScrollView that ignores touch events, and
 * therefore does NOT allow manual scrolling.
 * 
 * The only scrolling allowed is scrolling in code triggered by the menu button.
 * 
 * When the button is pressed, both the menu and the app will scroll. So the
 * menu isn't revealed from beneath the app, it adjoins the app and moves with
 * the app.
 */
public class HorzScrollWithListMenu extends Activity implements
		OnGestureListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * Helper for examples with a HSV that should be scrolled by a menu View's
	 * width.
	 */
	public static boolean menuOut = false;

	public static class ClickListenerForScrolling implements OnClickListener {
		HorizontalScrollView scrollView;
		View menu;

		/**
		 * Menu must NOT be out/shown to start with.
		 */
		public ClickListenerForScrolling(HorizontalScrollView scrollView,
				View menu) {
			super();
			this.scrollView = scrollView;
			this.menu = menu;
		}

		@Override
		public void onClick(View v) {
			ListenerScroll.listener_click(scrollView, menu);
		}
	}

	public static class ListenerScroll {

		public static boolean result = true;

		public static void listener_click(HorizontalScrollView scrollView,
				View menu) {
			// Context context = menu.getContext();
			int menuWidth = menu.getMeasuredWidth();
			// Ensure menu is visible
			menu.setVisibility(View.VISIBLE);
			int left = 0;
			if (!menuOut) {
				left = 0;
				// scrollView.smoothScrollTo(left, 0);
			} else {
				left = menuWidth;
				// scrollView.smoothScrollTo(left, 0);
			}
			set_smooth_scroll_to(scrollView, left, 0);
			// menuOut = !menuOut;
		}

		public static void set_smooth_scroll_to(
				HorizontalScrollView scrollView, int x, int y) {
			scrollView.smoothScrollTo(x, y);
			menuOut = !menuOut;
			if (menuOut) {
				result = false;
			} else {
				result = true;
			}
			// MyHorizontalScrollView.result = !MyHorizontalScrollView.result;
		}
	}

	/**
	 * Helper that remembers the width of the 'slide' button, so that the
	 * 'slide' button remains in view, even when the menu is showing.
	 */
	public static class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnSlide;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnSlide = btnSlide;
		}

		@Override
		public void onGlobalLayout() {
			btnWidth = btnSlide.getMeasuredWidth();
			System.out.println("btnWidth=" + btnWidth);
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			final int menuIdx = 0;
			if (idx == menuIdx) {
				dims[0] = w - btnWidth;
			}
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}
