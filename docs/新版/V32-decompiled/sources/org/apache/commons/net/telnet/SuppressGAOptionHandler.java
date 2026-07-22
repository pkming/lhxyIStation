package org.apache.commons.net.telnet;

/* JADX INFO: loaded from: classes3.dex */
public class SuppressGAOptionHandler extends TelnetOptionHandler {
    @Override // org.apache.commons.net.telnet.TelnetOptionHandler
    public int[] answerSubnegotiation(int[] iArr, int i) {
        return null;
    }

    @Override // org.apache.commons.net.telnet.TelnetOptionHandler
    public int[] startSubnegotiationLocal() {
        return null;
    }

    @Override // org.apache.commons.net.telnet.TelnetOptionHandler
    public int[] startSubnegotiationRemote() {
        return null;
    }

    public SuppressGAOptionHandler(boolean z, boolean z2, boolean z3, boolean z4) {
        super(3, z, z2, z3, z4);
    }

    public SuppressGAOptionHandler() {
        super(3, false, false, false, false);
    }
}
