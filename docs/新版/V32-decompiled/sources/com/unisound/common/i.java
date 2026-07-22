package com.unisound.common;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import org.apache.poi.hssf.usermodel.HSSFFont;

/* JADX INFO: loaded from: classes2.dex */
public class i {
    public static byte[] a;

    /* JADX WARN: Removed duplicated region for block: B:42:0x0041 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x003c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:? A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean a(android.content.Context r5, java.lang.String r6, java.io.File r7) throws java.lang.Throwable {
        /*
            java.lang.String r0 = r7.getAbsolutePath()
            e(r0)
            r0 = 0
            r1 = 0
            android.content.res.AssetManager r5 = r5.getAssets()     // Catch: java.lang.Throwable -> L38 java.lang.Exception -> L45
            java.io.InputStream r5 = r5.open(r6)     // Catch: java.lang.Throwable -> L38 java.lang.Exception -> L45
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L35
            r6.<init>(r7)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L35
            r7 = 10240(0x2800, float:1.4349E-41)
            byte[] r2 = new byte[r7]     // Catch: java.lang.Throwable -> L2f java.lang.Exception -> L36
        L1a:
            int r3 = r5.read(r2, r0, r7)     // Catch: java.lang.Throwable -> L2f java.lang.Exception -> L36
            r4 = -1
            if (r3 == r4) goto L25
            r6.write(r2, r0, r3)     // Catch: java.lang.Throwable -> L2f java.lang.Exception -> L36
            goto L1a
        L25:
            r5.close()     // Catch: java.lang.Throwable -> L2f java.lang.Exception -> L36
            r6.close()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L46
            r5 = 1
            return r5
        L2d:
            r7 = move-exception
            goto L3a
        L2f:
            r7 = move-exception
            goto L33
        L31:
            r7 = move-exception
            r6 = r1
        L33:
            r1 = r5
            goto L3a
        L35:
            r6 = r1
        L36:
            r1 = r5
            goto L46
        L38:
            r7 = move-exception
            r6 = r1
        L3a:
            if (r1 == 0) goto L3f
            r1.close()     // Catch: java.io.IOException -> L3f
        L3f:
            if (r6 == 0) goto L44
            r6.close()     // Catch: java.io.IOException -> L44
        L44:
            throw r7
        L45:
            r6 = r1
        L46:
            if (r1 == 0) goto L4b
            r1.close()     // Catch: java.io.IOException -> L4b
        L4b:
            if (r6 == 0) goto L50
            r6.close()     // Catch: java.io.IOException -> L50
        L50:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.common.i.a(android.content.Context, java.lang.String, java.io.File):boolean");
    }

    public static boolean a(Context context, String str, String str2, String str3) {
        File file = new File(str2);
        if (file.exists() && t.a(str3, file)) {
            return true;
        }
        return a(context, str, file);
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x0052 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean a(java.lang.String r5) throws java.lang.Throwable {
        /*
            java.io.File r0 = new java.io.File
            r0.<init>(r5)
            boolean r5 = r0.exists()
            r1 = 0
            if (r5 != 0) goto Ld
            return r1
        Ld:
            r5 = 0
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch: java.lang.Throwable -> L3a java.io.IOException -> L3e
            java.lang.String r3 = "rw"
            r2.<init>(r0, r3)     // Catch: java.lang.Throwable -> L3a java.io.IOException -> L3e
            long r3 = r2.length()     // Catch: java.io.IOException -> L38 java.lang.Throwable -> L4f
            int r5 = (int) r3     // Catch: java.io.IOException -> L38 java.lang.Throwable -> L4f
            r3 = 0
            r2.seek(r3)     // Catch: java.io.IOException -> L38 java.lang.Throwable -> L4f
            r0 = 16000(0x3e80, float:2.2421E-41)
            r3 = 1
            byte[] r5 = com.unisound.common.as.a(r5, r3, r0)     // Catch: java.io.IOException -> L38 java.lang.Throwable -> L4f
            if (r5 == 0) goto L34
            r2.write(r5)     // Catch: java.io.IOException -> L38 java.lang.Throwable -> L4f
            r2.close()     // Catch: java.io.IOException -> L2f
            goto L33
        L2f:
            r5 = move-exception
            r5.printStackTrace()
        L33:
            return r3
        L34:
            r2.close()     // Catch: java.io.IOException -> L4a
            goto L4e
        L38:
            r5 = move-exception
            goto L41
        L3a:
            r0 = move-exception
            r2 = r5
            r5 = r0
            goto L50
        L3e:
            r0 = move-exception
            r2 = r5
            r5 = r0
        L41:
            r5.printStackTrace()     // Catch: java.lang.Throwable -> L4f
            if (r2 == 0) goto L4e
            r2.close()     // Catch: java.io.IOException -> L4a
            goto L4e
        L4a:
            r5 = move-exception
            r5.printStackTrace()
        L4e:
            return r1
        L4f:
            r5 = move-exception
        L50:
            if (r2 == 0) goto L5a
            r2.close()     // Catch: java.io.IOException -> L56
            goto L5a
        L56:
            r0 = move-exception
            r0.printStackTrace()
        L5a:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.common.i.a(java.lang.String):boolean");
    }

    public static boolean a(boolean z, String str) {
        RandomAccessFile randomAccessFile;
        e(str);
        byte[] bArrA = a(z ? HSSFFont.COLOR_NORMAL : (short) -32767);
        RandomAccessFile randomAccessFile2 = null;
        try {
            try {
                randomAccessFile = new RandomAccessFile(str, "rw");
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(bArrA);
            try {
                randomAccessFile.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return true;
        } catch (Exception e3) {
            e = e3;
            randomAccessFile2 = randomAccessFile;
            e.printStackTrace();
            if (randomAccessFile2 == null) {
                return false;
            }
            try {
                randomAccessFile2.close();
                return false;
            } catch (IOException e4) {
                e4.printStackTrace();
                return false;
            }
        } catch (Throwable th2) {
            th = th2;
            randomAccessFile2 = randomAccessFile;
            if (randomAccessFile2 != null) {
                try {
                    randomAccessFile2.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static boolean a(byte[] bArr, String str) throws Throwable {
        RandomAccessFile randomAccessFile;
        e(str);
        RandomAccessFile randomAccessFile2 = null;
        try {
            try {
                randomAccessFile = new RandomAccessFile(str, "rw");
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(bArr);
            try {
                randomAccessFile.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return true;
        } catch (Exception e3) {
            e = e3;
            randomAccessFile2 = randomAccessFile;
            e.printStackTrace();
            if (randomAccessFile2 == null) {
                return false;
            }
            try {
                randomAccessFile2.close();
                return false;
            } catch (IOException e4) {
                e4.printStackTrace();
                return false;
            }
        } catch (Throwable th2) {
            th = th2;
            randomAccessFile2 = randomAccessFile;
            if (randomAccessFile2 != null) {
                try {
                    randomAccessFile2.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static byte[] a(Context context) {
        if (a == null) {
            a = Arrays.copyOfRange(a(context, "empty"), 0, 6400);
        }
        return a;
    }

    private static byte[] a(Context context, String str) throws Throwable {
        byte[] bArr;
        InputStream inputStreamOpen;
        int i = 0;
        InputStream inputStream = null;
        byte[] bArr2 = null;
        inputStream = null;
        try {
            try {
                inputStreamOpen = context.getAssets().open(str);
            } catch (Exception e) {
                e = e;
                bArr = null;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            bArr2 = new byte[6400];
            i = inputStreamOpen.read(bArr2, 0, 6400);
        } catch (Exception e2) {
            e = e2;
            byte[] bArr3 = bArr2;
            inputStream = inputStreamOpen;
            bArr = bArr3;
            e.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused) {
                }
            }
            bArr2 = bArr;
        } catch (Throwable th2) {
            th = th2;
            inputStream = inputStreamOpen;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused2) {
                }
            }
            throw th;
        }
        return i > 0 ? bArr2 : new byte[6400];
    }

    public static byte[] a(short s) {
        byte[] bArr = new byte[2];
        for (int i = 0; i < 2; i++) {
            bArr[i] = (byte) ((s >>> 8) & 255);
        }
        return bArr;
    }

    public static boolean b(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return g(str);
    }

    public static boolean b(boolean z, String str) throws Throwable {
        RandomAccessFile randomAccessFile;
        e(str);
        byte[] bArrA = a(z ? (short) 28000 : (short) -28000);
        RandomAccessFile randomAccessFile2 = null;
        try {
            try {
                randomAccessFile = new RandomAccessFile(str, "rw");
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(bArrA);
            try {
                randomAccessFile.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return true;
        } catch (Exception e3) {
            e = e3;
            randomAccessFile2 = randomAccessFile;
            e.printStackTrace();
            if (randomAccessFile2 == null) {
                return false;
            }
            try {
                randomAccessFile2.close();
                return false;
            } catch (IOException e4) {
                e4.printStackTrace();
                return false;
            }
        } catch (Throwable th2) {
            th = th2;
            randomAccessFile2 = randomAccessFile;
            if (randomAccessFile2 != null) {
                try {
                    randomAccessFile2.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static boolean b(byte[] bArr, String str) throws Throwable {
        RandomAccessFile randomAccessFile;
        e(str);
        RandomAccessFile randomAccessFile2 = null;
        try {
            try {
                randomAccessFile = new RandomAccessFile(str, "rw");
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(bArr);
            try {
                randomAccessFile.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return true;
        } catch (Exception e3) {
            e = e3;
            randomAccessFile2 = randomAccessFile;
            e.printStackTrace();
            if (randomAccessFile2 == null) {
                return false;
            }
            try {
                randomAccessFile2.close();
                return false;
            } catch (IOException e4) {
                e4.printStackTrace();
                return false;
            }
        } catch (Throwable th2) {
            th = th2;
            randomAccessFile2 = randomAccessFile;
            if (randomAccessFile2 != null) {
                try {
                    randomAccessFile2.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static boolean c(String str) {
        return f(str) == j.WAV_EXTENSION_NAME;
    }

    public static boolean d(String str) {
        if (new File(str).exists()) {
            return g(str);
        }
        return false;
    }

    private static void e(String str) {
        int iLastIndexOf;
        if (str != null && (iLastIndexOf = str.lastIndexOf(47)) >= 0) {
            new File(str.substring(0, iLastIndexOf)).mkdirs();
        }
    }

    private static j f(String str) {
        int iLastIndexOf;
        if (TextUtils.isEmpty(str) || (iLastIndexOf = str.lastIndexOf(".")) <= 0) {
            return null;
        }
        String strSubstring = str.substring(iLastIndexOf + 1);
        return TextUtils.isEmpty(strSubstring) ? j.NO_EXTENSION_NAME : strSubstring.equalsIgnoreCase(com.unisound.sdk.b.b) ? j.PCM_EXTENSION_NAME : strSubstring.equalsIgnoreCase("wav") ? j.WAV_EXTENSION_NAME : j.OTHER_EXTENSION_NAME;
    }

    private static boolean g(String str) {
        j jVarF = f(str);
        if (jVarF == j.PCM_EXTENSION_NAME || jVarF == j.WAV_EXTENSION_NAME) {
            return true;
        }
        r.e("fileName illegal");
        return false;
    }
}
