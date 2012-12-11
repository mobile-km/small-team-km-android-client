package com.teamkn.activity.dataitem;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.DataList;

public class DataItemStepHelpActivity extends TeamknBaseActivity{
	RelativeLayout data_item_step_rl;
	Button go_back_button;
	TextView data_item_step_tv;
	TextView data_item_step_text_tv;
	Button data_item_list_approach_button;
	Button data_item_next_button;
	
	
	DataList dataList ;
	String data_list_public;
	boolean list_approach = false;
	String[] step= {"把配菜准备好，鸡蛋用碗打均","把米饭加到蛋汁里办均","油锅加热后"};
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.data_item_step_help);
//		Intent intent = getIntent();
//		Integer data_list_id = intent.getIntExtra("data_list_id", -1);
//		dataList = DataListDBHelper.find(data_list_id);
//		data_list_public = intent.getStringExtra("data_list_public");
//		load_UI();
//		load_UI_click();
//	}
//
//	private void load_UI_click() {
//		if(data_list_public.equals("true")){
//			go_back_button.setText("公共列表");
//		}else if(data_list_public.equals("false")){
//			go_back_button.setText(current_user().name);
//		}
//		data_item_step_tv.setText("1");
//		data_item_step_text_tv.setText("钓鱼岛是中国的固有领土");
//		data_item_list_approach_button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
//		data_item_next_button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
//	}
//	private void load_UI() {
//		data_item_step_rl = (RelativeLayout)findViewById(R.id.data_item_step_rl);
//		go_back_button = (Button)findViewById(R.id.go_back_button);
//		data_item_step_tv = (TextView)findViewById(R.id.data_item_step_tv);
//		data_item_step_text_tv = (TextView)findViewById(R.id.data_item_step_text_tv);
//		data_item_list_approach_button = (Button)findViewById(R.id.data_item_list_approach_button);
//		data_item_next_button = (Button)findViewById(R.id.data_item_next_button); 
//	}
}
