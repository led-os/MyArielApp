package com.qinggan.app.arielapp.minor.wechat.utils;

/**
 * Created on 18-12-25.
 */

public class WechatConstants {
    public static final int WECHAT_DEFAULT_STEP = 0;
    public static final int WECHAT_GET_NAME_STEP = 1;
    public static final int WECHAT_INPUT_MSG_STEP = 2;
    public static final int WECHAT_SEND_MSG_STEP = 3;

    public static final int REPLY_WECHAT_MSG_FLAG = 1;
    public static final int NEXT_WECHAT_MSG_FLAG = 2;
    public static final int SEND_WECHAT_MSG_FLAG = 3;
    public static final int CANCEL_WECHAT_MSG_FLAG = 4;
    public static final int REINPUT_WECHAT_MSG_FLAG = 5;
    public static final int OPEN_TTS_WECHAT_MSG_FLAG = 6;
    public static final int CLOSE_TTS_WECHAT_MSG_FLAG = 7;

    public static final String CONTACTS_NAME = "contacts_name";

    public static final String WECHAT_SEND_MSG_ORDER = "wechat_hotword_send_msg";
    public static final String WECHAT_CANCEL_SEND_ORDER = "wechat_hotword_cancel_send";

    //个人中心，是否显示接收微信消息 key
    public static final String MSG_RECEIVE_SAVE_STATUS_KEY = "wechat_receive_save_status";
    public static final int WECHAT_UI_SEND_STEP2 = 0;
    public static final int WECHAT_UI_REINPUT = 1;
    public static final int WECHAT_UI_NEXT_WECHAT = 3;


    public static final int INVALID_RESOURCE_ID = -1;
}
