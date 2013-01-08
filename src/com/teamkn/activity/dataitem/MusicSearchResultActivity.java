package com.teamkn.activity.dataitem;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


import com.teamkn.R;
import com.teamkn.activity.dataitem.pull.DataItemPullListActivity;
import com.teamkn.activity.dataitem.pull.DataItemPullUpdateActivity;
import com.teamkn.activity.dataitem.pull.DataItemPullListActivity.RequestCode;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.MusicInfo;
import com.teamkn.model.User;
import com.teamkn.widget.adapter.DataItemPullAdapter;
import com.teamkn.widget.adapter.MusicInfoSearchAdapter;

public class MusicSearchResultActivity extends TeamknBaseActivity {
	
	private DataList data_list;
	private ArrayList<MusicInfo> data_items;
    private ListView search_result;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_search_result);
        
        load_listview();
    }
	
	
	private void load_listview() {
		search_result = (ListView) findViewById(R.id.list);
		
		Intent intent = getIntent();
		data_list = (DataList) intent.getSerializableExtra("data_list");
        data_items = (ArrayList<MusicInfo>) intent.getSerializableExtra("music_info_items");
        
     
		try {
			MusicInfoSearchAdapter adapter = new MusicInfoSearchAdapter(this);
			adapter.add_items(data_items);
			search_result.setAdapter(adapter);
			search_result.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> list_view, View list_item,
						int item_id, long position) {
					TextView selected_music = (TextView) list_item.findViewById(R.id.music_info_id);
					MusicInfo item = (MusicInfo) selected_music.getTag(R.id.music_info_id);
					Intent intent = new Intent(MusicSearchResultActivity.this, MusicSearchActivity.class);
					intent.putExtra("music_info", item);
					intent.putExtra("data_list", data_list);			
					startActivity(intent);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}