package com.vikaa.lubbi.core;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.vikaa.lubbi.receiver.Utils;
import com.vikaa.lubbi.ui.MainActivity;
import com.vikaa.lubbi.util.Logger;

import java.io.File;

public class BaseActivity extends FragmentActivity {
    public static MainActivity.CoreHandler handler;
    protected static String sign;
    public static String userId;
    protected static boolean hasFlash = false;
    protected static HttpUtils httpUtils = new HttpUtils();
    protected static boolean isLogin = false;
    public static BitmapUtils bitmapUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启动推送services
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(this, "api_key"));
        //建立缓存目录和下载目录
        String sdPath = getSDPath();
        if (sdPath != null) {
            String rootPath = sdPath + "/com.vikaa.lubbi";
            File root = new File(rootPath);
            if (!root.exists())
                root.mkdir();
            File cache = new File(rootPath + "/cache");
            if (!cache.exists())
                cache.mkdir();
            File down = new File(rootPath + "/down");
            if (!down.exists())
                down.mkdir();
            File log = new File(rootPath + "/logs");
            if (!log.exists())
                log.mkdir();
            bitmapUtils = new BitmapUtils(this, cache.toString());
            Logger.d(cache.toString());
        } else {
            bitmapUtils = new BitmapUtils(this);
        }
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        }
        return null;
    }
}