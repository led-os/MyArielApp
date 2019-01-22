package com.qinggan.app.arielapp.user.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.mobile.tsp.util.NetUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.qinggan.app.arielapp.minor.utils.Constants.DELETE_ADDRESS_EVENT;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private Context context;
    private List<AddressBean> dataList;
    private SwipeMenuRecyclerView mRecyclerView;

    public AddressAdapter(Context context, List<AddressBean> dataList,SwipeMenuRecyclerView mRecyclerView) {
        this.context = context;
        this.dataList = dataList;
        this.mRecyclerView = mRecyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(context).inflate(R.layout.address_item_lay,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final AddressBean addressBean=dataList.get(position);
        holder.name.setText(addressBean.getDisplayName());
        holder.address.setText(addressBean.getAddress());
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtil.isNetworkConnected(context)){
                    dataList.remove(position);
                    notifyItemRemoved(position);
                    if (dataList.size()==0){
                        notifyDataSetChanged();
                    }
                    notifyItemRangeChanged(position, dataList.size() - position);
                    mRecyclerView.smoothCloseMenu();
                    EventBus.getDefault().post(new EventBusBean(DELETE_ADDRESS_EVENT,
                            addressBean.getSid()
                    ));

                }else {
                    ToastUtil.show(R.string.no_network_tips,context);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public class  ViewHolder extends  RecyclerView.ViewHolder{
        private TextView name;
        private TextView address;
        private Button delBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.adapter_name);
            address=itemView.findViewById(R.id.adapter_address);
            delBtn=itemView.findViewById(R.id.btn_delete);
        }
    }
    public void setDataList( List<AddressBean> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();

    }
    public List<AddressBean> getDataList( ){
        return dataList;

    }

}
