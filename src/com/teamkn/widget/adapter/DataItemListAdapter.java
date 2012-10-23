package com.teamkn.widget.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.model.DataItem;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.model.database.UserDBHelper;

public class DataItemListAdapter extends ArrayAdapter<DataItem> {
	ArrayList<DataItem> dataItems;
	Activity activity;

	public DataItemListAdapter(Activity activity, int id,
			List<DataItem> dataItems2) {
		super(activity, id, dataItems2);
		this.dataItems = (ArrayList<DataItem>) dataItems2;
		this.activity = activity;
	}

	public ArrayList<DataItem> getList() {
		return dataItems;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			row = inflater.inflate(R.layout.list_data_item_list_item, parent,
					false);
		}
		ViewHolder view_holder = new ViewHolder();
		view_holder.data_item_info_tv = (TextView) row
				.findViewById(R.id.data_item_info_tv);
		view_holder.data_item_info_tv.setTag(R.id.tag_note_uuid, dataItems.get(position));
		
		view_holder.data_item_title_tv = (TextView) row
				.findViewById(R.id.data_item_title_tv);
		view_holder.data_item_title_tv_go = (TextView) row
				.findViewById(R.id.data_item_title_tv_go);
		view_holder.show_relativelayout = (RelativeLayout) row
				.findViewById(R.id.show_relativelayout);

		view_holder.data_item_title_tv.setText(dataItems.get(position).title);
		view_holder.data_item_title_tv_go.setText(dataItems.get(position).id
				+ "");
		view_holder.data_item_title_iv_frush = (ImageView)row.findViewById(R.id.data_item_title_iv_frush);
		if(UserDBHelper.find(DataListDBHelper.find(dataItems.get(position).data_list_id).user_id).user_id == AccountManager.current_user().user_id
				|| DataItemListActivity.is_fork){
			view_holder.data_item_title_iv_frush.setVisibility(View.VISIBLE);
		}else{
			view_holder.data_item_title_iv_frush.setVisibility(View.GONE);
		}
		System.out.println("aa" + dataItems.get(position).toString());
		return row;
	}

	private class ViewHolder {
		TextView data_item_title_tv;
		TextView data_item_title_tv_go;

		RelativeLayout show_relativelayout;
		TextView data_item_info_tv;
		
		ImageView data_item_title_iv_frush;
	}
}
