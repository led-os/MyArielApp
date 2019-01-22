package com.qinggan.app.arielapp.minor.main.mui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.qinggan.app.arielapp.R;

import java.util.List;

public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.ViewHolder> {
    private Context context;
    private List<PoiInfo> dataList;

    public PoiAdapter(Context context, List<PoiInfo> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public PoiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poi_item_layout,parent,false);

        return new PoiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.adStr.setText(dataList.get(position).name);
        holder.rdStr.setText(dataList.get(position).address);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public class  ViewHolder extends  RecyclerView.ViewHolder{
        private TextView adStr;
        private TextView rdStr;

        public ViewHolder(View itemView) {
            super(itemView);
            adStr=itemView.findViewById(R.id.adress_txt);
            rdStr=itemView.findViewById(R.id.road_txt);
        }
    }

}
