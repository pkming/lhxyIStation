package android.text.format;

import android.content.res.Resources;
import android.net.wifi.BatchedScanSettings;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import libcore.icu.LocaleData;

/* JADX INFO: loaded from: classes.dex */
public class Time {
    public static final int EPOCH_JULIAN_DAY = 2440588;
    public static final int FRIDAY = 5;
    public static final int HOUR = 3;
    public static final int MINUTE = 2;
    public static final int MONDAY = 1;
    public static final int MONDAY_BEFORE_JULIAN_EPOCH = 2440585;
    public static final int MONTH = 5;
    public static final int MONTH_DAY = 4;
    public static final int SATURDAY = 6;
    public static final int SECOND = 1;
    public static final int SUNDAY = 0;
    public static final int THURSDAY = 4;
    public static final String TIMEZONE_UTC = "UTC";
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int WEEK_DAY = 7;
    public static final int WEEK_NUM = 9;
    public static final int YEAR = 6;
    public static final int YEAR_DAY = 8;
    private static final String Y_M_D = "%Y-%m-%d";
    private static final String Y_M_D_T_H_M_S_000 = "%Y-%m-%dT%H:%M:%S.000";
    private static final String Y_M_D_T_H_M_S_000_Z = "%Y-%m-%dT%H:%M:%S.000Z";
    private static String sAm = null;
    private static String sDateCommand = "%a %b %e %H:%M:%S %Z %Y";
    private static String sDateOnlyFormat;
    private static String sDateTimeFormat;
    private static Locale sLocale;
    private static String[] sLongMonths;
    private static String[] sLongStandaloneMonths;
    private static String[] sLongWeekdays;
    private static String sPm;
    private static String[] sShortMonths;
    private static String[] sShortWeekdays;
    private static String sTimeOnlyFormat;
    private static char sZeroDigit;
    public boolean allDay;
    public long gmtoff;
    public int hour;
    public int isDst;
    public int minute;
    public int month;
    public int monthDay;
    public int second;
    public String timezone;
    public int weekDay;
    public int year;
    public int yearDay;
    private static final int[] DAYS_PER_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] sThursdayOffset = {-3, 3, 2, 1, 0, -1, -2};

    private native String format1(String str);

    public static int getJulianMondayFromWeeksSinceEpoch(int i) {
        return (i * 7) + MONDAY_BEFORE_JULIAN_EPOCH;
    }

    private static native int nativeCompare(Time time, Time time2);

    private native boolean nativeParse(String str);

    private native boolean nativeParse3339(String str);

    public native String format2445();

    public native long normalize(boolean z);

    public native void set(long j);

    public native void setToNow();

    public native void switchTimezone(String str);

    public native long toMillis(boolean z);

    public native String toString();

    public Time(String str) {
        Objects.requireNonNull(str, "timezone is null!");
        this.timezone = str;
        this.year = 1970;
        this.monthDay = 1;
        this.isDst = -1;
    }

    public Time() {
        this(TimeZone.getDefault().getID());
    }

    public Time(Time time) {
        set(time);
    }

    public int getActualMaximum(int i) {
        switch (i) {
            case 1:
            case 2:
                return 59;
            case 3:
                return 23;
            case 4:
                int i2 = DAYS_PER_MONTH[this.month];
                if (i2 != 28) {
                    return i2;
                }
                int i3 = this.year;
                if (i3 % 4 == 0) {
                    return (i3 % 100 != 0 || i3 % 400 == 0) ? 29 : 28;
                }
                return 28;
            case 5:
                return 11;
            case 6:
                return 2037;
            case 7:
                return 6;
            case 8:
                int i4 = this.year;
                return (i4 % 4 != 0 || (i4 % 100 == 0 && i4 % 400 != 0)) ? 364 : 365;
            case 9:
                throw new RuntimeException("WEEK_NUM not implemented");
            default:
                throw new RuntimeException("bad field=" + i);
        }
    }

    public void clear(String str) {
        Objects.requireNonNull(str, "timezone is null!");
        this.timezone = str;
        this.allDay = false;
        this.second = 0;
        this.minute = 0;
        this.hour = 0;
        this.monthDay = 0;
        this.month = 0;
        this.year = 0;
        this.weekDay = 0;
        this.yearDay = 0;
        this.gmtoff = 0L;
        this.isDst = -1;
    }

    public static int compare(Time time, Time time2) {
        Objects.requireNonNull(time, "a == null");
        Objects.requireNonNull(time2, "b == null");
        return nativeCompare(time, time2);
    }

    public String format(String str) {
        String strLocalizeDigits;
        synchronized (Time.class) {
            Locale locale = Locale.getDefault();
            Locale locale2 = sLocale;
            if (locale2 == null || locale == null || !locale.equals(locale2)) {
                LocaleData localeData = LocaleData.get(locale);
                sAm = localeData.amPm[0];
                sPm = localeData.amPm[1];
                sZeroDigit = localeData.zeroDigit;
                sShortMonths = localeData.shortMonthNames;
                sLongMonths = localeData.longMonthNames;
                sLongStandaloneMonths = localeData.longStandAloneMonthNames;
                sShortWeekdays = localeData.shortWeekdayNames;
                sLongWeekdays = localeData.longWeekdayNames;
                Resources system = Resources.getSystem();
                sTimeOnlyFormat = system.getString(17039423);
                sDateOnlyFormat = system.getString(17039422);
                sDateTimeFormat = system.getString(17039424);
                sLocale = locale;
            }
            strLocalizeDigits = format1(str);
            if (sZeroDigit != '0') {
                strLocalizeDigits = localizeDigits(strLocalizeDigits);
            }
        }
        return strLocalizeDigits;
    }

    private String localizeDigits(String str) {
        int length = str.length();
        int i = sZeroDigit - '0';
        StringBuilder sb = new StringBuilder(length);
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt >= '0' && cCharAt <= '9') {
                cCharAt = (char) (cCharAt + i);
            }
            sb.append(cCharAt);
        }
        return sb.toString();
    }

    public boolean parse(String str) {
        Objects.requireNonNull(str, "time string is null");
        if (!nativeParse(str)) {
            return false;
        }
        this.timezone = TIMEZONE_UTC;
        return true;
    }

    public boolean parse3339(String str) {
        Objects.requireNonNull(str, "time string is null");
        if (!nativeParse3339(str)) {
            return false;
        }
        this.timezone = TIMEZONE_UTC;
        return true;
    }

    public static String getCurrentTimezone() {
        return TimeZone.getDefault().getID();
    }

    public void set(Time time) {
        this.timezone = time.timezone;
        this.allDay = time.allDay;
        this.second = time.second;
        this.minute = time.minute;
        this.hour = time.hour;
        this.monthDay = time.monthDay;
        this.month = time.month;
        this.year = time.year;
        this.weekDay = time.weekDay;
        this.yearDay = time.yearDay;
        this.isDst = time.isDst;
        this.gmtoff = time.gmtoff;
    }

    public void set(int i, int i2, int i3, int i4, int i5, int i6) {
        this.allDay = false;
        this.second = i;
        this.minute = i2;
        this.hour = i3;
        this.monthDay = i4;
        this.month = i5;
        this.year = i6;
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0L;
    }

    public void set(int i, int i2, int i3) {
        this.allDay = true;
        this.second = 0;
        this.minute = 0;
        this.hour = 0;
        this.monthDay = i;
        this.month = i2;
        this.year = i3;
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0L;
    }

    public boolean before(Time time) {
        return compare(this, time) < 0;
    }

    public boolean after(Time time) {
        return compare(this, time) > 0;
    }

    public int getWeekNumber() {
        int i;
        int i2 = this.yearDay;
        int[] iArr = sThursdayOffset;
        int i3 = i2 + iArr[this.weekDay];
        if (i3 >= 0 && i3 <= 364) {
            i = i3 / 7;
        } else {
            Time time = new Time(this);
            time.monthDay += iArr[this.weekDay];
            time.normalize(true);
            i = time.yearDay / 7;
        }
        return i + 1;
    }

    public String format3339(boolean z) {
        if (z) {
            return format(Y_M_D);
        }
        if (TIMEZONE_UTC.equals(this.timezone)) {
            return format(Y_M_D_T_H_M_S_000_Z);
        }
        String str = format(Y_M_D_T_H_M_S_000);
        long j = this.gmtoff;
        String str2 = j < 0 ? "-" : "+";
        int iAbs = (int) Math.abs(j);
        return String.format(Locale.US, "%s%s%02d:%02d", str, str2, Integer.valueOf(iAbs / BatchedScanSettings.MAX_INTERVAL_SEC), Integer.valueOf((iAbs % BatchedScanSettings.MAX_INTERVAL_SEC) / 60));
    }

    public static boolean isEpoch(Time time) {
        return getJulianDay(time.toMillis(true), 0L) == 2440588;
    }

    public static int getJulianDay(long j, long j2) {
        return ((int) ((j + (j2 * 1000)) / 86400000)) + EPOCH_JULIAN_DAY;
    }

    public long setJulianDay(int i) {
        long j = ((long) (i - EPOCH_JULIAN_DAY)) * 86400000;
        set(j);
        this.monthDay += i - getJulianDay(j, this.gmtoff);
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        return normalize(true);
    }

    public static int getWeeksSinceEpochFromJulianDay(int i, int i2) {
        int i3 = 4 - i2;
        if (i3 < 0) {
            i3 += 7;
        }
        return (i - (EPOCH_JULIAN_DAY - i3)) / 7;
    }
}
