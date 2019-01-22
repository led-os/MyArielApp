package com.qinggan.app.arielapp.minor.wechat.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.wechat.MessageEvent;
import com.qinggan.app.arielapp.minor.wechat.ReplyStatus;
import com.qinggan.app.arielapp.minor.wechat.inter.WechatSendUICallback;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatManager;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;
import com.qinggan.qinglink.bean.UIControlElementItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pateo on 19-1-16.
 */

public class WechatSendHelper {

    private static final String TAG = WechatSendHelper.class.getSimpleName();
    private final String SEND_WECHAT_MSG_UI_CONTROL = "send_msg_wechat_ui_control";
    private final String CANCEL_WECHAT_MSG_UI_CONTROL = "cancel_reply_wechat_ui_control";
    private final String REINPUT_WECHAT_MSG_UI_CONTROL = "reinput_reply_wechat_ui_control";

    private final static String VOICE_SEND_WECHAT = "voice_send_wechat";

    private static WechatSendHelper instance;
    private static Context mContext;

    public String mWechatMsg;
    public String mWechatName;
    public ReplyStatus sendBtnStatus;
    public int wechatStep;
    public boolean isOnpause;

    private List<WechatSendUICallback> mUICallbackList = new ArrayList<>();
    private HotwordManager mHotwordManager;

    private WechatSendHelper() {

    }

