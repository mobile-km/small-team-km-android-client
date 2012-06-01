package com.teamkn.receiver;

import java.util.Timer;
import java.util.TimerTask;

import com.teamkn.Logic.HttpApi;
import com.teamkn.base.task.TeamknAsyncTask;

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
				Timer timer = new Timer();

				try {
					TimerTask timer_task = new TimerTask() {
						@Override
						public void run() {
							send_progress_broadcast(context, 1);
						}
					};
					timer.schedule(timer_task, 50, 50);
					HttpApi.mobile_data_syn();

					send_progress_broadcast(context, 100);

					Thread.sleep(500);

					return null;
				} catch (Exception e) {
					throw e;
				} finally {
					timer.cancel();
				}
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
