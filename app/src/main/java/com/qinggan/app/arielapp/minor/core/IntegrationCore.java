package com.qinggan.app.arielapp.minor.core;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BKMusicActivity;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.integration.MusicContacts;
import com.qinggan.app.arielapp.minor.integration.PateoFMCMD;
import com.qinggan.app.arielapp.minor.integration.PateoNewsCMD;
import com.qinggan.app.arielapp.minor.integration.PateoVehicleControlCMD;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.main.utils.InterceptorProxyUtils;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.music.MusicActivity;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneMainActivity;
import com.qinggan.app.arielapp.minor.radio.FMActivity;
import com.qinggan.app.arielapp.minor.scenario.RefreshUICallback;
import com.qinggan.app.arielapp.minor.scenario.SceneActivity;
import com.qinggan.app.arielapp.minor.utils.ListFilterHook;
import com.qinggan.app.arielapp.minor.utils.ListUtils;
import com.qinggan.app.arielapp.minor.utils.ShardPreUtils;
import com.qinggan.app.arielapp.minor.wechat.WeChatTranslucentActivity;
import com.qinggan.app.arielapp.minor.controller.ConnectivityController;
import com.qinggan.app.arielapp.minor.controller.DialogController;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.integration.BaiduNaviCMD;
import com.qinggan.app.arielapp.minor.integration.PateoBTCMD;
import com.qinggan.app.arielapp.minor.integration.PateoDatabaseCMD;
import com.qinggan.app.arielapp.minor.integration.PateoTSPCMD;
import com.qinggan.app.arielapp.minor.integration.QQMusicCMD;
import com.qinggan.app.arielapp.minor.integration.SystemCMD;
import com.qinggan.app.arielapp.minor.integration.VirtualClickCMD;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.minor.wechat.NoticeEvent;
import com.qinggan.app.arielapp.minor.wechat.NotificationBean;
import com.qinggan.app.arielapp.minor.wechat.utils.WeChatUtils;
import com.qinggan.app.arielapp.minor.wechat.utils.WechatConstants;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.tencent.qqmusic.third.api.contract.Data;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qinggan.app.arielapp.minor.integration.MusicContacts.DEFAULT_FILE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.EMPTYSTRINGVALUE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.KEYLASTSONGTITLE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.KEYOLDLIST;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.KEYOLDSONGALBUMTITLE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.KEYOLDSONGCOVERURI;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.KEYOLDSONGID;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.KEYOLDSONGSINGERTITLE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.KEYOLDSONGTITLE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.MODEGETFOLDERLIST;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.MODEGETPLAYLIST;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.MODEGETSONGLIST;
import static com.tencent.qqmusic.third.api.contract.Data.FolderType.RANK;

/**
 * Created by brian on 18-10-30.
 */

public class IntegrationCore implements PateoVirtualSDK.ActionCallback {
    private static final String TAG = IntegrationCore.class.getSimpleName();
    private static IntegrationCore mCore;
    private static final Object mLock = new Object();

    private ConnectivityController mConnectivityController;
    private DialogController mDialogController;
    private StageController mStageController;
    private CardController mCardController;
    private Context mContext;

    private Map<Integer, UICallback> mUICallbackList = new HashMap<>();

    private BTInterface mBTCmd;
    private DatabaseInterface mDBCmd;
    private NaviInterface mNaviCMD;
    public SystemInterface mSystemCmd;
    private TSPInterface mTSPCmd;
    private VirtualInterface mVirtualClickCmd;
    public MusicInterface mMusicCMD;
    public PateoFMCMD mPateoFMCMD;
    public PateoNewsCMD mPateoNewsCMD;

    private List<NotificationBean> mNotiList = new ArrayList<>();

    private boolean isNeedShowWeChat = true;

    public static final int METHOD_SYNC = 0;
    public static final int METHOD_ASYNC = 1;

    private static final int MSG_BT_CMD = 0;
    private static final int MSG_TSP_CMD = 1;
    private static final int MSG_SYSTEM_CMD = 2;
    private static final int MSG_DATABASE_CMD = 3;
    private static final int MSG_NAVI_CMD = 4;
    private static final int MSG_VIRTUAL_CMD = 5;
    private static final int MSG_MUSIC_CMD = 6;
    private static final int MSG_CARDINFO_CMD = 7;
    //private static final int MSG_UDPATE_CARDVIEW = 8;
    private static final int MSG_START_NAVI = 9;
    private static final int MSG_EXIT_NAVI = 10;

    //BT cmd list
    public static final int CMD_BT_ONE_KEY_WARM = 0x10;
    public static final int CMD_BT_ONE_KEY_COLD = 0x11;
    public static final int CMD_BT_RAIN_AND_FROG = 0x12;
    public static final int CMD_BT_SMOKE_MODE = 0x13;
    public static final int CMD_BT_GET_RADIO_LIST = 0x14;
    public static final int CMD_BT_SCAN_RADIO = 0x15;
    public static final int CMD_BT_ADJUST_VOLUMN = 0x16;
    public static final int CMD_BT_GET_CAR_INFO = 0x17;
    public static final int CMD_BT_GET_TEMP_IN_CAR = 0x18;
    public static final int CMD_BT_ONE_KEY_BREATHE = 0x19;
    public static final int CMD_BT_CAR_CONTROL = 0x1A;

