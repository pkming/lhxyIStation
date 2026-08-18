package com.amap.api.col.p0003sl;

import android.content.Context;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: compiled from: BinaryRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class kv extends in {
    protected Context a;
    protected is b;
    protected byte[] c;

    public abstract byte[] c();

    public abstract byte[] d();

    protected String e() {
        return "2.1";
    }

    public boolean f() {
        return true;
    }

    protected boolean h() {
        return false;
    }

    public kv(Context context, is isVar) {
        if (context != null) {
            this.a = context.getApplicationContext();
        }
        this.b = isVar;
        setBinary(true);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public Map<String, String> getParams() {
        String strF = ig.f(this.a);
        String strA = ij.a();
        String strA2 = ij.a(this.a, strA, "key=".concat(String.valueOf(strF)));
        HashMap map = new HashMap();
        map.put(SPUserInfoUtils.TS, strA);
        map.put("key", strF);
        map.put("scode", strA2);
        return map;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public final byte[] getEntityBytes() {
        byte[] bArr = this.c;
        if (bArr != null) {
            return bArr;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(i());
            byteArrayOutputStream.write(j());
            byteArrayOutputStream.write(k());
            byteArrayOutputStream.write(l());
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            this.c = byteArray;
            return byteArray;
        } catch (Throwable th) {
            try {
                jt.a(th, "bre", "geb");
                try {
                    byteArrayOutputStream.close();
                    return null;
                } catch (Throwable th2) {
                    jt.a(th2, "bre", "geb");
                    return null;
                }
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th3) {
                    jt.a(th3, "bre", "geb");
                }
            }
        }
    }

    private static byte[] i() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(it.a("PANDORA$"));
            byteArrayOutputStream.write(new byte[]{1});
            byteArrayOutputStream.write(new byte[]{0});
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable th) {
            try {
                jt.a(th, "bre", "gbh");
                try {
                    byteArrayOutputStream.close();
                    return null;
                } catch (Throwable th2) {
                    jt.a(th2, "bre", "gbh");
                    return null;
                }
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th3) {
                    jt.a(th3, "bre", "gbh");
                }
            }
        }
    }

    private byte[] j() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(new byte[]{3});
            if (f()) {
                Context context = this.a;
                boolean zH = h();
                is isVar = this.b;
                byte[] bArrA = ij.a(context, zH, isVar != null && "navi".equals(isVar.a()));
                byteArrayOutputStream.write(a(bArrA));
                byteArrayOutputStream.write(bArrA);
            } else {
                byteArrayOutputStream.write(new byte[]{0, 0});
            }
            byte[] bArrA2 = it.a(e());
            if (bArrA2 != null && bArrA2.length > 0) {
                byteArrayOutputStream.write(a(bArrA2));
                byteArrayOutputStream.write(bArrA2);
            } else {
                byteArrayOutputStream.write(new byte[]{0, 0});
            }
            byte[] bArrA3 = it.a(g());
            if (bArrA3 != null && bArrA3.length > 0) {
                byteArrayOutputStream.write(a(bArrA3));
                byteArrayOutputStream.write(bArrA3);
            } else {
                byteArrayOutputStream.write(new byte[]{0, 0});
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable th) {
            try {
                jt.a(th, "bre", "gpd");
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th2) {
                    jt.a(th2, "bre", "gred");
                }
                return new byte[]{0};
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th3) {
                    jt.a(th3, "bre", "gred");
                }
            }
        }
    }

    public String g() {
        return String.format("platform=Android&sdkversion=%s&product=%s", this.b.c(), this.b.a());
    }

    protected static byte[] a(byte[] bArr) {
        return it.a(bArr.length);
    }

    private byte[] k() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] bArrC = c();
            if (bArrC != null && bArrC.length != 0) {
                byteArrayOutputStream.write(new byte[]{1});
                byteArrayOutputStream.write(a(bArrC));
                byteArrayOutputStream.write(bArrC);
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(new byte[]{0});
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            } catch (Throwable th) {
                jt.a(th, "bre", "grrd");
            }
            return byteArray;
        } catch (Throwable th2) {
            try {
                jt.a(th2, "bre", "grrd");
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th3) {
                    jt.a(th3, "bre", "grrd");
                }
                return new byte[]{0};
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th4) {
                    jt.a(th4, "bre", "grrd");
                }
            }
        }
    }

    private byte[] l() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] bArrD = d();
            if (bArrD != null && bArrD.length != 0) {
                byteArrayOutputStream.write(new byte[]{1});
                byte[] bArrA = ij.a(bArrD);
                byteArrayOutputStream.write(a(bArrA));
                byteArrayOutputStream.write(bArrA);
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(new byte[]{0});
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            } catch (Throwable th) {
                jt.a(th, "bre", "gred");
            }
            return byteArray;
        } catch (Throwable th2) {
            try {
                jt.a(th2, "bre", "gred");
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th3) {
                    jt.a(th3, "bre", "gred");
                }
                return new byte[]{0};
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable th4) {
                    jt.a(th4, "bre", "gred");
                }
            }
        }
    }
}
