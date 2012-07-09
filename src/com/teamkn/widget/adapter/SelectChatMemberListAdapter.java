package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.Contact;

public class SelectChatMemberListAdapter extends TeamknBaseAdapter<Contact> {
  private List<Integer> select_chat_member_server_user_ids;

  public SelectChatMemberListAdapter(TeamknBaseActivity activity, List<Integer> select_chat_member_server_user_ids) {
    super(activity);
    this.select_chat_member_server_user_ids = select_chat_member_server_user_ids;
  }

  @Override
  public View inflate_view() {
    return inflate(R.layout.list_select_chat_member_item, null);
  }

  @Override
  public BaseViewHolder build_view_holder(
      View view) {
    ViewHolder view_holder      = new ViewHolder();
    
    view_holder.select_chat_member_user_id_tv = (TextView)view.findViewById(R.id.select_chat_member_user_id_tv);
    view_holder.select_chat_member_user_avatar_iv = (ImageView)view.findViewById(R.id.select_chat_member_user_avatar_iv);
    view_holder.select_chat_member_user_name_tv = (TextView)view.findViewById(R.id.select_chat_member_user_name_tv);
    view_holder.select_chat_member_select_cb = (CheckBox)view.findViewById(R.id.select_chat_member_select_cb);
    
    return view_holder;
  }

  @Override
  public void fill_with_data(BaseViewHolder holder, Contact item, int position) {
    ViewHolder view_holder = (ViewHolder) holder;
    view_holder.select_chat_member_user_id_tv.setTag(item.contact_user_id);
    view_holder.select_chat_member_user_name_tv.setText(item.contact_user_name);
    if(item.contact_user_avatar != null){
      Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(item.contact_user_avatar));
      Drawable drawable = new BitmapDrawable(bitmap);
      view_holder.select_chat_member_user_avatar_iv.setBackgroundDrawable(drawable);
    }else{
      view_holder.select_chat_member_user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
    }
    if(select_chat_member_server_user_ids.indexOf(item.contact_user_id) == -1){
      view_holder.select_chat_member_select_cb.setChecked(false);
    }else{
      view_holder.select_chat_member_select_cb.setChecked(true);
    }
  }
  
  public void select_item(View convertView){
    ViewHolder view_holder = (ViewHolder)convertView.getTag();
    CheckBox cb = view_holder.select_chat_member_select_cb;
    
    Integer id = (Integer)view_holder.select_chat_member_user_id_tv.getTag();
    
    if(cb.isChecked()){
      cb.setChecked(false);
      select_chat_member_server_user_ids.remove(id);
    }else{
      cb.setChecked(true);
      select_chat_member_server_user_ids.add(id);
    }
  }
  
  private class ViewHolder implements BaseViewHolder {
    TextView select_chat_member_user_id_tv;
    ImageView select_chat_member_user_avatar_iv;
    TextView select_chat_member_user_name_tv;
    CheckBox select_chat_member_select_cb;
  }

}
