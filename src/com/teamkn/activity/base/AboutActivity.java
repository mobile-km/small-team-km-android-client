package com.teamkn.activity.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.login_guide.LoginSwitchViewDemoActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.VersionCheck;
import com.teamkn.widget.adapter.AboutListAdapter;
import com.teamkn.widget.view.MyVersionDialog;

public class AboutActivity extends TeamknBaseActivity{
	ListView about_list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      setContentView(R.layout.base_about);
        setContentView(R.layout.about_apk);
        load_UI();
    }
    private void load_UI() {
    	about_list_view = (ListView)findViewById(R.id.about_list_view);
    	AboutListAdapter adapter = new AboutListAdapter(this);
    	adapter.add_items(getList());
    	about_list_view.setAdapter(adapter);
    	about_list_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				clickItem(arg2);
			}
		});
	}
    private void clickItem(int id){
    	switch (id) {
		case 0:
			break;
		case 1:  //点击的是欢迎界面的项
			LoginSwitchViewDemoActivity.is_login_go_to = false;
			open_activity(LoginSwitchViewDemoActivity.class);
			break;
		case 2:  //点击的是功能介绍的项
			
			break;
		case 3:  //点击的是关于软件的项
			click_item_version();
			break;
		default:
			break;
		}
//    	BaseUtils.toast("about click item id = " + id);
    }
    private void click_item_version(){
    	if (BaseUtils.is_wifi_active(this)) {
	    	new TeamknAsyncTask<Void, Void, VersionCheck>(AboutActivity.this,"正在检测") {
				@Override
				public VersionCheck do_in_background(Void... params)
						throws Exception {
					 String version = getResources().getString(R.string.app_version);
	//	             version = "0.51";
					 return HttpApi.get_version(version);
				}
				@Override
				public void on_success(VersionCheck check) {
					Dialog dialog = new MyVersionDialog(MyVersionDialog.ActivityCheck.ABOUT_ACTIVITY ,AboutActivity.this, R.style.MyVersionDialog,check);                   
					dialog.show();
				}
	    	}.execute();
    	}else{
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
		}
    }
	public void open_teamkn_website(View view) {
        Uri uri = Uri.parse("http://www.teamkn.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
	
	private List<Map<String, Object>> getList(){
		List<Map<String, Object>> lists = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "当前版本");
		map.put("img", R.drawable.translucence_hand);
		
		lists.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", "欢迎页面");
		map.put("img", R.drawable.translucence_hand);
		
		lists.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", "功能介绍");
		map.put("img", R.drawable.translucence_hand);
		
		lists.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", "检查新版");
		map.put("img", R.drawable.translucence_hand);
		
		lists.add(map);
		
		return lists;
	}
}
