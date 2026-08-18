package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: ThreadTask.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class md implements Runnable {
    a f;

    /* JADX INFO: compiled from: ThreadTask.java */
    interface a {
        void a(md mdVar);

        void b(md mdVar);
    }

    public abstract void runTask();

    @Override // java.lang.Runnable
    public final void run() {
        a aVar;
        try {
            if (Thread.interrupted()) {
                return;
            }
            runTask();
            if (Thread.interrupted() || (aVar = this.f) == null) {
                return;
            }
            aVar.a(this);
        } catch (Throwable th) {
            jw.c(th, "ThreadTask", "run");
            th.printStackTrace();
        }
    }

    public final void cancelTask() {
        try {
            a aVar = this.f;
            if (aVar != null) {
                aVar.b(this);
            }
        } catch (Throwable th) {
            jw.c(th, "ThreadTask", "cancelTask");
            th.printStackTrace();
        }
    }
}
