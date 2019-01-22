package com.qinggan.app.arielapp.minor.integration;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.core.MusicInterface;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.utils.ArielLog;
import com.qinggan.app.arielapp.minor.utils.ShardPreUtils;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.tencent.qqmusic.third.api.contract.CommonCmd;
import com.tencent.qqmusic.third.api.contract.Data;
import com.tencent.qqmusic.third.api.contract.IQQMusicApi;
import com.tencent.qqmusic.third.api.contract.IQQMusicApiCallback;
import com.tencent.qqmusic.third.api.contract.IQQMusicApiEventListener;
import com.tencent.qqmusic.third.api.contract.Keys;

import java.util.ArrayList;

import static com.qinggan.app.arielapp.minor.integration.MusicContacts.*;
import static com.tencent.qqmusic.third.api.contract.Data.FolderType.MY_FOLDER;
import static com.tencent.qqmusic.third.api.contract.Data.FolderType.MY_FOLDER_SONG_LIST;
import static com.tencent.qqmusic.third.api.contract.Data.FolderType.RANK;
import static com.tencent.qqmusic.third.api.contract.Data.FolderType.RECOMMEND_FOLDER;
import static com.tencent.qqmusic.third.api.contract.Events.API_EVENT_PLAY_LIST_CHANGED;
import static com.tencent.qqmusic.third.api.contract.Events.API_EVENT_PLAY_MODE_CHANGED;
import static com.tencent.qqmusic.third.api.contract.Events.API_EVENT_PLAY_SONG_CHANGED;
import static com.tencent.qqmusic.third.api.contract.Events.API_EVENT_PLAY_STATE_CHANGED;
import static com.tencent.qqmusic.third.api.contract.Events.API_EVENT_SONG_FAVORITE_STATE_CHANGED;

/**
 * Created by brian on 18-11-9.
 */

