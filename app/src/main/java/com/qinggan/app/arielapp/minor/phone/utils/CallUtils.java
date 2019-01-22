package com.qinggan.app.arielapp.minor.phone.utils;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.phone.Constants;
import com.qinggan.app.arielapp.minor.phone.bean.ContactsInfo;
import com.qinggan.app.arielapp.minor.phone.service.ArielPhoneService;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;

/**
 * Created by pateo on 18-10-30.
 */

public class CallUtils {

    private static final boolean DEBUG_ON = true;
    private static final String TAG = "CallUtils";
    public static final String ANDROID_OS_SERVICE_MANAGER = "android.os.ServiceManager";
    public static final String METHOD_GET_SERVICE = "getService";
    private static final String MANUFACTURER_HTC = "HTC";
    private static final String[] EMERGENCY_NNUMBERS = {"112","911","000","08","110","118","120","119","122"};

    public static void startCallDirect(Context context, ContactsInfo contactsInfo) {
        startCall(context, contactsInfo);
        startInCallUI(context, contactsInfo.getPhoneNum(), contactsInfo.getDisplayName());
    }

    public static void startCall(Context context, ContactsInfo contactsInfo) {
        startCallByPhoneNumber(context, contactsInfo.getPhoneNum());
    }

    public static void startCallByPhoneNumber(Context context, String phoneNum) {
        if (!isSimReady(context)) {
            ToastUtil.show(context.getString(R.string.phone_sim_error), context);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            logd(TAG, "call permission denied ");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PhoneAccountHandle phoneAccountHandle = checkAndAddSIMID(context);
            if (phoneAccountHandle != null) {
                logd(TAG, "if two SIM cards or getDefaultOutgoingPhoneAccount, make call from SIM1/Default");
                intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
            }
        }
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startInCallUI(Context context, String phoneNum, String phoneName) {
        if (!isSimReady(context) || isEmergencyNumber(phoneNum)) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            logd(TAG, "call permission denied ");
            return;
        }
        Intent intent = new Intent(context, ArielPhoneService.class);
        intent.putExtra(Constants.CONTACTS_NAME, phoneName);
        intent.putExtra(Constants.PHONE_NUMBER, phoneNum);
        context.startService(intent);
    }

