package com.teamkn.activity.base.slidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 仿Launcher中的WorkSapce，可以左右滑动切换屏幕的类
 * @author Yao.GUET
 * blog: http://blog.csdn.net/Yao_GUET
 * date: 2011-05-04
 */
public class ScrollLayout extends ViewGroup {

	public static Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	
	int countMIX=0;
	int countMIY=0;
	
	private int mCurScreen;
	private int mDefaultScreen = 1;
	
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	
	private static final int SNAP_VELOCITY = 600;
	private static final int SNAP_VELOCITY_Y = 10;
	
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;

	public ScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
		
		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
//		不要设置/*if (changed) {*/变量去判断布局是否改变，否则第二次界面刷新的时候不会启用if{}里面的程序
//		if (changed) {
		
			int childLeft = 0;
			final int childCount = getChildCount();
			
			for (int i=0; i<childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					if(i==0){
						final int childWidth = childView.getMeasuredWidth();
						childView.layout(childLeft, 0, 
								childLeft+childWidth, childView.getMeasuredHeight());
						childLeft += childWidth;
					}else{
						final int childWidth = childView.getMeasuredWidth();
						childView.layout(childLeft, 0, 
								childLeft+childWidth, childView.getMeasuredHeight());
						childLeft += childWidth;
					}	
				}
			}
		}
//	}
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
  
        final int width = MeasureSpec.getSize(widthMeasureSpec);   
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);   
        if (widthMode != MeasureSpec.EXACTLY) {   
            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!"); 
        }   
  
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);   
        if (heightMode != MeasureSpec.EXACTLY) {   
            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
        }   
  
        // The children are given the same width and height as the scrollLayout   
        final int count = getChildCount();   
        for (int i = 0; i < count; i++) {   
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);   
        } 
        scrollTo(mCurScreen * width, 0);         
    }  
    
    /**
     * According to the position of current layout
     * scroll to the destination page.
     */
    public void snapToDestination() {
    	final int screenWidth = getWidth();
    	final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;
    	snapToScreen(destScreen);
    }
    
    public void snapToScreen(int whichScreen) {

    	// get the valid layout page
    	whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));
    	if (getScrollX() != (whichScreen*getWidth())) {

    		final int delta = whichScreen*getWidth()-getScrollX();
    		if(whichScreen == 0 && mCurScreen ==1){
    			mScroller.startScroll(getScrollX(), 0, 
        				delta + 40, 0, Math.abs(delta)*2);
    			System.out.println("else = " +  getScrollX() + " : "
        				+ delta + 40 +" : " + Math.abs(delta)*2 + " : "+ whichScreen);
    		}else if(whichScreen == 0 && mCurScreen == 0){
//    			mScroller.startScroll(getScrollX(), 0, 
//        				delta + 40, 0, Math.abs(delta)*2);
    		}else{
    			mScroller.startScroll(getScrollX(), 0, 
        				delta, 0, Math.abs(delta)*2);
    			System.out.println("else = " +  getScrollX() + " : " 
        				+ delta +" : " + Math.abs(delta)*2 + " : "+ whichScreen);
    		}
    		mCurScreen = whichScreen;
    		invalidate();		// Redraw the layout
    	}
    }

    public void setToScreen(int whichScreen) {
    	whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));
    	mCurScreen = whichScreen;
    	scrollTo(whichScreen*getWidth(), 0);
    }
    
    public int getCurScreen() {
    	return mCurScreen;
    }
    
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mLastMotionY = y;
			break;
			
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int)(mLastMotionX - x);
			int deltaY = (int)(mLastMotionY - y);
		    
//			System.out.println("mCurScreen : deltax : countMIX = " + mCurScreen + " : " + deltaX + " : " + countMIX);
			if(Math.abs(deltaY) > 50){		
//				System.out.println(" deltaY : " + deltaY);
			}else{
				if( mCurScreen == 0 && deltaX <= SNAP_VELOCITY){		
				}else if(mCurScreen == getChildCount() -1 && deltaX>=0 && countMIX>=0){	
				}else{
					countMIX += deltaX;
					mLastMotionX = x;
		            scrollBy(deltaX, 0);  
				}
			}
			break;
			
		case MotionEvent.ACTION_UP: 
            // if (mTouchState == TOUCH_STATE_SCROLLING) {   
            final VelocityTracker velocityTracker = mVelocityTracker;   
            velocityTracker.computeCurrentVelocity(1000);   
            int velocityX = (int) velocityTracker.getXVelocity();   
//            向左是负的
//            向右是正的
            
            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {   
                // Fling enough to move left  
                snapToScreen(mCurScreen - 1);   
                
            } else if (velocityX < -SNAP_VELOCITY   
                    && mCurScreen < getChildCount() - 1) {   
                // Fling enough to move right  
            	countMIX  = 0;
                snapToScreen(mCurScreen + 1);    
                
            } else if(mCurScreen==1 && (x<60&&x>0) && (y>0 && y<60) ){	
            	snapToScreen(mCurScreen - 1); 
            }else if(mCurScreen==0 && (x<getChildAt(0).getMeasuredWidth()
            		&&x>getChildAt(0).getMeasuredWidth()-40) 
            		&& (y>0 && y<50) ){
            	 countMIX  = 0;
            	 snapToScreen(mCurScreen + 1); 
            }else {   
            	if(mCurScreen == getChildCount() -1 ){
            		countMIX  = 0;
            	}
            	if(mCurScreen == 0){	
            	}else{
                    snapToDestination();   
            	}
            }  

            if (mVelocityTracker != null) {   
                mVelocityTracker.recycle();   
                mVelocityTracker = null;   
            }   
            // }   
            mTouchState = TOUCH_STATE_REST;   
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && 
				(mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		
		final float x = ev.getX();
		final float y = ev.getY();
		
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int)Math.abs(mLastMotionX-x);
			final int yDiff = (int)Math.abs(mLastMotionY-y);
			
			if (xDiff> mTouchSlop && yDiff < SNAP_VELOCITY_Y) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;	
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished()? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}
	
}
