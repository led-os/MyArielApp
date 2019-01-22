package com.qinggan.app.arielapp.phonestate;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.ReversingRadarActivity;
import com.qinggan.qinglink.api.Constant;
import com.qinggan.qinglink.api.md.CanBusListener;
import com.qinggan.qinglink.bean.RadarInfo;
import com.qinggan.qinglink.bean.VehicleInfo;

import java.util.ArrayList;

/**
 * Created by zhongquansun on 2018/11/22.
 */
public class PhoneStateManager {

    private static final String TAG = PhoneStateManager.class.getSimpleName();
    private static PhoneStateManager sPhoneStateManager;
    private PhoneState mPhoneState = PhoneState.OUT_CAR_MODE;
    private boolean mIsConnectedToCar = false;
    private ConnectionToCarStateChangeListener mConnectionToCarStateChangeListener;
    private ArrayList<PhoneStateChangeListener> mPhoneStateChangeListenerList = new ArrayList<PhoneStateChangeListener>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mConnectionChanged = new Runnable() {
        @Override
        public void run() {
            if(mConnectionToCarStateChangeListener != null){
                mConnectionToCarStateChangeListener.onConnectionChanged(mIsConnectedToCar);
            }
        }
    };

    public static PhoneStateManager getInstance(Context context){
        if(null == sPhoneStateManager){
            sPhoneStateManager = new PhoneStateManager(context);
        }
        return sPhoneStateManager;
    }

    private LocalBroadcastManager localBroadcastManager;

    private PhoneStateManager(Context context){
        ArielApplication.addCanBusListener(new CanBusListener(){

            @Override
            public void onVehicleInfoChanged(VehicleInfo vehicleInfo) {
                Log.d(TAG, "onVehicleInfoChanged");
                super.onVehicleInfoChanged(vehicleInfo);
                int status = vehicleInfo.getAccStatus();
                Log.d(TAG, "onVehicleInfoChanged = " + status);
                boolean accon = ((status == Constant.Can.AccStatus.ACC_ON) ||
                        (status == Constant.Can.AccStatus.ACC_ACC) ||
                        (status == Constant.Can.AccStatus.ACC_START));
                Log.d(TAG, "onVehicleInfoChanged accon= " + accon);
                setPhoneState(accon ? PhoneState.IN_CAR_MODE : PhoneState.OUT_CAR_MODE);
            }

            //倒车雷达回调
            @Override
            public void onRadarInfoChanged(RadarInfo radarInfo) {
                super.onRadarInfoChanged(radarInfo);
                Log.d("RRadar9999","ReversingRadarActivity");
                if(radarInfo.getAccStatus()==RadarInfo.ACC_ON && radarInfo.getGearStatus()==RadarInfo.GEAR_REVERSE){
                    Log.d("RRadar00000","ReversingRadarActivity");
                    Intent intent = new Intent(ArielApplication.getApp(), ReversingRadarActivity.class);
                    intent.putExtra("radarInfo",radarInfo);
                    ArielApplication.getApp().startActivity(intent);
                }else{
                    if(localBroadcastManager == null){
                        localBroadcastManager= LocalBroadcastManager.getInstance(ArielApplication.getApp());
                    }
                    Intent intent=new Intent("com.qinggan.app.arielapp.radar_close");
                    localBroadcastManager.sendBroadcast(intent);
                }
            }
        });


    }

    /**
     * 获取当前应用工作模式
     * @return
     */
    public PhoneState getPhoneState(){
        return mPhoneState;
    }

    public void addPhoneStateChangeListener(PhoneStateChangeListener listener){
        if(!mPhoneStateChangeListenerList.contains(listener)){
            mPhoneStateChangeListenerList.add(listener);
        }
    }

    public void removePhoneStateChangeListener(PhoneStateChangeListener listener){
        if(mPhoneStateChangeListenerList.contains(listener)){
            mPhoneStateChangeListenerList.remove(listener);
        }
    }

    /**
     * 在车离车模式切换时的回调
     */
    public interface PhoneStateChangeListener{
        void onPhoneStateChange(PhoneState phoneState);
    }

    /**
     * 设置在车离车模式，仅供测试使用
     * @param phoneState
     */
    public void setPhoneState(PhoneState phoneState){
        if(phoneState != mPhoneState){
            Log.d("VoiceFloatView","setPhoneStateForTest : " + phoneState);
            mPhoneState = phoneState;
            for(PhoneStateChangeListener listener : mPhoneStateChangeListenerList){
                listener.onPhoneStateChange(mPhoneState);
            }
        }
    }

    public void setConnectionToCarStateChangeListener(ConnectionToCarStateChangeListener listener){
        mConnectionToCarStateChangeListener = listener;
    }

    public interface ConnectionToCarStateChangeListener{
        void onConnectionChanged(boolean connected);
    }

    public boolean isConnectedToCar(){
        return mIsConnectedToCar;
    }

    public void setConnectedToCar(boolean isConnectedToCar){
        if(isConnectedToCar != mIsConnectedToCar){
            Log.d("VoiceFloatView","setConnectedToCar : " + isConnectedToCar);
            mIsConnectedToCar = isConnectedToCar;
            mHandler.post(mConnectionChanged);
        }
    }
}
