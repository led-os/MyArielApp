package com.qinggan.app.arielapp.minor.push;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.push.bean.PushMessageBodyBean;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;


/**
 * Created by jianhou
 * Time on 2018/11/8.
 * Function  推送消息
 */
public class PushMessageFragment extends AbstractBaseFragment implements View.OnClickListener{
    private static final String TAG = "PushMessageFragment";
    private View mView;
    private Button mAskBtn;
    private Button mIgnoreBtn;
    private TextView mTvPushBody;
    private PushMessageBodyBean mPushMessageBodyBean;
    private ImageView mIvDelete;
    private FragmentManager fragmentManager;

    public static PushMessageFragment newInstance(PushMessageBodyBean pushMessageBodyBean){
          Bundle bundle = new Bundle();
          bundle.putParcelable("push", pushMessageBodyBean);
          PushMessageFragment pushMessageFragment = new PushMessageFragment();
          pushMessageFragment.setArguments(bundle);
          return pushMessageFragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_push_message, container, false);

        mTvPushBody = mView.findViewById(R.id.tv_message_body);

        mPushMessageBodyBean = getArguments().getParcelable("push");

        Log.i(TAG, "pushMessageBody = " + mPushMessageBodyBean.getPushBody());
        mTvPushBody.setText(mPushMessageBodyBean.getPushBody());
        fragmentManager = getFragmentManager();
        return mView;
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

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
        mAskBtn = mView.findViewById(R.id.ask_button);
        mIgnoreBtn = mView.findViewById(R.id.ignore_button);
        mIvDelete = mView.findViewById(R.id.iv_delete);

        mAskBtn.setOnClickListener(this);
        mIgnoreBtn.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ask_button:
                //处理用户请求
                break;
            case R.id.ignore_button:
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction
                   .addToBackStack(null)  //将当前fragment加入到返回栈中
                   .commit();
                break;
            case R.id.iv_delete:
                fragmentManager.popBackStack();
                break;
        }
    }
}



