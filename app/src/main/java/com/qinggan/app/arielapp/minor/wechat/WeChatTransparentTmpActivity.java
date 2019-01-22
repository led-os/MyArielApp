package com.qinggan.app.arielapp.minor.wechat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qinggan.app.virtualclick.utils.AppNameConstants;
import com.qinggan.app.arielapp.R;

import java.util.List;

/**
 * 这是一个临时的透明界面，为了解决在说第几个的时候显示，提供一个activity切换
 */

public class WeChatTransparentTmpActivity extends Activity {
    private String TAG = WeChatTransparentTmpActivity.class.getSimpleName() + "--alvin-";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        startWeChat(this, AppNameConstants.WECHAT_APP_PACKAGE_NAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    private void startWeChat(Context context, String packageName) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                System.out.println("---alvin--2--packageName---" + packageName);
                String className = ri.activityInfo.name;
                System.out.println("---alvin--2--className---" + className);

                Intent intent = new Intent(Intent.ACTION_MAIN);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                        .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                //重点是加这个
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
