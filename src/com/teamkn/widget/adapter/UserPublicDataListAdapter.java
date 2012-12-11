package com.teamkn.widget.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.DataList;

public class UserPublicDataListAdapter extends TeamknBaseAdapter<DataList> {
    Activity activity ;
    public UserPublicDataListAdapter(TeamknBaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public View inflate_view() {
        return inflate(R.layout.user_public_list_data_list_item, null);
    }

    @Override
    public BaseViewHolder build_view_holder(View view) {
    	
        ViewHolder view_holder      = new ViewHolder();
        view_holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
        
        //个人列表
        view_holder.list_note_title_tv_edit    = (TextView)  view.findViewById(R.id.list_note_title_tv_edit);
        view_holder.list_data_list_eye_tv = (TextView) view.findViewById(R.id.mi_list_data_list_eye_tv);
        view_holder.list_type_tv = (TextView)view.findViewById(R.id.list_type_tv);
        view_holder.data_list_forked_iv = (ImageView)view.findViewById(R.id.data_list_forked_iv);
        
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final DataList item,
                               int position) {
    	
    	final ViewHolder view_holder = (ViewHolder) holder;
        view_holder.info_tv.setTag(R.id.tag_note_uuid, item);

            	
    	String title = item.title;
    	if(title.length()>15){
        	title = title.substring(0, 12) + "..";
        }
        view_holder.list_note_title_tv_edit.setText(title);
        
//                view_holder.list_note_title_tv_go.setText(item.id+":"+item.server_data_list_id);
		if(item.public_boolean.equals("true")){
			view_holder.list_data_list_eye_tv.setText("分享");
		}else if(item.public_boolean.equals("false")){
			view_holder.list_data_list_eye_tv.setText("不分享");
		}
        if(item.kind.equals(MainActivity.RequestCode.STEP)){
        	view_holder.list_type_tv.setText("步骤");
        }else if(item.kind.equals(MainActivity.RequestCode.COLLECTION)){
        	view_holder.list_type_tv.setText("收集");
        }
        
		if(item.has_commits.equals("true")){
			view_holder.data_list_forked_iv.setVisibility(View.VISIBLE);
			view_holder.data_list_forked_iv.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.star_blue));
		}else{
			view_holder.data_list_forked_iv.setVisibility(View.GONE);
//        			view_holder.data_list_forked_iv.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_no));
		} 
    }
    
    private class ViewHolder implements BaseViewHolder {
    	TextView info_tv;
    	// 个人列表子项显示
        TextView list_note_title_tv_edit;   
        TextView list_data_list_eye_tv;
        TextView list_type_tv;
        ImageView data_list_forked_iv;

    }
}
