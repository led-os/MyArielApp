package com.qinggan.app.arielapp.capability.upgrade.bugly;

import android.util.Log;

import com.qinggan.app.arielapp.ActivityLifecycleListener;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BuildConfig;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.notification.ArielNotification;
import com.qinggan.app.arielapp.capability.upgrade.UpgradeManager;
import com.qinggan.app.arielapp.ui.widget.upgrade.AppUpgradeDialog;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;

/**
 * <Bugly升级>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-6]
 * @see [相关类/方法]
 * @since [V1]
 */
public class BuglyManager {

    public static final String TAG = BuglyManager.class.getSimpleName();
    private static volatile BuglyManager instance;

    private BuglyUpgradeListener listener;

    public static BuglyManager getInstance() {
        if (null == instance) {
            synchronized (BuglyManager.class) {
                if (null == instance)
                    instance = new BuglyManager();
            }
        }
        return instance;
    }

    public BuglyManager() {
    }

    /**
     * 初始化bugly
     */
    public void init() {
        //================全量更新=============
        /**** Beta高级设置*****/
        /**
         * true表示app启动自动初始化升级模块；
         * false不好自动初始化
         * 开发者如果担心sdk初始化影响app启动速度，可以设置为false
         * 在后面某个时刻手动调用
         */
        Beta.autoInit = true;

        /**
         * true表示初始化时自动检查升级
         * false表示不会自动检查升级，需要手动调用Beta.checkUpgrade()方法
         */
        Beta.autoCheckUpgrade = true;
        /**
         * 设置升级周期为60s（默认检查周期为0s），60s内SDK不重复向后天请求策略
         */
        Beta.upgradeCheckPeriod = 5 * 1000;

        Beta.initDelay = 1 * 1000;

        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源；
         */
        Beta.largeIconId = R.mipmap.ic_launcher;

        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源id;
         */
        Beta.smallIconId = R.mipmap.ic_launcher;


        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = R.mipmap.ic_launcher;


        /**
         * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = true;
        //设置是否显示消息通知,如果你不想在通知栏显示下载进度，你可以将这个接口设置为false，默认值为true
        Beta.enableNotification = false;
        //如果你使用我们默认弹窗是会显示apk信息的，如果你不想显示可以将这个接口设置为false。
        Beta.canShowApkInfo = true;

        /*在application中初始化时设置监听，监听策略的收取*/
        //设置这个属性之后,不会弹出bugly默认的提示窗口
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                Log.e(TAG, "upgradeListener onUpgrade isManual:" + isManual + " isSilence:" + isSilence);
                if (null != strategy) {
                    UpgradeManager.getUpgradeModel().setAppNewVersion(true);
                    //主动去检查,弹窗
                    new AppUpgradeDialog(ActivityLifecycleListener.currentActivity, strategy).show();
                }
            }
        };

        //设置这个属性之后,不会弹出bugly默认的toast
        Beta.upgradeStateListener = new UpgradeStateListener() {
            @Override
            public void onUpgradeFailed(boolean isManual) {
                Log.d(TAG, "onUpgradeFailed");
                if (null != listener)
                    listener.onCheckFailed();
            }

            @Override
            public void onUpgradeSuccess(boolean isManual) {
                Log.d(TAG, "onUpgradeSuccess");
                if (null != listener)
                    listener.onNewVersion(Beta.getUpgradeInfo());
            }

            @Override
            public void onUpgradeNoVersion(boolean isManual) {
                Log.d(TAG, "onUpgradeNoVersion");
                UpgradeManager.getUpgradeModel().setAppNewVersion(false);
                if (null != listener)
                    listener.onUpgradeNoVersion();
            }

            @Override
            public void onUpgrading(boolean isManual) {
                Log.d(TAG, "onUpgrading");
                if (null != listener)
                    listener.onCheckVersion();
            }

            @Override
            public void onDownloadCompleted(boolean isManual) {
                Log.d(TAG, "onDownloadCompleted");
            }
        };
        //==================热修复==================
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true;
        // 设置是否自动下载补丁
        Beta.canAutoDownloadPatch = true;
        // 设置是否提示用户重启
        Beta.canNotifyUserRestart = false;
        // 设置是否自动合成补丁
        Beta.canAutoPatch = true;
        /**
         * 补丁回调接口，可以监听补丁接收、下载、合成的回调
         */
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFileUrl) {
                Log.d(TAG, "BetaPatchListener onPatchReceived:" + patchFileUrl);
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                Log.d(TAG, "BetaPatchListener onDownloadReceived:savedLength:" + savedLength + ",totalLength:" + totalLength);
            }

            @Override
            public void onDownloadSuccess(String patchFilePath) {
                Log.d(TAG, "BetaPatchListener onDownloadSuccess:" + patchFilePath);
//                Beta.applyDownloadedPatch();
            }

            @Override
            public void onDownloadFailure(String msg) {
                Log.d(TAG, "BetaPatchListener onDownloadFailure:" + msg);
            }

            @Override
            public void onApplySuccess(String msg) {
                Log.d(TAG, "BetaPatchListener onApplySuccess:" + msg);
            }

            @Override
            public void onApplyFailure(String msg) {
                Log.d(TAG, "BetaPatchListener onApplyFailure:" + msg);
            }

            @Override
            public void onPatchRollback() {
                Log.d(TAG, "BetaPatchListener onPatchRollback---");
            }
        };
        long start = System.currentTimeMillis();
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId,调试时将第三个参数设置为true
        Bugly.init(ArielApplication.getApp(), "c5f4abd2a3", true);
        long end = System.currentTimeMillis();
        Log.e(TAG, "init time--->" + (end - start) + "ms");
