package android.net.dhcp;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
class DhcpDeclinePacket extends DhcpPacket {
    @Override // android.net.dhcp.DhcpPacket
    void finishPacket(ByteBuffer byteBuffer) {
    }

    DhcpDeclinePacket(int i, InetAddress inetAddress, InetAddress inetAddress2, InetAddress inetAddress3, InetAddress inetAddress4, byte[] bArr) {
        super(i, inetAddress, inetAddress2, inetAddress3, inetAddress4, bArr, false);
    }

    @Override // android.net.dhcp.DhcpPacket
    public String toString() {
        return super.toString() + " DECLINE";
    }

    @Override // android.net.dhcp.DhcpPacket
    public ByteBuffer buildPacket(int i, short s, short s2) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(1500);
        fillInPacket(i, this.mClientIp, this.mYourIp, s, s2, byteBufferAllocate, (byte) 1, false);
        byteBufferAllocate.flip();
        return byteBufferAllocate;
    }

    @Override // android.net.dhcp.DhcpPacket
    public void doNextOp(DhcpStateMachine dhcpStateMachine) {
        dhcpStateMachine.onDeclineReceived(this.mClientMac, this.mRequestedIp);
    }
}
