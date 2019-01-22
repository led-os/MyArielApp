package com.qinggan.app.arielapp.minor.main.mui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.commonui.EditClear;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
/****
 * 登录
 * ***/
public class LoginFragment extends AbstractBaseFragment implements View.OnClickListener {

    private final static String TAG = "login";
    private Context context;
    private View loginview;
    private TextView back_btn;
    private EditClear phone_txt;
    private EditClear yezhengma_txt;
    private FragmentManager fragmentManager;
    private Button login_btn;
    private LocalStorageTools localStorageTools;

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        context=getActivity();
        localStorageTools = new LocalStorageTools(context);
        loginview = inflater.inflate(R.layout.activity_login, container, false);
        back_btn=(TextView)loginview.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);

        phone_txt=(EditClear)loginview.findViewById(R.id.phone_txt);
        phone_txt.setHint("输入手机号");
        yezhengma_txt=(EditClear)loginview.findViewById(R.id.yezhengma_txt);
        yezhengma_txt.setHint("验证码");

        login_btn=(Button)loginview.findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        return loginview;

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                localStorageTools.setBoolean("isLogin",true);
                fragmentManager.popBackStack();
                break;
            case R.id.back_btn:

                fragmentManager.popBackStack();

                break;

            default:

                break;

        }

    }

}
