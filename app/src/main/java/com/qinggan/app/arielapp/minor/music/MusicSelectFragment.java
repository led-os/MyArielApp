package com.qinggan.app.arielapp.minor.music;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;

/**
 * Created by yang
 * Time on 2018/11/7.
 * Function  音乐选择页面
 */
public class MusicSelectFragment extends AbstractBaseFragment {

    View mView;
    private Context mContext = ArielApplication.getApp();
    ImageView mImageView;
    ImageView mNextView;
    CheckBox mCheck;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_music_select, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNextView = mView.findViewById(R.id.delete_image);
        mCheck = mView.findViewById(R.id.check);
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true){
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }
}
