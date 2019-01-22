package com.qinggan.app.arielapp.ui.pin.findback;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.vcm.IdentifyCodeResp;
import com.qinggan.mobile.tsp.models.vhlcontrol.Status;
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
public class FindbackPinPresenter {
    private IFindbackPinView iView;


    /**
     * 获取验证码
     *
     * @param phone
     */
    public void getIdentifyCode(String phone) {
        TspManager.getInstance(ArielApplication.getApp()).getIdentifyCode(phone, new RestCallback<IdentifyCodeResp>() {
            @Override
            public void success(IdentifyCodeResp identifyCodeResp, RestResponse restResponse) {
                if (null == iView) return;
                if (null != identifyCodeResp) {
                    String code = identifyCodeResp.getStatus().getCode();
                    if ("0".equals(code)) {
                        iView.onIdentifyCodeSuccess();
                    } else {
                        iView.onIdentifyCodeFail(identifyCodeResp.getStatus().getDescription());
                    }
                } else {
                    iView.onIdentifyCodeFail(ArielApplication.getApp().getString(R.string.f_code8));
                }
            }

            @Override
            public void failure(RestError restError) {
                if (null == iView) return;
                iView.onIdentifyCodeFail(restError.getKind() == RestError.Kind.NETWORK ? ArielApplication.getApp().getString(R.string.no_network_tips) : ArielApplication.getApp().getString(R.string.f_code8));
            }
        });
    }

    /**
     * 校验短信验证码
     *
     * @param phone
     * @param code
     */
    public void checkIdentifyCode(String phone, String code) {
        TspManager.getInstance(ArielApplication.getApp()).checkMessageCode(phone, code, new RestCallback<Status>() {
            @Override
            public void success(Status status, RestResponse restResponse) {
                if (null == iView) return;
                if (null != status) {
                    if ("0".equals(status.getCode())) {
                        iView.onCheckIdentifyCodeSuccess();
                    } else
                        iView.onCheckIdentifyCodeFail(status.getDescription());
                }else
                    iView.onCheckIdentifyCodeFail(ArielApplication.getApp().getString(R.string.f_code8));
            }

            @Override
            public void failure(RestError restError) {
                if (null == iView) return;
                iView.onCheckIdentifyCodeFail(restError.getKind() == RestError.Kind.NETWORK ? ArielApplication.getApp().getString(R.string.no_network_tips) : ArielApplication.getApp().getString(R.string.f_code8));
            }
        });
    }


    public void attachView(IFindbackPinView iView) {
        this.iView = iView;
    }

    public void detachView() {
        this.iView = null;
    }

}
