package com.qinggan.app.arielapp.minor.radio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.volume.ArielVolumeManager;
import com.qinggan.app.arielapp.minor.core.FMStatusListener;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.mui.adapter.BroadListAdpter;
import com.qinggan.app.arielapp.minor.music.ImitateIphoneSwitch;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.utils.WakeupControlMgr;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.qinglink.api.Constant;

import java.util.ArrayList;
import java.util.List;

public class FMFragment extends UIControlBaseFragment implements View.OnClickListener, WakeupControlMgr.WakeupControlListener{

    private final String TAG = FMFragment.class.getSimpleName();

    private static final int UPDATE_FREQUENCY = 1000;
    private static final int UPDATE_RADIO_STATUS = 1001;
    private static final int HIDDEN_STATUS_BUTTON = 1002;
    private static final int INIT_VOLUME_VIEW = 1003;
    private static final int HIDDEN_VOLUME_VIEW = 1004;
    private static final int SHOW_BUTTOM_SHEET = 1005;
    private static final int HIDDEN_BUTTOM_SHEET = 1006;
    private static final int RESET_SCANING_STATUS = 1007;

    private View fmMainView;
    private IntegrationCore integrationCore;
    private List<String> frequencyList;
    private int radioStatus = -1;
    private float currentFrequency = 0;
    private int radioType;

    private View radioBack;
    private LinearLayout radioVoice;
    private ImageView radioScan;
    private ImageView radioList;
    private ImageView radioRipple;
    private ImageView raidoPlaystate;
    private TextView radioFrequency;
    private TextView radioTitle;
    private TextView radioTypeText;
    private ProgressBar radioScanLoading;
    private RelativeLayout volumeLinearLayout;
    private ImitateIphoneSwitch imitateIphoneSwitch;
    private Handler mUIHandler;

    private RecyclerView mRecyclerView;
    private FrameLayout main_ll;
    private BroadListAdpter frequencyAdapter;
    private SpringBackBottomSheetDialog bottomSheetDialog;
    private View bottomRootView;

    private LinearLayoutManager mLayoutManager;
    private int firstPositon;
    private int endPositon;
    private int listSize;
    private boolean mIsUp = false;
    private boolean isVoiceScroll = false;

    private boolean isPlay = true;

    private int VOLUME_SWIPE_THRESHOLD = 4;
    private int SWITCH_RADIO_SWIPE_THRESHOLD = 10;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fmMainView = inflater.inflate(R.layout.fragment_fm_main_new,container,false);
        integrationCore = IntegrationCore.getIntergrationCore(getActivity());
        return fmMainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFrequencyData();
        initUIHandle();
        initView();

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        VOLUME_SWIPE_THRESHOLD *= dm.density;
        SWITCH_RADIO_SWIPE_THRESHOLD *= dm.density;
        mUIHandler.sendEmptyMessage(INIT_VOLUME_VIEW);

        mUiControlItems.clear();
        mUIControlElements.clear();

