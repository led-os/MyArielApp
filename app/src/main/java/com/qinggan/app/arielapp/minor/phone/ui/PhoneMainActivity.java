package com.qinggan.app.arielapp.minor.phone.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.UIControlBaseActivity;
import com.qinggan.app.arielapp.WheelControl.Listener.PhoneCallControlListener;
import com.qinggan.app.arielapp.WheelControl.WheelControl;
import com.qinggan.app.arielapp.minor.phone.CallLogManager;
import com.qinggan.app.arielapp.minor.phone.adapter.CallRecordAdapter;
import com.qinggan.app.arielapp.minor.phone.bean.CallRecord;
import com.qinggan.app.arielapp.minor.phone.bean.EventBusCallLog;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.voiceview.PhoneHintFloatView;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.qinggan.app.voiceapi.control.ConstantNavUc.PHONE_NO_SELECT;

public class PhoneMainActivity extends UIControlBaseActivity {

    private static final String TAG = PhoneMainActivity.class.getSimpleName();
    private static final int MSG_START_CALL_OUT = 1;
    private static final int MSG_START_VOICE_SEARCHING = 2;
    private static final int MSG_START_PLAY_VOICE_HINT = 3;
    private static final int MSG_HOTWORD_LAST_PAGE = 4;
    private static final int MSG_HOTWORD_NEXT_PAGE = 5;
    private static final int MSG_HOTWORD_ITEM_SELECT = 6;
    private static final int MSG_HOTWORD_FINISH = 7;

    private static final String MODULE_NAME = "PHONE_MAIN";
    private int[] numbers =  new int[]{R.string.phone_first, R.string.phone_second, R.string.phone_third, R.string.phone_fourth,
            R.string.phone_five, R.string.phone_six, R.string.phone_seven, R.string.phone_eight};
    private String[] numberUcs = new String[]{ConstantNavUc.PHONE_FIRST, ConstantNavUc.PHONE_SECOND, ConstantNavUc.PHONE_THIRD,  ConstantNavUc.PHONE_FOURTH,
            ConstantNavUc.PHONE_FIVE, ConstantNavUc.PHONE_SIX, ConstantNavUc.PHONE_SEVEN, ConstantNavUc.PHONE_EIGHT};

    private Context mContext;
    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;

    private CallRecordAdapter allCallLogAdapter;

    private ListView allCallLogListView;

    private List<CallRecord> allCallLogList = new ArrayList<>();
    private List<CallRecord> recentCallLogList;
    private List<CallRecord> favourCallLogList;


    RelativeLayout allCallRecordLayout;

    private LinePulseView btnVoiceInput;
    private TextView tvCallFor;
    private ImageButton btnBack;
    private PhoneHintFloatView phoneHintFloatView;

    private int selectItem = 0;
    private PhoneCallControlListener phoneCallControlListener = null;
    private WheelControl wheelControl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mContext = this;
        EventBus.getDefault().register(this);
        CallLogManager.getInstance(mContext).syncCallLog();

        VoicePolicyManage.getInstance().speakStop();
        Message msgHint = Message.obtain(mHandler);
        msgHint.what = MSG_START_PLAY_VOICE_HINT;
        mHandler.sendMessageDelayed(msgHint, 0);

