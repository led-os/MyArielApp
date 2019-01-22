package com.qinggan.app.arielapp.capability.upgrade.ota;

import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.qinglink.api.Constant;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.SystemEventListener;
import com.qinggan.qinglink.api.md.SystemEventManager;
import com.qinggan.qinglink.bean.OTAFullPkg;
import com.qinggan.qinglink.bean.OTAPatchPkg;
import com.qinggan.qinglink.bean.OTAVersion;

import java.util.List;

/**
 * <OTA升级>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-21]
 * @see [相关类/方法]
 * @since [V1]
 */
public class OTAManager {
    private static final String TAG = OTAManager.class.getSimpleName();
    private static OTAManager instance;

    public static OTAManager getInstance() {
        if (null == instance) {
            synchronized (OTAManager.class) {
                if (null == instance)
                    instance = new OTAManager();
            }
        }
        return instance;
    }

    private SystemEventManager systemEventManager;
    private SystemEventListener systemEventListener;

    //for app
    private OTAUpgradeListener otaUpgradeListener = null;

    /**
     * 升级类型
     */
    private OTAUpgradeType upgradeType = OTAUpgradeType.DEFAULT;
    /**
     * 蓝牙连接状态
     */
    private boolean rfcommConnect;

    private OTAManager() {
        systemEventListener = new SystemEventListener() {
            @Override
            public void onResponseCurrentVersion(OTAVersion otaVersion) {
                Log.d(TAG, "onResponseCurrentVersion:otaVersion:" + otaVersion.toString());
                if (null == otaVersion) return;
                String upgradingMPUVersion = otaVersion.mpuVersion;
                String upgradingMCUVersion = otaVersion.mcuVersion;
                if (null != otaUpgradeListener)
                    otaUpgradeListener.onCurrentVersion(upgradingMPUVersion, upgradingMCUVersion);
            }

            @Override
            public void onResponseVersionFromLocal(OTAVersion otaVersion) {
                Log.d(TAG, "onResponseVersionFromLocal:otaVersion:" + otaVersion.toString());
                upgradeType = OTAUpgradeType.UDISK;
                String upgradingMPUVersion = otaVersion.mpuVersion;
                String upgradingMCUVersion = otaVersion.mcuVersion;
                if (null != otaUpgradeListener)
                    otaUpgradeListener.onNewVersion(true, upgradingMPUVersion, upgradingMCUVersion, null);
            }

            @Override
            public void onResponseVersionFromNetwork(OTAFullPkg otaFullPkg, List<OTAPatchPkg> patchPkgList, long totalSize, long sendTime) {
                Log.d(TAG, "onResponseVersionFromNetwork");
                upgradeType = OTAUpgradeType.NETWORK;
                if (null != otaFullPkg) {
                    Log.d(TAG, "onResponseVersionFromNetwork:otaFullPkg:" + otaFullPkg.toString());
                    String upgradingMPUVersion = otaFullPkg.version.mpuVersion;
                    String upgradingMCUVersion = otaFullPkg.version.mcuVersion;
                    String description = otaFullPkg.description;
                    if (null != otaUpgradeListener)
                        otaUpgradeListener.onNewVersion(false, upgradingMPUVersion, upgradingMCUVersion, description);
                } else {
                    if (null != patchPkgList && !patchPkgList.isEmpty()) {
                        Log.d(TAG, "onResponseVersionFromNetwork:patchPkgList size is:" + patchPkgList.size());
                        if (patchPkgList.size() == 1) {
                            Log.e(TAG, "OTAPatchPkg:" + patchPkgList.get(0));
                            String upgradingMPUVersion = patchPkgList.get(0).version.mpuVersion;
                            String upgradingMCUVersion = patchPkgList.get(0).version.mcuVersion;
                            String description = patchPkgList.get(0).description;
                            if (null != otaUpgradeListener)
                                otaUpgradeListener.onNewVersion(false, upgradingMPUVersion, upgradingMCUVersion, description);
                        } else {
                            OTAPatchPkg tmp = patchPkgList.get(0);
                            try {
                                for (OTAPatchPkg item : patchPkgList) {
                                    OTAVersion itemVersion = item.version;
                                    int result = tmp.version.compareTo(itemVersion);
                                    if (result < 0) tmp = item;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "OTAPatchPkg for Exception:" + e.getMessage());
                            }
                            Log.d(TAG, "OTAPatchPkg:" + tmp);
                            String upgradingMPUVersion = tmp.version.mpuVersion;
                            String upgradingMCUVersion = tmp.version.mcuVersion;
                            String description = tmp.description;
                            if (null != otaUpgradeListener)
                                otaUpgradeListener.onNewVersion(false, upgradingMPUVersion, upgradingMCUVersion, description);
                        }
                    } else {
                        Log.d(TAG, "onResponseVersionFromNetwork,list is null or empty");
                        if (null != otaUpgradeListener)
                            otaUpgradeListener.onUpgradeNoVersion();
                    }
                }
            }

            @Override
            public void onDownLoadProgress(int current, int total, long currentSize, final long totalSize) {
                Log.d(TAG, "onDownLoadProgress:current:" + current + " total:" + total);
                if (null != otaUpgradeListener)
                    otaUpgradeListener.onUpgrading(true, current, total);
            }

            @Override
            public void onUpgradeProgress(int current, int total) {
                Log.d(TAG, "onUpgradeProgress:current:" + current + " total:" + total);
                if (null != otaUpgradeListener)
                    otaUpgradeListener.onUpgrading(false, current, total);
            }

            @Override
            public void onUpgradeStateChanged(int state) {
                switch (state) {
                    case Constant.SystemEvent.State.STATE_IDLE:
                        Log.d(TAG, "onUpgradeStateChanged,case STATE_IDLE");
                        break;
                    case Constant.SystemEvent.State.STATE_CHECKING:
                        Log.d(TAG, "onUpgradeStateChanged,case STATE_CHECKING");
                        break;
                    case Constant.SystemEvent.State.STATE_CHECKING_NETWORK:
                        Log.d(TAG, "onUpgradeStateChanged,case STATE_CHECKING_NETWORK");
                        break;
                    case Constant.SystemEvent.State.STATE_DOWNLOADING:
                        Log.d(TAG, "onUpgradeStateChanged,case STATE_DOWNLOADING");
                        break;
                    case Constant.SystemEvent.State.STATE_UPGRADING:
                        Log.d(TAG, "onUpgradeStateChanged,case STATE_UPGRADING");
                        break;
                    case Constant.SystemEvent.State.STATE_UNZIPPING:
                        Log.d(TAG, "onUpgradeStateChanged,case STATE_UNZIPPING");
                        break;
                    case Constant.SystemEvent.State.STATE_REBOOTING:
                        Log.d(TAG, "onUpgradeStateChanged,case STATE_REBOOTING");
                        break;
                    default:
                        break;
                }
                if (null != otaUpgradeListener)
                    otaUpgradeListener.onUpgradeStateChanged(state);
            }

            @Override
            public void onError(int error, String errorMsg) {
                upgradeType = OTAUpgradeType.DEFAULT;
                int errId = 0;
                switch (error) {
                    case Constant.SystemEvent.Error.ERROR_CHECK_FAILED:
                        Log.d(TAG, "onError,case ERROR_CHECK_FAILED");
                        //升级包文件出错
                        errId = R.string.upgrade_check_failed;
                        break;
                    case Constant.SystemEvent.Error.ERROR_DOWNLOAD_FAILED:
                        Log.d(TAG, "onError,case ERROR_DOWNLOAD_FAILED");
                        //下载失败
                        errId = R.string.download_faild;
                        break;
                    case Constant.SystemEvent.Error.ERROR_NO_LOCAL_DEVICE:
                        Log.d(TAG, "onError,case ERROR_NO_LOCAL_DEVICE");
                        errId = R.string.o024;
//                        if (NetUtil.isNetworkConnected(ArielApplication.getApp())) {
//                            Log.d(TAG, "onError,case ERROR_NO_LOCAL_DEVICE,begin to requestCheckVersionFromNetwork()");
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //开始检查网络版本
//                                    systemEventManager.sendCheckVersionFromNetwork();
//                                }
//                            });
//                        } else {
//                            Log.d(TAG, "onError,case ERROR_NO_LOCAL_DEVICE,network issue");
//                            // 提示车机没有网络
//                            errId = R.string.update_no_network;
//                        }
                        break;
                    case Constant.SystemEvent.Error.ERROR_NO_NETWORK:
                        Log.d(TAG, "onError,case ERROR_NO_NETWORK");
                        // 提示车机没有网络
                        errId = R.string.update_no_network;
                        break;
                    case Constant.SystemEvent.Error.ERROR_NETWORK_UNAUTHORIZED:
                        //token失效
                        Log.d(TAG, "onError,case ERROR_NETWORK_UNAUTHORIZED");
                        // 提示鉴权失败
                        errId = R.string.update_unauthorized;
                        break;
                    case Constant.SystemEvent.Error.ERROR_NO_NEW_UPGRADE:
                        Log.d(TAG, "onError,case ERROR_NO_NEW_UPGRADE");
                        errId = R.string.o_usb;
//                        if (NetUtil.isNetworkConnected(ArielApplication.getApp())) {
//                            Log.d(TAG, "onError,case ERROR_NO_NEW_UPGRADE,begin to requestCheckVersionFromNetwork()");
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //开始检查网络版本
//                                    systemEventManager.sendCheckVersionFromNetwork();
//                                }
//                            });
//                        } else {
//                            Log.d(TAG, "onError,case ERROR_NO_NEW_UPGRADE,network issue");
//                            // 提示车机没有网络
//                            errId = R.string.update_no_network;
//                        }
                        break;
                    case Constant.SystemEvent.Error.ERROR_VERIFY_FAILED:
                        Log.d(TAG, "onError,case ERROR_VERIFY_FAILED");
                        //升级包文件校验失败
                        errId = R.string.upgrade_verify_failed;
                        break;
                    case Constant.SystemEvent.Error.ERROR_NO_LOCAL_FILE:
                        Log.d(TAG, "onError,case ERROR_NO_LOCAL_FILE");
                        errId = R.string.o_usb1;
//                        if (NetUtil.isNetworkConnected(ArielApplication.getApp())) {
//                            Log.d(TAG, "onError,case ERROR_NO_LOCAL_FILE,begin to requestCheckVersionFromNetwork()");
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //开始检查网络版本
//                                    systemEventManager.sendCheckVersionFromNetwork();
//                                }
//                            });
//                        } else {
//                            Log.d(TAG, "onError,case ERROR_NO_NEW_UPGRADE,network issue");
//                            // 提示车机没有网络
//                            errId = R.string.update_no_network;
//                        }
                        break;
                    case Constant.SystemEvent.Error.ERROR_NO_NEW_UPGRADE_NETWORK:
                        Log.d(TAG, "onError,case ERROR_NO_NEW_UPGRADE_NETWORK");
                        // 提示已经是最新版本
                        if (null != otaUpgradeListener)
                            otaUpgradeListener.onUpgradeNoVersion();
                        break;
                    case Constant.SystemEvent.Error.ERROR_DOWNLOAD_NO_SPACE_SDCARD:
                        Log.d(TAG, "onError,case ERROR_DOWNLOAD_NO_SPACE_SDCARD");
                        //提示本地没有足够的空间
                        errId = R.string.no_space_sdcard;
                        break;
                    case Constant.SystemEvent.Error.ERROR_DOWNLOAD_NO_SPACE_UDISK:
                        Log.d(TAG, "onError,case ERROR_DOWNLOAD_NO_SPACE_UDISK");
                        //本地空间和U盘空间都不足
                        errId = R.string.no_space_no_udisk;
                        break;
                }
                if (null != otaUpgradeListener)
                    otaUpgradeListener.onError(error, errId == 0 ? errorMsg : ArielApplication.getApp().getString(errId));
            }
        };

        //服务初始化状态
        OnInitListener initListener = new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {
                Log.d(TAG, "initListener onConnectStatusChange:" + b);
            }
        };
        //蓝牙连接状态
        OnConnectListener connectListener = new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                Log.d(TAG, "connectListener onConnect:" + b);
                rfcommConnect = b;
                if (rfcommConnect) {
                    //连接之后自动检查下新版本
                }
            }
        };

        systemEventManager = SystemEventManager.getInstance(ArielApplication.getApp(), initListener, connectListener);
        systemEventManager.registerListener(systemEventListener);
    }

    /**
     * 检查新版本
     */
    public void checkNewVersion() {
        try {
            Log.d(TAG, "checkVersion:rfcomm is connect?" + rfcommConnect);
            if (rfcommConnect) {
                Log.d(TAG, "checkVersion:sendCheckVersionFromLocal");
                systemEventManager.sendCheckVersionFromLocal();
            }
        } catch (Exception e) {
            Log.d(TAG, "checkNewVersion,ex:" + e.getMessage());
        }
    }

    /**
     * 开始升级
     */
    public void startUpgrade() {
        Log.d(TAG, "startUpgrade:rfcomm is connect?" + rfcommConnect + ",upgradeType:" + upgradeType);
        try {
            if (rfcommConnect) {
                if (upgradeType == OTAUpgradeType.NETWORK) {
                    //网络升级
                    systemEventManager.sendUpgradeFromNetwork();
                    return;
                }
                if (upgradeType == OTAUpgradeType.UDISK) {
                    //U盘升级
                    systemEventManager.sendUpgradeFromLocal();
                    return;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "startUpgrade,ex:" + e.getMessage());
        }
    }


    /**
     * for TestActivity
     */
    public void startTestUpgrade(){
        if (rfcommConnect) {
            Log.d(TAG, "startTestUpgrade sendUpgradeFromLocal");
            systemEventManager.sendUpgradeFromLocal();
        }
    }

    /**
     * 获取当前版本
     */
    public void getCurrentVersion() {
        Log.d(TAG, "getCurrentVersion:rfcomm is connect?" + rfcommConnect);
        try {
            if (rfcommConnect) {
                Log.d(TAG, "getCurrentVersion:sendGetCurrentVersion");
                systemEventManager.sendGetCurrentVersion();
            }
        } catch (Exception e) {
            Log.d(TAG, "getCurrentVersion,ex:" + e.getMessage());
        }
    }

    /**
     * 蓝牙是否连接
     *
     * @return
     */
    public boolean isRfcommConnect() {
        return rfcommConnect;
    }

    public void registOTAUpgradeListener(OTAUpgradeListener listener) {
        this.otaUpgradeListener = listener;
    }

    public void unRegistOTAUpgradeListener() {
        this.otaUpgradeListener = null;
    }
}
