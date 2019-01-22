package com.qinggan.app.arielapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.capability.push.PushManager;
import com.qinggan.app.arielapp.capability.upgrade.bugly.BuglyManager;
import com.qinggan.app.arielapp.capability.volume.ArielVolumeManager;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.integration.QQMusicCMD;
import com.qinggan.app.arielapp.minor.main.driving.view.IntelligenceFragment;
import com.qinggan.app.arielapp.minor.main.driving.view.OrdinaryFragment;
import com.qinggan.app.arielapp.minor.main.driving.view.SimpleFragment;
import com.qinggan.app.arielapp.minor.main.mui.MainFragment;
import com.qinggan.app.arielapp.minor.main.mui.TestActivity;
import com.qinggan.app.arielapp.minor.main.navigation.bean.BaiduUiControlEvent;
import com.qinggan.app.arielapp.minor.phone.service.ArielPhoneService;
import com.qinggan.app.arielapp.minor.phone.utils.ChineseToPY;
import com.qinggan.app.arielapp.minor.push.bean.PushMessageBodyBean;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.wechat.MessageEvent;
import com.qinggan.app.arielapp.minor.wechat.WeChatSendMsgActivity;
import com.qinggan.app.arielapp.minor.wechat.WeChatTransparentTmpActivity;
import com.qinggan.app.arielapp.minor.wechat.utils.WechatConstants;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.session.SessionFactory;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.arielapp.ui.bluekey.AppBlueKeyListener;
import com.qinggan.app.arielapp.ui.bluekey.BleKeyHelper;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.arielapp.utils.AllWakeupEvent;
import com.qinggan.app.arielapp.utils.AppManager;
import com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl;
import com.qinggan.app.arielapp.voiceview.VoiceFloatViewService;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.dcs.location.LocationImpl;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;
import com.qinggan.app.voiceapi.DataTypeConstant;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.nluresult.DcsResultListener;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.app.voiceapi.nluresult.launchapp.LaunchAppCallback;
import com.qinggan.app.voiceapi.nluresult.launchapp.VoiceDcsCallback;
import com.qinggan.app.voiceapi.nluresult.music.MusicCommandCallback;
import com.qinggan.app.voiceapi.nluresult.music.MusicSearchCallback;
import com.qinggan.app.voiceapi.nluresult.radio.LocalRadioSearchCallback;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatCallback;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatManager;
import com.qinggan.app.voiceapi.tts.TtsHelper;
import com.qinggan.bluekey.encrypt.KeyStoreHelper;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.mqtt.PushMessage;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;
import com.qinggan.qinglink.bean.UIControlElementItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private final static String TAG = "MainActivity";

    private static final int REQUEST_CODE = 123;
    private static String BACK_TO_MAIN = "backToMain";
    FragmentManager mFragmentManager;
    public AbstractBaseFragment mCurrentFragment;
    FrameLayout main_content_view;
    private View rootView;

    private Context mContext = ArielApplication.getApp();
    private IntegrationCore integrationCore = null;
    private String wechatNotification;
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<MainActivity.MyTouchListener>();
    private static VoiceFloatViewService mVoiceFloatViewService;

    private String mWechatName;
    private String mWechatMsg;

    private ServiceConnection mVoiceFloatViewServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            VoiceFloatViewService.LocalBinder binder = (VoiceFloatViewService.LocalBinder) iBinder;
            mVoiceFloatViewService = binder.getService();
            ArielApplication.getApp().setVoiceFloatViewService(mVoiceFloatViewService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ArielApplication.getApp().setVoiceFloatViewService(null);
        }
    };

    public static VoiceFloatViewService getVoiceFloatViewService() {
        return mVoiceFloatViewService;
    }

    @Override
    protected void initView() {
        KeyStoreHelper.getInstance(getApplicationContext()).initKeyStore();
        BleKeyHelper.updateBleKey();
        initMainViewItems();
//        initPermission();
        initOverlayPermission();
        //打开模拟点击辅助
        AccessibilityUtil.initAccessibility(this);
        getSwipeBackLayout().setEnableGesture(false);
        BuglyManager.getInstance().init();//版本更新初始化
        NluResultManager.getInstance().initStt(this);
        NluResultManager.getInstance().setMusicCommandCallback(mMusicCommandCallback);
        NluResultManager.getInstance().setMusicSearchCallback(mMusicSearchCallback);
        NluResultManager.getInstance().addDcsResultListener(mDcsResultListener);
        NluResultManager.getInstance().setLaunchAppCallback(mLaunchAppCallback);
        NluResultManager.getInstance().setVoiceDcsCallback(mVoiceDcsCallback);
        NluResultManager.getInstance().setLocalRadioSearchCallback(mLoacalRadioCallback);
        integrationCore = IntegrationCore.getIntergrationCore(mContext);
        EventBus.getDefault().register(this);
        Intent intent = new Intent(getApplicationContext(), VoiceFloatViewService.class);
        bindService(intent, mVoiceFloatViewServiceConnection, Context.BIND_AUTO_CREATE);

        Intent arielPhoneService = new Intent(getApplicationContext(), ArielPhoneService.class);
        startService(arielPhoneService);
        TtsHelper.getInstance().initTtsHelper(this);
        if (TestActivity.isFileLogEnabled()) {
            Log.d(TAG, "isFileLogEnabled");
            TestActivity.startLog();
        } else {
            Log.d(TAG, "not isFileLogEnabled");
        }
        Log.e(TAG, "bind ble service");
        ArielApplication.mAppBlueKeyListener = new AppBlueKeyListener(MainActivity.this);
        BleKeyManager.getInstance(mContext).init(ArielApplication.mAppBlueKeyListener,false);
        ArielApplication.getApp().checkBluetooth();

     /*   new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 6000);*/


/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                testBle();
            }
        },12000);
