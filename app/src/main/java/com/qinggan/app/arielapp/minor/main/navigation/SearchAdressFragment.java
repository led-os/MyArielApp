package com.qinggan.app.arielapp.minor.main.navigation;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;

public class SearchAdressFragment extends AbstractBaseFragment implements View.OnClickListener{

    private View searchView;
    private ImageView close_tn;
    private ImageView mIvVoice;
    private LinearLayout mLlchangeMap;
    private FragmentManager fragmentManager;
    private LinearLayout mLlsearch;

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.close_tn:
                getFragmentManager().popBackStack();
                break;
            case R.id.iv_voice:
                Toast.makeText(getActivity(), "开始识别", Toast.LENGTH_SHORT).show();
                VoicePolicyManage.getInstance().record(true);
                break;
            case R.id.ll_changemap:
                fragmentManager.popBackStack();
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager,LocalFragmentManager.FragType.NAVIGATION,R.id.main_content_view);
                break;
            case R.id.ll_search:
                fragmentManager.popBackStack();
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager,LocalFragmentManager.FragType.POISEARCH,R.id.main_content_view);
        }
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager=getFragmentManager();
        searchView=inflater.inflate(R.layout.activity_search,container,false);
        close_tn=(ImageView)searchView.findViewById(R.id.close_tn);
        mIvVoice = searchView.findViewById(R.id.iv_voice);
        mLlchangeMap = searchView.findViewById(R.id.ll_changemap);
        mLlsearch = searchView.findViewById(R.id.ll_search);
        close_tn.setOnClickListener(this);
        mIvVoice.setOnClickListener(this);
        mLlchangeMap.setOnClickListener(this);
        mLlsearch.setOnClickListener(this);

        return searchView;
    }
}
