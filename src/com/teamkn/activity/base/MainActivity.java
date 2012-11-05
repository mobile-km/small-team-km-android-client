package com.teamkn.activity.base;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.activity.dataitem.DataItemListActivity;
import com.teamkn.activity.datalist.CreateDataListActivity;
import com.teamkn.activity.datalist.SearchDataActivity;
import com.teamkn.activity.usermsg.UserMsgActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.SharedParam;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataList;
import com.teamkn.model.Watch;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.model.database.UserDBHelper;
import com.teamkn.model.database.WatchDBHelper;
import com.teamkn.service.SynNoteService.SynNoteBinder;
import com.teamkn.widget.adapter.DataListAdapter;
import com.teamkn.widget.adapter.GroupAdapter;

public class MainActivity extends TeamknBaseActivity {

	View view_show;
	static TextView teamkn_show_msg_tv;
	LinearLayout layout;

	public static void set_teamkn_show_msg_tv(final String msg) {
		teamkn_show_msg_tv.post(new Runnable() {
			@Override
			public void run() {
				teamkn_show_msg_tv.setText(msg);
			}
		});
	}

	public static class RequestCode {
		public final static int CREATE_DATA_LIST = 0;
		public final static int SHOW_BACK = 9;

		public final static String COLLECTION = "COLLECTION";
		public final static String STEP = "STEP";
		public final static String ALL = "ALL";

		public static String data_list_type = ALL;

		public static String data_list_public = "false";

		static int account_page = 20;
		static int now_page = 1;
	}
	// node_listView_show 数据
	ListView data_list;
	public static DataListAdapter dataListAdapter;
	List<DataList> datalists;
	/*
	 * 收集，步骤，所有
	 */
	LinearLayout top;
	Button click_collection_button, click_step_button, click_all_button;
	TextView public_data_list_tv;
    /*
     * cursor imageview 页卡头标
     * */
	private static ImageView cursor;// 动画图片
	private static int offset = 0;// 动画图片偏移量
	private static int currIndex = 0;// 当前页卡编号
	private static int bmpW;// 动画图片宽度
	/*
     * popupwindow title public
     * */
	PopupWindow popupWindow; 
	private ListView lv_group;   
    private View view; 
    private List<String> groups; 
    TextView user_name_tv;
    
