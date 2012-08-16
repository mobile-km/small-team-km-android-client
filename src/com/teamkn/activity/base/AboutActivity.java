package com.teamkn.activity.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.HorzScrollWithListMenu;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.activity.base.slidingmenu.HorzScrollWithListMenu.ClickListenerForScrolling;
import com.teamkn.base.activity.TeamknBaseActivity;

public class AboutActivity extends TeamknBaseActivity   implements OnGestureListener  {
	private GestureDetector detector;
	//menu菜单
		 MyHorizontalScrollView scrollView;
		 View base_about;
		 View foot_view;  //底层  图层 隐形部分
		 ImageView iv_foot_view;
		 
		 boolean menuOut = false;
		 Handler handler = new Handler();
	//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       detector = new GestureDetector(this);
        
        // <<
		LayoutInflater inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));

        scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
        foot_view = findViewById(R.id.menu);    
        RelativeLayout foot_rl_about = (RelativeLayout)findViewById(R.id.foot_rl_about);

        base_about = inflater.inflate(R.layout.base_about, null);
        
        
        iv_foot_view = (ImageView) base_about.findViewById(R.id.iv_foot_view);
        new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view);
        HorzScrollWithListMenu.menuOut = false;
        iv_foot_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_menuOut();
			}
		});
        foot_rl_about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_menuOut();
			}
		});
        
        
        View transparent = new TextView(this);
        transparent.setBackgroundColor(android.R.color.transparent);
        final View[] children = new View[] { transparent, base_about };
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new HorzScrollWithListMenu.SizeCallbackForMenu(iv_foot_view));    
       
        
        //>>
//        setContentView(R.layout.base_about);
    }

    public void open_teamkn_website(View view) {
        Uri uri = Uri.parse("http://www.teamkn.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    
    /** 
     * 监听滑动 
     */
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	// // 滑动一段距离，up时触发，e1为down时的MotionEvent，e2为up时的MotionEvent  
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
        return false;  
	}
	@Override
	public void onLongPress(MotionEvent e) {	
	}
	boolean is_out = false;
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (e1.getX() - e2.getX() > 120 && !is_out ) {  //向左滑动 
			is_menuOut();
        }else if(e1.getX() - e2.getX() < -120  && is_out){
        	is_menuOut();
        }
		return true;
	}
	public void is_menuOut(){
		is_out = !is_out;
		ClickListenerForScrolling.flag_show_menu_move();
	}
	@Override
	public void onShowPress(MotionEvent e) {	
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	@Override 
	public boolean onTouchEvent(MotionEvent event) { 
		return this.detector.onTouchEvent(event); 
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	   this.detector.onTouchEvent(ev);
	   return super.dispatchTouchEvent(ev);
	}
}
