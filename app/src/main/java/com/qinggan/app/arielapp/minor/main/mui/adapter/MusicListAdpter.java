package com.qinggan.app.arielapp.minor.main.mui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;

import java.util.List;

public class MusicListAdpter extends RecyclerView.Adapter<MusicListAdpter.ViewHolder>{

    private Context mContext;
//    private List<GraderBean.DataBean.SoncateBeanXX.SoncateBeanX.SoncateBean> list1;

    private List<String> list;
    public OnItemClickListener onItemClickListener;

    public MusicListAdpter(Context mContext){
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

//    public void setList(List<GraderBean.DataBean.SoncateBeanXX.SoncateBeanX.SoncateBean> list1) {
//        this.list1 = list1;
//    }

    public void setList(List<String> list) {
        this.list = list;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.nameTv.setText("早安 875");
//        holder.timeTV.setText("07：00-09:00");
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView numTv;
        TextView nameTv;
        TextView timeTV;

        public ViewHolder(View itemView) {
            super(itemView);
            numTv = (ImageView)itemView.findViewById(R.id.music_item_img);
            nameTv = (TextView)itemView.findViewById(R.id.music_item_name);
            timeTV = (TextView)itemView.findViewById(R.id.music_item_time);
        }
    }
}

