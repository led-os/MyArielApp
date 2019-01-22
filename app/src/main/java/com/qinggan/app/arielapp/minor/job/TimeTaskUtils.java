package com.qinggan.app.arielapp.minor.job;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.qinggan.app.arielapp.minor.job.service.ArielTimeTaskService;
import com.qinggan.app.arielapp.utils.WLog;

import java.util.Calendar;

/**
 * Created by pateo on 18-12-8.
 */

public class TimeTaskUtils {

    private static final String TAG = TimeTaskUtils.class.getSimpleName();

    public static void startRepeatTimeTask(Context context, long triggerTime, long requestInterval,String taskType) {

        WLog.d(TAG, "startTimeTask begin");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ArielTimeTaskService.class);
        intent.putExtra(TimeTaskConstants.TASK_TYPE, taskType);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        try {
            manager.cancel(pendingIntent);
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, requestInterval, pendingIntent);
        } catch (Exception exception) {
            WLog.d(TAG, "startTimeTask exception:" + exception);
        }

        WLog.d(TAG, " startTimeTask end");
    }

    public static long getCardInfoTriggerTime() {
        long triggerTime = SystemClock.elapsedRealtime();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        Calendar calendar = Calendar.getInstance();
        if (hour >= 3 && hour < 15) {
            calendar.set(Calendar.HOUR_OF_DAY, 15);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 3);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }

        triggerTime = calendar.getTimeInMillis();

        return triggerTime;
    }

}
