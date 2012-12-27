package com.teamkn.activity.base.slidingmenu;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.database.UserDBHelper;
import com.teamkn.widget.adapter.DataListAdapter;

public class CreateDataListRightSliding extends RightSlidingContainer {
	public static class RequestCode {
		public final static String COLLECTION = "COLLECTION";
		public final static String STEP= "STEP";
		public static String data_list_type = COLLECTION;
		public static String data_list_public = "false";
	}

	private RadioButton radiobutton_COLLECTION;
	private RadioButton radiobutton_STEP;
	private View save_bn;
	
	private EditText create_data_list_et;
	private CheckBox data_list_public_checkbox;
	
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
	
	private OnClickListener save_listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			 final String add_data_list_et_str = create_data_list_et.getText().toString();
			 
			 if(BaseUtils.is_str_blank(add_data_list_et_str)){
				 BaseUtils.toast("列表名称不可以为空");
				 return;
			 }
			 
			 if(!BaseUtils.is_wifi_active(context)){
				 BaseUtils.toast(TeamknApplication.context.getResources().getString(R.string.is_wifi_active_msg));
				 return;
			 }
			 
			 save_bn.setClickable(false);
			 
     		new TeamknAsyncTask<Void, Void, DataList>(context,"正在创建") {
				@Override
				public DataList do_in_background(Void... params)
						throws Exception {
						data_list_public_checkbox();
						
						long current_seconds = System.currentTimeMillis();
						DataList dataList = new DataList(UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id ,
								add_data_list_et_str, RequestCode.data_list_type, RequestCode.data_list_public,"false",
								-1,current_seconds,current_seconds,-1,"false","false");
						DataList create_dataList = HttpApi.DataList.create(dataList);
					return create_dataList;
				}
				@Override
				public void on_success(DataList create_dataList) {
					final SlidingMenuView sliding_menu = (SlidingMenuView) context.findViewById(R.id.content_container);
					sliding_menu.close(false);
					DataListAdapter adapter = ((MainActivity)context).dataListAdapter;
					adapter.add_item(create_dataList);
					adapter.notifyDataSetChanged();
					save_bn.setClickable(true);
					create_data_list_et.setText("");
				}
			}.execute();
			
			
		}
	};
	

	public CreateDataListRightSliding(TeamknBaseActivity context, int layout_xml) {
		super(context, layout_xml);
	}

	@Override
	public void on_create() {
		radiobutton_COLLECTION = (RadioButton)right_root_view.findViewById(R.id.radiobutton_COLLECTION);
		radiobutton_COLLECTION.setSelected(true);
		radiobutton_COLLECTION.setOnClickListener(radioButtonListener);
		
		radiobutton_STEP = (RadioButton)right_root_view.findViewById(R.id.radiobutton_STEP);
		radiobutton_STEP.setOnClickListener(radioButtonListener);
		
		create_data_list_et = (EditText)right_root_view.findViewById(R.id.create_data_list_et);
		data_list_public_checkbox = (CheckBox)right_root_view.findViewById(R.id.data_list_public_checkbox);
		
		save_bn = right_root_view.findViewById(R.id.data_list_save_bn);
		save_bn.setOnClickListener(save_listener);
		
	}
	
	public void data_list_public_checkbox(){
		if(data_list_public_checkbox.isChecked()){
			RequestCode.data_list_public = "true";
		}else{
			RequestCode.data_list_public = "false";
		}
	}

}
