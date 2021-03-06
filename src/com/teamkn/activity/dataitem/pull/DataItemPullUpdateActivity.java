package com.teamkn.activity.dataitem.pull;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.teamkn.model.User;
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
	User committer;
	
	Button accept_or_refuse_button;
	boolean accept_button = false;
	
	DataList dataList_origin;
	List<DataItem> data_items_origin;
	DataList dataList_forked;
	List<DataItem> dataItems_forked;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_pull_update_list);
		Intent intent = getIntent();
		dataList = (DataList) intent.getSerializableExtra("data_list");
		committer = (User) intent.getSerializableExtra("committer");
		load_UI();
	}
	
	private void load_UI() {
		data_item_old_button = (Button)findViewById(R.id.data_item_old_button);
		data_item_new_button = (Button)findViewById(R.id.data_item_new_button);
		
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		
		listView = (ListView) findViewById(R.id.list); 
		api_load_list();
		
		accept_or_refuse_button = (Button)findViewById(R.id.accept_or_refuse_button);
		accept_or_refuse_button.setText("完全保留我的列表");
		accept_or_refuse_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(accept_button){
					showDialog("此操作会导致对方的列表内容完全覆盖我现在的列表");
				}else{
					showDialog("此操作会完全忽略对方列表内容");
				}
			}
		});
	}
	private void showDialog(String msg){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("注意：");
		builder.setMessage(msg);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				api_accept_or_refuse(accept_button);
			}
		});
		builder.show();
	}
    private void api_load_list(){
    	if(BaseUtils.is_wifi_active(this)){
    		new TeamknAsyncTask<Void, Void, Void>(DataItemPullUpdateActivity.this,"正在加载中") {
				@SuppressWarnings("unchecked")
				@Override
				public Void do_in_background(Void... params) throws Exception {
					
					Map<Object, Object> map = HttpApi.WatchList.diff(dataList.server_data_list_id, committer.user_id);
					
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
		accept_or_refuse_button.setText("完全保留我的列表");
		accept_button = false;
		load_list_UI(data_items_origin);
	}
	public void click_new_button(View view){
		data_item_old_button.setBackgroundColor(getResources().getColor(R.color.gainsboro));
		data_item_new_button.setBackgroundColor(getResources().getColor(R.color.darkgrey));
		accept_or_refuse_button.setText("完全接受对方的列表");
		accept_button = true;
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
						api_dataList=HttpApi.WatchList.accept_commits(dataList.server_data_list_id, committer.user_id);	
//						DataItemDBHelper.delete_by_data_list_id(dataList.id);
					}else{
						api_dataList=HttpApi.WatchList.reject_commits(dataList.server_data_list_id, committer.user_id);
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
		intent.putExtra("committer",committer);
		intent.putExtra("data_list",dataList);
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
}
