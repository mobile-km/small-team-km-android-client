package com.teamkn.activity.base;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.R.color;
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

		public static String data_list_public = "true";

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
	 * 搜索
	 */
	// ImageButton data_list_search_ib;
	EditText data_list_search_edit_et;

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

		teamkn_show_msg_tv = (TextView) findViewById(R.id.teamkn_show_msg_tv);

		LayoutInflater inflater = LayoutInflater.from(this);
		view_show = inflater.inflate(R.layout.base_main, null);
		layout.addView(view_show);

		// data_syn_textview =
		// (TextView)view_show.findViewById(R.id.main_data_syn_text);
		// data_syn_progress_bar =
		// (ProgressBar)view_show.findViewById(R.id.main_data_syn_progress_bar);
		// progress_set_num =
		// (TextView)view_show.findViewById(R.id.progress_set_num);
		// manual_syn_bn =
		// (ImageView)view_show.findViewById(R.id.manual_syn_bn);
		//
		// // 注册更新服务
		// Intent intent = new Intent(MainActivity.this,SynNoteService.class);
		// bindService(intent, conn, Context.BIND_AUTO_CREATE);
		//
		// // 开始后台索引服务
		// IndexService.start(this);
		// IndexTimerTask.index_task(IndexTimerTask.SCHEDULE_INTERVAL);
		//
		// // 注册更新表情反馈服务
		// startService(new Intent(MainActivity.this,FaceCommentService.class));
		// SharedParam.saveParam(this, 0);
		// FaceCommentService.context = this;
		//
		// // 启动刷新联系人状态服务
		// startService(new
		// Intent(MainActivity.this,RefreshContactStatusService.class));
		// // 启动更新 对话串的服务
		// startService(new Intent(MainActivity.this,SynChatService.class));

		Intent intent = getIntent();
		String data_list_public = intent.getStringExtra("data_list_public");
		String data_list_type = intent.getStringExtra("data_list_type");
		if (data_list_public != null && data_list_type != null) {
			RequestCode.data_list_public = data_list_public;
			RequestCode.data_list_type = data_list_type;
			System.out.println(RequestCode.data_list_public + " : "
					+ RequestCode.data_list_type);
		}
		load_UI();
		// 加载node_listview
		load_list();
	}

	private void load_UI() {
		// data_list_search_ib =
		// (ImageButton)findViewById(R.id.data_list_search_ib);
		data_list_search_edit_et = (EditText) findViewById(R.id.data_list_search_edit_et);

		public_data_list_tv = (TextView) findViewById(R.id.public_data_list_tv);
		public_data_list_tv.setOnClickListener(click_public_tv);

		// top = (LinearLayout)findViewById(R.id.top);
		// click_collection_button = (Button)
		// findViewById(R.id.click_collection_button);
		// click_step_button = (Button) findViewById(R.id.click_step_button);
		// click_all_button = (Button) findViewById(R.id.click_all_button);

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
		TextView user_name_tv = (TextView) view_show
				.findViewById(R.id.main_user_name);
		user_name_tv.setText(name);
		super.onResume();
	}

	// 加载node_listview
	private void load_list() {
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
					datalists = DataListDBHelper.all(RequestCode.data_list_type);
				}else{
					BaseUtils.toast("无法连接到网络，请检查网络配置");
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
						.findViewById(R.id.note_info_tv);
				final DataList item = (DataList) info_tv
						.getTag(R.id.tag_note_uuid);
				Intent intent = new Intent(MainActivity.this,DataItemListActivity.class);
				intent.putExtra("data_list_id",item.id);
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
		// // 解除 和 更新笔记服务的绑定
		// unbindService(conn);
		// // 关闭更新联系人状态服务
		// stopService(new
		// Intent(MainActivity.this,RefreshContactStatusService.class));
		// // 关闭更新对话串的服务
		// stopService(new Intent(MainActivity.this,SynChatService.class));
		// IndexService.stop();
		// stopService(new Intent(MainActivity.this,FaceCommentService.class));
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
		startActivity(new Intent(MainActivity.this,SearchDataActivity.class));
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

	public void button_show_color(final String type) {
		// Button click_collection_button,click_step_button,click_all_button;
		top.post(new Runnable() {
			@Override
			public void run() {
				if (type.equals(RequestCode.ALL)) {
					click_all_button.setBackgroundColor(color.blue);
					click_step_button.setBackgroundColor(color.aliceblue);
					click_collection_button.setBackgroundColor(color.aliceblue);
				} else if (type.equals(RequestCode.STEP)) {
					click_all_button.setBackgroundColor(color.aliceblue);
					click_step_button.setBackgroundColor(color.blue);
					click_collection_button.setBackgroundColor(color.aliceblue);
				} else if (type.equals(RequestCode.COLLECTION)) {
					click_all_button.setBackgroundColor(color.aliceblue);
					click_step_button.setBackgroundColor(color.aliceblue);
					click_collection_button.setBackgroundColor(color.blue);
				}
			}
		});
	}

	public void base_main_click_public_data_list_tv() {
		if (RequestCode.data_list_public.equals("true")) {
			public_data_list_tv.setText("公开");
			RequestCode.data_list_public = "false";
		} else {
			public_data_list_tv.setText("私有");
			RequestCode.data_list_public = "true";
		}
		load_list();
	}

	OnClickListener click_public_tv = new OnClickListener() {
		@Override
		public void onClick(View v) {
			base_main_click_public_data_list_tv();
		}
	};
}
