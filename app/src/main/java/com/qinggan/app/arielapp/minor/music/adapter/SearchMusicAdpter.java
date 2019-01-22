package com.qinggan.app.arielapp.minor.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.tencent.qqmusic.third.api.contract.Data;

import java.util.List;

public class SearchMusicAdpter extends RecyclerView.Adapter<SearchMusicAdpter.ViewHolder>{

    private Context mContext;
    private List<Data.Song> mlist;
    public OnItemClickListener onItemClickListener;

    public SearchMusicAdpter(Context mContext){
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setList(List<Data.Song> list) {
        this.mlist = list;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.play_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.numTv.setText(String.valueOf(position +1));
        holder.nameTv.setText(mlist.get(position).getTitle());
        holder.songTv.setText(mlist.get(position).getSinger().getTitle());
    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numTv;
        TextView nameTv;
        TextView songTv;

        public ViewHolder(View itemView) {
            super(itemView);
            numTv = (TextView)itemView.findViewById(R.id.broad_item_num);
            nameTv = (TextView)itemView.findViewById(R.id.broad_item_name);
            songTv = (TextView)itemView.findViewById(R.id.broad_item_time);
        }
    }
}

