package com.teamkn.activity.dataitem;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.chat.ChatActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataItem;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.widget.adapter.DataItemListAdapter;

public class DataItemListActivity extends TeamknBaseActivity {
	class RequestCode{
		public final static int CREATE_DATA_ITEM =  0 ;
	}
	ListViewInterceptor tlv;
	DataItemListAdapter dataItemListAdapter;
	List<DataItem> dataItems ;
	
	Integer data_list_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_item_list);
        load_list();
       //注册上下文菜单
	    registerForContextMenu(tlv);
    }
    private void load_list() {
    	tlv = (ListViewInterceptor) findViewById(R.id.list);
    	dataItems  = new ArrayList<DataItem>();
    	new TeamknAsyncTask<Void, Void, List<DataItem>>() {
			@Override
			public List<DataItem> do_in_background(Void... params) throws Exception {
					if(BaseUtils.is_wifi_active(DataItemListActivity.this)){
						Intent intent = getIntent();
						data_list_id = intent.getIntExtra("data_list_id", -1);
						HttpApi.DataItem.pull(data_list_id);
						dataItems = DataItemDBHelper.all(data_list_id);
					}
					return dataItems;
			}
			@Override
			public void on_success(List<DataItem> dataItems) {
				dataItemListAdapter = new DataItemListAdapter(DataItemListActivity.this,R.layout.list_data_item_list_item,dataItems);
				tlv.setAdapter(dataItemListAdapter);
				tlv.setDropListener(onDrop);
				tlv.getAdapter();	
				dataItemListAdapter.notifyDataSetChanged();
		    }
		}.execute(); 	
	}
    private ListViewInterceptor.DropListener onDrop = new ListViewInterceptor.DropListener() {
		@Override
		public void drop(int from, int to) {
			
			DataItem from_item = dataItems.get(from);
			DataItem to_item = dataItems.get(to);
			dataItemListAdapter.remove(from_item);
			dataItemListAdapter.insert(from_item, to);
			insert_into(from_item.server_data_item_id,to_item.server_data_item_id);
		}
	};
	protected void onResume() {
		// 设置用户头像和名字
		AccountUser user = current_user();
		byte[] avatar = user.avatar;
		String name = current_user().name;
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.main_user_avatar);
		if(avatar != null){
			Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(avatar));
			Drawable drawable = new BitmapDrawable(bitmap);
			rl.setBackgroundDrawable(drawable);
		}else{
		    rl.setBackgroundResource(R.drawable.user_default_avatar_normal);
		}
	    TextView user_name_tv = (TextView)findViewById(R.id.main_user_name);
	    user_name_tv.setText(name);
		super.onResume();
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
		DataItem from_itemn = dataItems.get((int) adapterContextMenuInfo.id);
		BaseUtils.toast(adapterContextMenuInfo.id  +  "  : " + dataItems.size()+ " : "+from_itemn.title );
		switch (item.getItemId()) {
		case 1://向上移动一行
			if((int) adapterContextMenuInfo.id>0){
				DataItem from_item = dataItems.get((int) adapterContextMenuInfo.id);
				DataItem to_item = dataItems.get((int) adapterContextMenuInfo.id-1);
				dataItemListAdapter.remove(from_item);
				dataItemListAdapter.insert(from_item, (int) adapterContextMenuInfo.id-1);
				insert_into(from_item.server_data_item_id,to_item.server_data_item_id);
			}
			break;
		case 2://向下移动一行
			if((int) adapterContextMenuInfo.id<dataItems.size()-1){
				DataItem from_item1 = dataItems.get((int) adapterContextMenuInfo.id);
				DataItem to_item1 = dataItems.get((int) adapterContextMenuInfo.id+1);
				dataItemListAdapter.remove(from_item1);
				dataItemListAdapter.insert(from_item1, (int) adapterContextMenuInfo.id+1);
				insert_into(from_item1.server_data_item_id,to_item1.server_data_item_id);
			}
			break;
		case 3://编辑当前子项
			final DataItem dataItem2 = dataItems.get((int) adapterContextMenuInfo.id);
			Toast.makeText(DataItemListActivity.this, dataItem2.title, Toast.LENGTH_SHORT).show();			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(DataItemListActivity.this);
			
			builder.setTitle("请修改");
			builder.setIcon(android.R.drawable.ic_dialog_info);
			
			final EditText view = new EditText(DataItemListActivity.this);
			view.setText(dataItem2.title);
			builder.setView(view);
			builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final String add_data_list_et_str = view.getText().toString();
                    if(add_data_list_et_str!=null 
                    		&& !add_data_list_et_str.equals(null)
                    		&& !BaseUtils.is_str_blank(add_data_list_et_str)
                    		&& !add_data_list_et_str.equals(dataItem2.title)){
                    	if(BaseUtils.is_wifi_active(DataItemListActivity.this)){	
                    		dataItem2.setTitle(add_data_list_et_str);
                    		DataItemDBHelper.update(dataItem2);
                    		try {
								HttpApi.DataItem.update(DataItemDBHelper.find(dataItem2.id));
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
		case 4://删除当前子项
			AlertDialog.Builder builder1 = new AlertDialog.Builder(DataItemListActivity.this);
			
			builder1.setTitle("确定要删除吗");
			builder1.setPositiveButton("确定", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DataItem dataItem3 = dataItems.get((int) adapterContextMenuInfo.id);
					if(BaseUtils.is_wifi_active(DataItemListActivity.this)){	
		        		DataItemDBHelper.delete_by_id(dataItem3.id);
		        		try {
		        			if(dataItem3.server_data_item_id>=0){
		        				HttpApi.DataItem.remove_contact(dataItem3.server_data_item_id);
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
		AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo)menuInfo;
		if (adapterContextMenuInfo.id!=0&&adapterContextMenuInfo.id!=-1) {
			menu.setHeaderTitle("弹出菜单");
			menu.add(1, 1, 1,  "向上移动一行");
			menu.add(1, 2, 2,  "向下移动一行");
			menu.add(1, 3, 3,  "编辑当前子项");
			menu.add(1,4,4,"删除当前子项");
		}
	}
	public void on_create_data_item_click(View view){
		Intent intent = new Intent(DataItemListActivity.this,CreateDataItemActivity.class);
		intent.putExtra("data_list_id", data_list_id);
//		this.startActivity(intent);
		this.startActivityForResult(intent, RequestCode.CREATE_DATA_ITEM);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		switch(requestCode){
		  case RequestCode.CREATE_DATA_ITEM:
			  load_list();
			  BaseUtils.toast("RequestCode.CREATE_DATA_ITEM    " + RequestCode.CREATE_DATA_ITEM);
		    break;
		}  
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private void insert_into(final int from_server,final int to_server){
		new TeamknAsyncTask<Void, Void, Void>() {
			@Override
			public Void do_in_background(Void... params) throws Exception {
				try {
					if(BaseUtils.is_wifi_active(DataItemListActivity.this)) {	
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
	
	
}
