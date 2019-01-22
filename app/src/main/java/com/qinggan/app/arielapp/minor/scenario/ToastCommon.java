package com.qinggan.app.arielapp.minor.scenario;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qinggan.app.arielapp.R;

/**
 * @Author: yang
 * @Date:on 2016/10/15.
 */

public class ToastCommon {
    public static void toast(Context context, String content)
    {
        View view= View.inflate(context,R.layout.toast_item, null);      //加载布局文件
        TextView textView=(TextView) view.findViewById(R.id.toast_text);    // 得到textview
        textView.setText(content);     //设置文本类荣，就是你想给用户看的提示数据
        Toast toast=new Toast(context);     //创建一个toast
        toast.setDuration(Toast.LENGTH_SHORT);          //设置toast显示时间，整数值
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);    //toast的显示位置，这里居中显示
        toast.setView(view);     //設置其显示的view,
        toast.show();             //显示toast
    }
    public static void toastSuccess(Context context, String content)
    {
        View view= View.inflate(context,R.layout.toast_item, null);      //加载布局文件
        ImageView toaIv = (ImageView) view.findViewById(R.id.toast_iv);
//        toaIv.setBackgroundResource(R.mipmap.person_success);
        TextView textView=(TextView) view.findViewById(R.id.toast_text);    // 得到textview
        textView.setText(content);     //设置文本类荣，就是你想给用户看的提示数据
        Toast toast=new Toast(context);     //创建一个toast
        toast.setDuration(Toast.LENGTH_SHORT);          //设置toast显示时间，整数值
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);    //toast的显示位置，这里居中显示
        toast.setView(view);     //設置其显示的view,
        toast.show();             //显示toast
    }
}
