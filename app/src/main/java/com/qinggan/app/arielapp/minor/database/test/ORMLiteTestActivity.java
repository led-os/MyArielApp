package com.qinggan.app.arielapp.minor.database.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;

import java.util.List;


public class ORMLiteTestActivity extends AppCompatActivity {

    private Button  insertButton;
    private Button  deleteButton;
    private Button  updateButton;
    private Button  queryButton;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ormlite_main);
        mContext = this;

        insertButton = (Button) findViewById(R.id.test_insert);
        deleteButton = (Button) findViewById(R.id.test_delete);
        updateButton = (Button) findViewById(R.id.test_update);
        queryButton = (Button) findViewById(R.id.test_query);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ORMLiteTestUtils.testInsertCardInfo(mContext);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CardInfo> list = ORMLiteTestUtils.testQueryCardInfos(mContext);
                if (list != null && list.size() > 0) {
                    ORMLiteTestUtils.testDeleteCardInfo(mContext,list.get(0));
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CardInfo> list = ORMLiteTestUtils.testQueryCardInfos(mContext);
                if (list != null && list.size() > 0) {
                    ORMLiteTestUtils.testUpdateCardInfo(mContext,list.get(0));
                }
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CardInfo> list = ORMLiteTestUtils.testQueryCardInfos(mContext);

                if (list != null && list.size() > 0) {
                    Intent intent = new Intent(mContext, ORMLiteTest2Activity.class);
                    intent.putExtra("cardinfo", list.get(0));
                    startActivity(intent);
                }
            }
        });

        /*Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mContactsName = bundle.getString(Constants.CONTACTS_NAME, "");
        mPhoneNumber = bundle.getString(Constants.PHONE_NUMBER, "");

        mContext = this;
        btnHungupPhone = (ImageButton) findViewById(R.id.btn_hungup_phone);
        btnHungupPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectCall();
            }
        });

        tvName = (TextView) findViewById(R.id.tv_name);
        tvPhoneNumber = (TextView) findViewById(R.id.tv_phone_number);

        tvName.setText(mContactsName);
        tvPhoneNumber.setText(mPhoneNumber);*/

    }

}
