package com.qinggan.app.arielapp.minor.phone.ui;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.UIControlBaseActivity;
import com.qinggan.app.arielapp.WheelControl.Listener.PhoneCallControlListener;
import com.qinggan.app.arielapp.WheelControl.WheelControl;
import com.qinggan.app.arielapp.minor.phone.Constants;
import com.qinggan.app.arielapp.minor.phone.adapter.ContactsInfoAdapter;
import com.qinggan.app.arielapp.minor.phone.bean.ContactsInfo;
import com.qinggan.app.arielapp.minor.phone.service.ArielPhoneService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.qinggan.app.voiceapi.control.ConstantNavUc.PHONE_NO_SELECT;

public class PhoneContactsActivity  extends UIControlBaseActivity {

    private static final String TAG = PhoneContactsActivity.class.getSimpleName();
    private static final int MSG_START_CALL_OUT = 1;
    private static final int MSG_LOADING_ANIM = 2;
    private static final int MSG_LOADING_CALL_OUT = 3;
    private static final int MSG_START_VOICE_SEARCHING = 4;
    private static final int MSG_HOTWORD_LAST_PAGE = 5;
    private static final int MSG_HOTWORD_NEXT_PAGE = 6;
    private static final int MSG_HOTWORD_ITEM_SELECT = 7;
    private static final String MODULE_NAME = "PHONE_CONTACTS";
    private int[] numbers =  new int[]{R.string.phone_first, R.string.phone_second, R.string.phone_third, R.string.phone_fourth,
            R.string.phone_five, R.string.phone_six, R.string.phone_seven, R.string.phone_eight};
    private String[] numberUcs = new String[]{ConstantNavUc.PHONE_FIRST, ConstantNavUc.PHONE_SECOND, ConstantNavUc.PHONE_THIRD,  ConstantNavUc.PHONE_FOURTH,
            ConstantNavUc.PHONE_FIVE, ConstantNavUc.PHONE_SIX, ConstantNavUc.PHONE_SEVEN, ConstantNavUc.PHONE_EIGHT};

    private Context mContext;
    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;
    private String initSearchMode = "";

    private List<ContactsInfo> contactsInfosList;

    RelativeLayout callPromptLayout;
    RelativeLayout contactsListLayout;

    private EditText etCallFor;
    private ImageButton btnBack;
    private LinePulseView btnVoiceInput;
    private LinearLayout phoneSelectionSuggestion;
    private PhoneHintFloatView phoneHintFloatView;

    private ContactsInfoAdapter contactsInfoAdapter;
    private ListView lvContactsList;


    private ContactsAsyncQueryHandler asyncQueryHandler;
    private String mContactsSearchKey = "";

    private int selectItem = -1;
    private PhoneCallControlListener phoneCallControlListener = null;
    private WheelControl wheelControl = null;

