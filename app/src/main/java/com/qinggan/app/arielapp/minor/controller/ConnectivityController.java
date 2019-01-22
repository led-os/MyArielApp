package com.qinggan.app.arielapp.minor.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;

import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.utils.ArielLog;
import com.qinggan.app.arielapp.minor.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 18-10-30.
 * 网络状态判定以及检测类
 */

public class ConnectivityController {
    private static final String TAG = ConnectivityController.class.getSimpleName();
    private static final int SYNC_NAVI_INFO = 1;
    private static final int SYNC_NAVI_INFO_DELAY_TIME = 10 * 1000;

    private static ConnectivityController mConController;
    private static final Object mLock = new Object();

    private Context mContext;

    private List<NetworkChangeCallback> mCallbackList = new ArrayList<>();

    private NetChangeReceiver mNetChangeReceiver;
    private boolean mHasRegisterReceiver = false;
    private int mNetType = NetUtils.NETWORK_NONE;

    private int preNetType = NetUtils.NETWORK_NONE;

    private ConnectivityManager mConManager;

    public static ConnectivityController getConController(Context context) {
        synchronized (mLock) {
            if (mConController == null) {
                mConController = new ConnectivityController(context);
            }
        }
        return mConController;
    }

    public interface NetworkChangeCallback {
        /**
         * 回调的时候请使用其他线程实现
         * @param netType　变更的网络类型
         */
        void networkChange(int netType);
    }

    /**
     * 注册网络变化广播
     */
    public void registerNetChangeReceiver(){
        if (mHasRegisterReceiver) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, TAG, "Have registered net change receiver, do nothing.");
            return;
        }

        if (mNetChangeReceiver == null) {
            mNetChangeReceiver = new NetChangeReceiver();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mNetChangeReceiver, intentFilter);
        mHasRegisterReceiver = true;
    }

    /**
     * 注销网络变化广播
     */
    public void unregisterNetChangeReceiver(){
        if (!mHasRegisterReceiver) {
            ArielLog.logController(ArielLog.LEVEL_DEBUG, TAG, "Haven't registered net change receiver, do nothing.");
            return;
        }

        if (mNetChangeReceiver == null) {
            return;
        }

        mContext.unregisterReceiver(mNetChangeReceiver);
    }

    private ConnectivityController(Context context) {
        mContext = context;
        mConManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        preNetType = mNetType = NetUtils.getNetWorkState(mConManager);
    }

    /**
     * 添加网络变化回调到列表里
     * @param callback　回调函数
     */
    public synchronized void addNetChangeCallback(NetworkChangeCallback callback){
        mCallbackList.add(callback);
    }

    /**
     * 分发网络变化信号给其他需要监听网络状态的模块
     */
    private void dispatchNetChange(){
        for (NetworkChangeCallback callback:mCallbackList) {
            callback.networkChange(mNetType);
        }
    }

    private class NetChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mNetType = NetUtils.getNetWorkState(mConManager);
                dispatchNetChange();

                if (preNetType == NetUtils.NETWORK_NONE && mNetType != NetUtils.NETWORK_NONE) {
                    if (mHandler.hasMessages(SYNC_NAVI_INFO)) {
                        mHandler.removeMessages(SYNC_NAVI_INFO);
                    }
                    //延迟等待网络稳定
                    mHandler.sendMessageDelayed(Message.obtain(mHandler, SYNC_NAVI_INFO), SYNC_NAVI_INFO_DELAY_TIME);
                }
                preNetType = mNetType;
            }
        }
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case SYNC_NAVI_INFO:
                    ArielLog.logController(ArielLog.LEVEL_DEBUG, TAG, "syncNaviInfo");
                    //网络从无到有，同步NaviInfo
                    MapUtils.syncNaviInfo(mContext);
                    break;

                default:
                    return false;
            }
            return false;
        }
    });

}
