package com.qinggan.app.arielapp.user.Bean;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.ui.bluekey.BleKeyHelper;
import com.qinggan.app.arielapp.ui.bluekey.BlueKeyEvent;
import com.qinggan.app.arielapp.user.UserInfoDBHelper;
import com.qinggan.bluekey.encrypt.KeyStoreHelper;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.service.BleCarKey;
import com.qinggan.mobile.tsp.auth.ARSAUtils;
import com.qinggan.mobile.tsp.auth.AuthLoginResponseItem;
import com.qinggan.mobile.tsp.bean.MyVehicleInfo;
import com.qinggan.mobile.tsp.bean.RespOwnerByDeviceIdBean;
import com.qinggan.mobile.tsp.bean.VehicleListBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.bluekey.ApplyReq;
import com.qinggan.mobile.tsp.models.bluekey.ApplyResp;
import com.qinggan.mobile.tsp.models.bluekey.BluetoothKeysBean;
import com.qinggan.mobile.tsp.models.bluekey.DataResp;
import com.qinggan.mobile.tsp.models.bluekey.DestoryReq;
import com.qinggan.mobile.tsp.models.bluekey.KeyQueryResp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyPair;


/**
 * Created by Yorashe on 18-11-20.
 */

public class UserInfo implements Serializable {
    private static final String TAG = UserInfo.class.getSimpleName();
    private int uid;
    private int seat;
    private String mobilePhone;
    private String nickName;
    private String avatar;
    private String userName;
    private String userSeq;
    private String hotWord;
    private String tpdsn;
    public boolean autoPopUnlock =  true;
    private AuthLoginResponseItem authLoginResponseItem;
    private VehicleListBean vehicleListBean = null;
    private BluetoothKeysBean mBluetoothKeysBean = null;
    public transient boolean isBlueKeyEnable = false;
    public transient boolean isCarControlEnable = false;

    private transient BindInfo bindInfo;

    public static final int TBOX_NOT_ACTIVED = 0;
    public static final int TBOX_ACTIVED = 1;
    private static Context mContext = ArielApplication.getApp().getApplicationContext();


    @Override
    public String toString() {
        return "UserInfo{" +
                "uid=" + uid +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", avatar='" + avatar + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public BindInfo getBindInfo() {
        return bindInfo;
    }

    public void setBindInfo(BindInfo bindInfo) {
        this.bindInfo = bindInfo;
    }

    public String getVin() {
        if (vehicleListBean == null) return "";
        if (vehicleListBean.getData() == null) return "";
        if (vehicleListBean.getData().size() == 0) return "";
        if (vehicleListBean.getData().get(vehicleListBean.getData().size() - 1).getVehicleInfo() == null)
            return "";
        return vehicleListBean.getData().get(vehicleListBean.getData().size() - 1).getVehicleInfo().getVin();
    }

    public int getSimActived() {
        return BindVehicleInfo.isAuth() == true ? 1 : 0;
    }


    public VehicleListBean getVehicleListBean() {
        return vehicleListBean;
    }

    public void setVehicleListBean(VehicleListBean vehicleListBean) {
        this.vehicleListBean = vehicleListBean;
    }

    public void save() {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String strJson = new Gson().toJson(this);
        editor.putString(Constants.USERINFO, strJson);
        Log.e(TAG, "save() strJson=" + strJson);
        editor.commit();
    }


    public static String getCreateTableSql() {
       /* "Password text," +
        "HeadUrl text," +*/
        return "create table if not exists users(_id integer not null primary key autoincrement," +
                "userName text," +
                "UserInfo Blob)";
    }

    public static UserInfo loadUserInfoFromDB(Context context, String userName) {
        UserInfo mUserInfo = null;
        UserInfoDBHelper helper = UserInfoDBHelper.getInstance(context);
        String where = "userName ='" + userName + "'";
        Cursor cursor = helper.query(UserInfoDBHelper.TABLE_USER, null, where, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UserInfo info = new UserInfo();
            mUserInfo = UserInfo.loadFromCursor(cursor);
        }
        return mUserInfo;
    }

    public static UserInfo loadFromCursor(Cursor cursor) {
        UserInfo mUserInfo = null;
        //Log.e(TAG, "index1=" + cursor.getColumnIndex("UserInfo"));
        byte dateUserInfo[] = cursor.getBlob(cursor.getColumnIndex("UserInfo"));
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(dateUserInfo);
        try {
            ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
            mUserInfo = (UserInfo) inputStream.readObject();
            inputStream.close();
            arrayInputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "loadFromCursor() e=" + e.toString());
            e.printStackTrace();
        }
        return mUserInfo;
    }

