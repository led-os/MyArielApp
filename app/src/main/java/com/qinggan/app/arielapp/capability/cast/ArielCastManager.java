package com.qinggan.app.arielapp.capability.cast;

import android.util.Log;
import android.view.Surface;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.cast.PresentationManager;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.CastListener;
import com.qinggan.qinglink.api.md.CastManager;
import com.qinggan.qinglink.api.md.VideoManager;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-19]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ArielCastManager {

    private static final String TAG = ArielCastManager.class.getSimpleName();
    private static volatile ArielCastManager instance;

    public static ArielCastManager getInstance() {
        if (null == instance) {
            synchronized (ArielCastManager.class) {
                if (null == instance)
                    instance = new ArielCastManager();
            }
        }
        return instance;
    }

    private boolean isCastConnect;
    private CastManager mQingCast;
    private VideoManager videoManager;
    private ArielCastManager() {
        mQingCast = CastManager.getInstance(ArielApplication.getApp(), new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {
                Log.d(TAG, "mQingCast onConnectStatusChange, " + b);
            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                Log.d(TAG, "mQingCast onConnect, " + b);
                isCastConnect = b;
                if (b) {
                    Log.d(TAG, "mQingCast onConnect, connect init");
                    mQingCast.init("192.168.49.1");
                    mQingCast.castMain();
                } else {
                    Log.d(TAG, "mQingCast onConnect, not connect");
                }
            }
        });
        mQingCast.registerListener(new CastListener() {
            @Override
            public void onCastInit(Surface surface) {
                Log.d(TAG, "mQingCast CastListener onCastInit");
                PresentationManager.getInstance().onCreate(ArielApplication.getApp(), surface, null);
            }

            @Override
            public void onCastMainPause() {

            }

            @Override
            public void onCastMainStop() {

            }
        });

         videoManager = VideoManager.getInstance(ArielApplication.getApp(), new OnInitListener() {
             @Override
             public void onConnectStatusChange(boolean b) {
                 Log.d(TAG, "videoManager onConnectStatusChange, " + b);
             }
         }, new OnConnectListener() {
             @Override
             public void onConnect(boolean b) {
                 Log.d(TAG, "videoManager onConnect, " + b);
             }
         });
    }

    public void stopCast() {
        Log.d(TAG, "stopCast,connect:" + isCastConnect + ",mQingCast:" + mQingCast);
        if (null != mQingCast) {
            mQingCast.stopCastMain();
            mQingCast.stopCastAlternate();
        }
    }
}
