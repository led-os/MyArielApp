package com.qinggan.app.arielapp.minor.music.imagecache;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qinggan.app.arielapp.minor.utils.ArielLog;
import com.qinggan.app.arielapp.minor.utils.BitmapUtis;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private ImageCache mImageCache;
    private ExecutorService mExecutorService;

    private List<AlbumDownloadCallback> mDownloadCallbackList;

    private List<String> mRunningList;

    private ImageLoader(){
        mImageCache = new MemoryCache();
        mDownloadCallbackList = new ArrayList<>();
        mRunningList = new ArrayList<>();
        mExecutorService =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void addDownloadCallback(AlbumDownloadCallback callBack){
        mDownloadCallbackList.add(callBack);
    }

    public void removeDownloadCallback(AlbumDownloadCallback callback){
        mDownloadCallbackList.remove(callback);
    }


    public void display(String url, ImageView imageView, String key) {
        ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--ImageLoader--display",
                "key = " + key);
        Bitmap bitmap = mImageCache.get(key);
        if (bitmap != null) {
            ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--ImageLoader--display",
                    "bitmap is not null");
            imageView.setImageBitmap(bitmap);
            return;
        }


        submitLoadRequest(url, imageView, key);
    }

    public void downloadWithoutDisplay(final String url, final String key){
        ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--ImageLoader--downloadWithoutDisplay",
                "key = " + key);

        for (String tempUrl:mRunningList) {
            if (tempUrl.equalsIgnoreCase(url)){
                ArielLog.logMusic(ArielLog.LEVEL_DEBUG,
                        "Music--ImageLoader--downloadWithoutDisplay",
                        "download " + url + ", has started, ignored.");
                return;
            }
        }

        ArielLog.logMusic(ArielLog.LEVEL_DEBUG,
                "Music--ImageLoader--downloadWithoutDisplay",
                "download " + url + ", started!!");

        mRunningList.add(url);
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImag(url, key);
                if (bitmap == null) {
                    mRunningList.remove(url);
                    dispatchDownloadFailed(key);
                    return;
                }

                mImageCache.set(key, bitmap);
                mRunningList.remove(url);
                dispatchDownloadSuccess(key);
            }
        });
    }

    private static volatile ImageLoader instance = null;

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                instance = new ImageLoader();
            }
        }
        return instance;
    }

    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    public ImageCache getImageCache() {
        return mImageCache;
    }

    private void submitLoadRequest(final String url, final ImageView imageView, final String key) {
        for (String tempUrl:mRunningList) {
            if (tempUrl.equalsIgnoreCase(url)){
                ArielLog.logMusic(ArielLog.LEVEL_DEBUG,
                        "Music--ImageLoader--submitLoadRequest",
                        "download " + url + ", has started, ignored.");
                return;
            }
        }

        ArielLog.logMusic(ArielLog.LEVEL_DEBUG,
                "Music--ImageLoader--submitLoadRequest",
                "download " + url + ", started!!");

        mRunningList.add(url);
        imageView.setTag(url);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImag(url, key);
                if (bitmap == null) {
                    mRunningList.remove(url);
                    dispatchDownloadFailed(key);
                    return;
                }

                mImageCache.set(key, bitmap);
                mRunningList.remove(url);
                dispatchDownloadSuccess(key);
                if (imageView.getTag().equals(url)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }

    private Bitmap downloadImag(String url, String key) {
        Bitmap bitmap = null;
        try {
            URL url1 = new URL(url);
            final HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private synchronized void dispatchDownloadSuccess(String key){
        for (AlbumDownloadCallback callback : mDownloadCallbackList) {
            if (callback != null) {
                callback.onAlbumDownloadSuccess(key);
            }
        }
    }

    private synchronized void dispatchDownloadFailed(String key){
        for (AlbumDownloadCallback callback : mDownloadCallbackList) {
            if (callback != null) {
                callback.onAlbumDownloadFailed(key);
            }

        }
    }

    public void display2(String url, ImageView imageView, RelativeLayout rl, String key) {
        Bitmap bitmap = mImageCache.get(key);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Bitmap afterBlur = BitmapUtis.doBlur(bitmap,10,20);
            rl.setBackground(new BitmapDrawable(afterBlur));
            return;
        }
        submitLoadRequest2(url, imageView, rl,key);
    }

    private void submitLoadRequest2(final String url, final ImageView imageView,final RelativeLayout rl,final String key) {
        imageView.setTag(url);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImag(url, key);
                if (bitmap == null) {
                    mRunningList.remove(url);
                    dispatchDownloadFailed(key);
                    return;
                }

                mImageCache.set(key, bitmap);
                mRunningList.remove(url);
                dispatchDownloadSuccess(key);
                if (imageView.getTag().equals(url)) {
                    imageView.setImageBitmap(bitmap);
                    Bitmap afterBlur = BitmapUtis.doBlur(bitmap,10,20);
                    rl.setBackground(new BitmapDrawable(afterBlur));
                    return;
                }
            }
        });
    }



    public interface AlbumDownloadCallback{
        void onAlbumDownloadSuccess(String key);
        void onAlbumDownloadFailed(String key);
    }
}
