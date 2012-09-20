package com.teamkn.widget.adapter;
import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;

public class GroupAdapter extends TeamknBaseAdapter<String> {

	public GroupAdapter(TeamknBaseActivity activity) {
		super(activity);
	}
	@Override
	public View inflate_view() {
		return inflate(R.layout.group_item_view, null);
	}
	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
        view_holder.groupItem = (TextView) view.findViewById(R.id.groupItem); 
        return view_holder;
	}

	@Override
	public void fill_with_data(BaseViewHolder holder,String item, int position) {
		 ViewHolder view_holder = (ViewHolder) holder;
		 view_holder.groupItem.setText(item);
	}  
//	groupItem  
	private class ViewHolder implements BaseViewHolder {
        TextView groupItem;
    }
}