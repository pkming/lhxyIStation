package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import com.amap.api.col.p0003sl.is;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.json.JSONObject;

/* JADX INFO: compiled from: Utils.java */
/* JADX INFO: loaded from: classes2.dex */
public final class it {
    static String a;
    private static final String[] b = {"arm64-v8a", "x86_64"};
    private static final String[] c = {"arm", "x86"};

    static {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 80; i++) {
            sb.append("=");
        }
        a = sb.toString();
    }

    public static String a(Context context) {
        String str;
        String[] strArr;
        if (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= 28) {
            str = "";
        } else {
            try {
                ApplicationInfo applicationInfo = context.getApplicationInfo();
                Field declaredField = Class.forName(ApplicationInfo.class.getName()).getDeclaredField("primaryCpuAbi");
                declaredField.setAccessible(true);
                str = (String) declaredField.get(applicationInfo);
            } catch (Throwable th) {
                jt.a(th, "ut", "gct");
                str = "";
            }
        }
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                String[] strArr2 = (String[]) Build.class.getDeclaredField("SUPPORTED_ABIS").get(null);
                if (strArr2 != null && strArr2.length > 0) {
                    str = strArr2[0];
                }
                if (!TextUtils.isEmpty(str) && Arrays.asList(b).contains(str)) {
                    String str2 = context.getApplicationInfo().nativeLibraryDir;
                    if (!TextUtils.isEmpty(str2)) {
                        if (Arrays.asList(c).contains(str2.substring(str2.lastIndexOf(File.separator) + 1)) && (strArr = (String[]) Build.class.getDeclaredField("SUPPORTED_32_BIT_ABIS").get(null)) != null && strArr.length > 0) {
                            str = strArr[0];
                        }
                    }
                }
            } catch (Throwable th2) {
                jt.a(th2, "ut", "gct_p");
            }
        }
        return TextUtils.isEmpty(str) ? Build.CPU_ABI : str;
    }

    public static boolean a(Context context, String str) {
        if (context == null || context.checkCallingOrSelfPermission(str) != 0) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
            try {
                if (((Integer) context.getClass().getMethod("checkSelfPermission", String.class).invoke(context, str)).intValue() != 0) {
                    return false;
                }
            } catch (Throwable th) {
                jt.a(th, "ut", "cpm");
            }
        }
        return true;
    }

    public static is a() throws Cif {
        return new is.a("collection", "1.0", "AMap_collection_1.0").a(new String[]{"com.amap.api.collection"}).a();
    }

    public static is b() throws Cif {
        return new is.a("co", "1.0.0", "AMap_co_1.0.0").a(new String[]{"com.amap.co", "com.amap.opensdk.co", "com.amap.location"}).a();
    }

    public static void a(ByteArrayOutputStream byteArrayOutputStream, String str) {
        if (TextUtils.isEmpty(str)) {
            try {
                byteArrayOutputStream.write(new byte[]{0});
                return;
            } catch (IOException e) {
                jt.a(e, "ut", "wsf");
                return;
            }
        }
        int length = str.length();
        if (length > 255) {
            length = 255;
        }
        a(byteArrayOutputStream, (byte) length, a(str));
    }

    public static String a(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return "";
        }
        try {
            return new String(bArr, "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            return new String(bArr);
        }
    }

    public static byte[] a(String str) {
        if (TextUtils.isEmpty(str)) {
            return new byte[0];
        }
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }

    public static void a(ByteArrayOutputStream byteArrayOutputStream, byte b2, byte[] bArr) {
        try {
            byteArrayOutputStream.write(new byte[]{b2});
            int i = b2 & 255;
            if (i < 255 && i > 0) {
                byteArrayOutputStream.write(bArr);
            } else if (i == 255) {
                byteArrayOutputStream.write(bArr, 0, 255);
            }
        } catch (IOException e) {
            jt.a(e, "ut", "wFie");
        }
    }

    public static String b(String str) {
        if (str == null) {
            return null;
        }
        String strC = il.c(a(str));
        try {
            return ((char) ((strC.length() % 26) + 65)) + strC;
        } catch (Throwable th) {
            jt.a(th, "ut", "tsfb64");
            return "";
        }
    }

    public static String c(String str) {
        return str.length() < 2 ? "" : il.a(str.substring(1));
    }

    public static boolean a(JSONObject jSONObject, String str) {
        return jSONObject != null && jSONObject.has(str);
    }

    public static byte[] c() {
        try {
            String[] strArrSplit = new StringBuffer("16,16,18,77,15,911,121,77,121,911,38,77,911,99,86,67,611,96,48,77,84,911,38,67,021,301,86,67,611,98,48,77,511,77,48,97,511,58,48,97,511,84,501,87,511,96,48,77,221,911,38,77,121,37,86,67,25,301,86,67,021,96,86,67,021,701,86,67,35,56,86,67,611,37,221,87").reverse().toString().split(",");
            byte[] bArr = new byte[strArrSplit.length];
            for (int i = 0; i < strArrSplit.length; i++) {
                bArr[i] = Byte.parseByte(strArrSplit[i]);
            }
            String[] strArrSplit2 = new StringBuffer(new String(il.b(new String(bArr)))).reverse().toString().split(",");
            byte[] bArr2 = new byte[strArrSplit2.length];
            for (int i2 = 0; i2 < strArrSplit2.length; i2++) {
                bArr2[i2] = Byte.parseByte(strArrSplit2[i2]);
            }
            return bArr2;
        } catch (Throwable th) {
            jt.a(th, "ut", "gIV");
            return new byte[16];
        }
    }

    public static String a(Throwable th) {
        StringWriter stringWriter;
        PrintWriter printWriter;
        try {
            stringWriter = new StringWriter();
            try {
                printWriter = new PrintWriter(stringWriter);
                try {
                    th.printStackTrace(printWriter);
                    for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause()) {
                        cause.printStackTrace(printWriter);
                    }
                    String string = stringWriter.toString();
                    try {
                        stringWriter.close();
                    } catch (Throwable th2) {
                        th2.printStackTrace();
                    }
                    try {
                        printWriter.close();
                    } catch (Throwable th3) {
                        th3.printStackTrace();
                    }
                    return string;
                } catch (Throwable th4) {
                    th = th4;
                    try {
                        th.printStackTrace();
                        if (stringWriter != null) {
                            try {
                                stringWriter.close();
                            } catch (Throwable th5) {
                                th5.printStackTrace();
                            }
                        }
                        if (printWriter != null) {
                            try {
                                printWriter.close();
                            } catch (Throwable th6) {
                                th6.printStackTrace();
                            }
                        }
                        return null;
                    } finally {
                    }
                }
            } catch (Throwable th7) {
                th = th7;
                printWriter = null;
            }
        } catch (Throwable th8) {
            th = th8;
            stringWriter = null;
            printWriter = null;
        }
    }

    public static String a(Map<String, String> map) {
        if (map.size() == 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        boolean z = true;
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (z) {
                    z = false;
                    stringBuffer.append(entry.getKey()).append("=").append(entry.getValue());
                } else {
                    stringBuffer.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
        } catch (Throwable th) {
            jt.a(th, "ut", "abP");
        }
        return stringBuffer.toString();
    }

    private static String f(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return "";
            }
            String[] strArrSplit = str.split("&");
            Arrays.sort(strArrSplit);
            StringBuffer stringBuffer = new StringBuffer();
            for (String str2 : strArrSplit) {
                if (!TextUtils.isEmpty(str2)) {
                    stringBuffer.append(str2);
                    stringBuffer.append("&");
                }
            }
            String string = stringBuffer.toString();
            if (string.length() > 1) {
                return (String) string.subSequence(0, string.length() - 1);
            }
        } catch (Throwable th) {
            jt.a(th, "ut", "sPa");
        }
        return str;
    }

    public static byte[] b(byte[] bArr) {
        try {
            return h(bArr);
        } catch (Throwable th) {
            jt.a(th, "ut", "gZp");
            return new byte[0];
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:17:0x003c -> B:58:0x005c). Please report as a decompilation issue!!! */
    public static byte[] c(byte[] bArr) {
        ByteArrayOutputStream byteArrayOutputStream;
        ZipOutputStream zipOutputStream;
        byte[] byteArray = null;
        if (bArr != null) {
            try {
            } catch (Throwable th) {
                jt.a(th, "ut", "zp2");
            }
            if (bArr.length != 0) {
                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    try {
                        zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
                        try {
                            zipOutputStream.putNextEntry(new ZipEntry("log"));
                            zipOutputStream.write(bArr);
                            zipOutputStream.closeEntry();
                            zipOutputStream.finish();
                            byteArray = byteArrayOutputStream.toByteArray();
                            try {
                                zipOutputStream.close();
                            } catch (Throwable th2) {
                                jt.a(th2, "ut", "zp1");
                            }
                            byteArrayOutputStream.close();
                        } catch (Throwable th3) {
                            th = th3;
                            try {
                                jt.a(th, "ut", "zp");
                                if (zipOutputStream != null) {
                                    try {
                                        zipOutputStream.close();
                                    } catch (Throwable th4) {
                                        jt.a(th4, "ut", "zp1");
                                    }
                                }
                                if (byteArrayOutputStream != null) {
                                    byteArrayOutputStream.close();
                                }
                                return byteArray;
                            } finally {
                            }
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        zipOutputStream = null;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    byteArrayOutputStream = null;
                    zipOutputStream = null;
                }
                return byteArray;
            }
        }
        return null;
    }

    public static byte[] a(int i) {
        return new byte[]{(byte) (i / 256), (byte) (i % 256)};
    }

    static PublicKey d() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, CertificateException, NullPointerException {
        ByteArrayInputStream byteArrayInputStream;
        try {
            byteArrayInputStream = new ByteArrayInputStream(il.b("MIICnjCCAgegAwIBAgIJAJ0Pdzos7ZfYMA0GCSqGSIb3DQEBBQUAMGgxCzAJBgNVBAYTAkNOMRMwEQYDVQQIDApTb21lLVN0YXRlMRAwDgYDVQQHDAdCZWlqaW5nMREwDwYDVQQKDAhBdXRvbmF2aTEfMB0GA1UEAwwWY29tLmF1dG9uYXZpLmFwaXNlcnZlcjAeFw0xMzA4MTUwNzU2NTVaFw0yMzA4MTMwNzU2NTVaMGgxCzAJBgNVBAYTAkNOMRMwEQYDVQQIDApTb21lLVN0YXRlMRAwDgYDVQQHDAdCZWlqaW5nMREwDwYDVQQKDAhBdXRvbmF2aTEfMB0GA1UEAwwWY29tLmF1dG9uYXZpLmFwaXNlcnZlcjCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA8eWAyHbFPoFPfdx5AD+D4nYFq4dbJ1p7SIKt19Oz1oivF/6H43v5Fo7s50pD1UF8+Qu4JoUQxlAgOt8OCyQ8DYdkaeB74XKb1wxkIYg/foUwN1CMHPZ9O9ehgna6K4EJXZxR7Y7XVZnbjHZIVn3VpPU/Rdr2v37LjTw+qrABJxMCAwEAAaNQME4wHQYDVR0OBBYEFOM/MLGP8xpVFuVd+3qZkw7uBvOTMB8GA1UdIwQYMBaAFOM/MLGP8xpVFuVd+3qZkw7uBvOTMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADgYEA4LY3g8aAD8JkxAOqUXDDyLuCCGOc2pTIhn0TwMNaVdH4hZlpTeC/wuRD5LJ0z3j+IQ0vLvuQA5uDjVyEOlBrvVIGwSem/1XGUo13DfzgAJ5k1161S5l+sFUo5TxpHOXr8Z5nqJMjieXmhnE/I99GFyHpQmw4cC6rhYUhdhtg+Zk="));
        } catch (Throwable unused) {
            byteArrayInputStream = null;
        }
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(c("IWC41MDk"));
            KeyFactory keyFactory = KeyFactory.getInstance(c("EUlNB"));
            Certificate certificateGenerateCertificate = certificateFactory.generateCertificate(byteArrayInputStream);
            if (certificateGenerateCertificate != null && keyFactory != null) {
                PublicKey publicKeyGeneratePublic = keyFactory.generatePublic(new X509EncodedKeySpec(certificateGenerateCertificate.getPublicKey().getEncoded()));
                try {
                    byteArrayInputStream.close();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                return publicKeyGeneratePublic;
            }
            try {
                byteArrayInputStream.close();
            } catch (Throwable th2) {
                th2.printStackTrace();
            }
            return null;
        } catch (Throwable unused2) {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (Throwable th3) {
                    th3.printStackTrace();
                }
            }
            return null;
        }
    }

    public static byte[] d(byte[] bArr) {
        try {
            return h(bArr);
        } catch (Throwable th) {
            th.printStackTrace();
            return new byte[0];
        }
    }

    static String e(byte[] bArr) {
        try {
            return g(bArr);
        } catch (Throwable th) {
            jt.a(th, "ut", "h2s");
            return null;
        }
    }

    static String f(byte[] bArr) {
        try {
            return g(bArr);
        } catch (Throwable th) {
            jt.a(th, "ut", "csb2h");
            return null;
        }
    }

    public static String g(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        if (bArr == null) {
            return null;
        }
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString(b2 & 255);
            if (hexString.length() == 1) {
                hexString = "0".concat(String.valueOf(hexString));
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static byte[] d(String str) {
        if (str.length() % 2 != 0) {
            str = "0".concat(String.valueOf(str));
        }
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) Integer.parseInt(str.substring(i2, i2 + 2), 16);
        }
        return bArr;
    }

    private static byte[] h(byte[] bArr) throws Throwable {
        ByteArrayOutputStream byteArrayOutputStream;
        GZIPOutputStream gZIPOutputStream = null;
        if (bArr == null) {
            return null;
        }
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                GZIPOutputStream gZIPOutputStream2 = new GZIPOutputStream(byteArrayOutputStream);
                try {
                    gZIPOutputStream2.write(bArr);
                    gZIPOutputStream2.finish();
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    gZIPOutputStream2.close();
                    byteArrayOutputStream.close();
                    return byteArray;
                } catch (Throwable th) {
                    th = th;
                    gZIPOutputStream = gZIPOutputStream2;
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        if (gZIPOutputStream != null) {
                            gZIPOutputStream.close();
                        }
                        if (byteArrayOutputStream != null) {
                            byteArrayOutputStream.close();
                        }
                        throw th2;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Throwable th4) {
            th = th4;
            byteArrayOutputStream = null;
        }
    }

    public static String a(long j) {
        return a(j, "yyyyMMdd HH:mm:ss:SSS");
    }

    public static String a(long j, String str) {
        try {
            return new SimpleDateFormat(str, Locale.CHINA).format(new Date(j));
        } catch (Throwable th) {
            jt.a(th, "ut", "ctt");
            return null;
        }
    }

    public static boolean e(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            return il.a(str).contains(Build.MODEL + Build.VERSION.SDK_INT);
        } catch (Throwable unused) {
            return false;
        }
    }

    public static void a(Context context, String str, String str2, JSONObject jSONObject) {
        String string;
        String str3;
        String string2;
        String strC;
        string = "";
        String strE = ig.e(context);
        String strB = io.b(strE);
        String strA = ig.a(context);
        try {
            if (jSONObject.has(DocumentsContract.EXTRA_INFO)) {
                string2 = jSONObject.getString(DocumentsContract.EXTRA_INFO);
                strC = "请在高德开放平台官网中搜索\"" + string2 + "\"相关内容进行解决";
            } else {
                string2 = "";
                strC = string2;
            }
            try {
            } catch (Throwable unused) {
                string = strC;
                str3 = string;
            }
        } catch (Throwable unused2) {
        }
        if ("INVALID_USER_SCODE".equals(string2)) {
            String string3 = jSONObject.has("sec_code") ? jSONObject.getString("sec_code") : "";
            string = jSONObject.has("sec_code_debug") ? jSONObject.getString("sec_code_debug") : "";
            if (strB.equals(string3) || strB.equals(string)) {
                str3 = c("C6K+35Zyo6auY5b635byA5pS+5bmz5Y+w5a6Y572R5Lit5pCc57Si") + "\"请求内容过长导致业务调用失败\"相关内容进行解决";
            }
            Log.i("authErrLog", a);
            Log.i("authErrLog", "                                   鉴权错误信息                                  ");
            Log.i("authErrLog", a);
            g("SHA1Package:".concat(String.valueOf(strE)));
            g("key:".concat(String.valueOf(strA)));
            g("csid:".concat(String.valueOf(str)));
            g("gsid:".concat(String.valueOf(str2)));
            g("json:" + jSONObject.toString());
            Log.i("authErrLog", "                                                                               ");
            Log.i("authErrLog", str3);
            Log.i("authErrLog", a);
        }
        if ("INVALID_USER_KEY".equals(string2)) {
            string = jSONObject.has("key") ? jSONObject.getString("key") : "";
            if (string.length() > 0 && !strA.equals(string)) {
                strC = c("C6K+35Zyo6auY5b635byA5pS+5bmz5Y+w5a6Y572R5LiK5Y+R6LW35oqA5pyv5ZKo6K+i5bel5Y2V4oCUPui0puWPt+S4jktleemXrumimO+8jOWSqOivoklOVkFMSURfVVNFUl9LRVnlpoLkvZXop6PlhrM=");
            }
        }
        str3 = strC;
        Log.i("authErrLog", a);
        Log.i("authErrLog", "                                   鉴权错误信息                                  ");
        Log.i("authErrLog", a);
        g("SHA1Package:".concat(String.valueOf(strE)));
        g("key:".concat(String.valueOf(strA)));
        g("csid:".concat(String.valueOf(str)));
        g("gsid:".concat(String.valueOf(str2)));
        g("json:" + jSONObject.toString());
        Log.i("authErrLog", "                                                                               ");
        Log.i("authErrLog", str3);
        Log.i("authErrLog", a);
    }

    private static void g(String str) {
        int i;
        while (true) {
            if (str.length() < 78) {
                break;
            }
            Log.i("authErrLog", "|" + str.substring(0, 78) + "|");
            str = str.substring(78);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("|").append(str);
        for (i = 0; i < 78 - str.length(); i++) {
            sb.append(" ");
        }
        sb.append("|");
        Log.i("authErrLog", sb.toString());
    }

    public static boolean b(Context context) {
        return jq.a(context);
    }

    public static Calendar a(String str, String str2) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str2, Locale.CHINA);
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(simpleDateFormat.parse(str));
            calendar.set(calendar.get(1), calendar.get(2), calendar.get(5), calendar2.get(11), calendar2.get(12), calendar2.get(13));
            return calendar;
        } catch (ParseException e) {
            jt.a(e, "ut", "ctt");
            return null;
        }
    }

    public static String b(Map<String, String> map) {
        String string;
        if (map != null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
            }
            string = sb.toString();
        } else {
            string = null;
        }
        return f(string);
    }
}
