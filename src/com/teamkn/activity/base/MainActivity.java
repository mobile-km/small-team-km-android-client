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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamkn.R;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.activity.note.EditNoteActivity;
import com.teamkn.activity.note.ShowNodeActivity;
import com.teamkn.activity.usermsg.UserMsgActivity;
import com.teamkn.application.TeamknApplication;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.IndexTimerTask;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.SharedParam;
import com.teamkn.model.AccountUser;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.service.FaceCommentService;
import com.teamkn.service.IndexService;
import com.teamkn.service.RefreshContactStatusService;
import com.teamkn.service.SynChatService;
import com.teamkn.service.SynNoteService;
import com.teamkn.service.SynNoteService.SynNoteBinder;
import com.teamkn.widget.adapter.NoteListAdapter;

public class MainActivity extends TeamknBaseActivity{

	View view_show;
	static TextView teamkn_show_msg_tv;
	static boolean isFirst = true;
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
        static int account_page = 10;
        static int now_page = 1;
    }
	//  node_listView_show 数据
	private  ListView note_list;

    NoteListAdapter note_list_adapter;
    List<Note> notes;

    private LinearLayout mLoadLayout;   
    TextView mTipContent;
    private final LayoutParams mProgressBarLayoutParams = new LinearLayout.LayoutParams(   

            LinearLayout.LayoutParams.WRAP_CONTENT,   

            LinearLayout.LayoutParams.WRAP_CONTENT); 
	//
	
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

	    mLoadLayout = new LinearLayout(this);   
	    mLoadLayout.setMinimumHeight(30);
	    mLoadLayout.setPadding(0, 5, 0, 5);
        mLoadLayout.setGravity(Gravity.CENTER);   
        mLoadLayout.setOrientation(LinearLayout.HORIZONTAL);               
        mTipContent = new TextView(this);   
        mTipContent.setText("加载更多");   
        mLoadLayout.addView(mTipContent, mProgressBarLayoutParams);   

		
		data_syn_textview = (TextView)view_show.findViewById(R.id.main_data_syn_text);
		data_syn_progress_bar = (ProgressBar)view_show.findViewById(R.id.main_data_syn_progress_bar);
		progress_set_num = (TextView)view_show.findViewById(R.id.progress_set_num);
		manual_syn_bn = (ImageView)view_show.findViewById(R.id.manual_syn_bn);
		
		// 注册更新服务
		Intent intent = new Intent(MainActivity.this,SynNoteService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		   
		// 开始后台索引服务
		IndexService.start(this);
		IndexTimerTask.index_task(IndexTimerTask.SCHEDULE_INTERVAL);
		
		// 注册更新表情反馈服务
		startService(new Intent(MainActivity.this,FaceCommentService.class));
		SharedParam.saveParam(this, 0);
		FaceCommentService.context = this;	
    
		// 启动刷新联系人状态服务
		startService(new Intent(MainActivity.this,RefreshContactStatusService.class));
		// 启动更新 对话串的服务
		startService(new Intent(MainActivity.this,SynChatService.class));
		
		//加载node_listview
		note_list = (ListView)layout.findViewById(R.id.note_list);
		note_list.addFooterView(mLoadLayout);
		mLoadLayout.setVisibility(View.VISIBLE);
		load_list();
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
	private ArrayList<Note> get_list_note(List<Note> notes2,int begin,int end){
		ArrayList<Note> get_notes = new ArrayList<Note>();
		for(int i = begin ;i < end ;i ++){
			get_notes.add(notes2.get(i));
		}
		RequestCode.now_page++;
		return get_notes;
	}
	//加载node_listview
	private void load_list() { 
		mTipContent.setText("加载更多");
	    notes  = new ArrayList<Note>();
		note_list_adapter = new NoteListAdapter(MainActivity.this);	
		
		new TeamknAsyncTask<Void, Void, ArrayList<Note>>() {
			@Override
			public ArrayList<Note> do_in_background(Void... params) throws Exception {
				try {
					notes = NoteDBHelper.all(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				RequestCode.now_page = 1 ;
				if(notes.size()<RequestCode.account_page){
					return get_list_note(notes,0,notes.size());
				}else{
					return get_list_note(notes,0,RequestCode.account_page);
				}
				
				
			}
			@Override
			public void on_success(ArrayList<Note> get_note) {
					note_list_adapter.add_items(get_note);
					note_list.setAdapter(note_list_adapter);	
		    }
		}.execute();

        note_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list_view,View list_item,int item_id,long position) {	

                TextView info_tv = (TextView) list_item.findViewById(R.id.note_info_tv);
                String   uuid    = (String)   info_tv.getTag(R.id.tag_note_uuid);
                String   kind    = (String)   info_tv.getTag(R.id.tag_note_kind);

                Intent   intent  = new Intent(MainActivity.this, ShowNodeActivity.class);
                intent.putExtra(EditNoteActivity.Extra.NOTE_UUID, uuid);
                intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, kind);
                
                if (kind == NoteDBHelper.Kind.IMAGE) {
                    String image_path = Note.note_image_file(uuid).getPath();
                    intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH,image_path);
                }                               
                startActivityForResult(intent, MainActivity.RequestCode.SHOW_BACK);
            }
        });
        mLoadLayout.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				
				if((RequestCode.now_page-1)*RequestCode.account_page < notes.size() && 
						 RequestCode.now_page*RequestCode.account_page < notes.size()){
					ArrayList<Note> get_notes;
					if( RequestCode.now_page*RequestCode.account_page >notes.size() ){
						get_notes = get_list_note(notes,(RequestCode.now_page-1)*RequestCode.account_page,notes.size());
					}else{
						get_notes = get_list_note(notes,(RequestCode.now_page-1)*RequestCode.account_page,RequestCode.now_page*RequestCode.account_page);
					}
					note_list_adapter.add_items(get_notes);
					note_list_adapter.notifyDataSetChanged();
				}else if((RequestCode.now_page-1)*RequestCode.account_page < notes.size()
						&& RequestCode.now_page*RequestCode.account_page > notes.size()){
					ArrayList<Note> get_notes = get_list_note(notes,(RequestCode.now_page-1)*RequestCode.account_page,notes.size());
					note_list_adapter.add_items(get_notes);
					note_list_adapter.notifyDataSetChanged();
				}else{
					mTipContent.setText("没有了");
				}
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
        switch (requestCode) {
            case MainActivity.RequestCode.EDIT_TEXT:
                load_list();
                break; 	
            case MainActivity.RequestCode.SHOW_BACK:
				Toast.makeText(MainActivity.this,notes.size() +" :  "
						+ RequestCode.now_page , 100).show();
            	load_list();
            	break;
        }
        Toast.makeText(MainActivity.this,notes.size() +" :  "
				+ RequestCode.now_page  + " -----------------", 200).show();
        note_list_adapter.notifyDataSetChanged(); 
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
		// 解除 和 更新笔记服务的绑定
		unbindService(conn);
		// 关闭更新联系人状态服务
		stopService(new Intent(MainActivity.this,RefreshContactStatusService.class));
		// 关闭更新对话串的服务
		stopService(new Intent(MainActivity.this,SynChatService.class));
		IndexService.stop();
		stopService(new Intent(MainActivity.this,FaceCommentService.class));
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
	                    
						
						note_list.post(new Runnable() {
							@Override
							public void run() {
//								System.out.println("ShowNodeActivity.isRefash = " + ShowNodeActivity.isRefash);
								if(ShowNodeActivity.isRefash){
									ShowNodeActivity.isRefash = false;
								}else if(isFirst){
									isFirst = false;
								}else{
									load_list();
								}	 
							}
						});
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
