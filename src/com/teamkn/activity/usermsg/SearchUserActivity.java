package com.teamkn.activity.usermsg;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.AccountUser;
import com.teamkn.widget.adapter.UserAdapter;

public class SearchUserActivity extends TeamknBaseActivity{
	EditText search_et;
	ImageButton search_ib;
	ListView list_view;
	
	UserAdapter adapter;
	List<AccountUser> users;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_user);
		load_ui();
	}
	private void load_ui(){
		search_et = (EditText)findViewById(R.id.search_box);
		search_ib = (ImageButton)findViewById(R.id.search_submit);
		list_view = (ListView)findViewById(R.id.list_view);
		search_ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String search_str = search_et.getText().toString();
				if(!BaseUtils.is_str_blank(search_str)){
					load_list(search_str);
				}else{
					BaseUtils.toast("输入的内容不能为空");
				}
			}
		});
	}
	private void load_list(final String query){
		if (BaseUtils.is_wifi_active(SearchUserActivity.this)) {
	    	new TeamknAsyncTask<Void, Void, List<AccountUser>>(SearchUserActivity.this,"内容加载中") {
				@Override
				public List<AccountUser> do_in_background(Void... params)
						throws Exception {
							users = HttpApi.search_user(query,1,100);
						return null;
				}
				@Override
				public void on_success(List<AccountUser> datalists) {
					set_list_ui();
				}
			}.execute();
    	}else{
			BaseUtils.toast("无法连接到网络，请检查网络配置");
		}
	}
	private void set_list_ui(){
		list_view.setVisibility(View.VISIBLE);
		adapter = new UserAdapter(this);
		adapter.add_items(users);
		list_view.setAdapter(adapter);
	}
}
