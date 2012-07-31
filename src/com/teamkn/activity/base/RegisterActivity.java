package com.teamkn.activity.base;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.chat.ChatActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.ChatNode;
import com.teamkn.model.User;
import com.teamkn.model.database.ChatNodeDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper.Kind;

public class RegisterActivity extends TeamknBaseActivity{
	public static String questError = null;
	private EditText et_email,et_name,et_password;
	private TextView tv_intent_no,tv_email_repead,tv_name_repead,tv_email_no,tv_name_no,tv_password_no;
	private LinearLayout ll_show;
	String email,name,password;
	public void click_headbar_button_back(View view){
		restart_to_login();
	}
	public void click_register_button(View view){
		//判断 网络，填写的是否有空值
		if(judgeRegister()){
			clearTV();
			questVoid();	
		}
	}
	public void questVoid(){
		 new TeamknAsyncTask<Void, Void, Integer>(RegisterActivity.this,"信息已提交") {
				@Override
				public Integer do_in_background(Void... params) throws Exception {
					questError = null;
					if(BaseUtils.is_wifi_active(RegisterActivity.this)){
				    	HttpApi.user_register(email, name, password);
				    }
				    return 1;
				}
				@Override
				public void on_success(Integer client_chat_node_id) {
					
					if(questError==null){
						open_activity(MainActivity.class);
		                finish();
					}else{
						ll_show.setVisibility(View.VISIBLE);
						tv_email_repead.setVisibility(View.VISIBLE);
						tv_email_repead.setText(questError);
					}
			    }
	   }.execute();
	}
    public void clearTV(){
    	ll_show.setVisibility(View.GONE);
    	tv_intent_no.setVisibility(View.GONE);
    	tv_email_no.setVisibility(View.GONE);
    	tv_name_no.setVisibility(View.GONE);
    	tv_password_no.setVisibility(View.GONE);
    	tv_email_repead.setVisibility(View.GONE);
    	tv_name_repead.setVisibility(View.GONE);
    }
	public boolean judgeRegister(){
		 boolean judge = true;
		 email = et_email.getText().toString();
		 name = et_name.getText().toString();
	     password = et_password.getText().toString();
		
		if( ! BaseUtils.is_wifi_active(RegisterActivity.this)){
			tv_intent_no.setVisibility(View.VISIBLE);
			judge = false;
		}
		
		if(BaseUtils.is_str_blank(email)){
			tv_email_no.setVisibility(View.VISIBLE);
			judge = false;
		}
		if(BaseUtils.is_str_blank(name)){
			tv_name_no.setVisibility(View.VISIBLE);
			judge = false;
		}
		if(BaseUtils.is_str_blank(password)){
			tv_password_no.setVisibility(View.VISIBLE);
			judge = false;
		}
		if(!judge){
			ll_show.setVisibility(View.VISIBLE);
		}
		return judge;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_register);
		load_ui();	
	}
	private void load_ui(){
		et_email = (EditText)findViewById(R.id.register_edittext_email);
		et_name = (EditText)findViewById(R.id.register_edittext_name);
		et_password = (EditText)findViewById(R.id.register_edittext_password);
		
		ll_show = (LinearLayout)findViewById(R.id.register_tv_error_show);
		tv_email_repead = (TextView)findViewById(R.id.register_tv_error_show_email_repeat);
		tv_name_repead = (TextView)findViewById(R.id.register_tv_error_show_name_repeat);
		tv_email_no = (TextView)findViewById(R.id.register_tv_error_show_email_no);
		tv_name_no = (TextView)findViewById(R.id.register_tv_error_show_name_no);
		tv_password_no = (TextView)findViewById(R.id.register_tv_error_show_password_no);
		tv_intent_no = (TextView)findViewById(R.id.register_tv_error_show_intent);
	}
}
