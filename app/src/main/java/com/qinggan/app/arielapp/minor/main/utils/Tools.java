package com.qinggan.app.arielapp.minor.main.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/***
 * 公共工具类
 *
 * Alan
 * ***/
public class Tools {


    //dp 转 px
    public static int getPixelsFromDp(AppCompatActivity baseActivity, int size){

        DisplayMetrics metrics =new DisplayMetrics();

        baseActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return(size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;

    }


    //判断SDK是否在6.0以上
    public static boolean getSdkVersionSix() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
    /**
     *
     * @param
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        StringBuffer time=new StringBuffer();
        if(days!=0){
            time.append(days+"天");
        }
        if(hours!=0){
            time.append(hours+"小时");
        }
        if(minutes!=0){
            time.append( minutes + "分钟 ");
        }
        return time+"";
    }


    //获取手机电池状态 by Alan
    public static class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if(action.equals(Intent.ACTION_POWER_CONNECTED)){//电源插入
                    Log.d("Alan", "power connected");
                    EventBus.getDefault().post(new EventBusBean("POWER","1"));
                }else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)){//电源断开
                    Log.e("Alan", "power disconnected");
                    EventBus.getDefault().post(new EventBusBean("POWER","0"));
                }else if(action.equals(Intent.ACTION_BATTERY_CHANGED)){//获取当前电量
                    int current=intent.getExtras().getInt("level");//获得当前电量
                    int total=intent.getExtras().getInt("scale");//获得总电量
                    int percent=current*100/total;
                    // 是否在充电
                    int status = intent.getExtras().getInt(BatteryManager.EXTRA_STATUS, -1);

                    EventBus.getDefault().post(new EventBusBean("PhoneState",percent,getTime(),getDate(),getWeek(),status));

                }


            }
        }
    }
    //获取当前时间
    public static String getTime(){
        String str="";
        SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        str = formatter.format(curDate);
        return str;
    }
    //获取当前时间
    public static String getDate(){
        String str="";
        SimpleDateFormat formatter = new SimpleDateFormat ("MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        str = formatter.format(curDate);
        return str;
    }

    /*获取星期几*/
    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

/*****
 *
 * 计算中英文混输字符串长度
 * ***/
    public static int calStrLen(String str) {
        int m = 0; char arr[] = str.toCharArray();
        for (char c : arr) {
        if (String.valueOf(c).matches("[\u4e00-\u9fa5]")) //中文字符
             {
                 m = m + 2;
             } else {
            m = m + 1;
        }
        }
        return m;
    }

    /**
     * 调整图片大小
     *
     * @param bitmap
     *            源
     * @param dst_w
     *            输出宽度
     * @param dst_h
     *            输出高度
     * @return
     */ public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
         int src_w = bitmap.getWidth(); int src_h = bitmap.getHeight();
         float scale_w = ((float) dst_w) / src_w;
         float scale_h = ((float) dst_h) / src_h;
         Matrix matrix = new Matrix();
         matrix.postScale(scale_w, scale_h);
         Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);
         return dstbmp;
     }



}
