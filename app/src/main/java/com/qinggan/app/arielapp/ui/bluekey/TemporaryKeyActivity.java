package com.qinggan.app.arielapp.ui.bluekey;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.databinding.ActivityTempKeyBinding;
import com.qinggan.app.arielapp.user.activity.LoginActivity;

/**
 * <临时钥匙申请成功页面>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-29]
 * @see [相关类/方法]
 * @since [V1]
 */
public class TemporaryKeyActivity extends BaseActivity implements View.OnClickListener {
    ActivityTempKeyBinding binding;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, TemporaryKeyActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void initView() {
        binding = DataBindingUtil.bind(findViewById(R.id.temp_key_root));
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
        return R.layout.activity_temp_key;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.later) {
            //以后再说
            finish();
            return;
        }
        if (v == binding.regist) {
            //快速注册获取永久钥匙
            LoginActivity.startAction(this);
            finish();
            return;
        }
    }
}
