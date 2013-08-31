package edu.SJTU.ChiChi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import edu.SJTU.ChiChi.R;

public class ImageLoader {

	BitmapCache mCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    public ImageLoader(Context context) {
    	mCache = new BitmapCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    final int stub_id = R.drawable.loading;

    public void DisplayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = mCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(url, imageView);
            //imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    public Bitmap getBitmap(String url) {
        //from SD cache
        Bitmap b = mCache.get(url);
        if (b != null) return b;
        //from web
        b = downloadBitmap(url);
        mCache.put(url, b);
        return b;
    }

    public Bitmap preloadBitmap(String url) {
        Bitmap b = mCache.get(url);
        if (b != null) return b;
        b = downloadBitmap(url);
        mCache.putOnlyDisk(url, b);
        return b;
    }

    public Bitmap downloadBitmap(String url) {
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            int length = (int) conn.getContentLength();
            if (length != -1) {
                byte[] imgData = new byte[length];
                byte[] buffer = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(buffer)) > 0) {
                    System.arraycopy(buffer, 0, imgData, destPos, readLen);
                    destPos += readLen;
                }
                bitmap = BitmapFactory.decodeByteArray(imgData, 0,
                        imgData.length);
            }
            return bitmap;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            //else
                //photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
    }

}