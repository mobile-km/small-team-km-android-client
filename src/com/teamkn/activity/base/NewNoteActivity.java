package com.teamkn.activity.base;

import com.teamkn.R;
import com.teamkn.base.activity.MindpinBaseActivity;
import com.teamkn.base.task.MindpinAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewNoteActivity extends MindpinBaseActivity {
  private EditText note_content_et;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.new_note);
    find_views();
  }

  private void find_views() {
    note_content_et = (EditText) findViewById(R.id.note_content_et);
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
    new MindpinAsyncTask<String, Void, Void>(this, R.string.login_now_login) {
      @Override
      public Void do_in_background(String... params) throws Exception {
        String note_content = params[0];
        NoteDBHelper.save(new Note(note_content));
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
