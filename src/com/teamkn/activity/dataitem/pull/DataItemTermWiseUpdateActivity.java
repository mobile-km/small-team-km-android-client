package com.teamkn.activity.dataitem.pull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
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
import com.teamkn.model.User;
import com.teamkn.widget.adapter.DataItemTermWiseUpdateAdapter;

public class DataItemTermWiseUpdateActivity extends TeamknBaseActivity{
	public static class RequestCode {
		public final static String CREATE = "CREATE";
		public final static String UPDATE = "UPDATE";
		public final static String REMOVE = "REMOVE";
		public final static String ORDER  = "ORDER";
		
		public final static String GET    = "GET";
		public final static String ACCEPT = "ACCEPT";
		public final static String REJUST = "REJUST";
		
	}
	
	TextView data_item_termwise_title_tv;
	TextView data_list_title_tv;
	
	ListView list_view;
	Button accept_button;
	ProgressBar progressBar;
	
	User committer;
	DataList dataList;
	
	Map<Object, Object> map ;
	
	List<DataItem> dataItems;
	List<DataItem> recordItems = new ArrayList<DataItem>();
	DataItem dataItem;

	int count_forked;
	boolean is_first = true;
	DataItemTermWiseUpdateAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_termwise_update_list);
		
		Intent intent = getIntent();
		committer = (User) intent.getSerializableExtra("committer");
		dataList = (DataList) intent.getSerializableExtra("data_list");
		is_first = true;
		load_UI();
	}
	private void load_UI() {
		data_item_termwise_title_tv = (TextView)findViewById(R.id.data_item_termwise_title_tv);
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		list_view = (ListView)findViewById(R.id.list_view);
		api_load_list(RequestCode.GET);
		accept_button = (Button)findViewById(R.id.accept_button);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		progressBar.setProgress(0);
	}
	private void api_load_list(final String action){
		if(BaseUtils.is_wifi_active(this)){
			new  TeamknAsyncTask<Void, Void, Map<Object, Object>>(DataItemTermWiseUpdateActivity.this,"正在加载中") {
				@Override
				public Map<Object, Object> do_in_background(Void... params) throws Exception {
					System.out.println(" --  "  +  dataList.server_data_list_id + "  : " + committer.user_id) ;
					if(action.equals(RequestCode.GET)){
						map = HttpApi.WatchList.next_commits(dataList.server_data_list_id, committer.user_id);
					}else if(action.equals(RequestCode.ACCEPT)){
						map = HttpApi.WatchList.accept_next_commit(dataList.server_data_list_id, committer.user_id);
					}else if(action.equals(RequestCode.REJUST)){
						map = HttpApi.WatchList.reject_next_commit(dataList.server_data_list_id, committer.user_id);
					}
					return map;
				}
				@SuppressWarnings("unchecked")
				@Override
				public void on_success(Map<Object, Object> result) {
					dataItem = (DataItem) map.get("dataItem");
					dataList = (DataList) map.get("dataList");
					dataItems = (List<DataItem>) map.get("dataItems");
					recordItems = dataItems ;
					
					set_progressBar(dataItem.getNext_commits_count());
					if(dataItem.isConflict()){
						accept_button.setClickable(false);
						accept_button.setBackgroundColor(getResources().getColor(R.color.antiquewhite));
					}
					updateOrInsert_dataItem();
					load_list_view();
				}
			}.execute();
		}else{
			BaseUtils.toast("无法连接网络");
		}
	}
	private List<DataItem> updateOrInsert_dataItem(){
		String strTitle = "修改条目";
		//创建
		if(dataItem.getOperation().equals(RequestCode.CREATE)){
			strTitle = "增加条目";
			dataItems.add(dataItem);	
		//移动
		}else if(dataItem.getOperation().equals(RequestCode.ORDER)){
			strTitle = "移动条目";
			dataItems = get_Move_DataItem(dataItems,dataItem);
		}else if(dataItem.getOperation().equals(RequestCode.UPDATE)){
			strTitle = "修改条目";
			dataItems = get_Update_DataItem(dataItems,dataItem);
		}else if(dataItem.getOperation().equals(RequestCode.REMOVE)){
			strTitle = "删除条目";
			dataItems = get_REMOVE_DataItem(dataItems,dataItem);
		}
		data_item_termwise_title_tv.setText(strTitle);
		return dataItems;
	}
	
	private void system(String[] names){
		for(String item : names){
			System.out.print(item + " : ");
		}
		System.out.println("order");
	}
	private List<DataItem> updateOperation(List<DataItem> new_dataItems ,DataItem data_Item){
		List<DataItem> new_item = new ArrayList<DataItem>();
		for(DataItem item : new_dataItems){
			if(item.seed.equals(data_Item.seed)){
				item.setPosition(data_Item.position);
			}
			new_item.add(item);
		}
		
		return new_item;
	}
	private List<DataItem> get_Update_DataItem(List<DataItem> new_dataItems ,DataItem data_Item){
		List<DataItem> new_items = new ArrayList<DataItem>();
		for(int i = 0 ; i < dataItems.size() ; i++){
			if(data_Item.seed.equals(dataItems.get(i).seed)){
				System.out.println(" update  " + data_Item.toString());
				if(data_Item.getOperation().equals(RequestCode.UPDATE)){
					new_items.add(data_Item);
				}	
			}else{
				new_items.add(dataItems.get(i));
			}
		}
		return new_items;
	}
	private List<DataItem> get_REMOVE_DataItem(List<DataItem> new_dataItems ,DataItem data_Item){
		List<DataItem> new_items = new ArrayList<DataItem>();
		for(int i = 0 ; i < dataItems.size() ; i++){
			if(data_Item.seed.equals(dataItems.get(i).seed)){
				if(data_Item.getOperation().equals(RequestCode.REMOVE)){
					DataItem dataItem2 = dataItems.get(i);
					dataItem2.setOperation(RequestCode.REMOVE);
					new_items.add(dataItem2);
				}	
			}else{
				new_items.add(dataItems.get(i));
			}
		}
		return new_items;
	}	
	private List<DataItem> get_Move_DataItem(List<DataItem> new_dataItems ,DataItem data_Item){
		
		List<DataItem> new_item = new ArrayList<DataItem>();
		boolean position_equal = false;
		boolean equal_fully = false;
		
		System.out.println("data_Item " + data_Item.toString());
		List<DataItem> order_dataItems =  new ArrayList<DataItem>();
		order_dataItems=new_dataItems;
		for(int i = 0 ; i< order_dataItems.size() ;i ++){
			System.out.println(i + "item : " + order_dataItems.get(i).toString());
			if(order_dataItems.get(i).position.equals(data_Item.position)){
				position_equal = true;
				if(order_dataItems.get(i).seed.equals(data_Item.seed)){
					new_item.add(data_Item);
					equal_fully = true;
				}else{
					new_item.add(order_dataItems.get(i));
					new_item.add(data_Item);
				}
			}else{
				new_item.add(order_dataItems.get(i));
				if(order_dataItems.get(i).seed.equals(data_Item.seed)){
					System.out.println("befour dataItems.remove(index)" + dataItems.size() + " : " + i);
					dataItems.remove(i);
					recordItems = updateOperation(recordItems,data_Item);
					System.out.println("after dataItems.remove(index)" + dataItems.size() + " : " + i);
				}
			}
		}
		if(equal_fully){
			accept_button.setClickable(false);
			accept_button.setBackgroundColor(getResources().getColor(R.color.antiquewhite));	
		}
		
		if(position_equal == false){
			List<DataItem> new_item_order = new ArrayList<DataItem>();
			dataItems.add(data_Item);
			String[] names = new String[dataItems.size()];
			for(int i = 0 ;i<dataItems.size();i++){
				names[i] = dataItems.get(i).position;
			}
			system(names);
			//排序(实现了中英文混排)
//			Arrays.sort(names, new PinyinComparator());
			for(int i=names.length-1; i>=0; i--) {
				for(int j=0; j<i; j++) {
				    if(names[j].compareTo(names[j+1]) > 0) {
				    	String temp = names[j];
				    	names[j] = names[j+1];
				    	names[j+1] = temp;
				    }
				}
			}
			system(names);
			for(int j = 0;j<names.length ; j ++){
				for(int i = 0 ; i<dataItems.size() ; i++){
					if(dataItems.get(i).position.equals(names[j])){
						boolean has = false;
						for(DataItem item : new_item_order){
							if(item.toString().equals(dataItems.get(i).toString())){
								has = true;
							}
						}
						if(!has){
							new_item_order.add(dataItems.get(i));
						}	
					}
				}
			}
			new_item = new_item_order;
			System.out.println(new_item.size());
		}	
		return new_item;
	}
	
	private void load_list_view() {	
		try {
			if((dataItem.getOperation().equals(RequestCode.UPDATE)
					|| dataItem.getOperation().equals(RequestCode.ORDER)) 
					&& dataItem.isConflict()!=true){
				AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 1.1f);
		    	alphaAnimation.setDuration(2000);
		    	alphaAnimation.setFillEnabled(true);
//		    	alphaAnimation.setRepeatCount(3000);
//		    	alphaAnimation.setFillBefore(true);
		    	list_view.startAnimation(alphaAnimation);
		    	alphaAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {	
						adapter = new DataItemTermWiseUpdateAdapter(DataItemTermWiseUpdateActivity.this);
						adapter.add_items(recordItems);
						list_view.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						adapter = new DataItemTermWiseUpdateAdapter(DataItemTermWiseUpdateActivity.this);
						adapter.add_items(dataItems);
						list_view.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
				});	
			}else{
				adapter = new DataItemTermWiseUpdateAdapter(DataItemTermWiseUpdateActivity.this);
				adapter.add_items(dataItems);
				list_view.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
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
		progressBar.setProgress(count_forked-next_commits_count);
		if(next_commits_count==0){
//			this.finish();
//			Intent intent = new Intent(DataItemTermWiseUpdateActivity.this,DataItemPullListActivity.class);
//			intent.putExtra("data_list_id", dataList_origin.id);
//			intent.putExtra("data_list_public", "fork");
//			startActivity(intent);
			api_load_list();
		}
	}
	public void click_accept_button(View view){
		api_load_list(RequestCode.ACCEPT);
	}
	public void click_refuse_button(View view){
		api_load_list(RequestCode.REJUST);
	}
	
	public void click_accept_all_button(View view){
		showDialog(true);
	}
	public void click_refuse_all_button(View view){
		showDialog(false);
	}
	private void showDialog(final boolean accept_or_refuse){
		String msg;
		if(accept_or_refuse){
			msg="此操作会完全接受其余修改动作";
		}else{
			msg="此操作会完全忽略其余修改动作";
		}
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("注意：");
		builder.setMessage(msg);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				api_accept_or_refuse(accept_or_refuse);
			}
		});
		builder.show();
	}
	private void api_accept_or_refuse(final boolean do_accept){
		if(BaseUtils.is_wifi_active(this)){
			new TeamknAsyncTask<Void, Void, DataList>(DataItemTermWiseUpdateActivity.this,"正在处理中") {
				@Override
				public DataList do_in_background(Void... params)
						throws Exception {
					DataList api_dataList = null;
					if(do_accept){
						api_dataList=HttpApi.WatchList.accept_rest_commits(dataList.server_data_list_id, committer.user_id);	
					}else{
						api_dataList=HttpApi.WatchList.reject_rest_commits(dataList.server_data_list_id, committer.user_id);
					}
					return api_dataList;
				}
				@Override
				public void on_success(DataList result) {	
					if(result != null){
						api_load_list();
					}else{
						
					}
				}
			}.execute();
		}else{
			BaseUtils.toast("无法连接网络");
		}
	}
	private void api_load_list(){
		new TeamknAsyncTask<Void, Void, List<User>>(DataItemTermWiseUpdateActivity.this,"正在跳转页面") {
			@Override
			public List<User> do_in_background(Void... params) throws Exception {
				return HttpApi.WatchList.commit_meta_list(dataList);
			}
			@Override
			public void on_success(List<User> result) {
				if(result!=null && result.size()>0){
					Intent intent = new Intent(DataItemTermWiseUpdateActivity.this,DataItemPullListActivity.class);
					intent.putExtra("data_list", dataList);
					intent.putExtra("data_list_public", "fork");
					startActivity(intent);
				}else{
					Intent intent = new Intent(DataItemTermWiseUpdateActivity.this,DataItemPullListActivity.class);
					intent.putExtra("data_list", dataList);
					intent.putExtra("data_list_public", "fork");
					startActivity(intent);
				}
			}
		}.execute();
	}
}
