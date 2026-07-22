package android.bluetooth;

import android.os.Handler;
import android.os.ParcelUuid;
import java.io.Closeable;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothServerSocket implements Closeable {
    private final int mChannel;
    private Handler mHandler;
    private int mMessage;
    final BluetoothSocket mSocket;

    BluetoothServerSocket(int i, boolean z, boolean z2, int i2) throws IOException {
        this.mChannel = i2;
        this.mSocket = new BluetoothSocket(i, -1, z, z2, null, i2, null);
    }

    BluetoothServerSocket(int i, boolean z, boolean z2, ParcelUuid parcelUuid) throws IOException {
        BluetoothSocket bluetoothSocket = new BluetoothSocket(i, -1, z, z2, null, -1, parcelUuid);
        this.mSocket = bluetoothSocket;
        this.mChannel = bluetoothSocket.getPort();
    }

    public BluetoothSocket accept() throws IOException {
        return accept(-1);
    }

    public BluetoothSocket accept(int i) throws IOException {
        return this.mSocket.accept(i);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        synchronized (this) {
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.obtainMessage(this.mMessage).sendToTarget();
            }
        }
        this.mSocket.close();
    }

    synchronized void setCloseHandler(Handler handler, int i) {
        this.mHandler = handler;
        this.mMessage = i;
    }

    void setServiceName(String str) {
        this.mSocket.setServiceName(str);
    }

    public int getChannel() {
        return this.mChannel;
    }
}
