package org.apache.tools.ant.util;

import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes3.dex */
public final class DateUtils {
    public static final DateFormat DATE_HEADER_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ", Locale.US);
    private static final DateFormat DATE_HEADER_FORMAT_INT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ", Locale.US);
    public static final String ISO8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO8601_DATE_PATTERN = "yyyy-MM-dd";
    public static final String ISO8601_TIME_PATTERN = "HH:mm:ss";
    private static final double[] LIMITS;
    private static final ChoiceFormat MINUTES_FORMAT;
    private static final String[] MINUTES_PART;
    private static final MessageFormat MINUTE_SECONDS;
    private static final int ONE_HOUR = 60;
    private static final int ONE_MINUTE = 60;
    private static final int ONE_SECOND = 1000;
    private static final ChoiceFormat SECONDS_FORMAT;
    private static final String[] SECONDS_PART;
    private static final int TEN = 10;

    static {
        MessageFormat messageFormat = new MessageFormat("{0}{1}");
        MINUTE_SECONDS = messageFormat;
        double[] dArr = {0.0d, 1.0d, 2.0d};
        LIMITS = dArr;
        String[] strArr = {"", "1 minute ", "{0,number,###############} minutes "};
        MINUTES_PART = strArr;
        String[] strArr2 = {"0 seconds", "1 second", "{1,number} seconds"};
        SECONDS_PART = strArr2;
        ChoiceFormat choiceFormat = new ChoiceFormat(dArr, strArr);
        MINUTES_FORMAT = choiceFormat;
        ChoiceFormat choiceFormat2 = new ChoiceFormat(dArr, strArr2);
        SECONDS_FORMAT = choiceFormat2;
        messageFormat.setFormat(0, choiceFormat);
        messageFormat.setFormat(1, choiceFormat2);
    }

    private DateUtils() {
    }

    public static String format(long j, String str) {
        return format(new Date(j), str);
    }

    public static String format(Date date, String str) {
        return createDateFormat(str).format(date);
    }

    public static String formatElapsedTime(long j) {
        long j2 = j / 1000;
        return MINUTE_SECONDS.format(new Object[]{new Long(j2 / 60), new Long(j2 % 60)});
    }

    private static DateFormat createDateFormat(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        simpleDateFormat.setLenient(true);
        return simpleDateFormat;
    }

    public static int getPhaseOfMoon(Calendar calendar) {
        int i = calendar.get(6);
        int i2 = ((calendar.get(1) - 1900) % 19) + 1;
        int i3 = ((i2 * 11) + 18) % 30;
        if ((i3 == 25 && i2 > 11) || i3 == 24) {
            i3++;
        }
        return (((((i + i3) * 6) + 11) % 177) / 22) & 7;
    }

    public static String getDateForHeader() {
        String str;
        Calendar calendar = Calendar.getInstance();
        int offset = calendar.getTimeZone().getOffset(calendar.get(0), calendar.get(1), calendar.get(2), calendar.get(5), calendar.get(7), calendar.get(14));
        StringBuffer stringBuffer = new StringBuffer(offset < 0 ? "-" : "+");
        int iAbs = Math.abs(offset);
        int i = iAbs / 3600000;
        int i2 = (iAbs / 60000) - (i * 60);
        if (i < 10) {
            stringBuffer.append("0");
        }
        stringBuffer.append(i);
        if (i2 < 10) {
            stringBuffer.append("0");
        }
        stringBuffer.append(i2);
        DateFormat dateFormat = DATE_HEADER_FORMAT_INT;
        synchronized (dateFormat) {
            str = dateFormat.format(calendar.getTime()) + stringBuffer.toString();
        }
        return str;
    }

    public static Date parseDateFromHeader(String str) throws ParseException {
        Date date;
        DateFormat dateFormat = DATE_HEADER_FORMAT_INT;
        synchronized (dateFormat) {
            date = dateFormat.parse(str);
        }
        return date;
    }

    public static Date parseIso8601DateTime(String str) throws ParseException {
        return new SimpleDateFormat(ISO8601_DATETIME_PATTERN).parse(str);
    }

    public static Date parseIso8601Date(String str) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(str);
    }

    public static Date parseIso8601DateTimeOrDate(String str) throws ParseException {
        try {
            return parseIso8601DateTime(str);
        } catch (ParseException unused) {
            return parseIso8601Date(str);
        }
    }
}
