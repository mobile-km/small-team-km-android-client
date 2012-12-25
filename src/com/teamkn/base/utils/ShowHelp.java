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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.MainActivity.RequestCode;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.activity.datalist.CreateDataListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;

public class ShowHelp {
	
	public static class Params{
		static int SHOW_TYPE ;
		
		public static final int SHOW_STEP_HELP =  0 ;
		public static final int SHOW_CREATE_HELP = 1 ;
		public static final int SHOW_NEXT_HELP =   2 ;
		public static final int SHOW_PUBLIC_HELP = 3 ;
		public static final int SHOW_COLLECTION_HELP = 4 ;
		
		static void setParam( int type,Context context ){
			SHOW_TYPE = type;
			
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
			int nowWidth = dm.widthPixels;
			int height = dm.heightPixels;
			boolean  is_big = false;
			if(nowWidth>400 && height > 700){
				is_big = true;
			}else{
				is_big = false;
			}
			switch (type) {
			case SHOW_STEP_HELP:
				setStep_Param(is_big);
				break;
			case SHOW_CREATE_HELP:
				setCreate_Param(is_big);
				break;
			case SHOW_NEXT_HELP:
				setNext_Param(is_big);
				break;
			case SHOW_PUBLIC_HELP:
				setPublic_Param(is_big);
				break;
			case SHOW_COLLECTION_HELP:
				setCollection_Param(is_big);
				break;
			default:
				break;
			}
		}
		
		static void setStep_Param(boolean is_big){
			if(is_big){
				Params.top =Params.top_480_800;
				Params.left =Params.left_480_800;
			}else{
				Params.top =Params.top_320_480;
				Params.left =Params.left_320_480;
			}
		}
		static void setCollection_Param(boolean is_big){
			if(is_big){
				Params.top =Params.top_480_800 + 140;
				Params.left =Params.left_480_800;
			}else{
				Params.top =Params.top_320_480 + 95;
				Params.left =Params.left_320_480;
			}
		}
		static void setPublic_Param(boolean is_big){
			if(is_big){
				Params.top =Params.public_top_480_800;
				Params.left =Params.public_left_480_800;
			}else{
				Params.top =Params.public_top_320_480;
				Params.left =Params.public_left_320_480;
			}
		}
		
		static void setCreate_Param(boolean is_big){
			if(is_big){
				Params.top =Params.create_top_480_800;
				Params.left =Params.create_left_480_800;
			}else{
				Params.top =Params.create_top_320_480;
				Params.left =Params.create_left_320_480;
			}
		}
		
		static void setNext_Param(boolean is_big){
			if(is_big){
				Params.top =Params.next_top_480_800;
				Params.left =Params.next_left_480_800;
			}else{
				Params.top =Params.next_top_320_480;
				Params.left =Params.next_left_320_480;
			}
		}
		
		static final int top_320_480 = -150;
		static final int left_320_480 = -200;
		static final int top_480_800 = -270;
		static final int left_480_800 = -250;
		
		static final int public_top_320_480 = 50;
		static final int public_left_320_480 = -35;
		static final int public_top_480_800 = 80;
		static final int public_left_480_800 = -35;
		
		static final int create_top_320_480 = -50;
		static final int create_left_320_480 = -48;
		static final int create_top_480_800 = -50;
		static final int create_left_480_800 = -80;
		
		static final int next_top_320_480 = 325;
		static final int next_left_320_480 = 230;
		static final int next_top_480_800 = 570;
		static final int next_left_480_800 = 340;
		
		static int top = top_320_480;
		static int left = left_320_480;
	}
	static PopupWindow popWin;

