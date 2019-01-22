package com.qinggan.app.arielapp.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.mobile.tsp.TspUtils;
import com.qinggan.mobile.tsp.auth.AuthLoginResponseItem;
import com.qinggan.mobile.tsp.bean.TokenBean;
import com.qinggan.mobile.tsp.bean.TokenUserInfo;
import com.qinggan.mobile.tsp.manager.TokenManager;
import com.qinggan.mobile.tsp.manager.TspManager;

/**
 * Created by Yorashe on 18-12-7.
 */

public class TokenUtils {
    private static TokenUtils sTokenUtils;
    private Context mContext;

    public static TokenUtils getInstance(Context context) {
        if (null == sTokenUtils) {
            sTokenUtils = new TokenUtils(context);
        }
        return sTokenUtils;
    }

    private TokenUtils(Context context) {
        this.mContext = context;
    }


    /**
     * 获取token失效时间
     * @param accessToken
     * @return
     */
    public  long getExpTime(String accessToken) {
        if (null == accessToken || (null != accessToken && accessToken.isEmpty()))
            return -1;
        String[] data = accessToken.split("\\.");
        String loginMsg = data[1];
        String loginMsgJson = TokenManager.getFromBASE64(loginMsg);
        Gson gson = new Gson();
        TokenUserInfo tokenUserInfo = gson.fromJson(loginMsgJson, TokenUserInfo.class);
        if(null != tokenUserInfo) {
            try {
                long time = Long.parseLong(TokenManager.standTimeToMillis(String.valueOf(tokenUserInfo.getExp())));
                return time;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


    //token 是否过期
    public boolean isExpired() {

        TokenBean tokenBean = transToTokenBean(ArielApplication.getmUserInfo().getAuthLoginResponseItem());
        if( tokenBean== null)
            return true;

        long curTime = TspUtils.getLTimeStamp();
        long expiredTime = tokenBean.getExpiredTime();

        if(curTime < expiredTime)
            return false;

        return true;
    }


    public void syncRefreshToken(){
        if (isExpired()){
            AuthLoginResponseItem responseItem =TspManager.getInstance(mContext).syncRefreshToken();
            UserInfo userInfo =ArielApplication.getmUserInfo();
            userInfo.setAuthLoginResponseItem(responseItem);
            ArielApplication.setmUserInfo(userInfo);
        }

    }


    public boolean needLogin() {
        if (ArielApplication.getmUserInfo()==null ||
                ArielApplication.getmUserInfo().getAuthLoginResponseItem()==null ){
            return false;
        }
        TokenBean tokenBean = transToTokenBean(ArielApplication.getmUserInfo().getAuthLoginResponseItem());
        if( tokenBean== null)
            return true;

        long curTime = TspUtils.getLTimeStamp();
        long expiredTime = tokenBean.getExpiredTimeToRefresh();
        if(curTime < expiredTime)
            return false;

        return true;
    }

//    syncRefreshToken
private TokenBean transToTokenBean(AuthLoginResponseItem authLoginResponseItem) {
    TokenBean tokenBean = null;
    if (authLoginResponseItem != null && authLoginResponseItem.getAccesstoken() != null) {
        tokenBean = new TokenBean();
        tokenBean.setToken(authLoginResponseItem.getAccesstoken());
        tokenBean.setRefreshToken(authLoginResponseItem.getRefreshtoken());
        long curTime = TspUtils.getLTimeStamp();
//            tokenBean.setExpiredTime(getExpTime(getFromBASE64(authLoginResponseItem.getAccesstoken())));
        tokenBean.setExpiredTime(getExpTime(tokenBean.getToken()));
        tokenBean.setExpiredTimeToRefresh(getExpTime(tokenBean.getRefreshToken()));
        Log.i("token", "token: [" + tokenBean.toString() + "] curTime:[" + curTime + "] expTime[" + tokenBean.getExpiredTime() + "]");
    }
    return tokenBean;
}

}
