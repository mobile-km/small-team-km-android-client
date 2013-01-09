package com.teamkn.widget.adapter;

import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.MusicInfo;


public class MusicInfoSearchAdapter extends TeamknBaseAdapter<MusicInfo> {
    public MusicInfoSearchAdapter(TeamknBaseActivity activity) {
        super(activity);
    }
    @Override
    public View inflate_view() {
        return inflate(R.layout.music_info_items, null);
    }

    @Override
    public BaseViewHolder build_view_holder(View view) {
        ViewHolder view_holder      = new ViewHolder();
        view_holder.v_music_info_id = (TextView) view.findViewById(R.id.music_info_id);
        view_holder.v_music_title = (TextView) view.findViewById(R.id.music_title);
        
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               MusicInfo item,
                               int position) {
        ViewHolder view_holder = (ViewHolder) holder;
        view_holder.v_music_info_id.setTag(R.id.music_info_id, item);
        
        view_holder.v_music_title.setText(item.music_title);
    }
    
    private class ViewHolder implements BaseViewHolder {
    	TextView v_music_info_id;
    	TextView v_music_title;
    }
}