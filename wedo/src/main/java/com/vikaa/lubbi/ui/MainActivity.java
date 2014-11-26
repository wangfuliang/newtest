package com.vikaa.lubbi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.vikaa.lubbi.R;
import com.vikaa.lubbi.core.BaseActivity;
import com.vikaa.lubbi.core.MyMessage;
import com.vikaa.lubbi.util.Logger;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化handler
        initHandler();
        //启动闪屏
        startFlash();
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
            Logger.d(msg.what+"");
            switch (msg.what){
                case MyMessage.FLASH_END:
                    startMain();
                    break;
            }
        }
    }

    /**
     * main初始化
     */
    private void startMain() {
        Logger.d("start main");
    }
}
