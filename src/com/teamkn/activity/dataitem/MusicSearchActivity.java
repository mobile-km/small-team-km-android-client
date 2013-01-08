package com.teamkn.activity.dataitem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.dataitem.DataItemListActivity.RequestCode;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.MusicInfo;
import com.teamkn.widget.adapter.DataItemListAdapter;
import com.teamkn.widget.adapter.MusicInfoSearchAdapter;



public class MusicSearchActivity extends Activity {
	
	private DataList data_list;
	
	private EditText v_query_text;
	private Button v_search_btn;
	private MusicInfo music_info;
	private LinearLayout v_music_result;
	private TextView v_music_title, v_album_title, v_author_name;
	private ImageView v_cover_src;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_search);
		
		load_UI();
    }
	
	private void load_UI() {
    	v_query_text = (EditText)findViewById(R.id.query_text);
    	v_search_btn = (Button)findViewById(R.id.search_btn);
    	    	
    	Intent intent = getIntent();
    	data_list = (DataList) intent.getSerializableExtra("data_list");
    	Log.d("aaaa", Integer.toString(data_list.server_data_list_id));
        music_info = (MusicInfo) intent.getSerializableExtra("music_info");
        
        
        if (music_info != null) {
        	v_music_result = (LinearLayout)findViewById(R.id.music_result);
        	v_music_title = (TextView)findViewById(R.id.music_title);
        	v_album_title = (TextView)findViewById(R.id.album_title);
        	v_author_name = (TextView)findViewById(R.id.author_name);
        	v_cover_src = (ImageView)findViewById(R.id.cover_src);
        	
        	v_music_result.setVisibility(View.VISIBLE);
        	v_music_title.setText(music_info.music_title);
        	v_album_title.setText(music_info.album_title);
        	v_author_name.setText(music_info.author_name);
        	
        	ImageCache.load_cached_image(music_info.cover_src, v_cover_src);
        }
   
	}
	
	public void do_search(View view) {
		final String query = v_query_text.getText().toString();
		
		if (BaseUtils.is_wifi_active(this)) {
			new TeamknAsyncTask<Void, Void, ArrayList<MusicInfo>>() {
				@Override
				public ArrayList<MusicInfo> do_in_background(Void... params) throws Exception {
					try {
						return HttpApi.DataItem.search_music(query);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				@Override
				public void on_success(ArrayList<MusicInfo> music_info_items) {
					Intent intent = new Intent(MusicSearchActivity.this,
							MusicSearchResultActivity.class);
				
					intent.putExtra("music_info_items", music_info_items);
					intent.putExtra("data_list", data_list);
					startActivity(intent);
				}
			}.execute();
		}else{
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
		}
	}
	
	
	public void save_data_item(View view) {
		
		if (BaseUtils.is_wifi_active(this)) {
			new TeamknAsyncTask<Void, Void, Void>() {
				@Override
				public Void do_in_background(Void... params) throws Exception {
					try {
						HttpApi.DataItem.create_music(music_info, data_list.server_data_list_id);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				@Override
				public void on_success(Void result) {
					Intent intent = new Intent(MusicSearchActivity.this, DataItemListActivity.class);
					intent.putExtra("data_list", data_list);
					
					startActivity(intent);
				}
			}.execute();
		}else{
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
		}
		
		
		
	}
	
}