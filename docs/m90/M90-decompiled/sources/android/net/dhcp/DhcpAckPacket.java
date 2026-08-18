package android.net.dhcp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
class DhcpAckPacket extends DhcpPacket {
    private final InetAddress mSrcIp;

    DhcpAckPacket(int i, boolean z, InetAddress inetAddress, InetAddress inetAddress2, byte[] bArr) {
        super(i, Inet4Address.ANY, inetAddress2, inetAddress, Inet4Address.ANY, bArr, z);
        this.mBroadcast = z;
        this.mSrcIp = inetAddress;
    }

    @Override // android.net.dhcp.DhcpPacket
    public String toString() {
        String string = super.toString();
        Iterator<InetAddress> it = this.mDnsServers.iterator();
        String str = " DNS servers: ";
        while (it.hasNext()) {
            str = str + it.next().toString() + " ";
        }
        return string + " ACK: your new IP " + this.mYourIp + ", netmask " + this.mSubnetMask + ", gateway " + this.mGateway + str + ", lease time " + this.mLeaseTime;
    }

    @Override // android.net.dhcp.DhcpPacket
    public ByteBuffer buildPacket(int i, short s, short s2) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(1500);
        fillInPacket(i, this.mBroadcast ? Inet4Address.ALL : this.mYourIp, this.mBroadcast ? Inet4Address.ANY : this.mSrcIp, s, s2, byteBufferAllocate, (byte) 2, this.mBroadcast);
        byteBufferAllocate.flip();
        return byteBufferAllocate;
    }

    @Override // android.net.dhcp.DhcpPacket
    void finishPacket(ByteBuffer byteBuffer) {
        addTlv(byteBuffer, TarConstants.LF_DIR, (byte) 5);
        addTlv(byteBuffer, TarConstants.LF_FIFO, this.mServerIdentifier);
        addTlv(byteBuffer, TarConstants.LF_CHR, this.mLeaseTime);
        if (this.mLeaseTime != null) {
            addTlv(byteBuffer, Ref3DPtg.sid, Integer.valueOf(this.mLeaseTime.intValue() / 2));
        }
        addTlv(byteBuffer, (byte) 1, this.mSubnetMask);
        addTlv(byteBuffer, (byte) 3, this.mGateway);
        addTlv(byteBuffer, HSSFErrorConstants.ERROR_VALUE, this.mDomainName);
        addTlv(byteBuffer, (byte) 28, this.mBroadcastAddress);
        addTlv(byteBuffer, (byte) 6, this.mDnsServers);
        addTlvEnd(byteBuffer);
    }

    private static final int getInt(Integer num) {
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    @Override // android.net.dhcp.DhcpPacket
    public void doNextOp(DhcpStateMachine dhcpStateMachine) {
        dhcpStateMachine.onAckReceived(this.mYourIp, this.mSubnetMask, this.mGateway, this.mDnsServers, this.mServerIdentifier, getInt(this.mLeaseTime));
    }
}
