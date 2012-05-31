package com.teamkn.base.utils.location;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Location;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;
import com.teamkn.Logic.AccountManager;
import com.teamkn.cache.image.ImageCache;

public class AvatarMyLocationOverlay extends MyLocationOverlay {

	public AvatarMyLocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
	}
	
	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView,
			Location lastFix, GeoPoint myLocation, long when) {
//		super.drawMyLocation(canvas, mapView, lastFix, myLocation, when);
		  Projection pj=mapView.getProjection();
	        if (lastFix != null) {
	                Point point = pj.toPixels(myLocation, null);
					final float radius = pj.metersToEquatorPixels(lastFix.getAccuracy());
					Paint paint = new Paint();
					paint.setAntiAlias(true);
					paint.setARGB(35, 131, 182, 222);
					paint.setAlpha(50);
					paint.setStyle(Style.FILL);
					canvas.drawCircle(point.x, point.y, radius, paint);
					paint.setARGB(225, 131, 182, 222);
					paint.setAlpha(150);
					paint.setStyle(Style.STROKE);
					canvas.drawCircle(point.x, point.y, radius, paint);
					
					File cache_file = ImageCache.get_cache_file(AccountManager.current_user().avatar_url);
					Bitmap bitmap = BitmapFactory.decodeFile(cache_file.getPath());
					
					canvas.drawBitmap(bitmap, point.x-bitmap.getWidth()/2, point.y-bitmap.getHeight(), new Paint());
			}
	}

}
