package com.amap.api.col.p0003sl;

import android.content.Context;
import android.view.Window;
import com.amap.api.maps.AMapException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: compiled from: AbstractBasicHandler.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class hy<T, V> extends db {
    protected T a;
    protected Context c;
    protected String d;
    protected int b = 1;
    protected boolean e = false;

    protected V a(lc lcVar) throws hx {
        return null;
    }

    protected abstract V a(String str) throws hx;

    protected abstract String c();

    public hy(Context context, T t) {
        a(context, t);
    }

    private void a(Context context, T t) {
        this.c = context;
        this.a = t;
        this.b = 1;
        setSoTimeout(Window.PROGRESS_SECONDARY_END);
        setConnectionTimeout(Window.PROGRESS_SECONDARY_END);
    }

    protected V a(byte[] bArr) throws hx {
        String str;
        try {
            str = new String(bArr, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            str = null;
        }
        if (str == null || "".equals(str)) {
            return null;
        }
        ia.a(str);
        return a(str);
    }

    public final V d() throws hx {
        if (this.a == null) {
            return null;
        }
        try {
            return e();
        } catch (hx e) {
            dx.a(e);
            throw e;
        }
    }

    private V e() throws hx {
        V vB = null;
        int i = 0;
        while (i < this.b) {
            try {
                setProxy(ir.a(this.c));
                if (this.e) {
                    vB = b(makeHttpRequestNeedHeader());
                } else {
                    vB = b(makeHttpRequest());
                }
                i = this.b;
            } catch (hx e) {
                i++;
                if (i >= this.b) {
                    throw new hx(e.a());
                }
            } catch (Cif e2) {
                i++;
                if (i < this.b) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException unused) {
                        if (AMapException.ERROR_CONNECTION.equals(e2.getMessage()) || AMapException.ERROR_SOCKET.equals(e2.getMessage()) || AMapException.ERROR_UNKNOW_SERVICE.equals(e2.getMessage())) {
                            throw new hx(com.amap.api.services.core.AMapException.AMAP_CLIENT_NETWORK_EXCEPTION);
                        }
                        throw new hx(e2.a());
                    }
                } else {
                    if (AMapException.ERROR_CONNECTION.equals(e2.getMessage()) || AMapException.ERROR_SOCKET.equals(e2.getMessage()) || AMapException.ERROR_UNKNOWN.equals(e2.a()) || AMapException.ERROR_UNKNOW_SERVICE.equals(e2.getMessage())) {
                        throw new hx(com.amap.api.services.core.AMapException.AMAP_CLIENT_NETWORK_EXCEPTION);
                    }
                    throw new hx(e2.a());
                }
            }
        }
        return vB;
    }

    private V b(byte[] bArr) throws hx {
        return a(bArr);
    }

    private V b(lc lcVar) throws hx {
        return a(lcVar);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public Map<String, String> getRequestHead() {
        is isVarA = dx.a();
        String strB = isVarA != null ? isVarA.b() : null;
        Hashtable hashtable = new Hashtable(16);
        hashtable.put("User-Agent", w.c);
        hashtable.put("Accept-Encoding", "gzip");
        hashtable.put("platinfo", String.format(Locale.US, "platform=Android&sdkversion=%s&product=%s", strB, "3dmap"));
        hashtable.put("X-INFO", ij.b(this.c));
        hashtable.put("key", ig.f(this.c));
        hashtable.put("logversion", "2.1");
        return hashtable;
    }
}
