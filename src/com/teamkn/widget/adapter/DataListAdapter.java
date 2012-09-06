package com.teamkn.widget.adapter;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.MainActivity.RequestCode;
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
        view_holder.list_note_title_tv_edit    = (TextView)  view.findViewById(R.id.list_note_title_tv_edit);
        view_holder.list_note_title_tv_go    = (TextView)  view.findViewById(R.id.list_note_title_tv_go);
//        view_holder.note_info_tv = (TextView) view.findViewById(R.id.note_info_tv);
        view_holder.show_relativelayout = (RelativeLayout) view.findViewById(R.id.show_relativelayout);
        view_holder.update_data_list_et = (EditText) view.findViewById(R.id.update_data_list_et);
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final DataList item,
                               int position) {
        final ViewHolder view_holder = (ViewHolder) holder;
//        view_holder.note_info_tv.setTag(R.id.tag_note_uuid, item.uuid);
        view_holder.list_note_title_tv_edit.setText(item.title);
        view_holder.list_note_title_tv_go.setText(item.id+"");
        
        view_holder.list_note_title_tv_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view_holder.show_relativelayout.setVisibility(View.GONE);
				view_holder.update_data_list_et.setVisibility(View.VISIBLE);
				view_holder.update_data_list_et.setText(item.title);
				view_holder.update_data_list_et.requestFocus();
			}
		});
        view_holder.list_note_title_tv_go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseUtils.toast("进入 ： " + item.id + " : " + item.title);
			}
		});
        view_holder.update_data_list_et.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {	
				if(view_holder.update_data_list_et.hasFocus()==false){       
                    final String add_data_list_et_str = view_holder.update_data_list_et.getText().toString();
                    if(add_data_list_et_str!=null 
                    		&& !add_data_list_et_str.equals(null)
                    		&& !BaseUtils.is_str_blank(add_data_list_et_str)){

                    	Toast.makeText(activity, "可以进行验证  " + add_data_list_et_str, Toast.LENGTH_SHORT).show(); 
                    	
                    	if(BaseUtils.is_wifi_active(activity)){
    						try {
    							
    							item.setTitle(add_data_list_et_str);
    							DataListDBHelper.update(item);   							
    							HttpApi.DataList.update(DataListDBHelper.find(item.id));		
							} catch (Exception e) {
								e.printStackTrace();
							}
    					}
                    	view_holder.update_data_list_et.setText(null);
                    	view_holder.show_relativelayout.setVisibility(View.VISIBLE);
        				view_holder.update_data_list_et.setVisibility(View.GONE);
        				view_holder.list_note_title_tv_edit.post(new Runnable() {
							
							@Override
							public void run() {
								view_holder.list_note_title_tv_edit.setText(add_data_list_et_str);	
							}
						});
//                    	view_holder.update_data_list_et.setFocusable(false);
//                    	load_list();
                    }
                }
			}
		});
    }

    private class ViewHolder implements BaseViewHolder {
        TextView list_note_title_tv_edit;   
        TextView list_note_title_tv_go;
        
        RelativeLayout show_relativelayout;
        EditText update_data_list_et;
//        TextView note_info_tv;
    }

}
