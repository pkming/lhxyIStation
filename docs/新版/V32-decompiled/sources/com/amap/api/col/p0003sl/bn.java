package com.amap.api.col.p0003sl;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: compiled from: OfflineDBOperation.java */
/* JADX INFO: loaded from: classes2.dex */
public class bn {
    private static volatile bn a;
    private static kf b;
    private Context c;

    public static bn a(Context context) {
        if (a == null) {
            synchronized (bn.class) {
                if (a == null) {
                    a = new bn(context);
                }
            }
        }
        return a;
    }

    private bn(Context context) {
        this.c = context;
        b = b(context);
    }

    private static kf b(Context context) {
        try {
            return new kf(context, bm.a());
        } catch (Throwable th) {
            jw.c(th, "OfflineDB", "getDB");
            th.printStackTrace();
            return null;
        }
    }

    private boolean b() {
        if (b == null) {
            b = b(this.c);
        }
        return b != null;
    }

    public final ArrayList<bi> a() {
        ArrayList<bi> arrayList = new ArrayList<>();
        if (!b()) {
            return arrayList;
        }
        Iterator it = b.b("", bi.class).iterator();
        while (it.hasNext()) {
            arrayList.add((bi) it.next());
        }
        return arrayList;
    }

    public final synchronized bi a(String str) {
        if (!b()) {
            return null;
        }
        List listB = b.b(bi.e(str), bi.class);
        if (listB.size() <= 0) {
            return null;
        }
        return (bi) listB.get(0);
    }

    public final synchronized void a(bi biVar) {
        if (b()) {
            b.a(biVar, bi.f(biVar.h()));
            a(biVar.e(), biVar.a());
        }
    }

    private static void a(String str, String str2) {
        if (str2 == null || str2.length() <= 0) {
            return;
        }
        String strA = bk.a(str);
        if (b.b(strA, bk.class).size() > 0) {
            b.a(strA, bk.class);
        }
        String[] strArrSplit = str2.split(";");
        ArrayList arrayList = new ArrayList();
        for (String str3 : strArrSplit) {
            arrayList.add(new bk(str, str3));
        }
        b.a((List) arrayList);
    }

    public final synchronized List<String> b(String str) {
        ArrayList arrayList = new ArrayList();
        if (!b()) {
            return arrayList;
        }
        arrayList.addAll(a((List<bk>) b.b(bk.a(str), bk.class)));
        return arrayList;
    }

    private static List<String> a(List<bk> list) {
        ArrayList arrayList = new ArrayList();
        if (list.size() > 0) {
            Iterator<bk> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().a());
            }
        }
        return arrayList;
    }

    public final synchronized void c(String str) {
        if (b()) {
            b.a(bl.e(str), bl.class);
            b.a(bk.a(str), bk.class);
            b.a(bj.a(str), bj.class);
        }
    }

    public final synchronized void b(bi biVar) {
        if (b()) {
            b.a(bl.f(biVar.h()), bl.class);
            b.a(bk.a(biVar.e()), bk.class);
            b.a(bj.a(biVar.e()), bj.class);
        }
    }

    public final void a(String str, int i, long j, long j2, long j3) {
        if (b()) {
            a(str, i, j, new long[]{j2, 0, 0, 0, 0}, new long[]{j3, 0, 0, 0, 0});
        }
    }

    private synchronized void a(String str, int i, long j, long[] jArr, long[] jArr2) {
        if (b()) {
            b.a(new bj(str, j, i, jArr[0], jArr2[0]), bj.a(str));
        }
    }

    public final synchronized String d(String str) {
        if (!b()) {
            return null;
        }
        List listB = b.b(bl.f(str), bl.class);
        return listB.size() > 0 ? ((bl) listB.get(0)).d() : null;
    }
}
