package com.qinggan.app.arielapp.user.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.BuildConfig;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.upgrade.bugly.BuglyManager;
import com.qinggan.app.arielapp.capability.upgrade.bugly.BuglyUpgradeListener;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import butterknife.BindView;
import butterknife.OnClick;



public class AboutActivity extends BaseActivity implements View.OnClickListener, BuglyUpgradeListener {

    @BindView(R.id.app_version_text)
    TextView appVersionText;
    @BindView(R.id.app_update_version)
    TextView appUpdateVersion;
    @BindView(R.id.app_update)
    LinearLayout appUpdate;
    @BindView(R.id.car_update_version)
    TextView carUpdateVersion;
    @BindView(R.id.car_update)
    LinearLayout carUpdate;
    @BindView(R.id.help)
    LinearLayout help;
    @BindView(R.id.back_btn)
    RelativeLayout backBtn;
    @BindView(R.id.car_update_lines)
    View mLines;

    @Override
    public int getLayoutId() {
        return R.layout.about_lay;
    }


    @Override
    public void initView() {
        appVersionText.setText("version " + BuildConfig.VERSION_NAME);

    }

    @Override
    protected void initData() {
        if (ArielApplication.getmUserInfo()==null || TextUtils.isEmpty(ArielApplication.getmUserInfo().getVin())
                || TextUtils.isEmpty(ArielApplication.getmUserInfo().getTpdsn())){
            carUpdate.setVisibility(View.GONE);
            mLines.setVisibility(View.GONE);
        }else {
            carUpdate.setVisibility(View.VISIBLE);
            mLines.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void initListener() {
        BuglyManager.getInstance().registBuglyUpgradeListener(this);
//        BuglyManager.getInstance().checkNewVersion();
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
        if (upgradeInfo!=null){
            appUpdateVersion.setVisibility(View.VISIBLE);
            appUpdateVersion.setText("有新版本"+upgradeInfo.versionName);
        }

    }

    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, AboutActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @OnClick({R.id.app_update, R.id.car_update, R.id.help,R.id.back_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_update:
                checkUpdate();
                break;
            case R.id.back_btn:
                finish();
                break;
            case R.id.car_update:
                VersionUpgradeActivity.startAction(this);
                break;
            case R.id.help:

                break;
            default:
                break;

        }

    }

    @Override
    public void onNewVersion(UpgradeInfo strategy) {
        if (strategy!=null){
            appUpdateVersion.setVisibility(View.VISIBLE);
            appUpdateVersion.setText("有新版本"+strategy.versionName);
        }

    }

    @Override
    public void onCheckVersion() {

    }

    @Override
    public void onUpgradeNoVersion() {
        ToastUtil.show("当前已是最新版本", this);

    }

    @Override
    public void onCheckFailed() {

    }

    private void checkUpdate() {
        if (BuglyManager.getInstance().checkNewVersion()) {
            ToastUtil.show("正在下载更新包中", this);
        }

    }

}
