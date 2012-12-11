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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.social_circle.UserPublicDataListActivity;
import com.teamkn.activity.social_circle.UserPublicDataListActivity.RequestCode;
import com.teamkn.activity.usermsg.UserMsgActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
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
        view_holder.list_data_list_eye_tv = (TextView) view.findViewById(R.id.mi_list_data_list_eye_tv);
        view_holder.list_type_tv = (TextView)view.findViewById(R.id.list_type_tv);
        view_holder.data_list_forked_iv = (ImageView)view.findViewById(R.id.data_list_forked_iv);
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
//    	DataListReading dataListReading = DataListReadingDBHelper.find(new DataListReading(-1, item.id, UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id));
//    	boolean isReading = true;
//        if(dataListReading.id<=0){
//        	isReading = false;
//        } 
    	final ViewHolder view_holder = (ViewHolder) holder;
    	view_holder.list_collect_tv_watch.setVisibility(View.GONE);
    	view_holder.data_list_forked_iv.setVisibility(View.GONE);
    	
        view_holder.info_tv.setTag(R.id.tag_note_uuid, item);
        
        if(item.is_removed.equals("true")){
        	view_holder.show_is_no_public_relativelayout.setVisibility(View.VISIBLE);
        	view_holder.show_is_yes_public_relativelayout.setVisibility(View.GONE);
        	view_holder.list_note_title_tv_edit.setText(activity.getResources().getString(R.string.is_no_data));	
        }else{

            if(   (  (item.public_boolean.equals(MainActivity.RequestCode.我的列表)
            		|| item.user_id == UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id)
            		&& MainActivity.RequestCode.data_list_public.equals(MainActivity.RequestCode.我的列表)
            		|| MainActivity.RequestCode.data_list_public.equals(MainActivity.RequestCode.协作列表)
            		|| MainActivity.RequestCode.data_list_public.equals(MainActivity.RequestCode.被协作列表)
            	  )
            ){
            	
            	view_holder.show_is_no_public_relativelayout.setVisibility(View.VISIBLE);
            	view_holder.show_is_yes_public_relativelayout.setVisibility(View.GONE);
            	
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
            }else{
            	
            	if(item.kind.equals(MainActivity.RequestCode.STEP)){
                	view_holder.list_type_tv_public.setText("步骤");
                }else if(item.kind.equals(MainActivity.RequestCode.COLLECTION)){
                	view_holder.list_type_tv_public.setText("收集");
                }
            	view_holder.show_is_no_public_relativelayout.setVisibility(View.GONE);
            	view_holder.show_is_yes_public_relativelayout.setVisibility(View.VISIBLE);
            	
        		final User user = UserDBHelper.find(item.user_id);
        		view_holder.data_list_item_user_name_tv.setText(user.user_name);
            	if (user.user_avatar!=null) {
            		Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(user.user_avatar));
                	Drawable drawable = new BitmapDrawable(bitmap);
            		view_holder.data_list_item_user_avatar_iv.setBackgroundDrawable(drawable);
                } else {
                	view_holder.data_list_item_user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
                }
            	view_holder.data_list_item_user_avatar_iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(activity,UserMsgActivity.class);
						intent.putExtra("service_user_id", user.user_id);
						activity.startActivity(intent);
					}
				});

            	String title = item.title;
                if(title.length()>9){
                	title = title.substring(0, 9) + "..";
                }
                view_holder.list_title_tv_public.setText(title);
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
        ImageView data_list_forked_iv;
        // 公共列表子项显示
        RelativeLayout show_is_yes_public_relativelayout;
        ImageView data_list_item_user_avatar_iv;
        TextView data_list_item_user_name_tv;
        TextView list_title_tv_public;
        ImageView list_collect_tv_watch;
        TextView list_type_tv_public;
        
//        public void onClick(final DataList item,final boolean go_watch) {
//        	final Watch watch = new Watch(-1,UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id).id , item.id);
//			final Watch fand_watch = WatchDBHelper.find(watch);
//			if (BaseUtils.is_wifi_active(activity)) {
//				new TeamknAsyncTask<Void, Void, Boolean>() {
//					@Override
//					public Boolean do_in_background(Void... params)
//							throws Exception {
//						boolean rsult=false;
//						if(fand_watch.id<=0){
//							rsult = true;
//							WatchDBHelper.createOrUpdate(watch);
//							HttpApi.WatchList.watch(item, rsult);
//						}else{
//							rsult = false;
//							WatchDBHelper.delete(watch);
//							HttpApi.WatchList.watch(item, rsult);
//						}
//						return rsult;
//					}
//					@Override
//					public void on_success(Boolean result) {
//						list_collect_tv_watch.setVisibility(View.VISIBLE);
//						data_list_forked_iv.setVisibility(View.VISIBLE);
//						if(go_watch && !MainActivity.RequestCode.data_list_public.equals("watch")){
//							data_list_forked_iv.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_yes));
//							list_collect_tv_watch.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_yes));
//						}else if(!MainActivity.RequestCode.data_list_public.equals("watch")){
//							list_collect_tv_watch.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_no));
//							data_list_forked_iv.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mi_collect_no));
//						}
//						String msg;
//						if(result==true){
//							msg = "添加成功 ^_^";
//						}else{
//							msg = "移除成功 ^_^";
//						}
//						BaseUtils.toast(msg);
//					}	
//				}.execute();
//			}else{
//				BaseUtils.toast("无法连接到网络，请检查网络配置");
//			}
//		}
    }
}
