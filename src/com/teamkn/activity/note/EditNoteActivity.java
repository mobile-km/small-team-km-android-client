package com.teamkn.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.teamkn.R;
import com.teamkn.activity.base.LoginActivity;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

public class EditNoteActivity extends TeamknBaseActivity {
    private EditText note_content_et;
    private ImageView note_image_iv;

    private Note note;
    private String kind;
    private String note_uuid;
    private String image_path;
    
    
    private  String text;
    private  Uri uri;
    public class Extra {
        public static final String NOTE_UUID = "note_uuid";
        public static final String NOTE_KIND = "note_kind";
        public static final String NOTE_IMAGE_PATH = "note_image_path";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);
        
        Intent intent = getIntent();
        
        note_uuid = intent.getStringExtra(EditNoteActivity.Extra.NOTE_UUID);
        kind = intent.getStringExtra(EditNoteActivity.Extra.NOTE_KIND);
        image_path = intent.getStringExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH);
        
        init_common();
        
        text = intent.getStringExtra(Intent.EXTRA_TEXT);       
        final Bundle content = intent.getExtras();
        System.out.println( "content.getParcelable(Intent.EXTRA_STREAM)  " +content.getParcelable(Intent.EXTRA_STREAM) );
        if ( content.getParcelable(Intent.EXTRA_STREAM) != null ) {
        	 uri = (Uri)content.getParcelable(Intent.EXTRA_STREAM); 
        	 image_path = uri.toString();
        }
        
        System.out.println(" getIntent() text = " + text);
        System.out.println(" getIntent() uri = " + uri);
        System.out.println(" getIntent() image_path = " + image_path);
        if(!is_logged_in()){
        	open_activity(LoginActivity.class);
            finish();
        }
        if(kind==null){
        	if(text!=null){
        		kind = NoteDBHelper.Kind.TEXT;
        	}
        	if(uri!=null){
        		kind = NoteDBHelper.Kind.IMAGE;
        	}        	
        }
        
        
       
        if (is_text_note()) {
            init_text_note();
        } else if (is_image_note()) {
            init_image_note();
        }
    }

    private void init_common() {
        if (note_uuid != null) {
            note = NoteDBHelper.find(note_uuid);
        }
    }

    private void init_text_note() {
        note_content_et = (EditText) findViewById(R.id.note_content_et);
        note_content_et.setVisibility(View.VISIBLE);
        if (note != null) {
            note_content_et.setText(note.content);
        }
        if(text!=null){
    		note_content_et.setText(text);
        }
    }

    
    private void init_image_note() {
        if (is_edit_note()) {
            note_content_et = (EditText) findViewById(R.id.note_content_et);
            note_content_et.setVisibility(View.VISIBLE);
            note_content_et.setText(note.content);
        } else {
            note_image_iv = (ImageView) findViewById(R.id.note_image_iv);
            note_image_iv.setVisibility(View.VISIBLE);

            try {
            	if(uri!=null){
//            		note_image_iv.setImageURI(uri);           		
            		Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            		note_image_iv.setImageBitmap(bitmap);
            	}else{
            		Bitmap bitmap = BitmapFactory.decodeFile(image_path);
                    note_image_iv.setImageBitmap(bitmap);
            	}   
            } catch (Exception e) {
                e.printStackTrace();
                image_path = null;
                BaseUtils.toast("加载图片失败");
            }
        }
    }

    private boolean is_edit_note() {
        return note_uuid != null;
    }

    private boolean is_text_note() {
        return kind.equals(NoteDBHelper.Kind.TEXT);
    }

    private boolean is_image_note() {
        return kind.equals(NoteDBHelper.Kind.IMAGE);
    }

    public void click_save_note_bn(View view) {
        if (is_text_note()) {
            save_text_note();
        } else if (is_image_note()) {
            if (is_edit_note()) {
                update_image_note();
            } else {
                create_image_note();
            }
        }
    }

    private void update_image_note() {
        String note_content = note_content_et.getText().toString();

        new TeamknAsyncTask<String, Void, Void>(this, R.string.saving) {
            @Override
            public Void do_in_background(String... params) throws Exception {
                String note_content = params[0];

                NoteDBHelper.update(note.uuid, note_content);
                return null;
            }

            @Override
            public void on_success(Void v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }.execute(note_content);
    }

    private void create_image_note() {
        if (null == image_path) {
            BaseUtils.toast(R.string.note_image_valid_blank);
            return;
        }

        new TeamknAsyncTask<String, Void, Void>(this, R.string.saving) {
            @Override
            public Void do_in_background(String... params) throws Exception {
                String image_path = params[0];

                NoteDBHelper.create_image_note(image_path);
                return null;
            }

            @Override
            public void on_success(Void v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }.execute(image_path);
    }

    private void save_text_note() {
        String note_content = note_content_et.getText().toString();

        if (BaseUtils.is_str_blank(note_content)) {
            BaseUtils.toast(R.string.note_content_valid_blank);
            return;
        }

        new TeamknAsyncTask<String, Void, Void>(this, R.string.saving) {
            @Override
            public Void do_in_background(String... params) throws Exception {
                String note_content = params[0];

                if (is_edit_note()) {
                    NoteDBHelper.update(note.uuid, note_content);
                } else {
                    NoteDBHelper.create_text_note(note_content);
                }
                return null;
            }

            @Override
            public void on_success(Void v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }.execute(note_content);
    }

}
