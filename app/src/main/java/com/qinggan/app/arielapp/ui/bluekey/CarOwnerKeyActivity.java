package com.qinggan.app.arielapp.ui.bluekey;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.databinding.ActivityCarOwnerBinding;

/**
 * <车主钥匙成功页面>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-29]
 * @see [相关类/方法]
 * @since [V1]
 */
public class CarOwnerKeyActivity extends BaseActivity implements View.OnClickListener {
    ActivityCarOwnerBinding binding;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, CarOwnerKeyActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void initView() {
        binding = DataBindingUtil.bind(findViewById(R.id.car_owner_root));
        binding.setListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_car_owner;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.ok){
            finish();
            return;
        }
    }
}
