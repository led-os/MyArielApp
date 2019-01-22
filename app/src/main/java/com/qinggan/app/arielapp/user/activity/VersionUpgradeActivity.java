package com.qinggan.app.arielapp.user.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.upgrade.tspota.TspOtaListener;
import com.qinggan.app.arielapp.capability.upgrade.tspota.TspOtaManager;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.ui.widget.dialog.NormalSelectionDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Yorashe on 18-11-23.
 */

public class VersionUpgradeActivity extends BaseActivity implements View.OnClickListener, TspOtaListener {

    @BindView(R.id.back_btn)
    RelativeLayout backBtn;
    @BindView(R.id.version_text)
    TextView versionText;
    @BindView(R.id.canupgrade_hint)
    TextView canupgradeHint;
    @BindView(R.id.upgrade_btn)
    TextView upgradeBtn;
    @BindView(R.id.upgrade_progress)
    ProgressBar upgradeProgress;
    @BindView(R.id.upgrade_hint)
    TextView upgradeHint;
    @BindView(R.id.upgrade_info)
    TextView upgradeInfo;
    @BindView(R.id.upgrade_lay)
    LinearLayout upgradeLay;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        TspOtaManager.getInstance().setListener(this);
        TspOtaManager.getInstance().getCurrentVersion(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getPdsn());
        TspOtaManager.getInstance().checkHasNewVersion(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getPdsn());
    }

    @Override
    protected void initListener() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.upgrade_lay;
    }

    /**
     * @param activity
     */
    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, VersionUpgradeActivity.class);
        activity.startActivity(intent);
    }

    @OnClick({R.id.back_btn, R.id.upgrade_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.upgrade_btn:
                checkUpdate();
                break;
        }
    }

    private void checkUpdate() {
        if (TextUtils.equals("现在升级", upgradeBtn.getText())) {
            upgradeLay.setVisibility(View.VISIBLE);
            upgradeHint.setText("正在下载升级包...");
            upgradeProgress.setVisibility(View.VISIBLE);
            TspOtaManager.getInstance().startUpgrade(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getPdsn(), "1");

        } else {
            TspOtaManager.getInstance().checkHasNewVersion(ArielApplication.getmUserInfo().getVin(), BindVehicleInfo.getPdsn());
        }
    }


    @Override
    public void onRspCurrentVersion(String version) {
        versionText.setText("version " + version);

    }

    @Override
    public void onUpgradeProcess(String processId, String upgradeStatus, String processDesc) {
        try {
            upgradeProgress.setProgress(Integer.parseInt(processDesc));
            if (Integer.parseInt(processDesc)>=100){
                upgradeHint.setText("正在重启车机，请稍后...");
                upgradeProgress.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRspNewVersion(String version, String releaseNote, String size) {
        canupgradeHint.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(version)){
            versionText.setText(version);
            upgradeInfo.setText("版本号 "+version+"\n大小　"+size+"M");
        }
        upgradeBtn.setText("现在升级");
    }

    @Override
    public void onRspNoUpgrade() {
        ToastUtil.show("当前已是最新版本", this);
        canupgradeHint.setVisibility(View.GONE);
        upgradeBtn.setText("检查更新");
    }

    @Override
    public void onError(String message) {
//        ToastUtil.show(message, this);
        showDialog(message);
    }

    private void showDialog(String message){
        NormalSelectionDialog dialog =new NormalSelectionDialog(this,message,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate();

            }
        });
        dialog.show();
    }



}
