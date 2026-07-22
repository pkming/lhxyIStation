package org.apache.commons.net.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes3.dex */
public class SubnetUtils {
    private static final int NBITS = 32;
    private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final Pattern addressPattern = Pattern.compile(IP_ADDRESS);
    private static final String SLASH_FORMAT = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})/(\\d{1,3})";
    private static final Pattern cidrPattern = Pattern.compile(SLASH_FORMAT);
    private int netmask = 0;
    private int address = 0;
    private int network = 0;
    private int broadcast = 0;
    private boolean inclusiveHostCount = false;

    int pop(int i) {
        int i2 = i - ((i >>> 1) & 1431655765);
        int i3 = (i2 & 858993459) + ((i2 >>> 2) & 858993459);
        int i4 = 252645135 & (i3 + (i3 >>> 4));
        int i5 = i4 + (i4 >>> 8);
        return (i5 + (i5 >>> 16)) & 63;
    }

    public SubnetUtils(String str) {
        calculate(str);
    }

    public SubnetUtils(String str, String str2) {
        calculate(toCidrNotation(str, str2));
    }

    public boolean isInclusiveHostCount() {
        return this.inclusiveHostCount;
    }

    public void setInclusiveHostCount(boolean z) {
        this.inclusiveHostCount = z;
    }

    public final class SubnetInfo {
        private SubnetInfo() {
        }

        private int netmask() {
            return SubnetUtils.this.netmask;
        }

        private int network() {
            return SubnetUtils.this.network;
        }

        private int address() {
            return SubnetUtils.this.address;
        }

        private int broadcast() {
            return SubnetUtils.this.broadcast;
        }

        private int low() {
            if (SubnetUtils.this.isInclusiveHostCount()) {
                return network();
            }
            if (broadcast() - network() > 1) {
                return network() + 1;
            }
            return 0;
        }

        private int high() {
            if (SubnetUtils.this.isInclusiveHostCount()) {
                return broadcast();
            }
            if (broadcast() - network() > 1) {
                return broadcast() - 1;
            }
            return 0;
        }

        public boolean isInRange(String str) {
            return isInRange(SubnetUtils.this.toInteger(str));
        }

        private boolean isInRange(int i) {
            int iLow = i - low();
            return iLow >= 0 && iLow <= high() - low();
        }

        public String getBroadcastAddress() {
            SubnetUtils subnetUtils = SubnetUtils.this;
            return subnetUtils.format(subnetUtils.toArray(broadcast()));
        }

        public String getNetworkAddress() {
            SubnetUtils subnetUtils = SubnetUtils.this;
            return subnetUtils.format(subnetUtils.toArray(network()));
        }

        public String getNetmask() {
            SubnetUtils subnetUtils = SubnetUtils.this;
            return subnetUtils.format(subnetUtils.toArray(netmask()));
        }

        public String getAddress() {
            SubnetUtils subnetUtils = SubnetUtils.this;
            return subnetUtils.format(subnetUtils.toArray(address()));
        }

        public String getLowAddress() {
            SubnetUtils subnetUtils = SubnetUtils.this;
            return subnetUtils.format(subnetUtils.toArray(low()));
        }

        public String getHighAddress() {
            SubnetUtils subnetUtils = SubnetUtils.this;
            return subnetUtils.format(subnetUtils.toArray(high()));
        }

        public int getAddressCount() {
            int iBroadcast = (broadcast() - network()) + (SubnetUtils.this.isInclusiveHostCount() ? 1 : -1);
            if (iBroadcast < 0) {
                return 0;
            }
            return iBroadcast;
        }

        public int asInteger(String str) {
            return SubnetUtils.this.toInteger(str);
        }

        public String getCidrSignature() {
            SubnetUtils subnetUtils = SubnetUtils.this;
            String str = subnetUtils.format(subnetUtils.toArray(address()));
            SubnetUtils subnetUtils2 = SubnetUtils.this;
            return subnetUtils.toCidrNotation(str, subnetUtils2.format(subnetUtils2.toArray(netmask())));
        }

        public String[] getAllAddresses() {
            int addressCount = getAddressCount();
            String[] strArr = new String[addressCount];
            if (addressCount == 0) {
                return strArr;
            }
            int iLow = low();
            int i = 0;
            while (iLow <= high()) {
                SubnetUtils subnetUtils = SubnetUtils.this;
                strArr[i] = subnetUtils.format(subnetUtils.toArray(iLow));
                iLow++;
                i++;
            }
            return strArr;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CIDR Signature:\t[").append(getCidrSignature()).append("]").append(" Netmask: [").append(getNetmask()).append("]\n").append("Network:\t[").append(getNetworkAddress()).append("]\n").append("Broadcast:\t[").append(getBroadcastAddress()).append("]\n").append("First Address:\t[").append(getLowAddress()).append("]\n").append("Last Address:\t[").append(getHighAddress()).append("]\n").append("# Addresses:\t[").append(getAddressCount()).append("]\n");
            return sb.toString();
        }
    }

    public final SubnetInfo getInfo() {
        return new SubnetInfo();
    }

    private void calculate(String str) {
        Matcher matcher = cidrPattern.matcher(str);
        if (matcher.matches()) {
            this.address = matchAddress(matcher);
            int iRangeCheck = rangeCheck(Integer.parseInt(matcher.group(5)), 0, 32);
            for (int i = 0; i < iRangeCheck; i++) {
                this.netmask |= 1 << (31 - i);
            }
            int i2 = this.address;
            int i3 = this.netmask;
            int i4 = i2 & i3;
            this.network = i4;
            this.broadcast = i4 | (~i3);
            return;
        }
        throw new IllegalArgumentException("Could not parse [" + str + "]");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int toInteger(String str) {
        Matcher matcher = addressPattern.matcher(str);
        if (matcher.matches()) {
            return matchAddress(matcher);
        }
        throw new IllegalArgumentException("Could not parse [" + str + "]");
    }

    private int matchAddress(Matcher matcher) {
        int iRangeCheck = 0;
        for (int i = 1; i <= 4; i++) {
            iRangeCheck |= (rangeCheck(Integer.parseInt(matcher.group(i)), -1, 255) & 255) << ((4 - i) * 8);
        }
        return iRangeCheck;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] toArray(int i) {
        int[] iArr = new int[4];
        for (int i2 = 3; i2 >= 0; i2--) {
            iArr[i2] = iArr[i2] | ((i >>> ((3 - i2) * 8)) & 255);
        }
        return iArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String format(int[] iArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iArr.length; i++) {
            sb.append(iArr[i]);
            if (i != iArr.length - 1) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    private int rangeCheck(int i, int i2, int i3) {
        if (i <= i2 || i > i3) {
            throw new IllegalArgumentException("Value [" + i + "] not in range (" + i2 + "," + i3 + "]");
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String toCidrNotation(String str, String str2) {
        return str + "/" + pop(toInteger(str2));
    }
}
