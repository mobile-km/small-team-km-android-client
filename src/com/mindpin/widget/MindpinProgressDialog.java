package com.mindpin.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.mindpin.R;
import com.mindpin.base.utils.BaseUtils;

public class MindpinProgressDialog extends Dialog {
	private String message;

	public MindpinProgressDialog(Context context,String message) {
		super(context, R.style.mindpin_progress_dialog);
		this.message = message;
	}
	
	public MindpinProgressDialog(Context context,int resid){
		super(context, R.style.mindpin_progress_dialog);
		this.message = context.getString(resid);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		setContentView(R.layout.mindpin_progress_dialog);
		
		if(!BaseUtils.is_str_blank(this.message)){
			TextView message_textview = (TextView)findViewById(R.id.mindpin_progress_dialog_message);
			message_textview.setText(this.message);
		}
	}

	public static MindpinProgressDialog show(Context context, String message) {
		MindpinProgressDialog dialog = new MindpinProgressDialog(context, message);
		dialog.show();
		return dialog;
	}
	
	public static MindpinProgressDialog show(Context context){
		MindpinProgressDialog dialog = new MindpinProgressDialog(context, R.string.now_loading);
		dialog.show();
		return dialog;
	}

}
