package com.qinggan.app.arielapp.minor.phone;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.phone.bean.CallRecord;
import com.qinggan.app.arielapp.minor.phone.bean.EventBusCallLog;
import com.qinggan.app.arielapp.minor.phone.bean.EventBusCardInfo;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pateo on 18-11-28.
 */

public class CallLogManager {


    private static final String TAG = CallLogManager.class.getSimpleName();

    private static CallLogManager instance;

    private static final int MSG_SET_CALL_LOG_LIST = 1;

    private Context mContext;
    private CallLogAsyncQueryHandler asyncQueryHandler;
    boolean isCallLogLoaded = false;
    boolean isFavourContactsLoaded = false;

    private List<CallRecord> allCallLogList;
    private List<CallRecord> saveCallLogList;
    private List<CallRecord> recentCallLogList;
    private List<CallRecord> favourCallLogList;
    List<String> favourContactsNumList;
    private Map<String, CallRecord> allCallLogMap;

    private CallLogManager(Context mContext) {
        this.mContext = mContext;
    }

    public void syncCallLog() {

        if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
            updatePhoneCardInfo(false);
            return;
        }
        loadCallData();
        loadFavourContacts();
    }

    private void loadFavourContacts() {
        isFavourContactsLoaded = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                favourContactsNumList = CallUtils.getKeepedContacts(mContext);
                isFavourContactsLoaded = true;
                if (isCallLogLoaded) {
                    Message msg = Message.obtain(mHandler);
                    msg.what = MSG_SET_CALL_LOG_LIST;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void loadCallData() {
        isCallLogLoaded = false;
        // 实例化
        asyncQueryHandler = new CallLogAsyncQueryHandler(mContext.getContentResolver());
        Uri uri = CallLog.Calls.CONTENT_URI; // call log Uri；
        // 查询的字段
        String[] projection = {
                CallLog.Calls.CACHED_FORMATTED_NUMBER,
                CallLog.Calls.CACHED_MATCHED_NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.GEOCODED_LOCATION,
                CallLog.Calls.NUMBER,
        };
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "date DESC");
    }

    /**
     * @author Administrator
     */
    private class CallLogAsyncQueryHandler extends AsyncQueryHandler {

        /*private HashMap<Integer, CallRecord> callRecordIdMap;
        private ArrayList<CallRecord> list;*/

        public CallLogAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            CallUtils.logd(TAG, "onQueryComplete");

            if (cursor != null) {
                CallUtils.logd(TAG, "cursor length is " + cursor.getCount());
                //callRecordIdMap = new HashMap<Integer, CallRecord>();
                allCallLogList = new ArrayList<>();
                saveCallLogList = new ArrayList<>();
                allCallLogMap = new HashMap<>();
                recentCallLogList = new ArrayList<>();
                favourCallLogList = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {

                        CallRecord record = new CallRecord();
                        record.setFormatted_number(cursor.getString(0));
                        record.setMatched_number(cursor.getString(1));
                        record.setName(cursor.getString(2));
                        record.setType(cursor.getString(3));
                        record.setDate(cursor.getLong(4));
                        record.setDuration(cursor.getLong(5));
                        record.setLocation(cursor.getString(6));
                        record.setNumber(cursor.getString(7));
//                        if (cursor.getPosition() % 10 == 0) {
//                            CallUtils.logd(TAG, "before add:" + record.toString());
//                        }
                        allCallLogList.add(record);
                        /*if (!list.contains(record)) {
                            list.add(record);
                            CallUtils.logd(TAG, record.toString());
                        } else {
                            record = null;
                        }*/
                    }
                    isCallLogLoaded = true;
                    if (allCallLogList.size() > 0 && isFavourContactsLoaded) {
                        Message msg = Message.obtain(mHandler);
                        msg.what = MSG_SET_CALL_LOG_LIST;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();  //关闭cursor，避免内存泄露
                }
            } else {
                CallUtils.logd(TAG, " call log is null ");
            }

            super.onQueryComplete(token, cookie, cursor);
        }

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            CallUtils.logd(TAG, "mHandler handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_SET_CALL_LOG_LIST:
                    for (CallRecord callRecord : allCallLogList) {
                        CallRecord temp = allCallLogMap.get(callRecord.getNumber());
                        int type = Integer.valueOf(callRecord.getType());
                        if (temp == null) {
                            if (allCallLogMap.size() >= Constants.MAX_RECENT_CALL_LOG_NUM) {
                                continue;
                            }
                            allCallLogMap.put(callRecord.getNumber(), callRecord);
                            saveCallLogList.add(callRecord);
                            callRecord.setCount(callRecord.getCount() + 1);
                            if (type == CallLog.Calls.MISSED_TYPE) {
                                callRecord.setMissCount(callRecord.getMissCount() + 1);
                            }
                            if (favourContactsNumList.contains(callRecord.getNumber())) {
                                if (!favourCallLogList.contains(callRecord)) {
                                    favourCallLogList.add(callRecord);
                                }
                            } else {
                                if (!recentCallLogList.contains(callRecord)) {
                                    recentCallLogList.add(callRecord);
                                }
                            }
                        } else {
                            //计算未接次数
                            int tempType = Integer.valueOf(temp.getType());

                            if ((tempType == CallLog.Calls.MISSED_TYPE && type == CallLog.Calls.MISSED_TYPE)
                                    && temp.getMissCount() == temp.getCount()) {
                                temp.setMissCount(temp.getMissCount() + 1);
                            }
                            temp.setCount(temp.getCount() + 1);
                            if ((temp.getName() == null && "".equals(temp.getName())) && callRecord.getName() == null) {
                                temp.setName(callRecord.getName());
                            }
                        }

                    }
                    updatePhoneCardInfo(true);

                    releaseCallLogEvent();

                    break;
                default:
                    return false;
            }
            return false;
        }
    });

    private void updatePhoneCardInfo(boolean hasPermission) {
        CallUtils.logd(TAG, "updatePhoneCardInfo hasPermission:" + hasPermission);
        boolean needInsert = false;
        IntegrationCore integrationCore = IntegrationCore.getIntergrationCore(mContext);
        List<CardInfo> cardInfos = (List<CardInfo>) (List) integrationCore.searchDbData(mContext, CardInfo.class.getName());
        CardInfo phoneCardInfo = null;
        if(cardInfos == null) {
            //return and wait init
            return;
        }
        for (CardInfo cardInfo : cardInfos) {
            if (CardController.CARD_TYPE_PHONE.equals(cardInfo.getCardId())) {
                phoneCardInfo = cardInfo;
                break;
            }
        }
        if (phoneCardInfo == null) {
            needInsert = true;
            phoneCardInfo = new CardInfo();
            phoneCardInfo.setCardId(CardController.CARD_TYPE_PHONE);
            phoneCardInfo.setTitle(mContext.getString(R.string.title_phone));
        }

        if (!hasPermission) {
            phoneCardInfo.setType(CardController.CARD_PHONE_UNAUTHORIZATION + "");
            phoneCardInfo.setSubTitle("");
            phoneCardInfo.setContent(mContext.getString(R.string.content_authorization));
            phoneCardInfo.setMessage("");
            phoneCardInfo.setNumber(0);

        } else {

            CallRecord targetCallRecord = null;
            boolean isMissCalllog = false;
            if (saveCallLogList != null && saveCallLogList.size() > 0) {
                for (CallRecord callRecord : saveCallLogList) {
                    CallUtils.logd(TAG, "saveCallLogList.callRecord：" + callRecord);
                    if (CallLog.Calls.MISSED_TYPE == Integer.valueOf(callRecord.getType())) {
                        targetCallRecord = callRecord;
                        isMissCalllog = true;
                        break;
                    }
                }
                if (targetCallRecord == null) {
                    targetCallRecord = saveCallLogList.get(0);
                }
                CallUtils.logd(TAG, "targetCallRecord：" + targetCallRecord);
                if (isMissCalllog) {
                    phoneCardInfo.setType(CardController.CARD_PHONE_MISSED_CALL + "");
                    phoneCardInfo.setSubTitle(mContext.getString(R.string.subtitle_missed_call));
                    if (targetCallRecord.getName() == null || targetCallRecord.getName().equals("")) {
                        phoneCardInfo.setContent(targetCallRecord.getNumber());
                    } else {
                        phoneCardInfo.setContent(targetCallRecord.getName());
                    }
                    phoneCardInfo.setNumber(targetCallRecord.getMissCount());
                } else {
                    phoneCardInfo.setType(CardController.CARD_PHONE_LAST_CALL + "");
                    phoneCardInfo.setSubTitle(mContext.getString(R.string.subtitle_last_call));
                    if (targetCallRecord.getName() == null || targetCallRecord.getName().equals("")) {
                        phoneCardInfo.setContent(targetCallRecord.getNumber());
                    } else {
                        phoneCardInfo.setContent(targetCallRecord.getName());
                    }
                    phoneCardInfo.setNumber(0);
                }
                phoneCardInfo.setMessage(targetCallRecord.getNumber());
            } else if (favourContactsNumList != null && favourContactsNumList.size() > 0) {
                String phoneNum = favourContactsNumList.get(0);
                String contactsName = CallUtils.getContactNameByPhoneNumber(mContext, phoneNum);

                CallUtils.logd(TAG, "favourContactsNumList phoneNum：" + phoneNum);
                phoneCardInfo.setType(CardController.CARD_PHONE_COLLECTION + "");
                phoneCardInfo.setSubTitle(mContext.getString(R.string.subtitle_collection_contact));
                if (contactsName == null || contactsName.equals("")) {
                    phoneCardInfo.setContent(phoneNum);
                } else {
                    phoneCardInfo.setContent(contactsName);
                }
                phoneCardInfo.setMessage(phoneNum);
                phoneCardInfo.setNumber(0);

            } else {
                phoneCardInfo.setType(CardController.CARD_PHONE_SEARCH + "");
                phoneCardInfo.setSubTitle(mContext.getString(R.string.subtitle_search));
                phoneCardInfo.setContent(mContext.getString(R.string.content_call_to_somebody));
                phoneCardInfo.setMessage("");
                phoneCardInfo.setNumber(0);
            }
        }
        if (needInsert) {
            integrationCore.cardInfoUpdate(IntegrationCore.CMD_DATABASE_CARD_MANAGER, IntegrationCore.CMD_DATABASE_CARD_INSERT, phoneCardInfo,
                    mContext, CardInfo.class.getName());
        } else {
            integrationCore.cardInfoUpdate(IntegrationCore.CMD_DATABASE_CARD_MANAGER, IntegrationCore.CMD_DATABASE_CARD_UPDATE, phoneCardInfo,
                    mContext, CardInfo.class.getName());
        }

        releaseCardInfoEvent(phoneCardInfo);
    }

    private void releaseCallLogEvent() {
        EventBusCallLog eventBusCallLog = new EventBusCallLog();
        eventBusCallLog.setFavourCallLogList(favourCallLogList);
        eventBusCallLog.setRecentCallLogList(recentCallLogList);
        EventBus.getDefault().post(eventBusCallLog);
    }

    private void releaseCardInfoEvent(CardInfo phoneCardInfo) {
        EventBusCardInfo eventBusCardInfo = new EventBusCardInfo();
        eventBusCardInfo.setCardInfo(phoneCardInfo);
        EventBus.getDefault().post(eventBusCardInfo);
    }

    public static CallLogManager getInstance(Context context) {
        if (instance == null) {
            instance = new CallLogManager(context);
        }
        return instance;
    }

    public List<CallRecord> getRecentCallLogList() {
        return recentCallLogList;
    }

    public List<CallRecord> getFavourCallLogList() {
        return favourCallLogList;
    }

}
