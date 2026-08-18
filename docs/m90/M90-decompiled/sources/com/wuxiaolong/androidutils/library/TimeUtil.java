package com.wuxiaolong.androidutils.library;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class TimeUtil {
    public static String getCurrentTime(String str) {
        return new SimpleDateFormat(str, Locale.getDefault()).format(new Date());
    }

    public static int getWeekOfMonth() {
        return Calendar.getInstance().get(4) - 1;
    }

    public static int getDayOfWeek() {
        int i = Calendar.getInstance().get(7);
        if (i == 1) {
            return 7;
        }
        return i - 1;
    }

    public static int getYear() {
        return GregorianCalendar.getInstance().get(1);
    }

    public static int getMonth() {
        return GregorianCalendar.getInstance().get(2);
    }

    public static int getDay() {
        return GregorianCalendar.getInstance().get(5);
    }

    public static int compareDate(String str, String str2, String str3) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str3, Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(str);
            Date date2 = simpleDateFormat.parse(str2);
            if (date.getTime() > date2.getTime()) {
                return 1;
            }
            return date.getTime() < date2.getTime() ? -1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String timeAddSubtract(String str, int i) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return simpleDateFormat.format(new Date(simpleDateFormat.parse(str).getTime() + ((long) (i * 24 * 60 * 60 * 1000))));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static String millisecond2String(Object obj, String str) {
        return new SimpleDateFormat(str, Locale.getDefault()).format(obj);
    }

    public static String unixTimestamp2BeijingTime(Object obj, String str) {
        return new SimpleDateFormat(str, Locale.getDefault()).format(obj);
    }

    public static long beijingTime2UnixTimestamp(String str, String str2) {
        try {
            return new SimpleDateFormat(str2, Locale.getDefault()).parse(str).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
