package com.unisound.common;

/* JADX INFO: loaded from: classes2.dex */
public class m {
    public static int a(String str) {
        try {
            return Integer.valueOf(str).intValue();
        } catch (Exception unused) {
            return 0;
        }
    }
}
