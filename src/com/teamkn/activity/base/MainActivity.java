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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.activity.usermsg.UserMsgActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.IndexTimerTask;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.SharedParam;
import com.teamkn.model.AccountUser;
import com.teamkn.model.DataList;
import com.teamkn.model.database.DataListDBHelper;
import com.teamkn.service.FaceCommentService;
import com.teamkn.service.IndexService;
import com.teamkn.service.RefreshContactStatusService;
import com.teamkn.service.SynChatService;
import com.teamkn.service.SynNoteService;
import com.teamkn.service.SynNoteService.SynNoteBinder;
import com.teamkn.widget.adapter.DataListAdapter;

public class MainActivity extends TeamknBaseActivity{

	View view_show;
	static TextView teamkn_show_msg_tv;
	LinearLayout layout;
	public static void set_teamkn_show_msg_tv(final String msg){
		teamkn_show_msg_tv.post(new Runnable() {
			@Override
			public void run() {
		        teamkn_show_msg_tv.setText(msg);
			}
		});
	}
	 
	public static class RequestCode {
		public final static int EDIT_TEXT = 0;
        public final static int SHOW_BACK= 9;
		
        public final static String COLLECTION = "COLLECTION";
        public final static String STEP= "STEP";
        static int account_page = 20;
        static int now_page = 1;
    }
	
	//  node_listView_show 数据
	ListView data_list;
	DataListAdapter dataListAdapter;
    List<DataList> datalists;
    View footer_view;
    EditText add_data_list_et;
    RelativeLayout show_add_data_list_rl;
    
	private TextView data_syn_textview;         // 同步更新时间
	private ProgressBar data_syn_progress_bar;  // 同步更新进度条
	private TextView progress_set_num; //同步更新时间
	private ImageView manual_syn_bn;
	private SynNoteBinder syn_note_binder;      // 同步更新binder 
	private SynUIBinder syn_ui_binder = new SynUIBinder();
	
	
	private ServiceConnection conn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			syn_note_binder = (SynNoteBinder)service;
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
		layout = (LinearLayout)findViewById(R.id.linearlayout_loading);
        
