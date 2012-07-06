package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.ChatNode;
import com.teamkn.model.User;

public class ChatNodeListAdapter extends TeamknBaseAdapter<ChatNode> {

  public ChatNodeListAdapter(TeamknBaseActivity activity) {
    super(activity);
  }

  @Override
  public View inflate_view() {
    return inflate(R.layout.list_chat_node_item, null);
  }

  @Override
  public BaseViewHolder build_view_holder(
      View view) {
    
    ViewHolder view_holder      = new ViewHolder();
    view_holder.user_avatar_iv   = (ImageView) view.findViewById(R.id.user_avatar_iv);
    view_holder.chat_node_content_tv = (TextView)  view.findViewById(R.id.chat_node_content_tv);
    
    return view_holder;
  }

  @Override
  public void fill_with_data(BaseViewHolder holder, ChatNode item, int position) {
    ViewHolder view_holder = (ViewHolder) holder;
    if(item.sender.user_avatar != null){
      Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(item.sender.user_avatar));
      Drawable drawable = new BitmapDrawable(bitmap);
      view_holder.user_avatar_iv.setBackgroundDrawable(drawable);
    }else{
      view_holder.user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
    }
    
    view_holder.chat_node_content_tv.setText(item.content);
  }
  
  private class ViewHolder implements BaseViewHolder {
    ImageView user_avatar_iv;
    TextView chat_node_content_tv; 
  }
}
