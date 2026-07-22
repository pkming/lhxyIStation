package android.os;

import android.os.IBinder;
import android.os.IInterface;
import android.util.ArrayMap;

/* JADX INFO: loaded from: classes.dex */
public class RemoteCallbackList<E extends IInterface> {
    private Object[] mActiveBroadcast;
    ArrayMap<IBinder, RemoteCallbackList<E>.Callback> mCallbacks = new ArrayMap<>();
    private int mBroadcastCount = -1;
    private boolean mKilled = false;

    public void onCallbackDied(E e) {
    }

    private final class Callback implements IBinder.DeathRecipient {
        final E mCallback;
        final Object mCookie;

        Callback(E e, Object obj) {
            this.mCallback = e;
            this.mCookie = obj;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (RemoteCallbackList.this.mCallbacks) {
                RemoteCallbackList.this.mCallbacks.remove(this.mCallback.asBinder());
            }
            RemoteCallbackList.this.onCallbackDied(this.mCallback, this.mCookie);
        }
    }

    public boolean register(E e) {
        return register(e, null);
    }

    public boolean register(E e, Object obj) {
        synchronized (this.mCallbacks) {
            if (this.mKilled) {
                return false;
            }
            IBinder iBinderAsBinder = e.asBinder();
            try {
                RemoteCallbackList<E>.Callback callback = new Callback(e, obj);
                iBinderAsBinder.linkToDeath(callback, 0);
                this.mCallbacks.put(iBinderAsBinder, callback);
                return true;
            } catch (RemoteException unused) {
                return false;
            }
        }
    }

    public boolean unregister(E e) {
        synchronized (this.mCallbacks) {
            RemoteCallbackList<E>.Callback callbackRemove = this.mCallbacks.remove(e.asBinder());
            if (callbackRemove == null) {
                return false;
            }
            callbackRemove.mCallback.asBinder().unlinkToDeath(callbackRemove, 0);
            return true;
        }
    }

    public void kill() {
        synchronized (this.mCallbacks) {
            for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                RemoteCallbackList<E>.Callback callbackValueAt = this.mCallbacks.valueAt(size);
                callbackValueAt.mCallback.asBinder().unlinkToDeath(callbackValueAt, 0);
            }
            this.mCallbacks.clear();
            this.mKilled = true;
        }
    }

    public void onCallbackDied(E e, Object obj) {
        onCallbackDied(e);
    }

    public int beginBroadcast() {
        synchronized (this.mCallbacks) {
            if (this.mBroadcastCount > 0) {
                throw new IllegalStateException("beginBroadcast() called while already in a broadcast");
            }
            int size = this.mCallbacks.size();
            this.mBroadcastCount = size;
            if (size <= 0) {
                return 0;
            }
            Object[] objArr = this.mActiveBroadcast;
            if (objArr == null || objArr.length < size) {
                objArr = new Object[size];
                this.mActiveBroadcast = objArr;
            }
            for (int i = 0; i < size; i++) {
                objArr[i] = this.mCallbacks.valueAt(i);
            }
            return size;
        }
    }

    public E getBroadcastItem(int i) {
        return ((Callback) this.mActiveBroadcast[i]).mCallback;
    }

    public Object getBroadcastCookie(int i) {
        return ((Callback) this.mActiveBroadcast[i]).mCookie;
    }

    public void finishBroadcast() {
        int i = this.mBroadcastCount;
        if (i < 0) {
            throw new IllegalStateException("finishBroadcast() called outside of a broadcast");
        }
        Object[] objArr = this.mActiveBroadcast;
        if (objArr != null) {
            for (int i2 = 0; i2 < i; i2++) {
                objArr[i2] = null;
            }
        }
        this.mBroadcastCount = -1;
    }

    public int getRegisteredCallbackCount() {
        synchronized (this.mCallbacks) {
            if (this.mKilled) {
                return 0;
            }
            return this.mCallbacks.size();
        }
    }
}
