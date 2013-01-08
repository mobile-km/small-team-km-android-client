package com.teamkn.activity.dataitem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.qrcode.QRCodeCameraActivity;
import com.teamkn.activity.qrcode_result.QRCodeResultActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.QRCodeResult;

public class CreateDataItemActivity extends TeamknBaseActivity{
	TextView show_page_title;
	TextView data_list_title_tv;
	ImageView click_data_item_save_iv;
	EditText create_data_item_title_et,create_data_item_content_et;

	String data_list_public;
	DataList dataList;
	DataItem dataItem;
	
	TextView create_data_list_msg_tv;
	QRCodeResult qrcode_result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_create);
		Intent intent = getIntent();
		dataList = (DataList) intent.getSerializableExtra("data_list");
		dataItem = (DataItem) intent.getSerializableExtra("data_item");
		data_list_public = intent.getStringExtra("data_list_public");
		qrcode_result = (QRCodeResult)intent.getExtras().getSerializable("code_result");
		load_UI();
	}
	private void load_UI() {
		show_page_title = (TextView)findViewById(R.id.show_page_title);
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		create_data_item_title_et = (EditText)findViewById(R.id.create_data_item_title_et);
		create_data_item_content_et = (EditText)findViewById(R.id.create_data_item_content_et);
		
		if(dataItem==null){
			show_page_title.setText("创建条目");	
		}else{ 
			show_page_title.setText("编辑条目");
			create_data_item_title_et.setText(dataItem.title);
			create_data_item_content_et.setText(dataItem.content);
		}
		data_list_title_tv.setText(dataList.title);
		
		create_data_list_msg_tv = (TextView)findViewById(R.id.create_data_list_msg_tv);
		create_data_list_msg_tv.setText("");
		
	}
	
	
	public void click_data_item_save_iv(View view){
		final String title_str = create_data_item_title_et.getText().toString();
		final String content_str = create_data_item_content_et.getText().toString();
		
		if(BaseUtils.is_str_blank(title_str) || BaseUtils.is_str_blank(content_str)){
			 BaseUtils.toast("标题和内容不可以为空");
			 return;
		}
     	if(!BaseUtils.is_wifi_active(this)){
     		BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
     		return;
     	}

		new TeamknAsyncTask<Void, Void, String>(CreateDataItemActivity.this,"正在处理") {

			@Override
			public String do_in_background(Void... params)
					throws Exception {
				String back =null ;
				if(dataItem != null && qrcode_result==null ){
					dataItem.setTitle(title_str);
					dataItem.setContent(content_str);	
					back = HttpApi.DataItem.update(dataItem); 
				}else{
					DataItem dataitem =new DataItem(-1, title_str, content_str, null, DataItem.Kind.TEXT, dataList.server_data_list_id, null, -1,null);
					back = HttpApi.DataItem.create(dataitem);  
				}	
				return back;
			}
			@Override
			public void on_success(String result) {
				if(BaseUtils.is_str_blank(result)){
		         	finish();
		         	BaseUtils.toast(getResources().getString(R.string.save_succeed_show));
				}else{
					BaseUtils.toast(result);
				}
				
			}
		}.execute();
	}
	public void click_qrcode_btn(View view){
		String title_str = create_data_item_title_et.getText().toString();
		
		if(BaseUtils.is_str_blank(title_str)){
			BaseUtils.toast("标题不可以为空");
			return;
		}
		if(dataItem == null ){
			dataItem = new DataItem();
		}
		dataItem.setTitle(title_str);
		Intent intent = new Intent(CreateDataItemActivity.this,QRCodeCameraActivity.class);
		QRCodeResult qrcode_result  =  new QRCodeResult(CreateDataItemActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("qrcode_result", qrcode_result);
		bundle.putSerializable("data_item", dataItem);
		bundle.putSerializable("data_list", dataList);
		bundle.putString("data_list_public", data_list_public);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
