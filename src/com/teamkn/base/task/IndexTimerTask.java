package com.teamkn.base.task;

import com.teamkn.base.search.Indexer;

import java.util.TimerTask;

public class IndexTimerTask extends TimerTask {
    public static final long SCHEDULE_INTERVAL = 600000;

    @Override
    public void run() {
        try {
            if (! Indexer.index_exists()) {
                Indexer.index_notes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
