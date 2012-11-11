package com.teamkn.activity.dataitem.pull;

import java.util.ArrayList;
import java.util.List;

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
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.widget.adapter.DataItemTermWiseUpdateAdapter;

public class DataItemTermWiseUpdateActivity extends TeamknBaseActivity{
	public static class RequestCode {
		public final static String CREATE = "CREATE";
		public final static String UPDATE = "UPDATE";
		public final static String REMOVE = "REMOVE";
		public final static String ORDER = "ORDER";
		
		public final static String GET = "GET";
		public final static String ACCEPT = "ACCEPT";
		public final static String REJUST = "REJUST";
		
	}
	TextView data_item_termwise_title_tv;
	TextView data_list_title_tv;
	
	ListView list_view;
	Button accept_button;
	ProgressBar progressBar;
	
	String[] seeds;
	DataList dataList_forked;
	DataList dataList_origin;
	
	List<DataItem> dataItems;
	List<DataItem> recordItems = new ArrayList<DataItem>();
	
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
		is_first = true;
		load_UI();
	}
	private void load_UI() {
		data_item_termwise_title_tv = (TextView)findViewById(R.id.data_item_termwise_title_tv);
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList_origin.title);
		list_view = (ListView)findViewById(R.id.list_view);
		api_load_list(RequestCode.GET);
		accept_button = (Button)findViewById(R.id.accept_button);
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
					recordItems = DataItemDBHelper.all(dataList_origin.id);
					dataItems = DataItemDBHelper.all(dataList_origin.id);
//					dataItems.add(dataItem.getNext_commits_count(), dataItem);	
					return dataItem;
				}
				@Override
				public void on_success(DataItem result) {
					set_progressBar(result.getNext_commits_count());
					if(result.getTitle()!=null && result.getTitle()!="" && !result.isConflict()){
						accept_button.setClickable(true);
						updateOrInsert_dataItem(result);
						load_list_view();
					}else if( result.isConflict()){
						accept_button.setClickable(false);
						accept_button.setBackgroundColor(getResources().getColor(R.color.antiquewhite));
						load_list_view();
					}
				}
			}.execute();
		}else{
			BaseUtils.toast("无法连接网络");
		}
	}
	private List<DataItem> updateOrInsert_dataItem(DataItem data_Item){
//		recordItems = dataItems;
		String strTitle = "修改条目";
		//创建
		if(data_Item.getOperation().equals(RequestCode.CREATE)){
			strTitle = "增加条目";
			System.out.println(" create " + data_Item.toString());
			dataItems.add(data_Item);	
		//移动
		}else if(data_Item.getOperation().equals(RequestCode.ORDER)){
			strTitle = "移动条目";
			System.out.println(" order " + data_Item.toString());
			for (int i = 0; i < dataItems.size(); i++) {
				System.out.println("item : " + dataItems.get(i).toString());
			}
			dataItems = getDataItem(dataItems,data_Item);
		}else{
			List<DataItem> new_dataItems = new ArrayList<DataItem>();
			// 修改  删除
			for(int i = 0 ; i < dataItems.size() ; i++){
				if(data_Item.seed.equals(dataItems.get(i).seed)){
					System.out.println(" update or remove " + data_Item.toString());
					if(data_Item.getOperation().equals(RequestCode.REMOVE)){
						strTitle = "删除条目";
						DataItem dataItem2 = dataItems.get(i);
						dataItem2.setOperation(RequestCode.REMOVE);
						new_dataItems.add(dataItem2);
					}else if(data_Item.getOperation().equals(RequestCode.UPDATE)){
						strTitle = "修改条目";
						new_dataItems.add(data_Item);
					}	
				}else{
					new_dataItems.add(dataItems.get(i));
				}
			}
			dataItems = new_dataItems;
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
	private List<DataItem> getDataItem(List<DataItem> new_dataItems ,DataItem data_Item){
		
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
				if(order_dataItems.get(i).seed.equals(data_Item.seed) && !order_dataItems.get(i).position.equals(data_Item.position)){
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
//			recordItems;
			if((dataItem.getOperation().equals(RequestCode.UPDATE)) 
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
			}else if((dataItem.getOperation().equals(RequestCode.ORDER)) 
					&& dataItem.isConflict()!=true){
				
				AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 1.1f);
		    	alphaAnimation.setDuration(2000);
		    	alphaAnimation.setFillEnabled(true);
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
						api_dataList=HttpApi.WatchList.accept_rest_commits(dataList_origin.server_data_list_id, committer_id);	
						DataItemDBHelper.delete_by_data_list_id(dataList_origin.id);
					}else{
						api_dataList=HttpApi.WatchList.reject_rest_commits(dataList_origin.server_data_list_id, committer_id);
					}
					return api_dataList;
				}
				@Override
				public void on_success(DataList result) {	
					if(result != null){
//						BaseUtils.toast("操作成功");
//						Intent intent = new Intent(DataItemTermWiseUpdateActivity.this,DataItemPullListActivity.class);
//						intent.putExtra("data_list_id", dataList_origin.id);
//						intent.putExtra("data_list_public", "fork");
//						startActivity(intent);
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
//				userList = HttpApi.WatchList.commit_meta_list(dataList);
				return HttpApi.WatchList.commit_meta_list(dataList_origin);
			}
			@Override
			public void on_success(List<User> result) {
				if(result!=null && result.size()>0){
					Intent intent = new Intent(DataItemTermWiseUpdateActivity.this,DataItemPullListActivity.class);
					intent.putExtra("data_list_id", dataList_origin.id);
					intent.putExtra("data_list_public", "fork");
					startActivity(intent);
				}else{
//					Intent intent = getIntent();
//					create_data_item = intent.getBooleanExtra("create_data_item", false);//是否是创建dataItem返回
//					Integer data_list_id = intent.getIntExtra("data_list_id", -1);//返回dataList的本地id
//					data_list_public = intent.getStringExtra("data_list_public");//返回dataList的中公开，自己私有，协作列表中的一个
//					update_title = intent.getBooleanExtra("is_update", false);//返回dataList的的title是否修改
//					
					Intent intent = new Intent(DataItemTermWiseUpdateActivity.this,DataItemPullListActivity.class);
					intent.putExtra("data_list_id", dataList_origin.id);
					intent.putExtra("data_list_public", "fork");
					intent.putExtra("create_data_item", true);
					startActivity(intent);
				}
			}
		}.execute();
	}
}
