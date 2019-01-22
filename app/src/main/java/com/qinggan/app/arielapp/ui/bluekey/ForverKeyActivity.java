package com.qinggan.app.arielapp.ui.bluekey;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.databinding.ActivityForverKeyBinding;

/**
 * <获取到永久钥匙>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-29]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ForverKeyActivity extends BaseActivity implements View.OnClickListener {
    ActivityForverKeyBinding binding;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, ForverKeyActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void initView() {
        binding = DataBindingUtil.bind(findViewById(R.id.forver_key_root));
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
        return R.layout.activity_forver_key;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.regist) {
            //升级车主获取更多权限
            VerfyCarIdentityActivity.startAction(this);
            finish();
            return;
        }
        if (v == binding.later) {
            //以后再说
            finish();
            return;
        }
    }
}
