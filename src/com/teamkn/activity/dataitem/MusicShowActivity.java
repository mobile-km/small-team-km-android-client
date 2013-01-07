package com.teamkn.activity.dataitem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;

public class MusicShowActivity extends Activity {
	private DataList data_list;
	private DataItem data_item;
	
	private TextView v_music_title, v_album_title, v_author_name;
	private ImageView v_cover_src;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_show);
        
        Intent intent = getIntent();
        data_list = (DataList) intent.getSerializableExtra("data_list");
        data_item = (DataItem) intent.getSerializableExtra("data_item");
		
		load_UI();
    }
    
    private void load_UI() {
    	v_music_title = (TextView)findViewById(R.id.music_title);
    	v_album_title = (TextView)findViewById(R.id.album_title);
    	v_author_name = (TextView)findViewById(R.id.author_name);
    	v_cover_src = (ImageView)findViewById(R.id.cover_src);
    	
    	v_music_title.setText(data_item.music_info.music_title);
    	v_album_title.setText(data_item.music_info.album_title);
    	v_author_name.setText(data_item.music_info.author_name);
    	
    	ImageCache.load_cached_image(data_item.music_info.cover_src, v_cover_src);
	}

}
