package com.autonavi.base.amap.mapcore;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import com.amap.api.col.p0003sl.du;
import com.amap.api.col.p0003sl.dv;
import com.amap.api.col.p0003sl.dx;
import com.amap.api.col.p0003sl.dy;
import com.amap.api.col.p0003sl.dz;
import com.amap.api.col.p0003sl.is;
import com.amap.api.col.p0003sl.jw;
import com.amap.api.col.p0003sl.jy;
import com.amap.api.col.p0003sl.md;
import com.autonavi.amap.mapcore.MsgProcessor;
import com.autonavi.base.ae.gmap.GLMapEngine;
import com.autonavi.config.a;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class AeUtil {
    private static final String AMAP_ASSETS_DB_CK_PATH = "ae/res.ck";
    private static final String AMAP_GLOBAL_DB_NAME = "global.db";
    private static final String AMAP_GLOBAL_SP_ITEM_MD5 = "amap_res_global_db_md5";
    private static final String AMAP_GLOBAL_SP_NAME = "amap_res_global_db";
    private static final String AMAP_INTERSECTION_ASSETS_DIR = "VM3DRes";
    public static final String AMAP_RESZIP_TARGET_DIR_NAME = "res920";
    public static final String CONFIGNAME = "GNaviConfig.xml";
    public static final boolean IS_AE = true;
    public static final String RESZIPNAME = "res.zip";
    public static final String ROOTPATH = "/amap/";
    public static final String ROOT_DATA_PATH_NAME = "data_v6";
    public static final String ROOT_DATA_PATH_OLD_NAME = "data";
    private static String global_db_path;

    public static String getGlobalDbPath() {
        return global_db_path;
    }

    public static boolean loadLib(Context context) {
        try {
            if (!a.b) {
                System.loadLibrary(a.a);
                a.b = true;
            }
            return true;
        } catch (Throwable th) {
            jw.c(th, "AeUtil", "loadLib");
            dx.a(th);
            dz.b(dy.c, "load so failed " + th.getMessage());
            return false;
        }
    }

    public static void initCrashHandle(Context context) {
        is isVarA;
        try {
            if (!jy.a(dx.a()).a(context) || (isVarA = dx.a()) == null) {
                return;
            }
            MsgProcessor.nativeInitInfo(context, jy.a(isVarA).b(context), isVarA.a(), isVarA.b(), isVarA.c(), isVarA.f());
        } catch (Throwable th) {
            dx.a(th);
        }
    }

    public static GLMapEngine.InitParam initResource(final Context context) throws Throwable {
        final String mapBaseStorage = FileUtil.getMapBaseStorage(context);
        String str = mapBaseStorage + "/data_v6/";
        File file = new File(mapBaseStorage);
        if (!file.exists()) {
            file.mkdir();
        }
        File file2 = new File(str);
        if (!file2.exists()) {
            file2.mkdir();
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                dv.a().a(new md() { // from class: com.autonavi.base.amap.mapcore.AeUtil.1
                    @Override // com.amap.api.col.p0003sl.md
                    public final void runTask() {
                        AeUtil.loadEngineRes(mapBaseStorage, context);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            loadEngineRes(mapBaseStorage, context);
        }
        GLMapEngine.InitParam initParam = new GLMapEngine.InitParam();
        byte[] fileContentsFromAssets = FileUtil.readFileContentsFromAssets(context, "ae/GNaviConfig.xml");
        initParam.mRootPath = mapBaseStorage;
        if (fileContentsFromAssets != null) {
            try {
                initParam.mConfigContent = new String(fileContentsFromAssets, "utf-8");
                if (!initParam.mConfigContent.contains(ROOT_DATA_PATH_NAME)) {
                    throw new Exception("GNaviConfig.xml 和数据目录data_v6不匹配");
                }
            } catch (Throwable th) {
                th.printStackTrace();
                du.c(context, "initConfig error:" + th.getMessage());
            }
        }
        initParam.mOfflineDataPath = str + "/map/";
        initParam.mP3dCrossPath = str;
        initParam.mOfflineDataPath = formatFileSeparator(initParam.mOfflineDataPath);
        initParam.mRootPath = formatFileSeparator(initParam.mRootPath);
        initParam.mP3dCrossPath = formatFileSeparator(initParam.mP3dCrossPath);
        return initParam;
    }

    public static void initIntersectionRes(final Context context, final GLMapEngine.InitParam initParam) {
        String mapBaseStorage = FileUtil.getMapBaseStorage(context);
        String str = mapBaseStorage + "/VM3DRes/";
        File file = new File(mapBaseStorage);
        if (!file.exists()) {
            file.mkdir();
        }
        File file2 = new File(str);
        if (!file2.exists()) {
            file2.mkdir();
        }
        initParam.mIntersectionResPath = str;
        initParam.mIntersectionResPath = formatFileSeparator(initParam.mIntersectionResPath);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                dv.a().a(new md() { // from class: com.autonavi.base.amap.mapcore.AeUtil.2
                    @Override // com.amap.api.col.p0003sl.md
                    public final void runTask() {
                        AeUtil.loadAndSaveIntersectionRes("map_assets/VM3DRes", initParam.mIntersectionResPath, context);
                    }
                });
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        loadAndSaveIntersectionRes("map_assets/VM3DRes", initParam.mIntersectionResPath, context);
    }

    private static String formatFileSeparator(String str) {
        return str != null ? str.replace("//", "/") : str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0043 A[PHI: r10
      0x0043: PHI (r10v5 java.lang.String) = (r10v4 java.lang.String), (r10v8 java.lang.String) binds: [B:11:0x002a, B:15:0x0040] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:60:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:40:0x00be -> B:53:0x00e0). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void loadEngineRes(java.lang.String r10, android.content.Context r11) {
        /*
            Method dump skipped, instruction units count: 230
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.base.amap.mapcore.AeUtil.loadEngineRes(java.lang.String, android.content.Context):void");
    }

    private static String getAssetsGlobalDbMd5(Context context) {
        return new String(FileUtil.readFileContentsFromAssets(context, AMAP_ASSETS_DB_CK_PATH));
    }

    private static boolean checkEngineRes(File file) {
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles != null && fileArrListFiles.length > 0) {
            for (File file2 : fileArrListFiles) {
                if (file2 != null && file2.getName().contains(AMAP_GLOBAL_DB_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loadAndSaveIntersectionRes(String str, String str2, Context context) {
        File file = new File(str2);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            String[] list = context.getAssets().list(str);
            if (list == null) {
                return;
            }
            for (String str3 : list) {
                readAssetsFileAndSave(str + "/" + str3, new File(str2, str3).getAbsolutePath(), context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readAssetsFileAndSave(String str, String str2, Context context) {
        FileOutputStream fileOutputStream;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        InputStream inputStream = null;
        try {
            InputStream inputStreamOpen = context.getAssets().open(str);
            try {
                File file = new File(str2);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i = inputStreamOpen.read(bArr, 0, 1024);
                        if (i <= 0) {
                            break;
                        } else {
                            fileOutputStream2.write(bArr, 0, i);
                        }
                    }
                    if (inputStreamOpen != null) {
                        try {
                            inputStreamOpen.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        fileOutputStream2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                } catch (Throwable th) {
                    inputStream = inputStreamOpen;
                    fileOutputStream = fileOutputStream2;
                    th = th;
                    try {
                        th.printStackTrace();
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e4) {
                                e4.printStackTrace();
                            }
                        }
                    } finally {
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                inputStream = inputStreamOpen;
                fileOutputStream = null;
            }
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = null;
        }
    }
}
