package com.qinggan.app.arielapp.minor.database.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;


public class ORMLiteTest2Activity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvMessage;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ormlite_detail);
        mContext = this;

        tvName = (TextView) findViewById(R.id.tv_name);
        tvMessage = (TextView) findViewById(R.id.tv_message);

        Intent intent = getIntent();
        CardInfo cardInfo = (CardInfo) intent.getParcelableExtra("cardinfo");

        tvName.setText(cardInfo.getContent());
        tvMessage.setText(cardInfo.getMessage());

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
