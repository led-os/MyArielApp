package com.qinggan.app.arielapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.capability.push.factory.HUIPushConstants;
import com.qinggan.app.arielapp.flyn.Eyes;
import com.qinggan.app.arielapp.minor.integration.PateoVehicleControlCMD;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.main.utils.InterceptorProxyUtils;
import com.qinggan.app.arielapp.minor.phone.view.MultiDirectionSlidingDrawer;
import com.qinggan.app.arielapp.minor.wechat.inter.SlidingDrawerCallBack;
import com.qinggan.app.arielapp.utils.RomUtils;
import com.qinggan.app.arielapp.utils.StatusBarCompat;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;

public class HUIActivity extends Activity implements SlidingDrawerCallBack {

    private ImageView huiSure;
    private ImageView huiClose;
    private ImageView huiImage;
    private TextView huiDescripe;
    private TextView huiCommand;

    private String domain;
    private String domainDescripe;
    MultiDirectionSlidingDrawer mDrawer;
    private int commandType;
    private final int COMMAND_NAVI_COMMPANY = 0X1001;
    private final int COMMAND_NAVI_HOME = 0X1002;
    private final int COMMAND_CLOSE_WINDOW = 0X1003;
    private final int COMMAND_AIR_CONDITION = 0X1004;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetcontentView();
        setContentView(R.layout.hui_mode);

        Bundle bundle = getIntent().getExtras();
        domain = bundle.getString("domain");
        domainDescripe = bundle.getString("domainDescribe");
        Log.d("HUIPushMode", "domain : " + domain + " domainDescripe : " + domainDescripe);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        huiSure = (ImageView) findViewById(R.id.hui_sure);
        huiClose = (ImageView) findViewById(R.id.hui_close);
        huiImage = (ImageView) findViewById(R.id.hui_icon);
        huiDescripe = findViewById(R.id.hui_descripe);
        huiCommand = findViewById(R.id.hui_command);


        mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
        mDrawer.animateOpen();
        mDrawer.setCallBack(this);
    }


    private void initData() {
        if (domain.equals(HUIPushConstants.DOMAIN_ONWORK)) {
            huiImage.setImageResource(R.drawable.hui_company);
            String str = "<font color='#FFA500'>当前拥堵</font>";
            huiDescripe.setText(Html.fromHtml(str));
            huiCommand.setText(R.string.hui_traffic);
            huiSure.setImageResource(R.drawable.hui_go_press);
            commandType = COMMAND_NAVI_COMMPANY;
        } else if (domain.equals(HUIPushConstants.DOMAIN_OFFWORK)) {
            huiImage.setImageResource(R.drawable.hui_home);
            String str = "<font color='#FFA500'>当前拥堵</font>";
            huiDescripe.setText(Html.fromHtml(str));
            huiCommand.setText(R.string.hui_traffic);
            huiSure.setImageResource(R.drawable.hui_go_press);
            commandType = COMMAND_NAVI_HOME;
        } else if (domain.equals(HUIPushConstants.DOMAIN_WEATHER)) {
            if (domainDescripe.equals(HUIPushConstants.BAD_WEATHER)) {
                huiImage.setImageResource(R.drawable.hui_water);
                huiDescripe.setText(R.string.hui_snow);
                huiCommand.setText(R.string.hui_snow_command);
                huiSure.setImageResource(R.drawable.hui_sure_press);
                commandType = COMMAND_CLOSE_WINDOW;
            } else if (domainDescripe.equals(HUIPushConstants.POLLUTION_WEATHER)) {
                huiImage.setImageResource(R.drawable.hui_air);
                huiDescripe.setText(R.string.hui_air);
                huiCommand.setText(R.string.hui_air_command);
                huiSure.setImageResource(R.drawable.hui_air_press);
                commandType = COMMAND_AIR_CONDITION;
            }
        }
    }


    private void doBeforeSetcontentView() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarCompat.setTranslucentStatus(this, true);
        SetTranslanteBar();
        // 默认着色状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || RomUtils.getMiuiVersion() >= 6 || RomUtils.checkIsMeizuRom()) {
            Eyes.translucentStatusBar(this, true);
        } else {
            Eyes.translucentStatusBar(this, false);
        }
    }

    private void SetTranslanteBar() {
        StatusBarCompat.translucentStatusBar(this);
    }

    private void initListener() {
        huiSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (commandType) {
                    case COMMAND_NAVI_COMMPANY:{
                        Intent goCompany =new Intent(HUIActivity.this,NavigationActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("Commpany",1);
                        goCompany.putExtras(bundle);
                        startActivity(goCompany);
                        finish();
                        break;
                    }
                    case COMMAND_NAVI_HOME:{
                        Intent goHome =new Intent(HUIActivity.this,NavigationActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("Home",2);
                        goHome.putExtras(bundle);
                        startActivity(goHome);
                        finish();
                        break;
                    }
                    case COMMAND_CLOSE_WINDOW:
                        VoicePolicyManage.getInstance().speak(ArielApplication.getApp().getResources().getString(R.string.hui_closewindow_voice));
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        PateoVehicleControlCMD.getInstance().closeWindow(ArielApplication.getApp());
                        //InterceptorProxyUtils.getVehicleControlProxy().closeWindow(ArielApplication.getApp());
                        finish();
                        break;
                    case COMMAND_AIR_CONDITION:
                        VoicePolicyManage.getInstance().speak(ArielApplication.getApp().getResources().getString(R.string.hui_closewindow_voice));
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        PateoVehicleControlCMD.getInstance().closeWindow(ArielApplication.getApp());
                        PateoVehicleControlCMD.getInstance().switchLoop(ArielApplication.getApp(),true);
                        //InterceptorProxyUtils.getVehicleControlProxy().closeWindow(ArielApplication.getApp());
                        //InterceptorProxyUtils.getVehicleControlProxy().switchLoop(ArielApplication.getApp(),true);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });

        huiClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void openCallBack() {

    }

    @Override
    public void closeCallBack() {
        finish();
    }
}
