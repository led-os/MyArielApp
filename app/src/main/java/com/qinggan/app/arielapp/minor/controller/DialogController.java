package com.qinggan.app.arielapp.minor.controller;

import android.os.Handler;

import com.qinggan.app.arielapp.minor.entity.DialogInfo;
import com.qinggan.app.arielapp.minor.utils.ArielLog;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by brian on 18-11-1.
 */

public class DialogController {
    private static final String TAG = DialogController.class.getSimpleName();
    private static DialogController mDialogController;
    private static final Object mSingleLock = new Object();

    private ConcurrentLinkedDeque<DialogInfo> mDialogInfoList = new ConcurrentLinkedDeque<>();
    private static final Object mExecuterLock = new Object();

    private DialogExecuter mExecuter;

    private boolean mHasDialogShowing = false;

    private DialogController(){
        mExecuter = new DialogExecuter();
    }

    public static DialogController getDialogController(){
        synchronized (mSingleLock) {
            if (mDialogController == null) {
                mDialogController = new DialogController();
            }
        }
        return mDialogController;
    }

    public void startDialogExecutor(){
        mExecuter.start();
    }

    public void setHasDialogShowing(boolean hasShowing){
        mHasDialogShowing = hasShowing;
    }

    public synchronized void addDialogInfoToList(DialogInfo dialogInfo) {
        mDialogInfoList.add(dialogInfo);
        notifyDialogExecutor();
    }

    private synchronized void notifyDialogExecutor(){
        mExecuterLock.notifyAll();
    }

    //对话框处理线程
    public class DialogExecuter extends Thread {
        public void run() {
            while (true) {
                DialogInfo info = mDialogInfoList.poll();

                if (info == null) {
                    try {
                        ArielLog.logController(ArielLog.LEVEL_DEBUG, TAG, "no dialog need to show, wait.");
                        mExecuterLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mHasDialogShowing) {
                        try {
                            mExecuterLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //TODO: alert a full screen dialog.
                }
            }
        }
    }

}
