package com.qinggan.app.arielapp.ui.widget.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.notification.ArielNotification;
import com.qinggan.app.arielapp.capability.upgrade.bugly.BuglyManager;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;

/**
 * <升级对话框>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-26]
 * @see [相关类/方法]
 * @since [V1]
 */
public class AppUpgradeDialog extends Dialog {
    private static final String TAG = AppUpgradeDialog.class.getSimpleName();

    public AppUpgradeDialog(@NonNull Context context) {
        super(context, R.style.upgradeDialog);
    }

    public AppUpgradeDialog(@NonNull Context context, UpgradeInfo strategy) {
        super(context, R.style.upgradeDialog);
        this.strategy = strategy;
    }

    UpgradeInfo strategy;
    TextView version, desc;
    Button cancelBtn, confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_upgrade_dialog);
        version = findViewById(R.id.version);
        desc = findViewById(R.id.desc);
        cancelBtn = findViewById(R.id.cancel);
        confirmBtn = findViewById(R.id.start);
        version.setText(String.format(getContext().getString(R.string.v2), strategy.versionName));
        desc.setText(strategy.newFeature);
        if (2 == strategy.upgradeType) {
            //强制升级
            cancelBtn.setVisibility(View.GONE);
            findViewById(R.id.cancel_confirm_divider).setVisibility(View.GONE);
            registDownloadListener();
            setCancelable(false);
            setCanceledOnTouchOutside(false);
        } else {
            setCancelable(true);
            setCanceledOnTouchOutside(true);
            BuglyManager.getInstance().registDownloadListener();
        }
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beta.startDownload();
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v3));
                if (2 != strategy.upgradeType) {
                    dismiss();
                }
            }
        });
    }

    private void registDownloadListener() {
        Log.d(TAG, "registDownloadListener");
        Beta.registerDownloadListener(new DownloadListener() {
            @Override
            public void onReceive(DownloadTask task) {
                Log.d(TAG, "DownloadListener onReceive");
                updateBtn(task);
            }

            @Override
            public void onCompleted(DownloadTask task) {
                Log.d(TAG, "DownloadListener onCompleted");
                updateBtn(task);
            }

            @Override
            public void onFailed(DownloadTask task, int code, String extMsg) {
                Log.d(TAG, "DownloadListener onFailed");
                updateBtn(task);
            }
        });
    }

    private void updateBtn(DownloadTask task) {
        Log.d(TAG, "updateBtn task status:" + task.getStatus());
        switch (task.getStatus()) {
            case DownloadTask.INIT:
            case DownloadTask.DELETED:
            case DownloadTask.FAILED: {
                confirmBtn.setText(getContext().getString(R.string.v3));
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v3));
            }
            break;
            case DownloadTask.COMPLETE: {
                confirmBtn.setText(getContext().getString(R.string.v4));
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v4));
            }
            break;
            case DownloadTask.DOWNLOADING: {
                String progress = String.valueOf(task.getSavedLength() * 100 / task.getTotalLength());
                confirmBtn.setText(getContext().getString(R.string.v5) + " " + progress + "%");
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v5) + " " + progress + "%");
            }
            break;
            case DownloadTask.PAUSED: {
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v14));
            }
            break;
            default:
                break;
        }
    }
}
