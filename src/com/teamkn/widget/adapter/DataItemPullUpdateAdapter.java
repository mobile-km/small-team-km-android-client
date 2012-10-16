package com.teamkn.widget.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.DataItem;

public class DataItemPullUpdateAdapter extends TeamknBaseAdapter<DataItem>{
	Context context;
	public DataItemPullUpdateAdapter(TeamknBaseActivity activity) {
		super(activity);
		context = activity;
	}

	@Override
	public View inflate_view() {
		return inflate(R.layout.list_data_item_pull_update_list_item, null);
	}

	@Override
	public com.teamkn.base.adapter.TeamknBaseAdapter.BaseViewHolder build_view_holder(
			View view) {
		ViewHolder holder = new ViewHolder();
		holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
		holder.data_item_rl = (RelativeLayout) view.findViewById(R.id.data_item_rl);
		holder.data_item_title_tv = (TextView) view.findViewById(R.id.data_item_title_tv);
		return holder;
	}

	@Override
	public void fill_with_data(
			com.teamkn.base.adapter.TeamknBaseAdapter.BaseViewHolder holder,
			DataItem item, int position) {
		ViewHolder view_holder = (ViewHolder)holder;
		view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
		view_holder.data_item_title_tv.setText(item.title);
		if(item.id < 2){ //增加
			view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.green));
		}else if(item.id < 4){ // 删除
			view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.red));
		}else if(item.id < 6){  // 修改
			view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.gray));
		}else if(item.id < 8){  // 移动
			view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.yellow));
		}else{  //正常
			view_holder.data_item_rl.setBackgroundColor(context.getResources().getColor(R.color.gainsboro));
		}
	}
   private class ViewHolder implements BaseViewHolder {
    	TextView info_tv;
    	// 个人列表子项显示
    	RelativeLayout data_item_rl;  
        TextView data_item_title_tv;
  }
}
