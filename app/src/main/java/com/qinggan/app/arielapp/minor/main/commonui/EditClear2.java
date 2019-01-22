package com.qinggan.app.arielapp.minor.main.commonui;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.R;


public class EditClear2 extends LinearLayout {
    ImageButton ib;
    EditText et;

    public EditClear2(Context context) {
        super(context);
    }

    public EditClear2(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.editclear2, this, true);
        init();
    }
    public void setHint(String str){
        et.setHint(str);
    }
    public void setText(String str){
        et.setText(str);
    }
    public String getText(){
        String str=et.getText().toString();
        return str;
    }
    public EditText getEditText(){
        return et;
    }


    public void setTextLength(int textLength){
        et.setInputType(InputType.TYPE_CLASS_NUMBER);//限制输入数
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(textLength)});
    }
    private void init() {
        ib = (ImageButton) findViewById(R.id.ib);
        et = (EditText) findViewById(R.id.et);
        et.addTextChangedListener(tw);// 为输入框绑定一个监听文字变化的监听器
        // 添加按钮点击事件
        ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBtn();// 隐藏按钮
                et.setText("");// 设置输入框内容为空
            }
        });
    }

    // 当输入框状态改变时，会调用相应的方法
    TextWatcher tw = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        // 在文字改变后调用
        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                hideBtn();// 隐藏按钮
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                if (isEnabled()) {
                    showBtn();// 显示按钮
                    et.setEnabled(true);
                }else {
                    hideBtn();
                    et.setEnabled(false);
                }
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
        }
    };

    public void hideBtn() {
        // 设置按钮不可见
        if (ib.isShown()){
            ib.setVisibility(View.GONE);
        }
    }

    public void showBtn() {
        // 设置按钮可见
        if (!ib.isShown()){
            ib.setVisibility(View.VISIBLE);
        }
    }
}