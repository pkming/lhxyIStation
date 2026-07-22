package android.text.format;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import libcore.icu.DateIntervalFormat;
import libcore.icu.LocaleData;

/* JADX INFO: loaded from: classes.dex */
public class DateUtils {

    @Deprecated
    public static final String ABBREV_MONTH_FORMAT = "%b";
    public static final String ABBREV_WEEKDAY_FORMAT = "%a";
    public static final long DAY_IN_MILLIS = 86400000;

    @Deprecated
    public static final int FORMAT_12HOUR = 64;

    @Deprecated
    public static final int FORMAT_24HOUR = 128;
    public static final int FORMAT_ABBREV_ALL = 524288;
    public static final int FORMAT_ABBREV_MONTH = 65536;
    public static final int FORMAT_ABBREV_RELATIVE = 262144;
    public static final int FORMAT_ABBREV_TIME = 16384;
    public static final int FORMAT_ABBREV_WEEKDAY = 32768;

    @Deprecated
    public static final int FORMAT_CAP_AMPM = 256;

    @Deprecated
    public static final int FORMAT_CAP_MIDNIGHT = 4096;

    @Deprecated
    public static final int FORMAT_CAP_NOON = 1024;

    @Deprecated
    public static final int FORMAT_CAP_NOON_MIDNIGHT = 5120;
    public static final int FORMAT_NO_MIDNIGHT = 2048;
    public static final int FORMAT_NO_MONTH_DAY = 32;
    public static final int FORMAT_NO_NOON = 512;

    @Deprecated
    public static final int FORMAT_NO_NOON_MIDNIGHT = 2560;
    public static final int FORMAT_NO_YEAR = 8;
    public static final int FORMAT_NUMERIC_DATE = 131072;
    public static final int FORMAT_SHOW_DATE = 16;
    public static final int FORMAT_SHOW_TIME = 1;
    public static final int FORMAT_SHOW_WEEKDAY = 2;
    public static final int FORMAT_SHOW_YEAR = 4;

    @Deprecated
    public static final int FORMAT_UTC = 8192;
    public static final long HOUR_IN_MILLIS = 3600000;

    @Deprecated
    public static final String HOUR_MINUTE_24 = "%H:%M";

    @Deprecated
    public static final int LENGTH_LONG = 10;

    @Deprecated
    public static final int LENGTH_MEDIUM = 20;

    @Deprecated
    public static final int LENGTH_SHORT = 30;

    @Deprecated
    public static final int LENGTH_SHORTER = 40;

    @Deprecated
    public static final int LENGTH_SHORTEST = 50;
    public static final long MINUTE_IN_MILLIS = 60000;
    public static final String MONTH_DAY_FORMAT = "%-d";
    public static final String MONTH_FORMAT = "%B";
    public static final String NUMERIC_MONTH_FORMAT = "%m";
    public static final long SECOND_IN_MILLIS = 1000;
    public static final String WEEKDAY_FORMAT = "%A";
    public static final long WEEK_IN_MILLIS = 604800000;
    public static final String YEAR_FORMAT = "%Y";
    public static final String YEAR_FORMAT_TWO_DIGITS = "%g";
    public static final long YEAR_IN_MILLIS = 31449600000L;
    private static String sElapsedFormatHMMSS;
    private static String sElapsedFormatMMSS;
    private static Configuration sLastConfig;
    private static final Object sLock = new Object();
    private static Time sNowTime;
    private static Time sThenTime;
    public static final int[] sameMonthTable = null;
    public static final int[] sameYearTable = null;

    @Deprecated
    public static String getDayOfWeekString(int i, int i2) {
        String[] strArr;
        LocaleData localeData = LocaleData.get(Locale.getDefault());
        if (i2 == 10) {
            strArr = localeData.longWeekdayNames;
        } else if (i2 != 20 && i2 != 30 && i2 != 40 && i2 == 50) {
            strArr = localeData.tinyWeekdayNames;
        } else {
            strArr = localeData.shortWeekdayNames;
        }
        return strArr[i];
    }

    @Deprecated
    public static String getAMPMString(int i) {
        return LocaleData.get(Locale.getDefault()).amPm[i + 0];
    }

