package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.usermsg.UserMsgActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.User;
import com.teamkn.model.database.UserDBHelper;

public class DataListAdapter extends TeamknBaseAdapter<DataList> {
    Activity activity ;
    public DataListAdapter(TeamknBaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public View inflate_view() {
        return inflate(R.layout.tkn_data_list_item, null);
    }

    @Override
    public BaseViewHolder build_view_holder(View view) {
    	
        ViewHolder view_holder      = new ViewHolder();
        view_holder.info_tv = (TextView) view.findViewById(R.id.info_tv);
        
        view_holder.linear_layout_avatar_box = (LinearLayout) view.findViewById(R.id.tknlist_avatar_box);
        view_holder.image_view_avatar          = (ImageView) view.findViewById(R.id.tknlist_author_avatar);
        view_holder.text_view_updated_time     = (TextView) view.findViewById(R.id.tknlist_updated_time);
    	
        view_holder.text_view_title       = (TextView) view.findViewById(R.id.tknlist_title);
        view_holder.text_view_author_name = (TextView) view.findViewById(R.id.tknlist_author);
    	
        view_holder.text_view_is_private         = (TextView) view.findViewById(R.id.tknlist_is_private);
        view_holder.text_view_has_been_forked    = (TextView) view.findViewById(R.id.tknlist_has_been_forked);
        view_holder.text_view_is_created_by_fork = (TextView) view.findViewById(R.id.tknlist_is_created_by_fork);
        view_holder.text_view_is_faved_by_me     = (TextView) view.findViewById(R.id.tknlist_is_faved_by_me);
        view_holder.text_view_kind               = (TextView) view.findViewById(R.id.tknlist_kind);
        
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final DataList item,
                               int position) {
    	
    	final ViewHolder view_holder = (ViewHolder) holder;
    	
        view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
        
        if("true".equals(item.is_removed)){
        	this._fill_with_data_on_removed(view_holder);
        	return;
        }
	    
	    view_holder.text_view_title.setText(item.title);
	    
	    _set_author_ui(item, view_holder);
	    _set_private_ui(item, view_holder);
	    _set_kind_ui(item, view_holder);
	    _set_been_fork_ui(item, view_holder);        
    }
    
    private class ViewHolder implements BaseViewHolder {
    	TextView info_tv;
    	
    	LinearLayout linear_layout_avatar_box;
    	ImageView image_view_avatar;
    	TextView text_view_updated_time;
    	
    	TextView text_view_title;
    	TextView text_view_author_name;
    	
    	TextView text_view_is_private;
    	TextView text_view_has_been_forked;
    	TextView text_view_is_created_by_fork;
    	TextView text_view_is_faved_by_me;
    	TextView text_view_kind;
    }
    
    private void _fill_with_data_on_removed(ViewHolder view_holder){
    	view_holder.image_view_avatar.setVisibility(View.GONE);
    	view_holder.text_view_updated_time.setVisibility(View.GONE);
    	
        view_holder.text_view_title.setText("列表已删除");
        view_holder.text_view_author_name.setText("未知");
    	
        view_holder.text_view_is_private.setVisibility(View.GONE);
        view_holder.text_view_has_been_forked.setVisibility(View.GONE);
        view_holder.text_view_is_created_by_fork.setVisibility(View.GONE);
        view_holder.text_view_is_faved_by_me.setVisibility(View.GONE);
    }
    
    // 判断是否应该显示头像
    private boolean _without_avatar(DataList item){
    	boolean c1 = item.public_boolean.equals(MainActivity.RequestCode.我的列表);
        boolean c2 = item.user_id == UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id;
        boolean c3 = MainActivity.RequestCode.data_list_public.equals(MainActivity.RequestCode.我的列表);
        boolean c4 = MainActivity.RequestCode.data_list_public.equals(MainActivity.RequestCode.协作列表);
        boolean c5 = MainActivity.RequestCode.data_list_public.equals(MainActivity.RequestCode.被协作列表);
        
        return (c1 || c2) && c3 || c4 || c5;
    }

    private void _set_private_ui(DataList item, ViewHolder view_holder){
    	boolean public_flag = item.public_boolean.equals("true");
    	view_holder.text_view_is_private.setVisibility( public_flag ? View.GONE : View.VISIBLE );
    }
    
    private void _set_kind_ui(DataList item, ViewHolder view_holder){
        String kind = item.kind;
    	
    	if(MainActivity.RequestCode.STEP.equals(kind)){
        	view_holder.text_view_kind.setText(R.string.tknlist_kind_step);
        }
        
        if(MainActivity.RequestCode.COLLECTION.equals(kind)){
        	view_holder.text_view_kind.setText(R.string.tknlist_kind_collection);
        }
    }
    
    private void _set_been_fork_ui(DataList item, ViewHolder view_holder){
		if(item.has_commits.equals("true")){
			view_holder.text_view_has_been_forked.setVisibility(View.VISIBLE);
			return;
		}
		
		view_holder.text_view_has_been_forked.setVisibility(View.GONE);
    }
    
    private void _set_author_ui(DataList item, ViewHolder view_holder){
    	
    	String friendly_time_string = BaseUtils.friendly_time_string(item.server_updated_time);
    	
    	// 不显示头像和作者名，用作者名来显示时间
	    if(_without_avatar(item)){
	    	view_holder.linear_layout_avatar_box.setVisibility(View.GONE);
	    	view_holder.text_view_author_name.setText(friendly_time_string);
	    	return;
	    }
	    
	    // 显示头像和作者名，用头像下方的text_view显示时间
    	view_holder.linear_layout_avatar_box.setVisibility(View.VISIBLE);
    	view_holder.text_view_updated_time.setText(friendly_time_string);
    	
    	// 以下相同
		final User user = item.get_user();
		
		// 作者名
		view_holder.text_view_author_name.setText(user.user_name);
		
		// 作者头像
    	if (null != user.user_avatar) {
    		Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(user.user_avatar));
        	Drawable drawable = new BitmapDrawable(bitmap);
    		view_holder.image_view_avatar.setBackgroundDrawable(drawable);
        }
    	
    	view_holder.image_view_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, UserMsgActivity.class);
				intent.putExtra("service_user_id", user.user_id);
				activity.startActivity(intent);	
			}
		});
    }
}
