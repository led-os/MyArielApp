package com.qinggan.app.arielapp.user.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.commonui.EditClear;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.ui.bluekey.BindCarActivity;
import com.qinggan.app.arielapp.ui.bluekey.ForverKeyActivity;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.app.arielapp.utils.VehicleUtils;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.mobile.tsp.auth.AuthLoginResponseItem;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.vcm.IdentifyCodeResp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.util.NetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qinggan.app.arielapp.minor.utils.Constants.LOGIN_EVENT;
import static com.qinggan.app.arielapp.minor.utils.Constants.USERINFO;

/**
 * Created by Yorashe on 18-11-15.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout mBackBtn;
    private EditClear mUserNameEdit;
    private EditClear mLoginCodeTextView;
    private TextView mUsernameHintTextView;
    private TextView mCodeHintTextView;
    private TextView mLoginBtn;
    private View mBindingView;
    private Button mBindingBtn;
    private Button mSayAgainBtn;
    private TextView mGetLoginCode;
    private MineCountDownTimer mCountDownTimer;
    private SharedPreferences pref;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_login_code:
                if (!checkPhoneNum(mUserNameEdit.getText())) {
//                    mUsernameHintTextView.setText(R.string.correct_phone_number_hint);
                    mUsernameHintTextView.setVisibility(View.VISIBLE);
                    return;
                }
                getCode();

                break;
            case R.id.login_btn:
//                new WheelControl(this);
//                ArielApplication.getCanBusManager().setAirConditionState(AirConditionState.AC_POWER_SWITCH,AirConditionState.SWITCH_ON);
//               TspManager.getInstance(mContext).requestUserLogin("13119489482", "123456", new RestCallback<AuthLoginResponseItem>() {
//                    @Override
//                    public void success(AuthLoginResponseItem identifyCodeResp, RestResponse response) {
//                        hideKeyBoard();
//                        setUserInfo(identifyCodeResp);
//                        UserInfo mUser = ArielApplication.getmUserInfo();
//                        //MainActivity.mAppBlueKeyListener.onBlueKeyAuthSuccess("");
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putString("phone", mUserNameEdit.getText().toString());
//                        //提交修改
//                        editor.commit();
//                        EventBus.getDefault().post(new EventBusBean(LOGIN_EVENT,
//                                "1"
//                        ));
//                        mUser.updateVehicleList();
//                    }
//
//                    @Override
//                    public void failure(RestError restError) {
//
//                    }
//                });
                UMAnalyse.count(UMCountEvent.DAY_REGIST);
                if (checkLogin()) {
                    toLogin();
                }
                break;
            case R.id.binding_btn:
                Intent intent = new Intent(mContext, BindCarActivity.class);
                mContext.startActivity(intent);
                finish();
                break;
            case R.id.say_again_btn:
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                finish();
                break;
            case R.id.back_btn:
                finish();
                break;
        }

    }


    private void toLogin() {
        showProgressDialog("正在登录...");
        TspManager.getInstance(this).requestUserLoginByVerificationCode(mUserNameEdit.getText().toString(), mLoginCodeTextView.getText().toString(), VehicleUtils.getIMEI(), new RestCallback<AuthLoginResponseItem>() {
            @Override
            public void success(AuthLoginResponseItem identifyCodeResp, RestResponse restResponse) {
                hideKeyBoard();
                setUserInfo(identifyCodeResp);
                UserInfo mUser = ArielApplication.getmUserInfo();
                //MainActivity.mAppBlueKeyListener.onBlueKeyAuthSuccess("");
                mUser.updateVehicleList();
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("phone", mUserNameEdit.getText().toString());
                //提交修改
                editor.commit();
                EventBus.getDefault().post(new EventBusBean(LOGIN_EVENT,
                        "1"
                ));
            }

            @Override
            public void failure(RestError restError) {
                hideProgressDialog();
                if (restError.getCode() == -1) {
                    ToastUtil.show(getResources().getString(R.string.no_network_tips), LoginActivity.this);
                } else {
                    mCodeHintTextView.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void showBindingView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBindingView = getLayoutInflater().inflate(R.layout.goto_binding, null, false);
        mBindingBtn = (Button) mBindingView.findViewById(R.id.binding_btn);
        mSayAgainBtn = (Button) mBindingView.findViewById(R.id.say_again_btn);
        mBindingBtn.setOnClickListener(this);
        mSayAgainBtn.setOnClickListener(this);
        mBindingView.setOnClickListener(this);
        addContentView(mBindingView, params);

    }

    private void setUserInfo(AuthLoginResponseItem identifyCodeResp) {
        TspManager.getInstance(this).setGlobalToken(identifyCodeResp);
        UserInfo userInfo = new UserInfo();
        try {
            userInfo.setUid(identifyCodeResp.getUserInfo().getUid());
            userInfo.setMobilePhone(identifyCodeResp.getUserInfo().getMobilePhone());
            userInfo.setAvatar(identifyCodeResp.getUserInfo().getNickName());
            userInfo.setUserName(identifyCodeResp.getUserInfo().getUserName());
            userInfo.setUserSeq(identifyCodeResp.getUserInfo().getUserSeq());
            userInfo.setAuthLoginResponseItem(identifyCodeResp);

            Log.e(TAG, "setUserInfo() userInfo=" + userInfo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArielApplication.setmUserInfo(userInfo);

    }

    private void getCode() {
        if (NetUtil.isNetworkConnected(this)) {
            startCountDownTimer();
            TspManager.getInstance(this).getIdentifyCode(mUserNameEdit.getText().toString(), new RestCallback<IdentifyCodeResp>() {
                @Override
                public void success(IdentifyCodeResp identifyCodeResp, RestResponse restResponse) {

                }

                @Override
                public void failure(RestError restError) {
                    ToastUtil.show(restError.getMessage(), LoginActivity.this);

                }
            });
        } else {
            ToastUtil.show(R.string.no_network_tips, LoginActivity.this);

        }

    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
//    public void onBlueKeyEvent(BlueKeyEvent event) {
//        Log.e(TAG, "onBlueKeyEvent() event=" + event.toString());
//        if (event != null) {
//            if (event.obj instanceof VehicleListBean) {
//                VehicleListBean bean = (VehicleListBean) event.obj;
//
//            } else if (event.obj instanceof String) {
//                String string = (String) event.obj;
//                if ("uploadBleKey".equals(string)) {
//                    if (event.success) {
//                        ForverKeyActivity.startAction(LoginActivity.this);
//                    } else {
//
//                        return;
//                    }
//                }
//            }
//        }
//        finish();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(String loginevent) {
        Log.e(TAG, "onLoginEvent() event=" + loginevent);
        if ("login_success".equals(loginevent)) {
            hideProgressDialog();
            finish();
            return;
        }
        if ("login_fail_getVehicleList".equals(loginevent)) {
            hideProgressDialog();
            ToastUtil.show("获取车辆列表失败", this);
            return;
        }
        if ("login_fail_vehicle_bind".equals(loginevent)) {
            hideProgressDialog();
            ToastUtil.show("有临时钥匙,车辆绑定失败", this);
            return;
        }
        if ("login_fail_blukey_upload".equals(loginevent)) {
            hideProgressDialog();
            ToastUtil.show("蓝牙钥匙上传失败", this);
            return;
        }
        if ("login_success_blekey_upload".equals(loginevent)) {
            hideProgressDialog();
            ToastUtil.show("蓝牙钥匙上传成功", this);
            //临时的蓝牙钥匙上传成功,重新获取车辆信息
            ArielApplication.getmUserInfo().updateVehicleList();
            EventBus.getDefault().unregister(this);
            ForverKeyActivity.startAction(LoginActivity.this);
            finish();
            return;
        }

        if ("login_fail_getBlukey".equals(loginevent)) {
            hideProgressDialog();
            ToastUtil.show("有车,获取蓝牙钥匙失败", this);
            return;
        }

        if ("login_success_carowner_fail".equals(loginevent) || "login_success_carowner_success".equals(loginevent)) {
            hideProgressDialog();
            finish();
            return;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        mBackBtn = (RelativeLayout) findViewById(R.id.back_btn);
        mUserNameEdit = (EditClear) findViewById(R.id.phone_txt);
        mUsernameHintTextView = (TextView) findViewById(R.id.phone_hint_text);
        mCodeHintTextView = (TextView) findViewById(R.id.edt_code_hint_text);
        mUserNameEdit.setHint(getResources().getString(R.string.phont_edittext_hint));
        mCodeHintTextView = (TextView) findViewById(R.id.edt_code_hint_text);
        mUserNameEdit.setTextLength(11);
        mLoginCodeTextView = (EditClear) findViewById(R.id.yezhengma_txt);
        mLoginCodeTextView.setHint(getResources().getString(R.string.getcode_edittext_hint));
        mLoginCodeTextView.setTextLength(4);
        mLoginBtn = (TextView) findViewById(R.id.login_btn);
        mGetLoginCode = (TextView) findViewById(R.id.get_login_code);
        EventBus.getDefault().register(this);
        mBackBtn.setOnClickListener(this);
    }

    /**
     * @param activity
     */
    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        activity.startActivity(intent);
    }

    @Override
    protected void initListener() {
        mBackBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
        mGetLoginCode.setOnClickListener(this);
        mUserNameEdit.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsernameHintTextView.setVisibility(View.INVISIBLE);
                if (s.length() == 11) {
                    setGetLoginCodeEnable(true);
                } else {
                    setGetLoginCodeEnable(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoginCodeTextView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCodeHintTextView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void initData() {
        pref = this.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
        String phone = pref.getString("phone", "");
        if (!TextUtils.isEmpty(phone)) {
            mUserNameEdit.setText(phone);
            setGetLoginCodeEnable(true);
            mUserNameEdit.getEditText().setSelection(phone.length());
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }


    /**
     * 匹配号码是否是11为数字
     *
     * @param phoneNum
     * @return 不是11位数字，返回false
     */
    public static boolean checkPhoneNum(String phoneNum) {
        if (!TextUtils.isEmpty(phoneNum)) {
            String format = "^\\d{11}$";
            Pattern p = Pattern.compile(format);
            Matcher m = p.matcher(phoneNum);
            return m.matches();
        } else {
            return false;
        }

    }

    private boolean checkLogin() {
        if (!checkPhoneNum(mUserNameEdit.getText())) {
//            mUsernameHintTextView.setText(R.string.correct_phone_number_hint);
            mUsernameHintTextView.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(mLoginCodeTextView.getText())) {
//            mCodeHintTextView.setText(R.string.input_verification_code);
            mCodeHintTextView.setVisibility(View.VISIBLE);

            return false;
        }
        return true;
    }


    /**
     * 启动倒计时
     */
    void startCountDownTimer() {
        if (null == mCountDownTimer)
            mCountDownTimer = new MineCountDownTimer(60000, 1000, mGetLoginCode);
        mCountDownTimer.start();
    }

    /**
     * 倒计时类
     */
    class MineCountDownTimer extends CountDownTimer {
        private View view;

        public MineCountDownTimer(long millisInFuture, long countDownInterval, View view) {
            super(millisInFuture, countDownInterval);
            this.view = view;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onTick(long millisUntilFinished) {
            if (null != view)
                view.setEnabled(false);
            if (null != mGetLoginCode)
//                mGetLoginCode.setText( String.format(getResources().getString(R.string.recapture), (millisUntilFinished / 1000)+""));
                mGetLoginCode.setText((millisUntilFinished / 1000) + "s");
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onFinish() {
            if (null != view)
                view.setEnabled(true);
            if (null != mGetLoginCode)
                mGetLoginCode.setText(R.string.get_verification_code);
        }
    }

    private void setGetLoginCodeEnable(boolean isEnable){
        if (isEnable) {
            mGetLoginCode.setEnabled(true);
            mGetLoginCode.setTextColor(getResources().getColor(R.color.white));
        } else {
            mGetLoginCode.setEnabled(false);
            mGetLoginCode.setTextColor(getResources().getColor(R.color.gary_9d));
        }
    }
}
