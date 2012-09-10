package com.teamkn.base.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDirs {
	// 拷贝文件
	
    public static void copyfile(File fromFile, File toFile,Boolean rewrite ){
    	if (!fromFile.exists()) {
            return;
    	}
    	if (!fromFile.isFile()) {
    		return ;
    	}
    	if (!fromFile.canRead()) {
    		return ;
    	}
    	if (!toFile.getParentFile().exists()) {
    		toFile.getParentFile().mkdirs();
    	}
    	if (toFile.exists() && rewrite) {
    		toFile.delete();
    	}
//  　　当文件不存时，canWrite一直返回的都是false
     // if (!toFile.canWrite()) {
      // MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" + toFile.getPath());
     // Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);
    	// return ;
      // }
		try {
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			java.io.FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); //将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
		} catch (Exception ex) {
			Log.e("readfile", ex.getMessage());
		}
	}
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
    public final static File TEAMKN_DATA_ITEM_DIR = get_or_create_dir("/teamkn/dataitems");
    
    public final static File TEAMKN_TEMP_DIR = get_or_create_dir("/teamkn/temp");
    public final static File TEAMKN_NOTES_DIR = get_or_create_dir("/teamkn/notes");
    public final static File TEAMKN_CAPTURE_DIR = get_or_create_dir("/teamkn/capture");
    public final static File TEAMKN_CAPTURE_TEMP_DIR = get_or_create_dir("/teamkn/capture_temp");
    public final static File TEAMKN_IMAGE_CACHE_DIR = get_or_create_dir("/teamkn/image_cache");
    
    public final static File TEAMKN_CHATS_DIR = get_or_create_dir("/teamkn/chats");
}
