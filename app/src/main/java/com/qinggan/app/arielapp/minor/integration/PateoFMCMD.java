package com.qinggan.app.arielapp.minor.integration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.core.FMStatusListener;
import com.qinggan.qinglink.api.Constant;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.RadioListener;
import com.qinggan.qinglink.api.md.RadioManager;

import java.util.ArrayList;
import java.util.List;

import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.utils.ArielLog;

public class PateoFMCMD {

    private static final String TAG = PateoFMCMD.class.getSimpleName();
//    private static volatile PateoFMCMD instance = null;

    private RadioManager mRadioManager;
    private Context mContext;
    private List<FMStatusListener> fmListeners = new ArrayList<>();

    private boolean isRadioConnected;
    private int radioStatus = -1;
    private float currentFrequency = -1;
    private int radioType = 1;
    private String testMacId;
    private ArrayList<String> allFrequencyList = new ArrayList<>();
    private ArrayList<String> fmFrequencyList = new ArrayList<>();
    private ArrayList<String> amFrequencyList = new ArrayList<>();

//    public static PateoFMCMD getInstance() {
//        if (instance == null) {
//            synchronized (PateoFMCMD.class) {
//                if (instance == null) {
//                    instance = new PateoFMCMD();
//                }
//            }
//        }
//        return instance;
//    }

    public static final int STATUS_ON = 1;
    public static final int STATUS_SCANNING = 2;
    public static final int STATUS_OFF = 3;

    public PateoFMCMD(Context context) {
        mContext = context;
        init();
//        connectToDevice();
    }

    private AudioPolicyManager mAudioPolicyManager;

    private AudioPolicyManager.OnAudioPolicyListener mAudioPolicyListener =
            new AudioPolicyManager.OnAudioPolicyListener() {
                private int status;
                @Override
                public boolean onPause() {
                    status = getRadioStatus();
                    doRadioOff();
                    return false;
                }

                @Override
                public boolean onResume() {
                    if(status == STATUS_ON) {
                        doRadioOn();
                    }
                    return false;
                }

                @Override
                public boolean onStop() {
                    doRadioOff();
                    return false;
                }
            };

    private void init() {
        Log.d(TAG,"init");
        mRadioManager = RadioManager.getInstance(mContext,
                mOnInitListener,
                mOnConnectListener);

        mAudioPolicyManager = AudioPolicyManager.getInstance();
    }

    private OnConnectListener mOnConnectListener = new OnConnectListener() {
        @Override
        public void onConnect(boolean isConnected) {
            Log.d(TAG,"bt is connected : "+isConnected);
            isRadioConnected = isConnected;
            if (isConnected) {
                doGetStatus();
                doGetCurrentFrequency();
                doGetFrequencyList();
            }
        }
    };

    private  OnInitListener mOnInitListener = new OnInitListener() {
        @Override
        public void onConnectStatusChange(boolean isConnected) {
            Log.d(TAG,"onConnectStatusChange isConnected : "+isConnected);
            if (isConnected){
                if (mRadioManager != null){
                    Log.d(TAG,"register radio listener");
                   mRadioManager.registerListener(mRadioListener);
                }
            }else {
                Log.e(TAG, "radio service disconnected.");
                mRadioManager = RadioManager.getInstance(ArielApplication.getApp(), mOnInitListener);
            }
        }

    };

    public void addFMListener(FMStatusListener listener){
        if (listener != null) {
            fmListeners.add(listener);
        }
    }

    public void removeListener(FMStatusListener listener) {
        fmListeners.remove(listener);
    }

    /**public void initTestDatas(){
        isRadioConnected = true;
        mRadioListener.onCurrentBandResponse(1);
        mRadioListener.onCurrentFrequencyResponse((float)137.9);
        fmFrequencyList.add("137.9");
        mRadioListener.onRadioStatusResponse(STATUS_ON);
    }*/

