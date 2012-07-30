package com.teamkn.base.utils;

import com.teamkn.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ShowHelp {
	/*   
	 * 
            添加     popupshow_help.xml 
            图片    camera_close.png  right.png  yuan.png
    viewhelp 把下面的代码放在当前的xml中
    
    context  显示的当前 的  activity
    
    View  显示的view
    
    String 显示的内容
    
	 * */
	
	public static void showHelp(Context context,View v,String str){
//    	viewhelp.setVisibility(View.VISIBLE);
		
		
		 
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.popupshow_help, null);
		view.setBackgroundColor(Color.TRANSPARENT);
        
		final PopupWindow popWin = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
		
		View click_v = (View)v;
		int[] intXY = new int[2];
		click_v.getLocationOnScreen(intXY);
		
		TextView popupshow_str_top = (TextView)view.findViewById(R.id.popupshow_str_top);
		TextView popupshow_str_but = (TextView)view.findViewById(R.id.popupshow_str_but);
		
		ImageView popupshow_image_top = (ImageView)view.findViewById(R.id.popupshow_image_top);
		ImageView popupshow_image_but = (ImageView)view.findViewById(R.id.popupshow_image_but);
		
		Display d = ((Activity) context).getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		d.getMetrics(dm);
		if(intXY[1] > d.getHeight()/2 ){
			popupshow_str_top.setText(str);
			popupshow_image_top.setVisibility(View.VISIBLE);
		}else{
			popupshow_str_but.setText(str);
			popupshow_image_but.setVisibility(View.VISIBLE);
		}
		popupshow_image_top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				viewhelp.setVisibility(View.GONE);	
//				viewhelp.removeAllViews();
				popWin.dismiss();
			}
		});
       
		popupshow_image_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				viewhelp.setVisibility(View.GONE);	
//				viewhelp.removeAllViews();
				popWin.dismiss();
			}
		});
		
		int top = ((intXY[1] + click_v.getHeight()/2)-50);
		int left = ((intXY[0] + click_v.getWidth()/2)-48);
		if(left<=0){
			left = 0;
		}
		LinearLayout help_top = (LinearLayout)view.findViewById(R.id.linearlayout_help_top);
        ImageView iv_top = new ImageView(context);
        iv_top.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.right));
        iv_top.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, top));
        help_top.addView(iv_top); 
        
        LinearLayout help_left = (LinearLayout)view.findViewById(R.id.linearlayout_help_left);
        ImageView iv_left = new ImageView(context);
        iv_left.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.right));
        iv_left.setLayoutParams(new LayoutParams(left, 100));
        help_left.addView(iv_left);

//        viewhelp.addView(view);
        
        
        popWin.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popWin.setOutsideTouchable(false); // 设置是否允许在外点击使其消失，到底有用没？ 
//		popWin.setAnimationStyle(R.style.AnimationPreview); // 设置动画 
        
        popWin.showAtLocation(v, Gravity.CENTER, 0, 0);
    }   
}
