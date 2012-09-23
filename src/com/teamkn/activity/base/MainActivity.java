package com.teamkn.activity.base;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataListDBHelper;
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
	DataListAdapter dataListAdapter;
	List<DataList> datalists;
	View footer_view;
	EditText add_data_list_et;
	RelativeLayout show_add_data_list_rl;

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
        
		
		Intent intent = getIntent();
		String data_list_public = intent.getStringExtra("data_list_public");
		String data_list_type = intent.getStringExtra("data_list_type");
		if (data_list_public != null && data_list_type != null) {
			RequestCode.data_list_public = data_list_public;
			RequestCode.data_list_type = data_list_type;
			System.out.println(RequestCode.data_list_public + " : "
					+ RequestCode.data_list_type);
		}
		// 加载node_listview
		InitImageView();
		load_list();
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
		user_name_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				showWindow(v);
			}
		});
		super.onResume();
		set_title();
	}
    private void set_title(){
    	if(RequestCode.data_list_public.equals("true")){
    		user_name_tv.setText("公共列表");
    	}else{
    		user_name_tv.setText(current_user().name + "的列表");
    	}
    }
	// 加载node_listview
	private void load_list() {
		request_pageselected();
		data_list = (ListView) layout.findViewById(R.id.data_list);
		// bind_add_footer_view();
		datalists = new ArrayList<DataList>();
		dataListAdapter = new DataListAdapter(MainActivity.this);

		new TeamknAsyncTask<Void, Void, List<DataList>>(MainActivity.this,"内容加载中") {
			@Override
			public List<DataList> do_in_background(Void... params)
					throws Exception {
				// datalists = NoteDBHelper.all(true);
				if (BaseUtils.is_wifi_active(MainActivity.this)) {
					HttpApi.DataList.pull(RequestCode.data_list_type,
							RequestCode.now_page, 100);
//					datalists = DataListDBHelper.all(
//							RequestCode.data_list_type,
//							RequestCode.data_list_public);
					datalists = DataListDBHelper.all(RequestCode.data_list_type,RequestCode.data_list_public);
				}else{
					BaseUtils.toast("无法连接到网络，请检查网络配置");
					System.out.println("无法连接到网络，请检查网络配置");
				}
				return datalists;
			}

			@Override
			public void on_success(List<DataList> datalists) {
				dataListAdapter.add_items(datalists);
				data_list.setAdapter(dataListAdapter);
				dataListAdapter.notifyDataSetChanged();
			}
		}.execute();
		
		data_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list_view, View list_item,
					int item_id, long position) {
				System.out.println(item_id + " : " + position);
				TextView info_tv = (TextView) list_item
						.findViewById(R.id.info_tv);
				final DataList item = (DataList) info_tv
						.getTag(R.id.tag_note_uuid);
				Intent intent = new Intent(MainActivity.this,DataItemListActivity.class);
				intent.putExtra("data_list_id",item.id);
				intent.putExtra("data_list_public", RequestCode.data_list_public);
				startActivity(intent);
			}
		});
	}

	// 处理其他activity界面的回调 有要改进的地方 如 记忆从别的地方回来，还要回到上次加载的地方
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case RequestCode.CREATE_DATA_LIST:
			load_list();
			BaseUtils.toast("RequestCode.CREATE_DATA_ITEM    "
					+ RequestCode.CREATE_DATA_LIST);
			break;
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
			// System.out.println("set_max_num   " + max_num);
			data_syn_progress_bar.post(new Runnable() {
				@Override
				public void run() {
					data_syn_progress_bar.setMax(num);
				}
			});
		}

		public void set_start_syn() {
			// System.out.println("set_start_syn");
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
			// System.out.println("set_progress  " + progress);
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
			// System.out.println("syn_success");
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
			// System.out.println("syn_fail");
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
		System.out.println("1------------");
		request_pageselected();
		load_list();
	}

	public void click_step_button(View view) {
		RequestCode.data_list_type = RequestCode.STEP;
		System.out.println("2------------");
		request_pageselected();
		load_list();
	}

	public void click_all_button(View view) {
		RequestCode.data_list_type = RequestCode.ALL;
		System.out.println("3------------");
		request_pageselected();
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
        if (popupWindow == null) {  
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
  
            view = layoutInflater.inflate(R.layout.group_list, null);  
  
            lv_group = (ListView) view.findViewById(R.id.lvGroup);  
            // 加载数据  
            groups = new ArrayList<String>();  
            groups.add("全部");  
            groups.add("我的列表");  
            groups.add("公共列表");    
  
            GroupAdapter groupAdapter = new GroupAdapter(this); 
            groupAdapter.add_items(groups);
            lv_group.setAdapter(groupAdapter);  
            // 创建一个PopuWidow对象  
            popupWindow = new PopupWindow(view, 180, 218);  
        }  
  
        // 使其聚集  
        popupWindow.setFocusable(true);  
        // 设置允许在外点击消失  
        popupWindow.setOutsideTouchable(true);  
  
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);  
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半  
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2  
                - popupWindow.getWidth() / 2;  
        Log.i("coder", "xPos:" + xPos);  
  
        popupWindow.showAsDropDown(parent, xPos-50, 0);  
  
        lv_group.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> adapterView, View view,  
                    int position, long id) {  
  
                Toast.makeText(MainActivity.this,groups.get(position), Toast.LENGTH_LONG).show();  
                switch (position) {
				case 0:
					break;
				case 1:
					RequestCode.data_list_public = "false";
					set_title();
					load_list();
					break;
				case 2:
					RequestCode.data_list_public = "true";
					set_title();
					load_list();
					break;
				default:
					break;
				}
                if (popupWindow != null) {  
                    popupWindow.dismiss();  
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
			System.out.println("arg0  "  + arg0);
		}
	}
}
