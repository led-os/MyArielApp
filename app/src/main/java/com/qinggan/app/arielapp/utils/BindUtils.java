package com.qinggan.app.arielapp.utils;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.widget.custom.StarBar;

/**
 * Created by shuohuang on 18-4-10.
 */

public class BindUtils {

    @BindingAdapter({"setStars"})
    public static void setStarCount(StarBar starBar, String count) {
        if (count != null) {
            try {
                float nCount = Float.parseFloat(count);
                starBar.setStarMark(nCount);
            } catch (NumberFormatException e) {
                System.out.println("异常：\"" + count + "\"不是float...");
            }
        }
    }

    @BindingAdapter({"textColor"})
    public static void setTextColor(TextView textView, double changeInPrice) {
        if (changeInPrice > 0) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.red));
        } else if (changeInPrice < 0) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.green));
        } else {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.white));
        }
    }


    @BindingAdapter({"setDate"})
    public static void setDate(TextView textView, String date) {
        String tmp = null;
        if (!TextUtils.isEmpty(date)) {
            String[] temp = date.split("-");
            if (temp.length == 3) {
                tmp = temp[1] + textView.getContext().getString(R.string.month) + temp[2] + textView.getContext().getString(R.string.date);
            }
        }

        textView.setText(tmp);
    }


}
