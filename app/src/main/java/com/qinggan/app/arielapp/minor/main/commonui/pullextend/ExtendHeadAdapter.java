package com.qinggan.app.arielapp.minor.main.commonui.pullextend;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGARecyclerViewHolder;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;


/**
 * Created by Alan on 2018/1/24.
 */

public class ExtendHeadAdapter extends BGARecyclerViewAdapter<String> {

    public ExtendHeadAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_header);
    }
    @Override
    public void setItemChildListener(final BGAViewHolderHelper helper, int viewType) {
        helper.setItemChildClickListener(R.id.item_title);

    }

    @Override
    public void fillData(BGAViewHolderHelper helper, int position, final String model) {

            final ProgressBar down_progress=helper.getView(R.id.down_progress);
            ArielApplication.getApp().setDownProgressBar(down_progress);
            down_progress.setProgress(Integer.valueOf(model));


    }


}
