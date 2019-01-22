package com.qinggan.app.cast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.cast.receiver.model.ArielCastNet;
import com.qinggan.app.cast.receiver.model.ArielCastPower;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.BATTERY_SERVICE;

/**
 * <投屏电源电量及信号强度>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-14]
 * @see [相关类/方法]
 * @since [V1]
 */
public class CastPhoneStateManager {
    private static final String TAG = CastPhoneStateManager.class.getSimpleName();
    private static volatile CastPhoneStateManager instance;

    public static CastPhoneStateManager getInstance() {
        if (null == instance) {
            synchronized (CastPhoneStateManager.class) {
                if (null == instance) {
                    instance = new CastPhoneStateManager();
                }
            }
        }
        return instance;
    }

    private boolean isRegist = false;

    private TelephonyManager mTelephonyManager;
    private PhoneStatListener mListener;
    private int mGsmSignalStrength;
    private NetWorkBroadCastReciver mNetWorkBroadCastReciver;
    private ArielCastNet arielCastNet = new ArielCastNet();


    private PowerBroadCastReceiver powerBroadCastReceiver;
    private int powerPercent;

    public void init() {
        Log.d(TAG, "--init--");
        //----信号强度----
        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) ArielApplication.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        //开始监听
        mListener = new PhoneStatListener();
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        /**由于信号值变化不大时，监听反应不灵敏，所以通过广播的方式同时监听wifi和信号改变更灵敏*/
        mNetWorkBroadCastReciver = new NetWorkBroadCastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        ArielApplication.getApp().registerReceiver(mNetWorkBroadCastReciver, intentFilter);

        //----电源电量----
        powerBroadCastReceiver = new PowerBroadCastReceiver();
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(Intent.ACTION_BATTERY_CHANGED);
        ArielApplication.getApp().registerReceiver(powerBroadCastReceiver, filter2);

        BatteryManager manager = (BatteryManager) ArielApplication.getApp().getSystemService(BATTERY_SERVICE);
        powerPercent = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        EventBus.getDefault().post(new ArielCastPower(powerPercent));
        isRegist = true;
    }

    public void unRegist() {
        Log.d(TAG, "--unRegist--isRegist:" + isRegist);
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
        if (isRegist) {
            ArielApplication.getApp().unregisterReceiver(mNetWorkBroadCastReciver);
            ArielApplication.getApp().unregisterReceiver(powerBroadCastReceiver);
        }
        isRegist = false;
    }


    private class PhoneStatListener extends PhoneStateListener { //获取信号强度

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //获取网络信号强度
            //获取0-4的5种信号级别，越大信号越好,但是api23开始才能用
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mGsmSignalStrength = signalStrength.getLevel();
                Log.d(TAG, "onSignalStrengthsChanged >=23 getLevel:" + mGsmSignalStrength);
            } else {
                int level = signalStrength.getGsmSignalStrength();
                Log.d(TAG, "onSignalStrengthsChanged level:" + level);
                mGsmSignalStrength = level / 6;
            }
            //网络信号改变时，获取网络信息
            getNetWorkInfo();
        }
    }

    class NetWorkBroadCastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getNetWorkInfo();
        }
    }

    /**
     * 获取网络的信息
     */
    private void getNetWorkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ArielApplication.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI: //wifi
                    WifiManager manager = (WifiManager) ArielApplication.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo connectionInfo = manager.getConnectionInfo();
                    int rssi = connectionInfo.getRssi();
                    int level = WifiManager.calculateSignalLevel(rssi, 5);
                    Log.d(TAG, "getNetWorkInfo TYPE_WIFI,level:" + level);
                    arielCastNet.setType(ConnectivityManager.TYPE_WIFI);
                    arielCastNet.setValue(level);
                    EventBus.getDefault().post(arielCastNet);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    Log.d(TAG, "getNetWorkInfo TYPE_MOBILE,level:" + mGsmSignalStrength);
                    arielCastNet.setType(ConnectivityManager.TYPE_MOBILE);
                    arielCastNet.setValue(mGsmSignalStrength);
                    EventBus.getDefault().post(arielCastNet);
                    break;
            }
        } else {
            arielCastNet.setType(-1);
            arielCastNet.setValue(0);
            EventBus.getDefault().post(arielCastNet);
        }
    }


    private class PowerBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);    ///电池剩余电量
            int scale = intent.getIntExtra("scale", 0);  ///获取电池满电量数值
            Log.d(TAG, "PowerBroadCastReceiver level:" + level + ",scale:" + scale);
            if (scale == 0) return;
            int currentPercent = level * 100 / scale;
            if (powerPercent == currentPercent) {
                Log.d(TAG, "PowerBroadCastReceiver powerPercent == currentPercent return");
                return;
            }
            EventBus.getDefault().post(new ArielCastPower(powerPercent));
        }
    }

    public ArielCastNet getArielCastNet() {
        return arielCastNet;
    }

    public int getPowerPercent() {
        return powerPercent;
    }

}