	public static View getView(int type,Context context){
		Params.setParam(type,context );
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = null;
		if(popWin!=null){
			popWin.dismiss();
		}
		switch (type) {
		case Params.SHOW_STEP_HELP:
			view = inflater.inflate(R.layout.popupshow_help, null);
			break;
		case Params.SHOW_CREATE_HELP:
	    	view  = inflater.inflate(R.layout.popupshow_create_help, null);
	    	break;
		case Params.SHOW_NEXT_HELP:
			view = inflater.inflate(R.layout.popupshow_next_help, null);
			break;
		case Params.SHOW_PUBLIC_HELP:
			view = inflater.inflate(R.layout.popupshow_public_help, null);
			break;
		case Params.SHOW_COLLECTION_HELP:
			view = inflater.inflate(R.layout.popupshow_help, null);
			break;
		default:
			break;
		}
		if(view!=null){
			view.setBackgroundColor(Color.TRANSPARENT);
			popWin = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
		}
		return view;
	}
	
	
	public static void showHelp(int show_type,final Context context,View v,String str){
		View view = getView(show_type,context);

		View click_v = (View)v;
		int[] intXY = new int[2];
		System.out.println(intXY[0] + " 1: " + intXY[1]);
		click_v.getLocationOnScreen(intXY);
		System.out.println(intXY[0] + " 2: " + intXY[1]);
		
		RelativeLayout popupshow_str_top_rl = (RelativeLayout)view.findViewById(R.id.popupshow_str_top_rl);
		RelativeLayout popupshow_str_but_rl = (RelativeLayout)view.findViewById(R.id.popupshow_str_but_rl);
		TextView popupshow_str_top = (TextView)view.findViewById(R.id.popupshow_str_top);
		TextView popupshow_str_but = (TextView)view.findViewById(R.id.popupshow_str_but);
		Button popupshow_cancel_prompt_top = (Button)view.findViewById(R.id.popupshow_cancel_prompt_top);
		Button popupshow_not_prompt_top = (Button)view.findViewById(R.id.popupshow_not_prompt_top);
		Button popupshow_cancel_prompt_but = (Button)view.findViewById(R.id.popupshow_cancel_prompt_but);
		Button popupshow_not_prompt_but = (Button)view.findViewById(R.id.popupshow_not_prompt_but);
		popupshow_cancel_prompt_top.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				popWin.dismiss();
			}
		});
		popupshow_cancel_prompt_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popWin.dismiss();
			}
		});
		popupshow_not_prompt_top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				set_Help_Show(context);
			}
		});
		popupshow_not_prompt_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				set_Help_Show(context);
			}
			
		});
		
		ImageView popupshow_image_top = (ImageView)view.findViewById(R.id.popupshow_image_top);
		ImageView popupshow_image_but = (ImageView)view.findViewById(R.id.popupshow_image_but);
		
		Display d = ((Activity) context).getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		d.getMetrics(dm);
		if(intXY[1] > d.getHeight()/2 ){
			popupshow_str_top.setText(str);
			popupshow_str_top_rl.setVisibility(View.VISIBLE);
			popupshow_str_but_rl.setVisibility(View.GONE);
			popupshow_image_top.setVisibility(View.VISIBLE);
		}else{
			popupshow_str_but.setText(str);
			popupshow_str_top_rl.setVisibility(View.GONE);
			popupshow_str_but_rl.setVisibility(View.VISIBLE);
			popupshow_image_but.setVisibility(View.VISIBLE);
		}
		popupshow_image_top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popWin.dismiss();
			}
		});
       
		popupshow_image_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popWin.dismiss();
			}
		});
		
		int top = ((intXY[1] + click_v.getHeight()/2)+Params.top );
		int left = ((intXY[0] + click_v.getWidth()/2)+Params.left );
		System.out.println("left "+left + " : " + (intXY[0] + click_v.getWidth()/2) + " : " + Params.left);
		System.out.println("top  "+top + " : " +(intXY[1] + click_v.getHeight()/2) + " : " + Params.top);
		if(left<=0){
			left = 0;
		}
		LinearLayout help_top = (LinearLayout)view.findViewById(R.id.linearlayout_help_top);
        ImageView iv_top = new ImageView(context);
        iv_top.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.translucence_cube));
        iv_top.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, top));
        if(Params.SHOW_TYPE !=  Params.SHOW_CREATE_HELP){
        	help_top.addView(iv_top); 
        } 
        
        LinearLayout help_left = (LinearLayout)view.findViewById(R.id.linearlayout_help_left);
        ImageView iv_left = new ImageView(context);
        if(Params.SHOW_TYPE !=  Params.SHOW_CREATE_HELP &&  Params.SHOW_TYPE !=  Params.SHOW_NEXT_HELP){
        	iv_left.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.translucence_cube));
        }
        iv_left.setLayoutParams(new LayoutParams(left, 100));
        help_left.addView(iv_left);
        
        TextView popupshow_yuan = (TextView)view.findViewById(R.id.popupshow_yuan);
        popupshow_yuan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickFocus(context);
			}
		});
