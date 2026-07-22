package android.media;

import java.io.Closeable;

/* JADX INFO: loaded from: classes.dex */
public interface DataSource extends Closeable {
    long getSize();

    int readAt(long j, byte[] bArr, int i);
}
