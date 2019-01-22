package com.qinggan.app.arielapp.minor.main.navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.qinggan.app.voiceapi.control.ConstantNavUc;

public class BdOpenAPIControl {

    private String TAG = BdOpenAPIControl.class.getSimpleName();

    private Context context;

    public int currVoiceCmd;
    private final int VOICE_NOT_EFFECTIVE = 0;//不响应语音
    public final int VOICE_ZOOM_IN = 1;//缩小地图
    public final int VOICE_ZOOM_OUT = 2;//放大地图
    private final int VOICE_OPENROADCONDITION = 4;//打开路况
    private final int VOICE_CLOSEROADCONDITION = 5;//关闭路况

    public BdOpenAPIControl(Context ctx) {
        context = ctx;
    }


    public void setVoiceCmd(String voiceCmd) {
        if (voiceCmd == null || voiceCmd.equals(""))
            return;
        if (voiceCmd.equals(ConstantNavUc.BAIDU_ZOOM_IN)) {
            currVoiceCmd = VOICE_ZOOM_IN;
        } else if (voiceCmd.equals(ConstantNavUc.BAIDU_ZOOM_OUT)) {
            currVoiceCmd = VOICE_ZOOM_OUT;
        } else if (voiceCmd.equals(ConstantNavUc.BAIDU_UI_OPEN_TMC)) {
            currVoiceCmd = VOICE_OPENROADCONDITION;
        } else if (voiceCmd.equals(ConstantNavUc.BAIDU_UI_CLOSE_TMC)) {
            currVoiceCmd = VOICE_CLOSEROADCONDITION;
        }
        doWork();
        Log.d(TAG, "currVoiceCmd==" + currVoiceCmd);

    }


    /**
     * 通过百度openAPI响应语音命令
     */
    private void doWork() {
        if (currVoiceCmd == VOICE_NOT_EFFECTIVE || BdMapUIcontrol.dringNaviStatus != BdMapUIcontrol.NAVI_STATUS_ING) {
            return;
        }
        String qt = null;
        Intent intent = new Intent();
        switch (currVoiceCmd) {
            case VOICE_ZOOM_IN:
                qt = "qt=rg_zoom_in";
                break;
            case VOICE_ZOOM_OUT:
                qt = "qt=rg_zoom_out";
                break;
            case VOICE_OPENROADCONDITION:
                qt = "qt=rg_open_navitrafficstatus";
                break;
            case VOICE_CLOSEROADCONDITION:
                qt = "qt=rg_close_navitrafficstatus";
                break;
        }
        StringBuilder loc = new StringBuilder();
        loc.append("baidumap://map/navi/instruction?");
        loc.append(qt);
        loc.append("&bdlog=true");
        loc.append("&src=andr.baidu.openAPIdemo");
        loc.append("&version=8.5");
        intent.setData(Uri.parse(loc.toString()));
        context.startActivity(intent);
        currVoiceCmd = VOICE_NOT_EFFECTIVE;
    }

}
