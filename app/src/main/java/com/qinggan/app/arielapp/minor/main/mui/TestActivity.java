package com.qinggan.app.arielapp.minor.main.mui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BKMusicActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.TestAarActivity;
import com.qinggan.app.arielapp.WheelControl.WheelControl;
import com.qinggan.app.arielapp.WheelControl.view.WheelViewManager;
import com.qinggan.app.arielapp.capability.auth.Certification;
import com.qinggan.app.arielapp.capability.upgrade.ota.OTAManager;
import com.qinggan.app.arielapp.capability.upgrade.ota.OTAUpgradeListener;
import com.qinggan.app.arielapp.capability.upgrade.tspota.TspOtaManager;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.capability.volume.ArielVolumeManager;
import com.qinggan.app.arielapp.minor.commonui.FullScreenDialog;
import com.qinggan.app.arielapp.minor.main.mui.huitest.TestHuiActivity;
import com.qinggan.app.arielapp.minor.main.mui.navitest.TestNaviActivity;
import com.qinggan.app.arielapp.minor.main.mui.vehicletest.VehicleTestActivity;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.minor.phone.service.ArielPhoneService;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneMainActivity;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.ui.pin.add.AddPinActivity;
import com.qinggan.app.arielapp.ui.pin.check.VerfyPinActivity;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.service.BlueKeyService;
import com.qinggan.bluekey.util.LogcatHelper;
import com.qinggan.qinglink.api.Constant;
import com.staryea.ui.CerStepFourActivity;

