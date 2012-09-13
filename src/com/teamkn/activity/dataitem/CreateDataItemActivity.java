package com.teamkn.activity.dataitem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.dataitem.DataItemListActivity.RequestCode;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;

public class CreateDataItemActivity extends TeamknBaseActivity{
	TextView data_list_title_tv;
	ImageView click_data_item_save_iv;
	EditText create_data_item_title_et,create_data_item_content_et;
	Integer data_list_id;
	DataList dataList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_create);
		Intent intent = getIntent();
		data_list_id = intent.getIntExtra("data_list_id", -1);
		dataList = DataListDBHelper.find(data_list_id);
		load_UI();
	}
	private void load_UI() {
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		data_list_title_tv.setText(dataList.title);
		
		create_data_item_title_et = (EditText)findViewById(R.id.create_data_item_title_et);
		create_data_item_content_et = (EditText)findViewById(R.id.create_data_item_content_et);
	}
	public void click_data_item_save_iv(View view){
		String title_str = create_data_item_title_et.getText().toString();
		String content_str = create_data_item_content_et.getText().toString();

		if(juast(title_str,content_str)){
         	if(BaseUtils.is_wifi_active(CreateDataItemActivity.this)){
					try {
						DataItem dataitem = 
								new DataItem(-1, title_str, content_str, null, DataItemDBHelper.Kind.TEXT, data_list_id, 0, -1);
						DataItemDBHelper.update(dataitem);
						HttpApi.DataItem.create(DataItemDBHelper.all(data_list_id).get(0));
					    
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
         	
//         	create_data_item_title_et.setText("");
//         	create_data_item_content_et.setText("");
         	Intent intent = new Intent(CreateDataItemActivity.this,DataItemListActivity.class);
    		intent.putExtra("data_list_id", data_list_id);
    		intent.putExtra("create_data_item", true);
    		startActivity(intent);
         	this.finish();
         }	
	}
	public boolean juast(String title,String content){
		boolean title_bl = false;
		boolean content_bl = false;
		if(title!=null 
         		&& !title.equals(null)
         		&& !BaseUtils.is_str_blank(title)){
			title_bl = true;
		}
		if(content!=null 
         		&& !content.equals(null)
         		&& !BaseUtils.is_str_blank(content)){
			content_bl = true;
		}
		if(title_bl&&content_bl){
			return true;
		}
		return false;
	}
	
}
