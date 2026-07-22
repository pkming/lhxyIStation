package org.apache.commons.net.time;

import android.widget.ExpandableListView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import org.apache.commons.net.DatagramSocketClient;

/* JADX INFO: loaded from: classes3.dex */
public final class TimeUDPClient extends DatagramSocketClient {
    public static final int DEFAULT_PORT = 37;
    public static final long SECONDS_1900_TO_1970 = 2208988800L;
    private final byte[] __dummyData = new byte[1];
    private final byte[] __timeData = new byte[4];

    public long getTime(InetAddress inetAddress, int i) throws IOException {
        byte[] bArr = this.__dummyData;
        DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length, inetAddress, i);
        byte[] bArr2 = this.__timeData;
        DatagramPacket datagramPacket2 = new DatagramPacket(bArr2, bArr2.length);
        this._socket_.send(datagramPacket);
        this._socket_.receive(datagramPacket2);
        byte[] bArr3 = this.__timeData;
        return (((long) (bArr3[3] & 255)) & ExpandableListView.PACKED_POSITION_VALUE_NULL) | (((long) ((bArr3[0] & 255) << 24)) & ExpandableListView.PACKED_POSITION_VALUE_NULL) | 0 | (((long) ((bArr3[1] & 255) << 16)) & ExpandableListView.PACKED_POSITION_VALUE_NULL) | (((long) ((bArr3[2] & 255) << 8)) & ExpandableListView.PACKED_POSITION_VALUE_NULL);
    }

    public long getTime(InetAddress inetAddress) throws IOException {
        return getTime(inetAddress, 37);
    }

    public Date getDate(InetAddress inetAddress, int i) throws IOException {
        return new Date((getTime(inetAddress, i) - 2208988800L) * 1000);
    }

    public Date getDate(InetAddress inetAddress) throws IOException {
        return new Date((getTime(inetAddress, 37) - 2208988800L) * 1000);
    }
}
