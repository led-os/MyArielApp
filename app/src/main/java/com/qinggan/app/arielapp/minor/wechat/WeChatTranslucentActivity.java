package com.qinggan.app.arielapp.minor.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.UIControlBaseActivity;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.phone.utils.ChineseToPY;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.minor.wechat.View.CarouselLayoutManager;
import com.qinggan.app.arielapp.minor.wechat.View.CarouselZoomPostLayoutListener;
import com.qinggan.app.arielapp.minor.wechat.View.CenterScrollListener;
import com.qinggan.app.arielapp.minor.wechat.View.MultiDirectionSlidingDrawer;
import com.qinggan.app.arielapp.minor.wechat.View.WeChatNoticesAdapter;
import com.qinggan.app.arielapp.minor.wechat.inter.SlidingDrawerCallBack;
import com.qinggan.app.arielapp.minor.wechat.inter.WechatReceiveUICallback;
import com.qinggan.app.arielapp.minor.wechat.utils.SystemTTS;
import com.qinggan.app.arielapp.minor.wechat.utils.WeChatUtils;
import com.qinggan.app.arielapp.minor.wechat.utils.WechatConstants;
import com.qinggan.app.arielapp.minor.wechat.utils.WechatReceiveHelper;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.arielapp.utils.AllWakeupEvent;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatAnswerCallback;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatCallback;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatManager;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 透明Activity
 */

