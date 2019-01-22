package com.qinggan.app.arielapp.minor.main.driving.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.integration.PateoNewsCMD;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.ExtendListHeader;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.ExtendHeadAdapter;
import com.qinggan.app.arielapp.minor.main.commonui.pullextend.PullExtendLayout;
import com.qinggan.app.arielapp.minor.main.driving.adapter.IntelligenceAdapter;
import com.qinggan.app.arielapp.minor.main.driving.helper.DefaultItemTouchHelpCallback;
import com.qinggan.app.arielapp.minor.main.driving.helper.DefaultItemTouchHelper;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.minor.utils.ArielLog;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.utils.WakeupControlMgr;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;

import static com.tencent.qqmusic.third.api.contract.Events.API_EVENT_PLAY_SONG_CHANGED;

//智能模式
@SuppressLint("ValidFragment")
public class IntelligenceFragment extends AbstractBaseFragment implements IntegrationCore.MusicListener,
        IntegrationCore.UICallback, WakeupControlMgr.WakeupControlListener {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<CardInfo> dataList = new ArrayList<CardInfo>();
    private IntelligenceAdapter intelligenceAdapter;
    private LocalStorageTools localStorageTools;
    private FragmentManager fragmentManager;

    private Context mContext;
    private IntegrationCore integrationCore;

    private List<BasicInfo> cardList = new ArrayList<>();
    //private Map<String, CardInfo> cardInfoMap = new HashMap<>();
    private CardController cardController;

    private ExtendHeadAdapter extendHeadAdapterNew;
    private LinePulseView wakeupIcon;

    ExtendListHeader mPullNewHeader;
    RecyclerView listHeader, listFooter;
    List<String> mDatas = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private static final Object mSyncLock = new Object();

    ArrayList<String> list = new ArrayList<String>() {{
        add(API_EVENT_PLAY_SONG_CHANGED);
    }};
    private PullExtendLayout pull_extend;
    //卡片的默认顺序
    public static String[] orderStrs = {CardController.CARD_TYPE_NAVI,
            CardController.CARD_TYPE_MUSIC,
            CardController.CARD_TYPE_PHONE,
            CardController.CARD_TYPE_RADIO,
            CardController.CARD_TYPE_SCENARIO,
            CardController.CARD_TYPE_NOTICE,
            CardController.CARD_TYPE_NEWS};
    public static final int ALL_CARD_SIZE = 7;

    @SuppressLint("ClickableViewAccessibility")
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        if (view == null) {//优化View减少View的创建次数
            view = inflater.inflate(R.layout.driving_intelligence_layout, container, false);
            wakeupIcon = (LinePulseView) view.findViewById(R.id.wakeup);
            wakeupIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VoicePolicyManage.getInstance().record(true);
                }
            });
        }
        mContext = getActivity();
        //cardController=CardController.getCardController(mContext);
        localStorageTools = new LocalStorageTools(getActivity());//初始化本地储存
        String orderStr = localStorageTools.getString("orderCard");
        if (!orderStr.isEmpty()) {
            orderStrs = orderStr.split(",");
        }
        if (orderStrs.length != ALL_CARD_SIZE) {
            reInitOrderStrs();
        }

        integrationCore = IntegrationCore.getIntergrationCore(mContext);
        integrationCore.setMusicListener(this);
        integrationCore.registerEventListener(list);
        //[BUG:L0001M-486] MODIFIED BY brianchen@pateo.com.cn, at 2018.12.05, BEGIN
        integrationCore.registerUICallback(IntegrationCore.KEY_UICALLBACK_INTELLIGENCE, this);
        //[BUG:L0001M-486] MODIFIED BY brianchen@pateo.com.cn, at 2018.12.05, END

        recyclerView = (RecyclerView) view.findViewById(R.id.intelligence_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.getItemAnimator().setChangeDuration(0);// 通过设置动画执行时间为0来解决闪烁问题
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直
        //先看本地是否有卡片数据
//        cardList= integrationCore.searchDbData(mContext, CardInfo.class.getName());
        updateData();

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

        //增加长按拖拽等效果
        DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        itemTouchHelper.setDragEnable(true);
        itemTouchHelper.setSwipeEnable(false);
        return view;
    }

    private void reInitOrderStrs(){
        orderStrs = new String[ALL_CARD_SIZE];
        orderStrs[0] = CardController.CARD_TYPE_NAVI;
        orderStrs[1] = CardController.CARD_TYPE_MUSIC;
        orderStrs[2] = CardController.CARD_TYPE_PHONE;
        orderStrs[3] = CardController.CARD_TYPE_RADIO;
        orderStrs[4] = CardController.CARD_TYPE_SCENARIO;
        orderStrs[5] = CardController.CARD_TYPE_NEWS;
    }


    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener =
            new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
                @Override
                public void onSwiped(int adapterPosition) {
                    // 滑动删除的时候，从数据源移除，并刷新这个Item。
                    if (dataList != null) {
                        dataList.remove(adapterPosition);
                        intelligenceAdapter.notifyItemRemoved(adapterPosition);
                    }
                }

                @Override
                public boolean onMove(int srcPosition, int targetPosition) {
                    if (dataList != null) {
                        // 更换数据源中的数据Item的位置
                        Collections.swap(dataList, srcPosition, targetPosition);
                        // 更新UI中的Item的位置，主要是给用户看到交互效果
                        intelligenceAdapter.notifyItemMoved(srcPosition, targetPosition);
                        /**String order = "";
                        for (int i = 0; i < dataList.size(); i++) {
                            orderStrs[i] = dataList.get(i).getCardId();
                            if (i == dataList.size() - 1) {
                                order += dataList.get(i).getCardId();
                            } else {
                                order += dataList.get(i).getCardId() + ",";
                            }
                        }

                        localStorageTools.setString("orderCard", order);*/
                        resetOrder("normal");

                        return true;
                    }
                    return false;
                }
            };

    /**Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            dataList.clear();
            for (int i = 0; i < orderStrs.length; i++) {
                String cardId = orderStrs[i];
                if (cardId.equalsIgnoreCase(CardController.CARD_TYPE_NEWS)) {
                    int newStatus = integrationCore.mPateoNewsCMD.getNewsStatus();
                    if (newStatus != PateoNewsCMD.STATUS_STOP) {
                        continue;
                    }
                }
                CardInfo cardInfo = integrationCore.getCardInfo(cardId);
                if (null != cardInfo) {
                    //cardInfoMap.put(cardId, cardInfo);
                    dataList.add(cardInfo);
                }
            }
            if (message.what == 1) {
                if (null != intelligenceAdapter) {
                    intelligenceAdapter.notifyDataSetChanged();
                }
            }
            if (message.what == 0) {
                intelligenceAdapter = new IntelligenceAdapter(activity,
                        dataList, fragmentManager);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(intelligenceAdapter);
                ArielApplication.getApp().setDrRecyclerview(recyclerView);
            }
            return false;
        }
    });*/

    private void updateData() {
        dataList.clear();
        for (int i = 0; i < orderStrs.length; i++) {
            String cardId = orderStrs[i];
            if (cardId.equalsIgnoreCase(CardController.CARD_TYPE_NEWS)) {
                int newStatus = integrationCore.mPateoNewsCMD.getNewsStatus();
                if (newStatus != PateoNewsCMD.STATUS_PLAY) {
                    continue;
                }
            } else {
                boolean shouldContinue = false;
                for (String tempCardId : mNoNeedCard) {
                    if (cardId.equalsIgnoreCase(tempCardId)) {
                        shouldContinue = true;
                    }
                }

                if (shouldContinue) {
                    continue;
                }
            }

            CardInfo cardInfo = integrationCore.getCardInfo(cardId);
            if (null != cardInfo) {
                Log.i("Brian_card", "Add a cardInfo with cardId = " + cardId);
                dataList.add(cardInfo);
            }
        }

        Message mesg = mUiHandler.obtainMessage(MSG_UPDATE_ALL);
        mUiHandler.sendMessage(mesg);
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(final EventBusBean event) {
        String type = event.getType();
        switch (type) {
            case "close":
                EventBus.getDefault().post(new EventBusBean("downClose"));
                break;
            case "heardStatus":
                if (pull_extend != null) {
                    pull_extend.closeExtendHeadAndFooter();
                }
                break;
            case "simDropDown"://继续下拉关闭
                if (pull_extend != null) {
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
                            //PhoneStateManager.getInstance(mContext).setPhoneStateForTest(PhoneState.OUT_CAR_MODE);
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
                } catch (Exception e) {
                    Log.i("Alan", "IntelligenceFragment no search event");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBack(ArrayList list, boolean state) {
        //TODO
    }

    @Override
    public void songChange() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Alan","IntelligenceFragment onResume()...");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        addWakeupElements();
    }

    @Override
    public void onPause() {
        super.onPause();
        WakeupControlMgr.getInstance().clearElementUCWords(WakeupControlMgr.INTELLIGENCE_NAME_SPACE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("yy","--hidden ： " + hidden);
    }

    public void rgisterEventListener() {
        if (integrationCore == null) {
            integrationCore = IntegrationCore.getIntergrationCore(mContext);
            integrationCore.setMusicListener(this);
            integrationCore.registerEventListener(list);
        }
    }

    @Override
    public void callback2UI(int cmdId, Bundle bundle) {

    }

    @Override
    public void updateCardView(String cardId) {
        synchronized (mSyncLock) {
            CardInfo cardInfo = integrationCore.getCardInfo(cardId);

            if (cardInfo == null || dataList == null) {
                return;
            }

            if (cardId.equalsIgnoreCase(CardController.CARD_TYPE_NEWS)) {
                //if news has been stopped and news card exist, remove news card.
                if (!cardInfo.getPlayOn()) {
                    if (exsitInDataList(cardInfo)) {
                        int index = dataList.indexOf(cardInfo);
                        dataList.remove(cardInfo);
                        resetOrder("remove");
                        //updateData();
                        removeNewsCard(index);
                        return;
                    }
                } else {
                    if (!exsitInDataList(cardInfo)) {
                        dataList.add(cardInfo);
                        resetOrder("add");
                        //updateData();
                        AddNewsCard(dataList.indexOf(cardInfo));
                        return;
                    }
                }
            }

            //if the size of data list have not changed. find the index of this cardId, and update it.
            int index = -1;
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getCardId().
                        equalsIgnoreCase(cardId)) {
                    index = i;
                }
            }

            //find no index in list, perhaps it has been hidden.
            if (index == -1) {
                return;
            }

            dataList.set(index, cardInfo);
            updateUI(cardId, index);
        }
    }

    private void AddNewsCard(int index){
        Message mesg = mUiHandler.obtainMessage(MSG_ADD_NEWS);
        mesg.arg1 = index;
        mUiHandler.sendMessage(mesg);
    }

    private void removeNewsCard(int index){
        Message mesg = mUiHandler.obtainMessage(MSG_REMOVE_NEWS);
        mesg.arg1 = index;
        mUiHandler.sendMessage(mesg);
    }

    private boolean exsitInDataList(CardInfo cardInfo){
        if (cardInfo == null) {
            return false;
        }

        for (CardInfo temp: dataList) {
            if (temp.getCardId().equalsIgnoreCase(cardInfo.getCardId())){
                return true;
            }
        }

        return false;
    }

    /**
     * This array is no need card group, but CardController.CARD_TYPE_NEWS is different;
     */
    private String[] mNoNeedCard = {
            CardController.CARD_TYPE_SCENARIO,
            //CardController.CARD_TYPE_RADIO
    };

    private void resetOrder(String code){
        switch (code) {
            case "add":
                resetOrderOnAdd();
                break;
            case "remove":
                resetOrderOnRemove();
                break;
            case "normal":
                resetOrderOnNormal();
                break;
        }
    }

    private void resetOrderOnAdd(){
        resetOrderOnNormal();
    }

    private void resetOrderOnRemove(){
        int orderSize = orderStrs.length;
        int noNeedLength = mNoNeedCard.length;
        int normalIndexLength = orderSize - noNeedLength;
        for (int i = 0; i < orderSize; i++) {
            if (i == orderSize - 1) {
                orderStrs[i] = CardController.CARD_TYPE_NEWS;
            } else {
                //orderStrs[i] = dataList.get(i).getCardId();
                if (i >= normalIndexLength - 1) {
                    orderStrs[i] = mNoNeedCard[i - (normalIndexLength - 1)];
                } else {
                    orderStrs[i] = dataList.get(i).getCardId();
                }
            }
        }

        int orderStrLength = orderStrs.length;
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < orderStrLength; j++) {
            if(j == orderStrLength - 1) {
                result.append(orderStrs[j]);
            } else {
                result.append(orderStrs[j] + ",");
            }
        }

        ArielLog.logCore(ArielLog.LEVEL_DEBUG, "News_card_order",
                "Card order = " + result.toString() + ", when resetOrderOnRemove");
        for (int i = 0; i < orderStrs.length; i++) {
            ArielLog.logCore(ArielLog.LEVEL_DEBUG, "News_card_order",
                    "==== " + orderStrs[i] + " ==== when resetOrderOnRemove");
        }
        localStorageTools.setString("orderCard", result.toString());
    }

    private void resetOrderOnNormal(){
        int index = 0;
        int dataListSize = dataList.size();
        for (int i = 0; i < dataListSize; i++) {
            CardInfo cardInfo = dataList.get(i);
            orderStrs[i] = cardInfo.getCardId();
            index = i;
        }

        int noNeedCardLength = mNoNeedCard.length;
        for (int k = 0; k < noNeedCardLength; k++) {
            orderStrs[index + k + 1] = mNoNeedCard[k];
        }

        int orderStrLength = orderStrs.length;
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < orderStrLength; j++) {
            if(j == orderStrLength - 1) {
                result.append(orderStrs[j]);
            } else {
                result.append(orderStrs[j] + ",");
            }
        }

        ArielLog.logCore(ArielLog.LEVEL_DEBUG, "News_card_order",
                "Card order = " + result.toString() + ", when resetOrderOnNormal");

        for (int i = 0; i < orderStrs.length; i++) {
            ArielLog.logCore(ArielLog.LEVEL_DEBUG, "News_card_order",
                    "==== " + orderStrs[i] + " ==== when resetOrderOnNormal");
        }
        localStorageTools.setString("orderCard", result.toString());
    }

    @Override
    public void updateAllCard() {

    }

    //[BUG:L0001M-486] ADDED BY brianchen@pateo.com.cn, at 2018.12.05, BEGIN
    @Override
    public void updateSimpleCard() {

    }
    //[BUG:L0001M-486] ADDED BY brianchen@pateo.com.cn, at 2018.12.05, END

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {//可见

        } else {
            if (pull_extend != null) {
                pull_extend.closeExtendHeadAndFooter();
            }
        }
    }

    @Override
    public void onBackFolder(ArrayList list) {

    }

    Handler mUiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int index = message.arg1;
            switch (message.what) {
                case MSG_UPDATE_MUSIC:
                case MSG_UPDATE_NAVI:
                case MSG_UPDATE_PHONE:
                case MSG_UPDATE_RADIO:
                case MSG_UPDATE_SCENIARO:
                case MSG_UPDATE_NEWS:
                    doUpdateOneCard(index);
                    break;
                case MSG_UPDATE_ALL:
                    doUpdateAllCard();
                    break;
                case MSG_ADD_NEWS:
                    doAddNews(index);
                    break;
                case MSG_REMOVE_NEWS:
                    doRemoveNews(index);
                    break;
            }

            return false;
        }
    });

    private void doAddNews(int index){
        intelligenceAdapter.notifyItemInserted(index);
    }

    private void doRemoveNews(int index){
        intelligenceAdapter.notifyItemRemoved(index);
    }

    private void doUpdateAllCard() {
        intelligenceAdapter = new IntelligenceAdapter(getActivity(),
                dataList, fragmentManager);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(intelligenceAdapter);
        ArielApplication.getApp().setDrRecyclerview(recyclerView);
    }

    private void doUpdateOneCard(int index){
        intelligenceAdapter.notifyItemChanged(index);
        recyclerView.invalidate(); // 刷新界面
    }

    public static final int MSG_UPDATE_NAVI = 0;
    public static final int MSG_UPDATE_MUSIC = 1;
    public static final int MSG_UPDATE_RADIO = 2;
    public static final int MSG_UPDATE_SCENIARO = 3;
    public static final int MSG_UPDATE_PHONE = 4;
    public static final int MSG_UPDATE_NEWS = 5;
    public static final int MSG_UPDATE_ALL = 6;
    public static final int MSG_ADD_NEWS = 7;
    public static final int MSG_REMOVE_NEWS = 8;

    private void updateUI(String cardId, int index) {
        Message mesg = mUiHandler.obtainMessage();
        switch (cardId) {
            case CardController.CARD_TYPE_MUSIC:
                mesg.what = MSG_UPDATE_MUSIC;
                break;
            case CardController.CARD_TYPE_NAVI:
                mesg.what = MSG_UPDATE_NAVI;
                break;
            case CardController.CARD_TYPE_PHONE:
                mesg.what = MSG_UPDATE_PHONE;
                break;
            case CardController.CARD_TYPE_RADIO:
                mesg.what = MSG_UPDATE_RADIO;
                break;
            case CardController.CARD_TYPE_SCENARIO:
                mesg.what = MSG_UPDATE_SCENIARO;
                break;
            case CardController.CARD_TYPE_NEWS:
                mesg.what = MSG_UPDATE_NEWS;
                break;
        }
        mesg.arg1 = index;
        mUiHandler.sendMessage(mesg);
    }

    @Override
    public void playStateChanged() {
    }

    @Override
    public void favStateChange() {

    }

    @Override
    public void playListChange() {

    }

    @Override
    public void playModeChange() {

    }

    @Override
    public void codeState(boolean state) {

    }

    @Override
    public void forSpecialLocalCode(String cmd, int ret) {

    }

    @Override
    public void backFolderName(String name) {

    }


    private void addWakeupElements() {
        WakeupControlMgr.getInstance().setElementUCWords(WakeupControlMgr.INTELLIGENCE_NAME_SPACE,
                0, 0, this);
    }

    @Override
    public void onItemSelected(String type, String key) {
        Log.e("IntelligenceFragment","onItemSelected type : " + type + "--key : " + key);
        if (!WakeupControlMgr.INTELLIGENCE_NAME_SPACE.equals(type)) {
            return;
        }

        if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            return;
        }
        
        if (WakeupControlMgr.INTELLIGENCE_START_NAV.equals(key)) {
            integrationCore.VoiceJump(StageController.Stage.NAVIGATION);
        } else if (WakeupControlMgr.INTELLIGENCE_START_MUSIC.equals(key)) {
            integrationCore.VoiceJump(StageController.Stage.MUSIC);
        } else if (WakeupControlMgr.INTELLIGENCE_START_FM.equals(key)) {
            integrationCore.VoiceJump(StageController.Stage.RADIO);
        } else if (WakeupControlMgr.INTELLIGENCE_START_CALL.equals(key)) {
            integrationCore.VoiceJump(StageController.Stage.PHONE);
        } else if (WakeupControlMgr.INTELLIGENCE_START_CAR.equals(key)) {
            integrationCore.VoiceJump(StageController.Stage.SCENARIO);
        }
    }
}
