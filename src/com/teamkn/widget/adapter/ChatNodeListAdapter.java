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
import com.teamkn.Logic.CompressPhoto;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.Note;
import com.teamkn.model.database.ChatNodeDBHelper.Kind;

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
    view_holder.user_content_iv = (ImageView)view.findViewById(R.id.user_content_iv);
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
    if(item.kind.equals(Kind.IMAGE)){
        String image_file_path = Chat.note_image_file(item.uuid).getPath();
        System.out.println("ChatNodeListAdapter ---  image_file_path " + image_file_path);
//  Bitmap bitmap = BitmapFactory.decodeFile(image_file_path);
        Bitmap bitmap = CompressPhoto.get_thumb_bitmap_form_file(image_file_path);
        view_holder.user_content_iv.setVisibility(View.VISIBLE);
        view_holder.chat_node_content_tv.setVisibility(View.GONE);
        view_holder.user_content_iv.setImageBitmap(bitmap);
    }else{
    	view_holder.chat_node_content_tv.setVisibility(View.VISIBLE);
    	view_holder.user_content_iv.setVisibility(View.GONE);
    	view_holder.chat_node_content_tv.setText(item.content);
    }
    
  }
  
  private class ViewHolder implements BaseViewHolder {
    ImageView user_avatar_iv;
    TextView chat_node_content_tv; 
    ImageView user_content_iv;
  }
}
