package android.net;

import android.util.Log;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class NetworkUtils {
    public static final int RESET_ALL_ADDRESSES = 3;
    public static final int RESET_IPV4_ADDRESSES = 1;
    public static final int RESET_IPV6_ADDRESSES = 2;
    private static final String TAG = "NetworkUtils";

    public static native int disableInterface(String str);

    public static native int enableInterface(String str);

    public static native String getDhcpError();

    public static native void markSocket(int i, int i2);

    public static native boolean releaseDhcpLease(String str);

    public static native int resetConnections(String str, int i);

    public static native boolean runDhcp(String str, DhcpResults dhcpResults);

    public static native boolean runDhcpRenew(String str, DhcpResults dhcpResults);

    public static native boolean stopDhcp(String str);

    public static InetAddress intToInetAddress(int i) {
        try {
            return InetAddress.getByAddress(new byte[]{(byte) (i & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255)});
        } catch (UnknownHostException unused) {
            throw new AssertionError();
        }
    }

    public static int inetAddressToInt(Inet4Address inet4Address) throws IllegalArgumentException {
        byte[] address = inet4Address.getAddress();
        return (address[0] & 255) | ((address[3] & 255) << 24) | ((address[2] & 255) << 16) | ((address[1] & 255) << 8);
    }

    public static int inetAddressToInt(InetAddress inetAddress) throws IllegalArgumentException {
        byte[] address = inetAddress.getAddress();
        if (address.length != 4) {
            throw new IllegalArgumentException("Not an IPv4 address");
        }
        return (address[0] & 255) | ((address[3] & 255) << 24) | ((address[2] & 255) << 16) | ((address[1] & 255) << 8);
    }

    public static int prefixLengthToNetmaskInt(int i) throws IllegalArgumentException {
        if (i < 0 || i > 32) {
            throw new IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)");
        }
        return Integer.reverseBytes((-1) << (32 - i));
    }

    public static int netmaskIntToPrefixLength(int i) {
        return Integer.bitCount(i);
    }

    public static InetAddress numericToInetAddress(String str) throws IllegalArgumentException {
        return InetAddress.parseNumericAddress(str);
    }

    public static InetAddress getNetworkPart(InetAddress inetAddress, int i) {
        if (inetAddress == null) {
            throw new RuntimeException("getNetworkPart doesn't accept null address");
        }
        byte[] address = inetAddress.getAddress();
        if (i < 0 || i > address.length * 8) {
            throw new RuntimeException("getNetworkPart - bad prefixLength");
        }
        int i2 = i / 8;
        byte b = (byte) (255 << (8 - (i % 8)));
        if (i2 < address.length) {
            address[i2] = (byte) (b & address[i2]);
        }
        while (true) {
            i2++;
            if (i2 < address.length) {
                address[i2] = 0;
            } else {
                try {
                    return InetAddress.getByAddress(address);
                } catch (UnknownHostException e) {
                    throw new RuntimeException("getNetworkPart error - " + e.toString());
                }
            }
        }
    }

    public static boolean addressTypeMatches(InetAddress inetAddress, InetAddress inetAddress2) {
        return ((inetAddress instanceof Inet4Address) && (inetAddress2 instanceof Inet4Address)) || ((inetAddress instanceof Inet6Address) && (inetAddress2 instanceof Inet6Address));
    }

    public static InetAddress hexToInet6Address(String str) throws IllegalArgumentException {
        try {
            return numericToInetAddress(String.format(Locale.US, "%s:%s:%s:%s:%s:%s:%s:%s", str.substring(0, 4), str.substring(4, 8), str.substring(8, 12), str.substring(12, 16), str.substring(16, 20), str.substring(20, 24), str.substring(24, 28), str.substring(28, 32)));
        } catch (Exception e) {
            Log.e(TAG, "error in hexToInet6Address(" + str + "): " + e);
            throw new IllegalArgumentException(e);
        }
    }

    public static String[] makeStrings(Collection<InetAddress> collection) {
        String[] strArr = new String[collection.size()];
        Iterator<InetAddress> it = collection.iterator();
        int i = 0;
        while (it.hasNext()) {
            strArr[i] = it.next().getHostAddress();
            i++;
        }
        return strArr;
    }

    public static String trimV4AddrZeros(String str) {
        if (str == null) {
            return null;
        }
        String[] strArrSplit = str.split("\\.");
        if (strArrSplit.length != 4) {
            return str;
        }
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 4; i++) {
            try {
                if (strArrSplit[i].length() > 3) {
                    return str;
                }
                sb.append(Integer.parseInt(strArrSplit[i]));
                if (i < 3) {
                    sb.append('.');
                }
            } catch (NumberFormatException unused) {
                return str;
            }
        }
        return sb.toString();
    }
}
