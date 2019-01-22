package com.qinggan.app.arielapp.ui.bluekey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.databinding.ActivityBindCarSuccessBinding;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <蓝牙连接车辆成功>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-29]
 * @see [相关类/方法]
 * @since [V1]
 */
public class BindCarSuccessActivity extends BaseActivity implements View.OnClickListener {
    public static final String RESTAT = "restart";
    ActivityBindCarSuccessBinding binding;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, BindCarSuccessActivity.class);
        activity.startActivity(intent);
    }

    public static void startActionForResult(Activity activity, String bindName, int reqCode) {
        Intent intent = new Intent(activity, BindCarSuccessActivity.class);
        intent.putExtra("bindName", bindName);
        activity.startActivityForResult(intent, reqCode);
    }

    AnimationDrawable animationDrawable;

    @Override
    protected void initView() {
        binding = DataBindingUtil.bind(findViewById(R.id.bind_success_root));
        binding.setListener(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.succeccHead.getLayoutParams();
        params.width = width;
        params.height = (int) (width * 0.97f);
        binding.succeccHead.setLayoutParams(params);


        binding.enginStart.setImageResource(R.drawable.engin_bg);
        animationDrawable = (AnimationDrawable) binding.enginStart.getDrawable();
        animationDrawable.start();
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        String bindName = getIntent().getStringExtra("bindName");
        if (!TextUtils.isEmpty(bindName)) {
            //  与“五菱AAAA”连接成功！"
            binding.bindCarName.setText("与\"" + bindName + "\"连接成功！");
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bind_car_success;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.reconnect || v == binding.backBtn) {
            //重新连接
            setResult(100);
            BindCarActivity.startAction(this);
            finish();
//            EventBus.getDefault().post(RESTAT);
            return;

        }
        if (v == binding.restartTest && Constants.canMonitorStartEngine) {
            //EventBus.getDefault().post(RESTAT);
            ArielApplication.mAppBlueKeyListener.onStartEngine();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(100);
        finish();
    }


    /**
     * 是否已经收到重启信号,防止多次收到重启,逻辑重复
     */
    private AtomicBoolean hasRestart = new AtomicBoolean(false);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void doEngineRestart(String event) {
        Log.d(TAG, "doEngineRestart:" + event);
        if (RESTAT.equals(event)) {
            if (hasRestart.get()) {
                Log.d(TAG, "doEngineRestart:has restart engine");
                return;
            }
            hasRestart.set(true);
            //ArielApplication.mAppBlueKeyListener.onStartEngine();
            if (ArielApplication.getmUserInfo() != null) {
                //已经登录,需要根据蓝牙钥匙上传的结果做跳转
                showProgressDialog("正在上传车辆信息...");
            } else {
                //未登录
                EventBus.getDefault().post("BleKeyTypeUpdate");
                TemporaryKeyActivity.startAction(this);
                setResult(101);
                finish();
            }
            return;
        }

        if ("login_fail_vehicle_bind".equals(event)) {
            hideProgressDialog();
            ToastUtil.show("车辆绑定失败", this);
            return;
        }

        if ("login_fail_blukey_upload".equals(event)) {
            hideProgressDialog();
            ToastUtil.show("蓝牙钥匙上传失败", this);
            return;
        }

        if ("login_success_blekey_upload".equals(event)) {
            hideProgressDialog();
            ArielApplication.getmUserInfo().updateVehicleList();
            EventBus.getDefault().unregister(this);
            ForverKeyActivity.startAction(this);
            setResult(101);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != animationDrawable)
            animationDrawable.stop();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