    private  RadioListener mRadioListener = new RadioListener() {

        @Override
        public void onFrequencyListResponse(String s) {
            Log.e(TAG, "onFrequencyListResponse s : " + s);
            for (FMStatusListener listener : fmListeners) {
                listener.onFrequencyListResponse(s);
            }
        }

        @Override
        public void onCurrentBandResponse(int type) {
            Log.e(TAG, "onCurrentBandResponse type : " + type);
            radioType = type;
            for (FMStatusListener listener : fmListeners) {
                listener.onCurrentBandResponse(type);
            }
        }

        @Override
        public void onCurrentFrequencyResponse(float frequency) {
            Log.e(TAG, "onCurrentFrequencyResponse frequency : " + frequency);
            if (frequency != 0 && frequency != 1) {
                currentFrequency = frequency;
            }
            for (FMStatusListener listener : fmListeners) {
                listener.onCurrentFrequencyResponse(frequency);
            }
        }

        @Override
        public void onRadioStatusResponse(int status) {
            Log.e(TAG, "onRadioStatusResponse state : " + status);

            if (status == Constant.Radio.Status.ON) {
                AudioPolicyManager.getInstance().requestAudioPolicy(mAudioPolicyListener, AudioPolicyManager.AudioType.FM);
            }

            radioStatus = status;
            /**
            if (radioStatus == STATUS_ON) {
                StageController.getStageController().setBGStream(StageController.BG_STREAM_RADIO);
            } else {
                ArielLog.logController(ArielLog.LEVEL_DEBUG, "Brian", "Set bg stream none. " +
                        "when onRadioStatusResponse");
                StageController.getStageController().setBGStream(StageController.BG_STREAM_NONE);
            }*/
            if (status == STATUS_OFF) {
                IntegrationCore.getIntergrationCore(mContext).
                        getCardController().radioSimpleCardTimeout();
            }

            IntegrationCore.getIntergrationCore(mContext).updateCard(CardController.CARD_TYPE_RADIO);
            for (FMStatusListener listener : fmListeners) {
                listener.onRadioStatusResponse(status);
            }
        }

        @Override
        public void onFmFrequencyListResponse(ArrayList<String> fmList) {
            Log.e(TAG, "onFmFrequencyListResponse fmList : " + fmList);
            fmFrequencyList.clear();
            fmFrequencyList.addAll(fmList);
            for (FMStatusListener listener : fmListeners) {
                listener.onFmFrequencyListResponse(fmList);
            }
        }

        @Override
        public void onAmFrequencyListResponse(ArrayList<String> amList) {
            Log.e(TAG, "onAmFrequencyListResponse amList : " + amList);
            amFrequencyList.clear();
            amFrequencyList.addAll(amList);
            for (FMStatusListener listener : fmListeners) {
                listener.onAmFrequencyListResponse(amList);
            }
        }
    };

    public int getRadioType() {
        return radioType;
    }

    public float getCurrentFrequency() {
        return currentFrequency;
    }

    public boolean isRadioConnected() {
        return isRadioConnected;
    }

    public String getTestMac() {
        return testMacId;
    }

    public int getRadioStatus() {
        return radioStatus;
    }

    public ArrayList<String> getFrequencyList() {
        allFrequencyList.clear();
        allFrequencyList.addAll(fmFrequencyList);
        allFrequencyList.addAll(amFrequencyList);

        Log.e(TAG, "getFrequencyList fmFrequencyList : " + fmFrequencyList);
        Log.e(TAG, "getFrequencyList amFrequencyList : " + amFrequencyList);
        Log.e(TAG, "getFrequencyList allFrequencyList : " + allFrequencyList);

        return allFrequencyList;
    }

    public void playCurrent() {
        Log.e(TAG, "playCurrent");
        if (currentFrequency >= 87.5 && currentFrequency <= 108) {
            doSetFrequency(currentFrequency);
        } else {
            if (allFrequencyList != null && allFrequencyList.size() > 0) {
                String frequency = allFrequencyList.get(0);
                doSetFrequency(Float.valueOf(frequency) / 1000);
            } else {
                doAutoScan();
            }

            Log.e(TAG, "currentFrequency not correct");
        }
    }