    public boolean deleteAllLoginUser(Context context) {
        UserInfoDBHelper helper = UserInfoDBHelper.getInstance(context);
        try {
            helper.db.execSQL("truncate table name " + UserInfoDBHelper.TABLE_USER);
        } catch (Exception e) {
            Log.e(TAG, "deleteLoginUser() Exception=" + e.toString());
            return false;
        }
        return true;
    }

    public boolean deleteLoginUser(Context context, UserInfo user) {
        UserInfoDBHelper helper = UserInfoDBHelper.getInstance(context);
        try {
            helper.db.execSQL("delete from " + UserInfoDBHelper.TABLE_USER + " where PhoneNumber='" + user.userName + "'");
        } catch (Exception e) {
            Log.e(TAG, "deleteLoginUser() Exception=" + e.toString());
            return false;
        }
        return true;
    }


    public boolean saveLoginUser(Context context) {
        try {
            UserInfoDBHelper helper = UserInfoDBHelper.getInstance(context);
            String where = "userName ='" + userName + "'";
            Cursor cursor = helper.query(UserInfoDBHelper.TABLE_USER, null, where, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                ContentValues cv = getContentValues();
                int lines = helper.update(UserInfoDBHelper.TABLE_USER, cv, where, null);
                cursor.close();
            } else {
                ContentValues cv = getContentValues();
                helper.insert(UserInfoDBHelper.TABLE_USER, cv);
            }
        } catch (Exception e) {
            Log.e(TAG, "saveLoginUser() Exception=" + e.toString());
            return false;
        }
        return true;
    }

    public ContentValues getContentValues() {
        ContentValues cv = null;
        try {
            cv = new ContentValues();
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            byte dataUserInfo[] = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();
            Log.e(TAG, "getContentValues() dataUserInfo.length=" + dataUserInfo.length);
            cv.put("userName", userName);
            cv.put("UserInfo", dataUserInfo);

        } catch (Exception e) {
            Log.e(TAG, "getContentValues() Exception=" + e.toString());
            return null;
        }
        return cv;
    }

    public void updateLicenseAndEngineNo() {

        UserInfo mUser = ArielApplication.getmUserInfo();
        if (mUser != null) {
            if (mUser.getVehicleListBean() != null && mUser.getVehicleListBean().getData() != null
                    && mUser.getVehicleListBean().getData().size() > 0) {
                String vin = mUser.getVehicleListBean().getData().get(mUser.getVehicleListBean().getData().size() - 1).getVehicleInfo().getVin();
                Log.e(TAG, "updateLicenseAndEngineNo() vin=" + vin);
                TspManager.getInstance(ArielApplication.getApp().getApplicationContext()).getVehicleInfo(vin, new RestCallback<MyVehicleInfo>() {

                    @Override
                    public void success(MyVehicleInfo myVehicleInfo, RestResponse restResponse) {
                        Log.e(TAG, "updateLicenseAndEngineNo success() myVehicleInfo =" + myVehicleInfo.toString());
                        EventBus.getDefault().post(myVehicleInfo);
                    }

                    @Override
                    public void failure(RestError restError) {
                        Log.e(TAG, "updateLicenseAndEngineNo failure() restError=" + restError.toString());
                    }
                });
            }
        }
    }


