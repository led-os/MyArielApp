package com.qinggan.app.arielapp.ui.bluekey;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.databinding.ActivityVerfyCarIdentityBinding;
import com.qinggan.app.arielapp.ui.pin.add.AddPinActivity;
import com.qinggan.mobile.tsp.bean.BaseBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

/**
 * <验证车主身份>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public class VerfyCarIdentityActivity extends BaseActivity implements View.OnClickListener {
    ActivityVerfyCarIdentityBinding binding;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, VerfyCarIdentityActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void initView() {
        binding = DataBindingUtil.bind(findViewById(R.id.verfy_root));
        binding.setListener(this);

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.loading);
        binding.imgLoading.startAnimation(rotate);
    }

    @Override
    protected void initData() {
        TspManager.getInstance(ArielApplication.getApp()).queryVehicleOwner(ArielApplication.getmUserInfo().getVin(), new RestCallback<BaseBean>() {
            @Override
            public void success(BaseBean baseBean, RestResponse restResponse) {
                if (null != baseBean && "0".equals(baseBean.getStatusCode())) {
                    Log.d(TAG, "verfy car owner success");
                    verfySuccess();
                } else {
                    verfyFail();
                }
            }

            @Override
            public void failure(RestError restError) {
                verfyFail();
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_verfy_car_identity;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.openLlb) {
            //打开菱菱邦
            return;
        }

        if (v == binding.backBtn || v == binding.later) {
            finish();
            return;
        }
    }

    private void verfySuccess() {
        binding.imgLoading.clearAnimation();
        Intent intent = new Intent(this, AddPinActivity.class);
        intent.putExtra("type", AddPinActivity.UPGRADE_CAR_OWNER_CODE);
        startActivity(intent);
        finish();
    }

    private void verfyFail() {
        binding.imgLoading.clearAnimation();
        binding.verfyLoading.setVisibility(View.GONE);
        binding.verfyFail.setVisibility(View.VISIBLE);
    }
}
