package android.util;

/* JADX INFO: loaded from: classes.dex */
public interface TrustedTime {
    long currentTimeMillis();

    boolean forceRefresh();

    long getCacheAge();

    long getCacheCertainty();

    boolean hasCache();
}
