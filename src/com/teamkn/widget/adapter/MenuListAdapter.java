package com.teamkn.widget.adapter;

import java.util.Map;

import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;

public class MenuListAdapter extends TeamknBaseAdapter<Map<String,Object>>{
	public MenuListAdapter(TeamknBaseActivity activity) {
		super(activity);
	}
	
	@Override
	public View inflate_view() {
		return inflate(R.layout.tkn_nav_list_item, null);
	}
	
	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
		
        view_holder.list_title_tv = (TextView) view.findViewById(R.id.list_title_tv);
        return view_holder;
	}
	
	@Override
	public void fill_with_data(BaseViewHolder holder,Map<String, Object> item, int position) {		
		 ViewHolder view_holder = (ViewHolder) holder;
	     view_holder.list_title_tv.setText(item.get("title").toString());
	}
	
	private class ViewHolder implements BaseViewHolder {
		TextView list_title_tv;
    }
}
