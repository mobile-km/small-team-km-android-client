package com.teamkn.Logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.teamkn.base.utils.FileDirs;

public class CompressPhoto {

	public static String get_compress_file_path(String original_file_path){
		int quality_size = TeamknPreferences.get_photo_quality();
		if(quality_size == 0){
			return original_file_path;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		//  如果该值设为true那么将不返回实际的bitmap
		//	不给其分配内存空间而里面只包括一些解码边界信息即图片大小信息	
        options.inJustDecodeBounds = true;
        //	获取这个图片的宽和高
        //	此时返回bm为空
        BitmapFactory.decodeFile(original_file_path, options); 
        options.inSampleSize = quality_size;
        
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(original_file_path,options);
        
        File file=new File(FileDirs.TEAMKN_TEMP_DIR, "upload_tmp.png");
        if(file.exists()){
        	file.delete();
        	file=new File(FileDirs.TEAMKN_TEMP_DIR, "upload_tmp.png");
        }
        try {
            FileOutputStream out=new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return file.getPath();
	}
}
