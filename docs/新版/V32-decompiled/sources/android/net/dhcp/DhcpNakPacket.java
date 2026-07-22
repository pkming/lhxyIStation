package android.net.dhcp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
class DhcpNakPacket extends DhcpPacket {
    DhcpNakPacket(int i, InetAddress inetAddress, InetAddress inetAddress2, InetAddress inetAddress3, InetAddress inetAddress4, byte[] bArr) {
        super(i, Inet4Address.ANY, Inet4Address.ANY, inetAddress3, inetAddress4, bArr, false);
    }

    @Override // android.net.dhcp.DhcpPacket
    public String toString() {
        return super.toString() + " NAK, reason " + (this.mMessage == null ? "(none)" : this.mMessage);
    }

    @Override // android.net.dhcp.DhcpPacket
    public ByteBuffer buildPacket(int i, short s, short s2) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(1500);
        fillInPacket(i, this.mClientIp, this.mYourIp, s, s2, byteBufferAllocate, (byte) 2, this.mBroadcast);
        byteBufferAllocate.flip();
        return byteBufferAllocate;
    }

    @Override // android.net.dhcp.DhcpPacket
    void finishPacket(ByteBuffer byteBuffer) {
        addTlv(byteBuffer, TarConstants.LF_DIR, (byte) 6);
        addTlv(byteBuffer, TarConstants.LF_FIFO, this.mServerIdentifier);
        addTlv(byteBuffer, PaletteRecord.STANDARD_PALETTE_SIZE, this.mMessage);
        addTlvEnd(byteBuffer);
    }

    @Override // android.net.dhcp.DhcpPacket
    public void doNextOp(DhcpStateMachine dhcpStateMachine) {
        dhcpStateMachine.onNakReceived();
    }
}
