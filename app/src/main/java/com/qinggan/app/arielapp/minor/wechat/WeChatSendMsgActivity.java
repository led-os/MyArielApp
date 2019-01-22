package com.qinggan.app.arielapp.minor.wechat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.wechat.View.MultiDirectionSlidingDrawer;
import com.qinggan.app.arielapp.minor.wechat.inter.SlidingDrawerCallBack;
import com.qinggan.app.arielapp.minor.wechat.inter.WechatSendUICallback;
import com.qinggan.app.arielapp.minor.wechat.utils.WechatConstants;
import com.qinggan.app.arielapp.minor.wechat.utils.WechatSendHelper;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.virtualclick.utils.ActionCode;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * wechat send activity
 */

public class WeChatSendMsgActivity extends Activity implements View.OnClickListener, SlidingDrawerCallBack ,WechatSendUICallback {
    private final String TAG = WeChatSendMsgActivity.class.getSimpleName();
    private MultiDirectionSlidingDrawer mDrawer;

    private TextView mSendMsgTxt, mSendMsgRemindTxt, mSpeechRemindTxt, mWechatSendNameTxt;
    private Button mReplyBtn, mCancelBtn, mOnlyConcelBtn;
    private RelativeLayout mIncomeMsgShowRela;

    /*private String mWechatName;
    private String mWechatMsg;

    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;
    private int wechatStep;*/


    private WechatSendHelper wechatSendHelper;
//    private final static String WECHAT_START = "wechat_start";
//    private final static String WECHAT_SELECT = "wechat_select";
//    private final static String WECHAT_CONTENT = "wechat_content";
//    private final static String WECHAT_SEND = "wechat_send";
//    private final static String WECHAT_END = "wechat_end";

    /*private final String SEND_WECHAT_MSG_UI_CONTROL = "send_msg_wechat_ui_control";
    private final String CANCEL_WECHAT_MSG_UI_CONTROL = "cancel_reply_wechat_ui_control";
    private final String REINPUT_WECHAT_MSG_UI_CONTROL = "reinput_reply_wechat_ui_control";

    private final static String VOICE_SEND_WECHAT = "voice_send_wechat";

    private ReplyStatus sendBtnStatus;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_wechat_send_main);
        wechatSendHelper = WechatSendHelper.getInstance(ArielApplication.getApp());
        initView();
        wechatSendHelper.mWechatName = getIntent().getStringExtra("username");
        EventBus.getDefault().register(this);
        VoicePolicyManage.getInstance().addTtsStatusListeners(wechatSendHelper.mTtsStateChangeListener);
        Log.d(TAG, "onCreate username : " + wechatSendHelper.mWechatName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        String sendName = String.format(getResources().getString(R.string.wechat_send_title_str), wechatSendHelper.mWechatName);
        mWechatSendNameTxt.setText(sendName);
        wechatSendHelper.initHotwordManager();
        /*mHotwordManager = HotwordManager.getInstance(WeChatSendMsgActivity.this, new OnInitListener() {
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
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        wechatSendHelper.reusmeWechatSendStatus();
        wechatSendHelper.registUICallback(this);
        /*isOnpause = false;
        sendBtnStatus = ReplyStatus.NOT_MSG;
        VoicePolicyManage.getInstance().speak(getString(R.string.wechat_send_input_remind_str));
        wechatStep = WechatConstants.WECHAT_INPUT_MSG_STEP;
        addWakeupElements();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        wechatSendHelper.pauseWechatSendStatus();
        wechatSendHelper.removeUICallback(this);
        /*isOnpause = true;
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
        }
        VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
        wechatStep = WechatConstants.WECHAT_DEFAULT_STEP;*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "onDestroy");
    }

    private void initView() {
        mSendMsgTxt = (TextView) findViewById(R.id.send_msg_txt);
        mSendMsgRemindTxt = (TextView) findViewById(R.id.send_msg_remind_txt);
        mSpeechRemindTxt = (TextView) findViewById(R.id.wechat_speech_remind_txt);
        mWechatSendNameTxt = (TextView) findViewById(R.id.wechat_send_name_txt);

        mReplyBtn = (Button) findViewById(R.id.wechat_reply_btn);
        mReplyBtn.setOnClickListener(this);
        mReplyBtn.setVisibility(View.GONE);
        mCancelBtn = (Button) findViewById(R.id.wechat_cancel_btn);
        mCancelBtn.setOnClickListener(this);
        mCancelBtn.setVisibility(View.GONE);
        mOnlyConcelBtn = (Button) findViewById(R.id.wechat_only_cancel_btn);
        mOnlyConcelBtn.setOnClickListener(this);

        mIncomeMsgShowRela= (RelativeLayout) findViewById(R.id.wechat_income_msg_show_rela);
        mIncomeMsgShowRela.setOnClickListener(this);

        mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
        mDrawer.animateOpen();
        mDrawer.setCallBack(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (wechatSendHelper != null) {
            wechatSendHelper.handleMessageEvent(event);
        } else {
            Log.d(TAG, "wechatSendHelper is null");
            Log.d(TAG, "skip MessageEvent:" + event);
        }
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.d(TAG, "event : " + event.toString());
        switch (event.getStep()) {
            case 1:
                break;
            case 2:
                //MSG
                mSendMsgTxt.setText(event.getMsg());
                mWechatName = event.getName();
                mWechatMsg = event.getMsg();
                mSendMsgRemindTxt.setVisibility(View.VISIBLE);
                mSpeechRemindTxt.setText(R.string.wechat_send_speech_remind_str);
                mReplyBtn.setVisibility(View.VISIBLE);
                mCancelBtn.setVisibility(View.VISIBLE);
                mOnlyConcelBtn.setVisibility(View.GONE);
                sendBtnStatus = ReplyStatus.SEND;
                wechatStep = WechatConstants.WECHAT_DEFAULT_STEP;
                mReplyBtn.setBackgroundResource(R.drawable.wechat_send_select);
                VoicePolicyManage.getInstance().speak(getString(R.string.wechat_send_confirm_remind_str));
                break;
            case 3:
                //SEND
                mWechatName = event.getName();
                EventBus.getDefault().post(WechatConstants.WECHAT_SEND_MSG_ORDER);
                handleVirtual(ActionCode.WECHAT_SEARCH_PERSON, event.getName(), null);
                WeChatSendMsgActivity.this.finish();
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
    }*/

