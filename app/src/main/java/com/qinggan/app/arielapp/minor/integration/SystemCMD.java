package com.qinggan.app.arielapp.minor.integration;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.media.SoundPool;

import com.android.internal.telephony.ITelephony;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.core.SystemInterface;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.Locale;

import static com.qinggan.app.arielapp.minor.integration.MusicContacts.KEYQQMUSIC;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTALBUM;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTARTIST;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTDURATION;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTID;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTTITLE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTURI;

import android.content.Intent;

import com.qinggan.app.arielapp.ActivityLifecycleListener;

/**
 * Created by brian on 18-11-3.
 */

public class SystemCMD implements SystemInterface {

    private static final String SERVICE_MANAGER_CLASS_NAME = "android.os.ServiceManager";
    private static final String TAG = "SystemCMD";
    AudioManager mAudioManager;
    TelephonyManager mTelephonyManager;
    private Context mContext;

    public SystemCMD(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * reject call
     */
    public void rejectCall() {
        try {
            Method method = Class.forName(SERVICE_MANAGER_CLASS_NAME).getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void adjustSystemVolume(int volume) {
        if (volume > 0) {
            mAudioManager.adjustSuggestedStreamVolume(mAudioManager.ADJUST_RAISE,
                    AudioManager.USE_DEFAULT_STREAM_TYPE, 0);
        } else {
            mAudioManager.adjustSuggestedStreamVolume(mAudioManager.ADJUST_LOWER,
                    AudioManager.USE_DEFAULT_STREAM_TYPE, 0);
        }
    }

    @Override
    public void adjustSystemVolumeF(double perc) {
        int streamType = getActiveStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE);
        int maxMusicVolume = mAudioManager.getStreamMaxVolume(streamType);
        int index = (int) Math.round(maxMusicVolume * perc);
        if (index >= 0) {
            mAudioManager.setStreamVolume(streamType, index, 0);
        }

    }

    @Override
    public void adjustMusicVolume(int volume) {
        if (volume > 0) {
            mAudioManager.adjustSuggestedStreamVolume(mAudioManager.ADJUST_RAISE,
                    AudioManager.STREAM_MUSIC, 0);
        } else {
            mAudioManager.adjustSuggestedStreamVolume(mAudioManager.ADJUST_LOWER,
                    AudioManager.STREAM_MUSIC, 0);
        }
    }

    @Override
    public void adjustMusicVolumeF(double perc) {
        int maxMusicVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int index = (int) Math.round(maxMusicVolume * perc);
        if (index >= 0) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        }
    }

    @Override
    public boolean isMute(int streamType) {
        if (Build.VERSION.SDK_INT >= 23) {
            return mAudioManager.isStreamMute(streamType);
        }
        return false;
    }

    public int getActiveStreamType(int type) {
        switch (type) {
            case AudioManager.USE_DEFAULT_STREAM_TYPE:
                if (TelephonyManager.CALL_STATE_OFFHOOK == mTelephonyManager.getCallState()
                        || TelephonyManager.CALL_STATE_RINGING == mTelephonyManager.getCallState()) {
                    return AudioManager.STREAM_VOICE_CALL;

                } else if (mAudioManager.isMusicActive()) {
                    return AudioManager.STREAM_MUSIC;
                    //}else if(){//ararm ,access,IVOKA,etc.
                } else {
                    return AudioManager.STREAM_SYSTEM;
                }
            default:
                return AudioManager.STREAM_SYSTEM;
        }
    }

    @Override
    public void setAllMute() {
        int[] mStreams = {mAudioManager.STREAM_VOICE_CALL,
                mAudioManager.STREAM_SYSTEM,
                mAudioManager.STREAM_RING,
                mAudioManager.STREAM_MUSIC,
                mAudioManager.STREAM_ALARM,
                mAudioManager.STREAM_NOTIFICATION};
        for (int stream : mStreams) {
            mAudioManager.adjustStreamVolume(stream, mAudioManager.ADJUST_TOGGLE_MUTE, 0);
        }
    }

    @Override
    public void setMusicMute() {
        mAudioManager.adjustStreamVolume(mAudioManager.STREAM_MUSIC, mAudioManager.ADJUST_TOGGLE_MUTE, 0);
    }

    @Override
    public void setSpeStreamMute(int streamType) {
        mAudioManager.adjustStreamVolume(streamType, mAudioManager.ADJUST_TOGGLE_MUTE, 0);
    }

    @Override
    public boolean judgeIsAM() {
        Calendar mCalendar = Calendar.getInstance();
        int apm = mCalendar.get(Calendar.AM_PM);
        if (apm == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSomeAppInstalled(String packageName, Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<String> getLocalSongList(int listType) {
        ArrayList<String> mList = new ArrayList<String>();
        ArrayList<String> mIdList = new ArrayList<String>();
        ArrayList<String> mTitleList = new ArrayList<String>();
        ArrayList<String> mAlbumList = new ArrayList<String>();
        ArrayList<String> mArtistList = new ArrayList<String>();
        ArrayList<String> mDurationList = new ArrayList<String>();
        int permissionGain = ArielApplication.getApp().getApplicationContext().checkCallingOrSelfPermission(
                "android.permission.READ_EXTERNAL_STORAGE");
        if(permissionGain != PackageManager.PERMISSION_GRANTED){
            return null;
        }
        Cursor cursor = mContext.getApplicationContext().getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                        null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                if (url.indexOf(KEYQQMUSIC) != -1) {
                    mList.add(url);
                    mIdList.add(id);
                    mTitleList.add(title);
                    mAlbumList.add(album);
                    mArtistList.add(artist);
                    mDurationList.add(duration);
                }
            }
            cursor.close();
            cursor = null;
        }
        switch (listType) {
            case LOCALLISTURI:
                return mList;
            case LOCALLISTID:
                return mIdList;
            case LOCALLISTTITLE:
                return mTitleList;
            case LOCALLISTALBUM:
                return mAlbumList;
            case LOCALLISTARTIST:
                return mArtistList;
            case LOCALLISTDURATION:
                return mDurationList;
        }
        return null;
    }

    @Override
    public void backToHomeActivity() {
        Log.i("Brian_back_home", "backToHomeActivity");
        if (!ActivityLifecycleListener.mIsForgroud) {
            Log.i("Brian_back_home", "back to recent use activity");
            Intent intent = new Intent();
            intent.setPackage("com.qinggan.app.arielapp");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setComponent(ActivityLifecycleListener.lastActivity.getComponentName());
            mContext.startActivity(intent);
        }
    }

    @Override
    public boolean isSomeAppOnFront(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

                Log.i("Test_am", packageName + "is on front.");
                return true;// 程序运行在前台
            }
        }
        return false;
    }

    @Override
    public void playWechatSuccessSendSound(int soundRsId) {
        final SoundPool wechatSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        final int wechatSoundId = wechatSoundPool.load(mContext, soundRsId, 1);

        wechatSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.i(TAG, "playWakeupSound onLoadComplete");
                wechatSoundPool.play(wechatSoundId, 1.0F, 1.0F, 0, 0, 1.0F);
            }
        });
    }
}