    //voice searching
    private ArrayList<ContactsInfo> voiceSearchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mContext = this;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            initSearchMode = bundle.getString(Constants.SEARCH_MODE, "");
        }

        initView();
        if (!Constants.SEARCH_MODE_VOICE_INPUT.equals(initSearchMode)) {
            CallUtils.logd(TAG, "initSearchMode:" + initSearchMode);
            Bundle bundleVoiceSearchData = intent.getBundleExtra("voiceSearchData");
            if(bundleVoiceSearchData != null) {
                voiceSearchData = bundleVoiceSearchData.getParcelableArrayList("list");
            }
            if(voiceSearchData != null && voiceSearchData.size() > 0) {
                CallUtils.logd(TAG, "voiceSearchData size:" + voiceSearchData.size());
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                etCallFor.setText(voiceSearchData.get(0).getDisplayName());
                //voice searching mode
                setContactsInfoAdapter(voiceSearchData, true);
//                Message msg = Message.obtain(mHandler);
//                msg.what = MSG_START_VOICE_SEARCHING;
//                mHandler.sendMessageDelayed(msg, 500);
//                startDialLoading();
                //speak hint
                VoicePolicyManage.getInstance().speak(getString(R.string.phone_hint_call_which_one));
            } else {
                loadContactsData();
            }
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            callPromptLayout.setVisibility(View.VISIBLE);
            contactsListLayout.setVisibility(View.GONE);
            //call voice search
//            VoicePolicyManage.getInstance().record(true);
            Message msg = Message.obtain(mHandler);
            msg.what = MSG_START_VOICE_SEARCHING;
            mHandler.sendMessageDelayed(msg, 200);
        }

        registUIControl();
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
        etCallFor.requestFocus();
        VoicePolicyManage.getInstance().addTtsStatusListeners(mTtsStateChangeListener);
        addHotword();

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
        phoneSelectionSuggestion.setVisibility(View.GONE);
        removeHotword();

        UMAnalyse.stopTime(UMDurationEvent.PHONE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finishOfVoiceSearch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDialLoading();
    }

    @Override
    protected void initView() {

        etCallFor = (EditText) findViewById(R.id.et_call_for);
        etCallFor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(voiceSearchData != null && voiceSearchData.size() > 0) {
                    return;
                }
                mContactsSearchKey = etCallFor.getText().toString();
                loadContactsData();
            }
        });

        btnVoiceInput = (LinePulseView) findViewById(R.id.btn_voice_input);
        btnVoiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call voice search
                phoneSelectionSuggestion.setVisibility(View.GONE);
                VoicePolicyManage.getInstance().record(true);
                if (!phoneHintFloatView.isShown()) {
                    phoneHintFloatView.show();
                }
            }
        });

        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvContactsList = (ListView) findViewById(R.id.lv_contacts_list);
        lvContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactsInfo contactsInfo = (ContactsInfo) contactsInfoAdapter.getItem(position);
                stopDialLoading();
                CallUtils.startCallDirect(mContext, contactsInfo);
            }
        });

        callPromptLayout = (RelativeLayout) findViewById(R.id.call_prompt_layout);
        contactsListLayout = (RelativeLayout) findViewById(R.id.contacts_list_layout);
        contactsListLayout.setVisibility(View.GONE);

        phoneSelectionSuggestion = (LinearLayout) findViewById(R.id.phone_selection_suggestion);
        phoneHintFloatView = PhoneHintFloatView.getInstance(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.phone_contacts;
    }

    /**
     * 初始化数据库查询参数
     */
    private void loadContactsData() {
        // 实例化
        asyncQueryHandler = new ContactsAsyncQueryHandler(mContext.getContentResolver());
        CallUtils.logd(TAG, "mContactsSearchKey:" + mContactsSearchKey);
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = {ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        };

        String selection = "";
        if (isPhoneNum(mContactsSearchKey)) {
            CallUtils.logd(TAG, "isPhoneNum true");
            selection = ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'"
                    + " AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE " + "'%" + mContactsSearchKey + "%'";
        } else {
            selection = ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'"
                    + " AND " + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE " + "'%" + mContactsSearchKey + "%'";
        }
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, selection, null,
                "sort_key asc");

    }

    private boolean isPhoneNum(String keyword) {
        //正则 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码
        if (keyword.matches("^([0-9]|[/+]).*")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSelectCancel() {
        CallUtils.logd(TAG, "onSelectCancel");
        stopDialLoading();
//        finishOfVoiceSearch();
        this.finish();
    }

    @Override
    public void onSelectItemPosition(int position) {
        CallUtils.logd(TAG, "onSelectItemPosition:"+position);
        stopDialLoading();
        if (voiceSearchData != null) {
            ContactsInfo contactsInfo = voiceSearchData.get(position);
            if (contactsInfo != null) {
                contactsInfoAdapter.setSelectedItem(position);
                contactsInfoAdapter.notifyDataSetChanged();
                CallUtils.startCallDirect(mContext, contactsInfo);
            }
        } else if (contactsInfosList != null && contactsInfosList.size() > 0) {
            CallUtils.logd(TAG, "onSelectItemPosition voiceSearchData is null");
            int selectPosition = lvContactsList.getFirstVisiblePosition() + position;
            ContactsInfo contactsInfo = contactsInfosList.get(selectPosition);
            if (contactsInfo != null) {
                contactsInfoAdapter.setSelectedItem(selectPosition);
                contactsInfoAdapter.notifyDataSetChanged();
                CallUtils.startCallDirect(mContext, contactsInfo);
            }
        }
    }

    @Override
    public void onSelectOtherOC(String action) {
        CallUtils.logd(TAG, "onSelectOtherOC:" + action);
        if (ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM.equals(action)) {
            lvContactsList.setSelection(lvContactsList.getLastVisiblePosition());
        } else if (ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM.equals(action)) {
            int prePosition = lvContactsList.getFirstVisiblePosition() - getListVisibleCount();
            if (prePosition > 0) {
                lvContactsList.setSelection(prePosition);
            } else {
                lvContactsList.setSelection(0);
            }
        } else if (ConstantNavUc.PHONE_BACK.equals(action)) {
            finish();
        }
    }

    private void registUIControl() {

        CallUtils.logd(TAG, "registUIControl");
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

        if (voiceSearchData != null) {
            int size = voiceSearchData.size();
            for (int i = 0; i < size; i++) {
                String num = voiceSearchData.get(i).getPhoneNum();
                UIControlItem uiItem = new UIControlItem();
                uiItem.setLabel(num);
                uiItem.setIndex(i);
                String url = mFragmentHashCode + "-" + PHONE_NO_SELECT + ":" + i;
                uiItem.setUrl(url);
//                CallUtils.logd(TAG, "registUIControl:" + url);
                mUiControlItems.add(uiItem);
            }
        } else if (contactsInfosList != null && contactsInfosList.size() > 0) {
            int count = lvContactsList.getLastVisiblePosition() - lvContactsList.getFirstVisiblePosition() + 1;
            for (int i = 0; i < count; i++) {
                    UIControlItem uiItem = new UIControlItem();
                    uiItem.setLabel(contactsInfosList.get(i).getPhoneNum());
                    uiItem.setIndex(i);
                    String url = mFragmentHashCode + "-" + PHONE_NO_SELECT + ":" + i;
                    uiItem.setUrl(url);
//                    CallUtils.logd(TAG, "url:" + url);
                    mUiControlItems.add(uiItem);
            }
        }

        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();
    }

    private void finishOfVoiceSearch() {
        if(voiceSearchData != null && voiceSearchData.size() > 0) {
//            VoicePolicyManage.getInstance().record(true);
            voiceSearchData = null;
            this.finish();
        }
    }

    /**
     * @author Administrator
     */
    private class ContactsAsyncQueryHandler extends AsyncQueryHandler {

        private HashMap<Integer, ContactsInfo> contactsInfoHashMap;

        public ContactsAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            CallUtils.logd(TAG, "onQueryComplete");
            if (cursor != null && cursor.getCount() > 0) {
                contactsInfoHashMap = new HashMap<>();
                contactsInfosList = new ArrayList<ContactsInfo>();
                try {
                    cursor.moveToFirst(); // 游标移动到第一项
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        String name = cursor.getString(1);
                        String number = cursor.getString(2);
                        String sortKey = cursor.getString(3);
                        int contactId = cursor.getInt(4);
                        Long photoId = cursor.getLong(5);
                        String lookUpKey = cursor.getString(6);

                        if (contactsInfoHashMap.containsKey(contactId)) {
                            // 无操作
                        } else {
                            // 创建联系人对象
                            ContactsInfo contact = new ContactsInfo();
                            contact.setDesplayName(name);
                            contact.setPhoneNum(number);
                            contact.setSortKey(sortKey);
                            contact.setPhotoId(photoId);
                            contact.setLookUpKey(lookUpKey);
                            contactsInfosList.add(contact);
                            contactsInfoHashMap.put(contactId, contact);
                            CallUtils.logd(TAG, contact.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();  //关闭cursor，避免内存泄露
                }

            } else {
                CallUtils.logd(TAG, " there is no contacts ");
            }
            setContactsInfoAdapter(contactsInfosList, false);

            //Test Code: start loading anim and make call after 5s
//            startDialLoading();
            super.onQueryComplete(token, cookie, cursor);
        }

    }

    /* start loading anim and make call after 5s */
    private void startDialLoading() {
        stopDialLoading();
        Message msg = Message.obtain(mHandler);
        msg.what = MSG_LOADING_ANIM;
        mHandler.sendMessageDelayed(msg, 100);
    }

    private void stopDialLoading() {
        mHandler.removeMessages(MSG_LOADING_CALL_OUT);
        mHandler.removeMessages(MSG_START_CALL_OUT);
    }
    private void startLoadingAnimAndCall() {
        if (etCallFor.getText() == null || etCallFor.getText().length() == 0) {
            return;
        }

        if (lvContactsList != null && lvContactsList.getCount() > 0) {
            phoneSelectionSuggestion.setVisibility(View.VISIBLE);
            contactsInfoAdapter.startDialLoading();
            ContactsInfo contactsInfo = (ContactsInfo) contactsInfoAdapter.getItem(0);
//            CallUtils.startCall(mContext, contactsInfo);
            Message msg = Message.obtain(mHandler);
            msg.what = MSG_LOADING_CALL_OUT;
            msg.obj = contactsInfo;
            mHandler.sendMessageDelayed(msg, 8000);
            Message msg2 = Message.obtain(mHandler);
            msg2.what = MSG_START_CALL_OUT;
            msg2.obj = contactsInfo;
            mHandler.sendMessageDelayed(msg2, 8100);
        }


        if (mTtsStateChangeListener != null) {
            VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
            mTtsStateChangeListener = null;
        }
    }

    private void setContactsInfoAdapter(List<ContactsInfo> list, boolean shouldShowSelected) {
        boolean shouldShowLetter = true;
        if (etCallFor.getText() != null && etCallFor.getText().length() > 0) {
            shouldShowLetter = false;
        }
        if (shouldShowSelected) {
            contactsInfoAdapter = new ContactsInfoAdapter(mContext, list, shouldShowLetter);
            contactsInfoAdapter.setSelectedItem(0);
        } else {
            contactsInfoAdapter = new ContactsInfoAdapter(mContext, list, shouldShowLetter);
        }
        lvContactsList.setAdapter(contactsInfoAdapter);
        callPromptLayout.setVisibility(View.GONE);
        contactsListLayout.setVisibility(View.VISIBLE);

        lvContactsList.setOnScrollListener(mOnScrollListener);
        lvContactsList.post(new Runnable() {
            @Override
            public void run() {
                registUIControl();
                addHotword();
            }
        });

    }


    private void listPositionUp() {
        if (selectItem <= 0) {
            return;
        }

        // item scorll out by user, then reset to position
        if (selectItem - 1 < lvContactsList.getFirstVisiblePosition()
                || selectItem - 1 > lvContactsList.getLastVisiblePosition()) {
            selectItem = lvContactsList.getLastVisiblePosition();
            contactsInfoAdapter.setSelectedItem(selectItem);
            contactsInfoAdapter.notifyDataSetChanged();
            lvContactsList.setSelection(selectItem - getListVisibleCount() + 1);
            return;
        }

        contactsInfoAdapter.setSelectedItem(selectItem - 1);
        contactsInfoAdapter.notifyDataSetChanged();
        scorllItemUp(selectItem - 1);
        selectItem--;
    }

    private void listPositionDown() {
        if (selectItem >= lvContactsList.getCount() - 1) {
            return;
        }

        // item scorll out by user, then reset to position
        if (selectItem + 1 > lvContactsList.getLastVisiblePosition()
                || selectItem + 1 < lvContactsList.getFirstVisiblePosition()) {
            selectItem = lvContactsList.getFirstVisiblePosition();
            contactsInfoAdapter.setSelectedItem(selectItem);
            contactsInfoAdapter.notifyDataSetChanged();
            lvContactsList.setSelection(selectItem);
            return;
        }

        contactsInfoAdapter.setSelectedItem(selectItem + 1);
        contactsInfoAdapter.notifyDataSetChanged();
        scorllItemDown(selectItem + 1);
        selectItem++;
    }

    private void scorllItemDown(int position) {
        int n = getListVisibleCount() / 2;
        if (position - lvContactsList.getFirstVisiblePosition() == n + 1
                || position - lvContactsList.getFirstVisiblePosition() == n + 2) {
            lvContactsList.setSelection(lvContactsList.getFirstVisiblePosition() + 1);
        }
    }

    private void scorllItemUp(int position) {
        int n = getListVisibleCount() / 2;
        if (position - lvContactsList.getFirstVisiblePosition() == n
                || position - lvContactsList.getFirstVisiblePosition() == n - 1) {
            lvContactsList.setSelection(lvContactsList.getFirstVisiblePosition() - 1);
        }
    }

    private int getListVisibleCount() {
        int tempCount = (lvContactsList.getLastVisiblePosition())
                - lvContactsList.getFirstVisiblePosition();
        return tempCount;
    }

    private void stratCallByCar() {
        ContactsInfo contactsInfo = null;
        if (voiceSearchData != null && voiceSearchData.size() > 0) {
            contactsInfo = voiceSearchData.get(selectItem);
        } else if (contactsInfosList != null && contactsInfosList.size() > 0) {
            contactsInfo = contactsInfosList.get(selectItem);
        }
        if (contactsInfo != null) {
            stopDialLoading();
            CallUtils.startCallByPhoneNumber(mContext, contactsInfo.getPhoneNum());
            CallUtils.startInCallUI(mContext, contactsInfo.getPhoneNum(), contactsInfo.getDisplayName());
        } else {
            CallUtils.logi(TAG, "stratCallByCar contactsInfo is null");
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
                        mHandler.sendMessage(msg);
                        break;
                    case ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM:
                        msg.what = MSG_HOTWORD_NEXT_PAGE;
                        mHandler.sendMessage(msg);
                        break;
                    case ConstantNavUc.PHONE_BACK:
                    case ConstantNavUc.PHONE_BACK1:
                    case ConstantNavUc.PHONE_BACK2:
//                        stopDialLoading();
                        finish();
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
        ArielApplication.clearHotwordTopic(MODULE_NAME);
    }

    private void sendHotWordItemPosition(Message msg, int position) {
        msg.what = MSG_HOTWORD_ITEM_SELECT;
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            CallUtils.logd(TAG, "mHandler handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_START_CALL_OUT:
                    ContactsInfo contactsInfo = (ContactsInfo) msg.obj;
                    //CallUtils.startIncallUi(mContext, contactsInfo);
                    //((PhoneMainActivity)getActivity()).switchView(PhoneMainActivity.IN_CALL_UI_FRAGMENT);
                    Intent intent = new Intent(mContext, ArielPhoneService.class);
                    intent.putExtra(Constants.CONTACTS_NAME, contactsInfo.getDisplayName());
                    intent.putExtra(Constants.PHONE_NUMBER, contactsInfo.getPhoneNum());
                    mContext.startService(intent);
                    finishOfVoiceSearch();
                    break;
                case MSG_LOADING_ANIM:
                    startLoadingAnimAndCall();
                    break;
                case MSG_LOADING_CALL_OUT:
                    CallUtils.startCall(mContext, (ContactsInfo) msg.obj);
                    break;
                case MSG_START_VOICE_SEARCHING:
                    VoicePolicyManage.getInstance().record(true);
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
                default:
                    return false;
            }
            return false;
        }
    });

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                if (lvContactsList.getLastVisiblePosition() + 1 != lvContactsList.getCount()) {
                    lvContactsList.smoothScrollToPosition(lvContactsList.getFirstVisiblePosition());
                }
                registUIControl();
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
            startDialLoading();
        }

        @Override
        public void onError() {
            CallUtils.logi(TAG, "mTtsStateChangeListener onError");
            Message msg = Message.obtain(mHandler);
            msg.what = MSG_START_VOICE_SEARCHING;
            mHandler.sendMessage(msg);
            startDialLoading();
        }
    };
}
