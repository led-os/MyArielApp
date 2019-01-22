package com.qinggan.app.arielapp.minor.main.mui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.scenario.ClickViewInterface;
import com.tencent.qqmusic.third.api.contract.Data;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.baidu.turbonet.base.ThreadUtils.runOnUiThread;

public class PlayListAdpter extends RecyclerView.Adapter<PlayListAdpter.ViewHolder>{

    private Context mContext;
    private List<Data.Song> mlist;
    public OnItemClickListener onItemClickListener;
    private ClickViewInterface mClickViewInterface;
    int selectPostion;
    boolean isClick;

    public void setmClickViewInterface(ClickViewInterface mClickViewInterface) {
        this.mClickViewInterface = mClickViewInterface;
    }

    public PlayListAdpter(Context mContext){
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setList(List<Data.Song> list) {
        this.mlist = list;
    }

    public void selectPosition(int position,boolean click) {
        selectPostion = position;
        isClick = click;
    }

    public void setInitPosition(int position) {
        selectPostion = position;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!TextUtils.isEmpty(mlist.get(position).getTitle())){
            holder.nameTv.setText(mlist.get(position).getTitle());
        }
        if (mlist.get(position).getSinger() != null &&!TextUtils.isEmpty(mlist.get(position).getSinger().getTitle())){
            holder.songTv.setText(mlist.get(position).getSinger().getTitle());
        }
        if (selectPostion == position) {
            holder.nameTv.setTextColor(Color.parseColor("#2e81eb"));
            holder.songTv.setTextColor(Color.parseColor("#2e81eb"));
        } else {
            holder.nameTv.setTextColor(Color.WHITE);
            holder.songTv.setTextColor(Color.WHITE);
        }
        holder.palyLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickViewInterface.OnClickPositionListener(view,position,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numTv;
        TextView nameTv;
        TextView songTv;
        LinearLayout palyLinearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            numTv = (TextView)itemView.findViewById(R.id.broad_item_num);
            nameTv = (TextView)itemView.findViewById(R.id.broad_item_name);
            songTv = (TextView)itemView.findViewById(R.id.broad_item_time);
            palyLinearLayout = itemView.findViewById(R.id.paly_item);
        }
    }
}

