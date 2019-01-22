package com.qinggan.app.arielapp.ui.viewholder;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/*******************************************************************************
 *
 * @author : Pateo harrishuang@pateo.com.cn
 *
 * Copyright (c) 2017-2020 Pateo. All Rights Reserved.
 *
 * Copying of this document or code and giving it to others and the 
 * use or communication of the contents thereof, are forbidden without
 * expressed authority. Offenders are liable to the payment of damages.
 * All rights reserved in the event of the grant of a invention patent or the 
 * registration of a utility model, design or code.
 *
 * Issued by Pateo.
 * Date: 2018-03-29
 *******************************************************************************/

public class BindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    protected final T mBinding;

    public BindingViewHolder(T binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public T getBinding() {
        return mBinding;
    }
}
