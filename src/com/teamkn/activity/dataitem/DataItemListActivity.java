package com.teamkn.activity.dataitem;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.widget.adapter.DataItemListAdapter;

public class DataItemListActivity extends TeamknBaseActivity {
	class RequestCode {
		public final static int CREATE_DATA_ITEM = 0;
	}
	/*
	 * data_list title edit
	 * */
	RelativeLayout data_list_title_rl;
	TextView data_list_title_tv;
	EditText data_list_title_et;
	ImageView data_list_image_iv_edit;
	
    /*
     * data_item  list 列表 
     * */
	ListViewInterceptor tlv;
	DataItemListAdapter dataItemListAdapter;
	List<DataItem> dataItems;

	LinearLayout list_no_data_show ;
	
	DataList dataList ;
	boolean create_data_item;
    boolean update_title_item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_list);
		Intent intent = getIntent();
		create_data_item = intent.getBooleanExtra("create_data_item", false);
		Integer data_list_id = intent.getIntExtra("data_list_id", -1);
		dataList = DataListDBHelper.find(data_list_id);

		data_list_title_rl = (RelativeLayout)findViewById(R.id.data_list_title_rl);
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		data_list_title_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
		data_list_title_tv.getPaint().setFakeBoldText(true);//加粗
		data_list_title_et = (EditText)findViewById(R.id.data_list_title_et);
		data_list_image_iv_edit = (ImageView) findViewById(R.id.data_list_image_iv_edit);
		data_list_image_iv_edit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
			    switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					data_list_title_rl.setBackgroundColor(getResources().getColor(R.color.lightgray));
					break;
				}
				return false;
			}
		});
		data_list_image_iv_edit.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				data_list_title_edit();
