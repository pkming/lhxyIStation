package android.os;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import libcore.io.OsConstants;

/* JADX INFO: loaded from: classes.dex */
class CommonTimeUtils {
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -4;
    public static final int ERROR_DEAD_OBJECT = -7;
    public static final int SUCCESS = 0;
    private String mInterfaceDesc;
    private IBinder mRemote;

    public CommonTimeUtils(IBinder iBinder, String str) {
        this.mRemote = iBinder;
        this.mInterfaceDesc = str;
    }

    public int transactGetInt(int i, int i2) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(this.mInterfaceDesc);
            this.mRemote.transact(i, parcelObtain, parcelObtain2, 0);
            if (parcelObtain2.readInt() == 0) {
                i2 = parcelObtain2.readInt();
            }
            return i2;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    public int transactSetInt(int i, int i2) {
        int i3;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(this.mInterfaceDesc);
            parcelObtain.writeInt(i2);
            this.mRemote.transact(i, parcelObtain, parcelObtain2, 0);
            i3 = parcelObtain2.readInt();
        } catch (RemoteException unused) {
            i3 = -7;
        } catch (Throwable th) {
            parcelObtain2.recycle();
            parcelObtain.recycle();
            throw th;
        }
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i3;
    }

    public long transactGetLong(int i, long j) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(this.mInterfaceDesc);
            this.mRemote.transact(i, parcelObtain, parcelObtain2, 0);
            if (parcelObtain2.readInt() == 0) {
                j = parcelObtain2.readLong();
            }
            return j;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    public int transactSetLong(int i, long j) {
        int i2;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(this.mInterfaceDesc);
            parcelObtain.writeLong(j);
            this.mRemote.transact(i, parcelObtain, parcelObtain2, 0);
            i2 = parcelObtain2.readInt();
        } catch (RemoteException unused) {
            i2 = -7;
        } catch (Throwable th) {
            parcelObtain2.recycle();
            parcelObtain.recycle();
            throw th;
        }
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i2;
    }

    public String transactGetString(int i, String str) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(this.mInterfaceDesc);
            this.mRemote.transact(i, parcelObtain, parcelObtain2, 0);
            if (parcelObtain2.readInt() == 0) {
                str = parcelObtain2.readString();
            }
            return str;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    public int transactSetString(int i, String str) {
        int i2;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(this.mInterfaceDesc);
            parcelObtain.writeString(str);
            this.mRemote.transact(i, parcelObtain, parcelObtain2, 0);
            i2 = parcelObtain2.readInt();
        } catch (RemoteException unused) {
            i2 = -7;
        } catch (Throwable th) {
            parcelObtain2.recycle();
            parcelObtain.recycle();
            throw th;
        }
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i2;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x00e7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.net.InetSocketAddress transactGetSockaddr(int r19) throws android.os.RemoteException {
        /*
            Method dump skipped, instruction units count: 247
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.CommonTimeUtils.transactGetSockaddr(int):java.net.InetSocketAddress");
    }

    public int transactSetSockaddr(int i, InetSocketAddress inetSocketAddress) {
        int i2;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(this.mInterfaceDesc);
            if (inetSocketAddress == null) {
                parcelObtain.writeInt(0);
            } else {
                parcelObtain.writeInt(1);
                InetAddress address = inetSocketAddress.getAddress();
                byte[] address2 = address.getAddress();
                int port = inetSocketAddress.getPort();
                if (address instanceof Inet4Address) {
                    int i3 = ((address2[1] & 255) << 16) | ((address2[0] & 255) << 24) | ((address2[2] & 255) << 8) | (address2[3] & 255);
                    parcelObtain.writeInt(OsConstants.AF_INET);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(port);
                } else if (address instanceof Inet6Address) {
                    Inet6Address inet6Address = (Inet6Address) address;
                    parcelObtain.writeInt(OsConstants.AF_INET6);
                    for (int i4 = 0; i4 < 4; i4++) {
                        int i5 = i4 * 4;
                        parcelObtain.writeInt((address2[i5 + 3] & 255) | ((address2[i5 + 0] & 255) << 24) | ((address2[i5 + 1] & 255) << 16) | ((address2[i5 + 2] & 255) << 8));
                    }
                    parcelObtain.writeInt(port);
                    parcelObtain.writeInt(0);
                    parcelObtain.writeInt(inet6Address.getScopeId());
                } else {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                    return -4;
                }
            }
            this.mRemote.transact(i, parcelObtain, parcelObtain2, 0);
            i2 = parcelObtain2.readInt();
        } catch (RemoteException unused) {
            i2 = -7;
        } catch (Throwable th) {
            parcelObtain2.recycle();
            parcelObtain.recycle();
            throw th;
        }
        parcelObtain2.recycle();
        parcelObtain.recycle();
        return i2;
    }
}
