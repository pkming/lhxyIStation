package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class NeighboringCellInfo implements Parcelable {
    public static final Parcelable.Creator<NeighboringCellInfo> CREATOR = new Parcelable.Creator<NeighboringCellInfo>() { // from class: android.telephony.NeighboringCellInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NeighboringCellInfo createFromParcel(Parcel parcel) {
            return new NeighboringCellInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NeighboringCellInfo[] newArray(int i) {
            return new NeighboringCellInfo[i];
        }
    };
    public static final int UNKNOWN_CID = -1;
    public static final int UNKNOWN_RSSI = 99;
    private int mCid;
    private int mLac;
    private int mNetworkType;
    private int mPsc;
    private int mRssi;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Deprecated
    public NeighboringCellInfo() {
        this.mRssi = 99;
        this.mLac = -1;
        this.mCid = -1;
        this.mPsc = -1;
        this.mNetworkType = 0;
    }

    @Deprecated
    public NeighboringCellInfo(int i, int i2) {
        this.mRssi = i;
        this.mCid = i2;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:23:0x0079
        	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1182)
        	at jadx.core.dex.visitors.regions.maker.ExcHandlersRegionMaker.collectHandlerRegions(ExcHandlersRegionMaker.java:53)
        	at jadx.core.dex.visitors.regions.maker.ExcHandlersRegionMaker.process(ExcHandlersRegionMaker.java:38)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:27)
        */
    public NeighboringCellInfo(int r7, java.lang.String r8, int r9) {
        /*
            r6 = this;
            r6.<init>()
            r6.mRssi = r7
            r7 = 0
            r6.mNetworkType = r7
            r0 = -1
            r6.mPsc = r0
            r6.mLac = r0
            r6.mCid = r0
            int r1 = r8.length()
            r2 = 8
            if (r1 <= r2) goto L18
            return
        L18:
            if (r1 >= r2) goto L35
            r3 = r7
        L1b:
            int r4 = 8 - r1
            if (r3 >= r4) goto L35
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "0"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r8 = r4.append(r8)
            java.lang.String r8 = r8.toString()
            int r3 = r3 + 1
            goto L1b
        L35:
            r1 = 1
            r2 = 16
            if (r9 == r1) goto L51
            r1 = 2
            if (r9 == r1) goto L51
            r1 = 3
            if (r9 == r1) goto L44
            switch(r9) {
                case 8: goto L44;
                case 9: goto L44;
                case 10: goto L44;
                default: goto L43;
            }
        L43:
            goto L81
        L44:
            r6.mNetworkType = r9     // Catch: java.lang.NumberFormatException -> L79
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8, r2)     // Catch: java.lang.NumberFormatException -> L79
            int r8 = r8.intValue()     // Catch: java.lang.NumberFormatException -> L79
            r6.mPsc = r8     // Catch: java.lang.NumberFormatException -> L79
            goto L81
        L51:
            r6.mNetworkType = r9     // Catch: java.lang.NumberFormatException -> L79
            java.lang.String r9 = "FFFFFFFF"
            boolean r9 = r8.equalsIgnoreCase(r9)     // Catch: java.lang.NumberFormatException -> L79
            if (r9 != 0) goto L81
            r9 = 4
            java.lang.String r1 = r8.substring(r9)     // Catch: java.lang.NumberFormatException -> L79
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1, r2)     // Catch: java.lang.NumberFormatException -> L79
            int r1 = r1.intValue()     // Catch: java.lang.NumberFormatException -> L79
            r6.mCid = r1     // Catch: java.lang.NumberFormatException -> L79
            java.lang.String r8 = r8.substring(r7, r9)     // Catch: java.lang.NumberFormatException -> L79
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8, r2)     // Catch: java.lang.NumberFormatException -> L79
            int r8 = r8.intValue()     // Catch: java.lang.NumberFormatException -> L79
            r6.mLac = r8     // Catch: java.lang.NumberFormatException -> L79
            goto L81
        L79:
            r6.mPsc = r0
            r6.mLac = r0
            r6.mCid = r0
            r6.mNetworkType = r7
        L81:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.telephony.NeighboringCellInfo.<init>(int, java.lang.String, int):void");
    }

    public NeighboringCellInfo(Parcel parcel) {
        this.mRssi = parcel.readInt();
        this.mLac = parcel.readInt();
        this.mCid = parcel.readInt();
        this.mPsc = parcel.readInt();
        this.mNetworkType = parcel.readInt();
    }

    public int getRssi() {
        return this.mRssi;
    }

    public int getLac() {
        return this.mLac;
    }

    public int getCid() {
        return this.mCid;
    }

    public int getPsc() {
        return this.mPsc;
    }

    public int getNetworkType() {
        return this.mNetworkType;
    }

    @Deprecated
    public void setCid(int i) {
        this.mCid = i;
    }

    @Deprecated
    public void setRssi(int i) {
        this.mRssi = i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = this.mPsc;
        if (i != -1) {
            StringBuilder sbAppend = sb.append(Integer.toHexString(i)).append("@");
            int i2 = this.mRssi;
            sbAppend.append(i2 != 99 ? Integer.valueOf(i2) : "-");
        } else {
            int i3 = this.mLac;
            if (i3 != -1 && this.mCid != -1) {
                StringBuilder sbAppend2 = sb.append(Integer.toHexString(i3)).append(Integer.toHexString(this.mCid)).append("@");
                int i4 = this.mRssi;
                sbAppend2.append(i4 != 99 ? Integer.valueOf(i4) : "-");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRssi);
        parcel.writeInt(this.mLac);
        parcel.writeInt(this.mCid);
        parcel.writeInt(this.mPsc);
        parcel.writeInt(this.mNetworkType);
    }
}
