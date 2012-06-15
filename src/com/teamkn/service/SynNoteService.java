package com.teamkn.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.base.MainActivity.SynUIBinder;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

public class SynNoteService extends Service {
  public SynUIBinder syn_ui_binder;
  private SynNoteBinder syn_note_binder;
  private SynNoteHandlerThread syn_note_thread;
  private SynNoteHandler syn_note_handler;
  
  @Override
  public void onCreate() {
    System.out.println("SynNoteService class  onCreate");
    System.out.println("SynNoteService class  onCreate Thread" + Thread.currentThread());
    syn_note_binder = new SynNoteBinder();
    syn_note_thread = new SynNoteHandlerThread();
    syn_note_thread.start();
    syn_note_handler = new SynNoteHandler(syn_note_thread);
    super.onCreate();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    System.out.println("SynNoteService class  onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public IBinder onBind(Intent intent) {
    System.out.println("SynNoteService class  onBind");
    System.out.println("SynNoteService class  onBind Thread" + Thread.currentThread());
    return syn_note_binder;
  }
  
  @Override
  public boolean onUnbind(Intent intent) {
    System.out.println("SynNoteService class  onUnbind");
    System.out.println("SynNoteService class  onUnbind Thread" + Thread.currentThread());
    return super.onUnbind(intent);
  }
  
  @Override
  public void onDestroy() {
    System.out.println("SynNoteService class  onDestroy");
    System.out.println("SynNoteService class  onDestroy Thread" + Thread.currentThread());
    syn_note_thread.set_cancel_flag();
    syn_note_thread.quit();
    super.onDestroy();
  }

  public class SynNoteBinder extends Binder {
    public void set_syn_ui_binder(SynUIBinder syn_ui_binder) {
      System.out.println("SynNoteBinder set_syn_ui_binder ");
      SynNoteService.this.syn_ui_binder = syn_ui_binder;
    }

    public void start() {
      System.out.println("SynNoteBinder start ");
      System.out.println("SynNoteBinder start Thread" + Thread.currentThread());
      syn_note_handler.sendEmptyMessage(SynNoteHandler.SYN_MESSAGE);
    }
  }

  public class SynNoteHandlerThread extends HandlerThread{
    private boolean cancel_flag = false;

    public SynNoteHandlerThread() {
      super("同步笔记的工作线程");
    }
    
    public void set_cancel_flag(){
      this.cancel_flag = true;
    }
    
    public boolean has_cancel_flag(){
      return this.cancel_flag;
    }
  }
  
  public class SynNoteHandler extends Handler{
    public static final int SYN_MESSAGE = 0;
    private SynNoteHandlerThread syn_note_thread;

    public SynNoteHandler(SynNoteHandlerThread syn_note_thread) {
      super(syn_note_thread.getLooper());
      this.syn_note_thread = syn_note_thread;
    }
    
    public void check_cancel_flag() throws Exception{
      if(syn_note_thread.has_cancel_flag()){
        throw new Exception();
      }
    }

    @Override
    public void handleMessage(Message msg) {
      try {
        System.out.println("SynNoteHandler handleMessage " + Thread.currentThread());
        HashMap<String,Object> map = HttpApi.Syn.handshake();
        String uuid = (String)map.get("syn_task_uuid");
        int server_count = (Integer)map.get("note_count");
        int unsyn_count = NoteDBHelper.unsyn_count();
        int count = server_count + unsyn_count;
        
        syn_ui_binder.set_max_num(count);
        int index = 0;
        syn_ui_binder.set_start_syn();
        List<Note> list = NoteDBHelper.all(true);
        for (Iterator<Note> iterator = list.iterator(); iterator.hasNext();) {
          check_cancel_flag();
          Note note = iterator.next();
          HttpApi.Syn.compare(uuid, note);
          index+=1;
          syn_ui_binder.set_progress(index);
        }
        boolean has_next = true; 
        while(has_next){
          check_cancel_flag();
          has_next = HttpApi.Syn.syn_next(uuid);
          index+=1;
          syn_ui_binder.set_progress(index);
        }
        syn_ui_binder.set_syn_success();
      } catch (Exception e) {
        syn_ui_binder.set_syn_fail();
      }
      super.handleMessage(msg);
    }
  }
}
