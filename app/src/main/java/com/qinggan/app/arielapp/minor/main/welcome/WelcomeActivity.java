package com.qinggan.app.arielapp.minor.main.welcome;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.UIControlBaseActivity;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.main.navigation.NavShowPresetDestActivity;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.utils.WLog;
import com.qinggan.app.arielapp.vehiclecontrol.SeatControlManager;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.qinglink.enumeration.VehicleState;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pateo on 18-12-24.
 */

public class WelcomeActivity  extends UIControlBaseActivity {

    private String TAG = WelcomeActivity.class.getSimpleName();

    private static final int MSG_TTS_WELCOME_SPEAK = 0;
    private static final int MSG_START_PRESET_NAVI = 1;

    private static final int TTS_WELCOME_SPEAK_DELAY = 0 * 1000;
    private static final int START_PRESET_NAVI_DELAY = 10 * 1000;

    private TextView arielWelcomePrompt;
    private ImageView animationIv;

    private boolean isOnResume = false;

    @Override
    protected void initView() {
        arielWelcomePrompt = (TextView) findViewById(R.id.ariel_welcome_prompt);
        animationIv = (ImageView) findViewById(R.id.animation_iv);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.welcome;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TTS_WELCOME_SPEAK), TTS_WELCOME_SPEAK_DELAY);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_START_PRESET_NAVI), START_PRESET_NAVI_DELAY);
        //调用座椅记忆恢复
        SeatControlManager.getInstance(WelcomeActivity.this).transferSeatMemorySetting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnResume = true;

        String owner = "";
        if (ArielApplication.getmUserInfo() != null) {

            WLog.d(TAG, "MobilePhone:" + ArielApplication.getmUserInfo().getMobilePhone());
            if (ArielApplication.getmUserInfo().getMobilePhone() != null) {
                String mobilePhone = ArielApplication.getmUserInfo().getMobilePhone();
                if (mobilePhone != null && mobilePhone.length() > 4) {
                    owner = mobilePhone.substring(mobilePhone.length() - 4);
                } else {
                    owner = mobilePhone;
                }
            }

        } else {
            WLog.d(TAG, "UserInfo is null");
        }
        arielWelcomePrompt.setText(getString(R.string.welcome_prompt_str, owner));

    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnResume = false;
    }

    /*@Override
    public void onBackPressed() {

    }*/

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TTS_WELCOME_SPEAK:
                    WLog.d(TAG,"MSG_TTS_WELCOME_SPEAK");
                    VoicePolicyManage.getInstance().speak(getString(R.string.ariel_welcome_prompt));
                    animationIv.setImageResource(R.drawable.welcome_animlist);
                    AnimationDrawable animationDrawable = (AnimationDrawable) animationIv.getDrawable();
                    animationDrawable.start();
                    break;
                case MSG_START_PRESET_NAVI:
                    //TODO
                    WLog.d(TAG,"MSG_START_PRESET_NAVI");

                    List<NaviInfo> routeList= MapUtils.queryAllPresetNaviInfo(WelcomeActivity.this);
                    WLog.d(TAG, "queryAllPresetNaviInfo routeList : " + routeList);
                    if (routeList != null && routeList.size() > 0) {
                        Log.d(TAG, "queryAllPresetNaviInfo routeList size: " + routeList.size() );
                        if(isOnResume){
                            Intent startPreset = new Intent(WelcomeActivity.this, NavShowPresetDestActivity.class);
                            startActivity(startPreset);
                        }
                    }

                    WelcomeActivity.this.finish();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
