package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.col.p0003sl.go;
import com.amap.api.col.p0003sl.lb;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.ServiceSettings;
import java.util.Map;

/* JADX INFO: compiled from: BasicHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class fg<T, V> extends in {
    protected T b;
    protected Context e;
    protected boolean a = true;
    protected int c = 1;
    protected String d = "";
    private int g = 1;
    protected String f = "";

    protected abstract V a(String str) throws AMapException;

    protected abstract String c();

    protected go.b e() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public Map<String, String> getParams() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public Map<String, String> getRequestHead() {
        return null;
    }

    @Override // com.amap.api.col.p0003sl.lb
    public String getSDKName() {
        return "sea";
    }

    public fg(Context context, T t) {
        a(context, t);
    }

    private void a(Context context, T t) {
        this.e = context;
        this.b = t;
        this.c = 1;
        setSoTimeout(ServiceSettings.getInstance().getSoTimeOut());
        setConnectionTimeout(ServiceSettings.getInstance().getConnectionTimeOut());
    }

    private String f() {
        return this.f;
    }

    private String g() {
        String ipv6url = getIPV6URL();
        if (ipv6url == null) {
            return null;
        }
        try {
            int iIndexOf = ipv6url.indexOf(".com/");
            int iIndexOf2 = ipv6url.indexOf("?");
            if (iIndexOf2 == -1) {
                return ipv6url.substring(iIndexOf + 5);
            }
            return ipv6url.substring(iIndexOf + 5, iIndexOf2);
        } catch (Throwable unused) {
            return null;
        }
    }

    protected V a(byte[] bArr) throws AMapException {
        String str;
        try {
            str = new String(bArr, "utf-8");
        } catch (Exception e) {
            fp.a(e, "ProtocalHandler", "loadData");
            str = null;
        }
        if (str == null || str.equals("")) {
            return null;
        }
        fp.b(str);
        return a(str);
    }

    public final V d() throws AMapException {
        if (this.b == null) {
            return null;
        }
        try {
            return h();
        } catch (AMapException e) {
            gy.a(g(), f(), e);
            throw e;
        }
    }

    private V h() throws AMapException {
        V v;
        go goVarA;
        go.c cVarA;
        try {
            go.b bVarE = e();
            boolean zB = go.a().b(bVarE);
            boolean z = false;
            int i = 0;
            boolean z2 = false;
            V vB = null;
            while (i < this.c) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                try {
                    try {
                        try {
                            int protocol = ServiceSettings.getInstance().getProtocol();
                            im.a().a(this.e);
                            la laVarC = la.c();
                            if (zB && (cVarA = go.a().a(bVarE)) != null && cVarA.a != null) {
                                vB = (V) cVarA.a;
                                try {
                                    gy.a(this.e, bVarE.a, cVarA.b);
                                    z2 = true;
                                } catch (Cif e) {
                                    e = e;
                                    z2 = true;
                                    gy.a(this.e, g(), System.currentTimeMillis() - jCurrentTimeMillis, z);
                                    i++;
                                    if (i >= this.c) {
                                        if (!com.amap.api.maps.AMapException.ERROR_CONNECTION.equals(e.getMessage()) && !com.amap.api.maps.AMapException.ERROR_SOCKET.equals(e.getMessage()) && !com.amap.api.maps.AMapException.ERROR_UNKNOWN.equals(e.a()) && !com.amap.api.maps.AMapException.ERROR_UNKNOW_SERVICE.equals(e.getMessage())) {
                                            throw new AMapException(e.a(), 1, e.c());
                                        }
                                        throw new AMapException(AMapException.AMAP_CLIENT_NETWORK_EXCEPTION, 1, e.c());
                                    }
                                    try {
                                        Thread.sleep(this.g * 1000);
                                        if (zB && !z2) {
                                            go.a().a(bVarE, vB);
                                        }
                                    } catch (InterruptedException unused) {
                                        if (!com.amap.api.maps.AMapException.ERROR_CONNECTION.equals(e.getMessage()) && !com.amap.api.maps.AMapException.ERROR_SOCKET.equals(e.getMessage()) && !com.amap.api.maps.AMapException.ERROR_UNKNOW_SERVICE.equals(e.getMessage())) {
                                            throw new AMapException(e.a(), 1, e.c());
                                        }
                                        throw new AMapException(AMapException.AMAP_CLIENT_NETWORK_EXCEPTION, 1, e.c());
                                    }
                                } catch (AMapException e2) {
                                    e = e2;
                                    z2 = true;
                                    gy.a(this.e, g(), System.currentTimeMillis() - jCurrentTimeMillis, z);
                                    i++;
                                    if (i >= this.c) {
                                        throw e;
                                    }
                                    if (zB && !z2) {
                                        goVarA = go.a();
                                        goVarA.a(bVarE, vB);
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    v = vB;
                                    z2 = true;
                                    if (zB) {
                                        go.a().a(bVarE, v);
                                    }
                                    throw th;
                                }
                            }
                            if (vB == null) {
                                byte[] bArrA = a(protocol, laVarC, this);
                                long jCurrentTimeMillis2 = System.currentTimeMillis();
                                vB = b(bArrA);
                                gy.a(this.e, g(), jCurrentTimeMillis2 - jCurrentTimeMillis, true);
                            }
                            i = this.c;
                        } catch (Throwable th2) {
                            th = th2;
                            v = null;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        v = vB;
                        if (zB && !z2) {
                            go.a().a(bVarE, v);
                        }
                        throw th;
                    }
                } catch (Cif e3) {
                    e = e3;
                } catch (AMapException e4) {
                    e = e4;
                }
                if (!zB || z2) {
                    z = false;
                } else {
                    goVarA = go.a();
                    goVarA.a(bVarE, vB);
                }
            }
            return vB;
        } catch (AMapException e5) {
            throw e5;
        } catch (Throwable th4) {
            th4.printStackTrace();
            throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWN_ERROR);
        }
    }

    private byte[] a(int i, la laVar, in inVar) throws Cif {
        lc lcVarE;
        setHttpProtocol(i == 1 ? lb.c.HTTP : lb.c.HTTPS);
        if (this.a) {
            lcVarE = la.a(inVar);
        } else {
            lcVarE = la.e(inVar);
        }
        if (lcVarE == null) {
            return null;
        }
        byte[] bArr = lcVarE.a;
        this.f = lcVarE.d;
        return bArr;
    }

    private V b(byte[] bArr) throws AMapException {
        return a(bArr);
    }
}
