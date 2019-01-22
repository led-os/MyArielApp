package com.qinggan.app.arielapp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.utils.AppManager;
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
 * <UIControlActivity 基类>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-11]
 * @see [相关类/方法]
 * @since [V1]
 */
public abstract class UIControlBaseActivity extends BaseActivity implements UIControlCallback {

    public List<UIControlElementItem> defaultElementItems = new ArrayList<>();

    //设置UIControl,url的头,防止界面切换后还能回调到
    protected int mFragmentHashCode;
    protected List<UIControlElementItem> mUIControlElements = new ArrayList<>();
    protected List<UIControlItem> mUiControlItems = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentHashCode = this.hashCode();
        initDefaultElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIControlMgr.getInstance().addElementContent(mUIControlElements);
        UIControlMgr.getInstance().addListContent(mUiControlItems);
        UIControlMgr.getInstance().updateContentToAsr();
        UIControlMgr.getInstance().registerUIControlCallback(this, mFragmentHashCode);
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

        UIControlElementItem backHome = new UIControlElementItem();
        backHome.addWord(getString(R.string.back_to_home));
        backHome.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.BACK_TO_HOME);
        defaultElementItems.add(backHome);

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

        UIControlElementItem findCar = new UIControlElementItem();
        findCar.addWord(getString(R.string.wakeup35));
        findCar.setIdentify(mFragmentHashCode + "-" + VEHICLECONTROL_FIND_CAR);
        defaultElementItems.add(findCar);

        UIControlElementItem wormCar = new UIControlElementItem();
        wormCar.addWord(getString(R.string.wakeup36));
        wormCar.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_WORM_CAR);
        defaultElementItems.add(wormCar);

        UIControlElementItem openWarm = new UIControlElementItem();
        openWarm.addWord(getString(R.string.wakeup37));
        openWarm.addWord(getString(R.string.wakeup38));
        openWarm.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_WORM);
        defaultElementItems.add(openWarm);

        UIControlElementItem openClod = new UIControlElementItem();
        openClod.addWord(getString(R.string.wakeup39));
        openClod.addWord(getString(R.string.wakeup40));
        openClod.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_CLOD);
        defaultElementItems.add(openClod);

        UIControlElementItem openRain = new UIControlElementItem();
        openRain.addWord(getString(R.string.wakeup41));
        openRain.addWord(getString(R.string.wakeup42));
        openRain.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_RAIN);
        defaultElementItems.add(openRain);

        UIControlElementItem openSmok = new UIControlElementItem();
        openSmok.addWord(getString(R.string.wakeup43));
        openSmok.addWord(getString(R.string.wakeup44));
        openSmok.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_SMOK);
        defaultElementItems.add(openSmok);

        UIControlElementItem playMedia = new UIControlElementItem();
        playMedia.addWord(getString(R.string.play_media_start));
        playMedia.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PLAY_MEDIA_START);
        defaultElementItems.add(playMedia);

        UIControlElementItem pauseMedia = new UIControlElementItem();
        pauseMedia.addWord(getString(R.string.play_media_pause));
        pauseMedia.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PLAY_MEDIA_PAUSE);
        defaultElementItems.add(pauseMedia);

        mUIControlElements.addAll(defaultElementItems);


    }

    public void addElementAndListContent() {
        UIControlMgr.getInstance().addElementAndListContent(mUIControlElements, mUiControlItems);
        UIControlMgr.getInstance().updateContentToAsr();
        UIControlMgr.getInstance().registerUIControlCallback(this, mFragmentHashCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIControlMgr.getInstance().releaseResource(mFragmentHashCode);
    }

    @Override
    public void onSelectCancel() {
        onBackPressed();
    }

    @Override
    public void onSelectBackHome(String action) {
        AppManager.getAppManager().returnToActivity(MainActivity.class);
//        ((MainActivity) getActivity()).mCurrentFragment = LocalFragmentManager.getInstance().
//                createMainFragment(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onPhoneCallAnswer(String action) {
        CallUtils.answerCall(this);
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
//        IntegrationCore.getIntergrationCore(this).handlePlay();
    }

    @Override
    public void onMediaPause(String action) {
//        IntegrationCore.getIntergrationCore(this).handlePause();
    }

    @Override
    public void onSelectOtherOC(String action) {
        switch (action){
            case VEHICLECONTROL_FIND_CAR:
                VehcleControlManager.getInstance(mContext).findCar(null);
                break;
            case VEHICLECONTROL_WORM_CAR:
                VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_WORM_CAR,null);
                break;
            case VEHICLECONTROL_OPEN_SMART_WORM:
                VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_WORM,null);

                break;
            case VEHICLECONTROL_OPEN_SMART_CLOD:
                VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_CLOD,null);

                break;
            case VEHICLECONTROL_OPEN_SMART_RAIN:
                VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_RAIN_MODE,null);

                break;
            case VEHICLECONTROL_OPEN_SMART_SMOK:
                VehcleControlManager.getInstance(mContext).oneSmartControl(VehcleControlManager.VEHCLECONTROL_SMOK_MODE,null);

                break;
                default:
                    break;
        }


    }
}
