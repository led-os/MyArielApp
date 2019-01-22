package com.qinggan.app.arielapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.mui.DriviverFragment;
import com.qinggan.app.arielapp.minor.main.mui.LoginFragment;
import com.qinggan.app.arielapp.minor.main.mui.MainFragment;
import com.qinggan.app.arielapp.minor.main.mui.RootFragment;
import com.qinggan.app.arielapp.minor.main.navigation.DestinatorFragment;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.main.navigation.SearchAdressFragment;
import com.qinggan.app.arielapp.minor.music.MusicFragment;
import com.qinggan.app.arielapp.minor.music.MusicSelectFragment;
import com.qinggan.app.arielapp.minor.music.PlayListFragment;
import com.qinggan.app.arielapp.minor.music.SearchMusicFragment;
import com.qinggan.app.arielapp.minor.music.VolumeFragment;
import com.qinggan.app.arielapp.minor.phone.bean.ContactsInfo;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneContactsActivity;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.minor.push.PushMessageFragment;
import com.qinggan.app.arielapp.minor.push.bean.PushMessageBodyBean;
import com.qinggan.app.arielapp.minor.radio.FMFragment;
import com.qinggan.app.arielapp.minor.scenario.ProfilesFragment;
import com.qinggan.app.arielapp.minor.wechat.WeChatMessageFrafment;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.nav.NewsFragment;
import com.qinggan.app.arielapp.ui.nav.RestaurantDetailFragment;
import com.qinggan.app.arielapp.ui.nav.ScenicDetailFragment;
import com.qinggan.app.arielapp.ui.nav.VoiceNavsFragment;
import com.qinggan.app.arielapp.ui.stock.StockFragment;
import com.qinggan.app.arielapp.ui.weather.WeatherFragment;
import com.qinggan.app.voiceapi.DataTypeConstant;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.bean.common.VoiceInputBean;
import com.qinggan.app.voiceapi.bean.nav.NavConditionBean;
import com.qinggan.app.voiceapi.bean.tel.TelContactBean;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;

import java.util.ArrayList;

import static com.qinggan.app.voiceapi.DataTypeConstant.ETA_TYPE;
import static com.qinggan.app.voiceapi.DataTypeConstant.PHONE_CALL_BY_NAME_LIST;
import static com.qinggan.app.voiceapi.DataTypeConstant.PHONE_CALL_BY_NAME_ONE;
import static com.qinggan.app.voiceapi.DataTypeConstant.PHONE_CALL_BY_NUMBER_TYPE;
import static com.qinggan.app.voiceapi.DataTypeConstant.POI_SEARCH_TYPE;
import static com.qinggan.app.voiceapi.DataTypeConstant.POI_TYPE;
import static com.qinggan.app.voiceapi.DataTypeConstant.ROUTE_SEARCH_TYPE;
import static com.qinggan.app.voiceapi.DataTypeConstant.SELECT_CALLEE_TYPE_LIST;
import static com.qinggan.app.voiceapi.DataTypeConstant.SELECT_CALLEE_TYPE_ONE;
import static com.qinggan.app.voiceapi.DataTypeConstant.TRAFFIC_CONDITION_TYPE;
import static com.qinggan.app.voiceapi.DataTypeConstant.UNIVERSAL_TYPE;
import static com.qinggan.app.voiceapi.DataTypeConstant.VOICE_INPUT_TYPE;


public class LocalFragmentManager {

    private final static String TAG = "LocalFragmentManager";

    private static LocalFragmentManager mInstance;

    public static LocalFragmentManager getInstance() {
        if (mInstance == null) {
            synchronized (LocalFragmentManager.class) {
                if (mInstance == null)
                    mInstance = new LocalFragmentManager();
            }
        }
        return mInstance;
    }

    private LocalFragmentManager() {

    }

    public AbstractBaseFragment createMainFragment(FragmentManager fragmentManager) {

        AbstractBaseFragment fragment = new MainFragment();
        fragmentManager.beginTransaction().replace(R.id.main_content_view, fragment, "main").addToBackStack(null).commitAllowingStateLoss();
        return fragment;
    }

