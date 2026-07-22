package android.os;

import android.os.IBinder;
import android.util.Log;
import com.android.internal.util.FastPrintWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class Binder implements IBinder {
    private static final boolean FIND_POTENTIAL_LEAKS = false;
    private static final String TAG = "Binder";
    private static String sDumpDisabled;
    private String mDescriptor;
    private int mObject;
    private IInterface mOwner;

    public static final native long clearCallingIdentity();

    /* JADX INFO: Access modifiers changed from: private */
    protected final native void destroy();

    public static final native void flushPendingCommands();

    public static final native int getCallingPid();

    public static final native int getCallingUid();

    public static final native int getThreadStrictModePolicy();

    private final native void init();

    public static final native void joinThreadPool();

    public static final native void restoreCallingIdentity(long j);

    public static final native void setThreadStrictModePolicy(int i);

    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Override // android.os.IBinder
    public boolean isBinderAlive() {
        return true;
    }

    @Override // android.os.IBinder
    public void linkToDeath(IBinder.DeathRecipient deathRecipient, int i) {
    }

    @Override // android.os.IBinder
    public boolean pingBinder() {
        return true;
    }

    @Override // android.os.IBinder
    public boolean unlinkToDeath(IBinder.DeathRecipient deathRecipient, int i) {
        return true;
    }

    public static final UserHandle getCallingUserHandle() {
        return new UserHandle(UserHandle.getUserId(getCallingUid()));
    }

    public static final boolean isProxy(IInterface iInterface) {
        return iInterface.asBinder() != iInterface;
    }

    public Binder() {
        init();
    }

    public void attachInterface(IInterface iInterface, String str) {
        this.mOwner = iInterface;
        this.mDescriptor = str;
    }

    @Override // android.os.IBinder
    public String getInterfaceDescriptor() {
        return this.mDescriptor;
    }

    @Override // android.os.IBinder
    public IInterface queryLocalInterface(String str) {
        if (this.mDescriptor.equals(str)) {
            return this.mOwner;
        }
        return null;
    }

    public static void setDumpDisabled(String str) {
        synchronized (Binder.class) {
            sDumpDisabled = str;
        }
    }

    protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (i == 1598968902) {
            parcel2.writeString(getInterfaceDescriptor());
            return true;
        }
        if (i != 1598311760) {
            return false;
        }
        ParcelFileDescriptor fileDescriptor = parcel.readFileDescriptor();
        String[] stringArray = parcel.readStringArray();
        if (fileDescriptor != null) {
            try {
                dump(fileDescriptor.getFileDescriptor(), stringArray);
            } finally {
                try {
                    fileDescriptor.close();
                } catch (IOException unused) {
                }
            }
        }
        if (parcel2 != null) {
            parcel2.writeNoException();
        } else {
            StrictMode.clearGatheredViolations();
        }
        return true;
    }

    @Override // android.os.IBinder
    public void dump(FileDescriptor fileDescriptor, String[] strArr) {
        String str;
        PrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(fileDescriptor));
        try {
            synchronized (Binder.class) {
                str = sDumpDisabled;
            }
            if (str == null) {
                try {
                    dump(fileDescriptor, fastPrintWriter, strArr);
                } catch (SecurityException e) {
                    fastPrintWriter.println("Security exception: " + e.getMessage());
                    throw e;
                } catch (Throwable th) {
                    fastPrintWriter.println();
                    fastPrintWriter.println("Exception occurred while dumping:");
                    th.printStackTrace(fastPrintWriter);
                }
            } else {
                fastPrintWriter.println(str);
            }
        } finally {
            fastPrintWriter.flush();
        }
    }

    @Override // android.os.IBinder
    public void dumpAsync(final FileDescriptor fileDescriptor, final String[] strArr) {
        final FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(fileDescriptor));
        new Thread("Binder.dumpAsync") { // from class: android.os.Binder.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Binder.this.dump(fileDescriptor, fastPrintWriter, strArr);
                } finally {
                    fastPrintWriter.flush();
                }
            }
        }.start();
    }

    @Override // android.os.IBinder
    public final boolean transact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (parcel != null) {
            parcel.setDataPosition(0);
        }
        boolean zOnTransact = onTransact(i, parcel, parcel2, i2);
        if (parcel2 != null) {
            parcel2.setDataPosition(0);
        }
        return zOnTransact;
    }

    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }

    private boolean execTransact(int i, int i2, int i3, int i4) {
        Parcel parcelObtain = Parcel.obtain(i2);
        Parcel parcelObtain2 = Parcel.obtain(i3);
        boolean zOnTransact = true;
        try {
            zOnTransact = onTransact(i, parcelObtain, parcelObtain2, i4);
        } catch (RemoteException e) {
            if ((i4 & 1) != 0) {
                Log.w(TAG, "Binder call failed.", e);
            }
            parcelObtain2.setDataPosition(0);
            parcelObtain2.writeException(e);
        } catch (OutOfMemoryError e2) {
            Log.e(TAG, "Caught an OutOfMemoryError from the binder stub implementation.", e2);
            RuntimeException runtimeException = new RuntimeException("Out of memory", e2);
            parcelObtain2.setDataPosition(0);
            parcelObtain2.writeException(runtimeException);
        } catch (RuntimeException e3) {
            if ((i4 & 1) != 0) {
                Log.w(TAG, "Caught a RuntimeException from the binder stub implementation.", e3);
            }
            parcelObtain2.setDataPosition(0);
            parcelObtain2.writeException(e3);
        }
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return zOnTransact;
    }
}