        teamkn_show_msg_tv = (TextView)findViewById(R.id.teamkn_show_msg_tv);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        view_show = inflater.inflate(R.layout.base_main, null);
        layout.addView(view_show);

//		data_syn_textview = (TextView)view_show.findViewById(R.id.main_data_syn_text);
//		data_syn_progress_bar = (ProgressBar)view_show.findViewById(R.id.main_data_syn_progress_bar);
//		progress_set_num = (TextView)view_show.findViewById(R.id.progress_set_num);
//		manual_syn_bn = (ImageView)view_show.findViewById(R.id.manual_syn_bn);
//		
//		// 注册更新服务
//		Intent intent = new Intent(MainActivity.this,SynNoteService.class);
//		bindService(intent, conn, Context.BIND_AUTO_CREATE);
//		   
//		// 开始后台索引服务
//		IndexService.start(this);
//		IndexTimerTask.index_task(IndexTimerTask.SCHEDULE_INTERVAL);
//		
//		// 注册更新表情反馈服务
//		startService(new Intent(MainActivity.this,FaceCommentService.class));
//		SharedParam.saveParam(this, 0);
//		FaceCommentService.context = this;	
//    
//		// 启动刷新联系人状态服务
//		startService(new Intent(MainActivity.this,RefreshContactStatusService.class));
//		// 启动更新 对话串的服务
//		startService(new Intent(MainActivity.this,SynChatService.class));
		
        
		//加载node_listview
		data_list = (ListView)layout.findViewById(R.id.data_list);
		data_list.setItemsCanFocus(true);
		bind_add_footer_view();
		load_list();
	}
	// 设置 创建新列表按钮事件
    private void bind_add_footer_view() {
        footer_view = getLayoutInflater().inflate(R.layout.list_data_list_item_footer, null);
        data_list.addFooterView(footer_view);
        add_data_list_et = (EditText)findViewById(R.id.add_data_list_et);
        footer_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                open_activity(LoginActivity.class);
//            	BaseUtils.toast("  show  footer view");
            	show_add_data_list_rl = (RelativeLayout)footer_view.findViewById(R.id.show_add_data_list_rl);
            	show_add_data_list_rl.setVisibility(View.GONE);
            	add_data_list_et.setVisibility(View.VISIBLE);	 
            }
        });
        add_data_list_et.setOnFocusChangeListener(new OnFocusChangeListener() {  
            @Override  
            public void onFocusChange(View v, boolean hasFocus) {  
                if(add_data_list_et.hasFocus()==false){       
                    String add_data_list_et_str = add_data_list_et.getText().toString();
                    if(add_data_list_et_str!=null 
                    		&& !add_data_list_et_str.equals(null)
                    		&& !BaseUtils.is_str_blank(add_data_list_et_str)){
                    	if(BaseUtils.is_wifi_active(MainActivity.this)){
    						try {
    							DataListDBHelper.create(current_user().user_id , add_data_list_et_str, RequestCode.COLLECTION, "true");
    							HttpApi.DataList.create(DataListDBHelper.all().get(0));
    						} catch (Exception e) {
								e.printStackTrace();
							}
    					}
                    	add_data_list_et.setText(null);
                    	show_add_data_list_rl.setVisibility(View.VISIBLE);
                    	add_data_list_et.setVisibility(View.GONE);
//                    	add_data_list_et.setFocusable(false);
                    	load_list();
                    }
                }      
            }  
        });
    }
	@Override
	protected void onResume() {
		// 设置用户头像和名字
		AccountUser user = current_user();
		byte[] avatar = user.avatar;
		String name = current_user().name;
		RelativeLayout rl = (RelativeLayout)view_show.findViewById(R.id.main_user_avatar);
		if(avatar != null){
			Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(avatar));
			Drawable drawable = new BitmapDrawable(bitmap);
			rl.setBackgroundDrawable(drawable);
		}else{
		    rl.setBackgroundResource(R.drawable.user_default_avatar_normal);
		}
	    TextView user_name_tv = (TextView)view_show.findViewById(R.id.main_user_name);
	    user_name_tv.setText(name);
		super.onResume();
	}
	//加载node_listview
	private void load_list() { 
		datalists  = new ArrayList<DataList>();
		dataListAdapter = new DataListAdapter(MainActivity.this);	
		
		new TeamknAsyncTask<Void, Void, List<DataList>>() {
			@Override
			public List<DataList> do_in_background(Void... params) throws Exception {
//					datalists = NoteDBHelper.all(true);
					if(BaseUtils.is_wifi_active(MainActivity.this)){
						HttpApi.DataList.pull(RequestCode.COLLECTION, RequestCode.now_page, RequestCode.account_page);
						datalists = DataListDBHelper.all();
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				System.out.println(arg2 + " : " + arg3);
			}
		});
    }	

	// 处理其他activity界面的回调  有要改进的地方 如 记忆从别的地方回来，还要回到上次加载的地方
    @Override
    protected void onActivityResult(int  requestCode, int resultCode,Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }

	//同步
	public void click_manual_syn(View view){
		if(syn_note_binder != null){
			manual_syn_bn.setVisibility(View.VISIBLE);
			data_syn_progress_bar.setVisibility(View.VISIBLE);
		    syn_note_binder.manual_syn();   
		}
	}

	public void click_update_user_msg(View view){
		open_activity(UserMsgActivity.class);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		// 解除 和 更新笔记服务的绑定
//		unbindService(conn);
//		// 关闭更新联系人状态服务
//		stopService(new Intent(MainActivity.this,RefreshContactStatusService.class));
//		// 关闭更新对话串的服务
//		stopService(new Intent(MainActivity.this,SynChatService.class));
//		IndexService.stop();
//		stopService(new Intent(MainActivity.this,FaceCommentService.class));
	}

	 public class SynUIBinder{
		    public void set_max_num(int max_num){
		      final int num = max_num;
//		      System.out.println("set_max_num   " + max_num);
		      data_syn_progress_bar.post(new Runnable() {
		          @Override
		          public void run() {
		            data_syn_progress_bar.setMax(num);
		          }
	          });
		    }
		    
		    public void set_start_syn(){
//		      System.out.println("set_start_syn");
		      data_syn_textview.post(new Runnable() {
			      @Override
			      public void run() {
			        data_syn_textview.setText(R.string.now_syning);
			        data_syn_progress_bar.setProgress(0);
			        data_syn_progress_bar.setVisibility(View.VISIBLE);
			      }
	          });
		    }
		    public void set_progress(int progress){
		      final int num = progress;
//		      System.out.println("set_progress  " + progress);
		      data_syn_progress_bar.post(new Runnable() {
			      @Override
			      public void run() {
			    	progress_set_num.setVisibility(View.VISIBLE);	
			    	data_syn_progress_bar.setVisibility(View.VISIBLE);
			        manual_syn_bn.setVisibility(View.GONE);
			    	int baifen = (num*100)/data_syn_progress_bar.getMax();
			    	progress_set_num.setText(baifen+"%");
			      }
	          });
		    }
		    
		    public void set_syn_success(){
//				System.out.println("syn_success");
				data_syn_textview.post(new Runnable() {
					@Override
					public void run() {
						String str = BaseUtils.date_string(TeamknPreferences.last_syn_success_client_time()); 
						data_syn_textview.setText("上次同步成功: " + str);
						data_syn_progress_bar.setVisibility(View.GONE);
						progress_set_num.setText("");
						manual_syn_bn.setVisibility(View.VISIBLE);	  
						
						load_list();
					}
				});
				  
				if(TeamknApplication.current_show_activity == null 
				    || !TeamknApplication.current_show_activity.equals("com.teamkn.activity.base.MainActivity")){
				    // TODO 增加通知提示
				}
		    }
	  
	      public void set_syn_fail() {
//		        System.out.println("syn_fail");
		        TeamknPreferences.touch_last_syn_fail_client_time();
		        data_syn_textview.post(new Runnable() {
					  @Override
					  public void run() {
					    String str = BaseUtils.date_string(TeamknPreferences.last_syn_fail_client_time()); 
					    data_syn_textview.setText("上次同步失败: " + str);
					    progress_set_num.setText("");
					    data_syn_progress_bar.setVisibility(View.GONE);
					    manual_syn_bn.setVisibility(View.VISIBLE);
					  }
		        });
	       }
	  }		
}
