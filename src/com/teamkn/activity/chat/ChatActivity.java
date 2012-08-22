package com.teamkn.activity.chat;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Attitudes;
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.database.AttitudesDBHelper;
import com.teamkn.model.database.ChatDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper.Kind;
import com.teamkn.widget.adapter.AttitudesListAdapter;
import com.teamkn.widget.adapter.ChatNodeListAdapter;

public class ChatActivity extends TeamknBaseActivity {
	  static LinearLayout showdialog;  
	  private static View popView = null;
	
	  static ListView listview_att;
	  static AttitudesListAdapter attitudesListAdapter_chat;
	  static int x ;
	  static int y ;
	  
	  boolean isRun = true;
	  
	  public class Extra {
		public static final String CLIENT_CHAT_ID = "client_chat_id";
		public static final int CHAT_ALBUM = 5;
	  }
	  
	 static ListView chat_node_lv;
	 TextView chat_node_list_below;
	 static EditText chat_node_et;
	 static InputMethodManager imm;
	 
	 static ImageView emotion_icon_smile;
	 static ImageView emotion_icon_wink;
	 static ImageView emotion_icon_gasp;
	 static ImageView emotion_icon_sad;
	 static ImageView emotion_icon_heart;
	 
	 static boolean chat_node_et_focus_yes = true;
	  
	  private int client_chat_id;
	  private static Chat chat;
	  private static ChatNodeListAdapter adapter;
	  
	  private static int visibleItemTop = 0;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    visibleItemTop = 0;
	    client_chat_id = getIntent().getIntExtra(Extra.CLIENT_CHAT_ID, 0);
	    chat = ChatDBHelper.find(client_chat_id);
	    setContentView(R.layout.chat);

	    chat_node_lv = (ListView)findViewById(R.id.chat_node_list);
	    chat_node_list_below = (TextView)findViewById(R.id.chat_node_list_below);
	    chat_node_et = (EditText)findViewById(R.id.chat_node_et);
	    showdialog = (LinearLayout)findViewById(R.id.showdialog);
	    
