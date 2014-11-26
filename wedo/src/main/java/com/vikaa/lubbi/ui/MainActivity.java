package com.vikaa.lubbi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.BaseApplication;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.util.Logger;
import com.vikaa.lubbi.util.SP;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化handler
        initHandler();
        //初始化HTTP
        httpUtils = new HttpUtils();
        //启动闪屏
        if (!hasFlash) {
            hasFlash = true;
            startFlash();
        } else {
            handler.sendEmptyMessage(MyMessage.START_MAIN);
        }
    }

    /**
     * 检测登录
     */
    private void checkLogin() {
        sign = (String) SP.get(getApplicationContext(), "user.sign", "");
        if (sign.length() == 0) {
            //去登录
            handler.sendEmptyMessage(MyMessage.GOTO_LOGIN);
        } else {
            //准备推送
            handler.sendEmptyMessage(MyMessage.START_MAIN);
        }
    }

    /**
     * 启动闪屏
     */
    private void startFlash() {
        Intent intent = new Intent(this, FlashActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化handler
     */
    private void initHandler() {
        handler = new CoreHandler();
    }

    /**
     * CoreHandler
     */
    public class CoreHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyMessage.START_MAIN:
                    checkLogin();
                    break;
                case MyMessage.GOTO_LOGIN:
                    startLogin();
                    break;
                case MyMessage.SYSTEM_EXIT:
                    finish();
                    System.exit(0);
                    break;
            }
        }
    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * main初始化
     */
    private void startMain() {
        Logger.d("start main");
    }
}