        mHotwordManager = HotwordManager.getInstance(this, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                if (b && !isOnpause) {
                    addHotword();
                } else {
                    if (mHotwordManager != null) {
                        mHotwordManager.clearElementUCWords(MODULE_NAME);
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        initPhoneWheelControllerListener();
        VoicePolicyManage.getInstance().addTtsStatusListeners(mTtsStateChangeListener);
        addHotword();
        if (isOnpause) {
            addUIControl();
        }
        isOnpause = false;

        UMAnalyse.startTime(UMDurationEvent.PHONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        phoneCallControlListener = null;
        if(wheelControl != null){
            wheelControl.setPhoneListener(null);
        }

        //remove and dismiss
        if (mTtsStateChangeListener != null) {
            VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
            mTtsStateChangeListener = null;
        }
        if (phoneHintFloatView.isShown()) {
            phoneHintFloatView.dismiss();
        }
        removeHotword();
        isOnpause = true;

        UMAnalyse.stopTime(UMDurationEvent.PHONE);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        allCallLogListView.setOnScrollListener(mOnScrollListener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.phone_main_call_log;
    }

    @Override
    public void onSelectCancel() {
        CallUtils.logd(TAG, "onSelectCancel");
        finish();
    }

    @Override
    public void onSelectItemPosition(int position) {
        CallUtils.logi(TAG, "onSelectItemPosition:" + position);
        if (allCallLogListView.getFirstVisiblePosition() + position + 1 > allCallLogListView.getCount()) {
            CallUtils.logi(TAG, "onSelectItemPosition out of list");
            return;
        }
        int headItemCount = 0;
        int firstVisiblePosition = allCallLogListView.getFirstVisiblePosition();
        if (firstVisiblePosition > 0) {
            for (int i = 0; i <= position; i++) {
                CallRecord cr = allCallLogList.get(firstVisiblePosition + i);
                if (cr.getItemType() != CallRecord.ITEM_TYPE_DATA) {
                    headItemCount++;
                }
            }
        } else {
            for (int i = 0; i <= position + 1; i++) {
                CallRecord cr = allCallLogList.get(firstVisiblePosition + i);
                if (cr.getItemType() != CallRecord.ITEM_TYPE_DATA) {
                    headItemCount++;
                }
            }
        }
        int selectPosition = firstVisiblePosition + position + headItemCount;
        CallUtils.logi(TAG, "onSelectItemPosition firstVisiblePosition:" + firstVisiblePosition + "| headItemCount" + headItemCount);
        CallRecord cr = allCallLogList.get(selectPosition);
        if (cr != null && cr.getItemType() == CallRecord.ITEM_TYPE_DATA) {
            selectItem = selectPosition;
            allCallLogAdapter.setSelectItem(selectPosition);
            allCallLogAdapter.notifyDataSetChanged();

            CallUtils.logi(TAG, "onSelectItemPosition cr:" + cr.getName() + "| " + cr.getNumber());
            startCallOut(cr);
        } else {
            if (cr == null) {
                CallUtils.logi(TAG, "onSelectItemPosition cr is null");
            } else {
                CallUtils.logi(TAG, "onSelectItemPosition cr.getItemType()：" + cr.getItemType());
            }
        }
    }

    @Override
    public void onSelectOtherOC(String action) {
        CallUtils.logd(TAG, "onSelectOtherOC：" + action);
        if (ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM.equals(action)) {
            CallUtils.logd(TAG, "onSelectOtherOC getLastVisiblePosition:" + allCallLogListView.getLastVisiblePosition());
            allCallLogListView.setSelection(allCallLogListView.getLastVisiblePosition());
        } else if (ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM.equals(action)) {
            int prePosition = allCallLogListView.getFirstVisiblePosition() - getListVisibleCount();
            if (prePosition > 0) {
                allCallLogListView.setSelection(prePosition);
            } else {
                allCallLogListView.setSelection(0);
            }
        } else if (ConstantNavUc.PHONE_BACK.equals(action)) {
            VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
            VoicePolicyManage.getInstance().interrupt();
            finish();
        } else if (action != null && action.startsWith(ConstantNavUc.PHONE_NAME_ITEM)) {
            String[] splitAction = action.split(ConstantNavUc.PHONE_NAME_ITEM);
            if (splitAction != null && splitAction.length == 2) {
                int itemPosition = Integer.parseInt(String.valueOf(splitAction[1]));
                CallUtils.logi(TAG, "onSelectItemPosition itemPosition:" + itemPosition);
                CallRecord cr = allCallLogList.get(itemPosition);
                if (cr != null && cr.getItemType() == CallRecord.ITEM_TYPE_DATA) {
                    selectItem = itemPosition;
                    allCallLogAdapter.setSelectItem(itemPosition);
                    allCallLogAdapter.notifyDataSetChanged();

                    CallUtils.logi(TAG, "onSelectItemPosition cr:" + cr.getName() + "| " + cr.getNumber());
                    startCallOut(cr);
                }
            }
        }
    }

    private void addUIControl() {
        //clear UIControl
        mUiControlItems.clear();
        mUIControlElements.clear();

        //add back
        UIControlElementItem back = new UIControlElementItem();
        back.addWord(getString(R.string.phone_back1));
        back.addWord(getString(R.string.phone_back2));
        back.addWord(getString(R.string.phone_back3));
        back.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PHONE_BACK);
        mUIControlElements.add(back);

        //add next and pre
        UIControlElementItem prePage = new UIControlElementItem();
        prePage.addWord(getString(R.string.last_page));
        prePage.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM);
        mUIControlElements.add(prePage);
        UIControlElementItem uiNextPage = new UIControlElementItem();
        uiNextPage.addWord(getString(R.string.next_page));
        uiNextPage.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM);
        mUIControlElements.add(uiNextPage);

        //add list item of ITEM_TYPE_DATA
        int count = allCallLogListView.getLastVisiblePosition() - allCallLogListView.getFirstVisiblePosition() + 1;
        int dataItemsIndex = 0;
        for (int i = 0; i < count; i++) {
            if (allCallLogList.get(i).getItemType() == CallRecord.ITEM_TYPE_DATA) {
                UIControlItem uiItem = new UIControlItem();
                uiItem.setLabel(allCallLogList.get(i).getNumber());
                uiItem.setIndex(dataItemsIndex);
                String url = mFragmentHashCode + "-" + PHONE_NO_SELECT + ":" + dataItemsIndex;
                uiItem.setUrl(url);
                mUiControlItems.add(uiItem);
                CallUtils.logd(TAG, "url:" + url);
                dataItemsIndex++;
            }
        }

        //add name at visible position
        int firstPosition = allCallLogListView.getFirstVisiblePosition();
        int lastPosition = allCallLogListView.getLastVisiblePosition();
        for (int i = firstPosition; i < lastPosition + 1; i++) {
            if (allCallLogList.get(i).getItemType() == CallRecord.ITEM_TYPE_DATA) {
                String name = allCallLogList.get(i).getName();
                if (name != null && name.length() > 0 ) {
                    UIControlElementItem item = new UIControlElementItem();
                    item.addWord(name);
                    item.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PHONE_NAME_ITEM + i);
                    mUIControlElements.add(item);
                }
            }
        }

        //add default
        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCallLog event) {
        recentCallLogList = event.getRecentCallLogList();
        favourCallLogList = event.getFavourCallLogList();

//        allCallLogList = new ArrayList<>();
        allCallLogList.clear();
        if (favourCallLogList != null && favourCallLogList.size() > 0) {
            CallRecord crHead = new CallRecord();
            crHead.setItemType(CallRecord.ITEM_TYPE_FAVOUR);
            allCallLogList.add(crHead);
            allCallLogList.addAll(favourCallLogList);
        }
        if (recentCallLogList != null && recentCallLogList.size() > 0) {
            CallRecord crHead = new CallRecord();
            crHead.setItemType(CallRecord.ITEM_TYPE_RECENT);
            allCallLogList.add(crHead);
            allCallLogList.addAll(recentCallLogList);
        }

        CallUtils.logd(TAG, "allCallLogList:" + allCallLogList.size());
        if (allCallLogList != null && allCallLogList.size() > 0) {
            allCallRecordLayout.setVisibility(View.VISIBLE);
            setAllCallLogAdapter(allCallLogList);
        } else {
            allCallRecordLayout.setVisibility(View.GONE);
        }

        allCallLogListView.setOnScrollListener(mOnScrollListener);
        allCallLogListView.post(new Runnable() {
            @Override
            public void run() {
                if(!isOnpause) {
                    addUIControl();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CallUtils.logd(TAG, "mHandler handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_START_CALL_OUT:
                    CallRecord cr = allCallLogList.get(msg.arg1);
                    if (cr != null && cr.getItemType() == CallRecord.ITEM_TYPE_DATA) {
                        startCallOut(cr);
                    }
                    break;
                case MSG_START_VOICE_SEARCHING:
                    if (mTtsStateChangeListener != null) {
                        VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
                        mTtsStateChangeListener = null;
                    }
                    VoicePolicyManage.getInstance().record(true);
                    phoneHintFloatView.show();
                    break;
                case MSG_START_PLAY_VOICE_HINT:
                    VoicePolicyManage.getInstance().speak(getString(R.string.phone_hint_call_for_voice));
                    break;
                case MSG_HOTWORD_LAST_PAGE:
                    onSelectOtherOC(ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM);
                    break;
                case MSG_HOTWORD_NEXT_PAGE:
                    onSelectOtherOC(ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM);
                    break;
                case MSG_HOTWORD_ITEM_SELECT:
                    onSelectItemPosition(msg.arg1);
                    break;
                case MSG_HOTWORD_FINISH:
                    VoicePolicyManage.getInstance().interrupt();
                    finish();
                    break;
            }
        };
    };

    @Override
    protected void initView() {
        allCallLogListView = (ListView) findViewById(R.id.all_call_record);
        allCallLogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CallUtils.logd(TAG, "recentCallLogListView.setOnItemClick");
                CallRecord callRecord = (CallRecord) allCallLogAdapter.getItem(position);
                CallUtils.logd(TAG, callRecord.toString());
                //startDialog();
                //sCallUtils.setSpeakerOnOff(mContext);
                //CallUtils.startCall(mContext, callRecord);
                //remove incall UI,use system phone incall
                /*Message msg = Message.obtain(mHandler);
                msg.what = MSG_START_INCALL;
                msg.obj = contactsInfo;
                mHandler.sendMessageDelayed(msg, 100);*/
                startCallOut(callRecord);
            }
        });

        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
                VoicePolicyManage.getInstance().interrupt();
                finish();
            }
        });

        tvCallFor = (TextView) findViewById(R.id.tv_call_for);
        tvCallFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhoneContactsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnVoiceInput = (LinePulseView) findViewById(R.id.btn_voice_input);
        btnVoiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, PhoneContactsActivity.class);