*/

        initWheelView();
    }


   /* public void testBle() {
        KeyStoreHelper helper = new KeyStoreHelper();
        helper.initKeyStore();

        Gson gson = new Gson();

        helper.setRsaKeyAlias("18652965530");
        byte[] encodeAccessKey = Base64.decode(
                "t7jV37zaEMuoJ4jNDawTCqqNAno76M65gpcwbNQCedgkwKzDJcVr+KZ0yHsOU66c6CKTtFMaCZh/7mrp/YU8FA\\u003d\\u003d", Base64.NO_WRAP);
        Log.e(TAG, "encodeAccessKey=" + BleProtocol.bytesToHexString(encodeAccessKey));
        Log.e(TAG, "pubkey=" + helper.getRSAPublic());
        String strPublicKey = helper.getRSAPublic();


        byte[] decodeAccessKey = helper.RSADecrypt(encodeAccessKey);
        Log.e(TAG, "decodeAccessKey=" + BleProtocol.bytesToHexString(decodeAccessKey));
        //mBluetoothKeysBean.getBluetoothSensitiveInfo().setBluetoothAccessKey(strDecode);
        List<BleCarKey> mKeyList = new ArrayList<BleCarKey>();
        BleCarKey mCarKey = new BleCarKey();
        mCarKey.setUserName("");
        mCarKey.setBleConnectKey("6aS3d7");
        mCarKey.setBlePassword(decodeAccessKey);
        mCarKey.setUserId(999553);
        mCarKey.setBleName("BLEV866793030058211");
        //mCarKey.setBleName("苏A1MC72");
        mCarKey.setBlePassOffset("goodbyworldkeyss");
        Log.e(TAG, "queryBlueKey() mCarKey=" + mCarKey.toString());
        mKeyList.add(mCarKey);
        if (BleKeyManager.getInstance(getApplicationContext()).setCarKey(mKeyList)) {
            Log.e(TAG, "蓝牙钥匙设置成功，进入扫描状态!");
        } else {
            Log.e(TAG, "蓝牙钥匙设置失败");
        }
    }
*/

    public interface MyTouchListener {
        void onTouchEvent(MotionEvent event);
    }

    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        EventBus.getDefault().postSticky("onRestart");
    }


    private void showPushMessageFragment(String msg) {
        String[] msgArray = msg.split("---");
        Log.i(TAG, "msg = " + msgArray[1]);

        PushMessage pushMessage = new Gson().fromJson(msgArray[1], PushMessage.class);
        PushMessageBodyBean pushMessageBodyBean = new PushMessageBodyBean();
        pushMessageBodyBean.setMsgTitle(pushMessage.getMessage().getMsgTitle());
        pushMessageBodyBean.setPushBody(pushMessage.getMessage().getPushBody());
        LocalFragmentManager.getInstance().showSubFragment(getSupportFragmentManager(), LocalFragmentManager.FragType.PUSHMESSAGE, R.id.main_content_view, pushMessageBodyBean);
    }

    @Override
    protected void initListener() {
        new VoiceVehicleControl(this);
        //new WheelControl(this);
    }

    @Override
    protected void initData() {
        mCurrentFragment = LocalFragmentManager.getInstance().createMainFragment(mFragmentManager);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_arie;
    }

    private void initOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return;
            } else {
            }
        } else {
        }
    }

    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        ArrayList<String> toApplyList = new ArrayList<>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        if (!toApplyList.isEmpty()) {
            String tmpList[] = new String[toApplyList.size()];
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), REQUEST_CODE);
        }
    }

    private void initMainViewItems() {
        mFragmentManager = getSupportFragmentManager();
        SDKInitializer.initialize(getApplicationContext());

        main_content_view = (FrameLayout) findViewById(R.id.main_content_view);
        rootView = findViewById(R.id.root);
    }

    @Override
    public void onBackPressed() {

        if (null != ArielApplication.getApp().getDrViewPager() && ArielApplication.getApp().getDrViewPager().getVisibility() == View.VISIBLE) {
            Fragment curFragment = getVisibleFragment();
            if (curFragment instanceof OrdinaryFragment || curFragment instanceof IntelligenceFragment || curFragment instanceof SimpleFragment) {
//                integrationCore.voiceCtrl(false);  //驾驶模式暂不退出
            } else {
                mFragmentManager.popBackStack();
            }
        } else {
            popBackStack();
        }


    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                if (fragment instanceof MainFragment) {

                } else {
                    return fragment;
                }

            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以做你要做的事情了。
                    VoicePolicyManage.getInstance().init(this);
                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        isOnpause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        integrationCore.initDbCarInfo();
        PateoVirtualSDK.initialize(MainActivity.this,
                IntegrationCore.getIntergrationCore(this));
        //注册移到这个位置是为了解决WeChatTranslucentActivity中也注册,
        //返回响应就返回到WeChatTranslucentActivity中
        //但是WeChatTranslucentActivity是会被销毁的。
        NluResultManager.getInstance().setWechatCallback(mWechatCallback);
        WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);

        isOnpause = false;
        mHotwordManager = HotwordManager.getInstance(MainActivity.this, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                if (b && !isOnpause) {
                    Log.d(TAG, "HotwordManager onConnect");
//                    addWakeupElementsforWeChat();
                } else {
                    if (null != mHotwordManager) {
                        Log.d(TAG, "HotwordManager onConnect clear");
                        mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
                    }
                }
            }
        });
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        LocationImpl.getInstance(this).release();
        NluResultManager.getInstance().release();
        IntegrationCore.getIntergrationCore(mContext).onDestroy();
        PushManager.getInstance().unBindService();
        unbindService(mVoiceFloatViewServiceConnection);
        //EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 以下内容为语音接口部分
     */
    //音乐操作指令返回
    private MusicCommandCallback mMusicCommandCallback = new MusicCommandCallback() {
        @Override
        public void onMusicPlay() {
//            Toast.makeText(MainActivity.this, "play command", Toast.LENGTH_SHORT).show();

//            if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
//                integrationCore.mPateoFMCMD.doRadioOn();
//            } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.MUSIC) {
//                Intent intent = new Intent("state_play");
//                ArielApplication.getApp().getContext().sendBroadcast(intent);
//                integrationCore.mMusicCMD.playMusic();
//            } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.NEWS) {
//
//            }
            Log.e("AudioPolicyManager", "--onMusicPlay");
            integrationCore.handlePlay();
        }

        @Override
        public void onMusicPause() {
//            Toast.makeText(MainActivity.this, "pause command", Toast.LENGTH_SHORT).show();
//            if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
//                 integrationCore.mPateoFMCMD.doRadioOff();
//            } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.MUSIC) {
//                Intent intent = new Intent("state_pause");
//                ArielApplication.getApp().getContext().sendBroadcast(intent);
//                integrationCore.mMusicCMD.pauseMusic();
//            } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.NEWS) {
//
//            }
            Log.e("AudioPolicyManager", "--onMusicPause");
            integrationCore.handlePause();
        }

        @Override
        public void onMusicStop() {
//            Toast.makeText(MainActivity.this, "stop command", Toast.LENGTH_SHORT).show();

            if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
                integrationCore.mPateoFMCMD.doRadioOff();
            } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.MUSIC) {
                Intent intent = new Intent("state_pause");
                ArielApplication.getApp().getContext().sendBroadcast(intent);
                integrationCore.setMusicStop();
            } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.NEWS) {

            }
        }

        @Override
        public void onMusicPrevious() {
//            Toast.makeText(MainActivity.this, "previous command", Toast.LENGTH_SHORT).show();
            integrationCore.setMusicPrevious();
        }

        @Override
        public void onMusicNext() {
//            Toast.makeText(MainActivity.this, "next command", Toast.LENGTH_SHORT).show();
            integrationCore.setMusicNext();
        }
    };

    String key;
    //音乐搜索返回
    private MusicSearchCallback mMusicSearchCallback = new MusicSearchCallback() {
        @Override
        public void onMusicSearchResult(String song, ArrayList<String> singers, ArrayList<String> tags) {
            Log.e(TAG, "-onMusicSearchResult : song = " + song + "--singers = " + singers + "--tags = " + tags);
            /**String searchString = "";
             if (singers != null && singers.size() > 0) {
             for (int i = 0; i < singers.size(); i++) {
             if (i == 0) {
             searchString = singers.get(i);
             } else {
             searchString = searchString + "和" + singers.get(i);
             }
             }
             }
             if (!TextUtils.isEmpty(song)) {
             searchString = searchString + " " + song;
             }
             integrationCore.searchMusic(searchString);*/

            //Add singer key
            StringBuilder searchBuilder = new StringBuilder();
            if (singers != null && singers.size() > 0) {
                for (int i = 0; i < singers.size(); i++) {
                    if (i == 0) {
                        searchBuilder.append(singers.get(i));
                    } else {
                        searchBuilder.append("和");
                        searchBuilder.append(singers.get(i));
                    }
                }
            }

            //Add song key
            if (!TextUtils.isEmpty(song)) {
                if (!TextUtils.isEmpty(searchBuilder.toString().trim())) {
                    searchBuilder.append(" ");
                }
                searchBuilder.append(song);
            }

            //add tag key
            if (tags != null && tags.size() > 0) {
                if (!TextUtils.isEmpty(searchBuilder.toString().trim())) {
                    searchBuilder.append(" ");
                }

                for (int j = 0; j < tags.size(); j++) {
                    if (j == 0) {
                        searchBuilder.append(tags.get(j));
                    } else {
                        searchBuilder.append(" ");
                        searchBuilder.append(tags.get(j));
                    }
                }
            }

            if (integrationCore == null) {
                return;
            }
            String searchKey = searchBuilder.toString().trim();
            if (TextUtils.isEmpty(searchKey)) {
                //VoicePolicyManage.getInstance().speak(mContext.getString(R.string.music_search_no_content));
                //如果mQQServiceListner已经在列表中，不会重复添加
                integrationCore.mMusicCMD.addQQServiceStatusListener(mQQServiceListner);
                integrationCore.mMusicCMD.playMusic();
            } else {
                integrationCore.searchMusic(searchKey);
            }
//            LocalFragmentManager.getInstance().showSubFragment(getSupportFragmentManager(), LocalFragmentManager.FragType.SEARCHMUSIC, R.id.main_content_view);
        }
    };

    private QQMusicCMD.OnQQServiceStatusListener mQQServiceListner = new QQMusicCMD.OnQQServiceStatusListener() {
        @Override
        public void onQQServiceConnected() {
            if (integrationCore == null) {
                return;
            }
            integrationCore.mMusicCMD.playMusic();
        }
    };


    //语音搜索内容返回
    private DcsResultListener mDcsResultListener = new DcsResultListener() {
        @Override
        public void onResult(final DcsDataWrapper dataWrapper) {
            if (dataWrapper == null) {
                return;
            }
            Log.i(TAG, "onResult:type is:" + dataWrapper.getType());
            final IASRSession session = SessionFactory.getInstance().obtain(dataWrapper.getType());
            AbstractBaseFragment mfragment = LocalFragmentManager.getInstance().createFragment(mFragmentManager,
                    dataWrapper, session, MainActivity.this);
            if (mfragment == null) {
                Log.i(TAG, "null fragment");
                return;
            }
            mCurrentFragment = mfragment;
            mCurrentFragment.setLoadedListener(new IFragmentStatusListener() {
                @Override
                public void onLoaded() {
                    Log.i(TAG, "handle session");
                    session.handleASRFeedback(dataWrapper);
                }
            });

        }

        @Override
        public void onShowSpeechContent(String content) {

        }

        @Override
        public void onShowBroadcastContent(String content) {

        }
    };

    private LaunchAppCallback mLaunchAppCallback = new LaunchAppCallback() {
        @Override
        public void openNavApp() {
            IntegrationCore.getIntergrationCore(ArielApplication.getApp()).VoiceJump(StageController.Stage.NAVIGATION);
        }

        @Override
        public void openPhone() {
            IntegrationCore.getIntergrationCore(ArielApplication.getApp()).VoiceJump(StageController.Stage.PHONE);
        }

        @Override
        public void openContextualMode() {
            IntegrationCore.getIntergrationCore(ArielApplication.getApp()).VoiceJump(StageController.Stage.SCENARIO);
        }

        @Override
        public void openRadio() {
            IntegrationCore.getIntergrationCore(MainActivity.this).VoiceJump(StageController.Stage.RADIO);
            IntegrationCore.getIntergrationCore(MainActivity.this).mPateoFMCMD.playCurrent();
        }

        @Override
        public void openMusic() {
            IntegrationCore.getIntergrationCore(ArielApplication.getApp()).VoiceJump(StageController.Stage.MUSIC);
        }
    };

    private VoiceDcsCallback mVoiceDcsCallback = new VoiceDcsCallback() {
        @Override
        public void openVoiceDcs(DcsDataWrapper dcsWrapper) {
            if (dcsWrapper == null) {
                return;
            }
            switch (dcsWrapper.getType()) {
                case DataTypeConstant.SCENIC_TYPE:
                case DataTypeConstant.RESTAURANT_TYPE: {
                    VoicePolicyManage.getInstance().speak("为您查询到如下结果，请选择");
                    Intent intent = new Intent(MainActivity.this, VoiceDcsActivity.class);
                    intent.putExtra("dcsWrapper", dcsWrapper);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
                case DataTypeConstant.NEWS_PLAY_INFO_TYPE:
                    if (!"com.qinggan.app.arielapp.VoiceNewsActivity".
                            equals(ActivityLifecycleListener.currentActivity.getClass().getName())) {
                        return;
                    } else {
                        Intent intent = new Intent(MainActivity.this, VoiceNewsActivity.class);
                        intent.putExtra("dcsWrapper", dcsWrapper);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    break;
                default:
                    Intent intent = new Intent(MainActivity.this, VoiceDcsActivity.class);
                    intent.putExtra("dcsWrapper", dcsWrapper);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }

        }
    };

    private LocalRadioSearchCallback mLoacalRadioCallback = new LocalRadioSearchCallback() {

        @Override
        public void onRadioSearchResult(String frequency, String type) {
            Log.d(TAG, "--mLoacalRadioCallback-frequency : " + frequency);

            IntegrationCore.getIntergrationCore(MainActivity.this).VoiceJump(StageController.Stage.RADIO);

            if (TextUtils.isEmpty(frequency)) {
                IntegrationCore.getIntergrationCore(MainActivity.this).mPateoFMCMD.playCurrent();
                return;
            }

            float fFrequency = Float.valueOf(frequency);
            boolean isVaildFrequency = IntegrationCore.getIntergrationCore(MainActivity.this).mPateoFMCMD.isValidFrequency(fFrequency);
            if (isVaildFrequency) {
                IntegrationCore.getIntergrationCore(MainActivity.this).mPateoFMCMD.doSetFrequency(fFrequency);
            } else {
                ToastUtil.show(R.string.frequency_invalid_toast, mContext);
            }
        }
    };

    //    private final static String WECHAT_START = "wechat_start";
//    private final static String WECHAT_SELECT = "wechat_select";
//    private final static String WECHAT_CONTENT = "wechat_content";
//    private final static String WECHAT_SEND = "wechat_send";
//    private final static String WECHAT_END = "wechat_end";
    private WechatCallback mWechatCallback = new WechatCallback() {
        @Override
        public void onPrepareSendWechat(String name) {
            if (PhoneStateManager.getInstance(MainActivity.this).getPhoneState() == PhoneState.OUT_CAR_MODE) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                return;
            }

            if (!AccessibilityUtil.isAccessibilitySettingsOn(MainActivity.this)) {
                Log.d(TAG,"Accessibility service is disable");
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                MainActivity.this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }

            UMAnalyse.startTime(UMDurationEvent.WECHAT);

            String pinyinName = ChineseToPY.getAllPinYinFirstLetter(name);
            WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_SEND_CONTENT);
            mWechatName = pinyinName;
            Log.d(TAG, "send wechat get name : " + mWechatName);

            Intent mIntent = new Intent(MainActivity.this, WeChatSendMsgActivity.class);
            mIntent.putExtra("username", mWechatName);
            startActivity(mIntent);
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
                addWakeupElementsforWeChat();
                mWindowManager.addView(mView, WheelViewParams);
            } else if (WechatManager.WECHAT_SEND_CANCEL.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_END);
                MessageEvent event = new MessageEvent();
                event.setStep(5);
                event.setName(mWechatName);
                EventBus.getDefault().post(event);
                mWindowManager.removeViewImmediate(mView);
                if (null != mHotwordManager) {
                    mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
                }
            } else if (WechatManager.WECHAT_SEND_AGAIN.equals(type)) {
                WechatManager.getInstance().setWechatDomain(WechatManager.WECHAT_SEND_CONTENT);
                MessageEvent event = new MessageEvent();
                event.setStep(6);
                event.setName(mWechatName);
                EventBus.getDefault().post(event);
            }
        }
    };

    private final static String VOICE_SEND_WECHAT = "voice_send_wechat_new";
    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;

    private void addWakeupElementsforWeChat() {
        if (mHotwordManager == null) {
            return;
        }

        Log.d(TAG, " addWakeupElements ");

        ArrayList<UIControlElementItem> elementItems =
                new ArrayList<>();
        int[] select_words = {R.string.navi_first,
                R.string.navi_second, R.string.navi_third};
        for (int i = 0; i < select_words.length; i++) {
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
    }

    public void onSelectOtherOC(String action) {
        Log.e(TAG, "onSelectOtherOC : " + action);
        switch (action) {
            case "1":
            case "2":
            case "3":
                mHandler.sendEmptyMessage(Integer.parseInt(action));
                break;
        }
    }

    long startTime = 0;

    /**
     * 手机返回键
     */
    public void popBackStack() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            startTime = currentTime;
        } else {
            finish();
            System.exit(0);
        }

    }

    private void handleVirtual(int actionCode, String name, String action) {
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
        AccessibilityUtil.initAccessibility(this);
        PateoVirtualSDK.doAction(MainActivity.this,
                actionBean, IntegrationCore.getIntergrationCore(MainActivity.this));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doAllWakeUpEvent(AllWakeupEvent allWakeupEvent) {
        Log.d(TAG, "doAllWakeUpEvent:allWakeupEvent:" + allWakeupEvent);
        //声音大一点
        if (allWakeupEvent == AllWakeupEvent.VOLUME_ADD) {
            Log.d(TAG, "connectToCar:" + PhoneStateManager.getInstance(ArielApplication.getApp().getContext()).isConnectedToCar() + ", isFm:" + AudioPolicyManager.getInstance().getCurrentAudioType());
            ArielVolumeManager.getInstance().adjustVolume(true, ArielVolumeManager.MOBILE_VOICE_TYPE);
            return;
        }
        //声音小一点
        if (allWakeupEvent == AllWakeupEvent.VOLUME_SUB) {
            Log.d(TAG, "connectToCar:" + PhoneStateManager.getInstance(ArielApplication.getApp().getContext()).isConnectedToCar() + ", isFm:" + AudioPolicyManager.getInstance().getCurrentAudioType());
            ArielVolumeManager.getInstance().adjustVolume(false, ArielVolumeManager.MOBILE_VOICE_TYPE);
            return;
        }

        //停止导航
        if (allWakeupEvent == AllWakeupEvent.STOP_NAV) {
//            IntegrationCore.getIntergrationCore(ArielApplication.getApp()).cancelNav();
            EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_STOP_NAVI));
            return;
        }
        //回首页
        if (allWakeupEvent == AllWakeupEvent.BACK_MAIN) {
            AppManager.getAppManager().returnToActivity(MainActivity.class);
            return;
        }
        //上一首
        if (allWakeupEvent == AllWakeupEvent.LAST_SONG) {
            integrationCore.setMusicPrevious();
            return;
        }
        //下一首
        if (allWakeupEvent == AllWakeupEvent.NEXT_SONG) {
            integrationCore.setMusicNext();
            return;
        }
        //暂停
        if (allWakeupEvent == AllWakeupEvent.PAUSE_MUSIC) {
            integrationCore.handlePause();
            return;
        }
        //播放
        if (allWakeupEvent == AllWakeupEvent.PLAY_MUSIC) {
            integrationCore.handlePlay();
            return;
        }
        //下一台
        if (allWakeupEvent == AllWakeupEvent.NEXT_FM) {
            if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
                integrationCore.mPateoFMCMD.seekToPlay(true);
            }
            return;
        }
        //上一台
        if (allWakeupEvent == AllWakeupEvent.LAST_FM) {
            if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
                integrationCore.mPateoFMCMD.seekToPlay(false);
            }
            return;
        }

