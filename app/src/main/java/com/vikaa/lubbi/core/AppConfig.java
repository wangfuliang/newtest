package com.vikaa.lubbi.core;

public class AppConfig {
    public final static boolean debug = true;
    public static int version = 1;

    public interface App {
        public static String APP_ID = "wxe0972b3609a72e60";
        public static String APP_KEY = "c54dd5a7aa8f81bd0186f040e8ab72b9";
    }

    public interface Api {
        public static String checkLogin = "http://app.qun.hk/remind/api/CheckLogin";
        public static String verifyCode = "http://app.qun.hk/remind/api/VerifyCode";
        public static String phoneLogin = "http://app.qun.hk/remind/api/PhoneLogin";
        public static String createRemind = "http://app.qun.hk/remind/api/createremind";
        public static String listUserRemind = "http://app.qun.hk/remind/api/listuserremind";
    }

    public interface Message{
        //显示提醒详情
        public static int ShowRemindDetail = 0x01;
    }
}
