package com.qinggan.app.arielapp.minor.phone.ui;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.CallLog;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.phone.bean.ContactsInfo;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.minor.phone.view.MultiDirectionSlidingDrawer;
import com.qinggan.app.arielapp.minor.utils.RomInfo;
import com.qinggan.app.arielapp.minor.wechat.inter.SlidingDrawerCallBack;
import com.qinggan.app.arielapp.voiceview.DragFloatView;
import com.qinggan.app.arielapp.voiceview.VoiceFloatView;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.BluetoothHeadsetListener;
import com.qinggan.qinglink.api.md.BluetoothHeadsetManager;
import com.qinggan.qinglink.bean.RadarInfo;

import java.lang.reflect.Method;

/**
 * Created by pateo on 18-11-8.
 */

public class IncallUIDialogNew extends Dialog implements SlidingDrawerCallBack {

    private static final String TAG = "IncallUIDialog";
    private static final int MSG_CLOSE_INCALL = 1;
    private static final int MSG_UPDATE_AUDIO_ICON = 2;
    private ImageButton btnAcceptPhone;
    AnimationDrawable acceptPhoneAnonimation;
    private ImageButton btnHungupPhone;
    private ImageButton btnMute;
    private ImageButton btnMicrophone;
    private ImageButton btnSpeaker;
    private ImageButton btnKeybroad;
    private ImageButton btnKeybroadHidden;

    private ImageButton btn_0;
    private ImageButton btn_1;
    private ImageButton btn_2;
    private ImageButton btn_3;
    private ImageButton btn_4;
    private ImageButton btn_5;
    private ImageButton btn_6;
    private ImageButton btn_7;
    private ImageButton btn_8;
    private ImageButton btn_9;
    private ImageButton btn_s;
    private ImageButton btn_j;

    private LinearLayout voicePromptLayout;
    private RelativeLayout phoneSkbLayout;
    private RelativeLayout mainLayout;
    private RelativeLayout keybroadLayout;

    private TextView tvContactsName;
    private TextView tvPhoneStatus;
    private Chronometer mChronometer;
    private TextView tvTopContactsName;
    private Chronometer mChronometerTop;
    private TextView tvSpeaker;
    private TextView tvKeybroad;
    private TextView tvKeybroadInput;

    private BluetoothHeadsetManager mBluetoothHeadsetManager;

    private Context mContext;
    private MultiDirectionSlidingDrawer mDrawer;

    /** normal 0, phone speaker 1, car speaker 2 */
    private int mAudioStatus = 0;


    public IncallUIDialogNew(Context context, ContactsInfo contactsInfo, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.phone_incall_main);

        mContext = context;
        initView(contactsInfo);

