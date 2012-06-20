package com.teamkn.base.search;

import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Indexer {
    private IndexWriter writer;

    public static void index_notes() throws Exception {
        Indexer    indexer = new Indexer();
        List<Note> notes   = NoteDBHelper.all(false);

        for (Note note: notes) {
            indexer.index_note(note);
        }

        indexer.close();
    }

    public Indexer() throws Exception {
        Directory indexDir = FSDirectory.open(new File(Config.INDEX_DIR));
        writer             = new IndexWriter(indexDir,
                                             new StandardAnalyzer(Version.LUCENE_36),
                                             true,
                                             IndexWriter.MaxFieldLength.UNLIMITED);
    }

    protected Document new_document(Note note) throws Exception {
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

    private void index_note(Note note) throws Exception {
        Document doc = new_document(note);
        writer.addDocument(doc);
    }

    private void delete_index(Note note) throws Exception {
        Term delete_term = new Term("note_uuid", note.uuid);
        writer.deleteDocuments(delete_term);
    }

    private void update_index(Note note) throws Exception {
        Term update_term = new Term("note_uuid", note.uuid);
        Document doc = new_document(note);
        writer.updateDocument(update_term, doc);
    }

    public void close() throws IOException {
        writer.close();
    }
}
