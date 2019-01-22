package com.qinggan.app.arielapp.ui.pin.findback;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.databinding.ActivityFindbackCodeBinding;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.ui.pin.add.AddPinActivity;
import com.qinggan.app.arielapp.utils.FormatUtil;

/**
 * <找回安全码>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-15]
 * @see [相关类/方法]
 * @since [V1]
 */
public class FindbackPinActivity extends BaseActivity implements View.OnClickListener, IFindbackPinView {

    ActivityFindbackCodeBinding binding;

    FindbackPinPresenter presenter;

    private Handler handler = new Handler();

    private int second = 60;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            binding.messageCodeBtn.setText(second + "S");
            if (second > 0) {
                second--;
                handler.postDelayed(this, 1000);
            } else {
                msgBtnReset();
            }
        }
    };

    /**
     * 获取验证码按钮重置
     */
    private void msgBtnReset() {
        second = 60;
        handler.removeCallbacks(runnable);
        binding.messageCodeBtn.setEnabled(true);
        binding.messageCodeBtn.setText(R.string.f_code);
    }

    @Override
    protected void initView() {
        binding = DataBindingUtil.bind(findViewById(R.id.find_back_root));
        binding.messageCode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
    }

    @Override
    protected void initData() {
        presenter = new FindbackPinPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void initListener() {
        binding.setListener(this);
        binding.phoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.phoneNoTip.getVisibility() == View.VISIBLE) {
                    binding.phoneNoTip.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(binding.phoneNo.getText().toString())) {
                    binding.phoneNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    binding.clear.setVisibility(View.GONE);
                } else {
                    binding.phoneNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                    binding.clear.setVisibility(View.VISIBLE);
                }
                if (binding.messageCode.getText().toString().length() == 4 && binding.phoneNo.getText().toString().length() == 11) {
                    binding.next.setEnabled(true);
                } else {
                    binding.next.setEnabled(false);
                }
            }
        });

        binding.messageCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.codeNoTip.getVisibility() == View.VISIBLE) {
                    binding.codeNoTip.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(binding.messageCode.getText().toString())) {
                    binding.messageCode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                } else {
                    binding.messageCode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                }
                if (binding.messageCode.getText().toString().length() == 4 && binding.phoneNo.getText().toString().length() == 11) {
                    binding.next.setEnabled(true);
                } else {
                    binding.next.setEnabled(false);
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_findback_code;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.headBackIcon) {
            finish();
        } else if (v == binding.clear) {
            //清空手机
            binding.phoneNo.setText("");
        } else if (v == binding.messageCodeBtn) {
            //手机号
            String phoneNo = binding.phoneNo.getText().toString();
            if (TextUtils.isEmpty(phoneNo)) {
                binding.phoneNo.requestFocus();
                binding.phoneNoTip.setText(R.string.f_code4);
                binding.phoneNoTip.setVisibility(View.VISIBLE);
                return;
            }
            if (!FormatUtil.isPhone(phoneNo)) {
                binding.phoneNo.requestFocus();
                binding.phoneNoTip.setText(R.string.f_code5);
                binding.phoneNoTip.setVisibility(View.VISIBLE);
                return;
            }
            // 获取验证码
            presenter.getIdentifyCode(phoneNo);

        } else if (v == binding.next) {
            //手机号
            String phoneNo = binding.phoneNo.getText().toString();
            //验证码
            String code = binding.messageCode.getText().toString();
            if (TextUtils.isEmpty(phoneNo)) {
                //提示输入手机号
                binding.phoneNo.requestFocus();
                binding.phoneNoTip.setText(R.string.f_code4);
                binding.phoneNoTip.setVisibility(View.VISIBLE);
                return;
            }
            if (!FormatUtil.isPhone(phoneNo)) {
                //提示输入正确格式的手机号
                binding.phoneNo.requestFocus();
                binding.phoneNoTip.setText(R.string.f_code5);
                binding.phoneNoTip.setVisibility(View.VISIBLE);
                return;
            }
            if (TextUtils.isEmpty(code)) {
                //提示输入验证码
                binding.messageCode.requestFocus();
                binding.codeNoTip.setText(R.string.f_code2);
                binding.codeNoTip.setVisibility(View.VISIBLE);
                return;
            }
            if (code.length() < 4) {
                //提示输入正确的验证码
                binding.messageCode.requestFocus();
                binding.codeNoTip.setText(R.string.f_code6);
                binding.codeNoTip.setVisibility(View.VISIBLE);
                return;
            }
            //服务器校验 短信验证码
            presenter.checkIdentifyCode(binding.phoneNo.getText().toString(), binding.messageCode.getText().toString());
            binding.next.setEnabled(false);
        }
    }

    @Override
    public void onIdentifyCodeSuccess() {
        //不可点击
        binding.messageCodeBtn.setEnabled(false);
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    @Override
    public void onIdentifyCodeFail(String msg) {
        ToastUtil.show(msg, this);
    }

    @Override
    public void onCheckIdentifyCodeSuccess() {
        //短信验证码校验成功
        Intent intent = new Intent(this, AddPinActivity.class);
        intent.putExtra("type", AddPinActivity.ADD_CODE);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckIdentifyCodeFail(String msg) {
        ToastUtil.show(msg, this);
        binding.next.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
