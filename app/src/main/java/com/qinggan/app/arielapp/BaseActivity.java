package com.qinggan.app.arielapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.capability.volume.ArielVolumeManager;
import com.qinggan.app.arielapp.flyn.Eyes;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.navigation.BdMapUIcontrol;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.user.activity.LoginActivity;
import com.qinggan.app.arielapp.utils.RomUtils;
import com.qinggan.app.arielapp.utils.StatusBarCompat;
import com.qinggan.app.arielapp.utils.TokenUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.qinggan.app.arielapp.minor.utils.Constants.LOGIN_EVENT;

/**
 * Created by SuperSpf on 07/06/17.
 */
public abstract class BaseActivity extends SwipeBackActivity {
    protected String TAG = getClass().getSimpleName();
    public Context mContext;
    public TokenUtils mTokenUtils;
    private ProgressDialog mProgressDialog;

    private BaiduReceiver baiduReceiver;

    private PhoneStateManager.PhoneStateChangeListener mPhoneStateChangeListener = new PhoneStateManager.PhoneStateChangeListener() {
        @Override
        public void onPhoneStateChange(PhoneState phoneState) {
            if (PhoneState.IN_CAR_MODE == phoneState) {
                BaseActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Log.d(TAG, "addFlags  FLAG_KEEP_SCREEN_ON = " + this.getClass().getSimpleName());
            } else {
                BaseActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Log.d(TAG, "clearFlags FLAG_KEEP_SCREEN_ON = " + this.getClass().getSimpleName());
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(NavigationBarUtil.hasNavigationBar(this)){
//            NavigationBarUtil.initActivity(findViewById(android.R.id.content));
//        }
        Log.i("xiaohf", getLocalClassName() + "onCreate: s");
        mContext = this;
        mTokenUtils = TokenUtils.getInstance(this);
        doBeforeSetcontentView();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
        registerBaiduReceiver();
        mProgressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        Log.i("xiaohf", getLocalClassName() + "onCreate: e");

        PhoneStateManager.getInstance(this).addPhoneStateChangeListener(mPhoneStateChangeListener);
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    public abstract int getLayoutId();

    /**
     * 设置layout前配置
     */
    private void doBeforeSetcontentView() {
//        // 无标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarCompat.setTranslucentStatus(this, true);
        SetTranslanteBar();

        // 默认着色状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || RomUtils.getMiuiVersion() >= 6 || RomUtils.checkIsMeizuRom()) {
            Eyes.translucentStatusBar(this, true);
        } else {
            Eyes.translucentStatusBar(this, false);
        }
        mContext = this;

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        MobclickAgent.onResume(this);
        if (ArielApplication.getmUserInfo() != null) {
            if (mTokenUtils.needLogin()) {
                needLogin();
            }
        }

    }

    public void needLogin() {
        ArielApplication.setmUserInfo(null);
        EventBus.getDefault().post(new EventBusBean(LOGIN_EVENT,
                "1"
        ));
        LoginActivity.startAction(this);
        ToastUtil.show(R.string.need_login_hint, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaiduReceiver();
        PhoneStateManager.getInstance(this).removePhoneStateChangeListener(mPhoneStateChangeListener);
    }

    protected boolean showVoiceView() {
        return true;
    }

    /**
     * 沉浸状态栏（4.4以上系统有效）
     */
    protected void SetTranslanteBar() {
        StatusBarCompat.translucentStatusBar(this);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 2个按钮提示弹出框
     * title 标题
     * lefttext 左边按钮，righttext右边按钮文字
     * clickListener 按钮点击事件
     * 右边确定，左边取消
     */
    public void ShowAleryDialog(String title, String content, String lefttext, String righttext
            , DialogInterface.OnClickListener oklistener, DialogInterface.OnClickListener cancellistener) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(TextUtils.isEmpty(righttext) ? "确定" : righttext, oklistener)
                .setNegativeButton(TextUtils.isEmpty(lefttext) ? "取消" : lefttext, cancellistener).create();
        dialog.show();
//        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(BaseUtil.getEndColor_int());
//        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(BaseUtil.getEndColor_int());
    }

    public AlertDialog showDialogComm(String msg, DialogInterface.OnClickListener oklistener) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("提醒")
                .setMessage(msg)
                .setPositiveButton("确定", oklistener)
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
        return dialog;
    }

    /**
     * 开启浮动加载进度条
     */
    public void showProgressDialog() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /*
     * 开启浮动加载进度条
     *
     * @param msg
     */
    public void showProgressDialog(String msg) {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mProgressDialog.setMessage(msg);
        }
    }

    /*
     * 停止浮动加载进度条
     */
    public void hideProgressDialog() {
        mProgressDialog.dismiss();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown");
        if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
            Log.d(TAG, "onKeyDown CurrentAudio FM");
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && ArielVolumeManager.getInstance().isVolumeConnect()) {
                Log.d(TAG, "onKeyDown KEYCODE_VOLUME_DOWN");
                ArielVolumeManager.getInstance().adjustVolume(false, ArielVolumeManager.MOBILE_KEY_TYPE);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && ArielVolumeManager.getInstance().isVolumeConnect()) {
                Log.d(TAG, "onKeyDown KEYCODE_VOLUME_UP");
                ArielVolumeManager.getInstance().adjustVolume(true,ArielVolumeManager.MOBILE_KEY_TYPE);
                return true;
            } else return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp");
        if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
            Log.d(TAG, "onKeyUp CurrentAudio FM");
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && ArielVolumeManager.getInstance().isVolumeConnect()) {
                Log.d(TAG, "onKeyUp KEYCODE_VOLUME_DOWN");
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && ArielVolumeManager.getInstance().isVolumeConnect()) {
                Log.d(TAG, "onKeyUp KEYCODE_VOLUME_UP");
                return true;
            } else return super.onKeyUp(keyCode, event);
        }else
            return super.onKeyUp(keyCode, event);
    }


    private void registerBaiduReceiver() {
        if (baiduReceiver == null) {
            baiduReceiver = new BaiduReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("QM_ON_APP_FOREGROUND_com.baidu.BaiduMap");
        intentFilter.addAction("QM_ON_APP_BACKGROUND_com.baidu.BaiduMap");
        registerReceiver(baiduReceiver, intentFilter);
    }

    public void unRegisterBaiduReceiver() {
        if (baiduReceiver == null) {
            return;
        }
        unregisterReceiver(baiduReceiver);

    }

    /**
     * 监听百度前后台状态
     */
    public class BaiduReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("QM_ON_APP_FOREGROUND_com.baidu.BaiduMap")) {
                BdMapUIcontrol.baiduIsForeground = true;
            } else if (action.equals("QM_ON_APP_BACKGROUND_com.baidu.BaiduMap")) {
                BdMapUIcontrol.baiduIsForeground = false;
            }
        }
    }
}
