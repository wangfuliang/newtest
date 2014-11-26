package com.vikaa.lubbi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    /**
     * 检测是否是手机
     * @param str
     * @return
     */
    public static boolean isPhone(String str){
        String regEx = "^1[3-8]\\d{9}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
