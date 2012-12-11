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
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.User;
import com.teamkn.widget.adapter.DataItemPullAdapter;

public class DataItemPullListActivity extends TeamknBaseActivity{
	DataList dataList;
	ListView pull_listview;
	List<User> userList;
	public static class RequestCode {
		public final static int CREATE_DATA_LIST = 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_pull_list);
		Intent intent = getIntent();
		dataList = (DataList) intent.getSerializableExtra("data_list");
		TextView data_list_title_tv = (TextView) findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		pull_listview = (ListView)findViewById(R.id.pull_listview);
		
		api_load_list();
	}
	private void api_load_list(){
		new TeamknAsyncTask<Void, Void, List<User>>(DataItemPullListActivity.this,"正在加载中") {
			@Override
			public List<User> do_in_background(Void... params) throws Exception {
				userList = HttpApi.WatchList.commit_meta_list(dataList);
				return userList;
			}
			@Override
			public void on_success(List<User> result) {
				if(userList!=null && userList.size()>0){
					load_listview();
				}else{
					BaseUtils.toast("处理完成");
					DataItemPullListActivity.this.finish();
				}
			}
		}.execute();
	}
	private void load_listview() {
		try {
			DataItemPullAdapter adapter = new DataItemPullAdapter(this);
			adapter.add_items(userList);
			pull_listview.setAdapter(adapter);
			pull_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> list_view, View list_item,
						int item_id, long position) {
					TextView info_tv = (TextView) list_item.findViewById(R.id.info_tv);
					User item = (User) info_tv.getTag(R.id.tag_note_uuid);
					Intent intent = new Intent(DataItemPullListActivity.this,DataItemPullUpdateActivity.class);
					intent.putExtra("data_list",dataList);
					intent.putExtra("committer",item);

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
			api_load_list();
			break;
		}
	}
}
