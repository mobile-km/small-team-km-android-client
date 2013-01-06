package com.teamkn.activity.qrcode_result;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.QRCodeResult;

public class QRcodeResultActivity extends TeamknBaseActivity{
	String barcode_format  ;
	String text  ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_result);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		QRCodeResult code_result = (QRCodeResult) bundle.get("code_result");
		
		TextView qrcode_result_tv = (TextView) findViewById(R.id.qrcode_result_tv);
		qrcode_result_tv.setText(code_result.code);
		http_api(code_result.code);
	}
	private void http_api(String code) {
		
		if(BaseUtils.is_str_blank(code)){
			return ;
		}
		
    	if (!BaseUtils.is_wifi_active(this)) {
    		BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
    		return ;
    	}
    	
        new TeamknAsyncTask<String, Void, Object>(this, R.string.login_now_login) {
            @Override
            public Object do_in_background(String... params) throws Exception {
            	String code = params[0];
            	HttpApi.get_qrcode_result(code);
                return null;
            }
            @Override
            public void on_success(Object check) {
            	if(check != null){
            		
            	}else{
            		
            	}
            }
        }.execute(code);
    	
    }
}
