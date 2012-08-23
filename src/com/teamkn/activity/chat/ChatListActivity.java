package com.teamkn.activity.chat;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.model.Chat;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.widget.adapter.ChatListAdapter;

public class ChatListActivity extends TeamknBaseActivity{

     View view_show;	
     List<Chat> chat_list = null;
	 ChatListAdapter adapter = null;
    
	  private ListView chat_list_lv;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	 
        
        setContentView(R.layout.horz_scroll_with_image_menu);
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout_loading);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        view_show = inflater.inflate(R.layout.chat_list, null);
        layout.addView(view_show);

	    chat_list_lv = (ListView)view_show.findViewById(R.id.chat_list_lv);
	    
	    load_listview();
	  }
	  @Override
	protected void onResume() {
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	  private void load_listview() {
		    adapter = new ChatListAdapter(ChatListActivity.this);
		    chat_list = ChatDBHelper.find_list();

			new TeamknAsyncTask<Void, Void, Void>(ChatListActivity.this,"加载中...") {

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
	}

	  public void click_new_chat_bn(View view){
	    open_activity(SelectChatMemberActivity.class);
	  }
	  
	// 钩子，自行重载
		public void on_go_back() {
			open_activity(ChatListActivity.class);
		};

}