        initVoiceWords();
    }


    private void initVoiceWords(){

        UIControlElementItem nextFrequency = new UIControlElementItem();
        nextFrequency.addWord(getString(R.string.voice_radio_next_frequency));
        nextFrequency.addWord(getString(R.string.voice_radio_change_frequency));
        nextFrequency.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NEXT_FREQUENCY_UI_CONTROL_ITEM);
        mUIControlElements.add(nextFrequency);

        UIControlElementItem preFrequency = new UIControlElementItem();
        preFrequency.addWord(getString(R.string.voice_radio_pre_frequency));
        preFrequency.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PRE_FREQUENCY_UI_CONTROL_ITEM);
        mUIControlElements.add(preFrequency);

        UIControlElementItem scanFrequency = new UIControlElementItem();
        scanFrequency.addWord(getString(R.string.voice_radio_scan_frequency1));
        scanFrequency.addWord(getString(R.string.voice_radio_scan_frequency2));
        scanFrequency.addWord(getString(R.string.voice_radio_scan_frequency3));
        scanFrequency.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.SCAN_FREQUENCY_UI_CONTROL_ITEM);
        mUIControlElements.add(scanFrequency);

        UIControlElementItem openMenu = new UIControlElementItem();
        openMenu.addWord(getString(R.string.voice_radio_open_menu));
        openMenu.addWord(getString(R.string.voice_radio_open_menu1));
        openMenu.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.RADIO_MENU_OPEN);
        mUIControlElements.add(openMenu);

        UIControlElementItem colseMenu = new UIControlElementItem();
        colseMenu.addWord(getString(R.string.voice_radio_close_menu));
        colseMenu.addWord(getString(R.string.voice_radio_close_menu1));
        colseMenu.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.RADIO_MENU_CLOSE);
        mUIControlElements.add(colseMenu);

        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();
    }

    private void addContentItemList() {
        Log.d(TAG,"addContentItemList");
        if (null == frequencyList || frequencyList.size() < 1) {
            Log.d(TAG,"addContentItemList return");
            return;
        }

        firstPositon = mLayoutManager.findFirstVisibleItemPosition();
        endPositon = mLayoutManager.findLastVisibleItemPosition();
        listSize = frequencyList.size();
        Log.d(TAG,"addContentItemList firstPositon : " + firstPositon + "endPositon : " + endPositon + "listSize : " + listSize);

        if (firstPositon >= 0 && endPositon >= 0 && listSize>0 && firstPositon<listSize && endPositon<listSize ) {
//            mUIControlList = mDcsBeanList.subList(firstPositon, listSize+1);
        } else {
            Log.d(TAG,"addContentItemList size return");
            return;
        }

        mUiControlItems.clear();
        mUIControlElements.clear();


        for(int i = firstPositon; i < endPositon + 1; i++) {
            String frequency = frequencyList.get(i);
            UIControlItem uiItem = new UIControlItem();
            uiItem.setLabel(frequency);
            uiItem.setIndex(i);
            String url = mFragmentHashCode + "-" + ConstantNavUc.RADIO_UI_CONTROL_ITEM + ":" + i;
            uiItem.setUrl(url);
            mUiControlItems.add(uiItem);
        }

        initVoiceWords();
    }


    private void addWakeupElements() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            firstPositon = mLayoutManager.findFirstVisibleItemPosition();
            endPositon = mLayoutManager.findLastVisibleItemPosition();
            listSize = frequencyList.size();
            if (firstPositon >= 0 && endPositon >= 0 && listSize>0 && firstPositon<listSize && endPositon<listSize ) {
                WakeupControlMgr.getInstance().setElementUCWords(WakeupControlMgr.FM_NAME_SPACE,
                        firstPositon, endPositon, this);
            }
        } else {
            WakeupControlMgr.getInstance().setElementUCWords(WakeupControlMgr.FM_NAME_SPACE,
                    0, 0, this);
        }
    }

    private void initView(){
        main_ll = fmMainView.findViewById(R.id.fm_main_ll);
        radioBack = fmMainView.findViewById(R.id.radio_back);
        radioVoice = fmMainView.findViewById(R.id.radio_voice);
        radioScan = fmMainView.findViewById(R.id.radio_frequency_scan);
        radioList = fmMainView.findViewById(R.id.radio_frequency_list);
        radioTypeText = fmMainView.findViewById(R.id.radio_type);
        radioFrequency = fmMainView.findViewById(R.id.radio_frequency);
        radioTitle = fmMainView.findViewById(R.id.radio_title);
        radioRipple = fmMainView.findViewById(R.id.raido_play_ripple);
        raidoPlaystate = fmMainView.findViewById(R.id.raido_play_state);
        volumeLinearLayout = fmMainView.findViewById(R.id.volume_linear);
        imitateIphoneSwitch = fmMainView.findViewById(R.id.volune_profile);
        radioScanLoading = fmMainView.findViewById(R.id.radio_scan_loading);

        main_ll.setOnTouchListener(touchListener);
        radioBack.setOnClickListener(onClickListener);
        radioVoice.setOnClickListener(onClickListener);
        radioScan.setOnClickListener(onClickListener);
        radioList.setOnClickListener(onClickListener);
        raidoPlaystate.setOnClickListener(onClickListener);

        if (radioStatus == Constant.Radio.Status.OFF) {
            isPlay = false;
            raidoPlaystate.setSelected(true);
            raidoPlaystate.setVisibility(View.VISIBLE);
        } else if (radioStatus == Constant.Radio.Status.ON){
            isPlay = true;
            raidoPlaystate.setVisibility(View.INVISIBLE);
            raidoPlaystate.setSelected(false);
        }
//        if (radioType == 1) {
            radioTypeText.setText("FM");
//        } else {
//            radioTypeText.setText("AM");
//        }
        radioFrequency.setText(String.valueOf(currentFrequency));

        integrationCore.mPateoFMCMD.addFMListener(fmStatusListener);

        if (radioStatus == Constant.Radio.Status.SCANNING) {
            updateScanStatus(true);
        } else {
            updateScanStatus(false);
        }

    }

    private void initFrequencyData(){
        frequencyAdapter = new BroadListAdpter(getContext());
        frequencyList = integrationCore.mPateoFMCMD.getFrequencyList();
        radioStatus = integrationCore.mPateoFMCMD.getRadioStatus();
        currentFrequency = integrationCore.mPateoFMCMD.getCurrentFrequency();
        radioType = integrationCore.mPateoFMCMD.getRadioType();
    }

    private void updateScanStatus(boolean isScaning) {
        if (isScaning) {
            radioScanLoading.setVisibility(View.VISIBLE);
            radioScan.setEnabled(false);
            radioList.setEnabled(false);
            raidoPlaystate.setEnabled(false);
        } else {
            radioScanLoading.setVisibility(View.GONE);
            radioScan.setEnabled(true);
            radioList.setEnabled(true);
            raidoPlaystate.setEnabled(true);

            if (mUIHandler.hasMessages(RESET_SCANING_STATUS)) {
                mUIHandler.removeMessages(RESET_SCANING_STATUS);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private void initUIHandle() {
        mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_FREQUENCY:
                        String frequency = (String) msg.obj;
                        radioFrequency.setText(frequency);
                        break;
                    case UPDATE_RADIO_STATUS:
                        int status = (int) msg.obj;
                        if (status == Constant.Radio.Status.SCANNING) {
                            updateScanStatus(true);
                        } else if (status == Constant.Radio.Status.ON) {
                            isPlay = true;
                            raidoPlaystate.setSelected(false);
                            raidoPlaystate.setVisibility(View.VISIBLE);
                            if (mUIHandler.hasMessages(HIDDEN_STATUS_BUTTON)) {
                                mUIHandler.removeMessages(HIDDEN_STATUS_BUTTON);
                            }
                            mUIHandler.sendEmptyMessageDelayed(HIDDEN_STATUS_BUTTON, 1000);
                            updateScanStatus(false);
                        } else if (status == Constant.Radio.Status.OFF) {
                            isPlay = false;
                            raidoPlaystate.setSelected(true);
                            raidoPlaystate.setVisibility(View.VISIBLE);
                            updateScanStatus(false);
                        } else {
                            updateScanStatus(false);
                        }
                        break;
                    case RESET_SCANING_STATUS:
                        updateScanStatus(false);
                        break;
                    case HIDDEN_STATUS_BUTTON:
                        raidoPlaystate.setVisibility(View.INVISIBLE);
                        break;
                    case INIT_VOLUME_VIEW:
                        imitateIphoneSwitch.setValues(getVolumeCurrent(),getViewHeight());
                        imitateIphoneSwitch.setColor(getResources().getColor(R.color.color_bea266));
                        break;
                    case HIDDEN_VOLUME_VIEW:
                        volumeLinearLayout.setVisibility(View.GONE);
                        break;
                    case SHOW_BUTTOM_SHEET:
                        showBottomSheetDialog();
                        break;
                    case HIDDEN_BUTTOM_SHEET:
                        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                            bottomSheetDialog.dismiss();
                            bottomSheetDialog = null;
                            mLayoutManager = null;
                        }
                        break;

                }
            }
        };
    }

    private void updateUI(int what, Object obj) {
        Message msg = mUIHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;

        mUIHandler.sendMessage(msg);
    }

    private void showBottomSheetDialog(){
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new SpringBackBottomSheetDialog(getContext(),R.style.CustomBottomSheetDialogTheme);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            bottomRootView = inflater.inflate(R.layout.radiolist_pop_layout,null,false);
            BottomSheetDialogLinearLayout bottom_ll = bottomRootView.findViewById(R.id.bottom_sheet_ll);
            ImageView expand = bottomRootView.findViewById(R.id.radiolist_expand);
            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                    bottomSheetDialog = null;
                    mLayoutManager = null;
                }
            });
            mRecyclerView = bottomRootView.findViewById(R.id.recycle_radio_list);
            mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(frequencyAdapter);
            frequencyAdapter.setList(frequencyList);
            frequencyAdapter.setOnItemClickListener(new BroadListAdpter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    float frequency = Float.valueOf(frequencyList.get(position)) / 1000;
                    Log.d(TAG,"click : "+position+" frequency : " + frequency);
                    updateUI(UPDATE_FREQUENCY, String.valueOf(frequency));
                    integrationCore.mPateoFMCMD.doSetFrequency(frequency);
                    bottomSheetDialog.dismiss();
                    bottomSheetDialog = null;
                    mLayoutManager = null;
                }
            });

            mRecyclerView.addOnScrollListener(mScrollListener);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    addContentItemList();
                    addWakeupElements();
                }
            });

            bottomSheetDialog.setContentView(bottomRootView);
            bottom_ll.bindBottomSheetDialog(bottomRootView);
            bottomSheetDialog.addSpringBackDisLimit(-1);
        }
        bottomSheetDialog.show();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.radio_back:
