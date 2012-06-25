package com.teamkn.base.task;

import com.teamkn.service.IndexService;

import java.util.Timer;
import java.util.TimerTask;

public class IndexTimerTask extends TimerTask {
    public static final long SCHEDULE_INTERVAL = 600000;

    public static void index_task(long interval) {
        Timer index_timer = new Timer();
        index_timer.scheduleAtFixedRate(new IndexTimerTask(),
                                        0,
                                        interval);
    }

    @Override
    public void run() {
        try {
            IndexService.build_all();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