	    chat_node_list_below.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeEditTextFouse();
			}
		});
	    //点击输入框以外的地方则隐藏输入法  
	    chat_node_et_focus_yes = true;
	    chat_node_et.setOnFocusChangeListener(new OnFocusChangeListener() {  
	        @Override  
	        public void onFocusChange(View v, boolean hasFocus) {  
	            // TODO Auto-generated method stub  
	            if (!hasFocus) {  
	                System.out.println("失去焦点"); 
	                chat_node_et_focus_yes = true;
	                // 失去焦点  
	            }else{
	            	chat_node_et_focus_yes = false;
	            }
	        }  
	    });  
	   imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
   	    build_list();
	  } 
	  private void build_list() {
	    // TODO 尝试不用异步，看是否影响交互
	    List<ChatNode> chat_node_list = ChatNodeDBHelper.find_list(client_chat_id);
	    adapter = new ChatNodeListAdapter(this);
	    adapter.add_items(chat_node_list);
	    chat_node_lv.setAdapter(adapter); 

	    chat_node_lv.setOnScrollListener(new OnScrollListener() {		
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}	
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {	
				if(totalItemCount>3){
					visibleItemTop = totalItemCount - visibleItemCount + 3;
				}else{
					visibleItemTop = totalItemCount - visibleItemCount + 1;
				}	
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
			    chat_node_lv.setSelection(visibleItemTop);
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
			     chat_node_lv.setSelection(visibleItemTop);
			}
		}.execute();     
     }
	 public static void add_chat_node_item(final ChatNode chatNode){
		 chat_node_lv.post(new Runnable() {
			@Override
			public void run() {
				 adapter.add_item(chatNode);
				 chat_node_lv.setSelection(visibleItemTop);
			}
		});
	 }
	 
	 //111
	 public static void click_send_attitudes_kind(final ChatActivity context,String kindStr,final int chat_node_id,final int server_chat_node_id){
		    final String kind = kindStr;
		    
			   
		    if(kind == null || kind.equals("")){
		        return;
		    }
		    new TeamknAsyncTask<Void, Void, Integer>(context,"请稍等") {
				@Override
				public Integer do_in_background(Void... params) throws Exception {
					
				    int current_user_id = AccountManager.current_user().user_id;
				    final Attitudes attitudes =  AttitudesDBHelper.create(chat_node_id,current_user_id,kind,"false");
				 
				    listview_att.post(new Runnable() {						
						@Override
						public void run() {
							attitudesListAdapter_chat.add_item(attitudes);
							adapter.notifyDataSetChanged();
						    attitudesListAdapter_chat.notifyDataSetChanged();
						}
					});
				   
				    if(BaseUtils.is_wifi_active(context) && chat.is_syned()){
				       HttpApi.Attitudes.create(chat_node_id,current_user_id,kind,server_chat_node_id);
				    }
				    
				    return attitudes.chat_node_id;
				}
				
				@Override
				public void on_success(Integer client_chat_node_id) {
				    AttitudesDBHelper.find(client_chat_node_id);
				    
//				    attitudesListAdapter_chat.add_item(attitudes);
//				    attitudesListAdapter_chat.notifyDataSetChanged();
			    }
			 }.execute();
			 
				
	 }
	 //222
	 
     public static boolean showDialog(final ChatActivity context,int[] intXY ,  final int chat_id,int server_chat_node_id,final ImageButton button,
    		 AttitudesListAdapter attitudesListAdapter,ListView listview){
    	if(chat_node_et_focus_yes){
    		attitudesListAdapter_chat = attitudesListAdapter; //内部listview的适配器
        	listview_att = listview; //内部的listview

        	x = intXY[0];
     		y = intXY[1];
            
     		LayoutInflater inflater = LayoutInflater.from(context);// 取得LayoutInflater对象
    	    popView = inflater.inflate(R.layout.pupupwindow, null);// 读取布局管理器
    		
    	    
    	    showdialog.setVisibility(View.VISIBLE);
    		popView.setVisibility(View.VISIBLE);
    		showdialog.setPadding(10, y-60, 10+popView.getWidth()+50, -(y+popView.getHeight()-60));
    		showdialog.addView(popView);
    		
    		
    		emotion_icon_smile = (ImageView)popView.findViewById(R.id.emotion_icon_smile);
    		emotion_icon_wink = (ImageView)popView.findViewById(R.id.emotion_icon_wink);
    		emotion_icon_gasp = (ImageView)popView.findViewById(R.id.emotion_icon_gasp);
    		emotion_icon_sad = (ImageView)popView.findViewById(R.id.emotion_icon_sad);
    		emotion_icon_heart = (ImageView)popView.findViewById(R.id.emotion_icon_heart);
    		
    		start_animation_from();
    		
    		ImageView imageView = (ImageView)popView.findViewById(R.id.comment_1);
    		emotion_icon_smile.setOnClickListener(new EmotionClick(context,button,imageView,chat_id ,server_chat_node_id));
    		emotion_icon_wink.setOnClickListener(new EmotionClick(context,button,imageView,chat_id ,server_chat_node_id));
    		emotion_icon_gasp.setOnClickListener(new EmotionClick(context,button,imageView,chat_id ,server_chat_node_id));
    		emotion_icon_sad.setOnClickListener(new EmotionClick(context,button,imageView,chat_id ,server_chat_node_id));
    		emotion_icon_heart.setOnClickListener(new EmotionClick(context,button,imageView,chat_id ,server_chat_node_id));
            showdialog.setOnClickListener(new EmotionClick(context,button,imageView,chat_id ,server_chat_node_id));
    	}else{
    		closeEditTextFouse();
    	}
		return false; 
     }

    private static void start_animation_from(){
    	 Animation smial = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                 Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	 smial.setStartOffset(20);
    	 smial.setFillAfter(true);
    	 smial.setDuration(400);
    	 
    	 Animation wink = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                 Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	 wink.setStartOffset(40);
    	 wink.setFillAfter(true);
    	 wink.setDuration(400);
    	 
    	 Animation gasp = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                 Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	 gasp.setStartOffset(60);
    	 gasp.setFillAfter(true);
    	 gasp.setDuration(400);
    	 
    	 Animation sad = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                 Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	 sad.setStartOffset(80);
    	 sad.setFillAfter(true);
    	 sad.setDuration(400);
    	 
    	 Animation heart = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                 Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    	 heart.setStartOffset(100);
    	 heart.setFillAfter(true);
    	 heart.setDuration(400);
         
    	 emotion_icon_smile.startAnimation(smial);
    	 emotion_icon_wink.startAnimation(wink);
    	 emotion_icon_gasp.startAnimation(gasp);
    	 emotion_icon_sad.startAnimation(sad);
    	 emotion_icon_heart.startAnimation(heart);
    }
 	
 	static class EmotionClick implements OnClickListener{
		ChatActivity context;
		ImageButton view_im = null;
		ImageView imageView = null;
		int chat_id ;
		int server_chat_node_id ;
		public EmotionClick(ChatActivity context,ImageButton button,ImageView imageView,int chat_id,int server_chat_node_id){
			this.context = context;
			this.view_im = button;
			this.imageView = imageView;
			this.chat_id = chat_id;
			this.server_chat_node_id = server_chat_node_id;
		}
		@Override
		public void onClick(View v) {
			System.out.println("v.getId() = "+v.getId());
			switch (v.getId()) {	
				case R.id.emotion_icon_gasp:
					view_im.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_gasp_extrasmall));	
					
	    			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_gasp));	
					click_send_attitudes_kind(context,AttitudesDBHelper.Kind.GASP,chat_id,server_chat_node_id);
	    		
					break;
				case R.id.emotion_icon_heart:
					view_im.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_heart_extrasmall));	
					
	    			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_heart));	
					click_send_attitudes_kind(context,AttitudesDBHelper.Kind.HEART,chat_id,server_chat_node_id);
	    		
					break;
				case R.id.emotion_icon_sad:
					view_im.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_sad_extrasmall));	
					
	    			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_sad));	
					click_send_attitudes_kind(context,AttitudesDBHelper.Kind.SAD,chat_id,server_chat_node_id);
	    		
					break;
				case R.id.emotion_icon_smile:
					view_im.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_smile_extrasmall));	
					imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_smile));	
					click_send_attitudes_kind(context,AttitudesDBHelper.Kind.SMILE,chat_id,server_chat_node_id);
					break;
				case R.id.emotion_icon_wink:
					view_im.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_wink_extrasmall));	
					
	    			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_wink));	
					click_send_attitudes_kind(context,AttitudesDBHelper.Kind.WINK,chat_id,server_chat_node_id);
	    		
					break;	
				default:
					break;
			}
//			end_animation_from();
			popView.setVisibility(View.GONE);
			showdialog.setVisibility(View.GONE);
			showdialog.removeView(popView);			
		}			
	};
	
	public static void closeEditTextFouse(){
        // 失去焦点  
        chat_node_et.clearFocus();  
        imm.hideSoftInputFromWindow(chat_node_et.getWindowToken(), 0);  
	}
}
