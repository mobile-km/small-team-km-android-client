package com.teamkn.activity.dataitem.pull;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.MainActivity.RequestCode;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.widget.adapter.DataItemPullAdapter;

public class DataItemPullListActivity extends TeamknBaseActivity{
	DataList dataList;
	ListView pull_listview;
	
	public static class RequestCode {
		public final static int CREATE_DATA_LIST = 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_pull_list);
		Intent intent = getIntent();
		Integer data_item_id = intent.getIntExtra("data_list_id", -1);
		if(data_item_id>=0){
			dataList = DataListDBHelper.find(data_item_id);
		}
		TextView data_list_title_tv = (TextView) findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		pull_listview = (ListView)findViewById(R.id.pull_listview);
		load_listview();
	}
	private void load_listview() {
		try {
			List<DataList> itemList = DataListDBHelper.all("ALL","true");
			DataItemPullAdapter adapter = new DataItemPullAdapter(this);
			adapter.add_items(itemList);
			pull_listview.setAdapter(adapter);
			pull_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> list_view, View list_item,
						int item_id, long position) {
					TextView info_tv = (TextView) list_item.findViewById(R.id.info_tv);
					DataList item = (DataList) info_tv.getTag(R.id.tag_note_uuid);
					Intent intent = new Intent(DataItemPullListActivity.this,DataItemPullUpdateActivity.class);
					intent.putExtra("data_list_id",item.id);

					System.out.println("mainactivity setonclick  = " +item.toString());
					startActivityForResult(intent, RequestCode.CREATE_DATA_LIST);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case RequestCode.CREATE_DATA_LIST:
//			BaseUtils.toast("RequestCode.CREATE_DATA_ITEM    "
//					+ RequestCode.CREATE_DATA_LIST);
//			load_listview();
			break;
		}
	}
}
