package com.qinggan.app.arielapp.minor.music.imagecache;

import android.graphics.Bitmap;

public interface ImageCache {

    public Bitmap get(String key);

    public void set(String key, Bitmap bitmap);
}
