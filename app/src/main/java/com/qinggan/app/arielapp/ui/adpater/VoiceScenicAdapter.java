package com.qinggan.app.arielapp.ui.adpater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.nav.ScenicBean;

import java.util.List;

public class VoiceScenicAdapter extends RecyclerView.Adapter<VoiceScenicAdapter.ScenicViewHolder> {

    List<DcsBean> mDcsBeanList;

    public VoiceScenicAdapter(List<DcsBean> list) {
        this.mDcsBeanList = list;
    }

    @Override
    public ScenicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_scenic_item, parent, false);
        ScenicViewHolder holder = new ScenicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ScenicViewHolder holder, final int position) {
        ScenicBean scenicBean = (ScenicBean) mDcsBeanList.get(position);
        holder.scenicNumber.setText(String.valueOf(position + 1));
        holder.scenicName.setText(scenicBean.getName());
        holder.scenicAddress.setText(scenicBean.getAddress());
        holder.scenicLayout.setOnClickListener(new View.OnClickListener() {
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

    public static class ScenicViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout scenicLayout;
        TextView scenicNumber;
        TextView scenicName;
        TextView scenicAddress;

        public ScenicViewHolder(View itemView) {
            super(itemView);
            scenicLayout = itemView.findViewById(R.id.scenic_item_root_layout);
            scenicNumber = itemView.findViewById(R.id.scenic_item_number);
            scenicName = itemView.findViewById(R.id.scenic_item_name);
            scenicAddress = itemView.findViewById(R.id.scenic_item_address);
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
