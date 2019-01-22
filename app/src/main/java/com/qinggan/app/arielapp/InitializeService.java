package com.qinggan.app.arielapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.qinggan.app.arielapp.capability.upgrade.bugly.BuglyManager;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.tts.TtsHelper;

/**
 * Created by Yorashe on 18-11-29.
 */

public class InitializeService extends IntentService {
    private static final String ACTION_INIT_WHEN_APP_CREATE = "com.qinggan.app.arielapp.services.action.INIT";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public InitializeService() {
        super("InitializeService");
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_WHEN_APP_CREATE.equals(action)) {
                performInit();
            }
        }
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT_WHEN_APP_CREATE);
        context.startService(intent);
    }

    private void performInit() {
//        //push
//        PushManager.getInstance();
        //初始化友盟
        UMAnalyse.init(this);
        //初始化升级
    }
}