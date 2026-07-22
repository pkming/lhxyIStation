package com.amap.api.col.p0003sl;

import org.json.JSONObject;

/* JADX INFO: compiled from: NativeCrashHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ka {

    /* JADX INFO: compiled from: NativeCrashHandler.java */
    static class a {
        public static ka a = new ka();
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0067  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean a(android.content.Context r10, java.lang.String r11, java.lang.String r12, java.util.List<com.amap.api.col.p0003sl.is> r13, boolean r14, com.amap.api.col.p0003sl.is r15) {
        /*
            Method dump skipped, instruction units count: 346
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ka.a(android.content.Context, java.lang.String, java.lang.String, java.util.List, boolean, com.amap.api.col.3sl.is):boolean");
    }

    private static JSONObject a(Thread thread) {
        if (thread == null || thread.getStackTrace() == null) {
            return null;
        }
        StackTraceElement[] stackTrace = thread.getStackTrace();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("threadId", thread.getId());
            jSONObject.put("threadName", thread.getName());
            jSONObject.put("threadGroup", thread.getThreadGroup());
            StringBuffer stringBuffer = new StringBuffer();
            for (StackTraceElement stackTraceElement : stackTrace) {
                stringBuffer.append(stackTraceElement);
                stringBuffer.append("<br />");
            }
            jSONObject.put("stacks", stringBuffer.toString());
        } catch (Throwable unused) {
        }
        return jSONObject;
    }
}
