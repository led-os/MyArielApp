package com.qinggan.app.arielapp.minor.scenario;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class ToastUtil {

    private static int position=-1;
    private static int xOffset=0,yOffset=0;

    private ToastUtil() {
    }

    public static void show(CharSequence text, Context context) {
        try {
            if (text != null) {
                Toast toast=null;
                if (text.length() < 10) {
                    toast=Toast.makeText(context, text, Toast.LENGTH_SHORT);
                } else {
                    toast=makeText(context, text, Toast.LENGTH_LONG);
                }
                toast.show();
            }
        } catch (Exception e){
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    public static void show(@StringRes int resId, Context context) {
        show(context.getString(resId), context);
    }

//    public static void setPosition(int position,int xOffset,int yOffset){
//        this.position=position;
//        this.xOffset=xOffset;
//        this.yOffset=yOffset;
//    }

}