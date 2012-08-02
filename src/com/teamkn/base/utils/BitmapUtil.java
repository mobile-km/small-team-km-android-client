package com.teamkn.base.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

 public class BitmapUtil {  
      static boolean  saveBitmap2file(Bitmap bmp,String filename){  
          CompressFormat format= Bitmap.CompressFormat.JPEG;  
          int quality = 100;  
          OutputStream stream = null;  
          try {  
                 stream = new FileOutputStream("/sdcard/" + filename);  
          } catch (FileNotFoundException e) {  
                 e.printStackTrace();  
          }  
   
          return bmp.compress(format, quality, stream);  
          }  
   
 }
