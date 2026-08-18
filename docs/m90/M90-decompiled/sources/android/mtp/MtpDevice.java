package android.mtp;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

/* JADX INFO: loaded from: classes.dex */
public final class MtpDevice {
    private static final String TAG = "MtpDevice";
    private final UsbDevice mDevice;
    private int mNativeContext;

    private native void native_close();

    private native boolean native_delete_object(int i);

    private native MtpDeviceInfo native_get_device_info();

    private native byte[] native_get_object(int i, int i2);

    private native int[] native_get_object_handles(int i, int i2, int i3);

    private native MtpObjectInfo native_get_object_info(int i);

    private native long native_get_parent(int i);

    private native long native_get_storage_id(int i);

    private native int[] native_get_storage_ids();

    private native MtpStorageInfo native_get_storage_info(int i);

    private native byte[] native_get_thumbnail(int i);

    private native boolean native_import_file(int i, String str);

    private native boolean native_open(String str, int i);

    static {
        System.loadLibrary("media_jni");
    }

    public MtpDevice(UsbDevice usbDevice) {
        this.mDevice = usbDevice;
    }

    public boolean open(UsbDeviceConnection usbDeviceConnection) {
        boolean zNative_open = native_open(this.mDevice.getDeviceName(), usbDeviceConnection.getFileDescriptor());
        if (!zNative_open) {
            usbDeviceConnection.close();
        }
        return zNative_open;
    }

    public void close() {
        native_close();
    }

    protected void finalize() throws Throwable {
        try {
            native_close();
        } finally {
            super.finalize();
        }
    }

    public String getDeviceName() {
        return this.mDevice.getDeviceName();
    }

    public int getDeviceId() {
        return this.mDevice.getDeviceId();
    }

    public String toString() {
        return this.mDevice.getDeviceName();
    }

    public MtpDeviceInfo getDeviceInfo() {
        return native_get_device_info();
    }

    public int[] getStorageIds() {
        return native_get_storage_ids();
    }

    public int[] getObjectHandles(int i, int i2, int i3) {
        return native_get_object_handles(i, i2, i3);
    }

    public byte[] getObject(int i, int i2) {
        return native_get_object(i, i2);
    }

    public byte[] getThumbnail(int i) {
        return native_get_thumbnail(i);
    }

    public MtpStorageInfo getStorageInfo(int i) {
        return native_get_storage_info(i);
    }

    public MtpObjectInfo getObjectInfo(int i) {
        return native_get_object_info(i);
    }

    public boolean deleteObject(int i) {
        return native_delete_object(i);
    }

    public long getParent(int i) {
        return native_get_parent(i);
    }

    public long getStorageId(int i) {
        return native_get_storage_id(i);
    }

    public boolean importFile(int i, String str) {
        return native_import_file(i, str);
    }
}
