package com.teamkn.activity.search;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataList;
import com.teamkn.widget.adapter.DataListAdapter;
import com.teamkn.widget.adapter.SearchAdapter;
import com.teamkn.widget.adapter.UserAdapter;

public class SearchActivity extends TeamknBaseActivity{
	public static class RequestCode {
		public static String data_list_public = "false";
	}
	
	private EditText search_box;
//	private ImageButton search_submit;
//	private Button search_list_btn,search_user_btn;
	private ListView search_result_list;
	private LinearLayout list_no_data_show;
	
	boolean search_list = true;
	List<DataList> lists;
	DataListAdapter listAdapter;
	List<AccountUser> users;
	UserAdapter userAdapter;
	
	List<String> search_history_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		load_ui();
		load_search_history();
	}
	private void load_ui() {
		search_box = (EditText)findViewById(R.id.search_box);
//		search_submit = (ImageButton)findViewById(R.id.search_submit);
//		search_list_btn = (Button)findViewById(R.id.search_list_btn);
//		search_user_btn = (Button)findViewById(R.id.search_user_btn);
		search_result_list = (ListView)findViewById(R.id.search_result_list);
		list_no_data_show = (LinearLayout)findViewById(R.id.list_no_data_show);
	}
	public void click_search_list(View view){
		search_list = true;
		load_search_history();
	}
	public void click_search_user(View view){
		search_list = false;
		load_search_history();
	}
	public void click_search_submit(View view){
		String search_str = search_box.getText().toString();
		if(!BaseUtils.is_str_blank(search_str)){
			httpApi(search_str);
		}
	}
	private void httpApi(final String search_str){
		if (BaseUtils.is_wifi_active(this)) {
	    	new TeamknAsyncTask<Void, Void, List<Object>>(this,getResources().getString(R.string.now_search)) {
				@Override
				public List<Object> do_in_background(Void... params){
					try {
						if(search_list){
							if(RequestCode.data_list_public.equals("true")){
								lists = HttpApi.DataList.search_public_timeline(search_str);
							}else if(RequestCode.data_list_public.equals("false")){
								lists = HttpApi.DataList.search_mine(search_str);
							}else if(RequestCode.data_list_public.equals("watch")){
								lists = HttpApi.DataList.search_mine_watch(search_str);
							}
							FileDirs.writeTxtFile(FileDirs.TEAMKN_SEARCH_LIST, search_str);
						}else{
							users = HttpApi.search_user(search_str,1,100);
							FileDirs.writeTxtFile(FileDirs.TEAMKN_SEARCH_USER, search_str);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				@Override
				public void on_success(List<Object> datalists) {
					load_search_history();
					if( (lists!=null && lists.size() > 0 ) || (users!=null && users.size() > 0)){
						load_list();
						search_result_list.setVisibility(View.VISIBLE);
						list_no_data_show.setVisibility(View.GONE);
					}else{
						search_result_list.setVisibility(View.GONE);
						list_no_data_show.setVisibility(View.VISIBLE);
					}
				}
			}.execute();
    	}else{
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
		}
	}
	private void load_list(){
		if(search_list){
			load_data_list();
		}else{
			load_user_list();
		}
		search_result_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				BaseUtils.toast("item_id = " + item_id);
			}
		});
	}
	private void load_data_list(){
		listAdapter = new DataListAdapter(this);
		listAdapter.add_items(lists);
		search_result_list.setAdapter(listAdapter);
	}
	
	private void load_user_list(){
		userAdapter = new UserAdapter(this);
		userAdapter.add_items(users);
		search_result_list.setAdapter(userAdapter);
	}
	private void search_box_set_text(String text) {
        EditText search_box = (EditText) findViewById(R.id.search_box);
        search_box.setText(text);
    }
	private void load_search_history() {
		ListView search_history =
                (ListView) findViewById(R.id.search_history);
        add_records(search_history);
    }
	private void add_records(ListView search_history) {
		// 获取信息
		if(search_list){
			search_history_list = FileDirs.readTxtFileList(FileDirs.TEAMKN_SEARCH_LIST);
		}else{
			search_history_list = FileDirs.readTxtFileList(FileDirs.TEAMKN_SEARCH_USER);
		}
        if (!search_history_list.isEmpty()) {
//        	layout.removeAllViews();
//            for (String record_text: search_history_list) {
//                Button record = new Button(this);
//                record.setText(record_text);
//                layout.addView(record);
//                record.setOnClickListener(new SearchHistoryRecordClickListener());
//            }
        	SearchAdapter adapter = new SearchAdapter(this);
        	adapter.add_items(search_history_list);
        	search_history.setAdapter(adapter);
        	search_history.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> list_view, View list_item,
						int item_id, long position) {
					TextView info_tv = (TextView) list_item.findViewById(R.id.info_tv);
					String record_string = (String) info_tv.getTag(R.id.tag_note_uuid);
		            search_box_set_text(record_string);
		            load_search_history();
		            httpApi(record_string);
				}
			});
        }
    }
}
