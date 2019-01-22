package com.qinggan.app.arielapp.ui.adpater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.nav.RestaurantBean;

import java.util.List;

public class VoiceRestaurantAdapter extends RecyclerView.Adapter<VoiceRestaurantAdapter.RestaurantViewHolder> {

    List<DcsBean> mDcsBeanList;

    public VoiceRestaurantAdapter(List<DcsBean> list) {
        this.mDcsBeanList = list;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_restaurant_item, parent, false);
        RestaurantViewHolder holder = new RestaurantViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, final int position) {
        RestaurantBean restaurantBean = (RestaurantBean) mDcsBeanList.get(position);
        holder.restaurantNumber.setText(String.valueOf(position + 1));
        holder.restaurantName.setText(restaurantBean.getName());
        holder.restaurantAddress.setText(restaurantBean.getAddress());
        holder.restaurantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDcsBeanList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout restaurantLayout;
        TextView restaurantNumber;
        TextView restaurantName;
        TextView restaurantAddress;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            restaurantLayout = itemView.findViewById(R.id.restaurant_item_root_layout);
            restaurantNumber = itemView.findViewById(R.id.restaurant_item_number);
            restaurantName = itemView.findViewById(R.id.restaurant_item_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_item_address);
        }
    }

    private OnItemClickListener mOnItemClickListener;//声明接口

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
