package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.base.MainActivity.RequestCode;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.DataListReading;
import com.teamkn.model.User;
import com.teamkn.model.Watch;
import com.teamkn.model.database.DataListReadingDBHelper;
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
        view_holder.list_collect_tv = (ImageView)view.findViewById(R.id.list_collect_tv);
        //公共列表
        view_holder.show_is_yes_public_relativelayout = (RelativeLayout)view.findViewById(R.id.show_is_yes_public_relativelayout);
        view_holder.data_list_item_user_avatar_iv= (ImageView)view.findViewById(R.id.data_list_item_user_avatar_iv);
        view_holder.data_list_item_user_name_tv = (TextView)view.findViewById(R.id.data_list_item_user_name_tv);
        view_holder.list_title_tv_public = (TextView)view.findViewById(R.id.list_title_tv_public);
        view_holder.list_collect_tv_watch = (ImageView)view.findViewById(R.id.list_collect_tv_watch);
        view_holder.list_type_tv_public = (TextView)view.findViewById(R.id.list_type_tv_public);
        return view_holder;
    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final DataList item,
                               int position) {
//    	DataListReading dataListReading = DataListReadingDBHelper.find(new DataListReading(-1, item.id, item.user_id));
    	DataListReading dataListReading = DataListReadingDBHelper.find(new DataListReading(-1, item.id, UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id));
    	boolean isReading = true;
        if(dataListReading.id<=0){
        	isReading = false;
        } 
    	final ViewHolder view_holder = (ViewHolder) holder;
    	view_holder.list_collect_tv_watch.setVisibility(View.GONE);
    	view_holder.list_collect_tv.setVisibility(View.GONE);
    	
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
            
            final Watch watch = WatchDBHelper.find(new Watch(-1,item.user_id , item.id));
    		if(item.public_boolean.equals("true") && watch.id>0){
    			view_holder.list_collect_tv.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_yes));
    		}else{
    			view_holder.list_collect_tv.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_no));
    		}
    		view_holder.list_collect_tv.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					boolean go_watch ;
					Watch watch = WatchDBHelper.find(new Watch(-1,item.user_id , item.id));
					if(item.public_boolean.equals("true") && watch.id>0){
						go_watch = false;
		    		}else{
		    			go_watch = true;
		    		}
					view_holder.onClick(item,go_watch);
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
            
            final Watch watch = WatchDBHelper.find(new Watch(-1,UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id , item.id));
    		System.out.println("watch.id = " + watch.toString() + " ｉｔｅｍ　：　" + item.toString());
    		if(watch.id<=0){
    			view_holder.list_collect_tv_watch.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_no));
    		}else{
    			view_holder.list_collect_tv_watch.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_yes));
    		}
            view_holder.list_collect_tv_watch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					boolean go_watch ;
					Watch watch = WatchDBHelper.find(new Watch(-1,UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id , item.id));
					
					if(MainActivity.RequestCode.data_list_public.equals("watch")){
						MainActivity.dataListAdapter.remove_item(item);
						MainActivity.dataListAdapter.notifyDataSetChanged();
					}
					if(watch.id<=0){
						go_watch = true;	
		    		}else{
		    			go_watch = false;
		    		}
					view_holder.onClick(item,go_watch);
				}
			});
            if(!isReading){
            	view_holder.show_is_yes_public_relativelayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
            	TextPaint tp = view_holder.list_title_tv_public.getPaint(); 
            	tp.setFakeBoldText(true);
            }else{
            	view_holder.show_is_yes_public_relativelayout.setBackgroundColor(activity.getResources().getColor(R.color.gainsboro));
            	TextPaint tp = view_holder.list_title_tv_public.getPaint(); 
            	tp.setFakeBoldText(false);
            }
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
        ImageView list_collect_tv;
        // 公共列表子项显示
        RelativeLayout show_is_yes_public_relativelayout;
        ImageView data_list_item_user_avatar_iv;
        TextView data_list_item_user_name_tv;
        TextView list_title_tv_public;
        ImageView list_collect_tv_watch;
        TextView list_type_tv_public;
        
        public void onClick(final DataList item,final boolean go_watch) {
        	final Watch watch = new Watch(-1,UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id , item.id);
			final Watch fand_watch = WatchDBHelper.find(watch);
			if (BaseUtils.is_wifi_active(activity)) {
				new TeamknAsyncTask<Void, Void, Boolean>() {
					@Override
					public Boolean do_in_background(Void... params)
							throws Exception {
						boolean rsult=false;
						if(fand_watch.id<=0){
							rsult = true;
							WatchDBHelper.createOrUpdate(watch);
							HttpApi.WatchList.watch(item, rsult);
						}else{
							rsult = false;
							WatchDBHelper.delete(watch);
							HttpApi.WatchList.watch(item, rsult);
						}
						return rsult;
					}
					@Override
					public void on_success(Boolean result) {
						list_collect_tv_watch.setVisibility(View.VISIBLE);
						list_collect_tv.setVisibility(View.VISIBLE);
						if(go_watch && !MainActivity.RequestCode.data_list_public.equals("watch")){
			    			list_collect_tv.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_yes));
							list_collect_tv_watch.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_yes));
						}else if(!MainActivity.RequestCode.data_list_public.equals("watch")){
							list_collect_tv_watch.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_no));
							list_collect_tv.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_no));
						}
						String msg;
						if(result==true){
							msg = "添加成功 ^_^";
						}else{
							msg = "移除成功 ^_^";
						}
						BaseUtils.toast(msg);
					}	
				}.execute();
			}else{
				BaseUtils.toast("无法连接到网络，请检查网络配置");
			}
		}
    }
}
