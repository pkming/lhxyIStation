package com.amap.api.col.p0003sl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/* JADX INFO: compiled from: ALLog.java */
/* JADX INFO: loaded from: classes2.dex */
public final class nu {
    public static void a(Throwable th) {
        if (nj.a) {
            b(th);
        }
    }

    private static String b(Throwable th) {
        if (th == null) {
            return "";
        }
        for (Throwable cause = th; cause != null; cause = cause.getCause()) {
            if (cause instanceof UnknownHostException) {
                return "";
            }
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}
