package android.net.wifi;

/* JADX INFO: loaded from: classes.dex */
class NetworkUpdateResult {
    boolean ipChanged;
    boolean isNewNetwork;
    int netId;
    boolean proxyChanged;

    public NetworkUpdateResult(int i) {
        this.isNewNetwork = false;
        this.netId = i;
        this.ipChanged = false;
        this.proxyChanged = false;
    }

    public NetworkUpdateResult(boolean z, boolean z2) {
        this.isNewNetwork = false;
        this.netId = -1;
        this.ipChanged = z;
        this.proxyChanged = z2;
    }

    public void setNetworkId(int i) {
        this.netId = i;
    }

    public int getNetworkId() {
        return this.netId;
    }

    public void setIpChanged(boolean z) {
        this.ipChanged = z;
    }

    public boolean hasIpChanged() {
        return this.ipChanged;
    }

    public void setProxyChanged(boolean z) {
        this.proxyChanged = z;
    }

    public boolean hasProxyChanged() {
        return this.proxyChanged;
    }

    public boolean isNewNetwork() {
        return this.isNewNetwork;
    }

    public void setIsNewNetwork(boolean z) {
        this.isNewNetwork = z;
    }
}
