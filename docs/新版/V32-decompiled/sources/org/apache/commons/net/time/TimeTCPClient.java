package org.apache.commons.net.time;

import android.widget.ExpandableListView;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.net.SocketClient;

/* JADX INFO: loaded from: classes3.dex */
public final class TimeTCPClient extends SocketClient {
    public static final int DEFAULT_PORT = 37;
    public static final long SECONDS_1900_TO_1970 = 2208988800L;

    public TimeTCPClient() {
        setDefaultPort(37);
    }

    public long getTime() throws IOException {
        return ((long) new DataInputStream(this._input_).readInt()) & ExpandableListView.PACKED_POSITION_VALUE_NULL;
    }

    public Date getDate() throws IOException {
        return new Date((getTime() - 2208988800L) * 1000);
    }
}
