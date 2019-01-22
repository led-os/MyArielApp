package com.qinggan.app.arielapp.minor.phone.service;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.phone.CallLogManager;
import com.qinggan.app.arielapp.minor.phone.Constants;
import com.qinggan.app.arielapp.minor.phone.bean.ContactsInfo;
import com.qinggan.app.arielapp.minor.phone.listener.ArielPhoneStateCallback;
import com.qinggan.app.arielapp.minor.phone.ui.IncallUIDialogNew;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.utils.AllWakeupEvent;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pateo on 18-11-8.
 */

public class ArielPhoneService extends Service{

    private static final String TAG = "ArielPhoneService";

    private static final int MSG_CALL_OFFHOOK = 1;
    private static final int MSG_CALL_RINGING = 2;
    private static final int MSG_CALL_STATE_IDLE = 0;
    private AudioManager audioManager;
    private ArielPhoneStateListener phoneStateListener;
    private TelephonyManager mTM;
    private Context mContext;
    private HotwordManager mHotwordManager;

    private IncallUIDialogNew incallUIDialog;

    private String callType = "";
    private static final String CALL_TYPE_CALL_IN = "call_in";
    private static final String CALL_TYPE_CALL_OUT = "call_out";

    private static final String MODULE_NAME = "PHONE_SERVICE";
    private static final String CALL_VOICE_HANGUP = "CALL_VOICE_HANGUP";
    private static final String CALL_VOICE_ACCEPT = "CALL_VOICE_ACCEPT";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        // 1.获取电话管理者对象
        CallUtils.logd(TAG, "onCreate");
        mContext = this;
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new ArielPhoneStateListener(mContext);
        mTM.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        super.onCreate();

        EventBus.getDefault().register(this);
        mHotwordManager = HotwordManager.getInstance(this, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                if (b && StageController.getStageController().getIsDuringCall()) {
                    addPhoneHotword();
                } else {
                    if (mHotwordManager != null) {
                        mHotwordManager.clearElementUCWords(MODULE_NAME);
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String contactsName = intent.getStringExtra(Constants.CONTACTS_NAME);
        String phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER);

        if (phoneNumber != null && !phoneNumber.equals("")) {
            callOut(contactsName, phoneNumber);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public void onDestroy() {
        // 取消电话状态的监听  
        CallUtils.logd(TAG, "onDestroy");
        if (mTM != null && phoneStateListener != null) {
            mTM.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    class ArielPhoneStateListener extends PhoneStateListener {

        private static final String TAG = "ArielPhoneStateListener";

        private Context mContext;
        private List<ArielPhoneStateCallback> phoneStateCallbackList = new ArrayList<>();

        public ArielPhoneStateListener(Context context) {
            mContext = context;
            audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }

        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    CallUtils.logd(TAG, "CALL_STATE_IDLE");
                    if (StageController.getStageController().getIsDuringCall()) {
                        removePhoneHotword();
                    }
                    StageController.getStageController().setIsDuringCall(false);
                    Message msgIdle = Message.obtain(mHandler);
                    msgIdle.what = MSG_CALL_STATE_IDLE;
                    msgIdle.obj = true;
                    mHandler.sendMessageDelayed(msgIdle, 100);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    CallUtils.logd(TAG, "CALL_STATE_OFFHOOK");
                    //don't add again, if call already offhook
                    if (!StageController.getStageController().getIsDuringCall()) {
                        addPhoneHotword();
                    }
                    StageController.getStageController().setIsDuringCall(true);
                    Message msgOffhook = Message.obtain(mHandler);
                    msgOffhook.what = MSG_CALL_OFFHOOK;
                    msgOffhook.obj = true;
                    mHandler.sendMessageDelayed(msgOffhook, 100);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    CallUtils.logd(TAG, "CALL_STATE_RINGING" + "  incomingNumber:" + incomingNumber);
                    if (!StageController.getStageController().getIsDuringCall()) {
                        addPhoneHotword();
                    }
                    StageController.getStageController().setIsDuringCall(true);
                    //setSpeekModle(true);
                /*if (checkPermission()) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }*/
                    CallUtils.logd(TAG, "phone BRAND:" + CallUtils.getPhoneProperty("BRAND"));

                    Message msgRingring = Message.obtain(mHandler);
                    msgRingring.what = MSG_CALL_RINGING;
                    msgRingring.obj = incomingNumber;
                    mHandler.sendMessageDelayed(msgRingring, 100);
                    break;
                default:
                    break;

            }
            //syncCallState(state);
            super.onCallStateChanged(state, incomingNumber);
        }
    }


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            CallUtils.logd(TAG, "mHandler handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_CALL_STATE_IDLE:
                    dismissIncallDialog();
                    if (checkPermission()) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                    CallLogManager.getInstance(mContext).syncCallLog();
                    break;
                case MSG_CALL_OFFHOOK:
                    callOffHook();
                    break;
                case MSG_CALL_RINGING:
                    String incomingNumber = (String) msg.obj;
                    String contactsName = CallUtils.getContactNameByPhoneNumber(mContext, incomingNumber);
                    callRinging(contactsName, incomingNumber);
                    break;
                default:
                    return false;
            }
            return false;
        }
    });

    private void callOffHook() {
        if (incallUIDialog != null) {
            if(CALL_TYPE_CALL_IN.equals(callType)) {
                incallUIDialog.setCallState(CallLog.Calls.INCOMING_TYPE, TelephonyManager.CALL_STATE_OFFHOOK);
            }else if(CALL_TYPE_CALL_OUT.equals(callType)){
                incallUIDialog.setCallState(CallLog.Calls.OUTGOING_TYPE, TelephonyManager.CALL_STATE_OFFHOOK);
            }
        }
    }

