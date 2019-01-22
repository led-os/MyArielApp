package com.qinggan.app.arielapp.capability.upgrade.tspota;

import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.utils.ByteUtil;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.ota.OTACheckUpgradeRsp;
import com.qinggan.mobile.tsp.models.ota.OTACheckVersionRsp;
import com.qinggan.mobile.tsp.models.ota.OTAUpgradeStatusRsp;
import com.qinggan.mobile.tsp.models.ota.OTAUploadConfirmRsp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

import java.util.List;

/**
 * <tsp检查ota版本>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-10]
 * @see [相关类/方法]
 * @since [V1]
 */
public class TspOtaManager {

    private static final String TAG = TspOtaManager.class.getSimpleName();

    private static volatile TspOtaManager instance;

    public static TspOtaManager getInstance() {
        if (null == instance) {
            synchronized (TspOtaManager.class) {
                if (null == instance)
                    instance = new TspOtaManager();
            }
        }
        return instance;
    }

    private TspOtaListener listener;

    public void setListener(TspOtaListener listener) {
        this.listener = listener;
    }

    /**
     * 通知listener
     */
    public void notifyListener(boolean fromPush, String processId, String processStatus, String processDesc) {
        Log.d(TAG, "notifyListener,fromPush:" + fromPush + ",processId:" + processId + ",processStatus:" + processStatus + ",processDesc:" + processDesc);

        OTAErrorEnum otaErrorEnum = OTAErrorEnum.getOTAError(processStatus);
        if (null != otaErrorEnum) {
            Log.d(TAG, "notifyListener,otaErrorEnum");
            //主动去同步升级状态的时候,错误的状态,暂时不需要反馈出来
            if (fromPush && null != listener) {
                Log.d(TAG, "notifyListener,otaErrorEnum1");
                listener.onUpgradeProcess(processId, processStatus, ArielApplication.getApp().getString(otaErrorEnum.resId));
            }
            return;
        }

        if (!fromPush && ("09".equals(processStatus) || "12".equals(processStatus) || "14".equals(processStatus))) {
            //主动去同步升级状态的时候,最终的状态,暂时不需要反馈出来
            Log.d(TAG, "notifyListener,2");
            return;
        }

        OTAProgressEnum progressEnum = OTAProgressEnum.getOTAProgress(processStatus);
        if (null != progressEnum) {
            Log.d(TAG, "notifyListener,progressEnum");
            if (null != listener) {
                Log.d(TAG, "notifyListener,progressEnum1");
                listener.onUpgradeProcess(processId, processStatus, ArielApplication.getApp().getString(progressEnum.resId));
            }
            return;
        }

    }

    /**
     * 检查当前版本
     */
    public void getCurrentVersion(final String vin, final String pdsn) {
        Log.d(TAG, "getCurrentVersion:vin:" + vin + ",pdsn:" + pdsn);
        TspManager.getInstance(ArielApplication.getApp()).checkOTAVersion(vin, pdsn, new RestCallback<OTACheckVersionRsp>() {
            @Override
            public void success(OTACheckVersionRsp otaCheckVersionRsp, RestResponse restResponse) {
                if (null != otaCheckVersionRsp) {
                    if ("0".equals(otaCheckVersionRsp.getStatusCode())) {
                        if (null != otaCheckVersionRsp.getData()) {
                            String version = otaCheckVersionRsp.getData().getCurrentVersion();
                            Log.d(TAG, "getCurrentVersion:" + version);
                            if (null != listener)
                                listener.onRspCurrentVersion(version);
                            checkCurrentStatus(vin, pdsn);
                        }
                    } else {
                        if (null != listener)
                            listener.onError(otaCheckVersionRsp.getStatusMessage());
                    }
                } else {
                    if (null != listener)
                        listener.onError("");
                }
            }

            @Override
            public void failure(RestError restError) {
                if (null != listener)
                    listener.onError(restError.getKind() == RestError.Kind.NETWORK ? ArielApplication.getApp().getString(R.string.x53_no_network_tips) : "");
            }
        });
    }

