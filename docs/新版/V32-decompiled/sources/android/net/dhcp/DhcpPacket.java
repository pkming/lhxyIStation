package android.net.dhcp;

import de.innosystec.unrar.rarfile.BaseBlock;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
abstract class DhcpPacket {
    protected static final byte CLIENT_ID_ETHER = 1;
    protected static final byte DHCP_BOOTREPLY = 2;
    protected static final byte DHCP_BOOTREQUEST = 1;
    protected static final byte DHCP_BROADCAST_ADDRESS = 28;
    static final short DHCP_CLIENT = 68;
    protected static final byte DHCP_CLIENT_IDENTIFIER = 61;
    protected static final byte DHCP_DNS_SERVER = 6;
    protected static final byte DHCP_DOMAIN_NAME = 15;
    protected static final byte DHCP_HOST_NAME = 12;
    protected static final byte DHCP_LEASE_TIME = 51;
    protected static final byte DHCP_MESSAGE = 56;
    protected static final byte DHCP_MESSAGE_TYPE = 53;
    protected static final byte DHCP_MESSAGE_TYPE_ACK = 5;
    protected static final byte DHCP_MESSAGE_TYPE_DECLINE = 4;
    protected static final byte DHCP_MESSAGE_TYPE_DISCOVER = 1;
    protected static final byte DHCP_MESSAGE_TYPE_INFORM = 8;
    protected static final byte DHCP_MESSAGE_TYPE_NAK = 6;
    protected static final byte DHCP_MESSAGE_TYPE_OFFER = 2;
    protected static final byte DHCP_MESSAGE_TYPE_REQUEST = 3;
    protected static final byte DHCP_PARAMETER_LIST = 55;
    protected static final byte DHCP_RENEWAL_TIME = 58;
    protected static final byte DHCP_REQUESTED_IP = 50;
    protected static final byte DHCP_ROUTER = 3;
    static final short DHCP_SERVER = 67;
    protected static final byte DHCP_SERVER_IDENTIFIER = 54;
    protected static final byte DHCP_SUBNET_MASK = 1;
    protected static final byte DHCP_VENDOR_CLASS_ID = 60;
    public static final int ENCAP_BOOTP = 2;
    public static final int ENCAP_L2 = 0;
    public static final int ENCAP_L3 = 1;
    private static final short IP_FLAGS_OFFSET = 16384;
    private static final byte IP_TOS_LOWDELAY = 16;
    private static final byte IP_TTL = 64;
    private static final byte IP_TYPE_UDP = 17;
    private static final byte IP_VERSION_HEADER_LEN = 69;
    protected static final int MAX_LENGTH = 1500;
    protected static final String TAG = "DhcpPacket";
    protected boolean mBroadcast;
    protected InetAddress mBroadcastAddress;
    protected final InetAddress mClientIp;
    protected final byte[] mClientMac;
    protected List<InetAddress> mDnsServers;
    protected String mDomainName;
    protected InetAddress mGateway;
    protected String mHostName;
    protected Integer mLeaseTime;
    protected String mMessage;
    private final InetAddress mNextIp;
    private final InetAddress mRelayIp;
    protected InetAddress mRequestedIp;
    protected byte[] mRequestedParams;
    protected InetAddress mServerIdentifier;
    protected InetAddress mSubnetMask;
    protected final int mTransId;
    protected final InetAddress mYourIp;

    private int intAbs(short s) {
        return s < 0 ? s + 65536 : s;
    }

    public abstract ByteBuffer buildPacket(int i, short s, short s2);

    public abstract void doNextOp(DhcpStateMachine dhcpStateMachine);

    abstract void finishPacket(ByteBuffer byteBuffer);

    protected DhcpPacket(int i, InetAddress inetAddress, InetAddress inetAddress2, InetAddress inetAddress3, InetAddress inetAddress4, byte[] bArr, boolean z) {
        this.mTransId = i;
        this.mClientIp = inetAddress;
        this.mYourIp = inetAddress2;
        this.mNextIp = inetAddress3;
        this.mRelayIp = inetAddress4;
        this.mClientMac = bArr;
        this.mBroadcast = z;
    }

    public int getTransactionId() {
        return this.mTransId;
    }

