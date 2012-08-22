package com.teamkn.activity.contact;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.model.Contact;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.pinyin4j.SideBar;
import com.teamkn.service.RefreshContactStatusService;
import com.teamkn.service.RefreshContactStatusService.RefreshContactStatusBinder;
import com.teamkn.widget.adapter.ContactListAdapter_update;

public class ContactsActivity extends TeamknBaseActivity implements  OnClickListener{
  private View contact_list_linkman;
  private SideBar indexBar;
  private WindowManager mWindowManager;
  private TextView mDialogText;
  private ListView lvContact;
  private RefreshContactStatusBinder refresh_contact_status_binder;
  private RefreshContactUiBinder refresh_contact_ui_binder = new RefreshContactUiBinder();
  
  private String item = null;
  private Button button_contact_list_linkman ;
  private Button button_contact_list_send_invite ;
  private Button button_contact_list_get_invite;
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
    item = ContactDBHelper.Status.APPLIED;
    mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    findView();
    button_contact_list_linkman.setOnClickListener(this);
    button_contact_list_send_invite.setOnClickListener(this);
    button_contact_list_get_invite.setOnClickListener(this);
    import_contact();
  }
  private void findView(){	
	button_contact_list_linkman = (Button)this.findViewById(R.id.button_contact_list_linkman);
    button_contact_list_send_invite = (Button)this.findViewById(R.id.button_contact_list_send_invite);
    button_contact_list_get_invite = (Button)this.findViewById(R.id.button_contact_list_get_invite);
    
	contact_list_linkman = (View)this.findViewById(R.id.contact_list_linkman);
	lvContact = (ListView)contact_list_linkman.findViewById(R.id.contact_list_lv);

  	indexBar = (SideBar) contact_list_linkman.findViewById(R.id.sideBar);  
    indexBar.setListView(lvContact); 
    
    mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null).findViewById(R.id.list_postion_tv);
    mDialogText.setVisibility(View.INVISIBLE);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
              LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
              WindowManager.LayoutParams.TYPE_APPLICATION,
              WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                      | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
              PixelFormat.TRANSLUCENT);
    try {
    	  
		mWindowManager.addView(mDialogText, lp);
		
	} catch (Exception e) {
		System.out.println("ｃｏｎｔａｃｔｓａｃｔｉｖｉｔｙ　　ｅｘｃｅｐｔｉｏｎ" +e.getMessage());
//		e.printStackTrace();
	}
      indexBar.setTextView(mDialogText);
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
        return null;
      }

      @Override
      public void on_success(Void resule) {
    	  load_contacts_to_list();
        Intent intent = new Intent(ContactsActivity.this,RefreshContactStatusService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
      }
    }.execute();
  }
  // 添加适配器
  public void load_contacts_to_list(){
    int current_user_id = AccountManager.current_user().user_id;
    List<Contact> all_contacts = ContactDBHelper.build_all_contacts(current_user_id); 
    
//    final ContactListAdapter adapter = new ContactListAdapter(ContactsActivity.this);
    
    final ContactListAdapter_update adapter_update = 
    		new ContactListAdapter_update(this, 
    				all_contacts,item);
//    adapter.add_items(all_contacts);
    
    lvContact.post(new Runnable() {
      @Override
      public void run() {
//        lvContact.setAdapter(adapter);
    	  lvContact.setAdapter(adapter_update);
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

@Override
public void onClick(View v) {
	Button button = (Button)v;
	switch (button.getId()) {
	case R.id.button_contact_list_linkman:
		item = ContactDBHelper.Status.APPLIED;
		load_contacts_to_list();
		break;
	case R.id.button_contact_list_send_invite:
		item = ContactDBHelper.Status.INVITED;
		load_contacts_to_list();
		break;
	case R.id.button_contact_list_get_invite:
		item = ContactDBHelper.Status.BE_INVITED;
		load_contacts_to_list();
		break;
	default:
		break;
	}
	
}

}
