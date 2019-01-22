package com.qinggan.app.arielapp.ui.pin.add;

import android.text.TextUtils;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.safecode.AddSafecodeRsp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-19]
 * @see [相关类/方法]
 * @since [V1]
 */
public class AddPinPresenter {
    private IAddPinView iView;

    /**
     * 设置或修改pin
     *
     * @param pin
     */
    public void modifyPin(String pin) {
        if (null == ArielApplication.getmUserInfo()) return;
        TspManager.getInstance(ArielApplication.getApp()).addOrUpdatePin(ArielApplication.getmUserInfo().getVin(), pin, new RestCallback<AddSafecodeRsp>() {
            @Override
            public void success(AddSafecodeRsp addSafecodeRsp, RestResponse restResponse) {
                if (null == iView) return;
                if (null != addSafecodeRsp) {
                    if ("0".equals(addSafecodeRsp.getStatusCode())) {
                        //成功
                        iView.modifyPinSuccess();
                    } else {
                        iView.modifyPinFail(addSafecodeRsp.getStatusMessage());
                    }
                } else iView.modifyPinFail(ArielApplication.getApp().getString(R.string.f_code8));
            }

            @Override
            public void failure(RestError restError) {
                if (null == iView) return;
                iView.modifyPinFail(restError.getKind() == RestError.Kind.NETWORK ? ArielApplication.getApp().getString(R.string.no_network_tips) : ArielApplication.getApp().getString(R.string.f_code8));
            }
        });
    }


    /**
     * 校验安全码
     *
     * @param pin
     */
    public void checkPin(String pin) {
        if (null == ArielApplication.getmUserInfo()) return;
        TspManager.getInstance(ArielApplication.getApp()).checkPin(ArielApplication.getmUserInfo().getVin(), pin, new RestCallback<AddSafecodeRsp>() {
            @Override
            public void success(AddSafecodeRsp addSafecodeRsp, RestResponse restResponse) {
                if (null == iView) return;
                if (null != addSafecodeRsp) {
                    String status = addSafecodeRsp.getStatusCode();
                    if ("0".equals(status)) {
                        //成功
                        iView.checkPinSuccess();
                    } else if ("4".equals(status)) {
                        //错误,data剩余次数
                        iView.checkPinError(TextUtils.isEmpty(addSafecodeRsp.getData()) ? 0 : Integer.parseInt(addSafecodeRsp.getData()));
                    } else if ("3".equals(status) || "5".equals(status)) {
                        //错误次数已达上限
                        iView.checkPinError(0);
                    } else {
                        iView.checkPinFail(addSafecodeRsp.getStatusMessage());
                    }
                } else iView.checkPinFail(ArielApplication.getApp().getString(R.string.f_code8));
            }

            @Override
            public void failure(RestError restError) {
                if (null == iView) return;
                iView.checkPinFail(restError.getKind() == RestError.Kind.NETWORK ? ArielApplication.getApp().getString(R.string.no_network_tips) : ArielApplication.getApp().getString(R.string.f_code8));
            }
        });
    }


    public void attachView(IAddPinView iView) {
        this.iView = iView;
    }

    public void detachView() {
        this.iView = null;
    }
}
