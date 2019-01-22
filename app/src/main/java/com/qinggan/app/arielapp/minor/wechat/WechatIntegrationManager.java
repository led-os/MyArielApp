package com.qinggan.app.arielapp.minor.wechat;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;
import com.qinggan.app.voiceapi.nluresult.wechat.WechatCallback;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;
import com.qinggan.qinglink.bean.UIControlElementItem;

import java.util.ArrayList;

/**
 * Created by pateo on 19-1-15.
 */

public class WechatIntegrationManager {

    private static final String TAG = WechatIntegrationManager.class.getSimpleName();
    private static WechatIntegrationManager instance;
    public final static String VOICE_SEND_WECHAT = "voice_send_wechat_new";

    private static Context mContext;
    private WechatCallbackImpl wechatCallbackImpl;
    private static HotwordManager mHotwordManager;
    private static WindowManager mWindowManager;
    private static View mView;
    private static WindowManager.LayoutParams mWheelViewParams;

    private WechatIntegrationManager() {

    }

    public static WechatIntegrationManager getInstance(Context context, HotwordManager hotwordManager,
                                                       WindowManager windowManager, View view,
                                                       WindowManager.LayoutParams wheelViewParams) {
        mContext = context;
        mHotwordManager = hotwordManager;
        mWindowManager = windowManager;
        mView = view;
        mWheelViewParams = wheelViewParams;
        if (instance == null) {
            synchronized (WechatIntegrationManager.class) {
                if (instance == null)
                    instance = new WechatIntegrationManager();
            }
        }
        return instance;
    }

    public WechatCallback createWechatCallback(Context context) {
        wechatCallbackImpl = new WechatCallbackImpl(context, instance, mHotwordManager, mWindowManager, mView, mWheelViewParams);
        return wechatCallbackImpl;
    }

    public void addWakeupElementsforWeChat() {
        if (mHotwordManager == null) {
            return;
        }

        Log.d(TAG, " addWakeupElements ");

        ArrayList<UIControlElementItem> elementItems =
                new ArrayList<>();
        int[] select_words = {R.string.navi_first, R.string.navi_second, R.string.navi_third};
        for (int i = 0; i < select_words.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem selectWechatItem =
                    new com.qinggan.qinglink.bean.UIControlElementItem();
            selectWechatItem.setWord(mContext.getString(select_words[i]));
            selectWechatItem.setIdentify("" + (i + 1));
            elementItems.add(selectWechatItem);
        }
        mHotwordManager.setElementUCWords(VOICE_SEND_WECHAT, elementItems);
        mHotwordManager.registerListener(VOICE_SEND_WECHAT, new HotwordListener() {
            @Override
            public void onItemSelected(String identify) {
                onSelectOtherOC(identify);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onSwitchPage(int i) {

            }
        });
    }


    public void onSelectOtherOC(String action) {
        Log.e(TAG, "onSelectOtherOC : " + action);
        switch (action) {
            case "1":
            case "2":
            case "3":
                mHandler.sendEmptyMessage(Integer.parseInt(action));
                break;
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg.what : " + msg.what);
            mWindowManager.removeViewImmediate(mView);
            Intent mIntent = new Intent(mContext, WeChatTransparentTmpActivity.class);
            mContext.startActivity(mIntent);
            handleVirtual(ActionCode.WECHAT_SELECT_INPUT_SEND, String.valueOf(msg.what), wechatCallbackImpl.getmWechatMsg());
            if (null != mHotwordManager) {
                mHotwordManager.clearElementUCWords(VOICE_SEND_WECHAT);
            }
        }
    };

    private void handleVirtual(int actionCode, String name, String action) {
        ActionBean actionBean = new ActionBean();
        switch (actionCode) {
            case ActionCode.WECHAT_SEARCH_PERSON:
                actionBean.setAddressee(name);
                break;
            case ActionCode.WECHAT_SELECT_CONTACTS:
                actionBean.setAction(action);
                break;
            case ActionCode.WECHAT_SEND_MSG:
                actionBean.setAction(action);
                break;
            case ActionCode.WECHAT_SEND_POSITION:
                //TODO
                break;
            case ActionCode.WECHAT_SEND_MONEY:
                //TODO
                break;
            case ActionCode.WECHAT_INPUT_SEND_MSG:
                actionBean.setAction(action);
                break;
            case ActionCode.WECHAT_CONFIRM_SEND:
                //TODO
                break;
            case ActionCode.WECHAT_SELECT_INPUT_SEND:
                actionBean.setAddressee(name);
                actionBean.setAction(action);
                break;
        }

        actionBean.setActionCode(actionCode);
        AccessibilityUtil.initAccessibility(mContext);
        PateoVirtualSDK.doAction(mContext,
                actionBean, IntegrationCore.getIntergrationCore(mContext));
    }
}
