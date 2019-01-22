package com.qinggan.app.arielapp.minor.main.mui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.commonui.TopToBottomFinishLayout;
import com.qinggan.app.arielapp.minor.main.driving.adapter.PageFragmentAdapter;
import com.qinggan.app.arielapp.minor.main.driving.view.IntelligenceFragment;
import com.qinggan.app.arielapp.minor.main.driving.view.OrdinaryFragment;
import com.qinggan.app.arielapp.minor.main.driving.view.SimpleFragment;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;

import java.util.ArrayList;
import java.util.List;

/***
 * 驾驶模式
 * **/
public class DriviverFragment extends AbstractBaseFragment implements ViewPager.OnPageChangeListener {
    private View dvriverview;
    private ViewPager viewPager;
    private PageFragmentAdapter adapter=null;
    private List<Fragment> fragmentList=new ArrayList<Fragment>();
    private LocalStorageTools localStorageTools;
    private FragmentManager fragmentManager;
    private Context context;
    AbstractBaseFragment currentFragment = null;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        System.out.println("DriviverFragment=-="+fragmentManager.getFragments());
        context=getActivity();
        dvriverview = inflater.inflate(R.layout.frag_dvriver, container, false);
        localStorageTools = new LocalStorageTools(getActivity());//初始化本地储存

        viewPager=(ViewPager)dvriverview.findViewById(R.id.view_list);
        viewPager.setOnPageChangeListener(this);
        fragmentList.clear();
        OrdinaryFragment ordinaryFragment =new OrdinaryFragment();
        IntelligenceFragment intelligenceFragment =new IntelligenceFragment();
        SimpleFragment simpleFragment=new SimpleFragment();
//
        fragmentList.add(ordinaryFragment);
        fragmentList.add(intelligenceFragment);
        fragmentList.add(simpleFragment);

        adapter=new PageFragmentAdapter(fragmentManager,fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);//设置缓存页数
        //获取上一次选中的下标
        int index= localStorageTools.getInteger("pageIndex");
        if(index==-1){//默认值是-1
            index=1;
        }
        viewPager.setCurrentItem(index);//设置显示哪个位置的页面






        return dvriverview;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //将当前选中的下标保存下来
        localStorageTools.setInteger("pageIndex",position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }


    @Override
    public void init(IASRSession session) {

    }


}
