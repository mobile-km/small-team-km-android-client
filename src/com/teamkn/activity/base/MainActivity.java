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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.activity.base.slidingmenu.HorzScrollWithListMenu;
import com.teamkn.activity.base.slidingmenu.HorzScrollWithListMenu.ClickListenerForScrolling;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.activity.contact.ContactsActivity;
import com.teamkn.activity.note.EditNoteActivity;
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

public class MainActivity extends TeamknBaseActivity implements OnGestureListener  {
	 private GestureDetector detector;
	 //menu菜单
	 MyHorizontalScrollView scrollView;
	 View base_main;
	 View foot_view;  //底层  图层 隐形部分
	 ImageView iv_foot_view;
	 
	 boolean menuOut = false;
	//
	public class RequestCode {
        public final static int EDIT_TEXT = 0;
    }
	//  node_listView_show 数据
	private static ListView note_list;
	// 定义每一页显示行数
    private  int VIEW_COUNT = 20;  
    // 定义的页数
    private  int index = 0;   
    // 当前页
    private  int currentPage = 1;     
    // 所以数据的条数
    private  int  totalCount;     
    // 每次取的数据，只要最后一次可能不一样。
    
    NoteListAdapter note_list_adapter;
    List<Note> notes;
    // 标记：上次的ID
    private  boolean isUpdating = true;
    
    View view;
    
    private LinearLayout mLoadLayout;   

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
		detector = new GestureDetector(this);
        // <<
		LayoutInflater inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));

        scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
        foot_view = findViewById(R.id.menu);    
        RelativeLayout foot_rl_node = (RelativeLayout)findViewById(R.id.foot_rl_node);

        base_main = inflater.inflate(R.layout.base_main, null);
        
        
        iv_foot_view = (ImageView) base_main.findViewById(R.id.iv_foot_view);
   
        new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view);
        HorzScrollWithListMenu.menuOut = false;
        iv_foot_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_menuOut();
			}
		});
        foot_rl_node.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_menuOut();
			}
		});
        
        
        View transparent = new TextView(this);
        transparent.setBackgroundColor(android.R.color.transparent);
        final View[] children = new View[] { transparent, base_main };
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new HorzScrollWithListMenu.SizeCallbackForMenu(iv_foot_view));    
       
        
        //>>
	    mLoadLayout = new LinearLayout(this);   
        mLoadLayout.setMinimumHeight(40);   
        mLoadLayout.setGravity(Gravity.CENTER);   
        mLoadLayout.setOrientation(LinearLayout.HORIZONTAL);   

        ProgressBar mProgressBar = new ProgressBar(this);   
        mProgressBar.setPadding(0, 0, 15, 0);   
        mLoadLayout.addView(mProgressBar, mProgressBarLayoutParams);             
        TextView mTipContent = new TextView(this);   
        mTipContent.setText("加载中...");   
        mLoadLayout.addView(mTipContent, mProgressBarLayoutParams);   
        mLoadLayout.setVisibility(View.GONE);   
		
		
		
		data_syn_textview = (TextView)base_main.findViewById(R.id.main_data_syn_text);
		data_syn_progress_bar = (ProgressBar)base_main.findViewById(R.id.main_data_syn_progress_bar);
		progress_set_num = (TextView)findViewById(R.id.progress_set_num);
		manual_syn_bn = (ImageView)base_main.findViewById(R.id.manual_syn_bn);
		
		// 注册更新服务
		Intent intent = new Intent(MainActivity.this,SynNoteService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		   
		// 开始后台索引服务
		IndexService.start(this);
		IndexTimerTask.index_task(IndexTimerTask.SCHEDULE_INTERVAL);
		
		// 注册更新表情反馈服务
		Intent intent1 = new Intent(MainActivity.this,FaceCommentService.class);
		startService(intent1);
		SharedParam.saveParam(this, 0);
		FaceCommentService.context = this;
    
		// 设置用户头像和名字
		AccountUser user = current_user();
		byte[] avatar = user.avatar;
		String name = current_user().name;
		RelativeLayout rl = (RelativeLayout)base_main.findViewById(R.id.main_user_avatar);
		if(avatar != null){
			Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(avatar));
			Drawable drawable = new BitmapDrawable(bitmap);
			rl.setBackgroundDrawable(drawable);
		}else{
		    rl.setBackgroundResource(R.drawable.user_default_avatar_normal);
		}
	    TextView user_name_tv = (TextView)base_main.findViewById(R.id.main_user_name);
	    user_name_tv.setText(name);
    
		// 启动刷新联系人状态服务
		startService(new Intent(MainActivity.this,RefreshContactStatusService.class));
		// 启动更新 对话串的服务
		startService(new Intent(MainActivity.this,SynChatService.class));
		
		//加载node_listview
		load_list();
		
	}
	private int getMaxResult() {
        int totalPage = (totalCount + VIEW_COUNT - 1) / VIEW_COUNT;
        if(currentPage == totalPage){
        	 return totalCount - (totalPage - 1) * VIEW_COUNT;
        }
        return VIEW_COUNT;
    }
	//加载node_listview
	private void load_list() {
		// 定义的页数
	    index = 0;   
	    // 当前页
	    currentPage = 1;  
	    
		note_list = (ListView)base_main.findViewById(R.id.note_list);
		
		notes  = new ArrayList<Note>();
		note_list_adapter = new NoteListAdapter(MainActivity.this);
		new TeamknAsyncTask<Void, Void, Void>() {
			@Override
			public Void do_in_background(Void... params) throws Exception {
				totalCount = NoteDBHelper.getCount();
				notes=NoteDBHelper.getAllItems(index, getMaxResult());
				System.out.println("************************* " + index + " : " + getMaxResult() + " : " + notes.size());
				return null;
			}
			@Override
			public void on_success(Void result) {
//				note_list.removeAllViews();
		        note_list_adapter.add_items(notes);
		        note_list.setAdapter(note_list_adapter);
			}
		}.execute();

        note_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list_view,View list_item,int item_id,long position) {
            	
                TextView info_tv = (TextView) list_item.findViewById(R.id.note_info_tv);
                String   uuid    = (String)   info_tv.getTag(R.id.tag_note_uuid);
                String   kind    = (String)   info_tv.getTag(R.id.tag_note_kind);
                
                Intent   intent  = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.Extra.NOTE_UUID, uuid);
                intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, kind);
                
                if (kind == NoteDBHelper.Kind.IMAGE) {
                    String image_path = Note.note_image_file(uuid).getPath();
                    intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH,image_path);
                }
                startActivityForResult(intent,MainActivity.RequestCode.EDIT_TEXT);
            }
        });

        
        
        note_list.setOnScrollListener(new OnScrollListener() {			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
         	   
         	  if (firstVisibleItem + visibleItemCount == totalItemCount && isUpdating) {
                  if (currentPage < (totalCount/VIEW_COUNT+1)) { // 防止最后一次取数据进入死循环。
                	  System.out.println(totalItemCount + " : " + totalCount + " : " + isUpdating  + " : " + currentPage );
                	  isUpdating=false ;
                	  ++currentPage;
 
                	  note_list.addFooterView(mLoadLayout);
                	  mLoadLayout.setVisibility(View.VISIBLE);
                	  AsyncUpdateDatasTask asyncUpdateWeiBoDatasTask = new AsyncUpdateDatasTask();
                      asyncUpdateWeiBoDatasTask.execute();   
                  }
              }  
			}
		});
    }
	class AsyncUpdateDatasTask extends AsyncTask<Void, Void, List<Note> > {
		 
        @Override
        protected List<Note> doInBackground(Void... params) {
            index += VIEW_COUNT;
            List<Note> list = new ArrayList<Note>();
            try {
				list = NoteDBHelper.getAllItems(index, getMaxResult());
			} catch (Exception e) {
				e.printStackTrace();
			}
            System.out.println("doInBackground  : " + list.size());
            return list;
        }
        @Override
        protected void onPostExecute(List<Note> noteOnther) {
            super.onPostExecute(noteOnther);
            note_list_adapter.add_items(noteOnther);
            note_list_adapter.notifyDataSetChanged();
            isUpdating=true;
            
            mLoadLayout.setVisibility(View.GONE); 
            note_list.removeFooterView(mLoadLayout);
        }
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
        }
