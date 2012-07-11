package com.teamkn.activity.chat;

import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper;
import com.teamkn.widget.adapter.ChatNodeListAdapter;

public class ChatActivity extends TeamknBaseActivity {
  public class Extra {
    public static final String CLIENT_CHAT_ID = "client_chat_id";
  }

  private ListView chat_node_lv;
  private EditText chat_node_et;
  private int client_chat_id;
  private Chat chat;
  private ChatNodeListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    client_chat_id = getIntent().getIntExtra(Extra.CLIENT_CHAT_ID, 0);
    chat = ChatDBHelper.find(client_chat_id);
    setContentView(R.layout.chat);
    
    chat_node_lv = (ListView)findViewById(R.id.chat_node_list);
    chat_node_et = (EditText)findViewById(R.id.chat_node_et);
    build_list();
  }
  
  private void build_list() {
    // TODO 尝试不用异步，看是否影响交互
    List<ChatNode> chat_node_list = ChatNodeDBHelper.find_list(client_chat_id);
    adapter = new ChatNodeListAdapter(this);
    adapter.add_items(chat_node_list);
    chat_node_lv.setAdapter(adapter);
  }

  public void click_send_chat_node_bn(View view){
    final String content = chat_node_et.getText().toString();
    if(content == null || content.equals("")){
      return;
    }
    
    new TeamknAsyncTask<Void, Void, Integer>(ChatActivity.this,"请稍等") {
      @Override
      public Integer do_in_background(Void... params) throws Exception {
        int current_user_id = AccountManager.current_user().user_id;
        ChatNode chat_node = ChatNodeDBHelper.create(client_chat_id,content,current_user_id);
        if(BaseUtils.is_wifi_active(ChatActivity.this) && chat.is_syned()){
          HttpApi.ChatNode.create(chat_node.uuid,chat.server_chat_id,content);
        }
        return chat_node.id;
      }

      @Override
      public void on_success(Integer client_chat_node_id) {
        ChatNode chat_node = ChatNodeDBHelper.find(client_chat_node_id);
        adapter.add_item(chat_node);
        chat_node_et.setText("");
      }
    }.execute();
    
  }
}