//                    integrationCore.changeStage(StageController.Stage.MAIN_IN_CAR);
                    getActivity().onBackPressed();
                    break;
                case R.id.radio_voice:
//                    connectBT();
                    VoicePolicyManage.getInstance().record(true);
                    break;
                case R.id.radio_frequency_scan:
                    updateScanStatus(true);
                    mUIHandler.sendEmptyMessageDelayed(RESET_SCANING_STATUS, 1000 * 70);
                    integrationCore.mPateoFMCMD.doAutoScan();
                    break;
                case R.id.radio_frequency_list:
                    frequencyList = integrationCore.mPateoFMCMD.getFrequencyList();

                    if (frequencyList != null && frequencyList.size() > 0) {
                        showBottomSheetDialog();
                    } else {
                        ToastUtil.show("没有电台",getContext());
                    }
                    break;
                case R.id.raido_play_state:
                    if (isPlay) {
                        isPlay = false;
                        raidoPlaystate.setSelected(true);
                        raidoPlaystate.setVisibility(View.VISIBLE);
                        integrationCore.mPateoFMCMD.doRadioOff();
                    } else {
                        isPlay = true;
                        raidoPlaystate.setSelected(false);
                        raidoPlaystate.setVisibility(View.VISIBLE);
                        integrationCore.mPateoFMCMD.doRadioOn();

                        if (mUIHandler.hasMessages(HIDDEN_STATUS_BUTTON)) {
                            mUIHandler.removeMessages(HIDDEN_STATUS_BUTTON);
                        }
                        mUIHandler.sendEmptyMessageDelayed(HIDDEN_STATUS_BUTTON, 1000);
                    }

                    break;
            }
        }
    };

    private FMStatusListener fmStatusListener = new FMStatusListener() {
        @Override
        public void onFrequencyListResponse(String s) {
            Log.d(TAG, "onFrequencyListResponse: s : "+s);
        }

        @Override
        public void onCurrentBandResponse(int type) {
            Log.d(TAG, "onCurrentBandResponse type : " + type);
            radioType = type;
        }

        @Override
        public void onCurrentFrequencyResponse(float frequency) {
            Log.d(TAG, "onCurrentFrequencyResponse: frequency : " + frequency);
            if (frequency != 0 && frequency != 1) {
                currentFrequency = frequency;
                updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
            }
        }

        @Override
        public void onRadioStatusResponse(int status) {
            Log.d(TAG, "onRadioStatusResponse status : " + status);

            if (status != Constant.Radio.Status.SCANNING) {
                frequencyList = integrationCore.mPateoFMCMD.getFrequencyList();
                frequencyAdapter.notifyDataSetChanged();
            }

            if (radioStatus != status) {
                updateUI(UPDATE_RADIO_STATUS, status);
                radioStatus = status;
            }
        }

        @Override
        public void onFmFrequencyListResponse(ArrayList<String> fmArrayList) {
            Log.d(TAG, "onFmFrequencyListResponse: fmArrayList : " + fmArrayList);
//            fmFrequencyList = fmArrayList;
        }

        @Override
        public void onAmFrequencyListResponse(ArrayList<String> amArrayList) {
            Log.d(TAG, "onAmFrequencyListResponse: amArrayList : " + amArrayList);
//            amFrequencyList = amArrayList;
        }

    };

    public void setTouchVolume(boolean increase) {
        float volumePercent = 0f;

        float increaseVolume;
        if (increase) {
            increaseVolume = 1;
        } else {
            increaseVolume = -1;
        }

        volumePercent =  getVolumeCurrent() +  increaseVolume/ ArielVolumeManager.getInstance().getMaxVehicleVolume();

        if (volumePercent > 1) {
            volumePercent = 1;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }

        imitateIphoneSwitch.setValues(volumePercent, getViewHeight());

        int volume = (int) Math.round(ArielVolumeManager.getInstance().getMaxVehicleVolume() * volumePercent);

        ArielVolumeManager.getInstance().setVolume(volume, ArielVolumeManager.MOBILE_FM_TYPE, false);
    }

    private float getVolumeCurrent(){

        int current = ArielVolumeManager.getInstance().getVehicleMediaVol();

        return (float)current/ArielVolumeManager.getInstance().getMaxVehicleVolume();
    }

    private int getViewHeight(){
        ViewGroup.LayoutParams layoutParams = imitateIphoneSwitch.getLayoutParams();
        return layoutParams.height;
    }

    @Override
    public void onItemSelected(String type, String key) {
        Log.d(TAG,"FMFragment onItemSelected type : " + type + "--key : " + key);

        if (!WakeupControlMgr.FM_NAME_SPACE.equals(type)) {
            return;
        }

        if (WakeupControlMgr.FM_NEXT_FREQUENCY.equals(key)) {
            integrationCore.mPateoFMCMD.seekToPlay(true);
            updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
        } else if (WakeupControlMgr.FM_LAST_FREQUENCY.equals(key)) {
            integrationCore.mPateoFMCMD.seekToPlay(false);
            updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
        } else if (WakeupControlMgr.FM_SCAN_FREQUENCY.equals(key)) {
            integrationCore.mPateoFMCMD.doAutoScan();
        } else if (WakeupControlMgr.FM_OPEN_MENU.equals(key) ||
                WakeupControlMgr.FM_OPEN_MENU1.equals(key) ||
                WakeupControlMgr.FM_OPEN_MENU2.equals(key)) {
            frequencyList = integrationCore.mPateoFMCMD.getFrequencyList();
            if (frequencyList != null && frequencyList.size() > 0) {
                if (mUIHandler.hasMessages(SHOW_BUTTOM_SHEET)) {
                    mUIHandler.removeMessages(SHOW_BUTTOM_SHEET);
                }
                mUIHandler.sendEmptyMessage(SHOW_BUTTOM_SHEET);
            } else {
                ToastUtil.show("没有电台",getContext());
            }
        } else if (WakeupControlMgr.FM_CLOSE_MENU.equals(key) ||
                WakeupControlMgr.FM_CLOSE_MENU1.equals(key)) {
            if (mUIHandler.hasMessages(HIDDEN_BUTTOM_SHEET)) {
                mUIHandler.removeMessages(HIDDEN_BUTTOM_SHEET);
            }
            mUIHandler.sendEmptyMessage(HIDDEN_BUTTOM_SHEET);
        } else if (WakeupControlMgr.FM_BACK_TO.equals(key) ||
                WakeupControlMgr.FM_BACK_TO1.equals(key)) {
            if (mUIHandler.hasMessages(HIDDEN_BUTTOM_SHEET)) {
                mUIHandler.removeMessages(HIDDEN_BUTTOM_SHEET);
            }
            mUIHandler.sendEmptyMessage(HIDDEN_BUTTOM_SHEET);

            getActivity().onBackPressed();

        } else if (WakeupControlMgr.FM_NEXT_PAGE.equals(key)) {
            if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
                return;
            }

            if (endPositon < listSize -1) {
                isVoiceScroll = true;
                mIsUp = false;
                mRecyclerView.smoothScrollToPosition(endPositon);
            }
        } else if (WakeupControlMgr.FM_LAST_PAGE.equals(key)) {
            if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
                return;
            }

            if (firstPositon > 0) {
                isVoiceScroll = true;
                mIsUp = true;
                mRecyclerView.smoothScrollToPosition(firstPositon);
            }
        } else {
            if (frequencyList == null || frequencyList.size() < 1) {
                return;
            }

            if ("0".equals(key)) {
                float frequency = Float.valueOf(frequencyList.get(0)) / 1000;
                updateUI(UPDATE_FREQUENCY, String.valueOf(frequency));
                integrationCore.mPateoFMCMD.doSetFrequency(frequency);
            } else {
                int index = Integer.valueOf(key);
                float frequency = Float.valueOf(frequencyList.get(index - 1)) / 1000;
                updateUI(UPDATE_FREQUENCY, String.valueOf(frequency));
                integrationCore.mPateoFMCMD.doSetFrequency(frequency);
            }

            if (mUIHandler.hasMessages(HIDDEN_BUTTOM_SHEET)) {
                mUIHandler.removeMessages(HIDDEN_BUTTOM_SHEET);
            }
            mUIHandler.sendEmptyMessage(HIDDEN_BUTTOM_SHEET);
        }
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onSelectCancel() {
        getActivity().onBackPressed();
    }

    @Override
    public void onSelectItemPosition(int position) {

    }

    @Override
    public void onSelectOtherOC(String action) {
        Log.d(TAG,"onSelectOtherOC action : " + action);
        if (ConstantNavUc.NEXT_FREQUENCY_UI_CONTROL_ITEM.equals(action)) {
            integrationCore.mPateoFMCMD.seekToPlay(true);
            updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
        } else if (ConstantNavUc.PRE_FREQUENCY_UI_CONTROL_ITEM.equals(action)) {
            integrationCore.mPateoFMCMD.seekToPlay(false);
            updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
        } else if (ConstantNavUc.SCAN_FREQUENCY_UI_CONTROL_ITEM.equals(action)) {
            integrationCore.mPateoFMCMD.doAutoScan();
        } else if (ConstantNavUc.RADIO_MENU_OPEN.equals(action)) {
            frequencyList = integrationCore.mPateoFMCMD.getFrequencyList();
            if (frequencyList != null && frequencyList.size() > 0) {
                showBottomSheetDialog();
            } else {
                ToastUtil.show("没有电台",getContext());
            }
        } else if (ConstantNavUc.RADIO_MENU_CLOSE.equals(action)) {
            if (bottomSheetDialog != null) {
                bottomSheetDialog.dismiss();
                bottomSheetDialog = null;
                mLayoutManager = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        addWakeupElements();
    }

    @Override
    public void onPause() {
        super.onPause();
        WakeupControlMgr.getInstance().clearElementUCWords(WakeupControlMgr.FM_NAME_SPACE);
    }

    @Override
    public void onDetach() {
        integrationCore.mPateoFMCMD.removeListener(fmStatusListener);
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
        bottomSheetDialog = null;
        mLayoutManager = null;
        super.onDetach();
    }

    @Override
    public void onBackPressed() {
        getActivity().onBackPressed();
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        private float startY = 0;//手指按下时的Y坐标
        private float startX = 0;//手指按下时的X坐标
        private int thresholdCount = 0;
        private float lastY = 0;
        private boolean isSlide = false;
        private boolean hasSlideVertical = false;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (radioStatus == Constant.Radio.Status.SCANNING) {
                return true;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSlide = true;
                    startX = event.getX();
                    lastY = startY = event.getY();
                    thresholdCount = 0;
                    hasSlideVertical = false;
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float endX = event.getX();
                    float endY = event.getY();
                    float distanceX = startX - endX;
                    float distanceY = startY - endY;
                    float absX = Math.abs(distanceX);
                    float absY = Math.abs(distanceY);
                    int count = (int) absY / VOLUME_SWIPE_THRESHOLD;
                    if (absY > absX && thresholdCount != count) {
                        if (endY - lastY < 0) {
                            setTouchVolume(true);
                        } else {
                            setTouchVolume(false);
                        }
                        volumeLinearLayout.setVisibility(View.VISIBLE);
                        thresholdCount = count;
                        hasSlideVertical = true;
                    }
                    lastY = endY;
                }
                break;
                case MotionEvent.ACTION_UP: {
                    float endX = event.getX();
                    float endY = event.getY();
                    float distanceX = startX - endX;
                    float distanceY = startY - endY;
                    float absX = Math.abs(distanceX);
                    float absY = Math.abs(distanceY);
                    if (isSlide && absX > absY) {
                        if (distanceX > SWITCH_RADIO_SWIPE_THRESHOLD) {
                            integrationCore.mPateoFMCMD.seekToPlay(false);
                            updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
                            isSlide = false;
                        } else if (distanceX < -SWITCH_RADIO_SWIPE_THRESHOLD) {
                            integrationCore.mPateoFMCMD.seekToPlay(true);
                            updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
                            isSlide = false;
                        }
                    }
                    if ((!hasSlideVertical) && absX < VOLUME_SWIPE_THRESHOLD && absY < VOLUME_SWIPE_THRESHOLD) {
                        onClickListener.onClick(raidoPlaystate);
                    }
                    hasSlideVertical = false;

                    mUIHandler.sendEmptyMessageDelayed(HIDDEN_VOLUME_VIEW, 500);
                }
                break;
            }
            return true;
        }
    };

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isVoiceScroll = false;
                addContentItemList();
                addWakeupElements();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!isVoiceScroll) {
                return;
            }

            int distance = 0;

            if (mLayoutManager == null || mRecyclerView == null) {
                return;
            }

            if (mIsUp) {
                int n = mLayoutManager.findLastVisibleItemPosition() - firstPositon;
                if ( 0 <= n && n < mRecyclerView.getChildCount()){
                    distance = -mRecyclerView.getChildAt(0).getBottom();
                }
            } else {
                int n = endPositon - mLayoutManager.findFirstVisibleItemPosition();
                if ( 0 <= n && n < mRecyclerView.getChildCount()){
                    distance = mRecyclerView.getChildAt(n).getTop();
                }
            }

            mRecyclerView.scrollBy(0, distance);
        }
    };

    public void onPrevious(){
        integrationCore.mPateoFMCMD.seekToPlay(false);
        updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
    }

    public void onNext(){
        integrationCore.mPateoFMCMD.seekToPlay(true);
        updateUI(UPDATE_FREQUENCY, String.valueOf(currentFrequency));
    }

}
