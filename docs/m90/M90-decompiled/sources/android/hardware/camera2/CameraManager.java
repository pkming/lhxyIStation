package android.hardware.camera2;

import android.content.Context;
import android.hardware.ICameraService;
import android.hardware.ICameraServiceListener;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.utils.BinderHolder;
import android.hardware.camera2.utils.CameraBinderDecorator;
import android.hardware.camera2.utils.CameraRuntimeException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class CameraManager {
    private static final String CAMERA_SERVICE_BINDER_NAME = "media.camera";
    private static final int USE_CALLING_UID = -1;
    private final ICameraService mCameraService;
    private final Context mContext;
    private ArrayList<String> mDeviceIdList;
    private final ArrayMap<AvailabilityListener, Handler> mListenerMap = new ArrayMap<>();
    private final Object mLock = new Object();

    public static abstract class AvailabilityListener {
        public void onCameraAvailable(String str) {
        }

        public void onCameraUnavailable(String str) {
        }
    }

    public CameraManager(Context context) {
        this.mContext = context;
        ICameraService iCameraService = (ICameraService) CameraBinderDecorator.newInstance(ICameraService.Stub.asInterface(ServiceManager.getService(CAMERA_SERVICE_BINDER_NAME)));
        this.mCameraService = iCameraService;
        try {
            iCameraService.addListener(new CameraServiceListener());
        } catch (CameraRuntimeException e) {
            throw new IllegalStateException("Failed to register a camera service listener", e.asChecked());
        } catch (RemoteException unused) {
        }
    }

    public String[] getCameraIdList() throws CameraAccessException {
        String[] strArr;
        synchronized (this.mLock) {
            try {
                try {
                    strArr = (String[]) getOrCreateDeviceIdListLocked().toArray(new String[0]);
                } catch (CameraAccessException e) {
                    throw new IllegalStateException("Failed to query camera service for device ID list", e);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return strArr;
    }

    public void addAvailabilityListener(AvailabilityListener availabilityListener, Handler handler) {
        if (handler == null) {
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper == null) {
                throw new IllegalArgumentException("No handler given, and current thread has no looper!");
            }
            handler = new Handler(looperMyLooper);
        }
        synchronized (this.mLock) {
            this.mListenerMap.put(availabilityListener, handler);
        }
    }

    public void removeAvailabilityListener(AvailabilityListener availabilityListener) {
        synchronized (this.mLock) {
            this.mListenerMap.remove(availabilityListener);
        }
    }

    public CameraCharacteristics getCameraCharacteristics(String str) throws CameraAccessException {
        synchronized (this.mLock) {
            if (!getOrCreateDeviceIdListLocked().contains(str)) {
                throw new IllegalArgumentException(String.format("Camera id %s does not match any currently connected camera device", str));
            }
        }
        CameraMetadataNative cameraMetadataNative = new CameraMetadataNative();
        try {
            this.mCameraService.getCameraCharacteristics(Integer.valueOf(str).intValue(), cameraMetadataNative);
            return new CameraCharacteristics(cameraMetadataNative);
        } catch (CameraRuntimeException e) {
            throw e.asChecked();
        } catch (RemoteException unused) {
            return null;
        }
    }

    private void openCameraDeviceUserAsync(String str, CameraDevice.StateListener stateListener, Handler handler) throws CameraAccessException {
        try {
            synchronized (this.mLock) {
                android.hardware.camera2.impl.CameraDevice cameraDevice = new android.hardware.camera2.impl.CameraDevice(str, stateListener, handler);
                BinderHolder binderHolder = new BinderHolder();
                this.mCameraService.connectDevice(cameraDevice.getCallbacks(), Integer.parseInt(str), this.mContext.getPackageName(), -1, binderHolder);
                cameraDevice.setRemoteDevice(ICameraDeviceUser.Stub.asInterface(binderHolder.getBinder()));
            }
        } catch (CameraRuntimeException e) {
            throw e.asChecked();
        } catch (RemoteException unused) {
        } catch (NumberFormatException unused2) {
            throw new IllegalArgumentException("Expected cameraId to be numeric, but it was: " + str);
        }
    }

    public void openCamera(String str, CameraDevice.StateListener stateListener, Handler handler) throws CameraAccessException {
        if (str == null) {
            throw new IllegalArgumentException("cameraId was null");
        }
        if (stateListener == null) {
            throw new IllegalArgumentException("listener was null");
        }
        if (handler == null) {
            if (Looper.myLooper() != null) {
                handler = new Handler();
            } else {
                throw new IllegalArgumentException("Looper doesn't exist in the calling thread");
            }
        }
        openCameraDeviceUserAsync(str, stateListener, handler);
    }

    private ArrayList<String> getOrCreateDeviceIdListLocked() throws CameraAccessException {
        boolean z;
        if (this.mDeviceIdList == null) {
            try {
                int numberOfCameras = this.mCameraService.getNumberOfCameras();
                this.mDeviceIdList = new ArrayList<>();
                CameraMetadataNative cameraMetadataNative = new CameraMetadataNative();
                for (int i = 0; i < numberOfCameras; i++) {
                    try {
                        this.mCameraService.getCameraCharacteristics(i, cameraMetadataNative);
                    } catch (CameraRuntimeException e) {
                        throw e.asChecked();
                    } catch (RemoteException | IllegalArgumentException unused) {
                        z = false;
                    }
                    if (cameraMetadataNative.isEmpty()) {
                        throw new AssertionError("Expected to get non-empty characteristics");
                    }
                    z = true;
                    if (z) {
                        this.mDeviceIdList.add(String.valueOf(i));
                    }
                }
            } catch (CameraRuntimeException e2) {
                throw e2.asChecked();
            } catch (RemoteException unused2) {
                return null;
            }
        }
        return this.mDeviceIdList;
    }

    private class CameraServiceListener extends ICameraServiceListener.Stub {
        public static final int STATUS_ENUMERATING = 2;
        public static final int STATUS_NOT_AVAILABLE = Integer.MIN_VALUE;
        public static final int STATUS_NOT_PRESENT = 0;
        public static final int STATUS_PRESENT = 1;
        private static final String TAG = "CameraServiceListener";
        private final ArrayMap<String, Integer> mDeviceStatus;

        private boolean isAvailable(int i) {
            return i == 1;
        }

        private boolean validStatus(int i) {
            return i == Integer.MIN_VALUE || i == 0 || i == 1 || i == 2;
        }

        @Override // android.hardware.ICameraServiceListener.Stub, android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        private CameraServiceListener() {
            this.mDeviceStatus = new ArrayMap<>();
        }

        @Override // android.hardware.ICameraServiceListener
        public void onStatusChanged(int i, int i2) throws RemoteException {
            synchronized (CameraManager.this.mLock) {
                Log.v(TAG, String.format("Camera id %d has status changed to 0x%x", Integer.valueOf(i2), Integer.valueOf(i)));
                final String strValueOf = String.valueOf(i2);
                if (!validStatus(i)) {
                    Log.e(TAG, String.format("Ignoring invalid device %d status 0x%x", Integer.valueOf(i2), Integer.valueOf(i)));
                    return;
                }
                Integer numPut = this.mDeviceStatus.put(strValueOf, Integer.valueOf(i));
                if (numPut != null && numPut.intValue() == i) {
                    Log.v(TAG, String.format("Device status changed to 0x%x, which is what it already was", Integer.valueOf(i)));
                    return;
                }
                if (numPut != null && isAvailable(i) == isAvailable(numPut.intValue())) {
                    Log.v(TAG, String.format("Device status was previously available (%d),  and is now again available (%d)so no new client visible update will be sent", Boolean.valueOf(isAvailable(i)), Boolean.valueOf(isAvailable(i))));
                    return;
                }
                int size = CameraManager.this.mListenerMap.size();
                for (int i3 = 0; i3 < size; i3++) {
                    Handler handler = (Handler) CameraManager.this.mListenerMap.valueAt(i3);
                    final AvailabilityListener availabilityListener = (AvailabilityListener) CameraManager.this.mListenerMap.keyAt(i3);
                    if (isAvailable(i)) {
                        handler.post(new Runnable() { // from class: android.hardware.camera2.CameraManager.CameraServiceListener.1
                            @Override // java.lang.Runnable
                            public void run() {
                                availabilityListener.onCameraAvailable(strValueOf);
                            }
                        });
                    } else {
                        handler.post(new Runnable() { // from class: android.hardware.camera2.CameraManager.CameraServiceListener.2
                            @Override // java.lang.Runnable
                            public void run() {
                                availabilityListener.onCameraUnavailable(strValueOf);
                            }
                        });
                    }
                }
            }
        }
    }
}
