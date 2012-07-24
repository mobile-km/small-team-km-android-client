package com.teamkn.activity.chat;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter.BaseViewHolder;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Attitudes;
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.IsShow;
import com.teamkn.model.database.AttitudesDBHelper;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper.Kind;
import com.teamkn.widget.adapter.AttitudesListAdapter;
import com.teamkn.widget.adapter.ChatNodeListAdapter;

public class ChatActivity extends TeamknBaseActivity {
	  static ListView listview_att;
	  static AttitudesListAdapter attitudesListAdapter_chat;
	  static ImageButton view_im;  
	  static LinearLayout layout_list;
	  static int x ;
	  static int y ;
	  static boolean isShow = false;
	  public class Extra {
		public static final String CLIENT_CHAT_ID = "client_chat_id";
		public static final int CHAT_ALBUM = 1;
	  }
	  
	  private ListView chat_node_lv;
	  private EditText chat_node_et;
	// 对话框
	 static LinearLayout comment_frame_linearLayout;
	 static ImageView emotion_icn_smile;
	 static ImageView emotion_icn_wink;
	 static ImageView emotion_icn_gasp;
	 static ImageView emotion_icn_sad;
	 static ImageView emotion_icn_heart;
	 static Animation smile_scale_animation;
	 static Animation wink_scale_animation;
	 static Animation gasp_scale_animation;
	 static Animation sad_scale_animation;
	 static Animation heart_scale_animation;
	
//	 Animation dialog_set_animation;
	 static Animation dialog_set_alpha_animation;
	 static Animation dialog_set_translate_animation;
	 static AnimationSet dialog_set_animation = new AnimationSet(false);
	 
	 static Animation dialog_from_animation;
	  
	  private int client_chat_id;
	  private static int chat_node_id;
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
	    
	    
   	    comment_frame_linearLayout=(LinearLayout)findViewById(R.id.comment_frame_linearLayout);
	    emotion_icn_smile = (ImageView)this.findViewById(R.id.emotion_icn_smile);
	    emotion_icn_wink = (ImageView)this.findViewById(R.id.emotion_icn_wink);
	    emotion_icn_gasp = (ImageView)this.findViewById(R.id.emotion_icn_gasp);
	    emotion_icn_sad = (ImageView)this.findViewById(R.id.emotion_icn_sad);
	    emotion_icn_heart = (ImageView)this.findViewById(R.id.emotion_icn_heart);
	    build_list();
	    
