package com.teamkn.activity.contact;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Contact;
import com.teamkn.model.database.ContactDBHelper;
import com.teamkn.pinyin4j.SideBar;
import com.teamkn.service.RefreshContactStatusService;
import com.teamkn.service.RefreshContactStatusService.RefreshContactStatusBinder;
import com.teamkn.widget.adapter.ContactListAdapter_update;

public class ContactsActivity extends TeamknBaseActivity implements  OnClickListener{
	View view_show;
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
    setContentView(R.layout.horz_scroll_with_image_menu);
    LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout_loading);

    LayoutInflater inflater = LayoutInflater.from(this);
    view_show = inflater.inflate(R.layout.contact_list, null);
    layout.addView(view_show);
    
    
    
    
    item = ContactDBHelper.Status.APPLIED;
    mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    findView();
    button_contact_list_linkman.setOnClickListener(this);
    button_contact_list_send_invite.setOnClickListener(this);
    button_contact_list_get_invite.setOnClickListener(this);
    import_contact();
  }
  private void findView(){	
	button_contact_list_linkman = (Button)view_show.findViewById(R.id.button_contact_list_linkman);
    button_contact_list_send_invite = (Button)view_show.findViewById(R.id.button_contact_list_send_invite);
    button_contact_list_get_invite = (Button)view_show.findViewById(R.id.button_contact_list_get_invite);
    
	contact_list_linkman = (View)view_show.findViewById(R.id.contact_list_linkman);
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
        
        // 启动刷新联系人状态服务
//     	startService(new Intent(ContactsActivity.this,RefreshContactStatusService.class));	
        
        
      }
    }.execute();
  }
    @Override
	protected void onResume() {
    	System.out.println("contactsActivity  bindService ------");
    	 startService(new Intent(ContactsActivity.this,RefreshContactStatusService.class));
    	 Intent intent = new Intent(ContactsActivity.this,RefreshContactStatusService.class);
    	 bindService(intent, conn, Context.BIND_AUTO_CREATE); 
		super.onResume();
	}
  // 添加适配器
  public void load_contacts_to_list(){
    int current_user_id = AccountManager.current_user().user_id;
    List<Contact> all_contacts = ContactDBHelper.build_all_contacts(current_user_id); 

    final ContactListAdapter_update adapter_update = 
    		new ContactListAdapter_update(this, 
    				all_contacts,item);
//    adapter.add_items(all_contacts);
    lvContact.post(new Runnable() {
      @Override
      public void run() {
    	  lvContact.setAdapter(adapter_update);
      }
    });
    
    lvContact.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
		          long id) {
			TextView info_tv = (TextView) view.findViewById(R.id.contactitem_gone);
			Contact contact = (Contact)  info_tv.getTag(R.id.tag_note_uuid);
			
	        Intent intent = new Intent(ContactsActivity.this,UserInfoActivity.class);
	        intent.putExtra(UserInfoActivity.Extra.USER_ID, contact.contact_user_id);
	        intent.putExtra(UserInfoActivity.Extra.USER_NAME, contact.contact_user_name);
	        intent.putExtra(UserInfoActivity.Extra.USER_AVATAR_BYTE, contact.contact_user_avatar);
	        intent.putExtra(UserInfoActivity.Extra.CONTACT_STATUS, contact.status);
	        startActivity(intent);
		}
	});
    lvContact.setOnItemLongClickListener(new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			if(item.equals(ContactDBHelper.Status.APPLIED)){
				TextView info_tv = (TextView) arg1.findViewById(R.id.contactitem_gone);
				Contact contact = (Contact)  info_tv.getTag(R.id.tag_note_uuid);
				dialog(contact);
			}
			return false;
		}
	});
  }
  protected void dialog(final Contact contact) {
	  AlertDialog.Builder builder = new Builder(ContactsActivity.this);
	  builder.setMessage("请确认删除联系人 ：  "+ contact.contact_user_name );
	  builder.setTitle("提示");
	  builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					if(BaseUtils.is_wifi_active(ContactsActivity.this)){
						HttpApi.Contact.remove_contact(contact.contact_user_id);
						load_contacts_to_list();
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	  });
	  builder.setNegativeButton("取消", null);
	  builder.create().show();
  }
  @Override
  protected void onDestroy() {
	stopService(new Intent(ContactsActivity.this,RefreshContactStatusService.class));
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
