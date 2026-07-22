package android.net.dhcp;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
class DhcpInformPacket extends DhcpPacket {
    DhcpInformPacket(int i, InetAddress inetAddress, InetAddress inetAddress2, InetAddress inetAddress3, InetAddress inetAddress4, byte[] bArr) {
        super(i, inetAddress, inetAddress2, inetAddress3, inetAddress4, bArr, false);
    }

    @Override // android.net.dhcp.DhcpPacket
    public String toString() {
        return super.toString() + " INFORM";
    }

    @Override // android.net.dhcp.DhcpPacket
    public ByteBuffer buildPacket(int i, short s, short s2) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(1500);
        fillInPacket(i, this.mClientIp, this.mYourIp, s, s2, byteBufferAllocate, (byte) 1, false);
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
        addTlvEnd(byteBuffer);
    }

    @Override // android.net.dhcp.DhcpPacket
    public void doNextOp(DhcpStateMachine dhcpStateMachine) {
        dhcpStateMachine.onInformReceived(this.mTransId, this.mClientMac, this.mRequestedIp == null ? this.mClientIp : this.mRequestedIp, this.mRequestedParams);
    }
}
