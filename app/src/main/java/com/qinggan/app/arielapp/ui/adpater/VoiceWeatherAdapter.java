package com.qinggan.app.arielapp.ui.adpater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ui.weather.WeatherHelper;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.common.WeatherForecastBean;

import java.util.List;

public class VoiceWeatherAdapter extends RecyclerView.Adapter<VoiceWeatherAdapter.WeatherViewHolder> {

    List<DcsBean> mDcsBeanList;

    public VoiceWeatherAdapter(List<DcsBean> list) {
        this.mDcsBeanList = list;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_weather_item, parent, false);
        WeatherViewHolder holder = new WeatherViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        WeatherForecastBean weatherForecastBean = (WeatherForecastBean)mDcsBeanList.get(position);

        String date = WeatherHelper.getSubDate(weatherForecastBean.getDate());
        if (position == 0) {
            date = "今天" +  "  (" + date + ")";
        } else if (position == 1) {
            date = "明天" + "  (" + date + ")";
        } else {
            date = WeatherHelper.getWeek(weatherForecastBean.getDay()) + "  (" + date + ")";
        }
        holder.weatherStatus.setText(date);
        holder.weatherIcon.setImageResource(WeatherHelper.getWeatherIcon(weatherForecastBean.getWeatherCondition()));
        holder.weatherTemp.setText(WeatherHelper.getDayWeatherText(weatherForecastBean.getLowTemperature().replace("℃", ""),
                weatherForecastBean.getHighTemperature().replace("℃", "")));
    }

    @Override
    public int getItemCount() {
        return mDcsBeanList.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView weatherStatus;
        ImageView weatherIcon;
        TextView weatherTemp;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            weatherStatus = itemView.findViewById(R.id.voice_weather_item_day);
            weatherIcon = itemView.findViewById(R.id.voice_weather_item_icon);
            weatherTemp = itemView.findViewById(R.id.voice_weather_item_temp);
        }
    }
}
