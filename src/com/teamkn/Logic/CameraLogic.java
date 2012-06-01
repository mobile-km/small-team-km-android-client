package com.teamkn.Logic;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.teamkn.base.utils.FileDirs;

public class CameraLogic {
	public final static int REQUEST_CODE_CAPTURE = 0;
	public static File IMAGE_CAPTURE_TEMP_PATH;
	public final static String HAS_IMAGE_CAPTURE = "has_image_capture";
	
	// 调用系统的照相机
	public static void call_system_camera(Activity activity) {
		Intent intent = get_photo_capture_intent();
		String name = capture_name_by_time();
		IMAGE_CAPTURE_TEMP_PATH = new File(FileDirs.TEAMKN_CAPTURE_DIR, name);
		Uri uri = Uri.fromFile(IMAGE_CAPTURE_TEMP_PATH);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		activity.startActivityForResult(intent, CameraLogic.REQUEST_CODE_CAPTURE);
	}
	
	public static Intent get_photo_capture_intent(){
		return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	}

	// 根据当前时间得到一个文件名
	private static String capture_name_by_time(){
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyyMMdd_HHmmss");
		String str = sdf.format(new Date());
		return "IMG_" + str + ".jpg";
	}
}
