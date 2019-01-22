package com.qinggan.app.arielapp.ui.pin.add;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.View;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.auth.Certification;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.databinding.ActivityAddCodeBinding;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.ui.bluekey.CarOwnerKeyActivity;
import com.qinggan.app.arielapp.ui.pin.findback.FindbackPinActivity;
import com.qinggan.app.arielapp.ui.widget.code.InputCompleteListener;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.certification.SyncCertificationStatusRsp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.staryea.ui.CerStepFourActivity;

/**
 * <增加/修改验证码>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-15]
 * @see [相关类/方法]
 * @since [V1]
 */
public class AddPinActivity extends BaseActivity implements View.OnClickListener, IAddPinView {
    private AddPinPresenter presenter;

    /**
     * 设置安全码
     */
    public static final int ADD_CODE = 100;
    /**
     * 修改安全码
     */
    public static final int UPDATE_CODE = 101;
    /**
     * 升级成车主
     */
    public static final int UPGRADE_CAR_OWNER_CODE = 102;

    /**
     * 设置.修改安全码成功
     */
    public static final int ADD_CODE_SUCCESS = 100;
    /**
     * 设置.修改安全码失败
     */
    public static final int ADD_CODE_FAIL = 101;

    ActivityAddCodeBinding binding;
    //第一次输入
    boolean first = true;
    //第二次输入
    boolean second = false;
    String firstString = "";
    String secondString = "";
    int type;

    @Override
    protected void initView() {
        binding = DataBindingUtil.bind(findViewById(R.id.add_code_root));
        binding.setListener(this);
    }

    @Override
    protected void initData() {
        type = getIntent().getIntExtra("type", ADD_CODE);
        presenter = new AddPinPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void initListener() {
        if (type == ADD_CODE || type == UPGRADE_CAR_OWNER_CODE) {
            //设置安全码
            binding.headTitle.setText(R.string.add_code);
            binding.tip.setText(R.string.add_code1);

            binding.codeView.setInputCompleteListener(new InputCompleteListener() {
                @Override
                public void inputComplete(String content) {
                    Log.d(TAG, "inputComplete first:" + first + " content:" + content);
                    if (first) {
                        first = false;
                        firstString = content;
                        Log.d(TAG, "firstString:" + firstString);
                        binding.tip.setText(getString(R.string.add_code2));
                        binding.codeView.clear();
                    } else {
                        String secondString = content;
                        Log.d(TAG, "secondString:" + secondString);
                        if (!firstString.equals(secondString)) {
                            binding.tipErr.setText(R.string.add_code3);
                            binding.tipErr.setVisibility(View.VISIBLE);
                        } else {
                            presenter.modifyPin(firstString);
                        }
                    }
                }

                @Override
                public void deleteContent() {
                    Log.d(TAG, "deleteContent:first:" + first);
                    binding.tipErr.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            //修改安全码
            binding.headTitle.setText(R.string.u_code);
            binding.tip.setText(R.string.u_code1);
            binding.codeView.setInputCompleteListener(new InputCompleteListener() {
                @Override
                public void inputComplete(String content) {
                    Log.d(TAG, "inputComplete first:" + first + " content:" + content);
                    if (first) {
                        //check pin
                        firstString = content;
                        presenter.checkPin(firstString);
                    } else if (second) {
                        second = false;
                        secondString = content;
                        Log.d(TAG, "secondString:" + secondString);
                        binding.tip.setText(getString(R.string.add_code2));
                        binding.codeView.clear();
                    } else {
                        String lastString = content;
                        Log.d(TAG, "lastString:" + lastString);
                        if (!secondString.equals(lastString)) {
                            binding.tipErr.setText(R.string.add_code3);
                            binding.tipErr.setVisibility(View.VISIBLE);
                        } else {
                            //secondString 新安全码
                            presenter.modifyPin(secondString);
                        }
                    }
                }

                @Override
                public void deleteContent() {
                    Log.d(TAG, "deleteContent:first:" + first);
                    binding.tipErr.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_add_code;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.headBackIcon) {
            finish();
        } else if (v == binding.overMaxFindbackBtn) {
            //找回安全码
            Intent intent = new Intent();
            intent.setClass(this, FindbackPinActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        Certification.unRegistAuthResultListener();
        super.onDestroy();
    }

    @Override
    public void modifyPinSuccess() {
        // 设置或修改安全码成功之后的逻辑
        BindVehicleInfo.setHasPin(true);
//        setResult(ADD_CODE_SUCCESS);
//        finish();
        Log.d(TAG, "modifyPinSuccess  type:" + type);
        if (type == UPGRADE_CAR_OWNER_CODE) {
            if (BindVehicleInfo.isAuth()) {
                //已经实名认证过
                CarOwnerKeyActivity.startAction(AddPinActivity.this);
                finish();
            } else {
                Log.d(TAG, "modifyPinSuccess go to auth");
                goAuth();
            }
        } else {
            setResult(ADD_CODE_SUCCESS);
            finish();
        }
    }

    @Override
    public void modifyPinFail(String msg) {
        ToastUtil.show(msg, this);
        // 设置或修改安全码失败之后的逻辑
    }

    @Override
    public void checkPinSuccess() {
        Log.d(TAG, "checkPinSuccess---");
        first = false;
        second = true;
        binding.tip.setText(getString(R.string.add_code1));
        binding.codeView.clear();
        binding.tipErr.setVisibility(View.INVISIBLE);
    }

    @Override
    public void checkPinFail(String msg) {
        ToastUtil.show(msg, this);
    }

    @Override
    public void checkPinError(int leftCount) {
        Log.d(TAG, "checkPinError---leftCount:" + leftCount);
        binding.codeView.clear();
        if (leftCount == 0) {
            //超过最大次数
            binding.inputCodeRl.setVisibility(View.GONE);
            binding.overMaxRl.setVisibility(View.VISIBLE);
        } else {
            binding.tipErr.setVisibility(View.VISIBLE);
            binding.tipErr.setText(String.format(getString(R.string.v_code2), leftCount));
        }
    }

    /**
     * 去实名认证
     */
    public void goAuth() {
        //去实名认证
        Certification.goToAuth(AddPinActivity.this);
        Certification.registAuthResultListener(new CerStepFourActivity.OnAuthrizeResultListener() {
            @Override
            public void onAuthrizeSuccessed(String iccid) {
                Log.e("ActiveCarActivity", "onAuthrizeSuccessed:" + iccid);
                //激活成功
                syncCertificationStatus();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("ActiveCarActivity", "onAuthrizeSuccessed:open CarOwnerKeyActivity and finish this");
                        CarOwnerKeyActivity.startAction(AddPinActivity.this);
                        finish();
                    }
                });
            }

            @Override
            public void onAuthrizeFailed() {
                Log.e("ActiveCarActivity", "onAuthrizeFailed:");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show("实名认证失败", AddPinActivity.this);
                        finish();
                    }
                });
            }
        });
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


}
