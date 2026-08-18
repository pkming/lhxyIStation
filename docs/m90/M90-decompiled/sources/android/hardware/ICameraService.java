package android.hardware;

import android.hardware.ICameraClient;
import android.hardware.ICameraServiceListener;
import android.hardware.IProCameraCallbacks;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.utils.BinderHolder;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICameraService extends IInterface {
    int addListener(ICameraServiceListener iCameraServiceListener) throws RemoteException;

    int connect(ICameraClient iCameraClient, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException;

    int connectDevice(ICameraDeviceCallbacks iCameraDeviceCallbacks, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException;

    int connectPro(IProCameraCallbacks iProCameraCallbacks, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException;

    int getCameraCharacteristics(int i, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    int getCameraInfo(int i, CameraInfo cameraInfo) throws RemoteException;

    int getNumberOfCameras() throws RemoteException;

    int removeListener(ICameraServiceListener iCameraServiceListener) throws RemoteException;

    public static abstract class Stub extends Binder implements ICameraService {
        private static final String DESCRIPTOR = "android.hardware.ICameraService";
        static final int TRANSACTION_addListener = 6;
        static final int TRANSACTION_connect = 3;
        static final int TRANSACTION_connectDevice = 5;
        static final int TRANSACTION_connectPro = 4;
        static final int TRANSACTION_getCameraCharacteristics = 8;
        static final int TRANSACTION_getCameraInfo = 2;
        static final int TRANSACTION_getNumberOfCameras = 1;
        static final int TRANSACTION_removeListener = 7;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICameraService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof ICameraService)) {
                return (ICameraService) iInterfaceQueryLocalInterface;
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
                    int numberOfCameras = getNumberOfCameras();
                    parcel2.writeNoException();
                    parcel2.writeInt(numberOfCameras);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    int i3 = parcel.readInt();
                    CameraInfo cameraInfo = new CameraInfo();
                    int cameraInfo2 = getCameraInfo(i3, cameraInfo);
                    parcel2.writeNoException();
                    parcel2.writeInt(cameraInfo2);
                    parcel2.writeInt(1);
                    cameraInfo.writeToParcel(parcel2, 1);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    ICameraClient iCameraClientAsInterface = ICameraClient.Stub.asInterface(parcel.readStrongBinder());
                    int i4 = parcel.readInt();
                    String string = parcel.readString();
                    int i5 = parcel.readInt();
                    BinderHolder binderHolder = new BinderHolder();
                    int iConnect = connect(iCameraClientAsInterface, i4, string, i5, binderHolder);
                    parcel2.writeNoException();
                    parcel2.writeInt(iConnect);
                    parcel2.writeInt(1);
                    binderHolder.writeToParcel(parcel2, 1);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    IProCameraCallbacks iProCameraCallbacksAsInterface = IProCameraCallbacks.Stub.asInterface(parcel.readStrongBinder());
                    int i6 = parcel.readInt();
                    String string2 = parcel.readString();
                    int i7 = parcel.readInt();
                    BinderHolder binderHolder2 = new BinderHolder();
                    int iConnectPro = connectPro(iProCameraCallbacksAsInterface, i6, string2, i7, binderHolder2);
                    parcel2.writeNoException();
                    parcel2.writeInt(iConnectPro);
                    parcel2.writeInt(1);
                    binderHolder2.writeToParcel(parcel2, 1);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    ICameraDeviceCallbacks iCameraDeviceCallbacksAsInterface = ICameraDeviceCallbacks.Stub.asInterface(parcel.readStrongBinder());
                    int i8 = parcel.readInt();
                    String string3 = parcel.readString();
                    int i9 = parcel.readInt();
                    BinderHolder binderHolder3 = new BinderHolder();
                    int iConnectDevice = connectDevice(iCameraDeviceCallbacksAsInterface, i8, string3, i9, binderHolder3);
                    parcel2.writeNoException();
                    parcel2.writeInt(iConnectDevice);
                    parcel2.writeInt(1);
                    binderHolder3.writeToParcel(parcel2, 1);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iAddListener = addListener(ICameraServiceListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(iAddListener);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iRemoveListener = removeListener(ICameraServiceListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(iRemoveListener);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    int i10 = parcel.readInt();
                    CameraMetadataNative cameraMetadataNative = new CameraMetadataNative();
                    int cameraCharacteristics = getCameraCharacteristics(i10, cameraMetadataNative);
                    parcel2.writeNoException();
                    parcel2.writeInt(cameraCharacteristics);
                    parcel2.writeInt(1);
                    cameraMetadataNative.writeToParcel(parcel2, 1);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements ICameraService {
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

            @Override // android.hardware.ICameraService
            public int getNumberOfCameras() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int getCameraInfo(int i, CameraInfo cameraInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i2 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        cameraInfo.readFromParcel(parcelObtain2);
                    }
                    return i2;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int connect(ICameraClient iCameraClient, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCameraClient != null ? iCameraClient.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i3 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        binderHolder.readFromParcel(parcelObtain2);
                    }
                    return i3;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int connectPro(IProCameraCallbacks iProCameraCallbacks, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iProCameraCallbacks != null ? iProCameraCallbacks.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i3 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        binderHolder.readFromParcel(parcelObtain2);
                    }
                    return i3;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int connectDevice(ICameraDeviceCallbacks iCameraDeviceCallbacks, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCameraDeviceCallbacks != null ? iCameraDeviceCallbacks.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i3 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        binderHolder.readFromParcel(parcelObtain2);
                    }
                    return i3;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int addListener(ICameraServiceListener iCameraServiceListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCameraServiceListener != null ? iCameraServiceListener.asBinder() : null);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int removeListener(ICameraServiceListener iCameraServiceListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCameraServiceListener != null ? iCameraServiceListener.asBinder() : null);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int getCameraCharacteristics(int i, CameraMetadataNative cameraMetadataNative) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i2 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        cameraMetadataNative.readFromParcel(parcelObtain2);
                    }
                    return i2;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
