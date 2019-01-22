package com.qinggan.app.arielapp.phonestate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;


import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.ReversingRadarActivity;
import com.qinggan.qinglink.bean.RadarInfo;

public class ReversingRadarReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            if(intent.getAction().equals("com.qinggan.app.arielapp.radar_open")){
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessageDelayed(msg,200);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    try{
                        RadarInfo mRadarInfo = ArielApplication.getCanBusManager().getRadarInfo();
                        if(mRadarInfo!=null){
                            if(mRadarInfo.getAccStatus() == RadarInfo.ACC_ON && mRadarInfo.getGearStatus() == RadarInfo.GEAR_REVERSE){
                                Intent intentRever = new Intent(ArielApplication.getApp(), ReversingRadarActivity.class);
                                intentRever.putExtra("radarInfo",mRadarInfo);
                                ArielApplication.getApp().startActivity(intentRever);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
}
