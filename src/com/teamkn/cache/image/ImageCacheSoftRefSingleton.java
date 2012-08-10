package com.teamkn.cache.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageCacheSoftRefSingleton {
    private static ImageCacheSoftRefSingleton instance = new ImageCacheSoftRefSingleton();

    private Map<File, SoftReference<Bitmap>> used_bitmap_list;

    private ImageCacheSoftRefSingleton() {
        used_bitmap_list = Collections.synchronizedMap(new LinkedHashMap<File, SoftReference<Bitmap>>());
    }

    
    
    // 解读一个文件为 bitmap 并放入 image_view
    // 如果页面上产生了太多的 image_view 和 bitmap 就会导致内存溢出
    // 因此需要手动管理他们的内存释放
    final static void set_bitmap_to_imageview(File cache_file, ImageView image_view) {
        Bitmap img_bitmap;
        try {
            if (null == image_view) return;

            img_bitmap = get_bitmap_from_file(cache_file);
            image_view.setImageBitmap(img_bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            img_bitmap = null;
        }
    }

    final static private Bitmap get_bitmap_from_file(File cache_file) {
        Bitmap img_bitmap;
        Map<File, SoftReference<Bitmap>> used_bitmap_list = instance.used_bitmap_list;

        // 先尝试从MAP中获取
        if (used_bitmap_list.containsKey(cache_file)) {
            SoftReference<Bitmap> ref = used_bitmap_list.get(cache_file);
            if (null != ref) {
                img_bitmap = ref.get();
                if (null != img_bitmap) {
                    return img_bitmap;
                }
            }
        }

        try {
            FileInputStream stream = new FileInputStream(cache_file);
            img_bitmap = BitmapFactory.decodeStream(stream);

            if (null == img_bitmap) {
                cache_file.delete();
            }

            clear_used_bitmap_list();
            used_bitmap_list.put(cache_file, new SoftReference<Bitmap>(img_bitmap));

            return img_bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            return null;
        }
    }

    final static private void clear_used_bitmap_list() {
        Map<File, SoftReference<Bitmap>> used_bitmap_list = instance.used_bitmap_list;

        int size = used_bitmap_list.size();
        if (size > 100) {
            System.gc();
            Iterator<File> it = used_bitmap_list.keySet().iterator();
            used_bitmap_list.remove(it.next());
        }
    }
}
