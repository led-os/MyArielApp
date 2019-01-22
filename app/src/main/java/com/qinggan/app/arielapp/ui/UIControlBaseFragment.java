package com.qinggan.app.arielapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.vehiclecontrol.VehcleControlManager;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.app.voiceapi.control.UIControlMgr;
import com.qinggan.app.voiceapi.nluresult.uicontrol.UIControlCallback;

import java.util.ArrayList;
import java.util.List;

import static com.qinggan.app.voiceapi.control.ConstantNavUc.VEHICLECONTROL_FIND_CAR;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.VEHICLECONTROL_OPEN_SMART_CLOD;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.VEHICLECONTROL_OPEN_SMART_RAIN;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.VEHICLECONTROL_OPEN_SMART_SMOK;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.VEHICLECONTROL_OPEN_SMART_WORM;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.VEHICLECONTROL_WORM_CAR;

/**
 * 用于设置UIControl,如果遇到release和regist的先后顺序混乱,就改用handler来做,不过暂时没有问题
 * Created by shuohuang on 18-6-4.
 */

public abstract class UIControlBaseFragment extends AbstractBaseFragment implements UIControlCallback {

    protected String TAG = getClass().getSimpleName();

    public List<UIControlElementItem> defaultElementItems = new ArrayList<>();