    public static WechatSendHelper getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            synchronized (WechatSendHelper.class) {
                if (instance == null) {
                    instance = new WechatSendHelper();
                }
            }
        }
        return instance;
    }

    public void handleMessageEvent(MessageEvent event) {
        Log.d(TAG, "event : " + event.toString());
        switch (event.getStep()) {
            case 1:
                break;
            case 2:
                //MSG
                mWechatName = event.getName();
                mWechatMsg = event.getMsg();
                sendBtnStatus = ReplyStatus.SEND;
                wechatStep = WechatConstants.WECHAT_SEND_MSG_STEP;
                updateUI(WechatConstants.WECHAT_UI_SEND_STEP2);
                VoicePolicyManage.getInstance().speak(mContext.getString(R.string.wechat_send_confirm_remind_str));
                break;
            case 3:
                //SEND
                mWechatName = event.getName();
                EventBus.getDefault().post(WechatConstants.WECHAT_SEND_MSG_ORDER);
                handleVirtual(ActionCode.WECHAT_SEARCH_PERSON, event.getName(), null);
                finishUI();
                break;
            case 4:
                break;
            case 5:
                //cancel
                mHandler.sendEmptyMessage(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
                break;
            case 6:
                //reinput
                mHandler.sendEmptyMessage(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
                break;
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg.what : " + msg.what);
            switch (msg.what) {
                case WechatConstants.SEND_WECHAT_MSG_FLAG:
                    Log.d(TAG, "SendBtnStatus : " + sendBtnStatus);
                    if (sendBtnStatus == ReplyStatus.SEND) {
                        EventBus.getDefault().post(WechatConstants.WECHAT_SEND_MSG_ORDER);
                        WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                        handleVirtual(ActionCode.WECHAT_SEARCH_PERSON, mWechatName, null);
                        finishUI();
                    }
                    break;
                case WechatConstants.CANCEL_WECHAT_MSG_FLAG:
                    VoicePolicyManage.getInstance().speakStop();
                    EventBus.getDefault().post(WechatConstants.WECHAT_CANCEL_SEND_ORDER);
                    intoMainAct();
                    break;
                case WechatConstants.REINPUT_WECHAT_MSG_FLAG:
                    Log.d(TAG, "SendBtnStatus : " + sendBtnStatus);
                    if (sendBtnStatus == ReplyStatus.SEND) {
                        WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_SEND_CONTENT);
                        updateUI(WechatConstants.WECHAT_UI_REINPUT);
                        sendBtnStatus = ReplyStatus.NOT_MSG;
                        VoicePolicyManage.getInstance().record(true);
                    }
                    break;
            }
        }
    };

    public void handleVirtual(int actionCode, String name, String action) {
        ActionBean actionBean = new ActionBean();
        switch (actionCode) {
            case ActionCode.WECHAT_SEARCH_PERSON:
                actionBean.setAddressee(name);
                break;
        }

        actionBean.setActionCode(actionCode);
        Log.d(TAG, "actionBean : " + actionBean);
        AccessibilityUtil.initAccessibility(mContext);
        PateoVirtualSDK.doAction(mContext, actionBean, IntegrationCore.getIntergrationCore(mContext));
    }


    private void intoMainAct() {
        WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
        finishUI();
        Intent mIntent = new Intent(mContext, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        UMAnalyse.stopTime(UMDurationEvent.WECHAT);
        mContext.startActivity(mIntent);
    }

    private void updateUI(int uiStatus) {
        if (mUICallbackList.size() > 0) {
            for (WechatSendUICallback uiCallback : mUICallbackList) {
                uiCallback.updateSendUI(uiStatus);
            }
        }
    }

    private void finishUI() {
        if (mUICallbackList.size() > 0) {
            for (WechatSendUICallback uiCallback : mUICallbackList) {
                uiCallback.finishUI();
            }
        }
    }

    public void reinputMsg(){
        mHandler.sendEmptyMessage(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
    }

    public void registUICallback(WechatSendUICallback uiCallback) {
        if (!mUICallbackList.contains(uiCallback)) {
            mUICallbackList.add(uiCallback);
        }
    }

    public void removeUICallback(WechatSendUICallback uiCallback) {
        if(mUICallbackList.contains(uiCallback)){
            mUICallbackList.remove(uiCallback);
        }
    }

    public void initHotwordManager(){
        mHotwordManager = HotwordManager.getInstance(mContext, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                if (b && !isOnpause) {
                    Log.d(TAG,"HotwordManager onConnect");
                    addWakeupElements();
                } else {
                    if (null != mHotwordManager) {
                        Log.d(TAG,"HotwordManager onConnect clear");
                        mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
                    }
                }
            }
        });
    }

    private void addWakeupElements() {
        if (mHotwordManager == null) {
            return;
        }

        Log.d(TAG, " addWakeupElements ");

        ArrayList<UIControlElementItem> elementItems =
                new ArrayList<>();


        com.qinggan.qinglink.bean.UIControlElementItem sendWechatItem =
                new com.qinggan.qinglink.bean.UIControlElementItem();
        sendWechatItem.setWord(mContext.getString(R.string.reply_wechat_send_ui_control));
        sendWechatItem.setIdentify(SEND_WECHAT_MSG_UI_CONTROL);
        elementItems.add(sendWechatItem);


        int[] cancel_words = {R.string.reply_wechat_exit_ui_control,
                R.string.reply_wechat_close_ui_control2};
        for (int i = 0; i < cancel_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem cancelWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            cancelWechatItem.setWord(mContext.getString(cancel_words[i]));
            cancelWechatItem.setIdentify(CANCEL_WECHAT_MSG_UI_CONTROL);
            elementItems.add(cancelWechatItem);
        }


        int[] reinput_words = {R.string.reinputs_wechat_cancel_ui_control,
                R.string.reinput_wechat_cancel_ui_control};
        for (int i = 0; i < reinput_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem reinputWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            reinputWechatItem.setWord(mContext.getString(reinput_words[i]));
            reinputWechatItem.setIdentify(REINPUT_WECHAT_MSG_UI_CONTROL);
            elementItems.add(reinputWechatItem);
        }

        int[] select_words = {R.string.navi_first,
                R.string.navi_second, R.string.navi_third};
        for (int i = 0; i < reinput_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem selectWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            selectWechatItem.setWord(mContext.getString(select_words[i]));
            selectWechatItem.setIdentify("" + (i + 1));
            elementItems.add(selectWechatItem);
        }


        mHotwordManager.setElementUCWords(VOICE_SEND_WECHAT, elementItems);
        mHotwordManager.registerListener(VOICE_SEND_WECHAT, new HotwordListener() {
            @Override
            public void onItemSelected(String identify) {
                onSelectOtherOC(identify);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onSwitchPage(int i) {

            }
        });
    }

    public VoicePolicyManage.TtsStateChangeListener mTtsStateChangeListener = new VoicePolicyManage.TtsStateChangeListener() {
        @Override
        public void onStart() {
        }

        @Override
        public void onDone() {
            Log.d(TAG, "onDone " + wechatStep);
            switch (wechatStep) {
                case WechatConstants.WECHAT_INPUT_MSG_STEP:
                case WechatConstants.WECHAT_SEND_MSG_STEP:
                    VoicePolicyManage.getInstance().record(true);
                    break;
            }
        }

        @Override
        public void onError() {
            Log.d(TAG, "onError " + wechatStep);
            switch (wechatStep) {
                case WechatConstants.WECHAT_INPUT_MSG_STEP:
                case WechatConstants.WECHAT_SEND_MSG_STEP:
                    VoicePolicyManage.getInstance().record(true);
                    break;
            }
        }
    };

    public void onSelectOtherOC(String action) {
        Log.e(TAG,"onSelectOtherOC : " + action);
        switch (action) {
            case SEND_WECHAT_MSG_UI_CONTROL:
                //发送后通知语音退出微信流程
                //有其他打断逻辑时记得设置 WECHAT_END domain通知语音退出微信流程
//                handleSendOrder();
                mHandler.sendEmptyMessage(WechatConstants.SEND_WECHAT_MSG_FLAG);
                break;
            case CANCEL_WECHAT_MSG_UI_CONTROL:
                mHandler.sendEmptyMessage(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
                break;
            case REINPUT_WECHAT_MSG_UI_CONTROL:
                mHandler.sendEmptyMessage(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
                break;
            case "1":
            case "2":
            case "3":
                handleVirtual(ActionCode.WECHAT_SELECT_INPUT_SEND, action, mWechatMsg);
                break;
        }
    }

    public void reusmeWechatSendStatus(){
        isOnpause = false;
        sendBtnStatus = ReplyStatus.NOT_MSG;
        VoicePolicyManage.getInstance().speak(mContext.getString(R.string.wechat_send_input_remind_str));
        wechatStep = WechatConstants.WECHAT_INPUT_MSG_STEP;
        addWakeupElements();
    }

    public void pauseWechatSendStatus(){
        isOnpause = true;
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
        }
        VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
        wechatStep = WechatConstants.WECHAT_DEFAULT_STEP;
    }
}