import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_DOWN;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_HANGUP;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_ICALL;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_IVOKA;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_SRC;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_UP;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_VOLUME_DOWN;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_VOLUME_UP;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, PhoneStateManager.PhoneStateChangeListener {

    private boolean micOn = false;
    private Button cellPhoneMicOn;
    private Button connectedToCarBtn;
    private Button car_lock_btn;
    private Button nav_btn;
    private Button weixin_btn;
    private Button phone_btn;
    private Button radio_btn;
    private Button hui_btn;
    private Button fangkong_btn;
    private Button car_btn;
    private Button add_mac_btn;
    private Button key51_btn;
    private EditText mac_text;
    private Button ble_lock;
    private Button ble_unlock;
    private Button ble_back;
    private Button ble_good_rssi;
    private Button ble_bad_rssi;
    private Button btn_log;

//    private WheelControl wheelControl;
    private Button leav_car;
    private Button musicTest;
    private LocalStorageTools localStorageTools = null;
    private TextView upgradeState, currentOtaVersion;

    private Handler handler = new Handler();
    private Button btn_car_control;
    private boolean isCarControl;
    private CheckBox startEngine;
    private EditText voiceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
//        if (wheelControl == null) {
//            wheelControl = new WheelControl(ArielApplication.getApp().getContext());
//        }
        Certification.registAuthResultListener(new CerStepFourActivity.OnAuthrizeResultListener() {
            @Override
            public void onAuthrizeSuccessed(String s) {
                ToastUtil.show("实名认证成功", TestActivity.this);
            }

            @Override
            public void onAuthrizeFailed() {
                ToastUtil.show("实名认证失败", TestActivity.this);
            }
        });
    }

    private void initView() {

        voiceEditText = findViewById(R.id.voice_increase);
        voiceEditText.setText(ArielVolumeManager.VEHICLE_VOICE_INCREASE + "");
        voiceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(voiceEditText.getText().toString()))
                    return;
                try {
                    ArielVolumeManager.VEHICLE_VOICE_INCREASE = Integer.parseInt(voiceEditText.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        localStorageTools = new LocalStorageTools(this);
        PhoneStateManager phoneStateManager = PhoneStateManager.getInstance(this);
        phoneStateManager.setConnectionToCarStateChangeListener(new PhoneStateManager.ConnectionToCarStateChangeListener() {
            @Override
            public void onConnectionChanged(boolean connected) {
                connectedToCarBtn.setText(connected ? "与车机MIC已连接" : "与车机MIC未连接");
            }
        });
        phoneStateManager.addPhoneStateChangeListener(this);
        connectedToCarBtn = findViewById(R.id.mic_connected);
        connectedToCarBtn.setText(phoneStateManager.isConnectedToCar() ? "与车机MIC已连接" : "与车机MIC未连接");
        cellPhoneMicOn = findViewById(R.id.mic_allway_on);
        cellPhoneMicOn.setOnClickListener(this);
        car_lock_btn = (Button) findViewById(R.id.car_lock_btn);
        car_lock_btn.setOnClickListener(this);

        nav_btn = (Button) findViewById(R.id.nav_btn);
        nav_btn.setOnClickListener(this);

        musicTest = (Button) findViewById(R.id.music_text);
        musicTest.setOnClickListener(this);

        phone_btn = (Button) findViewById(R.id.phone_btn);
        phone_btn.setOnClickListener(this);

        weixin_btn = (Button) findViewById(R.id.weixin_btn);
        weixin_btn.setOnClickListener(this);

        radio_btn = (Button) findViewById(R.id.radio_btn);
        radio_btn.setOnClickListener(this);

        hui_btn = (Button) findViewById(R.id.hui_btn);
        hui_btn.setOnClickListener(this);

        fangkong_btn = (Button) findViewById(R.id.fangkong_btn);
        fangkong_btn.setOnClickListener(this);

        car_btn = (Button) findViewById(R.id.car_btn);
        car_btn.setOnClickListener(this);

        add_mac_btn = (Button) findViewById(R.id.add_mac_btn);
        add_mac_btn.setOnClickListener(this);
        mac_text = (EditText) findViewById(R.id.mac_text);

        ble_lock = (Button) findViewById(R.id.ble_lock);
        ble_lock.setOnClickListener(this);

        ble_unlock = (Button) findViewById(R.id.ble_unlock);
        ble_unlock.setOnClickListener(this);

        ble_good_rssi = (Button) findViewById(R.id.ble_good_rssi);
        ble_good_rssi.setOnClickListener(this);

        ble_bad_rssi = (Button) findViewById(R.id.ble_bad_rssi);
        ble_bad_rssi.setOnClickListener(this);
        btn_log = (Button) findViewById(R.id.btn_log);
        btn_log.setOnClickListener(this);


        leav_car = findViewById(R.id.btn_leavecar_navi);
        leav_car.setOnClickListener(this);

        ble_back = (Button) findViewById(R.id.ble_back);
        ble_back.setOnClickListener(this);


        btn_car_control = (Button) findViewById(R.id.btn_car_control);
        btn_car_control.setOnClickListener(this);
        isCarControl = localStorageTools.getBoolean("isCarControl");
        if (isCarControl) {
            btn_car_control.setText(getString(R.string.off_car_control));
        } else {
            btn_car_control.setText(getString(R.string.on_car_control));

        }

        startEngine = findViewById(R.id.start_car);
        startEngine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constants.canMonitorStartEngine = isChecked;
            }
        });
        upgradeState = findViewById(R.id.ota8);
        currentOtaVersion = findViewById(R.id.ota7);
        findViewById(R.id.add_pin).setOnClickListener(this);
        findViewById(R.id.change_pin).setOnClickListener(this);
        findViewById(R.id.vefi_pin).setOnClickListener(this);

        findViewById(R.id.ota1).setOnClickListener(this);
        findViewById(R.id.ota2).setOnClickListener(this);
        findViewById(R.id.ota3).setOnClickListener(this);
        findViewById(R.id.ota4).setOnClickListener(this);
        findViewById(R.id.ota5).setOnClickListener(this);
        findViewById(R.id.ota6).setOnClickListener(this);
        findViewById(R.id.ota9).setOnClickListener(this);
        if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState() == PhoneState.IN_CAR_MODE) {
            leav_car.setText(getString(R.string.current_in_car));
        } else if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            leav_car.setText(getString(R.string.current_leav_car));
        }

        //ota listener
        OTAManager.getInstance().registOTAUpgradeListener(new OTAUpgradeListener() {
            @Override
            public void onCurrentVersion(final String mpuVersion, final String mcuVersion) {
                Log.d("Test-OTAManager", "onCurrentVersion mpuVersion:" + mpuVersion + ",mcuVersion:" + mcuVersion);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentOtaVersion.setText("当前版本:mpuVersion:" + mpuVersion + "\n mcuVersion:" + mcuVersion);
                        OTAManager.getInstance().startUpgrade();
                    }
                });
            }

            @Override
            public void onUpgradeNoVersion() {
                Log.d("Test-OTAManager", "onUpgradeNoVersion");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        upgradeState.setText("没有新版本");
                    }
                });
            }

            @Override
            public void onNewVersion(boolean fromUdisk, String mpuVersion, String mcuVersion, String description) {
                Log.d("Test-OTAManager", "onNewVersion fromUdisk:" + fromUdisk + ",mpuVersion:" + mpuVersion + ",mcuVersion:" + mcuVersion + ",description:" + description);
                if (fromUdisk) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            upgradeState.setText("U盘有新的版本,马上升级");
                            if (OTAManager.getInstance().isRfcommConnect())
                                OTAManager.getInstance().startUpgrade();
                            else {
                                upgradeState.setText("有新版本,但是rfcomm not connect");
                            }
                        }
                    });
                }
            }

            @Override
            public void onUpgrading(final boolean downloading, final int current, int total) {
                Log.d("Test-OTAManager", "onUpgrading downloading:" + downloading + ",current:" + current + ",total:" + total);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        upgradeState.setText(downloading ? ("正在下载" + current) : "正在升级:" + current);
                    }
                });
            }

            @Override
            public void onError(final int errorType, final String errorMsg) {
                Log.d("Test-OTAManager", "onError:errorType:" + errorType + ",errorMsg:" + errorMsg);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        upgradeState.setText("error:errorType:" + errorType + ",errorMsg:" + errorMsg);
                    }
                });
            }

            @Override
            public void onUpgradeStateChanged(int state) {
                Log.d("Test-OTAManager", "onUpgradeStateChanged:" + state);
                if (state == Constant.SystemEvent.State.STATE_REBOOTING) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            upgradeState.setText("车机重启!!!");
                        }
                    });
                }
            }
        });
    }

    public static boolean isFileLogEnabled() {
        SharedPreferences sp = ArielApplication.getApp().getContext().getSharedPreferences("log", Context.MODE_PRIVATE);
        boolean isEnable = sp.getBoolean("log_switch", false);
        return isEnable;
    }

    public static void setFileLogEnable(boolean isEnable) {
        SharedPreferences.Editor edit = ArielApplication.getApp().getContext().getSharedPreferences("log", Context.MODE_PRIVATE).edit();
        edit.putBoolean("log_switch", isEnable);
        edit.commit();
    }

    public static void startLog() {
        LogcatHelper.getInstance(ArielApplication.getApp().getContext(), "app", "*:e *:d", "").start();
    }

    public static void stopLog() {
        if (LogcatHelper.getInstance() != null) {
            LogcatHelper.getInstance().stop();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_log:
                if (isFileLogEnabled()) {
                    setFileLogEnable(false);
                    Toast.makeText(TestActivity.this, "文件LOG已关闭", Toast.LENGTH_SHORT).show();
                    stopLog();

                } else {
                    setFileLogEnable(true);
                    Toast.makeText(TestActivity.this, "文件LOG已打开", Toast.LENGTH_SHORT).show();
                    startLog();
                }
                break;
            case R.id.mic_allway_on:
                if (micOn) {
                    cellPhoneMicOn.setText("应用内离车模式手机MIC可用");
                    VoicePolicyManage.getInstance().keepMicOn(false);
                    micOn = false;
                } else {
                    cellPhoneMicOn.setText("手机MIC一直可用");
                    VoicePolicyManage.getInstance().keepMicOn(true);
                    micOn = true;
                }
                break;
            case R.id.car_lock_btn:
                Intent intent = new Intent(this, FullScreenDialog.class);
                intent.putExtra(Constants.KEY_DIALOG_CONTENT, Constants.TYPE_CAR_LOCK);
                startActivity(intent);
                break;
            case R.id.nav_btn:
                startActivity(new Intent(this, TestNaviActivity.class));
                break;
            case R.id.phone_btn:
                ToastUtil.show("进入电话功能测试", this);
                Intent arielPhoneService = new Intent(this, ArielPhoneService.class);
                startService(arielPhoneService);
                Intent phoneMainActivity = new Intent(this, PhoneMainActivity.class);
                startActivity(phoneMainActivity);
                break;
            case R.id.music_text:
                Intent musciIntent = new Intent(this, BKMusicActivity.class);
                startActivity(musciIntent);
                break;
            case R.id.weixin_btn:
                ToastUtil.show("进入微信功能测试", this);
                Intent mIntent = new Intent(this, TestAarActivity.class);
                startActivity(mIntent);
                break;
            case R.id.radio_btn:
                ToastUtil.show("进入收音机功能测试", this);
                break;
            case R.id.hui_btn:
                startActivity(new Intent(this, TestHuiActivity.class));
                break;
            case R.id.fangkong_btn:
                if (i == 0) {
                    ToastUtil.show("进入方控功能测试", this);
                }
                gotoFangkong();
                break;
            case R.id.car_btn:
                ToastUtil.show("进入车控功能测试", this);
                startActivity(new Intent(this, VehicleTestActivity.class));
                break;
            case R.id.add_mac_btn:
                if (TextUtils.isEmpty(mac_text.getText())) {
                    Toast.makeText(TestActivity.this, "请输入蓝牙地址", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    SharedPreferences.Editor edit = getSharedPreferences("save_key", Context.MODE_PRIVATE).edit();
                    edit.putString("mac_id", mac_text.getText().toString());
                    edit.commit();
//                android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
                break;
            case R.id.ble_lock:
                if (BleKeyManager.getInstance(TestActivity.this).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
                    BleKeyManager.getInstance(TestActivity.this).setDoor(1);
                } else {
                    ToastUtil.show("蓝牙钥匙还未连接通过认证", this);
                }

                break;
            case R.id.ble_unlock:
                if (BleKeyManager.getInstance(TestActivity.this).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
                    BleKeyManager.getInstance(TestActivity.this).setDoor(0);
                } else {
                    ToastUtil.show("蓝牙钥匙还未连接通过认证", this);
                }
                break;
            case R.id.ble_back:
                if (BleKeyManager.getInstance(TestActivity.this).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
                    BleKeyManager.getInstance(TestActivity.this).setTrunk(1);
                } else {
                    ToastUtil.show("蓝牙钥匙还未连接通过认证", this);
                }
                break;
            case R.id.add_pin:
                Intent intent1 = new Intent(this, AddPinActivity.class);
                intent1.putExtra("type", AddPinActivity.ADD_CODE);
                startActivity(intent1);
                break;
            case R.id.change_pin:
                Intent intent2 = new Intent(this, AddPinActivity.class);
                intent2.putExtra("type", AddPinActivity.UPDATE_CODE);
                startActivity(intent2);
                break;
            case R.id.vefi_pin:
                Intent intent3 = new Intent(this, VerfyPinActivity.class);
                startActivityForResult(intent3, 100);
                break;
            case R.id.cerfi:
                Certification.goToAuth(this);
                break;
            case R.id.ota1:
                if (null != ArielApplication.getmUserInfo())
                    TspOtaManager.getInstance().getCurrentVersion(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getTpdsn());
                else ToastUtil.show("not login", this);
                break;
            case R.id.ota2:
                if (null != ArielApplication.getmUserInfo())
                    TspOtaManager.getInstance().checkHasNewVersion(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getTpdsn());
                else ToastUtil.show("not login", this);
                break;
            case R.id.ota3:
                if (null != ArielApplication.getmUserInfo())
                    TspOtaManager.getInstance().checkCurrentStatus(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getTpdsn());
                else ToastUtil.show("not login", this);
                break;
            case R.id.ota4:
                if (null != ArielApplication.getmUserInfo())
                    TspOtaManager.getInstance().startUpgrade(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getTpdsn(), "1");
                else ToastUtil.show("not login", this);
                break;
            case R.id.ota5:
                if (null != ArielApplication.getmUserInfo())
                    TspOtaManager.getInstance().startUpgrade(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getTpdsn(), "2");
                else ToastUtil.show("not login", this);
                break;
            case R.id.ota6:
                if (OTAManager.getInstance().isRfcommConnect())
//                    OTAManager.getInstance().checkNewVersion();
                    OTAManager.getInstance().startTestUpgrade();
                else {
                    upgradeState.setText("rfcomm not connect");
                }
                break;
            case R.id.ota9:
                if (OTAManager.getInstance().isRfcommConnect())
                    OTAManager.getInstance().getCurrentVersion();
                else {
                    currentOtaVersion.setText("rfcomm not connect");
                }
                break;
            case R.id.btn_car_control:
                isCarControl = localStorageTools.getBoolean("isCarControl");
                if (isCarControl) {
                    localStorageTools.setBoolean("isCarControl", false);
                    btn_car_control.setText(getString(R.string.on_car_control));
                } else {
                    localStorageTools.setBoolean("isCarControl", true);
                    btn_car_control.setText(getString(R.string.off_car_control));
                }

                break;

            case R.id.btn_leavecar_navi:
                if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState() == PhoneState.IN_CAR_MODE) {
                    PhoneStateManager.getInstance(ArielApplication.getApp()).setPhoneState(PhoneState.OUT_CAR_MODE);
                    leav_car.setText(getString(R.string.current_leav_car));
                } else if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState() == PhoneState.OUT_CAR_MODE) {
                    PhoneStateManager.getInstance(ArielApplication.getApp()).setPhoneState(PhoneState.IN_CAR_MODE);
                    leav_car.setText(getString(R.string.current_in_car));
                }

            case R.id.ble_good_rssi: {
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                try {
                    int rssi = BleKeyManager.getInstance(this).getNearRssi();
                    et.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(50, 10, 50, 10);//4个参数按顺序分别是左上右下
                    et.setLayoutParams(layoutParams);

                    et.setText("" + rssi);

                    new AlertDialog.Builder(this)
                            .setTitle("蓝牙钥匙近车RSSI阈值")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(et)
                            .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        BleKeyManager.getInstance(TestActivity.this)
                                                .setNearRssi(Integer.parseInt(et.getEditableText().toString()));
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.ble_bad_rssi: {
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                try {
                    final int rssi = BleKeyManager.getInstance(this).getRemoteRssi();
                    et.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(50, 10, 50, 10);//4个参数按顺序分别是左上右下
                    et.setLayoutParams(layoutParams);
                    et.setText("" + rssi);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                new AlertDialog.Builder(this)
                        .setTitle("蓝牙钥匙离车RSSI阈值")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    BleKeyManager.getInstance(TestActivity.this)
                                            .setRemoteRssi(Integer.parseInt(et.getEditableText().toString()));
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
            break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && requestCode == 100) {
            ToastUtil.show("验证安全码成功", this);
        }
    }

    @Override
    protected void onDestroy() {
        OTAManager.getInstance().unRegistOTAUpgradeListener();
        super.onDestroy();
//        if (wheelControl != null) {
//            wheelControl.destroyBroadcast();
//        }
        Certification.unRegistAuthResultListener();
    }

    int i = 0;

    //方控测试
    private void gotoFangkong() {
        if (!WheelViewManager.getInstance().getWheelShow()) WheelViewManager.getInstance().show();
        int j = KEYCODE_DOWN;
        int k = Constant.Button.KeyAction.KEY_DOWN;
        switch (i++ % 16) {
            case 0:
                j = KEYCODE_DOWN;
                k = Constant.Button.KeyAction.KEY_DOWN;
                break;
            case 1:
                j = KEYCODE_DOWN;
                k = Constant.Button.KeyAction.KEY_UP;
                break;
            case 2:
                j = KEYCODE_UP;
                k = Constant.Button.KeyAction.KEY_DOWN;
                break;
            case 3:
                j = KEYCODE_UP;
                k = Constant.Button.KeyAction.KEY_UP;
                break;
            case 4:

                j = KEYCODE_VOLUME_DOWN;
                k = Constant.Button.KeyAction.KEY_DOWN;
                break;
            case 5:
                j = KEYCODE_VOLUME_DOWN;
                k = Constant.Button.KeyAction.KEY_UP;
                break;
            case 6:
                j = KEYCODE_VOLUME_UP;
                k = Constant.Button.KeyAction.KEY_DOWN;
                break;
            case 7:
                j = KEYCODE_VOLUME_UP;
                k = Constant.Button.KeyAction.KEY_UP;
                break;
            case 8:
                j = KEYCODE_ICALL;
                k = Constant.Button.KeyAction.KEY_DOWN;
                break;
            case 9:
                j = KEYCODE_ICALL;
                k = Constant.Button.KeyAction.KEY_UP;
                break;
            case 10:
                j = KEYCODE_HANGUP;
                k = Constant.Button.KeyAction.KEY_DOWN;
                break;
            case 11:
                j = KEYCODE_HANGUP;
                k = Constant.Button.KeyAction.KEY_UP;
                break;
            case 12:
                j = KEYCODE_IVOKA;
                k = Constant.Button.KeyAction.KEY_DOWN;
                break;
            case 13:
                j = KEYCODE_IVOKA;
                k = Constant.Button.KeyAction.KEY_UP;
                break;
            case 14:
                j = KEYCODE_SRC;
                k = Constant.Button.KeyAction.KEY_DOWN;
                break;
            case 15:
                j = KEYCODE_SRC;
                k = Constant.Button.KeyAction.KEY_UP;
                break;
            default:
                break;
        }

//        WheelViewManager.getInstance().updateWheelView(j, k);

    }

    @Override
    public void onPhoneStateChange(PhoneState phoneState) {
        if (phoneState == PhoneState.OUT_CAR_MODE) {
            leav_car.setText(getString(R.string.current_leav_car));
        } else if (phoneState == PhoneState.IN_CAR_MODE) {
            leav_car.setText(getString(R.string.current_in_car));
        }

    }
}