    private void callRinging(String name, String incomingNumber) {
        if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            CallUtils.logi(TAG,"callRinging PhoneState.OUT_CAR_MODE return");
            return;
        }
        callType = CALL_TYPE_CALL_IN;
        createIncallUIDialog(name, incomingNumber);
        incallUIDialog.setCallState(CallLog.Calls.INCOMING_TYPE, TelephonyManager.CALL_STATE_RINGING);
    }

    private void callOut(String name, String incomingNumber) {
        if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            CallUtils.logi(TAG,"callOut PhoneState.OUT_CAR_MODE return");
            return;
        }
        if (incallUIDialog != null) {
            CallUtils.logd(TAG, "incallUIDialog is no null, return");
            return;
        }
        callType = CALL_TYPE_CALL_OUT;
        createIncallUIDialog(name, incomingNumber);
        incallUIDialog.setCallState(CallLog.Calls.OUTGOING_TYPE, -1);
    }

    private void createIncallUIDialog(String name, String incomingNumber) {
        ContactsInfo contactsInfo = new ContactsInfo();
        contactsInfo.setDesplayName(name);
        contactsInfo.setPhoneNum(incomingNumber);
        incallUIDialog = new IncallUIDialogNew(mContext, contactsInfo, R.style.dialog_fullscreen);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            incallUIDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            incallUIDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        try {
            incallUIDialog.show();
        } catch(Exception e) {
            android.util.Log.e(TAG, "incallUIDialog show exception", e);
        }

        //test code
//        IncallUIView uiView = new IncallUIView(this,contactsInfo);
//        uiView.onStart();
//        uiView.show();
    }

    private void dismissIncallDialog() {
        if (incallUIDialog != null && incallUIDialog.isShowing()) {
            incallUIDialog.dismiss();
        }
        incallUIDialog = null;
    }

    private boolean checkPermission() {
        boolean hasPermission = false;
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            CallUtils.logd(TAG, "set ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS");
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onHandleEvent(AllWakeupEvent event){
        switch (event) {
            case CALL_ACCEPT:
                CallUtils.logd(TAG, "CALL_ACCEPT");
                CallUtils.answerCall(mContext);
                break;
            case CALL_REFUSE:
                CallUtils.logd(TAG, "CALL_REFUSE");
                CallUtils.rejectCall();
                break;
        }
    }

    private void addPhoneHotword() {
        if (mHotwordManager == null) {
            return;
        }
        CallUtils.logd(TAG, "addPhoneHotword");
        ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> mWakeUpElements = new ArrayList<>();

        com.qinggan.qinglink.bean.UIControlElementItem phoneAnswer = new com.qinggan.qinglink.bean.UIControlElementItem();
        phoneAnswer.setWord(getString(R.string.callin_voice_accept2));
        phoneAnswer.setIdentify(ConstantNavUc.PHONE_CALL_ANSWER);
        mWakeUpElements.add(phoneAnswer);

        com.qinggan.qinglink.bean.UIControlElementItem phoneAnswer1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        phoneAnswer1.setWord(getString(R.string.phone_call_answer1));
        phoneAnswer1.setIdentify(ConstantNavUc.PHONE_CALL_ANSWER1);
        mWakeUpElements.add(phoneAnswer1);

        com.qinggan.qinglink.bean.UIControlElementItem phoneAnswer2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        phoneAnswer2.setWord(getString(R.string.phone_call_answer));
        phoneAnswer2.setIdentify(ConstantNavUc.PHONE_CALL_ANSWER2);
        mWakeUpElements.add(phoneAnswer2);

        com.qinggan.qinglink.bean.UIControlElementItem phoneCancel = new com.qinggan.qinglink.bean.UIControlElementItem();
        phoneCancel.setWord(getString(R.string.callin_voice_hangup2));
        phoneCancel.setIdentify(ConstantNavUc.PHONE_CALL_CANCEL);
        mWakeUpElements.add(phoneCancel);

        com.qinggan.qinglink.bean.UIControlElementItem phoneCancel1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        phoneCancel1.setWord(getString(R.string.phone_call_cancel1));
        phoneCancel1.setIdentify(ConstantNavUc.PHONE_CALL_CANCEL1);
        mWakeUpElements.add(phoneCancel1);

        com.qinggan.qinglink.bean.UIControlElementItem phoneCancel2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        phoneCancel2.setWord(getString(R.string.callin_voice_hangup));
        phoneCancel2.setIdentify(ConstantNavUc.PHONE_CALL_CANCEL2);
        mWakeUpElements.add(phoneCancel2);

        mHotwordManager.setElementUCWords(MODULE_NAME, mWakeUpElements);
        mHotwordManager.registerListener(MODULE_NAME, new HotwordListener() {

            @Override
            public void onItemSelected(String s) {
                CallUtils.logd(TAG, "HotwordListener onItemSelected:" + s);
                switch (s){
                    case ConstantNavUc.PHONE_CALL_ANSWER:
                    case ConstantNavUc.PHONE_CALL_ANSWER1:
                    case ConstantNavUc.PHONE_CALL_ANSWER2:
                        CallUtils.answerCall(mContext);
                        break;
                    case ConstantNavUc.PHONE_CALL_CANCEL:
                    case ConstantNavUc.PHONE_CALL_CANCEL1:
                    case ConstantNavUc.PHONE_CALL_CANCEL2:
                        CallUtils.rejectCall();
                        break;
                }
            }
            @Override
            public void onPageSelected(int i) {}
            @Override
            public void onSwitchPage(int i) {}
        });
    }

    private void removePhoneHotword() {
        CallUtils.logd(TAG, "HotwordListener removeHotword");
        if (mHotwordManager != null) {
            mHotwordManager.clearElementUCWords(MODULE_NAME);
        }
    }
}