    @Deprecated
    public static String getMonthString(int i, int i2) {
        String[] strArr;
        LocaleData localeData = LocaleData.get(Locale.getDefault());
        if (i2 == 10) {
            strArr = localeData.longMonthNames;
        } else if (i2 != 20 && i2 != 30 && i2 != 40 && i2 == 50) {
            strArr = localeData.tinyMonthNames;
        } else {
            strArr = localeData.shortMonthNames;
        }
        return strArr[i];
    }

    public static CharSequence getRelativeTimeSpanString(long j) {
        return getRelativeTimeSpanString(j, System.currentTimeMillis(), 60000L);
    }

    public static CharSequence getRelativeTimeSpanString(long j, long j2, long j3) {
        return getRelativeTimeSpanString(j, j2, j3, 65556);
    }

    public static CharSequence getRelativeTimeSpanString(long j, long j2, long j3, int i) {
        long j4;
        int i2;
        Resources system = Resources.getSystem();
        boolean z = (i & 786432) != 0;
        boolean z2 = j2 >= j;
        long jAbs = Math.abs(j2 - j);
        if (jAbs < 60000 && j3 < 60000) {
            j4 = jAbs / 1000;
            i2 = z2 ? z ? 18022410 : 18022401 : z ? 18022414 : 18022406;
        } else if (jAbs < 3600000 && j3 < 3600000) {
            j4 = jAbs / 60000;
            i2 = z2 ? z ? 18022411 : 18022402 : z ? 18022415 : 18022407;
        } else {
            if (jAbs >= 86400000 || j3 >= 86400000) {
                if (jAbs < 604800000 && j3 < 604800000) {
                    return getRelativeDayString(system, j, j2);
                }
                return formatDateRange(null, j, j, i);
            }
            j4 = jAbs / 3600000;
            i2 = z2 ? z ? 18022412 : 18022403 : z ? 18022416 : 18022408;
        }
        return String.format(system.getQuantityString(i2, (int) j4), Long.valueOf(j4));
    }

    public static CharSequence getRelativeDateTimeString(Context context, long j, long j2, long j3, int i) {
        Resources system = Resources.getSystem();
        long jCurrentTimeMillis = System.currentTimeMillis();
        long jAbs = Math.abs(jCurrentTimeMillis - j);
        long j4 = j3 > 604800000 ? 604800000L : j3 < 86400000 ? 86400000L : j3;
        String dateRange = formatDateRange(context, j, j, 1);
        if (jAbs < j4) {
            return system.getString(17040354, getRelativeTimeSpanString(j, jCurrentTimeMillis, j2, i), dateRange);
        }
        return system.getString(17039425, getRelativeTimeSpanString(context, j, false), dateRange);
    }

    private static final String getRelativeDayString(Resources resources, long j, long j2) {
        Locale locale = resources.getConfiguration().locale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Time time = new Time();
        time.set(j);
        int julianDay = Time.getJulianDay(j, time.gmtoff);
        Time time2 = new Time();
        time2.set(j2);
        int iAbs = Math.abs(Time.getJulianDay(j2, time2.gmtoff) - julianDay);
        boolean z = j2 > j;
        if (iAbs == 1) {
            if (z) {
                return LocaleData.get(locale).yesterday;
            }
            return LocaleData.get(locale).tomorrow;
        }
        if (iAbs == 0) {
            return LocaleData.get(locale).today;
        }
        return String.format(resources.getQuantityString(z ? 18022405 : 18022409, iAbs), Integer.valueOf(iAbs));
    }

    private static void initFormatStrings() {
        synchronized (sLock) {
            initFormatStringsLocked();
        }
    }

    private static void initFormatStringsLocked() {
        Resources system = Resources.getSystem();
        Configuration configuration = system.getConfiguration();
        Configuration configuration2 = sLastConfig;
        if (configuration2 == null || !configuration2.equals(configuration)) {
            sLastConfig = configuration;
            sElapsedFormatMMSS = system.getString(17040359);
            sElapsedFormatHMMSS = system.getString(17040360);
        }
    }

    public static CharSequence formatDuration(long j) {
        Resources system = Resources.getSystem();
        if (j >= 3600000) {
            int i = (int) ((j + AlarmManager.INTERVAL_HALF_HOUR) / 3600000);
            return system.getQuantityString(18022420, i, Integer.valueOf(i));
        }
        if (j >= 60000) {
            int i2 = (int) ((j + 30000) / 60000);
            return system.getQuantityString(18022419, i2, Integer.valueOf(i2));
        }
        int i3 = (int) ((j + 500) / 1000);
        return system.getQuantityString(18022418, i3, Integer.valueOf(i3));
    }

