package com.qinggan.app.arielapp.ui.weather;

import android.text.TextUtils;

import com.qinggan.app.arielapp.R;


/**
 * Description: 天气工具类
 * Author: Seven
 * Date: 17-5-30.
 */

public class WeatherHelper {
    public static int getWeatherIcon(String weather) {
        String item = weather;
        if (item.contains("大雪") || item.contains("暴雪"))
            return R.drawable.weather_heavy_snow;
        else if (item.contains("中雪"))
            return R.drawable.weather_moderate_snow;
        else if (item.contains("小雪"))
            return R.drawable.weather_light_snow;
        else if (item.contains("阵雪"))
            return R.drawable.weather_showery_snow;
        else if (item.contains("冰雹"))
            return R.drawable.weather_hail;
        else if (item.contains("大雨") || item.contains("暴雨"))
            return R.drawable.weather_heavy_rain;
        else if (item.contains("中雨"))
            return R.drawable.weather_moderate_rain;
        else if (item.contains("小雨"))
            return R.drawable.weather_light_rain;
        else if (item.contains("阵雨"))
            return R.drawable.weather_showery_rain;
        else if (item.contains("雷阵雨"))
            return R.drawable.weather_thundershower;
        else if (item.contains("雨夹雪"))
            return R.drawable.weather_sleet;
        else if (item.contains("晴"))
            return R.drawable.weather_sun;
        else if (item.contains("多云"))
            return R.drawable.weather_cloudy;
        else if (item.contains("阴"))
            return R.drawable.weather_overcast;
        else if (item.contains("雾") || item.contains("霾"))
            return R.drawable.weather_fog;
        else if (item.contains("飓风"))
            return R.drawable.weather_hurricane;
        else if (item.contains("沙尘暴"))
            return R.drawable.weather_dust_storm;
        else {
            return R.drawable.weather_no_weather;
        }
    }

    public static String getAirQuality(String airQuality) {
        String temp = airQuality;
        try {
            int tmp = Integer.valueOf(airQuality);
            if (tmp < 50) {
                temp = "优 " + temp;
            } else if (tmp < 100) {
                temp = "良 " + temp;
            } else if (tmp < 150) {
                temp = "轻度污染 " + temp;
            } else if (tmp < 200) {
                temp = "中度污染 " + temp;
            } else if (tmp < 250) {
                temp = "重度污染 " + temp;
            } else {
                temp = "严重污染 " + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static String getDate(String date) {
        String tmp = date.substring(3, date.length());
        tmp = tmp.replace("月", " / ");
        tmp = tmp.replace("日", "");
        return tmp;
    }

    public static String getSubDate(String date) {
        String tmp = date.substring(5);
        return tmp;
    }

    public static String getWeek(String date) {
        String week = "";
        if ("MON".equalsIgnoreCase(date))
            week = "周一";
        else if ("TUE".equalsIgnoreCase(date))
            week = "周二";
        else if ("WED".equalsIgnoreCase(date))
            week = "周三";
        else if ("THU".equalsIgnoreCase(date))
            week = "周四";
        else if ("FRI".equalsIgnoreCase(date))
            week = "周五";
        else if ("SAT".equalsIgnoreCase(date))
            week = "周六";
        else if ("SUN".equalsIgnoreCase(date))
            week = "周日";
        return week;
    }

    public static String getWeatherText(String low, String high) {
        String tmp = null;
        if (!TextUtils.isEmpty(low)) {
            tmp = low;
            if (!TextUtils.isEmpty(high)) {
                tmp += "~" + high + "℃";
            }
        }
        return tmp;
    }

    public static String getDayWeatherText(String low, String high) {
        String tmp = null;
        if (!TextUtils.isEmpty(low)) {
            tmp = low;
            if (!TextUtils.isEmpty(high)) {
                tmp += "° ~ " + high + "°";
            }
        }
        return tmp;
    }
}