    //TSP cmd list
    public static final int CMD_TSP_ONE_KEY_WARM = 0x20;
    public static final int CMD_TSP_ONE_KEY_COLD = 0x21;
    public static final int CMD_TSP_RAIN_AND_FROG = 0x22;
    public static final int CMD_TSP_SMOKE_MODE = 0x23;
    public static final int CMD_TSP_GET_VALIDATE_CODE = 0x24;
    public static final int CMD_TSP_REGISTER = 0x25;
    public static final int CMD_TSP_LOGIN = 0x26;
    public static final int CMD_TSP_GET_CAR_INFO = 0x27;
    public static final int CMD_TSP_GET_TEMP_IN_CAR = 0x28;
    public static final int CMD_TSP_ONE_KEY_BREATE = 0x29;
    public static final int CMD_TSP_CAR_CONTROL = 0x2A;

    //system cmd list
    public static final int CMD_SYSTEM_GET_CURRENT_TIME = 0x30;
    public static final int CMD_SYSTEM_GET_CURRENT_MUSIC_INFO = 0x31;
    public static final int CMD_SYSTEM_GET_ALL_CONTACTS = 0x32;
    public static final int CMD_SYSTEM_GET_CONTACT_BY_NAME = 0x33;
    public static final int CMD_SYSTEM_GET_CALL_LOG = 0x34;
    public static final int CMD_SYSTEM_CALL_PHONE = 0x35;
    public static final int CMD_SYSTEM_HANGUP_PHONE = 0x36;
    public static final int CMD_SYSTEM_GET_ALL_MUSIC_APPS = 0x37;
    public static final int CMD_SYSTEM_GET_MUSIC_LIST = 0x38;
    public static final int CMD_SYSTEM_GET_MUSIC_INFO = 0x39;
    public static final int CMD_SYSTEM_GET_ALBUM_INFO = 0x3A;
    public static final int CMD_SYSTEM_MUSIC_CONTROL = 0x3B;
    public static final int CMD_SYSTEM_GET_ALL_NAVI_APPS = 0x3C;

    //database cmd list
    public static final int CMD_DATABASE_SET_DESTINATION = 0x40;
    public static final int CMD_DATABASE_GET_DESTINATION = 0x41;
    public static final int CMD_DATABASE_GET_POI_COLLECTIONS = 0x42;
    public static final int CMD_DATABASE_SAVE_POI_COLLETION = 0x43;
    public static final int CMD_DATABASE_GET_POI_SEARCH_HISTORY = 0x44;
    public static final int CMD_DATABASE_SAVE_POI_SEARCH = 0x45;
    public static final int CMD_DATABASE_SAVE_DEFAULT_MUSIC_APP = 0x46;
    public static final int CMD_DATABASE_SAVE_DEFAULT_NAVI_APP = 0x47;
    public static final int CMD_DATABASE_CARD_MANAGER = 0x48;
    //public static final int CMD_DATABASE_CARD_INIT = 0x49;

    public static final int CMD_DATABASE_CARD_INSERT = 0x01;
    public static final int CMD_DATABASE_CARD_DELETE = 0x02;
    public static final int CMD_DATABASE_CARD_UPDATE = 0x03;
    //navigation cmd list
    public static final int CMD_NAVI_TIME_INTERVER_BACK_HOME = 0x50;
    public static final int CMD_NAVI_CALL_ROUTE_PLAN = 0x51;
    public static final int CMD_NAVI_FIND_CAR = 0x52;

    //virtual click cmd list
    public static final int CMD_VIRTUAL_WECHAT_SEND_MESSAGE = 0x60;
    public static final int CMD_VIRTUAL_WECHAT_RED_PACKAGE = 0x61;
    public static final int CMD_VIRTUAL_WECHAT_SHARE_POSITION = 0x62;
    public static final int CMD_VIRTUAL_WECHAT_RECEIVE_MESSAGE = 0x63;
    public static final int CMD_VIRTUAL_NAVI_START_NAVIGATION = 0x64;

    //Voice cmd list
    public static final int CMD_SET_WAKE_UP_WORDS = 0x70;

    //CardView cmd
    public static final int CMD_CARDINFO_INIT = 0x80;
    public static final int CMD_CARDINFO_PACKAGE_CHANGE = 0x81;

    public static final int KEY_UICALLBACK_INTELLIGENCE = 0;
    public static final int KEY_UICALLBACK_SIMPLE = 1;

    public static final String KEY_CARDINFO_PACKAGE_NAME = "cardinfo_package_name";

    private static final int VOLUME_SPAN = 5;

