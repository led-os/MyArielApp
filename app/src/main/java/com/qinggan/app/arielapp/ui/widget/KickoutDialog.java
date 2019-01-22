package com.qinggan.app.arielapp.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ui.bluekey.ActiveCarActivity;
import com.qinggan.app.arielapp.ui.bluekey.BindCarActivity;
import com.qinggan.app.arielapp.ui.bluekey.MyCarActivity;
import com.qinggan.app.arielapp.ui.pin.add.AddPinActivity;
import com.qinggan.app.arielapp.ui.pin.check.VerfyPinActivity;
import com.qinggan.app.arielapp.ui.pin.findback.FindbackPinActivity;
import com.qinggan.app.arielapp.user.activity.AddressActivity;
import com.qinggan.app.arielapp.user.activity.LoginActivity;
import com.qinggan.app.arielapp.user.activity.WakeupSetActivity;
import com.qinggan.app.arielapp.utils.AppManager;
import com.qinggan.app.arielapp.vehiclecontrol.VehcleControlManager;
import com.staryea.ui.CerStepFourActivity;
import com.staryea.ui.CerStepOneActivity;
import com.staryea.ui.CerStepThreeActivity;
import com.staryea.ui.CerStepTwoActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * <互踢dialog>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-7]
 * @see [相关类/方法]
 * @since [V1]
 */
public class KickoutDialog extends Dialog {

    /**
     * 取消
     */
    public static final String KICK_CANCEL = "kick_cancel";

    /**
     * 重新登录
     */
    public static final String KICK_RELOGIN = "kick_relogin";

    public KickoutDialog(@NonNull Context context) {
        super(context, R.style.upgradeDialog);
    }

    Button cancelBtn, confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.view_kick_dialog);
        cancelBtn = findViewById(R.id.cancel);
        confirmBtn = findViewById(R.id.start);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出
                EventBus.getDefault().post(KICK_CANCEL);
                Log.d("KickoutDialog", "cancel ,open login start");
                dismiss();
                //离车模式退首页,驾驶模式当前页面退登
                if (!VehcleControlManager.getInstance(ArielApplication.getApp()).isInCarMode()) {
                    Log.d("KickoutDialog", "not in car,activity back to main");
                    AppManager.getAppManager().returnToActivity(MainActivity.class);
                }
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重新登录
                EventBus.getDefault().post(KICK_RELOGIN);
                dismiss();
                Log.d("KickoutDialog", "confirm ,open login start");
                LoginActivity.startAction(ArielApplication.getApp());
                AppManager.getAppManager().finishActivityList(new Class[]{FindbackPinActivity.class, AddPinActivity.class, VerfyPinActivity.class, ActiveCarActivity.class, BindCarActivity.class, MyCarActivity.class, WakeupSetActivity.class, AddressActivity.class, CerStepFourActivity.class, CerStepThreeActivity.class, CerStepTwoActivity.class, CerStepOneActivity.class});
                Log.d("KickoutDialog", "confirm ,open login end");
            }
        });
    }
}