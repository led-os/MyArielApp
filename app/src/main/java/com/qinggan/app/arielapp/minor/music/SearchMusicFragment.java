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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.mui.adapter.PlayListAdpter;
import com.qinggan.app.arielapp.minor.music.adapter.SearchMusicAdpter;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.tencent.qqmusic.third.api.contract.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang
 * Time on 2018/11/7.
 * Function  搜音乐
 */
public class SearchMusicFragment extends AbstractBaseFragment implements IntegrationCore.MusicListener{

    View mView;
    private RecyclerView recyclerView_broad;
    private Context mContext = ArielApplication.getApp();
    SearchMusicAdpter searchMusicAdpter = new SearchMusicAdpter(mContext);
    List<Data.Song> playlist = new ArrayList<>();
    List<String> list1 = new ArrayList<>();
    ImageView mImageView;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search_music, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initView();
//        initData();
    }

    private void initView() {
        mImageView = mView.findViewById(R.id.delete_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
//                nextFragment();
            }
        });
        recyclerView_broad = mView.findViewById(R.id.recycle_broad);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_broad.setLayoutManager(linearLayoutManager);
        recyclerView_broad.setAdapter(searchMusicAdpter);
        IntegrationCore.getIntergrationCore(mContext).setMusicListener(this);
    }

    void nextFragment(){
        Toast.makeText(mContext, "切换到firstMusicFragment中", Toast.LENGTH_SHORT).show();
        //开启事务跳转
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        MusicSelectFragment firstMusicFragment = new MusicSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putString("productTitle","fragment参数");
        firstMusicFragment.setArguments(bundle);
//        transaction
//                .addToBackStack(null)  //将当前fragment加入到返回栈中
//                .replace(R.id.fragment_container,firstMusicFragment)
//                .show(firstMusicFragment)
//                .commit();

    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            searchMusicAdpter.setList(playlist);
            recyclerView_broad.setAdapter(searchMusicAdpter);
            searchMusicAdpter.notifyDataSetChanged();
        }
    };

    private void initData(ArrayList list) {
        Data.Song song;
        for(int i = 0;i<list.size();i++){
            song = (Data.Song)list.get(i);playlist.add(song);
        }
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onBack(ArrayList list,boolean state) {
        initData(list);
    }

    @Override
    public void songChange() {

    }

    @Override
    public void onBackFolder(ArrayList list) {

    }


    @Override
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