    private Handler mCoreHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BT_CMD:
                    Log.i("Brian", "core handler");
                    doBTCMD(msg);
                    break;
                //case CMD_DATABASE_CARD_INIT:
                case MSG_CARDINFO_CMD:
                    //doInitCard(msg);
                    //initCardViews();
                    doCardInfoCMD(msg);
                    break;
                case MSG_START_NAVI:
                     startNav();
                     break;
                case MSG_EXIT_NAVI:
                     backToHomeActivity();
                     break;
            }
        }
    };

    private void doCardInfoCMD(Message msg){
        int cmd = msg.arg1;
        switch(cmd){
            case CMD_CARDINFO_INIT:
                initCardViews();
                break;
            /** case CMD_CARDINFO_PACKAGE_CHANGE:
                String packageName = msg.getData().getString(KEY_CARDINFO_PACKAGE_NAME);
                mCardController.receiveAppInstallOrRemove(packageName);
                break; */
        }
    }

    /**
     * 蓝牙指令分发
     *
     * @param msg 　蓝牙指令消息
     */
    private void doBTCMD(Message msg) {
        int cmd = msg.arg1;
        switch (cmd) {
            case CMD_BT_ONE_KEY_WARM:
                btOneKeyWarm(cmd);
                break;
            case CMD_BT_CAR_CONTROL:
                btCarControl(cmd);
                break;
        }

    }

    /**
     * 蓝牙车控
     *
     * @param cmd 　蓝牙指令类型
     */
    private void btCarControl(int cmd) {
        UICallback callback = mUICallbackList.get(cmd);
        if (callback != null) {
            Bundle data = new Bundle();
            data.putString("content", "蓝牙车控");
            callback.callback2UI(cmd, data);
        } else {
            Log.i("Brian", "Callback is null");
        }
    }

    /**
     * 蓝牙一键温暖
     *
     * @param cmd 　蓝牙指令类型
     */
    private void btOneKeyWarm(int cmd) {
        UICallback callback = mUICallbackList.get(cmd);
        if (callback != null) {
            Bundle data = new Bundle();
            data.putString("content", "一键温暖");
            callback.callback2UI(cmd, data);
        } else {
            Log.i("Brian", "Callback is null");
        }
    }

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private IntegrationCore(Context context) {
        mContext = context;
        mConnectivityController = ConnectivityController.getConController(mContext);
        mStageController = StageController.getStageController();
        mDialogController = DialogController.getDialogController();
        mConnectivityController.registerNetChangeReceiver();

        initCMDs();

        mCardController = new CardController(mContext, this);
    }

    public void checkEnv() {

    }

    public void initCMDs() {
        //TODO: Read from database.
        mBTCmd = new PateoBTCMD();
        mDBCmd = new PateoDatabaseCMD();
        mTSPCmd = new PateoTSPCMD(mContext);
        mNaviCMD = new BaiduNaviCMD();
        mSystemCmd = new SystemCMD(mContext);
        mVirtualClickCmd = new VirtualClickCMD();
        mMusicCMD = new QQMusicCMD(mContext);
        mPateoFMCMD = new PateoFMCMD(mContext);
        mPateoNewsCMD = new PateoNewsCMD(mContext);
    }

    public void reloadCMD(int type) {
        //TODO: Read from database;

    }

    public static IntegrationCore getIntergrationCore(Context context) {
        synchronized (mLock) {
            if (mCore == null) {
                mCore = new IntegrationCore(context);
            }
        }
        return mCore;
    }

    public void onDestroy() {
        mConnectivityController.unregisterNetChangeReceiver();
    }

    /**
     * public void oneKeyWarm() {
     * Message mesg = mCoreHandler.obtainMessage(MSG_BT_CMD);
     * mesg.arg1 = CMD_BT_ONE_KEY_WARM;
     * mCoreHandler.sendMessageDelayed(mesg, 5000);
     * //mCoreHandler.sendMessage(mesg);
     * }
     * <p>
     * public void btControl() {
     * Message mesg = mCoreHandler.obtainMessage(MSG_BT_CMD);
     * mesg.arg1 = CMD_BT_CAR_CONTROL;
     * mCoreHandler.sendMessageDelayed(mesg, 1000);
     * }
     **/

    public void registerUICallback(int cmdId, UICallback uiCallback) {
        mUICallbackList.put(cmdId, uiCallback);
    }

    public interface UICallback {
        void callback2UI(int cmdId, Bundle bundle);
        void updateCardView(String cardId);
        void updateAllCard();
        //[BUG:L0001M-486] ADDED BY brianchen@pateo.com.cn, at 2018.12.05, BEGIN
        void updateSimpleCard();
        //[BUG:L0001M-486] ADDED BY brianchen@pateo.com.cn, at 2018.12.05, END
    }

    /**
     * 由页面自身实现来完成对音乐的异步数据获取
     */
    public interface MusicListener {
        void onBack(ArrayList list,boolean state);

        void onBackFolder(ArrayList list);

        void songChange();

        void playStateChanged();

        void favStateChange();

        void playListChange();

        void playModeChange();

        void codeState(boolean state);

        void forSpecialLocalCode(String cmd,int ret);

        void backFolderName(String name);
    }

    //寻找车辆
    public void findCar(Context context, LatLng start_point, LatLng end_point) {
        mNaviCMD.startWalkNavi(context, start_point, end_point);
    }

    //路径规划
    public void planRoute(Context context, String from, LatLng dest, LatLng origin) {
        EventBus.getDefault().post(dest);
        mNaviCMD.planRoute(context, from, dest, origin);
    }

    public void planRoute(Context context, String from, LatLng dest) {
        EventBus.getDefault().post(dest);
        mNaviCMD.planRoute(context, from, dest);
    }

    /**
     * 开始情景模式
     */
    public void executeScenario(int num, RefreshUICallback callback) {
        switch (num){
            case 0:
                PateoVehicleControlCMD.getInstance().refreshAir(mContext);
                break;
            case 1:
                PateoVehicleControlCMD.getInstance().warmMode(mContext);
                break;
            case 2:
                PateoVehicleControlCMD.getInstance().snowMode(mContext);
                break;
            case 3:
                PateoVehicleControlCMD.getInstance().smokeMode(mContext);
                break;
        }
    //    mScenarioCMD.executeScenario(num);
    }

    private VoiceChangeModeCallback mVoiceChangeModeCallback;

    //模式切换 isIncar  true 代表离车切驾驶
    public boolean voiceCtrl(boolean isIncar) {
        boolean isSuccess = false;
        if (null != mVoiceChangeModeCallback) {
            isSuccess = mVoiceChangeModeCallback.voiceChangeMode(isIncar);
        }
        return isSuccess;
    }

    public void setVoiceCallback(VoiceChangeModeCallback callback) {
        mVoiceChangeModeCallback = callback;
    }

    public interface VoiceChangeModeCallback {
        boolean voiceChangeMode(boolean isIncar);
    }

    public void changeStage(StageController.Stage stage) {
        mStageController.setStage(stage);
    }

    public StageController.Stage getStage() {
        return mStageController.getCurrentStage();
    }

    //将搜索文本保存到数据库
    public void saveSearchText(NaviSearchHistory naviSearchHistory, Context context, String classname) {
        mDBCmd.insert(naviSearchHistory, context, classname);
    }

    public void savePresetDest(NaviInfo naviInfo, Context context, String classname) {
        mDBCmd.insert(naviInfo, context, classname);
    }

    public void updatePresetDest(NaviInfo naviInfo, Context context, String classname) {
        mDBCmd.update(naviInfo, context, classname);
    }

    public void updateNaviInfoHistory(NaviSearchHistory naviSearchHistory, Context context, String classname) {
        mDBCmd.update(naviSearchHistory, context, classname);
    }

    public void deleteNaviInfo(NaviInfo naviInfo, Context context, String classname) {
        mDBCmd.delete(naviInfo, context, classname);
    }

    public void deleteAllNaviInfo(Context context,String classname) {
        mDBCmd.deleteAll(context,classname);
    }

    public void deleteSearchHistoryNaviInfo(NaviSearchHistory naviSearchHistory, Context context, String classname){
        mDBCmd.delete(naviSearchHistory, context, classname);
    }

    public List<BasicInfo> queryDestInfo(NaviInfo naviInfo, Context context, String classname) {
        List<BasicInfo> naviInfos = queryDestInfoByFilter(naviInfo, context, classname ,true);
        return naviInfos;
    }

    public List<BasicInfo> queryDestInfoByFilter(NaviInfo naviInfo, Context context, String classname, boolean isFilter) {
        List<BasicInfo> naviInfos =mDBCmd.queryByModel(naviInfo, context, classname);
        if (!isFilter) {
            return naviInfos;
        }
        List<BasicInfo> result = ListUtils.filter((List<BasicInfo>)naviInfos, new ListFilterHook<BasicInfo>(){
            @Override
            public boolean compare(BasicInfo temp) {
                return MapUtils.NAVIINFO_SYNC_FLAG_DELETE != ((NaviInfo)temp).getSyncFlag();
            }
        });
        return result;
    }

    public List<BasicInfo> queryAllSearchHistory(NaviSearchHistory naviSearchHistory, Context context, String classname){
        List<BasicInfo> historyInfos = mDBCmd.queryAll(context, classname);
        return historyInfos;
    }

    public List<BasicInfo> querySearchHistory(NaviSearchHistory naviSearchHistory, Context context, String classname){
        List<BasicInfo> historyInfos = mDBCmd.queryByModel(naviSearchHistory, context, classname);
        return historyInfos;
    }

    public void getTSPNaviInfo() {
        mTSPCmd.getFavorAddressList();
    }

    public void saveTSPNaviInfo(NaviInfo naviInfo) {
        AddressBean addressBean = MapUtils.naviInfo2AddressBean(naviInfo);
        mTSPCmd.addFavorAddress(addressBean);
    }

    public void saveTSPNaviInfo(NaviInfo naviInfo, boolean isNotifyUI) {
        AddressBean addressBean = MapUtils.naviInfo2AddressBean(naviInfo);
        mTSPCmd.addFavorAddress(addressBean, isNotifyUI);
    }

    public void updateTSPNaviInfo(NaviInfo naviInfo) {
        AddressBean addressBean = MapUtils.naviInfo2AddressBean(naviInfo);
        mTSPCmd.updateFavorAddress(addressBean);
    }

    public void updateTSPNaviInfo(NaviInfo naviInfo, boolean isNotifyUI) {
        AddressBean addressBean = MapUtils.naviInfo2AddressBean(naviInfo);
        mTSPCmd.updateFavorAddress(addressBean, isNotifyUI);
    }

    public void deleteTSPNaviInfo(NaviInfo naviInfo) {
        mTSPCmd.delFavorAddress(naviInfo.getSid());
    }

    public void deleteTSPNaviInfo(NaviInfo naviInfo, boolean isNotifyUI) {
        mTSPCmd.delFavorAddress(naviInfo.getSid(), isNotifyUI);
    }

    //查找本地数据
    public List<BasicInfo> searchDbData(Context context, String daoType) {
        List<BasicInfo> dataList = new ArrayList<BasicInfo>();
        dataList = mDBCmd.queryAll(context, daoType);
        return dataList;
    }

    private void clearOtherFuncActivity(){
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public boolean VoiceJump(StageController.Stage stage) {
        boolean isSuccess = true;
//        if(integrationCore.getStage()==StageController.Stage.MAIN_IN_CAR){//当前处于驾驶模式
        switch (stage) {
            case SCENARIO:
                clearOtherFuncActivity();
                mCoreHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeStage(StageController.Stage.SCENARIO);

                        CardInfo  mCardInfo= getCardInfo(CardController.CARD_TYPE_SCENARIO);

                        clickCard(CardController.CARD_TYPE_SCENARIO, Integer.valueOf(mCardInfo.getType()),
                                CardController.ACTION_CLICK_CARD,null );
//                        Intent sceneIntent = new Intent(mContext, SceneActivity.class);
//                        sceneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        mContext.startActivity(sceneIntent);
                    }
                }, 1000);
                break;
            case MUSIC:
                Log.i("Brian_music", "voice jump to music");
                clearOtherFuncActivity();
                mCoreHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeStage(StageController.Stage.MUSIC);
                        CardInfo  mCardInfo= getCardInfo(CardController.CARD_TYPE_MUSIC);

                        clickCard(CardController.CARD_TYPE_MUSIC, Integer.valueOf(mCardInfo.getType()),
                                CardController.ACTION_CLICK_CARD,null );
                        playMusic();
