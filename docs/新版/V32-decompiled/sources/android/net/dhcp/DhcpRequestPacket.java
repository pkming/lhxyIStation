package android.net.dhcp;

import android.util.Log;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
class DhcpRequestPacket extends DhcpPacket {
    DhcpRequestPacket(int i, InetAddress inetAddress, byte[] bArr, boolean z) {
        super(i, inetAddress, Inet4Address.ANY, Inet4Address.ANY, Inet4Address.ANY, bArr, z);
    }

    @Override // android.net.dhcp.DhcpPacket
    public String toString() {
        return super.toString() + " REQUEST, desired IP " + this.mRequestedIp + " from host '" + this.mHostName + "', param list length " + (this.mRequestedParams == null ? 0 : this.mRequestedParams.length);
    }

    @Override // android.net.dhcp.DhcpPacket
    public ByteBuffer buildPacket(int i, short s, short s2) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(1500);
        fillInPacket(i, Inet4Address.ALL, Inet4Address.ANY, s, s2, byteBufferAllocate, (byte) 1, this.mBroadcast);
        byteBufferAllocate.flip();
        return byteBufferAllocate;
    }

    @Override // android.net.dhcp.DhcpPacket
    void finishPacket(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[7];
        bArr[0] = 1;
        System.arraycopy(this.mClientMac, 0, bArr, 1, 6);
        addTlv(byteBuffer, TarConstants.LF_DIR, (byte) 3);
        addTlv(byteBuffer, TarConstants.LF_CONTIG, this.mRequestedParams);
        addTlv(byteBuffer, TarConstants.LF_SYMLINK, this.mRequestedIp);
        addTlv(byteBuffer, TarConstants.LF_FIFO, this.mServerIdentifier);
        addTlv(byteBuffer, (byte) 61, bArr);
        addTlvEnd(byteBuffer);
    }

    @Override // android.net.dhcp.DhcpPacket
    public void doNextOp(DhcpStateMachine dhcpStateMachine) {
        InetAddress inetAddress = this.mRequestedIp == null ? this.mClientIp : this.mRequestedIp;
        Log.v("DhcpPacket", "requested IP is " + this.mRequestedIp + " and client IP is " + this.mClientIp);
        dhcpStateMachine.onRequestReceived(this.mBroadcast, this.mTransId, this.mClientMac, inetAddress, this.mRequestedParams, this.mHostName);
    }
}
