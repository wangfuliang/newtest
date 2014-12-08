package com.vikaa.lubbi.core;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

public class BaseApplication extends FrontiaApplication {
    public final static boolean debug = false;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public BDLocation bdLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册异常捕获句柄
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        //Baidu Location SDK
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mLocationClient.start();
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            if (location.getLocType() == 0)
                mLocationClient.stop();
            bdLocation = location;
        }
    }
}
