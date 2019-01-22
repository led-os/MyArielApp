package com.qinggan.app.arielapp.minor.main.mui.huitest;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.push.bean.PushMessageBodyBean;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.mobile.tsp.mqtt.PushMessage;
import com.qinggan.mobile.tsp.mqtt.QGMqttManagerImpl;
import com.qinggan.mobile.tsp.mqtt.QGMqttService;

public class TestHuiActivity extends BaseActivity {
    FragmentManager mFragmentManager;
    LinearLayout main_content_view;

    @Override
    protected void initView() {
        initMainViewItems();
    }

    private void initMainViewItems() {
        mFragmentManager = getSupportFragmentManager();
        main_content_view = (LinearLayout) findViewById(R.id.main_content_view);
        bindMqttService();
    }

    @Override
    protected void initData() {
        AbstractBaseFragment fragment = new TestHuiMainFragment();
        mFragmentManager.beginTransaction().replace(R.id.main_content_view, fragment, "main").addToBackStack(null).commitAllowingStateLoss();
    }

    private void bindMqttService() {
        Intent intent = new Intent();
        intent.setClass(TestHuiActivity.this, QGMqttService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            QGMqttService.Binder binder = (QGMqttService.Binder) service;
            QGMqttManagerImpl mQGMqttManagerImpl = binder.getQGMqttManagerImpl();
            Log.i(TAG, "onServiceConnected IMEI = " + getImei());
            mQGMqttManagerImpl.updateTopic(getImei());
            mQGMqttManagerImpl.setCallback(new QGMqttManagerImpl.Callback() {
                @Override
                public void messageArrived(com.qinggan.mobile.tsp.mqtt.PushMessage msg) {
                    Log.d("QGMqttService", "onServiceConnected xx: " + msg);
                    showPushMessageFragment(msg.toString());
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void showPushMessageFragment(String msg) {
        String[] msgArray = msg.split("---");
        Log.i(TAG, "msg = " + msgArray[1]);

        PushMessage pushMessage = new Gson().fromJson(msgArray[1], PushMessage.class);
        PushMessageBodyBean pushMessageBodyBean = new PushMessageBodyBean();
        pushMessageBodyBean.setMsgTitle(pushMessage.getMessage().getMsgTitle());
        pushMessageBodyBean.setPushBody(pushMessage.getMessage().getPushBody());
        LocalFragmentManager.getInstance().showSubFragment(getSupportFragmentManager(), LocalFragmentManager.FragType.PUSHMESSAGE, R.id.main_content_view, pushMessageBodyBean);
    }

    public String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        @SuppressLint("MissingPermission")
        String imei = telephonyManager.getDeviceId();

        if (imei == null) {
            return "";
        }

        return imei;
    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_test_navi;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
