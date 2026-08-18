package org.apache.poi.hssf.usermodel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFDateUtil {
    private static final int BAD_DATE = -1;
    private static final double CAL_1900_ABSOLUTE = ((double) absoluteDay(new GregorianCalendar(1900, 0, 1))) - 2.0d;
    private static final long DAY_MILLISECONDS = 86400000;

    public static boolean isInternalDateFormat(int i) {
        switch (i) {
            default:
                switch (i) {
                    case 45:
                    case 46:
                    case 47:
                        break;
                    default:
                        return false;
                }
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
                return true;
        }
    }

    public static boolean isValidExcelDate(double d) {
        return d > -4.9E-324d;
    }

    private HSSFDateUtil() {
    }

    public static double getExcelDate(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        if (gregorianCalendar.get(1) < 1900) {
            return -1.0d;
        }
        return ((((double) ((((((gregorianCalendar.get(11) * 60) + gregorianCalendar.get(12)) * 60) + gregorianCalendar.get(13)) * 1000) + gregorianCalendar.get(14))) / 8.64E7d) + ((double) absoluteDay(dayStart(gregorianCalendar)))) - CAL_1900_ABSOLUTE;
    }

    public static Date getJavaDate(double d) {
        return getJavaDate(d, false);
    }

    public static Date getJavaDate(double d, boolean z) {
        if (!isValidExcelDate(d)) {
            return null;
        }
        int i = 1900;
        int i2 = -1;
        int iFloor = (int) Math.floor(d);
        if (z) {
            i = 1904;
            i2 = 1;
        } else if (iFloor < 61) {
            i2 = 0;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar(i, 0, iFloor + i2);
        gregorianCalendar.set(14, (int) (((d - Math.floor(d)) * 8.64E7d) + 0.5d));
        return gregorianCalendar.getTime();
    }

    public static boolean isCellDateFormatted(HSSFCell hSSFCell) {
        if (hSSFCell != null && isValidExcelDate(hSSFCell.getNumericCellValue())) {
            return isInternalDateFormat(hSSFCell.getCellStyle().getDataFormat());
        }
        return false;
    }

    private static int absoluteDay(Calendar calendar) {
        return calendar.get(6) + daysInPriorYears(calendar.get(1));
    }

    private static int daysInPriorYears(int i) {
        if (i < 1601) {
            throw new IllegalArgumentException("'year' must be 1601 or greater");
        }
        int i2 = i - 1601;
        return (((i2 * 365) + (i2 / 4)) - (i2 / 100)) + (i2 / 400);
    }

    private static Calendar dayStart(Calendar calendar) {
        calendar.get(11);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.get(11);
        return calendar;
    }
}