	private TextView data_syn_textview; // 同步更新时间
	private ProgressBar data_syn_progress_bar; // 同步更新进度条
	private TextView progress_set_num; // 同步更新时间
	private ImageView manual_syn_bn;
	private SynNoteBinder syn_note_binder; // 同步更新binder
	private SynUIBinder syn_ui_binder = new SynUIBinder();

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			syn_note_binder = (SynNoteBinder) service;
			syn_note_binder.set_syn_ui_binder(syn_ui_binder);
			syn_note_binder.start();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// 当 SynNoteService 因异常而断开连接的时候，这个方法才会被调用
			System.out.println("ServiceConnection  onServiceDisconnected");
			syn_note_binder = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.horz_scroll_with_image_menu);
		layout = (LinearLayout) findViewById(R.id.linearlayout_loading);

		LayoutInflater inflater = LayoutInflater.from(this);
		view_show = inflater.inflate(R.layout.base_main, null);
		layout.addView(view_show);
        
		// 加载node_listview
		InitImageView(); //初始化 cursor中的收集，步骤，所有 滑动标
		Intent intent = getIntent();
		
		String data_list_public = intent.getStringExtra("data_list_public");
		String data_list_type = intent.getStringExtra("data_list_type");
		if (data_list_public != null && data_list_type != null) {
			RequestCode.data_list_public = data_list_public;
			RequestCode.data_list_type = data_list_type;
			System.out.println(RequestCode.data_list_public + " : "
					+ RequestCode.data_list_type);
		}	
		
		if(SharedParam.getPauseParam(this)){
			load_list();
		}else{
			load_data_list_or_watch(RequestCode.data_list_public);
		}
		SharedParam.savePauseParam(this, false);
	}
	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}
	@Override
	protected void onResume() {
		// 设置用户头像和名字
		AccountUser user = current_user();
		byte[] avatar = user.avatar;
		String name = current_user().name;
		RelativeLayout rl = (RelativeLayout) view_show
				.findViewById(R.id.main_user_avatar);
		if (avatar != null) {
			Bitmap bitmap = BitmapFactory
					.decodeStream(new ByteArrayInputStream(avatar));
			Drawable drawable = new BitmapDrawable(bitmap);
			rl.setBackgroundDrawable(drawable);
		} else {
			rl.setBackgroundResource(R.drawable.user_default_avatar_normal);
		}
		user_name_tv= (TextView) view_show
				.findViewById(R.id.main_user_name);
		user_name_tv.setText(name+"的列表");
		LinearLayout user_name_rl = (LinearLayout)findViewById(R.id.main_user_name_rl);
		user_name_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				showWindow(v);
			}
		});
		set_title();
		super.onResume();
	}
    private void set_title(){
    	if(RequestCode.data_list_public.equals("true")){
    		user_name_tv.setText("公共列表");
    	}else if(RequestCode.data_list_public.equals("watch")){
    		user_name_tv.setText("我的书签");
    	}else if(RequestCode.data_list_public.equals("false")){
    		String user_name_sub = current_user().name;
    		if(user_name_sub.length()>14){
    			user_name_sub = user_name_sub.substring(0, 14) + "..";
    		}
    		user_name_tv.setText(user_name_sub + "的列表");
    	}else if(RequestCode.data_list_public.equals("fork")){
    		user_name_tv.setText("列表协作");
    	}
    }
    private void load_data_list_or_watch(String watch_or_public){
    	if (BaseUtils.is_wifi_active(MainActivity.this)) {
	    	new TeamknAsyncTask<Void, Void, List<DataList>>(MainActivity.this,"内容加载中") {
				@Override
				public List<DataList> do_in_background(Void... params)
						throws Exception {
						if(RequestCode.data_list_public.equals("true")){
							HttpApi.DataList.public_timeline(RequestCode.now_page, 100);
						}else if(RequestCode.data_list_public.equals("false")){
							HttpApi.DataList.pull(RequestCode.data_list_type,RequestCode.now_page, 100);
						}else if(RequestCode.data_list_public.equals("watch")){
							HttpApi.WatchList.watch_public_timeline(RequestCode.now_page, 100);
						}else if(RequestCode.data_list_public.equals("fork")){
							datalists = HttpApi.DataList.forked_list(RequestCode.now_page, 100);
							System.out.println("before dataLists.size() :  " + datalists.size());
						}
						
						
					return null;
				}
				@Override
				public void on_success(List<DataList> datalists) {
					load_list();
				}
			}.execute();
    	}else{
			BaseUtils.toast("无法连接到网络，请检查网络配置");
		}
    }
	// 加载node_listview
	private void load_list() {
		request_pageselected();
		data_list = (ListView) layout.findViewById(R.id.data_list);
		
		dataListAdapter = new DataListAdapter(MainActivity.this);
		try {
			if(RequestCode.data_list_public.equals("watch")){
				List<Watch> watchs = WatchDBHelper.all_by_user_id(UserDBHelper.find_by_server_user_id(current_user().user_id).id);
				datalists = DataListDBHelper.all_by_watch_lists(watchs,RequestCode.data_list_type);
			}else if(RequestCode.data_list_public.equals("fork")){	
				datalists = DataListDBHelper.all(RequestCode.data_list_type,RequestCode.data_list_public);
				
			}else{
				datalists = DataListDBHelper.all(RequestCode.data_list_type,RequestCode.data_list_public);
			}
			System.out.println("after datalists.size() : " + datalists.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		dataListAdapter.add_items(datalists);
		data_list.setAdapter(dataListAdapter);
		dataListAdapter.notifyDataSetChanged();
		//注册上下文菜单
	    registerForContextMenu(data_list);
	    
		data_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				TextView info_tv = (TextView) list_item.findViewById(R.id.info_tv);
				DataList item = (DataList) info_tv.getTag(R.id.tag_note_uuid);
				Intent intent = new Intent(MainActivity.this,DataItemListActivity.class);
				intent.putExtra("data_list_id",item.id);
				intent.putExtra("data_list_public", RequestCode.data_list_public);
				System.out.println("mainactivity setonclick  = " +item.toString());
				
				boolean is_delete = false ;
				if(RequestCode.data_list_public.equals("fork")){
					is_delete = DataListDBHelper
							.is_delete(HttpApi.DataList.deletForkList, item);
				}else if(RequestCode.data_list_public.equals("watch")){
					is_delete = DataListDBHelper
							.is_delete(HttpApi.WatchList.deletWatchList, item);
				}
				if(is_delete){
					showDialog(item);
				}else{
					startActivityForResult(intent, RequestCode.SHOW_BACK);
				}
			}
		});
	}
	private void showDialog(final DataList dataList){
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setTitle("请修改");
		builder.setMessage("列表已经被原作者删除，是否删除该记录？");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DataListDBHelper.remove_by_server_id(dataList);
				dataListAdapter.remove_item(dataList);
				dataListAdapter.notifyDataSetChanged();
			}
		});
		builder.show();
	}
	// 处理其他activity界面的回调 有要改进的地方 如 记忆从别的地方回来，还要回到上次加载的地方
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println(requestCode+ " : " + resultCode );
		if (resultCode == Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case RequestCode.CREATE_DATA_LIST:
			load_list();
			break;
		case RequestCode.SHOW_BACK:
			System.out.println("DataItemListActivity.update_title " + DataItemListActivity.update_title);
			if(DataItemListActivity.update_title ==true){
				set_title();
				load_list();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 同步
	public void click_manual_syn(View view) {
		if (syn_note_binder != null) {
			manual_syn_bn.setVisibility(View.VISIBLE);
			data_syn_progress_bar.setVisibility(View.VISIBLE);
			syn_note_binder.manual_syn();
		}
	}

	public void click_update_user_msg(View view) {
		open_activity(UserMsgActivity.class);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public class SynUIBinder {
		public void set_max_num(int max_num) {
			final int num = max_num;
			data_syn_progress_bar.post(new Runnable() {
				@Override
				public void run() {
					data_syn_progress_bar.setMax(num);
				}
			});
		}

		public void set_start_syn() {
			data_syn_textview.post(new Runnable() {
				@Override
				public void run() {
					data_syn_textview.setText(R.string.now_syning);
					data_syn_progress_bar.setProgress(0);
					data_syn_progress_bar.setVisibility(View.VISIBLE);
				}
			});
		}

		public void set_progress(int progress) {
			final int num = progress;
			data_syn_progress_bar.post(new Runnable() {
				@Override
				public void run() {
					progress_set_num.setVisibility(View.VISIBLE);
					data_syn_progress_bar.setVisibility(View.VISIBLE);
					manual_syn_bn.setVisibility(View.GONE);
					int baifen = (num * 100) / data_syn_progress_bar.getMax();
					progress_set_num.setText(baifen + "%");
				}
			});
		}

		public void set_syn_success() {
			data_syn_textview.post(new Runnable() {
				@Override
				public void run() {
					String str = BaseUtils.date_string(TeamknPreferences
							.last_syn_success_client_time());
					data_syn_textview.setText("上次同步成功: " + str);
					data_syn_progress_bar.setVisibility(View.GONE);
					progress_set_num.setText("");
					manual_syn_bn.setVisibility(View.VISIBLE);

					load_list();
				}
			});

			if (TeamknApplication.current_show_activity == null
					|| !TeamknApplication.current_show_activity
							.equals("com.teamkn.activity.base.MainActivity")) {
				// TODO 增加通知提示
			}
		}

		public void set_syn_fail() {
			TeamknPreferences.touch_last_syn_fail_client_time();
			data_syn_textview.post(new Runnable() {
				@Override
				public void run() {
					String str = BaseUtils.date_string(TeamknPreferences
							.last_syn_fail_client_time());
					data_syn_textview.setText("上次同步失败: " + str);
					progress_set_num.setText("");
					data_syn_progress_bar.setVisibility(View.GONE);
					manual_syn_bn.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	public void click_search_ib(View view) {
		EditText search_box = (EditText)findViewById(R.id.search_box);
		String search_str = search_box.getText().toString();
		if(!BaseUtils.is_str_blank(search_str)){
			Intent intent = new Intent(MainActivity.this,SearchDataActivity.class);
			intent.putExtra("search_str", search_str);
			intent.putExtra("data_list_type", RequestCode.data_list_type);
			intent.putExtra("data_list_public", RequestCode.data_list_public);
			startActivity(intent);
		}
	}

	public void click_add_data_list_iv(View view) {
		Intent intent = new Intent(MainActivity.this,
				CreateDataListActivity.class);
		startActivityForResult(intent, RequestCode.CREATE_DATA_LIST);

	}

	public void click_collection_button(View view) {
		RequestCode.data_list_type = RequestCode.COLLECTION;
		load_list();
	}

	public void click_step_button(View view) {
		RequestCode.data_list_type = RequestCode.STEP;
		load_list();
	}

	public void click_all_button(View view) {
		RequestCode.data_list_type = RequestCode.ALL;
		load_list();
	}
	private void request_pageselected(){
		int index = 0;
		if(RequestCode.data_list_type.equals(RequestCode.COLLECTION)){
			index = 1 ;
		}else if(RequestCode.data_list_type.equals(RequestCode.STEP)){
			index = 2 ;
		}else if(RequestCode.data_list_type.equals(RequestCode.ALL)) {
			index = 0 ;
		}
		MyOnPageChangeListener.onPageSelected(index);
	}
	private void showWindow(View parent) { 
		final ImageView main_user_name_iv = (ImageView)findViewById(R.id.main_user_name_iv);
		main_user_name_iv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
		if (popupWindow == null) {  
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
  
            view = layoutInflater.inflate(R.layout.group_list, null);  
  
            lv_group = (ListView) view.findViewById(R.id.lvGroup);  
            // 加载数据  
            groups = new ArrayList<String>();  
//            groups.add("全部");  
            groups.add("我的列表");  
            groups.add("公共列表");  
            groups.add("我的书签");
            groups.add("列表协作");
  
            GroupAdapter groupAdapter = new GroupAdapter(this); 
            groupAdapter.add_items(groups);
            lv_group.setAdapter(groupAdapter);  
            // 创建一个PopuWidow对象  
            popupWindow = new PopupWindow(view, 200, 300);  
        }    
        // 使其聚集  
        popupWindow.setFocusable(true);  
        // 设置允许在外点击消失  
        popupWindow.setOutsideTouchable(true);  
//        popupWindow.set
        popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
//				System.out.println(popupWindow.);
				if(popupWindow == null){
					System.out.println("popupWindow  null");
				}else{
					System.out.println("popupWindow  yes  " + popupWindow );
				}
			}
		});
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);  
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半  
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2  
                - popupWindow.getWidth() / 2;  
        Log.i("coder", "xPos:" + xPos);  
  
        popupWindow.showAsDropDown(parent, xPos-60, 0);  
  
        lv_group.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> adapterView, View view,  
                    int position, long id) {  
  
                switch (position) {
//				case 0:
//					break;
				case 0:
					RequestCode.data_list_public = "false";
					set_title();
//					load_data_list();
//					load_list();
					load_data_list_or_watch(RequestCode.data_list_public);
					break;
				case 1:
					RequestCode.data_list_public = "true";
					set_title();
					load_data_list_or_watch(RequestCode.data_list_public);
					break;
				case 2:
					RequestCode.data_list_public = "watch";
					set_title();
					load_data_list_or_watch(RequestCode.data_list_public);
					break;
				case 3:
					RequestCode.data_list_public = "fork";
					set_title();
					load_data_list_or_watch(RequestCode.data_list_public);
					break;
				default:
					break;
				}  
                if (popupWindow != null) {  
                    popupWindow.dismiss();  
                    popupWindow = null;
                    main_user_name_iv.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }  
        }); 
        
    }  
	/**
	 * 页卡切换监听
	 */
	static class MyOnPageChangeListener{
		static int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		static int two = one * 2;// 页卡1 -> 页卡3 偏移量
		public static void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			try {
				animation.setFillAfter(true);// True:图片停在动画结束位置
				animation.setDuration(300);
				cursor.startAnimation(animation);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		if(info.id!=-1){
			menu.setHeaderTitle("弹出菜单");
			menu.add(1,1,1,"查看");
			final Integer id = (int) info.id;
			if(datalists.get(id).user_id == UserDBHelper.find_by_server_user_id(current_user().user_id).id){
				menu.add(1,2,2,"删除");
			}
//			menu.add(groupId, itemId, order, title)
		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case 1:
			break;
		case 2:
			System.out.println(info.id);
			final Integer id = (int) info.id;
			System.out.println("onContextItemSelected = "+datalists.get(id).toString());
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("确定要删除吗？");
			builder.setPositiveButton(getResources().getString(R.string.dialog_ok), new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					remove_data_list(datalists.get(id),id);
				}
			});
			builder.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
			builder.show();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
    private void remove_data_list(final DataList dataList,final int data_list_id){
    	if(BaseUtils.is_wifi_active(this)){
    		new TeamknAsyncTask<Void, Void, Boolean>(MainActivity.this,getResources().getString(R.string.now_deleting)) {
				@Override
				public Boolean do_in_background(Void... params)
						throws Exception {
					HttpApi.DataList.remove(dataList);
					return true;
				}
				@Override
				public void on_success(Boolean result) {
					datalists.remove(data_list_id);
					DataListDBHelper.remove_by_server_id(dataList);
					dataListAdapter.remove_item(dataList);
					dataListAdapter.notifyDataSetChanged();
					BaseUtils.toast("删除成功");
				}
			}.execute();
    	}else{
    		BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
    	}
    }
	@Override
	protected void onPause() {
//		this.finish();
//		Intent intent = new Intent(this,MainActivity.class);
//		intent.putExtra("data_list_public", RequestCode.data_list_public);
//		intent.putExtra("data_list_type", RequestCode.data_list_type);
//		SharedParam.savePauseParam(this, true);
//		startActivity(intent);
		super.onPause();
	}	
}
