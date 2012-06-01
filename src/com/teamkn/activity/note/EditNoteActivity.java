package com.teamkn.activity.note;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class EditNoteActivity extends TeamknBaseActivity {
  private EditText note_content_et;
  private Note note;
  private String type;
  private ImageView note_image_iv;
  private Uri note_image_uri;
  
  public class Extra{
    public static final String NOTE_UUID = "note_uuid";
    public static final String NOTE_TYPE = "note_type";
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_note);
    
    Intent intent = getIntent();
    String note_uuid = intent.getStringExtra(EditNoteActivity.Extra.NOTE_UUID);
    if(note_uuid != null){
      note = NoteDBHelper.find(note_uuid);
    }
    
    type = intent.getStringExtra(EditNoteActivity.Extra.NOTE_TYPE);
    
    if(is_text_note()){
      note_content_et = (EditText) findViewById(R.id.note_content_et);
      note_content_et.setVisibility(View.VISIBLE);
      if(note != null){
        note_content_et.setText(note.content);
      }      
    }else if(is_image_note()){
      note_image_iv = (ImageView) findViewById(R.id.note_image_iv);
      note_image_iv.setVisibility(View.VISIBLE);
      note_image_uri = intent.getData();
      Bitmap bitmap = null;
      try {
        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), note_image_uri);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      if(bitmap != null){
        note_image_iv.setImageBitmap(bitmap);
      }else{
        note_image_uri = null;
        BaseUtils.toast("加载图片失败");
      }
      
    }
  }
  
  private boolean is_edit_note(){
    return note != null;
  }
  
  private boolean is_text_note(){
    return type.equals(NoteDBHelper.Type.TEXT);
  }
  
  private boolean is_image_note(){
    return type.equals(NoteDBHelper.Type.IMAGE);
  }

  public void click_save_note_bn(View view) {
    if(is_text_note()){
      save_text_note();
    }else{
      save_image_note();
    }
  }
  
  private void save_image_note() {
    BaseUtils.toast("正在施工");
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
        
        if(is_edit_note()){
          NoteDBHelper.update(note.uuid, note_content);
        }else{
          NoteDBHelper.create(note_content);
        }
        return null;
      }

      @Override
      public void on_success(Void v) {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK,intent);
        finish();
      }
    }.execute(note_content);
  }

}
