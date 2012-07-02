package com.teamkn.service;

import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.activity.contact.ContactsActivity.RefreshContactUiBinder;
import com.teamkn.service.RefreshContactStatusService.RefreshContactStatusBinder;
import com.teamkn.service.RefreshContactStatusService.RefreshContactStatusHandler;
import com.teamkn.service.RefreshContactStatusService.RefreshContactStatusHandlerThread;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

public class RefreshContactStatusService extends Service {

  private RefreshContactStatusHandlerThread refresh_contact_status_handler_thread;
  private RefreshContactStatusHandler refresh_contact_status_handler;
  private RefreshContactStatusBinder refresh_contact_status_binder;
  public RefreshContactUiBinder refresh_contact_ui_binder;

  @Override
  public IBinder onBind(Intent intent) {
    return refresh_contact_status_binder;
  }
  
  @Override
  public void onCreate() {
    System.out.println("RefreshContactStatusService create");
    refresh_contact_status_binder = new RefreshContactStatusBinder();
    refresh_contact_status_handler_thread = new RefreshContactStatusHandlerThread();
    refresh_contact_status_handler_thread.start();
    refresh_contact_status_handler = new RefreshContactStatusHandler(refresh_contact_status_handler_thread);
    super.onCreate();
  }
  
  @Override
  public void onDestroy() {
    System.out.println("refresh_contact_status_service  destroy");
    refresh_contact_status_handler_thread.quit();
    super.onDestroy();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    System.out.println("refresh_contact_status_service  on start command");
    if(!TeamknPreferences.never_syn()){
      System.out.println("start loop");
      refresh_contact_status_handler.sendEmptyMessage(RefreshContactStatusHandler.REFRESH_STATUS);
    }
    System.out.println("refresh_contact_status_service  on start command end");
    return super.onStartCommand(intent, flags, startId);
  }

  public class RefreshContactStatusHandlerThread extends HandlerThread {
    public RefreshContactStatusHandlerThread() {
      super("更新联系人信息的工作线程");
    }
  }

  public class RefreshContactStatusHandler extends Handler {
    public static final int REFRESH_STATUS = 0;

    public RefreshContactStatusHandler(
        RefreshContactStatusHandlerThread refresh_contact_status_handler_thread) {
      super(refresh_contact_status_handler_thread.getLooper());
    }

    @Override
    public void handleMessage(Message msg) {
      refresh_contact_status();
      super.handleMessage(msg);
    }

    private void refresh_contact_status() {
      try {
        System.out.println("services  refresh_contact_status");
        HttpApi.Contact.refresh_status();
        if(refresh_contact_ui_binder !=null){
          refresh_contact_ui_binder.refresh_list();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }finally{
        sendEmptyMessageDelayed(REFRESH_STATUS, 5*1000);
        System.out.println("send delayed refresh_status");
      }
    }
  }
  
  public class RefreshContactStatusBinder extends Binder{
    public void set_refresh_contact_ui_binder(RefreshContactUiBinder refresh_contact_ui_binder){
      RefreshContactStatusService.this.refresh_contact_ui_binder = refresh_contact_ui_binder;
    }
  }

}
