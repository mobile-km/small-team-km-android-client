package com.teamkn.base.search;

import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
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
import java.util.List;

public class Indexer {
    public IndexWriter writer;
    private static Directory index_dir;
    private static Indexer instance = null;

    static {
        try {
            index_dir = FSDirectory.open(new File(Config.INDEX_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Indexer get_instance() throws Exception {
        if (instance == null) {
            instance = new Indexer();
        }

        return instance;
    }

    public static void index_notes() throws Exception {
        delete_all();
        List<Note> notes = NoteDBHelper.all(false);

        for (Note note: notes) {
            add_index(note);
        }
    }

    private Indexer() throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
                                                         new StandardAnalyzer(Version.LUCENE_36));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        writer = new IndexWriter(index_dir, config);
    }

    public static boolean index_exists() throws IOException {
        return IndexReader.indexExists(index_dir);
    }

    private static Document new_document(Note note) throws Exception {
        Document doc = new Document();

        doc.add(new Field("note_uuid",
                          note.uuid,
                          Field.Store.YES,
                          Field.Index.NOT_ANALYZED_NO_NORMS));

        doc.add(new Field("note_content",
                          note.content,
                          Field.Store.YES,
                          Field.Index.ANALYZED));

        return doc;
    }

    public static void add_index(Note note) throws Exception {
        get_instance().writer.addDocument(new_document(note));
    }

    public static void delete_index(Note note) throws Exception {
        if (note.is_removed == 1) {
            get_instance().writer.deleteDocuments(new Term("note_uuid",
                                                  note.uuid));
        }
    }

    public static void delete_all() throws Exception {
        get_instance().writer.deleteAll();
        commit();
    }

    public static void update_index(Note note) throws Exception {
        if (note.is_removed == 0) {
            get_instance().writer.updateDocument(new Term("note_uuid",
                                                 note.uuid),
                                                 new_document(note));
        }
    }

    public static void close() throws Exception {
        get_instance().writer.close();
        instance = null;
    }

    public static void commit() throws Exception {
        get_instance().writer.commit();
    }
}
