package com.mindpin.activity.base;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.base.utils.BaseUtils;

public class LoginActivity extends MindpinBaseActivity {
	private String email;
	private String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_login);
	}
	
	public void login_button_click(View view){
		prepare_email_and_password();				
		if(is_params_valid()){
			do_login();
		}
	}
	
	//获取邮箱，密码字符串。作准备。
	private void prepare_email_and_password(){
		EditText email_et = (EditText)findViewById(R.id.email_et);
		EditText password_et = (EditText)findViewById(R.id.password_et);
		email = email_et.getText().toString();
		password = password_et.getText().toString();
	}
	
	//参数检查
	private boolean is_params_valid(){
		
		//邮箱，密码不可以空
		if (BaseUtils.is_str_blank(email)) {
			BaseUtils.toast(R.string.login_email_valid_blank);
			return false;
		}

		if (BaseUtils.is_str_blank(password)) {
			BaseUtils.toast(R.string.login_password_valid_blank);
			return false;
		}
		
		return true;
	}
	
	//显示正在登录，并在一个线程中进行登录
	private void do_login(){		
		new MindpinAsyncTask<String, Void, Void>(this, R.string.login_now_login){
			@Override
			public Void do_in_background(String... params) throws Exception {
				String email = params[0];
				String password = params[1];
				HttpApi.user_authenticate(email, password);
				return null;
			}

			@Override
			public void on_success(Void v) {
				open_activity(MainActivity.class);
				finish();
			}
		}.execute(email, password);
	}
}
