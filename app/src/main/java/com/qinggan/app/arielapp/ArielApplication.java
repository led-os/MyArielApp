package com.qinggan.app.arielapp;

import android.app.ActivityManager;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.qinggan.app.arielapp.WheelControl.WheelControl;
import com.qinggan.app.arielapp.capability.upgrade.ota.OTAManager;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.capability.volume.ArielVolumeManager;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.ui.bluekey.AppBlueKeyListener;
import com.qinggan.app.arielapp.ui.bluekey.BindCarSuccessActivity;
import com.qinggan.app.arielapp.user.Bean.BindInfo;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.app.arielapp.user.activity.LoginActivity;
import com.qinggan.app.arielapp.utils.MyCrashHandler;
import com.qinggan.app.arielapp.utils.WakeupControlMgr;
import com.qinggan.app.arielapp.utils.WakeupEnum;
import com.qinggan.app.arielapp.vehiclecontrol.SeatControlManager;
import com.qinggan.app.arielapp.voiceview.VoiceFloatViewService;
import com.qinggan.app.cast.receiver.CastPhoneStateManager;
import com.qinggan.app.duerstt.utils.LogUtil;
import com.qinggan.app.voiceapi.tts.TtsHelper;
import com.qinggan.bluekey.encrypt.KeyStoreHelper;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.service.ServiceBlueKeyListener;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.CanBusListener;
import com.qinggan.qinglink.api.md.CanBusManager;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;
import com.qinggan.qinglink.bean.AirCondition;
import com.qinggan.qinglink.bean.RadarInfo;
import com.qinggan.qinglink.bean.UIControlElementItem;
import com.qinggan.qinglink.bean.VehicleInfo;
import com.recovery.callback.RecoveryCallback;
import com.recovery.core.Recovery;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import static com.qinggan.app.arielapp.minor.utils.Constants.USERINFO;


/*******************************************************************************
 *
 * @author : Pateo harrishuang@pateo.com.cn
 *
 * Copyright (c) 2017-2020 Pateo. All Rights Reserved.
 *
 * Copying of this document or code and giving it to others and the
 * use or communication of the contents thereof, are forbidden without
 * expressed authority. Offenders are liable to the payment of damages.
 * All rights reserved in the event of the grant of a invention patent or the
 * registration of a utility model, design or code.
 *
 * Issued by Pateo.
 * Date: 2018-03-12
 *******************************************************************************/

public class ArielApplication extends Application {
    public static final String TAG = ArielApplication.class.getSimpleName();

    static ArielApplication app;
    private static CanBusManager sCanBusManager;
    public static AppBlueKeyListener mAppBlueKeyListener;
    private static UserInfo mUserInfo;
    private static ArrayList<CanBusListener> mCanBusListenerList = new ArrayList<CanBusListener>();
    private Handler mHandler = new Handler();
    private ActivityLifecycleListener mActivityLifecycleListener;

