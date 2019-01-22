package com.qinggan.app.arielapp.minor.main.driving.helper;

import android.support.v7.widget.helper.ItemTouchHelper;

public class YolandaItemTouchHelper extends ItemTouchHelper {

    private Callback mCallback;

    public YolandaItemTouchHelper(Callback callback) {
        super(callback);
        this.mCallback=callback;
    }
    
    public Callback getCallback(){
        return  mCallback;
    }
}
