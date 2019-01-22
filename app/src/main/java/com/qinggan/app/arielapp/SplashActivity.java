package com.qinggan.app.arielapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qinggan.app.arielapp.capability.push.PushManager;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.wechat.utils.WechatConstants;
import com.qinggan.app.arielapp.ui.bluekey.BleKeyHelper;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.mobile.tsp.bean.BaseBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.device.DeviceOnlineStatusInfo;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import rx.functions.Action1;

import static com.qinggan.app.arielapp.minor.utils.Constants.USERINFO;

/**
 * Created by Yorashe on 18-12-5.
 */

public class SplashActivity extends BaseActivity {
    @BindView(R.id.splash_lay)
    LinearLayout splashLay;
    private boolean needCheck;
    private SharedPreferences pref;
    private TextView mStartBtn;
    private View mPremissView;
    private final String HAS_CHECK_PERMISSIONS = "HAS_CHECK_PERMISSIONS";
    private boolean isPermissionsChecked;


    @Override
    protected void initView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needCheck)
            checkPermissions();
    }

    @Override
    protected void initData() {
        pref = this.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
        isPermissionsChecked = pref.getBoolean(HAS_CHECK_PERMISSIONS, false);
        if (!isPermissionsChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPermissView();
        } else {
            checkPermissions();
        }
        loadOldData();

    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.splash_lay;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(null != intent){
            String action = intent.getAction();
            if("android.hardware.usb.action.USB_ACCESSORY_ATTACHE".equals(action)){
                com.qinggan.qinglink.api.md.USBProxy.connect(this, intent);
            }
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            checkPermissionsFor23();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkPermissionsFor26();
        } else {
            setPermissionsChecked();

        }
    }

    private void checkPermissionsFor23() {
        needCheck = false;
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        setPermissionSuccess(aBoolean);

                    }
                });
    }

    private void checkPermissionsFor26() {
        needCheck = false;
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION

        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        setPermissionSuccess(aBoolean);
                    }
                });
    }




    private void startMain() {
        PushManager.getInstance();
        startActivity(MainActivity.class, null);
        finish();
    }

    private void loadOldData() {
        //测试写死1号车pdsn
//        TspManager.getInstance(mContext).setmPdsn(TspManager.mPdsn1);
        String user = pref.getString(USERINFO, "");
        if (!TextUtils.isEmpty(user)) {
            try {
                UserInfo userInfo = new Gson().fromJson(user, UserInfo.class);
                TspManager.getInstance(this).setGlobalToken(userInfo.getAuthLoginResponseItem());
                ArielApplication.setmUserInfo(userInfo);
                wakeUpTbox();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void showPermissView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPremissView = getLayoutInflater().inflate(R.layout.permiss_lay, null, false);
        addContentView(mPremissView, params);
        mStartBtn = (TextView) findViewById(R.id.start_btn);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
    }

    private void setPermissionsChecked() {
        SharedPreferences.Editor editor = pref.edit();
        //存入数据
        editor.putBoolean(HAS_CHECK_PERMISSIONS, true);
        //提交修改
        editor.commit();
        if (mPremissView != null) {
            mPremissView.setVisibility(View.GONE);
        }
        splashLay.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMain();
            }
        }, 2000);
    }

    private void setPermissionSuccess(boolean isSuccess) {
        if (isSuccess) {
            if (!isPermissionsChecked) {
                setPermissionsChecked();
            } else {
                startMain();
            }
        } else {
            if (isPermissionsChecked) {
                needCheck = true;
            }
        }

    }


    private void wakeUpTbox(){
        if (!TextUtils.isEmpty(TspManager.getmPdsn()))
            TspManager.getInstance(mContext).getOnlineStatusByDeviceIds(new RestCallback<DeviceOnlineStatusInfo>() {
                @Override
                public void success(DeviceOnlineStatusInfo arg0, RestResponse response) {
                    try {
                        if (arg0.getDataList().get(0).isOnline()){
                            TspManager.getInstance(mContext).wakeupDevice(new RestCallback<BaseBean>() {
                                @Override
                                public void success(BaseBean arg0, RestResponse response) {

                                }

                                @Override
                                public void failure(RestError restError) {

                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failure(RestError restError) {

                }
            });
    }

}
