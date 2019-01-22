package com.qinggan.app.arielapp.minor.integration;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.tencent.qqmusic.third.api.contract.CommonCmd;
import com.tencent.qqmusic.third.api.contract.IQQMusicApi;

import static com.qinggan.app.arielapp.minor.integration.MusicContacts.EMPTYSTRINGVALUE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.NOMALERRORCODE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.NOMALSUCCESSCODE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.OPPO;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.PARAMSCALLBACKURL;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.PAUSECMD;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.PLAYCMD;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.PLAYPAUSED;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.PLAYSTARTED;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.QQINTENT;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.QQPACKAGE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.RESUMECMD;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.RETURNCODEPARAMS;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.TYPEUNKNOW;

public class MusicDamoService extends Service {
    public IQQMusicApi mQQMusicAPI;
    public String type,key ;
    boolean flag4OV ,threadFlag= false;
    boolean flagResume = false;
    AudioManager mAudioManager;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mQQMusicAPI = IQQMusicApi.Stub.asInterface(iBinder);
            if(mQQMusicAPI != null){
                IntegrationCore.getIntergrationCore(getApplicationContext()).
                        mMusicCMD.setMusicConn(true);
                if(type == null) type = TYPEUNKNOW;
                switch(type){
                    case PLAYCMD:
                        typeMain();
                        break;
                    default:
                        if(!OPPO.equals(getVendor())){
                            typeMain();
                        }
                        break;
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mQQMusicAPI = null;
            IntegrationCore.getIntergrationCore(getApplicationContext()).
                    mMusicCMD.setMusicConn(false);
            flag4OV = false;
            stopSelf();
        }
    };

    public void bindQQMusicService(){
        Intent intent = new Intent(QQINTENT);
        intent.setPackage(QQPACKAGE);
        getApplicationContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        //flag4OV = true;
    }

    @Override
    public void onCreate() {
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if(OPPO.equals(getVendor()) || "vivo".equals(getVendor())){
            new Thread(new OVThread()).start();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle mBundle = intent.getExtras();
        String tempType;
        tempType = (mBundle != null)?mBundle.getString("type"):null;
        if(tempType != null && !tempType.equals(EMPTYSTRINGVALUE)){
            flagResume = (tempType.equals("search"))?true:false;
        }
        bindQQMusicService();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        flag4OV = false;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void typeMain(){
        Bundle ret = null;
        try {
            ret = mQQMusicAPI.execute(PLAYCMD, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int code = ret.getInt(RETURNCODEPARAMS);
        while(!flag4OV){
            try {
                ret = mQQMusicAPI.execute(PLAYCMD, null);
                code = ret.getInt(RETURNCODEPARAMS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(code == NOMALSUCCESSCODE && mQQMusicAPI != null){
                flag4OV = true;
            }
        }
        if(code == NOMALSUCCESSCODE){
            if(!OPPO.equals(getVendor())) IntegrationCore.getIntergrationCore(getApplicationContext()).
                    mMusicCMD.setPre(true);
            IntegrationCore.getIntergrationCore(ArielApplication.getApp().getApplicationContext()).
                    getSystemCMD().backToHomeActivity();
            IntegrationCore.getIntergrationCore(ArielApplication.getApp()).
                    mMusicCMD.checkAudioPolicy();
            stopSelf();
        }
    }

    public String getVendor(){
        String[] vendorInfo = Build.FINGERPRINT.split("/");
        if(vendorInfo != null){
            return vendorInfo[0];
        }
        return null;
    }
    private class OVThread implements Runnable{
        @Override
        public void run() {
            Bundle ret = null;
            int code = NOMALERRORCODE;
            while(!threadFlag){
                if(mQQMusicAPI != null) {
                    IntegrationCore.getIntergrationCore(getApplicationContext()).
                            mMusicCMD.setMusicConn(true);
                    if (mQQMusicAPI != null) {
                        try {
                            ret = mQQMusicAPI.execute(PLAYCMD, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ret != null) {
                            code = ret.getInt(RETURNCODEPARAMS);
                        } else {
                            code = NOMALERRORCODE;
                        }
                    } else {
                        continue;
                    }
                    if(code == PLAYPAUSED){
                        CommonCmd.openQQMusic(getApplicationContext(), PARAMSCALLBACKURL);
                    }
                    if (code == NOMALSUCCESSCODE && mQQMusicAPI != null/* && mAudioManager.isMusicActive()*/) {
                        IntegrationCore.getIntergrationCore(getApplicationContext()).
                                mMusicCMD.setPre(true);
                        threadFlag = true;
                        Thread.currentThread().interrupt();
                    }
                }else{
                    bindQQMusicService();
                    continue;
                }
            }
            if(threadFlag){
                IntegrationCore.getIntergrationCore(ArielApplication.getApp().getApplicationContext()).
                        getSystemCMD().backToHomeActivity();
            }
        }
    }
}
