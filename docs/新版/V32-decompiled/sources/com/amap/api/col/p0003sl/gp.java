package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.go;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/* JADX INFO: compiled from: RequestCacheWorker.java */
/* JADX INFO: loaded from: classes2.dex */
class gp {
    private boolean a = true;
    private long b = 86400;
    private int c = 10;
    private long d = 0;
    private final LinkedHashMap<go.b, Object> e = new LinkedHashMap<>();
    private final Object f = new Object();
    private final LinkedHashMap<go.b, Object> g = new LinkedHashMap<>();
    private final Object h = new Object();
    private ArrayList<String> i = new ArrayList<>();

    public gp(String... strArr) {
        a(strArr);
    }

    private void a(String... strArr) {
        this.d = System.currentTimeMillis();
        this.e.clear();
        this.i.clear();
        for (String str : strArr) {
            if (str != null) {
                this.i.add(str);
            }
        }
    }

    protected boolean a(LinkedHashMap<go.b, Object> linkedHashMap, go.b bVar) {
        if (linkedHashMap == null || bVar == null) {
            return false;
        }
        return linkedHashMap.containsKey(bVar);
    }

    protected Object b(LinkedHashMap<go.b, Object> linkedHashMap, go.b bVar) {
        if (linkedHashMap == null || bVar == null) {
            return null;
        }
        return linkedHashMap.get(bVar);
    }

    protected Object c(LinkedHashMap<go.b, Object> linkedHashMap, go.b bVar) {
        if (linkedHashMap == null || bVar == null) {
            return null;
        }
        return linkedHashMap.remove(bVar);
    }

    public final go.c a(go.b bVar) {
        if (!this.a || bVar == null || !b(bVar)) {
            return null;
        }
        b();
        synchronized (this.f) {
            if (a(this.e, bVar)) {
                return new go.c(b(this.e, bVar), true);
            }
            synchronized (this.h) {
                if (a(this.g, bVar)) {
                    while (!a(this.e, bVar) && a(this.g, bVar)) {
                        try {
                            this.h.wait(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    this.g.put(bVar, null);
                }
            }
            return new go.c(b(this.e, bVar), false);
        }
    }

    public final void a(go.b bVar, Object obj) {
        if (this.a && bVar != null && b(bVar)) {
            b(bVar, obj);
            synchronized (this.h) {
                c(this.g, bVar);
                this.h.notify();
            }
        }
    }

    private void b(go.b bVar, Object obj) {
        synchronized (this.f) {
            a();
            b();
            this.e.put(bVar, obj);
        }
    }

    private void a() {
        int size = this.e.size();
        if (size <= 0 || size < this.c) {
            return;
        }
        go.b bVar = null;
        Iterator<go.b> it = this.e.keySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            go.b next = it.next();
            if (next != null) {
                bVar = next;
                break;
            }
        }
        c(this.e, bVar);
    }

    private void b() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if ((jCurrentTimeMillis - this.d) / 1000 > this.b) {
            this.e.clear();
            this.d = jCurrentTimeMillis;
        }
    }

    public final boolean b(go.b bVar) {
        if (bVar != null && bVar.a != null) {
            for (String str : this.i) {
                if (str != null && bVar.a.contains(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void a(go.a aVar) {
        if (aVar != null) {
            this.a = aVar.a();
            this.b = aVar.b();
            this.c = aVar.c();
        }
    }
}
