package com.teamkn.activity.contact;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.SearchUser;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.widget.adapter.SearchUserListAdapter;

public class SearchContactActivity extends TeamknBaseActivity {
  private EditText search_contact_et;
  private ListView search_user_lv;
  private SearchUserListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.search_contact);
    search_contact_et = (EditText)findViewById(R.id.search_contact_et);
    search_user_lv = (ListView)findViewById(R.id.search_user_list);
    search_user_lv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        SearchUser user = (SearchUser)adapter.getItem(position);
        Intent intent = new Intent(SearchContactActivity.this,UserInfoActivity.class);
        intent.putExtra(UserInfoActivity.Extra.USER_ID, user.user_id);
        intent.putExtra(UserInfoActivity.Extra.USER_NAME, user.user_name);
        intent.putExtra(UserInfoActivity.Extra.USER_AVATAR_URL, user.user_avator_url);
        intent.putExtra(UserInfoActivity.Extra.CONTACT_STATUS, user.contact_status);
        startActivity(intent);
      }
    });
  }
  
  public void click_search_contact_bn(View view){
    String query = search_contact_et.getText().toString();
    if("".equals(query)){return;}
    
    new TeamknAsyncTask<String, Void, List<SearchUser>>() {
      @Override
      public List<SearchUser> do_in_background(String... params) throws Exception {
        String query = params[0];
        List<SearchUser> list = HttpApi.Contact.search(query);
        return list;
      }

      @Override
      public void on_success(List<SearchUser> list) {
        adapter = new SearchUserListAdapter(SearchContactActivity.this);
        adapter.add_items(list);
        search_user_lv.setAdapter(adapter);
      }
    }.execute(query);
  }
}
