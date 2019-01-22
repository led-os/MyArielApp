package com.qinggan.app.arielapp.vehiclecontrol;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.AirCleanerEventsListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.AirConditionerEventsListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.AtmosphereLightEventListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.CarStatusListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.DemistEventsListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.DoorEventListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.DoorMirrorEventsListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.FrostActionListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.LightEventListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.SeatEventsListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.SmartModeEventListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.SunRoofEventListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.TrunkEventListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.WindowEventListener;
import com.qinggan.app.arielapp.vehiclecontrol.Listener.WiperEventsListener;
import com.qinggan.app.voiceapi.control.car.AirCleanerController;
import com.qinggan.app.voiceapi.control.car.AirConditionerController;
import com.qinggan.app.voiceapi.control.car.AtmosphereLightController;
import com.qinggan.app.voiceapi.control.car.CarStatusController;
import com.qinggan.app.voiceapi.control.car.DemistController;
import com.qinggan.app.voiceapi.control.car.DoorController;
import com.qinggan.app.voiceapi.control.car.FrostController;
import com.qinggan.app.voiceapi.control.car.LightController;
import com.qinggan.app.voiceapi.control.car.MirrorController;
import com.qinggan.app.voiceapi.control.car.SeatController;
import com.qinggan.app.voiceapi.control.car.SmartModeController;
import com.qinggan.app.voiceapi.control.car.SunRoofController;
import com.qinggan.app.voiceapi.control.car.TrunkController;
import com.qinggan.app.voiceapi.control.car.WindowController;
import com.qinggan.app.voiceapi.control.car.WiperController;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.CanBusListener;
import com.qinggan.qinglink.api.md.CanBusManager;
import com.qinggan.qinglink.bean.AirCondition;

/**
 * Created by Yorashe on 18-11-13.
 */

public class VoiceVehicleControl {

    public static final String TAG = VoiceVehicleControl.class.getSimpleName();
    private NluResultManager mControlManager;
//    private CanBusManager mCanBusManager;
    private AirConditionerController mAirConditionerController;
    private AirCleanerController mAirCleanerController;
    private DoorController mDoorController;
    private MirrorController mDoorMirrorController;
    private WindowController mWindowController;
    private LightController mLightController;
    private AtmosphereLightController mAtmosphereLightController;
    private TrunkController mTrunkController;
    private CarStatusController mCarStatusController;
    private DemistController mDemistController;
    private FrostController mfrostController;
    private SeatController mSeatController;
    private SunRoofController mSunRoofController;
    private WiperController mWiperController;
    private SmartModeController mSmartModeController;
    private Handler mHandler = new Handler();
    private Context mContext;
    private CanBusManager sCanBusManager;

    private CanBusListener mCanBusListener = new CanBusListener() {
        @Override
        public void onAirConditionChanged(final AirCondition airConditionData){
            super.onAirConditionChanged(airConditionData);
            Log.e(TAG, "onAirConditionChanged");
        }

    };

        public VoiceVehicleControl(Context mContext) {
        initEventsListener(mContext);
    }

    private void initEventsListener(Context mContext){
        this.mContext=mContext;
        sCanBusManager = ArielApplication.getCanBusManager();
        ArielApplication.addCanBusListener(mCanBusListener);
        mControlManager =  NluResultManager.getInstance();

        mAirConditionerController = new AirConditionerController();
        mAirConditionerController.setEventListener(new AirConditionerEventsListener(mContext,sCanBusManager));
        mControlManager.addCarController(AirConditionerController.CLASSIFY,
                mAirConditionerController);

        mAirCleanerController = new AirCleanerController();
        mAirCleanerController.setEventListener(new AirCleanerEventsListener(mContext));
        mControlManager.addCarController(AirCleanerController.CLASSIFY, mAirCleanerController);


        mDoorController = new DoorController();
        mDoorController.setEventListener(new DoorEventListener(mContext));
        mControlManager.addCarController(DoorController.CLASSIFY, mDoorController);

        mDoorMirrorController = new MirrorController();
        mDoorMirrorController.setEventListener(new DoorMirrorEventsListener(mContext));
        mControlManager.addCarController(MirrorController.CLASSIFY, mDoorMirrorController);

        mWindowController = new WindowController();
        mWindowController.setEventListener(new WindowEventListener(mContext));
        mControlManager.addCarController(WindowController.CLASSIFY, mWindowController);

        mLightController = new LightController();
        mLightController.setEventListener(new LightEventListener(mContext));
        mControlManager.addCarController(LightController.CLASSIFY, mLightController);
        mAtmosphereLightController = new AtmosphereLightController();
        mAtmosphereLightController.setEventListener(new AtmosphereLightEventListener(mContext));
        mControlManager.addCarController(AtmosphereLightController.CLASSIFY,
                mAtmosphereLightController);


        mTrunkController = new TrunkController();
        mTrunkController.setEventListener(new TrunkEventListener(mContext));
        mControlManager.addCarController(TrunkController.CLASSIFY, mTrunkController);

        mCarStatusController = new CarStatusController();
        mCarStatusController.setEventListener(new CarStatusListener(mContext));
        mControlManager.addCarController(CarStatusController.CLASSIFY, mCarStatusController);

        mDemistController = new DemistController();
        mDemistController.setEventListener(new DemistEventsListener(mContext));
        mControlManager.addCarController(DemistController.CLASSIFY, mDemistController);

        mfrostController = new FrostController();
        mfrostController.setEventListener(new FrostActionListener(mContext));
        mControlManager.addCarController(FrostController.CLASSIFY, mfrostController);

        mSeatController = new SeatController();
        mSeatController.setEventListener(new SeatEventsListener(mContext));
        mControlManager.addCarController(SeatController.CLASSIFY, mSeatController);


        mSunRoofController = new SunRoofController();
        mSunRoofController.setEventListener(new SunRoofEventListener(mContext));
        mControlManager.addCarController(SunRoofController.CLASSIFY, mSunRoofController);

        mWiperController = new WiperController();
        mWiperController.setEventListener(new WiperEventsListener(mContext,sCanBusManager));
        mControlManager.addCarController(WiperController.CLASSIFY, mWiperController);

        mSmartModeController = new SmartModeController();
        mSmartModeController.setEventListener(new SmartModeEventListener(mContext,sCanBusManager));
        mControlManager.addCarController(SmartModeController.CLASSIFY, mSmartModeController);
    }


}
