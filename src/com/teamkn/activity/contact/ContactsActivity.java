package com.teamkn.activity.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.note.SearchActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.service.RefreshContactStatusService;

public class ContactsActivity extends TeamknBaseActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_list);
    
    if(TeamknPreferences.never_syn()){
      import_contact_from_server();      
    }
  }
  
  public void click_to_search_contact_page(View view){
    open_activity(SearchContactActivity.class);
  }
  
  private void import_contact_from_server(){
    new TeamknAsyncTask<Void, Void, Void>(ContactsActivity.this,"正在从服务器导入联系人信息") {
      @Override
      public Void do_in_background(Void... params) throws Exception {
        HttpApi.Contact.refresh_status();
        return null;
      }

      @Override
      public void on_success(Void result) {
        startService(new Intent(ContactsActivity.this,RefreshContactStatusService.class));
      }
    }.execute();
  }
}
