package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.Logic.CompressPhoto;
import com.teamkn.activity.chat.ChatActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.IsShow;
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
    
    ViewHolder view_holder      = new ViewHolder();
    view_holder.user_avatar_iv   = (ImageView) view.findViewById(R.id.user_avatar_iv);
    view_holder.chat_node_content_tv = (TextView)  view.findViewById(R.id.chat_node_content_tv);
    view_holder.user_content_iv = (ImageView)view.findViewById(R.id.user_content_iv);
    view_holder.imagebutton_comment=(ImageButton)view.findViewById(R.id.imagebutton_comment);
    
    view_holder.comment_frame_linearLayout=(LinearLayout)view.findViewById(R.id.comment_frame_linearLayout);
    view_holder.emotion_icn_smile = (ImageView)view.findViewById(R.id.emotion_icn_smile);
    view_holder.emotion_icn_wink = (ImageView)view.findViewById(R.id.emotion_icn_wink);
    view_holder.emotion_icn_gasp = (ImageView)view.findViewById(R.id.emotion_icn_gasp);
    view_holder.emotion_icn_sad = (ImageView)view.findViewById(R.id.emotion_icn_sad);
    view_holder.emotion_icn_heart = (ImageView)view.findViewById(R.id.emotion_icn_heart);
    
    
    view_holder.smile_scale_animation = AnimationUtils.loadAnimation(context, R.anim.emotion_icn_smile_scale_animation);
    view_holder.smile_scale_animation.setFillAfter(true);
    view_holder.smile_scale_animation.setStartOffset(10);
	
    view_holder.wink_scale_animation = AnimationUtils.loadAnimation(context, R.anim.emotion_icn_wink_scale_animation);
    view_holder.wink_scale_animation.setFillAfter(true);
    view_holder.wink_scale_animation.setStartOffset(20);
	
    view_holder.gasp_scale_animation = AnimationUtils.loadAnimation(context, R.anim.emotion_icn_gasp_scale_animation);
    view_holder.gasp_scale_animation.setFillAfter(true);
    view_holder.gasp_scale_animation.setStartOffset(30);
	
    view_holder.sad_scale_animation = AnimationUtils.loadAnimation(context, R.anim.emotion_icn_sad_scale_animation);
    view_holder.sad_scale_animation.setFillAfter(true);
    view_holder.sad_scale_animation.setStartOffset(40);
	
    view_holder.heart_scale_animation = AnimationUtils.loadAnimation(context, R.anim.emotion_icn_heart_scale_animation);
    view_holder.heart_scale_animation.setFillAfter(true);
    view_holder.heart_scale_animation.setStartOffset(50);
	
    view_holder.dialog_set_animation = AnimationUtils.loadAnimation(context, R.anim.dialog_set_animation);
    view_holder.dialog_set_animation.setStartOffset(10);
    
    
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
    
//	view_holder.imagebutton_comment.setOnClickListener(new OnClickListener() {		
//		@Override
//		public void onClick(View v) {
//			if(!isShow.isShow_value){
//				view_holder.comment_frame_linearLayout.setVisibility(View.VISIBLE);
//				view_holder.emotion_icn_smile.startAnimation(view_holder.smile_scale_animation);				
//				view_holder.emotion_icn_wink.startAnimation(view_holder.wink_scale_animation);
//				view_holder.emotion_icn_gasp.startAnimation(view_holder.gasp_scale_animation);
//				view_holder.emotion_icn_sad.startAnimation(view_holder.sad_scale_animation);
//				view_holder.emotion_icn_heart.startAnimation(view_holder.heart_scale_animation);
//			}else{				
//				view_holder.comment_frame_linearLayout.setAnimation(view_holder.dialog_set_animation);
//				view_holder.comment_frame_linearLayout.setVisibility(View.GONE);			
//			}
//			isShow.setShow(!isShow.isShow_value);
//		
//		}
//	});
    view_holder.imagebutton_comment.setOnTouchListener(new OnTouchListener() {	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			
			//第二条思路
//			ChatActivity.isShow(new IsShow(x, y));	
			//第一条思路
			if(isShow.isShow_value()){
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					view_holder.comment_frame_linearLayout.setVisibility(View.VISIBLE);
					view_holder.emotion_icn_smile.startAnimation(view_holder.smile_scale_animation);				
					view_holder.emotion_icn_wink.startAnimation(view_holder.wink_scale_animation);
					view_holder.emotion_icn_gasp.startAnimation(view_holder.gasp_scale_animation);
					view_holder.emotion_icn_sad.startAnimation(view_holder.sad_scale_animation);
					view_holder.emotion_icn_heart.startAnimation(view_holder.heart_scale_animation);
					break;
				case MotionEvent.ACTION_MOVE:
					view_holder.comment_frame_linearLayout.setAnimation(view_holder.dialog_set_animation);
					view_holder.comment_frame_linearLayout.setVisibility(View.GONE);   
					
					break;
				case MotionEvent.ACTION_DOWN:
					view_holder.comment_frame_linearLayout.setAnimation(view_holder.dialog_set_animation);
					view_holder.comment_frame_linearLayout.setVisibility(View.GONE);
		           
					break;
				default:
					break;
				}
				
			}else{
				view_holder.comment_frame_linearLayout.setAnimation(view_holder.dialog_set_animation);
				view_holder.comment_frame_linearLayout.setVisibility(View.GONE);
	           
			}
			isShow.setShow(!isShow.isShow_value());
			return false;
		}
	});
    
  }
  
  private class ViewHolder implements BaseViewHolder {
    ImageView user_avatar_iv;
    TextView chat_node_content_tv; 
    ImageView user_content_iv;
    ImageButton imagebutton_comment;
    
    // 对话框
    LinearLayout comment_frame_linearLayout;
    
     

	 Animation smile_scale_animation;
	 Animation wink_scale_animation;
	 Animation gasp_scale_animation;
	 Animation sad_scale_animation;
	 Animation heart_scale_animation;
	
	 Animation dialog_set_animation;
	
	 ImageView emotion_icn_smile;
	 ImageView emotion_icn_wink;
	 ImageView emotion_icn_gasp;
	 ImageView emotion_icn_sad;
	 ImageView emotion_icn_heart;

  }
}