//                        Intent musicIntent = new Intent(mContext, BKMusicActivity.class);
//                        musicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        mContext.startActivity(musicIntent);
                    }
                }, 1000);
                break;
            case PHONE:
                clearOtherFuncActivity();
                mCoreHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeStage(StageController.Stage.PHONE);
                        CardInfo  mCardInfo= getCardInfo(CardController.CARD_TYPE_PHONE);

                        clickCard(CardController.CARD_TYPE_PHONE, Integer.valueOf(mCardInfo.getType()),
                                CardController.ACTION_CLICK_CARD,null );

//                        Intent phoneMainActivity = new Intent(mContext, PhoneMainActivity.class);
//                        phoneMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        mContext.startActivity(phoneMainActivity);
                    }
                }, 1000);
                break;
            case NAVIGATION:
                clearOtherFuncActivity();
                mCoreHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeStage(StageController.Stage.NAVIGATION);

                        CardInfo  mCardInfo= getCardInfo(CardController.CARD_TYPE_NAVI);

                        clickCard(CardController.CARD_TYPE_NAVI, Integer.valueOf(mCardInfo.getType()),
                                CardController.ACTION_CLICK_CARD,null );
//                        Intent navigationActivity = new Intent(mContext, NavigationActivity.class);
//                        navigationActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        mContext.startActivity(navigationActivity);
                    }
                }, 1000);
                break;
            case RADIO:
                clearOtherFuncActivity();
                mCoreHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeStage(StageController.Stage.RADIO);
                        CardInfo  mCardInfo= getCardInfo(CardController.CARD_TYPE_RADIO);

                        clickCard(CardController.CARD_TYPE_RADIO, Integer.valueOf(mCardInfo.getType()),
                                CardController.ACTION_CLICK_CARD,null );
