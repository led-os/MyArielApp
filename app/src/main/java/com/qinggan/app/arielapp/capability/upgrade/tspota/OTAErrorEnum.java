package com.qinggan.app.arielapp.capability.upgrade.tspota;

import android.text.TextUtils;

import com.qinggan.app.arielapp.R;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-10]
 * @see [相关类/方法]
 * @since [V1]
 */
public enum OTAErrorEnum {
    PRO_02("02", R.string.o02),
    PRO_021("021", R.string.o021),
    PRO_022("022", R.string.o022),
    PRO_023("023", R.string.o023),
    PRO_024("024", R.string.o024),
    PRO_025("025", R.string.o025),
    PRO_026("026", R.string.o026),
    PRO_05("05", R.string.o05),
    PRO_08("08", R.string.o08),
    PRO_11("11", R.string.o11),
    PRO_13("13", R.string.o13);
    String code;
    int resId;

    OTAErrorEnum(String code, int resId) {
        this.code = code;
        this.resId = resId;
    }

    public static OTAErrorEnum getOTAError(String code) {
        if (TextUtils.isEmpty(code)) return null;
        OTAErrorEnum[] errors = OTAErrorEnum.values();
        for (OTAErrorEnum error : errors) {
            if (code.equals(error.code)) {
                return error;
            }
        }
        return null;
    }
}