    //设置UIControl,url的头,防止界面切换后还能回调到
    protected int mFragmentHashCode;
    protected List<UIControlElementItem> mUIControlElements = new ArrayList<>();
    protected List<UIControlItem> mUiControlItems = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentHashCode = this.hashCode();
        initDefaultElements();
        Log.e(TAG, "onCreate mFragmentHashCode = " + mFragmentHashCode);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged = " + hidden);
        if (!hidden) {
            UIControlMgr.getInstance().addElementContent(mUIControlElements);
            UIControlMgr.getInstance().addListContent(mUiControlItems);
            UIControlMgr.getInstance().updateContentToAsr();
            UIControlMgr.getInstance().registerUIControlCallback(this, mFragmentHashCode);
        }
    }

    /**
     * 添加默认控制元素
     */
    private void initDefaultElements() {

        //下面是普通共有的
        UIControlElementItem cancel = new UIControlElementItem();
        cancel.addWord(getString(R.string.cancel));
        cancel.addWord(getString(R.string.back));
        cancel.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.CANCEL_ITEM);
        defaultElementItems.add(cancel);

        UIControlElementItem phoneAnswer = new UIControlElementItem();
        phoneAnswer.addWord(getString(R.string.phone_call_answer));
        phoneAnswer.addWord(getString(R.string.phone_call_answer1));
        phoneAnswer.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PHONE_CALL_ANSWER);
        defaultElementItems.add(phoneAnswer);

        UIControlElementItem phoneCancel = new UIControlElementItem();
        phoneCancel.addWord(getString(R.string.phone_call_cancel));
        phoneCancel.addWord(getString(R.string.phone_call_cancel1));
        phoneCancel.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PHONE_CALL_CANCEL);
        defaultElementItems.add(phoneCancel);

        UIControlElementItem backHome = new UIControlElementItem();
        backHome.addWord(getString(R.string.back_to_home));
        backHome.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.BACK_TO_HOME);
        defaultElementItems.add(backHome);

        //车控
        UIControlElementItem findCar = new UIControlElementItem();
        findCar.addWord(getString(R.string.wakeup35));
        findCar.addWord(getString(R.string.find_car));
        findCar.addWord(getString(R.string.find_car1));
        findCar.setIdentify(mFragmentHashCode + "-" + VEHICLECONTROL_FIND_CAR);
        defaultElementItems.add(findCar);

        UIControlElementItem wormCar = new UIControlElementItem();
        wormCar.addWord(getString(R.string.wakeup36));
        wormCar.addWord(getString(R.string.worm_mode));
        wormCar.setIdentify(mFragmentHashCode + "-" + VEHICLECONTROL_WORM_CAR);
        defaultElementItems.add(wormCar);

        UIControlElementItem openWarm = new UIControlElementItem();
        openWarm.addWord(getString(R.string.wakeup37));
        openWarm.addWord(getString(R.string.wakeup38));
        openWarm.addWord(getString(R.string.worm_mode));
        openWarm.setIdentify(mFragmentHashCode + "-" + VEHICLECONTROL_OPEN_SMART_WORM);
        defaultElementItems.add(openWarm);

        UIControlElementItem openClod = new UIControlElementItem();
        openClod.addWord(getString(R.string.wakeup39));
        openClod.addWord(getString(R.string.wakeup40));
        openClod.addWord(getString(R.string.cool_mode));
        openClod.addWord(getString(R.string.cool_mode1));
        openClod.setIdentify(mFragmentHashCode + "-" + VEHICLECONTROL_OPEN_SMART_CLOD);
        defaultElementItems.add(openClod);

        UIControlElementItem openRain = new UIControlElementItem();
        openRain.addWord(getString(R.string.wakeup41));
        openRain.addWord(getString(R.string.wakeup42));
        openRain.addWord(getString(R.string.wakeup42));
        openRain.addWord(getString(R.string.rain_mode));
        openRain.addWord(getString(R.string.rain_mode1));
        openRain.setIdentify(mFragmentHashCode + "-" + VEHICLECONTROL_OPEN_SMART_RAIN);
        defaultElementItems.add(openRain);

        UIControlElementItem openSmok = new UIControlElementItem();
        openSmok.addWord(getString(R.string.wakeup43));
        openSmok.addWord(getString(R.string.wakeup44));
        openSmok.setIdentify(mFragmentHashCode + "-" + VEHICLECONTROL_OPEN_SMART_SMOK);
        defaultElementItems.add(openSmok);

        UIControlElementItem playMedia = new UIControlElementItem();
        playMedia.addWord(getString(R.string.play_media_start));
        playMedia.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PLAY_MEDIA_START);
        defaultElementItems.add(playMedia);

        UIControlElementItem pauseMedia = new UIControlElementItem();
        pauseMedia.addWord(getString(R.string.play_media_pause));
        pauseMedia.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PLAY_MEDIA_PAUSE);
        defaultElementItems.add(pauseMedia);

    }

    public void addElementAndListContent() {
        UIControlMgr.getInstance().addElementAndListContent(mUIControlElements, mUiControlItems);
        UIControlMgr.getInstance().updateContentToAsr();
        UIControlMgr.getInstance().registerUIControlCallback(this, mFragmentHashCode);
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onDetach");
        super.onDetach();
        UIControlMgr.getInstance().releaseResource(mFragmentHashCode);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onSelectCancel() {
        getActivity().onBackPressed();
    }

    @Override
    public void onSelectBackHome(String action) {
//        ((MainActivity) getActivity()).mCurrentFragment = LocalFragmentManager.getInstance().
//                createMainFragment(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onPhoneCallAnswer(String action) {
        CallUtils.answerCall(getContext());
    }

    @Override
    public void onPhoneCallCancel(String action) {
        CallUtils.rejectCall();
    }

    @Override
    public void onSelectItemPosition(int position) {

    }

    @Override
    public void onMediaPlay(String action) {
//        IntegrationCore.getIntergrationCore(getActivity()).handlePlay();
    }

    @Override
    public void onMediaPause(String action) {
//        IntegrationCore.getIntergrationCore(getActivity()).handlePause();
    }

    @Override
    public void onSelectOtherOC(String action) {
        Log.i(TAG, "onSelectOtherOC action = " + action);
        switch (action) {
            case VEHICLECONTROL_FIND_CAR:
                VehcleControlManager.getInstance(getContext()).findCar(null);
                break;
            case VEHICLECONTROL_WORM_CAR:
                VehcleControlManager.getInstance(getContext()).oneSmartControl(VehcleControlManager.VEHCLECONTROL_WORM_CAR, null);
                break;
            case VEHICLECONTROL_OPEN_SMART_WORM:
                VehcleControlManager.getInstance(getContext()).oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_WORM, null);

                break;
            case VEHICLECONTROL_OPEN_SMART_CLOD:
                VehcleControlManager.getInstance(getContext()).oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_CLOD, null);

                break;
            case VEHICLECONTROL_OPEN_SMART_RAIN:
                VehcleControlManager.getInstance(getContext()).oneSmartControl(VehcleControlManager.VEHCLECONTROL_RAIN_MODE, null);

                break;
            case VEHICLECONTROL_OPEN_SMART_SMOK:
                VehcleControlManager.getInstance(getContext()).oneSmartControl(VehcleControlManager.VEHCLECONTROL_SMOK_MODE, null);

                break;
            default:
                break;
        }
    }
}
