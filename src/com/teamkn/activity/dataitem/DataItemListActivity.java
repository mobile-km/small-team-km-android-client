package com.teamkn.activity.dataitem;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.DataListReading;
import com.teamkn.model.Watch;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.model.database.DataListReadingDBHelper;
import com.teamkn.model.database.WatchDBHelper;
import com.teamkn.widget.adapter.DataItemListAdapter;

public class DataItemListActivity extends TeamknBaseActivity {
	class RequestCode {
		public final static int CREATE_DATA_ITEM = 0;
	}
	Button go_back_button;
	/*
	 * data_list title edit
	 * */
	RelativeLayout data_list_title_rl;
	TextView data_list_title_tv;
	ImageView data_list_image_iv_edit;
	ImageView data_list_image_iv_watch;
    /*
     * data_item  list 列表 
     * */
	ListViewInterceptor tlv;
	DataItemListAdapter dataItemListAdapter;
	List<DataItem> dataItems;
    /*
     * data_item step 列表
     * */
	RelativeLayout data_item_step_rl;
	TextView data_item_step_tv;
	TextView data_item_step_text_tv;
	Button data_item_list_approach_button;
	Button data_item_next_button;
	Button data_item_back_button;
	
	LinearLayout list_no_data_show ;
	
	DataList dataList ;
	boolean create_data_item;
	String data_list_public;
	String data_list_type;
	boolean show_step;
    int step_new = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_list);
		Intent intent = getIntent();
		create_data_item = intent.getBooleanExtra("create_data_item", false);
		Integer data_list_id = intent.getIntExtra("data_list_id", -1);
		dataList = DataListDBHelper.find(data_list_id);
		data_list_public = intent.getStringExtra("data_list_public");

		load_UI();
