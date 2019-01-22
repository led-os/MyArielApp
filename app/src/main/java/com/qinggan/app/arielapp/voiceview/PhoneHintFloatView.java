package com.qinggan.app.arielapp.voiceview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;

import java.util.Random;

/**
 * Created by zhongquansun on 2018/11/29.
 */
public class PhoneHintFloatView extends BaseFloatView {

    private static PhoneHintFloatView sPhoneHintFloatView;
    private TextView mHint;

    public static PhoneHintFloatView getInstance(Context context){
        if(null == sPhoneHintFloatView){
            sPhoneHintFloatView = new PhoneHintFloatView(context);
        }
        return sPhoneHintFloatView;
    }

    private PhoneHintFloatView(Context context) {
        super(context);
    }

    @Override
    protected void onWindowParamsCreate(WindowManager.LayoutParams layoutParams) {
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.dp170);
    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.phone_hint_float_view;
    }

    @Override
    protected void onViewCreate(View view) {
        mHint = view.findViewById(R.id.hint);
    }

    @Override
    public void show() {
        setRandomHint();
        super.show();
    }

    private void setRandomHint(){
        Random random = new Random();
        int hintValue = random.nextInt(3);
        switch (hintValue) {
            case 0:
                mHint.setText(R.string.call_prompt1);
                break;
            case 1:
                mHint.setText(R.string.call_prompt2);
                break;
            case 2:
                mHint.setText(R.string.call_prompt3);
                break;
        }
    }
}
