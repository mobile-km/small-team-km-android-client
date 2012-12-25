package com.teamkn.activity.datalist;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.database.UserDBHelper;

public class CreateDataListActivity extends TeamknBaseActivity{
	public static class RequestCode {
        public final static String COLLECTION = "COLLECTION";
        public final static String STEP= "STEP";
        
        public static String data_list_type = COLLECTION;
        
        public static String data_list_public = "false";
    }
	EditText create_data_list_et;
	TextView create_data_list_msg_tv;
	CheckBox data_list_public_checkbox;
	RadioButton radiobutton_COLLECTION,radiobutton_STEP;
	
	DataList create_dataList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_list_create);
		radiobutton_COLLECTION = (RadioButton)findViewById(R.id.radiobutton_COLLECTION);
		radiobutton_STEP = (RadioButton)findViewById(R.id.radiobutton_STEP);
		radiobutton_COLLECTION.setSelected(true);
		radiobutton_COLLECTION.setOnClickListener(radioButtonListener);
		radiobutton_STEP.setOnClickListener(radioButtonListener);
		
		create_data_list_et = (EditText)findViewById(R.id.create_data_list_et);
		create_data_list_msg_tv = (TextView)findViewById(R.id.create_data_list_msg_tv);
		data_list_public_checkbox = (CheckBox)findViewById(R.id.data_list_public_checkbox);
		
		create_data_list_msg_tv.setText("");
	}
	
	RadioButton.OnClickListener radioButtonListener = new RadioButton.OnClickListener(){
		public void onClick(View v) {
		  RadioButton radio= (RadioButton)v;
		  switch (radio.getId()) {
			case R.id.radiobutton_COLLECTION:
				RequestCode.data_list_type = RequestCode.COLLECTION;
				break;
			case R.id.radiobutton_STEP:
				RequestCode.data_list_type = RequestCode.STEP;
				break;
			default:
				break;
		 }
		}
	}; 	
	public void data_list_public_checkbox(){
		if(data_list_public_checkbox.isChecked()){
			RequestCode.data_list_public = "true";
//			RequestCode.data_list_public = "false";
		}else{
			RequestCode.data_list_public = "false";
		}
	}
	public void click_data_list_save_iv(View view){
		 Button click_data_list_save_ib = (Button)findViewById(R.id.click_data_list_save_ib);
		 click_data_list_save_ib.setClickable(false);
		 final String add_data_list_et_str = create_data_list_et.getText().toString();
         if(add_data_list_et_str!=null 
         		&& !add_data_list_et_str.equals(null)
         		&& !BaseUtils.is_str_blank(add_data_list_et_str)){
         	if(BaseUtils.is_wifi_active(CreateDataListActivity.this)){
         		new TeamknAsyncTask<Void, Void, List<DataList>>(CreateDataListActivity.this,"正在创建") {
    				@Override
    				public List<DataList> do_in_background(Void... params)
    						throws Exception {
    						data_list_public_checkbox();
    						
							long current_seconds = System.currentTimeMillis();
							DataList dataList = new DataList(UserDBHelper.find_by_server_user_id(current_user().user_id).id ,
									add_data_list_et_str, RequestCode.data_list_type, RequestCode.data_list_public,"false",
									-1,current_seconds,current_seconds,-1,"false","false");
							create_dataList = HttpApi.DataList.create(dataList);
    					return null;
    				}
    				@Override
    				public void on_success(List<DataList> datalists) {
    					Intent intent = new Intent(CreateDataListActivity.this,DataItemListActivity.class);
			         	intent.putExtra("data_list_public", RequestCode.data_list_public);
			         	intent.putExtra("data_list_type", RequestCode.data_list_type);
			         	intent.putExtra("data_list", create_dataList);
			         	startActivity(intent);
			         	CreateDataListActivity.this.finish();
    				}
    			}.execute();	
			}else{
				BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
			}
         }else{
        	 BaseUtils.toast("列表名称不可以为空");
         }
	}
	// 钩子，自行重载
	public void on_go_back() {
		Intent intent = new Intent(CreateDataListActivity.this,DataItemListActivity.class);
//		User user = UserDBHelper.find_by_server_user_id(current_user().user_id);
     	intent.putExtra("data_list_public", RequestCode.data_list_public);
     	intent.putExtra("data_list_type", MainActivity.RequestCode.ALL);
     	MainActivity.RequestCode.SHOW_NEXT = MainActivity.RequestCode.SHOW_CREATE_NEXT_HELP_CASE;
		setResult(MainActivity.RequestCode.SHOW_BACK,intent);
	};
}
