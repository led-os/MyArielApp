package com.qinggan.app.arielapp.minor.main.mui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;

import java.util.List;

public class BroadListAdpter extends RecyclerView.Adapter<BroadListAdpter.ViewHolder> {

    public OnItemClickListener onItemClickListener;
    private Context mContext;
    private List<String> list;

    public BroadListAdpter(Context mContext) {
        this.mContext = mContext;
    }

    public void setList(List<String> list) {
        this.list = list;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.radio_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (list != null && list.size() > 0){
            Float frequency = Float.valueOf(list.get(position));
            holder.radioListTitle.setText(frequency / 1000 + "");
        } else {
            holder.radioListTitle.setText("早安 875");
        }
        int pos = position + 1;
        holder.radioListIndex.setText(pos + "");
        holder.radioListTime.setText("07：00-09:00");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView radioListIndex;
        TextView radioListTitle;
        TextView radioListTime;
        ImageView radioListImage;
        ImageView radioListImageSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            radioListImage = itemView.findViewById(R.id.raido_list_image);
            radioListIndex = itemView.findViewById(R.id.radio_list_index);
            radioListTitle = itemView.findViewById(R.id.radio_list_title);
            radioListTime = itemView.findViewById(R.id.radio_list_time);
            radioListImageSelect = itemView.findViewById(R.id.raido_list_image_select);
        }
    }
}

