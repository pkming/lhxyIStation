package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/* JADX INFO: compiled from: AAIDKeyFactorUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ix {
    public static String a(Context context) {
        try {
            String strA = iu.a(context);
            try {
                if (!TextUtils.isEmpty(strA)) {
                    return strA;
                }
                strA = UUID.randomUUID().toString();
                iu.a(context, strA);
                return strA;
            } catch (Throwable unused) {
                return strA;
            }
        } catch (Throwable unused2) {
            return "";
        }
    }

    public static String a() {
        String str;
        str = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/sys/kernel/random/boot_id"));
            String line = bufferedReader.readLine();
            str = line != null ? line : "";
            bufferedReader.close();
        } catch (Throwable unused) {
        }
        return str;
    }

    public static String b() {
        String strValueOf = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("stat -c %z /data/app").getInputStream()));
            String line = bufferedReader.readLine();
            if (line != null) {
                String[] strArrSplit = line.split("\\.");
                Date dateA = a(strArrSplit[0]);
                String strTrim = strArrSplit[1] != null ? strArrSplit[1].split("\\+")[0].trim() : "";
                if (!TextUtils.isEmpty(strTrim)) {
                    strValueOf = (dateA.getTime() / 1000) + "." + c(strTrim);
                } else {
                    strValueOf = String.valueOf(dateA.getTime() / 1000);
                }
            }
            bufferedReader.close();
        } catch (Throwable unused) {
        }
        return strValueOf;
    }

    public static String c() {
        String strValueOf = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("stat -c %z /data/").getInputStream()));
            String line = bufferedReader.readLine();
            if (line != null) {
                String[] strArrSplit = line.split("\\.");
                Date dateA = a(strArrSplit[0]);
                String strTrim = strArrSplit[1] != null ? strArrSplit[1].split("\\+")[0].trim() : "";
                if (!TextUtils.isEmpty(strTrim)) {
                    strValueOf = (dateA.getTime() / 1000) + "." + c(strTrim);
                } else {
                    strValueOf = String.valueOf(dateA.getTime() / 1000);
                }
            }
            bufferedReader.close();
        } catch (Throwable unused) {
        }
        return strValueOf;
    }

    public static String d() {
        String strValueOf = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("stat -c %x /data/data").getInputStream()));
            String line = bufferedReader.readLine();
            if (line != null) {
                String[] strArrSplit = line.split("\\.");
                Date dateA = a(strArrSplit[0]);
                String strTrim = strArrSplit[1] != null ? strArrSplit[1].split("\\+")[0].trim() : "";
                if (!TextUtils.isEmpty(strTrim)) {
                    strValueOf = (dateA.getTime() / 1000) + "." + c(strTrim);
                } else {
                    strValueOf = String.valueOf(dateA.getTime() / 1000);
                }
            }
            bufferedReader.close();
        } catch (Throwable unused) {
        }
        return strValueOf;
    }

    public static String e() {
        String strValueOf = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("stat -c %x /data/app").getInputStream()));
            String line = bufferedReader.readLine();
            if (line != null) {
                String[] strArrSplit = line.split("\\.");
                Date dateA = a(strArrSplit[0]);
                String strTrim = strArrSplit[1] != null ? strArrSplit[1].split("\\+")[0].trim() : "";
                if (!TextUtils.isEmpty(strTrim)) {
                    strValueOf = (dateA.getTime() / 1000) + "." + c(strTrim);
                } else {
                    strValueOf = String.valueOf(dateA.getTime() / 1000);
                }
            }
            bufferedReader.close();
        } catch (Throwable unused) {
        }
        return strValueOf;
    }

    public static String f() {
        String str;
        str = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("stat -c %i /data/data").getInputStream()));
            String line = bufferedReader.readLine();
            str = line != null ? line : "";
            bufferedReader.close();
        } catch (Throwable unused) {
        }
        return str;
    }

    public static String g() {
        String str;
        str = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("stat -c %i /data/app").getInputStream()));
            String line = bufferedReader.readLine();
            str = line != null ? line : "";
            bufferedReader.close();
        } catch (Throwable unused) {
        }
        return str;
    }

    private static Date a(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        } catch (Throwable unused) {
            return null;
        }
    }

    public static String h() {
        try {
            Method declaredMethod = Build.class.getDeclaredMethod("getString", String.class);
            declaredMethod.setAccessible(true);
            String string = declaredMethod.invoke(null, "net.hostname").toString();
            return (string == null || string.equalsIgnoreCase("")) ? string : b(string);
        } catch (Exception unused) {
            return null;
        }
    }

    private static String b(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            byte[] bArrDigest = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bArrDigest) {
                stringBuffer.append(Integer.toHexString(b & 255));
            }
            return stringBuffer.toString();
        } catch (Throwable unused) {
            return "";
        }
    }

    private static int c(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return 0;
            }
            return Integer.parseInt(str);
        } catch (Throwable unused) {
            return 0;
        }
    }

    public static byte[] a(byte[] bArr, byte[] bArr2) {
        if (bArr != null && bArr.length != 0 && bArr2 != null && bArr2.length != 0) {
            for (int i = 0; i < bArr.length; i++) {
                bArr[i] = (byte) ((bArr[i] ^ bArr2[i % bArr2.length]) ^ (i & 255));
            }
        }
        return bArr;
    }
}
