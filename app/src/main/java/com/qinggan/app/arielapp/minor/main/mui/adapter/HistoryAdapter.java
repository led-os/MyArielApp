package com.qinggan.app.arielapp.minor.main.mui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.main.entity.Adresss;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private List<NaviSearchHistory> dataList;

    public HistoryAdapter(Context context, List<NaviSearchHistory> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(context).inflate(R.layout.histrory_item_layout,parent,false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final NaviSearchHistory naviSearchHistory=dataList.get(position);
        holder.adStr.setText(naviSearchHistory.getContents());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusBean("PresetAdress",naviSearchHistory));//将选中结果发送到activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public class  ViewHolder extends  RecyclerView.ViewHolder{
        private TextView adStr;
        public ViewHolder(View itemView) {
            super(itemView);
            adStr=itemView.findViewById(R.id.histrory_txt);
        }
    }

}
