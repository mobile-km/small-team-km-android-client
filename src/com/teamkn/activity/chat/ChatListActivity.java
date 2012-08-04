package com.teamkn.activity.chat;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.HorzScrollWithListMenu;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.Chat;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.widget.adapter.ChatListAdapter;

public class ChatListActivity extends TeamknBaseActivity {
	 //menu菜单
	 MyHorizontalScrollView scrollView;
	 View foot_view;  //底层  图层 隐形部分
	 ImageView iv_foot_view;
	 
	 boolean menuOut = false;
	 Handler handler = new Handler();
	//	
    View chat_list;	
    
    
	  private ListView chat_list_lv;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	     // <<
 		 LayoutInflater inflater = LayoutInflater.from(this);
         setContentView(inflater.inflate(R.layout.horz_scroll_with_image_menu, null));

         scrollView = (MyHorizontalScrollView) findViewById(R.id.myScrollView);
         foot_view = findViewById(R.id.menu);    
 		
         chat_list = inflater.inflate(R.layout.chat_list, null);
         
         
         iv_foot_view = (ImageView) chat_list.findViewById(R.id.iv_foot_view);
         iv_foot_view.setOnClickListener(new HorzScrollWithListMenu.ClickListenerForScrolling(scrollView, foot_view));
         View transparent = new TextView(this);
         transparent.setBackgroundColor(android.R.color.transparent);

         final View[] children = new View[] { transparent, chat_list };
         int scrollToViewIdx = 1;
         scrollView.initViews(children, scrollToViewIdx, new HorzScrollWithListMenu.SizeCallbackForMenu(iv_foot_view));    
         //>>

	    chat_list_lv = (ListView)chat_list.findViewById(R.id.chat_list_lv);
	  }
	  
	  @Override
	  protected void onResume() {
	    List<Chat> chat_list = ChatDBHelper.find_list();
	    ChatListAdapter adapter = new ChatListAdapter(this);
	    adapter.add_items(chat_list);
	    chat_list_lv.setAdapter(adapter);
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
}
