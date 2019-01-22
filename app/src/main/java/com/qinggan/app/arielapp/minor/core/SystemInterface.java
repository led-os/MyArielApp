package com.qinggan.app.arielapp.minor.core;

import android.content.Context;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by brian on 18-10-30.
 */

public interface SystemInterface {
    void rejectCall();
    void adjustSystemVolume(int volume);
    void adjustSystemVolumeF(double perc);
    void adjustMusicVolume(int volume);
    void adjustMusicVolumeF(double perc);
    int getActiveStreamType(int type);
    void setAllMute();
    boolean isMute(int streamType);
    void setMusicMute();
    void setSpeStreamMute(int streamType);

    boolean judgeIsAM();
    boolean isSomeAppInstalled(String packageName, Context context);

    ArrayList<String> getLocalSongList(int listType);
    void backToHomeActivity();

    boolean isSomeAppOnFront(Context context, String packageName);
    void playWechatSuccessSendSound(int soundRsId);
}
