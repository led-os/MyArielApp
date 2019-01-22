package com.qinggan.app.arielapp.ui.bluekey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.commonui.EditClear;
import com.qinggan.app.arielapp.minor.main.commonui.EditClear2;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.ui.pin.add.AddPinActivity;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.mobile.tsp.bean.BaseBean;
import com.qinggan.mobile.tsp.bean.BaseCodeBean;
import com.qinggan.mobile.tsp.bean.MyVehicleInfo;
import com.qinggan.mobile.tsp.bean.VehicleListBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.bluekey.ApplyReq;
import com.qinggan.mobile.tsp.models.bluekey.DestoryReq;
import com.qinggan.mobile.tsp.models.bluekey.KeyQueryResp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/****
 * 登录
 * ***/
public class MyCarActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "login";
    @BindView(R.id.switcher)
    SettingSwitcher mSwitcher;
    @BindView(R.id.ivCar)
    ImageView ivCar;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    /*   @BindView(R.id.txtCarStatus)
       TextView txtCarStatus;*/
    @BindView(R.id.ll_unbind)
    LinearLayout llUnbind;
    @BindView(R.id.txtCarName)
    TextView txtCarName;
    @BindView(R.id.labelVin)
    TextView labelVin;
    @BindView(R.id.txtVin)
    TextView txtVin;
    @BindView(R.id.txtEngine)
    TextView txtEngine;
    @BindView(R.id.etEngine)
    EditClear2 etEngine;
    @BindView(R.id.txtCarNo)
    TextView txtCarNo;
    @BindView(R.id.etCarNo)
    EditClear2 etCarNo;
    @BindView(R.id.txt_active_sim)
    TextView txt_active_sim;
    @BindView(R.id.btn_active_sim)
    RelativeLayout btnActiveSim;


    @BindView(R.id.ll_active_sim)
    LinearLayout llActiveSim;
    @BindView(R.id.ivBlueKey)
    ImageView ivBlueKey;
    @BindView(R.id.ivControl)
    ImageView ivControl;
    @BindView(R.id.ll_control)
    LinearLayout llControl;
    @BindView(R.id.ll_bind)
    RelativeLayout llBind;
    @BindView(R.id.btn_unbind)
    TextView btnUnbind;
    @BindView(R.id.btn_bind)
    Button btnBind;
    @BindView(R.id.txtControlAction)
    TextView txtControlAction;
    @BindView(R.id.rlBlueKey)
    RelativeLayout rlBlueKey;
    @BindView(R.id.rlCarControl)
    RelativeLayout rlCarControl;
    @BindView(R.id.txtActiveSimTip1)
    TextView txtActiveSimTip1;
    @BindView(R.id.rltop)
    RelativeLayout rltop;
    @BindView(R.id.llinfo)
    LinearLayout llinfo;
    @BindView(R.id.rlinfo)
    RelativeLayout rlinfo;
    @BindView(R.id.switcher_pop)
    SettingSwitcher switcherPop;
    @BindView(R.id.rlBlePop)
    RelativeLayout rlBlePop;
    @BindView(R.id.flaction)
    FrameLayout flaction;
    private Dialog m_pDialog = null;
    /*
     @BindView(R.id.txtActiveSimTip2)
     TextView txtActiveSimTip2;*/
    private Context context;
    private View loginview;
    private ImageView back_btn;
    private EditClear phone_txt;
    private EditClear yezhengma_txt;
    private FragmentManager fragmentManager;
    private Button login_btn;
    private LocalStorageTools localStorageTools;
    private long tick = 0;

    public enum MY_CAR_STATE {UNBIND, BIND_NOT_ACTIVATE, BIND_ACTIVATE_WAITING, BIND_SIM_ACTIVED, BIND_ACTIVATE_FAILED}

    private MY_CAR_STATE mCarState = MY_CAR_STATE.UNBIND;

    protected synchronized void showWaiting() {
        //WLog.printCallStack();
        m_pDialog = createLoadingDialog(this, "请稍后...");
        m_pDialog.show();
    }

    protected synchronized void hideWaiting() {
        if (m_pDialog != null) {
            m_pDialog.dismiss();
            m_pDialog = null;
        }
    }

    @OnClick(R.id.rlCarControl)
    public void onCarControlClicked() {

    }

    @OnClick(R.id.btn_bind)
    public void onBtnBindClicked() {
        Intent intent = new Intent(this, BindCarActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_unbind)
    public void onBtnUnbind() {
        if (System.currentTimeMillis() - tick > 0 && System.currentTimeMillis() - tick < 500) {
            return;
        }
        final UserInfo mUser = ArielApplication.getmUserInfo();
        final String vin = mUser.getVehicleListBean().getData().get(mUser.getVehicleListBean().getData().size() - 1).getVehicleInfo().getVin();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("解除绑定");
        builder.setMessage("确定要与车架号为"+vin+"的车辆解除绑定关系吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (mUser.getVehicleListBean() != null && mUser.getVehicleListBean().getData() != null
                        && mUser.getVehicleListBean().getData().size() > 0) {
                    showWaiting();
                    TspManager.getInstance(MyCarActivity.this).vehicleUnBind(vin, new RestCallback<BaseBean>() {
                        @Override
                        public void success(BaseBean baseBean, RestResponse restResponse) {
                            Log.e(TAG, "vehicleUnBind.success() baseBean=" + baseBean.toString());
                            if (baseBean.getStatusCode().equals("0")) {
                                hideWaiting();
                                Toast.makeText(MyCarActivity.this, "解绑车辆成功！", Toast.LENGTH_SHORT).show();
                                mUser.setVehicleListBean(null);
                                mUser.setmBluetoothKeysBean(null);
                                mUser.save();
                                BleKeyHelper.updateBleKey();
                                BleKeyManager.getInstance(MyCarActivity.this).unbindCar();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "vehicleUnBind.success() finish!");
                                        finish();
                                    }
                                }, 1000);

                            } else {
                                Toast.makeText(MyCarActivity.this, "解绑车辆失败,调试版本删除绑定~\n" + baseBean.getStatusMessage(), Toast.LENGTH_SHORT).show();
                                hideWaiting();
                                mUser.setVehicleListBean(null);
                                mUser.setmBluetoothKeysBean(null);
                                mUser.save();
                                BleKeyHelper.updateBleKey();
                                BleKeyManager.getInstance(MyCarActivity.this).unbindCar();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "vehicleUnBind.success() finish!");
                                        finish();
                                    }
                                }, 1000);
                            }
                        }

                        @Override
                        public void failure(RestError restError) {
                            Log.e(TAG, "vehicleUnBind.failure() restError=" + restError.toString());
                            Toast.makeText(MyCarActivity.this, "云端解绑车辆失败,调试版本强行删除绑定~", Toast.LENGTH_SHORT).show();
                            hideWaiting();
                            mUser.setVehicleListBean(null);
                            mUser.setmBluetoothKeysBean(null);
                            mUser.save();
                            BleKeyManager.getInstance(MyCarActivity.this).unbindCar();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(TAG, "vehicleUnBind.success() finish!");
                                    finish();
                                }
                            }, 1000);
                        }
                    });
                } else {
                    Toast.makeText(MyCarActivity.this, "车辆信息不正确，无法解绑车辆，请尝试重新登录~", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.show();
    }

    @OnClick(R.id.txtControlAction)
    public void onControlAction() {
        Intent intent = new Intent(this, AddPinActivity.class);
        intent.putExtra("type", AddPinActivity.UPDATE_CODE);
        startActivityForResult(intent, AddPinActivity.UPDATE_CODE);
    }

    @OnClick(R.id.btn_active_sim)
    public void onBtnActiveSimClicked() {
        UserInfo mUser = ArielApplication.getmUserInfo();
        if (mUser != null) {
            String strEngine = etEngine.getText().toString();
            String strCarNo = etCarNo.getText().toString();
            if (TextUtils.isEmpty(strEngine)) strEngine = " ";
            if (TextUtils.isEmpty(strCarNo)) strCarNo = " ";
           /* if (TextUtils.isEmpty(strEngine)) {
                Toast.makeText(this, "请填写发动机号~", Toast.LENGTH_SHORT).show();
                etEngine.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(strCarNo)) {
                Toast.makeText(this, "请填写车牌号~", Toast.LENGTH_SHORT).show();
                etCarNo.requestFocus();
                return;
            }*/
            if (mUser.getVehicleListBean() != null && mUser.getVehicleListBean().getData() != null
                    && mUser.getVehicleListBean().getData().size() > 0) {
                String vin = mUser.getVehicleListBean().getData().get(mUser.getVehicleListBean().getData().size() - 1).getVehicleInfo().getVin();

                Log.e(TAG, "onBtnActiveSimClicked() vin=" + vin);
                TspManager.getInstance(this).updateVehicle(vin, strCarNo, strEngine, new RestCallback<BaseCodeBean>() {
                    @Override
                    public void success(BaseCodeBean baseCodeBean, RestResponse restResponse) {
                        Toast.makeText(MyCarActivity.this, "车辆信息已保存!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "updateVehicle() updateVehicle baseCodeBean=" + baseCodeBean.toString());
                    }

                    @Override
                    public void failure(RestError restError) {
                        Log.e(TAG, "updateVehicle() updateVehicle restError=" + restError.toString());
                    }
                });
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onBlueKeyEvent(BlueKeyEvent event) {
        Log.e(TAG, "onBlueKeyEvent() event=" + event);
        if (event != null) {
            if (event.obj instanceof KeyQueryResp) {
                KeyQueryResp bean = (KeyQueryResp) event.obj;
                if (event.param == 1) {
                    if (event.success) {
                        //蓝牙钥匙申请并查询成功
                        setBlueKeyEnable();
                    } else {
                        //蓝牙钥匙申请并查询失败
                        setBlueKeyEnable();
                    }
                } else {
                    if (event.success) {
                        //蓝牙钥匙查询成功
                        setBlueKeyEnable();
                    } else {
                        //蓝牙钥匙查询失败
                        setBlueKeyEnable();
                    }
                }

            } else if (event.obj instanceof DestoryReq) {
                if (event.success) {
                    //蓝牙钥匙销毁成功
                    setBlueKeyEnable();
                } else {
                    //蓝牙钥匙销毁失败
                    setBlueKeyEnable();
                }

            } else if (event.obj instanceof ApplyReq) {
                if (event.param == 1) {
                    if (event.success) {
                        //蓝牙钥匙更换成功
                        setBlueKeyEnable();
                    } else {
                        //蓝牙钥匙更换失败
                        setBlueKeyEnable();
                    }
                } else {
                    if (event.success) {
                        //蓝牙钥匙申请成功
                        setBlueKeyEnable();
                    } else {
                        //蓝牙钥匙申请失败
                        setBlueKeyEnable();
                    }
                }

            }
        }
        //finish();
    }

    public void updateCarState() {
        switch (mCarState) {
            case UNBIND:

                llUnbind.setVisibility(View.VISIBLE);
                llBind.setVisibility(View.GONE);
                //txtCarStatus.setVisibility(View.GONE);
                break;
            case BIND_NOT_ACTIVATE:
                //ivCar.setBackgroundResource(R.drawable.pic_car);
                llUnbind.setVisibility(View.GONE);
                llBind.setVisibility(View.VISIBLE);
                //txtCarStatus.setVisibility(View.VISIBLE);
                llActiveSim.setVisibility(View.VISIBLE);
                btnActiveSim.setVisibility(View.VISIBLE);
                txt_active_sim.setText(getResources().getString(R.string.mycar_btn_active_sim1));
                llControl.setVisibility(View.GONE);
                txtActiveSimTip1.setText(R.string.mycar_sim_not_active);
                txtActiveSimTip1.setVisibility(View.VISIBLE);
                Drawable drawable = getResources().getDrawable(R.drawable.mycar_icon_assistance);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth() - 10, (int) (drawable.getMinimumHeight() - 10));
                txtActiveSimTip1.setCompoundDrawablePadding(20);
                txtActiveSimTip1.setCompoundDrawables(null, null,
                        drawable, null);
                etCarNo.setEnabled(true);
                etEngine.setEnabled(true);
                /*
                txtActiveSimTip2.setText(R.string.sim_active_tip1);
                txtActiveSimTip2.setVisibility(View.VISIBLE);*/
                break;
            case BIND_ACTIVATE_WAITING:
                //ivCar.setBackgroundResource(R.drawable.pic_car);
                llUnbind.setVisibility(View.GONE);
                llBind.setVisibility(View.VISIBLE);
                //txtCarStatus.setVisibility(View.VISIBLE);
                llActiveSim.setVisibility(View.VISIBLE);
                txt_active_sim.setText(getResources().getString(R.string.mycar_btn_active_sim1));
                llControl.setVisibility(View.INVISIBLE);
                btnActiveSim.setVisibility(View.INVISIBLE);
                txtActiveSimTip1.setText(R.string.mycar_sim_active_waiting);
                txtActiveSimTip1.setVisibility(View.VISIBLE);
                txtActiveSimTip1.setCompoundDrawables(null, null, null, null);
                etCarNo.setEnabled(true);
                etEngine.setEnabled(true);
                 /*
                txtActiveSimTip2.setVisibility(View.GONE);*/

                break;
            case BIND_SIM_ACTIVED:
                //ivCar.setBackgroundResource(R.drawable.pic_car);
                llUnbind.setVisibility(View.GONE);
                llBind.setVisibility(View.VISIBLE);
                //txtCarStatus.setVisibility(View.VISIBLE);
                llActiveSim.setVisibility(View.GONE);
                llControl.setVisibility(View.VISIBLE);
                txtActiveSimTip1.setText(R.string.mycar_sim_already_active);
                txtActiveSimTip1.setVisibility(View.VISIBLE);
                txtActiveSimTip1.setCompoundDrawables(null, null, null, null);
                etCarNo.setEnabled(true);
                etEngine.setEnabled(true);
                 /*
                txtActiveSimTip2.setVisibility(View.GONE);*/
                //如果是SIM卡已激活的状态下，刷新蓝牙钥匙状态和远程车控状态
                setBlueKeyEnable();

                break;

            case BIND_ACTIVATE_FAILED:
                //ivCar.setBackgroundResource(R.drawable.pic_car);
                llUnbind.setVisibility(View.GONE);
                llBind.setVisibility(View.VISIBLE);
                //txtCarStatus.setVisibility(View.VISIBLE);
                llActiveSim.setVisibility(View.VISIBLE);
                btnActiveSim.setVisibility(View.VISIBLE);
                txt_active_sim.setText(getResources().getString(R.string.mycar_btn_active_sim2));
                llControl.setVisibility(View.GONE);
                txtActiveSimTip1.setText(R.string.mycar_sim_active_failed);
                txtActiveSimTip1.setVisibility(View.VISIBLE);
                txtActiveSimTip1.setCompoundDrawables(null, null, null, null);
                   /*
                txtActiveSimTip2.setText(R.string.sim_active_tip2);
                txtActiveSimTip2.setVisibility(View.VISIBLE);*/
                break;
        }
    }

    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.anim_rotate);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (TextUtils.isEmpty(msg)) {
            tipTextView.setVisibility(View.GONE);
        } else {
            tipTextView.setText(msg);// 设置加载信息
        }
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }

    private void setBlueKeyEnable() {
        UserInfo mUserInfo = ArielApplication.getmUserInfo();
        Log.e(TAG, "setBlueKeyEnable()=" + mUserInfo.isBlueKeyEnable);
        mSwitcher.setChecked(mUserInfo.isBlueKeyEnable);
        mSwitcher.setEnabled(true);
        if (mUserInfo.isBlueKeyEnable) {
            rlBlePop.setVisibility(View.VISIBLE);
            Log.e(TAG, "mUserInfo.autoPopUnlock=" + mUserInfo.autoPopUnlock);
            switcherPop.setChecked(mUserInfo.autoPopUnlock);
        }else {
            rlBlePop.setVisibility(View.GONE);

        }
        hideWaiting();
  /*      ivBlueKey.setBackgroundResource(mUserInfo.isBlueKeyEnable ? R.drawable.button_bluetooth_blue : R.drawable.button_bluetooth)
        txtBlueKeyState.setText(getResources().getString(mUserInfo.isBlueKeyEnable ? R.string.mycar_bluekey_enable : R.string.mycar_bluekey_disable));
        txtBlueKeyState.setTextColor(mUserInfo.isBlueKeyEnable ? Color.WHITE : Color.BLACK);
        ;
        txtBlueKeyAction.setText(getResources().getString(mUserInfo.isBlueKeyEnable ? R.string.mycar_bluekey_action2 : R.string.mycar_bluekey_action1));
        */
    }

    private void setCarControlEnable() {
        UserInfo mUserInfo = ArielApplication.getmUserInfo();
/*
        ivControl.setBackgroundResource(mUserInfo.isCarControlEnable ? R.drawable.button_control_blue : R.drawable.button_control);

        txtControlState.setText(getResources().getString(mUserInfo.isCarControlEnable ? R.string.mycar_control_enable : R.string.mycar_control_disable));
        txtControlState.setTextColor(mUserInfo.isCarControlEnable ? Color.WHITE : Color.BLACK);
        txtControlAction.setText(getResources().getString(mUserInfo.isCarControlEnable ? R.string.mycar_control_action2 : R.string.mycar_control_action1));
        */
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                localStorageTools.setBoolean("isLogin", true);
                fragmentManager.popBackStack();
                break;
            case R.id.back_btn:
                onBtnActiveSimClicked();
                finish();
                //fragmentManager.popBackStack();

                break;

            default:

                break;

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        //setContentView(R.layout.my_car_activity);
        //unbinder = ButterKnife.bind(this);
        context = this;
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        EventBus.getDefault().register(this);
        final UserInfo mUser = ArielApplication.getmUserInfo();
        VehicleListBean mVehicleListBean = mUser.getVehicleListBean();
        switcherPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - tick > 0 && System.currentTimeMillis() - tick < 500) {
                    return;
                }
                UserInfo mUserInfo = ArielApplication.getmUserInfo();
                if (mUserInfo!=null) {
                    boolean isChecked = switcherPop.isChecked();
                    Log.e(TAG, "switcherPop.onCheckedChanged() isChecked=" + isChecked);
                    mUserInfo.autoPopUnlock = isChecked;
                    BleKeyManager.getInstance(mContext).setAutoPopUnlock(mUserInfo.autoPopUnlock);
                    mUserInfo.save();
                }

            }
        });
        Log.e(TAG, "onCreate() mVehicleListBean=" + mVehicleListBean);
        if (mVehicleListBean != null && mVehicleListBean.getData() != null && mVehicleListBean.getData().size() > 0) {
            String vin = mVehicleListBean.getData().get(mVehicleListBean.getData().size() - 1).getVehicleInfo().getVin();
            Log.e(TAG, "onCreate() vin=" + vin);
            Log.e(TAG, "onCreate() mUser.getSimActived()=" + mUser.getSimActived());
            txtVin.setText(vin);
            if (mUser.getSimActived() == UserInfo.TBOX_NOT_ACTIVED) {
                mCarState = MY_CAR_STATE.BIND_NOT_ACTIVATE;
            } else if (mUser.getSimActived() == UserInfo.TBOX_ACTIVED) {
                mCarState = MY_CAR_STATE.BIND_SIM_ACTIVED;
            }
            mCarState = MY_CAR_STATE.BIND_SIM_ACTIVED;
            Log.e(TAG, "onCreate() mUser.isBlueKeyEnable=" + mUser.isBlueKeyEnable);
            mSwitcher.setChecked(mUser.isBlueKeyEnable);
            mSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() - tick > 0 && System.currentTimeMillis() - tick < 500) {
                        return;
                    }
                    UserInfo mUserInfo = ArielApplication.getmUserInfo();
                    boolean isChecked = mSwitcher.isChecked();
                    Log.e(TAG, "switcher.onCheckedChanged() isChecked=" + isChecked);
                    if (isChecked) {
                       /* if (TextUtils.isEmpty(mUserInfo.getVin())) {
                            Toast.makeText(context, "错误!没有绑定的车辆信息!", Toast.LENGTH_SHORT).show();
                            return;
                        }*/
                        mSwitcher.setChecked(false);
                        BindCarActivity.startAction(MyCarActivity.this);
                        finish();
                        //mSwitcher.setEnabled(false);
                        //showWaiting();
                        //mUserInfo.applyBlueKey(mUserInfo.getVin());
                    } else {
                        mSwitcher.setEnabled(false);
                        showWaiting();
                        mUserInfo.destroyBlueKey();
                    }
                }
            });
            mUser.updateLicenseAndEngineNo();
        }
        //mCarState = MY_CAR_STATE.UNBIND;
        updateCarState();
    }

    @Override
    protected void initData() {
        //rlCarControl.setVisibility(ArielApplication.getmUserInfo().isCarOwner() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.my_car_activity;
    }

    @Override
    protected void onResume() {

        super.onResume();
        tick = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddPinActivity.ADD_CODE_SUCCESS) {
            if (requestCode == AddPinActivity.UPDATE_CODE) {
                //提示设置成功
                Toast.makeText(MyCarActivity.this, getResources().getString(R.string.mycar_pin_success), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onEventMyVehicleInfo(MyVehicleInfo mVehicleInfo) {
        Log.e(TAG, "onEvenMyVehicleInfo() mVehicleInfo=" + mVehicleInfo.toString());
        if (mVehicleInfo != null) {
            etCarNo.setText(mVehicleInfo.getLicense());
            etEngine.setText(mVehicleInfo.getEngineNo());
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbinder.unbind();
        EventBus.getDefault().unregister(this);
        if (m_pDialog != null) {
            m_pDialog.dismiss();
            m_pDialog = null;
        }
    }
}
