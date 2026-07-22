package com.amap.api.col.p0003sl;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/* JADX INFO: compiled from: FileStorageModel.java */
/* JADX INFO: loaded from: classes2.dex */
public final class jl {
    public static final String a = it.c("SYmFja3Vwcw");
    public static final String b = it.c("SLmFkaXU");
    public static final String c = it.c("JIw");

    public static synchronized void a(Context context, String str, String str2) {
        FileChannel channel;
        RandomAccessFile randomAccessFile;
        if (Build.VERSION.SDK_INT < 19 || (context != null && context.checkCallingOrSelfPermission(it.c("EYW5kcm9pZC5wZXJtaXNzaW9uLlJFQURfRVhURVJOQUxfU1RPUkFHRQ==")) == 0)) {
            String strA = a(context);
            if (TextUtils.isEmpty(strA)) {
                return;
            }
            String str3 = str + c + str2;
            File file = new File(strA + File.separator + a);
            File file2 = new File(file, b);
            FileLock fileLockTryLock = null;
            try {
                if (!file.exists() || file.isDirectory()) {
                    file.mkdirs();
                }
                file2.createNewFile();
                randomAccessFile = new RandomAccessFile(file2, "rws");
                try {
                    channel = randomAccessFile.getChannel();
                } catch (Throwable unused) {
                    channel = null;
                }
            } catch (Throwable unused2) {
                channel = null;
                randomAccessFile = null;
            }
            try {
                fileLockTryLock = channel.tryLock();
                if (fileLockTryLock != null) {
                    channel.write(ByteBuffer.wrap(str3.getBytes("UTF-8")));
                }
                if (fileLockTryLock != null) {
                    try {
                        fileLockTryLock.release();
                    } catch (IOException unused3) {
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException unused4) {
                    }
                }
                a(randomAccessFile);
            } catch (Throwable unused5) {
                if (fileLockTryLock != null) {
                    try {
                        fileLockTryLock.release();
                    } catch (IOException unused6) {
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException unused7) {
                    }
                }
                a(randomAccessFile);
            }
        }
    }

    private static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable unused) {
            }
        }
    }

    private static String a(Context context) {
        try {
            File externalCacheDir = Build.VERSION.SDK_INT >= 8 ? context.getExternalCacheDir() : null;
            if (externalCacheDir == null) {
                externalCacheDir = context.getCacheDir();
            }
            if (externalCacheDir != null) {
                return externalCacheDir.getAbsolutePath();
            }
        } catch (Exception unused) {
        }
        return null;
    }
}
