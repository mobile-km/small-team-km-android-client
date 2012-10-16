package com.teamkn.activity.dataitem.pull;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.widget.adapter.DataItemPullUpdateAdapter;

public class DataItemPullUpdateActivity extends TeamknBaseActivity{
	Button data_item_old_button;
	Button data_item_new_button;
	TextView data_list_title_tv;
	ListView listView;
	DataList dataList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_pull_update_list);
		Intent intent = getIntent();
		Integer data_list_id = intent.getIntExtra("data_list_id", -1);
		dataList = DataListDBHelper.find(data_list_id);
		load_UI();
	}
	
	private void load_UI() {
		data_item_old_button = (Button)findViewById(R.id.data_item_old_button);
		data_item_new_button = (Button)findViewById(R.id.data_item_new_button);
		
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		
		listView = (ListView) findViewById(R.id.list); 
		load_list_UI();
	}

	private void load_list_UI() {
		DataItemPullUpdateAdapter adapter = new DataItemPullUpdateAdapter(this);
		try {
			List<DataItem> dataItems = DataItemDBHelper.all(dataList.id);
			adapter.add_items(dataItems);
			listView.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void click_old_button(View view){
		data_item_old_button.setBackgroundColor(getResources().getColor(R.color.darkgrey));
		data_item_new_button.setBackgroundColor(getResources().getColor(R.color.gainsboro));
	}
	public void click_new_button(View view){
		data_item_old_button.setBackgroundColor(getResources().getColor(R.color.gainsboro));
		data_item_new_button.setBackgroundColor(getResources().getColor(R.color.darkgrey));
	}
	public void click_accept_button(View view){
		
	}
	public void click_refuse_button(View view){
		
	}
	public void click_termwise_button(View view){
		Intent intent = new Intent(DataItemPullUpdateActivity.this,DataItemTermWiseUpdateActivity.class);
		intent.putExtra("data_list_id", dataList.id);
		startActivity(intent);
	}	
}
