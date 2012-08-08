package com.teamkn.activity.chat;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.HorzScrollWithListMenu;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.model.Chat;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.widget.adapter.ChatListAdapter;

public class ChatListActivity extends TeamknBaseActivity  implements OnGestureListener  {
	private GestureDetector detector;
	 //menu菜单
	 MyHorizontalScrollView scrollView;
	 View foot_view;  //底层  图层 隐形部分
	 ImageView iv_foot_view;
	 
	 boolean menuOut = false;
	 Handler handler = new Handler();
	  //	
       View chat_listview;	
      List<Chat> chat_list = null;
	   ChatListAdapter adapter = null;
    
	  private ListView chat_list_lv;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    detector = new GestureDetector(this);
	     // <<
 		 LayoutInflater inflater = LayoutInflater.from(this);
         setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));

         scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
         foot_view = findViewById(R.id.menu);    
         RelativeLayout foot_rl_chat = (RelativeLayout)findViewById(R.id.foot_rl_chat);
         chat_listview = inflater.inflate(R.layout.chat_list, null);
         
         
         iv_foot_view = (ImageView) chat_listview.findViewById(R.id.iv_foot_view);
         iv_foot_view.setOnClickListener(new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view));
         foot_rl_chat.setOnClickListener(new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view));
       
         View transparent = new TextView(this);
         transparent.setBackgroundColor(android.R.color.transparent);

         final View[] children = new View[] { transparent, chat_listview };
         int scrollToViewIdx = 1;
         scrollView.initViews(children, scrollToViewIdx, new HorzScrollWithListMenu.SizeCallbackForMenu(iv_foot_view));    
         //>>

	    chat_list_lv = (ListView)chat_listview.findViewById(R.id.chat_list_lv);
	  }
	  
	  @Override
	  protected void onResume() {  
		adapter = new ChatListAdapter(ChatListActivity.this);
		new TeamknAsyncTask<Void, Void, Void>() {

			@Override
			public Void do_in_background(Void... params) throws Exception {
				chat_list = ChatDBHelper.find_list();
				return null;
			}
			@Override
			public void on_success(Void result) {
			    adapter.add_items(chat_list);
			    chat_list_lv.setAdapter(adapter);
			}
		}.execute();
	    
	    chat_list_lv.setOnItemClickListener(new OnItemClickListener() {
	      @Override
	      public void onItemClick(AdapterView<?> arg0, View item, int arg2,
	          long arg3) {
	        TextView tv = (TextView)item.findViewById(R.id.chat_id_tv);
	        Integer chat_id = (Integer)tv.getTag();
	        Intent intent = new Intent(ChatListActivity.this,ChatActivity.class);
	        intent.putExtra(ChatActivity.Extra.CLIENT_CHAT_ID, chat_id);
	        startActivity(intent);
	      }
	    });
	    super.onResume();
	  }
	  
	  public void click_new_chat_bn(View view){
	    open_activity(SelectChatMemberActivity.class);
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
			System.out.println("----------------  "  + e1.getX() + " : " + e2.getX());
			if (e1.getX() - e2.getX() > 120) {  //向左滑动 
	            HorzScrollWithListMenu.MyOnGestureListener.flag_show_menu(scrollView, foot_view);
	        } else if (e1.getX() - e2.getX() < -120) {  //向右滑动
	        	HorzScrollWithListMenu.MyOnGestureListener.flag_show_menu(scrollView, foot_view);
	        }  
	        return true;  
		}
		@Override
		public void onLongPress(MotionEvent e) {	
		}
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			return false;
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