	    //33333333
	    chat_node_lv.setOnTouchListener(new OnTouchListener() {	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
//					System.out.println("event.getX()>10 && event.getX()<260  " + (event.getX()>10 && event.getX()<250));
//					System.out.println("event.getY()>y-20 && event.getY()+70<y  " + (event.getY()>y-20 && event.getY()+70<y));
					if( isShow==true &&  !( (event.getX()>10 && event.getX()<260) && (event.getY()>y-80 && event.getY()<y+100)) ){
						end_animation_from();
						ImageView imageView = (ImageView)findViewById(R.id.comment_1);
						imageView.setImageDrawable(getResources().getDrawable(R.drawable.camera_library_background_pressed));
						
			    	}
					else if( isShow==true && ( (event.getX()>10 && event.getX()<260) && (event.getY()>y-70 && event.getY()<y))){
						ImageView imageView = (ImageView)findViewById(R.id.comment_1);
						if( (event.getX()>20 && event.getX()<70) && (event.getY()>y-80 && event.getY()<y-30)){
							view_im.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_smile_extrasmall));	
							
							imageView.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_smile));	
							click_send_attitudes_kind(AttitudesDBHelper.Kind.SMILE,chat_node_id);
						}
			    		if( (event.getX()>75 && event.getX()<125) && (event.getY()>y-80 && event.getY()<y-30)){
			    			view_im.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_wink_extrasmall));	
							
			    			imageView.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_wink));	
							click_send_attitudes_kind(AttitudesDBHelper.Kind.WINK,chat_node_id);
			    		}
			    		if( (event.getX()>130 && event.getX()<180) && (event.getY()>y-80 && event.getY()<y-30)){
			    			view_im.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_gasp_extrasmall));	
							
			    			imageView.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_gasp));	
							click_send_attitudes_kind(AttitudesDBHelper.Kind.GASP,chat_node_id);
			    		}
			    		if( (event.getX()>185 && event.getX()<235) && (event.getY()>y-80 && event.getY()<y-30)){
			    			view_im.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_sad_extrasmall));	
							
			    			imageView.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_sad));	
							click_send_attitudes_kind(AttitudesDBHelper.Kind.SAD,chat_node_id);
			    		}
			    		if( (event.getX()>240 && event.getX()<290) && (event.getY()>y-80 && event.getY()<y-30)){
			    			view_im.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_heart_extrasmall));	
							
			    			imageView.setImageDrawable(getResources().getDrawable(R.drawable.emotion_icn_heart));	
							click_send_attitudes_kind(AttitudesDBHelper.Kind.HEART,chat_node_id);
			    		}
			    		end_animation_from();
			    	}
					break;	
				default:
					break;
				}	
				return false;
			}
		});
	    //44444444 
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
			    ChatNode chat_node = ChatNodeDBHelper.create(client_chat_id,content,current_user_id,Kind.TEXT);
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
	public void click_chat_node_button_album(View view){
		    
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
		intent.setType("image/*");
		startActivityForResult(intent,ChatActivity.Extra.CHAT_ALBUM);	    
	}
	//处理其他activity界面的回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		switch(requestCode){
		  case ChatActivity.Extra.CHAT_ALBUM:
			System.out.println("onActivityResult content＿album_path  " );
				
		    String album_path = BaseUtils.get_file_path_from_image_uri(data.getData());
		    chat_node_add_album(album_path);
		    break;
		}  
		super.onActivityResult(requestCode, resultCode, data);
	}
	 private void chat_node_add_album(String album_path){
		final String content＿album_path = album_path;
		System.out.println("content＿album_path = " + content＿album_path );
		if(content＿album_path == null || content＿album_path.equals("")){
			return;
		}else{
			create_image_note(content＿album_path);     	            
		}
			
	 }
	 private void create_image_note(String image_path) {
		final String content_image_path = image_path;
		if (null == content_image_path) {
		     BaseUtils.toast(R.string.note_image_valid_blank);
		     return;
		}
		 
		new TeamknAsyncTask<Void, Void, Integer>(ChatActivity.this,"请稍等") {
			@Override
			public Integer do_in_background(Void... params) throws Exception {
			     int current_user_id = AccountManager.current_user().user_id;
			     ChatNode chat_node = ChatNodeDBHelper.create_image_chat(client_chat_id,content_image_path,current_user_id,Kind.IMAGE);
			     
			     if(BaseUtils.is_wifi_active(ChatActivity.this) && chat.is_syned()){
			       HttpApi.ChatNode.create_image(chat_node.uuid,chat.server_chat_id,content_image_path,ChatNodeDBHelper.Kind.IMAGE);
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
	 
	 
	 //111
	 public void click_send_attitudes_kind(String kindStr,final int chat_node_id){
		    final String kind = kindStr;
		    
			   
		    if(kind == null || kind.equals("")){
		        return;
		    }
		    new TeamknAsyncTask<Void, Void, Integer>(ChatActivity.this,"请稍等") {
				@Override
				public Integer do_in_background(Void... params) throws Exception {
					
				    int current_user_id = AccountManager.current_user().user_id;
				    System.out.println(current_user_id + " : " + chat_node_id + " : " + kind);
				    final Attitudes attitudes =  AttitudesDBHelper.create(chat_node_id,current_user_id,kind);
				  
				    System.out.println("attitudes values  " + attitudes.chat_node_id);
//				    listview_att.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							layout_list.setVisibility(View.VISIBLE);
//							attitudesListAdapter_chat.add_item(attitudes);
//						    attitudesListAdapter_chat.notifyDataSetChanged();  
//						}
//					});
				    
				    if(BaseUtils.is_wifi_active(ChatActivity.this) && chat.is_syned()){
				      HttpApi.Attitudes.create(chat_node_id,current_user_id,kind);
				    }
				    return attitudes.chat_node_id;
				}
				
				@Override
				public void on_success(Integer client_chat_node_id) {
				    Attitudes attitudes = AttitudesDBHelper.find(client_chat_node_id);
				    
//				    attitudesListAdapter_chat.add_item(attitudes);
//				    attitudesListAdapter_chat.notifyDataSetChanged();
				    end_animation_from();
				    System.out.println("1111111111111111 ");
			    }
			 }.execute();
			 
				
	 }
	 //222
	 
     public static boolean showDialog(int[] intXY , int chat_id,ImageButton view,LinearLayout layout,AttitudesListAdapter attitudesListAdapter,ListView listview){
    	attitudesListAdapter_chat = attitudesListAdapter;
    	chat_node_id = chat_id;
    	layout_list = layout;
    	listview_att = listview;
    	view_im = view;
    	System.out.println("@@@@@@@@@@@@@ x:y --- "  + intXY[0]  + " :  " + intXY[1]);
 		x = intXY[0];
 		y = intXY[1];

 		load_animation(x,y-50);
 		start_animation_from();
 		
 		isShow = true;
		return false; 
     }

     private static void load_animation(int x, int y){
     	// 窗体出现
 		dialog_from_animation = new TranslateAnimation(20, 20, y, y);
 		dialog_from_animation.setFillAfter(true);
 		// 框体消失
 		dialog_set_alpha_animation = new AlphaAnimation(1.0f, 0.0f);
 		dialog_set_translate_animation=new TranslateAnimation(x, x, y, y+50); 
 		dialog_set_animation.addAnimation( dialog_set_alpha_animation);
 		dialog_set_animation.addAnimation(dialog_set_translate_animation);
 		dialog_set_animation.setStartOffset(10);  
 	}
 	private static void start_animation_from() {
		comment_frame_linearLayout.setVisibility(View.VISIBLE);
		comment_frame_linearLayout.startAnimation(dialog_from_animation);				
	    
		//表情框内部动画   先不添加
//		Animation dialog = new AlphaAnimation(1.0f, 0.0f);
//		dialog.setFillAfter(true);
//		dialog.setDuration(1000);
//		emotion_icn_smile.startAnimation(dialog);
 	}
 	private static void end_animation_from(){
 	        // 框体消失
 			Animation dialog_set_alpha_animation = new AlphaAnimation(1.0f, 0.0f);
 			dialog_set_alpha_animation.setFillAfter(true);
 			Animation dialog_set_translate_animation=new TranslateAnimation(20, 20, y-45, y+10); 
 			dialog_set_translate_animation.setFillAfter(true);
 			AnimationSet dialog_set_animation = new AnimationSet(false);
 			dialog_set_animation.addAnimation( dialog_set_alpha_animation);
 			dialog_set_animation.addAnimation(dialog_set_translate_animation);
 			dialog_set_animation.setDuration(500);
 			comment_frame_linearLayout.setAnimation(dialog_set_animation);
 			dialog_set_animation.startNow();
 			comment_frame_linearLayout.setVisibility(View.GONE);
 			isShow=false;		
 	}
 	@Override
 	public boolean onTouchEvent(MotionEvent event) {
 		 if(isShow==true){
 			ImageView imageView = (ImageView)findViewById(R.id.comment_1);
 	        imageView.setImageDrawable(getResources().getDrawable(R.drawable.camera_library_background_pressed));
  		    end_animation_from();
 		 }
 	     return super.onTouchEvent(event);
 	}
}
