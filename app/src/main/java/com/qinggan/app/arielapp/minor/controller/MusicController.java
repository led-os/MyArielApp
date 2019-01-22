package com.qinggan.app.arielapp.minor.controller;

import com.tencent.qqmusic.third.api.contract.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 18-10-30.
 */

public class MusicController {

    private static MusicController instance = null;

    private MusicController(){}

    public static MusicController getInstance(){
        if (instance == null){
            synchronized (MusicController.class){
                instance = new MusicController();
            }
        }
        return instance;
    }

    private int defaultMode = -1;

    /**
     * 当前音乐列表类别
     */
    public static final int TOPPOPMODE = 0X1001;
    public static final int NEWSONGMODE = 0X1002;
    public static final int LOCALMODE = 0X1003;
    public static final int FAVOURITEMODE = 0X1004;
    public static final int SEARCHMODE = 0X1005;

    public int getDefaultMode() {
        return defaultMode;
    }

    public void setDefaultMode(int defaultMode) {
        this.defaultMode = defaultMode;
    }

    /**
     * 新歌榜歌曲列表
     */
    private ArrayList<Data.Song> newSongList = new ArrayList<>();
    /**
     * 本地歌曲列表
     */
    private ArrayList<Data.Song> localList = new ArrayList<>();
    /**
     * 流行巅峰榜歌曲列表
     */
    private ArrayList<Data.Song> topPopsList = new ArrayList<>();
    /**
     * 我喜欢歌曲列表
     */
    private ArrayList<Data.Song> favouriteList = new ArrayList<>();
    /**
     * 用户搜索歌曲列表
     */
    private ArrayList<Data.Song> searchList = new ArrayList<>();


    public ArrayList<Data.Song> getNewSongList() {
        return newSongList;
    }

    public void setNewSongList(ArrayList<Data.Song> newSongList) {
        this.newSongList = newSongList;
    }

    public ArrayList<Data.Song> getLocalList() {
        return localList;
    }

    public void setLocalList(ArrayList<Data.Song> localList) {
        this.localList = localList;
    }

    public ArrayList<Data.Song> getTopPopsList() {
        return topPopsList;
    }

    public void setTopPopsList(ArrayList<Data.Song> topPopsList) {
        this.topPopsList = topPopsList;
    }

    public ArrayList<Data.Song> getFavouriteList() {
        return favouriteList;
    }

    public void setFavouriteList(ArrayList<Data.Song> favouriteList) {
        this.favouriteList = favouriteList;
    }

    public ArrayList<Data.Song> getSearchList() {
        return searchList;
    }

    public void setSearchList(ArrayList<Data.Song> searchList) {
        this.searchList = searchList;
    }

    public List<Data.Song> getCurrentModeList(){
        switch (defaultMode){
            case TOPPOPMODE:
                return topPopsList;
            case NEWSONGMODE:
                return newSongList;
            case FAVOURITEMODE:
                return favouriteList;
            case LOCALMODE:
                return localList;
            case SEARCHMODE:
                return searchList;
        }
        return null;
    }
}
