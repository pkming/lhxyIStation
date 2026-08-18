package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class BatchedScanSettings implements Parcelable {
    public static final Parcelable.Creator<BatchedScanSettings> CREATOR = new Parcelable.Creator<BatchedScanSettings>() { // from class: android.net.wifi.BatchedScanSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatchedScanSettings createFromParcel(Parcel parcel) {
            BatchedScanSettings batchedScanSettings = new BatchedScanSettings();
            batchedScanSettings.maxScansPerBatch = parcel.readInt();
            batchedScanSettings.maxApPerScan = parcel.readInt();
            batchedScanSettings.scanIntervalSec = parcel.readInt();
            batchedScanSettings.maxApForDistance = parcel.readInt();
            int i = parcel.readInt();
            if (i > 0) {
                batchedScanSettings.channelSet = new ArrayList(i);
                while (true) {
                    int i2 = i - 1;
                    if (i <= 0) {
                        break;
                    }
                    batchedScanSettings.channelSet.add(parcel.readString());
                    i = i2;
                }
            }
            return batchedScanSettings;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatchedScanSettings[] newArray(int i) {
            return new BatchedScanSettings[i];
        }
    };
    public static final int DEFAULT_AP_FOR_DISTANCE = 0;
    public static final int DEFAULT_AP_PER_SCAN = 16;
    public static final int DEFAULT_INTERVAL_SEC = 30;
    public static final int DEFAULT_SCANS_PER_BATCH = 255;
    public static final int MAX_AP_FOR_DISTANCE = 255;
    public static final int MAX_AP_PER_SCAN = 255;
    public static final int MAX_INTERVAL_SEC = 3600;
    public static final int MAX_SCANS_PER_BATCH = 255;
    public static final int MAX_WIFI_CHANNEL = 196;
    public static final int MIN_AP_FOR_DISTANCE = 0;
    public static final int MIN_AP_PER_SCAN = 2;
    public static final int MIN_INTERVAL_SEC = 0;
    public static final int MIN_SCANS_PER_BATCH = 2;
    private static final String TAG = "BatchedScanSettings";
    public static final int UNSPECIFIED = Integer.MAX_VALUE;
    public Collection<String> channelSet;
    public int maxApForDistance;
    public int maxApPerScan;
    public int maxScansPerBatch;
    public int scanIntervalSec;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public BatchedScanSettings() {
        clear();
    }

    public void clear() {
        this.maxScansPerBatch = Integer.MAX_VALUE;
        this.maxApPerScan = Integer.MAX_VALUE;
        this.channelSet = null;
        this.scanIntervalSec = Integer.MAX_VALUE;
        this.maxApForDistance = Integer.MAX_VALUE;
    }

    public BatchedScanSettings(BatchedScanSettings batchedScanSettings) {
        this.maxScansPerBatch = batchedScanSettings.maxScansPerBatch;
        this.maxApPerScan = batchedScanSettings.maxApPerScan;
        if (batchedScanSettings.channelSet != null) {
            this.channelSet = new ArrayList(batchedScanSettings.channelSet);
        }
        this.scanIntervalSec = batchedScanSettings.scanIntervalSec;
        this.maxApForDistance = batchedScanSettings.maxApForDistance;
    }

    private boolean channelSetIsValid() {
        int i;
        Collection<String> collection = this.channelSet;
        if (collection != null && !collection.isEmpty()) {
            for (String str : this.channelSet) {
                try {
                    i = Integer.parseInt(str);
                } catch (NumberFormatException unused) {
                }
                if (i <= 0 || i > 196) {
                    if (!str.equals("A") && !str.equals("B")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isInvalid() {
        int i = this.maxScansPerBatch;
        if (i != Integer.MAX_VALUE && (i < 2 || i > 255)) {
            return true;
        }
        int i2 = this.maxApPerScan;
        if ((i2 != Integer.MAX_VALUE && (i2 < 2 || i2 > 255)) || !channelSetIsValid()) {
            return true;
        }
        int i3 = this.scanIntervalSec;
        if (i3 != Integer.MAX_VALUE && (i3 < 0 || i3 > 3600)) {
            return true;
        }
        int i4 = this.maxApForDistance;
        if (i4 != Integer.MAX_VALUE) {
            return i4 < 0 || i4 > 255;
        }
        return false;
    }

    public void constrain() {
        int i = this.scanIntervalSec;
        if (i == Integer.MAX_VALUE) {
            this.scanIntervalSec = 30;
        } else if (i < 0) {
            this.scanIntervalSec = 0;
        } else if (i > 3600) {
            this.scanIntervalSec = MAX_INTERVAL_SEC;
        }
        int i2 = this.maxScansPerBatch;
        if (i2 == Integer.MAX_VALUE) {
            this.maxScansPerBatch = 255;
        } else if (i2 < 2) {
            this.maxScansPerBatch = 2;
        } else if (i2 > 255) {
            this.maxScansPerBatch = 255;
        }
        int i3 = this.maxApPerScan;
        if (i3 == Integer.MAX_VALUE) {
            this.maxApPerScan = 16;
        } else if (i3 < 2) {
            this.maxApPerScan = 2;
        } else if (i3 > 255) {
            this.maxApPerScan = 255;
        }
        int i4 = this.maxApForDistance;
        if (i4 == Integer.MAX_VALUE) {
            this.maxApForDistance = 0;
        } else if (i4 < 0) {
            this.maxApForDistance = 0;
        } else if (i4 > 255) {
            this.maxApForDistance = 255;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BatchedScanSettings)) {
            return false;
        }
        BatchedScanSettings batchedScanSettings = (BatchedScanSettings) obj;
        if (this.maxScansPerBatch != batchedScanSettings.maxScansPerBatch || this.maxApPerScan != batchedScanSettings.maxApPerScan || this.scanIntervalSec != batchedScanSettings.scanIntervalSec || this.maxApForDistance != batchedScanSettings.maxApForDistance) {
            return false;
        }
        Collection<String> collection = this.channelSet;
        if (collection == null) {
            return batchedScanSettings.channelSet == null;
        }
        return collection.equals(batchedScanSettings.channelSet);
    }

    public int hashCode() {
        return this.maxScansPerBatch + (this.maxApPerScan * 3) + (this.scanIntervalSec * 5) + (this.maxApForDistance * 7) + (this.channelSet.hashCode() * 11);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBufferAppend = stringBuffer.append("BatchScanSettings [maxScansPerBatch: ");
        int i = this.maxScansPerBatch;
        StringBuffer stringBufferAppend2 = stringBufferAppend.append(i == Integer.MAX_VALUE ? "<none>" : Integer.valueOf(i)).append(", maxApPerScan: ");
        int i2 = this.maxApPerScan;
        StringBuffer stringBufferAppend3 = stringBufferAppend2.append(i2 == Integer.MAX_VALUE ? "<none>" : Integer.valueOf(i2)).append(", scanIntervalSec: ");
        int i3 = this.scanIntervalSec;
        StringBuffer stringBufferAppend4 = stringBufferAppend3.append(i3 == Integer.MAX_VALUE ? "<none>" : Integer.valueOf(i3)).append(", maxApForDistance: ");
        int i4 = this.maxApForDistance;
        stringBufferAppend4.append(i4 != Integer.MAX_VALUE ? Integer.valueOf(i4) : "<none>").append(", channelSet: ");
        if (this.channelSet == null) {
            stringBuffer.append("ALL");
        } else {
            stringBuffer.append("<");
            Iterator<String> it = this.channelSet.iterator();
            while (it.hasNext()) {
                stringBuffer.append(" " + it.next());
            }
            stringBuffer.append(">");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.maxScansPerBatch);
        parcel.writeInt(this.maxApPerScan);
        parcel.writeInt(this.scanIntervalSec);
        parcel.writeInt(this.maxApForDistance);
        Collection<String> collection = this.channelSet;
        parcel.writeInt(collection == null ? 0 : collection.size());
        Collection<String> collection2 = this.channelSet;
        if (collection2 != null) {
            Iterator<String> it = collection2.iterator();
            while (it.hasNext()) {
                parcel.writeString(it.next());
            }
        }
    }
}
