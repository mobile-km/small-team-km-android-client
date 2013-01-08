package com.teamkn.activity.dataitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.dataitem.pull.DataItemPullListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.User;
import com.teamkn.model.database.UserDBHelper;

public class DataItemListShow  extends TeamknBaseActivity {
	static class RequestCode {
	    final static int CREATE_DATA_ITEM = 0; 
	    final static int BACK = 9;
	    public static final String 我的列表 = "false";
		public static final String 公开的列表 = "true";
		public static final String 我的首页 = "follow";
		public static final String 协作列表 = "fork";
		public static final String 我的书签 = "watch";
		public static final String 个人的公开的列表 = "user_public_data_list";
	}
	TextView data_list_user_name_tv;
	ImageView data_item_add_iv;
	/*
	 * data_list title edit
	 * */
	RelativeLayout data_list_title_rl;
	TextView data_list_title_tv;
	ImageView data_list_image_iv_edit;
	ImageView data_list_image_iv_watch;
	
	TextView data_item_original_user_name;//原始的作者名
	ImageView data_item_push_iv;
	
	//获取数据，用来传递
	String data_list_public;
	String data_list_type;
	DataList dataList ;
	
	boolean is_curretn_user_data_list; // 是否是当前用户的data_list
	
	/// api  读数据
	Map<Object, Object> map;
	boolean is_reading ;
	List<DataItem> data_items;
	User user;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_list);
		//获取intent中的传递的值
		Intent intent = getIntent();
		data_list_public = intent.getStringExtra("data_list_public");//返回dataList的中公开，自己私有，协作列表中的一个
		dataList = (DataList)intent.getSerializableExtra("data_list");//返回dataList
		
		//判断是否是当前用户的列表 或者 是协作列表
		is_curretn_user_data_list  = UserDBHelper.find(dataList.user_id).user_id == current_user().user_id;	
		
		//加载ui元素以及数据
		load_UI();
		set_ui();
		load_data_item_list();
	}
	private void set_ui(){
		set_username_ui();
		set_show_now_user_and_add_but_and_edit_title();
	}
	//如果是当前用户 如果是则显示增加按钮和编辑title按钮 否则 不显示
	private void set_show_now_user_and_add_but_and_edit_title(){
		if(!is_curretn_user_data_list){
			data_list_image_iv_edit.setVisibility(View.GONE);
			data_item_add_iv.setVisibility(View.GONE);
			return ;
		}
		data_item_add_iv.setVisibility(View.VISIBLE);
		data_list_image_iv_edit.setVisibility(View.VISIBLE);
		//点击title触发背景颜色变化
		data_list_image_iv_edit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						data_list_title_rl.setBackgroundColor(getResources()
								.getColor(R.color.lightgray));
						break;
					}
				return false;
			}
		});
		//点击title触发事件
		data_list_image_iv_edit.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				data_list_title_edit();
				data_list_title_rl.setBackgroundColor(getResources().getColor(R.color.darkgrey));
			}
		});
	}
	//点击title触发事件
	private void data_list_title_edit(){
		AlertDialog.Builder builder = new Builder(DataItemListShow.this);
		builder.setTitle("请修改");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		View view = getLayoutInflater().inflate(R.layout.data_list_edit_title_dialog, (ViewGroup)findViewById(R.id.dialog));
		final EditText edit_title = (EditText) view.findViewById(R.id.data_list_title_et);
		final CheckBox edit_public = (CheckBox)view.findViewById(R.id.data_list_public_checkbox);
		edit_title.setText(dataList.title);
		
		if(dataList.public_boolean.equals("true")){
			edit_public.setChecked(true);
		}else{
			edit_public.setChecked(false);
		}
		builder.setView(view);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String add_data_list_et_str = edit_title.getText().toString();
				data_list_title_edit_click(add_data_list_et_str,edit_public.isChecked()+"");
			}
		});
		builder.show();
	}
	private void data_list_title_edit_click(final String add_data_list_et_str,final String edit_public){
		if(BaseUtils.is_str_blank(add_data_list_et_str)){
			return ;
		}
		if (!BaseUtils.is_wifi_active(this)) {
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
		}
		new TeamknAsyncTask<Void, Void, Void>(this,"正在处理") {
			@Override
			public Void do_in_background(Void... params)
					throws Exception {
				dataList.setTitle(add_data_list_et_str);
				dataList.setPublic_boolean(edit_public);	
				HttpApi.DataList.update(dataList);
				return null;
			}
			@Override
			public void on_success(Void result) {
				data_list_title_tv.setText(add_data_list_et_str);
			}
		}.execute();
	}
	//设置用户名
	private void set_username_ui(){
		String user_name_sub = UserDBHelper.find(dataList.user_id).user_name;
		if(user_name_sub!=null && user_name_sub.length()>7){
			user_name_sub = user_name_sub.substring(0, 7) + "..";
		}
		data_list_user_name_tv.setText(user_name_sub+"的列表");
	}
	private void load_UI(){
		//top的中间显示当前是谁的列表
		data_list_user_name_tv = (TextView)findViewById(R.id.data_list_user_name_tv); 
		//title
		data_list_title_rl=(RelativeLayout)findViewById(R.id.data_list_title_rl);//dataList的title文本RelativeLayout
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);//dataList的title文本TextView
		data_list_title_tv.setText(dataList.title);
		data_list_title_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
		data_list_title_tv.getPaint().setFakeBoldText(true);//加粗
		//加载创建按钮
		data_item_add_iv = (ImageView)findViewById(R.id.data_item_add_iv);
		//加载编辑title按钮
		data_list_image_iv_edit = (ImageView) findViewById(R.id.data_list_image_iv_edit);
		
		//在协作列表时 显示原有者的名字
		data_item_original_user_name = (TextView)findViewById(R.id.data_item_original_user_name);
		// 加载推送的按钮
		data_item_push_iv = (ImageView)findViewById(R.id.data_item_push_iv);
	}
	
	private void load_data_item_list(){
		data_items = new ArrayList<DataItem>();
		if (!BaseUtils.is_wifi_active(this)) {
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
			return;
		}
		new TeamknAsyncTask<Void, Void, List<DataItem>>(this,"内容加载中") {
			@SuppressWarnings("unchecked")
			@Override
			public List<DataItem> do_in_background(Void... params)throws Exception {
					map= HttpApi.DataItem.pull(dataList);
					is_reading = (Boolean) map.get("read");
					data_items = (List<DataItem>) map.get("dataItems");
					dataList = (DataList) map.get("data_list");
				return data_items;
			}
			@Override
			public void on_success(final List<DataItem> dataItems) {
				//加载收藏的按钮的显示以及触发
				load_watch_UI();
				load_push_UI();

//				//判断列表是否有数据
//				if(dataItems.size()==0){
//					tlv.setVisibility(View.GONE);
//					data_item_step_rl.setVisibility(View.GONE);
//					data_item_list_approach_button.setVisibility(View.GONE);
//					list_no_data_show.setVisibility(View.VISIBLE);
//				}else{
//					//判断是否是 要显示 步骤列表
//					show_step = (	data_list_public.equals(MainActivity.RequestCode.公开的列表) 
//									|| data_list_public.equals(MainActivity.RequestCode.我的书签) 
//									|| UserDBHelper.find(dataList.user_id).user_id == current_user().user_id 
//								)
//								&& dataList.kind.equals(MainActivity.RequestCode.STEP)
//								&& !is_reading ;
//					
//					load_step_or_list(show_step);
//					list_no_data_show.setVisibility(View.GONE);
//					//判断是以那种列表展示形式 列出数据a
//					if(show_step){
//						if(is_reading || UserDBHelper.find(dataList.user_id).user_id == current_user().user_id){
//							data_item_list_approach_button.setVisibility(View.VISIBLE);
//						}else{
//							data_item_list_approach_button.setVisibility(View.GONE);
//						}
//						data_item_list_approach_button.setText("以清单模式查看");
//						load_step();
//					}else{
//						data_item_list_approach_button.setVisibility(View.VISIBLE);
//						data_item_list_approach_button.setText("以向导模式查看");
//						load_list();
//					}
//				}
				
			}
		}.execute();
	}
	//加载收藏的按钮的显示以及触发
	private void load_watch_UI(){
		data_list_image_iv_watch = (ImageView)findViewById(R.id.data_list_image_iv_watch);
		
		//判断当前列表是否被收藏以及界面图片显示
		if(dataList.watched){
			data_list_image_iv_watch.setBackgroundDrawable(getResources().getDrawable(R.drawable.star_blue));
		}else{	
			data_list_image_iv_watch.setBackgroundDrawable(getResources().getDrawable(R.drawable.star_gray));
		}
		data_list_image_iv_watch.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				watch_data_list();
			}
		});
	}
	//添加收藏 或者 移除收藏
	private void watch_data_list(){
		if (!BaseUtils.is_wifi_active(this)) {
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
			return;
		}
		
		String msg = "";
		if(dataList.watched){
			msg = "正移除书签";
		}else{
			msg = "正添加书签";
		}
		new TeamknAsyncTask<Void, Void, Void>(this,msg) {
			@Override
			public Void do_in_background(Void... params) throws Exception {
				HttpApi.WatchList.watch(dataList, !dataList.watched);
				return null;
			}
			@Override
			public void on_success(Void result) {
				//请求成功后，图片变化显示
				if(!dataList.watched){
					data_list_image_iv_watch.setBackgroundDrawable(getResources().getDrawable(R.drawable.star_blue));
					BaseUtils.toast(getResources().getString(R.string.success_add_watch));
				}else{	
					data_list_image_iv_watch.setBackgroundDrawable(getResources().getDrawable(R.drawable.star_gray));
					BaseUtils.toast(getResources().getString(R.string.success_remove_watch));
				}
				dataList.setWatched(!dataList.watched);
			}	
		}.execute();
	}
	// 加载推送的按钮
	private void load_push_UI(){
		data_item_push_iv.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//if是 当前用户，并且被推送了   显示处理推送按钮的处理事件
				if(is_curretn_user_data_list  && dataList.has_commits.equals("true")){
					Intent intent = new Intent(DataItemListShow.this,DataItemPullListActivity.class);
					intent.putExtra("data_list", dataList);
					startActivityForResult(intent, RequestCode.BACK);
				}else{
					//推送操作
					fork_data_list();
				}				
			}
		});
		
		User user = UserDBHelper.find(dataList.user_id);
		//显示我迁出的图标  亮色的叉号
		boolean 我迁出的 = (data_list_public.equals(MainActivity.RequestCode.公开的列表)
							|| data_list_public.equals(MainActivity.RequestCode.我的首页)
							||data_list_public.equals(MainActivity.RequestCode.我的书签) 
						  )
						  && dataList.forked==true && user.user_id!=current_user().user_id;
		//显示我还没有迁出的图标  灰色的叉号
		boolean 没有迁出的 = (	data_list_public.equals(MainActivity.RequestCode.公开的列表)
								|| data_list_public.equals(MainActivity.RequestCode.我的首页)
								||data_list_public.equals(MainActivity.RequestCode.我的书签) 
							)
							&& dataList.forked==false  
							&& user.user_id!=current_user().user_id;
		//公开的列表 我的首页 我的书签  中 我的列表  不显示
		boolean 我的列表 = ( data_list_public.equals(MainActivity.RequestCode.公开的列表)
							|| data_list_public.equals(MainActivity.RequestCode.我的首页)
							||data_list_public.equals(MainActivity.RequestCode.我的书签)
						  )
						  && user.user_id == current_user().user_id ;
		//我协作的列表 分为 原作者 已删除  是否  分 从 XXX  迁出  与  已被原作者删除 
		boolean 我协作的列表 = data_list_public.equals(MainActivity.RequestCode.协作列表) ;
		//  自己的列表中有 被迁出的 并且被修改了
		//  在我的列表，或 被协作列表   显示  一个 说话的状态图的 图标 
		boolean 被协作并被修改 =  (   data_list_public.equals(MainActivity.RequestCode.我的列表)
									|| data_list_public.equals(MainActivity.RequestCode.被协作列表) 
								) 
								&& dataList.has_commits .equals("true") ;
		
		
		// 公开的列表 我的首页 我的书签  别人的列表中 我已经迁出的  
		if(我迁出的){
			data_item_push_iv.setVisibility(View.VISIBLE);
			data_item_push_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.hell_pencil));
			data_item_push_iv.setClickable(false);
			data_item_push_iv.setFocusable(false);
			
			// 公开的列表 我的首页 我的书签  别人的列表中 没有迁出的
		}else if(没有迁出的){
			data_item_push_iv.setVisibility(View.VISIBLE);
			data_item_push_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_pencil));
			
			// 公开的列表 我的首页 我的书签  中 我的列表
		}else if(我的列表){
			data_item_push_iv.setVisibility(View.GONE);
			
			//我协作的列表 分为 原作者 已删除  是否
		}else if(我协作的列表){
			if(map.get("user")!=null){
				//判断是否 是 协作列表 显示原始用户名
				data_item_push_iv.setVisibility(View.GONE);
				data_item_original_user_name.setVisibility(View.VISIBLE);
				
				if(dataList.forked_from_is_removed.equals("true")){
					data_item_original_user_name.setText(getResources().getString(R.string.is_no_data));
				}else{
					User map_user = (User) map.get("user");
					data_item_original_user_name.setText(Html.fromHtml("从<font  color=blue>"
					+map_user.user_name+"</font>的列表迁出"));
				}
			}else{
				data_item_original_user_name.setVisibility(View.VISIBLE);
				data_item_original_user_name.setText(getResources().getString(R.string.is_no_data));
			}
			
			//  自己的列表中有 被迁出的 并且被修改了
			//  在我的列表，或 被协作列表
		}else if(被协作并被修改){
			data_item_push_iv.setVisibility(View.VISIBLE);
			data_item_push_iv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.sym_action_chat));
			
			//	其他的情况不显示
		}else{
			data_item_push_iv.setClickable(false);
			data_item_push_iv.setFocusable(false);
		}
	}
	
	//推送操作
	private void fork_data_list(){
		if(!BaseUtils.is_wifi_active(this)){
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
		}
		new TeamknAsyncTask<Void, Void, DataList>(this,"正在加载") {
			@Override
			public DataList do_in_background(Void... params) throws Exception {
				
				DataList fork_dataList = HttpApi.DataList.fork(dataList);
				return fork_dataList;
			}
			@Override
			public void on_success(DataList result) {
				if(result!=null){
					data_item_push_iv.setVisibility(View.VISIBLE);
					data_item_push_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.hell_pencil));
					data_item_push_iv.setClickable(false);
					data_item_push_iv.setFocusable(false);
					
					MainActivity.RequestCode.data_list_public = RequestCode.协作列表;
					data_list_public = MainActivity.RequestCode.data_list_public;//返回dataList的中公开，自己私有，协作列表中的一个
					dataList = result;
					
					//判断是否是当前用户的列表 或者 是协作列表
					if(UserDBHelper.find(dataList.user_id).user_id == current_user().user_id 
							|| data_list_public.equals("fork")
					){
						is_curretn_user_data_list  = true;
					}else{
						is_curretn_user_data_list  = false;
					}	
					
					//加载ui元素以及数据
					load_UI();
					load_data_item_list();	
				}
			}
		}.execute();
	}
}
