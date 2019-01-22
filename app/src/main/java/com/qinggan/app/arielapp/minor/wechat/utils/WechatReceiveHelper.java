package com.qinggan.app.arielapp.minor.wechat.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.integration.MusicContacts;
import com.qinggan.app.arielapp.minor.phone.utils.ChineseToPY;
import com.qinggan.app.arielapp.minor.utils.ShardPreUtils;
import com.qinggan.app.arielapp.minor.wechat.NotificationBean;
import com.qinggan.app.arielapp.minor.wechat.ReplyStatus;
import com.qinggan.app.arielapp.minor.wechat.WeChatTranslucentActivity;
import com.qinggan.app.arielapp.minor.wechat.inter.WechatReceiveUICallback;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatAnswerCallback;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatCallback;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pateo on 19-1-16.
 */

public class WechatReceiveHelper {

    private static final String TAG = WechatReceiveHelper.class.getSimpleName();

    private final String MSG_TTS_SAVE_STATUS_KEY = "wechat_tts_save_status";

    public final static String WECHAT_START = "wechat_start";
    public final static String WECHAT_SELECT = "wechat_select";
    public final static String WECHAT_CONTENT = "wechat_content";
    public final static String WECHAT_SEND = "wechat_send";
    public final static String WECHAT_END = "wechat_end";

    private final static String WECHAT_ANSWER_START = "wechat_answer_start";

    private final String REPLY_WECHAT_MSG_UI_CONTROL = "reply_msg_wechat_ui_control";
    private final String NEXT_WECHAT_MSG_UI_CONTROL = "next_msg_wechat_ui_control";
    private final String SEND_WECHAT_MSG_UI_CONTROL = "send_msg_wechat_ui_control";
    private final String CANCEL_WECHAT_MSG_UI_CONTROL = "cancel_reply_wechat_ui_control";
    private final String REINPUT_WECHAT_MSG_UI_CONTROL = "reinput_reply_wechat_ui_control";
    private final String OPEN_TTS_WECHAT_MSG_UI_CONTROL = "open_msg_tts_wechat_ui_control";
    private final String CLOSE_TTS_WECHAT_MSG_UI_CONTROL = "close_msg_tts_wechat_ui_control";

    private final static String VOICE_REPLY_WECHAT = "voice_reply_wechat";

    private static WechatReceiveHelper instance;
    private static Context mContext;


    private List<WechatReceiveUICallback> mUICallbackList = new ArrayList<>();
    //private HotwordManager mHotwordManager;
    private boolean isOnpause;
    private boolean isScroll;

    private WechatReceiveHelper() {

    }

