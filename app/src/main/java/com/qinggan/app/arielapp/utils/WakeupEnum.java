package com.qinggan.app.arielapp.utils;

import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.qinglink.bean.UIControlElementItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * <免唤醒词>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-14]
 * @see [相关类/方法]
 * @since [V1]
 */
public enum WakeupEnum {
    WAKE_UP0(R.string.wakeup9, "0", AllWakeupEvent.VOLUME_ADD),
    WAKE_UP1(R.string.wakeup11, "1", AllWakeupEvent.VOLUME_SUB),
    WAKE_UP2(R.string.wakeup45, "2", AllWakeupEvent.STOP_NAV),
    WAKE_UP3(R.string.nav_exit, "3", AllWakeupEvent.STOP_NAV),
    WAKE_UP4(R.string.wakeup46, "4", AllWakeupEvent.BACK_MAIN),
    WAKE_UP5(R.string.wakeup1, "5", AllWakeupEvent.LAST_SONG),
    WAKE_UP6(R.string.wakeup2, "6", AllWakeupEvent.NEXT_SONG),
    WAKE_UP7(R.string.wakeup4, "7", AllWakeupEvent.PAUSE_MUSIC),
    WAKE_UP8(R.string.wakeup47, "8", AllWakeupEvent.PLAY_MUSIC),
    WAKE_UP9(R.string.voice_radio_change_frequency, "9", AllWakeupEvent.NEXT_FM);
//    WAKE_UP9(R.string.wakeup5, "9", AllWakeupEvent.LAST_FM);
//    WAKE_UP8(R.string.wakeup48, "8", AllWakeupEvent.STOP_NAV),
//    WAKE_UP9(R.string.wakeup49, "9", AllWakeupEvent.STOP_NAV),
//    WAKE_UP10(R.string.wakeup50, "10", AllWakeupEvent.STOP_NAV);

//    WAKE_UP0(R.string.wakeup1, "0", AllWakeupEvent.LAST_SONG),
//    WAKE_UP1(R.string.wakeup2, "1", AllWakeupEvent.NEXT_SONG),
//    WAKE_UP2(R.string.wakeup3, "2", AllWakeupEvent.PLAY_MUSIC),
//    WAKE_UP3(R.string.wakeup4, "3", AllWakeupEvent.PAUSE_MUSIC),
//    WAKE_UP4(R.string.wakeup5, "4", AllWakeupEvent.LAST_FM),
//    WAKE_UP5(R.string.wakeup6, "5", AllWakeupEvent.NEXT_FM),
//    WAKE_UP6(R.string.wakeup7, "6", AllWakeupEvent.PLAY_FM),
//    //    WAKE_UP7(R.string.w, "7"),
//    WAKE_UP8(R.string.wakeup9, "8", AllWakeupEvent.VOLUME_ADD),
//    WAKE_UP9(R.string.wakeup10, "9", AllWakeupEvent.VOLUME_ADD),
//    WAKE_UP10(R.string.wakeup11, "10", AllWakeupEvent.VOLUME_SUB),
//    WAKE_UP11(R.string.wakeup12, "11", AllWakeupEvent.VOLUME_SUB),
//    WAKE_UP12(R.string.wakeup13, "12", AllWakeupEvent.CALL_ACCEPT),
//    WAKE_UP13(R.string.wakeup14, "13", AllWakeupEvent.CALL_REFUSE),
//
//    WAKE_UP14(R.string.wakeup24, "14", AllWakeupEvent.WECHAT_REPLY),
//    WAKE_UP15(R.string.wakeup25, "15", AllWakeupEvent.WECHAT_CANCEL),
//    WAKE_UP16(R.string.wakeup26, "16", AllWakeupEvent.WECHAT_NEXT_MSG),
//    WAKE_UP17(R.string.wakeup27, "17", AllWakeupEvent.WECHAT_OPEN_READ_MSG),
//    WAKE_UP18(R.string.wakeup28, "18", AllWakeupEvent.WECHAT_CLOSE_READ_MSG),
//    WAKE_UP19(R.string.wakeup35, "19", AllWakeupEvent.VEHICLECONTROL_FIND_CAR),
//    WAKE_UP20(R.string.wakeup36, "20", AllWakeupEvent.VEHICLECONTROL_WORM_CAR),
//    WAKE_UP21(R.string.wakeup37, "21", AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_WORM),
//    WAKE_UP22(R.string.wakeup38, "22", AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_WORM_ALL_CAR),
//    WAKE_UP23(R.string.wakeup39, "23", AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_CLOD),
//    WAKE_UP24(R.string.wakeup40, "24", AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_CLOD_ALL_CAR),
//    WAKE_UP25(R.string.wakeup41, "25", AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_RAIN),
//    WAKE_UP26(R.string.wakeup42, "26", AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_RAIN_ALL_CAR),
//    WAKE_UP27(R.string.wakeup43, "27", AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_SMOK),
//    WAKE_UP28(R.string.wakeup44, "28", AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_SMOK_ALL_CAR);

    private int resId;
    private String mIdentify;
    private AllWakeupEvent wakeupEvent;

    WakeupEnum(int resId, String mIdentify, AllWakeupEvent wakeupEvent) {
        this.resId = resId;
        this.mIdentify = mIdentify;
        this.wakeupEvent = wakeupEvent;
    }

    /**
     * 生成唤醒词
     *
     * @return
     */
    public static ArrayList<UIControlElementItem> getWakeupElememts() {
        ArrayList<UIControlElementItem> elementItems = new ArrayList<>();
        WakeupEnum[] wakeupEnums = WakeupEnum.values();
        for (WakeupEnum wakeupEnum : wakeupEnums) {
            UIControlElementItem item = new UIControlElementItem();
            item.setWord(ArielApplication.getApp().getString(wakeupEnum.resId));
            item.setIdentify(wakeupEnum.mIdentify);
            elementItems.add(item);
        }
        return elementItems;
    }


    /**
     * 解析免唤醒词语意
     */
    public static void analyseWakeUpWord(String identify) {
        Log.d("WakeupEnum", "analyseWakeUpWord identify:" + identify);
        if (TextUtils.isEmpty(identify)) {
            return;
        }
        WakeupEnum[] list = WakeupEnum.values();
        for (WakeupEnum wakeupEnum : list) {
            if (identify.equals(wakeupEnum.mIdentify)) {
                Log.d("WakeupEnum", "find identify  wakeupenum");
                EventBus.getDefault().post(wakeupEnum.wakeupEvent);
                return;
            }
        }
    }
}
