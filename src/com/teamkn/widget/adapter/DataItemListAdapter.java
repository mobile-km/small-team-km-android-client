package com.teamkn.widget.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.MainActivity.RequestCode;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.activity.note.NoteListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataListDBHelper;

public class DataItemListAdapter extends TeamknBaseAdapter<DataItem> {
    Activity activity ;
    public DataItemListAdapter(TeamknBaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public View inflate_view() {
        return inflate(R.layout.list_data_item_list_item, null);
    }

    @Override
    public BaseViewHolder build_view_holder(View view) {

        ViewHolder view_holder      = new ViewHolder();
        view_holder.list_data_list_item_rl = (RelativeLayout) view.findViewById(R.id.list_data_list_item_rl);
        view_holder.list_note_title_tv_edit    = (TextView)  view.findViewById(R.id.list_note_title_tv_edit);
        view_holder.list_note_title_tv_go    = (TextView)  view.findViewById(R.id.list_note_title_tv_go);
        view_holder.note_info_tv = (TextView) view.findViewById(R.id.note_info_tv);
        view_holder.show_relativelayout = (RelativeLayout) view.findViewById(R.id.show_relativelayout);
        view_holder.update_data_list_et = (EditText) view.findViewById(R.id.update_data_list_et);
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final DataItem item,
                               int position) {
        final ViewHolder view_holder = (ViewHolder) holder;
        view_holder.note_info_tv.setTag(R.id.tag_note_uuid, item);
        view_holder.list_note_title_tv_edit.setText(item.title);
        view_holder.list_note_title_tv_go.setText(item.id+"");
        view_holder.update_data_list_et.setText(item.title);
        
      
        view_holder.list_note_title_tv_go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity,DataItemListActivity.class);
				activity.startActivity(intent);
			}
		}); 
    }

    private class ViewHolder implements BaseViewHolder {
        TextView list_note_title_tv_edit;   
        TextView list_note_title_tv_go;
        
        RelativeLayout show_relativelayout;
        EditText update_data_list_et;
        
        RelativeLayout list_data_list_item_rl;
        TextView note_info_tv;
    }
}
