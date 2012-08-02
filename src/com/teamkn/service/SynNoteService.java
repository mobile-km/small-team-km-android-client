package com.teamkn.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.HttpApi.NetworkUnusableException;
import com.teamkn.Logic.HttpApi.ServerErrorException;
import com.teamkn.Logic.NoteMeta;
import com.teamkn.Logic.NoteMetaMerge;
import com.teamkn.Logic.TeamknPreferences;
import com.teamkn.activity.base.MainActivity.SynUIBinder;
import com.teamkn.base.utils.BaseUtils;
import java.util.List;

public class SynNoteService extends Service {
  public SynUIBinder syn_ui_binder;
  private SynNoteBinder syn_note_binder;
  private SynNoteHandlerThread syn_note_thread;
  private SynNoteHandler syn_note_handler;
  
  @Override
  public void onCreate() {
    syn_note_binder = new SynNoteBinder();
    syn_note_thread = new SynNoteHandlerThread();
    syn_note_thread.start();
    syn_note_handler = new SynNoteHandler(syn_note_thread);
    super.onCreate();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return syn_note_binder;
  }
  
  @Override
  public boolean onUnbind(Intent intent) {
    return super.onUnbind(intent);
  }
  
  @Override
  public void onDestroy() {
    syn_note_handler.set_cancel_flag();
    syn_note_thread.quit();
    super.onDestroy();
  }

  public class SynNoteBinder extends Binder {
    public void set_syn_ui_binder(SynUIBinder syn_ui_binder) {
      SynNoteService.this.syn_ui_binder = syn_ui_binder;
    }

    public void start() {
      syn_note_handler.sendEmptyMessage(SynNoteHandler.SYN_MESSAGE);
    }
    
    public void manual_syn(){
      if(syn_note_handler.has_syning()){
        return;
      }
      syn_note_handler.removeMessages(SynNoteHandler.SYN_MESSAGE);
      syn_note_handler.sendEmptyMessage(SynNoteHandler.SYN_MESSAGE);
    }
  }

  public class SynNoteHandlerThread extends HandlerThread{
    public SynNoteHandlerThread() {
      super("同步笔记的工作线程");
    }
  }
  
  public class SynNoteHandler extends Handler{
    public static final int SYN_MESSAGE = 0;
    private boolean cancel_flag = false;
    private boolean syning_flag = false;

    public SynNoteHandler(SynNoteHandlerThread syn_note_thread) {
      super(syn_note_thread.getLooper());
    }
    
    public void check_cancel_flag() throws Exception{
      if(this.cancel_flag){
        throw new Exception();
      }
    }
    
    public void set_cancel_flag(){
      this.cancel_flag = true;
    }
    
    public boolean has_syning(){
      return this.syning_flag;
    }
    
    public void set_syning_flag(boolean flag){
      this.syning_flag = flag;
    }

    @Override
    public void handleMessage(Message msg) {
      check_network();
      super.handleMessage(msg);
    }
    
    public void check_network(){
      System.out.println(BaseUtils.is_wifi_active(SynNoteService.this));
      if(BaseUtils.is_wifi_active(SynNoteService.this)){
        start_syn();
      }else{
        syn_ui_binder.set_syn_fail();
        sendEmptyMessageDelayed(SYN_MESSAGE, 15*1000);
      }
    }
    
    public void start_syn(){
      try {
        set_syning_flag(true);
        System.out.println("SynNoteHandler handleMessage " + Thread.currentThread());
        
        NoteMetaMerge merge = HttpApi.Syn.detail_meta();
        List<NoteMeta> merge_list = merge.get_merge_list();
        
        syn_ui_binder.set_max_num(merge_list.size());
        int index = 0;
        syn_ui_binder.set_start_syn();
        
        long last_syn_success_server_time = 0;
        for (int i = 0; i < merge_list.size(); i++) {
          check_cancel_flag();
          NoteMeta note_meta = merge_list.get(i);
          last_syn_success_server_time = note_meta.syn();
          index+=1;
          syn_ui_binder.set_progress(index);
        }
        TeamknPreferences.set_last_syn_server_meta_updated_time(merge.last_syn_server_meta_updated_time);
        TeamknPreferences.set_last_syn_success_server_time(last_syn_success_server_time);
        TeamknPreferences.touch_last_syn_success_client_time();
        
        syn_ui_binder.set_syn_success();
        sendEmptyMessageDelayed(SYN_MESSAGE, 60*60*1000);
      }catch(ServerErrorException see){
        see.printStackTrace();
        syn_ui_binder.set_syn_fail();
        sendEmptyMessageDelayed(SYN_MESSAGE, 30*60*1000);
      }catch(NetworkUnusableException nue){
        nue.printStackTrace();
        syn_ui_binder.set_syn_fail();
        sendEmptyMessageDelayed(SYN_MESSAGE, 15*1000);
      }catch (Exception e) {
        e.printStackTrace();
        syn_ui_binder.set_syn_fail();
        sendEmptyMessageDelayed(SYN_MESSAGE, 15*1000);
      }finally{
        set_syning_flag(false);
      }
    }
  }
}
