package com.qinggan.app.arielapp.user.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Yorashe on 18-11-23.
 */

public class WakeupSetActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.back_btn)
    RelativeLayout backBtn;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.wakeup_lay;
    }

    /**
     * @param activity
     */
    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, WakeupSetActivity.class);
        activity.startActivity(intent);
    }
    @OnClick({R.id.back_btn})
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

}
