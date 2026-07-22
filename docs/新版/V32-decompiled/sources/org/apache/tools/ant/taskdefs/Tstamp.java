package org.apache.tools.ant.taskdefs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;

/* JADX INFO: loaded from: classes3.dex */
public class Tstamp extends Task {
    private Vector customFormats = new Vector();
    private String prefix = "";

    public void setPrefix(String str) {
        this.prefix = str;
        if (str.endsWith(".")) {
            return;
        }
        this.prefix += ".";
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        try {
            Date date = new Date();
            Enumeration enumerationElements = this.customFormats.elements();
            while (enumerationElements.hasMoreElements()) {
                ((CustomFormat) enumerationElements.nextElement()).execute(getProject(), date, getLocation());
            }
            setProperty("DSTAMP", new SimpleDateFormat("yyyyMMdd").format(date));
            setProperty("TSTAMP", new SimpleDateFormat("HHmm").format(date));
            setProperty("TODAY", new SimpleDateFormat("MMMM d yyyy", Locale.US).format(date));
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public CustomFormat createFormat() {
        CustomFormat customFormat = new CustomFormat();
        this.customFormats.addElement(customFormat);
        return customFormat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setProperty(String str, String str2) {
        getProject().setNewProperty(this.prefix + str, str2);
    }

    public class CustomFormat {
        private String country;
        private String language;
        private String pattern;
        private String propertyName;
        private TimeZone timeZone;
        private String variant;
        private int offset = 0;
        private int field = 5;

        public CustomFormat() {
        }

        public void setProperty(String str) {
            this.propertyName = str;
        }

        public void setPattern(String str) {
            this.pattern = str;
        }

        public void setLocale(String str) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, " \t\n\r\f,");
            try {
                this.language = stringTokenizer.nextToken();
                if (stringTokenizer.hasMoreElements()) {
                    this.country = stringTokenizer.nextToken();
                    if (stringTokenizer.hasMoreElements()) {
                        this.variant = stringTokenizer.nextToken();
                        if (stringTokenizer.hasMoreElements()) {
                            throw new BuildException("bad locale format", Tstamp.this.getLocation());
                        }
                        return;
                    }
                    return;
                }
                this.country = "";
            } catch (NoSuchElementException e) {
                throw new BuildException("bad locale format", e, Tstamp.this.getLocation());
            }
        }

        public void setTimezone(String str) {
            this.timeZone = TimeZone.getTimeZone(str);
        }

        public void setOffset(int i) {
            this.offset = i;
        }

        public void setUnit(String str) {
            Tstamp.this.log("DEPRECATED - The setUnit(String) method has been deprecated. Use setUnit(Tstamp.Unit) instead.");
            Unit unit = new Unit();
            unit.setValue(str);
            this.field = unit.getCalendarField();
        }

        public void setUnit(Unit unit) {
            this.field = unit.getCalendarField();
        }

        public void execute(Project project, Date date, Location location) {
            SimpleDateFormat simpleDateFormat;
            if (this.propertyName == null) {
                throw new BuildException("property attribute must be provided", location);
            }
            if (this.pattern == null) {
                throw new BuildException("pattern attribute must be provided", location);
            }
            if (this.language == null) {
                simpleDateFormat = new SimpleDateFormat(this.pattern);
            } else if (this.variant == null) {
                simpleDateFormat = new SimpleDateFormat(this.pattern, new Locale(this.language, this.country));
            } else {
                simpleDateFormat = new SimpleDateFormat(this.pattern, new Locale(this.language, this.country, this.variant));
            }
            if (this.offset != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(this.field, this.offset);
                date = calendar.getTime();
            }
            TimeZone timeZone = this.timeZone;
            if (timeZone != null) {
                simpleDateFormat.setTimeZone(timeZone);
            }
            Tstamp.this.setProperty(this.propertyName, simpleDateFormat.format(date));
        }
    }

    public static class Unit extends EnumeratedAttribute {
        private static final String DAY = "day";
        private static final String HOUR = "hour";
        private static final String MILLISECOND = "millisecond";
        private static final String MINUTE = "minute";
        private static final String SECOND = "second";
        private static final String WEEK = "week";
        private static final String YEAR = "year";
        private Map calendarFields;
        private static final String MONTH = "month";
        private static final String[] UNITS = {"millisecond", "second", "minute", "hour", "day", "week", MONTH, "year"};

        public Unit() {
            HashMap map = new HashMap();
            this.calendarFields = map;
            map.put("millisecond", new Integer(14));
            this.calendarFields.put("second", new Integer(13));
            this.calendarFields.put("minute", new Integer(12));
            this.calendarFields.put("hour", new Integer(11));
            this.calendarFields.put("day", new Integer(5));
            this.calendarFields.put("week", new Integer(3));
            this.calendarFields.put(MONTH, new Integer(2));
            this.calendarFields.put("year", new Integer(1));
        }

        public int getCalendarField() {
            return ((Integer) this.calendarFields.get(getValue().toLowerCase(Locale.ENGLISH))).intValue();
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return UNITS;
        }
    }
}