    public AbstractBaseFragment createFragment(FragmentManager fragmentManager, final DcsDataWrapper wrapper, final IASRSession session, Context context) {
        AbstractBaseFragment fragment = null;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String tag = null;
        switch (wrapper.getType()) {
            case VOICE_INPUT_TYPE:
                if (wrapper != null && wrapper.getDcsBean() != null) {
                    long time = ((VoiceInputBean) wrapper.getDcsBean()).getTimeoutInMilliseconds();
                    Log.d(TAG, "timeoutInMilliseconds:" + time);
                }
                break;
            case PHONE_CALL_BY_NAME_ONE:
            case SELECT_CALLEE_TYPE_ONE:
            case PHONE_CALL_BY_NUMBER_TYPE:
                //直接拨打电话
                String no = ((TelContactBean) wrapper.getDcsBean()).getPhoneNumber();
                String name = ((TelContactBean) wrapper.getDcsBean()).getDisplayName();
                CallUtils.startCallByPhoneNumber(ArielApplication.getApp(), no);
                CallUtils.startInCallUI(ArielApplication.getApp(), no, name);
                break;
            case PHONE_CALL_BY_NAME_LIST:
            case SELECT_CALLEE_TYPE_LIST:
                //选择
                ArrayList<DcsBean> data = wrapper.getDcsBeanArray();
                if (null != data && !data.isEmpty()) {
                    ArrayList<ContactsInfo> phoneData = new ArrayList<>();
                    for (DcsBean bean : data) {
                        TelContactBean telContactBean = (TelContactBean) bean;
                        ContactsInfo info = new ContactsInfo();
                        info.setPhoneNum(telContactBean.getPhoneNumber());
                        info.setDesplayName(telContactBean.getDisplayName());
                        phoneData.add(info);
                    }
//                    fragment = ContactsInfoFragment.newInstance(phoneData);
                    Intent intent = new Intent(ArielApplication.getApp(), PhoneContactsActivity.class);
                    Bundle args = new Bundle();
                    args.putParcelableArrayList("list", phoneData);
                    intent.putExtra("voiceSearchData", args);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ArielApplication.getApp().startActivity(intent);
                }
                break;
            case TRAFFIC_CONDITION_TYPE:
                NavConditionBean poiBeans = (NavConditionBean) wrapper.getDcsBean();
                String destination = poiBeans.getDestination();
                if ("DA_CONST_HOME".equals(destination)) {
                    //回家的交通情况
                } else if ("DA_CONST_COMPANY".equals(destination)) {
                    //去公司的交通情况
                } else
//                    fragment = NavAddressListFragment.newInstance(wrapper,NluResultManager.poiBeans);
                    startNavigationActivity(wrapper, NluResultManager.poiBeans, context);
                break;
            case ROUTE_SEARCH_TYPE:
                Log.d("VoicePolicyManage","ROUTE_SEARCH_TYPE");
                NavConditionBean poiBeans1 = (NavConditionBean) wrapper.getDcsBean();
                if ("DA_CONST_HOME".equals(poiBeans1.getDestination()) && TextUtils.isEmpty(poiBeans1.getOrigin())) {
                    //导航回家
                } else if ("DA_CONST_COMPANY".equals(poiBeans1.getDestination()) && TextUtils.isEmpty(poiBeans1.getOrigin())) {
                    //导航去公司
                } else
//                    fragment = NavAddressListFragment.newInstance(wrapper,NluResultManager.poiBeans);
                    startNavigationActivity(wrapper, NluResultManager.poiBeans, context);
                break;
            case UNIVERSAL_TYPE:
                break;
            case POI_SEARCH_TYPE:
            case ETA_TYPE:
//                fragment = NavAddressListFragment.newInstance(wrapper,NluResultManager.poiBeans);
                startNavigationActivity(wrapper, NluResultManager.poiBeans, context);
                break;
            case DataTypeConstant.SEND_WECHAT_BY_NAME:
                fragment = new WeChatMessageFrafment();
                break;

        }
        if (fragment != null)
            transaction.replace(R.id.main_content_view, fragment, tag).addToBackStack(null).commitAllowingStateLoss();

        return fragment;
    }

