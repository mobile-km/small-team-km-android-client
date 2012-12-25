package com.teamkn.widget.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.model.DataItem;

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

		view_holder.data_item_title_tv.setText(dataItems.get(position).title);
		view_holder.data_item_title_tv_go.setText(dataItems.get(position).id
				+ "");
		view_holder.data_item_title_iv_frush = (ImageView)row.findViewById(R.id.data_item_title_iv_frush);
		view_holder.data_item_title_iv_frush.setVisibility(View.VISIBLE);

		System.out.println("aa" + dataItems.get(position).toString());
		return row;
	}

	private class ViewHolder {
		TextView data_item_title_tv;
		TextView data_item_title_tv_go;

		TextView data_item_info_tv;
		
		ImageView data_item_title_iv_frush;
	}
}
