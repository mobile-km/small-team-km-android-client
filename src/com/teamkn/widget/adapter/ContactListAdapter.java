package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.contact.ContactsActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Contact;
import com.teamkn.model.database.ContactDBHelper;

public class ContactListAdapter extends TeamknBaseAdapter<Contact> {

  public ContactListAdapter(TeamknBaseActivity activity) {
    super(activity);
  }

  @Override
  public View inflate_view() {
    return inflate(R.layout.list_contact_item, null);
  }

  @Override
  public BaseViewHolder build_view_holder(
      View view) {
    ViewHolder view_holder      = new ViewHolder();
    view_holder.contact_user_header_tv = (TextView)view.findViewById(R.id.contact_user_header_tv);
    view_holder.contact_user_avatar_iv = (ImageView)view.findViewById(R.id.contact_user_avatar_iv);
    view_holder.contact_user_name_tv = (TextView)view.findViewById(R.id.contact_user_name_tv);
    
    view_holder.be_invited_contact_action_ll = (LinearLayout)view.findViewById(R.id.be_invited_contact_action_ll);
    view_holder.be_invited_contact_accept_invite_bn = (Button)view.findViewById(R.id.be_invited_contact_accept_invite_bn);
    view_holder.be_invited_contact_refuse_invite_bn = (Button)view.findViewById(R.id.be_invited_contact_refuse_invite_bn);
    
    view_holder.be_refused_contact_action_ll = (LinearLayout)view.findViewById(R.id.be_refused_contact_action_ll);
    view_holder.be_refused_contact_remove_contact_bn = (Button)view.findViewById(R.id.be_refused_contact_remove_contact_bn);
    
    view_holder.be_removed_contact_action_ll = (LinearLayout)view.findViewById(R.id.be_removed_contact_action_ll);
    view_holder.be_removed_contact_remove_contact_bn = (Button)view.findViewById(R.id.be_removed_contact_remove_contact_bn);
    
    view_holder.applied_contact_action_ll = (LinearLayout)view.findViewById(R.id.applied_contact_action_ll);
    view_holder.applied_contact_remove_contact_bn = (Button)view.findViewById(R.id.applied_contact_remove_contact_bn);
    
    return view_holder;
    
  }

  @Override
  public void fill_with_data(
      BaseViewHolder holder,
      Contact item, int position) {
    
    ViewHolder view_holder = (ViewHolder) holder;
    
    view_holder.contact_user_name_tv.setText(item.contact_user_name);
    if(item.contact_user_avatar != null){
      Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(item.contact_user_avatar));
      Drawable drawable = new BitmapDrawable(bitmap);
      view_holder.contact_user_avatar_iv.setBackgroundDrawable(drawable);
    }else{
      view_holder.contact_user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
    }
    view_holder.be_invited_contact_action_ll.setVisibility(View.GONE);
    view_holder.be_refused_contact_action_ll.setVisibility(View.GONE);
    view_holder.be_removed_contact_action_ll.setVisibility(View.GONE);
    view_holder.applied_contact_action_ll.setVisibility(View.GONE);
    
    if(item.status.equals(ContactDBHelper.Status.BE_INVITED)){
      view_holder.contact_user_header_tv.setText("接收到的邀请");
      fill_be_invited_action(view_holder,item,position);
    }else if(item.status.equals(ContactDBHelper.Status.BE_REFUSED)){
      view_holder.contact_user_header_tv.setText("被拒绝的邀请");
      fill_be_refused_action(view_holder,item,position);
    }else if(item.status.equals(ContactDBHelper.Status.BE_REMOVED)){
      view_holder.contact_user_header_tv.setText("把我删除的联系人");
      fill_be_removed_action(view_holder,item,position);
    }else if(item.status.equals(ContactDBHelper.Status.APPLIED)){
      view_holder.contact_user_header_tv.setText("联系人");
      fill_applied_action(view_holder,item,position);
    }else if(item.status.equals(ContactDBHelper.Status.INVITED)){
      view_holder.contact_user_header_tv.setText("发出的邀请");
    }
    
