package com.vikaa.lubbi.core;

public class AppConfig {
    public final static boolean debug = true;
    public final static int version = 1;

    public interface App {
        public final static String APP_ID = "wxe0972b3609a72e60";
        public final static String APP_KEY = "c54dd5a7aa8f81bd0186f040e8ab72b9";
    }

    public interface Api {
        public final static String checkLogin = "http://app.qun.hk/remind/api/CheckLogin";
        public final static String verifyCode = "http://app.qun.hk/remind/api/VerifyCode";
        public final static String phoneLogin = "http://app.qun.hk/remind/api/PhoneLogin";
        public final static String createRemind = "http://app.qun.hk/remind/api/createremind";
        public final static String listUserRemind = "http://app.qun.hk/remind/api/listuserremind";
        public final static String uploadUrl = "http://up.qiniu.com";
        public final static String uploadToken = "http://app.qun.hk/upload/token";
        public final static String createSign = "http://app.qun.hk/remind/api/createsign";
        public final static String getSignInfo = "http://app.qun.hk/remind/api/signinfo";
    }

    public interface Message {
        //显示提醒详情
        public final static int ShowRemindDetail = 0x01;
        public final static int SetRemindSign = 0x02;
    }
}