//        Bugly.setIsDevelopmentDevice(context.getApplicationContext(), true);
    }

    public void registDownloadListener() {
        Beta.registerDownloadListener(downloadListener);
    }

    DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onReceive(DownloadTask downloadTask) {
            updateNotification(downloadTask);
        }

        @Override
        public void onCompleted(DownloadTask downloadTask) {
            updateNotification(downloadTask);
        }

        @Override
        public void onFailed(DownloadTask downloadTask, int i, String s) {
            updateNotification(downloadTask);
        }
    };

    public void updateNotification(DownloadTask task) {
        Log.d(TAG, "updateBtn task status:" + task.getStatus());
        switch (task.getStatus()) {
            case DownloadTask.INIT:
            case DownloadTask.DELETED:
            case DownloadTask.FAILED: {
                //开始下载
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v3));
            }
            break;
            case DownloadTask.COMPLETE: {
                //安装
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v4));
            }
            break;
            case DownloadTask.DOWNLOADING: {
                //正在下载
                String progress = String.valueOf(task.getSavedLength() * 100 / task.getTotalLength());
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v5) + " " + progress + "%");
            }
            break;
            case DownloadTask.PAUSED: {
                //暂停
                ArielNotification.getInstance().createNotification(ArielApplication.getApp().getString(R.string.v14));
            }
            break;
            default:
                break;
        }
    }


    /**
     * 检查新版本
     */
    public boolean checkNewVersion() {
        Log.d(TAG, "checkNewVersion:" + Beta.getUpgradeInfo());
        //手动检查,有新版本不给任何bugly的提示
        boolean isDownloading = isDownloading();
        Log.d(TAG, "checkNewVersion:isDownloading:" + isDownloading);
        if (isDownloading) return true;
        Beta.checkUpgrade(true, true);
        return false;
    }

    /**
     * 开始升级
     */
    public void startUpgrade() {
        Log.d(TAG, "startUpgrade");
        Beta.startDownload();
    }

    /**
     * 是否在升级
     */
    public boolean isDownloading() {
        Log.d(TAG, "isDownloading");
        return null == Beta.getStrategyTask() ? false : (Beta.getStrategyTask().getStatus() == DownloadTask.DOWNLOADING);
    }

    /**
     * 获取当前版本
     */
    public String getCurrentVersion() {
        Log.d(TAG, "getCurrentVersion:" + BuildConfig.VERSION_NAME);
        return BuildConfig.VERSION_NAME;
    }


    public void registBuglyUpgradeListener(BuglyUpgradeListener listener) {
        this.listener = listener;
    }

    public void unRegistBuglyUpgradeListener(BuglyUpgradeListener listener) {
        this.listener = null;
    }

    /**
     * 升级的版本信息
     */
    private void loadUpgradeInfo() {

        /***** 获取升级信息 *****/
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();

        StringBuilder info = new StringBuilder();
        info.append("id: ").append(upgradeInfo.id).append("\n");
        info.append("标题: ").append(upgradeInfo.title).append("\n");
        info.append("升级说明: ").append(upgradeInfo.newFeature).append("\n");
        info.append("versionCode: ").append(upgradeInfo.versionCode).append("\n");
        info.append("versionName: ").append(upgradeInfo.versionName).append("\n");
        info.append("发布时间: ").append(upgradeInfo.publishTime).append("\n");
        info.append("安装包Md5: ").append(upgradeInfo.apkMd5).append("\n");
        info.append("安装包下载地址: ").append(upgradeInfo.apkUrl).append("\n");
        info.append("安装包大小: ").append(upgradeInfo.fileSize).append("\n");
        info.append("弹窗间隔（ms）: ").append(upgradeInfo.popInterval).append("\n");
        info.append("弹窗次数: ").append(upgradeInfo.popTimes).append("\n");
        info.append("发布类型（0:测试 1:正式）: ").append(upgradeInfo.publishType).append("\n");
        info.append("弹窗类型（1:建议 2:强制 3:手工）: ").append(upgradeInfo.upgradeType).append("\n");
        info.append("图片地址：").append(upgradeInfo.imageUrl);
    }
}
