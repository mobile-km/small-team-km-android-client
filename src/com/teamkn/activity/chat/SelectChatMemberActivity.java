package com.teamkn.activity.chat;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Contact;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.widget.adapter.SelectChatMemberListAdapter;

public class SelectChatMemberActivity extends TeamknBaseActivity {
  private List<Integer> select_chat_member_ids;
  private ListView select_chat_member_lv;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.select_chat_member);
    select_chat_member_ids = new ArrayList<Integer>();
    select_chat_member_lv = (ListView)findViewById(R.id.select_chat_member_lv);
    load_select_chat_member_list();
    
  }
  
  private void load_select_chat_member_list() {
    new TeamknAsyncTask<Void, Void, SelectChatMemberListAdapter>(this,"载入联系人") {

      @Override
      public SelectChatMemberListAdapter do_in_background(Void... params) throws Exception {
        List<Contact> list = ContactDBHelper.applied_contacts(current_user().user_id);
        SelectChatMemberListAdapter adapter = new SelectChatMemberListAdapter(SelectChatMemberActivity.this,select_chat_member_ids);
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
    new TeamknAsyncTask<Void,Void,Void>(this,"正在创建") {

      @Override
      public Void do_in_background(Void... params) throws Exception {
        List<Integer> user_list = select_chat_member_ids;
        // TODO
        return null;
      }

      @Override
      public void on_success(Void result) {
      }
      
    }.execute();
  }
}