//				data_list_title_edit_text();
				data_list_title_rl.setBackgroundColor(getResources().getColor(R.color.darkgrey));
			}
		});
		data_list_title_et.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				System.out.println("aaaaaa : e " + data_list_title_et.hasFocus());
				if(data_list_title_et.hasFocus()==false){
					System.out.println("  data_list_title_et  no has focus" );
				}else{
					System.out.println("  data_list_title_et   has focus" );
					update_title_item = true;
				}
			}
		});
		
		list_no_data_show = (LinearLayout)findViewById(R.id.list_no_data_show);
		
		load_list();
		//test_load_list();
		// 注册上下文菜单
		// registerForContextMenu(tlv);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(update_title_item && MotionEvent.ACTION_UP == event.getAction()){
			System.out.println("   ------------------------------   ");
			update_title_item = false;
			data_list_title_update_text();
		}
		return super.onTouchEvent(event);
	}
	private void data_list_title_update_text(){
		String update_str = data_list_title_et.getText().toString();
		data_list_title_tv.setVisibility(View.VISIBLE);
		data_list_title_et.setVisibility(View.GONE);
		data_list_title_et.setText("");
		data_list_title_tv.setText(dataList.title);
	}
	private void data_list_title_update_api(String update_str){
		 
	}
	private void data_list_title_edit_text(){
		data_list_title_tv.setVisibility(View.GONE);
		data_list_title_et.setVisibility(View.VISIBLE);
		data_list_title_et.setText(dataList.title);
	}
	private void data_list_title_edit(){
		AlertDialog.Builder builder = new AlertDialog.Builder(DataItemListActivity.this);

		builder.setTitle("请修改");
		builder.setIcon(android.R.drawable.ic_dialog_info);

		final EditText view = new EditText(DataItemListActivity.this);
		view.setText(dataList.title);
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
										.equals(dataList.title)) {
							if (BaseUtils
									.is_wifi_active(DataItemListActivity.this)) {
								dataList.setTitle(add_data_list_et_str);
								DataListDBHelper.update(dataList);
								try {
									HttpApi.DataList
											.update(DataListDBHelper
													.find(dataList.id));
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
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	
	private void test_load_list(){
		
		dataItems = new ArrayList<DataItem>();
		try {
			HttpApi.DataItem.pull(dataList.server_data_list_id);
			dataItems = DataItemDBHelper.all(dataList.id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataItemListAdapter = new DataItemListAdapter(
				DataItemListActivity.this,
				R.layout.list_data_item_list_item, dataItems);
		tlv = (ListViewInterceptor) findViewById(R.id.list);
		tlv.setAdapter(dataItemListAdapter);
		tlv.setDropListener(onDrop);
		tlv.getAdapter();
	}
	private void load_list() {
		tlv = (ListViewInterceptor) findViewById(R.id.list);
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
				
				dataItemListAdapter = new DataItemListAdapter(
						DataItemListActivity.this,
						R.layout.list_data_item_list_item, dataItems);
				
				tlv.setAdapter(dataItemListAdapter);
				tlv.setDropListener(onDrop);
				tlv.getAdapter();
				dataItemListAdapter.notifyDataSetChanged();
				
				if(dataItems.size()==0){
					list_no_data_show.setVisibility(View.VISIBLE);
				}else{
					list_no_data_show.setVisibility(View.GONE);
				}
				
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
			}
		}.execute();

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

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		DataItem from_itemn = dataItems.get((int) adapterContextMenuInfo.id);
		BaseUtils.toast(adapterContextMenuInfo.id + "  : " + dataItems.size()
				+ " : " + from_itemn.title);
		switch (item.getItemId()) {
		case 0:// 向上移动一行
			if ((int) adapterContextMenuInfo.id > 0) {
				DataItem from_item = dataItems
						.get((int) adapterContextMenuInfo.id);
				DataItem to_item = dataItems
						.get((int) adapterContextMenuInfo.id - 1);
				dataItemListAdapter.remove(from_item);
				dataItemListAdapter.insert(from_item,
						(int) adapterContextMenuInfo.id - 1);
				insert_into(from_item.server_data_item_id,
						to_item.server_data_item_id);
			}
			break;
		case 1:// 向下移动一行
			if ((int) adapterContextMenuInfo.id < dataItems.size() - 1) {
				DataItem from_item1 = dataItems
						.get((int) adapterContextMenuInfo.id);
				DataItem to_item1 = dataItems
						.get((int) adapterContextMenuInfo.id + 1);
				dataItemListAdapter.remove(from_item1);
				dataItemListAdapter.insert(from_item1,
						(int) adapterContextMenuInfo.id + 1);
				insert_into(from_item1.server_data_item_id,
						to_item1.server_data_item_id);
			}
			break;
		case 2:// 编辑当前子项
			final DataItem dataItem2 = dataItems
					.get((int) adapterContextMenuInfo.id);
			Toast.makeText(DataItemListActivity.this, dataItem2.title,
					Toast.LENGTH_SHORT).show();

			AlertDialog.Builder builder = new AlertDialog.Builder(
					DataItemListActivity.this);

			builder.setTitle("请修改");
			builder.setIcon(android.R.drawable.ic_dialog_info);

			final EditText view = new EditText(DataItemListActivity.this);
			view.setText(dataItem2.title);
			builder.setView(view);
			builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final String add_data_list_et_str = view.getText()
							.toString();
					if (add_data_list_et_str != null
							&& !add_data_list_et_str.equals(null)
							&& !BaseUtils.is_str_blank(add_data_list_et_str)
							&& !add_data_list_et_str.equals(dataItem2.title)) {
						if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {
							dataItem2.setTitle(add_data_list_et_str);
							DataItemDBHelper.update(dataItem2);
							try {
								HttpApi.DataItem.update(DataItemDBHelper
										.find(dataItem2.id));
							} catch (Exception e) {
								e.printStackTrace();
							}
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
			builder1.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DataItem dataItem3 = dataItems
							.get((int) adapterContextMenuInfo.id);
					if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {
						DataItemDBHelper.delete_by_id(dataItem3.id);
						try {
							if (dataItem3.server_data_item_id >= 0) {
								HttpApi.DataItem
										.remove_contact(dataItem3.server_data_item_id);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			builder1.setNegativeButton("取消", null);
			builder1.show();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo) menuInfo;
		if (adapterContextMenuInfo.id != 0 && adapterContextMenuInfo.id != -1) {
			menu.setHeaderTitle("弹出菜单");
			menu.add(0, 0, 0, "向上移动一行");
			menu.add(0, 1, 1, "向下移动一行");
			menu.add(0, 2, 2, "编辑当前子项");
			menu.add(0, 3, 3, "删除当前子项");
		}
	}

	public void on_create_data_item_click(View view) {
		Intent intent = new Intent(DataItemListActivity.this,
				CreateDataItemActivity.class);
		intent.putExtra("data_list_id", dataList.id);
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
			load_list();
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
										DataItemDBHelper.update(dataItem2);
										try {
											HttpApi.DataItem.update(DataItemDBHelper
													.find(dataItem2.id));
										} catch (Exception e) {
											e.printStackTrace();
										}
										load_list();
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
