package android.util;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.SystemClock;
import android.text.format.DateFormat;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import libcore.util.ZoneInfoDB;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class TimeUtils {
    private static final boolean DBG = false;
    public static final int HUNDRED_DAY_FIELD_LEN = 19;
    private static final long LARGEST_DURATION = 86399999999L;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final String TAG = "TimeUtils";
    private static String sLastCountry;
    private static String sLastUniqueCountry;
    private static ArrayList<TimeZone> sLastUniqueZoneOffsets;
    private static ArrayList<TimeZone> sLastZones;
    private static final Object sLastLockObj = new Object();
    private static final Object sLastUniqueLockObj = new Object();
    private static final Object sFormatSync = new Object();
    private static char[] sFormatStr = new char[24];

    private static int accumField(int i, int i2, boolean z, int i3) {
        if (i > 99 || (z && i3 >= 3)) {
            return i2 + 3;
        }
        if (i > 9 || (z && i3 >= 2)) {
            return i2 + 2;
        }
        if (z || i > 0) {
            return i2 + 1;
        }
        return 0;
    }

    public static TimeZone getTimeZone(int i, boolean z, long j, String str) {
        Resources.getSystem().getXml(17760273);
        Date date = new Date(j);
        TimeZone timeZone = TimeZone.getDefault();
        String id = timeZone.getID();
        int offset = timeZone.getOffset(j);
        boolean zInDaylightTime = timeZone.inDaylightTime(date);
        TimeZone timeZone2 = null;
        for (TimeZone timeZone3 : getTimeZones(str)) {
            if (timeZone3.getID().equals(id) && offset == i && zInDaylightTime == z) {
                return timeZone;
            }
            if (timeZone2 == null && timeZone3.getOffset(j) == i && timeZone3.inDaylightTime(date) == z) {
                timeZone2 = timeZone3;
            }
        }
        return timeZone2;
    }

    public static ArrayList<TimeZone> getTimeZonesWithUniqueOffsets(String str) {
        synchronized (sLastUniqueLockObj) {
            if (str != null) {
                if (str.equals(sLastUniqueCountry)) {
                    return sLastUniqueZoneOffsets;
                }
            }
            ArrayList<TimeZone> timeZones = getTimeZones(str);
            ArrayList<TimeZone> arrayList = new ArrayList<>();
            for (TimeZone timeZone : timeZones) {
                boolean z = false;
                int i = 0;
                while (true) {
                    if (i >= arrayList.size()) {
                        break;
                    }
                    if (arrayList.get(i).getRawOffset() == timeZone.getRawOffset()) {
                        z = true;
                        break;
                    }
                    i++;
                }
                if (!z) {
                    arrayList.add(timeZone);
                }
            }
            synchronized (sLastUniqueLockObj) {
                sLastUniqueZoneOffsets = arrayList;
                sLastUniqueCountry = str;
            }
            return arrayList;
        }
    }

    public static ArrayList<TimeZone> getTimeZones(String str) {
        synchronized (sLastLockObj) {
            if (str != null) {
                if (str.equals(sLastCountry)) {
                    return sLastZones;
                }
            }
            ArrayList<TimeZone> arrayList = new ArrayList<>();
            if (str == null) {
                return arrayList;
            }
            XmlResourceParser xml = Resources.getSystem().getXml(17760273);
            try {
                try {
                    XmlUtils.beginDocument(xml, "timezones");
                    while (true) {
                        XmlUtils.nextElement(xml);
                        String name = xml.getName();
                        if (name == null || !name.equals("timezone")) {
                            break;
                        }
                        if (str.equals(xml.getAttributeValue(null, "code")) && xml.next() == 4) {
                            TimeZone timeZone = TimeZone.getTimeZone(xml.getText());
                            if (!timeZone.getID().startsWith("GMT")) {
                                arrayList.add(timeZone);
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Got IO exception getTimeZone('" + str + "'): e=", e);
                } catch (XmlPullParserException e2) {
                    Log.e(TAG, "Got xml parser exception getTimeZone('" + str + "'): e=", e2);
                }
                synchronized (sLastLockObj) {
                    sLastZones = arrayList;
                    sLastCountry = str;
                }
                return arrayList;
            } finally {
                xml.close();
            }
        }
    }

    public static String getTimeZoneDatabaseVersion() {
        return ZoneInfoDB.getInstance().getVersion();
    }

    private static int printField(char[] cArr, int i, char c, int i2, boolean z, int i3) {
        int i4;
        if (!z && i <= 0) {
            return i2;
        }
        if ((!z || i3 < 3) && i <= 99) {
            i4 = i2;
        } else {
            int i5 = i / 100;
            cArr[i2] = (char) (i5 + 48);
            i4 = i2 + 1;
            i -= i5 * 100;
        }
        if ((z && i3 >= 2) || i > 9 || i2 != i4) {
            int i6 = i / 10;
            cArr[i4] = (char) (i6 + 48);
            i4++;
            i -= i6 * 10;
        }
        cArr[i4] = (char) (i + 48);
        int i7 = i4 + 1;
        cArr[i7] = c;
        return i7 + 1;
    }

    private static int formatDurationLocked(long j, int i) {
        char c;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        long j2 = j;
        if (sFormatStr.length < i) {
            sFormatStr = new char[i];
        }
        char[] cArr = sFormatStr;
        int i7 = 0;
        if (j2 == 0) {
            int i8 = i - 1;
            while (i7 < i8) {
                cArr[i7] = ' ';
                i7++;
            }
            cArr[i7] = '0';
            return i7 + 1;
        }
        if (j2 > 0) {
            c = '+';
        } else {
            c = '-';
            j2 = -j2;
        }
        if (j2 > LARGEST_DURATION) {
            j2 = 86399999999L;
        }
        int i9 = (int) (j2 % 1000);
        int iFloor = (int) Math.floor(j2 / 1000);
        if (iFloor > SECONDS_PER_DAY) {
            i2 = iFloor / SECONDS_PER_DAY;
            iFloor -= SECONDS_PER_DAY * i2;
        } else {
            i2 = 0;
        }
        if (iFloor > 3600) {
            i3 = iFloor / 3600;
            iFloor -= i3 * 3600;
        } else {
            i3 = 0;
        }
        if (iFloor > 60) {
            int i10 = iFloor / 60;
            i4 = iFloor - (i10 * 60);
            i5 = i10;
        } else {
            i4 = iFloor;
            i5 = 0;
        }
        if (i != 0) {
            int iAccumField = accumField(i2, 1, false, 0);
            int iAccumField2 = iAccumField + accumField(i3, 1, iAccumField > 0, 2);
            int iAccumField3 = iAccumField2 + accumField(i5, 1, iAccumField2 > 0, 2);
            int iAccumField4 = iAccumField3 + accumField(i4, 1, iAccumField3 > 0, 2);
            i6 = 0;
            for (int iAccumField5 = iAccumField4 + accumField(i9, 2, true, iAccumField4 > 0 ? 3 : 0) + 1; iAccumField5 < i; iAccumField5++) {
                cArr[i6] = ' ';
                i6++;
            }
        } else {
            i6 = 0;
        }
        cArr[i6] = c;
        int i11 = i6 + 1;
        boolean z = i != 0;
        int iPrintField = printField(cArr, i2, DateFormat.DATE, i11, false, 0);
        int iPrintField2 = printField(cArr, i3, DateFormat.HOUR, iPrintField, iPrintField != i11, z ? 2 : 0);
        int iPrintField3 = printField(cArr, i5, DateFormat.MINUTE, iPrintField2, iPrintField2 != i11, z ? 2 : 0);
        int iPrintField4 = printField(cArr, i4, 's', iPrintField3, iPrintField3 != i11, z ? 2 : 0);
        int iPrintField5 = printField(cArr, i9, DateFormat.MINUTE, iPrintField4, true, (!z || iPrintField4 == i11) ? 0 : 3);
        cArr[iPrintField5] = 's';
        return iPrintField5 + 1;
    }

    public static void formatDuration(long j, StringBuilder sb) {
        synchronized (sFormatSync) {
            sb.append(sFormatStr, 0, formatDurationLocked(j, 0));
        }
    }

    public static void formatDuration(long j, PrintWriter printWriter, int i) {
        synchronized (sFormatSync) {
            printWriter.print(new String(sFormatStr, 0, formatDurationLocked(j, i)));
        }
    }

    public static void formatDuration(long j, PrintWriter printWriter) {
        formatDuration(j, printWriter, 0);
    }

    public static void formatDuration(long j, long j2, PrintWriter printWriter) {
        if (j == 0) {
            printWriter.print("--");
        } else {
            formatDuration(j - j2, printWriter, 0);
        }
    }

    public static String formatUptime(long j) {
        long jUptimeMillis = j - SystemClock.uptimeMillis();
        if (jUptimeMillis > 0) {
            return j + " (in " + jUptimeMillis + " ms)";
        }
        if (jUptimeMillis < 0) {
            return j + " (" + (-jUptimeMillis) + " ms ago)";
        }
        return j + " (now)";
    }

    public static String logTimeOfDay(long j) {
        Calendar calendar = Calendar.getInstance();
        if (j >= 0) {
            calendar.setTimeInMillis(j);
            return String.format("%tm-%td %tH:%tM:%tS.%tL", calendar, calendar, calendar, calendar, calendar, calendar);
        }
        return Long.toString(j);
    }
}
