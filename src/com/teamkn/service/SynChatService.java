package com.teamkn.service;

import java.util.List;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

public class SynChatService extends Service {

  private SynChatHanderThread syn_chat_handler_thread;
  private SynChatHandler syn_chat_handler;

  @Override
  public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public void onCreate() {
    syn_chat_handler_thread = new SynChatHanderThread();
    syn_chat_handler_thread.start();
    syn_chat_handler = new SynChatHandler(syn_chat_handler_thread);
    super.onCreate();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    syn_chat_handler.sendEmptyMessage(SynChatHandler.SYN_MESSAGE);
    return super.onStartCommand(intent, flags, startId);
  }
  
  @Override
  public void onDestroy() {
    System.out.println("syn_chat_service destroy");
    syn_chat_handler.removeMessages(SynChatHandler.SYN_MESSAGE);
    syn_chat_handler_thread.quit();
    super.onDestroy();
  }
  
  public class SynChatHanderThread extends HandlerThread{
    public SynChatHanderThread() {
      super("同步对话串的工作线程");
    }
  }
  
  public class SynChatHandler extends Handler{
    public static final int SYN_MESSAGE = 0;
    
    public SynChatHandler(SynChatHanderThread syn_chat_handler_thread){
      super(syn_chat_handler_thread.getLooper());
    }
    
    @Override
    public void handleMessage(Message msg) {
      try {
        System.out.println("syn_chat_and_chat_node");
        syn_chat_and_chat_node();
      } catch (Exception e) {
        e.printStackTrace();
      }finally{
        sendEmptyMessageDelayed(SYN_MESSAGE, 15*1000);
      }
      super.handleMessage(msg);
    }

    private void syn_chat_and_chat_node() throws Exception {
      push_data();
      pull_data();
    }

    private void pull_data() throws Exception {
      HttpApi.Chat.pull_chats();
      HttpApi.ChatNode.pull_chat_nodes();
    }

    private void push_data() throws Exception {
      int current_user_id = AccountManager.current_user().user_id;
      // 查询没有同步过的 chat
      List<Chat> chat_list = ChatDBHelper.find_unsyn_list();
      // 同步chat
      for(Chat chat : chat_list){
        List<Integer> user_list = chat.server_user_id_list;
        user_list.remove((Integer)current_user_id);
        HttpApi.Chat.create(chat.uuid, user_list);
      }
      // 查询没有同步过的 chat_node
      List<ChatNode> chat_node_list = ChatNodeDBHelper.find_unsyn_list();
      // 同步 chat_node
      for(ChatNode chat_node : chat_node_list){
        Chat chat = ChatDBHelper.find(chat_node.chat_id);
        HttpApi.ChatNode.create(chat_node.uuid, chat.server_chat_id, chat_node.content);
      }
    }
  }
}
