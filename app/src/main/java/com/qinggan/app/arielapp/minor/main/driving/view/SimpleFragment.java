package com.qinggan.app.arielapp.minor.main.driving.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.main.commonui.BattaryView;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.ExtendListHeader;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.PullExtendLayout;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.ExtendHeadAdapter;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.SoPullExtendLayout;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.minor.main.utils.Tools;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneMainActivity;
import com.qinggan.app.arielapp.minor.radio.FMActivity;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.staryea.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

//极简模式
@SuppressLint("ValidFragment")
public class SimpleFragment extends AbstractBaseFragment implements  IntegrationCore.UICallback, OnClickListener, CardController.SimpleStopTimeoutListener {//[BUG:L0001M-486] MODIFIED BY brianchen@pateo.com.cn, at 2018.12.05, BEGIN

    private View view;
    private FragmentManager fragmentManager;
    private LocalStorageTools localStorageTools;
    private static Tools.BatteryReceiver receiver = null;

    private Context context;
    private IntegrationCore integrationCore;
    ExtendListHeader mPullNewHeader;
    RecyclerView listHeader, listFooter;
    List<String> mDatas = new ArrayList<>();
    private BattaryView battar_pro;
    private SoPullExtendLayout pull_extend;

    private int battery=0;//电量
    private String time="14:38";
    private String date="11月23日";
    private String week="星期五";
    private TextView time_txt;
    private TextView date_txt;

    private View mBottomCard;

    private ImageView mLeftIcon;
    private TextView mTopContent;
    private TextView mBottomContent;
    private ImageView mRightIcon;
    private TextView mWholeContent;

    private Handler mHandler;

    public final static int MSG_EXIT_CARD = 0;

    public final static int EXIT_MUSIC_RADIO_TIME_MILLS = 30000;
    public final static int EXIT_NEWS_TIME_MILLS = 1000;
    private ExtendHeadAdapter extendHeadAdapterNew;

    private CardInfo mShowingCard;
    private ImageView power_img;

    private int status =0;

    public SimpleFragment() {
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case MSG_EXIT_CARD:
                        mBottomCard.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }


    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {//优化View减少View的创建次数
            view = inflater.inflate(R.layout.driving_simple_layout, container, false);
            power_img=(ImageView)view.findViewById(R.id.power_img);
        }
        EventBus.getDefault().register(this);
        fragmentManager = getFragmentManager();
        this.context = getActivity();
        getPhoneEletric(context);
        localStorageTools = new LocalStorageTools(context);
        integrationCore = IntegrationCore.getIntergrationCore(context);
        //[BUG:L0001M-486] ADDED BY brianchen@pateo.com.cn, at 2018.12.05, BEGIN
        integrationCore.registerUICallback(IntegrationCore.KEY_UICALLBACK_SIMPLE, this);
        //[BUG:L0001M-486] ADDED BY brianchen@pateo.com.cn, at 2018.12.05, END
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

        battar_pro = (BattaryView) view.findViewById(R.id.battar_pro);
        battar_pro.setBattaryPercent(0);
        time_txt=(TextView)view.findViewById(R.id.time_txt);
        date_txt=(TextView)view.findViewById(R.id.date_txt);

        mBottomCard = view.findViewById(R.id.bottom_card);
        mTopContent = view.findViewById(R.id.top_content);
        mBottomContent = view.findViewById(R.id.bottom_content);
        mLeftIcon = view.findViewById(R.id.left_icon);
        mRightIcon = view.findViewById(R.id.right_icon);
        mWholeContent = view.findViewById(R.id.whole_content);

        mRightIcon.setOnClickListener(this);

        initCardView();

        integrationCore.setTimeoutListener(this);

        /**mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("Brian", "Fm test");
                integrationCore.mPateoFMCMD.initTestDatas();
            }
        }, 10000);*/

        return view;
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    private void gotoFMActivity() {
        Intent intent = new Intent(getActivity(), FMActivity.class);
        getActivity().startActivity(intent);
    }

