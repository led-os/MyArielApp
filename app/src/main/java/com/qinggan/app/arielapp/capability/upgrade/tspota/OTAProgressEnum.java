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
public enum OTAProgressEnum {
    PRO_00("01", R.string.o01),
    PRO_03("03",  R.string.o03),
    PRO_04("04", R.string.o04),
    PRO_06("06",  R.string.o06),
    PRO_07("07",  R.string.o07),
    PRO_09("09",  R.string.o09),
    PRO_10("10",  R.string.o10),
    PRO_12("12",  R.string.o12),
    PRO_14("14",  R.string.o14);
    String code;
    int resId;

    OTAProgressEnum(String code, int resId) {
        this.code = code;
        this.resId = resId;
    }

    public static OTAProgressEnum getOTAProgress(String code) {
        if (TextUtils.isEmpty(code)) return null;
        OTAProgressEnum[] progresses = OTAProgressEnum.values();
        for (OTAProgressEnum progress : progresses) {
            if (code.equals(progress.code)) {
                return progress;
            }
        }
        return null;
    }
}
