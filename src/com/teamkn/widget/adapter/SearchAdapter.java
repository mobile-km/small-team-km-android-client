package com.teamkn.widget.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;

public class SearchAdapter extends TeamknBaseAdapter<String>{
    Activity activity ;
    public SearchAdapter(TeamknBaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public View inflate_view() {
        return inflate(R.layout.tkn_search_list_item, null);
    }

    @Override
    public BaseViewHolder build_view_holder(View view) {
    	
        ViewHolder view_holder      = new ViewHolder();
        view_holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
        
        //个人列表
        view_holder.str_tv    = (TextView)  view.findViewById(R.id.str_tv);
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final String item,
                               int position) {
    	
    	final ViewHolder view_holder = (ViewHolder) holder;
        view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
        view_holder.str_tv.setText(item);
    }
    
    private class ViewHolder implements BaseViewHolder {
    	TextView info_tv;
        TextView str_tv;   
    }
}
