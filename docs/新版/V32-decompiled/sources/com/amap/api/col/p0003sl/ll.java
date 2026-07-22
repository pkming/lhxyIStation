package com.amap.api.col.p0003sl;

import android.content.Context;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

/* JADX INFO: compiled from: StatisticsManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class ll {
    static boolean a = false;
    static int b = 20;
    private static int c = 20;
    private static WeakReference<lf> d;
    private static int e;

    public static synchronized void a(boolean z, int i) {
        a = z;
        e = Math.max(0, i);
    }

    /* JADX INFO: compiled from: StatisticsManager.java */
    static class a extends md {
        static int a = 1;
        static int b = 2;
        static int c = 3;
        private Context d;
        private lk e;
        private int g;
        private List<lk> h;

        a(Context context, int i) {
            this.d = context;
            this.g = i;
        }

        a(Context context, int i, List<lk> list) {
            this(context, i);
            this.h = list;
        }

        a(Context context, int i, lk lkVar) {
            this(context, i);
            this.e = lkVar;
        }

        @Override // com.amap.api.col.p0003sl.md
        public final void runTask() {
            lk lkVar;
            Throwable th;
            int i = this.g;
            if (i == 1) {
                try {
                    if (this.d != null && this.e != null) {
                        synchronized (ll.class) {
                            Context context = this.d;
                            if (context != null && (lkVar = this.e) != null) {
                                ll.a(context, lkVar.a());
                                return;
                            }
                            return;
                        }
                    }
                    return;
                } catch (Throwable th2) {
                    jw.c(th2, "stm", "as");
                    return;
                }
            }
            if (i != 2) {
                if (i == 3) {
                    try {
                        if (this.d == null) {
                            return;
                        }
                        lf lfVarA = lm.a(ll.d);
                        lm.a(this.d, lfVarA, ju.h, 1000, 307200, "2");
                        if (lfVarA.g == null) {
                            lfVarA.g = new ln(new lr(this.d, new lo(new ls(new lu()))));
                        }
                        lfVarA.h = 3600000;
                        if (TextUtils.isEmpty(lfVarA.i)) {
                            lfVarA.i = "cKey";
                        }
                        if (lfVarA.f == null) {
                            lfVarA.f = new ly(this.d, lfVarA.h, lfVarA.i, new lv(lfVarA.a, new lw(this.d, ll.a, ll.c * 1024, ll.b * 1024, "staticUpdate", ll.e * 1024)));
                        }
                        lg.a(lfVarA);
                        return;
                    } catch (Throwable th3) {
                        jw.c(th3, "stm", "usd");
                        return;
                    }
                }
                return;
            }
            try {
                synchronized (ll.class) {
                    if (this.h != null && this.d != null) {
                        ByteArrayOutputStream byteArrayOutputStream = null;
                        byte[] byteArray = new byte[0];
                        try {
                            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                            try {
                                for (lk lkVar2 : this.h) {
                                    if (lkVar2 != null) {
                                        byteArrayOutputStream2.write(lkVar2.a());
                                    }
                                }
                                byteArray = byteArrayOutputStream2.toByteArray();
                                try {
                                    byteArrayOutputStream2.close();
                                } catch (Throwable th4) {
                                    th = th4;
                                    th.printStackTrace();
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                byteArrayOutputStream = byteArrayOutputStream2;
                                try {
                                    jw.c(th, "stm", "aStB");
                                    if (byteArrayOutputStream != null) {
                                        try {
                                            byteArrayOutputStream.close();
                                        } catch (Throwable th6) {
                                            th = th6;
                                            th.printStackTrace();
                                        }
                                    }
                                } finally {
                                }
                            }
                        } catch (Throwable th7) {
                            th = th7;
                        }
                        ll.a(this.d, byteArray);
                    }
                }
            } catch (Throwable th8) {
                jw.c(th8, "stm", "apb");
            }
        }
    }

    public static synchronized void a(lk lkVar, Context context) {
        mc.a().a(new a(context, a.a, lkVar));
    }

    public static synchronized void a(List<lk> list, Context context) {
        if (list != null) {
            try {
                if (list.size() != 0) {
                    mc.a().a(new a(context, a.b, list));
                }
            } catch (Throwable unused) {
            }
        }
    }

    public static synchronized void b(List<lk> list, Context context) {
        try {
            List<lk> listB = kx.b();
            if (listB != null && listB.size() > 0) {
                list.addAll(listB);
            }
        } catch (Throwable unused) {
        }
        a(list, context);
    }

    public static void a(Context context) {
        mc.a().a(new a(context, a.c));
    }

    static /* synthetic */ void a(Context context, byte[] bArr) throws IOException {
        lf lfVarA = lm.a(d);
        lm.a(context, lfVarA, ju.h, 1000, 307200, "2");
        if (lfVarA.e == null) {
            lfVarA.e = new kk();
        }
        try {
            lg.a(Integer.toString(new Random().nextInt(100)) + Long.toString(System.nanoTime()), bArr, lfVarA);
        } catch (Throwable th) {
            jw.c(th, "stm", "wts");
        }
    }
}
