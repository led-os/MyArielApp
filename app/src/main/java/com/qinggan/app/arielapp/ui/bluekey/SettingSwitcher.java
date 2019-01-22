package com.qinggan.app.arielapp.ui.bluekey;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.qinggan.app.arielapp.R;


public class SettingSwitcher extends CompoundButton implements android.view.View.OnClickListener {
    private static final String TAG = "SettingSwitcher";
    private boolean mChecked = false;
    private long lastDownTick = 0;
    public SettingSwitcher(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
        Log.d(TAG,"SettingSwitcher() 111");
    }

    public SettingSwitcher(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
        Log.d(TAG,"SettingSwitcher() 222");
    }

    public SettingSwitcher(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
        setChecked(isChecked(), false);
        Log.d(TAG,"SettingSwitcher() 333");
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        mChecked = checked;
        Log.e(TAG, "setChecked(),checked=" + checked);
        if (checked) {
            this.setBackground(getResources().getDrawable(R.drawable.setting_switcher_on));
        } else {
            this.setBackground(getResources().getDrawable(R.drawable.setting_switcher_off));
        }
    }

    public void setChecked(boolean checked, boolean anim) {
        super.setChecked(checked);
        mChecked = checked;
        Log.d(TAG, "setChecked(,),checked=" + checked);
        if (checked) {
            this.setBackground(getResources().getDrawable(R.drawable.setting_switcher_on));
        } else {
            this.setBackground(getResources().getDrawable(R.drawable.setting_switcher_off));
        }
    }


    @Override
    public void setEnabled(boolean enabled) {
        Log.d(TAG, "setEnabled(),enabled=" + enabled);
        super.setEnabled(enabled);
        setAlpha(enabled ? 1 : 0.5f);
    }

    @Override
    public boolean performClick()
    {
            Log.d(TAG, "performClick() +++");
            return super.performClick();
    }

    @Override
    public void onClick(View view) {
        //mChecked = !mChecked;
        Log.d(TAG, "onClick(,),mChecked=" + mChecked);
        if (mChecked) {
            this.setBackground(getResources().getDrawable(R.drawable.setting_switcher_on));
        } else {
            this.setBackground(getResources().getDrawable(R.drawable.setting_switcher_off));
        }
    }

}