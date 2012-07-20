package com.teamkn.activity.chat;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.IsShow;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper.Kind;
import com.teamkn.widget.adapter.ChatNodeListAdapter;

public class ChatActivity extends TeamknBaseActivity {
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
	    
	    
   	    comment_frame_linearLayout=(LinearLayout)findViewById(R.id.comment_frame_linearLayout);
	    emotion_icn_smile = (ImageView)this.findViewById(R.id.emotion_icn_smile);
	    emotion_icn_wink = (ImageView)this.findViewById(R.id.emotion_icn_wink);
	    emotion_icn_gasp = (ImageView)this.findViewById(R.id.emotion_icn_gasp);
	    emotion_icn_sad = (ImageView)this.findViewById(R.id.emotion_icn_sad);
	    emotion_icn_heart = (ImageView)this.findViewById(R.id.emotion_icn_heart);
	    
	    
	    
	    build_list();
	    
	  }
	  
	  private void build_list() {
	    // TODO 尝试不用异步，看是否影响交互
	    List<ChatNode> chat_node_list = ChatNodeDBHelper.find_list(client_chat_id);
	    adapter = new ChatNodeListAdapter(this);
	    adapter.add_items(chat_node_list);
	    chat_node_lv.setAdapter(adapter);
	    
	    System.out.println("```````` chat_node_lv.setOnItemClickListener(new OnItemClickListener() ");
	    chat_node_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View list_item, int item_id,
					long position) {
			      	 System.out.println("-------------------------------------------");
                 	 System.out.println("imageButton befaulter item_id = " + item_id  + " position = " + position);
            }
		});
	    
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
     public static boolean isShow(IsShow isShow){
    	 System.out.println("1*****************");
    	 System.out.println(isShow.getX() + "  :  " + isShow.getY());
    	 
    	 load_animation( isShow.getX() , isShow.getY());
    	 System.out.println("2*****************");
    	 startAnimation();
    	 System.out.println("3*****************");
		return false; 
     }

     private static  void load_animation(int x ,int y){
 
    	    //框体内部出现的现状
    	    smile_scale_animation =new ScaleAnimation(x, x, y, y,
    	    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	    smile_scale_animation.setFillAfter(true);
    	    smile_scale_animation.setStartOffset(10);
    		
    	    wink_scale_animation = new ScaleAnimation(x, x, y, y,
    	    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	    wink_scale_animation.setFillAfter(true);
    	    wink_scale_animation.setStartOffset(20);
    		
    	    gasp_scale_animation = new ScaleAnimation(x, x, y, y,
    	    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	    gasp_scale_animation.setFillAfter(true);
    	    gasp_scale_animation.setStartOffset(30);
    		
    	    sad_scale_animation = new ScaleAnimation(x, x, y, y,
    	    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	    sad_scale_animation.setFillAfter(true);
    	    sad_scale_animation.setStartOffset(40);
    		
    	    heart_scale_animation =new ScaleAnimation(x, x, y, y,
    	    		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	    heart_scale_animation.setFillAfter(true);
    	    heart_scale_animation.setStartOffset(50);
    		
    	    // 框体消失
    	    dialog_set_alpha_animation = new AlphaAnimation(1.0f, 0.0f);
    	    dialog_set_translate_animation=new TranslateAnimation(x, x, y, y+50); 
    	    dialog_set_animation.addAnimation( dialog_set_alpha_animation);
    	    dialog_set_animation.addAnimation(dialog_set_translate_animation);
    	    dialog_set_animation.setStartOffset(10); 
		
           // 窗体出现
    	   
     }
     private static  void startAnimation(){
    	    comment_frame_linearLayout.setAnimation(dialog_set_animation); 
    	    dialog_set_animation.startNow();
    	 
    	    emotion_icn_smile.startAnimation(smile_scale_animation);				
			emotion_icn_wink.startAnimation(wink_scale_animation);
			emotion_icn_gasp.startAnimation(gasp_scale_animation);
			emotion_icn_sad.startAnimation(sad_scale_animation);
			emotion_icn_heart.startAnimation(heart_scale_animation);
			
     }
}
