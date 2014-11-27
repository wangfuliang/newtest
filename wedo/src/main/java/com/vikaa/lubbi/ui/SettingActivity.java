package com.vikaa.lubbi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.util.Logger;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.message:
                Logger.d("message");
                break;
            case R.id.help:
                Intent intent = new Intent(this, BrowserActivity.class);
                startActivity(intent);
                break;
        }
    }
}
