package com.amap.api.col.p0003sl;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONObject;

/* JADX INFO: compiled from: Privacy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ip {
    private static volatile b a = b.Unknow;
    private static volatile d b = d.Unknow;
    private static volatile String c = "";
    private static volatile String d = "";
    private static volatile long e = -1;
    private static volatile a f = a.Unknow;
    private static volatile long g = -1;
    private static volatile String h = "";
    private static volatile String i = "";
    private static volatile long j = 0;
    private static volatile long k = 0;
    private static volatile boolean l = false;
    private static volatile boolean m = true;

    /* JADX INFO: compiled from: Privacy.java */
    public enum d {
        Unknow(-1),
        NotShow(0),
        DidShow(1);

        private int d;

        d(int i) {
            this.d = i;
        }

        public final int a() {
            return this.d;
        }

        public static d a(int i) {
            d dVar = NotShow;
            if (i == dVar.a()) {
                return dVar;
            }
            d dVar2 = DidShow;
            return i == dVar2.a() ? dVar2 : Unknow;
        }
    }

    /* JADX INFO: compiled from: Privacy.java */
    public enum b {
        Unknow(-1),
        NotContain(0),
        DidContain(1);

        private int d;

        b(int i) {
            this.d = i;
        }

        public final int a() {
            return this.d;
        }

        public static b a(int i) {
            b bVar = NotContain;
            if (i == bVar.a()) {
                return bVar;
            }
            b bVar2 = DidContain;
            return i == bVar2.a() ? bVar2 : Unknow;
        }
    }

    /* JADX INFO: compiled from: Privacy.java */
    public enum a {
        Unknow(-1),
        NotAgree(0),
        DidAgree(1);

        private int d;

        a(int i) {
            this.d = i;
        }

        public final int a() {
            return this.d;
        }

        public static a a(int i) {
            a aVar = NotAgree;
            if (i == aVar.a()) {
                return aVar;
            }
            a aVar2 = DidAgree;
            return i == aVar2.a() ? aVar2 : Unknow;
        }
    }

    /* JADX INFO: compiled from: Privacy.java */
    public enum c {
        SuccessCode(0),
        ShowUnknowCode(555570),
        ShowNoShowCode(555571),
        InfoUnknowCode(555572),
        InfoNotContainCode(555573),
        AgreeUnknowCode(555574),
        AgreeNotAgreeCode(555575),
        InvaildUserKeyCode(10001),
        IllegalArgument(20001);

        private final int j;

        c(int i) {
            this.j = i;
        }

        public final int a() {
            return this.j;
        }
    }

    public static void a(Context context, boolean z, boolean z2, is isVar) {
        d dVar;
        b bVar;
        if (z2) {
            dVar = d.DidShow;
        } else {
            dVar = d.NotShow;
        }
        if (z) {
            bVar = b.DidContain;
        } else {
            bVar = b.NotContain;
        }
        a(context, dVar, bVar, isVar);
    }

    private static synchronized void a(Context context, d dVar, b bVar, is isVar) {
        if (context == null || isVar == null) {
            return;
        }
        if (!l) {
            e(context);
            l = true;
        }
        Boolean bool = Boolean.FALSE;
        if (dVar != b) {
            bool = Boolean.TRUE;
            b = dVar;
        }
        if (bVar != a) {
            bool = Boolean.TRUE;
            a = bVar;
        }
        if (bool.booleanValue()) {
            c = isVar.a();
            d = isVar.b();
            long jCurrentTimeMillis = System.currentTimeMillis();
            e = jCurrentTimeMillis;
            j = jCurrentTimeMillis;
            d(context);
        }
    }

    public static void a(Context context, boolean z, is isVar) {
        a aVar;
        if (z) {
            aVar = a.DidAgree;
        } else {
            aVar = a.NotAgree;
        }
        a(context, aVar, isVar);
    }

    private static synchronized void a(Context context, a aVar, is isVar) {
        if (context == null || isVar == null) {
            return;
        }
        if (!l) {
            e(context);
            l = true;
        }
        Boolean bool = Boolean.FALSE;
        if (aVar != f) {
            Boolean bool2 = Boolean.TRUE;
            f = aVar;
            h = isVar.a();
            i = isVar.b();
            long jCurrentTimeMillis = System.currentTimeMillis();
            g = jCurrentTimeMillis;
            j = jCurrentTimeMillis;
            d(context);
        }
    }

    public static synchronized iq a(final Context context, is isVar) {
        boolean z;
        iq iqVar = null;
        if (context == null || isVar == null) {
            return new iq(c.IllegalArgument, isVar);
        }
        if (!l) {
            e(context);
            l = true;
        }
        if (b != d.DidShow) {
            if (b == d.Unknow) {
                iqVar = new iq(c.ShowUnknowCode, isVar);
            } else if (b == d.NotShow) {
                iqVar = new iq(c.ShowNoShowCode, isVar);
            }
            z = false;
        } else {
            z = true;
        }
        if (z && a != b.DidContain) {
            if (a == b.Unknow) {
                iqVar = new iq(c.InfoUnknowCode, isVar);
            } else if (a == b.NotContain) {
                iqVar = new iq(c.InfoNotContainCode, isVar);
            }
            z = false;
        }
        if (z && f != a.DidAgree) {
            if (f == a.Unknow) {
                iqVar = new iq(c.AgreeUnknowCode, isVar);
            } else if (f == a.NotAgree) {
                iqVar = new iq(c.AgreeNotAgreeCode, isVar);
            }
            z = false;
        }
        if (k != j) {
            final long j2 = j;
            k = j;
            try {
                final JSONObject jSONObject = new JSONObject();
                jSONObject.put("privacyInfo", a.a());
                jSONObject.put("privacyShow", b.a());
                jSONObject.put("showTime", e);
                jSONObject.put("show2SDK", c);
                jSONObject.put("show2SDKVer", d);
                jSONObject.put("privacyAgree", f.a());
                jSONObject.put("agreeTime", g);
                jSONObject.put("agree2SDK", h);
                jSONObject.put("agree2SDKVer", i);
                final boolean z2 = m;
                mc.a().a(new md() { // from class: com.amap.api.col.3sl.ip.2
                    @Override // com.amap.api.col.p0003sl.md
                    public final void runTask() {
                        if (z2) {
                            Iterator it = ip.b(ip.f(context)).iterator();
                            while (it.hasNext()) {
                                ip.a(context, ((File) it.next()).getName());
                            }
                        }
                        ip.d(context);
                        ip.a(context, jSONObject, j2);
                        boolean zB = ip.b(context, jSONObject);
                        if (zB) {
                            ip.b(context, ip.b(j2));
                        }
                        if (z2) {
                            ip.b(context);
                        }
                        if (zB) {
                            return;
                        }
                        ip.a(context, ip.b(j2));
                    }
                });
            } catch (Throwable unused) {
            }
        } else if (m) {
            mc.a().a(new md() { // from class: com.amap.api.col.3sl.ip.1
                @Override // com.amap.api.col.p0003sl.md
                public final void runTask() {
                    Iterator it = ip.b(ip.f(context)).iterator();
                    while (it.hasNext()) {
                        ip.a(context, ((File) it.next()).getName());
                    }
                    ip.b(context);
                }
            });
        }
        m = false;
        String strF = ig.f(context);
        if (strF == null || strF.length() <= 0) {
            iqVar = new iq(c.InvaildUserKeyCode, isVar);
            Log.e(isVar.a(), String.format("获取apikey失败：\nerrorCode : %d\n原因：%s", Integer.valueOf(iqVar.a.a()), iqVar.b));
        }
        if (z) {
            iqVar = new iq(c.SuccessCode, isVar);
        } else {
            Log.e(isVar.a(), String.format("隐私合规校验失败：\nerrorCode : %d\n原因：%s", Integer.valueOf(iqVar.a.a()), iqVar.b));
        }
        return iqVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void d(Context context) {
        if (context == null) {
            return;
        }
        if (!l) {
            e(context);
            l = true;
        }
        try {
            kj.a(context, "AMap.privacy.data", "AMap.privacy.data", String.format("%d&%d&%d&%s&%s&%d&%d&%s&%s&%d&%d", Integer.valueOf(a.a()), Integer.valueOf(b.a()), Long.valueOf(e), c, d, Integer.valueOf(f.a()), Long.valueOf(g), h, i, Long.valueOf(j), Long.valueOf(k)));
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static void e(Context context) {
        if (context == null) {
            return;
        }
        String strA = null;
        try {
            strA = kj.a(context, "AMap.privacy.data", "AMap.privacy.data");
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (strA == null) {
            return;
        }
        String[] strArrSplit = strA.split("&");
        if (strArrSplit.length != 11) {
            return;
        }
        try {
            a = b.a(Integer.parseInt(strArrSplit[0]));
            b = d.a(Integer.parseInt(strArrSplit[1]));
            e = Long.parseLong(strArrSplit[2]);
            d = strArrSplit[3];
            d = strArrSplit[4];
            f = a.a(Integer.parseInt(strArrSplit[5]));
            g = Long.parseLong(strArrSplit[6]);
            h = strArrSplit[7];
            i = strArrSplit[8];
            j = Long.parseLong(strArrSplit[9]);
            k = Long.parseLong(strArrSplit[10]);
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean b(Context context, JSONObject jSONObject) {
        try {
            kd kdVar = new kd();
            kdVar.b = context;
            kdVar.a = jSONObject;
            new ku();
            lc lcVarA = ku.a(kdVar);
            if (lcVarA == null) {
                return false;
            }
            JSONObject jSONObject2 = new JSONObject(it.a(lcVarA.a));
            if (jSONObject2.has("status")) {
                return jSONObject2.getInt("status") == 1;
            }
            return false;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String b(long j2) {
        return String.format("%d-%s", Long.valueOf(j2), "privacy.data");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String f(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/AMap/Privacy/Upload";
    }

    private static String g(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/AMap/Privacy/Reload";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ArrayList<File> b(String str) {
        ArrayList<File> arrayList = new ArrayList<>();
        if (str != null && str.length() != 0) {
            File file = new File(str);
            if (!file.exists()) {
                return arrayList;
            }
            File[] fileArrListFiles = file.listFiles();
            for (File file2 : fileArrListFiles) {
                if (file2.isFile()) {
                    arrayList.add(file2);
                }
            }
        }
        return arrayList;
    }

    static /* synthetic */ void a(Context context, String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        try {
            File file = new File(f(context) + "/" + str);
            if (file.exists()) {
                File file2 = new File(g(context) + "/" + str);
                if (!file2.getParentFile().exists()) {
                    file2.getParentFile().mkdirs();
                }
                file.renameTo(file2);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    static /* synthetic */ void b(Context context) {
        try {
            for (File file : b(g(context))) {
                try {
                    String name = file.getName();
                    if (!name.endsWith("-privacy.data")) {
                        file.delete();
                    } else {
                        String[] strArrSplit = name.split("-");
                        if (strArrSplit == null && strArrSplit.length != 2) {
                            file.delete();
                        } else if (Long.parseLong(strArrSplit[0]) <= 0) {
                            file.delete();
                        } else {
                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] bArr = new byte[fileInputStream.available()];
                            fileInputStream.read(bArr);
                            if (b(context, new JSONObject(new String(kj.b(context, bArr))))) {
                                file.delete();
                            }
                        }
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
    }

    static /* synthetic */ void a(Context context, JSONObject jSONObject, long j2) {
        FileOutputStream fileOutputStream = null;
        try {
            byte[] bArrA = kj.a(context, jSONObject.toString().getBytes());
            File file = new File(f(context) + "/" + b(j2));
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
            try {
                fileOutputStream2.write(bArrA);
                try {
                    fileOutputStream2.close();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            } catch (Throwable th2) {
                fileOutputStream = fileOutputStream2;
                th = th2;
                try {
                    th.printStackTrace();
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th3) {
                            th3.printStackTrace();
                        }
                    }
                } catch (Throwable th4) {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th5) {
                            th5.printStackTrace();
                        }
                    }
                    throw th4;
                }
            }
        } catch (Throwable th6) {
            th = th6;
        }
    }

    static /* synthetic */ void b(Context context, String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        try {
            File file = new File(f(context) + "/" + str);
            if (file.exists()) {
                file.delete();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
