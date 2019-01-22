package com.qinggan.app.arielapp.minor.music;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;


import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.mui.adapter.PlayListAdpter;
import com.qinggan.app.arielapp.minor.scenario.ClickViewInterface;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.tencent.qqmusic.third.api.contract.Data;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.baidu.turbonet.base.ThreadUtils.runOnUiThread;
import static com.qinggan.app.arielapp.minor.core.IntegrationCore.getIntergrationCore;

/**
 * Created by yang
 * Time on 2018/11/7.
 * Function  音乐播放页面
 */
public class PlayListFragment extends AbstractBaseFragment implements IntegrationCore.MusicListener,ClickViewInterface{

    View mView;
    private RecyclerView recyclerView_broad;
    private Context mContext = ArielApplication.getApp();
    PlayListAdpter playListAdpter = new PlayListAdpter(mContext);
    List<Data.Song> playlist = new ArrayList<>();
    ImageView mImageView;
    IntegrationCore integrationCore = null;
    ArrayList mList = new ArrayList<>();
    String currentSongId;
    ArrayList initList;
    List<Data.Song> cachelist = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_play_list, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initView();
    }

    private void initView() {
        integrationCore = getIntergrationCore(mContext);
        mImageView = mView.findViewById(R.id.delete_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        recyclerView_broad = mView.findViewById(R.id.recycle_broad);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_broad.setLayoutManager(linearLayoutManager);
        recyclerView_broad.setAdapter(playListAdpter);
        getIntergrationCore(mContext).setMusicListener(this);
        playListAdpter.setmClickViewInterface(this);
        initData();
    }

    private void initData() {
        cachelist = integrationCore.getPlayListCache();
        if (null != cachelist && cachelist.size() != 0){
            initCache((ArrayList) cachelist);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    playListAdpter.setList(playlist);
                    recyclerView_broad.setAdapter(playListAdpter);
                    playListAdpter.notifyDataSetChanged();
                    break;
            }

        }
    };

    private void initData(ArrayList list) {
        if (list.size() != 0 && null != list){
            Data.Song song;
            for(int i = 0;i<list.size();i++){
                song = (Data.Song) list.get(i);
                playlist.add(song);
                mList.add(song.getId());
            }
        }
        mHandler.sendEmptyMessage(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusReceive(String key) {
        if (key.equals("onRestart")){
            initPosition(initList);
        }
    }

    @Override
    public void onBack(ArrayList list,boolean state) {
        if (null == cachelist || cachelist.size() == 0){
            initCache(list);
        }
    }

    private void initCache(ArrayList list) {
        initData(list);
        currentSongId = integrationCore.getCurrentSong();
        initList =list;
        initPosition(list);
    }

    private void initPosition(ArrayList list){
        if (list.size() != 0 && null != list){
            Data.Song song;
            for(int i = 0;i<list.size();i++){
                song = (Data.Song) list.get(i);
                if (currentSongId.equals(song.getId())){
                    playListAdpter.setInitPosition(i);
                    playListAdpter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void OnClickPositionListener(View view, int... position) {
          switch (view.getId()){
              case R.id.paly_item:
                  if (mList.size() != 0 && null != mList){
                      integrationCore.playSongIdAtIndex(mList,position[0]);
                  }
                  if (position[1] == 1){
                      playListAdpter.selectPosition(position[0],true);
                      playListAdpter.notifyDataSetChanged();
                  }
          }
    }

    @Override
    public void OnClickContentListener(View view, String... content) {

    }

    @Override
    public void songChange() {

    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackFolder(ArrayList list) {

    }

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
}