    @Override
    public void openCallBack() {

    }

    @Override
    public void closeCallBack() {

    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        switch (viewId) {
            case R.id.wechat_reply_btn:
                //发送
                if (wechatSendHelper.mWechatName != null && !wechatSendHelper.mWechatName.equals("")) {
                    EventBus.getDefault().post(WechatConstants.WECHAT_SEND_MSG_ORDER);
                    wechatSendHelper.handleVirtual(ActionCode.WECHAT_SEARCH_PERSON, wechatSendHelper.mWechatName, null);
                    WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                } else {
                    VoicePolicyManage.getInstance().speak(getString(R.string.wechat_get_address_error_remind_str));
                    WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                    WeChatSendMsgActivity.this.finish();
                }
                break;
            case R.id.wechat_cancel_btn:
            case R.id.wechat_only_cancel_btn:
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                VoicePolicyManage.getInstance().speakStop();
                //恢复语音状态
                //销毁当前界面
                WeChatSendMsgActivity.this.finish();
                break;
            case R.id.wechat_income_msg_show_rela:
                //mHandler.sendEmptyMessage(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
                wechatSendHelper.reinputMsg();
                break;
        }
    }

    /*private void handleVirtual(int actionCode, String name, String action) {
        ActionBean actionBean = new ActionBean();
        switch (actionCode) {
            case ActionCode.WECHAT_SEARCH_PERSON:
                actionBean.setAddressee(name);
                break;
        }

        actionBean.setActionCode(actionCode);
        Log.d(TAG, "actionBean : " + actionBean);
        AccessibilityUtil.initAccessibility(this);
        PateoVirtualSDK.doAction(WeChatSendMsgActivity.this,
                actionBean, IntegrationCore.getIntergrationCore(WeChatSendMsgActivity.this));
    }*/

