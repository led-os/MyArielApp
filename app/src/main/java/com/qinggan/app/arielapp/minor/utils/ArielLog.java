package com.qinggan.app.arielapp.minor.utils;

import android.util.Log;

/**
 * Created by brian on 18-11-2.
 */

public class ArielLog {
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_CENTER = true;
    public static final boolean DEBUG_CONTROLLER = true;
    public static final boolean DEBUG_CORE = true;
    public static final boolean DEBUG_DB = true;
    public static final boolean DEBUG_MUSIC = true;
    public static final boolean DEBUG_NAVI = true;
    public static final boolean DEBUG_PHONE = true;
    public static final boolean DEBUG_RADIO = true;
    public static final boolean DEBUG_SCENARIO = true;

    public static final int LEVEL_INFO = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_ERROR = 2;

    public static void logPhone(int level, String tag, String content){
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_PHONE) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_PHONE) {
                    Log.d(tag, content);
                }
                break;
        }
    }


    public static void logCore(int level, String tag, String content) {
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_CORE) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_CORE) {
                    Log.d(tag, content);
                }
                break;
        }
    }

    public static void logMusic(int level, String tag, String content){
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_MUSIC) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_MUSIC) {
                    Log.d(tag, content);
                }
                break;
        }
    }

    public static void logNavi(int level, String tag, String content){
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_NAVI) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_NAVI) {
                    Log.d(tag, content);
                }
                break;
        }
    }

    public static void logCenter(int level, String tag, String content){
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_CENTER) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_CENTER) {
                    Log.d(tag, content);
                }
                break;
        }
    }

    public static void logController(int level, String tag, String content){
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_CONTROLLER) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_CONTROLLER) {
                    Log.d(tag, content);
                }
                break;
        }
    }

    public static void logDatabase(int level, String tag, String content){
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_DB) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_DB) {
                    Log.d(tag, content);
                }
                break;
        }
    }

    public static void logRadio(int level, String tag, String content){
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_RADIO) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_RADIO) {
                    Log.d(tag, content);
                }
                break;
        }
    }

    public static void logScenario(int level, String tag, String content){
        switch (level) {
            case LEVEL_INFO:
                Log.i(tag, content);
                break;
            case LEVEL_DEBUG:
                if (DEBUG && DEBUG_SCENARIO) {
                    Log.d(tag, content);
                }
                break;
            case LEVEL_ERROR:
                if (DEBUG && DEBUG_SCENARIO) {
                    Log.d(tag, content);
                }
                break;
        }
    }
}