    private ViewPager drViewPager;
    private RecyclerView drRecyclerview;
    private Context AppContext;
    private ProgressBar downProgressBar;
    private static SharedPreferences preferences;
    private static HotwordManager mHotwordManager;
    //    private static boolean isHotManagerInitSuccess;
    private static WheelControl wheelControl;
    private static boolean isRfcomConnect;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        // 安装tinker
        Beta.installTinker();
    }

    private void sendBlueConnToRfComm(BluetoothDevice device) {
        String strName = device.getName();
        Log.e(TAG, "sendBlueConnToRfComm() 经典蓝牙名:" + strName);
        if (strName.contains("BLEV")) {
            return;
        }
        Log.e(TAG, "sendBlueConnToRfComm() getAddress:" + device.getAddress());
        Intent bIntent = new Intent("com.pateo.bluekey.rfcomm");
        Bundle bundle = new Bundle();
        bundle.putString("name", device.getName());
        bundle.putString("macAdress", device.getAddress());
        bIntent.putExtras(bundle);
        getApplicationContext().sendBroadcast(bIntent);
    }


    BroadcastReceiver hfpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED);
                Log.e(TAG, "ACTION_CONNECTION_STATE_CHANGED device:" + device + ",state=" + state);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    sendBlueConnToRfComm(device);
                }

            }
        }
    };


    public void checkBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);

            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.d(TAG, "BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                Log.d(TAG, "devices:" + devices.size());

                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    Log.d(TAG, "checkBluetooth() connected:" + device.getName());
                    if (isConnected) {
                        int connectState = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
                        Log.d(TAG, "checkBluetooth() connectState:" + connectState);
                        if (connectState == BluetoothProfile.STATE_CONNECTED) {
                            sendBlueConnToRfComm(device);
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startListenBlueConnection() {
       /* IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//指明一个与远程设备建立的低级别（ACL）连接。
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//指明一个来自于远程设备的低级别（ACL）连接的断开
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);//指明一个为远程设备提出的低级别（ACL）的断
        registerReceiver(BluetoothReciever, filter); // 不要忘了之后解除绑定*/

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(hfpReceiver, filter);


    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
        String strCurrentProcess = getProcessName(getApplicationContext());
        Log.e(TAG, "onCreate() strCurrentProcess=" + strCurrentProcess);
        if (strCurrentProcess.equals(getPackageName())) {
            Log.e(TAG, "in main process");
            app = this;
            //KeyStoreHelper.getInstance(getApplicationContext()).initKeyStore();
            Recovery.getInstance()
                    .debug(true)
                    .recoverInBackground(false)
                    .recoverStack(true)
                    .mainPage(MainActivity.class)
                    .recoverEnabled(BuildConfig.LOG_DEBUG)//上线时关闭debug
                    .callback(new MyCrashCallback())
                    .silent(!BuildConfig.LOG_DEBUG, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                    .skip(MainActivity.class)
                    .init(this);
            MyCrashHandler.register();
            mActivityLifecycleListener = ActivityLifecycleListener.getInstance();
            registerActivityLifecycleCallbacks(mActivityLifecycleListener);
            InitializeService.start(this);
            sCanBusManager = CanBusManager.getInstance(this, mOnInitListener, onConnectListener);

            IntegrationCore integrationCore = IntegrationCore.getIntergrationCore(this);
            integrationCore.initDbCarInfo();
            preferences = this.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
            wheelControl = new WheelControl(this);
            initHotwordManager();
            ArielVolumeManager.getInstance();
            startListenBlueConnection();
            //OTA
            OTAManager.getInstance();
            WakeupControlMgr.getInstance().init(this);

            CastPhoneStateManager.getInstance().init();
        } else if (strCurrentProcess.contains(":ble")) {
            Log.e(TAG, "in remote process");
            ServiceBlueKeyListener mServiceBlueKeyListener = new ServiceBlueKeyListener(this.getApplicationContext());
            BleKeyManager.getInstance(this.getApplicationContext()).init(mServiceBlueKeyListener,false);
        }

    }

    public void setVoiceFloatViewService(VoiceFloatViewService voiceFloatViewService) {
        mActivityLifecycleListener.setVoiceFloatViewService(voiceFloatViewService);
    }

    public static ArielApplication getApp() {
        return app;
    }


    /**
     * 获取当前进程名称
     *
     * @param context
     * @return
     */
    private String getProcessName(Context context) {
        int pid = android.os.Process.myPid();

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : am
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }

        return "";
    }

    @Override
    public void onTerminate() {
        TtsHelper.getInstance().release();
        mHotwordManager.clearElementUCWords("common");
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(mActivityLifecycleListener);
        if (wheelControl != null) {
            wheelControl.destroyResource();
        }
        wheelControl = null;
    }

    private OnInitListener mOnInitListener = new OnInitListener() {
        @Override
        public void onConnectStatusChange(boolean b) {
            if (b) {
                Log.d(TAG, "can bus service connected.");
                if (null != sCanBusManager) {
                    sCanBusManager.registerListener(mCanBusListener);
                }
            } else {
                Log.e(TAG, "can bus service disconnected.");
                sCanBusManager = CanBusManager.getInstance(getApplicationContext(), mOnInitListener, onConnectListener);
                return;
            }

        }
    };

    /**
     * rfcom连接状态listener
     */
    private OnConnectListener onConnectListener = new OnConnectListener() {
        @Override
        public void onConnect(boolean isConnect) {
            //rfcom连接上后,更新座椅记忆账户
            Log.e(TAG, "rfcom onConnect===" + isConnect);
            isRfcomConnect=isConnect;
            if (isConnect) {
                if (null != mUserInfo && !TextUtils.isEmpty(TspManager.getmPdsn())) {
                    try {
                        SeatControlManager.getInstance(getApp()).updateSeatMemorySetting();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };


    private CanBusListener mCanBusListener = new CanBusListener() {
        @Override
        public void onAirConditionChanged(final AirCondition airConditionData) {
            super.onAirConditionChanged(airConditionData);
            Log.e(TAG, "onAirConditionChanged");
            if (null != airConditionData)
                Log.e(TAG, "onAirConditionChanged==" + airConditionData.toString());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (CanBusListener l : mCanBusListenerList) {
                        l.onAirConditionChanged(airConditionData);
                    }
                }
            });
        }

        @Override
        public void onVehicleInfoChanged(final VehicleInfo vehicleInfo) {
            super.onVehicleInfoChanged(vehicleInfo);
            Log.e(TAG, "onVehicleInfoChanged");
            if (null != vehicleInfo) {
                Log.e(TAG, "onVehicleInfoChanged==" + vehicleInfo.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (CanBusListener l : mCanBusListenerList) {
                            l.onVehicleInfoChanged(vehicleInfo);
                        }
                    }
                });
                if (vehicleInfo.getAccStatus() == 3) {
                    Log.e(TAG, "onVehicleInfoChanged AccStatus==3");
                    //EventBus.getDefault().post(BindCarSuccessActivity.RESTAT);
                }
            }
        }

        @Override
        public void onRadarInfoChanged(final RadarInfo radarInfo) {
            super.onRadarInfoChanged(radarInfo);
            Log.e(TAG, "onRadarInfoChanged");
            if (null != radarInfo)
                Log.e(TAG, "onRadarInfoChanged==" + radarInfo.toString());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (CanBusListener l : mCanBusListenerList) {
                        l.onRadarInfoChanged(radarInfo);
                    }
                }
            });
        }


    };

    public static CanBusManager getCanBusManager() {
        return sCanBusManager;
    }

    public static void addCanBusListener(CanBusListener l) {
        if (null != l) {
            mCanBusListenerList.add(l);
        }
    }

    public static void removeCanBusListener(CanBusListener l) {
        if (null != l && mCanBusListenerList.contains(l)) {
            mCanBusListenerList.remove(l);
        }
    }

    public static UserInfo getmUserInfo() {
        return mUserInfo;
    }

    public static UserInfo getmUserInfoWithLogin() {
        if (null == mUserInfo) {
            ToastUtil.show(getApp().getResources().getString(R.string.goto_login_first), getApp());
            LoginActivity.startAction(getApp());
            return null;
        }
        return mUserInfo;
    }

    public static void setmUserInfo(UserInfo mUserInfo) {
        ArielApplication.mUserInfo = mUserInfo;
        SharedPreferences.Editor editor = preferences.edit();
        if (mUserInfo != null) {
            editor.putString(USERINFO, new Gson().toJson(mUserInfo));
            String tpdSn = mUserInfo.getTpdsn();
            if (!TextUtils.isEmpty(tpdSn)) {
                TspManager.getInstance(getApp()).setmPdsn(tpdSn);
            } else {
                BindInfo bindInfo = mUserInfo.getBindInfo();
                if (bindInfo != null) {
                    String pdsn = bindInfo.tpdsn;
                    if (!TextUtils.isEmpty(pdsn)) {
                        TspManager.getInstance(getApp()).setmPdsn(pdsn);
                    }
                }
            }
            if (!TextUtils.isEmpty(TspManager.getmPdsn())) {
                SeatControlManager.getInstance(getApp()).getAndAddSeatMemorySetting();
            }
            //云端同步地址信息
            MapUtils.syncNaviInfo(getApp());
        } else {
            editor.putString(USERINFO, "");
            //退出登录,清空车辆pdsn及sim相关数据
            BindVehicleInfo.clear();
//            TspManager.getInstance(getApp()).setmPdsn("");
            //清除本地地址信息
            MapUtils.clearNaviInfo(getApp());
            MapUtils.clearNaviSearchHistory(getApp());
            //清除本地座椅记忆
            SeatControlManager.getInstance(getApp()).clearLocaSeatMemorySetting();
        }
        editor.commit();

    }

    public Context getContext() {
        return this.AppContext;
    }

    public ViewPager getDrViewPager() {
        return drViewPager;
    }

    public void setDrViewPager(ViewPager drViewPager) {
        this.drViewPager = drViewPager;
    }

    public RecyclerView getDrRecyclerview() {
        return drRecyclerview;
    }

    public void setDrRecyclerview(RecyclerView drRecyclerview) {
        this.drRecyclerview = drRecyclerview;
    }

    public ProgressBar getDownProgressBar() {
        return downProgressBar;
    }

    public void setDownProgressBar(ProgressBar downProgressBar) {
        this.downProgressBar = downProgressBar;
    }

    private void initHotwordManager() {
        mHotwordManager = HotwordManager.getInstance(this, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {
                //isHotManagerInitSuccess = b;
            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
//                isHotManagerInitSuccess = b;
                Log.i(TAG, "HotwordManager onConnect:" + b);
                if (b && ArielApplication.getmUserInfo() != null && !TextUtils.isEmpty(ArielApplication.getmUserInfo().getHotWord())) {
                    mHotwordManager.setCustomWakeupWord(ArielApplication.getmUserInfo().getHotWord());
                }
//                if (b) {
//                    //设置免唤醒词
//                    Log.i(TAG, "HotwordManager add common wakeup");
//                    mHotwordManager.setElementUCWords("common", WakeupEnum.getWakeupElememts());
//                }
            }
        });

        mHotwordManager.registerListener("common", new HotwordListener() {
            @Override
            public void onItemSelected(String s) {
                Log.d(TAG, "mHotwordManager HotwordListener onItemSelected:" + s);
                WakeupEnum.analyseWakeUpWord(s);
            }

            @Override
            public void onPageSelected(int i) {
                Log.d(TAG, "mHotwordManager HotwordListener onPageSelected");
            }

            @Override
            public void onSwitchPage(int i) {
                Log.d(TAG, "mHotwordManager HotwordListener onSwitchPage");
            }
        });
    }

    public static HotwordManager getmHotwordManager() {
        return /*isHotManagerInitSuccess ? */mHotwordManager/* : null*/;
    }

    /**
     * 添加免唤醒热词
     *
     * @param topic
     * @param hotwordListener
     * @return
     */
    public static boolean registHotwordElements(String topic, ArrayList<UIControlElementItem> elements, HotwordListener hotwordListener) {
        if (null != mHotwordManager /*&& isHotManagerInitSuccess*/) {
            mHotwordManager.setElementUCWords(topic, elements);
            return mHotwordManager.registerListener(topic, hotwordListener);
        }
        return false;
    }

    /**
     * 清除热词
     *
     * @param topic
     */
    public static void clearHotwordTopic(String topic) {
        if (null != mHotwordManager /*&& isHotManagerInitSuccess*/) {
            mHotwordManager.clearElementUCWords(topic);
        }
    }


    public static WheelControl getWheelControlManager() {
        return wheelControl;
    }

    public static final class MyCrashCallback implements RecoveryCallback {
        @Override
        public void stackTrace(String exceptionMessage) {
            LogUtil.e("exceptionMessage : ", exceptionMessage);
        }

        @Override
        public void cause(String cause) {
            LogUtil.e("cause : ", cause);
        }

        @Override
        public void exception(String exceptionType, String throwClassName, String throwMethodName, int throwLineNumber) {
            LogUtil.e("exceptionClassName : ", exceptionType);
            LogUtil.e("throwClassName : ", throwClassName);
            LogUtil.e("throwMethodName : ", throwMethodName);
            LogUtil.e("throwLineNumber : ", throwLineNumber + "");

        }

        @Override
        public void throwable(Throwable throwable) {

        }
    }

    public static boolean isRfcomConnect() {
        return isRfcomConnect;
    }

}
