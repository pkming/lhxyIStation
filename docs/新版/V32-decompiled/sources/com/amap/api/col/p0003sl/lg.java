package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.kr;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: compiled from: LogEngine.java */
/* JADX INFO: loaded from: classes2.dex */
public final class lg {
    public static void a(String str, byte[] bArr, lf lfVar) throws Throwable {
        kr krVarA;
        OutputStream outputStreamA = null;
        try {
            if (a(lfVar.a, str)) {
                return;
            }
            File file = new File(lfVar.a);
            if (!file.exists()) {
                file.mkdirs();
            }
            krVarA = kr.a(file, lfVar.b);
            try {
                krVarA.a(lfVar.d);
                byte[] bArrB = lfVar.e.b(bArr);
                kr.a aVarB = krVarA.b(str);
                outputStreamA = aVarB.a();
                outputStreamA.write(bArrB);
                aVarB.b();
                krVarA.c();
                if (outputStreamA != null) {
                    try {
                        outputStreamA.close();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
                try {
                    krVarA.close();
                    return;
                } catch (Throwable th2) {
                    th2.printStackTrace();
                    return;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Throwable th4) {
            th = th4;
            krVarA = null;
        }
        if (outputStreamA != null) {
            try {
                outputStreamA.close();
            } catch (Throwable th5) {
                th5.printStackTrace();
            }
        }
        if (krVarA != null) {
            try {
                krVarA.close();
                throw th;
            } catch (Throwable th6) {
                th6.printStackTrace();
                throw th;
            }
        }
        throw th;
    }

    public static int a(lf lfVar) {
        kr krVar = null;
        try {
            try {
                if (lfVar.f.d()) {
                    lfVar.f.a_(true);
                    kr krVarA = kr.a(new File(lfVar.a), lfVar.b);
                    try {
                        ArrayList arrayList = new ArrayList();
                        byte[] bArrA = a(krVarA, lfVar, arrayList);
                        if (bArrA != null && bArrA.length != 0) {
                            jv jvVar = new jv(bArrA, lfVar.c);
                            ku.a();
                            JSONObject jSONObject = new JSONObject(new String(ku.a(jvVar).a));
                            if (jSONObject.has("code") && jSONObject.getInt("code") == 1) {
                                if (lfVar.f != null && bArrA != null) {
                                    lfVar.f.a_(bArrA.length);
                                }
                                if (lfVar.f.a() < Integer.MAX_VALUE) {
                                    a(krVarA, arrayList);
                                } else {
                                    try {
                                        krVarA.d();
                                    } catch (Throwable th) {
                                        jw.c(th, "ofm", "dlo");
                                    }
                                }
                                return bArrA.length;
                            }
                            krVar = krVarA;
                        }
                        try {
                            krVarA.close();
                        } catch (Throwable th2) {
                            th2.printStackTrace();
                        }
                        return -1;
                    } catch (Throwable th3) {
                        th = th3;
                        krVar = krVarA;
                        try {
                            jw.c(th, "leg", "uts");
                            if (krVar != null) {
                                krVar.close();
                            }
                            return -1;
                        } catch (Throwable th4) {
                            if (krVar != null) {
                                try {
                                    krVar.close();
                                } catch (Throwable th5) {
                                    th5.printStackTrace();
                                }
                            }
                            throw th4;
                        }
                    }
                }
            } catch (Throwable th6) {
                th6.printStackTrace();
            }
        } catch (Throwable th7) {
            th = th7;
        }
        if (krVar != null) {
            krVar.close();
        }
        return -1;
    }

    private static byte[] a(kr krVar, lf lfVar, List<String> list) {
        try {
            File fileB = krVar.b();
            if (fileB != null && fileB.exists()) {
                int length = 0;
                for (String str : fileB.list()) {
                    if (str.contains(".0")) {
                        String str2 = str.split("\\.")[0];
                        byte[] bArrA = lm.a(krVar, str2);
                        length += bArrA.length;
                        list.add(str2);
                        if (length > lfVar.f.a()) {
                            break;
                        }
                        lfVar.g.b(bArrA);
                    }
                }
                if (length <= 0) {
                    return null;
                }
                return lfVar.g.a();
            }
        } catch (Throwable th) {
            jw.c(th, "leg", "gCo");
        }
        return new byte[0];
    }

    private static void a(kr krVar, List<String> list) {
        if (krVar != null) {
            try {
                Iterator<String> it = list.iterator();
                while (it.hasNext()) {
                    krVar.c(it.next());
                }
                krVar.close();
            } catch (Throwable th) {
                jw.c(th, "ofm", "dlo");
            }
        }
    }

    private static boolean a(String str, String str2) {
        try {
            return new File(str, str2 + ".0").exists();
        } catch (Throwable th) {
            jw.c(th, "leg", "fet");
            return false;
        }
    }
}