//        //接听电话
//        if (allWakeupEvent == AllWakeupEvent.CALL_ACCEPT) {
//            CallUtils.answerCall(ArielApplication.getApp());
//            return;
//        }
//        //挂断
//        if (allWakeupEvent == AllWakeupEvent.CALL_REFUSE) {
//            CallUtils.rejectCall();
//            return;
//        }
//        //上一台
//        if (allWakeupEvent == AllWakeupEvent.LAST_FM) {
//            IntegrationCore.getIntergrationCore(ArielApplication.getApp().getContext()).mPateoFMCMD.seekToPlay(false);
//            return;
//        }
//        //下一台
//        if (allWakeupEvent == AllWakeupEvent.NEXT_FM) {
//            IntegrationCore.getIntergrationCore(ArielApplication.getApp().getContext()).mPateoFMCMD.seekToPlay(true);
//            return;
//        }
//        //播放电台
//        if (allWakeupEvent == AllWakeupEvent.PLAY_FM) {
//            IntegrationCore.getIntergrationCore(ArielApplication.getApp().getContext()).mPateoFMCMD.playCurrent();
//            return;
//        }
//
//        //鸣笛闪灯，寻车
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_FIND_CAR) {
//            VehcleControlManager.getInstance(mContext).findCar(null);
//            return;
//        }
//        //热车
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_WORM_CAR) {
//            VehcleControlManager.getInstance(mContext).startEnine(null);
//            return;
//        }
//        //一键温暖
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_WORM) {
//            VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_WORM, null);
//            return;
//        }
//        //一键温暖
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_WORM_ALL_CAR) {
//            VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_WORM, null);
//            return;
//        }
//        //清凉
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_CLOD) {
//            VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_CLOD, null);
//            return;
//        }
//        //清凉
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_CLOD_ALL_CAR) {
//            VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_CLOD, null);
//            return;
//        }
//        //雨雪模式
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_RAIN) {
//            VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_RAIN_MODE, null);
//
//            return;
//        }
//        //雨雪模式
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_RAIN_ALL_CAR) {
//            VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_RAIN_MODE, null);
//            return;
//        }
//        //抽烟模式
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_SMOK) {
//            VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_SMOK_MODE, null);
//            return;
//        }
//        //抽烟模式
//        if (allWakeupEvent == AllWakeupEvent.VEHICLECONTROL_OPEN_SMART_SMOK_ALL_CAR) {
//            VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_SMOK_MODE, null);
//
//            return;
//        }
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String event) {
        switch (event) {
            case TspManager.LOGINEXPIRED:
                needLogin();
                break;
            case TspManager.NONETWORK:
                ToastUtil.show(R.string.no_network_tips, mContext);
                break;
            case WechatConstants.WECHAT_SEND_MSG_ORDER:
                addWakeupElementsforWeChat();
                mWindowManager.addView(mView, WheelViewParams);
                break;
            case WechatConstants.WECHAT_CANCEL_SEND_ORDER:
                mWindowManager.removeViewImmediate(mView);
                if (null != mHotwordManager) {
                    mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
                }
                break;
        }
    }

    View mView;
    WindowManager mWindowManager;
    WindowManager.LayoutParams WheelViewParams;

    public void initWheelView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).
                    inflate(R.layout.one_pixel_view, null);
        }
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (WheelViewParams == null) {
            WheelViewParams = new WindowManager.LayoutParams();
            WheelViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            WheelViewParams.gravity = Gravity.CENTER;
            WheelViewParams.format = PixelFormat.TRANSLUCENT;
            /* window parameter dp to pixel, for screen size of 5.5 inch temp */
            WheelViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            WheelViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg.what : " + msg.what);
            mWindowManager.removeViewImmediate(mView);
            Intent mIntent = new Intent(mContext, WeChatTransparentTmpActivity.class);
            mContext.startActivity(mIntent);
            handleVirtual(ActionCode.WECHAT_SELECT_INPUT_SEND, String.valueOf(msg.what), mWechatMsg);
            if (null != mHotwordManager) {
                mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
            }
        }
    };
}