    public void applyBlueKey(String vin) {
        /*KeyPair keyPair = ARSAUtils.generateRSAKeyPair(1024);*/
        Log.e(TAG, "applyBlueKey() vin=" + vin);
        Log.e(TAG, "applyBlueKey() mobilePhone=" + mobilePhone);
        String rsaAlias = mobilePhone;
        //helper.generateRSAKeyPair(rsaAlias);
/*        byte[] cipher = helper.RSAEncrypt("111111111111111111111".getBytes());
        byte[] plain = helper.RSADecrypt(cipher);
        Log.i(TAG,new String(plain));

        Log.i(TAG,"helper.GetPublicKey = "+helper.getRSAPublic());*/
        String base64PubKey = KeyStoreHelper.getInstance(mContext).getRSAPublic();
        Log.e(TAG, "base64PubKey=" + base64PubKey);
        final ApplyReq mApplyReq = new ApplyReq("tbox", vin, 365L, base64PubKey);
        TspManager.getInstance(ArielApplication.getApp().getApplicationContext())
                .sendBlueKeyApply(mApplyReq, new RestCallback<ApplyResp>() {
                    @Override
                    public void success(ApplyResp applyResp, RestResponse response) {
                        if (applyResp.getStatusCode().equals("0")) {
                            Toast.makeText(mContext, "蓝牙钥匙申请成功!", Toast.LENGTH_SHORT).show();
                            queryBlueKey(true);
                            EventBus.getDefault().post(new BlueKeyEvent(true, mApplyReq, 0));
                        } else {
                            Toast.makeText(mContext, "蓝牙钥匙申请失败!原因:\r" + applyResp.getStatusMessage(), Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new BlueKeyEvent(false, mApplyReq, 0));
                        }
                    }

                    @Override
                    public void failure(RestError restError) {
                        Toast.makeText(mContext, "获取蓝牙钥匙失败!\r" + restError.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "获取蓝牙钥匙失败!" + restError.getMessage());
                        EventBus.getDefault().post(new BlueKeyEvent(false, mApplyReq, 0));
                    }
                });
    }

    public void changeBlueKey(String vin) {
        KeyPair keyPair = ARSAUtils.generateRSAKeyPair(1024);
        String base64PubKey = ARSAUtils.publicKey2Base64(keyPair);
        Log.e(TAG, "change base64PubKey=" + base64PubKey);
        final ApplyReq mApplyReq = new ApplyReq("tbox", vin, 365L, base64PubKey);
        TspManager.getInstance(mContext)
                .sendBlueKeyChange(mApplyReq, new RestCallback<ApplyResp>() {
                    @Override
                    public void success(ApplyResp applyResp, RestResponse response) {
                        if (applyResp.getStatusCode().equals("0")) {
                            Toast.makeText(mContext, "更换蓝牙钥匙成功!", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new BlueKeyEvent(true, mApplyReq, 1));
                        } else {
                            Toast.makeText(mContext, "更换蓝牙钥匙失败!原因:\r" + applyResp.getStatusMessage(), Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new BlueKeyEvent(false, mApplyReq, 1));
                        }
                    }

                    @Override
                    public void failure(RestError restError) {
                        Toast.makeText(mContext, "更换蓝牙钥匙失败!\r" + restError.getMessage(), Toast.LENGTH_SHORT);
                        EventBus.getDefault().post(new BlueKeyEvent(false, mApplyReq, 1));
                    }
                });
    }


    public void destroyBlueKey() {
        DestoryReq req = new DestoryReq();
        req.setBluetoothId(mBluetoothKeysBean.getId());
        req.setDeviceType("tbox");
        req.setExpireTime(30L);
        req.setUserPubKey(mBluetoothKeysBean.getUserPubKey());
        req.setVin(mBluetoothKeysBean.getDeviceId());
        if (BleKeyManager.getInstance(mContext).unbindCar()) {
            TspManager.getInstance(mContext)
                    .sendBlueKeyDestory(req, new RestCallback<DataResp>() {
                        @Override
                        public void success(DataResp respBean, RestResponse response) {
                            //Gson gson = new Gson();
                            //String strGson = gson.toJson(applyResp);
                            if (respBean.getStatusCode().equals("0")) {
                                mBluetoothKeysBean = null;
                                isBlueKeyEnable = false;
                                save();
                                BleKeyHelper.updateBleKey();
                                Toast.makeText(mContext, "蓝牙钥匙销毁成功!", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new BlueKeyEvent(true, new DestoryReq()));
                            } else {
                                Toast.makeText(mContext, "蓝牙钥匙销毁失败!原因:\r" + respBean.getStatusMessage(), Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new BlueKeyEvent(false, new DestoryReq()));
                            }

                        }

                        @Override
                        public void failure(RestError restError) {
                            Toast.makeText(mContext, "蓝牙钥匙销毁失败!\r" + restError.getMessage(), Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new BlueKeyEvent(false, new DestoryReq()));
                        }
                    });
        } else {
            Toast.makeText(mContext, "蓝牙钥匙销毁失败,BLE连接中...", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new BlueKeyEvent(false, new DestoryReq()));
        }


    }

