package android.hardware.usb;

import android.util.Log;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class UsbRequest {
    private static final String TAG = "UsbRequest";
    private ByteBuffer mBuffer;
    private Object mClientData;
    private UsbEndpoint mEndpoint;
    private int mLength;
    private int mNativeContext;

    private native boolean native_cancel();

    private native void native_close();

    private native int native_dequeue_array(byte[] bArr, int i, boolean z);

    private native int native_dequeue_direct();

    private native boolean native_init(UsbDeviceConnection usbDeviceConnection, int i, int i2, int i3, int i4);

    private native boolean native_queue_array(byte[] bArr, int i, boolean z);

    private native boolean native_queue_direct(ByteBuffer byteBuffer, int i, boolean z);

    public boolean initialize(UsbDeviceConnection usbDeviceConnection, UsbEndpoint usbEndpoint) {
        this.mEndpoint = usbEndpoint;
        return native_init(usbDeviceConnection, usbEndpoint.getAddress(), usbEndpoint.getAttributes(), usbEndpoint.getMaxPacketSize(), usbEndpoint.getInterval());
    }

    public void close() {
        this.mEndpoint = null;
        native_close();
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mEndpoint != null) {
                Log.v(TAG, "endpoint still open in finalize(): " + this);
                close();
            }
        } finally {
            super.finalize();
        }
    }

    public UsbEndpoint getEndpoint() {
        return this.mEndpoint;
    }

    public Object getClientData() {
        return this.mClientData;
    }

    public void setClientData(Object obj) {
        this.mClientData = obj;
    }

    public boolean queue(ByteBuffer byteBuffer, int i) {
        boolean zNative_queue_array;
        boolean z = this.mEndpoint.getDirection() == 0;
        if (byteBuffer.isDirect()) {
            zNative_queue_array = native_queue_direct(byteBuffer, i, z);
        } else if (byteBuffer.hasArray()) {
            zNative_queue_array = native_queue_array(byteBuffer.array(), i, z);
        } else {
            throw new IllegalArgumentException("buffer is not direct and has no array");
        }
        if (zNative_queue_array) {
            this.mBuffer = byteBuffer;
            this.mLength = i;
        }
        return zNative_queue_array;
    }

    void dequeue() {
        int iNative_dequeue_array;
        boolean z = this.mEndpoint.getDirection() == 0;
        if (this.mBuffer.isDirect()) {
            iNative_dequeue_array = native_dequeue_direct();
        } else {
            iNative_dequeue_array = native_dequeue_array(this.mBuffer.array(), this.mLength, z);
        }
        if (iNative_dequeue_array >= 0) {
            this.mBuffer.position(Math.min(iNative_dequeue_array, this.mLength));
        }
        this.mBuffer = null;
        this.mLength = 0;
    }

    public boolean cancel() {
        return native_cancel();
    }
}
