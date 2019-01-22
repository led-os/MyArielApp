package com.qinggan.app.arielapp.utils;

import android.content.Context;

import com.qinggan.app.arielapp.R;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;
import com.qinggan.qinglink.bean.UIControlElementItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WakeupControlMgr {
    private static WakeupControlMgr mInstance = null;

    //电台
    public final static String FM_NAME_SPACE = "fm_name_space";
    public static HashMap<String, String> fmType = new HashMap<>();
    public final static String FM_SCAN_FREQUENCY = "fm_scan_frequency";
    public final static String FM_NEXT_FREQUENCY = "fm_next_frequency";
    public final static String FM_LAST_FREQUENCY = "fm_last_frequency";
    public final static String FM_OPEN_MENU = "fm_open_menu";
    public final static String FM_OPEN_MENU1 = "fm_open_menu1";
    public final static String FM_OPEN_MENU2 = "fm_open_menu2";
    public final static String FM_CLOSE_MENU = "fm_close_menu";
    public final static String FM_CLOSE_MENU1 = "fm_close_menu1";
    public final static String FM_BACK_TO = "fm_back_to";
    public final static String FM_BACK_TO1 = "fm_back_to1";
    public final static String FM_LAST_PAGE = "fm_last_page";
    public final static String FM_NEXT_PAGE = "fm_next_page";
    //美食景点列表
    public final static String NAV_LIST_SPACE = "nav_list_space";
    public static HashMap<String, String> navListType = new HashMap<>();
    public final static String NAVLIST_LAST_PAGE = "navlist_last_page";
    public final static String NAVLIST_NEXT_PAGE = "navlist_next_page";
    public final static String NAVLIST_BACK_TO = "navlist_back_to";
    //美食详情
    public final static String SCENIC_NAME_SPACE = "scenic_name_space";
    public static HashMap<String, String> scenicType = new HashMap<>();
    public final static String SCENIC_START_NAV = "scenic_start_nav";
    public final static String SCENIC_START_FAV = "scenic_start_fav";
    public final static String SCENIC_BACK_TO = "scenic_back_to";
    //景点详情
    public final static String RESTAURANT_NAME_SPACE = "restaurant_name_space";
    public static HashMap<String, String> restaurantType = new HashMap<>();
    public final static String RESTAURANT_START_NAV = "restaurant_start_nav";
    public final static String RESTAURANT_START_FAV = "restaurant_start_fav";
    public final static String RESTAURANT_START_CALL = "restaurant_start_fav";
    public final static String RESTAURANT_BACK_TO = "restaurant_back_to";
    //新闻
    public final static String NEWS_NAME_SPACE = "news_name_space";
    public static HashMap<String, String> newsType = new HashMap<>();
    public final static String NEWS_NEXT_PROGRAM = "news_next_program";
    public final static String NEWS_LAST_PROGRAM = "news_last_program";
    public final static String NEWS_BACK_TO = "news_back_to";
    //驾驶模式主页
    public final static String INTELLIGENCE_NAME_SPACE = "interlligence_name_space";
    public static HashMap<String, String> interlligenceType = new HashMap<>();
    public final static String INTELLIGENCE_START_NAV = "interlligence_start_nav";
    public final static String INTELLIGENCE_START_MUSIC = "interlligence_start_music";
    public final static String INTELLIGENCE_START_FM = "interlligence_start_fm";
    public final static String INTELLIGENCE_START_CALL = "interlligence_start_call";
    public final static String INTELLIGENCE_START_CAR = "interlligence_start_car";

    private String mWakeupType = "";

    private HotwordManager mHotwordManager;
    ArrayList<UIControlElementItem> elementItems = new ArrayList<>();

    private WakeupControlListener mWakeupControlListener;

    public synchronized static WakeupControlMgr getInstance() {
        if (mInstance == null) {
            mInstance = new WakeupControlMgr();
        }

        return mInstance;
    }

    private void initWakeupMaps(Context context) {
        //FM
        fmType.put(FM_SCAN_FREQUENCY, context.getResources().getString(R.string.voice_radio_scan_frequency2));
        fmType.put(FM_NEXT_FREQUENCY, context.getResources().getString(R.string.voice_radio_next_frequency));
        fmType.put(FM_LAST_FREQUENCY, context.getResources().getString(R.string.voice_radio_pre_frequency));
        fmType.put(FM_OPEN_MENU, context.getResources().getString(R.string.voice_radio_open_menu));
        fmType.put(FM_OPEN_MENU1, context.getResources().getString(R.string.voice_radio_open_menu1));
        fmType.put(FM_OPEN_MENU2, context.getResources().getString(R.string.voice_radio_open_menu2));
        fmType.put(FM_CLOSE_MENU, context.getResources().getString(R.string.voice_radio_close_menu));
        fmType.put(FM_CLOSE_MENU1, context.getResources().getString(R.string.voice_radio_close_menu1));
        fmType.put(FM_BACK_TO, context.getResources().getString(R.string.voice_radio_back_to));
        fmType.put(FM_BACK_TO1, context.getResources().getString(R.string.voice_radio_back_to1));
        fmType.put(FM_LAST_PAGE, context.getResources().getString(R.string.last_page));
        fmType.put(FM_NEXT_PAGE, context.getResources().getString(R.string.next_page));
        //NAV LIST
        navListType.put(NAVLIST_LAST_PAGE, context.getResources().getString(R.string.last_page));
        navListType.put(NAVLIST_NEXT_PAGE, context.getResources().getString(R.string.next_page));
        navListType.put(NAVLIST_BACK_TO, context.getResources().getString(R.string.back_to));
        //SCENIC
        scenicType.put(SCENIC_START_NAV, context.getResources().getString(R.string.start_nav));
        scenicType.put(SCENIC_START_FAV, context.getResources().getString(R.string.start_fav));
        scenicType.put(SCENIC_BACK_TO, context.getResources().getString(R.string.back_to));
        //RESTAURANT
        restaurantType.put(RESTAURANT_START_NAV, context.getResources().getString(R.string.start_nav));
        restaurantType.put(RESTAURANT_START_FAV, context.getResources().getString(R.string.start_fav));
        restaurantType.put(RESTAURANT_START_CALL, context.getResources().getString(R.string.start_phone_call));
        restaurantType.put(RESTAURANT_BACK_TO, context.getResources().getString(R.string.back_to));
        //NEWS
        newsType.put(NEWS_NEXT_PROGRAM, context.getResources().getString(R.string.voice_news_next));
        newsType.put(NEWS_LAST_PROGRAM, context.getResources().getString(R.string.voice_news_pre));
        newsType.put(NEWS_BACK_TO, context.getResources().getString(R.string.back_to));
        //INTELLIGENCE MAIN
        interlligenceType.put(INTELLIGENCE_START_NAV, context.getResources().getString(R.string.interlligence_start_nav));
        interlligenceType.put(INTELLIGENCE_START_MUSIC, context.getResources().getString(R.string.interlligence_start_music));
        interlligenceType.put(INTELLIGENCE_START_FM, context.getResources().getString(R.string.interlligence_start_fm));
        interlligenceType.put(INTELLIGENCE_START_CALL, context.getResources().getString(R.string.interlligence_start_call));
        interlligenceType.put(INTELLIGENCE_START_CAR, context.getResources().getString(R.string.interlligence_start_car));
    }

    public void init(Context context) {
        initWakeupMaps(context);

        mHotwordManager = com.qinggan.qinglink.api.md.HotwordManager.getInstance(context, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                if (b) {

                } else {

                }
            }
        });
    }


    public void setElementUCWords(final String type, int first, int end, WakeupControlListener listener) {
        if (mHotwordManager == null) {
            return;
        }

        if (listener == null) {
            return;
        }

        if (!type.equals(mWakeupType)) {
            mHotwordManager.clearElementUCWords(mWakeupType);
            mWakeupType = type;
        }
        mHotwordManager.clearElementUCWords(type);
        elementItems.clear();

        Set<Map.Entry<String, String>> entrySet = null;
        switch (type) {
            case FM_NAME_SPACE:
                entrySet = fmType.entrySet();
                break;
            case NAV_LIST_SPACE:
                entrySet = navListType.entrySet();
                break;
            case SCENIC_NAME_SPACE:
                entrySet = scenicType.entrySet();
                break;
            case RESTAURANT_NAME_SPACE:
                entrySet = restaurantType.entrySet();
                break;
            case NEWS_NAME_SPACE:
                entrySet = newsType.entrySet();
                break;
            case INTELLIGENCE_NAME_SPACE:
                entrySet = interlligenceType.entrySet();
                break;
        }

        if (entrySet == null) {
            return;
        }

        mWakeupControlListener = listener;

        for (Map.Entry<String, String> entry : entrySet) {
            com.qinggan.qinglink.bean.UIControlElementItem elementItem = new com.qinggan.qinglink.bean.UIControlElementItem();
            elementItem.setWord(entry.getValue());
            elementItem.setIdentify(entry.getKey());
            elementItems.add(elementItem);
        }

        if (first >= 0 && end > 0 && end > first) {
            for (int i = first; i < end + 1; i++) {
                com.qinggan.qinglink.bean.UIControlElementItem elementItem = new com.qinggan.qinglink.bean.UIControlElementItem();
                int index = i + 1;
                elementItem.setWord("第" + index + "个");
                elementItem.setIdentify(index + "");
                elementItems.add(elementItem);
            }
        }

        mHotwordManager.setElementUCWords(type, elementItems);
        mHotwordManager.registerListener(type, new HotwordListener() {
            @Override
            public void onItemSelected(String s) {
                mWakeupControlListener.onItemSelected(type, s);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onSwitchPage(int i) {

            }
        });

    }

    public void clearElementUCWords(String type) {
        mWakeupType = "";
        mHotwordManager.clearElementUCWords(type);
        mWakeupControlListener = null;
    }

    public interface WakeupControlListener {
        void onItemSelected(String type, String key);
    }

}
