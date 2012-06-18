package com.teamkn.cache.image;

import java.lang.ref.WeakReference;


public class DownloadTaskBinder {
    private final WeakReference<ImageDownloadTask> bitmapDownloaderTaskReference;

    public DownloadTaskBinder(ImageDownloadTask task) {
        bitmapDownloaderTaskReference = new WeakReference<ImageDownloadTask>(task);
    }

    public ImageDownloadTask get_binded_task() {
        return bitmapDownloaderTaskReference.get();
    }
}
