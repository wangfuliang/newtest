package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.os.CountDownTimer;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;

public class FlashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);


        new CountDownTimer(2000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                setResult(RESULT_OK);
                finish();
            }
        }.start();
    }
}
