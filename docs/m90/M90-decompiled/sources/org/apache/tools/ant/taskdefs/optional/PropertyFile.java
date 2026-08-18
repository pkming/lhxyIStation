package org.apache.tools.ant.taskdefs.optional;

import com.lianhexinye.m90.gps.Function;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.LayoutPreservingProperties;

/* JADX INFO: loaded from: classes3.dex */
public class PropertyFile extends Task {
    private String comment;
    private Vector entries = new Vector();
    private Properties properties;
    private File propertyfile;
    private boolean useJDKProperties;

    private boolean checkParam(File file) {
        return file != null;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        checkParameters();
        readFile();
        executeOperation();
        writeFile();
    }

    public Entry createEntry() {
        Entry entry = new Entry();
        this.entries.addElement(entry);
        return entry;
    }

    private void executeOperation() throws BuildException {
        Enumeration enumerationElements = this.entries.elements();
        while (enumerationElements.hasMoreElements()) {
            ((Entry) enumerationElements.nextElement()).executeOn(this.properties);
        }
    }

    private void readFile() throws Throwable {
        Throwable th;
        FileOutputStream fileOutputStream;
        Throwable th2;
        FileInputStream fileInputStream;
        if (this.useJDKProperties) {
            this.properties = new Properties();
        } else {
            this.properties = new LayoutPreservingProperties();
        }
        try {
            if (this.propertyfile.exists()) {
                log("Updating property file: " + this.propertyfile.getAbsolutePath());
                try {
                    fileInputStream = new FileInputStream(this.propertyfile);
                    try {
                        this.properties.load(new BufferedInputStream(fileInputStream));
                        fileInputStream.close();
                    } catch (Throwable th3) {
                        th2 = th3;
                        if (fileInputStream == null) {
                            throw th2;
                        }
                        fileInputStream.close();
                        throw th2;
                    }
                } catch (Throwable th4) {
                    th2 = th4;
                    fileInputStream = null;
                }
            } else {
                log("Creating new property file: " + this.propertyfile.getAbsolutePath());
                try {
                    fileOutputStream = new FileOutputStream(this.propertyfile.getAbsolutePath());
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (Throwable th5) {
                        th = th5;
                        if (fileOutputStream == null) {
                            throw th;
                        }
                        fileOutputStream.close();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    fileOutputStream = null;
                }
            }
        } catch (IOException e) {
            throw new BuildException(e.toString());
        }
    }

    private void checkParameters() throws BuildException {
        if (!checkParam(this.propertyfile)) {
            throw new BuildException("file token must not be null.", getLocation());
        }
    }

    public void setFile(File file) {
        this.propertyfile = file;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setJDKProperties(boolean z) {
        this.useJDKProperties = z;
    }

    private void writeFile() throws BuildException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            this.properties.store(byteArrayOutputStream, this.comment);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(this.propertyfile);
                try {
                    try {
                        fileOutputStream.write(byteArrayOutputStream.toByteArray());
                    } finally {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    FileUtils.getFileUtils().tryHardToDelete(this.propertyfile);
                    throw e;
                }
            } catch (IOException e2) {
                throw new BuildException(e2, getLocation());
            }
        } catch (IOException e3) {
            throw new BuildException(e3, getLocation());
        }
    }

    public static class Entry {
        private static final String DEFAULT_DATE_VALUE = "now";
        private static final int DEFAULT_INT_VALUE = 0;
        private static final String DEFAULT_STRING_VALUE = "";
        private String key = null;
        private int type = 2;
        private int operation = 2;
        private String value = null;
        private String defaultValue = null;
        private String newValue = null;
        private String pattern = null;
        private int field = 5;

        public void setKey(String str) {
            this.key = str;
        }

        public void setValue(String str) {
            this.value = str;
        }

        public void setOperation(Operation operation) {
            this.operation = Operation.toOperation(operation.getValue());
        }

        public void setType(Type type) {
            this.type = Type.toType(type.getValue());
        }

        public void setDefault(String str) {
            this.defaultValue = str;
        }

        public void setPattern(String str) {
            this.pattern = str;
        }

        public void setUnit(Unit unit) {
            this.field = unit.getCalendarField();
        }

