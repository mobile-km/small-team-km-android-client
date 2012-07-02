package com.teamkn.activity.contact;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
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
import com.teamkn.service.RefreshContactStatusService.RefreshContactStatusBinder;
import com.teamkn.widget.adapter.ContactListAdapter;

public class ContactsActivity extends TeamknBaseActivity {
  private ListView contact_list_lv;
  private RefreshContactStatusBinder refresh_contact_status_binder;
  private RefreshContactUiBinder refresh_contact_ui_binder = new RefreshContactUiBinder();
  
  private ServiceConnection conn = new ServiceConnection(){

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      refresh_contact_status_binder = (RefreshContactStatusBinder)service;
      refresh_contact_status_binder.set_refresh_contact_ui_binder(refresh_contact_ui_binder);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      // 当 Service 因异常而断开连接的时候，这个方法才会被调用
      System.out.println("ServiceConnection  onServiceDisconnected");
      refresh_contact_status_binder = null;
    }
  };

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
        Intent intent = new Intent(ContactsActivity.this,RefreshContactStatusService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
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
  
  @Override
  protected void onDestroy() {
    unbindService(conn);
    super.onDestroy();
  }
  
  public class RefreshContactUiBinder extends Binder{
    public void refresh_list(){
      ContactsActivity.this.load_contacts_to_list();
    }
  }

}
