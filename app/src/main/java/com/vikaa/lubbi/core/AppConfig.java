package com.vikaa.lubbi.core;

public class AppConfig {
    public final static boolean debug = true;
    public static int version = 1;

    public interface App{
        public static String APP_ID = "wxe0972b3609a72e60";
        public static String APP_KEY = "c54dd5a7aa8f81bd0186f040e8ab72b9";
    }
    public interface  Api{
        public static String autoLogin = "http://app.qun.hk/hello/Api/CheckLogin";
        public static String loginWechat = "http://app.qun.hk/hello/Api/wechat";
        public static String loginPhone = "http://app.qun.hk/hello/Api/Phone";
        public static String verifyCode = "http://app.qun.hk/hello/Api/VerifyCode";
        public static String setPush = "http://app.qun.hk/hello/Api/SetPush";
    }
}
