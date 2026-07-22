package com.amap.api.col.p0003sl;

import android.content.Context;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: compiled from: TaskManager.java */
/* JADX INFO: loaded from: classes2.dex */
public final class bh {
    private static bh a;
    private mc b;
    private LinkedHashMap<String, md> c = new LinkedHashMap<>();
    private boolean d = true;

    public static bh a() {
        return c();
    }

    private static synchronized bh c() {
        try {
            bh bhVar = a;
            if (bhVar == null) {
                a = new bh();
            } else if (bhVar.b == null) {
                bhVar.b = mc.b();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return a;
    }

    private bh() {
        try {
            if (this.b == null) {
                this.b = mc.c();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void d() {
        synchronized (this.c) {
            if (this.c.size() <= 0) {
                return;
            }
            for (Map.Entry<String, md> entry : this.c.entrySet()) {
                entry.getKey();
                ((bd) entry.getValue()).a();
            }
            this.c.clear();
        }
    }

    public final void a(bg bgVar) {
        synchronized (this.c) {
            bd bdVar = (bd) this.c.get(bgVar.b());
            if (bdVar == null) {
                return;
            }
            bdVar.a();
            this.c.remove(bgVar.b());
        }
    }

    public final void a(bg bgVar, Context context) throws Cif {
        if (!this.c.containsKey(bgVar.b())) {
            bd bdVar = new bd((bx) bgVar, context.getApplicationContext(), (byte) 0);
            synchronized (this.c) {
                this.c.put(bgVar.b(), bdVar);
            }
        }
        this.b.a(this.c.get(bgVar.b()));
    }

    public final void b() {
        d();
        this.b.e();
        this.b = null;
        e();
    }

    private static void e() {
        a = null;
    }

    public final void b(bg bgVar) {
        bd bdVar = (bd) this.c.get(bgVar.b());
        if (bdVar != null) {
            synchronized (this.c) {
                bdVar.b();
                this.c.remove(bgVar.b());
            }
        }
    }
}
