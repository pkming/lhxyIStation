package android.net;

import android.util.Log;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class LinkSocket extends Socket {
    private static final boolean DBG = true;
    private static final String TAG = "LinkSocket";

    public LinkSocket() {
        log("LinkSocket() EX");
    }

    public LinkSocket(LinkSocketNotifier linkSocketNotifier) {
        log("LinkSocket(notifier) EX");
    }

    public LinkSocket(LinkSocketNotifier linkSocketNotifier, Proxy proxy) {
        log("LinkSocket(notifier, proxy) EX");
    }

    public LinkProperties getLinkProperties() {
        log("LinkProperties() EX");
        return new LinkProperties();
    }

    public boolean setNeededCapabilities(LinkCapabilities linkCapabilities) {
        log("setNeeds() EX");
        return false;
    }

    public LinkCapabilities getNeededCapabilities() {
        log("getNeeds() EX");
        return null;
    }

    public LinkCapabilities getCapabilities() {
        log("getCapabilities() EX");
        return null;
    }

    public LinkCapabilities getCapabilities(Set<Integer> set) {
        log("getCapabilities(capabilities) EX");
        return new LinkCapabilities();
    }

    public void setTrackedCapabilities(Set<Integer> set) {
        log("setTrackedCapabilities(capabilities) EX");
    }

    public Set<Integer> getTrackedCapabilities() {
        log("getTrackedCapabilities(capabilities) EX");
        return new HashSet();
    }

    public void connect(String str, int i, int i2) throws IOException {
        log("connect(dstName, dstPort, timeout) EX");
    }

    public void connect(String str, int i) throws IOException {
        log("connect(dstName, dstPort, timeout) EX");
    }

    @Override // java.net.Socket
    @Deprecated
    public void connect(SocketAddress socketAddress, int i) throws IOException {
        log("connect(remoteAddr, timeout) EX DEPRECATED");
    }

    @Override // java.net.Socket
    @Deprecated
    public void connect(SocketAddress socketAddress) throws IOException {
        log("connect(remoteAddr) EX DEPRECATED");
    }

    public void connect(int i) throws IOException {
        log("connect(timeout) EX");
    }

    public void connect() throws IOException {
        log("connect() EX");
    }

    @Override // java.net.Socket, java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        log("close() EX");
    }

    public void requestNewLink(LinkRequestReason linkRequestReason) {
        log("requestNewLink(linkRequestReason) EX");
    }

    @Override // java.net.Socket
    @Deprecated
    public void bind(SocketAddress socketAddress) throws UnsupportedOperationException {
        log("bind(localAddr) EX throws IOException");
        throw new UnsupportedOperationException("bind is deprecated for LinkSocket");
    }

    public static final class LinkRequestReason {
        public static final int LINK_PROBLEM_NONE = 0;
        public static final int LINK_PROBLEM_UNKNOWN = 1;

        private LinkRequestReason() {
        }
    }

    protected static void log(String str) {
        Log.d(TAG, str);
    }
}
