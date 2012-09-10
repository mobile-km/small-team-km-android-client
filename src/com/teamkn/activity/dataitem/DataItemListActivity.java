package com.teamkn.activity.dataitem;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.MainActivity.RequestCode;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.widget.adapter.DataItemListAdapter;
import com.teamkn.widget.adapter.DataListAdapter;

public class DataItemListActivity extends TeamknBaseActivity {
	ListViewInterceptor tlv;
	DataItemListAdapter dataItemListAdapter;
	List<DataItem> dataItems ;
	
	Integer data_list_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_item_list);
        load_list();
    }
    private void load_list() {
    	tlv = (ListViewInterceptor) findViewById(R.id.list);
    	dataItems  = new ArrayList<DataItem>();
    	dataItemListAdapter = new DataItemListAdapter(DataItemListActivity.this);
    	
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
				dataItemListAdapter.add_items(dataItems);
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
			
			System.out.println(from + " : " + to);
			
			DataItem item = dataItems.get(from);
			dataItemListAdapter.remove_item(item);
			dataItemListAdapter.insert_item(item, to);
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
	public void on_create_data_item_click(View view){
		Intent intent = new Intent(DataItemListActivity.this,CreateDataItemActivity.class);
		intent.putExtra("data_list_id", data_list_id);
		this.startActivity(intent);
	}
}
