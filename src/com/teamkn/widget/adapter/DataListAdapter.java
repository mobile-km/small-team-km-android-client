package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.User;
import com.teamkn.model.Watch;
import com.teamkn.model.database.UserDBHelper;
import com.teamkn.model.database.WatchDBHelper;

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
        view_holder.list_type_tv = (TextView)view.findViewById(R.id.list_type_tv);
        view_holder.list_collect_tv = (TextView)view.findViewById(R.id.list_collect_tv);
        //公共列表
        view_holder.show_is_yes_public_relativelayout = (RelativeLayout)view.findViewById(R.id.show_is_yes_public_relativelayout);
        view_holder.data_list_item_user_avatar_iv= (ImageView)view.findViewById(R.id.data_list_item_user_avatar_iv);
        view_holder.data_list_item_user_name_tv = (TextView)view.findViewById(R.id.data_list_item_user_name_tv);
        view_holder.list_title_tv_public = (TextView)view.findViewById(R.id.list_title_tv_public);
        view_holder.list_collect_tv_watch = (TextView)view.findViewById(R.id.list_collect_tv_watch);
        view_holder.list_type_tv_public = (TextView)view.findViewById(R.id.list_type_tv_public);
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final DataList item,
                               int position) {
    	
        final ViewHolder view_holder = (ViewHolder) holder;
        view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
        if((item.public_boolean.equals("false")
        		|| item.user_id == UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id)
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
            	view_holder.list_type_tv.setText("步骤");
            	view_holder.list_note_title_tv_go.setBackgroundColor(activity.getResources().getColor(R.color.burlywood));
            }else if(item.kind.equals(MainActivity.RequestCode.COLLECTION)){
            	view_holder.list_type_tv.setText("收集");
            	view_holder.list_note_title_tv_go.setBackgroundColor(activity.getResources().getColor(R.color.blueviolet));
            }
            
            Watch watch = WatchDBHelper.find(new Watch(-1,item.user_id , item.id));
    		if(item.public_boolean.equals("true") && watch.id>0){
    			view_holder.list_collect_tv.setVisibility(View.VISIBLE);	
    		}else{
    			view_holder.list_collect_tv.setVisibility(View.GONE);	
    		}
    		view_holder.list_collect_tv.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					view_holder.onClick(item);
					view_holder.list_collect_tv.setVisibility(View.GONE);
				}
			});
        }else{
        	if(item.kind.equals(MainActivity.RequestCode.STEP)){
            	view_holder.list_type_tv_public.setText("步骤");
            }else if(item.kind.equals(MainActivity.RequestCode.COLLECTION)){
            	view_holder.list_type_tv_public.setText("收集");
            }
        	view_holder.show_is_no_public_relativelayout.setVisibility(View.GONE);
        	view_holder.show_is_yes_public_relativelayout.setVisibility(View.VISIBLE);
        	
        	User user = UserDBHelper.find(item.user_id);
        	view_holder.data_list_item_user_name_tv.setText(user.user_name);
        	if (user.user_avatar!=null) {
        		Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(user.user_avatar));
            	Drawable drawable = new BitmapDrawable(bitmap);
        		view_holder.data_list_item_user_avatar_iv.setBackgroundDrawable(drawable);
            } else {
            	view_holder.data_list_item_user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
            }
            view_holder.list_title_tv_public.setText(item.title);
            
            Watch watch = WatchDBHelper.find(new Watch(-1,item.user_id , item.id));
    		System.out.println("watch.id = " + watch.id);
    		if(watch.id<=0){
    			view_holder.list_collect_tv_watch.setVisibility(View.GONE);	
    		}else{
    			view_holder.list_collect_tv_watch.setVisibility(View.VISIBLE);	
    		}
            view_holder.list_collect_tv_watch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					view_holder.onClick(item);
					view_holder.list_collect_tv_watch.setVisibility(View.GONE);
				}
			});
        }
    }
    
    private class ViewHolder implements BaseViewHolder {
    	
    	TextView info_tv;
    	
    	// 个人列表子项显示
    	RelativeLayout show_is_no_public_relativelayout;
        TextView list_note_title_tv_edit;   
        TextView list_note_title_tv_go;
        TextView list_data_list_eye_tv;
        TextView list_type_tv;
        TextView list_collect_tv;
        
        
        // 公共列表子项显示
        RelativeLayout show_is_yes_public_relativelayout;
        ImageView data_list_item_user_avatar_iv;
        TextView data_list_item_user_name_tv;
        TextView list_title_tv_public;
        TextView list_collect_tv_watch;
        TextView list_type_tv_public;
        
        public void onClick(final DataList item) {
			new TeamknAsyncTask<Void, Void, Void>() {
				@Override
				public Void do_in_background(Void... params)
						throws Exception {
					if (BaseUtils.is_wifi_active(activity)) {
						Watch watch = new Watch(-1,item.user_id , item.id);
						WatchDBHelper.createOrUpdate(watch);
						HttpApi.WatchList.watch(item, false);
					}else{
						BaseUtils.toast("无法连接到网络，请检查网络配置");
					}
					return null;
				}
				@Override
				public void on_success(Void result) {
					BaseUtils.toast("移除成功 ^_^");
				}	
			}.execute();
		}
    }
}
