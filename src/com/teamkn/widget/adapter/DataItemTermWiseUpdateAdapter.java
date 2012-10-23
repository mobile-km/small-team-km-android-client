package com.teamkn.widget.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.dataitem.pull.DataItemTermWiseUpdateActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.DataItem;

public class DataItemTermWiseUpdateAdapter extends TeamknBaseAdapter<DataItem>{
	Context context;
	public DataItemTermWiseUpdateAdapter(TeamknBaseActivity activity) {
		super(activity);
		context = activity;
	}

	@Override
	public View inflate_view() {
		return inflate(R.layout.list_data_item_termwise_update_list_item, null);
	}

	@Override
	public com.teamkn.base.adapter.TeamknBaseAdapter.BaseViewHolder build_view_holder(
			View view) {
		ViewHolder holder = new ViewHolder();
		holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
		holder.data_item_rl = (RelativeLayout) view.findViewById(R.id.data_item_rl);
		holder.data_item_title_tv = (TextView) view.findViewById(R.id.data_item_title_tv);
		holder.data_item_update_iv = (ImageView) view.findViewById(R.id.data_item_update_iv);
		return holder;
	}

	@Override
	public void fill_with_data(
			com.teamkn.base.adapter.TeamknBaseAdapter.BaseViewHolder holder,
			final DataItem item, int position) {
		System.out.println("dataItem = " + item.toString());
		final ViewHolder view_holder = (ViewHolder)holder;
		view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
		
		if(item.getOperation()!=null){
			view_holder.data_item_update_iv.setVisibility(View.VISIBLE);
			if(item.getOperation().equals(DataItemTermWiseUpdateActivity.RequestCode.CREATE) ){ //增加
				view_holder.data_item_title_tv.setText(item.title);
				view_holder.data_item_update_iv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mi_add));
				view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.green));
			}else if(item.getOperation().equals(DataItemTermWiseUpdateActivity.RequestCode.REMOVE) ){ // 删除
				view_holder.data_item_title_tv.setText(item.title);
				view_holder.data_item_update_iv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mi_cut));
				view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.red));
			}else if(item.getOperation().equals(DataItemTermWiseUpdateActivity.RequestCode.UPDATE) ){  // 修改
//				AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
//		    	alphaAnimation.setDuration(2000);
//		    	alphaAnimation.setFillEnabled(true);
////		    	alphaAnimation.setFillBefore(true);
//		    	view_holder.data_item_title_tv.startAnimation(alphaAnimation);
//		    	alphaAnimation.setAnimationListener(new AnimationListener() {
//					@Override
//					public void onAnimationStart(Animation animation) {	
//						view_holder.data_item_title_tv.setText("修改之前。。。。。。。");
//					}
//					@Override
//					public void onAnimationRepeat(Animation animation) {
//					}
//					@Override
//					public void onAnimationEnd(Animation animation) {
						view_holder.data_item_title_tv.setText(item.title);
//					}
//				});
				view_holder.data_item_update_iv.setVisibility(View.GONE);
				view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.gray));
			}
			else {  // 移动
				view_holder.data_item_title_tv.setText(item.title);
				view_holder.data_item_update_iv.setVisibility(View.GONE);
				view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.yellow));
			}
		}else{  //正常
			view_holder.data_item_title_tv.setText(item.title);
			view_holder.data_item_update_iv.setVisibility(View.GONE);
			view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.gainsboro));
		}
		
   }
   
   private class ViewHolder implements BaseViewHolder {
    	TextView info_tv;
    	// 个人列表子项显示
    	RelativeLayout data_item_rl;  
        TextView data_item_title_tv;
        ImageView data_item_update_iv;
  }
}