    public static String formatElapsedTime(long j) {
        return formatElapsedTime(null, j);
    }

    public static String formatElapsedTime(StringBuilder sb, long j) {
        long j2;
        long j3;
        if (j >= 3600) {
            j2 = j / 3600;
            j -= 3600 * j2;
        } else {
            j2 = 0;
        }
        if (j >= 60) {
            j3 = j / 60;
            j -= 60 * j3;
        } else {
            j3 = 0;
        }
        if (sb == null) {
            sb = new StringBuilder(8);
        } else {
            sb.setLength(0);
        }
        java.util.Formatter formatter = new java.util.Formatter(sb, Locale.getDefault());
        initFormatStrings();
        return j2 > 0 ? formatter.format(sElapsedFormatHMMSS, Long.valueOf(j2), Long.valueOf(j3), Long.valueOf(j)).toString() : formatter.format(sElapsedFormatMMSS, Long.valueOf(j3), Long.valueOf(j)).toString();
    }

    public static final CharSequence formatSameDayTime(long j, long j2, int i, int i2) {
        java.text.DateFormat dateInstance;
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(j);
        Date time = gregorianCalendar.getTime();
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar2.setTimeInMillis(j2);
        if (gregorianCalendar.get(1) == gregorianCalendar2.get(1) && gregorianCalendar.get(2) == gregorianCalendar2.get(2) && gregorianCalendar.get(5) == gregorianCalendar2.get(5)) {
            dateInstance = java.text.DateFormat.getTimeInstance(i2);
        } else {
            dateInstance = java.text.DateFormat.getDateInstance(i);
        }
        return dateInstance.format(time);
    }

    public static boolean isToday(long j) {
        Time time = new Time();
        time.set(j);
        int i = time.year;
        int i2 = time.month;
        int i3 = time.monthDay;
        time.set(System.currentTimeMillis());
        return i == time.year && i2 == time.month && i3 == time.monthDay;
    }

    public static String formatDateRange(Context context, long j, long j2, int i) {
        return formatDateRange(context, new java.util.Formatter(new StringBuilder(50), Locale.getDefault()), j, j2, i).toString();
    }

    public static java.util.Formatter formatDateRange(Context context, java.util.Formatter formatter, long j, long j2, int i) {
        return formatDateRange(context, formatter, j, j2, i, null);
    }

    public static java.util.Formatter formatDateRange(Context context, java.util.Formatter formatter, long j, long j2, int i, String str) {
        if ((i & 193) == 1) {
            i |= DateFormat.is24HourFormat(context) ? 128 : 64;
        }
        try {
            formatter.out().append(DateIntervalFormat.formatDateRange(j, j2, i, str));
            return formatter;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static String formatDateTime(Context context, long j, int i) {
        return formatDateRange(context, j, j, i);
    }

    public static CharSequence getRelativeTimeSpanString(Context context, long j, boolean z) {
        String dateRange;
        long jCurrentTimeMillis = System.currentTimeMillis();
        long jAbs = Math.abs(jCurrentTimeMillis - j);
        synchronized (DateUtils.class) {
            if (sNowTime == null) {
                sNowTime = new Time();
            }
            if (sThenTime == null) {
                sThenTime = new Time();
            }
            sNowTime.set(jCurrentTimeMillis);
            sThenTime.set(j);
            int i = 17040339;
            if (jAbs < 86400000 && sNowTime.weekDay == sThenTime.weekDay) {
                dateRange = formatDateRange(context, j, j, 1);
                i = 17040340;
            } else if (sNowTime.year != sThenTime.year) {
                dateRange = formatDateRange(context, j, j, 131092);
            } else {
                dateRange = formatDateRange(context, j, j, 65552);
            }
            if (z) {
                dateRange = context.getResources().getString(i, dateRange);
            }
        }
        return dateRange;
    }

    public static CharSequence getRelativeTimeSpanString(Context context, long j) {
        return getRelativeTimeSpanString(context, j, false);
    }
}
