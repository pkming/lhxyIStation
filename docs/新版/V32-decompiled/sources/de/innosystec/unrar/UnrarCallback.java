package de.innosystec.unrar;

import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public interface UnrarCallback {
    boolean isNextVolumeReady(File file);

    void volumeProgressChanged(long j, long j2);
}
