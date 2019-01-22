package com.qinggan.app.arielapp.ui.bluekey;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 虚拟点击测试类
 */

public class BindCarActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "BindCarActivity";
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.txtBeforeBind1)
    TextView txtBeforeBind1;
    @BindView(R.id.txtBeforeBind2)
    TextView txtBeforeBind2;
    @BindView(R.id.btn_bind)
    Button btnBind;
    @BindView(R.id.rlBeforeBind)
    RelativeLayout rlBeforeBind;
    @BindView(R.id.ivConnecting)
    ImageView ivConnecting;
    @BindView(R.id.rlScanning)
    RelativeLayout rlScanning;
    //Unbinder unbinder;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    private static final long SCAN_PERIOD = 10000;
    @BindView(R.id.txtRefresh)
    TextView txtRefresh;
    @BindView(R.id.txtNoDevice)
    TextView txtNoDevice;
    @BindView(R.id.ivParing)
    ImageView ivParing;
    @BindView(R.id.rlIcon)
    RelativeLayout rlIcon;
    @BindView(R.id.tvState)
    TextView tvState;
    @BindView(R.id.tvCarName)
    TextView tvCarName;
    @BindView(R.id.rlBinding)
    RelativeLayout rlBinding;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.tvInfo)
    TextView tvInfo;
    @BindView(R.id.btn_active)
    Button btnActive;
    @BindView(R.id.btn_not_now)
    Button btnNotNow;
    @BindView(R.id.btn_try_again)
    Button btnTryAgain;
    @BindView(R.id.rlNoDevice)
    RelativeLayout rlNoDevice;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    public Context context;
    private View mRootView;

    private FragmentManager fragmentManager;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private Handler mHandler;
    private static boolean isLive = false;
    private long lOpenBthTick = 0;

    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, BindCarActivity.class);
        activity.startActivity(intent);
    }

    private void startBindDevice() {
        mUiState = UI_STATE.BIND_PARING;
        updateUiState();
    }

    @OnClick(R.id.btn_not_now)
    public void onBtnNotNow() {
        if (mUiState == UI_STATE.BIND_PARING) {
            if (BleKeyManager.getInstance(BindCarActivity.this).unbindCar()) {
                Log.e(TAG, "绑车中用户点击取消，取消成功！");
            } else {
                Log.e(TAG, "绑车中用户点击取消，取消失败！");
            }
        }
        finish();
    }

    public String getBindName(String strBleName) {
        String strName = strBleName.trim();
        if (strName.length() > 6) {
            strName = strName.substring(strName.length() - 6, strName.length());
            strName = "小菱 " + strName;
        }
        return strName;
    }


    String bindName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.ViewHolder> {
        private LayoutInflater mInflater;
        public List<BluetoothDevice> mListItems = new ArrayList<BluetoothDevice>();
        private int iSelectedIndex = 0;

        public void addDevice(BluetoothDevice device) {
            if (!mListItems.contains(device) && device.getName().length() > 6) {
                mListItems.add(device);
                notifyItemInserted(mListItems.size() - 1);
            }
        }

        public BluetoothDevice getSelectedDevice() {
            return mListItems.get(iSelectedIndex);
        }

        public BluetoothDevice getDevice(int position) {
            return mListItems.get(position);
        }

        public void clear() {
            mListItems.clear();
            notifyDataSetChanged();
        }


        public LeDeviceListAdapter() {
            mInflater = LayoutInflater.from(context);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTxtDeviceName;

            public ViewHolder(View arg0) {
                super(arg0);
            }
        }

        @Override
        public int getItemCount() {
            int count = mListItems.size();
            return count;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = mInflater.inflate(R.layout.bind_car_list_item, viewGroup, false);
            Log.e(TAG, "onCreateViewHolder() view=" + view);
            ViewHolder viewHolder = new ViewHolder(view);
            Log.e(TAG, "onCreateViewHolder() viewHolder=" + viewHolder);
            viewHolder.mTxtDeviceName = (TextView) view.findViewById(R.id.device_name);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            Log.e(TAG, "onBindViewHolder() pos=" + i);
            final String finalStrName = getBindName(mListItems.get(i).getName());
            viewHolder.mTxtDeviceName.setText(finalStrName);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iSelectedIndex = i;
                    Log.e(TAG, "onClick() 正在绑定车辆=" + mListItems.get(i).getName());
                    bindName = finalStrName;
                    startBindDevice();

/*                    MainActivity.mAppBlueKeyListener
                            .onBlueKeyBindInfo("V866793030057882:863976048939117:89861118257039848845:460113341236812:P866793030057882:P866793030057882");*/
                    if (BleKeyManager.getInstance(BindCarActivity.this).bindCar(mListItems.get(i).getAddress())) {
                        Log.e(TAG, "onClick() bindCar......");
                    } else {
                        Toast.makeText(BindCarActivity.this, "绑定车辆失败!", Toast.LENGTH_SHORT).show();
                        mUiState = UI_STATE.BIND_FAILED;
                        updateUiState();
                    }

                }
            });
        }

    }

    @Override
    protected void initView() {
        context = this;
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //unbinder = ButterKnife.bind(this);
        isLive = true;
        mHandler = new Handler();
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 检查设备上是否支持蓝牙
        mUiState = UI_STATE.BEFORE_BIND;
        updateUiState();
        backBtn.setOnClickListener(this);
        EventBus.getDefault().register(this);
        BleKeyManager.getInstance(this).stopKey();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReciever, filter);

        //LogcatHelper.getInstance(this,"app","*:e","").start();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.bind_car_activity;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isLive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runTimeout);
        unregisterReceiver(mBluetoothReciever);
        //unbinder.unbind();
        EventBus.getDefault().unregister(this);
        //LogcatHelper.getInstance().stop();
    }

    public enum UI_STATE {BEFORE_BIND, BIND_SCANING, BIND_NO_DEVICE, BIND_PARING, BIND_SUCCESS, BIND_FAILED}

    private UI_STATE mUiState = UI_STATE.BIND_SCANING;


    private Runnable runTimeout = new Runnable() {
        public void run() {
            if (isLive) {
                if (mUiState == UI_STATE.BIND_PARING) {
                    Log.e(TAG, "runTimeout BIND_FAILED!!!");
                    mUiState = UI_STATE.BIND_FAILED;
                    updateUiState();
                }
            }

        }
    };

    public void updateUiState() {
        switch (mUiState) {
            case BEFORE_BIND:
                rlBeforeBind.setVisibility(View.VISIBLE);
                rlScanning.setVisibility(View.GONE);
                rlBinding.setVisibility(View.GONE);
                ivParing.setVisibility(View.GONE);
                rlNoDevice.setVisibility(View.GONE);
                ivParing.clearAnimation();
                break;
            case BIND_SCANING:
                rlNoDevice.setVisibility(View.GONE);
                rlBeforeBind.setVisibility(View.GONE);
                rlScanning.setVisibility(View.VISIBLE);
                rlBinding.setVisibility(View.GONE);
                ivParing.setVisibility(View.GONE);
                ivParing.clearAnimation();
                ivConnecting.setVisibility(View.VISIBLE);
                txtRefresh.setVisibility(View.GONE);
                //txtNoDevice.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                mLeDeviceListAdapter = new LeDeviceListAdapter();
                mRecyclerView.setAdapter(mLeDeviceListAdapter);
                DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                divider.setDrawable(new ColorDrawable(Color.parseColor("#33000000")));
                mRecyclerView.addItemDecoration(divider);
                //mRecyclerView.addItemDecoration(new DividerGridItemDecoration(context));
                //mRecyclerView.setItemAnimator(new SlideInRightAnimator());
                mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                        if (!TextUtils.isEmpty(device.getName())) {
                            Log.e(TAG, "onLeScan() device.getName()=" + device.getName());
                            mLeDeviceListAdapter.addDevice(device);
                        }
                    }
                };
                scanLeDevice(true);
                break;
            case BIND_PARING: {
                rlNoDevice.setVisibility(View.GONE);
                rlBeforeBind.setVisibility(View.GONE);
                rlScanning.setVisibility(View.GONE);
                rlBinding.setVisibility(View.VISIBLE);
                scanLeDevice(false);
                ivIcon.setImageResource(R.drawable.pic_cir);
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
                ivParing.setVisibility(View.VISIBLE);
                ivParing.startAnimation(anim);
                tvState.setText("正在配对");
                btnNotNow.setText("取消");
                tvState.setTextColor(Color.WHITE);
                tvCarName.setText(bindName);
                tvInfo.setVisibility(View.GONE);
                btnActive.setVisibility(View.GONE);
                btnNotNow.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.GONE);
                mHandler.removeCallbacks(runTimeout);
                mHandler.postDelayed(runTimeout, 20000);

            }
            break;
            case BIND_NO_DEVICE: {
                rlBeforeBind.setVisibility(View.GONE);
                rlScanning.setVisibility(View.VISIBLE);
                rlBinding.setVisibility(View.GONE);
                scanLeDevice(false);
                ivParing.clearAnimation();
                ivParing.setVisibility(View.GONE);
                ivConnecting.setVisibility(View.GONE);
                txtRefresh.setVisibility(View.VISIBLE);
                rlNoDevice.setVisibility(View.VISIBLE);
                //txtNoDevice.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
            break;
            case BIND_SUCCESS: {
                rlNoDevice.setVisibility(View.GONE);
                rlBeforeBind.setVisibility(View.GONE);
                rlScanning.setVisibility(View.GONE);
                rlBinding.setVisibility(View.VISIBLE);
                scanLeDevice(false);
                ivIcon.setImageResource(R.drawable.pic_cir_success);
                ivParing.clearAnimation();
                ivParing.setVisibility(View.GONE);
                tvState.setText("配对成功");
                btnNotNow.setText("以后再说");
                tvCarName.setText(mLeDeviceListAdapter.getSelectedDevice().getName());
                tvInfo.setVisibility(View.VISIBLE);
                tvInfo.setText(getResources().getString(R.string.bind_active_info));
                btnActive.setVisibility(View.VISIBLE);
                btnNotNow.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.GONE);
            }
            break;
            case BIND_FAILED: {
                rlNoDevice.setVisibility(View.GONE);
                rlBeforeBind.setVisibility(View.GONE);
                rlScanning.setVisibility(View.GONE);
                rlBinding.setVisibility(View.VISIBLE);
                scanLeDevice(false);
                ivIcon.setImageResource(R.drawable.pic_cir_error);
                ivParing.clearAnimation();
                ivParing.setVisibility(View.GONE);
                tvState.setText("配对失败");

                tvCarName.setText("获取钥匙失败\n请重启车辆以后重试");
                tvInfo.setVisibility(View.GONE);
                btnActive.setVisibility(View.GONE);
                btnNotNow.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.VISIBLE);
            }
            break;
        }
    }


    BroadcastReceiver mBluetoothReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e(TAG, "mBluetoothReciever STATE_ON!");
                        if (System.currentTimeMillis() - lOpenBthTick > 0
                                && System.currentTimeMillis() - lOpenBthTick < 3000) {
                            mUiState = UI_STATE.BIND_SCANING;
                            updateUiState();
                        }

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };


    private Dialog mOpenBthDialog = null;

    private View.OnClickListener mOpenBthDlgListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_open:
                    lOpenBthTick = System.currentTimeMillis();
                    mBluetoothAdapter.enable();
                    if (mOpenBthDialog != null) {
                        mOpenBthDialog.dismiss();
                    }

                    break;
                case R.id.btn_unbind:
                    if (mOpenBthDialog != null) {
                        mOpenBthDialog.dismiss();
                    }
                    finish();
                    break;

            }
        }
    };

    @OnClick(R.id.btn_try_again)
    public void onBtnTryAgainClicked() {
        mUiState = UI_STATE.BIND_SCANING;
        updateUiState();
    }

    @OnClick(R.id.txtRefresh)
    public void onBtnRefreshClicked() {
        BleKeyManager.getInstance(this).stopKey();
        mUiState = UI_STATE.BIND_SCANING;
        updateUiState();
    }


    @OnClick(R.id.btn_active)
    public void onBtnActiveClicked() {
        Intent intent = new Intent(this, ActiveCarActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.btn_bind)
    public void onBtnBindClicked() {
        if (!mBluetoothAdapter.isEnabled()) {
            mOpenBthDialog = new Dialog(this, R.style.open_bluetooth_dialog);
            LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                    R.layout.dlg_open_bluetooth, null);
            root.findViewById(R.id.btn_open).setOnClickListener(mOpenBthDlgListener);
            root.findViewById(R.id.btn_unbind).setOnClickListener(mOpenBthDlgListener);
            mOpenBthDialog.setContentView(root);
            Window dialogWindow = mOpenBthDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);            //dialogWindow.setWindowAnimations(R.style.android.Animation.Dialog); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 100; // 新位置Y坐标
            lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9); // 宽度
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//		lp.alpha = 9f; // 透明度
            root.measure(0, 0);
            //lp.height = root.getMeasuredHeight();
            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
            mOpenBthDialog.show();
        } else {
            mUiState = UI_STATE.BIND_SCANING;
            updateUiState();
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:

                break;
            case R.id.back_btn:
                BleKeyHelper.updateBleKey();
                BleKeyHelper.runBlueKey(ArielApplication.getApp().getContext());
                finish();
                break;
            default:
                break;
        }
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            //showWaiting();
            // Stops scanning after a pre-defined scan period.
            mLeDeviceListAdapter.clear();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    if (!isLive || ivConnecting == null) {
                        return;
                    }
                    ivConnecting.clearAnimation();
                    ivConnecting.setVisibility(View.GONE);
                    txtRefresh.setVisibility(View.VISIBLE);
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if (mUiState == UI_STATE.BIND_SCANING) {
                        if (mLeDeviceListAdapter.getItemCount() == 0) {
                            mUiState = UI_STATE.BIND_NO_DEVICE;
                            updateUiState();
                        }
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;

            //mBluetoothAdapter.startLeScan(mLeScanCallback);
            final UUID[] serviceUuids = {ParcelUuid.fromString(Constants.UUID_SERVICE_CAR_CONTROL).getUuid()};
            mBluetoothAdapter.startLeScan(serviceUuids, mLeScanCallback);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
            ivConnecting.setVisibility(View.VISIBLE);
            ivConnecting.startAnimation(anim);
            txtRefresh.setVisibility(View.GONE);
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mScanning = false;
            if (ivConnecting != null) {
                ivConnecting.clearAnimation();
                ivConnecting.setVisibility(View.GONE);
            }
            txtRefresh.setVisibility(View.VISIBLE);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onBlueKeyEvent(BlueKeyEvent event) {
        Log.e(TAG, "onBlueKeyEvent() event=" + event.toString());
        if (event != null) {
            if (event.obj instanceof String) {
                if (((String) event.obj).equals("vehicleBind")) {
                    if (event.success) {
                        //车辆绑定成功
                        mUiState = UI_STATE.BIND_SUCCESS;
//                        updateUiState();
//                        UserInfo mUser = ArielApplication.getmUserInfo();
                        //MainActivity.mAppBlueKeyListener.onBlueKeyAuthSuccess("");
//                        mUser.updateVehicleList();
                        BindCarSuccessActivity.startActionForResult(BindCarActivity.this, bindName, 100);
                        finish();
                    } else {
                        //车辆绑定失败
                        mHandler.removeCallbacks(runTimeout);
                        mUiState = UI_STATE.BIND_FAILED;
                        updateUiState();
                    }
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (requestCode == 100) {
            if (resultCode == 100) {
                mUiState = UI_STATE.BIND_SCANING;
                updateUiState();
            } else if (resultCode == 101) {
                finish();
            }
        }
    }
}