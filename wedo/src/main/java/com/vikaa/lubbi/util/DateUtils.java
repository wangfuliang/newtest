package com.vikaa.lubbi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {

    private static SimpleDateFormat sf = null;

    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long dateToLong(String format,String time) {
        sf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 时间戳转标准时间
     *
     * @param timestamp
     * @return
     */
    public static String getStandardDateTime(long timestamp) {
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return sf.format(date);
    }

    /**
     * 自定义格式
     *
     * @param format
     * @return
     */
    public static String getCustom(String format, Date date) {
        sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * 自定义格式
     * @param format
     * @return
     */
    public static String getCustom(String format) {
        Date date = new Date();
        sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * 获取时间节
     *
     * @param time
     * @param format
     * @return
     */
    public static int getTimeSection(long time, String format) {
        Date d = new Date(time);
        sf = new SimpleDateFormat(format);
        int s = Integer.parseInt(sf.format(d));
        return s;
    }

    public static String parseWeek(int weekday){
        String[] list = new String[]{
                "周日",
                "周一",
                "周二",
                "周三",
                "周四",
                "周五",
                "周六"
        };
        return list[weekday];
    }
}