package com.qinggan.app.arielapp.minor.main.navigation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;

public class DestinatorFragment extends AbstractBaseFragment {


    private View destinatorview;
    private LinearLayout baidu_map;
    private ImageView finsh_btn;
    private Context mContext;
    private FragmentManager fragmentManager;
    IntegrationCore integrationCore = null;
    private LocalStorageTools localStorageTools;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager=getFragmentManager();
        mContext = getActivity();
        localStorageTools = new LocalStorageTools(mContext);
        destinatorview=inflater.inflate(R.layout.activity_destinator,container,false);
        integrationCore = IntegrationCore.getIntergrationCore(mContext);
        baidu_map=(LinearLayout)destinatorview.findViewById(R.id.baidu_map);
        baidu_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrationCore.changeStage(StageController.Stage.MAIN_IN_CAR);
                fragmentManager.popBackStack();
                localStorageTools.setBoolean("isSelectMap", true);
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager,LocalFragmentManager.FragType.SEARCHADRESS,R.id.main_content_view);
            }
        });
        finsh_btn=(ImageView)destinatorview.findViewById(R.id.finsh_btn);
        finsh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrationCore.changeStage(StageController.Stage.MAIN_IN_CAR);
                fragmentManager.popBackStack();
            }
        });

        return destinatorview;
    }
    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }
}