    /**
     * 接听电话
     */
    public static void answerCall(Context context) {
        if (!StageController.getStageController().getIsDuringCall()) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CallUtils.acceptCallGEO(context);
            } else {
                CallUtils.acceptCall(context);
            }
        } catch (Exception e) {
            Log.e(TAG, "answerCall exception");
        }

    }
    /**
     * 接听电话，8.0以上版本
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void acceptCallGEO(Context context) {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        tm.acceptRingingCall();
    }

    /**
     * 接听电话
     */
    public static void acceptCall(Context context) {
        try {
            ITelephony telephony = getITelephony();
            telephony.answerRingingCall();
        } catch (Exception e) {
            Log.e(TAG, "for version 4.1 or larger");
            acceptCall_4_1(context);
        }
    }

    /**
     * 4.1版本以上接听电话
     */
    private static void acceptCall_4_1(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //模拟无线耳机的按键来接听电话
        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER)
                && !audioManager.isWiredHeadsetOn();
        if (broadcastConnected) {
            broadcastHeadsetConnected(context, false);
        }
        try {
            try {
                logd(TAG, "Runtime.exec(KEYCODE_HEADSETHOOK) ");
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
            } catch (Exception e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                logd(TAG, "Runtime.exec(String) had an problem ");
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                context.sendOrderedBroadcast(btnDown, enforcedPerm);
                context.sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(context, false);
            }
        }
    }

    private static void broadcastHeadsetConnected(Context context, boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            context.sendOrderedBroadcast(i, null);
        } catch (Exception e) {
        }
    }

    //挂断
    public static void endPhone(Context context, TelephonyManager tm) {
        try {
            ITelephony telephony = getITelephony();
            telephony.endCall();
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "", e);
        } catch (Exception e) {
        }
    }

    /**
     * 挂断电话
     */
    public static void rejectCall() {
        if (!StageController.getStageController().getIsDuringCall()) {
            return;
        }
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "", e);
        } catch (Exception e) {
        }
    }

    @Nullable
    private static ITelephony getITelephony() throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        Method method = Class.forName(ANDROID_OS_SERVICE_MANAGER).getMethod(METHOD_GET_SERVICE, String.class);
        IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
        return ITelephony.Stub.asInterface(binder);
    }

    public static void silenceRinger() {
        logd(TAG, "silenceRinger");
        try {
            ITelephony telephony = getITelephony();
            telephony.silenceRinger();
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "", e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "", e);
            e.printStackTrace();
        }
    }

    public static void setSilent(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (checkNotificationPolicyAccessPermission(context)) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }


    public static void setSpeakerOnOff(Context context) {
        logd(TAG, "setSpeakerOnOff");
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            logd(TAG, "setSpeakerOnOff:" + !audioManager.isSpeakerphoneOn());
            //audioManager.setSpeakerphoneOn(!audioManager.isSpeakerphoneOn());
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSpeekModle(Context context, boolean isOpen) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.ROUTE_SPEAKER);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        //5.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }

        if (!audioManager.isSpeakerphoneOn() && isOpen) {
            logd(TAG, "setSpeakerOn true");
            audioManager.setSpeakerphoneOn(true);//开启免提
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        } else if (audioManager.isSpeakerphoneOn() && isOpen) {
            logd(TAG, "setSpeakerOn false");
            audioManager.setSpeakerphoneOn(false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                    AudioManager.STREAM_VOICE_CALL);
        }
    }

    public static boolean isMicrophoneMute(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isMute = audioManager.isMicrophoneMute();
        return isMute;
    }

    public static void switchMicrophoneMute(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isMute = audioManager.isMicrophoneMute();
        logd(TAG, "switchMicrophoneMute isMute:" + isMute);
        audioManager.setMicrophoneMute(!isMute);
    }

    public static void setMicrophoneMute(Context context, boolean isMute) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(isMute);
    }

    /**
     * get phone factory property
     *
     * @param key
     * @return
     */
    public static String getPhoneProperty(String key) {
        Field[] fields = Build.class.getFields();
        String value = "";
        for (Field f : fields) {
            try {
                String name = f.getName();
                if ((key.toUpperCase()).equals(name.toUpperCase())) {
                    value = f.get(key).toString();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    private static boolean checkNotificationPolicyAccessPermission(Context context) {
        boolean hasPermission = false;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            CallUtils.logd(TAG, "set ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS");
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            context.startActivity(intent);
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }

    /**
     * 获得收藏夹的联系人号码
     */
    public static List<String> getKeepedContacts(Context context) {
        List<String> favourContactsList = new ArrayList<>();
        Cursor cur = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.STARRED + "=1", null, null);
        //startManagingCursor(cur);
        int num = cur.getCount();
        CallUtils.logd(TAG, "getKeepedContacts num: " + num);

        try {
            while (cur.moveToNext()) {

                long id = cur.getLong(cur.getColumnIndex("_id"));
                Cursor pcur = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                                + Long.toString(id), null, null);

                try {
                    //处理多个号码的情况
                    String phoneNumbers = "";
                    while (pcur.moveToNext()) {
                        String strPhoneNumber = pcur.getString(pcur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //phoneNumbers += strPhoneNumber + ":";
                        Log.d(TAG, "favourContactsNum: "+strPhoneNumber);
                        favourContactsList.add(strPhoneNumber.replaceAll(" ",""));
                    }
                    //phoneNumbers += "\n";
                } catch (Exception e) {

                } finally {
                    pcur.close();
                }

            }
        } catch (Exception e) {
            CallUtils.loge(TAG, e.getMessage());
        } finally {
            cur.close();
        }
        return favourContactsList;
    }

    /**
     * 根据电话号码取得联系人姓名
     */
    public static String getContactNameByPhoneNumber(Context context, String number) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER };

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projection,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"+ number + "'", null, null);

        if (cursor == null) {
            Log.d(TAG, "getPeople null");
            return null;
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            // 取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            return name;
        }
        return null;
    }

    public static String getCallType(int anInt) {
        switch (anInt) {
            case CallLog.Calls.INCOMING_TYPE:
                return "呼入";
            case CallLog.Calls.OUTGOING_TYPE:
                return "呼出";
            case CallLog.Calls.MISSED_TYPE:
                return "未接";
            default:
                break;
        }
        return null;
    }


    public static String formatDate(long time) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        return format.format(new Date(time));
    }

    public static String formatDuration(long time) {
        long s = time % 60;
        long m = time / 60;
        long h = time / 60 / 60;
        StringBuilder sb = new StringBuilder();
        if (h > 0) {
            sb.append(h).append("小时");
        }
        if (m > 0) {
            sb.append(m).append("分");
        }
        sb.append(s).append("秒");
        return sb.toString();
    }

    public static boolean isSimReady(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = manager.getSimState();
        CallUtils.logd(TAG, "isSimReady simState:" + simState);
        if (simState == TelephonyManager.SIM_STATE_READY) {
            return true;
        }
        return false;
    }

    /**
     * check if two sim cards
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static PhoneAccountHandle checkAndAddSIMID(Context context) {
        PhoneAccountHandle phoneAccountHandle = null;
        try {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            if (telecomManager != null) {
                phoneAccountHandle = telecomManager.getDefaultOutgoingPhoneAccount("tel");
                if (phoneAccountHandle != null) {
                    CallUtils.logi(TAG, "checkAndAddSIMID: getDefaultOutgoingPhoneAccount is not null");
                    return phoneAccountHandle;
                }
                List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
                if (phoneAccountHandleList.size() >= 2) {
                    phoneAccountHandle = phoneAccountHandleList.get(0);
                    CallUtils.logi(TAG, "checkAndAddSIMID: set phoneAccountHandle");
                }
            }
        } catch (Exception e) {
            CallUtils.loge(TAG, "checkAndAddSIMID Exception");
        }
        return phoneAccountHandle;
    }

    public static boolean isEmergencyNumber(String number) {
        for(int i = 0; i < EMERGENCY_NNUMBERS.length;  i++) {
            if (EMERGENCY_NNUMBERS[i].equals(number)) {
                CallUtils.logd(TAG, "isEmergencyNumber true");
                return true;
            }
        }
        return false;
    }

    public static void logd(String tag, String msg) {
        if (DEBUG_ON) {
            Log.d(tag, msg);
        }
    }

    public static void loge(String tag, String msg) {
        if (DEBUG_ON) {
            Log.e(tag, msg);
        }
    }

    public static void logi(String tag, String msg) {
        if (DEBUG_ON) {
            Log.i(tag, msg);
        }
    }

    public static void logw(String tag, String msg) {
        if (DEBUG_ON) {
            Log.w(tag, msg);
        }
    }


}
