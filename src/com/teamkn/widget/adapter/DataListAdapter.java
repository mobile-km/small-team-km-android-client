package com.teamkn.widget.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataListDBHelper;

public class DataListAdapter extends TeamknBaseAdapter<DataList> {
    Activity activity ;
    public DataListAdapter(TeamknBaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public View inflate_view() {
        return inflate(R.layout.list_data_list_item, null);
    }

    @Override
    public BaseViewHolder build_view_holder(View view) {
    	
        ViewHolder view_holder      = new ViewHolder();
        view_holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
        
        //个人列表
        view_holder.show_is_no_public_relativelayout = (RelativeLayout) view.findViewById(R.id.show_is_no_public_relativelayout);
        view_holder.list_note_title_tv_edit    = (TextView)  view.findViewById(R.id.list_note_title_tv_edit);
        view_holder.list_note_title_tv_go    = (TextView)  view.findViewById(R.id.list_note_title_tv_go);
        view_holder.list_data_list_eye_tv = (TextView) view.findViewById(R.id.list_data_list_eye_tv);
       
        //公共列表
        view_holder.show_is_yes_public_relativelayout = (RelativeLayout)view.findViewById(R.id.show_is_yes_public_relativelayout);
        view_holder.data_list_item_user_avatar_iv= (ImageView)view.findViewById(R.id.data_list_item_user_avatar_iv);
        view_holder.data_list_item_user_name_tv = (TextView)view.findViewById(R.id.data_list_item_user_name_tv);
        view_holder.list_title_tv_public = (TextView)view.findViewById(R.id.list_title_tv_public);
        view_holder.list_collect_tv_public = (TextView)view.findViewById(R.id.list_collect_tv_public);
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final DataList item,
                               int position) {
    	
        final ViewHolder view_holder = (ViewHolder) holder;
        view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
        if((item.public_boolean.equals("false")
        		|| item.user_id == AccountManager.current_user().user_id)
        		&& MainActivity.RequestCode.data_list_public.equals("false")
        		){
        	
        	view_holder.show_is_no_public_relativelayout.setVisibility(View.VISIBLE);
        	view_holder.show_is_yes_public_relativelayout.setVisibility(View.GONE);
           
            view_holder.list_note_title_tv_edit.setText(item.title);
            view_holder.list_note_title_tv_go.setText(item.id+":"+item.server_data_list_id);
//            System.out.println(item.public_boolean);
    		if(item.public_boolean.equals("true")){
    			view_holder.list_data_list_eye_tv.setText("分享");
    		}else if(item.public_boolean.equals("false")){
    			view_holder.list_data_list_eye_tv.setText("不分享");
    		}
            if(item.kind.equals(MainActivity.RequestCode.STEP)){
            	view_holder.list_note_title_tv_go.setBackgroundColor(activity.getResources().getColor(R.color.burlywood));
            }else if(item.kind.equals(MainActivity.RequestCode.COLLECTION)){
            	view_holder.list_note_title_tv_go.setBackgroundColor(activity.getResources().getColor(R.color.blueviolet));
            }
        }else{
        	view_holder.show_is_no_public_relativelayout.setVisibility(View.GONE);
        	view_holder.show_is_yes_public_relativelayout.setVisibility(View.VISIBLE);
            view_holder.list_title_tv_public.setText(item.title);
        }
   
    }
    
    private class ViewHolder implements BaseViewHolder {
    	
    	TextView info_tv;
    	
    	// 个人列表子项显示
    	RelativeLayout show_is_no_public_relativelayout;
        TextView list_note_title_tv_edit;   
        TextView list_note_title_tv_go;
        TextView list_data_list_eye_tv;
        
        
        // 公共列表子项显示
        RelativeLayout show_is_yes_public_relativelayout;
        ImageView data_list_item_user_avatar_iv;
        TextView data_list_item_user_name_tv;
        TextView list_title_tv_public;
        TextView list_collect_tv_public;
    }
}
