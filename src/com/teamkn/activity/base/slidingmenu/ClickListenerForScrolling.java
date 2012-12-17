package com.teamkn.activity.base.slidingmenu;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;

public class ClickListenerForScrolling implements OnClickListener {
	public static boolean menuOut = false;
	public static boolean result = true;
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
		ClickListenerForScrolling.listener_click(scrollView, menu);
	}


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