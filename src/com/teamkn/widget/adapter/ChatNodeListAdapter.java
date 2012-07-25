package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.CompressPhoto;
import com.teamkn.activity.chat.ChatActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.utils.ListViewUtility;
import com.teamkn.model.Attitudes;
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.IsShow;
import com.teamkn.model.database.AttitudesDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper.Kind;

public class ChatNodeListAdapter extends TeamknBaseAdapter<ChatNode> {
		IsShow isShow = new IsShow();
	    Activity context;
  public ChatNodeListAdapter(TeamknBaseActivity activity) {	
	    super(activity);
	    this.context = activity;
  }

  @Override
  public View inflate_view() {
	    return inflate(R.layout.list_chat_node_item, null);
  }

  @Override
  public BaseViewHolder build_view_holder(
      View view) {
    
	    ViewHolder view_holder = new ViewHolder();
	    view_holder.user_avatar_iv   = (ImageView) view.findViewById(R.id.user_avatar_iv);
	    view_holder.chat_node_content_tv = (TextView)  view.findViewById(R.id.chat_node_content_tv);
	    view_holder.user_content_iv = (ImageView)view.findViewById(R.id.user_content_iv);
	    view_holder.imagebutton_comment=(ImageButton)view.findViewById(R.id.imagebutton_comment);
	    view_holder.listview_comment_result = (ListView)view.findViewById(R.id.listview_comment_result);
	    view_holder.subLayout = (LinearLayout)view.findViewById(R.id.subLayout);
	    return view_holder;
  }

  @Override
  public void fill_with_data(BaseViewHolder holder, ChatNode item, int position) {
	    final ViewHolder view_holder = (ViewHolder) holder;
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
	    
	    List<Attitudes> attitudes_list = AttitudesDBHelper.find_list(item.id);	
	    final AttitudesListAdapter attitudes_adapter = new AttitudesListAdapter(activity);
        if(attitudes_list.size()>0){
	    	
	    	view_holder.subLayout.setVisibility(View.VISIBLE);
	    	
	    	attitudes_adapter.add_items(attitudes_list);
	    	view_holder.listview_comment_result.setAdapter(attitudes_adapter);
	    	// 确定listview的高度
	    	ListViewUtility.setListViewHeightBasedOnChildren(view_holder.listview_comment_result);
	    	System.out.println("  test  comment   value " + item.id);
	    }else{
	    	attitudes_list.clear();
	    	view_holder.subLayout.setVisibility(View.GONE);
	    	System.out.println("  test  comment  no value " + item.id);
	    }
	    
	    final int chat_id = item.id;
		view_holder.imagebutton_comment.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				int[] intXY = new int[2];
				view_holder.imagebutton_comment.getLocationOnScreen(intXY);
				ChatActivity.showDialog(intXY,chat_id,view_holder.imagebutton_comment,view_holder.subLayout,attitudes_adapter,view_holder.listview_comment_result);
			}
		});
        
	    
  } 
  
  private class ViewHolder implements BaseViewHolder {
	    ImageView user_avatar_iv;
	    TextView chat_node_content_tv; 
	    ImageView user_content_iv;
	    ImageButton imagebutton_comment;
	    ListView listview_comment_result;
	    
	    LinearLayout subLayout;
  }
}
