package com.novax.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 计算间隔天数
     * ex 2024-12-20 23:59:50  2024-12-21 23:59:49  间隔2天
     */

    public static long getIntervalDays(Date startDate, Date endDate) throws ParseException {
        String start = simpleDateFormat1.format(startDate);
        String end = simpleDateFormat1.format(endDate);
        Long startTime = simpleDateFormat1.parse(start).getTime();
        Long endTime = simpleDateFormat1.parse(end).getTime();
        return (endTime - startTime) / (1000 * 60 * 60 * 24) + 1;
    }

    /**
     * 计算相差天数
     * ex 2024-12-20 23:59:50  2024-12-21 23:59:49  相差0天
     */
    public static long getDiscrepantDays(Date startDate, Date endDate) throws ParseException {
        String start = simpleDateFormat2.format(startDate);
        String end = simpleDateFormat2.format(endDate);
        Long startTime = simpleDateFormat2.parse(start).getTime();
        Long endTime = simpleDateFormat2.parse(end).getTime();
        return (endTime - startTime) / (1000 * 60 * 60 * 24);
    }

    /**
     * 获取当前时间 n 天之前的凌晨时间
     */
    public static Date getZeroTime(int n) throws ParseException {
        String time = simpleDateFormat1.format(new Date().getTime());
        Date date = simpleDateFormat1.parse(time);
        return new Date(date.getTime() - 1000L * 60 * 60 * 24 * (n - 1));
    }

    /**
     * 获取当前时间 n 天之前的凌点时间
     */
    public static Date getZeroTime2(int n) throws ParseException {
        String time = simpleDateFormat1.format(new Date().getTime());
        Date date = simpleDateFormat1.parse(time);
        return new Date(date.getTime() - 1000L * 60 * 60 * 24 * n);
    }

    /**
     * 获取当前时间n天之前的时间 具体到秒
     */
    public static Date addDay(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, n);
        return calendar.getTime();
    }

    // 本周一时间
    public static Date getThisMonday() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        // 设置周一为一周的开始，默认是周日Calendar.SUNDAY为一周的开始
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String date = simpleDateFormat1.format(calendar.getTime());
        return simpleDateFormat1.parse(date);
    }

    // 判断是否周一
    public static boolean isNotMonday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.MONDAY;
    }

    public static void main(String[] args) throws ParseException {
        // System.out.println(addDay(new Date(), -7));
        // System.out.println(addDay(new Date(),-1));

        // System.out.println(getZeroTime2(7));
        System.out.println(getZeroTime(8));
        // System.out.println(getThisMonday());
        // System.out.println(isNotMonday());
    }

}
