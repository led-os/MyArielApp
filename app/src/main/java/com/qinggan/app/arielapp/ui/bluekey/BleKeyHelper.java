package com.qinggan.app.arielapp.ui.bluekey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.user.Bean.BindInfo;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.bluekey.encrypt.KeyStoreHelper;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.protocol.BleProtocol;
import com.qinggan.bluekey.service.BleCarKey;
import com.qinggan.bluekey.service.BlueKeyListener;
import com.qinggan.bluekey.util.BleUtils;
import com.qinggan.mobile.tsp.bean.BaseBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.bluekey.ApplyResp;
import com.qinggan.mobile.tsp.models.bluekey.BluetoothKeysBean;
import com.qinggan.mobile.tsp.models.bluekey.DataResp;
import com.qinggan.mobile.tsp.models.bluekey.DestoryReq;
import com.qinggan.mobile.tsp.models.bluekey.KeyQueryResp;
import com.qinggan.mobile.tsp.models.bluekey.UploadReq;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class BleKeyHelper extends BleUtils {
    public static final String TAG = BleKeyHelper.class.getSimpleName();

    public static Context mContext = ArielApplication.getApp().getApplicationContext();


    public static BleCarKey getBleCarKey() {
        return mBleCarKey;
    }

    public static void updateBleKey() {
        UserInfo mUserInfo = ArielApplication.getmUserInfo();
        mBleCarKey = null;

        if (mUserInfo != null) {
            mUserInfo.isBlueKeyEnable = false;
            BluetoothKeysBean keyBean = mUserInfo.getmBluetoothKeysBean();
            if (keyBean != null && keyBean.getBluetoothSensitiveInfo() != null) {

                //帐号名下临时钥匙上传过云端，起码算是永久钥匙了
                Log.e(TAG,"keyBean.getUserPubKey()="+keyBean.getUserPubKey());
                Log.e(TAG,"KeyStoreHelper.getRSAPublic()="+KeyStoreHelper.getInstance(mContext).getRSAPublic());
                if (keyBean.getUserPubKey().equals(
                        KeyStoreHelper.getInstance(mContext).getRSAPublic())) {
                    mBleCarKey = new BleCarKey();
                    String base64AccessKey = keyBean.getBluetoothSensitiveInfo().getBluetoothAccessKey();
                    mBleCarKey.bleAccessKeyEncode = base64AccessKey;
                    Log.e(TAG, "getBleCarKey() base64AccessKey=" + base64AccessKey);
                    if (!TextUtils.isEmpty(base64AccessKey)) {
                        byte[] keyBufEncode = Base64.decode(base64AccessKey, Base64.NO_WRAP);
                        if (keyBufEncode != null) {
                            Log.e(TAG, "getBleCarKey() keyBuf=" + BleProtocol.bytesToHexString(keyBufEncode));
                            mBleCarKey.blePassword = KeyStoreHelper.getInstance(mContext).RSADecrypt(keyBufEncode);
                            if (mBleCarKey.blePassword != null) {
                                Log.e(TAG, "getBleCarKey() blePassword=" + BleProtocol.bytesToHexString(mBleCarKey.blePassword));
                                mBleCarKey.bleKeyId = Integer.parseInt(keyBean.getBleKeyId());
                                mBleCarKey.bleName = keyBean.getBluetoothSensitiveInfo().getBluetoothConName();
                                mBleCarKey.bleConnectKey = keyBean.getBluetoothSensitiveInfo().getBluetoothConKey();
                                if (mUserInfo.isCarOwner()) {
                                    mUserInfo.isBlueKeyEnable = true;
                                    mBleCarKey.setKeyType(BleCarKey.KEY_TYPE_OWNER);
                                } else {
                                    mUserInfo.isBlueKeyEnable = true;
                                    mBleCarKey.setKeyType(BleCarKey.KEY_TYPE_FOREVER);
                                }
                                BleKeyManager.getInstance(mContext).setAutoPopUnlock(mUserInfo.autoPopUnlock);
                                Log.e(TAG, "getBleCarKey()  mUserInfo.isBlueKeyEnable="+ mUserInfo.isBlueKeyEnable);
                            } else {
                                Log.e(TAG, "getBleCarKey() RSADecrypt error!!!");
                            }
                        } else {
                            Log.e(TAG, "getBleCarKey() Base64 error!!!");
                        }
                    }
                } else {
                    Log.e(TAG, "getBleCarKey() getRSAPublic NOT EQUAL!!!");
                }
            } else {
                Log.e(TAG, "getBleCarKey() keyBean Invalid!");
            }
        }
        if (mBleCarKey == null) {
            mBleCarKey = BleUtils.readTempKey(mContext);
            if (mBleCarKey != null) {
                int diff = compare_date(BleKeyHelper.getCurrentDate(), mBleCarKey.getExpireDate());
                if (diff > 0) {
                    Log.e(TAG, "getBleCarKey() TempKey Expired:" + mBleCarKey.getExpireDate());
                    mBleCarKey = null;
                }
            }
        }
        EventBus.getDefault().post("BleKeyTypeUpdate");
    }


    /**
     * 是否有车钥匙
     *
     * @return
     */
    public static boolean hasKey() {
        return null != BleKeyHelper.getBleCarKey();
    }

    /**
     * 是否有临时钥匙
     *
     * @return
     */
    public static boolean isTmpKey() {
        return hasKey() && BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_TEMP;
    }

    /**
     * 是否有永久钥匙
     *
     * @return
     */
    public static boolean isForverKey() {
        return hasKey() && BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_FOREVER;
    }

    /**
     * 是否有车主钥匙
     *
     * @return
     */
    public static boolean isCarOwnerKey() {
        return hasKey() && BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_OWNER;
    }
}