//                        Intent intent = new Intent(mContext, FMActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        mContext.startActivity(intent);
                    }
                }, 1000);
                break;
        }
//        }else{
//            isSuccess=false;
//            Toast.makeText(context,"当前界面不支持该功能",Toast.LENGTH_SHORT).show();
//        }
        return isSuccess;
    }


    /**
     * 取消导航
     */
    public void cancelNav() {
        Log.i(TAG, "cancelNav");
        ActionBean actionBean = new ActionBean();
        actionBean.setActionCode(3003);
        actionBean.setAppName("百度");
        actionBean.setAddressee("");
        actionBean.setAction("");
        PateoVirtualSDK.doAction(mContext,
                actionBean, this);
    }

    /**
     * 开始导航
     */
    public void startNav() {
        Log.i(TAG, "startNav");
        ActionBean actionBean = new ActionBean();
        actionBean.setActionCode(3002);
        actionBean.setAppName("百度");
        actionBean.setAddressee("");
        actionBean.setAction("");
        PateoVirtualSDK.doAction(mContext,
                actionBean, this);
    }

    /**
     * 导航里选中的路径,第一个第二个第三个
     *
     * @param index
     */
    public void onNavRouteSelect(int index) {
        Log.i(TAG, "onNavRouteSelect index = " + index);
        ActionBean actionBean = new ActionBean();
        switch (index) {
            case 0:
                actionBean.setActionCode(ActionCode.NAVIGATION_CHOOSE_AND_START);
                actionBean.setAppName("百度");
                actionBean.setAddressee("");
                actionBean.setAction("第一个");
                break;
            case 1:
                actionBean.setActionCode(ActionCode.NAVIGATION_CHOOSE_AND_START);
                actionBean.setAppName("百度");
                actionBean.setAddressee("");
                actionBean.setAction("第二个");
                break;
            case 2:
                actionBean.setActionCode(ActionCode.NAVIGATION_CHOOSE_AND_START);
                actionBean.setAppName("百度");
                actionBean.setAddressee("");
                actionBean.setAction("第三个");
                break;
        }

        PateoVirtualSDK.doAction(mContext,
                actionBean, this);
    }

    private static final String PACKAGE_NAME_BAIDU_MAP = "com.baidu.BaiduMap";
    private static final String PACKAGE_NAME_WECHAT = "com.tencent.mm";

    public void trggerWakeUpSelect(int index){
        if (mSystemCmd.isSomeAppOnFront(mContext, PACKAGE_NAME_BAIDU_MAP)) {
            onNavRouteSelect(index);
        } else if (mSystemCmd.isSomeAppOnFront(mContext, PACKAGE_NAME_WECHAT)) {
            //TODO:
        }
    }

    public void triggerWakeUpStartNav(){
        if (mSystemCmd.isSomeAppOnFront(mContext, PACKAGE_NAME_BAIDU_MAP)) {
            startNav();
        }
    }


    /**
     * 挂断电话
     */
    public void rejectCall() {
        mSystemCmd.rejectCall();
    }

    public void onSuccess(final int actionCode) {
        Log.d(TAG, "virtual onSuccess" + actionCode);
        if (actionCode == ActionCode.NAVIGATION_EXIT) {
            if (mCallBack != null) {
                mCallBack.onNaviCancle();
            }
            sendExitNaviMsg();
        }else if(actionCode == ActionCode.NAVIGATION_CHOOSE){
            sendStarNaviMsg();
        }

        switch (actionCode) {
            case ActionCode.WECHAT_SEND_MSG_TO_PERSON:
            case ActionCode.WECHAT_CONFIRM_SEND:
            case ActionCode.WECHAT_SELECT_INPUT_SEND:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleWechatVirtualResult(true, actionCode);
                    }
                }, 1500);

                break;
        }

        if (actionCode == ActionCode.WECHAT_SELECT_INPUT_SEND ||
                actionCode == ActionCode.WECHAT_SEND_MSG_TO_PERSON) {
            UMAnalyse.stopTime(UMDurationEvent.WECHAT);
        }
    }

    //发送开始导航消息
    public void sendStarNaviMsg() {
        Message msg = mCoreHandler.obtainMessage(MSG_START_NAVI);
        mCoreHandler.sendMessageDelayed(msg, 1500);
    }

    //发送结束导航消息
    public void sendExitNaviMsg() {
        Message msg = mCoreHandler.obtainMessage(MSG_EXIT_NAVI);
        mCoreHandler.sendMessageDelayed(msg, 1500);
    }

    private NaviCancleCallBack mCallBack;

    public void setNaviCallBack(NaviCancleCallBack callBack) {
        mCallBack = callBack;
    }

    public interface NaviCancleCallBack {
        void onNaviCancle();
    }

    @Override
    public void onFail(String reason, int actionCode) {
        Log.d(TAG, "virtual onFail : " + reason + " actionCode " + actionCode);
        switch (actionCode) {
            case ActionCode.WECHAT_SEND_MSG_TO_PERSON:
            case ActionCode.WECHAT_SEARCH_PERSON:
            case ActionCode.WECHAT_SELECT_CONTACTS:
            case ActionCode.WECHAT_SEND_MSG:
            case ActionCode.WECHAT_INPUT_SEND_MSG:
            case ActionCode.WECHAT_CONFIRM_SEND:
            case ActionCode.WECHAT_SELECT_INPUT_SEND:
                Toast.makeText(mContext, "执行失败", Toast.LENGTH_SHORT).show();
                handleWechatVirtualResult(false, actionCode);
                UMAnalyse.stopTime(UMDurationEvent.WECHAT);
                break;
        }
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "virtual onCancel");
    }

    @Override
    public void onNotification(String notification) {
        Log.d(TAG, "virtual notification : " + notification);
        if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState()
                == PhoneState.OUT_CAR_MODE) {
            Log.d(TAG,"处于离车模式，不显示微信");
            mNotiList.clear();
            return;
        }

        int status = ShardPreUtils.getInstance(MusicContacts.DEFAULT_FILE)
                .getIntValue(WechatConstants.MSG_RECEIVE_SAVE_STATUS_KEY);
        boolean isLastVoiceStatusOn = status > 0 ? true : false;
        if(!isLastVoiceStatusOn) {
            Log.d(TAG,"个人中心关闭了显示微信消息功能，不显示微信");
            mNotiList.clear();
            return;
        }

        WeChatUtils mWeChatUtils = new WeChatUtils();
        NotificationBean mBean = mWeChatUtils.handleNotificationStrToBean(notification);
        if (mBean == null) {
            return;
        }
        mNotiList.add(mBean);

        Log.d(TAG, "virtual notification : mNotiList " + mNotiList.size());

        if (mNotiList.size() < 2 || isNeedShowWeChat) {
            Intent mIntent = new Intent(mContext, WeChatTranslucentActivity.class);
            mIntent.putExtra(Constants.KEY_DIALOG_CONTENT, Constants.TYPE_WECHAT_COME_MSG);
            mIntent.putExtra(Constants.KEY_WECHAT_NOTICE, notification);
            mContext.startActivity(mIntent);
        } else {
            NoticeEvent event = new NoticeEvent();
            event.setNotice(mBean);
            EventBus.getDefault().post(event);
        }

    }

    public void cencalWeChatNotices() {
        if (mNotiList.size() > 0) {
            mNotiList.remove(0);
        }
    }

    private void handleWechatVirtualResult(boolean result, int actionCode) {
        Intent mIntent = new Intent(mContext, WeChatTranslucentActivity.class);
        mContext.startActivity(mIntent);

        NoticeEvent event = new NoticeEvent();
        event.setDoactionSuccess(result);
        event.setActionCode(actionCode);
        EventBus.getDefault().post(event);
    }

    public void cleanNoticesList() {
        mNotiList.clear();
    }

    //驾驶模式卡片数据管理
    public void cardInfoUpdate(int cmd, int commandType, BasicInfo basicInfo, Context context, String daoType) {
        if (commandType == CMD_DATABASE_CARD_INSERT) {
            mDBCmd.insert(basicInfo, context, daoType);
        } else if (commandType == CMD_DATABASE_CARD_DELETE) {
            mDBCmd.delete(basicInfo, context, daoType);
        } else if (commandType == CMD_DATABASE_CARD_UPDATE) {
            mDBCmd.update(basicInfo, context, daoType);
        }

        UICallback callback = mUICallbackList.get(cmd);
        if (callback != null) {
            ArrayList<CardInfo> list = (ArrayList<CardInfo>) (List) mDBCmd.queryAll(context, daoType);
            Bundle data = new Bundle();
            data.putParcelableArrayList("list", list);
            callback.callback2UI(cmd, data);
        } else {
            Log.i("Brian", "Callback is null");
        }
    }

    //初始化卡片信息
    public void initDbCarInfo() {
        Message mesg = mCoreHandler.obtainMessage(MSG_CARDINFO_CMD);
        //mesg.what = CMD_DATABASE_CARD_INIT;
        mesg.arg1 = CMD_CARDINFO_INIT;
        mCoreHandler.sendMessageDelayed(mesg, 0);
    }

    public SystemInterface getSystemCMD() {
        return mSystemCmd;
    }

    //CardControll Begin
    public List<CardInfo> readCardInfo() {
        return (List<CardInfo>) (List) mDBCmd.queryAll(mContext, CardInfo.class.getName());
    }

    public void updateCardInfo(List<CardInfo> cardInfos) {
        for (CardInfo cardInfo : cardInfos) {
            mDBCmd.update(cardInfo, mContext, CardInfo.class.getName());
        }
    }

    public CardInfo getCardInfo(String cardType) {
        return mCardController.getCardInfo(cardType);
    }

    public void restoreCardInfo() {
        mCardController.restoreCardInfo();
    }

    public void cardInfoCallback(String cardId){
        //[BUG:L0001M-486] MODIFIED BY brianchen@pateo.com.cn, at 2018.12.05, BEGIN
        UICallback intelligenceCallback = mUICallbackList.get(KEY_UICALLBACK_INTELLIGENCE);
        if (intelligenceCallback != null) {
            intelligenceCallback.updateCardView(cardId);
        } else {
            Log.i("Brian", "intelligenceCallback is null");
        }


        UICallback simpleCallback = mUICallbackList.get(KEY_UICALLBACK_SIMPLE);
        if (simpleCallback != null) {
            if (cardId.equalsIgnoreCase(CardController.CARD_TYPE_MUSIC) ||
                cardId.equalsIgnoreCase(CardController.CARD_TYPE_RADIO) ||
                cardId.equalsIgnoreCase(CardController.CARD_TYPE_NEWS)) {
                simpleCallback.updateSimpleCard();
            }
        } else {
            Log.i("Brian", "simpleCallback is null");
        }
        //[BUG:L0001M-486] MODIFIED BY brianchen@pateo.com.cn, at 2018.12.05, END
    }

    public void setTimeoutListener(CardController.SimpleStopTimeoutListener timeoutListener){
        mCardController.setTimeoutListener(timeoutListener);
    }

    public void playOrStopRadio(){
        final int radioStatus = mPateoFMCMD.getRadioStatus();
        switch (radioStatus) {
            case PateoFMCMD.STATUS_ON:
                mPateoFMCMD.doRadioOff();
                break;
            case PateoFMCMD.STATUS_SCANNING:
                //TODO:
                break;
            case PateoFMCMD.STATUS_OFF:
                mPateoFMCMD.doRadioOn();
                break;
        }
    }

    public void reloadCardView(String cardId){
        switch (cardId) {
            case CardController.CARD_TYPE_MUSIC:
                mCardController.reloadMusicCard();
                break;
            case CardController.CARD_TYPE_NAVI:
                mCardController.reloadNaviCard();
                break;
            case CardController.CARD_TYPE_NEWS:
                break;
            case CardController.CARD_TYPE_PHONE:
                break;
            case CardController.CARD_TYPE_RADIO:
                mCardController.reloadRadioCard();
                break;
            case CardController.CARD_TYPE_SCENARIO:
                mCardController.reloadScenarioCard();
                break;
        }
    }

    public CardController getCardController(){
        return mCardController;
    }

    //CardControll end

    public boolean judgeIsAM() {
        return mSystemCmd.judgeIsAM();
    }

    /**
     * 初始化 card view.
     */
    private void initCardViews() {
        mCardController.initCardInfos();
    }

    public void clickCard(String cardId, int cardType, int actionType, FragmentManager fragmentManager){
        mCardController.clickIntent(cardId,cardType,actionType,fragmentManager);
    }

    public boolean isSomeAppInstalled(String packageName, Context context){
        return mSystemCmd.isSomeAppInstalled(packageName, context);
    }

    public ArrayList<String> getOldSongList(){
        return ShardPreUtils.getInstance(DEFAULT_FILE).getOldList(KEYOLDLIST);
    }

    public String getOldSongID(){
        return ShardPreUtils.getInstance(DEFAULT_FILE).getStringValue(KEYOLDSONGID);
    }

    public String getOldSongTitle(){
        return ShardPreUtils.getInstance(DEFAULT_FILE).getStringValue(KEYOLDSONGTITLE);
    }

    public String getAlbumCoverUri(){
        return ShardPreUtils.getInstance(DEFAULT_FILE).getStringValue(KEYOLDSONGCOVERURI);
    }

    public String getAlbumTitle(){
        return ShardPreUtils.getInstance(DEFAULT_FILE).getStringValue(KEYOLDSONGALBUMTITLE);
    }

    public String getSingerTitle(){
        return ShardPreUtils.getInstance(DEFAULT_FILE).getStringValue(KEYOLDSONGSINGERTITLE);
    }

    public String getLastSongTitle(){
        return ShardPreUtils.getInstance(DEFAULT_FILE).getStringValue(KEYLASTSONGTITLE);
    }


    public void updateCard(String cardId){
        switch (cardId) {
            case CardController.CARD_TYPE_NAVI:
                mCardController.reloadNaviCard();
                break;
            case CardController.CARD_TYPE_MUSIC:
                mCardController.reloadMusicCard();
                break;
            case CardController.CARD_TYPE_PHONE:
                break;
            case CardController.CARD_TYPE_RADIO:
                mCardController.reloadRadioCard();
                break;
            case CardController.CARD_TYPE_SCENARIO:
                mCardController.reloadScenarioCard();
                break;
            case CardController.CARD_TYPE_NEWS:
                break;
        }
    }

    public void getVehicleDetailInfo() {
        mTSPCmd.getVehicleDetailInfo();
    }

    public void setNeedShowWeChat(boolean needShowWeChat) {
        isNeedShowWeChat = needShowWeChat;
    }

    /**
    public void updateCardViewWhenPackageChange(String packageName){
        Message mesg = mCoreHandler.obtainMessage(MSG_CARDINFO_CMD);
        mesg.arg1 = CMD_CARDINFO_PACKAGE_CHANGE;
        Bundle data = new Bundle();
        data.putString(KEY_CARDINFO_PACKAGE_NAME, packageName);
        mCoreHandler.sendMessage(mesg);
    }*/

    /**
     * 返回主界面
     */
    public void backToHomeActivity(){
        mSystemCmd.backToHomeActivity();
    }


    /*
    *Music Function Area begin
    */
    public void getLocalSongList() {
        mMusicCMD.getListAlias(MODEGETPLAYLIST, null, 0, 0);
    }

    /**
     * 将
     *
     * @param listener
     */
    public void setMusicListener(MusicListener listener) {
        mMusicCMD.setMusicListener(listener);
    }

    public void clearMusicListener(IntegrationCore.MusicListener listener) {
        mMusicCMD.clearMusicListener(listener);
    }

    public void playSongLocalPath(ArrayList<String> pathList){
        mMusicCMD.playSongLocalPath(pathList);
    }

    public Data.Song getCurrentPlayingSong(){
        return (mMusicCMD.getCurrentSong() != null)?mMusicCMD.getCurrentSong():null;
    }

    public void playOrPauseMusic(){
        if (getMusicStatus() == MusicContacts.PLAYSTARTED){
            mMusicCMD.pauseMusic();
        } else {
            mMusicCMD.playMusic();
        }
    }

    public Data.Song getLastPlayMusic(){
        return mMusicCMD.getOldSong();}

    public Data.Song getCurrentMusic(){
        return (mMusicCMD.getCurrentSong() != null)?mMusicCMD.getCurrentSong():null;
    }


    public void playOldSong(){
        ArrayList<String> mList = getOldSongList();
        String mID = getOldSongID();
        if(mList != null && mID != null && mID != EMPTYSTRINGVALUE){
            for(int i = 0;i<mList.size();i++){
                if(mID.equals(mList.get(i))){
                    mMusicCMD.playSongIdAtIndex(mList,i);
                }
            }
        }else if(mList != null){
            mMusicCMD.playSongIdAtIndex(mList,0);
        }
    }

    public Data.FolderInfo getFolderByTitle(String folderTitle) {
        return mMusicCMD.getFolderByTitle(folderTitle);
    }

    public long getCurrTime() {
        return mMusicCMD.getCurrTime();
    }

    public long getTotalTime() {
        return mMusicCMD.getTotalTime();
    }

    public void getSongList(Data.FolderInfo folder) {
        mMusicCMD.getListAlias(MODEGETSONGLIST, folder.getId(), folder.getType(), 0);
    }

    public String getFavouriteFolderId() {
        return mMusicCMD.getFavouriteFolderId();
    }

    public void getFavList() {
        mMusicCMD.getListAlias(MODEGETSONGLIST, getFavouriteFolderId(), 101, 0);
    }

    public void getRankFolderList(){
        mMusicCMD.getListAlias(MODEGETFOLDERLIST, "", RANK, 0);
    }

    public ArrayList<Data.Song> getPlayListCache() {
        return mMusicCMD.getPlayListCache();
    }

    public void registerEventListener(ArrayList<String> eventList) {
        mMusicCMD.registerEventListener(eventList);
    }

    public void unregisterEventListener(ArrayList<String> eventList) {
        mMusicCMD.unregisterEventListener(eventList);
    }

    public void getPlayList(){
        mMusicCMD.getListAlias(MODEGETPLAYLIST,null,0,0);
    }

    public int getMusicStatus() {
        return mMusicCMD.getPlaybackState();
    }

    public int playMusic() {
        return mMusicCMD.playMusic();
    }

    public void adjustVolumeF(double key) {
        mMusicCMD.adjustVolumeF(key);
    }

    public void setPhoneMediaVolume(boolean increase) {
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (increase) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + VOLUME_SPAN, 1);
        } else {
            if (currentVolume - VOLUME_SPAN > 0) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - VOLUME_SPAN, 1);
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 1);
            }
        }

    }


    public void searchMusic(String key) {
        mMusicCMD.search(key, 0, true);
    }

    public int setMusicPause() {
        return mMusicCMD.pauseMusic();
    }

    public void setMusicStop() {
        if (mMusicCMD.getPlaybackState() == 4) {
            mMusicCMD.stopMusic();
        }
    }

    public int setMusicPrevious() {
        return mMusicCMD.skipToPrevious();
    }

    public void setMusicNext() {
        mMusicCMD.skipToNext();
    }

    public void getListAlias() {
        mMusicCMD.getListAlias(1, null, 0, 0);
    }

    public void playSongIdAtIndex(ArrayList list, int index) {
        mMusicCMD.playSongIdAtIndex(list, index);
    }

    public String getCurrentSong() {
        return (mMusicCMD.getCurrentSong() != null)?mMusicCMD.getCurrentSong().getId():null;
    }

    public void getKnownFolderByTitle(String title){
        mMusicCMD.getKnownFolderByTitle(title);
    }
    /*
     *Music Function Area end
     */

    public void handlePlay() {
        if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.MUSIC) {
            mMusicCMD.playMusic();
        } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.NEWS) {
            mPateoNewsCMD.setNewsPlay();
        } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
            mPateoFMCMD.playCurrent();
        }
    }

    public void handlePause() {
        Log.e("AudioPolicyManager","--handlePause type : " + AudioPolicyManager.getInstance().getCurrentAudioType());
        if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.MUSIC) {
            mMusicCMD.pauseMusic();
        } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.NEWS) {
            mPateoNewsCMD.setNewsPause();
        } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
            mPateoFMCMD.doRadioOff();
        }
    }
}