    view_holder.contact_user_header_tv.setVisibility(View.VISIBLE);
    if(position != 0){
      Contact prev_contact = (Contact)getItem(position-1);
      if(prev_contact.status.equals(item.status)){
        view_holder.contact_user_header_tv.setVisibility(View.GONE);
      }
    }
  }
  
  private void fill_applied_action(ViewHolder view_holder, Contact item,
      int position) {
    // 删除联系人
    final int contact_user_id = item.contact_user_id;
    view_holder.applied_contact_remove_contact_bn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        remove_contact_record(contact_user_id);
      }
    });
    
    view_holder.applied_contact_action_ll.setVisibility(View.VISIBLE);
  }

  private void fill_be_removed_action(ViewHolder view_holder, Contact item,
      int position) {
    // 删除 已经把我删除联系人的 关联记录
    final int contact_user_id = item.contact_user_id;
    view_holder.be_removed_contact_remove_contact_bn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        remove_contact_record(contact_user_id);
      }
    });
    
    view_holder.be_removed_contact_action_ll.setVisibility(View.VISIBLE);
  }

  private void fill_be_refused_action(ViewHolder view_holder, Contact item,
      int position) {
    // 删除已经把我的邀请拒绝的 关联记录
    final int contact_user_id = item.contact_user_id;
    view_holder.be_refused_contact_remove_contact_bn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        remove_contact_record(contact_user_id);
      }
    });
    
    view_holder.be_refused_contact_action_ll.setVisibility(View.VISIBLE);
  }

  private void fill_be_invited_action(ViewHolder view_holder, Contact item,
      int position) {
    // 接受邀请
    final int contact_user_id = item.contact_user_id;
    view_holder.be_invited_contact_accept_invite_bn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        
        new TeamknAsyncTask<Void, Void, Void>(ContactListAdapter.this.activity,"请稍等") {
          @Override
          public Void do_in_background(Void... params) throws Exception {
            HttpApi.Contact.accept_invite(contact_user_id);
            ContactsActivity contacts_activity = (ContactsActivity)ContactListAdapter.this.activity;
            contacts_activity.load_contacts_to_list();
            return null;
          }

          @Override
          public void on_success(Void result) {
          }
        }.execute();
      }
    });
    
    // 拒绝邀请
    view_holder.be_invited_contact_refuse_invite_bn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new TeamknAsyncTask<Void, Void, Void>(ContactListAdapter.this.activity,"请稍等") {
          @Override
          public Void do_in_background(Void... params) throws Exception {
            HttpApi.Contact.refuse_invite(contact_user_id);
            ContactsActivity contacts_activity = (ContactsActivity)ContactListAdapter.this.activity;
            contacts_activity.load_contacts_to_list();
            return null;
          }

          @Override
          public void on_success(Void result) {
          }
        }.execute();
      }
    });
    view_holder.be_invited_contact_action_ll.setVisibility(View.VISIBLE);
  }
  
  private void remove_contact_record(final int contact_user_id){
    new TeamknAsyncTask<Void, Void, Void>(ContactListAdapter.this.activity,"请稍等") {
      @Override
      public Void do_in_background(Void... params) throws Exception {
        HttpApi.Contact.remove_contact(contact_user_id);
        ContactsActivity contacts_activity = (ContactsActivity)ContactListAdapter.this.activity;
        contacts_activity.load_contacts_to_list();
        return null;
      }

      @Override
      public void on_success(Void result) {
      }
    }.execute();
  }
  
  private class ViewHolder implements BaseViewHolder {
    TextView contact_user_header_tv;
    ImageView contact_user_avatar_iv;
    TextView contact_user_name_tv;
    
    // 接收到得邀请
    LinearLayout be_invited_contact_action_ll; 
    Button be_invited_contact_accept_invite_bn;
    Button be_invited_contact_refuse_invite_bn;
    
    // 被拒绝的邀请
    LinearLayout be_refused_contact_action_ll;
    Button be_refused_contact_remove_contact_bn;
    
    
    // 把我删除的联系人
    LinearLayout be_removed_contact_action_ll;
    Button be_removed_contact_remove_contact_bn;
    
    // 联系人
    LinearLayout applied_contact_action_ll;
    Button applied_contact_remove_contact_bn;
  }

}
