package com.teamkn.base.search;

import com.teamkn.base.task.IndexTimerTask;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import org.apache.http.util.VersionInfo;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Indexer {
    private IndexWriter writer;
    private static Directory indexDir;

    static {
        try {
            indexDir = FSDirectory.open(new File(Config.INDEX_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void index_task(long interval) {
        Timer index_timer = new Timer();
        index_timer.scheduleAtFixedRate(new IndexTimerTask(),
                                        0,
                                        interval);
    }

    public static void index_notes() throws Exception {
        Indexer    indexer = new Indexer(IndexWriterConfig.OpenMode.CREATE);
        List<Note> notes   = NoteDBHelper.all(false);

        for (Note note: notes) {
            indexer.add_index(note);
        }

        indexer.close();
    }

    public Indexer(IndexWriterConfig.OpenMode open_mode) throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
                                                         new StandardAnalyzer(Version.LUCENE_36));
        config.setOpenMode(open_mode);

        writer = new IndexWriter(indexDir, config);
    }

    public static boolean index_exists() throws IOException {
        return IndexReader.indexExists(indexDir);
    }

    private Document new_document(Note note) throws Exception {
        Document doc = new Document();

        doc.add(new Field("note_uuid",
                          note.uuid,
                          Field.Store.YES,
                          Field.Index.NO));

        doc.add(new Field("note_content",
                          note.content,
                          Field.Store.YES,
                          Field.Index.ANALYZED));

        return doc;
    }

    public void add_index(Note note) throws Exception {
        writer.addDocument(new_document(note));
    }

    public void delete_index(Note note) throws Exception {
        if (note.is_removed == 1) {
            writer.deleteDocuments(new Term("note_uuid",
                                            note.uuid));
        }
    }

    public void update_index(Note note) throws Exception {
        if (note.is_removed == 0) {
            writer.updateDocument(new Term("note_uuid",
                                           note.uuid),
                                  new_document(note));
        }
    }

    public void close() throws IOException {
        writer.close();
    }
}
