package com.mindpin.widget.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.application.MindpinApplication;
import com.mindpin.model.Collection;

public class SelectCollectionListAdapter extends BaseAdapter {
	private List<Collection> collections;
	private List<Integer> select_collection_ids;

	public SelectCollectionListAdapter(List<Collection> collections, List<Integer> select_collection_ids) {
		this.collections = collections;
		this.select_collection_ids = select_collection_ids;
	}

	@Override
	public int getCount() {
		return collections.size();
	}

	@Override
	public Object getItem(int position) {
		return collections.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Collection collection = collections.get(position);
		convertView = generate_view_holder(convertView);

		ViewHolder view_holder = (ViewHolder) convertView.getTag();
		fill_with_collection_data(view_holder, collection);
		bind_checkbox(convertView,view_holder, collection);
		return convertView;
	}
	
	public void select_item(View convertView,int position){
		ViewHolder view_holder = (ViewHolder)convertView.getTag();
		CheckBox cb = view_holder.check_box;
		Integer id = collections.get(position).collection_id;
		
		if(cb.isChecked()){
			cb.setChecked(false);
			convertView.setBackgroundColor(android.R.color.black);
			select_collection_ids.remove(id);
		}else{
			cb.setChecked(true);
			convertView.setBackgroundColor(R.color.darkgray);
			select_collection_ids.add(id);
		}
	}
	
	public void add_item(Collection collection){
		Integer id = collection.collection_id;
		select_collection_ids.add(id);
		collections.add(collection);
		notifyDataSetChanged();
	}

	private void bind_checkbox(View convertView, ViewHolder view_holder, Collection collection) {

		if (select_collection_ids != null
				&& select_collection_ids.indexOf(collection.collection_id) != -1) {
			view_holder.check_box.setChecked(true);
			convertView.setBackgroundColor(R.color.darkgray);
		} else {
			view_holder.check_box.setChecked(false);
			convertView.setBackgroundColor(android.R.color.transparent);
		}
	}

	private View generate_view_holder(View convertView) {
		if (null == convertView) {
			ViewHolder view_holder = new ViewHolder();
			convertView = MindpinApplication.inflate(R.layout.select_collection_item,
					null);
			view_holder.id_tv = (TextView) convertView
					.findViewById(R.id.collection_id);
			view_holder.title_tv = (TextView) convertView
					.findViewById(R.id.collection_title);
			view_holder.check_box = (CheckBox) convertView
					.findViewById(R.id.check_box);
			convertView.setTag(view_holder);
		}
		return convertView;
	}

	private void fill_with_collection_data(ViewHolder view_holder,
			Collection collection) {
		view_holder.id_tv.setText(collection.collection_id + "");
		view_holder.title_tv.setText(collection.title);
	}
	
	private class ViewHolder {
		public CheckBox check_box;
		public TextView id_tv;
		public TextView title_tv;
	}

}
