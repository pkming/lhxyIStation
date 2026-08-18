package android.hardware.usb;

import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;

/* JADX INFO: loaded from: classes.dex */
public class UsbDeviceConnection {
    private static final String TAG = "UsbDeviceConnection";
    private final UsbDevice mDevice;
    private int mNativeContext;

    private native int native_bulk_request(int i, byte[] bArr, int i2, int i3, int i4);

    private native boolean native_claim_interface(int i, boolean z);

    private native void native_close();

    private native int native_control_request(int i, int i2, int i3, int i4, byte[] bArr, int i5, int i6, int i7);

    private native byte[] native_get_desc();

    private native int native_get_fd();

    private native String native_get_serial();

    private native boolean native_open(String str, FileDescriptor fileDescriptor);

    private native boolean native_release_interface(int i);

    private native UsbRequest native_request_wait();

    public UsbDeviceConnection(UsbDevice usbDevice) {
        this.mDevice = usbDevice;
    }

    boolean open(String str, ParcelFileDescriptor parcelFileDescriptor) {
        return native_open(str, parcelFileDescriptor.getFileDescriptor());
    }

    public void close() {
        native_close();
    }

    public int getFileDescriptor() {
        return native_get_fd();
    }

    public byte[] getRawDescriptors() {
        return native_get_desc();
    }

    public boolean claimInterface(UsbInterface usbInterface, boolean z) {
        return native_claim_interface(usbInterface.getId(), z);
    }

    public boolean releaseInterface(UsbInterface usbInterface) {
        return native_release_interface(usbInterface.getId());
    }

    public int controlTransfer(int i, int i2, int i3, int i4, byte[] bArr, int i5, int i6) {
        return controlTransfer(i, i2, i3, i4, bArr, 0, i5, i6);
    }

    public int controlTransfer(int i, int i2, int i3, int i4, byte[] bArr, int i5, int i6, int i7) {
        checkBounds(bArr, i5, i6);
        return native_control_request(i, i2, i3, i4, bArr, i5, i6, i7);
    }

    public int bulkTransfer(UsbEndpoint usbEndpoint, byte[] bArr, int i, int i2) {
        return bulkTransfer(usbEndpoint, bArr, 0, i, i2);
    }

    public int bulkTransfer(UsbEndpoint usbEndpoint, byte[] bArr, int i, int i2, int i3) {
        checkBounds(bArr, i, i2);
        return native_bulk_request(usbEndpoint.getAddress(), bArr, i, i2, i3);
    }

    public UsbRequest requestWait() {
        UsbRequest usbRequestNative_request_wait = native_request_wait();
        if (usbRequestNative_request_wait != null) {
            usbRequestNative_request_wait.dequeue();
        }
        return usbRequestNative_request_wait;
    }

    public String getSerial() {
        return native_get_serial();
    }

    private static void checkBounds(byte[] bArr, int i, int i2) {
        int length = bArr != null ? bArr.length : 0;
        if (i < 0 || i + i2 > length) {
            throw new IllegalArgumentException("Buffer start or length out of bounds.");
        }
    }
}
