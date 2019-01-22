package com.qinggan.app.arielapp.ui.bluekey;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.auth.Certification;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.certification.SyncCertificationStatusRsp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.staryea.ui.CerStepFourActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveCarActivity extends Activity implements View.OnClickListener {

    private final static String TAG = ActiveCarActivity.class.getSimpleName();

    public Context context;
    @BindView(R.id.tip1)
    TextView tip1;
    @BindView(R.id.tip2)
    TextView tip2;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.rlBeforeActive)
    RelativeLayout rlBeforeActive;
    @BindView(R.id.back_btn)
    ImageView backBtn;


    @OnClick(R.id.btn_action)
    public void doActiveAction() {
        //去实名认证
        Certification.goToAuth(ActiveCarActivity.this);
        Certification.registAuthResultListener(new CerStepFourActivity.OnAuthrizeResultListener() {
            @Override
            public void onAuthrizeSuccessed(String iccid) {
                Log.e("ActiveCarActivity", "onAuthrizeSuccessed:" + iccid);
                //激活成功
                syncCertificationStatus();
                // TODO: 18-11-27 激活成功 UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });


            }

            @Override
            public void onAuthrizeFailed() {
                Log.e("ActiveCarActivity", "onAuthrizeFailed:");
                // TODO: 18-11-27 激活失败 UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.active_car_activity);
        ButterKnife.bind(this);
        mUiState = UI_STATE.BEFORE_ACTIVE;
        updateUiState();
        backBtn.setOnClickListener(this);
    }


    public enum UI_STATE {BEFORE_ACTIVE}

    private UI_STATE mUiState = UI_STATE.BEFORE_ACTIVE;

    public void updateUiState() {
        switch (mUiState) {
            case BEFORE_ACTIVE:
                rlBeforeActive.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * 同步实名状态结果给tsp
     */
    private void syncCertificationStatus() {
        if (null == ArielApplication.getmUserInfo()) return;
        String imsi = BindVehicleInfo.getImsi();
        Log.d("ActiveCarActivity", "syncCertificationStatus,imsi:" + imsi);
        TspManager.getInstance(ArielApplication.getApp()).syncCertificationStatus(ArielApplication.getmUserInfo().getVin(), ArielApplication.getmUserInfo().getMobilePhone(), imsi, new RestCallback<SyncCertificationStatusRsp>() {
            @Override
            public void success(SyncCertificationStatusRsp syncCertificationStatusRsp, RestResponse restResponse) {
                if ("0".equals(syncCertificationStatusRsp.getStatusCode())) {
                    //同步实名认证成功
                    BindVehicleInfo.setAuth(true);
                } else {
                    //同步实名认证失败
                }
            }

            @Override
            public void failure(RestError restError) {
                //同步实名认证失败
            }
        });
    }

    @Override
    protected void onDestroy() {
        Certification.unRegistAuthResultListener();
        super.onDestroy();
    }
}