    public static WechatReceiveHelper getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            synchronized (WechatReceiveHelper.class) {
                if (instance == null) {
                    instance = new WechatReceiveHelper();
                }
            }
        }
        return instance;
    }


    private void updateUI(int uiStatus) {
        if (mUICallbackList.size() > 0) {
            for (WechatReceiveUICallback uiCallback : mUICallbackList) {
                uiCallback.updateReceiveUI(uiStatus);
            }
        }
    }

    private void finishUI() {
        System.out.println("---alvin-----mUICallbackList.size()------>>" + mUICallbackList.size());
        if (mUICallbackList.size() > 0) {
            for (WechatReceiveUICallback uiCallback : mUICallbackList) {
                uiCallback.finishUI();
            }
        }
    }


    public void registUICallback(WechatReceiveUICallback uiCallback) {
        if (!mUICallbackList.contains(uiCallback)) {
            mUICallbackList.add(uiCallback);
        }
    }

    public void removeUICallback(WechatReceiveUICallback uiCallback) {
        if (mUICallbackList.contains(uiCallback)) {
            mUICallbackList.remove(uiCallback);
        }
    }


    public ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> createWakeupElements() {
        Log.d(TAG, " createWakeupElements ");

        ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> elementItems =
                new ArrayList<>();
//        com.qinggan.qinglink.bean.UIControlElementItem replyWechatItem =
//                new com.qinggan.qinglink.bean.UIControlElementItem();
//        replyWechatItem.setWord(getString(R.string.reply_wechat_ui_control));
//        replyWechatItem.setIdentify(REPLY_WECHAT_MSG_UI_CONTROL);
//        elementItems.add(replyWechatItem);

        int[] reply_words = {R.string.reply_wechat_ui_control,
                R.string.replys_wechat_ui_control};
        for (int i = 0; i < reply_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem replyWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            replyWechatItem.setWord(mContext.getString(reply_words[i]));
            replyWechatItem.setIdentify(REPLY_WECHAT_MSG_UI_CONTROL);
            elementItems.add(replyWechatItem);
        }


//        com.qinggan.qinglink.bean.UIControlElementItem nextWechatItem =
//                new com.qinggan.qinglink.bean.UIControlElementItem();
//        nextWechatItem.setWord(getString(R.string.next_wechat_msg_ui_control));
//        nextWechatItem.setIdentify(NEXT_WECHAT_MSG_UI_CONTROL);
//        elementItems.add(nextWechatItem);

        int[] next_words = {R.string.next_wechat_msg_ui_control,
                R.string.nexts_wechat_msg_ui_control};
        for (int i = 0; i < next_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem nextWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            nextWechatItem.setWord(mContext.getString(next_words[i]));
            nextWechatItem.setIdentify(NEXT_WECHAT_MSG_UI_CONTROL);
            elementItems.add(nextWechatItem);
        }

        com.qinggan.qinglink.bean.UIControlElementItem sendWechatItem =
                new com.qinggan.qinglink.bean.UIControlElementItem();
        sendWechatItem.setWord(mContext.getString(R.string.reply_wechat_send_ui_control));
        sendWechatItem.setIdentify(SEND_WECHAT_MSG_UI_CONTROL);
        elementItems.add(sendWechatItem);


//        com.qinggan.qinglink.bean.UIControlElementItem notReplyWechatItem =
//                new com.qinggan.qinglink.bean.UIControlElementItem();
//        notReplyWechatItem.setWord(getString(R.string.reply_wechat_not_ui_control));
//        notReplyWechatItem.setIdentify(CANCEL_WECHAT_MSG_UI_CONTROL);
//        elementItems.add(notReplyWechatItem);
//
//        com.qinggan.qinglink.bean.UIControlElementItem cancelWechatItem =
//                new com.qinggan.qinglink.bean.UIControlElementItem();
//        cancelWechatItem.setWord(getString(R.string.reply_wechat_cancel_ui_control));
//        cancelWechatItem.setIdentify(CANCEL_WECHAT_MSG_UI_CONTROL);
//        elementItems.add(cancelWechatItem);

        int[] cancel_words = {R.string.reply_wechat_back_ui_control,
                R.string.reply_wechat_exit_ui_control,
                R.string.reply_wechat_close_ui_control,
                R.string.reply_wechat_close_ui_control2,
                R.string.reply_wechat_close_ui_control3,
                R.string.reply_wechat_close_ui_control4};
        for (int i = 0; i < cancel_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem cancelWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            cancelWechatItem.setWord(mContext.getString(cancel_words[i]));
            cancelWechatItem.setIdentify(CANCEL_WECHAT_MSG_UI_CONTROL);
            elementItems.add(cancelWechatItem);
        }


//        com.qinggan.qinglink.bean.UIControlElementItem reinputWechatItem =
//                new com.qinggan.qinglink.bean.UIControlElementItem();
//        reinputWechatItem.setWord(getString(R.string.reinput_wechat_cancel_ui_control));
//        reinputWechatItem.setIdentify(REINPUT_WECHAT_MSG_UI_CONTROL);
//        elementItems.add(reinputWechatItem);

        int[] reinput_words = {R.string.reinputs_wechat_cancel_ui_control,
                R.string.reinput_wechat_cancel_ui_control};
        for (int i = 0; i < reinput_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem reinputWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            reinputWechatItem.setWord(mContext.getString(reinput_words[i]));
            reinputWechatItem.setIdentify(REINPUT_WECHAT_MSG_UI_CONTROL);
            elementItems.add(reinputWechatItem);
        }


        int[] open_tts_words = {R.string.open_msg_tts_wechat_ui_control,
                R.string.open_msg_tts_wechat_ui_control2,
                R.string.open_msg_tts_wechat_ui_control3};
        for (int i = 0; i < open_tts_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem openTTSItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            openTTSItem.setWord(mContext.getString(open_tts_words[i]));
            openTTSItem.setIdentify(OPEN_TTS_WECHAT_MSG_UI_CONTROL);
            elementItems.add(openTTSItem);
        }

        int[] close_tts_words = {R.string.close_msg_tts_wechat_ui_control,
                R.string.close_msg_tts_wechat_ui_control2,
                R.string.close_msg_tts_wechat_ui_control3};
        for (int i = 0; i < close_tts_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem closeTTSItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            closeTTSItem.setWord(mContext.getString(close_tts_words[i]));
            closeTTSItem.setIdentify(CLOSE_TTS_WECHAT_MSG_UI_CONTROL);
            elementItems.add(closeTTSItem);
        }

        return elementItems;

    }

    public void onSelectOtherOC(String action) {
        Log.e(TAG, "onSelectOtherOC : " + action);
//        if (action.equals(REPLY_WECHAT_MSG_UI_CONTROL)) {
//            if (replyBtnStatus == ReplyStatus.INTO_REPLY) {
//                intoReplyorInCome(true);
//            }
//            //开始回复通知语音开始微信回复流程
//            NluResultManager.getInstance().setWechatDomain(WECHAT_ANSWER_START);
//        } else if (action.equals(NEXT_WECHAT_MSG_UI_CONTROL)) {
//            if (mActualNotiList.size() > 1) {
//                mWeChatMsgListView.scrollToPosition(adapterCenterPionstion + 1);
//                intoReplyorInCome(false);
//                updateWeChatView(null);
//            } else {
//                intoMainAct();
//            }
//        } else if (action.equals(SEND_WECHAT_MSG_UI_CONTROL)) {
//            //发送后通知语音退出微信流程
//            //有其他打断逻辑时记得设置 WECHAT_END domain通知语音退出微信流程
//            handleSendOrder();
//        }

        switch (action) {
            case REPLY_WECHAT_MSG_UI_CONTROL:
                mHandler.sendEmptyMessage(WechatConstants.REPLY_WECHAT_MSG_FLAG);
                break;
            case NEXT_WECHAT_MSG_UI_CONTROL:
                mHandler.sendEmptyMessage(WechatConstants.NEXT_WECHAT_MSG_FLAG);
                break;
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
            case OPEN_TTS_WECHAT_MSG_UI_CONTROL:
                mHandler.sendEmptyMessage(WechatConstants.OPEN_TTS_WECHAT_MSG_FLAG);
                break;
            case CLOSE_TTS_WECHAT_MSG_UI_CONTROL:
                mHandler.sendEmptyMessage(WechatConstants.CLOSE_TTS_WECHAT_MSG_FLAG);
                break;
        }
    }


    public int wechatStep;
    public int adapterCenterPionstion = -1;
    public ReplyStatus replyBtnStatus;
    public String mUserVoiceInput;
    //列表view使用的list
    public List<NotificationBean> mNotiList = new ArrayList<>();
    //实际逻辑控制的list
    public List<NotificationBean> mActualNotiList = new ArrayList<>();

    public boolean voiceSwitchState;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg.what : " + msg.what);
            switch (msg.what) {
                case WechatConstants.REPLY_WECHAT_MSG_FLAG:
                    intoReplyorInCome(true);
                    WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER_CONTENT);
                    speekMsg(mContext.getString(R.string.wechat_reply_remind_str));
                    wechatStep = WechatConstants.WECHAT_GET_NAME_STEP;
                    break;
                case WechatConstants.NEXT_WECHAT_MSG_FLAG:
                    if (mActualNotiList.size() > 1) {
                        updateUI(WechatConstants.WECHAT_UI_NEXT_WECHAT);
                        /*mWeChatMsgListView.scrollToPosition(adapterCenterPionstion + 1);
                        intoReplyorInCome(false);
                        updateWeChatView(null);*/
                    } else {
                        intoMainAct();
                    }
                    break;
                case WechatConstants.SEND_WECHAT_MSG_FLAG:
                    Log.d(TAG, "replyBtnStatus : " + replyBtnStatus);
                    if (replyBtnStatus == ReplyStatus.SEND) {
                        autoReply();
                        intoReplyorInCome(false);
                    }
                    break;
                case WechatConstants.CANCEL_WECHAT_MSG_FLAG:
                    VoicePolicyManage.getInstance().speakStop();
                    intoMainAct();
                    break;
                case WechatConstants.REINPUT_WECHAT_MSG_FLAG:
                    mUserVoiceInput = "";
                    System.out.println("---alvin--adapterCenterPionstion--" + adapterCenterPionstion);
                    mNotiList.get(adapterCenterPionstion).setInput("");
                    updateUI(WechatConstants.WECHAT_UI_REINPUT);
                    /*mAdapter.notifyItemChanged(adapterCenterPionstion);
                    mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);*/
                    replyBtnStatus = ReplyStatus.NOT_MSG;
                    VoicePolicyManage.getInstance().record(true);
                    WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER_CONTENT);
                    break;
                case WechatConstants.OPEN_TTS_WECHAT_MSG_FLAG:
                    if (!voiceSwitchState) {
                        updateVoiceStatus(true, R.string.wechat_voice_switch_on_str);
                        /*voiceSwitchState = true;
                        mVoiceSwitchBtn.setSelected(true);
                        mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_on_str);*/
                    }
                    break;
                case WechatConstants.CLOSE_TTS_WECHAT_MSG_FLAG:
                    if (voiceSwitchState) {
                        updateVoiceStatus(false, R.string.wechat_voice_switch_off_str);
                        /*voiceSwitchState = false;
                        mVoiceSwitchBtn.setSelected(false);
                        mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_off_str);*/
                    }
                    break;
            }
        }
    };


    /*public void initHotwordManager(){
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
                        mHotwordManager.clearElementUCWords(VOICE_REPLY_WECHAT);
                    }
                }
            }
        });
    }*/

    public void initOrSaveTTS(boolean isInit) {
        if (isInit) {
            //1.读取数据库,0是关闭，1是开启
            int status = ShardPreUtils.getInstance(MusicContacts.DEFAULT_FILE)
                    .getIntValue(MSG_TTS_SAVE_STATUS_KEY);
            boolean isLastVoiceStatusOn = status > 0 ? true : false;
            //2.更新view状态
            updateVoiceStatus(isLastVoiceStatusOn,-1);
        } else {
            int saveStatus = voiceSwitchState ? 1 : 0;
            ShardPreUtils.getInstance(MusicContacts.DEFAULT_FILE)
                    .putIntValue(MSG_TTS_SAVE_STATUS_KEY, saveStatus);
        }
    }

    private void updateVoiceStatus(boolean isSelected, int textResourceId) {
        if (mUICallbackList.size() > 0) {
            for (WechatReceiveUICallback uiCallback : mUICallbackList) {
                uiCallback.updateVoiceStatus(isSelected, textResourceId);
            }
        }
    }

    public void speekMsg(final NotificationBean msgBean) {
        if (msgBean == null || !voiceSwitchState) {
            return;
        }
        String speekMsg = msgBean.getSender() + "发来消息" + msgBean.getMsg();
        VoicePolicyManage.getInstance().speak(speekMsg);
    }

    public void speekMsg(String msg) {
        if (msg == null || msg.length() == 0) {
            return;
        }
        VoicePolicyManage.getInstance().speak(msg);
    }

    public void intoMainAct() {
        System.out.println("--alvin----intoMainAct-->>>");
        WechatManager.getInstance().setWechatDomain(WECHAT_END);
//        VoicePolicyManage.getInstance().record(true);
        IntegrationCore.getIntergrationCore(mContext).cleanNoticesList();
        finishUI();
        Intent mIntent = new Intent(mContext, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //彻底退出微信接收消息界面
        UMAnalyse.stopTime(UMDurationEvent.WECHAT);
        mContext.startActivity(mIntent);
    }

    private void intoReplyorInCome(boolean isReply) {
        if (mUICallbackList.size() > 0) {
            for (WechatReceiveUICallback uiCallback : mUICallbackList) {
                uiCallback.intoReplyorInCome(isReply);
            }
        }
    }
 
    private void autoReply() {
        if (mUICallbackList.size() > 0) {
            for (WechatReceiveUICallback uiCallback : mUICallbackList) {
                uiCallback.autoReply();
            }
        }
    }

    /*
    public WechatAnswerCallback mWechatAnswerCallback = new WechatAnswerCallback() {
        @Override
        public void onWechatAnswerMessage(String message) {
            //message为响应回复后的说话内容
            Log.d(TAG, "user voice input msg : " + message);
            WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER);
            if (message.equals(mContext.getString(R.string.reinput_wechat_cancel_ui_control))) {
                mUserVoiceInput = "";
                mNotiList.get(adapterCenterPionstion).setInput("");
                setWechatAnswer(adapterCenterPionstion,R.mipmap.wechat_not_reply_btn);
                *//*mAdapter.notifyItemChanged(adapterCenterPionstion);
                mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);*//*
                replyBtnStatus = ReplyStatus.NOT_MSG;
            } else {
                mUserVoiceInput = message;
                mNotiList.get(adapterCenterPionstion).setInput(message);
                setWechatAnswer(adapterCenterPionstion,-1);
                *//*mAdapter.notifyItemChanged(adapterCenterPionstion);*//*
                replyBtnStatus = ReplyStatus.SEND;
                wechatStep = WechatConstants.WECHAT_INPUT_MSG_STEP;
                speekMsg(mContext.getString(R.string.wechat_send_speech_remind_str));
            }
        }

        @Override
        public void onWechatAnswerSend(String type) {
            Log.d(TAG, "user voice input msg type : " + type);
            if (WechatManager.WECHAT_ANSWER_START.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                mHandler.sendEmptyMessage(WechatConstants.SEND_WECHAT_MSG_FLAG);
            } else if (WechatManager.WECHAT_ANSWER_CANCEL.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                mHandler.sendEmptyMessage(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
            } else if (WechatManager.WECHAT_ANSWER_AGAIN.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER_CONTENT);
                mHandler.sendEmptyMessage(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
            }
        }
    };

    */


    /*public void setOnReusmeWechatReceiveStatus(){
        isOnpause = false;
        addWakeupElements();
    }*/

    /*public void setOnPauseWechatReceiveStatus(){
        initOrSaveTTS(false);
        isOnpause = true;
        wechatStep = WechatConstants.WECHAT_DEFAULT_STEP;
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords(VOICE_REPLY_WECHAT);
        }
    }*/

    public void handleVirtual(int actionCode, String name, String action) {
        ActionBean actionBean = new ActionBean();
        switch (actionCode) {
            case ActionCode.WECHAT_SEARCH_PERSON:
                actionBean.setAddressee(name);
                break;
            case ActionCode.WECHAT_SELECT_CONTACTS:
                actionBean.setAction(action);
                break;
            case ActionCode.WECHAT_SEND_MSG:
                actionBean.setAction(action);
                break;
            case ActionCode.WECHAT_SEND_POSITION:
                //TODO
                break;
            case ActionCode.WECHAT_SEND_MONEY:
                //TODO
                break;
            case ActionCode.WECHAT_INPUT_SEND_MSG:
                actionBean.setAction(action);
                break;
            case ActionCode.WECHAT_CONFIRM_SEND:
                //TODO
                break;
            case ActionCode.WECHAT_SELECT_INPUT_SEND:
                actionBean.setAddressee(name);
                actionBean.setAction(action);
                break;
        }

        actionBean.setActionCode(actionCode);
        Log.d(TAG, "actionBean is : " + actionBean);
        AccessibilityUtil.initAccessibility(mContext);
        PateoVirtualSDK.doAction(mContext,
                actionBean, IntegrationCore.getIntergrationCore(mContext));
    }

    public void sendHandlerMsg(int msgWhat){
        mHandler.sendEmptyMessage(msgWhat);
    }
    /*private void handleReply() {
        if (replyBtnStatus == ReplyStatus.INTO_REPLY) {
            intoReplyorInCome(true);
        }
    }

    */


    private WechatCallback mWechatCallback = new WechatCallback() {
        @Override
        public void onPrepareSendWechat(String name) {
            if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
                WechatManager.getInstance().setWechatDomain(WECHAT_END);
                return;
            }

            String pinyinName = ChineseToPY.getAllPinYinFirstLetter(name);
            WechatManager.getInstance().setWechatDomain(WECHAT_SELECT);
            //VoicePolicyManage.getInstance().record(true);
            handleVirtual(ActionCode.WECHAT_SEARCH_PERSON, pinyinName, null);
        }

        @Override
        public void onWechatPersonSelect(final String selectedId) {
//            Toast.makeText(WeChatTranslucentActivity.this, "第 " + selectedId + " 个", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WechatManager.getInstance().setWechatDomain(WECHAT_END);
                    //VoicePolicyManage.getInstance().record(true);
                    handleVirtual(ActionCode.WECHAT_SELECT_INPUT_SEND, selectedId, mUserVoiceInput);
                }
            }, 1000);
        }

        @Override
        public void onWechatSendMessage(String message) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            WechatManager.getInstance().setWechatDomain(WECHAT_SEND);
            //VoicePolicyManage.getInstance().record(true);
            handleVirtual(ActionCode.WECHAT_INPUT_SEND_MSG, null, message);
        }

        @Override
        public void onSendWechat(String type) {
            Toast.makeText(mContext, "发送", Toast.LENGTH_SHORT).show();
            WechatManager.getInstance().setWechatDomain(WECHAT_END);
            handleVirtual(ActionCode.WECHAT_CONFIRM_SEND, null, null);
        }
    };

    private WechatAnswerCallback mWechatAnswerCallback = new WechatAnswerCallback() {
        @Override
        public void onWechatAnswerMessage(String message) {
            //message为响应回复后的说话内容
            Log.d(TAG, "user voice input msg : " + message);
            WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER);
            if (message.equals(mContext.getString(R.string.reinput_wechat_cancel_ui_control))) {
                mUserVoiceInput = "";
                mNotiList.get(adapterCenterPionstion).setInput("");
                setWechatAnswerMessageUI(adapterCenterPionstion, R.mipmap.wechat_not_reply_btn);
                //mAdapter.notifyItemChanged(adapterCenterPionstion);
                //mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);
                replyBtnStatus = ReplyStatus.NOT_MSG;
            } else {
                mUserVoiceInput = message;
                mNotiList.get(adapterCenterPionstion).setInput(message);
                setWechatAnswerMessageUI(adapterCenterPionstion, WechatConstants.INVALID_RESOURCE_ID);
                //mAdapter.notifyItemChanged(adapterCenterPionstion);
                replyBtnStatus = ReplyStatus.SEND;
                wechatStep = WechatConstants.WECHAT_INPUT_MSG_STEP;
                speekMsg(mContext.getString(R.string.wechat_send_speech_remind_str));
            }
        }

        @Override
        public void onWechatAnswerSend(String type) {
            Log.d(TAG, "user voice input msg type : " + type);
            if (WechatManager.WECHAT_ANSWER_START.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                sendHandlerMsg(WechatConstants.SEND_WECHAT_MSG_FLAG);
                //mHandler.sendEmptyMessage(WechatConstants.SEND_WECHAT_MSG_FLAG);
            } else if (WechatManager.WECHAT_ANSWER_CANCEL.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                sendHandlerMsg(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
                //mHandler.sendEmptyMessage(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
            } else if (WechatManager.WECHAT_ANSWER_AGAIN.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER_CONTENT);
                sendHandlerMsg(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
                //mHandler.sendEmptyMessage(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
            }
        }
    };

    private void setWechatAnswerMessageUI(int adapterCenterPionstion,int resourceId) {
        if (mUICallbackList.size() > 0) {
            for (WechatReceiveUICallback uiCallback : mUICallbackList) {
                uiCallback.setWechatAnswerMessageUI(adapterCenterPionstion, resourceId);
            }
        }
    }

    public WechatCallback getWechatCallback() {
        return mWechatCallback;
    }

    public WechatAnswerCallback getWechatAnswerCallback() {
        return mWechatAnswerCallback;
    }
}