    public boolean isCarOwner() {
        if (vehicleListBean!=null && vehicleListBean.getData()!=null && vehicleListBean.getData().get(0)!=null){
            if (vehicleListBean.getData().get(0).getVehicleInfo()!=null) {
                if (TextUtils.isEmpty(vehicleListBean.getData().get(0).getVehicleInfo().getAid())) {
                    return false;
                }
                return vehicleListBean.getData().get(0).getVehicleInfo().getAid().equals(
                        authLoginResponseItem.getUserInfo().getAid());
            }
        }
        return false;


    }

    public void queryIsCarOwner() {
        String vin = vehicleListBean.getData().get(0).getVehicleInfo().getVin();
        Log.e(TAG, "queryIsCarOwner() vin=" + vin);
        TspManager.getInstance(ArielApplication.getApp().getApplicationContext())
                .findOwnerByDeviceId(vin, new RestCallback<RespOwnerByDeviceIdBean>() {
                    @Override
                    public void success(RespOwnerByDeviceIdBean respBean, RestResponse restResponse) {
                        Log.e(TAG, "queryIsCarOwner.success() respBean=" + respBean);
                        if (respBean != null) {
                            if (respBean.getCode().equals("0") && respBean.getData() != null) {
                                String aid = respBean.getData().getUserAccountId();
                                Log.e(TAG, "queryIsCarOwner.success() aid=" + aid);
                                vehicleListBean.getData().get(0).getVehicleInfo().setAid(aid);
                                save();
                            } else {
                            }
                        }
                        BleKeyHelper.updateBleKey();
                        BleKeyHelper.runBlueKey(mContext);
                        EventBus.getDefault().post("login_success_carowner_success");

                    }

                    @Override
                    public void failure(RestError restError) {
                        Log.e(TAG, "queryIsCarOwner.failure() +++");
                        vehicleListBean.getData().get(0).getVehicleInfo().setAid("");
                        save();
                        BleKeyHelper.updateBleKey();
                        BleKeyHelper.runBlueKey(mContext);
                        EventBus.getDefault().post("login_success_carowner_fail");
                    }
                });
    }

    public void queryBlueKey(final boolean isApply) {
        Log.e(TAG, "queryBlueKey() isApply=" + isApply);
        TspManager.getInstance(ArielApplication.getApp().getApplicationContext())
                .sendBlueKeyQuery(new RestCallback<KeyQueryResp>() {
                    @Override
                    public void success(KeyQueryResp arg0, RestResponse response) {
                        //Gson gson = new Gson();
                        //String strGson = gson.toJson(arg0);
                        if (arg0 != null && arg0.getData() != null && arg0.getData().size() > 0) {
                            for (int i=0;i<arg0.getData().size();i++) {
                                if (arg0.getData().get(i).getDeviceId().equals(getVin())) {
                                    mBluetoothKeysBean = arg0.getData().get(i).getBluetoothKeys().get(0);
                                    updateBlueKeyState();
                                    BleKeyHelper.updateBleKey();
                                    if (!checkTmpKey()) {
                                        EventBus.getDefault().post("BleKeyTypeUpdate");
                                    }
                                    queryIsCarOwner();
                                    //runBlueKey(isApply,arg0);
                                    save();
                                    return;
                                }
                            }
                        }
                        Log.e(TAG, "获取蓝牙钥匙失败!");
                        updateBlueKeyState();
                        EventBus.getDefault().post(new BlueKeyEvent(false, new KeyQueryResp(), isApply ? 1 : 0));
                        if (!checkTmpKey()) {
                            EventBus.getDefault().post("onBleKeyTypeUpdate");
                            EventBus.getDefault().post("login_success");
                        }
                    }


                    @Override
                    public void failure(RestError restError) {
                        Log.e(TAG, "获取蓝牙钥匙失败!");
                        updateBlueKeyState();
                        EventBus.getDefault().post(new BlueKeyEvent(false, new KeyQueryResp(), isApply ? 1 : 0));
                        EventBus.getDefault().post("login_fail_getBlukey");
                    }
                });
    }

    public BluetoothKeysBean getmBluetoothKeysBean() {
        return mBluetoothKeysBean;
    }

    public void setmBluetoothKeysBean(BluetoothKeysBean mBluetoothKeysBean) {
        this.mBluetoothKeysBean = mBluetoothKeysBean;
    }

