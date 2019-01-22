package com.qinggan.app.arielapp.minor.main.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.R;
/****
 * 导航软件选择界面
 *
 * ***/
public class DestinatorActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout baidu_map;
    private ImageView finsh_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinator);
        initview();
    }
    //初始化控件
    private void initview() {
        baidu_map=(LinearLayout)findViewById(R.id.baidu_map);
        baidu_map.setOnClickListener(this);
        finsh_btn=(ImageView)findViewById(R.id.finsh_btn);
        finsh_btn.setOnClickListener(this);
    }
    //控件点击监听
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.baidu_map:
                Intent intent=new Intent(this,SearchActivity.class);
                this.startActivity(intent);
                break;
            case R.id.finsh_btn:
                finish();
                break;
        }
    }
}