        //Init MD and HU manger and listener
        mBluetoothHeadsetManager = BluetoothHeadsetManager.getInstance(mContext, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {
                CallUtils.logi(TAG, "mBluetoothHeadsetManager OnInitListener onConnectStatusChange:" + b);
            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                CallUtils.logi(TAG, "mBluetoothHeadsetManager OnConnectListener onConnect:" + b);
                if (b && mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendGetAudioStatus();
                    CallUtils.logi(TAG, "mBluetoothHeadsetManager sendGetAudioStatus:" + result);
                    btnSpeaker.setClickable(true);
                    btnSpeaker.setEnabled(true);
                    btnKeybroad.setClickable(true);
                    btnKeybroad.setEnabled(true);
                    tvSpeaker.setEnabled(true);
                    tvKeybroad.setEnabled(true);
                } else {
                    btnSpeaker.setClickable(false);
                    btnSpeaker.setEnabled(false);
                    btnKeybroad.setClickable(false);
                    btnKeybroad.setEnabled(false);
                    tvSpeaker.setEnabled(false);
                    tvKeybroad.setEnabled(false);
                }
            }
        });
        if (mBluetoothHeadsetManager != null) {
            mBluetoothHeadsetManager.registerListener(mBluetoothHeadsetListener);
        } else {
            CallUtils.logi(TAG, "mBluetoothHeadsetManager is null!");
        }
    }

    @Override
    public void show() {
        super.show();

        AudioPolicyManager.getInstance().requestAudioPolicy(new AudioPolicyManager.OnAudioPolicyListener() {
            @Override
            public boolean onPause() {
                return false;
            }

            @Override
            public boolean onResume() {
                return false;
            }

            @Override
            public boolean onStop() {
                return false;
            }
        }, AudioPolicyManager.AudioType.PHONE);
        DragFloatView.getInstance(getContext()).dismiss();
        VoiceFloatView.setInCallOrNot(true);

        UMAnalyse.startTime(UMDurationEvent.PHONE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        AudioPolicyManager.getInstance().abandonAudioPolicy(AudioPolicyManager.AudioType.PHONE);
        VoiceFloatView.setInCallOrNot(false);

        UMAnalyse.stopTime(UMDurationEvent.PHONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MultiDirectionSlidingDrawer mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
        mDrawer.animateOpen();
        mDrawer.setCallBack(this);
    }

    private void initView(ContactsInfo contactsInfo) {
        tvSpeaker = (TextView) this.findViewById(R.id.tv_speaker);

        btnHungupPhone = (ImageButton) this.findViewById(R.id.btn_hungup_phone);
        btnHungupPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectCall();
            }
        });

        btnAcceptPhone = (ImageButton) this.findViewById(R.id.btn_accept_phone);
        btnAcceptPhone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CallUtils.acceptCallGEO(mContext);
                } else {
                    CallUtils.acceptCall(mContext);
                }

                try{
                    RadarInfo mRadarInfo = ArielApplication.getCanBusManager().getRadarInfo();
                    if(mRadarInfo!=null){
                        if(mRadarInfo.getAccStatus() == RadarInfo.ACC_ON && mRadarInfo.getGearStatus() == RadarInfo.GEAR_REVERSE){
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                Intent intent = new Intent();
                                intent.setAction("com.qinggan.app.arielapp.radar_open");
                                ComponentName componentName = new ComponentName(ArielApplication.getApp(),"com.qinggan.app.arielapp.phonestate.ReversingRadarReceiver");
                                intent.setComponent(componentName);
                                ArielApplication.getApp().sendBroadcast(intent);
                            }else{
                                Intent intent = new Intent();
                                intent.setAction("com.qinggan.app.arielapp.radar_open");
                                ArielApplication.getApp().sendBroadcast(intent);
                            }
                            IncallUIDialogNew.this.dismiss();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnMute = (ImageButton) this.findViewById(R.id.btn_mute);
        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                    CallUtils.silenceRinger();
                } else {
                    CallUtils.setSilent(mContext);
                }
                //silent headUnit
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendMuteRing(true);
                    CallUtils.logi(TAG, "mBluetoothHeadsetManager sendMuteRing result:" + result);
                }
            }
        });

        btnMicrophone = (ImageButton) this.findViewById(R.id.btn_microphone);
        if (CallUtils.isMicrophoneMute(mContext)) {
            btnMicrophone.setImageResource(R.drawable.phone_button_lo);
        }
        btnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallUtils.switchMicrophoneMute(mContext);
                if (CallUtils.isMicrophoneMute(mContext)) {
                    btnMicrophone.setImageResource(R.drawable.phone_button_lo);
                } else {
                    btnMicrophone.setImageResource(R.drawable.phone_buttun_lo_icon_true);
                }
            }
        });

        btnSpeaker = (ImageButton) this.findViewById(R.id.btn_speaker);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioStatus == 2) {
                    boolean result = mBluetoothHeadsetManager.sendDisconnectAudio();
                    CallUtils.logi(TAG, "btnSpeaker sendDisconnectAudio:" + result);
                    mAudioStatus = 1;
                } else {
                    boolean result = mBluetoothHeadsetManager.sendConnectAudio();
                    CallUtils.logi(TAG, "btnSpeaker sendConnectAudio:" + result);
                    mAudioStatus = 2;
                }
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendGetAudioStatus();
                    CallUtils.logi(TAG, "btnSpeaker mBluetoothHeadsetManager sendGetAudioStatus:" + result);
                }
            }
        });

        btnKeybroad = (ImageButton) this.findViewById(R.id.btn_keybroad);
        btnKeybroad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.setVisibility(View.GONE);
                keybroadLayout.setVisibility(View.VISIBLE);
                btnKeybroadHidden.setVisibility(View.VISIBLE);
            }
        });

        btnKeybroadHidden = (ImageButton) this.findViewById(R.id.btn_keybroad_hidden);
        btnKeybroadHidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.setVisibility(View.VISIBLE);
                keybroadLayout.setVisibility(View.GONE);
                btnKeybroadHidden.setVisibility(View.GONE);
            }
        });

        voicePromptLayout = (LinearLayout) this.findViewById(R.id.voice_prompt_layout);
        phoneSkbLayout = (RelativeLayout) this.findViewById(R.id.phone_skb_layout);
        mainLayout = (RelativeLayout) this.findViewById(R.id.main_layout);
        keybroadLayout = (RelativeLayout) this.findViewById(R.id.keybroad_layout);

        CallUtils.logd("IncallUIDialog", "contactsName：" + contactsInfo.getDisplayName());
        CallUtils.logd("IncallUIDialog", "phoneNumber：" + contactsInfo.getPhoneNum());
        tvContactsName = (TextView) this.findViewById(R.id.tv_contacts_name);
        tvPhoneStatus = (TextView) this.findViewById(R.id.tv_phone_status);
        tvTopContactsName = (TextView) this.findViewById(R.id.tv_top_contacts_name);

        if (contactsInfo.getDisplayName() != null && !contactsInfo.getDisplayName().equals("")) {
            tvContactsName.setText(contactsInfo.getDisplayName());
            tvTopContactsName.setText(contactsInfo.getDisplayName());
        } else {
            tvContactsName.setText(contactsInfo.getPhoneNum());
            tvTopContactsName.setText(contactsInfo.getPhoneNum());
        }

        mChronometer = (Chronometer) this.findViewById(R.id.chronometer);
        mChronometerTop = (Chronometer) this.findViewById(R.id.chronometer_top);
        tvSpeaker = (TextView) this.findViewById(R.id.tv_speaker);

        mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);


        initKeybroadView();

    }

    private void initKeybroadView() {
        tvKeybroad = (TextView) this.findViewById(R.id.tv_keybroad);
        tvKeybroadInput = (TextView) this.findViewById(R.id.tv_keybroad_input);

        btn_0 = (ImageButton) this.findViewById(R.id.btn_0);
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("0");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("0");
                    CallUtils.logi(TAG, "sendDTMF 0:" + result);
                }
            }
        });
        btn_1 = (ImageButton) this.findViewById(R.id.btn_1);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("1");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("1");
                    CallUtils.logi(TAG, "sendDTMF 1:" + result);
                }
            }
        });
        btn_2 = (ImageButton) this.findViewById(R.id.btn_2);
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("2");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("2");
                    CallUtils.logi(TAG, "sendDTMF 2:" + result);
                }
            }
        });
        btn_3 = (ImageButton) this.findViewById(R.id.btn_3);
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("3");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("3");
                    CallUtils.logi(TAG, "sendDTMF 3:" + result);
                }
            }
        });
        btn_4 = (ImageButton) this.findViewById(R.id.btn_4);
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("4");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("4");
                    CallUtils.logi(TAG, "sendDTMF 4:" + result);
                }
            }
        });
        btn_5 = (ImageButton) this.findViewById(R.id.btn_5);
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("5");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("5");
                    CallUtils.logi(TAG, "sendDTMF 5:" + result);
                }
            }
        });
        btn_6 = (ImageButton) this.findViewById(R.id.btn_6);
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("6");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("6");
                    CallUtils.logi(TAG, "sendDTMF 6:" + result);
                }
            }
        });

        btn_7 = (ImageButton) this.findViewById(R.id.btn_7);
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("7");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("7");
                    CallUtils.logi(TAG, "sendDTMF 7:" + result);
                }
            }
        });
        btn_8 = (ImageButton) this.findViewById(R.id.btn_8);
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("8");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("8");
                    CallUtils.logi(TAG, "sendDTMF 8:" + result);
                }
            }
        });
        btn_9 = (ImageButton) this.findViewById(R.id.btn_9);
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("9");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("9");
                    CallUtils.logi(TAG, "sendDTMF 9:" + result);
                }
            }
        });

        btn_s = (ImageButton) this.findViewById(R.id.btn_s);
        btn_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("*");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("*");
                    CallUtils.logi(TAG, "sendDTMF *:" + result);
                }
            }
        });
        btn_j = (ImageButton) this.findViewById(R.id.btn_j);
        btn_j.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeybroadInput("#");
                if (mBluetoothHeadsetManager != null) {
                    boolean result = mBluetoothHeadsetManager.sendDTMF("#");
                    CallUtils.logi(TAG, "sendDTMF #:" + result);
                }
            }
        });

        keybroadLayout.setVisibility(View.GONE);
        btnKeybroadHidden.setVisibility(View.GONE);

    }

    private void setKeybroadInput(String text) {
        String keyinStr = tvKeybroadInput.getText().toString();
        if (keyinStr.length() > 15) {
            keyinStr = keyinStr.substring(keyinStr.length() - 15);
        }
        tvKeybroadInput.setText(keyinStr + text);
    }

    public void rejectCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
            mHandler.sendEmptyMessageDelayed(MSG_CLOSE_INCALL, 1000);
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "", e);
        } catch (Exception e) {
        }
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            CallUtils.logd(TAG, "mHandler handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_CLOSE_INCALL:
                    IncallUIDialogNew.this.dismiss();
                    break;
                case MSG_UPDATE_AUDIO_ICON:
                    if (msg.arg1 == 1) {
                        setAudioOfMDHU(true);
                    } else {
                        setAudioOfMDHU(false);
                    }
                    break;
                default:
                    return false;
            }
            return false;
        }
    });

    public void setCallState(int callType, int callState) {
        CallUtils.logd(TAG,"callType:" +callType +"   callState:"+callState);
        if (CallLog.Calls.INCOMING_TYPE == callType && callState == TelephonyManager.CALL_STATE_RINGING) {
            setCallInRinging();
        } else if (CallLog.Calls.INCOMING_TYPE == callType && callState == TelephonyManager.CALL_STATE_OFFHOOK) {
            setCallInOffHook();
        } else if (CallLog.Calls.OUTGOING_TYPE == callType && callState != TelephonyManager.CALL_STATE_OFFHOOK) {
            setCallOutStart();
        } else if (CallLog.Calls.OUTGOING_TYPE == callType && callState == TelephonyManager.CALL_STATE_OFFHOOK) {
            setCallOutOffHook();
        }
    }

    private void setCallInRinging() {
        CallUtils.logd(TAG,"setCallInRinging");
        btnAcceptPhone.setVisibility(View.VISIBLE);
        btnHungupPhone.setVisibility(View.VISIBLE);
        btnMute.setVisibility(View.VISIBLE);
        setMicrophoneByRom(View.GONE);
        voicePromptLayout.setVisibility(View.VISIBLE);
        phoneSkbLayout.setVisibility(View.GONE);
        mChronometer.setVisibility(View.GONE);
        tvPhoneStatus.setVisibility(View.VISIBLE);
        tvPhoneStatus.setText(R.string.phone_incoming_call);

        acceptPhoneAnonimation = (AnimationDrawable) btnAcceptPhone.getDrawable();
        acceptPhoneAnonimation.start();
    }

    private void setCallInOffHook() {
        CallUtils.logd(TAG,"setCallInOffHook");
        btnAcceptPhone.setVisibility(View.GONE);
        btnHungupPhone.setVisibility(View.VISIBLE);
        btnMute.setVisibility(View.GONE);
        setMicrophoneByRom(View.VISIBLE);
        voicePromptLayout.setVisibility(View.GONE);
        phoneSkbLayout.setVisibility(View.VISIBLE);

        mChronometer.setVisibility(View.VISIBLE);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mChronometerTop.setBase(SystemClock.elapsedRealtime());
        mChronometerTop.start();

        tvPhoneStatus.setVisibility(View.GONE);

        if (acceptPhoneAnonimation != null) {
            acceptPhoneAnonimation.stop();
        }
    }

    private void setCallOutStart() {
        CallUtils.logd(TAG,"setCallOutStart");
        btnAcceptPhone.setVisibility(View.GONE);
        btnHungupPhone.setVisibility(View.VISIBLE);
        btnMute.setVisibility(View.GONE);
        setMicrophoneByRom(View.GONE);
        voicePromptLayout.setVisibility(View.VISIBLE);
        phoneSkbLayout.setVisibility(View.GONE);
        mChronometer.setVisibility(View.GONE);
        tvPhoneStatus.setVisibility(View.VISIBLE);
        tvPhoneStatus.setText(R.string.phone_out_call);

    }

    private void setCallOutOffHook() {
        CallUtils.logd(TAG,"setCallOutOffHook");
        btnAcceptPhone.setVisibility(View.GONE);
        btnHungupPhone.setVisibility(View.VISIBLE);
        btnMute.setVisibility(View.GONE);
        setMicrophoneByRom(View.VISIBLE);
        voicePromptLayout.setVisibility(View.GONE);
        phoneSkbLayout.setVisibility(View.VISIBLE);

        mChronometer.setVisibility(View.VISIBLE);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mChronometerTop.setBase(SystemClock.elapsedRealtime());
        mChronometerTop.start();

        tvPhoneStatus.setVisibility(View.GONE);

    }

    private void setMicrophoneByRom(int visible) {
        int romName = RomInfo.getmRomInfo().getRomName();
        switch (romName) {
            case RomInfo.ROM_EMUI:
            case RomInfo.ROM_OPPO:
            case RomInfo.ROM_VIVO:
                btnMicrophone.setVisibility(View.GONE);
                break;
            default:
                btnMicrophone.setVisibility(visible);
        }
    }

    /*
    * connect/disconnect audio of HU, isOn
    * true:HU
    * false:MD
     */
    private void setAudioOfMDHU(boolean isOn){
        CallUtils.logd(TAG, "setAudioOfMDHU： " + isOn);
        if (isOn) {
            btnSpeaker.setImageResource(R.drawable.phone_button_phone);
            tvSpeaker.setText(R.string.incall_earphone);
            mAudioStatus = 2;
        } else {
            btnSpeaker.setImageResource(R.drawable.phone_button_car);
            tvSpeaker.setText(R.string.incall_car_speaker);
            mAudioStatus = 1;
        }
    }

    BluetoothHeadsetListener mBluetoothHeadsetListener = new BluetoothHeadsetListener() {
        @Override
        public void onAudioStatusResponse(boolean b) {
            CallUtils.logi(TAG, "mBluetoothHeadsetManager BluetoothHeadsetListener onAudioStatusResponse:" + b);
            Message msg = Message.obtain(mHandler);
            msg.what = MSG_UPDATE_AUDIO_ICON;
            msg.arg1 = b ? 1 : 0;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    public void openCallBack() {
        tvTopContactsName.setVisibility(View.GONE);
        mChronometerTop.setVisibility(View.GONE);
    }

    @Override
    public void closeCallBack() {
        tvTopContactsName.setVisibility(View.VISIBLE);
        mChronometerTop.setVisibility(View.VISIBLE);
    }
}
