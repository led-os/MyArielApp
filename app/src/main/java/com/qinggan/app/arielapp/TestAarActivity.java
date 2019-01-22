package com.qinggan.app.arielapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;

/**
 * 虚拟点击测试类
 */

public class TestAarActivity extends Activity implements PateoVirtualSDK.ActionCallback {
    Button mtest1, mtest2, mtest3, mtest4, mtest5,
            mtest6, mtest7, mtest8, mtest9, mtest10,
            mtest11, mtest12;
    EditText appName, appAddress, appAction;
    TextView noti;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_aar);
        init();
    }

    private void init() {
        mtest1 = (Button) this.findViewById(R.id.test_btn1);
        mtest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(1);
            }
        });

        mtest2 = (Button) this.findViewById(R.id.test_btn2);
        mtest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(2);
            }
        });
        mtest3 = (Button) this.findViewById(R.id.test_btn3);
        mtest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(3);
            }
        });
        mtest4 = (Button) this.findViewById(R.id.test_btn4);
        mtest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(4);
            }
        });
        mtest5 = (Button) this.findViewById(R.id.test_btn5);
        mtest5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(5);
            }
        });
        mtest6 = (Button) this.findViewById(R.id.test_btn6);
        mtest6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(6);
            }
        });

        mtest7 = (Button) this.findViewById(R.id.test_btn7);
        mtest7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(TestAarActivity.this, MainActivity.class);
                startActivity(mIntent);
            }
        });

        mtest8 = (Button) this.findViewById(R.id.test_btn8);
        mtest8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(8);
            }
        });
        mtest9 = (Button) this.findViewById(R.id.test_btn9);
        mtest9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(9);
            }
        });
        mtest10 = (Button) this.findViewById(R.id.test_btn10);
        mtest10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(10);
            }
        });
        mtest11 = (Button) this.findViewById(R.id.test_btn11);
        mtest11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(11);
            }
        });
        mtest12 = (Button) this.findViewById(R.id.test_btn12);
        mtest12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(12);
            }
        });
