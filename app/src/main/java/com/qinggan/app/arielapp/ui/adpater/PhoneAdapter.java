package com.qinggan.app.arielapp.ui.adpater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;

import java.util.List;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-10-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {

    List<String> datas;

    public PhoneAdapter(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public PhoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone, parent, false);
        PhoneViewHolder holder = new PhoneViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PhoneViewHolder holder, int position) {
        holder.phoneNo.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class PhoneViewHolder extends RecyclerView.ViewHolder {

        TextView phoneNo;

        public PhoneViewHolder(View itemView) {
            super(itemView);
            phoneNo = itemView.findViewById(R.id.phone_no);
        }
    }
}