    //切换上一台下一台
    public void seekToPlay(boolean isNext) {
        Log.e(TAG, "seekToPlay isNext ： " + isNext);
        allFrequencyList.clear();
        allFrequencyList.addAll(fmFrequencyList);
        allFrequencyList.addAll(amFrequencyList);

        int index = 0;
        String sFrequency = String.valueOf((int)(currentFrequency * 1000));
        if (allFrequencyList == null || allFrequencyList.size() < 2) {
            return;
        }

        for (int i = 0; i < allFrequencyList.size(); i++) {
            if (sFrequency.equals(allFrequencyList.get(i))) {
                index = i;
                break;
            }
        }

        if (isNext) {
            if (index == (allFrequencyList.size() - 1)) {
                sFrequency = allFrequencyList.get(0);
            } else {
                sFrequency = allFrequencyList.get(index + 1);
            }
        } else {
            if (index == 0) {
                sFrequency = allFrequencyList.get(allFrequencyList.size() - 1);
            } else {
                sFrequency = allFrequencyList.get(index - 1);
            }
        }

        currentFrequency = Float.valueOf(sFrequency) / 1000;

        doSetFrequency(currentFrequency);
    }

    public  boolean doRadioOn(){

        Log.e("AudioPolicyManager","--doRadioOn ");
        boolean canPlay = mAudioPolicyManager.
                requestAudioPolicy(mAudioPolicyListener, AudioPolicyManager.AudioType.FM);

        if (!canPlay) {
            return false;
        }

        if (mRadioManager == null){
            Log.e(TAG, "doRadioOn mRadioManager is null");
            return false;
        }
        Log.e(TAG, "doRadioOn ");
        return mRadioManager.sendRadioOn();
    }

    public boolean doRadioOff(){

        if (mRadioManager == null){
            Log.e(TAG, "doRadioOff mRadioManager is null");
            return false;
        }
        radioStatus = STATUS_OFF;
        Log.e(TAG, "doRadioOff ");
        return mRadioManager.sendRadioOff();
    }

    public  boolean doAutoScan(){
        if (mRadioManager == null){
            Log.e(TAG, "doAutoScan mRadioManager is null");
            return false;
        }
        Log.e(TAG, "doAutoScan ");
        return mRadioManager.sendAutoScan();
    }

    public boolean isValidFrequency(float frequency) {
        String stringFre = String.valueOf((int)(frequency * 1000));
        if (allFrequencyList != null && allFrequencyList.contains(stringFre)) {
            return true;
        }
        return false;
    }

    public  boolean doSetFrequency(float frequency){
        boolean canPlay = mAudioPolicyManager.
                requestAudioPolicy(mAudioPolicyListener, AudioPolicyManager.AudioType.FM);

        if (!canPlay) {
            return false;
        }

        if (mRadioManager == null){
            Log.e(TAG, "doSetFrequency mRadioManager is null");
            return false;
        }
        Log.e(TAG, "doSetFrequency frequency " + frequency);
        return mRadioManager.sendSetFrequency(frequency);
    }

    public  boolean doGetFrequencyList(){
        if (mRadioManager == null){
            Log.e(TAG, "doSetFrequency mRadioManager is null");
            return false;
        }
        Log.e(TAG, "doGetFrequencyList ");
        return mRadioManager.sendGetFrequencyList();
    }

    public  boolean doGetCurrentFrequency(){
        if (mRadioManager == null){
            Log.e(TAG, "doGetCurrentFrequency mRadioManager is null");
            return false;
        }
        Log.e(TAG, "doGetCurrentFrequency ");
        return mRadioManager.sendGetCurrentFrequency();
    }

    public  boolean doGetStatus(){
        if (mRadioManager == null){
            Log.e(TAG, "doGetStatus mRadioManager is null");
            return false;
        }
        Log.e(TAG, "doGetStatus ");
        return mRadioManager.sendGetRadioStatus();
    }

    public  boolean doGetBand(){
        if (mRadioManager == null){
            Log.e(TAG, "doGetBand mRadioManager is null");
            return false;
        }
        Log.e(TAG, "doGetBand ");
        return mRadioManager.sendGetCurrentBand();
    }

}
