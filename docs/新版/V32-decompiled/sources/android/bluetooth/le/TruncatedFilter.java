package android.bluetooth.le;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class TruncatedFilter {
    private final ScanFilter mFilter;
    private final List<ResultStorageDescriptor> mStorageDescriptors;

    public TruncatedFilter(ScanFilter scanFilter, List<ResultStorageDescriptor> list) {
        this.mFilter = scanFilter;
        this.mStorageDescriptors = list;
    }

    public ScanFilter getFilter() {
        return this.mFilter;
    }

    public List<ResultStorageDescriptor> getStorageDescriptors() {
        return this.mStorageDescriptors;
    }
}
