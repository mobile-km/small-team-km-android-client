package com.mindpin.cache.image;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloadTask extends AsyncTask<String, Integer, File> {
	public String image_url;
	private final WeakReference<ImageView> image_view_reference;
	
	public ImageDownloadTask(ImageView image_view){
		image_view_reference = new WeakReference<ImageView>(image_view);
	}
	
	@Override
	protected File doInBackground(String... params) {
		try {
			String image_url = params[0];
			
			File cache_file = ImageCache.get_cache_file(image_url);
			
			if(null != cache_file && !cache_file.exists()){
				URL url = new URL(image_url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				InputStream is = conn.getInputStream();
				FileUtils.copyInputStreamToFile(is, cache_file);
				is.close(); //这里有时候会取不到file
			}
			
			return cache_file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	protected void onPostExecute(File cache_file) {
		if (isCancelled()) {
			cache_file = null;
			return;
		}
		
		if (image_view_reference != null) {
			ImageView image_view = image_view_reference.get();
			if (image_view != null) {
				ImageCacheSoftRefSingleton.set_bitmap_to_imageview(cache_file, image_view);
			}
		}
		
	}

}
