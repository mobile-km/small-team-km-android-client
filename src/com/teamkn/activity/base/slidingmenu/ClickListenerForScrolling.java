package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

public class ClickListenerForScrolling implements OnClickListener {
	public static boolean menuOut = false;
	static HorizontalScrollView scrollView;
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
		listener_click();
	}

	public static void listener_click() {
		
		WindowManager wm = (WindowManager) scrollView.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int menuWidth = display.getWidth();

		int left = 0;
		if (!menuOut) {
			left = 0;
		} else {
			left = menuWidth;
		}
		set_smooth_scroll_to(scrollView, left, 0);
	}

	public static void set_smooth_scroll_to(HorizontalScrollView scrollView, int x, int y) {
		scrollView.smoothScrollTo(x, y);
		menuOut = !menuOut;
	}
}