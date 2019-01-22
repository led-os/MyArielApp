package com.qinggan.app.arielapp.minor.main.driving.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.ExtendListHeader;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.ExtendHeadAdapter;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.SoPullExtendLayout;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneMainActivity;
import com.qinggan.app.arielapp.minor.radio.FMActivity;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;


@SuppressLint("ValidFragment")
public class OrdinaryFragment extends AbstractBaseFragment {
    private FragmentManager fragmentManager;
    private View view;


    private LinearLayout qingjing_lay;
    private LinearLayout music_lay;
    private LinearLayout phone_lay;
    private LinearLayout shouyinji_lay;
    private LinearLayout navigation_lay;
    private Context context;
    private IntegrationCore integrationCore;
    ExtendListHeader mPullNewHeader;
    RecyclerView listHeader, listFooter;
    List<String> mDatas = new ArrayList<>();
    private SoPullExtendLayout pull_extend;
    private LocalStorageTools localStorageTools;
    private ExtendHeadAdapter extendHeadAdapterNew;
    private LinePulseView wakeupIcon;



    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        this.context = getActivity();
        integrationCore = IntegrationCore.getIntergrationCore(context);
        if (view == null) {//优化View减少View的创建次数
            view = inflater.inflate(R.layout.driving_ordinary_layout, container, false);
            navigation_lay = (LinearLayout) view.findViewById(R.id.navigation_lay);
            navigation_lay.setOnClickListener(this);
            qingjing_lay = (LinearLayout) view.findViewById(R.id.qingjing_lay);
            qingjing_lay.setOnClickListener(this);
            music_lay = (LinearLayout) view.findViewById(R.id.music_lay);
            music_lay.setOnClickListener(this);
            phone_lay = (LinearLayout) view.findViewById(R.id.phone_lay);
            phone_lay.setOnClickListener(this);
            shouyinji_lay = (LinearLayout) view.findViewById(R.id.shouyinji_lay);
            shouyinji_lay.setOnClickListener(this);
            wakeupIcon = (LinePulseView) view.findViewById(R.id.wakeup);
            wakeupIcon.setOnClickListener(this);
        }
        EventBus.getDefault().register(this);
        localStorageTools = new LocalStorageTools(context);
        pull_extend = view.findViewById(R.id.pull_extend);
        mPullNewHeader = view.findViewById(R.id.extend_header);
        listHeader = mPullNewHeader.getRecyclerView();
        listHeader.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mDatas.add("0");

        extendHeadAdapterNew = new ExtendHeadAdapter(listHeader);
        extendHeadAdapterNew.addNewData(mDatas);
        extendHeadAdapterNew.setOnItemChildClickListener(new BGAOnItemChildClickListener() {
            @Override
            public void onItemChildClick(ViewGroup parent, View childView, int position) {
                switch (position) {
                    case 0:
                        pull_extend.closeExtendHeadAndFooter();
                        EventBus.getDefault().post(new EventBusBean("downClose"));
                        break;
                    default:
                        break;
                }
            }
        });
        listHeader.setAdapter(extendHeadAdapterNew);
        return view;
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onClick(View v) {
        if(pull_extend!=null) {
            pull_extend.closeExtendHeadAndFooter();
        }
        switch (v.getId()) {
            case R.id.navigation_lay:
                integrationCore.VoiceJump(StageController.Stage.NAVIGATION);
                break;
            case R.id.qingjing_lay:
                integrationCore.VoiceJump(StageController.Stage.SCENARIO);
                break;
            case R.id.music_lay:
                integrationCore.VoiceJump(StageController.Stage.MUSIC);
                break;
            case R.id.phone_lay:
                integrationCore.VoiceJump(StageController.Stage.PHONE);
                break;
            case R.id.shouyinji_lay:
                integrationCore.VoiceJump(StageController.Stage.RADIO);
                integrationCore.mPateoFMCMD.playCurrent();
                break;
            case R.id.wakeup:
                VoicePolicyManage.getInstance().record(true);
                break;
        }

        super.onClick(v);
    }


    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(final EventBusBean event){
        String type=event.getType();
        switch (type){
            case "simDropDown"://继续下拉关闭
                if(pull_extend!=null) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(500);//休眠0.5秒
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            pull_extend.closeExtendHeadAndFooter();
                        }
                    }.start();
                }
                EventBus.getDefault().post(new EventBusBean("downClose"));
                break;
            case "proNum":
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDatas.clear();
                            mDatas.add(event.getIntData() + "");
                            extendHeadAdapterNew.addNewData(mDatas);
                            extendHeadAdapterNew.notifyDataSetChangedWrapper();
                        }
                    });
                }catch (Exception e){
                    Log.i("Alan","OrdinaryFragment no search event");
                }
                break;
            default:
                break;
        }
    }


    private void gotoFMActivity() {
        Intent intent = new Intent(getActivity(), FMActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {//可见

        }else{
            if(pull_extend!=null) {
                pull_extend.closeExtendHeadAndFooter();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