        protected void executeOn(Properties properties) throws BuildException {
            checkParameters();
            if (this.operation == 3) {
                properties.remove(this.key);
                return;
            }
            String str = (String) properties.get(this.key);
            try {
                int i = this.type;
                if (i == 0) {
                    executeInteger(str);
                } else if (i == 1) {
                    executeDate(str);
                } else if (i == 2) {
                    executeString(str);
                } else {
                    throw new BuildException("Unknown operation type: " + this.type);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (this.newValue == null) {
                this.newValue = "";
            }
            properties.put(this.key, this.newValue);
        }

        private void executeDate(String str) throws BuildException {
            Calendar calendar = Calendar.getInstance();
            if (this.pattern == null) {
                this.pattern = Function.FORMAT_DATE1_TIME;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.pattern);
            String currentValue = getCurrentValue(str);
            if (currentValue == null) {
                currentValue = DEFAULT_DATE_VALUE;
            }
            if (DEFAULT_DATE_VALUE.equals(currentValue)) {
                calendar.setTime(new Date());
            } else {
                try {
                    calendar.setTime(simpleDateFormat.parse(currentValue));
                } catch (ParseException unused) {
                }
            }
            if (this.operation != 2) {
                try {
                    int i = Integer.parseInt(this.value);
                    if (this.operation == 1) {
                        i *= -1;
                    }
                    calendar.add(this.field, i);
                } catch (NumberFormatException unused2) {
                    throw new BuildException("Value not an integer on " + this.key);
                }
            }
            this.newValue = simpleDateFormat.format(calendar.getTime());
        }

        /* JADX WARN: Removed duplicated region for block: B:22:0x003c  */
        /* JADX WARN: Removed duplicated region for block: B:23:0x003f  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private void executeInteger(java.lang.String r6) throws org.apache.tools.ant.BuildException {
            /*
                r5 = this;
                java.lang.String r0 = r5.pattern
                if (r0 == 0) goto Lc
                java.text.DecimalFormat r0 = new java.text.DecimalFormat
                java.lang.String r1 = r5.pattern
                r0.<init>(r1)
                goto L11
            Lc:
                java.text.DecimalFormat r0 = new java.text.DecimalFormat
                r0.<init>()
            L11:
                r1 = 0
                java.lang.String r6 = r5.getCurrentValue(r6)     // Catch: java.lang.Throwable -> L21
                if (r6 == 0) goto L21
                java.lang.Number r6 = r0.parse(r6)     // Catch: java.lang.Throwable -> L21
                int r6 = r6.intValue()     // Catch: java.lang.Throwable -> L21
                goto L22
            L21:
                r6 = r1
            L22:
                int r2 = r5.operation
                r3 = 2
                if (r2 != r3) goto L29
                r1 = r6
                goto L43
            L29:
                java.lang.String r2 = r5.value
                r3 = 1
                if (r2 == 0) goto L37
                java.lang.Number r2 = r0.parse(r2)     // Catch: java.lang.Throwable -> L37
                int r2 = r2.intValue()     // Catch: java.lang.Throwable -> L37
                goto L38
            L37:
                r2 = r3
            L38:
                int r4 = r5.operation
                if (r4 != 0) goto L3f
                int r1 = r6 + r2
                goto L43
            L3f:
                if (r4 != r3) goto L43
                int r1 = r6 - r2
            L43:
                long r1 = (long) r1
                java.lang.String r6 = r0.format(r1)
                r5.newValue = r6
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.optional.PropertyFile.Entry.executeInteger(java.lang.String):void");
        }

        private void executeString(String str) throws BuildException {
            String currentValue = getCurrentValue(str);
            String str2 = "";
            if (currentValue == null) {
                currentValue = "";
            }
            int i = this.operation;
            if (i == 2) {
                str2 = currentValue;
            } else if (i == 0) {
                str2 = currentValue + this.value;
            }
            this.newValue = str2;
        }

        private void checkParameters() throws BuildException {
            int i = this.type;
            if (i == 2 && this.operation == 1) {
                throw new BuildException("- is not supported for string properties (key:" + this.key + ")");
            }
            if (this.value == null && this.defaultValue == null && this.operation != 3) {
                throw new BuildException("\"value\" and/or \"default\" attribute must be specified (key:" + this.key + ")");
            }
            if (this.key == null) {
                throw new BuildException("key is mandatory");
            }
            if (i == 2 && this.pattern != null) {
                throw new BuildException("pattern is not supported for string properties (key:" + this.key + ")");
            }
        }

        private String getCurrentValue(String str) {
            String str2;
            String str3;
            if (this.operation != 2) {
                if (str == null) {
                    str = this.defaultValue;
                }
                return str;
            }
            String str4 = this.value;
            String str5 = (str4 == null || this.defaultValue != null) ? null : str4;
            if (str4 == null && this.defaultValue != null && str != null) {
                str5 = str;
            }
            if (str4 == null && (str3 = this.defaultValue) != null && str == null) {
                str5 = str3;
            }
            if (str4 != null && this.defaultValue != null && str != null) {
                str5 = str4;
            }
            return (str4 == null || (str2 = this.defaultValue) == null || str != null) ? str5 : str2;
        }

        public static class Operation extends EnumeratedAttribute {
            public static final int DECREMENT_OPER = 1;
            public static final int DELETE_OPER = 3;
            public static final int EQUALS_OPER = 2;
            public static final int INCREMENT_OPER = 0;

            @Override // org.apache.tools.ant.types.EnumeratedAttribute
            public String[] getValues() {
                return new String[]{"+", "-", "=", "del"};
            }

            public static int toOperation(String str) {
                if ("+".equals(str)) {
                    return 0;
                }
                if ("-".equals(str)) {
                    return 1;
                }
                return "del".equals(str) ? 3 : 2;
            }
        }

        public static class Type extends EnumeratedAttribute {
            public static final int DATE_TYPE = 1;
            public static final int INTEGER_TYPE = 0;
            public static final int STRING_TYPE = 2;

            @Override // org.apache.tools.ant.types.EnumeratedAttribute
            public String[] getValues() {
                return new String[]{"int", "date", "string"};
            }

            public static int toType(String str) {
                if ("int".equals(str)) {
                    return 0;
                }
                return "date".equals(str) ? 1 : 2;
            }
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
            return ((Integer) this.calendarFields.get(getValue().toLowerCase())).intValue();
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return UNITS;
        }
    }
}
