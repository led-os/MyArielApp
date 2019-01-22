package com.qinggan.app.arielapp.user.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.ui.widget.MyRadioGroup;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.userfavorite.UserFavoriteResp;
import com.qinggan.mobile.tsp.models.userfavorite.WakeUpKeyResp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Yorashe on 18-11-23.
 */

public class WakeupSetActivityOld extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.back_btn)
    RelativeLayout backBtn;
    @BindView(R.id.hi_lingji_text)
    TextView hiLingjiText;
    @BindView(R.id.hi_lingji)
    RadioButton hiLingji;
    @BindView(R.id.hi_lingji_lay)
    RelativeLayout hiLingjiLay;
    @BindView(R.id.hi_smallling_text)
    TextView hiSmalllingText;
    @BindView(R.id.hi_smallling)
    RadioButton hiSmallling;
    @BindView(R.id.hi_smallling_lay)
    RelativeLayout hiSmalllingLay;
    @BindView(R.id.hi_babyjun_text)
    TextView hiBabyjunText;
    @BindView(R.id.hi_babyjun)
    RadioButton hiBabyjun;
    @BindView(R.id.hi_babyjun_lay)
    RelativeLayout hiBabyjunLay;
    @BindView(R.id.wakeup_group)
    MyRadioGroup mWakeupGroup;

    private boolean isFirstCheck;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        getWakeUpKey();
    }

    @Override
    protected void initListener() {
        mWakeupGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                if (isFirstCheck){
                    isFirstCheck=false;
                    return;
                }
                switch (checkedId) {
                    case R.id.hi_lingji:
                        setWakeUpKey(hiLingjiText.getText().toString());
                        break;
                    case R.id.hi_smallling:
                        setWakeUpKey(hiSmalllingText.getText().toString());

                        break;
                    case R.id.hi_babyjun:
                        setWakeUpKey(hiBabyjunText.getText().toString());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.wakeup_lay_old;
    }

    /**
     * @param activity
     */
    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, WakeupSetActivityOld.class);
        activity.startActivity(intent);
    }
    @OnClick({R.id.back_btn,R.id.hi_lingji_lay,R.id.hi_smallling_lay,R.id.hi_babyjun_lay})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.hi_lingji_lay:
                hiLingji.performClick();
                break;
            case R.id.hi_smallling_lay:
                hiSmallling.performClick();
                break;
            case R.id.hi_babyjun_lay:
                hiBabyjun.performClick();
                break;
        }
    }

    private void setWakeUpKey(final String word) {
        TspManager.getInstance(mContext).setWakeupCall(word, new RestCallback<UserFavoriteResp>() {
            @Override
            public void success(UserFavoriteResp arg0, RestResponse response) {
                ToastUtil.show("唤醒词语以设置为" + word, mContext);
                UserInfo userInfo =ArielApplication.getmUserInfo();
                userInfo.setHotWord(word);
                ArielApplication.setmUserInfo(userInfo);
                if (null!= ArielApplication.getmHotwordManager())
                    ArielApplication.getmHotwordManager().setCustomWakeupWord(word);
            }

            @Override
            public void failure(RestError restError) {
                ToastUtil.show(restError.getMessage(), mContext);

            }
        });
    }
    private void getWakeUpKey() {
        TspManager.getInstance(mContext).getWakeupCall( new RestCallback<WakeUpKeyResp>() {
            @Override
            public void success(WakeUpKeyResp arg0, RestResponse response) {
                if (null!=arg0.getData()&&arg0.getData().size()>0){
                    String key =arg0.getData().get(0).getContent();
                    checkKey(key);
                }else{
                    isFirstCheck=true;
                    hiBabyjun.setChecked(true);
                }

            }


            @Override
            public void failure(RestError restError) {
                ToastUtil.show(restError.getMessage(), mContext);

            }
        });
    }

    private void checkKey(String key) {
        if (!TextUtils.isEmpty(key)){
            if (TextUtils.equals(hiLingjiText.getText().toString(),key)){
                isFirstCheck=true;
                hiLingji.setChecked(true);
            }else if (TextUtils.equals(hiSmalllingText.getText().toString(),key)){
                isFirstCheck=true;
                hiSmallling.setChecked(true);
            }else if (TextUtils.equals(hiBabyjunText.getText().toString(),key)){
                isFirstCheck=true;
                hiBabyjun.setChecked(true);

            }
        }

    }

}
