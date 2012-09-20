package com.teamkn.activity.datalist;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.widget.adapter.DataListAdapter;

public class SearchDataActivity extends TeamknBaseActivity{
	ListView search_result_list;
	LinearLayout list_no_data_show;
	DataListAdapter dataListAdapter ;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_data);
		
		search_result_list = (ListView)findViewById(R.id.search_result_list);
		list_no_data_show = (LinearLayout)findViewById(R.id.list_no_data_show);
        ImageButton   search_submit = (ImageButton)findViewById(R.id.search_submit);
        search_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText search_box    = (EditText) findViewById(R.id.search_box);
				final String search_str = search_box.getText().toString();
				search_list(search_str);
			}
		});   
        Intent intent = getIntent();
		String search_str = intent.getStringExtra("search_str");
		if(!BaseUtils.is_str_blank(search_str)){
			search_list(search_str);
		}
	}
	private void search_list(final String search_str){
		System.out.println("search_str  1" + search_str);
		System.out.println(BaseUtils.is_str_blank(search_str));
			new TeamknAsyncTask<Void, Void, List<DataList>>(SearchDataActivity.this,"正在搜索") {
				@Override
				public List<DataList> do_in_background(Void... params) throws Exception {
					List<DataList> datalists = new ArrayList<DataList>() ;
					System.out.println("search_str  1" + search_str);
					if(!BaseUtils.is_str_blank(search_str)){
						if (BaseUtils.is_wifi_active(SearchDataActivity.this)) {
							try {
								datalists = HttpApi.DataList.search(search_str);
								System.out.println("search_str  2 " + datalists.size());
							} catch (Exception e) {
								e.printStackTrace();
							}
					    }else{
							BaseUtils.toast("无法连接到网络，请检查网络配置");
						}
					}
					
					return datalists;
				}
				@Override
				public void on_success(List<DataList> datalists) {
					dataListAdapter = new DataListAdapter(SearchDataActivity.this);
					dataListAdapter.add_items(datalists);
					search_result_list.setAdapter(dataListAdapter);
					dataListAdapter.notifyDataSetChanged();
					if(datalists.size()==0){
						list_no_data_show.setVisibility(View.VISIBLE);
					}else{
						list_no_data_show.setVisibility(View.GONE);
					}
				}
			}.execute()	;
			search_result_list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> list_view, View list_item,
						int item_id, long position) {
					System.out.println(item_id + " : " + position);
					TextView info_tv = (TextView) list_item
							.findViewById(R.id.note_info_tv);
					final DataList item = (DataList) info_tv
							.getTag(R.id.tag_note_uuid);
					Intent intent = new Intent(SearchDataActivity.this,DataItemListActivity.class);
					intent.putExtra("data_list_id",item.id);
					startActivity(intent);
				}
		  });
	}
}
