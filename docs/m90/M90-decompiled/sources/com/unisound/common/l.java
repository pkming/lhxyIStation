package com.unisound.common;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes2.dex */
public class l {
    public static void a(String str) {
        int iLastIndexOf;
        if (str != null && (iLastIndexOf = str.lastIndexOf(47)) >= 0) {
            new File(str.substring(0, iLastIndexOf)).mkdirs();
        }
    }

    public static boolean a() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean a(InputStream inputStream, OutputStream outputStream) {
        boolean z = false;
        if (inputStream != null && outputStream != null) {
            byte[] bArr = new byte[1024];
            while (true) {
                try {
                    try {
                        try {
                            int i = inputStream.read(bArr);
                            if (i <= 0) {
                                break;
                            }
                            outputStream.write(bArr, 0, i);
                        } catch (Throwable th) {
                            try {
                                outputStream.close();
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            throw th;
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        outputStream.close();
                        inputStream.close();
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            outputStream.close();
            inputStream.close();
            z = true;
        }
        return z;
    }

    public static boolean a(String str, String str2, boolean z) {
        File file = new File(str);
        File file2 = new File(str2);
        if (!file.isFile() || !file2.isFile()) {
            return false;
        }
        try {
            file2.getParentFile().mkdirs();
            return a(new FileInputStream(file), new FileOutputStream(file2, false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static String b() {
        String path = Environment.getExternalStorageDirectory().getPath();
        return (path == null || path.endsWith(File.separator)) ? path : path + File.separator;
    }
}
