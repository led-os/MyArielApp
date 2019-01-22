package com.qinggan.app.arielapp.ui.bluekey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.user.Bean.BindInfo;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.bluekey.encrypt.KeyStoreHelper;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.model.BleControlResult;
import com.qinggan.bluekey.service.BleCarKey;
import com.qinggan.bluekey.service.BlueKeyListener;
import com.qinggan.mobile.tsp.bean.BaseBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.bluekey.ApplyResp;
import com.qinggan.mobile.tsp.models.bluekey.UploadReq;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AppBlueKeyListener implements BlueKeyListener {

    private Activity context = null;
    private AlertDialog dialog = null;
    private static final String TAG = AppBlueKeyListener.class.getSimpleName();
    private long lNextUnlockTick = 0;
    private boolean isLockClicked = false;

    public AppBlueKeyListener(Activity context) {
        this.context = context;
         /*   m_pDialog = new ProgressDialog(context);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);*/
    }
  /*      private ProgressDialog m_pDialog = null;
        protected void showWaiting()
        {
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(false);
            m_pDialog.setMessage("请稍侯...");
            m_pDialog.show();
        }
        protected void hideWaiting() {
            m_pDialog.hide();
        }*/


      /*   public void showCarLock(final String macAddress) {
            //Log.e(TAG,"showCarLock() getBleName="+key.getBleName());
            LayoutInflater inflater = LayoutInflater.from(ArielApplication.getApp().getApplicationContext());
            View v = inflater.inflate(R.layout.lock_layout, null);
            ImageView ivLock = v.findViewById(R.id.imgLock);
            dialog = new AlertDialog.Builder(context)
                     .setView(v)
                     .create();
            ivLock.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                    //showWaiting();
                    isLockClicked = true;
                    if (BleKeyManager.getInstance(context).setDoor(1)) {
                        Toast.makeText(context, "解锁指令发送成功!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent("com.pateo.bluekey.rfcomm");
                        Bundle bundle = new Bundle();
                        bundle.putString("macAdress", macAddress);
                        intent.putExtras(bundle);
                        ArielApplication.getApp().getApplicationContext().sendBroadcast(intent);
                    }else {
                        Toast.makeText(context, "解锁指令发送失败!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                    dialog = null;
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                 @Override
                 public void onDismiss(DialogInterface dialog) {
                     Log.e(TAG,"onDismiss() +++");
                     dialog = null;
                     if (!isLockClicked) {
                         BleKeyManager.getInstance(context).disconnectCar();
                     }

                 }
             });
            isLockClicked = false;
            dialog.show();
            final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            DisplayMetrics metrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            final WindowManager.LayoutParams params1 = dialog.getWindow().getAttributes();
            params1.width = (290 * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
            params1.height = (403 * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
            dialog.getWindow().setAttributes(params1);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }*/

    @Override
    public void onServiceConnected() {
        Log.e(TAG, "onServiceConnected() +++");
        BleKeyHelper.updateBleKey();
        BleKeyHelper.runBlueKey(context);
    }

    @Override
    public void onServiceDisconnected() {

        Log.e(TAG, "onServiceDisconnected() +++");
    }

    @Override
    public void onNearByCarEvent(final List<BleCarKey> mKeyList) {
        Gson gson = new Gson();
        Log.e(TAG, "onNearByCarEvent() PID:" + android.os.Process.myPid());
        Log.e(TAG, "onNearByCarEvent() mKeyList=" + gson.toJson(mKeyList));
        /*    if (dialog==null || (dialog!=null && !dialog.isShowing())) {

                if (!BleKeyManager.getInstance(context).connectCar(mKeyList.get(0))) {
                    Log.e(TAG,"onNearByCarEvent() connectCar failed!");
                }

            }*/
    }

    @Override
    public void onBlueKeyConnected() {
        Log.e(TAG, "onBlueKeyConnected() PID:" + android.os.Process.myPid());
    }

    @Override
    public void onBlueKeyConnectFailed() {
        Log.e(TAG, "onBlueKeyConnectFailed() PID:" + android.os.Process.myPid());
          /*  if (dialog!=null) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "与车辆连接异常!", Toast.LENGTH_SHORT).show();
                        //hideWaiting();
                    }
                });
            }*/
    }

    @Override
    public void onBlueKeyDisconnect(final String reason) {
        Log.e(TAG, "onBlueKeyDisconnect() PID:" + android.os.Process.myPid());
        //Log.e(TAG,"onBlueKeyDisconnect() reason:"+ reason);
          /*  if (dialog!=null) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
                        hideWaiting();
                    }
                });
            }*/
    }

    @Override
    public void onBlueKeyAuthSuccess(final String macAddress) {
        Log.e(TAG, "onBlueKeyDisconnect() PID:" + android.os.Process.myPid());
        Log.e(TAG, "onBlueKeyDisconnect() macAddress:" + macAddress);

        /*context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //showCarLock(macAddress);
            *//*    Intent intent = new Intent( ArielApplication.getApp().getApplicationContext(), BlueKeyLockActivity.class);
                intent.putExtra("mac",macAddress);
                ArielApplication.getApp().getApplicationContext().startActivity(intent);*//*
            }
        });*/

    }

    @Override
    public void onBlueKeyAuthFailed(final String reason) {
        Log.e(TAG, "onBlueKeyAuthFailed() PID:" + android.os.Process.myPid());
        Log.e(TAG, "onBlueKeyAuthFailed() reason:" + reason);
          /*  if (dialog!=null) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG,"onBlueKeyAuthFailed() reason="+reason);
                        Toast.makeText(context, reason,Toast.LENGTH_SHORT).show();
                        hideWaiting();
                    }
                });
            }*/
    }

    @Override
    public void onBlueKeyCarStatus(final int status) {
        Log.e(TAG, "onBlueKeyCarStatus() PID:" + android.os.Process.myPid());
/*            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean bLock = ((status & 0X01)>0);
                    boolean bTrunk = ((status & 0X02)>0);
                    boolean bSkyWin = ((status & 0X03)>0);
                    boolean bDoorWin = ((status & 0X04)>0);
                    String strStatus = "车锁:"+bLock+" 后备箱:"+bTrunk+" 天窗:"+bSkyWin+" 车窗:"+bDoorWin;
                    Log.e(TAG,"onBlueKeyCarStatus() strStatus="+strStatus);
                    Toast.makeText(context,strStatus,Toast.LENGTH_SHORT).show();
                }
            });*/
    }

    @Override
    public void onBlueKeyCmdResult(final int type, final int status) {
        Log.e(TAG, "onBlueKeyCmdResult() PID:" + android.os.Process.myPid());
        Log.e(TAG, "onBlueKeyCmdResult() type=" + type + ",status=" + status);
        EventBus.getDefault().post(new BleControlResult(type,status));
        /*    context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (type) {
                        case 0x01:
                            Toast.makeText(context,"开锁操作"+((status==1)?"成功":"失败"),Toast.LENGTH_SHORT).show();
                            break;
                        case 0x02:
                            Toast.makeText(context,"闭锁操作"+((status==1)?"成功":"失败"),Toast.LENGTH_SHORT).show();
                            break;
                        case 0x03:
                            Toast.makeText(context,"开后备箱操作"+((status==1)?"成功":"失败"),Toast.LENGTH_SHORT).show();
                            break;
                        case 0x04:
                            Toast.makeText(context,"闪灯鸣笛操作"+((status==1)?"成功":"失败"),Toast.LENGTH_SHORT).show();
                            break;
                        case 0x05:
                            Toast.makeText(context,"开启天窗操作"+((status==1)?"成功":"失败"),Toast.LENGTH_SHORT).show();
                            break;
                        case 0x06:
                            Toast.makeText(context,"关闭天窗操作"+((status==1)?"成功":"失败"),Toast.LENGTH_SHORT).show();
                            break;
                    }
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BleKeyManager.getInstance(context).getCarStatus();
                        }
                    }, 1000);
                }
            });*/

    }

    private BleCarKey mBleKey;

    @Override
    public void onBlueKeyBindInfo(final String info) {
        Log.e(TAG, "onBlueKeyBindInfo() info = " + info);
        Gson gson = new Gson();
        try {
            mBleKey = gson.fromJson(info, BleCarKey.class);
            mBleKey.setKeyType(BleCarKey.KEY_TYPE_TEMP);
            EventBus.getDefault().post(new BlueKeyEvent(true, "vehicleBind"));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new BlueKeyEvent(false, "vehicleBind"));
        }

    }

    @Override
    public void onBlueKeyBindFailed(String s) {
        Log.e(TAG, "onBlueKeyBindFailed() s = " + s);
        Toast.makeText(context,"绑车失败:"+s,Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new BlueKeyEvent(false, "vehicleBind"));
    }

    @Override
    public void onStartEngine() {
        Log.e(TAG, "onStartEngine() mBleKey=" + mBleKey);
        EventBus.getDefault().post(BindCarSuccessActivity.RESTAT);
        BleKeyManager.getInstance(context).disconnectCar();
        if (mBleKey != null) {
            BleKeyHelper.mBleCarKey = mBleKey;
            BleKeyHelper.saveTempKey(context, mBleKey);
            final UserInfo mUserInfo = ArielApplication.getmUserInfo();
            if (mUserInfo != null) {
                vehicleBind(mUserInfo,false);
            }
            BleKeyHelper.runBlueKey(context);
            Log.e(TAG,"onStartEngine() rfCommAddr:"+ mBleKey.rfCommAddr);
/*            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"onStartEngine() rfCommAddr:"+ mBleKey.rfCommAddr);
                    Intent intent = new Intent("com.pateo.bluekey.rfcomm");
                    Bundle bundle = new Bundle();
                    bundle.putString("macAdress", mBleKey.rfCommAddr);
                    intent.putExtras(bundle);
                    context.sendBroadcast(intent);
                }
            }, 5000);*/
        }

    }

    public void vehicleBind(final UserInfo mUserInfo, boolean tmpKeyBind) {
        if (mBleKey==null) return;
        if (TextUtils.isEmpty(mBleKey.vin)) {
            Log.e(TAG,"vehicleBind() mBleKey.vin is null");
            return;
        }
        if (TextUtils.isEmpty(mBleKey.imei)) {
            Log.e(TAG,"vehicleBind() mBleKey.imei is null");
            return;
        }
        if (TextUtils.isEmpty(mBleKey.iccid)) {
            Log.e(TAG,"vehicleBind() mBleKey.iccid is null");
            return;
        }
        if (TextUtils.isEmpty(mBleKey.imsi)) {
            Log.e(TAG,"vehicleBind() mBleKey.imsi is null");
            return;
        }
        if (TextUtils.isEmpty(mBleKey.tpdsn)) {
            Log.e(TAG,"vehicleBind() mBleKey.tpdsn is null");
            return;
        }

        mUserInfo.setBindInfo(new BindInfo(mBleKey.vin, mBleKey.imei, mBleKey.iccid, mBleKey.imsi, mBleKey.tpdsn, mBleKey.pdsn));
        ArielApplication.setmUserInfo(mUserInfo);
        TspManager.getInstance(context).vehicleBind(mBleKey.vin, mBleKey.pdsn, mBleKey.tpdsn, mBleKey.imsi, mBleKey.iccid, new RestCallback<BaseBean>() {
            @Override
            public void success(BaseBean baseCodeBean, RestResponse restResponse) {
                Log.e(TAG, "onBlueKeyBindInfo() vehicleBind baseCodeBean=" + baseCodeBean.toString());
                if (baseCodeBean.getStatusCode().trim().equals("0")) {
                    BindVehicleInfo.setIccid(mBleKey.iccid);
                    BindVehicleInfo.setImsi(mBleKey.imsi);
                    BindVehicleInfo.setTpdsn(mBleKey.tpdsn);
                    BindVehicleInfo.setPdsn(mBleKey.pdsn);

                    UploadReq mReq = new UploadReq();
                    mReq.deviceType = "tbox";
                    mReq.setVin(mBleKey.vin);
                    mReq.setExpireTime(365L);
                    mReq.setUserPubKey(KeyStoreHelper.getInstance(context).getRSAPublic());
                    mReq.setBluetoothAccessKey(mBleKey.getBleAccessKeyEncode());
                    mReq.setBluetoothConName(mBleKey.getBleName());
                    mReq.setBluetoothConKey(mBleKey.getBleConnectKey());
                    mReq.setTboxPubKey(mBleKey.getTboxPubKey());
                    mReq.setBleKeyId("" + mBleKey.getBleKeyId());
                    //日新增绑定车辆
                    UMAnalyse.count(UMCountEvent.DAY_BIND);
                    Log.d(TAG,"sendBlueKeyUpload...");
                    TspManager.getInstance(ArielApplication.getApp().getApplicationContext())
                            .sendBlueKeyUpload(mReq, new RestCallback<ApplyResp>() {
                                @Override
                                public void success(ApplyResp applyResp, RestResponse response) {
                                    Log.d(TAG,"sendBlueKeyUpload success");
                                    if (applyResp.getStatusCode().equals("0") && applyResp.getData() != null) {
                                        mUserInfo.setmBluetoothKeysBean(applyResp.getData());
                                        mUserInfo.save();
                                        BleKeyHelper.removeTempKey(context);
                                        //runBlueKey(isApply,arg0);
                                        Toast.makeText(context, "上传蓝牙钥匙成功!", Toast.LENGTH_SHORT).show();
                                        //queryBlueKey(true);
                                        BleKeyHelper.updateBleKey();
                                        BleKeyHelper.runBlueKey(context);
                                        EventBus.getDefault().post(
                                                new BlueKeyEvent(true, "uploadBleKey"));

                                        EventBus.getDefault().post("login_success_blekey_upload");
                                        if (mUserInfo!=null) {
                                            mUserInfo.updateVehicleList();
                                        }
                                    } else {
                                        Toast.makeText(context, "上传蓝牙钥匙失败!原因:\r" + applyResp.getStatusMessage(), Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(
                                                new BlueKeyEvent(false, "uploadBleKey"));
                                        EventBus.getDefault().post("login_fail_blukey_upload");
                                        BleKeyHelper.updateBleKey();
                                    }
                                }

                                @Override
                                public void failure(RestError restError) {
                                    Log.e(TAG, "上传蓝牙钥匙失败!" + restError.getMessage());
                                    EventBus.getDefault().post(
                                            new BlueKeyEvent(false, "uploadBleKey"));
                                    EventBus.getDefault().post("login_fail_blukey_upload");
                                    BleKeyHelper.updateBleKey();
                                }
                            });
                }else {
                    Toast.makeText(context, "绑定车辆失败:"+baseCodeBean.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post("login_fail_vehicle_bind");
                    BleKeyHelper.updateBleKey();
                }
            }

            @Override
            public void failure(RestError restError) {
                Log.e(TAG, "onBlueKeyBindInfo() vehicleBind restError=" + restError.toString());
                EventBus.getDefault().post("login_fail_vehicle_bind");
                BleKeyHelper.updateBleKey();
            }
        });
    }


}
