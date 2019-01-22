package com.qinggan.app.arielapp.ui.widget.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.upgrade.tspota.TspOtaManager;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.utils.ByteUtil;
import com.qinggan.mobile.tsp.models.ota.OTACheckUpgradeRsp;

import java.util.List;

/**
 * <ota升级对话框>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-3]
 * @see [相关类/方法]
 * @since [V1]
 */
public class OTAUpgradeDialog extends Dialog {
    private static final String TAG = OTAUpgradeDialog.class.getSimpleName();

    public OTAUpgradeDialog(@NonNull Context context) {
        super(context, R.style.upgradeDialog);
    }

    public OTAUpgradeDialog(@NonNull Context context, OTACheckUpgradeRsp.PackageInfo packageInfo) {
        super(context, R.style.upgradeDialog);
        this.packageInfo = packageInfo;
    }

    OTACheckUpgradeRsp.PackageInfo packageInfo;
    TextView version, desc, size;
    Button cancelBtn, confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_ota_upgrade_dialog);
        version = findViewById(R.id.version);
        size = findViewById(R.id.size);
        desc = findViewById(R.id.desc);
        cancelBtn = findViewById(R.id.cancel);
        confirmBtn = findViewById(R.id.start);

        OTACheckUpgradeRsp.FullProfile fullProfile = packageInfo.getFullProfile();
        if (null != fullProfile && !TextUtils.isEmpty(fullProfile.getFileUrl())) {
            version.setText(String.format(getContext().getString(R.string.v2), fullProfile.getSwReference()));
            desc.setText(fullProfile.getReleaseNote());
            size.setText(String.format(getContext().getString(R.string.v10), TextUtils.isEmpty(fullProfile.getSwSize()) ? "0" : ByteUtil.getSize(Integer.parseInt(fullProfile.getSwSize()))));
        } else {
            List<OTACheckUpgradeRsp.PatchProfile> patchProfiles = packageInfo.getPatchProfile();
            version.setText(String.format(getContext().getString(R.string.v2), patchProfiles.get(0).getSwReference()));
            desc.setText(patchProfiles.get(0).getReleaseNote());
            size.setText(String.format(getContext().getString(R.string.v10), TextUtils.isEmpty(patchProfiles.get(0).getSwSize()) ? "0" : ByteUtil.getSize(Integer.parseInt(patchProfiles.get(0).getSwSize()))));
        }
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncCommond("0");
                dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncCommond("1");
                dismiss();
            }
        });
    }

    /**
     * 上传指令给云端
     *
     * @param commond
     */
    private void syncCommond(String commond) {
        String vin = ArielApplication.getmUserInfo() == null ? "" : ArielApplication.getmUserInfo().getVin();
        String pdsn = BindVehicleInfo.getPdsn();
        Log.d(TAG, "syncCommond:vin:" + vin + ",pdsn:" + pdsn);
        if (TextUtils.isEmpty(vin) || TextUtils.isEmpty(pdsn)) {
            return;
        }
        TspOtaManager.getInstance().startUpgrade(vin, pdsn, commond);
    }
}
