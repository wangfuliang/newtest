package com.vikaa.lubbi.util;

import java.util.List;
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


    /**
     * 替换回车
     *
     * @param str
     * @return
     */
    public static String escape(String str) {
        return str.replaceAll("\\\\n", "\\\n");
    }

    /**
     * list转字符串
     * @param list
     * @param separator
     * @return
     */
    public String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }
}
