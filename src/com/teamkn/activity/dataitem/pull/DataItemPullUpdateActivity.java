package com.teamkn.activity.dataitem.pull;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.widget.adapter.DataItemPullUpdateAdapter;

public class DataItemPullUpdateActivity extends TeamknBaseActivity{
	public static class RequestCode{
		public final static int BACK = 0;
	}
	Button data_item_old_button;
	Button data_item_new_button;
	TextView data_list_title_tv;
	ListView listView;
	DataList dataList;
	int committer_id;
	
	DataList dataList_origin;
	List<DataItem> data_items_origin;
	DataList dataList_forked;
	List<DataItem> dataItems_forked;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_pull_update_list);
		Intent intent = getIntent();
		Integer data_list_id = intent.getIntExtra("data_list_id", -1);
		committer_id = intent.getIntExtra("committer_id", -1);
		dataList = DataListDBHelper.find(data_list_id);
		load_UI();
	}
	
	private void load_UI() {
		data_item_old_button = (Button)findViewById(R.id.data_item_old_button);
		data_item_new_button = (Button)findViewById(R.id.data_item_new_button);
		
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		
		listView = (ListView) findViewById(R.id.list); 
		api_load_list();
	}
    private void api_load_list(){
    	if(BaseUtils.is_wifi_active(this)){
    		new TeamknAsyncTask<Void, Void, Void>(DataItemPullUpdateActivity.this,"正在加载中") {
				@SuppressWarnings("unchecked")
				@Override
				public Void do_in_background(Void... params) throws Exception {
					System.out.println(dataList.server_data_list_id +"  :  "+ committer_id);
					
					Map<Object, Object> map = HttpApi.WatchList.diff(dataList.server_data_list_id, committer_id);
					
					dataList_origin = (DataList) map.get("dataList_origin");
					System.out.println(dataList_origin.toString());
					data_items_origin = (List<DataItem>) map.get("data_items_origin");
					System.out.println(data_items_origin.size());
					
					dataList_forked = (DataList) map.get("dataList_forked");
					System.out.println(dataList_forked.toString());
					dataItems_forked = (List<DataItem>) map.get("dataItems_forked");
					System.out.println(dataItems_forked.size());
					return null;
				}
				@Override
				public void on_success(Void result) {	
					load_list_UI(data_items_origin);
				}
			}.execute();
    	}else{
    		BaseUtils.toast("无法连接网络");
    	}
    }
	private void load_list_UI(List<DataItem> dataItems) {
		DataItemPullUpdateAdapter adapter = new DataItemPullUpdateAdapter(this);
		try {
			adapter.add_items(dataItems);
			listView.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void click_old_button(View view){
		data_item_old_button.setBackgroundColor(getResources().getColor(R.color.darkgrey));
		data_item_new_button.setBackgroundColor(getResources().getColor(R.color.gainsboro));
		load_list_UI(data_items_origin);
	}
	public void click_new_button(View view){
		data_item_old_button.setBackgroundColor(getResources().getColor(R.color.gainsboro));
		data_item_new_button.setBackgroundColor(getResources().getColor(R.color.darkgrey));
		load_list_UI(dataItems_forked);
	}
	public void click_accept_button(View view){
		api_accept_or_refuse(true);
	}
	public void click_refuse_button(View view){
		api_accept_or_refuse(false);
	}
	private void api_accept_or_refuse(final boolean do_accept){
		if(BaseUtils.is_wifi_active(this)){
			new TeamknAsyncTask<Void, Void, DataList>(DataItemPullUpdateActivity.this,"正在处理中") {
				@Override
				public DataList do_in_background(Void... params)
						throws Exception {
					DataList api_dataList = null;
					if(do_accept){
						api_dataList=HttpApi.WatchList.accept_commits(dataList.server_data_list_id, committer_id);	
						DataItemDBHelper.delete_by_data_list_id(dataList.id);
					}else{
						api_dataList=HttpApi.WatchList.reject_commits(dataList.server_data_list_id, committer_id);
					}
					return api_dataList;
				}
				@Override
				public void on_success(DataList result) {	
					if(result != null){
						BaseUtils.toast("操作成功");
						DataItemPullUpdateActivity.this.finish();
					}else{
						
					}
				}
			}.execute();
		}else{
			BaseUtils.toast("无法连接网络");
		}
	}
	public void click_termwise_button(View view){
		Intent intent = new Intent(DataItemPullUpdateActivity.this,DataItemTermWiseUpdateActivity.class);
		intent.putExtra("server_dataList_forked_id",dataList_forked.server_data_list_id);
		intent.putExtra("server_dataList_origin_id", dataList_origin.server_data_list_id);
		intent.putExtra("committer_id",committer_id);
		intent.putExtra("seeds", getDataItems_forked_Seeds());
		startActivityForResult(intent, RequestCode.BACK);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Activity.RESULT_OK){
			return;
		}
		switch (requestCode) {
		case RequestCode.BACK:
			load_UI();
			break;
		default:
			break;
		}
	}
	private String[] getDataItems_forked_Seeds(){
		String[] seeds = new String[dataItems_forked.size()];
		for(int i = 0 ; i < dataItems_forked.size() ;i ++){
			seeds[i] = dataItems_forked.get(i).seed;
		}
		return seeds;
	}
}