    private void startNavigationActivity(DcsDataWrapper wrapper, ArrayList<DcsBean> poiBeans, Context context) {
        Log.i(TAG, "NAVI startNavigationActivity ");
        Intent intent = new Intent(ArielApplication.getApp(), NavigationActivity.class);
        intent.putExtra("wrapper", wrapper);
        intent.putParcelableArrayListExtra("address", poiBeans);
        context.startActivity(intent);
    }

    public AbstractBaseFragment createDcsFragment(FragmentManager fragmentManager, final DcsDataWrapper wrapper, final IASRSession session) {
        AbstractBaseFragment fragment = null;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String tag = null;
        switch (wrapper.getType()) {
            case DataTypeConstant.WEATHER_TYPE:
                tag = "weather";
                fragment = new WeatherFragment();
                fragment.init(session);
                break;
            case DataTypeConstant.STOCK_TYPE:
                //股票
                tag = "stock";
                fragment = new StockFragment();
                fragment.init(session);
                break;
            case DataTypeConstant.RESTAURANT_TYPE:
            case DataTypeConstant.SCENIC_TYPE:
                tag = "voicenavs";
                fragment = new VoiceNavsFragment();
                fragment.init(session);
                break;
            case DataTypeConstant.SCENIC_DETAIL_TYPE:
                tag = "scenic_detail";
                fragment = new ScenicDetailFragment();
                fragment.init(session);
                break;
            case DataTypeConstant.NEWS_PLAY_INFO_TYPE:
                //音乐
                tag = "news";
                fragment = (NewsFragment)fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new NewsFragment();
                    fragment.init(session);
                } else {
                    session.handleASRFeedback(wrapper);
                    fragment.init(session);
                }
                break;
        }
        if (fragment != null)
            transaction.replace(R.id.voice_dcs_content, fragment, tag).addToBackStack(null).commitAllowingStateLoss();

