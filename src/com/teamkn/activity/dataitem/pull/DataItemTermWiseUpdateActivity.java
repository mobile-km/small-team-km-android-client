package com.teamkn.activity.dataitem.pull;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.teamkn.widget.adapter.DataItemTermWiseUpdateAdapter;

public class DataItemTermWiseUpdateActivity extends TeamknBaseActivity{
	public static class RequestCode {
		public final static String CREATE = "CREATE";
		public final static String UPDATE = "UPDATE";
		public final static String REMOVE = "REMOVE";
		
		public final static String GET = "GET";
		public final static String ACCEPT = "ACCEPT";
		public final static String REJUST = "REJUST";
		
	}
	TextView data_list_title_tv;
	ListView list_view;
	ProgressBar progressBar;
	
	String[] seeds;
	DataList dataList_forked;
	DataList dataList_origin;
	
	List<DataItem> dataItems;
	public static DataItem dataItem;
	int committer_id;
	
	int count_forked;
	boolean is_first = true;
	public static DataItemTermWiseUpdateAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_termwise_update_list);
		Intent intent = getIntent();
		Integer dataList_forked_id = intent.getIntExtra("server_dataList_forked_id", -1);
		Integer dataList_origin_id = intent.getIntExtra("server_dataList_origin_id", -1);
		seeds = intent.getStringArrayExtra("seeds");
		dataList_forked = DataListDBHelper.find_by_server_data_list_id(dataList_forked_id);
		dataList_origin = DataListDBHelper.find_by_server_data_list_id(dataList_origin_id);
		
		committer_id = intent.getIntExtra("committer_id", -1);
		load_UI();
	}
	private void load_UI() {
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList_origin.title);
		list_view = (ListView)findViewById(R.id.list_view);
		api_load_list(RequestCode.GET);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		progressBar.setProgress(0);
	}
	private void api_load_list(final String action){
		if(BaseUtils.is_wifi_active(this)){
			new  TeamknAsyncTask<Void, Void, DataItem>(DataItemTermWiseUpdateActivity.this,"正在加载中") {
				@Override
				public DataItem do_in_background(Void... params) throws Exception {
					System.out.println("dataList_origin = "+dataList_origin.toString());
					System.out.println("dataList_forked = "+dataList_forked.toString());
					if(action.equals(RequestCode.GET)){
						dataItem = HttpApi.WatchList.next_commits(dataList_origin.server_data_list_id, committer_id);
					}else if(action.equals(RequestCode.ACCEPT)){
						dataItem = HttpApi.WatchList.accept_next_commit(dataList_origin.server_data_list_id, committer_id,dataItem);
					}else if(action.equals(RequestCode.REJUST)){
						dataItem = HttpApi.WatchList.reject_next_commit(dataList_origin.server_data_list_id, committer_id);
					}
					dataItems = DataItemDBHelper.all(dataList_origin.id);
//					dataItems.add(dataItem.getNext_commits_count(), dataItem);	
					return dataItem;
				}
				@Override
				public void on_success(DataItem result) {
					set_progressBar(result.getNext_commits_count());
					if(result.getTitle()!=null && result.getTitle()!=""){
						updateOrInsert_dataItem(result);
						load_list_view();
					}
				}
			}.execute();
		}else{
			BaseUtils.toast("无法连接网络");
		}
	}
	private List<DataItem> updateOrInsert_dataItem(DataItem dataItem){
		List<DataItem> new_dataItems = new ArrayList<DataItem>();
		if(dataItem.getOperation().equals(RequestCode.CREATE)){		
		}
		//dataItems.add(dataItem);
		// 修改
		for(int i = 0 ; i < dataItems.size() ; i++){
			if(dataItem.seed.equals(dataItems.get(i).seed)){
				
				System.out.println(" update or remove " + dataItem.toString());
				if(dataItem.getOperation().equals(RequestCode.REMOVE)){
					DataItem dataItem2 = dataItems.get(i);
					dataItem2.setOperation(RequestCode.REMOVE);
					DataItemDBHelper.delete_by_seed(dataItems.get(i).seed);
					new_dataItems.add(dataItem2);
				}else{
					new_dataItems.add(dataItem);
				}
				
			}else{
				System.out.println(" no change " + dataItem.toString());
				new_dataItems.add(dataItems.get(i));
			}
		}
		if(dataItem.getOperation().equals(RequestCode.CREATE)){
			System.out.println(" create " + dataItem.toString());
			new_dataItems.add(insert_data_item(dataItem), dataItem);
//			new_dataItems.add(dataItem);
			
		}
		dataItems = new_dataItems;
		return dataItems;
	}
	
	private int insert_data_item(DataItem item){
		int result = 0 ;
		for(int i = 0 ;i < seeds.length ;i ++){
			if(seeds[i] .equals(item.seed)){
				String before_seed = seeds[i-1];
				for(int j = 0 ; j < dataItems.size() ; j++){
					if(dataItems.get(j).seed.equals(before_seed)){
						result = j+1;
					}
				}
			}
		}
		return result;
	}
	
	private void load_list_view() {
		adapter = new DataItemTermWiseUpdateAdapter(this);
		try {
			adapter.add_items(dataItems);
			list_view.setAdapter(adapter);
//			adapter.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void set_progressBar(int next_commits_count){
		if(is_first){
			count_forked = next_commits_count;
			is_first = false;
			progressBar.setMax(next_commits_count);
		}
		progressBar.setProgress(next_commits_count);
		if(next_commits_count==0){
			this.finish();
		}
	}
	public void click_accept_button(View view){
		api_load_list(RequestCode.ACCEPT);
	}
	public void click_refuse_button(View view){
		api_load_list(RequestCode.REJUST);
	}
}
