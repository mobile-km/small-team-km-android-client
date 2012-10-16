package com.teamkn.activity.dataitem.pull;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.widget.adapter.DataItemTermWiseUpdateAdapter;

public class DataItemTermWiseUpdateActivity extends TeamknBaseActivity{
	TextView data_list_title_tv;
	ListView list_view;
	ProgressBar progressBar;
	DataList dataList;
	public static DataItemTermWiseUpdateAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_termwise_update_list);
		Intent intent = getIntent();
		Integer data_list_id = intent.getIntExtra("data_list_id", -1);
		dataList = DataListDBHelper.find(data_list_id);
		load_UI();
	}
	private void load_UI() {
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		list_view = (ListView)findViewById(R.id.list_view);
		load_list_view();
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		progressBar.setProgress(0);
	}
	private void load_list_view() {
		adapter = new DataItemTermWiseUpdateAdapter(this);
		try {
			List<DataItem> dataItems = DataItemDBHelper.all(dataList.id);
			adapter.add_items(dataItems);
			list_view.setAdapter(adapter);
//			adapter.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void click_progressBar(View view){
	    int progress = progressBar.getProgress()+25;
		progressBar.setProgress(progress);
	}
}
