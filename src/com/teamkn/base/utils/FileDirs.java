package com.teamkn.base.utils;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    //从byte[]转file
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
         File file = null;
         try {
        file = new File(outputFile);
             FileOutputStream fstream = new FileOutputStream(file);
             stream = new BufferedOutputStream(fstream);
             stream.write(b);
         } catch (Exception e) {
             e.printStackTrace();
        } finally {
            if (stream != null) {
                 try {
                    stream.close();
                 } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
         return file;
     }
   //从file转为byte[]
    public static byte[] getBytesFromFile(File f){
       if (f == null){
           return null;
      }
       try {
           FileInputStream stream = new FileInputStream(f);
           ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
           byte[] b = new byte[1000];
           int n;
           while ((n = stream.read(b)) != -1)
               out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e){
       }
        return null;
     }

    public final static File TEAMKN_DIR = get_or_create_dir("/teamkn");
    public final static File TEAMKN_TEMP_DIR = get_or_create_dir("/teamkn/temp");
    public final static File TEAMKN_NOTES_DIR = get_or_create_dir("/teamkn/notes");
    public final static File TEAMKN_CAPTURE_DIR = get_or_create_dir("/teamkn/capture");
    public final static File TEAMKN_CAPTURE_TEMP_DIR = get_or_create_dir("/teamkn/capture_temp");
    public final static File TEAMKN_IMAGE_CACHE_DIR = get_or_create_dir("/teamkn/image_cache");
    
    public final static File TEAMKN_CHATS_DIR = get_or_create_dir("/teamkn/chats");
}