    /*private void addWakeupElements() {
        if (mHotwordManager == null) {
            return;
        }

        Log.d(TAG, " addWakeupElements ");

        ArrayList<UIControlElementItem> elementItems =
                new ArrayList<>();


        com.qinggan.qinglink.bean.UIControlElementItem sendWechatItem =
                new com.qinggan.qinglink.bean.UIControlElementItem();
        sendWechatItem.setWord(getString(R.string.reply_wechat_send_ui_control));
        sendWechatItem.setIdentify(SEND_WECHAT_MSG_UI_CONTROL);
        elementItems.add(sendWechatItem);


        int[] cancel_words = {R.string.reply_wechat_exit_ui_control,
                R.string.reply_wechat_close_ui_control2};
        for (int i = 0; i < cancel_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem cancelWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            cancelWechatItem.setWord(getString(cancel_words[i]));
            cancelWechatItem.setIdentify(CANCEL_WECHAT_MSG_UI_CONTROL);
            elementItems.add(cancelWechatItem);
        }


        int[] reinput_words = {R.string.reinputs_wechat_cancel_ui_control,
                R.string.reinput_wechat_cancel_ui_control};
        for (int i = 0; i < reinput_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem reinputWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            reinputWechatItem.setWord(getString(reinput_words[i]));
            reinputWechatItem.setIdentify(REINPUT_WECHAT_MSG_UI_CONTROL);
            elementItems.add(reinputWechatItem);
        }

        int[] select_words = {R.string.navi_first,
                R.string.navi_second, R.string.navi_third};
        for (int i = 0; i < reinput_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem selectWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            selectWechatItem.setWord(getString(select_words[i]));
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
    }*/

    /*public void onSelectOtherOC(String action) {
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
    }*/

    /*Handler mHandler = new Handler() {
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
                        WeChatSendMsgActivity.this.finish();
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
                        mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);
                        mSendMsgTxt.setText("");
                        sendBtnStatus = ReplyStatus.NOT_MSG;
                        VoicePolicyManage.getInstance().record(true);
                    }
                    break;
            }
        }
    };*/

    /*private void intoMainAct() {
        WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
        WeChatSendMsgActivity.this.finish();
        Intent mIntent = new Intent(WeChatSendMsgActivity.this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        WeChatSendMsgActivity.this.startActivity(mIntent);
    }*/

    /*private VoicePolicyManage.TtsStateChangeListener mTtsStateChangeListener = new VoicePolicyManage.TtsStateChangeListener() {
        @Override
        public void onStart() {
        }

        @Override
        public void onDone() {
            Log.d(TAG, "onDone " + wechatSendHelper.wechatStep);
            switch (wechatSendHelper.wechatStep) {
                case 2:
                    VoicePolicyManage.getInstance().record(true);
                    break;
            }
        }

        @Override
        public void onError() {
            Log.d(TAG, "onError " + wechatSendHelper.wechatStep);
            switch (wechatSendHelper.wechatStep) {
                case 2:
                    VoicePolicyManage.getInstance().record(true);
                    break;
            }
        }
    };*/

    @Override
    public void updateSendUI(int uiStatus) {

        switch (uiStatus) {
            case WechatConstants.WECHAT_UI_SEND_STEP2:
                mSendMsgTxt.setText(wechatSendHelper.mWechatMsg);
                mSendMsgRemindTxt.setVisibility(View.VISIBLE);
                mSpeechRemindTxt.setText(R.string.wechat_send_speech_remind_str);
                mReplyBtn.setVisibility(View.VISIBLE);
                mCancelBtn.setVisibility(View.VISIBLE);
                mOnlyConcelBtn.setVisibility(View.GONE);
                mReplyBtn.setBackgroundResource(R.drawable.wechat_send_select);
                break;
            case WechatConstants.WECHAT_UI_REINPUT:
                mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);
                mSendMsgTxt.setText("");
                break;
        }

    }

    @Override
    public void finishUI() {
        WeChatSendMsgActivity.this.finish();
    }
}
