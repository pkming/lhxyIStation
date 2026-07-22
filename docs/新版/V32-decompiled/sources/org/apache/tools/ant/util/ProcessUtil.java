package org.apache.tools.ant.util;

import java.lang.management.ManagementFactory;

/* JADX INFO: loaded from: classes3.dex */
public class ProcessUtil {
    public static String getProcessId(String str) {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        int iIndexOf = name.indexOf(64);
        if (iIndexOf < 1) {
            return str;
        }
        try {
            return Long.toString(Long.parseLong(name.substring(0, iIndexOf)));
        } catch (NumberFormatException unused) {
            return str;
        }
    }

    public static void main(String[] strArr) {
        System.out.println(getProcessId("<PID>"));
        try {
            Thread.sleep(120000L);
        } catch (Exception unused) {
        }
    }
}
