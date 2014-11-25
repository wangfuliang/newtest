package com.vikaa.lubbi.core;

import android.app.Application;
import android.os.Environment;

import com.baidu.frontia.FrontiaApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

public class BaseApplication extends FrontiaApplication {
    public static boolean hashFlashed = false;

    @Override
    public void onCreate() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);
        //保存图片，创建下载目录
        String rootPath = Environment.getExternalStorageDirectory() + "/com.vikaa.lubbi/";
        File rootFile = new File(rootPath);
        if (!rootFile.exists())
            rootFile.mkdir();
        String downDir = rootPath + "down/";
        File down = new File(downDir);
        if (!down.exists())
            down.mkdir();
        super.onCreate();
    }
}