    /**
     * 检查当前ota的状态
     */
    public void checkCurrentStatus(String vin, String pdsn) {
        Log.d(TAG, "checkCurrentStatus:vin:" + vin + ",pdsn:" + pdsn);
        TspManager.getInstance(ArielApplication.getApp()).checkOTAUpgradeStatus(vin, pdsn, new RestCallback<OTAUpgradeStatusRsp>() {
            @Override
            public void success(OTAUpgradeStatusRsp otaUpgradeStatusRsp, RestResponse restResponse) {
                if (null != otaUpgradeStatusRsp) {
                    if ("0".equals(otaUpgradeStatusRsp.getStatusCode())) {
                        if (null != otaUpgradeStatusRsp.getData()) {
                            //过程id
                            String processId = otaUpgradeStatusRsp.getData().getProcessId();
                            //状态
                            String processStatus = otaUpgradeStatusRsp.getData().getProcessStatus();
                            //描述
                            String processDesc = otaUpgradeStatusRsp.getData().getProcessDesc();
                            Log.d(TAG, "checkCurrentStatus:processId:" + processId + ",processStatus:" + processStatus + ",processDesc:" + processDesc);
                            notifyListener(false, processId, processStatus, processDesc);
                        }
                    } else {
                        if (null != listener)
                            listener.onError(otaUpgradeStatusRsp.getStatusMessage());
                    }
                } else {
                    if (null != listener)
                        listener.onError("");
                }
            }

            @Override
            public void failure(RestError restError) {
                if (null != listener)
                    listener.onError(restError.getKind() == RestError.Kind.NETWORK ? ArielApplication.getApp().getString(R.string.x53_no_network_tips) : "");
            }
        });
    }


    /**
     * 检查是否有新版本
     *
     * @param pdsn
     */
    public void checkHasNewVersion(String vin, String pdsn) {
        Log.d(TAG, "checkHasNewVersion:vin:" + vin + ",pdsn:" + pdsn);
        TspManager.getInstance(ArielApplication.getApp()).checkOTAUpgrade(vin, pdsn, new RestCallback<OTACheckUpgradeRsp>() {
            @Override
            public void success(OTACheckUpgradeRsp otaCheckUpgradeRsp, RestResponse restResponse) {
                if (null != otaCheckUpgradeRsp) {
                    if ("0".equals(otaCheckUpgradeRsp.getStatusCode())) {
                        if (null != otaCheckUpgradeRsp.getData()) {
                            try {
                                List<OTACheckUpgradeRsp.PatchProfile> patchProfileList = otaCheckUpgradeRsp.getData().getPatchProfile();
                                OTACheckUpgradeRsp.FullProfile fullProfile = otaCheckUpgradeRsp.getData().getFullProfile();

                                if (null != fullProfile && !TextUtils.isEmpty(fullProfile.getFileUrl())) {
                                    if (null != listener)
                                        listener.onRspNewVersion(fullProfile.getSwReference(), fullProfile.getReleaseNote(), TextUtils.isEmpty(fullProfile.getSwSize()) ? "0" : ByteUtil.getSize(Integer.parseInt(fullProfile.getSwSize())));
                                    return;
                                }
                                if (!patchProfileList.isEmpty()) {
                                    OTACheckUpgradeRsp.PatchProfile patchProfile = patchProfileList.get(0);
                                    if (null != listener)
                                        listener.onRspNewVersion(patchProfile.getSwReference(), patchProfile.getReleaseNote(), TextUtils.isEmpty(patchProfile.getSwSize()) ? "0" : ByteUtil.getSize(Integer.parseInt(patchProfile.getSwSize())));
                                    return;
                                }
                                //没有新版本
                                if (null != listener)
                                    listener.onRspNoUpgrade();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if ("500".equals(otaCheckUpgradeRsp.getStatusCode())) {
                        //没有新版本
                        if (null != listener)
                            listener.onRspNoUpgrade();
                    } else {
                        if (null != listener)
                            listener.onError(otaCheckUpgradeRsp.getStatusMessage());
                    }
                } else {
                    if (null != listener)
                        listener.onError("");
                }
            }

            @Override
            public void failure(RestError restError) {
                if (null != listener)
                    listener.onError(restError.getKind() == RestError.Kind.NETWORK ? ArielApplication.getApp().getString(R.string.x53_no_network_tips) : "");
            }
        });
    }

    /**
     * @param vin
     * @param pdsn
     * @param upgrade 0-取消升级 1-立即升级 2-延迟升级
     */
    public void startUpgrade(String vin, String pdsn, String upgrade) {
        Log.d(TAG, "startUpgrade:vin:" + vin + ",pdsn:" + pdsn);
        TspManager.getInstance(ArielApplication.getApp()).confirmUpgrade(vin, pdsn, upgrade, new RestCallback<OTAUploadConfirmRsp>() {
            @Override
            public void success(OTAUploadConfirmRsp otaUploadConfirmRsp, RestResponse restResponse) {

            }

            @Override
            public void failure(RestError restError) {

            }
        });
    }

}
