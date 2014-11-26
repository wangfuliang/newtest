package com.vikaa.lubbi.core;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.vikaa.lubbi.receiver.Utils;
import com.vikaa.lubbi.ui.MainActivity;
import com.vikaa.lubbi.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends FragmentActivity {
    protected static MainActivity.CoreHandler handler;
    protected static String sign;
    public static String userId;
    protected static boolean hasFlash = false;
    protected static HttpUtils httpUtils;
    protected static boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启动推送services
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(this, "api_key"));
    }
}