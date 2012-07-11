package com.teamkn.activity.chat;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Chat;
import com.teamkn.model.Contact;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.widget.adapter.SelectChatMemberListAdapter;

public class SelectChatMemberActivity extends TeamknBaseActivity {
  private List<Integer> select_chat_member_server_user_ids;
  private ListView select_chat_member_lv;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.select_chat_member);
    select_chat_member_server_user_ids = new ArrayList<Integer>();
    select_chat_member_lv = (ListView)findViewById(R.id.select_chat_member_lv);
    load_select_chat_member_list();
    
  }
  
  private void load_select_chat_member_list() {
    new TeamknAsyncTask<Void, Void, SelectChatMemberListAdapter>(this,"载入联系人") {

      @Override
      public SelectChatMemberListAdapter do_in_background(Void... params) throws Exception {
        List<Contact> list = ContactDBHelper.applied_contacts(current_user().user_id);
        SelectChatMemberListAdapter adapter = new SelectChatMemberListAdapter(SelectChatMemberActivity.this,select_chat_member_server_user_ids);
        adapter.add_items(list);
        return adapter;
      }

      @Override
      public void on_success(final SelectChatMemberListAdapter adapter) {
        select_chat_member_lv.setOnItemClickListener(new OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> arg0, View convertView, int arg2,
              long arg3) {
            adapter.select_item(convertView);
          }
        });
        select_chat_member_lv.setAdapter(adapter);
      }

    }.execute();
  }

  public void click_submit_select_chat_member(View view){
    if(select_chat_member_server_user_ids.size() == 0){
      BaseUtils.toast("xxx");
      return;
    }
    
    new TeamknAsyncTask<Void,Void,Integer>(this,"正在创建") {

      @Override
      public Integer do_in_background(Void... params) throws Exception {
        List<Integer> server_user_id_list = select_chat_member_server_user_ids;
        Chat chat = ChatDBHelper.create(server_user_id_list);
        if(BaseUtils.is_wifi_active(SelectChatMemberActivity.this)){
          HttpApi.Chat.create(chat.uuid,server_user_id_list);
        }
        return chat.id;
      }

      @Override
      public void on_success(Integer client_chat_id) {
        Intent intent = new Intent(SelectChatMemberActivity.this,ChatActivity.class);
        intent.putExtra(ChatActivity.Extra.CLIENT_CHAT_ID, (int)client_chat_id);
        startActivity(intent);
        SelectChatMemberActivity.this.finish();
      }
      
    }.execute();
  }
}