//		load_list();
		if(data_list_public.equals("true") && dataList.kind.equals(MainActivity.RequestCode.STEP)){
			show_step = true;
		}else{
			show_step = false;
		}
		load_step_or_list(show_step);
		load_data_item_list(show_step);
	}
	private void load_UI(){
		go_back_button = (Button)findViewById(R.id.go_back_button);
		if(data_list_public.equals("true")){
			go_back_button.setText("公共列表");
		}else if(data_list_public.equals("false")){
			go_back_button.setText(current_user().name);
		}
		load_watch_UI();
		data_list_title_rl=(RelativeLayout)findViewById(R.id.data_list_title_rl);
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		data_list_title_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
		data_list_title_tv.getPaint().setFakeBoldText(true);//加粗
		data_list_image_iv_edit = (ImageView) findViewById(R.id.data_list_image_iv_edit);
		data_list_image_iv_edit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						data_list_title_rl.setBackgroundColor(getResources()
								.getColor(R.color.lightgray));
						break;
					}
				return false;
			}
		});
		data_list_image_iv_edit.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				data_list_title_edit();
				data_list_title_rl.setBackgroundColor(getResources().getColor(R.color.darkgrey));
			}
		});
		list_no_data_show = (LinearLayout)findViewById(R.id.list_no_data_show);
		
		// data_item list 列表
		tlv = (ListViewInterceptor) findViewById(R.id.list);
		// data_item  step 列表
		data_item_step_rl = (RelativeLayout)findViewById(R.id.data_item_step_rl);
		data_item_step_tv = (TextView)findViewById(R.id.data_item_step_tv);
		data_item_step_text_tv = (TextView)findViewById(R.id.data_item_step_text_tv);
		data_item_list_approach_button = (Button)findViewById(R.id.data_item_list_approach_button);
		data_item_next_button = (Button)findViewById(R.id.data_item_next_button); 
		data_item_back_button = (Button)findViewById(R.id.data_item_back_button);		
	}
	private void data_list_title_edit(){
		AlertDialog.Builder builder = new Builder(DataItemListActivity.this);
		builder.setTitle("请修改");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		final EditText view = new EditText(DataItemListActivity.this);
		view.setText(dataList.title);
		builder.setView(view);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String add_data_list_et_str = view.getText().toString();
				if (add_data_list_et_str != null&& !add_data_list_et_str.equals(null)
						&& !BaseUtils.is_str_blank(add_data_list_et_str)
						&& !add_data_list_et_str.equals(dataList.title)) {
						if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {
							dataList.setTitle(add_data_list_et_str);
							DataListDBHelper.update(dataList);
							try {
								HttpApi.DataList .update(DataListDBHelper.find(dataList.id));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						data_list_title_tv.post(new Runnable() {
							@Override
							public void run() {
							data_list_title_tv.setText(add_data_list_et_str);
							}
					    });
				}
			}
		});
		builder.show();
	}
	private void load_watch_UI(){
		data_list_image_iv_watch = (ImageView)findViewById(R.id.data_list_image_iv_watch);
		final Watch watch = WatchDBHelper.find(new Watch(-1,current_user().user_id , dataList.id));
		System.out.println("watch.id = " + watch.id);
		if(dataList.public_boolean.equals("true")){
			if(watch.id<=0){
				data_list_image_iv_watch.setAlpha(100);	
			}else{
				data_list_image_iv_watch.setAlpha(50);
			}
		}else{
			data_list_image_iv_watch.setVisibility(View.GONE);
		}
		
		data_list_image_iv_watch.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(watch.id<=0){
					watch_data_list(true);
					data_list_image_iv_watch.setAlpha(50);
				}else{
					watch_data_list(false);
					data_list_image_iv_watch.setAlpha(100);
				}
			}
		});
	}
	private void watch_data_list(final boolean watch_boolean){
		new TeamknAsyncTask<Void, Void, Void>() {
			@Override
			public Void do_in_background(Void... params) throws Exception {
				if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {
					Watch watch = new Watch(-1,current_user().user_id , dataList.id);
					if(watch_boolean){
						WatchDBHelper.createOrUpdate(watch);
					}else{
						WatchDBHelper.delete(watch);
					}
					HttpApi.WatchList.watch(dataList, watch_boolean);
				}else{
					BaseUtils.toast("无法连接到网络，请检查网络配置");
				}
				return null;
			}
			@Override
			public void on_success(Void result) {
				if(watch_boolean){
					BaseUtils.toast("成功加入书签 ^_^");
				}else{
					BaseUtils.toast("移除书签成功 ^_^");
				}
				
			}	
		}.execute();
	}
	private void load_step_or_list(boolean show_step){
		if(show_step){
			data_item_step_rl.setVisibility(View.VISIBLE);
			tlv.setVisibility(View.GONE);
		}else{
			data_item_step_rl.setVisibility(View.GONE);
			tlv.setVisibility(View.VISIBLE);
		}
	}
	private void load_data_item_list(final boolean show_step){
		dataItems = new ArrayList<DataItem>();
		new TeamknAsyncTask<Void, Void, List<DataItem>>(DataItemListActivity.this,"内容加载中") {
			@Override
			public List<DataItem> do_in_background(Void... params)
					throws Exception {
				if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {

					HttpApi.DataItem.pull(dataList.server_data_list_id);
					dataItems = DataItemDBHelper.all(dataList.id);
				}else{
					BaseUtils.toast("无法连接到网络，请检查网络配置");
				}
				return dataItems;
			}
			@Override
			public void on_success(final List<DataItem> dataItems) {	
				if(dataItems.size()==0){
					list_no_data_show.setVisibility(View.VISIBLE);
				}else{
					list_no_data_show.setVisibility(View.GONE);
					if(show_step){
						load_step();
					}else{
						load_list();
					}
				}
			}
		}.execute();
	}
	private void load_step(){
		DataListReading reading =DataListReadingDBHelper.find(new DataListReading(-1, dataList.id, dataList.user_id)); 
		if(reading.id>0 || dataList.user_id==current_user().user_id){
			data_item_list_approach_button.setVisibility(View.VISIBLE);
			data_item_list_approach_button.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					show_step = false;
					load_step_or_list(show_step);
					load_list();
				}
			});
		}else{
			data_item_list_approach_button.setVisibility(View.GONE);
		}
		set_step_ui();
		data_item_next_button.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				step_new++;
				set_step_ui();
			}
		});
		data_item_back_button.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				step_new--;
				set_step_ui();
			}
		});
	}
	private void set_step_ui(){
		if(step_new<=dataItems.size()-1){
			data_item_step_tv.setVisibility(View.VISIBLE);
			if(step_new<=1){
				data_item_back_button.setVisibility(View.GONE);
				data_item_list_approach_button.setVisibility(View.VISIBLE);
			}else{
				data_item_list_approach_button.setVisibility(View.GONE);
				data_item_back_button.setVisibility(View.VISIBLE);
			}
			if(step_new==dataItems.size()-1){
				data_item_next_button.setText("结束");
			}
			data_item_step_tv.setText((1+step_new)+"");
			data_item_step_text_tv.setText(dataItems.get(step_new).content);
		}else{
			show_step = false;
			load_step_or_list(show_step);
			load_list();
		}
	}
	private void load_list() {
		dataItemListAdapter = new DataItemListAdapter(
				DataItemListActivity.this,
				R.layout.list_data_item_list_item, dataItems);
		tlv.setAdapter(dataItemListAdapter);
		tlv.setDropListener(onDrop);
		tlv.getAdapter();
		dataItemListAdapter.notifyDataSetChanged();	
		set_list_listener();
	}
    private void set_list_listener(){
    	tlv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view,
					int scrollState) {
			}
			@Override
			public void onScroll(AbsListView view,
					int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
				if (create_data_item
						&& visibleItemCount <= dataItems.size()) {
					System.out.println(dataItems.size());
					System.out.println(firstVisibleItem + " : "
							+ visibleItemCount + " :　" + totalItemCount
							+ " : " + create_data_item);
					tlv.setSelection(dataItems.size()
							- visibleItemCount);
					create_data_item = false;
				}
			}
		});

		tlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				TextView info_tv = (TextView) list_item
						.findViewById(R.id.data_item_info_tv);
				final DataItem item = (DataItem) info_tv
						.getTag(R.id.tag_note_uuid);
				Intent intent = new Intent(DataItemListActivity.this,CreateDataItemActivity.class);
				intent.putExtra("data_item_id",item.id);
				intent.putExtra("data_list_public",data_list_public);
				startActivity(intent);
			}
		});
		tlv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// BaseUtils.toast(arg3 + " : "+ arg1.getId() ) ;
				DataItem from_item = dataItems.get((int) arg3);
				new AlertDialog.Builder(DataItemListActivity.this)
						.setTitle("当前选项：   " + from_item.title)
						.setItems(
								new String[] { 
										"向上移动一行", 
										"向下移动一行", 
										"编辑当前子项",
										"删除当前子项" },
								new CreateContextMenu((int) arg3))
						.setNegativeButton("取消", null).show();
				return false;
			}
		});
    }
	private ListViewInterceptor.DropListener onDrop = new ListViewInterceptor.DropListener() {
		@Override
		public void drop(int from, int to) {

			DataItem from_item = dataItems.get(from);
			DataItem to_item = dataItems.get(to);
			dataItemListAdapter.remove(from_item);
			dataItemListAdapter.insert(from_item, to);
			dataItemListAdapter.notifyDataSetChanged();
			insert_into(from_item.server_data_item_id,
					to_item.server_data_item_id);
		}
	};
	public void on_create_data_item_click(View view) {
		Intent intent = new Intent(DataItemListActivity.this,
				CreateDataItemActivity.class);
		intent.putExtra("data_list_id", dataList.id);
		intent.putExtra("data_list_public", data_list_public);
		// this.startActivity(intent);
		this.startActivityForResult(intent, RequestCode.CREATE_DATA_ITEM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case RequestCode.CREATE_DATA_ITEM:
			load_data_item_list(show_step);
			BaseUtils.toast("RequestCode.CREATE_DATA_ITEM    "
					+ RequestCode.CREATE_DATA_ITEM);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void insert_into(final int from_server, final int to_server) {
		new TeamknAsyncTask<Void, Void, Void>() {
			@Override
			public Void do_in_background(Void... params) throws Exception {
				try {
					if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {
						HttpApi.DataItem.order(from_server, to_server);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void on_success(Void result) {
			}
		}.execute();
	}

	class CreateContextMenu implements OnClickListener {
		int from_id;

		public CreateContextMenu(int from_id) {
			this.from_id = from_id;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:// 向上移动一行
				if (from_id > 0) {
					DataItem from_item = dataItems.get(from_id);
					DataItem to_item = dataItems.get(from_id - 1);
					dataItemListAdapter.remove(from_item);
					dataItemListAdapter.insert(from_item, from_id - 1);
					insert_into(from_item.server_data_item_id,
							to_item.server_data_item_id);
				}
				break;
			case 1:// 向下移动一行
				if (from_id < dataItems.size() - 1) {
					DataItem from_item1 = dataItems.get(from_id);
					DataItem to_item1 = dataItems.get(from_id + 1);
					dataItemListAdapter.remove(from_item1);
					dataItemListAdapter.insert(from_item1, from_id + 1);
					insert_into(from_item1.server_data_item_id,
							to_item1.server_data_item_id);
				}
				break;
			case 2:// 编辑当前子项
				final DataItem dataItem2 = dataItems.get(from_id);
				Toast.makeText(DataItemListActivity.this, dataItem2.title,
						Toast.LENGTH_SHORT).show();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						DataItemListActivity.this);

				builder.setTitle("请修改");
				builder.setIcon(android.R.drawable.ic_dialog_info);

				final EditText view = new EditText(DataItemListActivity.this);
				view.setText(dataItem2.title);
				builder.setView(view);
				builder.setPositiveButton("确定",
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								final String add_data_list_et_str = view
										.getText().toString();
								if (add_data_list_et_str != null
										&& !add_data_list_et_str.equals(null)
										&& !BaseUtils
												.is_str_blank(add_data_list_et_str)
										&& !add_data_list_et_str
												.equals(dataItem2.title)) {
									if (BaseUtils
											.is_wifi_active(DataItemListActivity.this)) {
										dataItem2
												.setTitle(add_data_list_et_str);
										DataItemDBHelper.update_by_id(dataItem2);
										try {
											HttpApi.DataItem.update(DataItemDBHelper
													.find(dataItem2.id));
										} catch (Exception e) {
											e.printStackTrace();
										}
										load_data_item_list(show_step);
									}
								}
							}
						});
				builder.setNegativeButton("取消", null);
				builder.show();
				break;
			case 3:// 删除当前子项
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						DataItemListActivity.this);

				builder1.setTitle("确定要删除吗");
				builder1.setPositiveButton("确定",
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								DataItem dataItem3 = dataItems.get(from_id);
								if (BaseUtils
										.is_wifi_active(DataItemListActivity.this)) {
									DataItemDBHelper.delete_by_id(dataItem3.id);
									try {
										if (dataItem3.server_data_item_id >= 0) {
											HttpApi.DataItem
													.remove_contact(dataItem3.server_data_item_id);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									dataItems.remove(from_id);
									load_list();
								}
							}
						});
				builder1.setNegativeButton("取消", null);
				builder1.show();
				break;
			default:
				break;
			}
		}
	}
	
}
