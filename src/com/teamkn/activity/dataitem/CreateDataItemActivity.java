package com.teamkn.activity.dataitem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataItemDBHelper;
import com.teamkn.model.database.DataListDBHelper;

public class CreateDataItemActivity extends TeamknBaseActivity{
	TextView show_page_title;
	TextView data_list_title_tv;
	ImageView click_data_item_save_iv;
	EditText create_data_item_title_et,create_data_item_content_et;
	
	Integer data_list_id;
	Integer data_item_id;
	DataList dataList;
	DataItem dataItem;
	boolean is_update = false;
	
	TextView create_data_list_msg_tv;
	String msg = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_create);
		Intent intent = getIntent();
		data_list_id = intent.getIntExtra("data_list_id", -1);
		data_item_id = intent.getIntExtra("data_item_id", -1);

		load_UI();
	}
	private void load_UI() {
		show_page_title = (TextView)findViewById(R.id.show_page_title);
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		create_data_item_title_et = (EditText)findViewById(R.id.create_data_item_title_et);
		create_data_item_content_et = (EditText)findViewById(R.id.create_data_item_content_et);
		
		if( data_list_id !=-1){
			dataList = DataListDBHelper.find(data_list_id);
			is_update = false;
			show_page_title.setText("创建条目");
			data_list_title_tv.setText(dataList.title);
		}else{ 
			dataItem = DataItemDBHelper.find(data_item_id);
			dataList = DataListDBHelper.find(dataItem.data_list_id);
			is_update = true;
			show_page_title.setText("编辑条目");
			data_list_title_tv.setText(dataList.title);
			create_data_item_title_et.setText(dataItem.title);
			create_data_item_content_et.setText(dataItem.content);
		}
		
		create_data_list_msg_tv = (TextView)findViewById(R.id.create_data_list_msg_tv);
		create_data_list_msg_tv.setText("");
	}
	public void click_data_item_save_iv(View view){
		final String title_str = create_data_item_title_et.getText().toString();
		final String content_str = create_data_item_content_et.getText().toString();
		
		if(juast(title_str,content_str)){
         	if(BaseUtils.is_wifi_active(CreateDataItemActivity.this)){
         			String show_str;
         			if(is_update){
         				show_str = "修改中...";
         			}else{
         				show_str = "创建中...";
         			}
					new TeamknAsyncTask<Void, Void, String>(CreateDataItemActivity.this,show_str) {

						@Override
						public String do_in_background(Void... params)
								throws Exception {
							
							try {
								String back = "" ;
								if(is_update){
									dataItem.setTitle(title_str);
									dataItem.setContent(content_str);	
									if(DataItemDBHelper.find(title_str , dataItem.data_list_id).id>=0 && DataItemDBHelper.find(title_str , dataItem.data_list_id).id != dataItem.id){
										msg = "题目不可重复";
									}else{
										DataItemDBHelper.update(dataItem);
										back = HttpApi.DataItem.update(dataItem); 
									}
								}else{
									if(DataItemDBHelper.find(title_str , data_list_id).id>=0){
										msg = "创建的题目不可重复";
									}else{
										DataItem dataitem = 
												new DataItem(-1, title_str, content_str, null, DataItemDBHelper.Kind.TEXT, data_list_id, 0, -1);
										DataItemDBHelper.update(dataitem);
										back = HttpApi.DataItem.create(DataItemDBHelper.all(data_list_id).get(0));  
										msg = back;
									}
								}
								
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							return msg;
						}

						@Override
						public void on_success(String result) {
							System.out.println(is_update + " : " + msg + " : " + result);
							if(BaseUtils.is_str_blank(msg)){
								Intent intent = new Intent(CreateDataItemActivity.this,DataItemListActivity.class);
					    		intent.putExtra("data_list_id", dataList.id);
					    		if(is_update){
					    			intent.putExtra("create_data_item", false);
					    		}else{
					    			intent.putExtra("create_data_item", true);
					    		}
					    		startActivity(intent);
					         	finish();
							}else{
								BaseUtils.toast(msg);
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
}
