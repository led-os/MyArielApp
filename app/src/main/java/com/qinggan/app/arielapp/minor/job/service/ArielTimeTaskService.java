package com.qinggan.app.arielapp.minor.job.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.qinggan.app.arielapp.minor.job.TimeTaskConstants;
import com.qinggan.app.arielapp.utils.WLog;

/**
 * Created by pateo on 18-12-7.
 */

public class ArielTimeTaskService extends Service {
    private static final String TAG = "ArielTimeTaskService";

    private static final int MSG_SYNC_CARD_INFO = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        WLog.d(TAG,"ArielTimeTaskService onStartCommand");

        String taskType = "";
        if (intent != null) {
            taskType = intent.getStringExtra(TimeTaskConstants.TASK_TYPE);
        }

        if (TimeTaskConstants.TASK_TYPE_SYNC_CARDINFO.equals(taskType)) {
            mJobHandler.sendEmptyMessage(MSG_SYNC_CARD_INFO);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    Handler mJobHandler = new Handler(new Handler.Callback() {
        // 在Handler中，需要实现handleMessage(Message msg)方法来处理任务逻辑。
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SYNC_CARD_INFO:
                    WLog.d(TAG,"MSG_SYNC_CARD_INFO");

                    break;
                default:
                    return false;
            }
            return true;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
