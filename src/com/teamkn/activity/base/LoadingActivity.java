package com.teamkn.activity.base;

import android.os.Bundle;
import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

//此乃应用注册入口
public class LoadingActivity extends TeamknBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_loading);

        // 这里是应用的入口，进入该activity后再根据当前登录状态，进入login或是main
        open_activity(is_logged_in() ? MainActivity.class : LoginActivity.class);
        finish();
    }
}
