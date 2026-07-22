package android.os;

import android.os.IRemoteCallback;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public abstract class RemoteCallback implements Parcelable {
    public static final Parcelable.Creator<RemoteCallback> CREATOR = new Parcelable.Creator<RemoteCallback>() { // from class: android.os.RemoteCallback.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteCallback createFromParcel(Parcel parcel) {
            IBinder strongBinder = parcel.readStrongBinder();
            if (strongBinder != null) {
                return new RemoteCallbackProxy(IRemoteCallback.Stub.asInterface(strongBinder));
            }
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteCallback[] newArray(int i) {
            return new RemoteCallback[i];
        }
    };
    final Handler mHandler;
    final IRemoteCallback mTarget;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected abstract void onResult(Bundle bundle);

    class DeliverResult implements Runnable {
        final Bundle mResult;

        DeliverResult(Bundle bundle) {
            this.mResult = bundle;
        }

        @Override // java.lang.Runnable
        public void run() {
            RemoteCallback.this.onResult(this.mResult);
        }
    }

    class LocalCallback extends IRemoteCallback.Stub {
        LocalCallback() {
        }

        @Override // android.os.IRemoteCallback
        public void sendResult(Bundle bundle) {
            RemoteCallback.this.mHandler.post(RemoteCallback.this.new DeliverResult(bundle));
        }
    }

    static class RemoteCallbackProxy extends RemoteCallback {
        @Override // android.os.RemoteCallback
        protected void onResult(Bundle bundle) {
        }

        RemoteCallbackProxy(IRemoteCallback iRemoteCallback) {
            super(iRemoteCallback);
        }
    }

    public RemoteCallback(Handler handler) {
        this.mHandler = handler;
        this.mTarget = new LocalCallback();
    }

    RemoteCallback(IRemoteCallback iRemoteCallback) {
        this.mHandler = null;
        this.mTarget = iRemoteCallback;
    }

    public void sendResult(Bundle bundle) throws RemoteException {
        this.mTarget.sendResult(bundle);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            return this.mTarget.asBinder().equals(((RemoteCallback) obj).mTarget.asBinder());
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public int hashCode() {
        return this.mTarget.asBinder().hashCode();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.mTarget.asBinder());
    }
}