    //是否可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        } else {
            //相当于Fragment的onPause
            if(pull_extend!=null) {
                pull_extend.closeExtendHeadAndFooter();
            }
        }
    }

    //获取手机电量
    public static void getPhoneEletric(Context mContext) {
        if(receiver!=null){
            return;
        }
        receiver = new Tools.BatteryReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mContext.registerReceiver(receiver, filter);//注册BroadcastReceiver


    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(final EventBusBean event) {
        String type = event.getType();
        switch (type) {
            case "PhoneState"://手机状态信息 包含：电量，时间,星期
                battery=event.getBattery();
                time=event.getTime();
                date=event.getDate();
                week=event.getWeek();
                status=event.getStatus();
                updateUI();
                break;
            case "POWER"://手机状态信息 是否正在充电
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(event.getMsg().equals("1")){
                            power_img.setVisibility(View.VISIBLE);
                        }else{
                            power_img.setVisibility(View.GONE);
                        }
                    }
                });

                break;
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        mDatas.add(event.getIntData()+"");
                        extendHeadAdapterNew.addNewData(mDatas);
                        extendHeadAdapterNew.notifyDataSetChangedWrapper();
                    }
                });

                break;

            default:
                break;
        }
    }
    //更新ui
    private void updateUI() {
        time_txt.post(new Runnable() {
            @Override
            public void run() {
                time_txt.setText(time);
            }
        });
        date_txt.post(new Runnable() {
            @Override
            public void run() {
                date_txt.setText(date+"  "+week);
            }
        });
        battar_pro.post(new Runnable() {
            @Override
            public void run() {
                battar_pro.setBattaryPercent(battery);
            }
        });
      if(status==BatteryManager.BATTERY_STATUS_CHARGING||status==BatteryManager.BATTERY_STATUS_FULL){
//            Toast.makeText(context, "充电中!",Toast.LENGTH_SHORT).show();
                   power_img.setVisibility(View.VISIBLE);
       }else{
//            Toast.makeText(context, "未充电",Toast.LENGTH_SHORT).show();
                  power_img.setVisibility(View.GONE);
       }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //[BUG:L0001M-486] ADDED BY brianchen@pateo.com.cn, at 2018.12.05, BEGIN
    @Override
    public void callback2UI(int cmdId, Bundle bundle) {

    }

    @Override
    public void updateCardView(String cardId) {

    }

    @Override
    public void updateAllCard() {

    }

    @Override
    public void updateSimpleCard() {
        /**CardInfo cardInfo = integrationCore.getCardInfo(CardController.CARD_TYPE_MUSIC);
        mSingerName.setText(cardInfo.getContent());
        mSongName.setText(cardInfo.getMessage());
        int rightImg = cardInfo.getRightIconImg();
        if (rightImg == 0) {
            mPlayBtn.setImageResource(R.drawable.driving_mode_s_botton_play);
        } else {
            mPlayBtn.setImageResource(rightImg);
        }*/

        if (getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initCardView();
            }
        });
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        switch (viewId) {
            case R.id.right_icon:
                triggerFromCardInfo();
                break;
        }
    }

    //[BUG:L0001M-486] ADDED BY brianchen@pateo.com.cn, at 2018.12.05, END

    public void initCardView() {
        mHandler.removeMessages(MSG_EXIT_CARD);
        /**int bgStream = StageController.getStageController().getBGStream();
         switch(bgStream){
         case StageController.BG_STREAM_MUSIC:
         doInitMusicCardView();
         break;
         case StageController.BG_STREAM_RADIO:
         doInitRadioCardView();
         break;
         case StageController.BG_STREAM_NEWS:
         doInitNewsCardView();
         break;
         case StageController.BG_STREAM_NONE:
         doNoneStreamUpdate();
         break;
         }*/

        final AudioPolicyManager.AudioType audioType =
                AudioPolicyManager.getInstance().getCurrentAudioType();
        Log.i("Brian_card", "simple card audioType = " + audioType);
        switch (audioType) {
            case MUSIC:
                doInitMusicCardView();
                break;
            case FM:
                doInitRadioCardView();
                break;
            case NEWS:
                doInitNewsCardView();
                break;
            case NO_MEDIA:
                doNoneStreamUpdate();
                break;
        }
    }

    @Override
    public void timeoutExitCard(String cardId) {
        mHandler.removeMessages(MSG_EXIT_CARD);
        Message mesg = mHandler.obtainMessage(MSG_EXIT_CARD);
        Log.i("Brian_card", "exit card when card id = " + cardId);
        if (cardId.equalsIgnoreCase(CardController.CARD_TYPE_MUSIC)
                || cardId.equalsIgnoreCase(CardController.CARD_TYPE_RADIO)){
            mHandler.sendMessageDelayed(mesg, EXIT_MUSIC_RADIO_TIME_MILLS);
        } else if (cardId.equalsIgnoreCase(CardController.CARD_TYPE_NEWS)) {
            mHandler.sendMessageDelayed(mesg, EXIT_NEWS_TIME_MILLS);
        }

    }

    private void triggerFromCardInfo(){
        final String cardId = mShowingCard.getCardId();
        switch(cardId){
            case CardController.CARD_TYPE_MUSIC:
                integrationCore.playOrPauseMusic();
                break;
            case CardController.CARD_TYPE_RADIO:
                integrationCore.playOrStopRadio();
                break;
            case CardController.CARD_TYPE_NEWS:
                integrationCore.mPateoNewsCMD.setNewsPause();
                break;
        }
    }

    public void doInitMusicCardView(){
        mBottomCard.setVisibility(View.VISIBLE);
        mShowingCard = integrationCore.getCardInfo(CardController.CARD_TYPE_MUSIC);
        /**
        mTopContent.setText(cardInfo.getContent());
        mTopContent.setVisibility(View.VISIBLE);
        mBottomContent.setText(cardInfo.getMessage());
        mBottomContent.setVisibility(View.VISIBLE);
        mWholeContent.setVisibility(View.GONE);*/

        String mesg = mShowingCard.getMessage();
        String content = mShowingCard.getContent();
        if (TextUtils.isEmpty(mesg)) {
            mWholeContent.setText(content);
            mWholeContent.setVisibility(View.VISIBLE);
            mTopContent.setVisibility(View.GONE);
            mBottomContent.setVisibility(View.GONE);
        } else {
            mTopContent.setText(content);
            mTopContent.setVisibility(View.VISIBLE);
            mBottomContent.setText(mesg);
            mBottomContent.setVisibility(View.VISIBLE);
            mWholeContent.setVisibility(View.GONE);
        }

        mLeftIcon.setImageResource(R.drawable.drivermode_simple_icon_music);
        boolean onPlay = mShowingCard.getPlayOn();
        if (onPlay) {
            mRightIcon.setImageResource(R.drawable.simple_mode_music_radio_pause_selector);
        } else {
            mRightIcon.setImageResource(R.drawable.simple_mode_music_radio_play_selector);
            Message timeOut = mHandler.obtainMessage(MSG_EXIT_CARD);
            mHandler.sendMessageDelayed(timeOut, EXIT_MUSIC_RADIO_TIME_MILLS);
        }
    }

    public void doInitRadioCardView(){
        mBottomCard.setVisibility(View.VISIBLE);
        mShowingCard = integrationCore.getCardInfo(CardController.CARD_TYPE_RADIO);
        /**mTopContent.setText(cardInfo.getContent());
        mTopContent.setVisibility(View.VISIBLE);
        mBottomContent.setText(cardInfo.getMessage());
        mBottomContent.setVisibility(View.VISIBLE);
        mWholeContent.setVisibility(View.GONE);*/

        String mesg = mShowingCard.getMessage();
        String content = mShowingCard.getContent();
        if (TextUtils.isEmpty(mesg)) {
            mWholeContent.setText(content);
            mWholeContent.setVisibility(View.VISIBLE);
            mTopContent.setVisibility(View.GONE);
            mBottomContent.setVisibility(View.GONE);
        } else {
            mTopContent.setText(content);
            mTopContent.setVisibility(View.VISIBLE);
            mBottomContent.setText(mesg);
            mBottomContent.setVisibility(View.VISIBLE);
            mWholeContent.setVisibility(View.GONE);
        }

        mLeftIcon.setImageResource(R.drawable.drivermode_simple_icon_radio);
        boolean onPlay = mShowingCard.getPlayOn();
        if (onPlay) {
            mRightIcon.setImageResource(R.drawable.simple_mode_music_radio_pause_selector);
        } else {
            mRightIcon.setImageResource(R.drawable.simple_mode_music_radio_play_selector);
            Message timeOut = mHandler.obtainMessage(MSG_EXIT_CARD);
            mHandler.sendMessageDelayed(timeOut, EXIT_MUSIC_RADIO_TIME_MILLS);
        }
    }

    public void doInitNewsCardView(){
        mBottomCard.setVisibility(View.VISIBLE);
        mShowingCard = integrationCore.getCardInfo(CardController.CARD_TYPE_NEWS);
        mTopContent.setVisibility(View.GONE);
        mBottomContent.setVisibility(View.GONE);
        mWholeContent.setText(mShowingCard.getContent());
        mWholeContent.setVisibility(View.VISIBLE);
        mLeftIcon.setImageResource(R.drawable.drivermode_simple_icon_news);
        mRightIcon.setImageResource(R.drawable.simple_mode_news_stop_selector);

        boolean onPlay = mShowingCard.getPlayOn();
        if (!onPlay) {
            Message timeOut = mHandler.obtainMessage(MSG_EXIT_CARD);
            mHandler.sendMessageDelayed(timeOut, EXIT_NEWS_TIME_MILLS);
        }
    }

    public void doNoneStreamUpdate(){
        mBottomCard.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=receiver&&null!=context){
            context.unregisterReceiver(receiver);
            receiver=null;
        }
    }
}
