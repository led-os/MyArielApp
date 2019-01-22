package com.qinggan.app.arielapp.minor.main.navigation.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.navigation.bean.NavAddressInfoBean;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;

import java.util.List;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-10-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryHodler> {
    private OnClickListener mClickListener;
    private List<NavAddressInfoBean> datas;
    private Context mContext;
    private String mPresetType;

    public SearchHistoryAdapter(List<NavAddressInfoBean> datas) {
        this.datas = datas;
        mPresetType = "";
    }

    public void setPresetType(String type) {
        mPresetType = type;
        notifyDataSetChanged();
    }

    @Override
    public SearchHistoryHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_address, parent, false);
        SearchHistoryHodler holder = new SearchHistoryHodler(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final SearchHistoryHodler holder, int position) {
        if (datas.get(position).getmName().equals("") && datas.get(position).getmAddress().equals("")) {
            holder.mItem.setVisibility(View.GONE);
            holder.mclearTv.setVisibility(View.VISIBLE);
            holder.mclearTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(holder.getAdapterPosition());
                }
            });
            return;
        }
        holder.mItem.setVisibility(View.VISIBLE);
        holder.mTvNavAddress.setText(datas.get(position).getmAddress());
        holder.mTvNavName.setText(datas.get(position).getmName());
        if (datas.get(position).getType().equals("0")) {
            if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
                holder.mIvSearch.setBackgroundResource(R.drawable.navi_search_);
            }else{
                holder.mIvSearch.setBackgroundResource(R.mipmap.navi_icon_seicon);
            }
            holder.mIvSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(holder.getAdapterPosition());
                }
            });
        } else if (datas.get(position).getType().equals("1")) {
            if (mPresetType.equals(Constants.PRESET_HOME_TYPE)) {
                holder.mIvSearch.setBackgroundResource(R.drawable.navi_icon_home);
            } else if (mPresetType.equals(Constants.PRESET_COMPANY_TYPE)) {
                holder.mIvSearch.setBackgroundResource(R.drawable.navi_icon_companyicon);
            } else if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState()
                           == PhoneState.OUT_CAR_MODE){
//                holder.mIvSearch.setBackgroundResource(R.drawable.navi_buttun_preinstall);
                holder.mIvSearch.setBackgroundResource(R.drawable.navi_preset_destination_whitebg);
            }else {
                holder.mIvSearch.setBackgroundResource(R.drawable.navi_buttun_naviicon);
            }

            holder.mIvSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.gotoNavi(holder.getAdapterPosition(),!mPresetType.equals(""));
                }
            });

        }


        if (!TextUtils.isEmpty(datas.get(position).getmDistance())) {
            holder.mTotalDistance.setText(datas.get(position).getmDistance()
                    + mContext.getString(R.string.metre));
        } else {
            holder.mTotalDistance.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.deleteHis(holder.getAdapterPosition());
                return true;
            }
        });
        // add
        if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            holder.mTvNavAddress.setTextColor(mContext.getColor(R.color.gary_9d));
            holder.mTvNavName.setTextColor(mContext.getColor(R.color.black));
            holder.mTotalDistance.setTextColor(mContext.getColor(R.color.black));
        } else {
            holder.mTvNavAddress.setTextColor(mContext.getColor(R.color.white));
            holder.mTvNavAddress.setAlpha(1);
            holder.mTvNavName.setTextColor(mContext.getColor(R.color.white));
            holder.mTotalDistance.setTextColor(mContext.getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class SearchHistoryHodler extends RecyclerView.ViewHolder {
        TextView mTvNavAddress;
        TextView mTvNavName;
        TextView mTotalDistance;
        ImageView mIvSearch;
        TextView mclearTv;
        View mItem;

        public SearchHistoryHodler(View itemView) {
            super(itemView);
            mTvNavAddress = itemView.findViewById(R.id.tv_nav_address);
            mTvNavName = itemView.findViewById(R.id.tv_nav_name);
            mTotalDistance = itemView.findViewById(R.id.tv_total_distance);
            mIvSearch = itemView.findViewById(R.id.iv_navi_icon);
            mclearTv = itemView.findViewById(R.id.clear_all);
            mItem = itemView.findViewById(R.id.navi_list_item);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);

        void gotoNavi(int position,boolean preset);

        void deleteHis(int position);


    }

    public void setClickListener(OnClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }
}
