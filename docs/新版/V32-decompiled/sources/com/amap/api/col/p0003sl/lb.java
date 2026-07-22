package com.amap.api.col.p0003sl;

import android.text.TextUtils;
import com.amap.api.col.p0003sl.ku;
import java.net.Proxy;
import java.util.Map;

/* JADX INFO: compiled from: Request.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class lb {
    public static final int DEFAULT_RETRY_TIMEOUT = 5000;
    private String d;
    private boolean e;
    private boolean f;
    ku.a o;
    int l = 20000;
    int m = 20000;
    Proxy n = null;
    private boolean a = false;
    private int b = 20000;
    private boolean c = true;
    private a g = a.NORMAL;
    private b h = b.FIRST_NONDEGRADE;

    public byte[] getEntityBytes() {
        return null;
    }

    protected String getIPDNSName() {
        return "";
    }

    public abstract Map<String, String> getParams();

    public abstract Map<String, String> getRequestHead();

    public String getSDKName() {
        return "";
    }

    public abstract String getURL();

    public boolean isIgnoreGZip() {
        return false;
    }

    public boolean isSupportIPV6() {
        return false;
    }

    /* JADX INFO: compiled from: Request.java */
    public enum a {
        NORMAL(0),
        INTERRUPT_IO(1),
        NEVER(2),
        FIX(3),
        SINGLE(4);

        private int f;

        a(int i) {
            this.f = i;
        }
    }

    /* JADX INFO: compiled from: Request.java */
    public enum b {
        FIRST_NONDEGRADE(0),
        NEVER_GRADE(1),
        DEGRADE_BYERROR(2),
        DEGRADE_ONLY(3),
        FIX_NONDEGRADE(4),
        FIX_DEGRADE_BYERROR(5),
        FIX_DEGRADE_ONLY(6);

        private int h;

        b(int i2) {
            this.h = i2;
        }

        public final int a() {
            return this.h;
        }

        public final boolean b() {
            int i2 = this.h;
            return i2 == FIRST_NONDEGRADE.h || i2 == NEVER_GRADE.h || i2 == FIX_NONDEGRADE.h;
        }

        public final boolean c() {
            int i2 = this.h;
            return i2 == DEGRADE_BYERROR.h || i2 == DEGRADE_ONLY.h || i2 == FIX_DEGRADE_BYERROR.h || i2 == FIX_DEGRADE_ONLY.h;
        }

        public final boolean d() {
            int i2 = this.h;
            return i2 == DEGRADE_BYERROR.h || i2 == FIX_DEGRADE_BYERROR.h;
        }

        public final boolean e() {
            return this.h == NEVER_GRADE.h;
        }
    }

    /* JADX INFO: compiled from: Request.java */
    public enum c {
        HTTP(0),
        HTTPS(1);

        private int c;

        c(int i) {
            this.c = i;
        }
    }

    final String a() {
        return a(getURL());
    }

    private String a(String str) {
        byte[] entityBytes = getEntityBytes();
        if (entityBytes == null || entityBytes.length == 0) {
            return str;
        }
        Map<String, String> params = getParams();
        if (ku.e != null) {
            if (params != null) {
                params.putAll(ku.e);
            } else {
                params = ku.e;
            }
        }
        if (params == null) {
            return str;
        }
        String strA = ky.a(params);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str).append("?").append(strA);
        return stringBuffer.toString();
    }

    final String b() {
        return a(getIPV6URL());
    }

    protected boolean isIPRequest() {
        return !TextUtils.isEmpty(getIPDNSName());
    }

    public String getIPV6URL() {
        return getURL();
    }

    public final void setConnectionTimeout(int i) {
        this.l = i;
    }

    public final void setSoTimeout(int i) {
        this.m = i;
    }

    public int getConntectionTimeout() {
        return this.l;
    }

    public int getSoTimeout() {
        return this.m;
    }

    public final void setProxy(Proxy proxy) {
        this.n = proxy;
    }

    public Proxy getProxy() {
        return this.n;
    }

    public void setDegradeAbility(a aVar) {
        this.g = aVar;
    }

    protected a getDegradeAbility() {
        return this.g;
    }

    protected boolean isBinary() {
        return this.a;
    }

    public void setBinary(boolean z) {
        this.a = z;
    }

    protected boolean isHttps() {
        return this.f;
    }

    public void setHttpProtocol(c cVar) {
        this.f = cVar == c.HTTPS;
    }

    public ku.a getUrlConnectionImpl() {
        return this.o;
    }

    public void setUrlConnectionImpl(ku.a aVar) {
        this.o = aVar;
    }

    protected b getDegradeType() {
        return this.h;
    }

    public void setDegradeType(b bVar) {
        this.h = bVar;
    }

    protected int getReal_max_timeout() {
        return this.b;
    }

    public void setReal_max_timeout(int i) {
        this.b = i;
    }

    protected boolean isHostToIP() {
        return this.c;
    }

    public void setHostToIP(boolean z) {
        this.c = z;
    }

    protected String getNon_degrade_final_Host() {
        return this.d;
    }

    public void setNon_degrade_final_Host(String str) {
        this.d = str;
    }

    protected boolean isIPV6Request() {
        return this.e;
    }

    public void setIPV6Request(boolean z) {
        this.e = z;
    }

    protected String parseSdkNameFromRequest() {
        String sDKName;
        try {
            sDKName = getSDKName();
            try {
                if (TextUtils.isEmpty(sDKName)) {
                    if (this.a) {
                        sDKName = parseSDKNameFromPlatInfo(((kv) this).g());
                    } else {
                        sDKName = parseSdkNameFromHeader(getRequestHead());
                    }
                }
            } catch (Throwable th) {
                th = th;
                jt.a(th, "ht", "pnfr");
            }
        } catch (Throwable th2) {
            th = th2;
            sDKName = "";
        }
        return sDKName;
    }

    protected String parseSdkNameFromHeader(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        try {
            if (map.containsKey("platinfo")) {
                return parseSDKNameFromPlatInfo(map.get("platinfo"));
            }
            return null;
        } catch (Throwable th) {
            jt.a(th, "ht", "pnfh");
            return null;
        }
    }

    protected String parseSDKNameFromPlatInfo(String str) {
        String str2;
        String strTrim = "";
        try {
            if (!TextUtils.isEmpty(str)) {
                String[] strArrSplit = str.split("&");
                if (strArrSplit.length > 1) {
                    int length = strArrSplit.length;
                    int i = 0;
                    String str3 = "";
                    while (true) {
                        if (i >= length) {
                            str2 = "";
                            break;
                        }
                        str2 = strArrSplit[i];
                        if (str2.contains("sdkversion")) {
                            str3 = str2;
                        }
                        if (str2.contains("product")) {
                            break;
                        }
                        i++;
                    }
                    if (!TextUtils.isEmpty(str2)) {
                        String[] strArrSplit2 = str2.split("=");
                        if (strArrSplit2.length > 1) {
                            strTrim = strArrSplit2[1].trim();
                            if (!TextUtils.isEmpty(str3) && TextUtils.isEmpty(jh.a(strTrim))) {
                                String[] strArrSplit3 = str3.split("=");
                                if (strArrSplit3.length > 1) {
                                    jh.a(strTrim, strArrSplit3[1].trim());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable th) {
            jt.a(th, "ht", "pnfp");
        }
        return strTrim;
    }
}
