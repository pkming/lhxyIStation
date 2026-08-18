package com.amap.api.col.p0003sl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: compiled from: BaseTileRequest.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class df extends db {
    @Override // com.amap.api.col.p0003sl.db, com.amap.api.col.p0003sl.lb
    public Map<String, String> getParams() {
        return null;
    }

    public df() {
        setProxy(ir.a(ab.a));
        setConnectionTimeout(5000);
        setSoTimeout(50000);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public Map<String, String> getRequestHead() {
        Hashtable hashtable = new Hashtable(16);
        hashtable.put("User-Agent", w.c);
        hashtable.put("Accept-Encoding", "gzip");
        hashtable.put("platinfo", String.format(Locale.US, "platform=Android&sdkversion=%s&product=%s", "10.0.600", "3dmap"));
        hashtable.put("x-INFO", ij.a(ab.a));
        hashtable.put("key", ig.f(ab.a));
        hashtable.put("logversion", "2.1");
        return hashtable;
    }

    protected String appendTsScode(String str) {
        String strA = a(str);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str);
        String strA2 = ij.a();
        stringBuffer.append("&ts=".concat(String.valueOf(strA2)));
        stringBuffer.append("&scode=" + ij.a(ab.a, strA2, strA));
        return stringBuffer.toString();
    }

    private static String a(String str) {
        String[] strArrSplit = str.split("&");
        Arrays.sort(strArrSplit);
        StringBuffer stringBuffer = new StringBuffer();
        for (String str2 : strArrSplit) {
            stringBuffer.append(b(str2));
            stringBuffer.append("&");
        }
        String string = stringBuffer.toString();
        return string.length() > 1 ? (String) string.subSequence(0, string.length() - 1) : str;
    }

    private static String b(String str) {
        if (str == null) {
            return str;
        }
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            jw.c(e, "AbstractProtocalHandler", "strReEncoder");
            return "";
        } catch (Exception e2) {
            jw.c(e2, "AbstractProtocalHandler", "strReEncoderException");
            return "";
        }
    }

    @Override // com.amap.api.col.p0003sl.lb
    public String getIPV6URL() {
        String url = getURL();
        return (url == null || !url.contains("http://restsdk.amap.com/v4/gridmap?")) ? url : dx.a(url);
    }

    @Override // com.amap.api.col.p0003sl.lb
    public boolean isSupportIPV6() {
        String url = getURL();
        return url != null && url.contains("http://restsdk.amap.com/v4/gridmap?");
    }
}
