package com.qinggan.app.arielapp.ui.adpater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ui.nav.NavAddressBean;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.nav.NavPOIBean;

import java.util.List;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-10-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public class NavAddressAdapter extends RecyclerView.Adapter<NavAddressAdapter.NavAddressHodler> implements View.OnClickListener{
    private OnItemClickListener mItemClickListener;
    List<NavAddressBean> datas;

    public NavAddressAdapter(List<NavAddressBean> datas) {
        this.datas = datas;
    }

    @Override
    public NavAddressHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav_address, parent, false);
        NavAddressHodler holder = new NavAddressHodler(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(NavAddressHodler holder, int position) {
        holder.itemView.setTag(position);
        holder.mTvNavAddress.setText(datas.get(position).getmAddress());
        holder.mTvNavName.setText(datas.get(position).getmName());
        holder.mTvNavNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick((Integer) view.getTag());
        }
    }

    public static class NavAddressHodler extends RecyclerView.ViewHolder {

        TextView mTvNavAddress;
        TextView mTvNavName;
        TextView mTvNavNumber;

        public NavAddressHodler(View itemView) {
            super(itemView);
            mTvNavAddress = itemView.findViewById(R.id.tv_nav_address);
            mTvNavName = itemView.findViewById(R.id.tv_nav_name);
            mTvNavNumber = itemView.findViewById(R.id.tv_nav_number);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
