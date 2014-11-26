package com.vikaa.lubbi.core;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.vikaa.lubbi.ui.MainActivity;

public class BaseActivity extends FragmentActivity {
    protected static MainActivity.CoreHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}