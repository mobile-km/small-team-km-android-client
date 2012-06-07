package com.teamkn.receiver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SynDataBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
	  
		new TeamknAsyncTask<String, Integer, Void>() {

			public void on_start() {
				send_progress_broadcast(context, 0);
			};

			@Override
			public Void do_in_background(String... params) throws Exception {
			  HashMap<String,Object> map = HttpApi.Syn.handshake();
			  String uuid = (String)map.get("syn_task_uuid");
			  int server_count = (Integer)map.get("note_count");
			  int unsyn_count = NoteDBHelper.unsyn_count();
			  int count = server_count + unsyn_count;
			  
			  send_max_num(context,count);
			  int index = 0;
			  send_progress_broadcast(context,index);
			  List<Note> list = NoteDBHelper.all(true);
			  for (Iterator<Note> iterator = list.iterator(); iterator.hasNext();) {
          Note note = iterator.next();
          HttpApi.Syn.compare(uuid, note);
          index+=1;
          send_progress_broadcast(context,index);
        }
			  boolean has_next = true; 
			  while(has_next){
			    has_next = HttpApi.Syn.syn_next(uuid);
			    index+=1;
          send_progress_broadcast(context,index);
			  }
				return null;
			}

			public void on_success(Void v) {
				// nothing
			}

			public boolean on_unknown_exception() {
			  send_exception(context);
				return false;
			};
			
			public void on_final() {
			  send_final(context);
			};
		}.execute();

	}
	
	private void send_exception(Context context){
	   Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
	    i.putExtra("type", "exception");
	    context.sendBroadcast(i);
	}
	
	private void send_final(Context context){
	  Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
    i.putExtra("type", "final");
    context.sendBroadcast(i);
	}

	private void send_progress_broadcast(Context context, int progress) {
		Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
		i.putExtra("type", "progress");
		i.putExtra("progress", progress);
		context.sendBroadcast(i);
	}
	
	private void send_max_num(Context context, int max_num) {
	  Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
	  i.putExtra("type", "set_max");
	  i.putExtra("set_max", max_num);
	  context.sendBroadcast(i);
	}

}
