/*
 * 文 件 名:  WLog.java
 * 版    权:  Pateo Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  zixiangliu
 * 修改时间:  2014-4-25
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.qinggan.app.arielapp.utils;

import android.util.Log;

import com.qinggan.app.arielapp.BuildConfig;

/**
 * <日志工具类>
 * <日志工具类，对原先的Log进行相关的封装>
 *
 * @author zixiangliu
 * @version [版本号, 2014-4-25]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class WLog {

    private static boolean DEBUG_ENABLE = BuildConfig.DEBUG;
    private static final int CHUNK_SIZE = 4000;

    private static final int VERBOSE = android.util.Log.VERBOSE;
    private static final int DEBUG = android.util.Log.DEBUG;
    private static final int INFO = android.util.Log.INFO;
    private static final int WARN = android.util.Log.WARN;
    private static final int ERROR = android.util.Log.ERROR;

    private static String moduleName = "ArielApp";
    private static boolean isLongSupport = false;

    public static void setLongContentSupport(boolean support) {
        isLongSupport = support;
    }

    public static void v(String tag, String str) {
        if (DEBUG_ENABLE) {
            logContent(VERBOSE, moduleName, tag, str);
        }
    }

    public static void d(String tag, String str) {
        if (DEBUG_ENABLE) {
            logContent(DEBUG, moduleName, tag, str);
        }
    }

    public static void i(String tag, String str) {
        if (DEBUG_ENABLE) {
            logContent(INFO, moduleName, tag, str);
        }
    }

    public static void e(String tag, String str) {
        if (DEBUG_ENABLE) {
            logContent(ERROR, moduleName, tag, str);
        }
    }

    public static void w(String tag, String str) {
        if (DEBUG_ENABLE) {
            logContent(WARN, moduleName, tag, str);
        }
    }

    private static void logContent(int type, String moduleName, String tag, String content) {
        if (content == null || content.isEmpty()) {
            return;
        }

        if (!isLongSupport) {
            logPrint(type, moduleName, tag, content);
            return;
        }

        int length = content.length();

        if (length < CHUNK_SIZE) {
            logPrint(type, moduleName, tag, content);
        } else {
            for (int i = 0; i < length; i += CHUNK_SIZE) {
                int count = Math.min(length - i, CHUNK_SIZE);
                String msg = new String(content.getBytes(), i, count);
                logPrint(type, moduleName, tag, msg);
            }
        }
    }

    private static void logPrint(int type, String moduleName, String tag, String content) {
        switch (type) {
            case VERBOSE:
                android.util.Log.v(moduleName, "[ " + tag + " ] : " + content);
                break;
            case DEBUG:
                android.util.Log.d(moduleName, "[ " + tag + " ] : " + content);
                break;
            case INFO:
                android.util.Log.i(moduleName, "[ " + tag + " ] : " + content);
                break;
            case WARN:
                android.util.Log.w(moduleName, "[ " + tag + " ] : " + content);
                break;
            case ERROR:
                android.util.Log.e(moduleName, "[ " + tag + " ] : " + content);
                break;
            default:
                break;
        }
    }

    public static void printCallStack() {
        java.util.Map<Thread, StackTraceElement[]> ts = Thread.getAllStackTraces();
        StackTraceElement[] ste = ts.get(Thread.currentThread());
        for (StackTraceElement s : ste) {
          Log.e("CallStack",s.toString());
        }
    }
}
