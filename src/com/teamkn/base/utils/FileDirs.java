package com.teamkn.base.utils;

import android.os.Environment;

import java.io.File;

public class FileDirs {

    // 尝试获得一个文件夹引用，如果文件夹不存在就创建该文件夹
    public static File get_or_create_dir(String path) {
        File dir = new File(
                Environment.getExternalStorageDirectory().getPath() + path
        );
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public final static File TEAMKN_DIR = get_or_create_dir("/teamkn");
    public final static File TEAMKN_TEMP_DIR = get_or_create_dir("/teamkn/temp");
    public final static File TEAMKN_NOTES_DIR = get_or_create_dir("/teamkn/notes");
    public final static File TEAMKN_CAPTURE_DIR = get_or_create_dir("/teamkn/capture");
    public final static File TEAMKN_CAPTURE_TEMP_DIR = get_or_create_dir("/teamkn/capture_temp");
    public final static File TEAMKN_IMAGE_CACHE_DIR = get_or_create_dir("/teamkn/image_cache");
}
