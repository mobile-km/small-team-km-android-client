package com.teamkn.activity.dataitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.dataitem.pull.DataItemPullListActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.ShowHelp;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.User;
import com.teamkn.model.database.UserDBHelper;
import com.teamkn.widget.adapter.DataItemListAdapter;
public class DataItemListActivity extends TeamknBaseActivity {
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
    /*
     * data_item  list 列表 
     * */
	ListViewInterceptor tlv;
	DataItemListAdapter dataItemListAdapter;
    /*
     * data_item step 列表
     * */
	RelativeLayout data_item_step_rl;
	TextView data_item_step_tv;
	TextView data_item_step_text_tv;
	TextView data_item_step_content_text_tv;
	Button data_item_list_approach_button;
	Button data_item_next_button;
	Button data_item_back_button;
	
//	Button data_item_list_guide_button;
	TextView data_item_original_user_name;//原始的作者名
	
	ImageView data_item_push_iv;
	LinearLayout list_no_data_show ;  // 没有数据时显示
	
	DataList dataList ;
	
	//获取数据，用来传递
	String data_list_public;
	String data_list_type;
	//是否显示show_step
	boolean show_step;
	/// api  读数据
	Map<Object, Object> map;
	boolean is_reading ;
	List<DataItem> dataItems;
	User user;
	
    int step_new = 0;  //显示step的步骤，默认是0
    