        return fragment;
    }

    public AbstractBaseFragment crateDcsSubFragment(FragmentManager fragmentManager, AbstractBaseFragment currentFrag, FragType type, final IFragmentStatusListener mFragmentStatusListener) {

        AbstractBaseFragment fragment = (AbstractBaseFragment) fragmentManager.findFragmentByTag(type.getTag());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment == null) {
            switch (type) {
                case RESTAURANT_DETAIL:
                    fragment = new RestaurantDetailFragment();
                    break;
                case SCENIC_DETAIL:
                    fragment = new ScenicDetailFragment();
                    break;
                case MEDIA_PLAY:
                    //媒体播放界面
//                    fragment = new MediaPlayFragment();
                    break;
                case MEDIA_STYLE:
                    //媒体分类界面
//                    fragment = new MusicStyleFragment();
                    break;
            }

            transaction.add(R.id.voice_dcs_content, fragment, type.getTag());
            fragment.setLoadedListener(new IFragmentStatusListener() {
                @Override
                public void onLoaded() {
                    if (mFragmentStatusListener != null)
                        mFragmentStatusListener.onLoaded();
                }
            });

        }
        if (currentFrag != null) {
            transaction.hide(currentFrag);
        }
        transaction.addToBackStack(null).commitAllowingStateLoss();

        return fragment;
    }

    public AbstractBaseFragment showSubFragment(FragmentManager fragmentManager, FragType type, int contain_id, PushMessageBodyBean pushMessageBodyBean) {
        AbstractBaseFragment fragment = (AbstractBaseFragment) fragmentManager.findFragmentByTag(type.getTag());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment == null) {
            switch (type) {
                case PUSHMESSAGE:
                    fragment = PushMessageFragment.newInstance(pushMessageBodyBean);
                    break;
            }
        }

        transaction.replace(contain_id, fragment);
        transaction.show(fragment);
        transaction.attach(fragment);
        transaction.addToBackStack(null).commitAllowingStateLoss();
        return fragment;
    }

    public AbstractBaseFragment showSubFragment(FragmentManager fragmentManager, FragType type, int contain_id) {

        AbstractBaseFragment fragment = (AbstractBaseFragment) fragmentManager.findFragmentByTag(type.getTag());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment == null) {
            switch (type) {
                case MAIN:
                    //主界面
                    fragment = new MainFragment();
                    break;
                case LOGIN:
                    //登录界面
                    fragment = new LoginFragment();
                    break;
                case DRIVING:

                    //驾驶模式
                    fragment = new DriviverFragment();
                    transaction.setCustomAnimations(R.anim.activity_open, R.anim.activity_close);

                    break;
                case PHONE:
                    //拨打电话
//                    fragment = new PhoneMainFragment();
                    break;
                case CONTACTS:
                    //联系人
//                    fragment = new ContactsInfoFragment();
                    break;
                case MUSIC:
                    //音乐播放
                    fragment = new MusicFragment();
                    break;
                case VOLUME:
                    //声音控制界面
                    fragment = new VolumeFragment();
                    break;

                case QINGJING:
                    //情景模式
                    fragment = new ProfilesFragment();
                    break;
                case SHOUYINJI:
                    //收音机
                    fragment = new FMFragment();
                    break;
                case MUSICLIST:
                    //音乐列表
                    fragment = new PlayListFragment();
                    break;
                case MUSICSELECT:
                    //音乐播放器选择
                    fragment = new MusicSelectFragment();
                    break;
                case NAVIGATION:
                    //map选择
                    fragment = new DestinatorFragment();
                    break;
                case SEARCHADRESS:
                    //map选择
                    fragment = new SearchAdressFragment();
                    break;
                case ROOT:
                    //
                    fragment = new RootFragment();

                    break;
                case WECHATSENDMSG:
                    fragment = new WeChatMessageFrafment();
                    break;
                case SEARCHMUSIC:
                    fragment = new SearchMusicFragment();
                    break;
                case POISEARCH:
//                    fragment = new NavPoiSearchFragment();
                    break;
                case OILPOISEARCH:
//                    fragment = NavPoiSearchFragment.newInstance(true);
                    break;
            }
        }

        transaction.replace(contain_id, fragment);
        transaction.show(fragment);
        transaction.attach(fragment);
        transaction.addToBackStack(null).commitAllowingStateLoss();
        return fragment;
    }

    public enum FragType {
        RESTAURANT_DETAIL {
            @Override
            public String getTag() {
                return "restaurant_detail";
            }
        },
        SCENIC_DETAIL {
            @Override
            public String getTag() {
                return "scenic_detail";
            }
        },
        MEDIA_PLAY {
            @Override
            public String getTag() {
                return "media_play";
            }
        },
        MEDIA_STYLE {
            @Override
            public String getTag() {
                return "media_style";
            }
        },
        MAIN {
            @Override
            public String getTag() {
                return "main";
            }
        },
        DRIVING {
            @Override
            public String getTag() {
                return "driving";
            }
        },
        LOGIN {
            @Override
            public String getTag() {
                return "login";
            }
        },
        PHONE {
            @Override
            public String getTag() {
                return "phone";
            }
        },
        CONTACTS {
            @Override
            public String getTag() {
                return "contacts";
            }
        },
        MUSIC {
            @Override
            public String getTag() {
                return "music";
            }
        },
        VOLUME {
            @Override
            public String getTag() {
                return "music";
            }
        },
        MUSICLIST {
            @Override
            public String getTag() {
                return "musiclist";
            }
        },
        QINGJING {
            @Override
            public String getTag() {
                return "qingjing";
            }
        },
        SHOUYINJI {
            @Override
            public String getTag() {
                return "shouyinji";
            }
        },
        MUSICSELECT {
            @Override
            public String getTag() {
                return "musicselect";
            }
        },
        NAVIGATION {
            @Override
            public String getTag() {
                return "navigation";
            }
        },
        SEARCHADRESS {
            @Override
            public String getTag() {
                return "searchadress";
            }
        },
        WECHATSENDMSG {
            @Override
            public String getTag() {
                return "wechat_send_msg";
            }
        },
        SEARCHMUSIC {
            @Override
            public String getTag() {
                return "search_music";
            }
        },
        ROOT {
            @Override
            public String getTag() {
                return "root";
            }
        },
        POISEARCH {
            @Override
            public String getTag() {
                return "poisearch";
            }
        },
        PUSHMESSAGE {
            @Override
            public String getTag() {
                return "pushmessage";
            }
        },
        OILPOISEARCH {
            @Override
            public String getTag() {
                return "oilpoisearch";
            }
        };

        abstract public String getTag();
    }

}