package com.teamkn.activity.qrcode_result;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
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
		QRCodeResult codeResult = (QRCodeResult) bundle.get("code_result");
		
		TextView qrcode_result_tv = (TextView) findViewById(R.id.qrcode_result_tv);
		qrcode_result_tv.setText(codeResult.farmat + " ------  " + codeResult.text);
	}
}
