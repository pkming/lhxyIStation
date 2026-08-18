package com.autonavi.aps.amapapi.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.il;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.autonavi.aps.amapapi.restruct.d;
import com.autonavi.aps.amapapi.utils.j;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;

/* JADX INFO: compiled from: Cache.java */
/* JADX INFO: loaded from: classes2.dex */
public final class a {
    Hashtable<String, ArrayList<C0021a>> a = new Hashtable<>();
    private long i = 0;
    private boolean j = false;
    private String k = "2.0.201501131131".replace(".", "");
    private String l = null;
    boolean b = true;
    long c = 0;
    String d = null;
    d e = null;
    private String m = null;
    private long n = 0;
    boolean f = true;
    boolean g = true;
    String h = String.valueOf(AMapLocationClientOption.GeoLanguage.DEFAULT);

    public final void a(String str, StringBuilder sb, com.autonavi.aps.amapapi.model.a aVar, Context context, boolean z) {
        try {
            if (j.a(aVar)) {
                String str2 = str + "&" + aVar.isOffset() + "&" + aVar.i() + "&" + aVar.j();
                if (!a(str2, aVar) || aVar.e().equals("mem") || aVar.e().equals("file") || aVar.e().equals("wifioff") || "-3".equals(aVar.d())) {
                    return;
                }
                if (b()) {
                    c();
                }
                JSONObject jSONObjectF = aVar.f();
                if (j.a(jSONObjectF, "offpct")) {
                    jSONObjectF.remove("offpct");
                    aVar.a(jSONObjectF);
                }
                if (str2.contains("wifi")) {
                    if (TextUtils.isEmpty(sb)) {
                        return;
                    }
                    if (aVar.getAccuracy() >= 300.0f) {
                        int i = 0;
                        for (String str3 : sb.toString().split("#")) {
                            if (str3.contains(",")) {
                                i++;
                            }
                        }
                        if (i >= 8) {
                            return;
                        }
                    } else if (aVar.getAccuracy() <= 3.0f) {
                        return;
                    }
                    if (str2.contains("cgiwifi") && !TextUtils.isEmpty(aVar.g())) {
                        String strReplace = str2.replace("cgiwifi", "cgi");
                        com.autonavi.aps.amapapi.model.a aVarH = aVar.h();
                        if (j.a(aVarH)) {
                            a(strReplace, new StringBuilder(), aVarH, context, true);
                        }
                    }
                } else if (str2.contains("cgi") && ((sb != null && sb.indexOf(",") != -1) || "4".equals(aVar.d()))) {
                    return;
                }
                com.autonavi.aps.amapapi.model.a aVarA = a(str2, sb, false);
                if (j.a(aVarA) && aVarA.toStr().equals(aVar.toStr(3))) {
                    return;
                }
                this.i = j.b();
                C0021a c0021a = new C0021a();
                c0021a.a(aVar);
                c0021a.a(TextUtils.isEmpty(sb) ? null : sb.toString());
                if (this.a.containsKey(str2)) {
                    this.a.get(str2).add(c0021a);
                } else {
                    ArrayList<C0021a> arrayList = new ArrayList<>();
                    arrayList.add(c0021a);
                    this.a.put(str2, arrayList);
                }
                if (z) {
                    try {
                        a(str2, aVar, sb, context);
                    } catch (Throwable th) {
                        com.autonavi.aps.amapapi.utils.b.a(th, "Cache", "add");
                    }
                }
            }
        } catch (Throwable th2) {
            com.autonavi.aps.amapapi.utils.b.a(th2, "Cache", "add");
        }
    }

    public final com.autonavi.aps.amapapi.model.a a(Context context, String str, StringBuilder sb, boolean z, boolean z2) {
        if (TextUtils.isEmpty(str) || !com.autonavi.aps.amapapi.utils.a.e()) {
            return null;
        }
        String str2 = str + "&" + this.f + "&" + this.g + "&" + this.h;
        if (str2.contains("gps") || !com.autonavi.aps.amapapi.utils.a.e() || sb == null) {
            return null;
        }
        if (b()) {
            c();
            return null;
        }
        if (z && !this.j) {
            try {
                String strA = a(str2, sb, context);
                c();
                a(context, strA, z2);
            } catch (Throwable unused) {
            }
        }
        if (this.a.isEmpty()) {
            return null;
        }
        return a(str2, sb, z2);
    }

