package com.amap.api.col.p0003sl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: compiled from: AmapDelegateListenerManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class q {
    private ConcurrentHashMap<Integer, a> a = new ConcurrentHashMap<>();

    /* JADX INFO: compiled from: AmapDelegateListenerManager.java */
    private class a<T> {
        public List<T> a = Collections.synchronizedList(new ArrayList());
        public T b = null;

        public a() {
        }
    }

    public final <T> void a(Integer num, T t) {
        ConcurrentHashMap<Integer, a> concurrentHashMap;
        if (t == null || (concurrentHashMap = this.a) == null) {
            return;
        }
        try {
            a aVar = concurrentHashMap.get(num);
            if (aVar == null) {
                aVar = new a();
                this.a.putIfAbsent(num, aVar);
            }
            if (aVar.a == null || aVar.a.contains(t)) {
                return;
            }
            aVar.a.add(t);
        } catch (Throwable unused) {
        }
    }

    public final <T> void b(Integer num, T t) {
        ConcurrentHashMap<Integer, a> concurrentHashMap;
        a aVar;
        if (t == null || (concurrentHashMap = this.a) == null) {
            return;
        }
        try {
            if (!concurrentHashMap.containsKey(num) || (aVar = this.a.get(num)) == null || aVar.a == null || !aVar.a.contains(t)) {
                return;
            }
            aVar.a.remove(t);
        } catch (Throwable unused) {
        }
    }

    public final <T> void a(Integer num) {
        a aVar;
        try {
            if (!this.a.containsKey(num) || (aVar = this.a.get(num)) == null || aVar.a == null) {
                return;
            }
            aVar.a.clear();
        } catch (Throwable unused) {
        }
    }

    public final <T> List<T> a(int i) {
        try {
            a aVar = this.a.get(Integer.valueOf(i));
            if (aVar != null) {
                return aVar.a;
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    public final <T> void a(int i, T t) {
        ConcurrentHashMap<Integer, a> concurrentHashMap = this.a;
        if (concurrentHashMap == null) {
            return;
        }
        try {
            a aVar = concurrentHashMap.get(Integer.valueOf(i));
            if (aVar == null) {
                aVar = new a();
                this.a.putIfAbsent(Integer.valueOf(i), aVar);
            }
            if (aVar.b == t) {
                return;
            }
            b(Integer.valueOf(i), aVar.b);
            aVar.b = t;
            a(Integer.valueOf(i), t);
        } catch (Throwable unused) {
        }
    }

    public final <T> void a() {
        ConcurrentHashMap<Integer, a> concurrentHashMap = this.a;
        if (concurrentHashMap == null) {
            return;
        }
        try {
            Iterator<Map.Entry<Integer, a>> it = concurrentHashMap.entrySet().iterator();
            while (it.hasNext()) {
                a value = it.next().getValue();
                value.a.clear();
                value.b = null;
            }
            this.a.clear();
        } catch (Throwable unused) {
        }
    }
}
