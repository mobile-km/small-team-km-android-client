package com.teamkn.application;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.teamkn.base.search.Indexer;
import com.teamkn.base.task.IndexTimerTask;

public class TeamknApplication extends Application {
    public static Context context;
    public static LayoutInflater mInflater;
    public static String current_show_activity;

    public static View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        return mInflater.inflate(resource, root, attachToRoot);
    }

    public static View inflate(int resource, ViewGroup root) {
        return mInflater.inflate(resource, root);
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        mInflater = LayoutInflater.from(context);
        Indexer.index_task(IndexTimerTask.SCHEDULE_INTERVAL);
        super.onCreate();
    }

    final public static String now_loading = "正在载入…";
    final public static String now_sending = "正在发送…";
}
