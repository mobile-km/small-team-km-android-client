package com.teamkn.activity.base.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView.SizeCallback;

/**
 * This demo uses a custom HorizontalScrollView that ignores touch events, and therefore does NOT allow manual scrolling.
 * 
 * The only scrolling allowed is scrolling in code triggered by the menu button.
 * 
 * When the button is pressed, both the menu and the app will scroll. So the menu isn't revealed from beneath the app, it
 * adjoins the app and moves with the app.
 */
public class HorzScrollWithListMenu extends Activity implements OnGestureListener{
    MyHorizontalScrollView scrollView;
    View menu;
    View app;
    ImageView btnSlide;
//    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Helper for examples with a HSV that should be scrolled by a menu View's width.
     */
    public static boolean menuOut = false;
    
    public static class ClickListenerForScrolling implements OnClickListener{
        static HorizontalScrollView scrollView;  //横向的scrollview
        static View menu;  // 导航抽屉界面
        static int menuWidth;  // 导航抽屉宽
        
        static int ji_left_no = 0; // 左边的距离
    	static int ji_left_is = 0; // 右边的距离
        /**
         * Menu must NOT be out/shown to start with.
         */
        public ClickListenerForScrolling(HorizontalScrollView scrollView, View menu) {
            super();
            ClickListenerForScrolling.scrollView = scrollView;
            ClickListenerForScrolling.menu = menu;
            ClickListenerForScrolling.menu.getContext();
            
            // Ensure menu is visible
            ClickListenerForScrolling.menu.setVisibility(View.VISIBLE);
        }
        
        @Override
        public void onClick(View v) {
        	flag_show_menu_move();
        }
    	public static void flag_show_menu_move(){
    		ClickListenerForScrolling.menuWidth = menu.getMeasuredWidth();
            ClickListenerForScrolling.ji_left_no = ClickListenerForScrolling.menuWidth;
    		System.out.println(menuOut);
            if (menuOut) {   
            	new Thread(){
            	    boolean isrun = true;
            	    int left = 0;
            		public void run() {
            			try {	
	               			while(isrun){
	               				ClickListenerForScrolling.ji_left_no -=1;
	               				ClickListenerForScrolling.menu.post(new Runnable() {
	            					@Override
	            					public void run() {
	            						ClickListenerForScrolling.scrollView.smoothScrollTo(ClickListenerForScrolling.ji_left_no, 0);
	            					    System.out.println(ji_left_no);
	            					}
	            				}) ;
	               				Thread.sleep(1);
	               				if(ClickListenerForScrolling.ji_left_no<=left){
	               					isrun = false;
	               					ClickListenerForScrolling.ji_left_no = 0;
//	               					menuOut = false;
	               				}
	               			}	
	           			} catch (InterruptedException e) {
	           				e.printStackTrace();
	           			}
            		};
            	}.start();        
            } else {
            	
            	new Thread(){
            	    boolean isrun = true;
            	    int left = ClickListenerForScrolling.menuWidth;
            		public void run() {
            			try {	  
	              			while(isrun){
	              				ClickListenerForScrolling.ji_left_is +=1;
	              				ClickListenerForScrolling.menu.post(new Runnable() {					
	            					@Override
	            					public void run() {	
	            						ClickListenerForScrolling.scrollView.smoothScrollTo(ClickListenerForScrolling.ji_left_is, 0);
	            						 System.out.println(ji_left_is);
	            					}
	            				}); 
	              				Thread.sleep(1);
	              				if(ClickListenerForScrolling.ji_left_is>=left){
	              					isrun = false;
	              					ClickListenerForScrolling.ji_left_is = 0;
//	              					menuOut = true;
	              				}
	              			}	
	          			} catch (InterruptedException e) {
	          				e.printStackTrace();
	          			}
            		};
            	}.start();	
            }  
            menuOut = !menuOut;
    	}
    }
    /**
     * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
     * showing.
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
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {	
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
