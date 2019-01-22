package com.qinggan.app.arielapp.minor.core;

import com.qinggan.app.arielapp.minor.integration.QQMusicCMD;
import com.tencent.qqmusic.third.api.contract.Data;
import com.tencent.qqmusic.third.api.contract.IQQMusicApi;
import com.tencent.qqmusic.third.api.contract.IQQMusicApiCallback;
import com.tencent.qqmusic.third.api.contract.IQQMusicApiEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 18-11-9.
 */

public interface MusicInterface {
    void searchMusicByKey(String key);
    void setMusicListener(IntegrationCore.MusicListener listener);
    void clearMusicListener(IntegrationCore.MusicListener listener);

    //richard.cheng add begin
    /*get info*/
    int getPlaybackState();
    Data.Song getCurrentSong();
    long getCurrTime();
    int getPlayMode();
    long getTotalTime();
    int getVersion();
    /*control*/
    boolean loginQQMusic();
    boolean openQQMusic();
    int pauseMusic();
    int playMusic();
    int resumeMusic();
    int skipToNext();
    int skipToPrevious();
    int stopMusic();
    void setPlayMode(int playMode);
    void search(String keyword, int searchType, boolean firstPage);
    int adjustVolume(int volume);
    void adjustVolumeF(double perc);
    /*ops*/
    void addToFavouriteAlias(boolean isPath,ArrayList<String> favList);
    String getFavouriteFolderId();
    void isFavouriteAlias(boolean isPath,ArrayList<String> list);
    void removeFromFavouriteAlias(boolean isPath,ArrayList<String> list);
    void getListAlias(int mode,String folderId, int folderType, int page);
    void playSongAlias(int mode,ArrayList<String> songIdList, int index);
    void registerEventListener(ArrayList<String> eventList);
    void unregisterEventListener(ArrayList<String> eventList);
    boolean isServiceConncted();
    void bindQQMusicService();
    void setMusicConn(boolean conn);
    ArrayList<Data.Song> getPlayListCache();
    void playSongIdAtIndex(ArrayList songIdList, int index);
    void playSongMidAtIndex(ArrayList songIdList, int index);
    ArrayList<String> getPlayIDList();
    Data.Song getPlaySongAtIndex(int index);
    Data.Song getPlaySongByID(String id);
    ArrayList<Data.Song> getSearchList();
    ArrayList<Data.FolderInfo> getFolderList(int type);
    Data.FolderInfo getFolderByIndex(int type,int index);
    Data.FolderInfo getFolderByTitle(String folderTitle);
    Data.FolderInfo getFolderById(String folderId);
    void setPre(boolean flag);
    Data.Song getLastSong();
    Data.Song getOldSong();
    void getKnownFolderByTitle(String songName);
    void playSongLocalPath(ArrayList<String> pathList);
    //richard.cheng add end

    boolean checkAudioPolicy();
    void addQQServiceStatusListener(QQMusicCMD.OnQQServiceStatusListener listener);
}