    protected void fillInPacket(int i, InetAddress inetAddress, InetAddress inetAddress2, short s, short s2, ByteBuffer byteBuffer, byte b, boolean z) {
        int iPosition;
        int iPosition2;
        int iPosition3;
        int iPosition4;
        int iPosition5;
        int iPosition6;
        byte[] address = inetAddress.getAddress();
        byte[] address2 = inetAddress2.getAddress();
        byteBuffer.clear();
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        if (i == 1) {
            byteBuffer.put(IP_VERSION_HEADER_LEN);
            byteBuffer.put((byte) 16);
            iPosition5 = byteBuffer.position();
            byteBuffer.putShort((short) 0);
            byteBuffer.putShort((short) 0);
            byteBuffer.putShort((short) 16384);
            byteBuffer.put((byte) 64);
            byteBuffer.put(IP_TYPE_UDP);
            iPosition6 = byteBuffer.position();
            byteBuffer.putShort((short) 0);
            byteBuffer.put(address2);
            byteBuffer.put(address);
            iPosition = byteBuffer.position();
            iPosition2 = byteBuffer.position();
            byteBuffer.putShort(s2);
            byteBuffer.putShort(s);
            iPosition3 = byteBuffer.position();
            byteBuffer.putShort((short) 0);
            iPosition4 = byteBuffer.position();
            byteBuffer.putShort((short) 0);
        } else {
            iPosition = 0;
            iPosition2 = 0;
            iPosition3 = 0;
            iPosition4 = 0;
            iPosition5 = 0;
            iPosition6 = 0;
        }
        byteBuffer.put(b);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) this.mClientMac.length);
        byteBuffer.put((byte) 0);
        byteBuffer.putInt(this.mTransId);
        byteBuffer.putShort((short) 0);
        if (z) {
            byteBuffer.putShort(BaseBlock.LONG_BLOCK);
        } else {
            byteBuffer.putShort((short) 0);
        }
        byteBuffer.put(this.mClientIp.getAddress());
        byteBuffer.put(this.mYourIp.getAddress());
        byteBuffer.put(this.mNextIp.getAddress());
        byteBuffer.put(this.mRelayIp.getAddress());
        byteBuffer.put(this.mClientMac);
        byteBuffer.position(byteBuffer.position() + (16 - this.mClientMac.length) + 64 + 128);
        byteBuffer.putInt(1669485411);
        finishPacket(byteBuffer);
        if ((byteBuffer.position() & 1) == 1) {
            byteBuffer.put((byte) 0);
        }
        if (i == 1) {
            short sPosition = (short) (byteBuffer.position() - iPosition2);
            byteBuffer.putShort(iPosition3, sPosition);
            byteBuffer.putShort(iPosition4, (short) checksum(byteBuffer, intAbs(byteBuffer.getShort(iPosition6 + 2)) + 0 + intAbs(byteBuffer.getShort(iPosition6 + 4)) + intAbs(byteBuffer.getShort(iPosition6 + 6)) + intAbs(byteBuffer.getShort(iPosition6 + 8)) + 17 + sPosition, iPosition2, byteBuffer.position()));
            byteBuffer.putShort(iPosition5, (short) byteBuffer.position());
            byteBuffer.putShort(iPosition6, (short) checksum(byteBuffer, 0, 0, iPosition));
        }
    }

    private int checksum(ByteBuffer byteBuffer, int i, int i2, int i3) {
        int iPosition = byteBuffer.position();
        byteBuffer.position(i2);
        ShortBuffer shortBufferAsShortBuffer = byteBuffer.asShortBuffer();
        byteBuffer.position(iPosition);
        int i4 = (i3 - i2) / 2;
        short[] sArr = new short[i4];
        shortBufferAsShortBuffer.get(sArr);
        for (int i5 = 0; i5 < i4; i5++) {
            i += intAbs(sArr[i5]);
        }
        int i6 = i2 + (i4 * 2);
        if (i3 != i6) {
            short s = byteBuffer.get(i6);
            if (s < 0) {
                s = (short) (s + 256);
            }
            i += s * 256;
        }
        int i7 = ((i >> 16) & 65535) + (i & 65535);
        return intAbs((short) (~((i7 + ((i7 >> 16) & 65535)) & 65535)));
    }

    protected void addTlv(ByteBuffer byteBuffer, byte b, byte b2) {
        byteBuffer.put(b);
        byteBuffer.put((byte) 1);
        byteBuffer.put(b2);
    }

    protected void addTlv(ByteBuffer byteBuffer, byte b, byte[] bArr) {
        if (bArr != null) {
            byteBuffer.put(b);
            byteBuffer.put((byte) bArr.length);
            byteBuffer.put(bArr);
        }
    }

    protected void addTlv(ByteBuffer byteBuffer, byte b, InetAddress inetAddress) {
        if (inetAddress != null) {
            addTlv(byteBuffer, b, inetAddress.getAddress());
        }
    }

    protected void addTlv(ByteBuffer byteBuffer, byte b, List<InetAddress> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        byteBuffer.put(b);
        byteBuffer.put((byte) (list.size() * 4));
        Iterator<InetAddress> it = list.iterator();
        while (it.hasNext()) {
            byteBuffer.put(it.next().getAddress());
        }
    }

    protected void addTlv(ByteBuffer byteBuffer, byte b, Integer num) {
        if (num != null) {
            byteBuffer.put(b);
            byteBuffer.put((byte) 4);
            byteBuffer.putInt(num.intValue());
        }
    }

    protected void addTlv(ByteBuffer byteBuffer, byte b, String str) {
        if (str != null) {
            byteBuffer.put(b);
            byteBuffer.put((byte) str.length());
            for (int i = 0; i < str.length(); i++) {
                byteBuffer.put((byte) str.charAt(i));
            }
        }
    }

    protected void addTlvEnd(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
    }

    public static String macToString(byte[] bArr) {
        String str = "";
        for (int i = 0; i < bArr.length; i++) {
            str = str + ("0" + Integer.toHexString(bArr[i])).substring(r2.length() - 2);
            if (i != bArr.length - 1) {
                str = str + ":";
            }
        }
        return str;
    }

    public String toString() {
        return macToString(this.mClientMac);
    }

    private static InetAddress readIpAddress(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[4];
        byteBuffer.get(bArr);
        try {
            return InetAddress.getByAddress(bArr);
        } catch (UnknownHostException unused) {
            return null;
        }
    }

    private static String readAsciiString(ByteBuffer byteBuffer, int i) {
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        return new String(bArr, 0, i, StandardCharsets.US_ASCII);
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0194 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:105:0x0196 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.net.dhcp.DhcpPacket decodeFullPacket(java.nio.ByteBuffer r27, int r28) {
        /*
            Method dump skipped, instruction units count: 582
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.dhcp.DhcpPacket.decodeFullPacket(java.nio.ByteBuffer, int):android.net.dhcp.DhcpPacket");
    }

    public static DhcpPacket decodeFullPacket(byte[] bArr, int i) {
        return decodeFullPacket(ByteBuffer.wrap(bArr).order(ByteOrder.BIG_ENDIAN), i);
    }

    public static ByteBuffer buildDiscoverPacket(int i, int i2, byte[] bArr, boolean z, byte[] bArr2) {
        DhcpDiscoverPacket dhcpDiscoverPacket = new DhcpDiscoverPacket(i2, bArr, z);
        dhcpDiscoverPacket.mRequestedParams = bArr2;
        return dhcpDiscoverPacket.buildPacket(i, (short) 67, (short) 68);
    }

    public static ByteBuffer buildOfferPacket(int i, int i2, boolean z, InetAddress inetAddress, InetAddress inetAddress2, byte[] bArr, Integer num, InetAddress inetAddress3, InetAddress inetAddress4, InetAddress inetAddress5, List<InetAddress> list, InetAddress inetAddress6, String str) {
        DhcpOfferPacket dhcpOfferPacket = new DhcpOfferPacket(i2, z, inetAddress, inetAddress2, bArr);
        dhcpOfferPacket.mGateway = inetAddress5;
        dhcpOfferPacket.mDnsServers = list;
        dhcpOfferPacket.mLeaseTime = num;
        dhcpOfferPacket.mDomainName = str;
        dhcpOfferPacket.mServerIdentifier = inetAddress6;
        dhcpOfferPacket.mSubnetMask = inetAddress3;
        dhcpOfferPacket.mBroadcastAddress = inetAddress4;
        return dhcpOfferPacket.buildPacket(i, (short) 68, (short) 67);
    }

    public static ByteBuffer buildAckPacket(int i, int i2, boolean z, InetAddress inetAddress, InetAddress inetAddress2, byte[] bArr, Integer num, InetAddress inetAddress3, InetAddress inetAddress4, InetAddress inetAddress5, List<InetAddress> list, InetAddress inetAddress6, String str) {
        DhcpAckPacket dhcpAckPacket = new DhcpAckPacket(i2, z, inetAddress, inetAddress2, bArr);
        dhcpAckPacket.mGateway = inetAddress5;
        dhcpAckPacket.mDnsServers = list;
        dhcpAckPacket.mLeaseTime = num;
        dhcpAckPacket.mDomainName = str;
        dhcpAckPacket.mSubnetMask = inetAddress3;
        dhcpAckPacket.mServerIdentifier = inetAddress6;
        dhcpAckPacket.mBroadcastAddress = inetAddress4;
        return dhcpAckPacket.buildPacket(i, (short) 68, (short) 67);
    }

    public static ByteBuffer buildNakPacket(int i, int i2, InetAddress inetAddress, InetAddress inetAddress2, byte[] bArr) {
        DhcpNakPacket dhcpNakPacket = new DhcpNakPacket(i2, inetAddress2, inetAddress, inetAddress, inetAddress, bArr);
        dhcpNakPacket.mMessage = "requested address not available";
        dhcpNakPacket.mRequestedIp = inetAddress2;
        return dhcpNakPacket.buildPacket(i, (short) 68, (short) 67);
    }

    public static ByteBuffer buildRequestPacket(int i, int i2, InetAddress inetAddress, boolean z, byte[] bArr, InetAddress inetAddress2, InetAddress inetAddress3, byte[] bArr2, String str) {
        DhcpRequestPacket dhcpRequestPacket = new DhcpRequestPacket(i2, inetAddress, bArr, z);
        dhcpRequestPacket.mRequestedIp = inetAddress2;
        dhcpRequestPacket.mServerIdentifier = inetAddress3;
        dhcpRequestPacket.mHostName = str;
        dhcpRequestPacket.mRequestedParams = bArr2;
        return dhcpRequestPacket.buildPacket(i, (short) 67, (short) 68);
    }
}