public class WeChatTranslucentActivity extends UIControlBaseActivity implements View.OnClickListener,
        SlidingDrawerCallBack ,WechatReceiveUICallback {
    private String TAG = WeChatTranslucentActivity.class.getSimpleName();
    private TextView mInComeContactTxt, mInComeMsgTxt, mVoiceSwitchTxt, mReplyRemindTxt,
            mSpeechRemindTxt;
    private EditText mReplyMsgTxt;
    private Button mReplyBtn, mCancelBtn;
    private ImageButton mVoiceSwitchBtn;
    private RelativeLayout mVoiceTitleRela, mReplyTitleRela;
    private MultiDirectionSlidingDrawer mDrawer;
    private RecyclerView mWeChatMsgListView;

    //private boolean voiceSwitchState;

    /*//列表view使用的list
    private List<NotificationBean> mNotiList = new ArrayList<>();
    //实际逻辑控制的list
    private List<NotificationBean> mActualNotiList = new ArrayList<>();
    //当前显示的item位置
    private int adapterCenterPionstion = -1;*/
    //
    private boolean isShowDrawer;

    private SystemTTS mTTS;

    private WeChatNoticesAdapter mAdapter;

    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;

    /*private int wechatStep;
    private ReplyStatus replyBtnStatus;
    private String mUserVoiceInput;  //用户语音输入子串*/

    //private final String MSG_TTS_SAVE_STATUS_KEY = "wechat_tts_save_status";

    private final static String WECHAT_START = "wechat_start";
    private final static String WECHAT_SELECT = "wechat_select";
    private final static String WECHAT_CONTENT = "wechat_content";
    private final static String WECHAT_SEND = "wechat_send";
    private final static String WECHAT_END = "wechat_end";

    /*private final static String WECHAT_ANSWER_START = "wechat_answer_start";

    private final String REPLY_WECHAT_MSG_UI_CONTROL = "reply_msg_wechat_ui_control";
    private final String NEXT_WECHAT_MSG_UI_CONTROL = "next_msg_wechat_ui_control";
    private final String SEND_WECHAT_MSG_UI_CONTROL = "send_msg_wechat_ui_control";
    private final String CANCEL_WECHAT_MSG_UI_CONTROL = "cancel_reply_wechat_ui_control";
    private final String REINPUT_WECHAT_MSG_UI_CONTROL = "reinput_reply_wechat_ui_control";
    private final String OPEN_TTS_WECHAT_MSG_UI_CONTROL = "open_msg_tts_wechat_ui_control";
    private final String CLOSE_TTS_WECHAT_MSG_UI_CONTROL = "close_msg_tts_wechat_ui_control";*/

    private final static String VOICE_REPLY_WECHAT = "voice_reply_wechat";

    private WechatReceiveHelper mWechatReceiveHelper;

    /**
     * isScroll是判断是否滑动的flag，当mActualNotiList size是1时，回复消息,设置滑动，
     * 是不会滑动的，这时执行成功返回时，要根据这个flag的状态来更新view
     */
    private boolean isScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mWechatReceiveHelper = WechatReceiveHelper.getInstance(ArielApplication.getApp());
        super.onCreate(savedInstanceState);
        mWechatReceiveHelper.registUICallback(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateView();
        IntegrationCore.getIntergrationCore(this).setNeedShowWeChat(false);
//        initUIControl();
        NluResultManager.getInstance().setWechatAnswerCallback(mWechatReceiveHelper.getWechatAnswerCallback());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        isOnpause = false;
        VoicePolicyManage.getInstance().addTtsStatusListeners(mTtsStateChangeListener);
        addWakeupElements();
        UMAnalyse.startTime(UMDurationEvent.WECHAT);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        IntegrationCore.getIntergrationCore(this).setNeedShowWeChat(true);
        VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
        mWechatReceiveHelper.initOrSaveTTS(false);
        isOnpause = true;
        mWechatReceiveHelper.wechatStep = WechatConstants.WECHAT_DEFAULT_STEP;
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords(VOICE_REPLY_WECHAT);
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        isShowDrawer = false;
        EventBus.getDefault().unregister(this);
        mWechatReceiveHelper.removeUICallback(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoticeEvent event) {
        Log.d(TAG, "event : " + event.toString());
        if (event.getNotice() != null) {
            // 更新界面
            mAdapter.setLastMsg(false);
            mAdapter.addNotice(event.getNotice());
            mWechatReceiveHelper.mActualNotiList.add(event.getNotice());
            mAdapter.notifyItemInserted(mWechatReceiveHelper.mNotiList.size());
            //更新指定的item view,mActualNotiList是实际的list记录
            //mActualNotiList的0位置显示的item内容
            if (mWechatReceiveHelper.mActualNotiList.size() > 0) {
                mAdapter.notifyItemChanged(mWechatReceiveHelper.mNotiList.indexOf(mWechatReceiveHelper.mActualNotiList.get(0)));
            }
            updateSpeechRemindView();
        } else {
            if (!event.isDoactionSuccess()) {
                switch (event.getActionCode()) {
                    case ActionCode.WECHAT_SEARCH_PERSON:
                        WechatManager.getInstance().setWechatDomain(WECHAT_END);
                        break;
                    case ActionCode.WECHAT_SELECT_CONTACTS:
                        WechatManager.getInstance().setWechatDomain(WECHAT_SELECT);
                        break;
                    case ActionCode.WECHAT_SEND_MSG:
                        WechatManager.getInstance().setWechatDomain(WECHAT_CONTENT);
                        break;
                }
            }

            System.out.println("--alvin----isScroll-->>>" + isScroll);
            //回复成功后，消息列表长度减一
            if (!isScroll) {
                mWechatReceiveHelper.intoMainAct();
            } else {
                updateWeChatView(null);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllWakeupEvent event) {
        switch (event) {
            case WECHAT_REPLY:
                handleReply();
                break;
            case WECHAT_CANCEL:
                handleCancel();
                break;
            case WECHAT_NEXT_MSG:
                handleNextMsg();
                break;
            case WECHAT_OPEN_READ_MSG:
                handleOpenRead();
                break;
            case WECHAT_CLOSE_READ_MSG:
                handleCloseRead();
                break;
        }
    }

    @Override
    protected void initView() {
        mWechatReceiveHelper.mNotiList.clear();
        mWechatReceiveHelper.mActualNotiList.clear();

        mWechatReceiveHelper.replyBtnStatus = ReplyStatus.INTO_REPLY;

        mSpeechRemindTxt = (TextView) findViewById(R.id.wechat_speech_remind_txt);
        mVoiceSwitchTxt = (TextView) findViewById(R.id.wechat_voice_switch_txt);

        mReplyBtn = (Button) findViewById(R.id.wechat_reply_btn);
        mCancelBtn = (Button) findViewById(R.id.wechat_cancel_btn);

        mVoiceSwitchBtn = (ImageButton) findViewById(R.id.wechat_voice_btn);

        mVoiceTitleRela = (RelativeLayout) findViewById(R.id.wechat_voice_title_rela);
        mReplyTitleRela = (RelativeLayout) findViewById(R.id.wechat_reply_title_rela);
        mReplyTitleRela.setVisibility(View.GONE);

        mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
        mWeChatMsgListView = (RecyclerView) findViewById(R.id.wechat_msg_list_view);

        mTTS = SystemTTS.getInstance(getApplicationContext());

        NluResultManager.getInstance().setWechatCallback(mWechatReceiveHelper.getWechatCallback());
        EventBus.getDefault().register(this);

        initHotwordManager();
        /*mHotwordManager = HotwordManager.getInstance(WeChatTranslucentActivity.this, new OnInitListener() {
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
        });*/
    }

    private void initHotwordManager(){
        mHotwordManager = HotwordManager.getInstance(WeChatTranslucentActivity.this, new OnInitListener() {
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
    }

    @Override
    protected void initData() {
        mWechatReceiveHelper.initOrSaveTTS(true);
    }

    /*private void initOrSaveTTS(boolean isInit) {
        if (isInit) {
            //1.读取数据库,0是关闭，1是开启
            int status = ShardPreUtils.getInstance(MusicContacts.DEFAULT_FILE)
                    .getIntValue(MSG_TTS_SAVE_STATUS_KEY);
            boolean isLastVoiceStatusOn = status > 0 ? true : false;
            //2.更新view状态
            updateVoiceStatus(isLastVoiceStatusOn);
        } else {
            int saveStatus = mWechatReceiveHelper.voiceSwitchState ? 1 : 0;
            ShardPreUtils.getInstance(MusicContacts.DEFAULT_FILE)
                    .putIntValue(MSG_TTS_SAVE_STATUS_KEY, saveStatus);
        }
    }*/

    protected void initListener() {
        mReplyBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        mVoiceSwitchBtn.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wechat_notices_main;
    }

    private void updateView() {
        if (!isShowDrawer) {
            Intent intent = getIntent();
            String notice = intent.getStringExtra(Constants.KEY_WECHAT_NOTICE);
            WeChatUtils mWeChatUtils = new WeChatUtils();
            NotificationBean mBean = mWeChatUtils.handleNotificationStrToBean(notice);

            updateWeChatView(mBean);

            if (mWechatReceiveHelper.mActualNotiList.size() == 0) {
                mWechatReceiveHelper.intoMainAct();
            }

            mAdapter = new WeChatNoticesAdapter(this, mWechatReceiveHelper.mNotiList, mTextWatcher);
            initRecyclerView(mWeChatMsgListView, new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false), mAdapter);

            mDrawer.animateOpen();
            mDrawer.setCallBack(this);

            mWechatReceiveHelper.speekMsg(mBean);
        }
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        switch (viewId) {
            case R.id.wechat_reply_btn:
                switch (mWechatReceiveHelper.replyBtnStatus) {
                    case SEND:
//                        //1.自动执行微信回复
                        autoReply();
//                        //2如果有下一条就停留在当前页面，如果没有下一条就小时
                        intoReplyorInCome(false);
//                        handleSendOrder();
                        break;
                    case NOT_MSG:
                        break;
                    case INTO_REPLY:
                        intoReplyorInCome(true);
                        WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER_CONTENT);
                        mWechatReceiveHelper.speekMsg(getString(R.string.wechat_reply_remind_str));
                        mWechatReceiveHelper.wechatStep = WechatConstants.WECHAT_GET_NAME_STEP;
                        break;
                }
                break;
            case R.id.wechat_cancel_btn:
//                wechatStep = WechatConstants.WECHAT_DEFAULT_STEP;
//                VoicePolicyManage.getInstance().speakStop();
//                NluResultManager.getInstance().setWechatDomain(WECHAT_END);
//                if (mActualNotiList.size() > 1) {
//                    mWeChatMsgListView.scrollToPosition(adapterCenterPionstion + 1);
//                    intoReplyorInCome(false);
//                    updateWeChatView(null);
//                } else {
//                    intoMainAct();
//                }
                mWechatReceiveHelper.sendHandlerMsg(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
                //mHandler.sendEmptyMessage(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
                break;
            case R.id.wechat_voice_btn:
                if (!mWechatReceiveHelper.voiceSwitchState) {
                    mWechatReceiveHelper.voiceSwitchState = true;
                    mVoiceSwitchBtn.setSelected(true);
                    mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_on_str);
                } else {
                    mWechatReceiveHelper.voiceSwitchState = false;
                    mVoiceSwitchBtn.setSelected(false);
                    mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_off_str);
                }
                break;
        }
    }

    public void autoReply() {
        if (!AccessibilityUtil.isAccessibilitySettingsOn(WeChatTranslucentActivity.this)) {
            Log.d(TAG, "Accessibility service is disable");
            this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }

        if (mWeChatMsgListView == null) {
            Log.d(TAG, "mWeChatMsgListView is null : " + mWechatReceiveHelper.wechatStep);
            return;
        }

        String input = "";
        String sender = "";
        if (mWeChatMsgListView.getFocusedChild() == null) {
            Log.d(TAG, "mWeChatMsgListView getFocusedChild is null : " + mWechatReceiveHelper.wechatStep);
            NotificationBean notificationBean = mWechatReceiveHelper.mNotiList.get(mWechatReceiveHelper.adapterCenterPionstion);
            if (notificationBean == null || TextUtils.isEmpty(notificationBean.getInput()) || TextUtils.isEmpty(notificationBean.getSender())) {
                Log.d(TAG, "mWeChatMsgListView getFocusedChild is null : " + mWechatReceiveHelper.wechatStep);
            } else {
                input = notificationBean.getInput();
                sender = notificationBean.getSender();
            }

        } else {
            mReplyMsgTxt = (EditText) mWeChatMsgListView.getFocusedChild().findViewById(R.id.reply_msg_txt);
            mInComeContactTxt =
                    (TextView) mWeChatMsgListView.getFocusedChild().findViewById(R.id.income_msg_contact_txt);

            input = mReplyMsgTxt.getText().toString();
            sender = mInComeContactTxt.getText().toString();
        }

        if (TextUtils.isEmpty(input) || TextUtils.isEmpty(sender)) {
            Log.d(TAG, "input or sender is null : " + mWechatReceiveHelper.wechatStep);
            return;
        }

        /*mReplyMsgTxt = (EditText) mWeChatMsgListView.getFocusedChild().findViewById(R.id.reply_msg_txt);
        mInComeContactTxt =
                (TextView) mWeChatMsgListView.getFocusedChild().findViewById(R.id.income_msg_contact_txt);

        if (mReplyMsgTxt.getText() == null || mReplyMsgTxt.getText().toString().length() == 0) {
            //IntegrationCore.getIntergrationCore(this).onFail("回复信息为空", ActionCode.NOT_INCLUDE_MESSAGE);
            return;
        }

        if (mInComeContactTxt.getText() == null || mInComeContactTxt.getText().length() == 0) {
            //IntegrationCore.getIntergrationCore(this).onFail("回复联系人为空", ActionCode.NOT_INCLUDE_CONTACTS);
            return;
        }*/

        ActionBean actionBean = new ActionBean();
        actionBean.setActionCode(ActionCode.WECHAT_SEND_MSG_TO_PERSON);
        actionBean.setAppName("微信");
        actionBean.setAddressee(sender);
        actionBean.setAction(input);

        if (mWechatReceiveHelper.mActualNotiList.size() > 1) {
            mWeChatMsgListView.scrollToPosition(mWechatReceiveHelper.adapterCenterPionstion + 1);
            isScroll = true;
        } else {
            isScroll = false;
            IntegrationCore.getIntergrationCore(this).cencalWeChatNotices();
        }

        PateoVirtualSDK.doAction(WeChatTranslucentActivity.this,
                actionBean, IntegrationCore.getIntergrationCore(this));
    }

    @Override
    public void setWechatAnswerMessageUI(int adapterCenterPionstion, int resourceId) {
        mAdapter.notifyItemChanged(adapterCenterPionstion);
        if (WechatConstants.INVALID_RESOURCE_ID != resourceId) {
            mReplyBtn.setBackgroundResource(resourceId);
        }
    }

    public void intoReplyorInCome(boolean isReply) {
        if (mVoiceTitleRela == null || mReplyTitleRela == null || mWechatReceiveHelper.mNotiList == null
                || mReplyBtn == null || mAdapter == null) {
            Log.d(TAG, "intoReplyorInCome view is null");
            return;
        }

        mVoiceTitleRela.setVisibility(isReply ? View.GONE : View.VISIBLE);
        mReplyTitleRela.setVisibility(isReply ? View.VISIBLE : View.GONE);
        mWechatReceiveHelper.mNotiList.get(mWechatReceiveHelper.adapterCenterPionstion).setInput(null);
        mWechatReceiveHelper.mNotiList.get(mWechatReceiveHelper.adapterCenterPionstion).setRelpy(isReply);
        mAdapter.notifyItemChanged(mWechatReceiveHelper.adapterCenterPionstion);
        mReplyBtn.setBackgroundResource(isReply ? R.mipmap.wechat_not_reply_btn : R.drawable.wechat_reply_select);
        mSpeechRemindTxt.setText(isReply ? R.string.wechat_reply_input_msg_remind_str :
                R.string.wechat_income_remind_str);

        mWechatReceiveHelper.replyBtnStatus = isReply ? ReplyStatus.NOT_MSG : ReplyStatus.INTO_REPLY;
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String originText = editable.toString();
            Log.d(TAG, "originText : " + originText);
            if (editable != null && editable.toString().length() > 0) {
                mReplyBtn.setBackgroundResource(R.drawable.wechat_send_select);
                mWechatReceiveHelper.replyBtnStatus = ReplyStatus.SEND;
                mWechatReceiveHelper.mUserVoiceInput = originText;
                mWechatReceiveHelper.wechatStep = WechatConstants.WECHAT_INPUT_MSG_STEP;
                mWechatReceiveHelper.mNotiList.get(mWechatReceiveHelper.adapterCenterPionstion).setInput(originText);
            } else {
                if (mReplyTitleRela.getVisibility() == View.VISIBLE) {
                    mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);
                    mWechatReceiveHelper.replyBtnStatus = ReplyStatus.NOT_MSG;
                }
            }
        }
    };

    private void updateWeChatView(NotificationBean notice) {
        //1.更新通知列表
        if (notice != null) {
            mWechatReceiveHelper.mNotiList.add(notice);
            mWechatReceiveHelper.mActualNotiList.add(notice);
        }
        //2.通知超过一条时，显示下一条
        updateSpeechRemindView();
    }

    private void updateSpeechRemindView() {
        if (mWechatReceiveHelper.mActualNotiList.size() > 1) {
            mSpeechRemindTxt.setText(R.string.wechat_incomes_remind_str);
        } else {
            mSpeechRemindTxt.setText(R.string.wechat_income_remind_str);
        }
    }

    @Override
    public void openCallBack() {
        Log.d(TAG, "openCallBack");
        isShowDrawer = true;
    }

    @Override
    public void closeCallBack() {
        Log.d(TAG, "closeCallBack");
        isShowDrawer = false;

        //上滑消失后的回调，清空所有通知记录
        mWechatReceiveHelper.mNotiList.clear();
        IntegrationCore.getIntergrationCore(this).cleanNoticesList();
        this.finish();
    }

    private void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager,
                                  final WeChatNoticesAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(1);

        recyclerView.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        recyclerView.setHasFixedSize(true);
        // sample adapter with random data
        recyclerView.setAdapter(adapter);
        // enable center post scrolling
        recyclerView.addOnScrollListener(new CenterScrollListener());
//        // enable center post touching on item and item click listener
//        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
//            @Override
//            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
//                final int position = recyclerView.getChildLayoutPosition(v);
//                final String msg = String.format(Locale.US, "Item %1$d was clicked", position);
//                Toast.makeText(WeChatTranslucentActivity.this, msg, Toast.LENGTH_SHORT).show();
//            }
//        }, recyclerView, layoutManager);

        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
                    Log.d(TAG, "onCenterItemChanged adapterPosition : " + adapterPosition);
                    mWechatReceiveHelper.adapterCenterPionstion = adapterPosition;
                    if (mWechatReceiveHelper.mNotiList.size() > 1 && adapterPosition == (mWechatReceiveHelper.mNotiList.size() - 1)) {
                        mAdapter.setLastMsg(true);
                        mAdapter.notifyItemChanged(mWechatReceiveHelper.mNotiList.size() - 1);
                    }

                    if (adapterPosition != 0) {
                        mWechatReceiveHelper.mActualNotiList.remove(0);
                        IntegrationCore.getIntergrationCore(WeChatTranslucentActivity.this).cencalWeChatNotices();
                        updateSpeechRemindView();
                        mWechatReceiveHelper.speekMsg(mWechatReceiveHelper.mActualNotiList.get(0));
                    }
                }
            }
        });
    }

    /*private void speekMsg(final NotificationBean msgBean) {
        if (msgBean == null || !mWechatReceiveHelper.voiceSwitchState) {
            return;
        }
        String speekMsg = msgBean.getSender() + "发来消息" + msgBean.getMsg();
        VoicePolicyManage.getInstance().speak(speekMsg);
    }

    private void speekMsg(String msg) {
        if (msg == null || msg.length() == 0) {
            return;
        }
        VoicePolicyManage.getInstance().speak(msg);
    }*/

    /*private void intoMainAct() {
        WechatManager.getInstance().setWechatDomain(WECHAT_END);
//        VoicePolicyManage.getInstance().record(true);
        IntegrationCore.getIntergrationCore(WeChatTranslucentActivity.this).cleanNoticesList();
        WeChatTranslucentActivity.this.finish();
        Intent mIntent = new Intent(WeChatTranslucentActivity.this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        WeChatTranslucentActivity.this.startActivity(mIntent);
    }*/

    private void updateVoiceStatus(boolean isOn) {
        if (isOn) {
            mWechatReceiveHelper.voiceSwitchState = true;
            mVoiceSwitchBtn.setSelected(true);
            mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_on_str);
        } else {
            mWechatReceiveHelper.voiceSwitchState = false;
            mVoiceSwitchBtn.setSelected(false);
            mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_off_str);
        }
    }


    @Override
    public void updateVoiceStatus(boolean isSelected, int textResourceId) {
        mWechatReceiveHelper.voiceSwitchState = isSelected;
        mVoiceSwitchBtn.setSelected(isSelected);
        mVoiceSwitchTxt.setText(textResourceId);
    }

    /*private WechatCallback mWechatCallback = new WechatCallback() {
        @Override
        public void onPrepareSendWechat(String name) {
            if (PhoneStateManager.getInstance(WeChatTranslucentActivity.this).getPhoneState() == PhoneState.OUT_CAR_MODE) {
                WechatManager.getInstance().setWechatDomain(WECHAT_END);
                return;
            }

            String pinyinName = ChineseToPY.getAllPinYinFirstLetter(name);
            WechatManager.getInstance().setWechatDomain(WECHAT_SELECT);
            //VoicePolicyManage.getInstance().record(true);
            mWechatReceiveHelper.handleVirtual(ActionCode.WECHAT_SEARCH_PERSON, pinyinName, null);
        }

        @Override
        public void onWechatPersonSelect(final String selectedId) {
//            Toast.makeText(WeChatTranslucentActivity.this, "第 " + selectedId + " 个", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WechatManager.getInstance().setWechatDomain(WECHAT_END);
                    //VoicePolicyManage.getInstance().record(true);
                    mWechatReceiveHelper.handleVirtual(ActionCode.WECHAT_SELECT_INPUT_SEND, selectedId, mWechatReceiveHelper.mUserVoiceInput);
                }
            }, 1000);
        }

        @Override
        public void onWechatSendMessage(String message) {
            Toast.makeText(WeChatTranslucentActivity.this, message, Toast.LENGTH_SHORT).show();
            WechatManager.getInstance().setWechatDomain(WECHAT_SEND);
            //VoicePolicyManage.getInstance().record(true);
            mWechatReceiveHelper.handleVirtual(ActionCode.WECHAT_INPUT_SEND_MSG, null, message);
        }

        @Override
        public void onSendWechat(String type) {
            Toast.makeText(WeChatTranslucentActivity.this, "发送", Toast.LENGTH_SHORT).show();
            WechatManager.getInstance().setWechatDomain(WECHAT_END);
            mWechatReceiveHelper.handleVirtual(ActionCode.WECHAT_CONFIRM_SEND, null, null);
        }
    };*/

    /*private void handleVirtual(int actionCode, String name, String action) {
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
        AccessibilityUtil.initAccessibility(this);
        PateoVirtualSDK.doAction(WeChatTranslucentActivity.this,
                actionBean, IntegrationCore.getIntergrationCore(WeChatTranslucentActivity.this));
    }*/

    /*private void initUIControl() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        UIControlElementItem replyItem = new UIControlElementItem();
        replyItem.addWord(getString(R.string.reply_wechat_ui_control));
        replyItem.setIdentify(mFragmentHashCode + "-" + REPLY_WECHAT_MSG_UI_CONTROL);
        mUIControlElements.add(replyItem);

        UIControlElementItem nextMsgItem = new UIControlElementItem();
        nextMsgItem.addWord(getString(R.string.next_wechat_msg_ui_control));
        nextMsgItem.setIdentify(mFragmentHashCode + "-" + NEXT_WECHAT_MSG_UI_CONTROL);
        mUIControlElements.add(nextMsgItem);

        UIControlElementItem sendMsgItem = new UIControlElementItem();
        sendMsgItem.addWord(getString(R.string.reply_wechat_send_ui_control));
        sendMsgItem.setIdentify(mFragmentHashCode + "-" + SEND_WECHAT_MSG_UI_CONTROL);
        mUIControlElements.add(sendMsgItem);

        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();
    }*/

    @Override
    public void onSelectOtherOC(String action) {
        Log.e(TAG,"onSelectOtherOC : " + action);
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
        mWechatReceiveHelper.onSelectOtherOC(action);
        /*switch (action) {
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
        }*/
    }

    private void handleSendOrder() {
        if (mWechatReceiveHelper.mActualNotiList.size() > 1) {
            mWeChatMsgListView.scrollToPosition(mWechatReceiveHelper.adapterCenterPionstion + 1);
            isScroll = true;
        } else {
            isScroll = false;
            IntegrationCore.getIntergrationCore(this).cencalWeChatNotices();
        }

        intoReplyorInCome(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WechatManager.getInstance().setWechatDomain(WECHAT_SELECT);
                final String Str = mWechatReceiveHelper.mActualNotiList.get(0).getSender();
                mWechatReceiveHelper.handleVirtual(ActionCode.WECHAT_SEARCH_PERSON, Str, "");
            }
        }, 1500);
    }

    private void handleReply() {
        if (mWechatReceiveHelper.replyBtnStatus == ReplyStatus.INTO_REPLY) {
            intoReplyorInCome(true);
        }
    }

    private void handleCancel() {
//        mCancelBtn.performClick();
        closeCallBack();
    }

    private void handleNextMsg() {
        mCancelBtn.performClick();
    }

    private void handleOpenRead() {
        if (!mWechatReceiveHelper.voiceSwitchState) {
            mWechatReceiveHelper.voiceSwitchState = true;
            mVoiceSwitchBtn.setSelected(true);
            mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_on_str);
        }
    }

    private void handleCloseRead() {
        if (mWechatReceiveHelper.voiceSwitchState) {
            mWechatReceiveHelper.voiceSwitchState = false;
            mVoiceSwitchBtn.setSelected(false);
            mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_off_str);
        }
    }

    /*private WechatAnswerCallback mWechatAnswerCallback = new WechatAnswerCallback() {
        @Override
        public void onWechatAnswerMessage(String message) {
            //message为响应回复后的说话内容
            Log.d(TAG, "user voice input msg : " + message);
            WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER);
            if (message.equals(getString(R.string.reinput_wechat_cancel_ui_control))) {
                mWechatReceiveHelper.mUserVoiceInput = "";
                mWechatReceiveHelper.mNotiList.get(mWechatReceiveHelper.adapterCenterPionstion).setInput("");
                mAdapter.notifyItemChanged(mWechatReceiveHelper.adapterCenterPionstion);
                mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);
                mWechatReceiveHelper.replyBtnStatus = ReplyStatus.NOT_MSG;
            } else {
                mWechatReceiveHelper.mUserVoiceInput = message;
                mWechatReceiveHelper.mNotiList.get(mWechatReceiveHelper.adapterCenterPionstion).setInput(message);
                mAdapter.notifyItemChanged(mWechatReceiveHelper.adapterCenterPionstion);
                mWechatReceiveHelper.replyBtnStatus = ReplyStatus.SEND;
                mWechatReceiveHelper.wechatStep = WechatConstants.WECHAT_INPUT_MSG_STEP;
                mWechatReceiveHelper.speekMsg(getString(R.string.wechat_send_speech_remind_str));
            }
        }

        @Override
        public void onWechatAnswerSend(String type) {
            Log.d(TAG, "user voice input msg type : " + type);
            if (WechatManager.WECHAT_ANSWER_START.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                mWechatReceiveHelper.sendHandlerMsg(WechatConstants.SEND_WECHAT_MSG_FLAG);
                //mHandler.sendEmptyMessage(WechatConstants.SEND_WECHAT_MSG_FLAG);
            } else if (WechatManager.WECHAT_ANSWER_CANCEL.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                mWechatReceiveHelper.sendHandlerMsg(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
                //mHandler.sendEmptyMessage(WechatConstants.CANCEL_WECHAT_MSG_FLAG);
            } else if (WechatManager.WECHAT_ANSWER_AGAIN.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER_CONTENT);
                mWechatReceiveHelper.sendHandlerMsg(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
                //mHandler.sendEmptyMessage(WechatConstants.REINPUT_WECHAT_MSG_FLAG);
            }
        }
    };*/

    private void addWakeupElements() {
        if (mHotwordManager == null) {
            return;
        }

        Log.d(TAG, " addWakeupElements ");

        /*ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> elementItems =
                new ArrayList<>();
//        com.qinggan.qinglink.bean.UIControlElementItem replyWechatItem =
//                new com.qinggan.qinglink.bean.UIControlElementItem();
//        replyWechatItem.setWord(getString(R.string.reply_wechat_ui_control));
//        replyWechatItem.setIdentify(REPLY_WECHAT_MSG_UI_CONTROL);
//        elementItems.add(replyWechatItem);

        int[] reply_words = {R.string.reply_wechat_ui_control,
                R.string.replys_wechat_ui_control};
        for (int i = 0 ; i < reply_words.length; i ++) {
            com.qinggan.qinglink.bean.UIControlElementItem replyWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            replyWechatItem.setWord(getString(reply_words[i]));
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
            nextWechatItem.setWord(getString(next_words[i]));
            nextWechatItem.setIdentify(NEXT_WECHAT_MSG_UI_CONTROL);
            elementItems.add(nextWechatItem);
        }

        com.qinggan.qinglink.bean.UIControlElementItem sendWechatItem =
                new com.qinggan.qinglink.bean.UIControlElementItem();
        sendWechatItem.setWord(getString(R.string.reply_wechat_send_ui_control));
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
        for (int i = 0 ; i < cancel_words.length; i ++) {
            com.qinggan.qinglink.bean.UIControlElementItem cancelWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            cancelWechatItem.setWord(getString(cancel_words[i]));
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
        for (int i = 0 ; i < reinput_words.length; i ++) {
            com.qinggan.qinglink.bean.UIControlElementItem reinputWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            reinputWechatItem.setWord(getString(reinput_words[i]));
            reinputWechatItem.setIdentify(REINPUT_WECHAT_MSG_UI_CONTROL);
            elementItems.add(reinputWechatItem);
        }


        int[] open_tts_words = {R.string.open_msg_tts_wechat_ui_control,
                R.string.open_msg_tts_wechat_ui_control2,
                R.string.open_msg_tts_wechat_ui_control3};
        for (int i = 0 ; i < open_tts_words.length; i ++) {
            com.qinggan.qinglink.bean.UIControlElementItem openTTSItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            openTTSItem.setWord(getString(open_tts_words[i]));
            openTTSItem.setIdentify(OPEN_TTS_WECHAT_MSG_UI_CONTROL);
            elementItems.add(openTTSItem);
        }

        int[] close_tts_words = {R.string.close_msg_tts_wechat_ui_control,
                R.string.close_msg_tts_wechat_ui_control2,
                R.string.close_msg_tts_wechat_ui_control3};
        for (int i = 0 ; i < close_tts_words.length; i ++) {
            com.qinggan.qinglink.bean.UIControlElementItem closeTTSItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            closeTTSItem.setWord(getString(close_tts_words[i]));
            closeTTSItem.setIdentify(CLOSE_TTS_WECHAT_MSG_UI_CONTROL);
            elementItems.add(closeTTSItem);
        }*/

        registHotword(mWechatReceiveHelper.createWakeupElements());
    }

    private void registHotword(ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> elementItems){
        mHotwordManager.setElementUCWords(VOICE_REPLY_WECHAT, elementItems);
        mHotwordManager.registerListener(VOICE_REPLY_WECHAT, new HotwordListener() {
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

    /*Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg.what : " + msg.what);
            switch (msg.what) {
                case WechatConstants.REPLY_WECHAT_MSG_FLAG:
                    intoReplyorInCome(true);
                    WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER_CONTENT);
                    speekMsg(getString(R.string.wechat_reply_remind_str));
                    wechatStep = WechatConstants.WECHAT_GET_NAME_STEP;
                    break;
                case WechatConstants.NEXT_WECHAT_MSG_FLAG:
                    if (mActualNotiList.size() > 1) {
                        mWeChatMsgListView.scrollToPosition(adapterCenterPionstion + 1);
                        intoReplyorInCome(false);
                        updateWeChatView(null);
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
                    mAdapter.notifyItemChanged(adapterCenterPionstion);
                    mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);
                    replyBtnStatus = ReplyStatus.NOT_MSG;
                    VoicePolicyManage.getInstance().record(true);
                    WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_ANSWER_CONTENT);
                    break;
                case WechatConstants.OPEN_TTS_WECHAT_MSG_FLAG:
                    if (!voiceSwitchState) {
                        voiceSwitchState = true;
                        mVoiceSwitchBtn.setSelected(true);
                        mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_on_str);
                    }
                    break;
                case WechatConstants.CLOSE_TTS_WECHAT_MSG_FLAG:
                    if (voiceSwitchState) {
                        voiceSwitchState = false;
                        mVoiceSwitchBtn.setSelected(false);
                        mVoiceSwitchTxt.setText(R.string.wechat_voice_switch_off_str);
                    }
                    break;
            }
        }
    };*/

    private VoicePolicyManage.TtsStateChangeListener mTtsStateChangeListener = new VoicePolicyManage.TtsStateChangeListener() {
        @Override
        public void onStart() {
        }

        @Override
        public void onDone() {
            Log.d(TAG, "onDone " + mWechatReceiveHelper.wechatStep);
            switch (mWechatReceiveHelper.wechatStep) {
                case 1:
                    VoicePolicyManage.getInstance().record(true);
                    break;
            }
        }

        @Override
        public void onError() {
            Log.d(TAG, "onError " + mWechatReceiveHelper.wechatStep);
            switch (mWechatReceiveHelper.wechatStep) {
                case 1:
                    VoicePolicyManage.getInstance().record(true);
                    break;
            }
        }
    };

    public void updateReceiveUI(int uiStatus) {
        if (WechatConstants.WECHAT_UI_NEXT_WECHAT == uiStatus) {
            mWeChatMsgListView.scrollToPosition(mWechatReceiveHelper.adapterCenterPionstion + 1);
            intoReplyorInCome(false);
            updateWeChatView(null);
        } else if(WechatConstants.WECHAT_UI_REINPUT == uiStatus){
            mAdapter.notifyItemChanged(mWechatReceiveHelper.adapterCenterPionstion);
            mReplyBtn.setBackgroundResource(R.mipmap.wechat_not_reply_btn);
        }
    }

    @Override
    public void finishUI() {
        System.out.println("----alvin--------finishUI------>>");
        Log.d(TAG,"finishUI");
        mWechatReceiveHelper.mNotiList.clear();
        mWechatReceiveHelper.mActualNotiList.clear();
        WeChatTranslucentActivity.this.finish();
    }
}
