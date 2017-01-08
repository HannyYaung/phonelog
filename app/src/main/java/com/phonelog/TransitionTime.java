package com.phonelog;

import android.content.Context;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
/**
 * User: LJM
 * Date&Time: 2016-09-29 & 17:33
 * Describe: Describe Text
 */

/**
 * 用来计算显示的时间是多久之前的！
 */
public class TransitionTime {

    private Date endDate;
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    public static final int WEEKDAYS = 7;
    public static String[] WEEK = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public TransitionTime(long endTime) {
        endDate = new Date(endTime);
    }

    // 获得今天日期
    public static String getTodayData() {
        return getDate(getNow());

    }

    // 获得昨天日期
    public static String getYesData() {
        return getDate(getNow() - 86400000L);
    }

    // 获得当前时间的毫秒表示
    public static long getNow() {
        GregorianCalendar now = new GregorianCalendar();
        return now.getTimeInMillis();

    }

    public static String dataFormart(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateStr);
            String str = sdf.format(date);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String dataFormartMd(String dateStr) {
        SimpleDateFormat sdfy = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfm = new SimpleDateFormat("MM-dd");
        try {
            Date date = sdfy.parse(dateStr);
            String str = sdfm.format(date);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String dataFormartYM(String dateStr) {
        SimpleDateFormat sdfy = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfm = new SimpleDateFormat("yyyy-MM");
        try {
            Date date = sdfy.parse(dateStr);
            String str = sdfm.format(date);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String dataFormartMdt(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dateStr);
            String str = sdf.format(date);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String dataFormartT(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfT = new SimpleDateFormat("HH:mm");
        try {
            Date date = sdf.parse(dateStr);
            String str = sdfT.format(date);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    // 根据输入的毫秒数，获得日期字符串
    public static String getDate(long millis) {
        return formatter.format(millis);

    }

    public static String convertTimeFirstStyle(long date) {

        String strs = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }


    /**
     * 时间返回,如下
     * 发送消息时，对话框界面的时间显示规则为24小时制，
     * 一.24小时内，（例如16：30，8：30）
     * 二.24小时-48小时内，显示（昨天16：30）
     * 三.一周内，显示（星期四 16：30）
     * 四：一周外显示（2016-09-01 16：30）
     *
     * @param context
     * @param time
     * @return
     */
    public static String getDisplayTimeAndDesc(Context context, long time) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - time < (60 * 1000 * 60 * 24)) {//一天
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date day = new Date(time);
            String t1 = format.format(day);
            return t1;
        } else if (currentTimeMillis - time < (60 * 1000 * 60 * 24 * 2)) {//48小时内
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date yes = new Date(time);
            String t1 = format.format(yes);
            return "昨天" + t1;
        } else if (currentTimeMillis - time < (60 * 1000 * 60 * 24 * 7)) {//一周
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            SimpleDateFormat formatWek = new SimpleDateFormat("yyyy-MM-dd");
            Date wek = new Date(time);
            String t1 = format.format(wek);
            return stringDataWek(formatWek.format(wek)) + " " + t1;
        } else {
            // 月日 : 时分
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(time);
        }
    }

    public static String stringDataWek(String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(data, pos);
        // 再转换为时间
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }


    /**
     * 日期变量转成对应的星期字符串
     *
     * @param milliseconds data
     * @return 日期变量转成对应的星期字符串
     */
    public static String DateToWeek(long milliseconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            return null;
        }

        return WEEK[dayIndex - 1];
    }

}
