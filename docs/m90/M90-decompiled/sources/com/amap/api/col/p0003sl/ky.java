package com.amap.api.col.p0003sl;

import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateFormat;
import com.amap.api.col.p0003sl.ih;
import com.amap.api.col.p0003sl.ku;
import com.amap.api.col.p0003sl.lb;
import com.amap.api.maps.AMapException;
import com.amap.apis.utils.core.api.AMapUtilCoreApi;
import com.amap.apis.utils.core.api.NetProxy;
import com.unisound.client.SpeechConstants;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSession;
import org.apache.http.conn.ConnectTimeoutException;

/* JADX INFO: compiled from: HttpUrlUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ky {
    private static SoftReference<SSLContext> k;
    private static SoftReference<kz> t;
    private boolean a;
    private SSLContext b;
    private Proxy c;
    private String g;
    private ku.a h;
    private d i;
    private boolean l;
    private String m;
    private String n;
    private volatile boolean d = false;
    private long e = -1;
    private long f = 0;
    private String j = "";
    private boolean o = false;
    private boolean p = false;
    private String q = "";
    private String r = "";
    private String s = "";
    private f u = new f();

    public static void b() {
    }

    private void d(lb lbVar) throws Cif {
        this.i = new d((byte) 0);
        this.p = lbVar.isIPV6Request();
        this.c = lbVar.getProxy();
        this.h = lbVar.getUrlConnectionImpl();
        this.l = lbVar.isBinary();
        this.j = lbVar.parseSdkNameFromRequest();
        this.a = im.a().b(lbVar.isHttps());
        String strB = lbVar.getDegradeType().b() ? lbVar.b() : lbVar.a();
        this.m = strB;
        String strA = a(strB);
        this.m = strA;
        String strA2 = kx.a(strA, this.j);
        this.m = strA2;
        "restrictionURLTest: ".concat(String.valueOf(strA2));
        if (!le.a().a(strA2)) {
            this.n = lbVar.getIPDNSName();
            if ("loc".equals(this.j)) {
                String strA3 = lbVar.a();
                String strB2 = lbVar.b();
                if (!TextUtils.isEmpty(strA3)) {
                    try {
                        this.r = new URL(strA3).getHost();
                    } catch (Exception unused) {
                    }
                }
                if (TextUtils.isEmpty(strB2)) {
                    return;
                }
                try {
                    if (!TextUtils.isEmpty(this.n)) {
                        this.q = this.n;
                        return;
                    } else {
                        this.q = new URL(strB2).getHost();
                        return;
                    }
                } catch (Exception unused2) {
                    return;
                }
            }
            return;
        }
        "restriction hit: ".concat(String.valueOf(strA2));
        throw new Cif("限制访问的接口");
    }

    private static String a(String str) {
        URL url;
        int i;
        NetProxy netProxy = AMapUtilCoreApi.getNetProxy();
        if (netProxy == null) {
            return str;
        }
        URL url2 = null;
        try {
            url = new URL(str);
        } catch (Exception e2) {
            e2.printStackTrace();
            url = null;
        }
        if (url == null) {
            return str;
        }
        String strOnHostProxy = netProxy.onHostProxy(url.getHost(), url.getPath());
        if (TextUtils.isEmpty(strOnHostProxy)) {
            return str;
        }
        String[] strArrSplit = strOnHostProxy.split(":");
        int port = 0;
        if (strArrSplit.length == 2) {
            try {
                i = Integer.parseInt(strArrSplit[1]);
            } catch (Exception e3) {
                e3.printStackTrace();
                i = 0;
            }
            port = i;
            strOnHostProxy = strArrSplit[0];
        }
        try {
            String protocol = url.getProtocol();
            if (port == 0) {
                port = url.getPort();
            }
            url2 = new URL(protocol, strOnHostProxy, port, url.getFile().toString());
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        return url2 == null ? str : url2.toString();
    }

    ky() {
        ih.e();
        try {
            this.g = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        } catch (Throwable th) {
            jt.a(th, "ht", "ic");
        }
    }

    final void a() {
        this.d = true;
    }

    final void a(long j) {
        this.f = j;
    }

    final void b(long j) {
        this.e = j;
    }

    private static String a(String str, Map<String, String> map) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        if (ku.e != null) {
            if (map != null) {
                map.putAll(ku.e);
            } else {
                map = ku.e;
            }
        }
        if (map == null || map.size() <= 0) {
            return str;
        }
        int iIndexOf = str.indexOf("?");
        if (iIndexOf >= 0) {
            HashMap map2 = new HashMap();
            String strSubstring = str.substring(iIndexOf);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null) {
                    value = "";
                }
                if (!strSubstring.matches(".*[\\?\\&]" + URLEncoder.encode(key) + "=.*")) {
                    map2.put(key, value);
                }
            }
            map = map2;
        }
        if (map.size() == 0) {
            return str;
        }
        String strA = a(map);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str);
        if (iIndexOf >= 0) {
            if (!str.endsWith("?") && !str.endsWith("&")) {
                stringBuffer.append("&");
            }
        } else {
            stringBuffer.append("?");
        }
        if (strA != null) {
            stringBuffer.append(strA);
        }
        return stringBuffer.toString();
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x0076  */
    /* JADX WARN: Removed duplicated region for block: B:505:0x0523 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:509:0x0431 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:511:0x043c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:513:0x0559 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:515:0x0564 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:517:0x046f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:519:0x047a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:523:0x0388 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:527:0x0393 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:529:0x059a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:531:0x05a5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:533:0x04ad A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:535:0x04b8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:537:0x03c0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:539:0x03cb A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:541:0x05d8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:543:0x04e2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:545:0x05e3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:550:0x04ed A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:552:0x03f5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:554:0x0400 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:560:0x0518 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:566:0x0420 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:568:0x03e4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:572:0x0507 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:574:0x0548 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:576:0x03af A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:578:0x045e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:580:0x0377 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:582:0x05c7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:584:0x0589 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:586:0x04d1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:588:0x049c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    final void a(com.amap.api.col.p0003sl.lb r19, com.amap.api.col.3sl.kw.a r20) {
        /*
            Method dump skipped, instruction units count: 1563
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ky.a(com.amap.api.col.3sl.lb, com.amap.api.col.3sl.kw$a):void");
    }

    final Map<String, String> a(lb lbVar) throws Cif {
        int i;
        HttpURLConnection httpURLConnection;
        String headerFieldKey;
        HttpURLConnection httpURLConnection2 = null;
        try {
            try {
                d(lbVar);
                this.m = a(this.m, lbVar.getParams());
                httpURLConnection = a(lbVar, false, false).a;
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        httpURLConnection2.disconnect();
                    } catch (Throwable th2) {
                        jt.a(th2, "hth", "mgr");
                    }
                }
                this.u.d();
                throw th;
            }
        } catch (Cif e2) {
            e = e2;
        } catch (ConnectException e3) {
            e = e3;
        } catch (SocketException e4) {
            e = e4;
        } catch (SocketTimeoutException e5) {
            e = e5;
        } catch (InterruptedIOException unused) {
        } catch (MalformedURLException unused2) {
        } catch (UnknownHostException unused3) {
        } catch (SSLException e6) {
            e = e6;
        } catch (ConnectTimeoutException e7) {
            e = e7;
        } catch (IOException unused4) {
        } catch (Throwable th3) {
            th = th3;
        }
        try {
            this.u.b = SystemClock.elapsedRealtime();
            httpURLConnection.connect();
            this.u.a();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode >= 400) {
                this.u.b(responseCode);
                this.u.a(10);
                Cif cif = new Cif("http读取header失败");
                cif.a(responseCode);
                throw cif;
            }
            HashMap map = new HashMap();
            for (i = 0; i < 50 && (headerFieldKey = httpURLConnection.getHeaderFieldKey(i)) != null; i++) {
                map.put(headerFieldKey.toLowerCase(), httpURLConnection.getHeaderField(headerFieldKey));
            }
            this.u.a((lc) null);
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Throwable th4) {
                    jt.a(th4, "hth", "mgr");
                }
            }
            this.u.d();
            return map;
        } catch (Cif e8) {
            e = e8;
            this.u.a(e.g());
            throw e;
        } catch (ConnectException e9) {
            e = e9;
            this.u.b(a(e));
            this.u.a(6);
            throw new Cif(AMapException.ERROR_CONNECTION);
        } catch (MalformedURLException unused5) {
            this.u.a(8);
            throw new Cif("url异常 - MalformedURLException");
        } catch (ConnectTimeoutException e10) {
            e = e10;
            e.printStackTrace();
            this.u.b(a(e));
            this.u.a(2);
            throw new Cif("IO 操作异常 - IOException");
        } catch (InterruptedIOException unused6) {
            this.u.b(7101);
            this.u.a(7);
            throw new Cif(AMapException.ERROR_UNKNOWN);
        } catch (SocketException e11) {
            e = e11;
            this.u.b(a(e));
            this.u.a(6);
            throw new Cif(AMapException.ERROR_SOCKET);
        } catch (SocketTimeoutException e12) {
            e = e12;
            this.u.b(a(e));
            this.u.a(2);
            throw new Cif("socket 连接超时 - SocketTimeoutException");
        } catch (UnknownHostException unused7) {
            this.u.a(9);
            throw new Cif("未知主机 - UnKnowHostException");
        } catch (SSLException e13) {
            e = e13;
            e.printStackTrace();
            this.u.b(a(e));
            this.u.a(4);
            throw new Cif("IO 操作异常 - IOException");
        } catch (IOException unused8) {
            this.u.a(7);
            throw new Cif("IO 操作异常 - IOException");
        } catch (Throwable th5) {
            th = th5;
            this.u.a(9);
            th.printStackTrace();
            throw new Cif(AMapException.ERROR_UNKNOWN);
        }
    }

    final lc b(lb lbVar) throws Cif {
        HttpURLConnection httpURLConnection = null;
        try {
            try {
                try {
                    try {
                        try {
                            try {
                                try {
                                    d(lbVar);
                                    String strA = a(this.m, lbVar.getParams());
                                    this.m = strA;
                                    lc lcVarB = kx.b(strA, this.j);
                                    if (lcVarB == null) {
                                        b bVarA = a(lbVar, false, true);
                                        httpURLConnection = bVarA.a;
                                        this.u.b = SystemClock.elapsedRealtime();
                                        httpURLConnection.connect();
                                        this.u.a();
                                        lc lcVarA = a(bVarA, lbVar.isIgnoreGZip());
                                        this.u.a(lcVarA);
                                        if (httpURLConnection != null) {
                                            try {
                                                httpURLConnection.disconnect();
                                            } catch (Throwable th) {
                                                jt.a(th, "ht", "mgr");
                                            }
                                        }
                                        this.u.d();
                                        return lcVarA;
                                    }
                                    this.u.d();
                                    return lcVarB;
                                } catch (ConnectException e2) {
                                    this.u.b(a(e2));
                                    this.u.a(6);
                                    throw new Cif(AMapException.ERROR_CONNECTION);
                                }
                            } catch (Cif e3) {
                                if (!e3.i() && e3.g() != 10) {
                                    this.u.a(e3.f());
                                }
                                throw e3;
                            } catch (Throwable th2) {
                                th2.printStackTrace();
                                this.u.a(9);
                                throw new Cif(AMapException.ERROR_UNKNOWN);
                            }
                        } catch (MalformedURLException unused) {
                            this.u.a(8);
                            throw new Cif("url异常 - MalformedURLException");
                        } catch (SSLException e4) {
                            e4.printStackTrace();
                            this.u.b(a(e4));
                            this.u.a(4);
                            throw new Cif("IO 操作异常 - IOException");
                        }
                    } catch (SocketTimeoutException e5) {
                        this.u.b(a(e5));
                        this.u.a(2);
                        throw new Cif("socket 连接超时 - SocketTimeoutException");
                    } catch (UnknownHostException unused2) {
                        this.u.a(9);
                        throw new Cif("未知主机 - UnKnowHostException");
                    }
                } catch (ConnectTimeoutException e6) {
                    e6.printStackTrace();
                    this.u.b(a(e6));
                    this.u.a(2);
                    throw new Cif("IO 操作异常 - IOException");
                } catch (IOException unused3) {
                    this.u.a(7);
                    throw new Cif("IO 操作异常 - IOException");
                }
            } catch (InterruptedIOException unused4) {
                this.u.b(7101);
                this.u.a(7);
                throw new Cif(AMapException.ERROR_UNKNOWN);
            } catch (SocketException e7) {
                this.u.b(a(e7));
                this.u.a(6);
                throw new Cif(AMapException.ERROR_SOCKET);
            }
        } catch (Throwable th3) {
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Throwable th4) {
                    jt.a(th4, "ht", "mgr");
                }
            }
            this.u.d();
            throw th3;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v0, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r7v15 */
    /* JADX WARN: Type inference failed for: r7v17 */
    final lc c(lb lbVar) throws Cif {
        OutputStream outputStream;
        DataOutputStream dataOutputStream = 0;
        dataOutputStream = 0;
        try {
            try {
                d(lbVar);
                lc lcVarB = kx.b(this.m, this.j);
                if (lcVarB == null) {
                    b bVarA = a(lbVar, true, true);
                    HttpURLConnection httpURLConnection = bVarA.a;
                    try {
                        this.u.b = SystemClock.elapsedRealtime();
                        httpURLConnection.connect();
                        this.u.a();
                        byte[] entityBytes = lbVar.getEntityBytes();
                        if (entityBytes == null || entityBytes.length == 0) {
                            Map<String, String> params = lbVar.getParams();
                            if (ku.e != null) {
                                if (params != null) {
                                    params.putAll(ku.e);
                                } else {
                                    params = ku.e;
                                }
                            }
                            String strA = a(params);
                            if (!TextUtils.isEmpty(strA)) {
                                entityBytes = it.a(strA);
                            }
                        }
                        if (entityBytes != null && entityBytes.length > 0) {
                            try {
                                this.u.b = SystemClock.elapsedRealtime();
                                outputStream = httpURLConnection.getOutputStream();
                                try {
                                    DataOutputStream dataOutputStream2 = new DataOutputStream(outputStream);
                                    try {
                                        dataOutputStream2.write(entityBytes);
                                        dataOutputStream2.close();
                                        if (outputStream != null) {
                                            outputStream.close();
                                        }
                                        this.u.b();
                                    } catch (Throwable th) {
                                        th = th;
                                        dataOutputStream = dataOutputStream2;
                                        if (dataOutputStream != 0) {
                                            dataOutputStream.close();
                                        }
                                        if (outputStream != null) {
                                            outputStream.close();
                                        }
                                        this.u.b();
                                        throw th;
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                outputStream = null;
                            }
                        }
                        lc lcVarA = a(bVarA, lbVar.isIgnoreGZip());
                        this.u.a(lcVarA);
                        if (httpURLConnection != null) {
                            try {
                                httpURLConnection.disconnect();
                            } catch (Throwable th4) {
                                jt.a(th4, "ht", "mPt");
                            }
                        }
                        this.u.d();
                        return lcVarA;
                    } catch (Cif e2) {
                        e = e2;
                    } catch (SocketTimeoutException e3) {
                        e = e3;
                        e.printStackTrace();
                        this.u.b(a(e));
                        this.u.a(2);
                        throw new Cif("socket 连接超时 - SocketTimeoutException");
                    } catch (UnknownHostException e4) {
                        e = e4;
                        e.printStackTrace();
                        this.u.a(5);
                        throw new Cif("未知主机 - UnKnowHostException");
                    } catch (ConnectTimeoutException e5) {
                        e = e5;
                        e.printStackTrace();
                        this.u.b(a(e));
                        this.u.a(2);
                        throw new Cif("IO 操作异常 - IOException");
                    } catch (InterruptedIOException unused) {
                        this.u.b(7101);
                        this.u.a(7);
                        throw new Cif(AMapException.ERROR_UNKNOWN);
                    } catch (ConnectException e6) {
                        e = e6;
                        e.printStackTrace();
                        this.u.b(a(e));
                        this.u.a(6);
                        throw new Cif(AMapException.ERROR_CONNECTION);
                    } catch (MalformedURLException e7) {
                        e = e7;
                        e.printStackTrace();
                        this.u.a(8);
                        throw new Cif("url异常 - MalformedURLException");
                    } catch (SocketException e8) {
                        e = e8;
                        e.printStackTrace();
                        this.u.b(a(e));
                        this.u.a(6);
                        throw new Cif(AMapException.ERROR_SOCKET);
                    } catch (SSLException e9) {
                        e = e9;
                        e.printStackTrace();
                        this.u.b(a(e));
                        this.u.a(4);
                        throw new Cif("IO 操作异常 - IOException");
                    } catch (IOException e10) {
                        e = e10;
                        e.printStackTrace();
                        this.u.a(7);
                        throw new Cif("IO 操作异常 - IOException");
                    } catch (Throwable th5) {
                        th = th5;
                        jt.a(th, "ht", "mPt");
                        this.u.a(9);
                        throw new Cif(AMapException.ERROR_UNKNOWN);
                    }
                } else {
                    this.u.d();
                    return lcVarB;
                }
            } catch (Throwable th6) {
                if (0 != 0) {
                    try {
                        dataOutputStream.disconnect();
                    } catch (Throwable th7) {
                        jt.a(th7, "ht", "mPt");
                    }
                }
                this.u.d();
                throw th6;
            }
        } catch (Cif e11) {
            e = e11;
        } catch (MalformedURLException e12) {
            e = e12;
        } catch (SocketTimeoutException e13) {
            e = e13;
        } catch (InterruptedIOException unused2) {
        } catch (ConnectException e14) {
            e = e14;
        } catch (SocketException e15) {
            e = e15;
        } catch (UnknownHostException e16) {
            e = e16;
        } catch (SSLException e17) {
            e = e17;
        } catch (ConnectTimeoutException e18) {
            e = e18;
        } catch (IOException e19) {
            e = e19;
        } catch (Throwable th8) {
            th = th8;
        }
        if (!e.i() && e.g() != 10) {
            this.u.a(e.g());
        }
        jt.a(e, "ht", "mPt");
        throw e;
    }

    /* JADX INFO: compiled from: HttpUrlUtil.java */
    public static class b {
        public HttpURLConnection a;
        public int b = this.b;
        public int b = this.b;

        public b(HttpURLConnection httpURLConnection) {
            this.a = httpURLConnection;
        }
    }

    private kz c() {
        try {
            SoftReference<kz> softReference = t;
            if (softReference == null || softReference.get() == null) {
                t = new SoftReference<>(new kz(ih.c, this.b));
            }
            kz kzVar = k != null ? t.get() : null;
            return kzVar == null ? new kz(ih.c, this.b) : kzVar;
        } catch (Throwable th) {
            jw.c(th, "ht", "gsf");
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:125:0x0271  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x02a1  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x02b0  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x0206 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:143:0x00d3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0062  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00d0  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x014c A[Catch: all -> 0x01bb, TryCatch #2 {all -> 0x01bb, blocks: (B:45:0x00d3, B:48:0x00eb, B:50:0x00ee, B:52:0x00f2, B:54:0x00f8, B:58:0x0101, B:61:0x010d, B:63:0x0110, B:65:0x0116, B:75:0x0146, B:77:0x014c, B:79:0x0156, B:81:0x0169, B:83:0x0191, B:85:0x01b2, B:86:0x01b5, B:66:0x012e, B:68:0x0132, B:70:0x0135, B:72:0x013b, B:73:0x0142), top: B:143:0x00d3 }] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01bf  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01ed  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x01f2  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01f5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.amap.api.col.3sl.ky.b a(com.amap.api.col.p0003sl.lb r17, boolean r18, boolean r19) throws com.amap.api.col.p0003sl.Cif, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 702
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ky.a(com.amap.api.col.3sl.lb, boolean, boolean):com.amap.api.col.3sl.ky$b");
    }

    private static String a(HttpURLConnection httpURLConnection) {
        List<String> list;
        if (httpURLConnection == null) {
            return "";
        }
        try {
            Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
            if (headerFields != null && (list = headerFields.get("gsid")) != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Throwable unused) {
        }
        return "";
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:104:0x0178 A[Catch: all -> 0x01cc, IOException -> 0x01d2, SocketTimeoutException -> 0x0200, ConnectTimeoutException -> 0x0205, TRY_ENTER, TryCatch #20 {SocketTimeoutException -> 0x0200, ConnectTimeoutException -> 0x0205, IOException -> 0x01d2, all -> 0x01cc, blocks: (B:3:0x0009, B:5:0x001d, B:7:0x0027, B:9:0x002d, B:10:0x0034, B:43:0x00a6, B:104:0x0178, B:105:0x01cb), top: B:166:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:157:0x0225 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:161:0x0230 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:167:0x020f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:171:0x021a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:183:? A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00a1 A[PHI: r4
      0x00a1: PHI (r4v10 java.lang.String) = (r4v0 java.lang.String), (r4v26 java.lang.String), (r4v26 java.lang.String) binds: [B:4:0x001b, B:152:0x00a1, B:12:0x0044] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00a6 A[Catch: all -> 0x01cc, IOException -> 0x01d2, SocketTimeoutException -> 0x0200, ConnectTimeoutException -> 0x0205, TRY_ENTER, TRY_LEAVE, TryCatch #20 {SocketTimeoutException -> 0x0200, ConnectTimeoutException -> 0x0205, IOException -> 0x01d2, all -> 0x01cc, blocks: (B:3:0x0009, B:5:0x001d, B:7:0x0027, B:9:0x002d, B:10:0x0034, B:43:0x00a6, B:104:0x0178, B:105:0x01cb), top: B:166:0x0009 }] */
    /* JADX WARN: Type inference failed for: r6v0 */
    /* JADX WARN: Type inference failed for: r6v20 */
    /* JADX WARN: Type inference failed for: r6v21 */
    /* JADX WARN: Type inference failed for: r6v3, types: [java.io.InputStream] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.amap.api.col.p0003sl.lc a(com.amap.api.col.3sl.ky.b r17, boolean r18) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 570
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ky.a(com.amap.api.col.3sl.ky$b, boolean):com.amap.api.col.3sl.lc");
    }

    private static String b(Map<String, List<String>> map) {
        try {
            List<String> list = map.get("sc");
            if (list == null || list.size() <= 0) {
                return "";
            }
            String str = list.get(0);
            if (TextUtils.isEmpty(str)) {
                return "";
            }
            if (str.contains("#")) {
                String[] strArrSplit = str.split("#");
                if (strArrSplit.length <= 1) {
                    return "";
                }
                str = strArrSplit[0];
            }
            return str;
        } catch (Throwable unused) {
            return "";
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x003c  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x003f A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0040  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean a(java.util.Map<java.lang.String, java.util.List<java.lang.String>> r7, boolean r8) {
        /*
            r6 = this;
            java.lang.String r0 = "#"
            java.lang.String r1 = "lct"
            r2 = 1
            r3 = 0
            java.lang.String r4 = "sc"
            java.lang.Object r4 = r7.get(r4)     // Catch: java.lang.Throwable -> L62
            java.util.List r4 = (java.util.List) r4     // Catch: java.lang.Throwable -> L62
            if (r4 == 0) goto L3c
            int r5 = r4.size()     // Catch: java.lang.Throwable -> L62
            if (r5 <= 0) goto L3c
            java.lang.Object r4 = r4.get(r3)     // Catch: java.lang.Throwable -> L62
            java.lang.String r4 = (java.lang.String) r4     // Catch: java.lang.Throwable -> L62
            boolean r5 = android.text.TextUtils.isEmpty(r4)     // Catch: java.lang.Throwable -> L62
            if (r5 != 0) goto L3c
            boolean r5 = r4.contains(r0)     // Catch: java.lang.Throwable -> L62
            if (r5 != 0) goto L2a
        L28:
            r0 = r2
            goto L3d
        L2a:
            java.lang.String[] r0 = r4.split(r0)     // Catch: java.lang.Throwable -> L62
            int r4 = r0.length     // Catch: java.lang.Throwable -> L62
            if (r4 <= r2) goto L3c
            java.lang.String r4 = "1"
            r0 = r0[r2]     // Catch: java.lang.Throwable -> L62
            boolean r0 = r4.equals(r0)     // Catch: java.lang.Throwable -> L62
            if (r0 == 0) goto L3c
            goto L28
        L3c:
            r0 = r3
        L3d:
            if (r0 != 0) goto L40
            return r3
        L40:
            if (r8 == 0) goto L63
            boolean r8 = r7.containsKey(r1)     // Catch: java.lang.Throwable -> L62
            if (r8 == 0) goto L62
            java.lang.Object r7 = r7.get(r1)     // Catch: java.lang.Throwable -> L62
            java.util.List r7 = (java.util.List) r7     // Catch: java.lang.Throwable -> L62
            if (r7 == 0) goto L62
            int r8 = r7.size()     // Catch: java.lang.Throwable -> L62
            if (r8 <= 0) goto L62
            long r7 = com.amap.api.col.p0003sl.ih.a(r7)     // Catch: java.lang.Throwable -> L62
            java.lang.String r0 = r6.j     // Catch: java.lang.Throwable -> L62
            boolean r7 = com.amap.api.col.p0003sl.ih.a(r0, r7)     // Catch: java.lang.Throwable -> L62
            r2 = r7
            goto L63
        L62:
            r2 = r3
        L63:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.ky.a(java.util.Map, boolean):boolean");
    }

    private void a(Map<String, String> map, HttpURLConnection httpURLConnection, boolean z) {
        c cVarG;
        if (map != null) {
            try {
                for (String str : map.keySet()) {
                    httpURLConnection.addRequestProperty(str, map.get(str));
                }
            } catch (Throwable th) {
                jt.a(th, "ht", "adh");
                return;
            }
        }
        if (ku.d != null) {
            for (String str2 : ku.d.keySet()) {
                httpURLConnection.addRequestProperty(str2, ku.d.get(str2));
            }
        }
        String strB = "";
        if (z && !this.m.contains("/v3/iasdkauth") && !TextUtils.isEmpty(this.j) && ih.d(this.j)) {
            this.o = true;
            ih.g gVarF = ih.f(this.j);
            httpURLConnection.addRequestProperty("lct", String.valueOf(gVarF.a));
            httpURLConnection.addRequestProperty("lct-info", gVarF.b);
            httpURLConnection.addRequestProperty("aks", ih.c(ih.a(this.j)));
            httpURLConnection.addRequestProperty("lct-args", a(ih.b(this.j) != null ? ih.b(this.j).b() : "", this.j));
        }
        httpURLConnection.addRequestProperty("csid", this.g);
        if (b(this.u.c.e)) {
            f fVar = this.u;
            if (!TextUtils.isEmpty(fVar.c.c)) {
                strB = il.b(kq.a(fVar.c.c.getBytes(), "YXBtX25ldHdvcmtf".getBytes()));
                new StringBuilder("上报本次请求serverIp:").append(fVar.c.c).append("加密后：").append(strB);
            }
            if (!TextUtils.isEmpty(strB)) {
                httpURLConnection.addRequestProperty("sip", strB);
            }
            if (ih.j && (cVarG = ih.g()) != null) {
                httpURLConnection.addRequestProperty("nls", cVarG.b());
                this.u.e = cVarG;
            }
            a aVarF = ih.f();
            if (aVarF != null) {
                httpURLConnection.addRequestProperty("nlf", aVarF.b());
                this.u.d = aVarF;
            }
        }
    }

    /* JADX INFO: compiled from: HttpUrlUtil.java */
    private static class e implements HostnameVerifier {
        private String a;
        private String b;

        private e() {
        }

        /* synthetic */ e(byte b) {
            this();
        }

        public final void a(String str) {
            String[] strArrSplit;
            if (!TextUtils.isEmpty(this.a) && str.contains(":") && (strArrSplit = str.split(":")) != null && strArrSplit.length > 0) {
                this.a = strArrSplit[0];
            } else {
                this.a = str;
            }
        }

        public final void b(String str) {
            this.b = str;
        }

        public final String a() {
            return this.b;
        }

        @Override // javax.net.ssl.HostnameVerifier
        public final boolean verify(String str, SSLSession sSLSession) {
            HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
            if (!TextUtils.isEmpty(this.a)) {
                return this.a.equals(str);
            }
            if (!TextUtils.isEmpty(this.b)) {
                return defaultHostnameVerifier.verify(this.b, sSLSession);
            }
            return defaultHostnameVerifier.verify(str, sSLSession);
        }
    }

    /* JADX INFO: compiled from: HttpUrlUtil.java */
    private static class d {
        private Vector<e> a;
        private volatile e b;

        private d() {
            this.a = new Vector<>();
            this.b = new e((byte) 0);
        }

        /* synthetic */ d(byte b) {
            this();
        }

        public final e a(String str) {
            if (TextUtils.isEmpty(str)) {
                return this.b;
            }
            byte b = 0;
            for (int i = 0; i < this.a.size(); i++) {
                e eVar = this.a.get(i);
                if (eVar != null && eVar.a().equals(str)) {
                    return eVar;
                }
            }
            e eVar2 = new e(b);
            eVar2.b(str);
            this.a.add(eVar2);
            return eVar2;
        }
    }

    static String a(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                value = "";
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(key));
            sb.append("=");
            sb.append(URLEncoder.encode(value));
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean b(String str) {
        if (this.l) {
            return true;
        }
        return (!TextUtils.isEmpty(this.n) && (this.n.contains("rest") || this.n.contains("apilocate"))) || c(str);
    }

    private static boolean c(String str) {
        return str.contains("rest") || str.contains("apilocate");
    }

    private static int a(Exception exc) {
        if (exc instanceof SSLHandshakeException) {
            return SpeechConstants.VPR_EVENT_RECORDING_START;
        }
        if (exc instanceof SSLKeyException) {
            return SpeechConstants.VPR_EVENT_RECORDING_STOP;
        }
        if (exc instanceof SSLProtocolException) {
            return SpeechConstants.VPR_EVENT_VOLUME_UPDATED;
        }
        if (exc instanceof SSLPeerUnverifiedException) {
            return 4104;
        }
        if (exc instanceof ConnectException) {
            return 6101;
        }
        if (exc instanceof SocketException) {
            return 6102;
        }
        if (exc instanceof ConnectTimeoutException) {
            return 2101;
        }
        if (exc instanceof SocketTimeoutException) {
            return SpeechConstants.TTS_EVENT_SYNTHESIZER_START;
        }
        return 0;
    }

    /* JADX INFO: compiled from: HttpUrlUtil.java */
    class f {
        long a = 0;
        long b = 0;
        c c = new c();
        a d;
        c e;
        String f;
        URL g;

        f() {
        }

        public final void a(lb lbVar, URL url) {
            this.g = url;
            this.c.d = url.getPath();
            this.c.e = url.getHost();
            if (!TextUtils.isEmpty(ky.this.n) && lbVar.getDegradeType().b()) {
                c cVar = this.c;
                cVar.c = cVar.e.replace("[", "").replace("]", "");
                this.c.e = ky.this.n;
            }
            if (lbVar.getDegradeType().b()) {
                lbVar.setNon_degrade_final_Host(this.c.e);
            }
            if (lbVar.getDegradeType().d()) {
                this.f = lbVar.getNon_degrade_final_Host();
            }
        }

        public final void a() {
            this.c.h = SystemClock.elapsedRealtime() - this.b;
        }

        public final void b() {
            this.c.i = SystemClock.elapsedRealtime() - this.b;
        }

        public final void c() {
            this.c.j = SystemClock.elapsedRealtime() - this.b;
        }

        public final void a(lc lcVar) {
            c cVarClone;
            try {
                this.c.f = SystemClock.elapsedRealtime() - this.a;
                if (lcVar != null) {
                    lcVar.f = this.c.b.c();
                }
                if (this.c.b.b() && this.c.f > 10000) {
                    ih.a(false, this.c.e);
                }
                if (this.c.b.d()) {
                    ih.a(false, this.f);
                }
                boolean zB = ky.this.b(this.c.e);
                if (zB) {
                    ih.c(this.c);
                    ih.a(true, this.d);
                    if (this.c.f > ih.e && (cVarClone = this.c.clone()) != null) {
                        cVarClone.m = 1;
                        ih.b(cVarClone);
                        new StringBuilder("!!!finish&error-").append(cVarClone.toString());
                        ky.b();
                    }
                }
                ih.a(this.g.toString(), this.c.b.c(), false, zB);
                new StringBuilder("!!!finish-").append(this.c.toString());
                ky.b();
            } catch (Throwable unused) {
            }
        }

        public final void a(int i) {
            "----errorcode-----".concat(String.valueOf(i));
            ky.b();
            try {
                this.c.f = SystemClock.elapsedRealtime() - this.a;
                this.c.m = i;
                if (this.c.b.e()) {
                    ih.a(false, this.c.e);
                }
                boolean zB = ky.this.b(this.c.e);
                if (zB) {
                    if (ky.this.p && !TextUtils.isEmpty(ky.this.n) && this.c.b.b()) {
                        ih.d();
                    }
                    if (this.c.b.c()) {
                        ih.a(this.c.b.c(), this.c.e);
                    }
                    ih.c(this.e);
                    ih.a(false, this.d);
                    ih.b(this.c);
                }
                ih.a(this.g.toString(), this.c.b.c(), true, zB);
                new StringBuilder("!!!error-").append(this.c.toString());
                ky.b();
            } catch (Throwable unused) {
            }
        }

        public final void b(int i) {
            this.c.n = i;
        }

        public final void a(long j) {
            this.c.l = new DecimalFormat("0.00").format(j / 1024.0f);
        }

        public final void d() {
            c cVarClone = this.c.clone();
            if (this.c.f > ih.e) {
                cVarClone.m = 1;
            }
            ih.a(cVarClone);
        }
    }

    /* JADX INFO: compiled from: HttpUrlUtil.java */
    public static class c implements Cloneable {
        public String a = "";
        public lb.b b = lb.b.FIRST_NONDEGRADE;
        public String c = "";
        public String d = "";
        public String e = "";
        public long f = 0;
        public long g = 0;
        public long h = 0;
        public long i = 0;
        public long j = 0;
        public String k = "-";
        public String l = "-";
        public int m = 0;
        public int n = 0;

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public final c clone() {
            try {
                return (c) super.clone();
            } catch (CloneNotSupportedException unused) {
                return null;
            }
        }

        protected final String b() {
            String str;
            String str2 = !TextUtils.isEmpty(this.c) ? this.c + "#" : "-#";
            if (!TextUtils.isEmpty(this.d)) {
                str = str2 + this.d + "#";
            } else {
                str = str2 + "-#";
            }
            String str3 = (((str + this.b.a() + "#") + this.h + "#") + this.j + "#") + this.f;
            String strB = il.b(kq.a(str3.getBytes(), "YXBtX25ldHdvcmtf".getBytes()));
            new StringBuilder("上报耗时数据").append(str3).append("加密后：").append(strB);
            ky.b();
            return strB;
        }

        public final String toString() {
            return "RequestInfo{csid='" + this.a + DateFormat.QUOTE + ", degradeType=" + this.b + ", serverIp='" + this.c + DateFormat.QUOTE + ", path='" + this.d + DateFormat.QUOTE + ", hostname='" + this.e + DateFormat.QUOTE + ", totalTime=" + this.f + ", DNSTime=" + this.g + ", connectionTime=" + this.h + ", writeTime=" + this.i + ", readTime=" + this.j + ", serverTime='" + this.k + DateFormat.QUOTE + ", datasize='" + this.l + DateFormat.QUOTE + ", errorcode=" + this.m + ", errorcodeSub=" + this.n + '}';
        }
    }

    /* JADX INFO: compiled from: HttpUrlUtil.java */
    public static class a implements Cloneable, Comparable {
        public int a;
        public String b;
        public String c;
        public String d;
        public String e;
        public int f;
        public int g;
        public int h;
        public long i;
        public volatile AtomicInteger j = new AtomicInteger(1);

        public a(c cVar) {
            this.b = cVar.c;
            this.c = cVar.e;
            this.e = cVar.d;
            this.f = cVar.m;
            this.g = cVar.n;
            this.h = cVar.b.a();
            this.d = cVar.a;
            this.i = cVar.f;
            if (this.f == 10) {
                this.a = 0;
            }
        }

        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public final a clone() {
            try {
                return (a) super.clone();
            } catch (CloneNotSupportedException unused) {
                return null;
            }
        }

        @Override // java.lang.Comparable
        public final int compareTo(Object obj) {
            return this.a - ((a) obj).a;
        }

        public final String b() {
            String str;
            String str2;
            String str3;
            String str4;
            try {
                String str5 = this.f + "#";
                if (!TextUtils.isEmpty(this.e)) {
                    str = str5 + this.e + "#";
                } else {
                    str = str5 + "-#";
                }
                String str6 = (str + this.h + "#") + this.j + "#";
                if (!TextUtils.isEmpty(this.b)) {
                    str2 = str6 + this.b + "#";
                } else {
                    str2 = str6 + "-#";
                }
                if (this.f == 1) {
                    str3 = str2 + this.d + "#";
                } else {
                    str3 = str2 + "-#";
                }
                if (this.f == 1) {
                    str4 = str3 + this.i + "#";
                } else {
                    str4 = str3 + "-#";
                }
                String str7 = (str4 + this.c + "#") + this.g;
                String strB = il.b(kq.a(str7.getBytes(), "YXBtX25ldHdvcmtf".getBytes()));
                new StringBuilder("上报异常数据").append(str7).append("加密后：").append(strB);
                ky.b();
                return strB;
            } catch (Exception unused) {
                return null;
            }
        }
    }

    private static String a(String str, String str2) {
        return String.format("platform=Android&sdkversion=%s&product=%s&manufacture=%s&abitype=%s", str, str2, Build.MANUFACTURER, ih.c != null ? it.a(ih.c) : "");
    }
}
