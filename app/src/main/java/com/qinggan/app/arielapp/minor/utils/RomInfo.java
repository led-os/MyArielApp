package com.qinggan.app.arielapp.minor.utils;

import com.qinggan.app.arielapp.utils.RomUtils;

public class RomInfo {
    private static final String TAG = "RomInfo";

    public static final int ROM_MIUI = 1;
    public static final int ROM_EMUI = 2;
    public static final int ROM_FLYME = 3;
    public static final int ROM_OPPO = 4;
    public static final int ROM_VIVO = 5;
    public static final int ROM_QIKU = 6;

    private int mRom = 0;
    private static RomInfo mRomInfo;

    private static final Object mLock = new Object();

    public static RomInfo getmRomInfo() {
        if (mRomInfo == null) {
            synchronized (mLock) {
                if (mRomInfo == null) {
                    mRomInfo = new RomInfo();
                }
            }
        }
        return mRomInfo;
    }

    private RomInfo() {
        if (RomUtils.checkIsHuaweiRom()) {
            mRom = ROM_EMUI;
        } else if (RomUtils.checkIsMiuiRom()) {
            mRom = ROM_MIUI;
        } else if (RomUtils.checkIsMeizuRom()) {
            mRom = ROM_FLYME;
        } else if (RomUtils.checkIs360Rom()) {
            mRom = ROM_QIKU;
        } else if (RomUtils.checkIsOPPORom()) {
            mRom = ROM_OPPO;
        } else if (RomUtils.checkIsVIVORom()) {
            mRom = ROM_VIVO;
        }
        ArielLog.logCore(ArielLog.LEVEL_DEBUG, TAG, "RomInfo:" + mRom);
    }

    public int getRomName() {
        ArielLog.logCore(ArielLog.LEVEL_DEBUG, TAG, "getRomName:" + mRom);
        return mRom;
    }
}
