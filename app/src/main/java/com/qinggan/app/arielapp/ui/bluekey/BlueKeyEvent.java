package com.qinggan.app.arielapp.ui.bluekey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qinggan.app.arielapp.R;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.service.BleCarKey;
import com.qinggan.bluekey.service.BlueKeyListener;

import java.util.HashMap;
import java.util.List;

public class BlueKeyEvent {
    public boolean success = false;
    public Object obj = null;
    public int param = 0;
    public BlueKeyEvent(boolean success, Object obj) {
        this.success = success;
        this.obj = obj;
        this.param = 0;
    }

    @Override
    public String toString() {
        return "BlueKeyEvent{" +
                "success=" + success +
                ", obj=" + obj +
                ", param=" + param +
                '}';
    }

    public BlueKeyEvent(boolean success, Object obj, int param) {
        this.success = success;
        this.obj = obj;
        this.param = param;
    }
}
