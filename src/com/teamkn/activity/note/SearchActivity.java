package com.teamkn.activity.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.teamkn.R;
import com.teamkn.base.search.Indexer;
import com.teamkn.base.search.SearchHistory;
import com.teamkn.base.search.Searcher;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.widget.adapter.NoteListAdapter;
import org.apache.lucene.queryParser.ParseException;

import java.io.IOException;
import java.util.LinkedList;
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

        EditText search_box    = (EditText) findViewById(R.id.search_box);
        Button   search_submit = (Button)   findViewById(R.id.search_submit);
        Button   search_clear  = (Button)   findViewById(R.id.search_clear);

        search_submit.setOnClickListener(new SearchSubmitClickListener());
        search_clear.setOnClickListener(new SearchClearClickListener());
        search_box.setOnKeyListener(new SearchBoxEnterListener());
        load_search_history();
    }

    private void do_index() {
        try {
            Indexer.index_notes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add_records(ViewGroup layout) {
        LinkedList<String> records = SearchHistory.get();

        if (!records.isEmpty()) {
            layout.removeAllViews();
            for (String record_text: records) {
                Button record = new Button(this);
                record.setText(record_text);
                layout.addView(record);
            }
        }
    }

    private void set_on_search_record_click_listener(ViewGroup history_view,
                                                     SearchHistoryRecordClickListener listener) {
        int child_count = history_view.getChildCount();

        for (int i = 0; i < child_count; i++) {
            View child_view = history_view.getChildAt(i);
            child_view.setOnClickListener(listener);
        }
    }

    private void load_search_history() {
        LinearLayout search_history =
                (LinearLayout) findViewById(R.id.search_history);
        add_records(search_history);

        set_on_search_record_click_listener(search_history,
                                            new SearchHistoryRecordClickListener());
    }

    private void load_result_list(List<Note> notes) {
        ListView        search_result_list = (ListView) findViewById(R.id.search_result_list);
        NoteListAdapter note_list_adapter  = new NoteListAdapter(SearchActivity.this);

        note_list_adapter.add_items(notes);
        search_result_list.setAdapter(note_list_adapter);
        search_result_list.setOnItemClickListener(new NoteClickListener());
        search_result_list.setOnCreateContextMenuListener(new NoteItemContextMenuListener());
    }

    private void search_box_set_text(String text) {
        EditText search_box = (EditText) findViewById(R.id.search_box);
        search_box.setText(text);
    }

    private void do_search(String query_string) throws IOException, ParseException {
        List<Note> result = Searcher.search(query_string);
        load_result_list(result);
    }

    private void do_search_box_search() {
        EditText search_box = (EditText) findViewById(R.id.search_box);
        String query_string = search_box.getText().toString();
        SearchHistory.put(query_string);

        LinearLayout search_history =
                (LinearLayout) findViewById(R.id.search_history);

        try {
            do_search(query_string);
            add_records(search_history);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SearchHistoryRecordClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Button record = (Button) view;
            String record_string = (String) record.getText();

            search_box_set_text(record_string);
            load_search_history();
            try {
                do_search(record_string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private class SearchClearClickListener implements View.OnClickListener {

        @Override
        public void onClick(View button) {
            search_box_set_text("");
        }
    }

    private class SearchSubmitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View button) {
            do_search_box_search();
        }
    }

    private class SearchBoxEnterListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode           == KeyEvent.KEYCODE_ENTER)) {

                do_search_box_search();
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
