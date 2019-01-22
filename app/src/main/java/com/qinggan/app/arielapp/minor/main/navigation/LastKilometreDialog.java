package com.qinggan.app.arielapp.minor.main.navigation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.notification.ArielNotification;
import com.qinggan.app.arielapp.capability.upgrade.bugly.BuglyManager;
import com.qinggan.app.arielapp.minor.core.NaviInterface;
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
public class LastKilometreDialog extends Dialog {

    private Context context;
    private int cont;

    private confirmListener listener;

    TextView desc;
    Button cancelBtn, confirmBtn;
    Handler handler;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (cont > 0) {
                cont = cont - 1;
                confirmBtn.setText(String.format(context.getString(R.string.v16), cont + ""));
                if (handler != null) {
                    handler.postDelayed(runnable, 1000);
                }
            } else {
                dismiss();
                listener.onConfirm();
            }
        }
    };


    public LastKilometreDialog(Context context, confirmListener lis) {
        super(context, R.style.upgradeDialog);
        this.context = context;
        listener = lis;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_kilometre_dialog);
        desc = findViewById(R.id.desc);
        cancelBtn = findViewById(R.id.cancel);
        confirmBtn = findViewById(R.id.start);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onCancle();
            }
        });
        cont = 10;
        confirmBtn.setText(String.format(context.getString(R.string.v16), "10"));
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                handler = null;
                listener.onConfirm();

            }
        });
    }


    public interface confirmListener {
        void onConfirm();

        void onCancle();
    }


}
