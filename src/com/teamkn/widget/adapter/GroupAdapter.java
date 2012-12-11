package com.teamkn.widget.adapter;
import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
//http://image.baidu.com/i?tn=listdetail&ie=utf8&pn=12&start=0&result=30&word=%E7%BE%8E%E5%A5%B3%20%E5%85%A8%E9%83%A8&tag1=%E7%BE%8E%E5%A5%B3&tag2=%E5%85%A8%E9%83%A8&ln=11586&rn=1&ct=503316480#pn12&-1&di5561337636089409959&objURLhttp%3A%2F%2Ft3.baidu.com%2Fit%2Fu%3D3376186993%2C668011507%26fm%3D24%26gp%3D0.jpg&fromURLhttp%3A%2F%2Fhuaban.com%2Fpins%2F10548759%2F&W440&H659&T8888&S60&TPjpg
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