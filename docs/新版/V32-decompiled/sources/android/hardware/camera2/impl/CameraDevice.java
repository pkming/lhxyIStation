package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.utils.CameraBinderDecorator;
import android.hardware.camera2.utils.CameraRuntimeException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CameraDevice implements android.hardware.camera2.CameraDevice {
    private static final int REQUEST_ID_NONE = -1;
    private final boolean DEBUG;
    private final String TAG;
    private final String mCameraId;
    private final Handler mDeviceHandler;
    private final CameraDevice.StateListener mDeviceListener;
    private ICameraDeviceUser mRemoteDevice;
    private final Object mLock = new Object();
    private final CameraDeviceCallbacks mCallbacks = new CameraDeviceCallbacks();
    private boolean mIdle = true;
    private final SparseArray<CaptureListenerHolder> mCaptureListenerMap = new SparseArray<>();
    private int mRepeatingRequestId = -1;
    private final ArrayList<Integer> mRepeatingRequestIdDeletedList = new ArrayList<>();
    private final SparseArray<Surface> mConfiguredOutputs = new SparseArray<>();
    private final Runnable mCallOnOpened = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.1
        @Override // java.lang.Runnable
        public void run() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            CameraDevice.this.mDeviceListener.onOpened(CameraDevice.this);
        }
    };
    private final Runnable mCallOnUnconfigured = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.2
        @Override // java.lang.Runnable
        public void run() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            CameraDevice.this.mDeviceListener.onUnconfigured(CameraDevice.this);
        }
    };
    private final Runnable mCallOnActive = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.3
        @Override // java.lang.Runnable
        public void run() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            CameraDevice.this.mDeviceListener.onActive(CameraDevice.this);
        }
    };
    private final Runnable mCallOnBusy = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.4
        @Override // java.lang.Runnable
        public void run() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            CameraDevice.this.mDeviceListener.onBusy(CameraDevice.this);
        }
    };
    private final Runnable mCallOnClosed = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.5
        @Override // java.lang.Runnable
        public void run() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            CameraDevice.this.mDeviceListener.onClosed(CameraDevice.this);
        }
    };
    private final Runnable mCallOnIdle = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.6
        @Override // java.lang.Runnable
        public void run() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            CameraDevice.this.mDeviceListener.onIdle(CameraDevice.this);
        }
    };
    private final Runnable mCallOnDisconnected = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.7
        @Override // java.lang.Runnable
        public void run() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            CameraDevice.this.mDeviceListener.onDisconnected(CameraDevice.this);
        }
    };

    public CameraDevice(String str, CameraDevice.StateListener stateListener, Handler handler) {
        if (str == null || stateListener == null || handler == null) {
            throw new IllegalArgumentException("Null argument given");
        }
        this.mCameraId = str;
        this.mDeviceListener = stateListener;
        this.mDeviceHandler = handler;
        String str2 = String.format("CameraDevice-%s-JV", str);
        this.TAG = str2;
        this.DEBUG = Log.isLoggable(str2, 3);
    }

    public CameraDeviceCallbacks getCallbacks() {
        return this.mCallbacks;
    }

    public void setRemoteDevice(ICameraDeviceUser iCameraDeviceUser) {
        synchronized (this.mLock) {
            this.mRemoteDevice = (ICameraDeviceUser) CameraBinderDecorator.newInstance(iCameraDeviceUser);
            this.mDeviceHandler.post(this.mCallOnOpened);
            this.mDeviceHandler.post(this.mCallOnUnconfigured);
        }
    }

    @Override // android.hardware.camera2.CameraDevice
    public String getId() {
        return this.mCameraId;
    }

    @Override // android.hardware.camera2.CameraDevice
    public void configureOutputs(List<Surface> list) throws CameraAccessException {
        if (list == null) {
            list = new ArrayList<>();
        }
        synchronized (this.mLock) {
            checkIfCameraClosed();
            HashSet<Surface> hashSet = new HashSet(list);
            ArrayList<Integer> arrayList = new ArrayList();
            for (int i = 0; i < this.mConfiguredOutputs.size(); i++) {
                int iKeyAt = this.mConfiguredOutputs.keyAt(i);
                Surface surfaceValueAt = this.mConfiguredOutputs.valueAt(i);
                if (!list.contains(surfaceValueAt)) {
                    arrayList.add(Integer.valueOf(iKeyAt));
                } else {
                    hashSet.remove(surfaceValueAt);
                }
            }
            this.mDeviceHandler.post(this.mCallOnBusy);
            stopRepeating();
            try {
                waitUntilIdle();
                for (Integer num : arrayList) {
                    this.mRemoteDevice.deleteStream(num.intValue());
                    this.mConfiguredOutputs.delete(num.intValue());
                }
                for (Surface surface : hashSet) {
                    this.mConfiguredOutputs.put(this.mRemoteDevice.createStream(0, 0, 0, surface), surface);
                }
                if (list.size() > 0) {
                    this.mDeviceHandler.post(this.mCallOnIdle);
                } else {
                    this.mDeviceHandler.post(this.mCallOnUnconfigured);
                }
            } catch (CameraRuntimeException e) {
                if (e.getReason() == 4) {
                    throw new IllegalStateException("The camera is currently busy. You must wait until the previous operation completes.");
                }
                throw e.asChecked();
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // android.hardware.camera2.CameraDevice
    public CaptureRequest.Builder createCaptureRequest(int i) throws CameraAccessException {
        CaptureRequest.Builder builder;
        synchronized (this.mLock) {
            checkIfCameraClosed();
            CameraMetadataNative cameraMetadataNative = new CameraMetadataNative();
            try {
                try {
                    this.mRemoteDevice.createDefaultRequest(i, cameraMetadataNative);
                    builder = new CaptureRequest.Builder(cameraMetadataNative);
                } catch (CameraRuntimeException e) {
                    throw e.asChecked();
                }
            } catch (RemoteException unused) {
                return null;
            }
        }
        return builder;
    }

    @Override // android.hardware.camera2.CameraDevice
    public int capture(CaptureRequest captureRequest, CameraDevice.CaptureListener captureListener, Handler handler) throws CameraAccessException {
        return submitCaptureRequest(captureRequest, captureListener, handler, false);
    }

    @Override // android.hardware.camera2.CameraDevice
    public int captureBurst(List<CaptureRequest> list, CameraDevice.CaptureListener captureListener, Handler handler) throws CameraAccessException {
        if (list.isEmpty()) {
            Log.w(this.TAG, "Capture burst request list is empty, do nothing!");
            return -1;
        }
        throw new UnsupportedOperationException("Burst capture implemented yet");
    }

    private int submitCaptureRequest(CaptureRequest captureRequest, CameraDevice.CaptureListener captureListener, Handler handler, boolean z) throws CameraAccessException {
        int iSubmitRequest;
        if (captureListener != null) {
            handler = checkHandler(handler);
        }
        synchronized (this.mLock) {
            checkIfCameraClosed();
            if (z) {
                stopRepeating();
            }
            try {
                try {
                    iSubmitRequest = this.mRemoteDevice.submitRequest(captureRequest, z);
                    if (captureListener != null) {
                        this.mCaptureListenerMap.put(iSubmitRequest, new CaptureListenerHolder(captureListener, captureRequest, handler, z));
                    }
                    if (z) {
                        this.mRepeatingRequestId = iSubmitRequest;
                    }
                    if (this.mIdle) {
                        this.mDeviceHandler.post(this.mCallOnActive);
                    }
                    this.mIdle = false;
                } catch (CameraRuntimeException e) {
                    throw e.asChecked();
                }
            } catch (RemoteException unused) {
                return -1;
            }
        }
        return iSubmitRequest;
    }

    @Override // android.hardware.camera2.CameraDevice
    public int setRepeatingRequest(CaptureRequest captureRequest, CameraDevice.CaptureListener captureListener, Handler handler) throws CameraAccessException {
        return submitCaptureRequest(captureRequest, captureListener, handler, true);
    }

    @Override // android.hardware.camera2.CameraDevice
    public int setRepeatingBurst(List<CaptureRequest> list, CameraDevice.CaptureListener captureListener, Handler handler) throws CameraAccessException {
        if (list.isEmpty()) {
            Log.w(this.TAG, "Set Repeating burst request list is empty, do nothing!");
            return -1;
        }
        throw new UnsupportedOperationException("Burst capture implemented yet");
    }

    @Override // android.hardware.camera2.CameraDevice
    public void stopRepeating() throws CameraAccessException {
        synchronized (this.mLock) {
            checkIfCameraClosed();
            int i = this.mRepeatingRequestId;
            if (i != -1) {
                this.mRepeatingRequestId = -1;
                this.mRepeatingRequestIdDeletedList.add(Integer.valueOf(i));
                try {
                    this.mRemoteDevice.cancelRequest(i);
                } catch (CameraRuntimeException e) {
                    throw e.asChecked();
                } catch (RemoteException unused) {
                }
            }
        }
    }

    @Override // android.hardware.camera2.CameraDevice
    public void waitUntilIdle() throws CameraAccessException {
        synchronized (this.mLock) {
            checkIfCameraClosed();
            if (this.mRepeatingRequestId != -1) {
                throw new IllegalStateException("Active repeating request ongoing");
            }
            try {
                this.mRemoteDevice.waitUntilIdle();
                this.mRepeatingRequestId = -1;
                this.mRepeatingRequestIdDeletedList.clear();
                this.mCaptureListenerMap.clear();
            } catch (CameraRuntimeException e) {
                throw e.asChecked();
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // android.hardware.camera2.CameraDevice
    public void flush() throws CameraAccessException {
        synchronized (this.mLock) {
            checkIfCameraClosed();
            this.mDeviceHandler.post(this.mCallOnBusy);
            try {
                try {
                    this.mRemoteDevice.flush();
                } catch (CameraRuntimeException e) {
                    throw e.asChecked();
                }
            } catch (RemoteException unused) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x001d A[Catch: all -> 0x000b, TryCatch #2 {, blocks: (B:4:0x0003, B:6:0x0007, B:11:0x000e, B:12:0x0019, B:14:0x001d, B:15:0x0024, B:16:0x0027), top: B:21:0x0003, inners: #3 }] */
    @Override // android.hardware.camera2.CameraDevice, java.lang.AutoCloseable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void close() {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            android.hardware.camera2.ICameraDeviceUser r1 = r4.mRemoteDevice     // Catch: java.lang.Throwable -> Lb android.hardware.camera2.utils.CameraRuntimeException -> Ld android.os.RemoteException -> L19
            if (r1 == 0) goto L19
            r1.disconnect()     // Catch: java.lang.Throwable -> Lb android.hardware.camera2.utils.CameraRuntimeException -> Ld android.os.RemoteException -> L19
            goto L19
        Lb:
            r1 = move-exception
            goto L29
        Ld:
            r1 = move-exception
            java.lang.String r2 = r4.TAG     // Catch: java.lang.Throwable -> Lb
            java.lang.String r3 = "Exception while closing: "
            android.hardware.camera2.CameraAccessException r1 = r1.asChecked()     // Catch: java.lang.Throwable -> Lb
            android.util.Log.e(r2, r3, r1)     // Catch: java.lang.Throwable -> Lb
        L19:
            android.hardware.camera2.ICameraDeviceUser r1 = r4.mRemoteDevice     // Catch: java.lang.Throwable -> Lb
            if (r1 == 0) goto L24
            android.os.Handler r1 = r4.mDeviceHandler     // Catch: java.lang.Throwable -> Lb
            java.lang.Runnable r2 = r4.mCallOnClosed     // Catch: java.lang.Throwable -> Lb
            r1.post(r2)     // Catch: java.lang.Throwable -> Lb
        L24:
            r1 = 0
            r4.mRemoteDevice = r1     // Catch: java.lang.Throwable -> Lb
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb
            return
        L29:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.camera2.impl.CameraDevice.close():void");
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    static class CaptureListenerHolder {
        private final Handler mHandler;
        private final CameraDevice.CaptureListener mListener;
        private final boolean mRepeating;
        private final CaptureRequest mRequest;

        CaptureListenerHolder(CameraDevice.CaptureListener captureListener, CaptureRequest captureRequest, Handler handler, boolean z) {
            if (captureListener == null || handler == null) {
                throw new UnsupportedOperationException("Must have a valid handler and a valid listener");
            }
            this.mRepeating = z;
            this.mHandler = handler;
            this.mRequest = captureRequest;
            this.mListener = captureListener;
        }

        public boolean isRepeating() {
            return this.mRepeating;
        }

        public CameraDevice.CaptureListener getListener() {
            return this.mListener;
        }

        public CaptureRequest getRequest() {
            return this.mRequest;
        }

        public Handler getHandler() {
            return this.mHandler;
        }
    }

    public class CameraDeviceCallbacks extends ICameraDeviceCallbacks.Stub {
        static final int ERROR_CAMERA_DEVICE = 1;
        static final int ERROR_CAMERA_DISCONNECTED = 0;
        static final int ERROR_CAMERA_SERVICE = 2;

        @Override // android.hardware.camera2.ICameraDeviceCallbacks.Stub, android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public CameraDeviceCallbacks() {
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onCameraError(final int i) {
            Runnable runnable;
            if (CameraDevice.this.isClosed()) {
                return;
            }
            synchronized (CameraDevice.this.mLock) {
                try {
                    if (i == 0) {
                        runnable = CameraDevice.this.mCallOnDisconnected;
                    } else {
                        if (i != 1 && i != 2) {
                            Log.e(CameraDevice.this.TAG, "Unknown error from camera device: " + i);
                        }
                        runnable = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.CameraDeviceCallbacks.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (CameraDevice.this.isClosed()) {
                                    return;
                                }
                                CameraDevice.this.mDeviceListener.onError(CameraDevice.this, i);
                            }
                        };
                    }
                    CameraDevice.this.mDeviceHandler.post(runnable);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onCameraIdle() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            if (CameraDevice.this.DEBUG) {
                Log.d(CameraDevice.this.TAG, "Camera now idle");
            }
            synchronized (CameraDevice.this.mLock) {
                if (!CameraDevice.this.mIdle) {
                    CameraDevice.this.mDeviceHandler.post(CameraDevice.this.mCallOnIdle);
                }
                CameraDevice.this.mIdle = true;
            }
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onCaptureStarted(int i, final long j) {
            final CaptureListenerHolder captureListenerHolder;
            if (CameraDevice.this.DEBUG) {
                Log.d(CameraDevice.this.TAG, "Capture started for id " + i);
            }
            synchronized (CameraDevice.this.mLock) {
                captureListenerHolder = (CaptureListenerHolder) CameraDevice.this.mCaptureListenerMap.get(i);
            }
            if (captureListenerHolder == null || CameraDevice.this.isClosed()) {
                return;
            }
            captureListenerHolder.getHandler().post(new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.CameraDeviceCallbacks.2
                @Override // java.lang.Runnable
                public void run() {
                    if (CameraDevice.this.isClosed()) {
                        return;
                    }
                    captureListenerHolder.getListener().onCaptureStarted(CameraDevice.this, captureListenerHolder.getRequest(), j);
                }
            });
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onResultReceived(int i, CameraMetadataNative cameraMetadataNative) throws RemoteException {
            final CaptureListenerHolder captureListenerHolder;
            Runnable runnable;
            if (CameraDevice.this.DEBUG) {
                Log.d(CameraDevice.this.TAG, "Received result for id " + i);
            }
            Boolean bool = (Boolean) cameraMetadataNative.get(CaptureResult.QUIRKS_PARTIAL_RESULT);
            boolean z = bool != null && bool.booleanValue();
            synchronized (CameraDevice.this.mLock) {
                captureListenerHolder = (CaptureListenerHolder) CameraDevice.this.mCaptureListenerMap.get(i);
                if (captureListenerHolder != null && !captureListenerHolder.isRepeating() && !z) {
                    CameraDevice.this.mCaptureListenerMap.remove(i);
                }
                if (captureListenerHolder != null && captureListenerHolder.isRepeating() && !z && CameraDevice.this.mRepeatingRequestIdDeletedList.size() > 0) {
                    Iterator it = CameraDevice.this.mRepeatingRequestIdDeletedList.iterator();
                    while (it.hasNext()) {
                        int iIntValue = ((Integer) it.next()).intValue();
                        if (iIntValue < i) {
                            CameraDevice.this.mCaptureListenerMap.remove(iIntValue);
                            it.remove();
                        }
                    }
                }
            }
            if (captureListenerHolder == null || CameraDevice.this.isClosed()) {
                return;
            }
            final CaptureRequest request = captureListenerHolder.getRequest();
            final CaptureResult captureResult = new CaptureResult(cameraMetadataNative, request, i);
            if (z) {
                runnable = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.CameraDeviceCallbacks.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (CameraDevice.this.isClosed()) {
                            return;
                        }
                        captureListenerHolder.getListener().onCapturePartial(CameraDevice.this, request, captureResult);
                    }
                };
            } else {
                runnable = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.CameraDeviceCallbacks.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (CameraDevice.this.isClosed()) {
                            return;
                        }
                        captureListenerHolder.getListener().onCaptureCompleted(CameraDevice.this, request, captureResult);
                    }
                };
            }
            captureListenerHolder.getHandler().post(runnable);
        }
    }

    private Handler checkHandler(Handler handler) {
        if (handler != null) {
            return handler;
        }
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper == null) {
            throw new IllegalArgumentException("No handler given, and current thread has no looper!");
        }
        return new Handler(looperMyLooper);
    }

    private void checkIfCameraClosed() {
        if (this.mRemoteDevice == null) {
            throw new IllegalStateException("CameraDevice was already closed");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isClosed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mRemoteDevice == null;
        }
        return z;
    }
}
