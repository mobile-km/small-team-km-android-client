package com.teamkn.activity.note;

import java.util.ArrayList;
import java.util.List;
import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.widget.adapter.NoteListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NoteListActivity extends TeamknBaseActivity {
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
      notes = NoteDBHelper.all(false);
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
        TextView info_tv = (TextView) list_item.findViewById(R.id.note_info_tv);
        String uuid = (String) info_tv.getTag(R.id.tag_note_uuid);
        String kind = (String) info_tv.getTag(R.id.tag_note_kind);
        
        Intent intent = new Intent(NoteListActivity.this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.Extra.NOTE_UUID, uuid);
        intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, kind);
        
        if(kind == NoteDBHelper.Kind.IMAGE){
          String image_path = NoteDBHelper.note_image_file(uuid).getPath();
          intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH,image_path);
        }
        startActivityForResult(intent, NoteListActivity.RequestCode.EDIT_TEXT);
      }
    });
    
    note_list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
      @Override
      public void onCreateContextMenu(ContextMenu menu, View v,
          ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, 0, 0, "删除");
      }
    });
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo menuInfo;
    menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    
    TextView note_info_tv = (TextView) menuInfo.targetView.findViewById(R.id.note_info_tv);
    String uuid = (String) note_info_tv.getTag(R.id.tag_note_uuid);
    destroy_note_confirm(uuid);
    
    return super.onContextItemSelected(item);
  }

  private void destroy_note_confirm(final String uuid) {
    AlertDialog.Builder builder = new AlertDialog.Builder(NoteListActivity.this); //这里只能用this，不能用appliction_context
    
    builder
      .setMessage("确认要删除吗？")
      .setPositiveButton(R.string.dialog_ok,
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,
              int which) {
            NoteDBHelper.destroy(uuid);
            load_list();
          }
        })
      .setNegativeButton(R.string.dialog_cancel, null)
      .show();
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
