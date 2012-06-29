package com.teamkn.activity.contact;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.model.Contact;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.service.RefreshContactStatusService;
import com.teamkn.widget.adapter.ContactListAdapter;

public class ContactsActivity extends TeamknBaseActivity {
  private ListView contact_list_lv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_list);
    contact_list_lv = (ListView)findViewById(R.id.contact_list_lv);
    
    import_contact();      
  }
  
  public void click_to_search_contact_page(View view){
    open_activity(SearchContactActivity.class);
  }
  
  private void import_contact(){
    new TeamknAsyncTask<Void, Void, Void>(ContactsActivity.this,"正在读取联系人信息") {

      @Override
      public Void do_in_background(Void... params) throws Exception {
        if(TeamknPreferences.never_syn()){
          HttpApi.Contact.refresh_status();
        }
        load_contacts_to_list();
        return null;
      }

      @Override
      public void on_success(Void resule) {
        startService(new Intent(ContactsActivity.this,RefreshContactStatusService.class));
      }
    }.execute();
  }
  
  public void load_contacts_to_list(){
    int current_user_id = AccountManager.current_user().user_id;
    List<Contact> all_contacts = ContactDBHelper.build_all_contacts(current_user_id); 
    final ContactListAdapter adapter = new ContactListAdapter(ContactsActivity.this);
    adapter.add_items(all_contacts);
    
    contact_list_lv.post(new Runnable() {
      @Override
      public void run() {
        contact_list_lv.setAdapter(adapter);
      }
    });
  }

}
