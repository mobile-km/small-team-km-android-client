package com.teamkn.activity.note;

import java.util.ArrayList;
import java.util.List;
import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.base.activity.MindpinBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.widget.adapter.NoteListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NoteListActivity extends MindpinBaseActivity {
  public class RequestCode {
    public final static int EDIT_TEXT = 1;
  }

  private ListView note_list;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.note_list);
    note_list = (ListView) findViewById(R.id.note_list);
    load_list();
  }

  private void load_list() {
    List<Note> notes = new ArrayList<Note>();
    try {
      notes = NoteDBHelper.all();
    } catch (Exception e) {
      BaseUtils.toast("读取 note 列表失败");
      e.printStackTrace();
    }
    NoteListAdapter note_list_adapter = new NoteListAdapter(this);
    note_list_adapter.add_items(notes);
    note_list.setAdapter(note_list_adapter);

    note_list.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> list_view, View list_item,
          int item_id, long position) {
        TextView uuid_tv = (TextView) list_item.findViewById(R.id.note_uuid_tv);
        String uuid = (String) uuid_tv.getText();

        Intent intent = new Intent();
        intent.setClass(NoteListActivity.this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.Extra.NOTE_UUID, uuid);
        startActivityForResult(intent, NoteListActivity.RequestCode.EDIT_TEXT);
      }
    });
  }

  // 处理其他activity界面的回调
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    switch (requestCode) {
    case NoteListActivity.RequestCode.EDIT_TEXT:
      load_list();
      break;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

}
