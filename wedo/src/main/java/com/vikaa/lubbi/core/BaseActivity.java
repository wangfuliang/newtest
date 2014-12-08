package com.vikaa.lubbi.core;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
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
    protected static String cachePath;
    protected static String downPath;
    protected static String logPath;


    public final static String AppID = "wx8060e1480d0ee7ab";
    public final static String AppSecret = "fa2511ecab83cf6e985928d884b52511";

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
            cachePath = rootPath + "/cache";
            File cache = new File(cachePath);
            if (!cache.exists())
                cache.mkdir();
            downPath = rootPath + "/down";
            File down = new File(downPath);
            if (!down.exists())
                down.mkdir();
            logPath = rootPath + "/logs";
            File log = new File(logPath);
            if (!log.exists())
                log.mkdir();
            bitmapUtils = new BitmapUtils(this, cache.toString());
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

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }
}