//                intent.putExtra(Constants.SEARCH_MODE, Constants.SEARCH_MODE_VOICE_INPUT);
//                startActivity(intent);
                VoicePolicyManage.getInstance().record(true);
                if (!phoneHintFloatView.isShown()) {
                    phoneHintFloatView.show();
                }
            }
        });

        allCallRecordLayout = (RelativeLayout) findViewById(R.id.all_contacts_call_record_layout);
        phoneHintFloatView = PhoneHintFloatView.getInstance(this);
    }

    private void setAllCallLogAdapter(List<CallRecord> list) {
        int size = 0;
        if (favourCallLogList != null && favourCallLogList.size() > 0) {
            size = favourCallLogList.size();
        }
        allCallLogAdapter = new CallRecordAdapter(mContext, list, size);
        allCallLogListView.setAdapter(allCallLogAdapter);
    }

    private void startCallOut(CallRecord callRecord) {
        CallUtils.startCallByPhoneNumber(mContext, callRecord.getNumber());
        CallUtils.startInCallUI(mContext, callRecord.getNumber(), callRecord.getName());
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void listPositionUp() {
        if (selectItem <= 0) {
            return;
        }

        // item scorll out by user, then reset to position
        if (selectItem - 1 < allCallLogListView.getFirstVisiblePosition()
                || selectItem - 1 > allCallLogListView.getLastVisiblePosition()) {
            selectItem = allCallLogListView.getLastVisiblePosition();
            allCallLogAdapter.setSelectItem(selectItem);
            allCallLogAdapter.notifyDataSetChanged();
            allCallLogListView.setSelection(selectItem);
            return;
        }

        allCallLogAdapter.setSelectItem(selectItem - 1);
        allCallLogAdapter.notifyDataSetChanged();
        scorllItemUp(selectItem - 1);
        selectItem--;
    }

    private void listPositionDown() {
        if (selectItem >= allCallLogListView.getCount() - 1) {
            return;
        }

        // item scorll out by user, then reset to position
        if (selectItem + 1 > allCallLogListView.getLastVisiblePosition()
                || selectItem + 1 < allCallLogListView.getFirstVisiblePosition()) {
            selectItem = allCallLogListView.getFirstVisiblePosition();
            allCallLogAdapter.setSelectItem(selectItem);
            allCallLogAdapter.notifyDataSetChanged();
            allCallLogListView.setSelection(selectItem);
            return;
        }

        allCallLogAdapter.setSelectItem(selectItem + 1);
        allCallLogAdapter.notifyDataSetChanged();
        scorllItemDown(selectItem + 1);
        selectItem++;
    }

    private void scorllItemDown(int position) {
        int n = getListVisibleCount() / 2;
        if (position - allCallLogListView.getFirstVisiblePosition() == n
                || position - allCallLogListView.getFirstVisiblePosition() == n + 1) {
            allCallLogListView.setSelection(allCallLogListView.getFirstVisiblePosition() + 1);
        }
    }

    private void scorllItemUp(int position) {
        int n = getListVisibleCount() / 2;
        if (position - allCallLogListView.getFirstVisiblePosition() == n
                || position - allCallLogListView.getFirstVisiblePosition() == n - 1) {
            allCallLogListView.setSelection(allCallLogListView.getFirstVisiblePosition() - 1);
        }
    }

    private int getListVisibleCount() {
        int tempCount = (allCallLogListView.getLastVisiblePosition())
                - allCallLogListView.getFirstVisiblePosition();
        return tempCount;
    }

    private void stratCallByCar() {
        CallRecord callRecord = allCallLogList.get(selectItem);
        if (callRecord.getItemType() == CallRecord.ITEM_TYPE_DATA) {
            this.startCallOut(callRecord);
        }
    }

    private void initPhoneWheelControllerListener() {
        if (wheelControl == null) {
            wheelControl = ArielApplication.getWheelControlManager();
        }
        if (phoneCallControlListener != null) phoneCallControlListener = null;
        phoneCallControlListener = new PhoneCallControlListener() {
            @Override
            public void previous() {
                listPositionUp();
            }

            @Override
            public void next() {
                listPositionDown();
            }

            @Override
            public void hangUp() {

            }

            @Override
            public void inCall() {
                stratCallByCar();
            }
        };
        wheelControl.setPhoneListener(phoneCallControlListener);
    }

    private void addHotword() {
        if (mHotwordManager == null) {
            CallUtils.logd(TAG, "addPhoneHotword mHotwordManager is null");
            return;
        }
        removeHotword();
        CallUtils.logd(TAG, "addPhoneHotword");
        ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> mWakeUpElements = new ArrayList<>();

        //add next and pre
        com.qinggan.qinglink.bean.UIControlElementItem prePage = new com.qinggan.qinglink.bean.UIControlElementItem();
        prePage.setWord(getString(R.string.last_page));
        prePage.setIdentify(ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM);
        mWakeUpElements.add(prePage);
        com.qinggan.qinglink.bean.UIControlElementItem uiNextPage = new com.qinggan.qinglink.bean.UIControlElementItem();
        uiNextPage.setWord(getString(R.string.next_page));
        uiNextPage.setIdentify(ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM);
        mWakeUpElements.add(uiNextPage);

        //add back
        com.qinggan.qinglink.bean.UIControlElementItem back1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        back1.setWord(getString(R.string.phone_back4));
        back1.setIdentify(ConstantNavUc.PHONE_BACK);
        mWakeUpElements.add(back1);
        com.qinggan.qinglink.bean.UIControlElementItem back2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        back2.setWord(getString(R.string.phone_back5));
        back2.setIdentify(ConstantNavUc.PHONE_BACK1);
        mWakeUpElements.add(back2);
//        com.qinggan.qinglink.bean.UIControlElementItem back3 = new com.qinggan.qinglink.bean.UIControlElementItem();
//        back3.setWord(getString(R.string.phone_back3));
//        back3.setIdentify(ConstantNavUc.PHONE_BACK2);
//        mWakeUpElements.add(back3);

        //add list item of ITEM_TYPE_DATA
        for(int i = 0; i < 8; i++){
            com.qinggan.qinglink.bean.UIControlElementItem numberNaviElement = new com.qinggan.qinglink.bean.UIControlElementItem();
            numberNaviElement.setWord(getString(numbers[i]));
            numberNaviElement.setIdentify(numberUcs[i]);
            mWakeUpElements.add(numberNaviElement);
        }

        mHotwordManager.setElementUCWords(MODULE_NAME, mWakeUpElements);
        mHotwordManager.registerListener(MODULE_NAME, new HotwordListener() {

            @Override
            public void onItemSelected(String s) {
                CallUtils.logd(TAG, "HotwordListener onItemSelected:" + s);
                Message msg = Message.obtain(mHandler);
                switch (s){
                    case ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM:
                        msg.what = MSG_HOTWORD_LAST_PAGE;
                        mHandler.sendMessageDelayed(msg, 0);
                        break;
                    case ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM:
//                        allCallLogListView.setSelection(allCallLogListView.getLastVisiblePosition());
                        msg.what = MSG_HOTWORD_NEXT_PAGE;
                        mHandler.sendMessageDelayed(msg, 0);
                        break;
                    case ConstantNavUc.PHONE_BACK:
                    case ConstantNavUc.PHONE_BACK1:
                    case ConstantNavUc.PHONE_BACK2:
                        msg.what = MSG_HOTWORD_FINISH;
                        mHandler.sendMessageDelayed(msg, 0);
                        break;
                    case ConstantNavUc.PHONE_FIRST:
                        sendHotWordItemPosition(msg, 0);
                        break;
                    case ConstantNavUc.PHONE_SECOND:
                        sendHotWordItemPosition(msg, 1);
                        break;
                    case ConstantNavUc.PHONE_THIRD:
                        sendHotWordItemPosition(msg, 2);
                        break;
                    case ConstantNavUc.PHONE_FOURTH:
                        sendHotWordItemPosition(msg, 3);
                        break;
                    case ConstantNavUc.PHONE_FIVE:
                        sendHotWordItemPosition(msg, 4);
                        break;
                    case ConstantNavUc.PHONE_SIX:
                        sendHotWordItemPosition(msg, 5);
                        break;
                    case ConstantNavUc.PHONE_SEVEN:
                        sendHotWordItemPosition(msg, 6);
                        break;
                    case ConstantNavUc.PHONE_EIGHT:
                        sendHotWordItemPosition(msg, 7);
                        break;
                }
            }
            @Override
            public void onPageSelected(int i) {}
            @Override
            public void onSwitchPage(int i) {}
        });
    }

    private void removeHotword() {
        CallUtils.logd(TAG, "HotwordListener removeHotword");
        if (mHotwordManager != null) {
            mHotwordManager.clearElementUCWords(MODULE_NAME);
        }
    }

    private void sendHotWordItemPosition(Message msg, int position) {
        msg.what = MSG_HOTWORD_ITEM_SELECT;
        msg.arg1 = position;
        mHandler.sendMessageDelayed(msg, 0);
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                if (allCallLogListView.getLastVisiblePosition() + 1 != allCallLogListView.getCount()) {
                    allCallLogListView.smoothScrollToPosition(allCallLogListView.getFirstVisiblePosition());
                }
                addUIControl();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    VoicePolicyManage.TtsStateChangeListener mTtsStateChangeListener = new VoicePolicyManage.TtsStateChangeListener(){

        @Override
        public void onStart() {

        }

        @Override
        public void onDone() {
            CallUtils.logi(TAG, "mTtsStateChangeListener onDone");
            Message msg = Message.obtain(mHandler);
            msg.what = MSG_START_VOICE_SEARCHING;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onError() {
            CallUtils.logi(TAG, "mTtsStateChangeListener onError");
            Message msg = Message.obtain(mHandler);
            msg.what = MSG_START_VOICE_SEARCHING;
            mHandler.sendMessage(msg);
        }
    };
}
