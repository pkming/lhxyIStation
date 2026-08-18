package com.tencent.bugly.proguard;

import java.io.Serializable;

/* JADX INFO: compiled from: BUGLY */
/* JADX INFO: loaded from: classes2.dex */
public final class t implements Serializable, Comparable<t> {
    public long a;
    public String b;
    public long c;
    public int d;
    public String e;
    public String f;
    public long g;

    @Override // java.lang.Comparable
    public final /* bridge */ /* synthetic */ int compareTo(t tVar) {
        return (int) (this.c - tVar.c);
    }
}
