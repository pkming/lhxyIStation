package com.unisound.sdk;

import android.content.Context;
import android.content.res.AssetManager;
import android.mtp.MtpConstants;
import cn.yunzhisheng.asrfix.JniAsrFix;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class y {
    public static final String f = "ml";
    public static final int i = 0;
    public static final int j = -100;
    public static final int k = -200;
    public static final int l = -300;
    public static int m = 20;
    private static f o = new f();
    public String a;
    public String[] b = {"tri", "l", "wid", "am", "digit", "wseg", "stat"};
    public String c = "am";
    public String d = "net";
    private String n = ".dat";
    public String e = "main";
    public boolean g = false;
    public boolean h = false;

    public y() {
        o.a(this);
    }

    private boolean a(AssetManager assetManager, String str) {
        try {
            File file = new File(str);
            boolean z = true;
            if (!file.exists()) {
                return true;
            }
            InputStream inputStreamOpen = assetManager.open("version/data");
            byte[] bArr = new byte[20];
            int i2 = inputStreamOpen.read(bArr, 0, 20);
            String strTrim = i2 > 0 ? new String(bArr, 0, i2).trim() : "";
            inputStreamOpen.close();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            if (line != null && line.equals(strTrim)) {
                z = false;
            }
            bufferedReader.close();
            return z;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean a(AssetManager assetManager, String str, String str2) throws Throwable {
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                fileOutputStream = new FileOutputStream(new File(str2));
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            byte[] bArr = new byte[10240];
            if (m == 1) {
                InputStream inputStreamOpen = assetManager.open(str + "/data");
                while (true) {
                    int i2 = inputStreamOpen.read(bArr, 0, 10240);
                    if (i2 <= 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, i2);
                }
                inputStreamOpen.close();
            } else {
                for (String str3 : assetManager.list(str)) {
                    InputStream inputStreamOpen2 = assetManager.open(str + "/" + str3);
                    while (true) {
                        int i3 = inputStreamOpen2.read(bArr, 0, 10240);
                        if (i3 <= 0) {
                            break;
                        }
                        fileOutputStream.write(bArr, 0, i3);
                    }
                    inputStreamOpen2.close();
                }
            }
            try {
                fileOutputStream.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return true;
        } catch (Exception e3) {
            e = e3;
            fileOutputStream2 = fileOutputStream;
            com.unisound.common.r.e("init asr model error");
            e.printStackTrace();
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            return false;
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static boolean a(AssetManager assetManager, String str, String str2, boolean z, boolean z2) {
        if (!z && new File(str2).exists()) {
            if (!z2 || JniAsrFix.a(str2)) {
                return true;
            }
            com.unisound.common.r.e("reset model file " + str2);
        }
        return a(assetManager, str, str2);
    }

    public static boolean a(String str, String str2, StringBuffer stringBuffer) {
        try {
            File file = new File(str + str2);
            if (!file.exists()) {
                return true;
            }
            stringBuffer.append("<").append(str2).append(">").append("\n");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    stringBuffer.append("</").append(str2).append(">").append("\n");
                    bufferedReader.close();
                    return true;
                }
                if (line.length() > 0) {
                    stringBuffer.append(line).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean a(String str, String str2, List<String> list) {
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(new File(str + str2));
            if (list != null) {
                try {
                    HashMap map = new HashMap();
                    Iterator<String> it = list.iterator();
                    while (it.hasNext()) {
                        String next = it.next();
                        if (next != null) {
                            if (-1 == next.indexOf(">")) {
                                next = next.replaceAll(">", "");
                            }
                            if (-1 != next.indexOf("<")) {
                                next = next.replaceAll("<", "");
                            }
                            String strTrim = next.trim();
                            if (strTrim.length() > 0) {
                                map.put(strTrim, null);
                            }
                        }
                    }
                    byte[] bytes = "\n".getBytes();
                    Iterator it2 = map.keySet().iterator();
                    while (it2.hasNext()) {
                        fileOutputStream2.write(((String) it2.next()).getBytes());
                        fileOutputStream2.write(bytes);
                    }
                } catch (Exception e) {
                    e = e;
                    fileOutputStream = fileOutputStream2;
                    e.printStackTrace();
                    if (fileOutputStream == null) {
                        return false;
                    }
                    try {
                        fileOutputStream.close();
                        return false;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return false;
                    }
                }
            }
            fileOutputStream2.close();
            return true;
        } catch (Exception e3) {
            e = e3;
        }
    }

    public static boolean d(String str) {
        return JniAsrFix.a(str);
    }

    private boolean i(String str) {
        File file = new File(str);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    private static void j(String str) {
        com.unisound.common.r.e(str);
    }

    public int a(String str, String str2) {
        return o.a(str, str2);
    }

    public int a(String str, String str2, String str3) {
        return a(str, str2, str3, a());
    }

    public int a(String str, String str2, String str3, String str4) {
        File file = new File(str4);
        if (file.exists()) {
            file.delete();
        } else {
            File file2 = new File(file.getParent());
            if (!file2.exists()) {
                file2.mkdirs();
            }
        }
        if ((o.a() ? o.a(str, str2, str3, str4) : o.a(str, str2, str3, this.a, str4)) == 0) {
            return 0;
        }
        return l;
    }

    public int a(Map<String, List<String>> map) {
        return l;
    }

    public String a() {
        return this.a + this.d + ".dat";
    }

    public String a(String str, List<String> list) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<" + str + ">").append("\n");
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            stringBuffer.append(it.next()).append("\n");
        }
        stringBuffer.append("</" + str + ">");
        com.unisound.common.r.c("ModelData : ", "getVocabString --> vocab = " + stringBuffer.toString());
        return stringBuffer.toString();
    }

    public void a(Context context) {
        new File(this.a + "version").delete();
    }

    public void a(String str) {
        this.a = str + "/";
    }

    public boolean a(Context context, String str) {
        try {
            InputStream inputStreamOpen = context.getAssets().open(str);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamOpen));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    inputStreamOpen.close();
                    return true;
                }
                String[] strArrSplit = line.split("=");
                if (strArrSplit.length == 2) {
                    String strTrim = strArrSplit[0].trim();
                    String strTrim2 = strArrSplit[1].trim();
                    if ("models".equals(strTrim)) {
                        this.b = strTrim2.split(",");
                    } else if ("am".equals(strTrim)) {
                        this.c = strTrim2;
                    } else if ("custom".equals(strTrim)) {
                        this.d = strTrim2;
                    } else if ("domain".equals(strTrim)) {
                        this.e = strTrim2;
                    }
                }
            }
        } catch (Exception e) {
            com.unisound.common.r.e("model list error");
            e.printStackTrace();
            return false;
        }
    }

    public boolean a(Context context, boolean z) {
        synchronized (this) {
            if (this.g) {
                return true;
            }
            AssetManager assets = context.getAssets();
            File file = new File(this.a);
            if (!file.exists()) {
                file.mkdirs();
            }
            boolean zA = a(assets, this.a + "version");
            if (zA) {
                com.unisound.common.r.a("init asr models..");
            }
            b();
            for (String str : this.b) {
                if (!a(assets, str, this.a + str + ".dat", zA, this.h)) {
                    return false;
                }
            }
            if (zA) {
                com.unisound.common.r.a("init asr models ok");
                a(assets, "version", this.a + "version");
            }
            if (!o.a() && z) {
                o.a(this.a);
            }
            this.g = true;
            return true;
        }
    }

    public int b(String str, String str2, String str3) {
        return o.a(str, str2, str3);
    }

    public String b(String str) {
        return this.a + str + "_partialFile";
    }

    public void b(Context context) throws Throwable {
        a(context.getAssets(), "version", this.a + "version");
    }

    public boolean b() {
        String str = this.a + this.d + this.n;
        if (JniAsrFix.a(str)) {
            return true;
        }
        i(str);
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v0, types: [android.content.Context] */
    /* JADX WARN: Type inference failed for: r6v1, types: [android.content.Context] */
    /* JADX WARN: Type inference failed for: r6v3 */
    public boolean b(Context context, String str) throws Throwable {
        if (!this.g) {
            d((Context) context);
        }
        try {
            File file = new File(this.a + this.c + this.n);
            FileInputStream fileInputStream = new FileInputStream(new File(str));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[10240];
            while (true) {
                int i2 = fileInputStream.read(bArr, 0, 10240);
                if (i2 <= 0) {
                    fileInputStream.close();
                    fileOutputStream.close();
                    context = 1;
                    return true;
                }
                fileOutputStream.write(bArr, 0, i2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.unisound.common.r.e("setAMFile error");
            a(context.getAssets(), this.c, this.a + this.c + this.n);
            return false;
        }
    }

    public StringBuffer c() {
        StringBuffer stringBuffer = new StringBuffer(MtpConstants.DEVICE_PROPERTY_UNDEFINED);
        try {
            File file = new File(this.a + this.d + this.n);
            if (!file.exists()) {
                return stringBuffer;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    inputStreamReader.close();
                    fileInputStream.close();
                    return stringBuffer;
                }
                stringBuffer.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return stringBuffer;
        }
    }

    protected boolean c(Context context) {
        this.g = false;
        synchronized (this) {
            AssetManager assets = context.getAssets();
            File file = new File(this.a);
            if (!file.exists()) {
                file.mkdirs();
            }
            boolean zA = a(assets, this.a + "version");
            if (zA) {
                com.unisound.common.r.a("init asr models..");
            }
            try {
                byte[] bArr = new byte[10240];
                for (String str : assets.list("models")) {
                    FileOutputStream fileOutputStream = new FileOutputStream(this.a + str);
                    InputStream inputStreamOpen = assets.open("models/" + str);
                    while (true) {
                        int i2 = inputStreamOpen.read(bArr, 0, 10240);
                        if (i2 <= 0) {
                            break;
                        }
                        fileOutputStream.write(bArr, 0, i2);
                    }
                    inputStreamOpen.close();
                    fileOutputStream.close();
                }
                if (zA) {
                    com.unisound.common.r.a("init asr models ok");
                    a(assets, "version", this.a + "version");
                }
                this.g = true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean c(Context context, String str) {
        try {
            for (String str2 : context.getAssets().list("")) {
                if (str2.equals(str)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean c(String str) {
        return false;
    }

    public boolean d() {
        boolean zA;
        synchronized (this) {
            zA = JniAsrFix.a(this.a + this.d + this.n);
        }
        return zA;
    }

    public boolean d(Context context) {
        return a(context, false);
    }

    public int e() {
        try {
            synchronized (this) {
                File file = new File(this.a + this.c + this.n);
                if (!file.exists()) {
                    return 0;
                }
                return (int) file.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.unisound.common.r.e("setAMFile error");
            return 0;
        }
    }

    public int e(String str) {
        int iCompileDecodeNet = JniAsrFix.compileDecodeNet(this.a, str);
        if (iCompileDecodeNet == 0) {
            return 0;
        }
        j("setUserData DecodeNet error:" + iCompileDecodeNet);
        return l;
    }

    public boolean e(Context context) {
        synchronized (this) {
            AssetManager assets = context.getAssets();
            File file = new File(this.a);
            if (!file.exists()) {
                file.mkdirs();
            }
            return a(assets, this.c, new StringBuilder().append(this.a).append(this.c).append(this.n).toString());
        }
    }

    public String f(String str) {
        String str2 = "#JSGF V1.0 utf-8 cn;\ngrammar " + str + ";\npublic <" + str + "> =( \"<s>\" (\n<NAME>\n) \"</s>\");";
        com.unisound.common.r.c("ModelData : ", "getJsgf --> jsgf = " + str2);
        return str2;
    }

    public boolean f() {
        return JniAsrFix.a(this.a + this.c);
    }

    public String g(String str) {
        return this.a + "jsgf_model/" + str + ".dat";
    }

    public void g() {
        o.b();
    }

    public int h(String str) {
        return o.b(str);
    }
}