    boolean is_curretn_user_data_list; // 是否是当前用户的data_list
    String public_boolean = "true";//修改中用到

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
		load_data_item_list();
	}
	private void load_UI(){
		//top的中间显示当前是谁的列表
		data_list_user_name_tv = (TextView)findViewById(R.id.data_list_user_name_tv); 
		String user_name_sub = UserDBHelper.find(dataList.user_id).user_name;
		if(user_name_sub!=null && user_name_sub.length()>7){
			user_name_sub = user_name_sub.substring(0, 7) + "..";
		}
		data_list_user_name_tv.setText(user_name_sub+"的列表");

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
		//如果是当前用户 如果是则显示增加按钮和编辑title按钮 否则 不显示
		if(is_curretn_user_data_list){
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
			
		}else{
			data_list_image_iv_edit.setVisibility(View.GONE);
			data_item_add_iv.setVisibility(View.GONE);
		}
		// 列表没有数据的LinearLayout
		list_no_data_show = (LinearLayout)findViewById(R.id.list_no_data_show);
		// 列表显示的方式 分为 步骤 和 列表 两种方法显示
		// data_item list 列表
		tlv = (ListViewInterceptor) findViewById(R.id.list);
		// data_item  step 列表
		data_item_step_rl = (RelativeLayout)findViewById(R.id.data_item_step_rl);
		data_item_step_tv = (TextView)findViewById(R.id.data_item_step_tv);//步骤显示的步骤text
		data_item_step_text_tv = (TextView)findViewById(R.id.data_item_step_text_tv);//步骤显示的步骤title
		data_item_step_content_text_tv = (TextView)findViewById(R.id.data_item_step_content_text_tv);//步骤显示的步骤内容
		//步骤显示的以清单模式查看
		data_item_list_approach_button = (Button)findViewById(R.id.data_item_list_approach_button);
		data_item_next_button = (Button)findViewById(R.id.data_item_next_button); //下一步按钮
		data_item_back_button = (Button)findViewById(R.id.data_item_back_button);//上一步按钮
		//在协作列表时 显示原有者的名字
		data_item_original_user_name = (TextView)findViewById(R.id.data_item_original_user_name);
		// 加载推送的按钮
		data_item_push_iv = (ImageView)findViewById(R.id.data_item_push_iv);
		data_item_push_iv.setVisibility(View.GONE);
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
			String msg = "";
			if(dataList.watched){
				msg = "正移除书签";
			}else{
				msg = "正添加书签";
			}
			new TeamknAsyncTask<Void, Void, Void>(DataItemListActivity.this,msg) {
				@Override
				public Void do_in_background(Void... params) throws Exception {
					if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {
						HttpApi.WatchList.watch(dataList, !dataList.watched);
					}else{
						BaseUtils.toast("无法连接到网络，请检查网络配置");
					}
					return null;
				}
				@Override
				public void on_success(Void result) {
					//请求成功后，图片变化显示
					if(!dataList.watched){
						data_list_image_iv_watch.setBackgroundDrawable(getResources().getDrawable(R.drawable.star_blue));
						BaseUtils.toast("成功加入书签 ^_^");
					}else{	
						data_list_image_iv_watch.setBackgroundDrawable(getResources().getDrawable(R.drawable.star_gray));
						BaseUtils.toast("移除书签成功 ^_^");
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
					Intent intent = new Intent(DataItemListActivity.this,DataItemPullListActivity.class);
					intent.putExtra("data_list", dataList);
					startActivityForResult(intent, RequestCode.BACK);
				}else{
					//推送操作
					fork_data_list();
				}				
			}
		});
		
		System.out.println("dataList.toString() " + dataList.toString());
		System.out.println("current_user.toString() " + current_user().user_id);
		User user = UserDBHelper.find(dataList.user_id);
		
		// 公开的列表 我的首页 我的书签  别人的列表中 我已经迁出的  
		if((data_list_public.equals(MainActivity.RequestCode.公开的列表)
				|| data_list_public.equals(MainActivity.RequestCode.我的首页)
				||data_list_public.equals(MainActivity.RequestCode.我的书签))
				&& dataList.forked==true && user.user_id!=current_user().user_id){
			data_item_push_iv.setVisibility(View.VISIBLE);
			data_item_push_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.hell_pencil));
			data_item_push_iv.setClickable(false);
			data_item_push_iv.setFocusable(false);
			
			// 公开的列表 我的首页 我的书签  别人的列表中 没有迁出的
		}else if((data_list_public.equals(MainActivity.RequestCode.公开的列表)
				|| data_list_public.equals(MainActivity.RequestCode.我的首页)
				||data_list_public.equals(MainActivity.RequestCode.我的书签))
				&& dataList.forked==false  && user.user_id!=current_user().user_id){
			data_item_push_iv.setVisibility(View.VISIBLE);
			data_item_push_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_pencil));
			
			// 公开的列表 我的首页 我的书签  中 我的列表
		}else if((data_list_public.equals(MainActivity.RequestCode.公开的列表)
				|| data_list_public.equals(MainActivity.RequestCode.我的首页)
				||data_list_public.equals(MainActivity.RequestCode.我的书签))
				&& user.user_id == current_user().user_id){
			data_item_push_iv.setVisibility(View.GONE);
			
			//我协作的列表 分为 原作者 已删除  是否
		}else if(data_list_public.equals(MainActivity.RequestCode.协作列表)){
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
		}else if((data_list_public.equals(MainActivity.RequestCode.我的列表)
				|| data_list_public.equals(MainActivity.RequestCode.被协作列表) 
				) && dataList.has_commits .equals("true") ){
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
		if(BaseUtils.is_wifi_active(DataItemListActivity.this)){
			new TeamknAsyncTask<Void, Void, DataList>(DataItemListActivity.this,"正在加载") {
				@Override
				public DataList do_in_background(Void... params) throws Exception {
					
					DataList fork_dataList = HttpApi.DataList.fork(dataList);
//					HttpApi.DataItem.pull(fork_dataList);
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
		}else{
			BaseUtils.toast("无法连接网络");
		}
	}
	//点击title触发事件
	private void data_list_title_edit(){
		AlertDialog.Builder builder = new Builder(DataItemListActivity.this);
		builder.setTitle("请修改");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		View view = getLayoutInflater().inflate(R.layout.data_list_edit_title_dialog, (ViewGroup)findViewById(R.id.dialog));
		final EditText edit_title = (EditText) view.findViewById(R.id.data_list_title_et);
		final CheckBox edit_public = (CheckBox)view.findViewById(R.id.data_list_public_checkbox);
		edit_title.setText(dataList.title);
		
		if(dataList.public_boolean.equals("true")){
			edit_public.setChecked(true);
			public_boolean = "true";
		}else{
			edit_public.setChecked(false);
			public_boolean = "false";
		}
		builder.setView(view);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String add_data_list_et_str = edit_title.getText().toString();
				if(edit_public.isChecked()){
					public_boolean = "true";
				}else{
					public_boolean = "false";
				}
				if (add_data_list_et_str != null&& !add_data_list_et_str.equals(null)
						&& !BaseUtils.is_str_blank(add_data_list_et_str)) {
						if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {
							new TeamknAsyncTask<Void, Void, Void>(DataItemListActivity.this,"正在处理") {
								@Override
								public Void do_in_background(Void... params)
										throws Exception {
									dataList.setTitle(add_data_list_et_str);
									dataList.setPublic_boolean(public_boolean);	
									try {
										HttpApi.DataList.update(dataList);
									} catch (Exception e) {
										e.printStackTrace();
									}
									return null;
								}
								@Override
								public void on_success(Void result) {
									data_list_title_tv.setText(add_data_list_et_str);
								}
							}.execute();
						}else{
							BaseUtils.toast("无法连接网络");
						}
				}
			}
		});
		builder.show();
	}
	//隐藏的是步骤还是列表
	private void load_step_or_list(boolean show_step){
		if(show_step){
			data_item_step_rl.setVisibility(View.VISIBLE);
			tlv.setVisibility(View.GONE);
		}else{
			data_item_step_rl.setVisibility(View.GONE);
			tlv.setVisibility(View.VISIBLE);
		}
	}
	private void load_data_item_list(){
		dataItems = new ArrayList<DataItem>();
		new TeamknAsyncTask<Void, Void, List<DataItem>>(DataItemListActivity.this,"内容加载中") {
			@SuppressWarnings("unchecked")
			@Override
			public List<DataItem> do_in_background(Void... params)
					throws Exception {
					map= HttpApi.DataItem.pull(dataList);
					is_reading = (Boolean) map.get("read");
					dataItems = (List<DataItem>) map.get("dataItems");
					dataList = (DataList) map.get("data_list");
				return dataItems;
			}
			@Override
			public void on_success(final List<DataItem> dataItems) {
				//加载收藏的按钮的显示以及触发
				load_watch_UI();
				load_push_UI();
				
				//判断是否是 要显示 步骤列表
				if((data_list_public.equals(MainActivity.RequestCode.公开的列表) 
						|| data_list_public.equals(MainActivity.RequestCode.我的书签) 
						|| UserDBHelper.find(dataList.user_id).user_id == current_user().user_id 
					)&& dataList.kind.equals(MainActivity.RequestCode.STEP)
					 && !is_reading){
					show_step = true;
				}else{
					show_step = false;
				}

				//判断列表是否有数据
				if(dataItems.size()==0){
					tlv.setVisibility(View.GONE);
					data_item_step_rl.setVisibility(View.GONE);
					data_item_list_approach_button.setVisibility(View.GONE);
					list_no_data_show.setVisibility(View.VISIBLE);
				}else{
					load_step_or_list(show_step);
					list_no_data_show.setVisibility(View.GONE);
					//判断是以那种列表展示形式 列出数据a
					if(show_step){
						if(is_reading || UserDBHelper.find(dataList.user_id).user_id == current_user().user_id){
							data_item_list_approach_button.setVisibility(View.VISIBLE);
						}else{
							data_item_list_approach_button.setVisibility(View.GONE);
						}
						data_item_list_approach_button.setText("以清单模式查看");
						load_step();
						
//						ShowHelp.showHelp(ShowHelp.Params.SHOW_NEXT_HELP,this, data_item_next_button, str);
					}else{
						load_list();
						if((data_list_public.equals(MainActivity.RequestCode.公开的列表)
								|| data_list_public.equals(MainActivity.RequestCode.我的书签) 
								|| UserDBHelper.find(dataList.user_id).user_id == current_user().user_id) 
								&& dataList.kind.equals(MainActivity.RequestCode.STEP)){
							data_item_list_approach_button.setVisibility(View.VISIBLE);
							data_item_list_approach_button.setText("以向导模式查看");
						}else{	
							data_item_list_approach_button.setVisibility(View.GONE);
						}
					}
				}
				
			}
		}.execute();
	}
	
	private void load_step(){
		data_item_list_approach_button.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					data_item_list_approach_button.setVisibility(View.GONE);
					data_item_list_approach_button.setText("以向导模式查看");
					show_step = false;
					load_step_or_list(show_step);
					load_list();
				}
		});
		set_step_ui();
		data_item_next_button.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				step_new++;
				set_step_ui();
			}
		});
		
		data_item_back_button.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				step_new--;
				set_step_ui();
			}
		});
		data_item_next_button.setVisibility(View.VISIBLE);
		//帮助显示
		if(MainActivity.RequestCode.SHOW_HELP == MainActivity.RequestCode.SHOW_STEP_HELP){
			ShowHelp.showHelp(ShowHelp.Params.SHOW_NEXT_HELP,this, data_item_next_button, getResources().getString(R.string.show_next_help));
		}
	}
	public void set_step_ui(){
		if(step_new<=dataItems.size()-1){
			data_item_step_tv.setVisibility(View.VISIBLE);
			if(step_new<=0){
				data_item_back_button.setVisibility(View.GONE);
			}else{
				data_item_back_button.setVisibility(View.VISIBLE);
			}
			if(step_new==dataItems.size()-1){
				data_item_next_button.setText("结束");
			}
			data_item_step_tv.setText((1+step_new)+"");
			data_item_step_text_tv.setText(dataItems.get(step_new).title);
			data_item_step_content_text_tv.setText(dataItems.get(step_new).content);
		}else{
			show_step = false;
			load_step_or_list(show_step);
			load_list();
			step_new = 0;
		}
	}
	private void load_list() {
		if((data_list_public.equals("true")
				|| data_list_public.equals("watch") || UserDBHelper.find(dataList.user_id).user_id == current_user().user_id) 
				&& dataList.kind.equals(MainActivity.RequestCode.STEP)
				&& dataItems.size()>0){
			data_item_list_approach_button.setVisibility(View.VISIBLE);
			data_item_list_approach_button.setText("以向导模式查看");
			data_item_list_approach_button.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					data_item_list_approach_button.setText("以清单模式查看");
					data_item_next_button.setText("下一步");
					if(step_new==dataItems.size()-1){
						data_item_next_button.setText("结束");
					}
					show_step = true;
					load_step_or_list(show_step);
					load_step();
				}
			});
		}else{	
			data_item_list_approach_button.setVisibility(View.GONE);
		}
		dataItemListAdapter = new DataItemListAdapter(
				DataItemListActivity.this,
				R.layout.list_data_item_list_item, dataItems);
		tlv.setAdapter(dataItemListAdapter);
		tlv.setDropListener(onDrop);
		tlv.getAdapter();
		dataItemListAdapter.notifyDataSetChanged();	
		
		set_list_listener(is_curretn_user_data_list);
		
	}
    private boolean set_list_listener(boolean is_curretn_user_data_list){
    	if(!is_curretn_user_data_list){
    		return is_curretn_user_data_list;
    	}

		tlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				TextView info_tv = (TextView) list_item
						.findViewById(R.id.data_item_info_tv);
				final DataItem item = (DataItem) info_tv
						.getTag(R.id.tag_note_uuid);
				Intent intent = new Intent(DataItemListActivity.this,CreateDataItemActivity.class);
				intent.putExtra("data_item",item);
				intent.putExtra("data_list",dataList);
				intent.putExtra("data_list_public",data_list_public);
				startActivityForResult(intent,RequestCode.BACK);
			}
		});
		tlv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				DataItem from_item = dataItems.get((int) arg3);
				new AlertDialog.Builder(DataItemListActivity.this)
						.setTitle("当前选项：   " + from_item.title)
						.setItems(
								new String[] { 
										"向上移动一行", 
										"向下移动一行", 
										"编辑当前子项",
										"删除当前子项" },
								new CreateContextMenu((int) arg3))
						.setNegativeButton("取消", null).show();
				return false;
			}
		});
		return is_curretn_user_data_list;
    }
	private ListViewInterceptor.DropListener onDrop = new ListViewInterceptor.DropListener() {
		@Override
		public void drop(int from, int to) {

			DataItem from_item = dataItems.get(from);
			DataItem to_item = dataItems.get(to);
			dataItemListAdapter.remove(from_item);
			dataItemListAdapter.insert(from_item, to);
			dataItemListAdapter.notifyDataSetChanged();
			
			System.out.println("from 0: to 1  : size() = " + from + " : " + to + " : " + dataItems.size());
			if(to<=0){
				insert_into(from_item.server_data_item_id,
						"",to_item.position);
			}else if(to>=dataItems.size()){
				insert_into(from_item.server_data_item_id,
						to_item.position,"");
			}else if(from < to && to < dataItems.size()-1){
				insert_into(from_item.server_data_item_id,
						dataItems.get(to-1).position,dataItems.get(to+1).position);
				System.out.println((to-1) + " : " + (to+1));
			}else if(from < to && to == dataItems.size()-1){
				insert_into(from_item.server_data_item_id,
						dataItems.get(to-1).position,"");
			}else{
				insert_into(from_item.server_data_item_id,
						dataItems.get(to-1).position,to_item.position);
				System.out.println((to-1) + " : " + (to));
			}
			System.out.println("from_item : " + from_item);
			System.out.println("to_item : " + to_item);	
		}
	};
	public void on_create_data_item_click(View view) {
		Intent intent = new Intent(DataItemListActivity.this,
				CreateDataItemActivity.class);
		intent.putExtra("data_list", dataList);
		intent.putExtra("data_list_public", data_list_public);
		// this.startActivity(intent);
		this.startActivityForResult(intent, RequestCode.CREATE_DATA_ITEM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case RequestCode.CREATE_DATA_ITEM:
			load_data_item_list();
			break;
		case RequestCode.BACK:
			load_UI();
			load_data_item_list();
			break;
		}
	}

	private void insert_into(final int from_server,final String left_position,final String right_position) {
		new TeamknAsyncTask<Void, Void, Void>() {
			@Override
			public Void do_in_background(Void... params) throws Exception {
				try {
					if (BaseUtils.is_wifi_active(DataItemListActivity.this)) {
						HttpApi.DataItem.order(from_server, left_position,right_position);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			public void on_success(Void result) {	
			}
		}.execute();
	}

	class CreateContextMenu implements OnClickListener {
		int from_id;

		public CreateContextMenu(int from_id) {
			this.from_id = from_id;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:// 向上移动一行
				if (from_id > 0) {
					DataItem from_item = dataItems.get(from_id);
					DataItem to_item = dataItems.get(from_id - 1);
					dataItemListAdapter.remove(from_item);
					dataItemListAdapter.insert(from_item, from_id - 1);
					if(from_id==1){
						insert_into(from_item.server_data_item_id,
								"",to_item.position);
					}else{
						insert_into(from_item.server_data_item_id,
								dataItems.get(from_id - 2).position,to_item.position);
					}
					
				}
				break;
			case 1:// 向下移动一行
				System.out.println("from_id : size = " + from_id  + " : " + dataItems.size());
				if (from_id < dataItems.size() - 2) {
					DataItem from_item1 = dataItems.get(from_id);
					DataItem to_item1 = dataItems.get(from_id + 1);
					dataItemListAdapter.remove(from_item1);
					dataItemListAdapter.insert(from_item1, from_id + 1);
					if(from_id==dataItems.size()-1){
						insert_into(from_item1.server_data_item_id,
								to_item1.position,"");
					}else{
						insert_into(from_item1.server_data_item_id,
								to_item1.position,dataItems.get(from_id + 2).position);
					}
					
				}else if(from_id < dataItems.size() - 1){
					DataItem from_item1 = dataItems.get(from_id);
					DataItem to_item1 = dataItems.get(from_id + 1);
					dataItemListAdapter.remove(from_item1);
					dataItemListAdapter.insert(from_item1, from_id + 1);
					insert_into(from_item1.server_data_item_id,
								to_item1.position,"");
				}
				break;
			case 2:// 编辑当前子项
				final DataItem dataItem2 = dataItems.get(from_id);
				Toast.makeText(DataItemListActivity.this, dataItem2.title,
						Toast.LENGTH_SHORT).show();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						DataItemListActivity.this);

				builder.setTitle("请修改");
				builder.setIcon(android.R.drawable.ic_dialog_info);

				final EditText view = new EditText(DataItemListActivity.this);
				view.setText(dataItem2.title);
				builder.setView(view);
				builder.setPositiveButton("确定",
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								final String add_data_list_et_str = view
										.getText().toString();
								if (add_data_list_et_str != null
										&& !add_data_list_et_str.equals(null)
										&& !BaseUtils
												.is_str_blank(add_data_list_et_str)
										&& !add_data_list_et_str
												.equals(dataItem2.title)) {
									if (BaseUtils
											.is_wifi_active(DataItemListActivity.this)) {				
										new TeamknAsyncTask<Void, Void, Void>() {
											@Override
											public Void do_in_background(
													Void... params)
													throws Exception {
												dataItem2.setTitle(add_data_list_et_str);
												try {
													HttpApi.DataItem.update(dataItem2);
												} catch (Exception e) {
													e.printStackTrace();
												}
												return null;
											}
											@Override
											public void on_success(Void result) {
												load_data_item_list();
											}
										}.execute();
									}else{
										BaseUtils.toast("无法连接网络");
									}
								}
							}
						});
				builder.setNegativeButton("取消", null);
				builder.show();
				break;
			case 3:// 删除当前子项
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						DataItemListActivity.this);				
				builder1.setTitle("确定要删除吗");
				builder1.setPositiveButton("确定",
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								final DataItem dataItem3 = dataItems.get(from_id);
								if (BaseUtils
										.is_wifi_active(DataItemListActivity.this)) {
									new TeamknAsyncTask<Void, Void, Void>(DataItemListActivity.this,"正在处理") {
										@Override
										public Void do_in_background(
												Void... params)
												throws Exception {
											try {
												if (dataItem3.server_data_item_id >= 0) {
													HttpApi.DataItem
															.remove_contact(dataItem3.server_data_item_id);
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
											return null;
										}
										@Override
										public void on_success(Void result) {
											dataItems.remove(from_id);
											load_list();
										}
									}.execute();
								}else{
									BaseUtils.toast("无法连接网络");
								}
							}
						});
				builder1.setNegativeButton("取消", null);
				builder1.show();
				break;
			default:
				break;
			}
		}
	}
	// 钩子，自行重载
	public void on_go_back() {
		System.out.println("dataitemlistactivity on_go_back()");
		if(current_user().is_show_tip){
			if(MainActivity.RequestCode.SHOW_STEP_HELP==MainActivity.RequestCode.SHOW_HELP){
				MainActivity.RequestCode.SHOW_NEXT = MainActivity.RequestCode.SHOW_COLLECTION_HELP_CASE ;
			}
			if(MainActivity.RequestCode.SHOW_COLLECTION_HELP==MainActivity.RequestCode.SHOW_HELP){
				MainActivity.RequestCode.SHOW_NEXT = MainActivity.RequestCode.SHOW_CREATE_HELP_CASE ;
			}
			if(MainActivity.RequestCode.SHOW_CREATE_HELP == MainActivity.RequestCode.SHOW_HELP){
				MainActivity.RequestCode.SHOW_NEXT = MainActivity.RequestCode.SHOW_CREATE_NEXT_HELP_CASE ;
			}
		}else{
			MainActivity.RequestCode.SHOW_NEXT = MainActivity.RequestCode.SHOW_NOT_HELP_CASE ;
		}
		Intent intent = new Intent(DataItemListActivity.this,MainActivity.class);
		intent.putExtra("data_list_public", data_list_public);
     	intent.putExtra("data_list_type", data_list_type);
		setResult(MainActivity.RequestCode.SHOW_BACK,intent);
	};
}
