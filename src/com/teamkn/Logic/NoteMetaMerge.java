package com.teamkn.Logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import com.teamkn.model.Note;

public class NoteMetaMerge {
  private List<Note> client_changed_notes;
  private List<NoteMeta> server_note_metas;
  public long last_syn_server_meta_updated_time;

  public NoteMetaMerge(List<Note> client_changed_notes, List<NoteMeta> server_note_metas, long last_syn_server_meta_updated_time){
    this.client_changed_notes = client_changed_notes;
    this.server_note_metas = server_note_metas;
    this.last_syn_server_meta_updated_time = last_syn_server_meta_updated_time;
  }
  
  public List<NoteMeta> get_merge_list(){
    HashMap<String, NoteMeta> map = new HashMap<String, NoteMeta>();
    
    for (int i = 0; i < server_note_metas.size(); i++) {
      NoteMeta note_meta = server_note_metas.get(i);
      map.put(note_meta.uuid, note_meta);
    }
    
    for (int i = 0; i < client_changed_notes.size(); i++) {
      Note client_note = client_changed_notes.get(i);
      NoteMeta note_meta = map.get(client_note.uuid);
      if(note_meta != null){
        note_meta.client_updated_time = client_note.client_updated_time;
      }else{
        NoteMeta meta = new NoteMeta(client_note.uuid, 0, client_note.client_updated_time);
        map.put(client_note.uuid, meta);
      }
    }
    
    Collection<NoteMeta> values = map.values();
    ArrayList<NoteMeta> list = new ArrayList<NoteMeta>(values);
    return list;
  }
}
