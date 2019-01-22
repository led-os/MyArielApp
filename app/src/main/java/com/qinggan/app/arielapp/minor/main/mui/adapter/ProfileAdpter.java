package com.qinggan.app.arielapp.minor.main.mui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.scenario.ClickViewInterface;
import com.qinggan.app.arielapp.minor.scenario.ProfilesFragment;

import java.util.List;

public class ProfileAdpter extends RecyclerView.Adapter<ProfileAdpter.ViewHolder>{

    private Context mContext;
    private ClickViewInterface clickViewInterface;
    private List<String> list;
    int selectPostion;
    boolean isClick;
    int initPosition;

    /**
     * adapter点击事件
     */
    public interface OnItemClickListener{
        void onItemClick(View view);
        void onItemLongClick(View view);
    }

    private OnItemClickListener mItemClickListener;


    public void setItemClickListener(OnItemClickListener itemClickListener){
        mItemClickListener = itemClickListener;
    }

    public void selectPosition(int position,boolean click) {
        selectPostion = position;
        isClick = click;
    }

    public ProfileAdpter(Context mContext){
        this.mContext = mContext;
    }



    public void setList(List<String> list) {
        this.list = list;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.scene_mode_item_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTV.setText(list.get(position).toString());
        if (position == 0){
            holder.liner.setBackgroundResource(R.drawable.scene_bg_selected1);
            holder.mIv.setImageResource(R.drawable.scene_icon_sleet_on);
        }else if (position == 1){
            holder.liner.setBackgroundResource(R.drawable.scene_bg_selected2);
            holder.mIv.setImageResource(R.drawable.scene_icon_warmth_on);
        }else if (position == 2){
            holder.liner.setBackgroundResource(R.drawable.scene_bg_selected4);
            holder.mIv.setImageResource(R.drawable.scene_icon_cool_on);
        }else{
            holder.liner.setBackgroundResource(R.drawable.scene_bg_selected3);
            holder.mIv.setImageResource(R.drawable.scene_icon_smoking_on);
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout itemBg;
        TextView mTV;
        ImageView mIv;
        RelativeLayout liner;
        public ViewHolder(View itemView) {
            super(itemView);
            itemBg = itemView.findViewById(R.id.rl_item_bg);
            mTV = (TextView)itemView.findViewById(R.id.profile_image_tv);
            mIv = (ImageView) itemView.findViewById(R.id.profile_image_item);
            liner = (RelativeLayout) itemView.findViewById(R.id.proliner);
        }
    }
}

