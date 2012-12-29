package com.teamkn.activity.dataitem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.qrcode.CaptureActivity;
import com.teamkn.activity.qrcode_result.QRcodeResultActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;

public class CreateDataItemActivity extends TeamknBaseActivity{
	TextView show_page_title;
	TextView data_list_title_tv;
	ImageView click_data_item_save_iv;
	EditText create_data_item_title_et,create_data_item_content_et;

	String data_list_public;
	DataList dataList;
	DataItem dataItem;
	
	TextView create_data_list_msg_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_create);
		Intent intent = getIntent();
		dataList = (DataList) intent.getSerializableExtra("data_list");
		dataItem = (DataItem) intent.getSerializableExtra("data_item");
		data_list_public = intent.getStringExtra("data_list_public");

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
		
		if(juast(title_str,content_str)){
         	if(BaseUtils.is_wifi_active(CreateDataItemActivity.this)){
         			String show_str;
         			if(dataItem != null ){
         				show_str = "修改中...";
         			}else{
         				show_str = "创建中...";
         			}
					new TeamknAsyncTask<Void, Void, String>(CreateDataItemActivity.this,show_str) {

						@Override
						public String do_in_background(Void... params)
								throws Exception {
							String back =null ;
							try {
								if(dataItem != null ){
									dataItem.setTitle(title_str);
									dataItem.setContent(content_str);	
									back = HttpApi.DataItem.update(dataItem); 
								}else{
									DataItem dataitem =
											new DataItem(-1, title_str, content_str, 
													     null, DataItem.Kind.TEXT, 
													dataList.server_data_list_id, null, -1,null);
									back = HttpApi.DataItem.create(dataitem);  
								}	
							} catch (Exception e) {
								e.printStackTrace();
							}
							return back;
						}

						@Override
						public void on_success(String result) {
							
							if(BaseUtils.is_str_blank(result)){
//								Intent intent = new Intent(CreateDataItemActivity.this,DataItemListActivity.class);
//					    		intent.putExtra("data_list", dataList);
//					    		intent.putExtra("data_list_public", data_list_public);
//					    		startActivity(intent);
					         	finish();
					         	BaseUtils.toast(getResources().getString(R.string.save_succeed_show));
							}else{
								BaseUtils.toast(result);
							}
							
						}
					}.execute();
			}else{
				BaseUtils.toast("无法连接到网络，请检查网络配置");
			}
         }else{
        	 BaseUtils.toast("标题 和 内容 不可以为空");
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
	public void click_qrcode_btn(View view){
//		open_activity(CaptureActivity.class);
		Intent intent = new Intent(CreateDataItemActivity.this,CaptureActivity.class);
		CaptureActivity.result_activity = CaptureActivity.class;
		startActivity(intent);
	}
}
