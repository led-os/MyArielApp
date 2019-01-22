package com.qinggan.app.arielapp.minor.main.navigation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.navigation.bean.BaiduUiControlEvent;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制百度地图的语音及其响应
 */
public class BdMapUIcontrol {

    public static int dringNaviStatus;//
    public static int walkNaviStatus;
    public static final int NAVI_STATUS_RESET = 0; //不表示任何状态
    public static final int NAVI_STATUS_ING = 1;//导航中
    public static int NAVI_STATUS_FINISHED = 2;//自然结束导航(或者用户触发)
    public static final int NAVI_STATUS_FINISHEDBYVIRTUAL = 3;//通过虚拟点击退出

    public static boolean baiduIsForeground;

    public static boolean isVirtualBack;


    private BdOpenAPIControl mBdOpenAPIControl;

    private BdServiceCheckTool mBdServiceCheckTool;

    private Context mContext;


    public BdMapUIcontrol(Context ctx) {
        mContext = ctx;
        if (mBdOpenAPIControl == null) {
            mBdOpenAPIControl = new BdOpenAPIControl(ctx);
        }
        if (mBdServiceCheckTool == null) {
            mBdServiceCheckTool = new BdServiceCheckTool(ctx);
        }

        mBdServiceCheckTool.checkService();

    }

    public int[] getBdVoiceCmd() {
        return voiceCmd;
    }


    public String[] getBdnavUc() {
        return navUc;
    }


    public List<UIControlElementItem> getBdCmdUIControl(int fragmentHashCode) {
        List<UIControlElementItem> controlElements = new ArrayList<>();
        for (int i = 0; i < voiceCmd.length; i++) {
            UIControlElementItem controlElementItem = new UIControlElementItem();
            controlElementItem.addWord(mContext.getString(voiceCmd[i]));
            controlElementItem.setIdentify(fragmentHashCode + "-" + navUc[i]);
            controlElements.add(controlElementItem);
        }
        return controlElements;
    }


    public void handleBdVoiceAction(String action) {
        switch (action) {
            case ConstantNavUc.NAV_EXIT:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_STOP_NAVI));
                break;

            case ConstantNavUc.BAIDU_UI_TIME_FAST:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_UI_TIME_FAST));
                break;
            case ConstantNavUc.BAIDU_UI_LESS_FEE:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_UI_LESS_FEE));
                break;
            case ConstantNavUc.BAIDU_UI_AVOID_TRAFFIC:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_UI_AVOID_TRAFFIC));
                break;
            case ConstantNavUc.BAIDU_UI_AVOID_HIGHT:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_UI_AVOID_HIGHT));
                break;
            case ConstantNavUc.BAIDU_UI_HIGHWAY:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_UI_HIGHWAY));
                break;
            case ConstantNavUc.BAIDU_UI_OPEN_TMC:
                mBdOpenAPIControl.setVoiceCmd(ConstantNavUc.BAIDU_UI_OPEN_TMC);
                break;
            case ConstantNavUc.BAIDU_UI_CLOSE_TMC:
                mBdOpenAPIControl.setVoiceCmd(ConstantNavUc.BAIDU_UI_CLOSE_TMC);
                break;
            case ConstantNavUc.BAIDU_UI_3D:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_UI_3D));
                break;
            case ConstantNavUc.BAIDU_UI_2D:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_UI_2D));
                break;
            case ConstantNavUc.BAIDU_STOP_NAVI:
                EventBus.getDefault().post(new BaiduUiControlEvent(ConstantNavUc.BAIDU_STOP_NAVI));
                break;

            case ConstantNavUc.BAIDU_ZOOM_IN:
                mBdOpenAPIControl.setVoiceCmd(ConstantNavUc.BAIDU_ZOOM_IN);
                break;
            case ConstantNavUc.BAIDU_ZOOM_OUT:
                mBdOpenAPIControl.setVoiceCmd(ConstantNavUc.BAIDU_ZOOM_OUT);
                break;
        }
    }


    private int[] voiceCmd = new int[]{
            R.string.navi_exit,
            R.string.navi_close,
            R.string.navi_exit,
            R.string.baiduui_less_time,
            R.string.baiduui_less_fee,
            R.string.avoidtrafficjam,
            R.string.avoidexpressway,
            R.string.firstexpressway,
            R.string.openroadcondition,
            R.string.closeroadcondition,
            R.string.baiduui_2d,
            R.string.baiduui_3d,
            R.string.baidustopnavi,
            R.string.baiduzoomin,
            R.string.baiduzoomout,
    };


    private String[] navUc = new String[]{
            ConstantNavUc.NAV_EXIT,
            ConstantNavUc.NAV_EXIT,
            ConstantNavUc.NAV_EXIT,
            ConstantNavUc.BAIDU_UI_TIME_FAST,
            ConstantNavUc.BAIDU_UI_LESS_FEE,
            ConstantNavUc.BAIDU_UI_AVOID_TRAFFIC,
            ConstantNavUc.BAIDU_UI_AVOID_HIGHT,
            ConstantNavUc.BAIDU_UI_HIGHWAY,
            ConstantNavUc.BAIDU_UI_OPEN_TMC,
            ConstantNavUc.BAIDU_UI_CLOSE_TMC,
            ConstantNavUc.BAIDU_UI_2D,
            ConstantNavUc.BAIDU_UI_3D,
            ConstantNavUc.BAIDU_STOP_NAVI,
            ConstantNavUc.BAIDU_ZOOM_IN,
            ConstantNavUc.BAIDU_ZOOM_OUT,
    };

}
