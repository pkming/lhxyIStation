package android.test;

/* JADX INFO: loaded from: classes.dex */
public interface PerformanceTestCase {

    public interface Intermediates {
        void addIntermediate(String str);

        void addIntermediate(String str, long j);

        void finishTiming(boolean z);

        void setInternalIterations(int i);

        void startTiming(boolean z);
    }

    boolean isPerformanceOnly();

    int startPerformance(Intermediates intermediates);
}
