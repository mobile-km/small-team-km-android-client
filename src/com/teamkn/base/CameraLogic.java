package com.teamkn.base;

import java.io.File;
import com.teamkn.base.utils.FileDirs;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class CameraLogic {
  public final static File IMAGE_CAPTURE_TEMP_FILE = new File(FileDirs.TEAMKN_CAPTURE_TEMP_DIR, "IMG_TEMP.jpg");
  public final static String HAS_IMAGE_CAPTURE = "has_image_capture";

  // 调用系统的照相机
  public static void call_system_camera(Activity activity,int requestCode) {
    Intent intent = get_photo_capture_intent();
    Uri uri = Uri.fromFile(IMAGE_CAPTURE_TEMP_FILE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    activity.startActivityForResult(intent, requestCode);
  }

  public static Intent get_photo_capture_intent() {
    return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  }
}
