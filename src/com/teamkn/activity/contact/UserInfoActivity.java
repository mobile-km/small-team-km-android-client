package com.teamkn.activity.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.Logic.SearchUser;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.cache.image.ImageCache;

public class UserInfoActivity extends TeamknBaseActivity {
  public class Extra {
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_AVATAR_URL = "user_avatar_url";
    public static final String CONTACT_STATUS = "contact_status";
}

  private TextView user_info_user_name_tv;
  private ImageView user_info_user_avatar_iv;
  
  private LinearLayout user_info_invite_action_ll;
  private EditText user_info_invite_message_et;
  private Button user_info_send_invite_bn;
  
  private LinearLayout user_info_be_invite_action_ll;
  private Button user_info_accept_invite_bn;
  private Button user_info_refuse_invite_bn;
  
  private LinearLayout user_info_contact_action_ll;
  private int user_id;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_info);
    Intent intent = getIntent();
    user_id = intent.getIntExtra(Extra.USER_ID, 0);
    String user_name = intent.getStringExtra(Extra.USER_NAME);
    String user_avatar_url = intent.getStringExtra(Extra.USER_AVATAR_URL);
    String contact_status = intent.getStringExtra(Extra.CONTACT_STATUS);
    
    // 显示用户名和头像
    user_info_user_name_tv = (TextView)findViewById(R.id.user_info_user_name_tv);
    user_info_user_avatar_iv = (ImageView)findViewById(R.id.user_info_user_avatar_iv);
    user_info_user_name_tv.setText(user_name);
    ImageCache.load_cached_image(user_avatar_url, user_info_user_avatar_iv);

    if(SearchUser.ContactStatus.APPLIED.equals(contact_status)){
      // 互为联系人
      user_info_contact_action_ll = (LinearLayout)findViewById(R.id.user_info_contact_action_ll);
      user_info_contact_action_ll.setVisibility(View.VISIBLE);
    }else if(SearchUser.ContactStatus.BE_INVITED.equals(contact_status)){
      // 接受邀请
      user_info_be_invite_action_ll = (LinearLayout)findViewById(R.id.user_info_be_invite_action_ll);
      user_info_accept_invite_bn = (Button)findViewById(R.id.user_info_accept_invite_bn);
      user_info_accept_invite_bn.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          new TeamknAsyncTask<Void, Void, Void>(UserInfoActivity.this,"请稍后") {
            @Override
            public Void do_in_background(Void... params) throws Exception {
              HttpApi.Contact.accept_invite(user_id);
              return null;
            }

            @Override
            public void on_success(Void result) {
              UserInfoActivity.this.finish();
            }
          }.execute();
        }
      });
      
      user_info_refuse_invite_bn = (Button)findViewById(R.id.user_info_refuse_invite_bn);
      user_info_refuse_invite_bn.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          new TeamknAsyncTask<Void, Void, Void>(UserInfoActivity.this,"请稍后") {
            @Override
            public Void do_in_background(Void... params) throws Exception {
              HttpApi.Contact.refuse_invite(user_id);
              return null;
            }

            @Override
            public void on_success(Void result) {
              UserInfoActivity.this.finish();
            }
          }.execute();
        }
      });
      
      user_info_be_invite_action_ll.setVisibility(View.VISIBLE);
    }else if(!SearchUser.ContactStatus.SELF.equals(contact_status)){
      // 发送邀请
      user_info_invite_action_ll = (LinearLayout)findViewById(R.id.user_info_invite_action_ll);
      user_info_invite_message_et = (EditText)findViewById(R.id.user_info_invite_message_et);
      user_info_send_invite_bn = (Button)findViewById(R.id.user_info_send_invite_bn);
      user_info_send_invite_bn.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          String message = user_info_invite_message_et.getText().toString();
          new TeamknAsyncTask<String, Void, Void>(UserInfoActivity.this,"请稍后") {
            @Override
            public Void do_in_background(String... params) throws Exception {
              String message = params[0];
              HttpApi.Contact.invite(user_id,message);
              return null;
            }

            @Override
            public void on_success(Void result) {
              UserInfoActivity.this.finish();
            }
          }.execute(message);
        }
      });
      user_info_invite_action_ll.setVisibility(View.VISIBLE);
    }
  }
}