    private com.autonavi.aps.amapapi.model.a a(String str, StringBuilder sb, boolean z) {
        C0021a c0021aA;
        try {
            if (str.contains("cgiwifi") || str.contains("wifi")) {
                c0021aA = a(sb, str);
            } else {
                c0021aA = (str.contains("cgi") && this.a.containsKey(str) && this.a.get(str).size() > 0) ? this.a.get(str).get(0) : null;
            }
            if (c0021aA != null && j.a(c0021aA.a())) {
                com.autonavi.aps.amapapi.model.a aVarA = c0021aA.a();
                aVarA.e("mem");
                aVarA.h(c0021aA.b());
                if (!z && !com.autonavi.aps.amapapi.utils.a.a(aVarA.getTime())) {
                    Hashtable<String, ArrayList<C0021a>> hashtable = this.a;
                    if (hashtable != null && hashtable.containsKey(str)) {
                        this.a.get(str).remove(c0021aA);
                    }
                }
                if (j.a(aVarA)) {
                    this.c = 0L;
                }
                aVarA.setLocationType(4);
                return aVarA;
            }
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Cache", "get1");
        }
        return null;
    }

    private boolean b() {
        long jB = j.b();
        long j = this.i;
        long j2 = jB - j;
        if (j == 0) {
            return false;
        }
        return this.a.size() > 360 || j2 > 172800000;
    }

    private static boolean a(String str, com.autonavi.aps.amapapi.model.a aVar) {
        if (TextUtils.isEmpty(str) || !j.a(aVar) || str.startsWith("#")) {
            return false;
        }
        return str.contains(LocationManager.NETWORK_PROVIDER);
    }

    private void c() {
        this.i = 0L;
        if (!this.a.isEmpty()) {
            this.a.clear();
        }
        this.j = false;
    }

    private C0021a a(StringBuilder sb, String str) {
        C0021a c0021a;
        char c;
        C0021a c0021a2;
        if (this.a.isEmpty() || TextUtils.isEmpty(sb)) {
            return null;
        }
        if (!this.a.containsKey(str)) {
            return null;
        }
        Hashtable hashtable = new Hashtable();
        Hashtable hashtable2 = new Hashtable();
        Hashtable hashtable3 = new Hashtable();
        ArrayList<C0021a> arrayList = this.a.get(str);
        char c2 = 1;
        int size = arrayList.size() - 1;
        while (size >= 0) {
            C0021a c0021a3 = arrayList.get(size);
            if (!TextUtils.isEmpty(c0021a3.b())) {
                if (!a(c0021a3.b(), sb)) {
                    c = 0;
                } else {
                    if (j.a(c0021a3.b(), sb.toString())) {
                        c0021a2 = c0021a3;
                        c0021a = c0021a2;
                        break;
                    }
                    c = c2;
                }
                a(c0021a3.b(), (Hashtable<String, String>) hashtable);
                a(sb.toString(), (Hashtable<String, String>) hashtable2);
                hashtable3.clear();
                Iterator it = hashtable.keySet().iterator();
                while (it.hasNext()) {
                    hashtable3.put((String) it.next(), "");
                }
                Iterator it2 = hashtable2.keySet().iterator();
                while (it2.hasNext()) {
                    hashtable3.put((String) it2.next(), "");
                }
                Set setKeySet = hashtable3.keySet();
                double[] dArr = new double[setKeySet.size()];
                double[] dArr2 = new double[setKeySet.size()];
                Iterator it3 = setKeySet.iterator();
                int i = 0;
                while (it3 != null && it3.hasNext()) {
                    String str2 = (String) it3.next();
                    double d = 1.0d;
                    dArr[i] = hashtable.containsKey(str2) ? 1.0d : 0.0d;
                    if (!hashtable2.containsKey(str2)) {
                        d = 0.0d;
                    }
                    dArr2[i] = d;
                    i++;
                }
                setKeySet.clear();
                double[] dArrA = a(dArr, dArr2);
                if (dArrA[0] < 0.800000011920929d) {
                    c0021a2 = c0021a3;
                    if (dArrA[c2] >= Math.min(com.autonavi.aps.amapapi.utils.a.g(), 0.618d) || (c != 0 && dArrA[0] >= Math.min(com.autonavi.aps.amapapi.utils.a.g(), 0.618d))) {
                        c0021a = c0021a2;
                        break;
                    }
                } else {
                    c0021a2 = c0021a3;
                    c0021a = c0021a2;
                    break;
                }
            }
            size--;
            c2 = 1;
        }
        c0021a = null;
        hashtable.clear();
        hashtable2.clear();
        hashtable3.clear();
        return c0021a;
    }

