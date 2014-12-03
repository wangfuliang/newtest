package com.ddhigh.application.core;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {
    protected static Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
