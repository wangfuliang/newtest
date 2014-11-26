package com.vikaa.lubbi.core;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lidroid.xutils.HttpUtils;
import com.vikaa.lubbi.ui.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends FragmentActivity {
    protected static MainActivity.CoreHandler handler;
    protected static String sign;
    protected static Map<String, String> pushParams = new HashMap<String, String>();
    protected static boolean hasFlash = false;
    protected static HttpUtils httpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}