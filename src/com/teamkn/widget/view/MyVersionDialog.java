package com.teamkn.widget.view;

import com.teamkn.R;
import com.teamkn.activity.base.AboutActivity;
import com.teamkn.activity.base.LoginActivity;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.login_guide.LoginSwitchViewDemoActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.VersionCheck;

import android.app.Dialog; 
import android.content.Context; 
import android.os.Bundle; 
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class MyVersionDialog extends Dialog{
	public static final class ActivityCheck{
		public static final int LOGIN_ACTIVITY = 0;
		public static final int ABOUT_ACTIVITY = 1;
	}
	 TextView title_tv , version_tv ,add_function_tv ,app_big_tv;
	 Button dialog_button_ok,dialog_button_cancel;
	 
	 int activity_check;
	 Context context;  
	 VersionCheck check;
	 public MyVersionDialog(Context context){         
		 super(context);               
		 this.context = context;     
	 }     
	 public MyVersionDialog(int activity_check,Context context, int theme,VersionCheck check){         
		 super(context, theme);  
		 this.activity_check = activity_check ;
		 this.context = context;   
		 this.check = check;
	 }     
	 @Override    
	 protected void onCreate(Bundle savedInstanceState) {               
		 super.onCreate(savedInstanceState);         
		 this.setContentView(R.layout.version_check_dialog);  
		 load_UI();
		 set_UI();
	 }
	private void set_UI() {
		
		if(check.action.equals(VersionCheck.Action.UPDATE)){
			title_tv.setText(context.getResources().getString(R.string.app_version_title_update_msg));
		}else if(check.action.equals(VersionCheck.Action.UPDATE)){
			title_tv.setText(context.getResources().getString(R.string.app_version_title_expired_msg));
		}
		version_tv.setText("最新版本：" + check.version);
		add_function_tv.setText(check.change_log);
		dialog_button_ok.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		dialog_button_cancel.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    switch (activity_check) {
				case ActivityCheck.LOGIN_ACTIVITY:
					if(((LoginActivity) context).current_user().is_show_tip){
			    		((LoginActivity) context).open_activity(LoginSwitchViewDemoActivity.class);
			    	}else{
			    		((LoginActivity) context).open_activity(MainActivity.class);
			    	}
					break;
				case ActivityCheck.ABOUT_ACTIVITY:
			    	((AboutActivity) context).open_activity(AboutActivity.class);
					break;
				default:
					break;
				}
		    	
			}
		});
		
		System.out.println("check "+ check.toString());
	}
	private void load_UI() {
		title_tv =  (TextView)findViewById(R.id.title_tv);
		version_tv =  (TextView)findViewById(R.id.version_tv);
		add_function_tv =  (TextView)findViewById(R.id.add_function_tv);
		app_big_tv =  (TextView)findViewById(R.id.app_big_tv);
		
		dialog_button_ok = (Button)findViewById(R.id.dialog_button_ok);
		dialog_button_cancel = (Button)findViewById(R.id.dialog_button_cancel);
	}   
	 
}
