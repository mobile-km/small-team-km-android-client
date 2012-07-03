package com.teamkn.Logic;

import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

public class NoteMeta {
  public class Action{
    public static final String PUSH = "PUSH";
    public static final String PULL = "PULL";
  }
  public String uuid;
  public long server_updated_time;
  public long client_updated_time;
  
  public NoteMeta(String uuid, long server_updated_time, long client_updated_time){
    this.uuid = uuid;
    this.server_updated_time = server_updated_time;
    this.client_updated_time = client_updated_time;
  }
  
  public String get_action(){
    long last_syn_success_client_time = TeamknPreferences.last_syn_success_client_time();
    long last_syn_success_server_time = TeamknPreferences.last_syn_success_server_time();
    long client_adjusted_updated_time = client_updated_time - (last_syn_success_client_time - last_syn_success_server_time);
    
    if(client_adjusted_updated_time >= server_updated_time){
      return NoteMeta.Action.PUSH;
    }else{
      return NoteMeta.Action.PULL;
    }
  }
  
  public long syn() throws Exception{
    String action = get_action();
    if(action.equals(NoteMeta.Action.PUSH)){
      return HttpApi.Syn.pull(uuid);
    }else{
      Note note = NoteDBHelper.find(uuid);
      return HttpApi.Syn.push(note);
    }
  }
}
