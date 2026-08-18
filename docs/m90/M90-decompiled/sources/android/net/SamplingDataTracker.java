package android.net;

/* JADX INFO: loaded from: classes.dex */
public class SamplingDataTracker {
    private static final boolean DBG = false;
    private static final String TAG = "SamplingDataTracker";
    private SamplingSnapshot mBeginningSample;
    private SamplingSnapshot mEndingSample;
    private SamplingSnapshot mLastSample;
    public final Object mSamplingDataLock = new Object();
    private final int MINIMUM_SAMPLING_INTERVAL = 15000;
    private final int MINIMUM_SAMPLED_PACKETS = 30;

    public static class SamplingSnapshot {
        public long mRxByteCount;
        public long mRxPacketCount;
        public long mRxPacketErrorCount;
        public long mTimestamp;
        public long mTxByteCount;
        public long mTxPacketCount;
        public long mTxPacketErrorCount;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v10, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v14 */
    /* JADX WARN: Type inference failed for: r2v15 */
    /* JADX WARN: Type inference failed for: r2v16 */
    /* JADX WARN: Type inference failed for: r2v17 */
    /* JADX WARN: Type inference failed for: r2v18 */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r2v5, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r2v9 */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:29:0x00a6
        	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1182)
        	at jadx.core.dex.visitors.regions.maker.ExcHandlersRegionMaker.collectHandlerRegions(ExcHandlersRegionMaker.java:53)
        	at jadx.core.dex.visitors.regions.maker.ExcHandlersRegionMaker.process(ExcHandlersRegionMaker.java:38)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:27)
        */
    public static void getSamplingSnapshots(java.util.Map<java.lang.String, android.net.SamplingDataTracker.SamplingSnapshot> r8) {
        /*
            java.lang.String r0 = "could not close /proc/net/dev"
            java.lang.String r1 = "SamplingDataTracker"
            r2 = 0
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L91 java.io.IOException -> L93 java.io.FileNotFoundException -> L9b
            java.io.FileReader r4 = new java.io.FileReader     // Catch: java.lang.Throwable -> L91 java.io.IOException -> L93 java.io.FileNotFoundException -> L9b
            java.lang.String r5 = "/proc/net/dev"
            r4.<init>(r5)     // Catch: java.lang.Throwable -> L91 java.io.IOException -> L93 java.io.FileNotFoundException -> L9b
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L91 java.io.IOException -> L93 java.io.FileNotFoundException -> L9b
            r3.readLine()     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r3.readLine()     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
        L17:
            java.lang.String r2 = r3.readLine()     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            if (r2 == 0) goto L86
            java.lang.String r2 = r2.trim()     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            java.lang.String r4 = "[ ]+"
            java.lang.String[] r2 = r2.split(r4)     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            int r4 = r2.length     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5 = 17
            if (r4 >= r5) goto L2d
            goto L17
        L2d:
            r4 = 0
            r5 = r2[r4]     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            java.lang.String r6 = ":"
            java.lang.String[] r5 = r5.split(r6)     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r4 = r5[r4]     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            boolean r5 = r8.containsKey(r4)     // Catch: java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            if (r5 == 0) goto L17
            android.net.SamplingDataTracker$SamplingSnapshot r5 = new android.net.SamplingDataTracker$SamplingSnapshot     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5.<init>()     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r6 = 1
            r6 = r2[r6]     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            long r6 = java.lang.Long.parseLong(r6)     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5.mTxByteCount = r6     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r6 = 2
            r6 = r2[r6]     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            long r6 = java.lang.Long.parseLong(r6)     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5.mTxPacketCount = r6     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r6 = 3
            r6 = r2[r6]     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            long r6 = java.lang.Long.parseLong(r6)     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5.mTxPacketErrorCount = r6     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r6 = 9
            r6 = r2[r6]     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            long r6 = java.lang.Long.parseLong(r6)     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5.mRxByteCount = r6     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r6 = 10
            r6 = r2[r6]     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            long r6 = java.lang.Long.parseLong(r6)     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5.mRxPacketCount = r6     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r6 = 11
            r2 = r2[r6]     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            long r6 = java.lang.Long.parseLong(r2)     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5.mRxPacketErrorCount = r6     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r5.mTimestamp = r6     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            r8.put(r4, r5)     // Catch: java.lang.NumberFormatException -> L17 java.lang.Throwable -> L8a java.io.IOException -> L8d java.io.FileNotFoundException -> L8f
            goto L17
        L86:
            r3.close()     // Catch: java.io.IOException -> La6
            goto La9
        L8a:
            r8 = move-exception
            r2 = r3
            goto Laa
        L8d:
            r2 = r3
            goto L93
        L8f:
            r2 = r3
            goto L9b
        L91:
            r8 = move-exception
            goto Laa
        L93:
            java.lang.String r8 = "could not read /proc/net/dev"
            android.util.Slog.e(r1, r8)     // Catch: java.lang.Throwable -> L91
            if (r2 == 0) goto La9
            goto La2
        L9b:
            java.lang.String r8 = "could not find /proc/net/dev"
            android.util.Slog.e(r1, r8)     // Catch: java.lang.Throwable -> L91
            if (r2 == 0) goto La9
        La2:
            r2.close()     // Catch: java.io.IOException -> La6
            goto La9
        La6:
            android.util.Slog.e(r1, r0)
        La9:
            return
        Laa:
            if (r2 == 0) goto Lb3
            r2.close()     // Catch: java.io.IOException -> Lb0
            goto Lb3
        Lb0:
            android.util.Slog.e(r1, r0)
        Lb3:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.SamplingDataTracker.getSamplingSnapshots(java.util.Map):void");
    }

    public void startSampling(SamplingSnapshot samplingSnapshot) {
        synchronized (this.mSamplingDataLock) {
            this.mLastSample = samplingSnapshot;
        }
    }

    public void stopSampling(SamplingSnapshot samplingSnapshot) {
        synchronized (this.mSamplingDataLock) {
            if (this.mLastSample != null && samplingSnapshot.mTimestamp - this.mLastSample.mTimestamp > 15000 && getSampledPacketCount(this.mLastSample, samplingSnapshot) > 30) {
                this.mBeginningSample = this.mLastSample;
                this.mEndingSample = samplingSnapshot;
                this.mLastSample = null;
            }
        }
    }

    public void resetSamplingData() {
        synchronized (this.mSamplingDataLock) {
            this.mLastSample = null;
        }
    }

    public long getSampledTxByteCount() {
        SamplingSnapshot samplingSnapshot;
        synchronized (this.mSamplingDataLock) {
            return (this.mBeginningSample == null || (samplingSnapshot = this.mEndingSample) == null) ? LinkQualityInfo.UNKNOWN_LONG : samplingSnapshot.mTxByteCount - this.mBeginningSample.mTxByteCount;
        }
    }

    public long getSampledTxPacketCount() {
        SamplingSnapshot samplingSnapshot;
        synchronized (this.mSamplingDataLock) {
            return (this.mBeginningSample == null || (samplingSnapshot = this.mEndingSample) == null) ? LinkQualityInfo.UNKNOWN_LONG : samplingSnapshot.mTxPacketCount - this.mBeginningSample.mTxPacketCount;
        }
    }

    public long getSampledTxPacketErrorCount() {
        SamplingSnapshot samplingSnapshot;
        synchronized (this.mSamplingDataLock) {
            return (this.mBeginningSample == null || (samplingSnapshot = this.mEndingSample) == null) ? LinkQualityInfo.UNKNOWN_LONG : samplingSnapshot.mTxPacketErrorCount - this.mBeginningSample.mTxPacketErrorCount;
        }
    }

    public long getSampledRxByteCount() {
        SamplingSnapshot samplingSnapshot;
        synchronized (this.mSamplingDataLock) {
            return (this.mBeginningSample == null || (samplingSnapshot = this.mEndingSample) == null) ? LinkQualityInfo.UNKNOWN_LONG : samplingSnapshot.mRxByteCount - this.mBeginningSample.mRxByteCount;
        }
    }

    public long getSampledRxPacketCount() {
        SamplingSnapshot samplingSnapshot;
        synchronized (this.mSamplingDataLock) {
            return (this.mBeginningSample == null || (samplingSnapshot = this.mEndingSample) == null) ? LinkQualityInfo.UNKNOWN_LONG : samplingSnapshot.mRxPacketCount - this.mBeginningSample.mRxPacketCount;
        }
    }

    public long getSampledPacketCount() {
        return getSampledPacketCount(this.mBeginningSample, this.mEndingSample);
    }

    public long getSampledPacketCount(SamplingSnapshot samplingSnapshot, SamplingSnapshot samplingSnapshot2) {
        return (samplingSnapshot == null || samplingSnapshot2 == null) ? LinkQualityInfo.UNKNOWN_LONG : (samplingSnapshot2.mRxPacketCount - samplingSnapshot.mRxPacketCount) + (samplingSnapshot2.mTxPacketCount - samplingSnapshot.mTxPacketCount);
    }

    public long getSampledPacketErrorCount() {
        return (this.mBeginningSample == null || this.mEndingSample == null) ? LinkQualityInfo.UNKNOWN_LONG : getSampledRxPacketErrorCount() + getSampledTxPacketErrorCount();
    }

    public long getSampledRxPacketErrorCount() {
        SamplingSnapshot samplingSnapshot;
        synchronized (this.mSamplingDataLock) {
            return (this.mBeginningSample == null || (samplingSnapshot = this.mEndingSample) == null) ? LinkQualityInfo.UNKNOWN_LONG : samplingSnapshot.mRxPacketErrorCount - this.mBeginningSample.mRxPacketErrorCount;
        }
    }

    public long getSampleTimestamp() {
        synchronized (this.mSamplingDataLock) {
            SamplingSnapshot samplingSnapshot = this.mEndingSample;
            if (samplingSnapshot == null) {
                return LinkQualityInfo.UNKNOWN_LONG;
            }
            return samplingSnapshot.mTimestamp;
        }
    }

    public int getSampleDuration() {
        SamplingSnapshot samplingSnapshot;
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample == null || (samplingSnapshot = this.mEndingSample) == null) {
                return Integer.MAX_VALUE;
            }
            return (int) (samplingSnapshot.mTimestamp - this.mBeginningSample.mTimestamp);
        }
    }

    public void setCommonLinkQualityInfoFields(LinkQualityInfo linkQualityInfo) {
        synchronized (this.mSamplingDataLock) {
            linkQualityInfo.setLastDataSampleTime(getSampleTimestamp());
            linkQualityInfo.setDataSampleDuration(getSampleDuration());
            linkQualityInfo.setPacketCount(getSampledPacketCount());
            linkQualityInfo.setPacketErrorCount(getSampledPacketErrorCount());
        }
    }
}
