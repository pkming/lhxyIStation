package android.bluetooth.le;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class ScanCallback {
    public static final int SCAN_FAILED_ALREADY_STARTED = 1;
    public static final int SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 2;
    public static final int SCAN_FAILED_FEATURE_UNSUPPORTED = 4;
    public static final int SCAN_FAILED_INTERNAL_ERROR = 3;

    public void onBatchScanResults(List<ScanResult> list) {
    }

    public void onScanFailed(int i) {
    }

    public void onScanResult(int i, ScanResult scanResult) {
    }
}
