package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.BulkCursorDescriptor;
import android.database.Cursor;
import android.database.CursorToBulkCursorAdaptor;
import android.database.DatabaseUtils;
import android.database.IContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContentProviderNative extends Binder implements IContentProvider {
    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    public abstract String getProviderName();

    public ContentProviderNative() {
        attachInterface(this, IContentProvider.descriptor);
    }

    public static IContentProvider asInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IContentProvider iContentProvider = (IContentProvider) iBinder.queryLocalInterface(IContentProvider.descriptor);
        return iContentProvider != null ? iContentProvider : new ContentProviderProxy(iBinder);
    }

    @Override // android.os.Binder
    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws Throwable {
        String[] strArr;
        String[] strArr2;
        try {
            if (i == 1) {
                parcel.enforceInterface(IContentProvider.descriptor);
                String string = parcel.readString();
                Uri uriCreateFromParcel = Uri.CREATOR.createFromParcel(parcel);
                int i3 = parcel.readInt();
                Cursor cursor = null;
                if (i3 > 0) {
                    String[] strArr3 = new String[i3];
                    for (int i4 = 0; i4 < i3; i4++) {
                        strArr3[i4] = parcel.readString();
                    }
                    strArr = strArr3;
                } else {
                    strArr = null;
                }
                String string2 = parcel.readString();
                int i5 = parcel.readInt();
                if (i5 > 0) {
                    String[] strArr4 = new String[i5];
                    for (int i6 = 0; i6 < i5; i6++) {
                        strArr4[i6] = parcel.readString();
                    }
                    strArr2 = strArr4;
                } else {
                    strArr2 = null;
                }
                String string3 = parcel.readString();
                IContentObserver iContentObserverAsInterface = IContentObserver.Stub.asInterface(parcel.readStrongBinder());
                Cursor cursorQuery = query(string, uriCreateFromParcel, strArr, string2, strArr2, string3, ICancellationSignal.Stub.asInterface(parcel.readStrongBinder()));
                if (cursorQuery != null) {
                    try {
                        BulkCursorDescriptor bulkCursorDescriptor = new CursorToBulkCursorAdaptor(cursorQuery, iContentObserverAsInterface, getProviderName()).getBulkCursorDescriptor();
                        try {
                            parcel2.writeNoException();
                            parcel2.writeInt(1);
                            bulkCursorDescriptor.writeToParcel(parcel2, 1);
                        } catch (Throwable th) {
                            th = th;
                            if (cursor != null) {
                                cursor.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = cursorQuery;
                    }
                } else {
                    parcel2.writeNoException();
                    parcel2.writeInt(0);
                }
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(IContentProvider.descriptor);
                String type = getType(Uri.CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                parcel2.writeString(type);
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(IContentProvider.descriptor);
                Uri uriInsert = insert(parcel.readString(), Uri.CREATOR.createFromParcel(parcel), ContentValues.CREATOR.createFromParcel(parcel));
                parcel2.writeNoException();
                Uri.writeToParcel(parcel2, uriInsert);
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(IContentProvider.descriptor);
                int iDelete = delete(parcel.readString(), Uri.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readStringArray());
                parcel2.writeNoException();
                parcel2.writeInt(iDelete);
                return true;
            }
            if (i != 10) {
                switch (i) {
                    case 13:
                        parcel.enforceInterface(IContentProvider.descriptor);
                        int iBulkInsert = bulkInsert(parcel.readString(), Uri.CREATOR.createFromParcel(parcel), (ContentValues[]) parcel.createTypedArray(ContentValues.CREATOR));
                        parcel2.writeNoException();
                        parcel2.writeInt(iBulkInsert);
                        return true;
                    case 14:
                        parcel.enforceInterface(IContentProvider.descriptor);
                        ParcelFileDescriptor parcelFileDescriptorOpenFile = openFile(parcel.readString(), Uri.CREATOR.createFromParcel(parcel), parcel.readString(), ICancellationSignal.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        if (parcelFileDescriptorOpenFile != null) {
                            parcel2.writeInt(1);
                            parcelFileDescriptorOpenFile.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 15:
                        parcel.enforceInterface(IContentProvider.descriptor);
                        AssetFileDescriptor assetFileDescriptorOpenAssetFile = openAssetFile(parcel.readString(), Uri.CREATOR.createFromParcel(parcel), parcel.readString(), ICancellationSignal.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        if (assetFileDescriptorOpenAssetFile != null) {
                            parcel2.writeInt(1);
                            assetFileDescriptorOpenAssetFile.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    default:
                        switch (i) {
                            case 20:
                                parcel.enforceInterface(IContentProvider.descriptor);
                                String string4 = parcel.readString();
                                int i7 = parcel.readInt();
                                ArrayList<ContentProviderOperation> arrayList = new ArrayList<>(i7);
                                for (int i8 = 0; i8 < i7; i8++) {
                                    arrayList.add(i8, ContentProviderOperation.CREATOR.createFromParcel(parcel));
                                }
                                ContentProviderResult[] contentProviderResultArrApplyBatch = applyBatch(string4, arrayList);
                                parcel2.writeNoException();
                                parcel2.writeTypedArray(contentProviderResultArrApplyBatch, 0);
                                return true;
                            case 21:
                                parcel.enforceInterface(IContentProvider.descriptor);
                                Bundle bundleCall = call(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readBundle());
                                parcel2.writeNoException();
                                parcel2.writeBundle(bundleCall);
                                return true;
                            case 22:
                                parcel.enforceInterface(IContentProvider.descriptor);
                                String[] streamTypes = getStreamTypes(Uri.CREATOR.createFromParcel(parcel), parcel.readString());
                                parcel2.writeNoException();
                                parcel2.writeStringArray(streamTypes);
                                return true;
                            case 23:
                                parcel.enforceInterface(IContentProvider.descriptor);
                                AssetFileDescriptor assetFileDescriptorOpenTypedAssetFile = openTypedAssetFile(parcel.readString(), Uri.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readBundle(), ICancellationSignal.Stub.asInterface(parcel.readStrongBinder()));
                                parcel2.writeNoException();
                                if (assetFileDescriptorOpenTypedAssetFile != null) {
                                    parcel2.writeInt(1);
                                    assetFileDescriptorOpenTypedAssetFile.writeToParcel(parcel2, 1);
                                } else {
                                    parcel2.writeInt(0);
                                }
                                return true;
                            case 24:
                                parcel.enforceInterface(IContentProvider.descriptor);
                                ICancellationSignal iCancellationSignalCreateCancellationSignal = createCancellationSignal();
                                parcel2.writeNoException();
                                parcel2.writeStrongBinder(iCancellationSignalCreateCancellationSignal.asBinder());
                                return true;
                            case 25:
                                parcel.enforceInterface(IContentProvider.descriptor);
                                Uri uriCanonicalize = canonicalize(parcel.readString(), Uri.CREATOR.createFromParcel(parcel));
                                parcel2.writeNoException();
                                Uri.writeToParcel(parcel2, uriCanonicalize);
                                return true;
                            case 26:
                                parcel.enforceInterface(IContentProvider.descriptor);
                                Uri uriUncanonicalize = uncanonicalize(parcel.readString(), Uri.CREATOR.createFromParcel(parcel));
                                parcel2.writeNoException();
                                Uri.writeToParcel(parcel2, uriUncanonicalize);
                                return true;
                            default:
                                return super.onTransact(i, parcel, parcel2, i2);
                        }
                }
            }
            parcel.enforceInterface(IContentProvider.descriptor);
            int iUpdate = update(parcel.readString(), Uri.CREATOR.createFromParcel(parcel), ContentValues.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readStringArray());
            parcel2.writeNoException();
            parcel2.writeInt(iUpdate);
            return true;
        } catch (Exception e) {
            DatabaseUtils.writeExceptionToParcel(parcel2, e);
            return true;
        }
    }
}
