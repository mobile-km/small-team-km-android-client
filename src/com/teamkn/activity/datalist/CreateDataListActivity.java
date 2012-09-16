package com.teamkn.activity.datalist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.MainActivity.RequestCode;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataListDBHelper;

public class CreateDataListActivity extends TeamknBaseActivity{
	public static class RequestCode {
        public final static String COLLECTION = "COLLECTION";
        public final static String STEP= "STEP";
        
        public static String data_list_type = COLLECTION;
        
        public static String data_list_public = "true";
    }
	EditText create_data_list_et;
	TextView create_data_list_msg_tv;
	ImageView data_list_public_iv;
	RadioButton radiobutton_COLLECTION,radiobutton_STEP;
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
		data_list_public_iv = (ImageView)findViewById(R.id.data_list_public_iv);
		
		create_data_list_msg_tv.setText("");
		data_list_public_iv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_lock_lock));
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
	public void click_data_list_public_iv(View view){
		if(RequestCode.data_list_public .equals("false")){
			RequestCode.data_list_public = "true";
			data_list_public_iv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_lock_lock));
		}else{
			RequestCode.data_list_public = "false";
			data_list_public_iv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_lock_idle_lock));
		}
	}
	public void click_data_list_save_iv(View view){
		 String add_data_list_et_str = create_data_list_et.getText().toString();
         if(add_data_list_et_str!=null 
         		&& !add_data_list_et_str.equals(null)
         		&& !BaseUtils.is_str_blank(add_data_list_et_str)){
         	if(BaseUtils.is_wifi_active(CreateDataListActivity.this)){
					try {
						
						DataList dataList = DataListDBHelper.find_by_title(add_data_list_et_str);
						if(dataList.id>0){
							create_data_list_msg_tv.setText("列表名称已存在");
							create_data_list_msg_tv.setSelected(true);
						}else{
							dataList= new DataList(current_user().user_id , add_data_list_et_str, RequestCode.data_list_type, RequestCode.data_list_public,-1);
							DataListDBHelper.update(dataList);
							HttpApi.DataList.create(DataListDBHelper.all(RequestCode.data_list_type,RequestCode.data_list_public).get(0));
							
							Intent intent = new Intent(CreateDataListActivity.this,MainActivity.class);
				         	
				         	intent.putExtra("data_list_public", RequestCode.data_list_public);
				         	intent.putExtra("data_list_type", RequestCode.data_list_type);
				         	startActivity(intent);
				         	this.finish();
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
         }
	}
}
