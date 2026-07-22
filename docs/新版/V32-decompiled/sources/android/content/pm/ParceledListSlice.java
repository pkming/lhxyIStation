package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ParceledListSlice<T extends Parcelable> implements Parcelable {
    public static final Parcelable.ClassLoaderCreator<ParceledListSlice> CREATOR = new Parcelable.ClassLoaderCreator<ParceledListSlice>() { // from class: android.content.pm.ParceledListSlice.2
        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Parcelable.Creator
        public ParceledListSlice createFromParcel(Parcel parcel) {
            return new ParceledListSlice(parcel, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.ClassLoaderCreator
        public ParceledListSlice createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new ParceledListSlice(parcel, classLoader);
        }

        @Override // android.os.Parcelable.Creator
        public ParceledListSlice[] newArray(int i) {
            return new ParceledListSlice[i];
        }
    };
    private static boolean DEBUG = false;
    private static final int MAX_FIRST_IPC_SIZE = 131072;
    private static final int MAX_IPC_SIZE = 262144;
    private static String TAG = "ParceledListSlice";
    private final List<T> mList;

    public ParceledListSlice(List<T> list) {
        this.mList = list;
    }

    private ParceledListSlice(Parcel parcel, ClassLoader classLoader) {
        int i = parcel.readInt();
        this.mList = new ArrayList(i);
        if (DEBUG) {
            Log.d(TAG, "Retrieving " + i + " items");
        }
        if (i <= 0) {
            return;
        }
        Parcelable.Creator<T> parcelableCreator = parcel.readParcelableCreator(classLoader);
        int i2 = 0;
        while (i2 < i && parcel.readInt() != 0) {
            this.mList.add((T) parcel.readCreator(parcelableCreator, classLoader));
            if (DEBUG) {
                String str = TAG;
                StringBuilder sbAppend = new StringBuilder().append("Read inline #").append(i2).append(": ");
                List<T> list = this.mList;
                Log.d(str, sbAppend.append(list.get(list.size() - 1)).toString());
            }
            i2++;
        }
        if (i2 >= i) {
            return;
        }
        IBinder strongBinder = parcel.readStrongBinder();
        while (i2 < i) {
            if (DEBUG) {
                Log.d(TAG, "Reading more @" + i2 + " of " + i + ": retriever=" + strongBinder);
            }
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            parcelObtain.writeInt(i2);
            try {
                strongBinder.transact(1, parcelObtain, parcelObtain2, 0);
                while (i2 < i && parcelObtain2.readInt() != 0) {
                    this.mList.add((T) parcelObtain2.readCreator(parcelableCreator, classLoader));
                    if (DEBUG) {
                        String str2 = TAG;
                        StringBuilder sbAppend2 = new StringBuilder().append("Read extra #").append(i2).append(": ");
                        List<T> list2 = this.mList;
                        Log.d(str2, sbAppend2.append(list2.get(list2.size() - 1)).toString());
                    }
                    i2++;
                }
                parcelObtain2.recycle();
                parcelObtain.recycle();
            } catch (RemoteException e) {
                Log.w(TAG, "Failure retrieving array; only received " + i2 + " of " + i, e);
                return;
            }
        }
    }

    public List<T> getList() {
        return this.mList;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        int iDescribeContents = 0;
        for (int i = 0; i < this.mList.size(); i++) {
            iDescribeContents |= this.mList.get(i).describeContents();
        }
        return iDescribeContents;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, final int i) {
        final int size = this.mList.size();
        parcel.writeInt(size);
        if (DEBUG) {
            Log.d(TAG, "Writing " + size + " items");
        }
        if (size > 0) {
            parcel.writeParcelableCreator(this.mList.get(0));
            int i2 = 0;
            while (i2 < size && parcel.dataSize() < 131072) {
                parcel.writeInt(1);
                this.mList.get(i2).writeToParcel(parcel, i);
                if (DEBUG) {
                    Log.d(TAG, "Wrote inline #" + i2 + ": " + this.mList.get(i2));
                }
                i2++;
            }
            if (i2 < size) {
                parcel.writeInt(0);
                Binder binder = new Binder() { // from class: android.content.pm.ParceledListSlice.1
                    @Override // android.os.Binder
                    protected boolean onTransact(int i3, Parcel parcel2, Parcel parcel3, int i4) throws RemoteException {
                        if (i3 != 1) {
                            return super.onTransact(i3, parcel2, parcel3, i4);
                        }
                        int i5 = parcel2.readInt();
                        if (ParceledListSlice.DEBUG) {
                            Log.d(ParceledListSlice.TAG, "Writing more @" + i5 + " of " + size);
                        }
                        while (i5 < size && parcel3.dataSize() < 262144) {
                            parcel3.writeInt(1);
                            ((Parcelable) ParceledListSlice.this.mList.get(i5)).writeToParcel(parcel3, i);
                            if (ParceledListSlice.DEBUG) {
                                Log.d(ParceledListSlice.TAG, "Wrote extra #" + i5 + ": " + ParceledListSlice.this.mList.get(i5));
                            }
                            i5++;
                        }
                        if (i5 < size) {
                            if (ParceledListSlice.DEBUG) {
                                Log.d(ParceledListSlice.TAG, "Breaking @" + i5 + " of " + size);
                            }
                            parcel3.writeInt(0);
                        }
                        return true;
                    }
                };
                if (DEBUG) {
                    Log.d(TAG, "Breaking @" + i2 + " of " + size + ": retriever=" + binder);
                }
                parcel.writeStrongBinder(binder);
            }
        }
    }
}
