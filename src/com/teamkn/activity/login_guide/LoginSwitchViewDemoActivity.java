package com.teamkn.activity.login_guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.base.activity.TeamknBaseActivity;


public class LoginSwitchViewDemoActivity extends TeamknBaseActivity implements OnPageChangeListener, OnClickListener{  

	private LoginScrollLayout mScrollLayout;	
	private ImageView[] mImageViews;	
	private int mViewCount;	
	private int mCurSel;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_guide);        
        init();  
    }
    
    private void init()
    {
    	mScrollLayout = (LoginScrollLayout) findViewById(R.id.ScrollLayout); 	
    	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
    	//动态添加一个layout控件
//    	LinearLayout layout=new LinearLayout(this);
//    	layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
//    	layout.setBackgroundResource(R.drawable.a1);
//    	mScrollLayout.addView(layout);
    	//动态添加一个imageView控件        
//    	ImageView imageView=new ImageView(this);
//    	imageView.setLayoutParams(new LayoutParams(R.dimen.login_image, R.dimen.login_image));
////    	imageView.setPadding(15, 15, 15, 15);
//    	imageView.setImageResource(R.drawable.btn_radio_off);
//    	linearLayout.addView(imageView);
    	
    	mViewCount = mScrollLayout.getChildCount();
    	
    	mImageViews = new ImageView[mViewCount];   	
    	
    	System.out.println("mViewCount.count " + mViewCount);
    	
    	for(int i = 0; i < mViewCount; i++)    	{
    		mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
    		mImageViews[i].setEnabled(true);
    		mImageViews[i].setOnClickListener(this);
    		mImageViews[i].setTag(i);
    	}    	
    	mCurSel = 0;
    	mImageViews[mCurSel].setEnabled(false);    	
//    	mScrollLayout.SetOnViewChangeListener(this);
    	
    }

    private void setCurPoint(int index)
    {
    	if (index < 0 || index > mViewCount - 1 || mCurSel == index)    	{
    		return ;
    	}    	
    	mImageViews[mCurSel].setEnabled(true);
    	mImageViews[index].setEnabled(false);    	
    	mCurSel = index;
    }

//    @Override
//	public void OnViewChange(int view) {
//		// TODO Auto-generated method stub
//		setCurPoint(view);
//	}

	@Override
	public void onClick(View v) {
		int pos = (Integer)(v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {	
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {	
	}

	@Override
	public void onPageSelected(int view) {
		setCurPoint(view);
	}
	public void click_experience(View view){
		Toast.makeText(this, "click_experience", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(LoginSwitchViewDemoActivity.this,MainActivity.class);
		intent.putExtra("first_login", true);
		startActivity(intent);
		this.finish();
	}
}