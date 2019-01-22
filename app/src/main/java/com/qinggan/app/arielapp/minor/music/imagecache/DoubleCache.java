package com.qinggan.app.arielapp.minor.music.imagecache;

import android.graphics.Bitmap;

public class DoubleCache implements ImageCache {
    private ImageCache mMemoryCache;
    private ImageCache mDiskCache;

    public DoubleCache() {
        mMemoryCache = new MemoryCache();
        mDiskCache = new DiskCache();
    }

    private static volatile DoubleCache instance = null;

    public static DoubleCache getCacheInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                instance = new DoubleCache();
            }
        }
        return instance;
    }

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap == null) {
            bitmap = mDiskCache.get(url);
        }
        return bitmap;
    }

    @Override
    public void set(String url, Bitmap bitmap) {
        mMemoryCache.set(url, bitmap);
        mDiskCache.set(url, bitmap);
    }
}