public class QQMusicCMD implements MusicInterface {
    private Context mContext;
    private Context ApplicationContext;
    public volatile IQQMusicApi mQQMusicAPI;
    private IQQMusicApiCallbackImpl mQQMusicCallback;
    private IQQMusicApiEventListenerImpl mIQQMusicApiEventListenerImpl;
    private volatile String type;
    boolean resStatus = false;
    private volatile boolean preFlag = false;
    int version = NOMALSUCCESSCODE;
    AudioManager mAudioManager;
    int cMusicVolume, maxMusicVolume, volumePerc = NOMALSUCCESSCODE;
    public boolean isMusicConnected, isMusicConnChanged, playStatus = false;
    private volatile ArrayList<Data.Song> playList, searchResoultList;
    private volatile ArrayList<String> searchResoultIDList;
    private volatile ArrayList<Data.FolderInfo> recommendFolderCacheList,
            rankFolderCacheList, myFolderCacheList;
    private volatile ArrayList<String> playIDList = new ArrayList<String>();
    private volatile int typeFolder = 0;
    private ArrayList<IntegrationCore.MusicListener> mListeners = new ArrayList<IntegrationCore.MusicListener>();
    private Data.Song lastSong, oldSong;
    private ArrayList<Data.Song> mSongList = new ArrayList<Data.Song>();
    private volatile ArrayList<String> oldPlayIDList = new ArrayList<String>();
    private boolean shouldSaveOldList = false,shouldBind = false;
    private boolean DEBUG = ArielLog.DEBUG_MUSIC;
    String opsType, opsKey;
    private Bundle searchBundle = new Bundle();
    private boolean getKonwFlag = false;
    private String knownSongName, knowFolderType;
    private Data.FolderInfo knownLikeFolder, knownTopFolder, knownNewFolder;
    private int autoIndex = 0;
    private ArrayList autoSaveList = new ArrayList();
    private long permissionHinNowtime = 0L,permissionHinOldtime = 0L;
    private int i = 0, j = 0, k = 0, oldState = -1;
    private long oldTime = 0L;
    private Data.FolderInfo newMusicFolder;
    private ArrayList<String> newSongIdList = new ArrayList<String>();
    private ArrayList<OnQQServiceStatusListener> mConnectedListenerList;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGEGETRANK:
                    getFolderList("", RANK, 0);
                    break;
                case MESSAGEGETRANKNEWMUSIC:
                    typeFolder = TYPELISTNEWMUSIC;
                    getSongList(newMusicFolder.getId(),newMusicFolder.getType(),0);
                    break;
                case MEDDAGEOFFLINESEARCH:
                    search(opsKey, 0, true);
                    break;
                case MESSAGEGETLOVEMUSIC:
                    knowFolderType = LIVE_MUSIC;
                    if (knownLikeFolder != null)
                        getSongList(knownLikeFolder.getId(), knownLikeFolder.getType(), 0);
                    break;
                case MESSAGEGETTOPMUSIC:
                    knowFolderType = TOP_MUSIC;
                    if (knownTopFolder != null)
                        getSongList(knownTopFolder.getId(), knownTopFolder.getType(), 0);
                    break;
                case MESSAGEGETNEWMUSIC:
                    knowFolderType = NEW_MUSIC;
                    if (knownNewFolder != null)
                        getSongList(knownNewFolder.getId(), knownNewFolder.getType(), 0);
                    break;
                case MESSAGE_PLAY_NEXT_SONG:
                    Bundle vipNextSong = msg.getData();
                    int index = vipNextSong.getInt("index");
                    ArrayList list = vipNextSong.getStringArrayList("list");
                    playSongIdAtIndex(list,index);
                    break;
                case MESSAGE_FIRST_PLAY_NEW:
                    shouldBind = true;
                    Bundle playNewSong = msg.getData();
                    ArrayList listNew =  playNewSong.getStringArrayList("list");
                    playSongId(listNew);
                    break;
                case MESSAGE_SEND_LIST:
                    //bindQQMusicService();
                    Message msg2 = new Message();
                    msg2.what = MESSAGE_FIRST_PLAY_NEW;
                    Bundle playNewSong2 = new Bundle();
                    playNewSong2.putStringArrayList("list",newSongIdList);
                    msg2.setData(playNewSong2);
                    mHandler.sendMessageDelayed(msg2,500);
                    break;
                case MESSAGE_PLAY_OFFLINE_SEARCH:
                    IntegrationCore.getIntergrationCore(ArielApplication.getApp().getApplicationContext()).
                            playSongIdAtIndex(searchResoultIDList, 0);
                    if (opsType != null && opsType.equals("search")
                            && opsKey != null && !opsKey.equals(EMPTYSTRINGVALUE)) {
                        opsType = opsKey = null;
                        preFlag =false;
                    }
                    break;
            }
        }
    };

    private AudioPolicyManager mAudioPolicyManager;

    private AudioPolicyManager.OnAudioPolicyListener mAudioPolicyListener =
            new AudioPolicyManager.OnAudioPolicyListener() {
        private int status;
        @Override
        public boolean onPause() {
            status = getPlaybackState();
            k = 0;
            pauseMusic();
            return false;
        }

        @Override
        public boolean onResume() {
            if(status == PLAYSTARTED) {
                k = 0;
                resumeMusic();
            }
            return false;
        }

        @Override
        public boolean onStop() {
            k = 0;
            stopMusic();
            return false;
        }
    };

    public QQMusicCMD(Context context) {
        mContext = context;
        ApplicationContext = context.getApplicationContext();
        mQQMusicCallback = new IQQMusicApiCallbackImpl();
        mIQQMusicApiEventListenerImpl = new IQQMusicApiEventListenerImpl();
        if (mQQMusicAPI == null) {
            bindQQMusicService();
        }
        setQQMusicCallback(mQQMusicCallback);
        setQQMusicEventListener(mIQQMusicApiEventListenerImpl);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        mAudioPolicyManager = AudioPolicyManager.getInstance();

        mConnectedListenerList = new ArrayList<>();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mQQMusicAPI = IQQMusicApi.Stub.asInterface(iBinder);
            registerEventListener(list);
            if (!"vivo".equals(getVendor())) {
                if (opsType != null && opsType.equals("search")
                        && opsKey != null && !opsKey.equals(EMPTYSTRINGVALUE)) {
                    mHandler.removeMessages(MEDDAGEOFFLINESEARCH);
                    mHandler.sendEmptyMessage(MEDDAGEOFFLINESEARCH);
                }
            }
            isMusicConnected = true;
            playStatus = false;
            Log.d("AudioPolicyManager","music onServiceConnected");
            if(null != mQQMusicAPI){
                int state = getPlaybackState();
                Log.d("AudioPolicyManager","music onServiceConnected = " + state);
                if(PLAYSTARTED == state){
                    checkAudioPolicy();
                }

                for (OnQQServiceStatusListener listener : mConnectedListenerList) {
                    listener.onQQServiceConnected();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            unregisterEventListener(list);
            mQQMusicAPI = null;
            isMusicConnected = false;
            if (searchResoultList != null) searchResoultList.clear();
            if (searchResoultIDList != null) searchResoultIDList.clear();
        }
    };

    public interface OnQQServiceStatusListener{
        void onQQServiceConnected();
    }

    public static final Object mQQServiceLock = new Object();

    @Override
    public void addQQServiceStatusListener(OnQQServiceStatusListener listener){
        if (listener == null) {
            return;
        }
        synchronized (mQQServiceLock) {
            if (!mConnectedListenerList.contains(listener)){
                mConnectedListenerList.add(listener);
            }
        }
    }

    public synchronized void setQQMusicCallback(IQQMusicApiCallbackImpl callback) {
        mQQMusicCallback = callback;
    }

    public String getVendor() {
        String[] vendorInfo = Build.FINGERPRINT.split("/");
        if (vendorInfo != null) {
            return vendorInfo[0];
        }
        return null;
    }

    public synchronized void setQQMusicEventListener(IQQMusicApiEventListenerImpl eventListener) {
        mIQQMusicApiEventListenerImpl = eventListener;
    }

    @Override
    public void searchMusicByKey(String key) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(), " searchMusicByKey");
        if (!isMusicConnected) {
            return;
        }
        type = OPSTYPESEARCHKEY;
        Bundle params = new Bundle();
        params.putString(PARAMSKEYWORD, key);
        params.putInt(PARAMSSEARCHTYPE, 0);
        params.putBoolean(PARAMSFIRSTPAGE, true);
        try {
            if (DEBUG)
                android.util.Log.i(QQMusicCMD.class.getSimpleName(), " searchMusicByKey key is : " + key);
            mQQMusicAPI.executeAsync(SEARCHCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPlaybackState() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getPlaybackState");
        if (!isMusicConnected) {
            return NOMALERRORCODE;
        }
        return getValueCodeInt(GETPLAYBACKSTATECMD);
    }

    @Override
    public Data.Song getCurrentSong() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getCurrentSong isMusicConnected is : " + isMusicConnected);
        if (!isMusicConnected) {
            return null;
        }
        try {
            Gson gson = new Gson();
            Data.Song song = gson.fromJson(getValueCodeOb(GETCURRENTSONGCMD), Data.Song.class);
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " getCurrentSong song try is : " + song);
            return song;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getCurrTime() {
        //if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
         //       " getCurrTime");
        if (!isMusicConnected) {
            return NOMALLONGERRORCODE;
        }
        return getValueCodeFlo(GETCURRENTTIMECMD);
    }

    @Override
    public int getPlayMode() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getPlayMode");
        if (!isMusicConnected) {
            return NOMALERRORCODE;
        }
        return getValueCodeInt(GETPLAYMODECMD);
    }

    @Override
    public long getTotalTime() {
        //if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
          //      " getTotalTime");
        if (!isMusicConnected) {
            return NOMALLONGERRORCODE;
        }
        return getValueCodeFlo(GETTOTALTIMECMD);
    }

    @Override
    public int getVersion() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getVersion");
        if (!isMusicConnected || mQQMusicAPI == null) {
            return NOMALERRORCODE;
        }
        Bundle ret = null;
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(), " hi");
            ret = mQQMusicAPI.execute(HICMD, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int code = ret.getInt(RETURNCODEPARAMS);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(), " hi code is : " + code);
        if (code == NOMALSUCCESSCODE) {
            return NOMALSUCCESSCODE;
        }
        version = ret.getInt(Keys.API_RETURN_KEY_VERSION);
        return version;

    }

    @Override
    public boolean loginQQMusic() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " loginQQMusic");
        if (!isMusicConnected) {
            return false;
        }
        type = OPSTYPELOGINQQMUSIC;
        Bundle ret = CommonCmd.loginQQMusic(mContext, PARAMSCALLBACKURL);
        int code = ret.getInt(RETURNCODEPARAMS);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " loginQQMusic code is : " + code);
        if (code != NOMALSUCCESSCODE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean openQQMusic() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " openQQMusic");
        type = OPSTYPEOPENQQMUSIC;
        Bundle ret = CommonCmd.openQQMusic(mContext, PARAMSCALLBACKURL);
        int code = ret.getInt(RETURNCODEPARAMS);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " openQQMusic code is : " + code);
        if (/*code != 0*//*!isMusicConnected*/!playStatus || getVersion() != 0) {
            Intent mIntent = new Intent(ApplicationContext, MusicDamoService.class);
            if (searchBundle != null) {
                mIntent.putExtras(searchBundle);
            }
            ApplicationContext.startService(mIntent);
            isMusicConnChanged = true;
            playStatus = true;
        }
        return true;
    }

    @Override
    public int pauseMusic() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " pauseMusic");
        if (mQQMusicAPI == null && !isMusicConnected) {
            openQQMusic();
            bindQQMusicService();
            return NOMALERRORCODE;
        }
        if (getPlaybackState() == PLAYPAUSED) {
            return NOMALSUCCESSCODE;
        }
        int code = getValueCodeInt(PAUSECMD);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " pauseMusic code is : " + code);
        if (code != NOMALSUCCESSCODE) {
            openQQMusic();
            bindQQMusicService();
        }
        return code;
    }

    private int pauseMusicStrict(){
        return getValueCodeInt(STOPCMD);
    }

    @Override
    public int playMusic() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " playMusic");

        boolean canPlay = mAudioPolicyManager.requestAudioPolicy(mAudioPolicyListener,
                AudioPolicyManager.AudioType.MUSIC);
        if (!canPlay) {
            ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "", "Audio policy manager do not allow" +
                    "to play music");
            return NOMALERRORCODE;
        }

        int state = getPlaybackState();
        if (mQQMusicAPI == null && !isMusicConnected) {
            openQQMusic();
            bindQQMusicService();
            return NOMALSUCCESSCODE;
        }
        if (state == PLAYSTARTED
                || state == PLAYPAUSED
                || state == PLAYPAUSING) {
            return resumeMusic();
        }
        int code = getValueCodeInt(PLAYCMD);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " playMusic code is : " + code);
        if (code == NOMALSUCCESSCODE) {
            playStatus = false;
            if(getCurrentSong() == null){
                setPre(true);
            }
        }
        if (code != NOMALSUCCESSCODE) {
            openQQMusic();
            bindQQMusicService();
        }
        return code;
    }

    @Override
    public int resumeMusic() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " resumeMusic");
        if (!isMusicConnected) {
            return NOMALERRORCODE;
        }
        return getValueCodeInt(RESUMECMD);
    }

    @Override
    public int skipToNext() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " skipToNext");
        int state = getPlaybackState();
        if (state == PLAYSTARTED) {
            return getValueCodeInt(NEXTCMD);
        } else if (state == PLAYPAUSED) {
            resumeMusic();
        } else {
            checkPlayState();
            playMusic();
        }
        return getValueCodeInt(NEXTCMD);
    }

    @Override
    public int skipToPrevious() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " skipToPrevious");
        int state = getPlaybackState();
        if (state == PLAYSTARTED) {
            return getValueCodeInt(PREVCMD);
        } else if (state == PLAYPAUSED) {
            resumeMusic();
        } else {
            checkPlayState();
            playMusic();
        }
        return getValueCodeInt(PREVCMD);
    }

    @Override
    public int stopMusic() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " stopMusic");
        if (!isMusicConnected) {
            return NOMALERRORCODE;
        }
        return getValueCodeInt(STOPCMD);
    }

    @Override
    public void setPlayMode(int playMode) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " setPlayMode");
        if (!isMusicConnected) {
            return;
        }
        type = SETPLAYMODECMD;
        Bundle params = new Bundle();
        params.putInt(PARAMSPLAYMODE, playMode);
        try {
            if (DEBUG)
                android.util.Log.i(QQMusicCMD.class.getSimpleName(), " setPlayMode playMode is : " + playMode);
            params = mQQMusicAPI.execute(SETPLAYMODECMD, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void search(String keyword, int searchType, boolean firstPage) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " search");

        if (!isMusicConnected || getVersion() != 0) {
            if (searchBundle == null) {
                searchBundle = new Bundle();
                searchBundle.putString("type", "search");
                searchBundle.putString("key", keyword);
            } else {
                searchBundle.putString("type", "search");
                searchBundle.putString("key", keyword);
            }
            checkPlayState();
        }
        type = SEARCHCMD;
        Bundle params = new Bundle();
        params.putString(PARAMSKEYWORD, keyword);
        params.putInt(PARAMSSEARCHTYPE, 0);
        params.putBoolean(PARAMSFIRSTPAGE, true);
        try {
            if (DEBUG)
                android.util.Log.i(QQMusicCMD.class.getSimpleName(), " search keyword is : " + keyword);
            if (mQQMusicAPI == null) {
                return;
            }
            UMAnalyse.startTime(UMDurationEvent.MUSIC);
            mQQMusicAPI.executeAsync(SEARCHCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int adjustVolume(int volume) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(), " entered adjustVolume");
        volumePerc = NOMALSUCCESSCODE;
        if (volume > 0) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,/*AudioManager.FLAG_SHOW_UI*/0);
        } else {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,/*AudioManager.FLAG_SHOW_UI*/0);
        }
        cMusicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxMusicVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumePerc = volumePerc | cMusicVolume;
        volumePerc = volumePerc << 4;
        volumePerc = volumePerc | maxMusicVolume;
        return volumePerc;
    }

    @Override
    public void adjustVolumeF(double perc) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " adjustVolumeF");
        maxMusicVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int index = (int) Math.round(maxMusicVolume * perc);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " adjustVolumeF perc is : " + perc + " index is : " + index);
        if (index >= 0) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        }
    }

    @Override
    public void addToFavouriteAlias(boolean isPath, ArrayList<String> favList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " addToFavouriteAlias");
        if (!isMusicConnected) {
            return;
        }
        type = OPSTYPEADDTOFAV;
        if (isPath) {
            addLocalPathToFavourite(favList);
        } else {
            addToFavourite(favList);
        }
    }

    @Override
    public String getFavouriteFolderId() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getFavouriteFolderId");
        if (!isMusicConnected) {
            return null;
        }
        return getValueCodeStr(GETFAVFOLDERIDCMD);
    }

    @Override
    public void isFavouriteAlias(boolean isPath, ArrayList<String> list) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " isFavouriteAlias");
        if (!isMusicConnected) {
            return;
        }
        if (isPath) {
            isFavouriteLocalPath(list);
        } else {
            isFavouriteMid(list);
        }
    }

    @Override
    public void removeFromFavouriteAlias(boolean isPath, ArrayList<String> list) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " removeFromFavouriteAlias");
        if (!isMusicConnected) {
            return;
        }
        if (isPath) {
            removeFromFavourite(list);
        } else {
            removeLocalPathFromFavourite(list);
        }
    }

    @Override
    public void getListAlias(int mode, String folderId, int folderType, int page) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getListAlias");
        checkPlayState();
        switch (mode) {
            case MODEGETFOLDERLIST:
                getFolderList(folderId, folderType, page);
                break;
            case MODEGETPLAYLIST:
                if (playList != null && playList.size() != 0) {
                    if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                            " mListeners is : " + mListeners + " preFlag is : " + preFlag);
                    if (mListeners != null) {
                        for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                            mMusicListener.onBack(playList, false);
                        }
                    }
                } else {
                    getPlayList(page);
                }
                break;
            case MODEGETSONGLIST:
                getSongList(folderId, folderType, page);
                break;
        }
    }

    @Override
    public boolean isServiceConncted() {
        return isMusicConnected;
    }

    @Override
    public void playSongAlias(int mode, ArrayList<String> songIdList, int index) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " playSongAlias");

        boolean canPlay = mAudioPolicyManager.requestAudioPolicy(mAudioPolicyListener,
                AudioPolicyManager.AudioType.MUSIC);
        if (!canPlay) {
            ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "", "Audio policy manager do not allow" +
                    "to play music");
            return;
        }

        if (!isMusicConnected) {
            return;
        }
        switch (mode) {
            case MODEPLAYSONGID:
                if (index == PARAMINDEXINV) {
                    playSongId(songIdList);
                } else {
                    playSongIdAtIndex(songIdList, index);
                }
                break;
            case MODEPLAYSONGLOCALPATH:
                playSongLocalPath(songIdList);
                break;
            case MODEPLAYSONGMID:
                if (index == PARAMINDEXINV) {
                    playSongMid(songIdList);
                } else {
                    playSongMidAtIndex(songIdList, index);
                }
                break;
        }
    }

    ArrayList<String> list = new ArrayList<String>() {{
        add(API_EVENT_PLAY_SONG_CHANGED);
        add(API_EVENT_PLAY_LIST_CHANGED);
        add(API_EVENT_PLAY_MODE_CHANGED);
        add(API_EVENT_PLAY_STATE_CHANGED);
        add(API_EVENT_SONG_FAVORITE_STATE_CHANGED);
    }};

    @Override
    public void registerEventListener(ArrayList<String> eventList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " registerEventListener");
        if (!isMusicConnected || mQQMusicAPI == null) {
            return;
        }
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " enterd registerEventListener");
            Log.d("zhongquan","mIQQMusicApiEventListenerImpl = " + mIQQMusicApiEventListenerImpl);
            mQQMusicAPI.registerEventListener(/*eventList*/list, mIQQMusicApiEventListenerImpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterEventListener(ArrayList<String> eventList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " unregisterEventListener");
        if (!isMusicConnected || mQQMusicAPI == null) {
            return;
        }
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " enterd unregisterEventListener");
            mQQMusicAPI.unregisterEventListener(eventList, mIQQMusicApiEventListenerImpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void clearMusicListener(IntegrationCore.MusicListener listener) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " clearMusicListener");
        if (mListeners != null && mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void setMusicListener(IntegrationCore.MusicListener listener) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " setMusicListener");
        if (mListeners != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void bindQQMusicService() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " bindQQMusicService");
        Intent intent = new Intent(QQINTENT);
        intent.setPackage(QQPACKAGE);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindQQMusicService(){
        mContext.unbindService(mServiceConnection);
    }

    private class IQQMusicApiCallbackImpl extends IQQMusicApiCallback.Stub {

        @Override
        public void onReturn(Bundle bundle) throws RemoteException {
            getAsyncResponse(bundle);
        }
    }

    private class IQQMusicApiEventListenerImpl extends IQQMusicApiEventListener.Stub {

        @Override
        public void onEvent(String s, Bundle bundle) throws RemoteException {
            switch (s) {
                case API_EVENT_PLAY_LIST_CHANGED:
                    i++;
                    if (playList != null) playList.clear();
                    if (mListeners != null) {
                        for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                            mMusicListener.playListChange();
                        }
                    }
                    break;
                case API_EVENT_PLAY_MODE_CHANGED:
                    if (mListeners != null) {
                        for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                            mMusicListener.playModeChange();
                        }
                    }
                    break;
                case API_EVENT_PLAY_SONG_CHANGED:
                    int state2 = getPlaybackState();
                    if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                            "  state2 is : " + state2);
                    if (lastSong == null) lastSong = getCurrentSong();
                    if (oldSong == null) {
                        oldSong = lastSong;
                        saveOldSong(DEFAULT_FILE, oldSong);
                        shouldSaveOldList = true;
                        getPlayList(0);
                    }
                    if (lastSong != null && !lastSong.getTitle().equals(getCurrentSong().getTitle())) {
                        lastSong = getCurrentSong();
                        mSongList.add(lastSong);
                        checkOldListSize(mSongList);
                        if (mSongList.size() > 1) {
                            oldSong = mSongList.get(0);
                            saveOldSong(DEFAULT_FILE, oldSong);
                            shouldSaveOldList = true;
                            getPlayList(0);
                        }
                        saveLastSong(DEFAULT_FILE, lastSong);
                    }
                    if (mListeners != null && (PLAYINIT == state2
                                            || PLAYIDLE == state2
                                            || PLAYSTARTED == state2
                                            || PLAYPREPARED == state2)) {
                        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                                " API_EVENT_PLAY_SONG_CHANGED state2 is : " + state2);
                        for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                            if (resStatus){
                                mMusicListener.songChange();
                            }
                        }
                    }
                    break;
                case API_EVENT_PLAY_STATE_CHANGED:
                    if(PLAYSTARTED == getPlaybackState()){
                        UMAnalyse.startTime(UMDurationEvent.MUSIC);
                    }else if(PLAYSTOPPED == getPlaybackState() || PLAYPAUSED == getPlaybackState()){
                        UMAnalyse.stopTime(UMDurationEvent.MUSIC);
                    }
                    j++;
                    int state = getPlaybackState();
                    if (state == PLAYSTOPPED && ((SystemClock.elapsedRealtime() - oldTime) < STATE_CHANGE_GAP))
                        k++;
                    if (k >= STATE_CHANGE_COUNT || ((SystemClock.elapsedRealtime() - oldTime) > STATE_CHANGE_GAP) || state != PLAYSTOPPED)
                        k = 0;
                    /*if (Math.abs(k) % 3 == 2 && state == PLAYSTOPPED) new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(ArielApplication.getApp().getApplicationContext()
                                    , R.string.loss_net_hint, Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }).start();*/
                    oldTime = SystemClock.elapsedRealtime();
                    if (lastSong == null) lastSong = getCurrentSong();
                    if (oldSong == null) {
                        oldSong = lastSong;
                        saveOldSong(DEFAULT_FILE, oldSong);
                        shouldSaveOldList = true;
                        getPlayList(0);
                    }
                    saveLastSong(DEFAULT_FILE, lastSong);
                    if (mListeners != null && oldState != state && (PLAYPAUSED == state
                            || PLAYIDLE == state
                            || PLAYSTARTED == state
                            || PLAYSTOPPED == state
                            || PLAYPAUSING == state)) {
                        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                                " API_EVENT_PLAY_STATE_CHANGED state is : " + state);
                        /**if (state == PLAYSTARTED) {
                            StageController.getStageController().setBGStream(StageController.BG_STREAM_MUSIC);
                        }

                        if (state == PLAYPAUSED) {
                            StageController.getStageController().setBGStream(StageController.BG_STREAM_NONE);
                        }*/
                        if (state == PLAYSTARTED) {
                            mAudioPolicyManager.requestAudioPolicy(mAudioPolicyListener,
                                    AudioPolicyManager.AudioType.MUSIC);
                        }

                        if (state == PLAYSTOPPED) {
                            mAudioPolicyManager.abandonAudioPolicy(AudioPolicyManager.AudioType.MUSIC);
                        }

                        for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                            mMusicListener.playStateChanged();
                        }
                    }
                    oldState = state;
                    break;
                case API_EVENT_SONG_FAVORITE_STATE_CHANGED:
                    if (mListeners != null) {
                        for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                            mMusicListener.favStateChange();
                        }
                    }
                    break;

            }
        }
    }

    private String mCurrentId;

    int getValueCodeInt(String cmd) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getValueCodeInt cmd is : " + cmd);
        if (mQQMusicAPI == null) {
            return -1;
        }
        if (cmd.equalsIgnoreCase(PREVCMD)) {
            Data.Song currentSong = getCurrentSong();
            if (currentSong != null) {
                mCurrentId =  currentSong.getId();
            }
        }
        Bundle ret;
        try {
            ret = mQQMusicAPI.execute(cmd, null);
        } catch (Exception e) {
            e.printStackTrace();
            return NOMALERRORCODE;
        }
        int code = ret.getInt(RETURNCODEPARAMS);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getValueCodeInt cmd is : " + cmd + " code is : " + code);
        if (code != NOMALSUCCESSCODE) {
            return NOMALERRORCODE;
        }

        int value = ret.getInt(RETURNDATAPARAMS);
        switch (cmd) {
            case PLAYCMD:
            case PAUSECMD:
            case RESUMECMD:
            case STOPCMD:
            case NEXTCMD:
                return code;
            case PREVCMD:
                Data.Song currentSong = getCurrentSong();
                if (currentSong != null) {
                    if (currentSong.getId().equalsIgnoreCase(mCurrentId)) {
                        pauseMusicStrict();
                        return ERROR_CODE_NO_PERMISSION;
                    }
                }
                return code;
        }
        return value;
    }

    long getValueCodeFlo(String cmd) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getValueCodeFlo cmd is : " + cmd);
        if (mQQMusicAPI == null) return -1L;
        Bundle ret;
        try {
            ret = mQQMusicAPI.execute(cmd, null);
        } catch (Exception e) {
            e.printStackTrace();
            return NOMALLONGERRORCODE;
        }
        int code = ret.getInt(RETURNCODEPARAMS);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getValueCodeFlo cmd is : " + cmd + " code is : " + code);
        if (code != NOMALSUCCESSCODE) {
            return NOMALLONGERRORCODE;
        }

        long value = ret.getLong(RETURNDATAPARAMS);
        return value;
    }

    String getValueCodeStr(String cmd) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getValueCodeStr cmd is : " + cmd);
        if (mQQMusicAPI == null) {
            return null;
        }
        Bundle ret;
        try {
            ret = mQQMusicAPI.execute(cmd, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int code = ret.getInt(RETURNCODEPARAMS);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getValueCodeStr cmd is : " + cmd + " code is : " + code);
        if (code != NOMALSUCCESSCODE) {
            return null;
        }

        String value = ret.getString(RETURNDATAPARAMS);
        return value;
    }

    String getValueCodeOb(String cmd) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getValueCodeOb cmd is : " + cmd);
        if (mQQMusicAPI == null) {
            return null;
        }
        Bundle ret;
        try {
            ret = mQQMusicAPI.execute(cmd, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int code = ret.getInt(RETURNCODEPARAMS);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getValueCodeOb cmd is : " + cmd + " code is : " + code);
        if (code != NOMALSUCCESSCODE) {
            return null;
        }

        String value = ret.getString(RETURNDATAPARAMS);
        return value;
    }

    public void addLocalPathToFavourite(ArrayList<String> favPathList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " addLocalPathToFavourite");
        if (mQQMusicAPI == null) return;
        type = ADDLOCALPATHTOFAVCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSLOCALPATHLIST, favPathList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " addLocalPathToFavourite");
            mQQMusicAPI.executeAsync(ADDLOCALPATHTOFAVCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToFavourite(ArrayList midList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " addToFavourite");
        if (mQQMusicAPI == null) return;
        type = ADDTOFAVCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSMIDLIST, midList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " addToFavourite");
            mQQMusicAPI.executeAsync(ADDTOFAVCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isFavouriteLocalPath(ArrayList localPathList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " isFavouriteLocalPath");
        if (mQQMusicAPI == null) return;
        type = ISFAVLOCALPATHCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSLOCALPATHLIST, localPathList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " isFavouriteLocalPath");
            mQQMusicAPI.executeAsync(ISFAVLOCALPATHCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isFavouriteMid(ArrayList midList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " isFavouriteMid");
        if (mQQMusicAPI == null) return;
        type = ISFAVMIDCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSMIDLIST, midList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " isFavouriteMid");
            mQQMusicAPI.executeAsync(ISFAVMIDCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFromFavourite(ArrayList midList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " removeFromFavourite");
        if (mQQMusicAPI == null) return;
        type = REMOVEFROMFAVCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSMIDLIST, midList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " removeFromFavourite");
            mQQMusicAPI.executeAsync(REMOVEFROMFAVCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeLocalPathFromFavourite(ArrayList localPathList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " removeLocalPathFromFavourite");
        if (mQQMusicAPI == null) return;
        type = REMOVELOCALPATHFROMFAVCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSLOCALPATHLIST, localPathList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " removeLocalPathFromFavourite");
            mQQMusicAPI.executeAsync(REMOVELOCALPATHFROMFAVCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFolderList(String folderId, int folderType, int page) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getFolderList");
        if (mQQMusicAPI == null) return;
        type = GETFOLDERLISTCMD;
        switch (folderType) {
            case RECOMMEND_FOLDER:
                typeFolder = RECOMMEND_FOLDER;
                break;
            case RANK:
                typeFolder = RANK;
                break;
            case MY_FOLDER:
                typeFolder = MY_FOLDER;
                //loginQQMusic();
                break;
        }
        Bundle params = new Bundle();
        params.putString(PARAMSFOLDERID, folderId);
        params.putInt(PARAMSFOLDERTYPE, folderType);
        params.putInt(PARAMSPAGE, page);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " getFolderList folderType is : " + folderType);
            mQQMusicAPI.executeAsync(GETFOLDERLISTCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPlayList(int page) {
        if(preFlag) return;
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getPlayList");
        if (mQQMusicAPI == null) return;
        type = GETPLAYLISTCMD;
        Bundle params = new Bundle();
        params.putInt(PARAMSPAGE, page);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " getPlayList");
            mQQMusicAPI.executeAsync(GETPLAYLISTCMD, params, mQQMusicCallback);
            if(!preFlag && !shouldSaveOldList) new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(ArielApplication.getApp().getApplicationContext()
                            , R.string.get_play_list_info, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSongList(String folderId, int folderType, int page) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getSongList");
        if (mQQMusicAPI == null) return;
        type = GETSONGLISTCMD;
        Bundle params = new Bundle();
        params.putString(PARAMSFOLDERID, folderId);
        params.putInt(PARAMSFOLDERTYPE, folderType);
        params.putInt(PARAMSPAGE, page);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " getSongList folderId is : " + folderId + " folderType is : " + folderType);
            mQQMusicAPI.executeAsync(GETSONGLISTCMD, params, mQQMusicCallback);
            if(!preFlag && !shouldSaveOldList) new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(ArielApplication.getApp().getApplicationContext()
                            , R.string.get_song_list_info, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSongId(ArrayList songIdList) {
        if(shouldBind) {
            //bindQQMusicService();
            shouldBind = false;
        }
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " playSongId");

        if (!checkAudioPolicy())
            return;

        if (mQQMusicAPI == null) return;
        type = PLAYSONGIDCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSSONGIDLIST, songIdList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " playSongId");
            mQQMusicAPI.executeAsync(PLAYSONGIDCMD, params, mQQMusicCallback);
            if(!preFlag && !shouldSaveOldList) new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(ArielApplication.getApp().getApplicationContext()
                            , R.string.require_play_info, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playSongIdAtIndex(ArrayList songIdList, int index) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " playSongIdAtIndex");

        if (!checkAudioPolicy())
            return;

        if (mQQMusicAPI == null) return;
        type = PLAYSONGIDATINDEXCMD;
        autoIndex = index;
        autoSaveList = songIdList;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSSONGIDLIST, songIdList);
        params.putInt(PARAMSINDEX, index);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " playSongIdAtIndex index is : " + index);
            mQQMusicAPI.executeAsync(PLAYSONGIDATINDEXCMD, params, mQQMusicCallback);
            if(!preFlag && !shouldSaveOldList) new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(ArielApplication.getApp().getApplicationContext()
                            , R.string.require_play_info, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkAudioPolicy(){
        boolean canPlay = mAudioPolicyManager.requestAudioPolicy(mAudioPolicyListener,
                AudioPolicyManager.AudioType.MUSIC);
        if (!canPlay) {
            ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "", "Audio policy manager do not allow" +
                    "to play music");
            return false;
        }

        return true;
    }

    public void playSongLocalPath(ArrayList<String> pathList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " playSongLocalPath");

        if (!checkAudioPolicy())
            return;

        if (mQQMusicAPI == null) return;
        type = PLAYSONGLOCALPATHCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSPATHLIST, pathList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " playSongLocalPath");
            mQQMusicAPI.executeAsync(PLAYSONGLOCALPATHCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSongMid(ArrayList midList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " playSongMid");

        if (!checkAudioPolicy())
            return;

        if (mQQMusicAPI == null) return;
        type = PLAYSONGMIDCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSMIDLIST, midList);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " playSongMid");
            mQQMusicAPI.executeAsync(PLAYSONGMIDCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playSongMidAtIndex(ArrayList midList, int index) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " playSongMidAtIndex");

        if (!checkAudioPolicy())
            return;

        if (mQQMusicAPI == null) return;
        type = PLAYSONGMIDATINDEXCMD;
        Bundle params = new Bundle();
        params.putStringArrayList(PARAMSMIDLIST, midList);
        params.putInt(PARAMSINDEX, index);
        try {
            if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                    " playSongMidAtIndex index is : " + index);
            mQQMusicAPI.executeAsync(PLAYSONGMIDATINDEXCMD, params, mQQMusicCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void getAsyncResponse(Bundle bundle) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getAsyncResponse");
        int code = bundle.getInt(RETURNCODEPARAMS);
        if (code != NOMALSUCCESSCODE) {
            resStatus = false;
        } else {
            resStatus = true;
        }
        String data = bundle.getString(Keys.API_RETURN_KEY_DATA);
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getAsyncResponse code is : " + code + " type is : " + type);
        if (knowFolderType != null && getKonwFlag && GETSONGLISTCMD.equals(type) &&
                ((LIVE_MUSIC.equals(knowFolderType))
                || (TOP_MUSIC.equals(knowFolderType))
                || (NEW_MUSIC.equals(knowFolderType)))) {
            if (code == 0 && data != null/* && data.indexOf(knownSongName) != -1*/) {
                Gson gson = new Gson();
                JsonParser tempParser = new JsonParser();
                JsonArray Jarray = tempParser.parse(data).getAsJsonArray();
                for (JsonElement obj : Jarray) {
                    Data.Song song = gson.fromJson(obj, Data.Song.class);
                    if (song != null && song.getTitle() != null
                            && knownSongName != null && knownSongName.equals(song.getTitle())) {
                        if (mListeners != null) {
                            for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                                mMusicListener.backFolderName(knowFolderType);
                            }
                        }
                        knowFolderType = EMPTYSTRINGVALUE;
                        knownSongName = EMPTYSTRINGVALUE;
                        getKonwFlag = false;
                    }
                }
            }
            switch (knowFolderType) {
                case LIVE_MUSIC:
                    mHandler.removeMessages(MESSAGEGETLOVEMUSIC);
                    mHandler.removeMessages(MESSAGEGETTOPMUSIC);
                    mHandler.removeMessages(MESSAGEGETNEWMUSIC);
                    mHandler.obtainMessage(MESSAGEGETTOPMUSIC);
                    mHandler.sendEmptyMessage(MESSAGEGETTOPMUSIC);
                    break;
                case TOP_MUSIC:
                    mHandler.removeMessages(MESSAGEGETLOVEMUSIC);
                    mHandler.removeMessages(MESSAGEGETTOPMUSIC);
                    mHandler.removeMessages(MESSAGEGETNEWMUSIC);
                    mHandler.obtainMessage(MESSAGEGETNEWMUSIC);
                    mHandler.sendEmptyMessage(MESSAGEGETNEWMUSIC);
                    break;
            }
            if (NEW_MUSIC.equals(knowFolderType)) {
                if (mListeners != null) {
                    for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                        mMusicListener.backFolderName(null);
                    }
                }
                knowFolderType = "";
                knownSongName = "";
                getKonwFlag = false;
            }
            return;
        }
        switch (type) {
            case OPSTYPESEARCHKEY:
                getList(data);
                break;
            case SETPLAYMODECMD:
                break;
            case SEARCHCMD:
                UMAnalyse.stopTime(UMDurationEvent.MUSIC);
                getList(data);
                break;
            case ADDLOCALPATHTOFAVCMD:
                break;
            case ADDTOFAVCMD:
                break;
            case ISFAVLOCALPATHCMD:
                break;
            case ISFAVMIDCMD:
                break;
            case REMOVEFROMFAVCMD:
                break;
            case REMOVELOCALPATHFROMFAVCMD:
                break;
            case GETFOLDERLISTCMD:
                String hintInfo = EMPTYSTRINGVALUE;
                if(!resStatus){
                    switch(code){
                        case REQUEST_ERROR_CODE_ILLEGAL_ARGUMENT:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_args_illegal);
                            break;
                        case REQUEST_ERROR_CODE_INTERRUPTED:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_interrupted);
                            break;
                        case REQUEST_ERROR_CODE_NO_PERMISSION:
                            //hintInfo = mContext.getResources().getString(R.string.
                                //        error_no_permission);
                            break;
                        case REQUEST_ERROR_CODE_NOT_INITIALIZED:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_no_intallized);
                            break;
                        case REQUEST_ERROR_CODE_UNKNOWN:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_uknow);
                            break;
                        case REQUEST_ERROR_CODE_UNSUPPORTED_ACTION:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_no_support_action);
                            break;
                        case REQUEST_ERROR_CODE_FAILED_TO_QUERY_FROM_MID:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_query_mid);
                            break;
                        case REQUEST_ERROR_CODE_FAILED_TO_QUERY_FROM_SONG_ID:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_query_song_id);
                            break;
                        case REQUEST_ERROR_CODE_DATA_ARGUMENT:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_data_args);
                            break;
                        case REQUEST_ERROR_CODE_DATA_INNER:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_data_inner);
                            break;
                        case REQUEST_ERROR_CODE_DATA_NETWORK:
                                hintInfo = mContext.getResources().getString(R.string.
                                        error_data_net);
                            break;
                        case REQUEST_ERROR_CODE_AUTHENTICATION:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_auth);
                            break;
                        case REQUEST_ERROR_CODE_LIST_TOO_LONG:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_list_long);
                            break;
                        case REQUEST_ERROR_CODE_NO_MEDIA:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_no_media);
                            break;
                        case REQUEST_ERROR_CODE_NO_NETWORK:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_no_net);
                            break;
                        case REQUEST_ERROR_CODE_NO_PLAY_PERMISSION:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_no_play_permission);
                            break;
                        case REQUEST_ERROR_CODE_NO_STORAGE:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_no_play_stor);
                            break;
                        case REQUEST_ERROR_CODE_OPERATOR_NETWORK:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_opera_net);
                            break;
                        case REQUEST_ERROR_CODE_OVER_SEA_FORBIDDEN:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_forb_over_sea);
                            break;
                        case REQUEST_ERROR_CODE_PLAYER_ERROR:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_player_error);
                            break;
                        case REQUEST_ERROR_CODE_TOO_OFTEN:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_too_often);
                            break;
                        case REQUEST_ERROR_CODE_PLAY_UNKNOWN:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_play_unknow);
                            break;
                        case REQUEST_ERROR_CODE_SERVICE_INITIATING_TIMEOUT:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_sv_ini_timeout);
                            break;
                        case REQUEST_ERROR_CODE_SERVICE_NOT_READY:
                            hintInfo = mContext.getResources().getString(R.string.
                                        error_code_sv_not_ready);
                            break;
                    }
                    final String hint = hintInfo;
                    if(!EMPTYSTRINGVALUE.equals(hint))
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(ArielApplication.getApp().getApplicationContext()
                                        , hint, Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }).start();
                }else if(data == null || EMPTYSTRINGVALUE.equals(data)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(ArielApplication.getApp().getApplicationContext()
                                        , R.string.empty_folder, Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }).start();
                }
                if(data != null && !EMPTYSTRINGVALUE.equals(data))
                getFolderList(data);
                break;
            case GETPLAYLISTCMD:
                getList(data);
                break;
            case GETSONGLISTCMD:
                if(code == REQUEST_ERROR_CODE_AUTHENTICATION){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(ArielApplication.getApp().getApplicationContext()
                                    , R.string.error_code_auth, Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }).start();
                }
                getList(data);
                break;
            case PLAYSONGIDCMD:
                break;
            case PLAYSONGIDATINDEXCMD:
                if(!resStatus && code == ERROR_CODE_NO_PERMISSION){
                    permissionHinNowtime = SystemClock.elapsedRealtime();
                    if(permissionHinNowtime - permissionHinOldtime > PERMISSION_HINT_MIN_SKIP_TIME || permissionHinOldtime == 0L) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                Toast.makeText(ArielApplication.getApp().getApplicationContext()
                                        , R.string.have_no_right, Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        }).start();
                    }
                    mHandler.removeMessages(MESSAGE_PLAY_NEXT_SONG);
                    Message msg = new Message();
                    msg.what = MESSAGE_PLAY_NEXT_SONG;
                    Bundle vipNextSong = new Bundle();
                    vipNextSong.putInt("index",++autoIndex);
                    vipNextSong.putStringArrayList("list",autoSaveList);
                    msg.setData(vipNextSong);
                    mHandler.sendMessageDelayed(msg,500);
                    permissionHinOldtime = SystemClock.elapsedRealtime();
                }
                if (mListeners != null) {
                    for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                        mMusicListener.codeState(resStatus);
                    }
                }
                break;
            case PLAYSONGLOCALPATHCMD:
                if (mListeners != null) {
                    for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                        mMusicListener.forSpecialLocalCode(PLAYSONGLOCALPATHCMD, code);
                    }
                }
                break;
            case PLAYSONGMIDCMD:
                break;
            case PLAYSONGMIDATINDEXCMD:
                break;
        }
    }

    public synchronized ArrayList getList(String data) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getList");
        ArrayList<String> temp = new ArrayList<String>();
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray Jarray = parser.parse(data).getAsJsonArray();
        ArrayList<Data.Song> searchList = new ArrayList<Data.Song>();
        for (JsonElement obj : Jarray) {
            Data.Song song = gson.fromJson(obj, Data.Song.class);
            searchList.add(song);
        }
        if(preFlag && typeFolder == TYPELISTNEWMUSIC && GETSONGLISTCMD.equals(type)){
            if(searchList != null && searchList.size() > 0){
                for(Data.Song songNew:searchList){
                    newSongIdList.add(songNew.getId());
                }
                if (mListeners != null) {
                    for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                        mMusicListener.onBack(searchList, true);
                    }
                }
                if(newSongIdList != null && newSongIdList.size() > 0 &&
                        (getCurrentSong() == null)){
                      //unBindQQMusicService();
                      mHandler.sendEmptyMessageDelayed(MESSAGE_SEND_LIST,500);
                }
            }
            preFlag = false;
            return null;
        }
        if (shouldSaveOldList && searchList != null && !preFlag && !SEARCHCMD.equals(type)) {
            ArrayList<String> tempOld = new ArrayList<String>();
            for (Data.Song old : searchList) {
                tempOld.add(old.getId());
            }
            oldPlayIDList = tempOld;
            saveOldList(DEFAULT_FILE, oldPlayIDList);
            shouldSaveOldList = false;
            return null;
        }
        if (GETPLAYLISTCMD.equals(type)) {
            playList = searchList;
            for (Data.Song song1 : playList) {
                temp.add(song1.getId());
            }
            playIDList = temp;
        }
        if (SEARCHCMD.equals(type) || OPSTYPESEARCHKEY.equals(type)) {
            if (searchResoultList != null) searchResoultList.clear();
            if (searchResoultIDList != null) searchResoultIDList.clear();
            searchResoultList = new ArrayList<Data.Song>();
            searchResoultIDList = new ArrayList<String>();
            for (Data.Song tempSong : searchList) {
                searchResoultList.add(tempSong);
                searchResoultIDList.add(tempSong.getId());
            }
            mHandler.removeMessages(MESSAGE_PLAY_OFFLINE_SEARCH);
            mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_OFFLINE_SEARCH,500);
        }
        if (DEBUG && searchList != null) android.util.Log.i(QQMusicCMD.
                class.getSimpleName(), " songList size is : " + searchList.size());
        if (DEBUG && searchList != null) android.util.Log.i(QQMusicCMD.
                class.getSimpleName(), " mListeners is : " + mListeners +
        " shouldSaveOldList is : " + shouldSaveOldList + " preFlag is : " + preFlag);
        if (mListeners != null && !shouldSaveOldList) {
            for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                if (GETPLAYLISTCMD.equals(type)) {
                    mMusicListener.onBack(searchList, false);
                } else {
                    mMusicListener.onBack(searchList, true);
                }
            }
        }
        return searchList;
    }

    public synchronized ArrayList getFolderList(String data) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getFolderList");
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray Jarray = parser.parse(data).getAsJsonArray();
        ArrayList<Data.FolderInfo> folderList = new ArrayList<Data.FolderInfo>();
        for (JsonElement obj : Jarray) {
            Data.FolderInfo folder = gson.fromJson(obj, Data.FolderInfo.class);
            folderList.add(folder);
        }
        if (preFlag) {
            switch (typeFolder) {
                case RANK:
                    rankFolderCacheList = folderList;
                    if(rankFolderCacheList != null) {
                        for (Data.FolderInfo folder : rankFolderCacheList) {
                            if(folder != null && folder.getMainTitle() != null
                                    && !EMPTYSTRINGVALUE.equals(folder.getMainTitle())
                                    && NEW_MUSIC.equals(folder.getMainTitle())){
                                newMusicFolder = folder;
                            }
                        }
                    }
            }
            mHandler.sendEmptyMessage(MESSAGEGETRANKNEWMUSIC);
        }
        if (DEBUG && folderList != null) android.util.Log.i(QQMusicCMD.
                class.getSimpleName(), " folderList size is : " + folderList.size());
        if (mListeners != null) {
            for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                mMusicListener.onBackFolder(folderList);
            }
        }
        return folderList;
    }

    public void checkPlayState() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " checkPlayState");
        int code = getVersion();
        if (!isMusicConnected || code != 0) {
            openQQMusic();
            bindQQMusicService();
        }
    }

    @Override
    public void setMusicConn(boolean conn) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " setMusicConn");
        isMusicConnected = conn;
        if (isMusicConnected) {
            bindQQMusicService();
        }
    }

    @Override
    public ArrayList<Data.Song> getPlayListCache() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getPlayListCache");
        return playList;
    }

    @Override
    public ArrayList<String> getPlayIDList() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getPlayIDList");
        return playIDList;
    }

    @Override
    public Data.Song getPlaySongAtIndex(int index) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getPlaySongAtIndex");
        return playList.get(i - 1);
    }

    @Override
    public Data.Song getPlaySongByID(String id) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getPlaySongByID");
        for (Data.Song song2 : playList) {
            if (id != null && id.equals(song2.getId())) {
                return song2;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Data.Song> getSearchList() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getSearchList ");
        return searchResoultList;
    }

    @Override
    public ArrayList<Data.FolderInfo> getFolderList(int type) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getFolderList ");
        switch (type) {
            case RECOMMEND_FOLDER:
                return recommendFolderCacheList;
            case RANK:
                return rankFolderCacheList;
            case MY_FOLDER:
                return myFolderCacheList;
        }
        return null;
    }

    @Override
    public Data.FolderInfo getFolderByIndex(int type, int index) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getFolderByIndex ");
        switch (type) {
            case RECOMMEND_FOLDER:
                return (recommendFolderCacheList != null) ?
                        recommendFolderCacheList.get(index) : null;
            case RANK:
                return (rankFolderCacheList != null) ?
                        rankFolderCacheList.get(index) : null;
        }
        return null;
    }

    @Override
    public Data.FolderInfo getFolderByTitle(String folderTitle) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getFolderByTitle folderTitle is ： " + folderTitle);
        if (recommendFolderCacheList != null) {
            for (Data.FolderInfo folderinfo : recommendFolderCacheList) {
                if (folderTitle != null && folderTitle.equals(folderinfo.getMainTitle()))
                    return folderinfo;
            }
        }
        if (rankFolderCacheList != null) {
            for (Data.FolderInfo folderinfo : rankFolderCacheList) {
                if (folderTitle != null && folderTitle.equals(folderinfo.getMainTitle()))
                    return folderinfo;
            }
        }

        if (myFolderCacheList != null) {
            for (Data.FolderInfo folderinfo : myFolderCacheList) {
                if (folderTitle != null && folderTitle.equals(folderinfo.getMainTitle()))
                    return folderinfo;
            }
        }
        return null;
    }

    @Override
    public void getKnownFolderByTitle(String songName) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getKnownFolderByTitle songName is ： " + songName);
        knownSongName = songName;
        String[] splitName = songName.split(SPACESTRINGVALUE);
        ArrayList<String> mList = IntegrationCore.getIntergrationCore(mContext).
                getSystemCMD().getLocalSongList(LOCALLISTTITLE);
        if (mList != null && mList.size() != 0) {
            for (String temp : mList) {
                if (temp != null && temp.equals(songName)) {
                    if (mListeners != null) {
                        for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                            mMusicListener.backFolderName(LOCAL_MUSIC);
                        }
                    }
                    return;
                }
                if (splitName != null && splitName.length > 1 && splitName[0] != null) {
                    if (temp != null && temp.equals(splitName[0])) {
                        if (mListeners != null) {
                            for (IntegrationCore.MusicListener mMusicListener : mListeners) {
                                mMusicListener.backFolderName(LOCAL_MUSIC);
                            }
                        }
                        return;
                    }
                }
            }
        }
        if (myFolderCacheList != null) {
            for (Data.FolderInfo folderinfo : myFolderCacheList) {
                if (LIVE_MUSIC.equals(folderinfo.getMainTitle())) {
                    knowFolderType = LIVE_MUSIC;
                    getKonwFlag = true;
                    knownLikeFolder = folderinfo;
                }
            }
        }
        if (rankFolderCacheList != null) {
            for (Data.FolderInfo folderinfo : rankFolderCacheList) {
                if (TOP_MUSIC.equals(folderinfo.getMainTitle())) {
                    knowFolderType = TOP_MUSIC;
                    knownTopFolder = folderinfo;
                } else if (NEW_MUSIC.equals(folderinfo.getMainTitle())) {
                    knowFolderType = NEW_MUSIC;
                    knownNewFolder = folderinfo;
                }
            }
        }
        mHandler.removeMessages(MESSAGEGETLOVEMUSIC);
        mHandler.removeMessages(MESSAGEGETTOPMUSIC);
        mHandler.removeMessages(MESSAGEGETNEWMUSIC);
        mHandler.obtainMessage(MESSAGEGETLOVEMUSIC);
        mHandler.sendEmptyMessage(MESSAGEGETLOVEMUSIC);
    }

    @Override
    public Data.FolderInfo getFolderById(String folderId) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getFolderById");
        if (recommendFolderCacheList != null) {
            for (Data.FolderInfo folderinfo : recommendFolderCacheList) {
                if (folderId != null && folderId.equals(folderinfo.getId()))
                    return folderinfo;
            }
        }
        if (rankFolderCacheList != null) {
            for (Data.FolderInfo folderinfo : rankFolderCacheList) {
                if (folderId != null && folderId.equals(folderinfo.getId()))
                    return folderinfo;
            }
        }
        return null;
    }

    @Override
    public void setPre(boolean flag) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " setPre");
        if (searchBundle != null) {
            opsType = searchBundle.getString("type");
            opsKey = searchBundle.getString("key");
        }
        preFlag = flag;
        if (preFlag) {
            registerEventListener(list);
            if (opsType != null && opsType.equals("search")
                    && opsKey != null && !opsKey.equals(EMPTYSTRINGVALUE)) {
                mHandler.removeMessages(MEDDAGEOFFLINESEARCH);
                mHandler.sendEmptyMessage(MEDDAGEOFFLINESEARCH);
            }else{
                mHandler.removeMessages(MESSAGEGETRANK);
                mHandler.sendEmptyMessage(MESSAGEGETRANK);
            }
        }
    }


    @Override
    public Data.Song getLastSong() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getLastSong");
        return lastSong;
    }

    @Override
    public Data.Song getOldSong() {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " getOldSong");
        return oldSong;
    }

    public void saveLastSong(String fileName, Data.Song lastSong) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " saveLastSong");
        if (fileName == null) {
            fileName = DEFAULT_FILE;
        }
        ShardPreUtils.getInstance(fileName).putStringValue(KEYLASTSONGTITLE, lastSong.getTitle());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYLASTSONGID, lastSong.getId());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYLASTSONGMID, lastSong.getMid());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYLASTSONGCOVERURI, lastSong.getAlbum().getCoverUri());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYLASTSONGALBUMTITLE, lastSong.getAlbum().getTitle());
        ShardPreUtils.getInstance(fileName).putLongValue(KEYLASTSONGALBUMID, lastSong.getAlbum().getId());
        ShardPreUtils.getInstance(fileName).putLongValue(KEYLASTSONGSINGERID, lastSong.getSinger().getId());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYLASTSONGSINGERTITLE, lastSong.getSinger().getTitle());
    }

    public void saveOldSong(String fileName, Data.Song lastSong) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " saveOldSong");
        if (fileName == null) {
            fileName = DEFAULT_FILE;
        }
        ShardPreUtils.getInstance(fileName).putStringValue(KEYOLDSONGTITLE, lastSong.getTitle());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYOLDSONGID, lastSong.getId());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYOLDSONGMID, lastSong.getMid());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYOLDSONGCOVERURI, lastSong.getAlbum().getCoverUri());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYOLDSONGALBUMTITLE, lastSong.getAlbum().getTitle());
        ShardPreUtils.getInstance(fileName).putLongValue(KEYOLDSONGALBUMID, lastSong.getAlbum().getId());
        ShardPreUtils.getInstance(fileName).putLongValue(KEYOLDSONGSINGERID, lastSong.getSinger().getId());
        ShardPreUtils.getInstance(fileName).putStringValue(KEYOLDSONGSINGERTITLE, lastSong.getSinger().getTitle());
    }

    public void saveOldList(String fileName, ArrayList<String> mlist) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " saveOldList");
        ShardPreUtils.getInstance(fileName).putOldList(KEYOLDLIST, mlist);
    }

    public void checkOldListSize(ArrayList<Data.Song> mSongList) {
        if (DEBUG) android.util.Log.i(QQMusicCMD.class.getSimpleName(),
                " checkOldListSize");
        if (mSongList != null && mSongList.size() != 0) {
            while (mSongList.size() > 2) {
                mSongList.remove(0);
            }
        }
    }
}