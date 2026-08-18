package com.amap.api.col.p0003sl;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/* JADX INFO: compiled from: Table.java */
/* JADX INFO: loaded from: classes2.dex */
public class oc {
    private static final ThreadLocal<CharsetDecoder> b = new ThreadLocal<CharsetDecoder>() { // from class: com.amap.api.col.3sl.oc.1
        @Override // java.lang.ThreadLocal
        protected final /* synthetic */ CharsetDecoder initialValue() {
            return a();
        }

        private static CharsetDecoder a() {
            return Charset.forName("UTF-8").newDecoder();
        }
    };
    public static final ThreadLocal<Charset> a = new ThreadLocal<Charset>() { // from class: com.amap.api.col.3sl.oc.2
        @Override // java.lang.ThreadLocal
        protected final /* synthetic */ Charset initialValue() {
            return a();
        }

        private static Charset a() {
            return Charset.forName("UTF-8");
        }
    };
    private static final ThreadLocal<CharBuffer> c = new ThreadLocal<>();
}
