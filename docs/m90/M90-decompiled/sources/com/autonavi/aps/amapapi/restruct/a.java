package com.autonavi.aps.amapapi.restruct;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.il;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: compiled from: AgeEstimator.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class a<T> {
    public String a;
    private File b;
    private Handler e;
    private String f;
    private boolean g;
    private boolean c = false;
    private Map<String, C0020a> d = new ConcurrentHashMap();
    private Runnable h = new Runnable() { // from class: com.autonavi.aps.amapapi.restruct.a.2
        @Override // java.lang.Runnable
        public final void run() {
            if (a.this.c) {
                if (a.this.g) {
                    a.this.e();
                    a.e(a.this);
                }
                if (a.this.e != null) {
                    a.this.e.postDelayed(a.this.h, 60000L);
                }
            }
        }
    };

    public static int a(long j, long j2) {
        if (j < j2) {
            return -1;
        }
        return j == j2 ? 0 : 1;
    }

    abstract void a(T t, long j);

    abstract long b();

    public abstract String b(T t);

    abstract int c(T t);

    abstract long c();

    abstract long d(T t);

    static /* synthetic */ boolean e(a aVar) {
        aVar.g = false;
        return false;
    }

    public a(Context context, String str, Handler handler) {
        this.f = null;
        if (context == null) {
            return;
        }
        this.e = handler;
        this.a = TextUtils.isEmpty(str) ? "unknow" : str;
        this.f = com.autonavi.aps.amapapi.utils.j.l(context);
        try {
            this.b = new File(context.getFilesDir().getPath(), this.a);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        d();
    }

    public final void a() {
        Handler handler;
        if (!this.c && (handler = this.e) != null) {
            handler.removeCallbacks(this.h);
            this.e.postDelayed(this.h, 60000L);
        }
        this.c = true;
    }

    public final void a(boolean z) {
        Handler handler = this.e;
        if (handler != null) {
            handler.removeCallbacks(this.h);
        }
        if (!z) {
            this.h.run();
        }
        this.c = false;
    }

    public final void a(T t) {
        b(t, com.autonavi.aps.amapapi.utils.j.b());
    }

    public final void a(List<T> list) {
        long jB = com.autonavi.aps.amapapi.utils.j.b();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            b(it.next(), jB);
        }
        if (this.d.size() >= list.size()) {
            this.g = true;
        }
        if (this.d.size() > 16384 || c() <= 0) {
            this.d.clear();
            for (T t : list) {
                this.d.put(b(t), new C0020a(c(t), d(t), jB));
            }
        }
    }

    private void b(T t, long j) {
        if (t == null || d(t) < 0) {
            return;
        }
        String strB = b(t);
        C0020a c0020a = this.d.get(strB);
        if (c0020a == null) {
            a(t, j);
            this.d.put(strB, new C0020a(c(t), d(t), j));
            this.g = true;
            return;
        }
        c0020a.c = j;
        if (c0020a.a != c(t)) {
            a(t, j);
            c0020a.a = c(t);
            c0020a.b = d(t);
            this.g = true;
            return;
        }
        a(t, c0020a.b);
    }

    private void d() {
        long jB;
        try {
            StringBuilder sb = new StringBuilder("restore from：\n");
            Iterator<String> it = com.autonavi.aps.amapapi.utils.j.a(this.b).iterator();
            while (it.hasNext()) {
                try {
                    String str = new String(com.autonavi.aps.amapapi.security.a.b(il.b(it.next()), this.f), "UTF-8");
                    sb.append(str).append("\n");
                    String[] strArrSplit = str.split(",");
                    if (strArrSplit.length >= 4) {
                        jB = Long.parseLong(strArrSplit[3]);
                    } else {
                        jB = com.autonavi.aps.amapapi.utils.j.b();
                    }
                    this.d.put(strArrSplit[0], new C0020a(Integer.parseInt(strArrSplit[1]), Long.parseLong(strArrSplit[2]), jB));
                } catch (Throwable th) {
                    if (this.b.exists()) {
                        this.b.delete();
                    }
                    th.printStackTrace();
                }
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e() {
        if (c() > 0) {
            this.d.size();
            if (b() > 0) {
                long jB = com.autonavi.aps.amapapi.utils.j.b();
                Iterator<Map.Entry<String, C0020a>> it = this.d.entrySet().iterator();
                while (it.hasNext()) {
                    if (jB - this.d.get(it.next().getKey()).c > b()) {
                        it.remove();
                    }
                }
            }
            if (this.d.size() > c()) {
                ArrayList arrayList = new ArrayList(this.d.keySet());
                Collections.sort(arrayList, new Comparator<String>() { // from class: com.autonavi.aps.amapapi.restruct.a.1
                    /* JADX INFO: Access modifiers changed from: private */
                    @Override // java.util.Comparator
                    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
                    public int compare(String str, String str2) {
                        return a.a(((C0020a) a.this.d.get(str2)).c, ((C0020a) a.this.d.get(str)).c);
                    }
                });
                for (int iC = (int) c(); iC < arrayList.size(); iC++) {
                    this.d.remove(arrayList.get(iC));
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, C0020a> entry : this.d.entrySet()) {
            try {
                sb.append(il.b(com.autonavi.aps.amapapi.security.a.a((entry.getKey() + "," + entry.getValue().a + "," + entry.getValue().b + "," + entry.getValue().c).getBytes("UTF-8"), this.f)) + "\n");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String string = sb.toString();
        if (TextUtils.isEmpty(string)) {
            return;
        }
        com.autonavi.aps.amapapi.utils.j.a(this.b, string);
    }

    /* JADX INFO: renamed from: com.autonavi.aps.amapapi.restruct.a$a, reason: collision with other inner class name */
    /* JADX INFO: compiled from: AgeEstimator.java */
    static class C0020a {
        int a;
        long b;
        long c;

        public C0020a(int i, long j, long j2) {
            this.a = i;
            this.b = j;
            this.c = j2;
        }
    }

    public final long e(T t) {
        return (com.autonavi.aps.amapapi.utils.j.b() - d(t)) / 1000;
    }
}
