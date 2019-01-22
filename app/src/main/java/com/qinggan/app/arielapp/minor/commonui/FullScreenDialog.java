package com.qinggan.app.arielapp.minor.commonui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.TestAarActivity;
import com.qinggan.app.arielapp.minor.controller.DialogController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.minor.wechat.NoticeEvent;
import com.qinggan.app.arielapp.minor.wechat.NotificationBean;
import com.qinggan.app.arielapp.minor.wechat.utils.WeChatUtils;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 18-10-30.
 */

public class FullScreenDialog extends Activity implements View.OnClickListener {

    private final String TAG = FullScreenDialog.class.getSimpleName();
    private Button mReplyBtn, mIgnoreBtn;
    private NotificationBean mBean;

    private List<NotificationBean> mNotiList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogController.getDialogController().setHasDialogShowing(true);

        initView();
        PateoVirtualSDK.initialize(FullScreenDialog.this,
                IntegrationCore.getIntergrationCore(this));

        EventBus.getDefault().register(this);
    }

    private void initView() {
        Intent intent = getIntent();
        int dialogType = intent.getIntExtra(Constants.KEY_DIALOG_CONTENT,
                Constants.TYPE_INVAID);
        String notice = intent.getStringExtra(Constants.KEY_WECHAT_NOTICE);
        WeChatUtils mWeChatUtils = new WeChatUtils();
        mBean = mWeChatUtils.handleNotificationStrToBean(notice);

        int viewId = 0;
        switch (dialogType) {
            case Constants.TYPE_WECHAT_COME_MSG:
                viewId = R.layout.fragment_wechat_message;
                break;
            case Constants.TYPE_CAR_LOCK:
                viewId = R.layout.lock_layout;
                break;
        }
        setContentView(viewId);
       if(mBean!=null){
           mReplyBtn = this.findViewById(R.id.ask_button);
           mReplyBtn.setOnClickListener(this);

           mIgnoreBtn = this.findViewById(R.id.ignore_button);
           mIgnoreBtn.setOnClickListener(this);
       }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogController.getDialogController().setHasDialogShowing(false);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        switch (viewId) {
            case R.id.ask_button:
                doAction();
                finish();
                break;
            case R.id.ignore_button:
                Intent mIntent = new Intent(FullScreenDialog.this, TestAarActivity.class);
                startActivity(mIntent);
                finish();
                break;
        }
    }

    private void doAction() {
        if (!AccessibilityUtil.isAccessibilitySettingsOn(FullScreenDialog.this)) {
            this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }

        if (mBean == null) {
            IntegrationCore.getIntergrationCore(this).onFail("获取微信通知失败", ActionCode.NOT_FIND_APP);
            return ;
        }

        ActionBean actionBean = new ActionBean();
        actionBean.setActionCode(1001);
        actionBean.setAppName("微信");
        actionBean.setAddressee(mBean.getSender() != null ? mBean.getSender() : "老兄");
        actionBean.setAction("测试语句");

        PateoVirtualSDK.doAction(FullScreenDialog.this,
                actionBean, IntegrationCore.getIntergrationCore(this));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoticeEvent event) {
        Log.i(TAG, "message is " + event.getNotice());
        // 更新界面
        updateWeChatView(event.getNotice());
    }

    private void updateWeChatView(NotificationBean notice) {
        //1.更新通知列表
        mNotiList.add(notice);
        //2.通知超过一条时，显示下一条
        //3.暂未加还原状态的处理，需要在界面效果做的时候加入
    }
}
