package com.qinggan.app.arielapp.minor.wechat.inter;

/**
 * Created by pateo on 19-1-16.
 */

public interface WechatReceiveUICallback {

    void updateReceiveUI(int uiStatus);

    void finishUI();

    void intoReplyorInCome(boolean isReply);

    void updateVoiceStatus(boolean isSelected, int textResourceId);

    void autoReply();

    void setWechatAnswerMessageUI(int adapterCenterPionstion,int resourceId);
}