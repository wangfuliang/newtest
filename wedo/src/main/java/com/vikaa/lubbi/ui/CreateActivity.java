package com.vikaa.lubbi.ui;

import android.os.Bundle;
import android.view.View;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.util.Logger;

public class CreateActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.create:
                Logger.d("create");
                break;
        }
    }
}
