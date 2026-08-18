package android.net.dhcp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
class DhcpDiscoverPacket extends DhcpPacket {
    DhcpDiscoverPacket(int i, byte[] bArr, boolean z) {
        super(i, Inet4Address.ANY, Inet4Address.ANY, Inet4Address.ANY, Inet4Address.ANY, bArr, z);
    }

    @Override // android.net.dhcp.DhcpPacket
    public String toString() {
        return super.toString() + " DISCOVER " + (this.mBroadcast ? "broadcast " : "unicast ");
    }

    @Override // android.net.dhcp.DhcpPacket
    public ByteBuffer buildPacket(int i, short s, short s2) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(1500);
        InetAddress inetAddress = Inet4Address.ALL;
        fillInPacket(i, Inet4Address.ALL, Inet4Address.ANY, s, s2, byteBufferAllocate, (byte) 1, true);
        byteBufferAllocate.flip();
        return byteBufferAllocate;
    }

    @Override // android.net.dhcp.DhcpPacket
    void finishPacket(ByteBuffer byteBuffer) {
        addTlv(byteBuffer, TarConstants.LF_DIR, (byte) 1);
        addTlv(byteBuffer, TarConstants.LF_CONTIG, this.mRequestedParams);
        addTlvEnd(byteBuffer);
    }

    @Override // android.net.dhcp.DhcpPacket
    public void doNextOp(DhcpStateMachine dhcpStateMachine) {
        dhcpStateMachine.onDiscoverReceived(this.mBroadcast, this.mTransId, this.mClientMac, this.mRequestedParams);
    }
}
