package com.qinggan.app.arielapp.minor.wechat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.qinggan.app.arielapp.minor.phone.utils.ChineseToPY;
import com.qinggan.app.arielapp.minor.wechat.MessageEvent;
import com.qinggan.app.arielapp.minor.wechat.WeChatSendMsgActivity;
import com.qinggan.app.arielapp.minor.wechat.WechatIntegrationManager;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatCallback;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatManager;
import com.qinggan.qinglink.api.md.HotwordManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by pateo on 19-1-15.
 */

public class WechatCallbackImpl implements WechatCallback {

    private static final String TAG = WechatCallbackImpl.class.getSimpleName();
    private String mWechatName;
    private String mWechatMsg;
    private Context mContext;
    private HotwordManager mHotwordManager;
    private WindowManager mWindowManager;
    private WechatIntegrationManager mWechatIntegrationManager;
    private View mView;
    private WindowManager.LayoutParams mWheelViewParams;

    public WechatCallbackImpl(Context context, WechatIntegrationManager wechatIntegrationManager,
                              HotwordManager hotwordManager, WindowManager windowManager,
                              View view, WindowManager.LayoutParams wheelViewParams) {
        mContext = context;
        mHotwordManager = hotwordManager;
        mWechatIntegrationManager = wechatIntegrationManager;
        mWheelViewParams = wheelViewParams;
        mWindowManager = windowManager;
        mView = view;
    }

    @Override
    public void onPrepareSendWechat(String name) {
        if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
            return;
        }

        String pinyinName = ChineseToPY.getAllPinYinFirstLetter(name);
        WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_SEND_CONTENT);
        mWechatName = pinyinName;
        Log.d(TAG, "send wechat get name : " + mWechatName);

        Intent mIntent = new Intent(mContext, WeChatSendMsgActivity.class);
        mIntent.putExtra("username", mWechatName);
        mContext.startActivity(mIntent);
    }

    @Override
    public void onWechatPersonSelect(String selectedId) {
        Log.d(TAG, "send wechat select id : " + selectedId);
//            NluResultManager.getInstance().setWechatDomain(WECHAT_END);
//            //VoicePolicyManage.getInstance().record(true);
//            handleVirtual(ActionCode.WECHAT_SELECT_INPUT_SEND, selectedId, mWechatMsg);
    }

    @Override
    public void onWechatSendMessage(String message) {
        mWechatMsg = message;
        Log.d(TAG, "send wechat get msg : " + mWechatMsg);
        WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_SEND);
        MessageEvent event = new MessageEvent();
        event.setStep(2);
        event.setMsg(mWechatMsg);
        event.setName(mWechatName);
        EventBus.getDefault().post(event);
    }

    @Override
    public void onSendWechat(String type) {
        Log.d(TAG, "send wechat send type ： " + type);

        if (WechatManager.WECHAT_SEND_START.equals(type)) {
            WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
            MessageEvent event = new MessageEvent();
            event.setStep(3);
            event.setName(mWechatName);
            EventBus.getDefault().post(event);
            mWechatIntegrationManager.addWakeupElementsforWeChat();
            mWindowManager.addView(mView, mWheelViewParams);
        } else if (WechatManager.WECHAT_SEND_CANCEL.equals(type)) {
            WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
            MessageEvent event = new MessageEvent();
            event.setStep(5);
            event.setName(mWechatName);
            EventBus.getDefault().post(event);
            mWindowManager.removeViewImmediate(mView);
            if (null != mHotwordManager) {
                mHotwordManager.clearElementUCWords(WechatIntegrationManager.VOICE_SEND_WECHAT);
            }
        } else if (WechatManager.WECHAT_SEND_AGAIN.equals(type)) {
            WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_SEND_CONTENT);
            MessageEvent event = new MessageEvent();
            event.setStep(6);
            event.setName(mWechatName);
            EventBus.getDefault().post(event);
        }
    }

    public String getmWechatName() {
        return mWechatName;
    }

    public void setmWechatName(String mWechatName) {
        this.mWechatName = mWechatName;
    }

    public String getmWechatMsg() {
        return mWechatMsg;
    }

    public void setmWechatMsg(String mWechatMsg) {
        this.mWechatMsg = mWechatMsg;
    }
}
