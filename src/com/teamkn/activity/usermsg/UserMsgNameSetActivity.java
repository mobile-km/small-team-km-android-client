package com.teamkn.activity.usermsg;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;

public class UserMsgNameSetActivity extends TeamknBaseActivity{
	private EditText et_set_name;
	private TextView tv_show_update_error;
	
	public static String requestError = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_msg_set_name);
		load_ui();
		et_set_name.setText(current_user().name);
		tv_show_update_error.setText("");  
	}
    private void load_ui(){
    	et_set_name = (EditText)findViewById(R.id.et_set_name);
    	tv_show_update_error = (TextView)findViewById(R.id.tv_show_update_error);
    }
    public void click_button_save_name(View view){
    	tv_show_update_error.setText("");
    	String load_name = et_set_name.getText().toString();
    	if(BaseUtils.is_str_blank(load_name)){
    		tv_show_update_error.setText("用户名不可以为空");
    	}else{
    		request_push_name(load_name);
    	}
    }
	private void request_push_name(final String load_name) {
		new TeamknAsyncTask<Void, Void, Integer>(UserMsgNameSetActivity.this,"信息已提交") {
			@Override
			public Integer do_in_background(Void... params) throws Exception {
				if(BaseUtils.is_wifi_active(UserMsgNameSetActivity.this)){
			    	HttpApi.user_set_name(load_name);
			    }
			    return 1;
			}
			@Override
			public void on_success(Integer client_chat_node_id) {
				if(requestError==null){
					open_activity(UserMsgActivity.class);
	                finish();
				}else{
					tv_show_update_error.setText(requestError);
				}
		    }
       }.execute();
	}
}
