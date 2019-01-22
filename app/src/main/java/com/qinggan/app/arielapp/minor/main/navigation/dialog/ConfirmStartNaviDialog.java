package com.qinggan.app.arielapp.minor.main.navigation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;

/**
 * <ota升级对话框>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-3]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ConfirmStartNaviDialog extends Dialog {
    private static final String TAG = ConfirmStartNaviDialog.class.getSimpleName();

    public ConfirmStartNaviDialog(@NonNull Context context) {
        super(context, R.style.upgradeDialog);
    }

    TextView desc;
    Button cancelBtn, confirmBtn;
    OnClickListener mOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_confirm_navi_dialog);
        desc = findViewById(R.id.desc);
        cancelBtn = findViewById(R.id.cancel);
        confirmBtn = findViewById(R.id.start);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onCancleBtnClick();
                dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onConfirmBtnClick();
                dismiss();
            }
        });
    }

    public void setOnclickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public interface OnClickListener {
        void onConfirmBtnClick();
        void onCancleBtnClick();
    }
}
