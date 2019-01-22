package com.qinggan.app.arielapp.ui.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.iview.IWeatherView;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.session.weather.QueryWeatherSession;
import com.qinggan.app.arielapp.ui.DividerItemDecoration;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.ui.adpater.VoiceWeatherAdapter;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.common.WeatherBriefBean;
import com.qinggan.app.voiceapi.bean.common.WeatherForecastBean;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;
import com.qinggan.qinglink.bean.UIControlElementItem;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends UIControlBaseFragment implements IWeatherView {
    private static final String TAG = "WeatherFragment";
    private IFragmentStatusListener mFragmentStatusListener;
    private LinearLayout mWeatherLayout;
    private RecyclerView mRecyclerView;
    private VoiceWeatherAdapter mWeatherAdapter;
    private LinearLayoutManager mLayoutManager;

    ArrayList<UIControlElementItem> elementItems = new ArrayList<>();
    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;

    @Override
    public void onShowDaysWeather(List<DcsBean> dcsBeans) {
        Log.i(TAG, "onShowDaysWeather");
        mRecyclerView = mWeatherLayout.findViewById(R.id.voice_weather_recyclerview);
        mWeatherAdapter = new VoiceWeatherAdapter(dcsBeans);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWeatherAdapter);
    }

    @Override
    public void onShowTodayWeather(WeatherBriefBean weatherBriefBean, WeatherForecastBean weatherForecastBean) {
        ImageView currentIcon = mWeatherLayout.findViewById(R.id.voice_weather_icon);
        TextView currentCity = mWeatherLayout.findViewById(R.id.voice_weather_city);
        TextView currentTemp = mWeatherLayout.findViewById(R.id.voice_weather_temp);
        TextView currentStatus = mWeatherLayout.findViewById(R.id.voice_weather_status);
        TextView currentWind = mWeatherLayout.findViewById(R.id.voice_weather_wind);
        TextView currentPM25 = mWeatherLayout.findViewById(R.id.voice_weather_pm25);

        currentIcon.setImageResource(WeatherHelper.getWeatherIcon(weatherForecastBean.getWeatherCondition()));
        currentCity.setText(weatherBriefBean.getCity());
        currentTemp.setText(weatherBriefBean.getTemperature());
        String status = weatherForecastBean.getWeatherCondition();
        status = status + " " + WeatherHelper.getDayWeatherText(weatherForecastBean.getLowTemperature().replace(getString(R.string.weather_temp), ""),
                weatherForecastBean.getHighTemperature().replace(getString(R.string.weather_temp), ""));
        currentStatus.setText(status);
        currentWind.setText(weatherForecastBean.getWindCondition());
        currentPM25.setText(weatherBriefBean.getAirQuality() + " " + weatherBriefBean.getPM25());
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {
        mFragmentStatusListener = fragmentStatus;
    }

    @Override
    public void init(IASRSession session) {
        ((QueryWeatherSession) session).registerOnShowListener(this);
    }

    @Nullable
    @Override
    public View inflaterView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.voice_weather_frag, container, false);
        mWeatherLayout = (LinearLayout) root.findViewById(R.id.voice_weather_root_layout);
        View closeImage = mWeatherLayout.findViewById(R.id.voice_weather_close);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        if (mFragmentStatusListener != null) {
            mFragmentStatusListener.onLoaded();
        }

        mHotwordManager = HotwordManager.getInstance(getContext(), new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                if (b && !isOnpause) {
                    addWakeupElements();
                } else {
                    if (null != mHotwordManager) {
                        mHotwordManager.clearElementUCWords("voice_weather");
                    }
                }
            }
        });


        return root;
    }

    private void addWakeupElements() {
        Log.d(TAG,"WeatherFragment addWakeupElements mHotwordManager ： " + mHotwordManager);
        if (mHotwordManager == null) {
            return;
        }

        elementItems.clear();

        Log.d(TAG,"WeatherFragment onItemSelected add");
        com.qinggan.qinglink.bean.UIControlElementItem backTo = new com.qinggan.qinglink.bean.UIControlElementItem();
        backTo.setWord(getString(R.string.back_to));
        backTo.setIdentify(ConstantNavUc.NAV_BACK_TO);
        elementItems.add(backTo);

        mHotwordManager.setElementUCWords("voice_weather", elementItems);
        mHotwordManager.registerListener("voice_weather", new HotwordListener() {
            @Override
            public void onItemSelected(String identify) {
                Log.d(TAG,"WeatherFragment onItemSelected identify ： " + identify);

                if (ConstantNavUc.NAV_BACK_TO.equals(identify)) {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onSwitchPage(int i) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnpause = false;
        addWakeupElements();
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnpause = true;
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords("voice_weather");
        }
    }

}