//        viewhelp.addView(view);
        
        popWin.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popWin.setOutsideTouchable(false); // 设置是否允许在外点击使其消失，到底有用没？ 
//		popWin.setAnimationStyle(R.style.AnimationPreview); // 设置动画 
        
        popWin.showAtLocation(v, Gravity.CENTER, 0, 0);
	}
	public static void onClickFocus(Context context) {
		popWin.dismiss();
		switch (Params.SHOW_TYPE) {
		case Params.SHOW_STEP_HELP:
			Intent intent = new Intent(context,DataItemListActivity.class);
			intent.putExtra("data_list",MainActivity.datalists.get(0));
			intent.putExtra("data_list_public", RequestCode.data_list_public);
			((Activity) context).startActivityForResult(intent, RequestCode.SHOW_BACK);
			break;
		case Params.SHOW_CREATE_HELP:
			Intent create_intent = new Intent(context,CreateDataListActivity.class);
			((Activity) context).startActivityForResult(create_intent, RequestCode.SHOW_BACK);
			break;
		case Params.SHOW_NEXT_HELP:
			MainActivity.RequestCode.SHOW_HELP = MainActivity.RequestCode.SHOW_NOT_HELP;
			break;
		case Params.SHOW_PUBLIC_HELP:
			Intent public_intent = new Intent(context,MainActivity.class);
			public_intent.putExtra("data_list_type",RequestCode.data_list_type);
			RequestCode.data_list_public = MainActivity.RequestCode.被协作列表;
			public_intent.putExtra("data_list_public", RequestCode.data_list_public);
			((Activity) context).startActivity(public_intent);
			((Activity) context).finish();
			break;
		case Params.SHOW_COLLECTION_HELP:
			Intent show_collection_intent = new Intent(context,DataItemListActivity.class);
			show_collection_intent.putExtra("data_list",MainActivity.datalists.get(1));
			show_collection_intent.putExtra("data_list_public", RequestCode.data_list_public);
			((Activity) context).startActivityForResult(show_collection_intent, RequestCode.SHOW_BACK);
			break;
		default:
			break;
		}
	}
	
	class CancelOrNotPromptClick implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.popupshow_cancel_prompt_top:
			case R.id.popupshow_cancel_prompt_but:
				
				break;
			case R.id.popupshow_not_prompt_top:
			case R.id.popupshow_not_prompt_but:
				
			break;
			default:
				break;
			}	
		}	
	}

	private static void set_Help_Show(final Context context){
		if (BaseUtils.is_wifi_active(context)) {
	    	new TeamknAsyncTask<Void, Void, Boolean>((TeamknBaseActivity)context,"正在处理") {
				@Override
				public Boolean do_in_background(Void... params)
						throws Exception {
					HttpApi.change_show_tip(false);	
					((TeamknBaseActivity)context).current_user().setIs_show_tip(false);
					return null;
				}
				@Override
				public void on_success(Boolean v) {
					popWin.dismiss();
				}
			}.execute();
    	}else{
			BaseUtils.toast("无法连接到网络，请检查网络配置");
		}
    }	
}
