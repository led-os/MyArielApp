package com.qinggan.app.arielapp.minor.music.imagecache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiskCache implements ImageCache {

    private String filepath = Environment.getExternalStorageDirectory().toString() + "/Imageloder/";

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = BitmapFactory.decodeFile(filepath + url);
        if (bitmap == null) {
            return null;
        } else {
            return bitmap;
        }
    }

    @Override
    public void set(String url, Bitmap bitmap) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Imageloder");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = url;
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
