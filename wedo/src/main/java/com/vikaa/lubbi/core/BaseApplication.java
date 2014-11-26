package com.vikaa.lubbi.core;

import android.app.Application;

public class BaseApplication extends Application {
    public final static boolean debug = true;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