//        open_activity(MainActivity.class);
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
	
	public void click_headbar_button_contacts(View view){
	    open_activity(ContactsActivity.class);
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
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.menu_about:
//			open_activity(AboutActivity.class);
//			break;
//		case R.id.menu_setting:
//			open_activity(TeamknSettingActivity.class);
//			break;
//		case R.id.menu_account_management:
//			open_activity(AccountManagerActivity.class);
//			break;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}

	 public class SynUIBinder{
	    public void set_max_num(int max_num){
	      final int num = max_num;
	      System.out.println("set_max_num   " + max_num);
	      data_syn_progress_bar.post(new Runnable() {
	          @Override
	          public void run() {
	            data_syn_progress_bar.setMax(num);
	          }
          });
	    }
	    
	    public void set_start_syn(){
	      System.out.println("set_start_syn");
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
	      System.out.println("set_progress  " + progress);
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
			System.out.println("syn_success");
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
							load_list(); 
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
        System.out.println("syn_fail");
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
	 
	    /** 
	     * 监听滑动 
	     */
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
		// // 滑动一段距离，up时触发，e1为down时的MotionEvent，e2为up时的MotionEvent  
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
	        return false;  
		}
		@Override
		public void onLongPress(MotionEvent e) {	
		}
		boolean is_out = false;
		int i = 0 ;
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			if (e1.getX() - e2.getX() > 120 && !is_out) {  //向左滑动 
				is_menuOut();
	        }else if(e1.getX() - e2.getX() < -120  && is_out ){
	        	is_menuOut();
	        }
			return true;
		}
		public void is_menuOut(){
			i++;
			is_out = !is_out;
			ClickListenerForScrolling.flag_show_menu_move();
			System.out.println(i+" mainActivity.java is_out : menuOut "+ is_out + " : " + HorzScrollWithListMenu.menuOut);	
		}
		
		@Override
		public void onShowPress(MotionEvent e) {	
		}
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		@Override 
		public boolean onTouchEvent(MotionEvent event) { 
			return this.detector.onTouchEvent(event); 
		}
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
		   this.detector.onTouchEvent(ev);
		   return super.dispatchTouchEvent(ev);
		}		
}
