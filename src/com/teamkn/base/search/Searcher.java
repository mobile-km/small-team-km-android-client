package com.teamkn.base.search;

import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

    public static List<Note> search(String queryString) throws Exception {
        Directory     index_dir      = FSDirectory.open(new File(Config.INDEX_DIR));
        IndexReader   index_reader   = IndexReader.open(index_dir);
        IndexSearcher index_searcher = new IndexSearcher(index_reader);
        QueryParser   parser         = new QueryParser(Version.LUCENE_36,
                                                       "note_content",
                                                       new StandardAnalyzer(Version.LUCENE_36));
        Query         query         = parser.parse(queryString);
        int           count         = NoteDBHelper.total_count();
        TopDocs       result        = index_searcher.search(query, count);

        List<Note> notes = new ArrayList<Note>();

        for (ScoreDoc scoreDoc: result.scoreDocs) {
            Document doc  = index_searcher.doc(scoreDoc.doc);
            Note     note = NoteDBHelper.find(doc.get("note_uuid"));
            notes.add(note);
        }

        index_searcher.close();
        index_reader.close();
        return notes;
    }
}
