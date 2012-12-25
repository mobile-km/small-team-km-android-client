package com.teamkn.base.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Toast;

import com.teamkn.application.TeamknApplication;

public class BaseUtils {

    public static int dp_to_px(int dip) {
        Resources r = TeamknApplication.context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
    }

    @SuppressLint("SimpleDateFormat")
	public static String date_string(long time_seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("MM月d日");
        return sdf.format(new Date(time_seconds * 1000));
    }
    
    @SuppressLint("SimpleDateFormat")
	public static String time_string(long time_seconds){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("HH:mm");
        return sdf.format(new Date(time_seconds * 1000));
    }
    
	@SuppressLint("SimpleDateFormat")
	public static String friendly_time_string(long time_seconds) {
		Date date = new Date(time_seconds * 1000);
		Date now = new Date();
		
		// 如果不是当年的，显示年份
		if ( now.getYear() != date.getYear() ){
			return (date.getYear() + 1900) + "年";
		}
		
		// 是当年的，显示可读的日期，或时间，或x分钟前
		long seconds_delta = (now.getTime() - date.getTime()) / 1000; // 相差秒数
		int MM = (int) seconds_delta / 60; // 相差分钟数
		int hh = (int) MM / 60; // 相差小时数
		int dd = (int) hh / 24; // 相差天数
		
		String re;
		if (dd >= 1) {
			re = date_string(time_seconds);
		} else if (hh >= 1) {
			re = time_string(time_seconds);
		} else if (MM >= 1) {
			re = MM + "分钟前";
		} else {
			re = "1分钟前";
		}
		return re;
	}
    
    // yyyy-MM-ddTHH:mm:ssZ
    @SuppressLint("SimpleDateFormat")
	public static long parse_iso_time_string_to_long(String iso_time_string) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        return sdf.parse(iso_time_string).getTime();
    }
	public static boolean is_wifi_active(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}
//   　如果拟开发一个网络应用的程序，首先考虑是否接入网络，在Android手机中判断是否联网可以通过 ConnectivityManager 类
//    的isAvailable()方法判断，首先获取网络通讯类的实例
//    ConnectivityManager cwjManager=
//    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//   ，使用cwjManager.getActiveNetworkInfo().isAvailable(); 
//   来返回是否有效，如果为True则表示当前Android手机已经联网，可能是WiFi或GPRS、HSDPA等等，
//   具体的可以通过 ConnectivityManager 类的getActiveNetworkInfo() 方法判断详细的接入方式，
//   需要注意的是有关调用需要加入 这个权限，android开发网提醒大家在真机上Market和Browser程序都使用了这个方法
//   ，来判断是否继续，同时在一些网络超时的时候也可以检查下网络连接是否存在，以免浪费手机上的电力资源。
    
    
    // [1,2,3,4] -> "1,2,3,4"
    public static String integer_list_to_string(List<Integer> ids) {
        String res = "";
        if (ids != null) {
            for (Integer s : ids) {
                if ("".equals(res)) {
                    res = res + s;
                } else {
                    res = res + "," + s;
                }
            }
        }
        return res;
    }

    // ["1","2","3","4"] -> "1,2,3,4"
    public static String string_list_to_string(List<String> strs) {
        String res = "";
        if (strs != null) {
            for (String s : strs) {
                if ("".equals(res)) {
                    res = res + s;
                } else {
                    res = res + "," + s;
                }
            }
        }
        return res;
    }

    public static List<String> string_to_string_list(String string) {
        List<String> list = new ArrayList<String>();
        String[] arr = string.split(",");
        for (String str : arr) {
            if (!"".equals(str)) {
                list.add(str);
            }
        }
        return list;
    }

    public static List<Integer> string_to_integer_list(String string) {
        List<Integer> list = new ArrayList<Integer>();
        String[] arr = string.split(",");
        for (String str : arr) {
            if (!"".equals(str)) {
                list.add(Integer.parseInt(str));
            }
        }
        return list;
    }

    // 把字节流转换成字符串
    public static String convert_stream_to_string(InputStream is) {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,
                        "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (Exception e) {
                return "";
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    //判断字符串非空
    public static boolean is_str_blank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // 快速显示一个toast
    public static void toast(int string_resource_id) {
        Toast toast = Toast.makeText(
                TeamknApplication.context,
                string_resource_id,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void toast(String string) {
        Toast toast = Toast.makeText(
                TeamknApplication.context,
                string,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static Bitmap to_round_corner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static String location_to_string(Location location) {
        if (location == null) {
            return "";
        }
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        return lat + "," + lng;
    }


    public static String get_file_path_from_image_uri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        //好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = MediaStore.Images.Media.query(TeamknApplication.context.getContentResolver(),
                uri, proj);
        //按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径
        return cursor.getString(column_index);
    }
    
    public static String file_path_join(String ... strs){
      if(strs.length == 0) return "";
      
      File f = null;
        
      for(String str : strs){
        if(null == f){
          f = new File(str);
        }else{
          f = new File(f, str);
        }
      }
      return f.getPath();
    }

}