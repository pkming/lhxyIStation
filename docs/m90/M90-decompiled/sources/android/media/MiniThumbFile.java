package android.media;

import android.app.backup.FullBackup;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Hashtable;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class MiniThumbFile {
    public static final int BYTES_PER_MINTHUMB = 10000;
    private static final int HEADER_SIZE = 13;
    private static final int MINI_THUMB_DATA_FILE_VERSION = 3;
    private static final String TAG = "MiniThumbFile";
    private static final Hashtable<String, MiniThumbFile> sThumbFiles = new Hashtable<>();
    private ByteBuffer mBuffer = ByteBuffer.allocateDirect(10000);
    private FileChannel mChannel;
    private RandomAccessFile mMiniThumbFile;
    private Uri mUri;

    public static synchronized void reset() {
        Iterator<MiniThumbFile> it = sThumbFiles.values().iterator();
        while (it.hasNext()) {
            it.next().deactivate();
        }
        sThumbFiles.clear();
    }

    public static synchronized MiniThumbFile instance(Uri uri) {
        MiniThumbFile miniThumbFile;
        String str = uri.getPathSegments().get(1);
        Hashtable<String, MiniThumbFile> hashtable = sThumbFiles;
        miniThumbFile = hashtable.get(str);
        if (miniThumbFile == null) {
            miniThumbFile = new MiniThumbFile(Uri.parse("content://media/external/" + str + "/media"));
            hashtable.put(str, miniThumbFile);
        }
        return miniThumbFile;
    }

    private String randomAccessFilePath(int i) {
        return (Environment.getExternalStorageDirectory().toString() + "/DCIM/.thumbnails") + "/.thumbdata" + i + "-" + this.mUri.hashCode();
    }

    private void removeOldFile() {
        File file = new File(randomAccessFilePath(2));
        if (file.exists()) {
            try {
                file.delete();
            } catch (SecurityException unused) {
            }
        }
    }

    private RandomAccessFile miniThumbDataFile() {
        if (this.mMiniThumbFile == null) {
            removeOldFile();
            String strRandomAccessFilePath = randomAccessFilePath(3);
            File parentFile = new File(strRandomAccessFilePath).getParentFile();
            if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
                Log.e(TAG, "Unable to create .thumbnails directory " + parentFile.toString());
            }
            File file = new File(strRandomAccessFilePath);
            try {
                try {
                    this.mMiniThumbFile = new RandomAccessFile(file, "rw");
                } catch (IOException unused) {
                }
            } catch (IOException unused2) {
                this.mMiniThumbFile = new RandomAccessFile(file, FullBackup.ROOT_TREE_TOKEN);
            }
            RandomAccessFile randomAccessFile = this.mMiniThumbFile;
            if (randomAccessFile != null) {
                this.mChannel = randomAccessFile.getChannel();
            }
        }
        return this.mMiniThumbFile;
    }

    public MiniThumbFile(Uri uri) {
        this.mUri = uri;
    }

    public synchronized void deactivate() {
        RandomAccessFile randomAccessFile = this.mMiniThumbFile;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
                this.mMiniThumbFile = null;
            } catch (IOException unused) {
            }
        }
    }

    public synchronized long getMagic(long j) {
        if (miniThumbDataFile() != null) {
            long j2 = 10000 * j;
            FileLock fileLockLock = null;
            try {
                try {
                    this.mBuffer.clear();
                    this.mBuffer.limit(9);
                    fileLockLock = this.mChannel.lock(j2, 9L, true);
                    if (this.mChannel.read(this.mBuffer, j2) == 9) {
                        this.mBuffer.position(0);
                        if (this.mBuffer.get() == 1) {
                            long j3 = this.mBuffer.getLong();
                            if (fileLockLock != null) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException unused) {
                                }
                            }
                            return j3;
                        }
                    }
                } catch (IOException e) {
                    Log.v(TAG, "Got exception checking file magic: ", e);
                    if (fileLockLock != null) {
                    }
                } catch (RuntimeException e2) {
                    Log.e(TAG, "Got exception when reading magic, id = " + j + ", disk full or mount read-only? " + e2.getClass());
                    if (fileLockLock != null) {
                    }
                }
                if (fileLockLock != null) {
                    try {
                        fileLockLock.release();
                    } catch (IOException unused2) {
                    }
                }
            } catch (Throwable th) {
                if (fileLockLock != null) {
                    try {
                        fileLockLock.release();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        }
        return 0L;
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x0073 A[EXC_TOP_SPLITTER, PHI: r8
      0x0073: PHI (r8v4 java.nio.channels.FileLock) = (r8v2 java.nio.channels.FileLock), (r8v5 java.nio.channels.FileLock) binds: [B:20:0x0071, B:29:0x009d] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized void saveMiniThumbToFile(byte[] r10, long r11, long r13) throws java.io.IOException {
        /*
            r9 = this;
            monitor-enter(r9)
            java.io.RandomAccessFile r0 = r9.miniThumbDataFile()     // Catch: java.lang.Throwable -> La2
            if (r0 != 0) goto L9
            monitor-exit(r9)
            return
        L9:
            r0 = 10000(0x2710, double:4.9407E-320)
            long r0 = r0 * r11
            r8 = 0
            if (r10 == 0) goto L9d
            int r2 = r10.length     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r3 = 9987(0x2703, float:1.3995E-41)
            if (r2 <= r3) goto L16
            monitor-exit(r9)
            return
        L16:
            java.nio.ByteBuffer r2 = r9.mBuffer     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r2.clear()     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            java.nio.ByteBuffer r2 = r9.mBuffer     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r3 = 1
            r2.put(r3)     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            java.nio.ByteBuffer r2 = r9.mBuffer     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r2.putLong(r13)     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            java.nio.ByteBuffer r13 = r9.mBuffer     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            int r14 = r10.length     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r13.putInt(r14)     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            java.nio.ByteBuffer r13 = r9.mBuffer     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r13.put(r10)     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            java.nio.ByteBuffer r10 = r9.mBuffer     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r10.flip()     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            java.nio.channels.FileChannel r2 = r9.mChannel     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r5 = 10000(0x2710, double:4.9407E-320)
            r7 = 0
            r3 = r0
            java.nio.channels.FileLock r8 = r2.lock(r3, r5, r7)     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            java.nio.channels.FileChannel r10 = r9.mChannel     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            java.nio.ByteBuffer r13 = r9.mBuffer     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            r10.write(r13, r0)     // Catch: java.lang.Throwable -> L48 java.lang.RuntimeException -> L4a java.io.IOException -> L77
            goto L9d
        L48:
            r10 = move-exception
            goto L97
        L4a:
            r10 = move-exception
            java.lang.String r13 = "MiniThumbFile"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L48
            r14.<init>()     // Catch: java.lang.Throwable -> L48
            java.lang.String r0 = "couldn't save mini thumbnail data for "
            java.lang.StringBuilder r14 = r14.append(r0)     // Catch: java.lang.Throwable -> L48
            java.lang.StringBuilder r11 = r14.append(r11)     // Catch: java.lang.Throwable -> L48
            java.lang.String r12 = "; disk full or mount read-only? "
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch: java.lang.Throwable -> L48
            java.lang.Class r10 = r10.getClass()     // Catch: java.lang.Throwable -> L48
            java.lang.StringBuilder r10 = r11.append(r10)     // Catch: java.lang.Throwable -> L48
            java.lang.String r10 = r10.toString()     // Catch: java.lang.Throwable -> L48
            android.util.Log.e(r13, r10)     // Catch: java.lang.Throwable -> L48
            if (r8 == 0) goto La0
        L73:
            r8.release()     // Catch: java.io.IOException -> La0 java.lang.Throwable -> La2
            goto La0
        L77:
            r10 = move-exception
            java.lang.String r13 = "MiniThumbFile"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L48
            r14.<init>()     // Catch: java.lang.Throwable -> L48
            java.lang.String r0 = "couldn't save mini thumbnail data for "
            java.lang.StringBuilder r14 = r14.append(r0)     // Catch: java.lang.Throwable -> L48
            java.lang.StringBuilder r11 = r14.append(r11)     // Catch: java.lang.Throwable -> L48
            java.lang.String r12 = "; "
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch: java.lang.Throwable -> L48
            java.lang.String r11 = r11.toString()     // Catch: java.lang.Throwable -> L48
            android.util.Log.e(r13, r11, r10)     // Catch: java.lang.Throwable -> L48
            throw r10     // Catch: java.lang.Throwable -> L48
        L97:
            if (r8 == 0) goto L9c
            r8.release()     // Catch: java.io.IOException -> L9c java.lang.Throwable -> La2
        L9c:
            throw r10     // Catch: java.lang.Throwable -> La2
        L9d:
            if (r8 == 0) goto La0
            goto L73
        La0:
            monitor-exit(r9)
            return
        La2:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MiniThumbFile.saveMiniThumbToFile(byte[], long, long):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00b5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0053 A[EXC_TOP_SPLITTER, PHI: r0
      0x0053: PHI (r0v5 java.nio.channels.FileLock) = (r0v3 java.nio.channels.FileLock), (r0v4 java.nio.channels.FileLock), (r0v7 java.nio.channels.FileLock) binds: [B:34:0x0087, B:38:0x00ac, B:21:0x0051] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized byte[] getMiniThumbFromFile(long r11, byte[] r13) {
        /*
            r10 = this;
            monitor-enter(r10)
            java.io.RandomAccessFile r0 = r10.miniThumbDataFile()     // Catch: java.lang.Throwable -> Lb9
            r1 = 0
            if (r0 != 0) goto La
            monitor-exit(r10)
            return r1
        La:
            r2 = 10000(0x2710, double:4.9407E-320)
            long r2 = r2 * r11
            java.nio.ByteBuffer r0 = r10.mBuffer     // Catch: java.lang.Throwable -> L5b java.lang.RuntimeException -> L5d java.io.IOException -> L88
            r0.clear()     // Catch: java.lang.Throwable -> L5b java.lang.RuntimeException -> L5d java.io.IOException -> L88
            java.nio.channels.FileChannel r4 = r10.mChannel     // Catch: java.lang.Throwable -> L5b java.lang.RuntimeException -> L5d java.io.IOException -> L88
            r7 = 10000(0x2710, double:4.9407E-320)
            r9 = 1
            r5 = r2
            java.nio.channels.FileLock r0 = r4.lock(r5, r7, r9)     // Catch: java.lang.Throwable -> L5b java.lang.RuntimeException -> L5d java.io.IOException -> L88
            java.nio.channels.FileChannel r4 = r10.mChannel     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            java.nio.ByteBuffer r5 = r10.mBuffer     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            int r2 = r4.read(r5, r2)     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            r3 = 13
            if (r2 <= r3) goto L51
            java.nio.ByteBuffer r3 = r10.mBuffer     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            r4 = 0
            r3.position(r4)     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            java.nio.ByteBuffer r3 = r10.mBuffer     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            r3.get()     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            java.nio.ByteBuffer r3 = r10.mBuffer     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            r3.getLong()     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            java.nio.ByteBuffer r3 = r10.mBuffer     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            int r3 = r3.getInt()     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            int r5 = r3 + 13
            if (r2 < r5) goto L51
            int r2 = r13.length     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            if (r2 < r3) goto L51
            java.nio.ByteBuffer r2 = r10.mBuffer     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            r2.get(r13, r4, r3)     // Catch: java.lang.RuntimeException -> L57 java.io.IOException -> L59 java.lang.Throwable -> Lb1
            if (r0 == 0) goto L4f
            r0.release()     // Catch: java.io.IOException -> L4f java.lang.Throwable -> Lb9
        L4f:
            monitor-exit(r10)
            return r13
        L51:
            if (r0 == 0) goto Laf
        L53:
            r0.release()     // Catch: java.io.IOException -> Laf java.lang.Throwable -> Lb9
            goto Laf
        L57:
            r13 = move-exception
            goto L5f
        L59:
            r13 = move-exception
            goto L8a
        L5b:
            r11 = move-exception
            goto Lb3
        L5d:
            r13 = move-exception
            r0 = r1
        L5f:
            java.lang.String r2 = "MiniThumbFile"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb1
            r3.<init>()     // Catch: java.lang.Throwable -> Lb1
            java.lang.String r4 = "Got exception when reading thumbnail, id = "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> Lb1
            java.lang.StringBuilder r11 = r3.append(r11)     // Catch: java.lang.Throwable -> Lb1
            java.lang.String r12 = ", disk full or mount read-only? "
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch: java.lang.Throwable -> Lb1
            java.lang.Class r12 = r13.getClass()     // Catch: java.lang.Throwable -> Lb1
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch: java.lang.Throwable -> Lb1
            java.lang.String r11 = r11.toString()     // Catch: java.lang.Throwable -> Lb1
            android.util.Log.e(r2, r11)     // Catch: java.lang.Throwable -> Lb1
            if (r0 == 0) goto Laf
            goto L53
        L88:
            r13 = move-exception
            r0 = r1
        L8a:
            java.lang.String r2 = "MiniThumbFile"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb1
            r3.<init>()     // Catch: java.lang.Throwable -> Lb1
            java.lang.String r4 = "got exception when reading thumbnail id="
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> Lb1
            java.lang.StringBuilder r11 = r3.append(r11)     // Catch: java.lang.Throwable -> Lb1
            java.lang.String r12 = ", exception: "
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch: java.lang.Throwable -> Lb1
            java.lang.StringBuilder r11 = r11.append(r13)     // Catch: java.lang.Throwable -> Lb1
            java.lang.String r11 = r11.toString()     // Catch: java.lang.Throwable -> Lb1
            android.util.Log.w(r2, r11)     // Catch: java.lang.Throwable -> Lb1
            if (r0 == 0) goto Laf
            goto L53
        Laf:
            monitor-exit(r10)
            return r1
        Lb1:
            r11 = move-exception
            r1 = r0
        Lb3:
            if (r1 == 0) goto Lb8
            r1.release()     // Catch: java.io.IOException -> Lb8 java.lang.Throwable -> Lb9
        Lb8:
            throw r11     // Catch: java.lang.Throwable -> Lb9
        Lb9:
            r11 = move-exception
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MiniThumbFile.getMiniThumbFromFile(long, byte[]):byte[]");
    }
}
