package android.text.format;

import android.content.Context;
import android.net.wifi.BatchedScanSettings;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import com.lianhexinye.m90.gps.Function;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import libcore.icu.ICU;
import libcore.icu.LocaleData;

/* JADX INFO: loaded from: classes.dex */
public class DateFormat {

    @Deprecated
    public static final char AM_PM = 'a';

    @Deprecated
    public static final char CAPITAL_AM_PM = 'A';

    @Deprecated
    public static final char DATE = 'd';

    @Deprecated
    public static final char DAY = 'E';

    @Deprecated
    public static final char HOUR = 'h';

    @Deprecated
    public static final char HOUR_OF_DAY = 'k';

    @Deprecated
    public static final char MINUTE = 'm';

    @Deprecated
    public static final char MONTH = 'M';

    @Deprecated
    public static final char QUOTE = '\'';

    @Deprecated
    public static final char SECONDS = 's';

    @Deprecated
    public static final char STANDALONE_MONTH = 'L';

    @Deprecated
    public static final char TIME_ZONE = 'z';

    @Deprecated
    public static final char YEAR = 'y';
    private static boolean sIs24Hour;
    private static Locale sIs24HourLocale;
    private static final Object sLocaleLock = new Object();

    public static boolean is24HourFormat(Context context) {
        boolean zEquals;
        String string = Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24);
        if (string == null) {
            Locale locale = context.getResources().getConfiguration().locale;
            Object obj = sLocaleLock;
            synchronized (obj) {
                Locale locale2 = sIs24HourLocale;
                if (locale2 != null && locale2.equals(locale)) {
                    return sIs24Hour;
                }
                java.text.DateFormat timeInstance = java.text.DateFormat.getTimeInstance(1, locale);
                String str = (!(timeInstance instanceof SimpleDateFormat) || ((SimpleDateFormat) timeInstance).toPattern().indexOf(72) < 0) ? "12" : "24";
                synchronized (obj) {
                    sIs24HourLocale = locale;
                    zEquals = str.equals("24");
                    sIs24Hour = zEquals;
                }
                return zEquals;
            }
        }
        return string.equals("24");
    }

    public static String getBestDateTimePattern(Locale locale, String str) {
        return ICU.getBestDateTimePattern(str, locale.toString());
    }

    public static java.text.DateFormat getTimeFormat(Context context) {
        return new SimpleDateFormat(getTimeFormatString(context));
    }

    public static String getTimeFormatString(Context context) {
        LocaleData localeData = LocaleData.get(context.getResources().getConfiguration().locale);
        return is24HourFormat(context) ? localeData.timeFormat24 : localeData.timeFormat12;
    }

    public static java.text.DateFormat getDateFormat(Context context) {
        return getDateFormatForSetting(context, Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT));
    }

    public static java.text.DateFormat getDateFormatForSetting(Context context, String str) {
        return new SimpleDateFormat(getDateFormatStringForSetting(context, str));
    }

    private static String getDateFormatStringForSetting(Context context, String str) {
        if (str != null) {
            int iIndexOf = str.indexOf(77);
            int iIndexOf2 = str.indexOf(100);
            int iIndexOf3 = str.indexOf(121);
            if (iIndexOf >= 0 && iIndexOf2 >= 0 && iIndexOf3 >= 0) {
                String string = context.getString(17039421);
                if (iIndexOf3 < iIndexOf && iIndexOf3 < iIndexOf2) {
                    if (iIndexOf < iIndexOf2) {
                        return String.format(string, Function.FORMAT_YEAR, "MM", "dd");
                    }
                    return String.format(string, Function.FORMAT_YEAR, "dd", "MM");
                }
                if (iIndexOf < iIndexOf2) {
                    if (iIndexOf2 < iIndexOf3) {
                        return String.format(string, "MM", "dd", Function.FORMAT_YEAR);
                    }
                    return String.format(string, "MM", Function.FORMAT_YEAR, "dd");
                }
                if (iIndexOf < iIndexOf3) {
                    return String.format(string, "dd", "MM", Function.FORMAT_YEAR);
                }
                return String.format(string, "dd", Function.FORMAT_YEAR, "MM");
            }
        }
        return LocaleData.get(context.getResources().getConfiguration().locale).shortDateFormat4;
    }

    public static java.text.DateFormat getLongDateFormat(Context context) {
        return java.text.DateFormat.getDateInstance(1);
    }

    public static java.text.DateFormat getMediumDateFormat(Context context) {
        return java.text.DateFormat.getDateInstance(2);
    }

    public static char[] getDateFormatOrder(Context context) {
        return ICU.getDateFormatOrder(getDateFormatString(context));
    }

    private static String getDateFormatString(Context context) {
        return getDateFormatStringForSetting(context, Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT));
    }

    public static CharSequence format(CharSequence charSequence, long j) {
        return format(charSequence, new Date(j));
    }

    public static CharSequence format(CharSequence charSequence, Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return format(charSequence, gregorianCalendar);
    }

    public static boolean hasSeconds(CharSequence charSequence) {
        return hasDesignator(charSequence, 's');
    }

    public static boolean hasDesignator(CharSequence charSequence, char c) {
        if (charSequence == null) {
            return false;
        }
        int length = charSequence.length();
        int i = 0;
        while (i < length) {
            char cCharAt = charSequence.charAt(i);
            int iSkipQuotedText = 1;
            if (cCharAt == '\'') {
                iSkipQuotedText = skipQuotedText(charSequence, i, length);
            } else if (cCharAt == c) {
                return true;
            }
            i += iSkipQuotedText;
        }
        return false;
    }

    private static int skipQuotedText(CharSequence charSequence, int i, int i2) {
        int i3 = 1;
        int i4 = i + 1;
        if (i4 < i2 && charSequence.charAt(i4) == '\'') {
            return 2;
        }
        while (i4 < i2) {
            if (charSequence.charAt(i4) == '\'') {
                i3++;
                i4++;
                if (i4 >= i2 || charSequence.charAt(i4) != '\'') {
                    break;
                }
            } else {
                i4++;
                i3++;
            }
        }
        return i3;
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00a4  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00b5  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00c0  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00d7  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00e3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.CharSequence format(java.lang.CharSequence r12, java.util.Calendar r13) {
        /*
            Method dump skipped, instruction units count: 258
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.format.DateFormat.format(java.lang.CharSequence, java.util.Calendar):java.lang.CharSequence");
    }

    private static String getDayOfWeekString(LocaleData localeData, int i, int i2, int i3) {
        boolean z = i3 == 99;
        return i2 == 5 ? z ? localeData.tinyStandAloneWeekdayNames[i] : localeData.tinyWeekdayNames[i] : i2 == 4 ? z ? localeData.longStandAloneWeekdayNames[i] : localeData.longWeekdayNames[i] : z ? localeData.shortStandAloneWeekdayNames[i] : localeData.shortWeekdayNames[i];
    }

    private static String getMonthString(LocaleData localeData, int i, int i2, int i3) {
        boolean z = i3 == 76;
        if (i2 == 5) {
            return z ? localeData.tinyStandAloneMonthNames[i] : localeData.tinyMonthNames[i];
        }
        if (i2 == 4) {
            return z ? localeData.longStandAloneMonthNames[i] : localeData.longMonthNames[i];
        }
        if (i2 == 3) {
            return z ? localeData.shortStandAloneMonthNames[i] : localeData.shortMonthNames[i];
        }
        return zeroPad(i + 1, i2);
    }

    private static String getTimeZoneString(Calendar calendar, int i) {
        TimeZone timeZone = calendar.getTimeZone();
        if (i < 2) {
            return formatZoneOffset(calendar.get(16) + calendar.get(15), i);
        }
        return timeZone.getDisplayName(calendar.get(16) != 0, 0);
    }

    private static String formatZoneOffset(int i, int i2) {
        int i3 = i / 1000;
        StringBuilder sb = new StringBuilder();
        if (i3 < 0) {
            sb.insert(0, "-");
            i3 = -i3;
        } else {
            sb.insert(0, "+");
        }
        int i4 = i3 / BatchedScanSettings.MAX_INTERVAL_SEC;
        int i5 = (i3 % BatchedScanSettings.MAX_INTERVAL_SEC) / 60;
        sb.append(zeroPad(i4, 2));
        sb.append(zeroPad(i5, 2));
        return sb.toString();
    }

    private static String getYearString(int i, int i2) {
        return i2 <= 2 ? zeroPad(i % 100, 2) : String.format(Locale.getDefault(), "%d", Integer.valueOf(i));
    }

    private static int appendQuotedText(SpannableStringBuilder spannableStringBuilder, int i, int i2) {
        int i3 = i + 1;
        if (i3 < i2 && spannableStringBuilder.charAt(i3) == '\'') {
            spannableStringBuilder.delete(i, i3);
            return 1;
        }
        int i4 = 0;
        spannableStringBuilder.delete(i, i3);
        int i5 = i2 - 1;
        while (i < i5) {
            if (spannableStringBuilder.charAt(i) == '\'') {
                int i6 = i + 1;
                if (i6 < i5 && spannableStringBuilder.charAt(i6) == '\'') {
                    spannableStringBuilder.delete(i, i6);
                    i5--;
                    i4++;
                    i = i6;
                } else {
                    spannableStringBuilder.delete(i, i6);
                    break;
                }
            } else {
                i++;
                i4++;
            }
        }
        return i4;
    }

    private static String zeroPad(int i, int i2) {
        return String.format(Locale.getDefault(), "%0" + i2 + "d", Integer.valueOf(i));
    }
}
