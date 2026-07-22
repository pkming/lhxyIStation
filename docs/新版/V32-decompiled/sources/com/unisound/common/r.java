package com.unisound.common;

import android.content.Context;
import android.util.Log;
import cn.yunzhisheng.asr.JniUscClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class r {
    public static final String A = "getResult";
    public static final String B = "error";
    public static final String C = "success";
    public static final String D = "partial";
    public static final String E = "last";
    private static aj F = null;
    private static final String G = "USCLOG";
    private static String H = "";
    private static String I = "";
    private static boolean K = true;
    private static int L = 0;
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 2;
    public static final int d = 3;
    public static final int e = 4;
    public static final int f = 5;
    public static final int g = 0;
    public static final int h = 1;
    public static final int i = 2;
    public static final int j = 3;
    public static boolean k = true;
    public static boolean l = false;
    public static boolean m = false;
    public static boolean n = true;
    public static boolean o = true;
    public static final String p = "USC";
    public static int q = 0;
    public static final String r = "init";
    public static final String s = "create";
    public static final String t = "initVPR";
    public static final String u = "initNLU";
    public static final String v = "collectedInfo";
    public static final String w = "start";
    public static final String x = "recognition";
    public static final String y = "stop";
    public static final String z = "cancel";
    private static StringBuffer J = new StringBuffer();
    private static Object M = new Object();

    private static String a(long j2) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(j2));
    }

    public static String a(JniUscClient jniUscClient) {
        String strC = jniUscClient.c(54);
        H = strC;
        return strC;
    }

    private static String a(String str, String str2, String str3, int i2, String str4, Context context, String str5, String str6, long j2) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(q.a, str);
            jSONObject.put(q.b, str2);
            jSONObject.put(q.c, str3);
            jSONObject.put(q.e, b(j2));
            jSONObject.put("packageName", context.getPackageName());
            jSONObject.put("status", i2);
            jSONObject.put(q.h, str4);
            jSONObject.put(q.i, str5);
            jSONObject.put(q.j, str6);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return jSONObject.toString();
    }

    public static void a() {
        StringBuffer stringBuffer = J;
        stringBuffer.delete(0, stringBuffer.length());
        H = "";
        I = "";
    }

    public static void a(Context context) {
        if (o) {
            new Thread(new s(context.getCacheDir().getAbsolutePath() + "/errorlog/")).start();
        }
    }

    public static void a(Context context, boolean z2, String str, String str2, int i2, String str3) {
        if (n) {
            try {
                String str4 = context.getCacheDir().getAbsolutePath() + "/javalog";
                String str5 = context.getCacheDir().getAbsolutePath() + "/clog";
                String str6 = context.getCacheDir().getAbsolutePath() + "/errorlog";
                String str7 = context.getCacheDir().getAbsolutePath() + "/errorlog/" + System.currentTimeMillis();
                String str8 = context.getCacheDir().getAbsolutePath() + "/javalogbk";
                String str9 = context.getCacheDir().getAbsolutePath() + "/clogbk";
                File file = new File(str4);
                File file2 = new File(str5);
                if (file.exists() && file.length() > 1000000) {
                    File file3 = new File(str8);
                    if (file3.exists()) {
                        file3.delete();
                    }
                    file.renameTo(file3);
                }
                if (file2.exists() && file2.length() > 1000000) {
                    File file4 = new File(str9);
                    if (file4.exists()) {
                        file4.delete();
                    }
                    file.renameTo(file4);
                }
                f(str4, new String(J));
                h(str5, H);
                if (z2) {
                    File file5 = new File(str7);
                    if (!file5.getParentFile().exists()) {
                        file5.getParentFile().mkdirs();
                    }
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    String str10 = str2.equals("") ? k.q + jCurrentTimeMillis : str2;
                    i(str6);
                    h(str7, a(str, k.x, str10, i2, str3, context, I, H, jCurrentTimeMillis));
                }
            } catch (IOException e2) {
                e(e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    public static void a(aj ajVar) {
        F = ajVar;
    }

    public static void a(String str) {
        if (k) {
            Log.v(p, str);
            h(str);
        }
    }

    public static void a(String str, String str2) {
        if (k) {
            Log.v(str, str2);
            h(str2);
        }
    }

    public static void a(String str, String str2, Throwable th) {
        if (k) {
            Log.e(str, str2, th);
        }
    }

    public static void a(String str, String str2, Map<String, String> map, String str3, String str4, String str5) {
        if (k) {
            String str6 = "Time=" + a(System.currentTimeMillis());
            if (str != null) {
                str6 = str6 + "&Action=" + str;
            }
            if (str2 != null) {
                str6 = str6 + "&Status=" + str2;
            }
            if (map != null) {
                String str7 = "{";
                for (String str8 : map.keySet()) {
                    str7 = str7 + str8 + ":" + map.get(str8) + "#";
                }
                str6 = str6 + "&Params=" + (str7 + "}");
            }
            if (str3 != null) {
                str6 = str6 + "&Result=" + str3;
            }
            if (str4 != null) {
                str6 = str6 + "&Errno=" + str4;
            }
            if (str5 != null) {
                str6 = str6 + "&Message=" + str5;
            }
            String str9 = str6 + "%";
            I = str9;
            J.append(str9);
        }
    }

    public static void a(String str, Throwable th) {
        a(p, str, th);
    }

    private static String b(long j2) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(j2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String b(File file) {
        BufferedReader bufferedReader;
        StringBuffer stringBuffer = new StringBuffer();
        if (!file.exists()) {
            return null;
        }
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (IOException e2) {
            e2.getStackTrace();
        }
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            stringBuffer.append(line);
            return new String(stringBuffer);
        }
        bufferedReader.close();
        return new String(stringBuffer);
    }

    public static void b(String str) {
        if (k) {
            Log.i(p, str);
            h(str);
        }
    }

    public static void b(String str, String str2) {
        if (k) {
            Log.i(str, str2);
            h(str2);
        }
    }

    public static void c(String str) {
        if (k) {
            Log.d(p, str);
            h(str);
        }
    }

    public static void c(String str, String str2) {
        if (k) {
            Log.d(str, str2);
            h(str2);
        }
    }

    static /* synthetic */ int d() {
        int i2 = L;
        L = i2 + 1;
        return i2;
    }

    public static void d(String str) {
        if (k) {
            Log.w(p, str);
            h(str);
        }
    }

    public static void d(String str, String str2) {
        if (k) {
            Log.w(str, str2);
            h(str2);
        }
    }

    public static void e(String str) {
        Log.e(p, str);
        h(str);
    }

    public static void e(String str, String str2) {
        if (k) {
            Log.e(str, str2);
            h(str2);
        }
    }

    public static void f(String str) {
        if (l) {
            Log.d("USC_TIME_TRACE", str + ": " + a(System.currentTimeMillis()));
            h(str);
        }
    }

    public static void f(String str, String str2) {
        FileWriter fileWriter = new FileWriter(str, true);
        new BufferedWriter(fileWriter);
        fileWriter.write(str2);
        fileWriter.flush();
        fileWriter.close();
    }

    public static void g(String str) {
        if (m) {
            Log.e(p, str);
            h(str);
        }
    }

    public static void g(String str, String str2) {
        FileWriter fileWriter = new FileWriter(str, true);
        new BufferedWriter(fileWriter);
        fileWriter.write(str2);
        fileWriter.flush();
        fileWriter.close();
    }

    private static void h(String str) {
        aj ajVar = F;
        if (ajVar != null) {
            ajVar.a(5, 2, str);
        }
    }

    public static void h(String str, String str2) {
        FileWriter fileWriter = new FileWriter(str, true);
        new BufferedWriter(fileWriter);
        fileWriter.write(str2);
        fileWriter.flush();
        fileWriter.close();
    }

    private static void i(String str) {
        File[] fileArrListFiles = new File(str).listFiles();
        if (fileArrListFiles.length >= 10) {
            if (fileArrListFiles.length >= 10) {
                for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                    int i3 = 0;
                    while (i3 < fileArrListFiles.length - 1) {
                        int i4 = i3 + 1;
                        if (fileArrListFiles[i3].lastModified() > fileArrListFiles[i4].lastModified()) {
                            File file = fileArrListFiles[i3];
                            fileArrListFiles[i3] = fileArrListFiles[i4];
                            fileArrListFiles[i4] = file;
                        }
                        i3 = i4;
                    }
                }
            }
            if (fileArrListFiles[0].exists()) {
                fileArrListFiles[0].delete();
            }
            i(str);
        }
    }
}
