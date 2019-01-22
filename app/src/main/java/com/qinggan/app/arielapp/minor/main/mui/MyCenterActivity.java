package com.qinggan.app.arielapp.minor.main.mui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.integration.MusicContacts;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.ShardPreUtils;
import com.qinggan.app.arielapp.minor.wechat.utils.WechatConstants;
import com.qinggan.app.arielapp.ui.bluekey.BleKeyHelper;
import com.qinggan.app.arielapp.ui.bluekey.MyCarActivity;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.app.arielapp.user.activity.AboutActivity;
import com.qinggan.app.arielapp.user.activity.AddressActivity;
import com.qinggan.app.arielapp.user.activity.LoginActivity;
import com.qinggan.app.arielapp.user.activity.WakeupSetActivity;
import com.qinggan.mobile.tsp.manager.TokenManager;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.userfavorite.WakeUpKeyResp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.util.NetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

import static com.qinggan.app.arielapp.minor.utils.Constants.LOGIN_EVENT;

public class MyCenterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.about_lay)
    LinearLayout aboutLay;
    private LinearLayout head_container;
    private float mPosX;
    private float mPosY;
    private float mCurPosX;
    private float mCurPosY;
    private TextView login_btn;
    private TextView car_name;
    private ImageView person;
    private LinearLayout _mycar;
    private TextView name_txt;
    private LinearLayout test_btn;
    private TextView num_txt;
    private LinearLayout voice_wakeup_lay;
    private LinearLayout offLineMap;
    private RelativeLayout back_btn;
    private LinearLayout login_out;
    private TextView wakeup_text;
    private ImageButton mWechatReceivieSwitchBtn;

    private Boolean receiveSwitchState = false; //微信接收switch状态


    @Override
    protected void initView() {
        head_container = (LinearLayout) findViewById(R.id.head_container);
        login_btn = (TextView) findViewById(R.id.login_btn);//登录or注册
        car_name = (TextView) findViewById(R.id.car_name);//登录or注册
        login_btn.setOnClickListener(this);
        person = (ImageView) findViewById(R.id.person);//头像
        person.setOnClickListener(this);
        _mycar = (LinearLayout) findViewById(R.id._mycar);//我的车辆
        _mycar.setOnClickListener(this);
        login_out = (LinearLayout) findViewById(R.id.login_out);//退出登录
        login_out.setOnClickListener(this);
        name_txt = (TextView) findViewById(R.id.name_txt);
        num_txt = (TextView) findViewById(R.id.num_txt);
        test_btn = (LinearLayout) findViewById(R.id.test_btn);
        test_btn.setOnClickListener(this);
        voice_wakeup_lay = (LinearLayout) findViewById(R.id.voice_wakeup_lay);
        wakeup_text = (TextView) findViewById(R.id.wakeup_text);
        offLineMap = (LinearLayout) findViewById(R.id.offLineMap);
        voice_wakeup_lay.setOnClickListener(this);
        offLineMap.setOnClickListener(this);
        back_btn = (RelativeLayout) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        mWechatReceivieSwitchBtn = (ImageButton) findViewById(R.id.wechat_receivie_switch_btn);
        mWechatReceivieSwitchBtn.setOnClickListener(this);
        initUserDate();
        initOrSaveWechatStatus(true);
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        EventBus.getDefault().register(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_center;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getWakeUpKey();
    }


    @OnClick({R.id.about_lay})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id._mycar:
                UserInfo mUser = ArielApplication.getmUserInfo();
                if (mUser != null) {
                    Intent intent = new Intent(this, MyCarActivity.class);
                    startActivity(intent);
                } else {
                    LoginActivity.startAction(this);
                }
                break;
            case R.id.person:
                ArielApplication.getmUserInfoWithLogin();
                break;
            case R.id.login_btn://登录or注册
                LoginActivity.startAction(this);
                break;
            case R.id.test_btn://测试界面
                Intent intent_test = new Intent(this, TestActivity.class);
                startActivity(intent_test);
                break;
            case R.id.voice_wakeup_lay://语音唤醒词修改
//                if (ArielApplication.getmUserInfoWithLogin() != null)
                    WakeupSetActivity.startAction(this);

                break;
            case R.id.offLineMap://常用地址
                if (ArielApplication.getmUserInfoWithLogin() != null)
                    AddressActivity.startAction(this);

                break;
            case R.id.back_btn://返回

                finish();
                overridePendingTransition(R.anim.slide_jingzhi, R.anim.slide_out_left);
                break;
            case R.id.login_out://退出登录
                loginOut();
                break;
            case R.id.about_lay://关于
                AboutActivity.startAction(this);
                break;
            case R.id.wechat_receivie_switch_btn:
                if (!receiveSwitchState) {
                    receiveSwitchState = true;
                    mWechatReceivieSwitchBtn.setSelected(true);
                } else {
                    receiveSwitchState = false;
                    mWechatReceivieSwitchBtn.setSelected(false);
                }
                initOrSaveWechatStatus(false);
                break;
            default:

                break;

        }
    }

    private void getWakeUpKey() {
        if (null != ArielApplication.getmUserInfo())
            TspManager.getInstance(this).getWakeupCall(new RestCallback<WakeUpKeyResp>() {
                @Override
                public void success(WakeUpKeyResp arg0, RestResponse response) {
                    if (null != arg0.getData() && arg0.getData().size() > 0) {
                        String key = arg0.getData().get(0).getContent();
                        if (!TextUtils.isEmpty(key)) {
                            wakeup_text.setText(key);
                        }
                        if (null != ArielApplication.getmUserInfo() && TextUtils.isEmpty(ArielApplication.getmUserInfo().getHotWord())) {
                            UserInfo userInfo = ArielApplication.getmUserInfo();
                            userInfo.setHotWord(key);
                            ArielApplication.setmUserInfo(userInfo);
                            if (null != ArielApplication.getmHotwordManager())
                                ArielApplication.getmHotwordManager().setCustomWakeupWord(key);
                        }
                    }

                }


                @Override
                public void failure(RestError restError) {

                }
            });
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(EventBusBean event) {
        String type = event.getType();
        switch (type) {

            case LOGIN_EVENT:
                initUserDate();
                break;
            default:
                break;
        }
    }


    private void loginOut() {
        if (NetUtil.isNetworkConnected(mContext)) {
            showProgressDialog("正在退出登录...");
            TspManager.getInstance(mContext).loginOut(new RestCallback<String>() {
                @Override
                public void success(String s, RestResponse restResponse) {
                    hideProgressDialog();
                    ArielApplication.setmUserInfo(null);
                    BleKeyHelper.updateBleKey();
                    BleKeyHelper.runBlueKey(mContext);
                    EventBus.getDefault().post(new EventBusBean(LOGIN_EVENT,
                            "1"
                    ));
                    ToastUtil.show("已退出登录", mContext);
                    TokenManager.getInstance(mContext).logoutGlobalToken();

                }

                @Override
                public void failure(RestError restError) {
                    hideProgressDialog();
                    ToastUtil.show(restError.getMessage(), mContext);

                }
            });
        } else {
            ToastUtil.show(R.string.no_network_tips, mContext);

        }
    }

    private void initUserDate() {
        login_btn.setVisibility(ArielApplication.getmUserInfo() == null ? View.VISIBLE : View.GONE);
        login_out.setVisibility(ArielApplication.getmUserInfo() != null ? View.VISIBLE : View.GONE);
        if (ArielApplication.getmUserInfo() != null && !TextUtils.isEmpty(ArielApplication.getmUserInfo().getMobilePhone())) {
            name_txt.setText(ArielApplication.getmUserInfo().getMobilePhone());
            name_txt.setVisibility(View.VISIBLE);
            num_txt.setVisibility(View.VISIBLE);
            car_name.setVisibility(View.VISIBLE);
            person.setImageResource(R.mipmap.icon_head);
        } else {
            name_txt.setVisibility(View.GONE);
            num_txt.setVisibility(View.GONE);
            car_name.setVisibility(View.GONE);
            person.setImageResource(R.mipmap.icon_head_default);

        }
    }

    private void initOrSaveWechatStatus(boolean isInit) {
        if (isInit) {
            //1.读取数据库,0是关闭，1是开启
            int status = ShardPreUtils.getInstance(MusicContacts.DEFAULT_FILE)
                    .getIntValue(WechatConstants.MSG_RECEIVE_SAVE_STATUS_KEY);
            receiveSwitchState = status > 0 ? true : false;
            //2.更新view状态
            mWechatReceivieSwitchBtn.setSelected(receiveSwitchState);
        } else {
            int saveStatus = receiveSwitchState ? 1 : 0;
            ShardPreUtils.getInstance(MusicContacts.DEFAULT_FILE)
                    .putIntValue(WechatConstants.MSG_RECEIVE_SAVE_STATUS_KEY, saveStatus);
        }
    }
}
