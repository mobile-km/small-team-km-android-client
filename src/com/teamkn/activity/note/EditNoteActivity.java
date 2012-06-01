package com.teamkn.activity.note;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditNoteActivity extends TeamknBaseActivity {
  private EditText note_content_et;
  private String note_uuid;
  private Note note;
  
  public class Extra{
    public static final String NOTE_UUID = "note_uuid";
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_note);
    
    note_uuid = getIntent().getStringExtra(EditNoteActivity.Extra.NOTE_UUID);
    note_content_et = (EditText) findViewById(R.id.note_content_et);
    
    if(note_uuid != null){
      note = NoteDBHelper.find(note_uuid);
      note_content_et.setText(note.content);
    }
  }
  
  private boolean is_edit_note(){
    return note != null;
  }
  

  public void click_save_note_bn(View view) {
    String note_content = note_content_et.getText().toString();

    if (BaseUtils.is_str_blank(note_content)) {
      BaseUtils.toast(R.string.note_content_valid_blank);
      return;
    }
    save_note(note_content);
  }

  private void save_note(String note_content) {
    new TeamknAsyncTask<String, Void, Void>(this, R.string.saving) {
      @Override
      public Void do_in_background(String... params) throws Exception {
        String note_content = params[0];
        
        if(is_edit_note()){
          NoteDBHelper.update(note_uuid, note_content);
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