    public void updateBlueKeyState() {
        //if (helper.hasAlias(mobilePhone)) {
        //helper.setRsaKeyAlias(mobilePhone);
        Log.e(TAG, "updateBlueKeyState() hasAlias");
        if (mBluetoothKeysBean == null) {
            Log.e(TAG, "updateBlueKeyState() mBluetoothKeysBean is null");
            isBlueKeyEnable = false;
        } else {
            if (mBluetoothKeysBean.getUserPubKey().equals(KeyStoreHelper.getInstance(mContext).getRSAPublic())) {
                //KeyStore保存的公钥与云端更新的公钥信息一致，判断有效
                Log.e(TAG, "updateBlueKeyState() equals");
                isBlueKeyEnable = true;

            } else {
                Log.e(TAG, "updateBlueKeyState() not equals");
                isBlueKeyEnable = false;
            }
        }
      /*  } else {
            //本地没有创建过蓝牙钥匙的密钥对，肯定蓝牙钥匙无效
            mBluetoothKeysBean = null;
            isBlueKeyEnable = false;
        }*/
    }

    public void updateVehicleList() {
        final Context context = ArielApplication.getApp().getApplicationContext();
        TspManager.getInstance(context)
                .getVehicleList(new RestCallback<VehicleListBean>() {
                    @Override
                    public void success(VehicleListBean arg0, RestResponse response) {
                         if (arg0.getStatusCode().equals("0") && arg0.getData() != null) {
                            if (arg0.getData().size() > 0) {
                                if (arg0.getData().get(arg0.getData().size() - 1).getVehicleInfo() != null) {

                                    vehicleListBean = arg0;
                                    EventBus.getDefault().post(arg0.getData().get(arg0.getData().size() - 1).getVehicleInfo());
                                    Gson gson = new Gson();
                                    Log.e(TAG, "updateVehicleList.success() vehicleListBean=" + gson.toJson(vehicleListBean));
                                    Log.e(TAG, "updateVehicleList.success() getVin()=" + getVin());
                                    ArielApplication.setmUserInfo(UserInfo.this);
                                    //查询实名认证状态
                                    if (!TextUtils.isEmpty(ArielApplication.getmUserInfo().getVin())) {
                                        BindVehicleInfo.checkPin(ArielApplication.getmUserInfo().getVin());
                                        BindVehicleInfo.findCertificationStatus(ArielApplication.getmUserInfo().getVin(), ArielApplication.getmUserInfo().getMobilePhone());
                                    }
                                    save();
                                    //成功获取到绑定的车辆的话，发起蓝牙钥匙的查询
                                    queryBlueKey(false);
                                    EventBus.getDefault().post(new BlueKeyEvent(true, arg0));
                                    BindVehicleInfo.getVehicleListDetail();
                                    return;
                                }
                            }
                        }

                        if (!checkTmpKey()) {
                            EventBus.getDefault().post("login_success");
                            EventBus.getDefault().post("BleKeyTypeUpdate");
                        }
                        EventBus.getDefault().post(new BlueKeyEvent(false, arg0));
                    }

                    @Override
                    public void failure(RestError restError) {
//                        Toast.makeText(context, restError.getMessage(), Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new VehicleListBean());
                        EventBus.getDefault().post("login_fail_getVehicleList");
                    }
                });
    }


    private boolean checkTmpKey() {
        if (null != BleKeyHelper.getBleCarKey() && BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_TEMP) {
            // TODO: 19-1-1 判断车主
            Toast.makeText(ArielApplication.getApp(), "正在上传蓝牙钥匙信息", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "checkTmpKey has temp key");
            ArielApplication.mAppBlueKeyListener.vehicleBind(ArielApplication.getmUserInfo(), true);
            return true;
        }
        return false;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }

    public String getHotWord() {
        return hotWord;
    }

    public void setHotWord(String hotWord) {
        this.hotWord = hotWord;
    }

    public UserInfo() {
    }

    public String getTpdsn() {
        return tpdsn;
    }

    public void setTpdsn(String tpdsn) {
        this.tpdsn = tpdsn;
    }

    public AuthLoginResponseItem getAuthLoginResponseItem() {
        return authLoginResponseItem;
    }

    public void setAuthLoginResponseItem(AuthLoginResponseItem authLoginResponseItem) {
        this.authLoginResponseItem = authLoginResponseItem;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }
}
