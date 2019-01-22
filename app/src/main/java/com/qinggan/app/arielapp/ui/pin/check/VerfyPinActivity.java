package com.qinggan.app.arielapp.ui.pin.check;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.View;

import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.databinding.ActivityVerfycodeBinding;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.ui.pin.add.AddPinPresenter;
import com.qinggan.app.arielapp.ui.pin.add.IAddPinView;
import com.qinggan.app.arielapp.ui.pin.findback.FindbackPinActivity;
import com.qinggan.app.arielapp.ui.widget.code.InputCompleteListener;

/**
 * <验证安全码>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-15]
 * @see [相关类/方法]
 * @since [V1]
 */
public class VerfyPinActivity extends BaseActivity implements View.OnClickListener, IAddPinView {

    public static int VERFY_PIN_SUCCESS = 100;
    ActivityVerfycodeBinding binding;
    AddPinPresenter presenter;

    @Override
    protected void initView() {
        binding = DataBindingUtil.bind(findViewById(R.id.verfy_code_root));
    }

    @Override
    protected void initData() {
        presenter = new AddPinPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void initListener() {
        binding.setListener(this);
        binding.codeView.setInputCompleteListener(new InputCompleteListener() {
            @Override
            public void inputComplete(String content) {
                Log.d(TAG, "inputComplete:content:" + content);
                presenter.checkPin(content);
            }

            @Override
            public void deleteContent() {
                binding.tipErr.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_verfycode;
    }

    @Override
    public void onClick(View v) {
        //找回安全码
        if (v == binding.headBackIcon ) {
            //返回
            finish();
        } else if (v == binding.forgetCode || v == binding.overMaxFindbackBtn) {
            //找回安全码
            Intent intent = new Intent();
            intent.setClass(this, FindbackPinActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void modifyPinSuccess() {

    }

    @Override
    public void modifyPinFail(String msg) {

    }

    @Override
    public void checkPinSuccess() {
        setResult(VERFY_PIN_SUCCESS);
        finish();
    }

    @Override
    public void checkPinFail(String msg) {
        ToastUtil.show(msg, this);
    }

    @Override
    public void checkPinError(int leftCount) {
        Log.d(TAG, "checkPinError---leftCount:" + leftCount);
//        binding.codeView.clearInputContent();
        if (leftCount == 0) {
            //超过最大次数
            binding.inputCodeRl.setVisibility(View.GONE);
            binding.overMaxRl.setVisibility(View.VISIBLE);
        } else {
            binding.tipErr.setVisibility(View.VISIBLE);
            binding.tipErr.setText(String.format(getString(R.string.v_code2), leftCount));
        }
    }
}
