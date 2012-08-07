package com.teamkn.cache.image;

import android.widget.ImageView;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.FileDirs;

import java.io.File;
import java.net.URI;

public class ImageCache {
  
    final static public void load_cached_image(File file, ImageView image_view){
      image_view.setTag(null); //这一句不能漏，否则可能图片错位
      ImageCacheSoftRefSingleton.set_bitmap_to_imageview(file, image_view);
    }

    // 尝试在传入的view上载入指定的url的图片
    final static public void load_cached_image(String image_url, ImageView image_view) {
        File cache_file = ImageCache.get_cache_file(image_url);

        if (null == cache_file || !cache_file.exists()) {
            download(image_url, image_view);
        } else {
            image_view.setTag(null); //这一句不能漏，否则可能图片错位
            ImageCacheSoftRefSingleton.set_bitmap_to_imageview(cache_file, image_view);
        }
    }

    // 调用asyncTask下载图片
    final static private void download(String image_url, ImageView image_view) {
        if (cancel_download(image_url, image_view)) {
            ImageDownloadTask task = new ImageDownloadTask(image_view);
            DownloadTaskBinder downloadedDrawable = new DownloadTaskBinder(task);
            image_view.setTag(downloadedDrawable);
            task.execute(image_url);
        }
    }

    private static boolean cancel_download(String image_url, ImageView image_view) {
        ImageDownloadTask task = get_binded_task(image_view);
        if (task != null) {
            String task_image_url = task.image_url;

            if (BaseUtils.is_str_blank(task_image_url) || !task_image_url.equals(image_url)) {
                task.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static ImageDownloadTask get_binded_task(ImageView image_view) {
        if (image_view != null) {
            DownloadTaskBinder ref = (DownloadTaskBinder) image_view.getTag();
            if (null != ref) {
                return ref.get_binded_task();
            }
        }
        return null;
    }


    // 根据图像url，获取本地的磁盘缓存文件路径
    // 规则是：
    // ->
    // /teamkn/image_cache/ uri.hashCode()+".cache"
    public static File get_cache_file(String image_url) {
        try {
            URI uri = new URI(image_url);
            String filename = uri.hashCode() + ".cache";
            File cache_file = new File(
                    FileDirs.TEAMKN_IMAGE_CACHE_DIR, filename);

            return cache_file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
