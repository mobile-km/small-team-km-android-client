package com.teamkn.activity.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.teamkn.R;
import com.teamkn.base.search.Indexer;
import com.teamkn.base.search.Searcher;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.widget.adapter.NoteListAdapter;

import java.util.List;

public class SearchActivity extends NoteListActivity {
    public class RequestCode {
        public final static int EDIT_TEXT = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        do_index();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        load_search_box();
    }

    private void do_index() {
        try {
            Indexer.index_notes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load_search_box() {
        EditText search_box = (EditText) findViewById(R.id.search_field);
        search_box.setOnKeyListener(new SearchBoxSubmitListener(search_box));
    }

    private void load_result_list(List<Note> notes) {
        ListView        search_result_list = (ListView) findViewById(R.id.search_result_list);
        NoteListAdapter note_list_adapter  = new NoteListAdapter(SearchActivity.this);

        note_list_adapter.add_items(notes);
        search_result_list.setAdapter(note_list_adapter);
        search_result_list.setOnItemClickListener(new NoteClickListener());
        search_result_list.setOnCreateContextMenuListener(new NoteItemContextMenuListener());
    }

    private class NoteClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView,
                                View           list_item,
                                int            i,
                                long           l) {
            TextView info_tv = (TextView) list_item.findViewById(R.id.note_info_tv);
            String   uuid    = (String)   info_tv.getTag(R.id.tag_note_uuid);
            String   kind    = (String)   info_tv.getTag(R.id.tag_note_kind);

            Intent intent  = new Intent(SearchActivity.this, EditNoteActivity.class);
            intent.putExtra(EditNoteActivity.Extra.NOTE_UUID, uuid);
            intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, kind);

            if (kind == NoteDBHelper.Kind.IMAGE) {
                String image_path = Note.note_image_file(uuid).getPath();
                intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH,
                        image_path);
            }
            startActivityForResult(intent,
                                   SearchActivity.RequestCode.EDIT_TEXT);
        }

    }

    private class SearchBoxSubmitListener implements View.OnKeyListener {
        private EditText search_field;

        public SearchBoxSubmitListener(EditText start_search_field) {
            search_field = start_search_field;
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode           == KeyEvent.KEYCODE_ENTER)) {

                String query_string = search_field.getText().toString();

                try {
                    List<Note> result = Searcher.search(query_string);

                    load_result_list(result);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return true;
            }
            return false;
        }

    }

    private class NoteItemContextMenuListener implements View.OnCreateContextMenuListener {
        @Override
        public void onCreateContextMenu(ContextMenu                 menu,
                                        View                        view,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, 0, 0, "删除");
        }
    }
}
