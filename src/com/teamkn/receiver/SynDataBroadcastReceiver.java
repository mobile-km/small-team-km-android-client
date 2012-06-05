package com.teamkn.receiver;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
			  String uuid = HttpApi.Syn.handshake();
			  List<Note> list = NoteDBHelper.all(true);
			  
			  for (Iterator<Note> iterator = list.iterator(); iterator.hasNext();) {
          Note note = iterator.next();
          HttpApi.Syn.compare(uuid, note);
        }
			  boolean has_next = true; 
			  while(has_next){
			    has_next = HttpApi.Syn.syn_next(uuid);
			  }
			  
				send_progress_broadcast(context, 100);
				return null;
			}

			public void on_success(Void v) {
				// nothing
			}

			public boolean on_unknown_exception() {
				send_progress_broadcast(context, -1);
				return false;
			};
			
			public void on_final() {
				send_progress_broadcast(context, 101);
			};
		}.execute();

	}

	private void send_progress_broadcast(Context context, int progress) {
		Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
		i.putExtra("progress", progress);
		context.sendBroadcast(i);
	}

}
