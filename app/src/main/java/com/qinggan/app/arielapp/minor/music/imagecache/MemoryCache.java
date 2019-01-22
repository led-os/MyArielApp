package com.qinggan.app.arielapp.minor.music.imagecache;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class MemoryCache implements ImageCache {

    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCache() {
        final int max = (int) (Runtime.getRuntime().maxMemory() / 1024);
        mMemoryCache = new LruCache<String, Bitmap>(max / 4) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    @Override
    public Bitmap get(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap == null) {
        } else {
        }
        return bitmap;
    }

    @Override
    public void set(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
    }

}
