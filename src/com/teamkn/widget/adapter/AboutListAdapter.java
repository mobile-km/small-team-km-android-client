package com.teamkn.widget.adapter;

import java.util.Map;

import android.content.Context;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;

public class AboutListAdapter extends TeamknBaseAdapter<Map<String,Object>>{
	Context context;
	public AboutListAdapter(TeamknBaseActivity activity) {
		super(activity);
		context = activity;
	}
	@Override
	public View inflate_view() {
		return inflate(R.layout.list_menu_list_item, null);
	}
	@Override
	public BaseViewHolder build_view_holder(View view) {
		ViewHolder view_holder = new ViewHolder();
//        view_holder.list_item_rl = (RelativeLayout) view.findViewById(R.id.list_item_rl);
        view_holder.info_tv    = (TextView)  view.findViewById(R.id.info_tv);
        view_holder.list_title_tv_go    = (TextView)  view.findViewById(R.id.list_title_tv_go);
        view_holder.list_title_tv = (TextView) view.findViewById(R.id.list_title_tv);
        return view_holder;
	}
	@Override
	public void fill_with_data(BaseViewHolder holder,Map<String, Object> item, int position) {		
		 ViewHolder view_holder = (ViewHolder) holder;
	     view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
	     view_holder.list_title_tv.setText(item.get("title").toString());
//	     view_holder.list_title_tv_go.setBackgroundResource(Integer.valueOf(item.get("img").toString()));
	     if(position==0){
	    	 view_holder.list_title_tv_go.setText(context.getResources().getString(R.string.app_version));
	     }else{
	    	 view_holder.list_title_tv_go.setText(">");
	     }
	     
	     TextPaint paint = view_holder.list_title_tv_go.getPaint();  
	     paint.setFakeBoldText(true);
	}
	private class ViewHolder implements BaseViewHolder {
		TextView list_title_tv;   
        TextView list_title_tv_go;
        
//        RelativeLayout list_item_rl;
        TextView info_tv;
    }
}
