package com.teamkn.activity.base;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.teamkn.Logic.HttpApi;
import com.teamkn.R;
import com.teamkn.activity.login_guide.LoginSwitchViewDemoActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;

public class LoginActivity extends TeamknBaseActivity {
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_login);
    }

    public void login_button_click(View view) {
        prepare_email_and_password();
        if (is_params_valid()) {
            do_login();
        }
    }
    public void register_button_click(View view){
    	open_activity(RegisterActivity.class);
        finish();
    }
    //获取邮箱，密码字符串。作准备。
    private void prepare_email_and_password() {
        EditText email_et = (EditText) findViewById(R.id.email_et);
        EditText password_et = (EditText) findViewById(R.id.password_et);
        email = email_et.getText().toString();
        password = password_et.getText().toString();
    }

    //参数检查
    private boolean is_params_valid() {

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
    private void do_login() {
        new TeamknAsyncTask<String, Void, Void>(this, R.string.login_now_login) {
            @Override
            public Void do_in_background(String... params) throws Exception {
                // 为了在不联网的情况下使用，注释掉
                String email = params[0];
                String password = params[1];
                HttpApi.user_authenticate(email, password);
                return null;
            }
            @Override
            public void on_success(Void v) {
            	is_first_login(true);
                finish();
            }
        }.execute(email, password);
    }
    private void is_first_login(boolean is_first){
    	if(is_first){
    		open_activity(LoginSwitchViewDemoActivity.class);
    	}else{
    		open_activity(MainActivity.class);
    	}
    	
    }
}
