package com.teamkn.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.MainActivity.RequestCode;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataListDBHelper;

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
	static class Params{
		static void setParam(Context context){
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
			int nowWidth = dm.widthPixels;
			int height = dm.heightPixels;
			if(nowWidth>400 && height > 700){
				Params.top =Params.top_480_800;
				Params.left =Params.left_480_800;
			}else{
				Params.top =Params.top_320_480;
				Params.left =Params.left_320_480;
			}
		}
		static final int top_320_480 = -150;
		static final int left_320_480 = -200;
		static final int top_480_800 = -270;
		static final int left_480_800 = -250;
		static int top = top_320_480;
		static int left = left_320_480;
		
	}
	static PopupWindow popWin;
	public static void showHelp(final Context context,View v,String str){
		Params.setParam(context);
//    	viewhelp.setVisibility(View.VISIBLE);
		System.out.println("----------------------------");
		System.out.println(context.toString());
		System.out.println(v.toString());
		System.out.println(str);
		System.out.println("----------------------------"); 
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.popupshow_help, null);
		view.setBackgroundColor(Color.TRANSPARENT);
		if(popWin==null){
			popWin = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
		}

		View click_v = (View)v;
		int[] intXY = new int[2];
		System.out.println(intXY[0] + " 1: " + intXY[1]);
		click_v.getLocationOnScreen(intXY);
		System.out.println(intXY[0] + " 2: " + intXY[1]);
		
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
		
		int top = ((intXY[1] + click_v.getHeight()/2)+ Params.top );
		int left = ((intXY[0] + click_v.getWidth()/2)+ Params.left );
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
        
        TextView popupshow_yuan = (TextView)view.findViewById(R.id.popupshow_yuan);
        popupshow_yuan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popWin.dismiss();
				
				DataList item = DataListDBHelper.find_first();
				System.out.println("popwin click item =  " + item.toString());
				Intent intent = new Intent(context,DataItemListActivity.class);
				intent.putExtra("data_list_id",item.id);
				intent.putExtra("data_list_public", RequestCode.data_list_public);
				((Activity) context).startActivityForResult(intent, RequestCode.SHOW_BACK);
			}
		});
//        viewhelp.addView(view);
        
        popWin.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popWin.setOutsideTouchable(false); // 设置是否允许在外点击使其消失，到底有用没？ 
//		popWin.setAnimationStyle(R.style.AnimationPreview); // 设置动画 
        
        popWin.showAtLocation(v, Gravity.CENTER, 0, 0);
    }   
}
