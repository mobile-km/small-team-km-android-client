package com.teamkn.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.teamkn.base.search.Indexer;
import com.teamkn.model.Note;

public class IndexService extends Service {
    private static Intent intent;
    private static Activity start_activity;
    public static IndexHandler handler = new IndexHandler();

    public static void start(Activity activity) {
        intent = new Intent(activity, IndexService.class);
        start_activity = activity;
        start_activity.startService(intent);
    }

    public static void stop() {
        if (start_activity != null) {
            start_activity.stopService(intent);
        }
    }

    public static Message obtain_index_request(Note note, int action) {
    	System.out.println("action = " + action + " : " + note.uuid);
        return Message.obtain(handler, action, note);
    }

    public static Message obtain_index_request() {
        return obtain_index_request(null, IndexHandler.action.ALL);
    }

    public static void build_all() {
        try {
            if (! Indexer.index_exists()) {
                obtain_index_request().sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        System.out.print("from index service: service created, oh yeah~~~~");
    }

    @Override
    public void onDestroy() {
        try {
            Indexer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class IndexHandler extends Handler {
        public static class action {
            public static final int ADD    = 0;
            public static final int DELETE = 1;
            public static final int UPDATE = 2;
            public static final int ALL    = 3;
        }

        @Override
        public void handleMessage(Message message) {
            IndexRunnable runnable = new IndexRunnable(message.what,
                                                       (Note) message.obj);
            post(runnable);
        }
    }

    public static class IndexRunnable implements Runnable {
        private final int action;
        private final Note note;

        public IndexRunnable(int action_input, Note note_input) {
            action = action_input;
            note   = note_input;
        }
        @Override
        public void run() {
            System.out.print("from index service: Sir, Runnable anonymous is serving your request!");
            // arg1 -> 0, 1, 2 -> 0: add; 1: delete; 2: update.
            try {
                if (action != IndexHandler.action.ALL && ! Indexer.index_exists()) {
                    Indexer.index_notes();
                    Indexer.commit();
                    return;
                }

                switch (action) {
                    case IndexHandler.action.ADD:
                        Indexer.add_index((Note) note);
                        break;
                    case IndexHandler.action.DELETE:
                        Indexer.delete_index((Note) note);
                        break;
                    case IndexHandler.action.UPDATE:
                        Indexer.update_index((Note) note);
                        break;
                    case IndexHandler.action.ALL:
                        Indexer.index_notes();
                        break;
                }

                Indexer.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

