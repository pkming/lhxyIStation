package android.net.dhcp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
class DhcpOfferPacket extends DhcpPacket {
    private final InetAddress mSrcIp;

    DhcpOfferPacket(int i, boolean z, InetAddress inetAddress, InetAddress inetAddress2, byte[] bArr) {
        super(i, Inet4Address.ANY, inetAddress2, Inet4Address.ANY, Inet4Address.ANY, bArr, z);
        this.mSrcIp = inetAddress;
    }

    @Override // android.net.dhcp.DhcpPacket
    public String toString() {
        String string = super.toString();
        String str = ", DNS servers: ";
        if (this.mDnsServers != null) {
            Iterator<InetAddress> it = this.mDnsServers.iterator();
            while (it.hasNext()) {
                str = str + it.next() + " ";
            }
        }
        return string + " OFFER, ip " + this.mYourIp + ", mask " + this.mSubnetMask + str + ", gateway " + this.mGateway + " lease time " + this.mLeaseTime + ", domain " + this.mDomainName;
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
        addTlv(byteBuffer, TarConstants.LF_DIR, (byte) 2);
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

    @Override // android.net.dhcp.DhcpPacket
    public void doNextOp(DhcpStateMachine dhcpStateMachine) {
        dhcpStateMachine.onOfferReceived(this.mBroadcast, this.mTransId, this.mClientMac, this.mYourIp, this.mServerIdentifier);
    }
}
