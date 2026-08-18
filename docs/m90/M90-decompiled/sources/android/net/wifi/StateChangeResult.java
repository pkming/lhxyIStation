package android.net.wifi;

/* JADX INFO: loaded from: classes.dex */
public class StateChangeResult {
    String BSSID;
    int networkId;
    SupplicantState state;
    WifiSsid wifiSsid;

    StateChangeResult(int i, WifiSsid wifiSsid, String str, SupplicantState supplicantState) {
        this.state = supplicantState;
        this.wifiSsid = wifiSsid;
        this.BSSID = str;
        this.networkId = i;
    }
}
