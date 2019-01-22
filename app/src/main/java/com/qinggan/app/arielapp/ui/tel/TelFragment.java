package com.qinggan.app.arielapp.ui.tel;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.adpater.PhoneAdapter;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.tel.TelContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <描述>
 *
 * @author   NAME:yanguozhu
 * @version [版本号, 18-11-2]
 * @see [相关类/方法]
 * @since [V1]
 */
public class TelFragment extends AbstractBaseFragment {
    RecyclerView recyclerView;

    public static TelFragment newInstance(ArrayList<DcsBean> data, int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putParcelableArrayList("list", data);
        TelFragment fragment = new TelFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

//    BaseSceneWord sceneWord;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.frag_tel, container, false);
        recyclerView = (RecyclerView) rootView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<DcsBean> dcsBeans = getArguments().getParcelableArrayList("list");
        int size = dcsBeans.size();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TelContactBean number = (TelContactBean) dcsBeans.get(i);
            if (TextUtils.isEmpty(number.getContactName())) {
                strings.add(number.getPhoneNumber());
            } else {
                strings.add(number.getContactName());
            }
        }
        recyclerView.setAdapter(new PhoneAdapter(strings));

//        sceneWord = SceneWordFactory.createSceneWord(getArguments().getInt("type", 0));
//        if (null != sceneWord) {
//            Map<String, String> special = new HashMap<>();
//            special.put(NAV_CANCEL, "取消导航");
//            sceneWord.onCreateSpecialWord(special);
//            sceneWord.onCreateListWord(strings);
//        }
        return rootView;
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);

    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }
}
