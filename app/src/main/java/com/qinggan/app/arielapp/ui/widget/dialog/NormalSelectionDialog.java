package com.qinggan.app.arielapp.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;

public class NormalSelectionDialog {


    private Dialog mDialog;
    private View dialogView;
    private TextView title;
    private LinearLayout mCancel;
    private LinearLayout mRetest;
    private LinearLayout linearLayout;


    public NormalSelectionDialog(Context mContext ,String message,View.OnClickListener l) {

        mDialog = new Dialog(mContext, R.style.bottomDialogStyle);
        dialogView = View.inflate(mContext, R.layout.widget_bottom_dialog, null);
        mDialog.setContentView(dialogView); // 一定要在setAttributes(lp)之前才有效
        //设置dialog的宽
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(mContext).getScreenWidth() );
        lp.gravity = Gravity.BOTTOM;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);


        title = (TextView) dialogView.findViewById(R.id.action_dialog_title);
        if (!TextUtils.isEmpty(message)) {
            title.setText(message);
        }
        linearLayout = (LinearLayout) dialogView.findViewById(R.id.action_dialog_linearlayout);
        mCancel = (LinearLayout) dialogView.findViewById(R.id.cancel_lay);
        mCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                mDialog.dismiss();
            }
        });
        mRetest = (LinearLayout) dialogView.findViewById(R.id.retest_lay);
        mRetest.setOnClickListener(l);


        mDialog.setCanceledOnTouchOutside(true);
    }


    public boolean isShowing() {

        return mDialog.isShowing();
    }

    public void show() {

        mDialog.show();

    }

    public void dismiss() {

        mDialog.dismiss();
    }

}