//        mtest8 = (Button) this.findViewById(R.id.test_btn8);
//        mtest8.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doAction(8);
//            }
//        });
//        mtest8 = (Button) this.findViewById(R.id.test_btn8);
//        mtest8.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doAction(8);
//            }
//        });
//        mtest8 = (Button) this.findViewById(R.id.test_btn8);
//        mtest8.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doAction(8);
//            }
//        });

        noti = (TextView) this.findViewById(R.id.noti_txt);

        appName = (EditText) this.findViewById(R.id.wechat_appname);
        appAddress = (EditText) this.findViewById(R.id.wechat_address);
        appAction = (EditText) this.findViewById(R.id.wechat_action);

        PateoVirtualSDK.initialize(TestAarActivity.this, this);
    }

    private void doAction(int tag) {
        if (!AccessibilityUtil.isAccessibilitySettingsOn(TestAarActivity.this)) {
            this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }

        ActionBean actionBean = new ActionBean();
        switch (tag) {
            case 1:
                actionBean.setActionCode(3001);
                actionBean.setAppName("百度");
                //此参数设置仅为在展示页使用百度地图，正常使用不要填此参数
//                actionBean.setAddressee("导航");
                actionBean.setAddressee("");
                actionBean.setAction("第二个");
                break;
            case 2:
                actionBean.setActionCode(3002);
                actionBean.setAppName("百度");
//                actionBean.setAddressee("导航");
                actionBean.setAddressee("");
                actionBean.setAction("");
                break;
            case 3:
                actionBean.setActionCode(3003);
                actionBean.setAppName("百度");
//                actionBean.setAddressee("导航");
                actionBean.setAddressee("");
                actionBean.setAction("");
                break;
            case 4:
//                actionBean.setActionCode(1004);
//                actionBean.setAppName("微信");
//                actionBean.setAddressee("江海军");
//                actionBean.setAction("");

                actionBean.setActionCode(1002);
                actionBean.setAppName("微信");
                if (appAddress.getText().toString().equals("")) {
                    ToastUtil.show("请填写联系人",this);
                    return;
                }
                actionBean.setAddressee(appAddress.getText().toString());
                actionBean.setAction("");
                break;
            case 5:
//                actionBean.setActionCode(1005);
//                actionBean.setAppName("微信");
//                actionBean.setAddressee("江海军");
//                actionBean.setAction("1");

                actionBean.setActionCode(1003);
                actionBean.setAppName("微信");
                if (appAddress.getText().toString().equals("")) {
                    ToastUtil.show("请填写联系人",this);
                    return;
                }
                if (appAction.getText().toString().equals("") || !isNumeric(appAction.getText().toString())) {
                    ToastUtil.show("请填写金额(数字)",this);
                    return;
                }
                actionBean.setAddressee(appAddress.getText().toString());
                actionBean.setAction(appAction.getText().toString());
                break;
            case 6:
//                actionBean.setActionCode(1006);
//                actionBean.setAppName("微信");
//                actionBean.setAddressee("江海军");
//                actionBean.setAction("这是一条测试消息");

                actionBean.setActionCode(1001);
                actionBean.setAppName("微信");
                if (appAddress.getText().toString().equals("")) {
                    ToastUtil.show("请填写联系人",this);
                    return;
                }
                if (appAction.getText().toString().equals("")) {
                    ToastUtil.show("请填写消息内容",this);
                    return;
                }

                actionBean.setAddressee(appAddress.getText().toString());
                actionBean.setAction(appAction.getText().toString());
                break;

            case 8:
                //分步操作----搜索联系人
                actionBean.setActionCode(ActionCode.WECHAT_SEARCH_PERSON);
                if (appAddress.getText().toString().equals("")) {
                    ToastUtil.show("请填写联系人",this);
                    return;
                }
                actionBean.setAddressee(appAddress.getText().toString());
                actionBean.setAction("");
                break;
            case 9:
                //分步操作----选择联系人
                actionBean.setActionCode(ActionCode.WECHAT_SELECT_CONTACTS);
                if (appAddress.getText().toString().equals("")) {
                    ToastUtil.show("请填写联系人",this);
                    return;
                }
                if (appAction.getText().toString().equals("") || !isNumeric(appAction.getText().toString())) {
                    ToastUtil.show("请填写第几个联系人(数字)",this);
                    return;
                }
                actionBean.setAddressee(appAddress.getText().toString());
                actionBean.setAction(appAction.getText().toString());
                break;
            case 10:
                //分步操作----发消息
                actionBean.setActionCode(ActionCode.WECHAT_SEND_MSG);
                actionBean.setAddressee("");
                if (appAction.getText().toString().equals("")) {
                    ToastUtil.show("请填写消息内容",this);
                    return;
                }
                actionBean.setAction(appAction.getText().toString());
                break;
            case 11:
                //分步操作----发定位
                actionBean.setActionCode(ActionCode.WECHAT_SEND_POSITION);
                actionBean.setAddressee("");
                actionBean.setAction("");
                break;
            case 12:
                //分步操作----发红包
                actionBean.setActionCode(ActionCode.WECHAT_SEND_MONEY);
                actionBean.setAddressee("");
                if (appAction.getText().toString().equals("") || !isNumeric(appAction.getText().toString())) {
                    ToastUtil.show("请填写金额(数字)",this);
                    return;
                }
                actionBean.setAction(appAction.getText().toString());
                break;
        }


        System.out.println("---alvin--doaction---");
        PateoVirtualSDK.doAction(this,actionBean, this);
    }

    @Override
    public void onSuccess(int actionCode) {
        System.out.println("---alvin----onSuccess---->>" + actionCode);
        Toast.makeText(TestAarActivity.this, "执行成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(String reason, int actionCode) {
        System.out.println("---alvin----onFail---->>" + reason);
        Toast.makeText(TestAarActivity.this, "执行失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        System.out.println("---alvin----onCancel---->>");
    }

    @Override
    public void onNotification(String notification) {
        noti.setText(notification);
        System.out.println("---alvin----notification---->>" + notification);
    }

    private boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

}