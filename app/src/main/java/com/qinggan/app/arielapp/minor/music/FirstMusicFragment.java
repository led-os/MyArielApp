package com.qinggan.app.arielapp.minor.music;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.main.mui.adapter.MusicListAdpter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang
 * Time on 2018/11/8.
 * Function  音乐首页
 */
public class FirstMusicFragment extends Fragment {

    View mView;
    private RecyclerView recyclerView_music;
    private Context mContext = ArielApplication.getApp();
    MusicListAdpter musicListAdpter = new MusicListAdpter(mContext);
    List<String> list1 = new ArrayList<>();

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_music_list, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        recyclerView_music = mView.findViewById(R.id.recycle_music);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_music.setLayoutManager(linearLayoutManager);
    }

    private void initData() {
        for (int i = 1; i < 10; i++) {
            list1.add("快速降温");
        }
        musicListAdpter.setList(list1);
        recyclerView_music.setAdapter(musicListAdpter);
    }

}

