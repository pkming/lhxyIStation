package org.apache.tools.ant.types;

import android.provider.DocumentsContract;

/* JADX INFO: loaded from: classes3.dex */
public class LogLevel extends EnumeratedAttribute {
    public static final LogLevel ERR = new LogLevel("error");
    public static final LogLevel WARN = new LogLevel("warn");
    public static final LogLevel INFO = new LogLevel(DocumentsContract.EXTRA_INFO);
    public static final LogLevel VERBOSE = new LogLevel("verbose");
    public static final LogLevel DEBUG = new LogLevel("debug");
    private static int[] levels = {0, 1, 1, 2, 3, 4};

    public LogLevel() {
    }

    private LogLevel(String str) {
        this();
        setValue(str);
    }

    @Override // org.apache.tools.ant.types.EnumeratedAttribute
    public String[] getValues() {
        return new String[]{"error", "warn", "warning", DocumentsContract.EXTRA_INFO, "verbose", "debug"};
    }

    public int getLevel() {
        return levels[getIndex()];
    }
}
