package com.qinggan.app.cast.presentation.allapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.PresentationManager;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public class AllAppAdapter extends RecyclerView.Adapter<AllAppAdapter.VH> {
    private static final String TAG = AllAppAdapter.class.getSimpleName();

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.p_item_icon_view, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        final AllAppConfig config = AllAppConfig.values()[position];
        holder.appIcon.setImageResource(config.iconId);
        holder.appName.setText(holder.itemView.getContext().getString(config.nameId));
        holder.appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick config position:" + position);
                PresentationManager.getInstance().showPresentation(config.getPresentation());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick config position:" + position);
                PresentationManager.getInstance().showPresentation(config.getPresentation());
            }
        });
    }

    @Override
    public int getItemCount() {
        return AllAppConfig.values().length;
    }

    // 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public  ImageView appIcon;
        public TextView appName;
        public VH(View v) {
            super(v);
            appIcon = v.findViewById(R.id.icon);
            appName=v.findViewById(R.id.icon_text);
        }
    }

}
