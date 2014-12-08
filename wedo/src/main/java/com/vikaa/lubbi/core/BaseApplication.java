package com.vikaa.lubbi.core;

import com.baidu.frontia.FrontiaApplication;

public class BaseApplication extends FrontiaApplication {
    public final static boolean debug = false;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册异常捕获句柄
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
