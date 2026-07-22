package com.amap.api.col.p0003sl;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/* JADX INFO: compiled from: ServiceUtils.java */
/* JADX INFO: loaded from: classes2.dex */
public class fb {
    private static AssetManager b = null;
    private static Resources c = null;
    private static Resources d = null;
    private static boolean e = true;
    private static Context f = null;
    private static String g = "amap_resource";
    private static String h = "1_0_0";
    private static String j = ".jar";
    private static String k = g + h + j;
    private static String i = ".png";
    private static String l = g + h + i;
    private static String m = "";
    private static String n = m + k;
    private static Resources.Theme o = null;
    private static Resources.Theme p = null;
    private static Field q = null;
    private static Field r = null;
    private static Activity s = null;
    public static int a = -1;

    public static boolean a(Context context) {
        try {
            f = context;
            File fileB = b(context);
            if (fileB != null) {
                m = fileB.getAbsolutePath() + "/";
            }
            n = m + k;
            if (!e) {
                return true;
            }
            if (!c(context)) {
                return false;
            }
            AssetManager assetManagerA = a(n);
            b = assetManagerA;
            c = a(context, assetManagerA);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return true;
    }

    private static File b(Context context) {
        File filesDir;
        try {
            if (context == null) {
                if (context != null) {
                    context.getFilesDir();
                }
                return null;
            }
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && Environment.getExternalStorageDirectory().canWrite()) {
                    filesDir = context.getExternalFilesDir("LBS");
                } else {
                    filesDir = context.getFilesDir();
                }
                if (filesDir == null && context != null) {
                    context.getFilesDir();
                }
                return filesDir;
            } catch (Exception e2) {
                e2.printStackTrace();
                if (0 != 0 || context == null) {
                    return null;
                }
                return context.getFilesDir();
            }
        } catch (Throwable th) {
            if (0 == 0 && context != null) {
                context.getFilesDir();
            }
            throw th;
        }
    }

    public static Resources a() {
        Resources resources = c;
        return resources == null ? f.getResources() : resources;
    }

    private static Resources a(Context context, AssetManager assetManager) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.setToDefaults();
        return new Resources(assetManager, displayMetrics, context.getResources().getConfiguration());
    }

    private static AssetManager a(String str) {
        AssetManager assetManager = null;
        try {
            Class<?> cls = Class.forName("android.content.res.AssetManager");
            AssetManager assetManager2 = (AssetManager) cls.getConstructor(null).newInstance(null);
            try {
                cls.getDeclaredMethod("addAssetPath", String.class).invoke(assetManager2, str);
                return assetManager2;
            } catch (Throwable th) {
                th = th;
                assetManager = assetManager2;
                jw.c(th, "ResourcesUtil", "getAssetManager(String apkPath)");
                return assetManager;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static boolean c(Context context) {
        d(context);
        InputStream inputStreamOpen = null;
        try {
            inputStreamOpen = context.getResources().getAssets().open(l);
            if (b(inputStreamOpen)) {
                return true;
            }
            e();
            OutputStream outputStreamA = a(inputStreamOpen);
            if (inputStreamOpen != null) {
                try {
                    inputStreamOpen.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                    jw.c(e2, "ResourcesUtil", "copyResourceJarToAppFilesDir(Context ctx)");
                }
            }
            outputStreamA.close();
            return true;
        } catch (Throwable th) {
            try {
                th.printStackTrace();
                jw.c(th, "ResourcesUtil", "copyResourceJarToAppFilesDir(Context ctx)");
                if (inputStreamOpen == null) {
                    return false;
                }
                try {
                    inputStreamOpen.close();
                    return false;
                } catch (IOException e3) {
                    e3.printStackTrace();
                    jw.c(e3, "ResourcesUtil", "copyResourceJarToAppFilesDir(Context ctx)");
                    return false;
                }
            } finally {
                if (inputStreamOpen != null) {
                    try {
                        inputStreamOpen.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                        jw.c(e4, "ResourcesUtil", "copyResourceJarToAppFilesDir(Context ctx)");
                    }
                }
            }
        }
    }

    private static OutputStream a(InputStream inputStream) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(m, k));
        byte[] bArr = new byte[1024];
        while (true) {
            int i2 = inputStream.read(bArr);
            if (i2 <= 0) {
                return fileOutputStream;
            }
            fileOutputStream.write(bArr, 0, i2);
        }
    }

    private static boolean b(InputStream inputStream) throws IOException {
        File file = new File(n);
        long length = file.length();
        int iAvailable = inputStream.available();
        if (!file.exists() || length != iAvailable) {
            return false;
        }
        inputStream.close();
        return true;
    }

    private static void e() {
        File[] fileArrListFiles = new File(m).listFiles(new a());
        if (fileArrListFiles == null || fileArrListFiles.length <= 0) {
            return;
        }
        for (File file : fileArrListFiles) {
            file.delete();
        }
    }

    private static void d(Context context) {
        m = context.getFilesDir().getAbsolutePath();
        n = m + "/" + k;
    }

    public static View a(Context context, int i2) {
        XmlResourceParser xml = a().getXml(i2);
        View viewInflate = null;
        if (!e) {
            return LayoutInflater.from(context).inflate(xml, (ViewGroup) null);
        }
        try {
            int i3 = a;
            if (i3 == -1) {
                i3 = 0;
            }
            viewInflate = LayoutInflater.from(new fa(context, i3, fb.class.getClassLoader())).inflate(xml, (ViewGroup) null);
        } finally {
            try {
            } finally {
            }
        }
        return viewInflate;
    }

    /* JADX INFO: compiled from: ServiceUtils.java */
    static class a implements FilenameFilter {
        a() {
        }

        @Override // java.io.FilenameFilter
        public final boolean accept(File file, String str) {
            return str.startsWith(fb.g) && !str.endsWith(new StringBuilder().append(fb.h).append(fb.j).toString());
        }
    }
}
