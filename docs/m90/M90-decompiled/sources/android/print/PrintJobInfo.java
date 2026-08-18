package android.print;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class PrintJobInfo implements Parcelable {
    public static final Parcelable.Creator<PrintJobInfo> CREATOR = new Parcelable.Creator<PrintJobInfo>() { // from class: android.print.PrintJobInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintJobInfo createFromParcel(Parcel parcel) {
            return new PrintJobInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintJobInfo[] newArray(int i) {
            return new PrintJobInfo[i];
        }
    };
    public static final int STATE_ANY = -1;
    public static final int STATE_ANY_ACTIVE = -3;
    public static final int STATE_ANY_SCHEDULED = -4;
    public static final int STATE_ANY_VISIBLE_TO_CLIENTS = -2;
    public static final int STATE_BLOCKED = 4;
    public static final int STATE_CANCELED = 7;
    public static final int STATE_COMPLETED = 5;
    public static final int STATE_CREATED = 1;
    public static final int STATE_FAILED = 6;
    public static final int STATE_QUEUED = 2;
    public static final int STATE_STARTED = 3;
    private Bundle mAdvancedOptions;
    private int mAppId;
    private PrintAttributes mAttributes;
    private boolean mCanceling;
    private int mCopies;
    private long mCreationTime;
    private PrintDocumentInfo mDocumentInfo;
    private PrintJobId mId;
    private String mLabel;
    private PageRange[] mPageRanges;
    private PrinterId mPrinterId;
    private String mPrinterName;
    private int mState;
    private String mStateReason;
    private String mTag;

    public static String stateToString(int i) {
        switch (i) {
            case 1:
                return "STATE_CREATED";
            case 2:
                return "STATE_QUEUED";
            case 3:
                return "STATE_STARTED";
            case 4:
                return "STATE_BLOCKED";
            case 5:
                return "STATE_COMPLETED";
            case 6:
                return "STATE_FAILED";
            case 7:
                return "STATE_CANCELED";
            default:
                return "STATE_UNKNOWN";
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PrintJobInfo() {
    }

    public PrintJobInfo(PrintJobInfo printJobInfo) {
        this.mId = printJobInfo.mId;
        this.mLabel = printJobInfo.mLabel;
        this.mPrinterId = printJobInfo.mPrinterId;
        this.mPrinterName = printJobInfo.mPrinterName;
        this.mState = printJobInfo.mState;
        this.mAppId = printJobInfo.mAppId;
        this.mTag = printJobInfo.mTag;
        this.mCreationTime = printJobInfo.mCreationTime;
        this.mCopies = printJobInfo.mCopies;
        this.mStateReason = printJobInfo.mStateReason;
        this.mPageRanges = printJobInfo.mPageRanges;
        this.mAttributes = printJobInfo.mAttributes;
        this.mDocumentInfo = printJobInfo.mDocumentInfo;
        this.mCanceling = printJobInfo.mCanceling;
        this.mAdvancedOptions = printJobInfo.mAdvancedOptions;
    }

    private PrintJobInfo(Parcel parcel) {
        this.mId = (PrintJobId) parcel.readParcelable(null);
        this.mLabel = parcel.readString();
        this.mPrinterId = (PrinterId) parcel.readParcelable(null);
        this.mPrinterName = parcel.readString();
        this.mState = parcel.readInt();
        this.mAppId = parcel.readInt();
        this.mTag = parcel.readString();
        this.mCreationTime = parcel.readLong();
        this.mCopies = parcel.readInt();
        this.mStateReason = parcel.readString();
        Parcelable[] parcelableArray = parcel.readParcelableArray(null);
        if (parcelableArray != null) {
            this.mPageRanges = new PageRange[parcelableArray.length];
            for (int i = 0; i < parcelableArray.length; i++) {
                this.mPageRanges[i] = (PageRange) parcelableArray[i];
            }
        }
        this.mAttributes = (PrintAttributes) parcel.readParcelable(null);
        this.mDocumentInfo = (PrintDocumentInfo) parcel.readParcelable(null);
        this.mCanceling = parcel.readInt() == 1;
        this.mAdvancedOptions = parcel.readBundle();
    }

    public PrintJobId getId() {
        return this.mId;
    }

    public void setId(PrintJobId printJobId) {
        this.mId = printJobId;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public void setLabel(String str) {
        this.mLabel = str;
    }

    public PrinterId getPrinterId() {
        return this.mPrinterId;
    }

    public void setPrinterId(PrinterId printerId) {
        this.mPrinterId = printerId;
    }

    public String getPrinterName() {
        return this.mPrinterName;
    }

    public void setPrinterName(String str) {
        this.mPrinterName = str;
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int i) {
        this.mState = i;
    }

    public int getAppId() {
        return this.mAppId;
    }

    public void setAppId(int i) {
        this.mAppId = i;
    }

    public String getTag() {
        return this.mTag;
    }

    public void setTag(String str) {
        this.mTag = str;
    }

    public long getCreationTime() {
        return this.mCreationTime;
    }

    public void setCreationTime(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("creationTime must be non-negative.");
        }
        this.mCreationTime = j;
    }

    public int getCopies() {
        return this.mCopies;
    }

    public void setCopies(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("Copies must be more than one.");
        }
        this.mCopies = i;
    }

    public String getStateReason() {
        return this.mStateReason;
    }

    public void setStateReason(String str) {
        this.mStateReason = str;
    }

    public PageRange[] getPages() {
        return this.mPageRanges;
    }

    public void setPages(PageRange[] pageRangeArr) {
        this.mPageRanges = pageRangeArr;
    }

    public PrintAttributes getAttributes() {
        return this.mAttributes;
    }

    public void setAttributes(PrintAttributes printAttributes) {
        this.mAttributes = printAttributes;
    }

    public PrintDocumentInfo getDocumentInfo() {
        return this.mDocumentInfo;
    }

    public void setDocumentInfo(PrintDocumentInfo printDocumentInfo) {
        this.mDocumentInfo = printDocumentInfo;
    }

    public boolean isCancelling() {
        return this.mCanceling;
    }

    public void setCancelling(boolean z) {
        this.mCanceling = z;
    }

    public boolean hasAdvancedOption(String str) {
        Bundle bundle = this.mAdvancedOptions;
        return bundle != null && bundle.containsKey(str);
    }

    public String getAdvancedStringOption(String str) {
        Bundle bundle = this.mAdvancedOptions;
        if (bundle != null) {
            return bundle.getString(str);
        }
        return null;
    }

    public int getAdvancedIntOption(String str) {
        Bundle bundle = this.mAdvancedOptions;
        if (bundle != null) {
            return bundle.getInt(str);
        }
        return 0;
    }

    public Bundle getAdvancedOptions() {
        return this.mAdvancedOptions;
    }

    public void setAdvancedOptions(Bundle bundle) {
        this.mAdvancedOptions = bundle;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mId, i);
        parcel.writeString(this.mLabel);
        parcel.writeParcelable(this.mPrinterId, i);
        parcel.writeString(this.mPrinterName);
        parcel.writeInt(this.mState);
        parcel.writeInt(this.mAppId);
        parcel.writeString(this.mTag);
        parcel.writeLong(this.mCreationTime);
        parcel.writeInt(this.mCopies);
        parcel.writeString(this.mStateReason);
        parcel.writeParcelableArray(this.mPageRanges, i);
        parcel.writeParcelable(this.mAttributes, i);
        parcel.writeParcelable(this.mDocumentInfo, 0);
        parcel.writeInt(this.mCanceling ? 1 : 0);
        parcel.writeBundle(this.mAdvancedOptions);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PrintJobInfo{");
        sb.append("label: ").append(this.mLabel);
        sb.append(", id: ").append(this.mId);
        sb.append(", state: ").append(stateToString(this.mState));
        sb.append(", printer: " + this.mPrinterId);
        sb.append(", tag: ").append(this.mTag);
        sb.append(", creationTime: " + this.mCreationTime);
        sb.append(", copies: ").append(this.mCopies);
        StringBuilder sbAppend = new StringBuilder().append(", attributes: ");
        PrintAttributes printAttributes = this.mAttributes;
        sb.append(sbAppend.append(printAttributes != null ? printAttributes.toString() : null).toString());
        StringBuilder sbAppend2 = new StringBuilder().append(", documentInfo: ");
        PrintDocumentInfo printDocumentInfo = this.mDocumentInfo;
        sb.append(sbAppend2.append(printDocumentInfo != null ? printDocumentInfo.toString() : null).toString());
        sb.append(", cancelling: " + this.mCanceling);
        StringBuilder sbAppend3 = new StringBuilder().append(", pages: ");
        PageRange[] pageRangeArr = this.mPageRanges;
        sb.append(sbAppend3.append(pageRangeArr != null ? Arrays.toString(pageRangeArr) : null).toString());
        sb.append(", hasAdvancedOptions: " + (this.mAdvancedOptions != null));
        sb.append("}");
        return sb.toString();
    }

    public static final class Builder {
        private final PrintJobInfo mPrototype;

        public Builder(PrintJobInfo printJobInfo) {
            this.mPrototype = printJobInfo != null ? new PrintJobInfo(printJobInfo) : new PrintJobInfo();
        }

        public void setCopies(int i) {
            this.mPrototype.mCopies = i;
        }

        public void setAttributes(PrintAttributes printAttributes) {
            this.mPrototype.mAttributes = printAttributes;
        }

        public void setPages(PageRange[] pageRangeArr) {
            this.mPrototype.mPageRanges = pageRangeArr;
        }

        public void putAdvancedOption(String str, String str2) {
            if (this.mPrototype.mAdvancedOptions == null) {
                this.mPrototype.mAdvancedOptions = new Bundle();
            }
            this.mPrototype.mAdvancedOptions.putString(str, str2);
        }

        public void putAdvancedOption(String str, int i) {
            if (this.mPrototype.mAdvancedOptions == null) {
                this.mPrototype.mAdvancedOptions = new Bundle();
            }
            this.mPrototype.mAdvancedOptions.putInt(str, i);
        }

        public PrintJobInfo build() {
            return this.mPrototype;
        }
    }
}