    private static boolean a(String str, StringBuilder sb) {
        String strSubstring;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(sb) || !str.contains(",access") || sb.indexOf(",access") == -1) {
            return false;
        }
        String[] strArrSplit = str.split(",access");
        if (strArrSplit[0].contains("#")) {
            strSubstring = strArrSplit[0].substring(strArrSplit[0].lastIndexOf("#") + 1);
        } else {
            strSubstring = strArrSplit[0];
        }
        if (TextUtils.isEmpty(strSubstring)) {
            return false;
        }
        return sb.toString().contains(strSubstring + ",access");
    }

    private static void a(String str, Hashtable<String, String> hashtable) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        hashtable.clear();
        for (String str2 : str.split("#")) {
            if (!TextUtils.isEmpty(str2) && !str2.contains("|")) {
                hashtable.put(str2, "");
            }
        }
    }

    private static double[] a(double[] dArr, double[] dArr2) {
        double[] dArr3 = new double[3];
        double d = 0.0d;
        double d2 = 0.0d;
        double d3 = 0.0d;
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < dArr.length; i3++) {
            d2 += dArr[i3] * dArr[i3];
            d3 += dArr2[i3] * dArr2[i3];
            d += dArr[i3] * dArr2[i3];
            if (dArr2[i3] == 1.0d) {
                i2++;
                if (dArr[i3] == 1.0d) {
                    i++;
                }
            }
        }
        dArr3[0] = d / (Math.sqrt(d2) * Math.sqrt(d3));
        double d4 = i;
        dArr3[1] = (d4 * 1.0d) / ((double) i2);
        dArr3[2] = d4;
        for (int i4 = 0; i4 < 2; i4++) {
            if (dArr3[i4] > 1.0d) {
                dArr3[i4] = 1.0d;
            }
        }
        return dArr3;
    }

    public final void a(Context context) {
        if (this.j) {
            return;
        }
        try {
            c();
            a(context, (String) null, false);
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Cache", "loadDB");
        }
        this.j = true;
    }

    /* JADX INFO: renamed from: com.autonavi.aps.amapapi.storage.a$a, reason: collision with other inner class name */
    /* JADX INFO: compiled from: Cache.java */
    static class C0021a {
        private com.autonavi.aps.amapapi.model.a a = null;
        private String b = null;

        protected C0021a() {
        }

        public final com.autonavi.aps.amapapi.model.a a() {
            return this.a;
        }

        public final void a(com.autonavi.aps.amapapi.model.a aVar) {
            this.a = aVar;
        }

        public final String b() {
            return this.b;
        }

        public final void a(String str) {
            if (TextUtils.isEmpty(str)) {
                this.b = null;
            } else {
                this.b = str.replace("##", "#");
            }
        }
    }

    private String a(String str, StringBuilder sb, Context context) {
        String strSubstring;
        if (context == null) {
            return null;
        }
        JSONObject jSONObject = new JSONObject();
        try {
            this.l = j.l(context);
            if (str.contains("&")) {
                str = str.substring(0, str.indexOf("&"));
            }
            String strSubstring2 = str.substring(str.lastIndexOf("#") + 1);
            if (strSubstring2.equals("cgi")) {
                jSONObject.put("cgi", str.substring(0, str.length() - 12));
            } else if (!TextUtils.isEmpty(sb) && sb.indexOf(",access") != -1) {
                jSONObject.put("cgi", str.substring(0, str.length() - (strSubstring2.length() + 9)));
                String[] strArrSplit = sb.toString().split(",access");
                if (strArrSplit[0].contains("#")) {
                    strSubstring = strArrSplit[0].substring(strArrSplit[0].lastIndexOf("#") + 1);
                } else {
                    strSubstring = strArrSplit[0];
                }
                jSONObject.put("mmac", strSubstring);
            }
            return il.b(com.autonavi.aps.amapapi.security.a.a(jSONObject.toString().getBytes("UTF-8"), this.l));
        } catch (Throwable unused) {
            return null;
        }
    }

    private void a(String str, AMapLocation aMapLocation, StringBuilder sb, Context context) throws Exception {
        if (context == null) {
            return;
        }
        if (this.l == null) {
            this.l = j.l(context);
        }
        String strA = a(str, sb, context);
        StringBuilder sb2 = new StringBuilder();
        SQLiteDatabase sQLiteDatabaseOpenOrCreateDatabase = null;
        try {
            sQLiteDatabaseOpenOrCreateDatabase = context.openOrCreateDatabase("hmdb", 0, null);
            sb2.append("CREATE TABLE IF NOT EXISTS hist");
            sb2.append(this.k);
            sb2.append(" (feature VARCHAR PRIMARY KEY, nb VARCHAR, loc VARCHAR, time VARCHAR);");
            sQLiteDatabaseOpenOrCreateDatabase.execSQL(sb2.toString());
            sb2.delete(0, sb2.length());
            sb2.append("REPLACE INTO ");
            sb2.append("hist").append(this.k);
            sb2.append(" VALUES (?, ?, ?, ?)");
            Object[] objArr = new Object[4];
            objArr[0] = strA;
            byte[] bArrA = com.autonavi.aps.amapapi.security.a.a(sb.toString().getBytes("UTF-8"), this.l);
            objArr[1] = bArrA;
            objArr[2] = com.autonavi.aps.amapapi.security.a.a(aMapLocation.toStr().getBytes("UTF-8"), this.l);
            objArr[3] = Long.valueOf(aMapLocation.getTime());
            for (int i = 1; i < 3; i++) {
                objArr[i] = il.b((byte[]) objArr[i]);
            }
            sQLiteDatabaseOpenOrCreateDatabase.execSQL(sb2.toString(), objArr);
            sb2.delete(0, sb2.length());
        } catch (Throwable th) {
            try {
                com.autonavi.aps.amapapi.utils.b.a(th, "DB", "updateHist");
                sb2.delete(0, sb2.length());
                if (sQLiteDatabaseOpenOrCreateDatabase == null || !sQLiteDatabaseOpenOrCreateDatabase.isOpen()) {
                    return;
                }
                sQLiteDatabaseOpenOrCreateDatabase.close();
            } finally {
                sb2.delete(0, sb2.length());
                if (sQLiteDatabaseOpenOrCreateDatabase != null && sQLiteDatabaseOpenOrCreateDatabase.isOpen()) {
                    sQLiteDatabaseOpenOrCreateDatabase.close();
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:107:0x02c0 A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:109:0x02c5 A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:134:0x0284 A[EDGE_INSN: B:134:0x0284->B:85:0x0284 BREAK  A[LOOP:0: B:40:0x00da->B:87:0x0294], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:139:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0294 A[LOOP:0: B:40:0x00da->B:87:0x0294, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void a(android.content.Context r20, java.lang.String r21, boolean r22) throws java.lang.Exception {
        /*
            Method dump skipped, instruction units count: 737
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.storage.a.a(android.content.Context, java.lang.String, boolean):void");
    }

    private void c(Context context) throws Exception {
        boolean zIsOpen;
        if (context == null) {
            return;
        }
        SQLiteDatabase sQLiteDatabaseOpenOrCreateDatabase = null;
        try {
            sQLiteDatabaseOpenOrCreateDatabase = context.openOrCreateDatabase("hmdb", 0, null);
            if (!j.a(sQLiteDatabaseOpenOrCreateDatabase, "hist")) {
                if (sQLiteDatabaseOpenOrCreateDatabase != null) {
                    if (zIsOpen) {
                        return;
                    } else {
                        return;
                    }
                }
                return;
            }
            try {
                sQLiteDatabaseOpenOrCreateDatabase.delete("hist" + this.k, "time<?", new String[]{String.valueOf(j.a() - 172800000)});
            } catch (Throwable th) {
                com.autonavi.aps.amapapi.utils.b.a(th, "DB", "clearHist");
                String message = th.getMessage();
                if (!TextUtils.isEmpty(message)) {
                    message.contains("no such table");
                }
            }
            if (sQLiteDatabaseOpenOrCreateDatabase == null || !sQLiteDatabaseOpenOrCreateDatabase.isOpen()) {
                return;
            }
            sQLiteDatabaseOpenOrCreateDatabase.close();
        } catch (Throwable th2) {
            try {
                com.autonavi.aps.amapapi.utils.b.a(th2, "DB", "clearHist p2");
                if (sQLiteDatabaseOpenOrCreateDatabase == null || !sQLiteDatabaseOpenOrCreateDatabase.isOpen()) {
                    return;
                }
                sQLiteDatabaseOpenOrCreateDatabase.close();
            } finally {
                if (sQLiteDatabaseOpenOrCreateDatabase != null && sQLiteDatabaseOpenOrCreateDatabase.isOpen()) {
                    sQLiteDatabaseOpenOrCreateDatabase.close();
                }
            }
        }
    }

    public final void b(Context context) {
        try {
            c();
            c(context);
            this.j = false;
            this.d = null;
            this.n = 0L;
        } catch (Throwable th) {
            com.autonavi.aps.amapapi.utils.b.a(th, "Cache", "destroy part");
        }
    }

    public final void a(AMapLocationClientOption aMapLocationClientOption) {
        this.g = aMapLocationClientOption.isNeedAddress();
        this.f = aMapLocationClientOption.isOffset();
        this.b = aMapLocationClientOption.isLocationCacheEnable();
        this.h = String.valueOf(aMapLocationClientOption.getGeoLanguage());
    }

    public final void a(d dVar) {
        this.e = dVar;
    }

    private boolean a(com.autonavi.aps.amapapi.model.a aVar, boolean z) {
        if (a(z)) {
            return aVar == null || com.autonavi.aps.amapapi.utils.a.a(aVar.getTime()) || z;
        }
        return false;
    }

    private boolean a(boolean z) {
        if (com.autonavi.aps.amapapi.utils.a.e() || z) {
            return this.b || com.autonavi.aps.amapapi.utils.a.f() || z;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:63:0x00d5  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x00e0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final com.autonavi.aps.amapapi.model.a a(com.autonavi.aps.amapapi.restruct.e r16, boolean r17, com.autonavi.aps.amapapi.model.a r18, com.autonavi.aps.amapapi.restruct.k r19, java.lang.StringBuilder r20, java.lang.String r21, android.content.Context r22, boolean r23) {
        /*
            Method dump skipped, instruction units count: 241
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.aps.amapapi.storage.a.a(com.autonavi.aps.amapapi.restruct.e, boolean, com.autonavi.aps.amapapi.model.a, com.autonavi.aps.amapapi.restruct.k, java.lang.StringBuilder, java.lang.String, android.content.Context, boolean):com.autonavi.aps.amapapi.model.a");
    }

    public final void a(String str) {
        this.d = str;
    }

    public final void a() {
        this.c = 0L;
        this.d = null;
    }
}
