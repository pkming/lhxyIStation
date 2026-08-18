package android.app.backup;

import android.app.IBackupAgent;
import android.app.QueuedWork;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.OsConstants;
import libcore.io.StructStat;

/* JADX INFO: loaded from: classes.dex */
public abstract class BackupAgent extends ContextWrapper {
    private static final boolean DEBUG = true;
    private static final String TAG = "BackupAgent";
    public static final int TYPE_DIRECTORY = 2;
    public static final int TYPE_EOF = 0;
    public static final int TYPE_FILE = 1;
    public static final int TYPE_SYMLINK = 3;
    private final IBinder mBinder;
    Handler mHandler;

    public abstract void onBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) throws IOException;

    public void onCreate() {
    }

    public void onDestroy() {
    }

    public abstract void onRestore(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor) throws IOException;

    class SharedPrefsSynchronizer implements Runnable {
        public final CountDownLatch mLatch = new CountDownLatch(1);

        SharedPrefsSynchronizer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            QueuedWork.waitToFinish();
            this.mLatch.countDown();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitForSharedPrefs() {
        if (this.mHandler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        }
        SharedPrefsSynchronizer sharedPrefsSynchronizer = new SharedPrefsSynchronizer();
        this.mHandler.postAtFrontOfQueue(sharedPrefsSynchronizer);
        try {
            sharedPrefsSynchronizer.mLatch.await();
        } catch (InterruptedException unused) {
        }
    }

    public BackupAgent() {
        super(null);
        this.mHandler = null;
        this.mBinder = new BackupServiceBinder().asBinder();
    }

    public void onFullBackup(FullBackupDataOutput fullBackupDataOutput) throws IOException {
        File externalFilesDir;
        ApplicationInfo applicationInfo = getApplicationInfo();
        String canonicalPath = new File(applicationInfo.dataDir).getCanonicalPath();
        String canonicalPath2 = getFilesDir().getCanonicalPath();
        String canonicalPath3 = getDatabasePath("foo").getParentFile().getCanonicalPath();
        String canonicalPath4 = getSharedPrefsFile("foo").getParentFile().getCanonicalPath();
        String canonicalPath5 = getCacheDir().getCanonicalPath();
        String canonicalPath6 = applicationInfo.nativeLibraryDir != null ? new File(applicationInfo.nativeLibraryDir).getCanonicalPath() : null;
        HashSet<String> hashSet = new HashSet<>();
        String packageName = getPackageName();
        if (canonicalPath6 != null) {
            hashSet.add(canonicalPath6);
        }
        hashSet.add(canonicalPath5);
        hashSet.add(canonicalPath3);
        hashSet.add(canonicalPath4);
        hashSet.add(canonicalPath2);
        fullBackupFileTree(packageName, FullBackup.ROOT_TREE_TOKEN, canonicalPath, hashSet, fullBackupDataOutput);
        hashSet.add(canonicalPath);
        hashSet.remove(canonicalPath2);
        fullBackupFileTree(packageName, FullBackup.DATA_TREE_TOKEN, canonicalPath2, hashSet, fullBackupDataOutput);
        hashSet.add(canonicalPath2);
        hashSet.remove(canonicalPath3);
        fullBackupFileTree(packageName, FullBackup.DATABASE_TREE_TOKEN, canonicalPath3, hashSet, fullBackupDataOutput);
        hashSet.add(canonicalPath3);
        hashSet.remove(canonicalPath4);
        fullBackupFileTree(packageName, FullBackup.SHAREDPREFS_TREE_TOKEN, canonicalPath4, hashSet, fullBackupDataOutput);
        if (Process.myUid() == 1000 || (externalFilesDir = getExternalFilesDir(null)) == null) {
            return;
        }
        fullBackupFileTree(packageName, "ef", externalFilesDir.getCanonicalPath(), null, fullBackupDataOutput);
    }

    public final void fullBackupFile(File file, FullBackupDataOutput fullBackupDataOutput) {
        String str;
        String str2;
        File externalFilesDir;
        ApplicationInfo applicationInfo = getApplicationInfo();
        try {
            String canonicalPath = new File(applicationInfo.dataDir).getCanonicalPath();
            String canonicalPath2 = getFilesDir().getCanonicalPath();
            String canonicalPath3 = getDatabasePath("foo").getParentFile().getCanonicalPath();
            String canonicalPath4 = getSharedPrefsFile("foo").getParentFile().getCanonicalPath();
            String canonicalPath5 = getCacheDir().getCanonicalPath();
            String canonicalPath6 = null;
            String canonicalPath7 = applicationInfo.nativeLibraryDir == null ? null : new File(applicationInfo.nativeLibraryDir).getCanonicalPath();
            if (Process.myUid() != 1000 && (externalFilesDir = getExternalFilesDir(null)) != null) {
                canonicalPath6 = externalFilesDir.getCanonicalPath();
            }
            String canonicalPath8 = file.getCanonicalPath();
            if (canonicalPath8.startsWith(canonicalPath5) || canonicalPath8.startsWith(canonicalPath7)) {
                Log.w(TAG, "lib and cache files are not backed up");
                return;
            }
            if (canonicalPath8.startsWith(canonicalPath3)) {
                str = FullBackup.DATABASE_TREE_TOKEN;
                str2 = canonicalPath3;
            } else if (canonicalPath8.startsWith(canonicalPath4)) {
                str2 = canonicalPath4;
                str = FullBackup.SHAREDPREFS_TREE_TOKEN;
            } else if (canonicalPath8.startsWith(canonicalPath2)) {
                str = FullBackup.DATA_TREE_TOKEN;
                str2 = canonicalPath2;
            } else if (canonicalPath8.startsWith(canonicalPath)) {
                str = FullBackup.ROOT_TREE_TOKEN;
                str2 = canonicalPath;
            } else if (canonicalPath6 == null || !canonicalPath8.startsWith(canonicalPath6)) {
                Log.w(TAG, "File " + canonicalPath8 + " is in an unsupported location; skipping");
                return;
            } else {
                str = "ef";
                str2 = canonicalPath6;
            }
            Log.i(TAG, "backupFile() of " + canonicalPath8 + " => domain=" + str + " rootpath=" + str2);
            FullBackup.backupToTar(getPackageName(), str, null, str2, canonicalPath8, fullBackupDataOutput.getData());
        } catch (IOException unused) {
            Log.w(TAG, "Unable to obtain canonical paths");
        }
    }

    protected final void fullBackupFileTree(String str, String str2, String str3, HashSet<String> hashSet, FullBackupDataOutput fullBackupDataOutput) {
        String canonicalPath;
        File[] fileArrListFiles;
        File file = new File(str3);
        if (file.exists()) {
            LinkedList linkedList = new LinkedList();
            linkedList.add(file);
            while (linkedList.size() > 0) {
                File file2 = (File) linkedList.remove(0);
                try {
                    canonicalPath = file2.getCanonicalPath();
                } catch (ErrnoException e) {
                    Log.w(TAG, "Error scanning file " + file2 + " : " + e);
                } catch (IOException unused) {
                    Log.w(TAG, "Error canonicalizing path of " + file2);
                }
                if (hashSet == null || !hashSet.contains(canonicalPath)) {
                    StructStat structStatLstat = Libcore.os.lstat(canonicalPath);
                    if (OsConstants.S_ISLNK(structStatLstat.st_mode)) {
                        Log.i(TAG, "Symlink (skipping)!: " + file2);
                    } else {
                        if (OsConstants.S_ISDIR(structStatLstat.st_mode) && (fileArrListFiles = file2.listFiles()) != null) {
                            for (File file3 : fileArrListFiles) {
                                linkedList.add(0, file3);
                            }
                        }
                        FullBackup.backupToTar(str, str2, null, str3, canonicalPath, fullBackupDataOutput.getData());
                    }
                }
            }
        }
    }

    public void onRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, File file, int i, long j2, long j3) throws IOException {
        FullBackup.restoreFile(parcelFileDescriptor, j, i, j2, j3, file);
    }

    protected void onRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, int i, String str, String str2, long j2, long j3) throws IOException {
        long j4 = j2;
        Log.d(TAG, "onRestoreFile() size=" + j + " type=" + i + " domain=" + str + " relpath=" + str2 + " mode=" + j4 + " mtime=" + j3);
        String canonicalPath = null;
        if (str.equals(FullBackup.DATA_TREE_TOKEN)) {
            canonicalPath = getFilesDir().getCanonicalPath();
        } else if (str.equals(FullBackup.DATABASE_TREE_TOKEN)) {
            canonicalPath = getDatabasePath("foo").getParentFile().getCanonicalPath();
        } else if (str.equals(FullBackup.ROOT_TREE_TOKEN)) {
            canonicalPath = new File(getApplicationInfo().dataDir).getCanonicalPath();
        } else if (str.equals(FullBackup.SHAREDPREFS_TREE_TOKEN)) {
            canonicalPath = getSharedPrefsFile("foo").getParentFile().getCanonicalPath();
        } else if (str.equals(FullBackup.CACHE_TREE_TOKEN)) {
            canonicalPath = getCacheDir().getCanonicalPath();
        } else if (str.equals("ef")) {
            if (Process.myUid() != 1000 && getExternalFilesDir(null) != null) {
                j4 = -1;
                canonicalPath = getExternalFilesDir(null).getCanonicalPath();
            }
        } else {
            Log.i(TAG, "Unrecognized domain " + str);
        }
        long j5 = j4;
        if (canonicalPath != null) {
            File file = new File(canonicalPath, str2);
            String canonicalPath2 = file.getCanonicalPath();
            if (canonicalPath2.startsWith(canonicalPath + File.separatorChar)) {
                Log.i(TAG, "[" + str + " : " + str2 + "] mapped to " + canonicalPath2);
                onRestoreFile(parcelFileDescriptor, j, file, i, j5, j3);
                return;
            }
            Log.e(TAG, "Cross-domain restore attempt: " + canonicalPath2);
        }
        Log.i(TAG, "[ skipping file " + str2 + "]");
        FullBackup.restoreFile(parcelFileDescriptor, j, i, j5, j3, null);
    }

    public final IBinder onBind() {
        return this.mBinder;
    }

    public void attach(Context context) {
        attachBaseContext(context);
    }

    private class BackupServiceBinder extends IBackupAgent.Stub {
        private static final String TAG = "BackupServiceBinder";

        private BackupServiceBinder() {
        }

        @Override // android.app.IBackupAgent
        public void doBackup(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, int i, IBackupManager iBackupManager) throws RemoteException {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            Log.v(TAG, "doBackup() invoked");
            try {
                try {
                    try {
                        BackupAgent.this.onBackup(parcelFileDescriptor, new BackupDataOutput(parcelFileDescriptor2.getFileDescriptor()), parcelFileDescriptor3);
                        try {
                            iBackupManager.opComplete(i);
                        } catch (RemoteException unused) {
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "onBackup (" + BackupAgent.this.getClass().getName() + ") threw", e);
                        throw new RuntimeException(e);
                    }
                } catch (RuntimeException e2) {
                    Log.d(TAG, "onBackup (" + BackupAgent.this.getClass().getName() + ") threw", e2);
                    throw e2;
                }
            } finally {
                BackupAgent.this.waitForSharedPrefs();
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                try {
                    iBackupManager.opComplete(i);
                } catch (RemoteException unused2) {
                }
            }
        }

        @Override // android.app.IBackupAgent
        public void doRestore(ParcelFileDescriptor parcelFileDescriptor, int i, ParcelFileDescriptor parcelFileDescriptor2, int i2, IBackupManager iBackupManager) throws RemoteException {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            Log.v(TAG, "doRestore() invoked");
            try {
                try {
                    try {
                        BackupAgent.this.onRestore(new BackupDataInput(parcelFileDescriptor.getFileDescriptor()), i, parcelFileDescriptor2);
                        try {
                            iBackupManager.opComplete(i2);
                        } catch (RemoteException unused) {
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "onRestore (" + BackupAgent.this.getClass().getName() + ") threw", e);
                        throw new RuntimeException(e);
                    }
                } catch (RuntimeException e2) {
                    Log.d(TAG, "onRestore (" + BackupAgent.this.getClass().getName() + ") threw", e2);
                    throw e2;
                }
            } finally {
                BackupAgent.this.waitForSharedPrefs();
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                try {
                    iBackupManager.opComplete(i2);
                } catch (RemoteException unused2) {
                }
            }
        }

        @Override // android.app.IBackupAgent
        public void doFullBackup(ParcelFileDescriptor parcelFileDescriptor, int i, IBackupManager iBackupManager) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            Log.v(TAG, "doFullBackup() invoked");
            BackupAgent.this.waitForSharedPrefs();
            try {
                try {
                    try {
                        BackupAgent.this.onFullBackup(new FullBackupDataOutput(parcelFileDescriptor));
                        BackupAgent.this.waitForSharedPrefs();
                        try {
                            new FileOutputStream(parcelFileDescriptor.getFileDescriptor()).write(new byte[4]);
                        } catch (IOException unused) {
                            Log.e(TAG, "Unable to finalize backup stream!");
                        }
                        Binder.restoreCallingIdentity(jClearCallingIdentity);
                        try {
                            iBackupManager.opComplete(i);
                        } catch (RemoteException unused2) {
                        }
                    } catch (RuntimeException e) {
                        Log.d(TAG, "onBackup (" + BackupAgent.this.getClass().getName() + ") threw", e);
                        throw e;
                    }
                } catch (IOException e2) {
                    Log.d(TAG, "onBackup (" + BackupAgent.this.getClass().getName() + ") threw", e2);
                    throw new RuntimeException(e2);
                }
            } finally {
            }
        }

        @Override // android.app.IBackupAgent
        public void doRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, int i, String str, String str2, long j2, long j3, int i2, IBackupManager iBackupManager) throws RemoteException {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    BackupAgent.this.onRestoreFile(parcelFileDescriptor, j, i, str, str2, j2, j3);
                    try {
                        iBackupManager.opComplete(i2);
                    } catch (RemoteException unused) {
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                BackupAgent.this.waitForSharedPrefs();
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                try {
                    iBackupManager.opComplete(i2);
                } catch (RemoteException unused2) {
                }
            }
        }
    }
}
