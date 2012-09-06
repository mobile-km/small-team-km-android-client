package com.teamkn.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.bitmapshow.BitmapShowActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

public class ShowNodeActivity extends TeamknBaseActivity{
	public static boolean isRefash = false;
	String note_uuid;
	Note item ;
	ImageView show_node_msg_iv;
	TextView show_node_msg_tv;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_node_msg);
		
		show_node_msg_iv = (ImageView)findViewById(R.id.show_node_msg_iv);
		show_node_msg_tv = (TextView)findViewById(R.id.show_node_msg_tv);
		
        Intent intent = getIntent();
        note_uuid = intent.getStringExtra(EditNoteActivity.Extra.NOTE_UUID);
        isRefash = false;
        loadUI();
	}
	private void loadUI(){
		item = NoteDBHelper.find(note_uuid);
		if (item.kind.equals(NoteDBHelper.Kind.IMAGE)) {
        	ImageCache.load_cached_image(Note.note_thumb_image_file(item.uuid),show_node_msg_iv);
        	show_node_msg_iv.setVisibility(View.VISIBLE);
        } else {
//        	show_node_msg_iv.setVisibility(View.GONE);
        	show_node_msg_iv.setImageDrawable(getResources().getDrawable(R.drawable.test_test_feiji));
        }
		show_node_msg_tv.setText(item.content);
		show_node_msg_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShowNodeActivity.this,BitmapShowActivity.class);
	        	intent.putExtra("note_uuid", item.uuid);
	        	startActivityForResult(intent, 0);
			}
		});	
	}
	public void on_edit_note_button_click(View view){
		Intent   intent  = new Intent(ShowNodeActivity.this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.Extra.NOTE_UUID, item.uuid);
        intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, item.kind);
        
        if (item.kind == NoteDBHelper.Kind.IMAGE) {
            String image_path = Note.note_image_file(item.uuid).getPath();
            intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH,image_path);
        } 
        
        startActivityForResult(intent,MainActivity.RequestCode.EDIT_TEXT);
        
        isRefash = true;
	}
	@Override
    protected void onActivityResult(int  requestCode, int resultCode,Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case MainActivity.RequestCode.EDIT_TEXT:
              loadUI();
              break;
        }     
    }
	public void go_show_refash(View view){
		if(isRefash){
			open_activity(MainActivity.class);
			ShowNodeActivity.this.finish();
		}else{
			this.finish();
		}
	}
}
