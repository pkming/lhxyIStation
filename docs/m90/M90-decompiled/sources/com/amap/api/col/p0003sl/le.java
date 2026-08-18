package com.amap.api.col.p0003sl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: compiled from: URIRestrictManager.java */
/* JADX INFO: loaded from: classes2.dex */
public class le {
    static final /* synthetic */ boolean a = true;
    private static le b;
    private final Map<String, Map<String, List<String>>> c = new HashMap();
    private final Map<String, List<String>> d = new HashMap();
    private final List<String> e = new ArrayList();

    public static synchronized le a() {
        if (b == null) {
            b = new le();
        }
        return b;
    }

    public final boolean a(String str) {
        a aVarA;
        if (str == null || str.length() == 0 || (aVarA = a(b(str))) == null) {
            return false;
        }
        return b(aVarA);
    }

    private static a b(String str) {
        try {
            return new a(new URL(str));
        } catch (MalformedURLException unused) {
            String str2 = null;
            if (str.contains("://")) {
                return null;
            }
            while (str.startsWith("/")) {
                str = str.substring(1);
            }
            String[] strArrSplit = str.split("/");
            int i = 0;
            if (strArrSplit[0].contains(".")) {
                str2 = strArrSplit[0];
                i = 1;
            }
            StringBuilder sb = new StringBuilder();
            while (i < strArrSplit.length) {
                if (sb.length() > 0) {
                    sb.append("/");
                }
                sb.append(strArrSplit[i]);
                i++;
            }
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '/') {
                sb.deleteCharAt(sb.length() - 1);
            }
            return new a(str2, sb.toString());
        }
    }

    private static a a(a aVar) {
        if (aVar == null) {
            return null;
        }
        if (aVar.c == null || aVar.c.length() == 0) {
            return aVar;
        }
        while (aVar.c.charAt(aVar.c.length() - 1) == '/') {
            aVar.c = aVar.c.substring(0, aVar.c.length() - 1);
        }
        while (aVar.c.charAt(0) == '/') {
            aVar.c = aVar.c.substring(1);
        }
        return aVar;
    }

    private synchronized boolean b(a aVar) {
        if (aVar != null) {
            if (aVar.c.length() != 0) {
                Map<String, List<String>> map = this.d;
                if (aVar.a != null && this.c.containsKey(aVar.a)) {
                    map = this.c.get(aVar.a);
                }
                List<String> list = this.e;
                boolean z = a;
                if (!z && map == null) {
                    throw new AssertionError();
                }
                if (aVar.b != null && map.containsKey(aVar.b)) {
                    list = map.get(aVar.b);
                }
                if (!z && list == null) {
                    throw new AssertionError();
                }
                Iterator<String> it = list.iterator();
                while (it.hasNext()) {
                    if (a(it.next(), aVar.c)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private static boolean a(String str, String str2) {
        String[] strArrSplit = str.split("/");
        String[] strArrSplit2 = str2.split("/");
        if (strArrSplit2.length < strArrSplit.length) {
            return false;
        }
        for (int i = 0; i < strArrSplit.length; i++) {
            if (!strArrSplit[i].equals("*") && !strArrSplit[i].equals(strArrSplit2[i])) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: compiled from: URIRestrictManager.java */
    private static class a {
        public String a;
        public String b;
        public String c;

        public a(URL url) {
            this.a = url.getProtocol();
            this.b = url.getHost();
            this.c = url.getPath();
        }

        public a(String str, String str2) {
            this.a = null;
            this.b = str;
            this.c = str2;
        }
    }
}
