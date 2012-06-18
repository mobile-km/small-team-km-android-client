package com.teamkn.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
    private ImageView view;

    public DownloadImageTask(ImageView view) {
        this.view = view;
    }

    protected Bitmap doInBackground(String... arg0) {
        String url = arg0[0];
        return get_bitmap(url);
    }

    private Bitmap get_bitmap(String image_url) {
        Bitmap mBitmap = null;
        try {
            URL url = new URL(image_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            mBitmap = BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return mBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return mBitmap;
        }
        return mBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        view.setImageBitmap(result);
    }
}
