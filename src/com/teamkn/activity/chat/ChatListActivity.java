package com.teamkn.activity.chat;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.model.Chat;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.widget.adapter.ChatListAdapter;

public class ChatListActivity extends TeamknBaseActivity {
  private ListView chat_list_lv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chat_list);
    
    chat_list_lv = (ListView)findViewById(R.id.chat_list_lv);
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
