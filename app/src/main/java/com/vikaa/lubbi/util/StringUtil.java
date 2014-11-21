package com.vikaa.lubbi.util;

import org.json.JSONArray;
import org.json.JSONObject;

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
     *
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

    /**
     * 获取url的文件名
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        String[] params = url.split("/");
        return params[params.length - 1];
    }

    /**
     * 合并jsonarray
     * @param mData
     * @param array
     * @return
     */
    public static JSONArray joinJSONArray(JSONArray mData, JSONArray array) {
        StringBuffer buffer = new StringBuffer();
        try {
            int len = mData.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) mData.get(i);
                if (i == len - 1)
                    buffer.append(obj1.toString());
                else
                    buffer.append(obj1.toString()).append(",");
            }
            len = array.length();
            if (len > 0)
                buffer.append(",");
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) array.get(i);
                if (i == len - 1)
                    buffer.append(obj1.toString());
                else
                    buffer.append(obj1.toString()).append(",");
            }
            buffer.insert(0, "[").append("]");
            return new JSONArray(buffer.toString());
        } catch (Exception e) {
        }
        return null;
    }
}
