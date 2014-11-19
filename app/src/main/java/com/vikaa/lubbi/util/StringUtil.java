package com.vikaa.lubbi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * 获取字符串中的数字
     *
     * @param str
     * @return
     */
    public static String getNumber(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    public static String escape(String str) {
        return str.replaceAll("\\\\n", "\\\n");
    }
}
