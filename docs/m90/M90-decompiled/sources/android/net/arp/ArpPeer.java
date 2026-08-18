package android.net.arp;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.os.SystemClock;
import android.util.Log;
import de.innosystec.unrar.rarfile.BaseBlock;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import libcore.net.RawSocket;

/* JADX INFO: loaded from: classes.dex */
public class ArpPeer {
    private static final int ARP_LENGTH = 28;
    private static final boolean DBG = false;
    private static final int ETHERNET_TYPE = 1;
    private static final int IPV4_LENGTH = 4;
    private static final int MAC_ADDR_LENGTH = 6;
    private static final int MAX_LENGTH = 1500;
    private static final String TAG = "ArpPeer";
    private final byte[] L2_BROADCAST;
    private String mInterfaceName;
    private final InetAddress mMyAddr;
    private final byte[] mMyMac = new byte[6];
    private final InetAddress mPeer;
    private final RawSocket mSocket;

    public ArpPeer(String str, InetAddress inetAddress, String str2, InetAddress inetAddress2) throws SocketException {
        this.mInterfaceName = str;
        this.mMyAddr = inetAddress;
        if (str2 != null) {
            for (int i = 0; i < 6; i++) {
                int i2 = i * 3;
                this.mMyMac[i] = (byte) Integer.parseInt(str2.substring(i2, i2 + 2), 16);
            }
        }
        if ((inetAddress instanceof Inet6Address) || (inetAddress2 instanceof Inet6Address)) {
            throw new IllegalArgumentException("IPv6 unsupported");
        }
        this.mPeer = inetAddress2;
        byte[] bArr = new byte[6];
        this.L2_BROADCAST = bArr;
        Arrays.fill(bArr, (byte) -1);
        this.mSocket = new RawSocket(this.mInterfaceName, (short) 2054);
    }

    public byte[] doArp(int i) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(MAX_LENGTH);
        byte[] address = this.mPeer.getAddress();
        long jElapsedRealtime = SystemClock.elapsedRealtime() + ((long) i);
        byteBufferAllocate.clear();
        byteBufferAllocate.order(ByteOrder.BIG_ENDIAN);
        byteBufferAllocate.putShort((short) 1);
        byteBufferAllocate.putShort(BaseBlock.LHD_VERSION);
        byteBufferAllocate.put((byte) 6);
        byteBufferAllocate.put((byte) 4);
        byteBufferAllocate.putShort((short) 1);
        byteBufferAllocate.put(this.mMyMac);
        byteBufferAllocate.put(this.mMyAddr.getAddress());
        byteBufferAllocate.put(new byte[6]);
        byteBufferAllocate.put(address);
        byteBufferAllocate.flip();
        this.mSocket.write(this.L2_BROADCAST, byteBufferAllocate.array(), 0, byteBufferAllocate.limit());
        byte[] bArr = new byte[MAX_LENGTH];
        while (SystemClock.elapsedRealtime() < jElapsedRealtime) {
            if (this.mSocket.read(bArr, 0, MAX_LENGTH, -1, (int) (jElapsedRealtime - SystemClock.elapsedRealtime())) >= 28 && bArr[0] == 0 && bArr[1] == 1 && bArr[2] == 8 && bArr[3] == 0 && bArr[4] == 6 && bArr[5] == 4 && bArr[6] == 0 && bArr[7] == 2 && bArr[14] == address[0] && bArr[15] == address[1] && bArr[16] == address[2] && bArr[17] == address[3]) {
                byte[] bArr2 = new byte[6];
                System.arraycopy(bArr, 8, bArr2, 0, 6);
                return bArr2;
            }
        }
        return null;
    }

    public static boolean doArp(String str, LinkProperties linkProperties, int i, int i2, int i3) {
        String interfaceName = linkProperties.getInterfaceName();
        Iterator<LinkAddress> it = linkProperties.getLinkAddresses().iterator();
        InetAddress address = it.hasNext() ? it.next().getAddress() : null;
        Iterator<RouteInfo> it2 = linkProperties.getRoutes().iterator();
        try {
            ArpPeer arpPeer = new ArpPeer(interfaceName, address, str, it2.hasNext() ? it2.next().getGateway() : null);
            int i4 = 0;
            for (int i5 = 0; i5 < i2; i5++) {
                if (arpPeer.doArp(i) != null) {
                    i4++;
                }
            }
            boolean z = i4 >= i3;
            arpPeer.close();
            return z;
        } catch (SocketException e) {
            Log.e(TAG, "ARP test initiation failure: " + e);
            return true;
        }
    }

    public void close() {
        try {
            this.mSocket.close();
        } catch (IOException unused) {
        }
    }
}
