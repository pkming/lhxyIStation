package android.print;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.print.IPrintSpoolerCallbacks;
import android.print.IPrintSpoolerClient;

/* JADX INFO: loaded from: classes.dex */
public interface IPrintSpooler extends IInterface {
    void createPrintJob(PrintJobInfo printJobInfo) throws RemoteException;

    void getPrintJobInfo(PrintJobId printJobId, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i, int i2) throws RemoteException;

    void getPrintJobInfos(IPrintSpoolerCallbacks iPrintSpoolerCallbacks, ComponentName componentName, int i, int i2, int i3) throws RemoteException;

    void removeObsoletePrintJobs() throws RemoteException;

    void setClient(IPrintSpoolerClient iPrintSpoolerClient) throws RemoteException;

    void setPrintJobCancelling(PrintJobId printJobId, boolean z) throws RemoteException;

    void setPrintJobState(PrintJobId printJobId, int i, String str, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i2) throws RemoteException;

    void setPrintJobTag(PrintJobId printJobId, String str, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i) throws RemoteException;

    void writePrintJobData(ParcelFileDescriptor parcelFileDescriptor, PrintJobId printJobId) throws RemoteException;

    public static abstract class Stub extends Binder implements IPrintSpooler {
        private static final String DESCRIPTOR = "android.print.IPrintSpooler";
        static final int TRANSACTION_createPrintJob = 4;
        static final int TRANSACTION_getPrintJobInfo = 3;
        static final int TRANSACTION_getPrintJobInfos = 2;
        static final int TRANSACTION_removeObsoletePrintJobs = 1;
        static final int TRANSACTION_setClient = 8;
        static final int TRANSACTION_setPrintJobCancelling = 9;
        static final int TRANSACTION_setPrintJobState = 5;
        static final int TRANSACTION_setPrintJobTag = 6;
        static final int TRANSACTION_writePrintJobData = 7;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrintSpooler asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IPrintSpooler)) {
                return (IPrintSpooler) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    removeObsoletePrintJobs();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    getPrintJobInfos(IPrintSpoolerCallbacks.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt() != 0 ? ComponentName.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt(), parcel.readInt());
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    getPrintJobInfo(parcel.readInt() != 0 ? PrintJobId.CREATOR.createFromParcel(parcel) : null, IPrintSpoolerCallbacks.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt());
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    createPrintJob(parcel.readInt() != 0 ? PrintJobInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    setPrintJobState(parcel.readInt() != 0 ? PrintJobId.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readString(), IPrintSpoolerCallbacks.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    setPrintJobTag(parcel.readInt() != 0 ? PrintJobId.CREATOR.createFromParcel(parcel) : null, parcel.readString(), IPrintSpoolerCallbacks.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    writePrintJobData(parcel.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? PrintJobId.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    setClient(IPrintSpoolerClient.Stub.asInterface(parcel.readStrongBinder()));
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    setPrintJobCancelling(parcel.readInt() != 0 ? PrintJobId.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IPrintSpooler {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.print.IPrintSpooler
            public void removeObsoletePrintJobs() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void getPrintJobInfos(IPrintSpoolerCallbacks iPrintSpoolerCallbacks, ComponentName componentName, int i, int i2, int i3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iPrintSpoolerCallbacks != null ? iPrintSpoolerCallbacks.asBinder() : null);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void getPrintJobInfo(PrintJobId printJobId, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printJobId != null) {
                        parcelObtain.writeInt(1);
                        printJobId.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(iPrintSpoolerCallbacks != null ? iPrintSpoolerCallbacks.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void createPrintJob(PrintJobInfo printJobInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printJobInfo != null) {
                        parcelObtain.writeInt(1);
                        printJobInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setPrintJobState(PrintJobId printJobId, int i, String str, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printJobId != null) {
                        parcelObtain.writeInt(1);
                        printJobId.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeStrongBinder(iPrintSpoolerCallbacks != null ? iPrintSpoolerCallbacks.asBinder() : null);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setPrintJobTag(PrintJobId printJobId, String str, IPrintSpoolerCallbacks iPrintSpoolerCallbacks, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printJobId != null) {
                        parcelObtain.writeInt(1);
                        printJobId.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeString(str);
                    parcelObtain.writeStrongBinder(iPrintSpoolerCallbacks != null ? iPrintSpoolerCallbacks.asBinder() : null);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void writePrintJobData(ParcelFileDescriptor parcelFileDescriptor, PrintJobId printJobId) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelFileDescriptor != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (printJobId != null) {
                        parcelObtain.writeInt(1);
                        printJobId.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(7, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setClient(IPrintSpoolerClient iPrintSpoolerClient) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iPrintSpoolerClient != null ? iPrintSpoolerClient.asBinder() : null);
                    this.mRemote.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.print.IPrintSpooler
            public void setPrintJobCancelling(PrintJobId printJobId, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printJobId != null) {
                        parcelObtain.writeInt(1);
                        printJobId.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(9, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
